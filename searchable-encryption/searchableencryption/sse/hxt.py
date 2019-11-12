""" Implementation of Hidden Cross Tags (HXT) protocol based on
`Result Pattern Hiding Searchable Encryption for Conjunctive Queries`

See: https://dl.acm.org/citation.cfm?id=3243753
"""
import logging
import multiprocessing
import time
import base64
from pybloomfilter import BloomFilter

from searchableencryption.sse import oxt
from searchableencryption.sse.oxt import KEY_CONNECTOR, get_rind, bloomfilter_hash
from searchableencryption.toolbox.cryptoprimitives import random_secure, cal_cmac_aes, hash_sha256, \
    convert_int_to_bytes, CMAC_AES128_KEY_LENGTH_IN_BYTES, convert_to_bytes, convert_int_from_bytes, decrypt_aes, \
    encrypt_aes
# for some reason, our own PairingGroup does not work when serialize/deserialize while moving files from macOS to ubuntu
from charm.toolbox.pairinggroup import PairingGroup, ZR

logger = logging.getLogger(__name__)


var_dict = {}  # parallel variables


def setup(database: dict, password: str,
          bloomfilter_file=None,
          bf_false_positive_rate=oxt.BLOOMFILTER_DEFAULT_FALSE_POSITIVE_RATE,
          paralleled=False, num_processes=None,
          benchmark=False) -> tuple:
    """
    Setup method of HXT for a database
    :param database: database with id -> list of words
    :param password: password to create keys
    :param bloomfilter_file: file to save bloomfilter. If None, memory is used
    :param bf_false_positive_rate: bloomfilter false positive rate
    :param bool paralleled: should we parallel the process or not. NOTE: (maybe) incorrect parallelism.
    :param num_processes: number of process if using parallel
    :param benchmark: whether we are doing benchmarking or not
    :return: (key, encrypted database)
    """
    global var_dict
    K_H = random_secure(CMAC_AES128_KEY_LENGTH_IN_BYTES)  # Key for SHVE

    start = time.time()

    key_oxt, iv, g_serialized, edb1, bf, bits = oxt.setup(
        database,
        password,
        bloomfilter_file=bloomfilter_file,
        bf_false_positive_rate=bf_false_positive_rate,
        paralleled=paralleled,
        num_processes=num_processes)
    end1 = time.time()

    time_gen_oxt = end1 - start
    logger.info(('HXT time gen OXT:', time_gen_oxt))

    shve = dict()
    bits_size = bits.length()
    logger.info(('bits_size', bits_size))
    
    if paralleled:
        logger.info('Parallel encode_shve')
        # parallel processing
        pool = multiprocessing.Pool(processes=num_processes)
        encoded_hves = pool.starmap(encode_shve, list(zip([K_H] * bits_size, range(bits_size), bits)))
        for shve_key, shve_cipher in encoded_hves:
            shve[shve_key] = shve_cipher
    else:
        # sequential processing
        logger.info('Seq encode_shve')
        for i in range(bits_size):
            shve_key, shve_cipher = encode_shve(K_H, i, bits[i])
            shve[shve_key] = shve_cipher

    key = key_oxt + (K_H,)
    end = time.time()

    time_gen_hxt_only = end - end1

    logger.info(('HXT time gen SHVE:', time_gen_hxt_only))

    if benchmark:
        return key, iv, g_serialized, edb1, bf, bits, shve, time_gen_oxt, time_gen_hxt_only, bits_size
    else:
        return key, iv, g_serialized, edb1, bf, bits, shve


