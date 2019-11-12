""" Utilities for range search
"""
from enum import Enum


class LocationEncoding(Enum):
    BRC = 'BRC'  # used in the paper
    HIERARCHICAL = 'HIERARCHICAL'  # available but not used in the paper
    GRAY = 'GRAY'  # not available now


TREE_LEFT_BRANCH_CHAR = '0'
TREE_RIGHT_BRANCH_CHAR = '1'
PREFIX_X_DEFAULT = 'x'
PREFIX_Y_DEFAULT = 'y'


def get_covering_nodes(d: int, value: int) -> list:
    """
    Get nodes that cover location value in a range of from [0, d-1].
    d is a power of 2.
    The binary tree is built bottom-up with the leaf nodes are values from 0 to d-1.
    Path to a node is represented by a string of 0-1 with 0 is left and 1 is right.
    For example, for a range of d = 8, node 2 is covered by:
      - root node [0,7]: empty string and IS NOT considered
      - node [0,3]: string '0'
      - node [2,3]: string '01'
      - node [2]: string '010'
    So, get_covered_nodes(8, 2) = ['0', '01', '010']

    :param int d: length of range, assuming to be a power of 2
    :param int value: location in range [0, d-1]
    :return: list of string representations of paths of nodes covering location value
    """
    bit_len = (d - 1).bit_length()
    res = ['']
    for i in range(bit_len - 1, -1, -1):
        # reverse loop
        node_id = res[-1] + str(get_nth_bit(value, i))
        res.append(node_id)
    del res[0]

    return res


def get_covered_range(node_path: str, d: int):
    """
    Get range covered by a node represented by path of the node.
    Example:
    >>> get_covered_range('10', 8)
    >>> (4, 5)
    :param node_path:
    :param d:
    :return: (start, end_inclusive)
    """
    bit_len = d.bit_length() - 1
    start = 0
    current_size = d
    for i in range(len(node_path)):
        if node_path[i] == TREE_RIGHT_BRANCH_CHAR:
            start += current_size // 2
        current_size //= 2
    end = start + 2 ** (bit_len - len(node_path))

    return start, end - 1


def get_pre_order_node_id(node_path: str, d: int):
    """
    Get a number as the node id for a node in the given path on the binary tree covering range [0, d-1]
    The node id is assigned using pre-order traverse on the tree
    :param node_path:
    :param d:
    :return:
    """
    v = 0
    for i in range(len(node_path)):
        if node_path[i] == TREE_LEFT_BRANCH_CHAR:
            v += 1
        else:
            v += d
        d //= 2
    return v


def get_to_pre_order_id_from_node_path(node_path: str, d: int):
    """
    Get Pre-order id of a node from its path, prefixed by 'x' or 'y'
    :param node_path:
    :param d:
    :return:
    """
    pre_order_id = get_pre_order_node_id(node_path[1:], d)
    if node_path[0] == 'y':
        max_node_count = 2 * d - 1  # maximum nodes of the tree covering the range [0, d-1]
        pre_order_id += max_node_count

    return pre_order_id


def get_nth_bit(a: int, n: int) -> int:
    """
    Get n-th bit of a
    :param a: value to get bit
    :param n: the position to get bit
    :return: the n-th bit of a
    """
    return 1 if not not(a & (1 << n)) else 0


