""" Hidden Vector Encryption based on the Appendix of
'An Efficient Privacy-Preserving System for Monitoring Mobile Users:
Making Searchable Encryption Practical'

Link: https://dl.acm.org/citation.cfm?id=2557559

Names of variables follows the naming of the Appendix.

Even though in this implementation, the same group object is passed around
(it does not contain any private information),
one can easily adapt to only pass the parameters around
"""

from charm.toolbox.integergroup import IntegerGroup

from searchableencryption.hve.hveutil import WILDCARD, PARAM_KEY_N0, PARAM_KEY_N1, get_unit_element
from searchableencryption.toolbox.pairinggroup \
    import PairingGroup, G1, GT, pair, convert_params_to_string


def remove_private_group_params(group_param: dict) -> dict:
    """
    Remove private components of the group parameters
    :param group_param:
    :return:
    """
    group_param_copy = group_param.copy()
    group_param_copy.pop(PARAM_KEY_N0, None)
    group_param_copy.pop(PARAM_KEY_N1, None)

    return group_param_copy


def setup(width: int, group_param: dict):
    """
    Performs the setup algorithm for HVE.

    The group params should:
    - 'type': 'a1'
    - contain values for 'p', 'n', 'l'
    - contain 2 primes 'n0', 'n1' where n = n0 * n1

    :param int width: the length of attribute vector
    :param dict group_param: group parameters

    :returns: (publicKey, secretKey) pair
    """
    int_group = IntegerGroup()

    p = group_param[PARAM_KEY_N0]
    q = group_param[PARAM_KEY_N1]

    group_param_copy = remove_private_group_params(group_param)

    group = PairingGroup()
    group.init_from_str(convert_params_to_string(group_param_copy))

    g_q = random_gq(group, p, q)
    a = int(random_zr(int_group, p))

    u = {}
    h = {}
    w = {}
    for i in range(width):
        u[i] = random_gp(group, p, q)
        h[i] = random_gp(group, p, q)
        w[i] = random_gp(group, p, q)

    g = random_gp(group, p, q)
    v = random_gp(group, p, q)
    assert g.initPP(), "ERROR: Failed to init pre-computation table for g."
    assert v.initPP(), "ERROR: Failed to init pre-computation table for v."

    V = v * random_gq(group, p, q)
    A = pair(g, v) ** a
    U = {}
    H = {}
    W = {}
    for i in range(width):
        U[i] = u[i] * random_gq(group, p, q)
        H[i] = h[i] * random_gq(group, p, q)
        W[i] = w[i] * random_gq(group, p, q)

    pk = {'group': group,
          'g_q': g_q,
          'V': V,
          'A': A,
          'U': U,
          'H': H,
          'W': W}

    sk = {'group': group,
          'g_q': g_q,
          'a': a,
          'u': u,
          'h': h,
          'w': w,
          'g': g,
          'v': v,
          'p': p,
          'q': q}

    return pk, sk


def encrypt(pk, I, M=None):
    """ Encrypt a index vector I with values of components as 0 or 1, with optional message M

    The message is an element in GT. If it is not provided, the identity element of GT is used

    :param pk: public key
    :param I: a index vector
    :param M: message
    :returns: cipher text for I (and M if any)
    """
    int_group = IntegerGroup()
    g_q = pk['g_q']
    group = pk['group']
    n = group.order()
    s = int(random_zr(int_group, n))
    if M is None:
        M = get_unit_element(group, GT)
    C_prime = (pk['A'] ** s) * M

    Z = g_q ** int(random_zr(int_group, n))
    C_0 = (pk['V'] ** s) * Z

    C_1 = {}
    C_2 = {}
    U = pk['U']
    H = pk['H']
    W = pk['W']
    for i in range(len(I)):
        Z_1_i = g_q ** int(random_zr(int_group, n))
        C_1[i] = (((U[i] ** I[i]) * H[i]) ** s) * Z_1_i

        Z_2_i = g_q ** int(random_zr(int_group, n))
        C_2[i] = (W[i] ** s) * Z_2_i

    C = {'C_prime': C_prime,
         'C_0': C_0,
         'C_1': C_1,
         'C_2': C_2}

    return C