def query(edb: dict, shve: dict,
          keywords: list,
          key: tuple, iv: bytes, bf: BloomFilter, g_serialized: bytes,
          paralleled=False,
          num_processes=None,
          benchmarking=False):
    """
    Query HXT for some keywords
    :param edb: OXT encrypted database
    :param shve: SHVE data
    :param keywords: list of keywords
    :param key: all keys
    :param iv: IV
    :param bf: bloomfilter
    :param g_serialized: serialized g value
    :param paralleled: should we parallel the process or not. NOTE: (maybe) incorrect parallelism.
    :param num_processes: number of processes used for parallelism
    :param benchmarking: whether this is a benchmark or not (default is False)
    :return: the list of results. If benchmarking, also return (number of xtags for stag) as the 2nd value of the tuple
    """
    pairing = PairingGroup('SS512')
    start_time = time.time()

    # ------ CLIENT  ------
    g = pairing.deserialize(g_serialized)
    assert g.initPP(), "ERROR: Failed to init pre-computation table for g."

    (K_P, K_S, K_X, K_I, K_Z, K_T, K_H) = key

    num_bits = bf.num_bits

    # client generates stag
    stag = cal_cmac_aes(K_T, convert_to_bytes(keywords[0]))

    logging.debug(('client generates stag time:', time.time() - start_time))

    result = []

    if not stag:
        if benchmarking:
            return result, 0
        else:
            return result

    start_time = time.time()
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
    logging.debug(('server get keys with stag time:', time.time() - start_time))

    # ---------------- CLIENT -------------
    start_time = time.time()
    # list of (e, y) with y is Element
    oxt_t_set_tuples = t_set_result
    if not oxt_t_set_tuples:
        if benchmarking:
            return result, len(t_set_result)
        else:
            return result

    # Concat xtoken with OXT Tuple
    xtoken_tuples = list()
    w1 = keywords[0]
    xterms = keywords[1:]

    num_t_set_stag = len(oxt_t_set_tuples)
    if paralleled:
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_worker_cal_xtokens_client,
                initargs=(K_Z, K_X, w1, xterms, pairing, g, oxt_t_set_tuples)) as pool:
            # pool = multiprocessing.Pool()
            xtoken_tuples = pool.map(cal_xtokens_client_parallel, [c for c in range(num_t_set_stag)])

    else:
        for c in range(num_t_set_stag):
            xtoken_tuple = cal_xtokens_client(K_Z, K_X, w1, c, xterms, pairing, g, oxt_t_set_tuples)

            xtoken_tuples.append(xtoken_tuple)  # each pair is ((e, y_c), xtokens_[c])

    logging.debug(('client generates xtokens time:', time.time() - start_time))
    # ---------------- SERVER -------------
    start_time = time.time()
    # Server is generating xtag

    xtags_tuples = list()
    if paralleled:
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_worker_cal_xtags_server,
                initargs=(pairing,)) as pool:
            xtags_tuples = pool.map(cal_xtags_server_parallel, xtoken_tuples)
    else:
        for c in range(len(xtoken_tuples)):
            xtags_tuples.append(cal_xtags_server(xtoken_tuples[c], pairing))  # each pair is ((e, y) -> xtags_of_c)

    logging.debug(('Server generating xtag time:', time.time() - start_time))

    start_time = time.time()
    xtags_hash_tuples = list()

    if paralleled:
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_worker_cal_xtags_hashes_server,
                initargs=(bf.hash_seeds, num_bits)) as pool:
            xtags_hash_tuples = pool.map(cal_xtags_hashes_server_parallel, xtags_tuples)

    else:
        for xtags_tuple in xtags_tuples:
            # there is a little bit of change in the algorithm here, just the order. Nothing special
            # Instead of computer hashes after computing each xtag, the map() operation above
            # server computes all xtags, then computes hashes for each of them later

            # each pair is ((e, y) -> hashes_of_xtags_of_c)
            xtags_hash_tuples.append(cal_xtags_hashes_server(xtags_tuple, bf.hash_seeds, num_bits))

    logging.debug(('Server generating xtag hashes time:', time.time() - start_time))

    start_time = time.time()
    # Start to match HVE
    es = list()

    if paralleled:
        # parallel processing
        with multiprocessing.Pool(
                processes=num_processes,
                initializer=init_worker_matching_shve,
                initargs=(K_H, iv, shve)) as pool:
            # pool = multiprocessing.Pool()
            tmp = pool.map(matching_shve_parallel, xtags_hash_tuples)
            for e in tmp:
                if e is not None:
                    es.append(e)
            logging.debug(es)
    else:
        for hash_tuple in xtags_hash_tuples:
            e = matching_shve(hash_tuple, K_H, iv, shve)
            if e is not None:
                es.append(e)

    logging.debug(('Server match SHVE time:', time.time() - start_time))

    # ---------------- CLIENT -------------
    start_time = time.time()
    # client decrypt e
    K_e = cal_cmac_aes(K_S, convert_to_bytes(keywords[0]))
    rinds = [decrypt_aes(K_e, iv, e) for e in es]
    result = [get_rind(base64.decodebytes(rind), K_P).decode() for rind in rinds]

    logging.debug(('client decrypt e time:', time.time() - start_time))

    if benchmarking:
        return result, len(t_set_result)
    else:
        return result


