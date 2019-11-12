""" Implementation of Oblivious Cross-Tags (OXT) protocol based on
`Result Pattern Hiding Searchable Encryption for Conjunctive Queries`

See: https://dl.acm.org/citation.cfm?id=3243753
"""
import logging
import multiprocessing
import base64
from collections import defaultdict
from pybloomfilter import BloomFilter
import mmh3
from bitarray import bitarray
from searchableencryption.toolbox.cryptoprimitives import random_secure, convert_to_bytes, cal_cmac_aes, \
    CMAC_AES128_KEY_LENGTH_IN_BYTES, convert_int_from_bytes, encrypt_aes, decrypt_aes
# for some reason, our own PairingGroup does not work when serialize/deserialize while moving files from macOS to ubuntu
from charm.toolbox.pairinggroup import PairingGroup, GT, ZR

KEY_CONNECTOR = '~'
BLOOMFILTER_DEFAULT_FALSE_POSITIVE_RATE = 1e-6

logger = logging.getLogger(__name__)

var_dict = {}


def setup(database: dict, password: str,
          bloomfilter_file=None, bf_false_positive_rate=BLOOMFILTER_DEFAULT_FALSE_POSITIVE_RATE,
          paralleled=False, num_processes=None) -> tuple:
    """
    Setup method of OXT for a database
    :param database: database with id -> list of words
    :param password: password to create keys
    :param bloomfilter_file: file to read/write bloomfilter
    :param bf_false_positive_rate: bloomfilter false positive rate
    :param bool paralleled: should we parallel the process or not
    :param num_processes: number of process used if parallel
    :return: (key, encrypted database)
    """
    global var_dict

    # TODO: generate keys from password
    K_P = random_secure(1)  # key to XOR index

    K_S = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for e
    iv = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)   # IV for AES encryption
    K_X = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for xtag
    K_I = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for index
    K_Z = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for Z
    K_T = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for keyword

    pairing = PairingGroup('SS512')

    g = pairing.random(GT)
    assert g.initPP(), "ERROR: Failed to init pre-computation table for g."

    total_pairs = 0
    inverted_index_all_pairs = defaultdict(list)  # word -> list of ids containing this word

    if paralleled:
        # parallel processing
        logger.info('Parallel gen_inverted_index')
        pool = multiprocessing.Pool()
        num_docs = len(database)
        inverted_tuples = pool.starmap(gen_inverted_index_paralleled, list(zip(database.items(), [K_P] * num_docs)))
        for inverted_list in inverted_tuples:
            for word, rind in inverted_list:
                inverted_index_all_pairs[word].append(rind)
                total_pairs += 1

    else:
        # sequential processing
        logger.info('Seq inverted_index_all_pairs')
        for (ind, words) in database.items():
            inverted_list = gen_inverted_index(ind, words, K_P)

            for word, rind in inverted_list:
                inverted_index_all_pairs[word].append(rind)  # rind is now bytes
                total_pairs += 1

    # generate xtags. Each xtag is for a pair (word, index)
    xtags = set()

    if paralleled:
        logger.info('Parallel xtags')
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_gen_xtags_parallel,
                initargs=(K_X, pairing, K_I, g)) as pool:
            xtags_lists = pool.map(gen_xtags_parallel, inverted_index_all_pairs.items())

            for xtags_list in xtags_lists:
                xtags.update(xtags_list)

            var_dict = {}
    else:
        logger.info('Seq xtags')
        for word, indices in inverted_index_all_pairs.items():
            xtags.update(gen_xtags(word, indices, K_X, pairing, K_I, g))

    # Create a Bloom filter and bitarray
    if bloomfilter_file is not None:
        bf = BloomFilter(total_pairs, bf_false_positive_rate, bloomfilter_file)
    else:
        bf = BloomFilter(total_pairs, bf_false_positive_rate)
    num_bits = bf.num_bits
    bits = bitarray(num_bits)
    bits.setall(False)

    # compute the positions of each xtag and set it
    # the reason we need to use bits array because the library doesn't expose bits. e.g. check if a bit is set or not
    xtag: str
    for xtag in xtags:
        bf.add(xtag)

        # mimic set in bits array
        for hash_seed in bf.hash_seeds:
            pos = bloomfilter_hash(xtag, hash_seed) % num_bits
            bits[pos] = True

    # generate encrypted database
    edb1 = dict()
    if paralleled:
        logger.info('Parallel edb1')
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_gen_t_set_parallel,
                initargs=(K_S, K_I, K_Z, K_T, iv, pairing)) as pool:
            t_set_dict_lists = pool.map(gen_t_set_parallel, inverted_index_all_pairs.items())

            for t_set_dict in t_set_dict_lists:
                edb1.update(t_set_dict)

            var_dict = {}
    else:
        logger.info('Seq edb1')

        for word, indices in inverted_index_all_pairs.items():
            edb1.update(gen_t_set(word, indices, K_S, K_I, K_Z, K_T, iv, pairing))

    key = (K_P, K_S, K_X, K_I, K_Z, K_T)
    g_serialized = pairing.serialize(g)

    return key, iv, g_serialized, edb1, bf, bits