def gen_token(sk, I_star):
    """ Create search token for a given query I_star.
    Elements of I_star is 0, 1, or a `WILDCARD`.

    :param sk: secret key
    :param I_star: query
    :returns: search token
    """
    intGroup = IntegerGroup()

    u = sk['u']
    h = sk['h']
    w = sk['w']
    v = sk['v']
    g = sk['g']
    a = sk['a']
    p = sk['p']

    K_0 = g ** a
    r_1 = {}
    r_2 = {}
    for i in range(len(I_star)):
        r_1[i] = int(random_zr(intGroup, p))
        r_2[i] = int(random_zr(intGroup, p))

    for i in range(len(I_star)):
        if I_star[i] == WILDCARD:
            continue
        tmp = u[i] ** I_star[i]
        tmp = tmp * h[i]
        tmp = tmp ** r_1[i]
        tmp = tmp * (w[i] ** r_2[i])
        K_0 = K_0 * tmp

    K_1 = {}
    K_2 = {}
    for i in range(len(I_star)):
        K_1[i] = v ** r_1[i]
        K_2[i] = v ** r_2[i]

    token = {'I_star': I_star,
             'K_0': K_0,
             'K_1': K_1,
             'K_2': K_2}

    return token


def query(token, cipher, predicate_only=False, group=None):
    """ Evaluates if the predicate represented by `token` holds for ciphertext `cipher`.
    If evaluating predicate only
    (i.e. only check if `token` holds for ciphertext `cipher`
    and do not care about message in the cipher text),
    the group should be passed because the output of decryption will be compared
    to the identity element of GT in the group

    :param token: search token
    :param cipher: cipher text
    :param bool predicate_only: whether or not only evaluates predicate
    :param PairingGroup group: the pairing group, only needed if predicateOnly
    :returns: if predicateOnly, return whether the predicate represented by `token`
              holds for cipher text `cipher`;
              otherwise, return the decrypted message
              (which is the orginal message if the decryption succeeded
              or just a random element in GT)
    """
    I_star = token['I_star']
    C_prime = cipher['C_prime']
    C_0 = cipher['C_0']
    C_1 = cipher['C_1']
    C_2 = cipher['C_2']
    K_0 = token['K_0']
    K_1 = token['K_1']
    K_2 = token['K_2']

    tmp = 1
    for i in range(len(I_star)):
        if I_star[i] == WILDCARD:
            continue
        tmp = tmp * pair(C_1[i], K_1[i]) * pair(C_2[i], K_2[i])
    tmp = pair(C_0, K_0) / tmp
    M_prime = C_prime / tmp

    if predicate_only:
        assert group is not None, "Error: group must be provided when evaluate predicate only"
        M_identity = get_unit_element(group, GT)
        return M_identity == M_prime

    return M_prime


def serialize_public_key(width, pk, group_param):
    group = pk['group']

    g_q = pk['g_q']
    V = pk['V']
    A = pk['A']
    U = pk['U']
    H = pk['H']
    W = pk['W']

    g_q_serialized = group.serialize(g_q)
    V_serialized = group.serialize(V)
    A_serialized = group.serialize(A)
    U_serialized = dict()
    H_serialized = dict()
    W_serialized = dict()
    for i in range(width):
        U_serialized[i] = group.serialize(U[i])
        H_serialized[i] = group.serialize(H[i])
        W_serialized[i] = group.serialize(W[i])

    return group_param, g_q_serialized, V_serialized, A_serialized, U_serialized, H_serialized, W_serialized


def deserialize_public_key(width, group_param,
                           g_q_serialized, V_serialized, A_serialized, U_serialized, H_serialized, W_serialized):
    group = PairingGroup()
    group.init_from_str(convert_params_to_string(group_param))

    pk = dict()
    pk['group'] = group
    g_q = group.deserialize(g_q_serialized)
    V = group.deserialize(V_serialized)
    A = group.deserialize(A_serialized)
    U = dict()
    H = dict()
    W = dict()
    for i in range(width):
        U[i] = group.deserialize(U_serialized[i])
        H[i] = group.deserialize(H_serialized[i])
        W[i] = group.deserialize(W_serialized[i])
    pk['g_q'] = g_q
    pk['V'] = V
    pk['A'] = A
    pk['U'] = U
    pk['H'] = H
    pk['W'] = W

    return pk