def init_encode_shve(K_H, bits):
    var_dict['K_H'] = K_H
    var_dict['bits'] = bits


def encode_shve_parallel(start_index, end_index):
    shve_tuples = list()
    K_H = var_dict['K_H']
    bits = var_dict['bits']

    for i in range(start_index, end_index):
        shve_tuples.append(encode_shve(K_H, i, bits[i]))

    return shve_tuples


def init_worker_cal_xtokens_client(K_Z, K_X, w1, xterms, pairing, g, oxt_t_set_tuples):
    var_dict['K_Z'] = K_Z
    var_dict['K_X'] = K_X
    var_dict['w1'] = w1
    var_dict['xterms'] = xterms
    var_dict['pairing'] = pairing
    var_dict['g'] = g
    var_dict['oxt_t_set_tuples'] = oxt_t_set_tuples


def cal_xtokens_client_parallel(c):
    K_Z = var_dict['K_Z']
    K_X = var_dict['K_X']
    w1 = var_dict['w1']
    xterms = var_dict['xterms']
    pairing = var_dict['pairing']
    g = var_dict['g']
    oxt_t_set_tuples = var_dict['oxt_t_set_tuples']

    return cal_xtokens_client(K_Z, K_X, w1, c, xterms, pairing, g, oxt_t_set_tuples)


def cal_xtokens_client(K_Z, K_X, w1, c, xterms, pairing, g, oxt_t_set_tuples):
    z = cal_cmac_aes(K_Z, convert_to_bytes(w1) + convert_to_bytes(c))
    e_z = pairing.init(ZR, convert_int_from_bytes(z))
    xtokens_serialized = list()
    for xterm in xterms:
        kxw = cal_cmac_aes(K_X, convert_to_bytes(xterm))
        e_kxw = pairing.init(ZR, convert_int_from_bytes(kxw))

        xtoken = g ** (e_z * e_kxw)

        xtokens_serialized.append(pairing.serialize(xtoken))

    return oxt_t_set_tuples[c], xtokens_serialized  # each pair is ((e, y_c), xtokens_[c])


def init_worker_matching_shve(K_H, iv, shve):
    var_dict['K_H'] = K_H
    var_dict['iv'] = iv
    var_dict['shve'] = shve


def matching_shve_parallel(hash_tuple):
    K_H = var_dict['K_H']
    iv = var_dict['iv']
    shve = var_dict['shve']
    return matching_shve(hash_tuple, K_H, iv, shve)