def query(edb: dict, keywords: list, key: tuple, iv: bytes, bf: BloomFilter, g_serialized: bytes) -> list:
    """
    Query OXT for some keywords
    :param g_serialized:
    :param bf:
    :param iv:
    :type key: tuple
    :param key:
    :param edb:
    :param keywords:
    :return:
    """

    pairing = PairingGroup('SS512')

    # ------ CLIENT  ------
    g = pairing.deserialize(g_serialized)
    assert g.initPP(), "ERROR: Failed to init pre-computation table for g."

    (K_P, K_S, K_X, K_I, K_Z, K_T) = key

    stag = cal_cmac_aes(K_T, convert_to_bytes(keywords[0]))
    if not stag:
        return []

    # ---------------- SERVER -------------
    # get keys with stag
    t_set_result = list()
    c = 0
    while True:
        key = base64.encodebytes(stag).decode() + KEY_CONNECTOR + str(c)
        if key in edb:
            t_set_result.append(edb[key])
            c += 1
        else:
            break

    # ---------------- CLIENT -------------
    # list of (e, y) with y is Element
    oxt_t_set_tuples = t_set_result
    if not oxt_t_set_tuples:
        return []

    # Concat xtoken with OXT Tuple
    xtoken_tuples = list()
    xterms = keywords[1:]

    for c in range(len(oxt_t_set_tuples)):
        z = cal_cmac_aes(K_Z, convert_to_bytes(keywords[0]) + convert_to_bytes(c))
        e_z = pairing.init(ZR, convert_int_from_bytes(z))
        xtokens_serialized = list()
        for xterm in xterms:
            kxw = cal_cmac_aes(K_X, convert_to_bytes(xterm))
            e_kxw = pairing.init(ZR, convert_int_from_bytes(kxw))

            xtoken = g ** (e_z * e_kxw)

            xtokens_serialized.append(pairing.serialize(xtoken))

        xtoken_tuples.append((oxt_t_set_tuples[c][1], xtokens_serialized))

    # ---------------- SERVER -------------
    # match xtags in BF
    es = list()
    for c in range(len(xtoken_tuples)):
        # check matched in bloomfilter immediately
        xtag_matched = True
        y_c = pairing.deserialize(xtoken_tuples[c][0])
        xtokens_serialized = xtoken_tuples[c][1]
        for xtoken_serialized in xtokens_serialized:
            xtag = pairing.deserialize(xtoken_serialized) ** y_c
            xtag_matched = xtag_matched and (str(xtag) in bf)

        if xtag_matched:
            es.append(oxt_t_set_tuples[c][0])

    # ---------------- CLIENT -------------
    # client decrypt e
    K_e = cal_cmac_aes(K_S, convert_to_bytes(keywords[0]))
    rinds = [decrypt_aes(K_e, iv, e) for e in es]
    result = [get_rind(base64.decodebytes(rind), K_P).decode() for rind in rinds]

    return result