def serialize_secret_key(width, sk, group_param):
    group = sk['group']

    g_q_serialized = group.serialize(sk['g_q'])
    a_serialized = sk['a']
    g_serialized = group.serialize(sk['g'])
    v_serialized = group.serialize(sk['v'])
    p_serialized = sk['p']
    q_serialized = sk['q']

    u_serialized = dict()
    h_serialized = dict()
    w_serialized = dict()
    for i in range(width):
        u_serialized[i] = group.serialize(sk['u'][i])
        h_serialized[i] = group.serialize(sk['h'][i])
        w_serialized[i] = group.serialize(sk['w'][i])

    return (group_param, g_q_serialized, a_serialized, g_serialized, v_serialized, p_serialized, q_serialized,
            u_serialized, h_serialized, w_serialized)


def deserialize_secret_key(width, group_param,
                           g_q_serialized, a_serialized, g_serialized, v_serialized, p_serialized, q_serialized,
                           u_serialized, h_serialized, w_serialized):
    group = PairingGroup()
    group.init_from_str(convert_params_to_string(group_param))

    sk = dict()
    sk['group'] = group

    sk['g_q'] = group.deserialize(g_q_serialized)
    sk['a'] = a_serialized
    sk['g'] = group.deserialize(g_serialized)
    sk['v'] = group.deserialize(v_serialized)
    sk['p'] = p_serialized
    sk['q'] = q_serialized

    u = dict()
    h = dict()
    w = dict()
    for i in range(width):
        u[i] = group.deserialize(u_serialized[i])
        h[i] = group.deserialize(h_serialized[i])
        w[i] = group.deserialize(w_serialized[i])

    sk['u'] = u
    sk['h'] = h
    sk['w'] = w

    return sk


def serialize_cipher(width, cipher, group):
    C_prime_serialized = group.serialize(cipher['C_prime'])
    C_0_serialized = group.serialize(cipher['C_0'])
    C_1_serialized = dict()
    C_2_serialized = dict()
    for i in range(width):
        C_1_serialized[i] = group.serialize(cipher['C_1'][i])
        C_2_serialized[i] = group.serialize(cipher['C_2'][i])

    return C_prime_serialized, C_0_serialized, C_1_serialized, C_2_serialized


def deserialize_cipher(width, C_prime_serialized, C_0_serialized, C_1_serialized, C_2_serialized, group):
    cipher = dict()

    cipher['C_prime'] = group.deserialize(C_prime_serialized)
    cipher['C_0'] = group.deserialize(C_0_serialized)

    cipher['C_1'] = dict()
    cipher['C_2'] = dict()

    for i in range(width):
        cipher['C_1'][i] = group.deserialize(C_1_serialized[i])
        cipher['C_2'][i] = group.deserialize(C_2_serialized[i])

    return cipher


def serialize_token(width, token, group):
    I_star_serialized = token['I_star']
    K_0_serialized = group.serialize(token['K_0'])
    K_1_serialized = dict()
    K_2_serialized = dict()
    for i in range(width):
        K_1_serialized[i] = group.serialize(token['K_1'][i])
        K_2_serialized[i] = group.serialize(token['K_2'][i])

    return I_star_serialized, K_0_serialized, K_1_serialized, K_2_serialized


def deserialize_token(width, I_star_serialized, K_0_serialized, K_1_serialized, K_2_serialized, group):
    token = dict()

    token['I_star'] = I_star_serialized
    token['K_0'] = group.deserialize(K_0_serialized)

    token['K_1'] = dict()
    token['K_2'] = dict()

    for i in range(width):
        token['K_1'][i] = group.deserialize(K_1_serialized[i])
        token['K_2'][i] = group.deserialize(K_2_serialized[i])

    return token


def random_gp(group: PairingGroup, p, q):
    return group.random(G1) ** q


def random_gq(group: PairingGroup, p, q):
    return group.random(G1) ** p


def random_zr(group: IntegerGroup, r):
    """ Generate a random element in Zr
    """
    return group.random(max=int(r))