def get_best_range_cover(d: int, start: int, end: int) -> list:
    """
    Get plain text of best range cover of range [start, end] in domain d, which is assumed to be power of 2.
    More detailed in: https://eprint.iacr.org/2013/379.pdf
    :param d: length of rain
    :param start: start of range
    :param end: end of range
    :return: list of string representations of nodes covering
    """
    if end < start:
        raise ValueError('Invalid value of range [start, end]')

    if start == end:
        node_id = get_covering_nodes(d, start)[-1]
        node_level = (d - 1).bit_length()
        return [(node_id, node_level)]

    # now start < end
    start_node_id = get_covering_nodes(d, start)[-1]
    end_node_id = get_covering_nodes(d, end)[-1]
    #     print(start_node_id)
    #     print(end_node_id)

    result = list()

    # finds the first bit in which start and end differ
    t = 0
    while t < len(start_node_id) and start_node_id[t] == end_node_id[t]:
        t += 1
    #     print('t', t)

    if check_left_most(start_node_id, t):
        #         print('is left most 1')
        # start is left most
        if check_right_most(end_node_id, t):
            #             print('is right most 1')
            # end is right most => get entire node
            node_id = start_node_id[:t]
            node_level = t
            return [(node_id, node_level)]
        else:
            # end is NOT right most => get entire LEFT child
            #             print('is NOT right most 1')
            node_id = start_node_id[:t+1]
            node_level = t + 1
            result.append((node_id, node_level))
    else:
        # print('is NOT left most 1')
        mu = len(start_node_id) - 1
        while t < mu and start_node_id[mu] == TREE_LEFT_BRANCH_CHAR:
            mu -= 1
        #         print('mu', mu)
        for i in range(t + 1, mu + 1):
            if start_node_id[i] == TREE_LEFT_BRANCH_CHAR:
                #                 print('left i', i)
                node_id = start_node_id[:i] + TREE_RIGHT_BRANCH_CHAR
                node_level = i
                result.append((node_id, node_level))
        node_id = start_node_id[:mu+1]
        node_level = mu
        result.append((node_id, node_level))
    if check_right_most(end_node_id, t):
        # end is right most => get entire RIGHT child
        #         print('is right most 2')
        node_id = end_node_id[:t+1]
        node_level = t + 1
        result.append((node_id, node_level))
    else:
        #         print('is NOT right most 2')
        nu = len(end_node_id) - 1
        while t < nu and end_node_id[nu] == TREE_RIGHT_BRANCH_CHAR:
            nu -= 1
        #         print('nu', nu)
        for i in range(t + 1, nu + 1):
            if end_node_id[i] == TREE_RIGHT_BRANCH_CHAR:
                node_id = end_node_id[:i] + TREE_LEFT_BRANCH_CHAR
                node_level = i
                result.append((node_id, node_level))
        node_id = end_node_id[:nu+1]
        node_level = nu
        result.append((node_id, node_level))

    return result


def check_left_most(a: str, pos: int) -> bool:
    """ Check if a is the left most child of the tree start at the node level `pos` in the path of a
    :param str a: a path
    :param int pos: node level
    :return: whether a is the left most child or not
    """
    for i in range(pos, len(a)):
        if a[i] == TREE_RIGHT_BRANCH_CHAR:
            return False

    return True


def check_right_most(a: str, pos: int) -> bool:
    """ Check if a is the right most child of the tree start at the node level `pos` in the path of a
    :param str a: a path
    :param int pos: node level
    :return: whether a is the right most child or not
    """
    for i in range(pos, len(a)):
        if a[i] == TREE_LEFT_BRANCH_CHAR:
            return False

    return True


def map_point_to_cell(
        x: float, y: float,
        min_x: float, max_x: float,
        min_y: float, max_y: float,
        num_cells: int) -> tuple:
    """
    Given a space [min_x, max_x] for x dimension and [min_y, max_y] in y dimension,
    and the partition of the space into grid of num_cells x num_cells,
    map a point (x, y) in the space to cell id of the cell of the grid containing point (x, y)
    :param x:
    :param y:
    :param min_x:
    :param max_x:
    :param min_y:
    :param max_y:
    :param num_cells:
    :return: (cell_id_x, cell_id_y)
    :raise: ValueError if point (x, y) is out of range
    """
    if x < min_x or max_x < x or y < min_y or max_y < y:
        raise ValueError('Point out of range')
    x_cell_len = abs(max_x - min_x) / num_cells
    y_cell_len = abs(max_y - min_y) / num_cells
    cell_x = (x - min_x) // x_cell_len
    cell_y = (y - min_y) // y_cell_len

    return cell_x, cell_y