def gen_xtags(word, indices, K_X, pairing, K_I, g) -> list:
    xtags = list()
    kxw = cal_cmac_aes(K_X, convert_to_bytes(word))
    kxw_zr = pairing.init(ZR, convert_int_from_bytes(kxw))
    for ind in indices:
        xind = cal_cmac_aes(K_I, ind)
        xind_zr = pairing.init(ZR, convert_int_from_bytes(xind))
        xtag = g ** (kxw_zr * xind_zr)

        xtags.append(str(xtag))
    return xtags


def gen_t_set(word, indices, K_S, K_I, K_Z, K_T, iv, pairing) -> dict:
    edb_word = dict()
    word_bytes = convert_to_bytes(word)
    K_e = cal_cmac_aes(K_S, word_bytes)

    c = 0
    for index in indices:
        xind = cal_cmac_aes(K_I, index)
        z = cal_cmac_aes(K_Z, word_bytes + convert_to_bytes(c))
        e = encrypt_aes(K_e, iv, index)  # e is the encryption of word-index pair
        y = pairing.init(ZR, convert_int_from_bytes(xind)) / pairing.init(ZR, convert_int_from_bytes(z))
        tag = cal_cmac_aes(K_T, word_bytes)
        key = base64.encodebytes(tag).decode() + KEY_CONNECTOR + str(c)

        # Generate Tuple of (key~i, e, y)
        edb_word[key] = (e, pairing.serialize(y))
        c += 1

    return edb_word


def get_rind(ind: bytes, key: bytes) -> bytes:
    """
    Hide/Unhide index by XOR
    :param ind: index to hide/unhide
    :param key: key
    :return: Hidden/Unhidden index
    """
    return bytes((b ^ key[0]) for b in ind)


def gen_inverted_index(ind, words: list, key: bytes):
    """
    Generate list of inverted indices for words in document ind
    :param ind: document index
    :param list words: words of the document
    :param bytes key: key for masking the index
    :return: list of (word, masked_index)
    """
    res = list()
    rind = base64.encodebytes(get_rind(convert_to_bytes(str(ind)), key))  # rind is now bytes
    for word in words:
        res.append((word, rind))

    return res


def gen_inverted_index_paralleled(ind_words: tuple, key: bytes):
    """
    Function to use for paralleled version
    """
    return gen_inverted_index(ind_words[0], ind_words[1], key)


def bloomfilter_hash(val: str, hash_seed: int):
    """
    The hash function that pybloomfilter library used. Check source code of `add` method to see that
    :param val: the value to hash
    :param hash_seed: the hash seed
    :return: hashed value
    """
    h = mmh3.hash64(val, hash_seed, signed=False)
    return h[0] ^ h[1]


def init_gen_xtags_parallel(K_X, pairing, K_I, g):
    var_dict['K_X'] = K_X
    var_dict['pairing'] = pairing
    var_dict['K_I'] = K_I
    var_dict['g'] = g


def gen_xtags_parallel(word_indices):
    K_X = var_dict['K_X']
    pairing = var_dict['pairing']
    K_I = var_dict['K_I']
    g = var_dict['g']

    return gen_xtags(word_indices[0], word_indices[1], K_X, pairing, K_I, g)


def init_gen_t_set_parallel(K_S, K_I, K_Z, K_T, iv, pairing):
    var_dict['K_S'] = K_S
    var_dict['K_I'] = K_I
    var_dict['K_Z'] = K_Z
    var_dict['K_T'] = K_T
    var_dict['iv'] = iv
    var_dict['pairing'] = pairing


def gen_t_set_parallel(word_indices):
    K_S = var_dict['K_S']
    K_I = var_dict['K_I']
    K_Z = var_dict['K_Z']
    K_T = var_dict['K_T']
    iv = var_dict['iv']
    pairing = var_dict['pairing']

    word = word_indices[0]
    indices = word_indices[1]

    return gen_t_set(word, indices, K_S, K_I, K_Z, K_T, iv, pairing)
