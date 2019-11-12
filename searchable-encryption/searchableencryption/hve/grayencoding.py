""" HVE with gray encoding.
Section 3.3 of 'An Efficient Privacy-Preserving System for Monitoring Mobile Users:
Making Searchable Encryption Practical'

Link: https://dl.acm.org/citation.cfm?id=2557559
"""

encoded_dict = dict()
encoded_dict[1] = [[0], [1]]


def get_encoding_vector(dim: int) -> list:
    """ Get grey encoding binary representation of vector dimension dim.
    See Section 3.3 in `https://dl.acm.org/citation.cfm?id=2557559`

    :param int dim: dimension of the vector

    :returns: a list of binary representation
    """
    global encoded_dict

    if dim in encoded_dict:
        return encoded_dict[dim]

    # if dim == 1:
    #     return [[0], [1]]

    g_prev = get_encoding_vector(dim - 1)

    rep = list()

    for val in g_prev:
        rep.append([0] + val)

    for val in reversed(g_prev):
        rep.append([1] + val)

    encoded_dict[dim] = rep

    return rep


def encode_cell_id(d: int, x: int, y: int) -> list:
    """ Get grey encoding binary representation of cell (x, y) with the grid of d x d.
    See Section 3.3 in `https://dl.acm.org/citation.cfm?id=2557559`

    :param int d: dimension of the grid
    :param int x: x index
    :param int y: y index

    :returns: a list of binary representation
    """
    dim = d.bit_length() - 1

    g_dim = get_encoding_vector(dim)

    rep = g_dim[y] + g_dim[x]

    return rep