def matching_shve(hash_tuple, K_H, iv, shve):
    # ---------------- CLIENT-------------
    # Compute HVE key on client (SHVE Keygen)
    # Generate a random mask
    # hashTuple._2 is v_c in the paper
    D0 = bytearray(16)
    xtag_hashes = hash_tuple[1]
    for i in range(len(xtag_hashes)):
        v_l_i = 1
        l_i = xtag_hashes[i]  # u_j in Algorithm 2 and l_i in SHVE
        # d is hve_cipher
        d = encode_shve_cipher(K_H, v_l_i, l_i)
        for j in range(len(d)):
            D0[j] = D0[j] ^ d[j]

    # Randomly choose a key K
    K = random_secure(len(D0))
    for i in range(len(D0)):
        D0[i] = D0[i] ^ K[i]
    # D0 done
    # use K to encrypt "0"
    D1 = encrypt_aes(K, iv, convert_int_to_bytes(0))  # D1 done

    # ---------------- SERVER -------------
    # retrieve HVE ciphertext SHVE.Query.
    # hveCipher should XORed of all c_l (see the first part of k_prime in SHVE algorithm)
    hve_cipher = bytearray(16)
    for i in range(len(xtag_hashes)):
        l_i = xtag_hashes[i]  # u_j in Algorithm 2 and l_i in SHVE
        key_l_i = encode_shve_key(l_i)
        c_l_i = shve[key_l_i]

        for j in range(len(c_l_i)):
            hve_cipher[j] = hve_cipher[j] ^ c_l_i[j]

    # SHVE decrypt, hve_cipher is c in the paper
    k_prime = bytearray(len(D0))
    for i in range(len(D0)):
        k_prime[i] = hve_cipher[i] ^ D0[i]

    # noinspection PyBroadException
    try:
        # if we can decrypt, it is valid document; otherwise, exception is thrown
        decrypt_aes(bytes(k_prime), iv, D1)
        e = hash_tuple[0][0]
        return e

    except:
        # traceback.print_exc()
        return None


def init_worker_cal_xtags_server(pairing):
    var_dict['pairing'] = pairing


def cal_xtags_server_parallel(xtoken_tuple):
    pairing = var_dict['pairing']
    return cal_xtags_server(xtoken_tuple, pairing)


def cal_xtags_server(xtoken_tuple, pairing):
    xtags = set()
    e_y_tuple = xtoken_tuple[0]
    y_c = pairing.deserialize(e_y_tuple[1])  # a little bit different from OXT
    xtokens_serialized = xtoken_tuple[1]
    for xtoken_serialized in xtokens_serialized:
        xtag = pairing.deserialize(xtoken_serialized) ** y_c

        xtags.add(str(xtag))

    return e_y_tuple, list(xtags)  # each pair is ((e, y) -> xtags_of_c)


def init_worker_cal_xtags_hashes_server(bf_hash_seeds, bf_num_bits):
    var_dict['bf_hash_seeds'] = bf_hash_seeds
    var_dict['bf_num_bits'] = bf_num_bits


def cal_xtags_hashes_server_parallel(xtags_tuple):
    bf_hash_seeds = var_dict['bf_hash_seeds']
    bf_num_bits = var_dict['bf_num_bits']
    return cal_xtags_hashes_server(xtags_tuple, bf_hash_seeds, bf_num_bits)


def cal_xtags_hashes_server(xtags_tuple, bf_hash_seeds, bf_num_bits):
    # map xtags to hash values
    xtag_hashes = set()  # xtagHashes is the list of u_j for each xtag
    xtags = xtags_tuple[1]
    for c in range(len(xtags)):
        # mimic set in bits array
        xtag = xtags[c]
        for hash_seed in bf_hash_seeds:
            pos = bloomfilter_hash(xtag, hash_seed) % bf_num_bits
            xtag_hashes.add(pos)

    return xtags_tuple[0], list(xtag_hashes)


def encode_shve(key: bytes, ind: int, value: bool) -> tuple:
    """
    Perform Symmetric HVE
    :param key: symmetric key
    :param ind: index
    :param value: value of the index
    :return: Symmetric HVE code
    """
    int_value = 1 if value else 0
    hve_cipher = encode_shve_cipher(key, int_value, ind)
    hve_key = encode_shve_key(ind)
    return hve_key, hve_cipher


def encode_shve_cipher(key: bytes, int_value: int, ind: int) -> bytes:
    return cal_cmac_aes(key, (str(int_value) + str(ind)).encode())


def encode_shve_key(ind: int) -> bytes:
    return base64.encodebytes(hash_sha256(convert_int_to_bytes(ind)))
