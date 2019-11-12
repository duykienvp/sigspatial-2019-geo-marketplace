""" HVE with hierarchical encoding.
Section 3.2 of 'An Efficient Privacy-Preserving System for Monitoring Mobile Users:
Making Searchable Encryption Practical'

Link: https://dl.acm.org/citation.cfm?id=2557559
"""


def encode_cell_id(d: int, x: int, y: int) -> list:
    """ Get hierarchical encoding binary representation of cell (x, y) with the grid of d x d.
    See Section 3.2 in `https://dl.acm.org/citation.cfm?id=2557559`

    :param int d: dimension of the grid
    :param int x: x index
    :param int y: y index

    :returns: a list of binary representation
    """
    rep = []
    mid = int(d / 2)

    if x < mid:
        rep.append(0)
    else:
        rep.append(1)
        x -= mid

    if y < mid:
        rep.append(0)
    else:
        rep.append(1)
        y -= mid

    if 1 < mid:
        rep.extend(encode_cell_id(mid, x, y))

    return rep


def encode_range_query(rectangle_query: tuple, d: int) -> list:
    """
    Get the list of encoded ranges for a range query (i.e., query decomposition)
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: range of a dimension
    :return: list of encoded ranges of the query
    """
    # print(rectangle_query, d)
    results = []
    (x, y, x_len, y_len) = rectangle_query
    if x == y == 0 and x_len == y_len == d:
        # no need to decompose
        results.append("")
        return results

    # now we need to decompose
    mid = d // 2

    # the "00" quarter: only have overlap if the top-left corner is inside this quarter
    if x < mid and y < mid:
        new_x_top_left = x
        new_y_top_left = y
        new_x_len = min(x_len, mid - x)
        new_y_len = min(y_len, mid - y)

        decomposed_values = encode_range_query((new_x_top_left, new_y_top_left, new_x_len, new_y_len), mid)
        for v in decomposed_values:
            results.append("00" + v)

    # the "01" quarter: only have overlap if the bottom-left corner is inside this quarter
    if x < mid and mid < y + y_len:
        new_x_top_left = x
        new_y_top_left = max(y, mid)
        new_x_len = min(x_len, mid - x)
        new_y_len = y + y_len - new_y_top_left  # bottom_left - top
        new_y_top_left = new_y_top_left - mid  # convert to new coordinate

        decomposed_values = encode_range_query((new_x_top_left, new_y_top_left, new_x_len, new_y_len), mid)
        for v in decomposed_values:
            results.append("01" + v)

    # the "10" quarter: only have overlap if the top_right corner is inside this quarter
    if mid < x + x_len and y < mid:
        new_x_top_left = max(x, mid)
        new_y_top_left = y
        new_x_len = x + x_len - new_x_top_left
        new_y_len = min(y_len, mid - y)
        new_x_top_left = new_x_top_left - mid  # convert to new coordinate

        decomposed_values = encode_range_query((new_x_top_left, new_y_top_left, new_x_len, new_y_len), mid)
        for v in decomposed_values:
            results.append("10" + v)

    # the "11" quarter: only have overlap if the bottom_right corner is inside this quarter
    if mid < x + x_len and mid < y + y_len:
        new_x_top_left = max(x, mid)
        new_y_top_left = max(y, mid)
        new_x_len = x + x_len - new_x_top_left
        new_y_len = y + y_len - new_y_top_left
        new_x_top_left = new_x_top_left - mid  # convert to new coordinate
        new_y_top_left = new_y_top_left - mid  # convert to new coordinate

        decomposed_values = encode_range_query((new_x_top_left, new_y_top_left, new_x_len, new_y_len), mid)
        for v in decomposed_values:
            results.append("11" + v)

    return results


def decompose_full_node(node_id, max_level):
    """
    Decompose a full node into smaller node at the max level max_level if the node is at the higher level
    :param node_id: node id as (e.g) string at the form "011000"
    :param max_level: the maximum level allowed
    :return:
    """
    current_level = len(node_id) // 2
    prev_ids = [node_id]
    for i in range(max_level - current_level):
        new_ids = list()
        for an_id in prev_ids:
            new_ids.append(an_id + '00')
            new_ids.append(an_id + '01')
            new_ids.append(an_id + '10')
            new_ids.append(an_id + '11')

        prev_ids = new_ids

    return prev_ids


