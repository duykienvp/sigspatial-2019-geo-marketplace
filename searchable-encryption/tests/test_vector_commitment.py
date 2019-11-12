"""
Test vector commitment
"""
import logging
from charm.core.math.integer import randomBits

from .context import vectorcommitment, Config

logger = logging.getLogger(__name__)


def test_vector_commitment():
    k = Config.benchmark_vector_commitment_key_sizes[0]
    l = Config.benchmark_vector_commitment_message_elem_bit_len
    q = Config.benchmark_vector_commitment_vector_lens[0]

    n, a, s, e, p_1, p_2 = vectorcommitment.key_gen(k, l, q)

    m = list()

    for i in range(q):
        m.append(int(randomBits(l)))

    c, aux = vectorcommitment.commit(m, s, n)

    for i in range(q):
        lamda_i = vectorcommitment.open(m, i, e[i], s, p_1, p_2)

        verification_result = vectorcommitment.verify(c, m[i], i, s, lamda_i, e, n, l)
        logging.info(verification_result)
        assert verification_result



