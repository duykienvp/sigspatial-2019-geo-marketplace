"""
Vector commitment based on "Vector Commitments and their Applications", Section 3.2.

Link: https://eprint.iacr.org/2011/495.pdf
"""
import logging
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import rsa
from charm.core.math.integer import isPrime, randomPrime, random, integer, reduce, toInt


logger = logging.getLogger(__name__)


def key_gen(key_size, message_elem_bit_len, vector_len):
    """
    Generate keys for vector commitment
    :param key_size: security key size (k in the paper)
    :param message_elem_bit_len: length of message in bits (l in the paper)
    :param vector_len: length of commitment vector (q in the paper)
    :return:
    """
    k = key_size
    l = message_elem_bit_len
    q = vector_len

    private_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=k,
        backend=default_backend()
    )
    private_numbers = private_key.private_numbers()

    n = private_numbers.public_numbers.n
    p_1 = private_numbers.p
    p_2 = private_numbers.q

    # logger.info(p_1)
    # logger.info(p_2)

    phi_n = (p_1 - 1) * (p_2 - 1)

    # generate list of primes [e_1,...,e_q], each has length (l+1)
    e = list()
    while True:
        tmp_prime = reduce(randomPrime(l + 1) % n)
        if isPrime(tmp_prime) and phi_n % int(tmp_prime) != 0:
            e.append(int(tmp_prime))
            if len(e) == q:
                break

    a = random(n)

    s = list()
    for i in range(q):
        tmp_exponent = integer(1) % n
        for j in range(q):
            if j != i:
                tmp_exponent = reduce(tmp_exponent * (integer(e[j]) % n))
        s_i = reduce(a ** tmp_exponent)
        s.append(int(s_i))

    return n, int(a), s, e, p_1, p_2


def commit(message, s, n):
    """
    Commit a vector `message` of length
    :param message: the message to commit
    :param s:
    :param n:
    :return: ciphertext, auxiliary
    """
    assert len(message) == len(s), 'Message and S must have same length'
    q = len(s)
    c = -1
    for i in range(q):
        tmp = reduce((integer(s[i]) % n) ** message[i])
        if c == -1:
            c = tmp
        else:
            c = reduce(c * tmp)

    aux = message

    return int(c), aux


def open(message, i, e_i, s, p_1, p_2):
    """
    Open position i of message
    :param message: the message to open
    :param i: the position of the message to open
    :param e_i:
    :param s:
    :param p_1:
    :param p_2:
    :return: lamda_i
    """
    assert len(message) == len(s), 'Message and S must have same length. Found {0} vs {1}'.format(len(message), len(s))
    assert i < len(message), 'Invalid position to open: length of message {0} {1}'.format(len(message), i)
    lamda_tmp = -1
    q = len(message)
    n = p_1 * p_2
    for j in range(q):
        if j != i:
            tmp = reduce((integer(s[j]) % n) ** message[j])
            if lamda_tmp == -1:
                lamda_tmp = tmp
            else:
                lamda_tmp = reduce(lamda_tmp * tmp)

    phi_n = (p_1 - 1) * (p_2 - 1)
    d = 1 / (integer(e_i) % phi_n)

    lamda_i = reduce(lamda_tmp ** reduce(d))

    return int(lamda_i)


def verify(c, m, i, s, lamda_i, e, n, message_elem_bit_len):
    """
    Verify the value at position i is m
    :param n:
    :param c: ciphertext
    :param m: value m
    :param i: position
    :param s:
    :param lamda_i:
    :param e:
    :param message_elem_bit_len: max bit length of m
    :return: whether the value at position i of ciphertext is m
    """
    if m.bit_length() <= message_elem_bit_len:
        return c == int(reduce((integer(s[i]) % n) ** m) * reduce((integer(lamda_i) % n) ** e[i]))
    return False
