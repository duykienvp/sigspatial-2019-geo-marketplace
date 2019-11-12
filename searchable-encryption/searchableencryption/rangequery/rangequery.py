""" SSE with support for range query.
Based on Range covering technique: BRC Construction, Section 4.1 of
`Delegatable Pseudorandom Functions and Applications`:
Link: https://eprint.iacr.org/2013/379.pdf
"""
from searchableencryption.rangequery import rangeutil
from searchableencryption.rangequery.rangeutil import LocationEncoding, PREFIX_X_DEFAULT, PREFIX_Y_DEFAULT
from searchableencryption.hve import hveutil, hierarchicalencoding


def get_words_for_locations_brc(database: dict, d: int, prefix_x='', prefix_y='') -> dict:
    """
    Get words for locations of items in `database`.
    Location of an item is the tuple (x, y) of location (x, y) in a grid d x d with 0-based index.
    Each dimension of (x, y) is converted separately into covered nodes of the binary tree covering range [0, d-1],
    creating the list of words (as strings) `x_words` (and `y_words`), respectively.

    See more about Range covering techniques in Section 2.2 of `Practical Private Range Search Revisited`.
    Link: https://dl.acm.org/citation.cfm?id=2882911

    :param dict database: the database as id => (x, y)
    :param int d: range of a dimension
    :param str prefix_x: prefix to add to x_words
    :param str prefix_y: prefix to add to y_words
    :return: words of items as id => (x_words, y_words)
    """
    result = dict()
    for item_id, location in database.items():
        x = location[0]
        y = location[1]
        if x < 0 or y < 0 or d <= x or d <= y:
            raise ValueError('Location is out of range')

        x_words = rangeutil.get_covering_nodes(d, x)
        y_words = rangeutil.get_covering_nodes(d, y)
        if prefix_x:
            x_words = [prefix_x + word for word in x_words]
        if prefix_y:
            y_words = [prefix_y + word for word in y_words]

        result[item_id] = (x_words, y_words)

    return result


def get_brc_words_for_rectangle(rectangle_query: tuple, d: int) -> tuple:
    """
    Get words for a range represented as an rectangle with the top left position and length along x and y axes.
    Each dimension is processed separately into list of nodes of the binary tree covering range [0, d-1] that the edge
    of the rectangle cover.

    See more about Range covering techniques in Section 2.2 of `Practical Private Range Search Revisited`.
    Link: https://dl.acm.org/citation.cfm?id=2882911

    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param int d: range of a dimension
    :return: words of rectangle as (x_words, y_words): list of words in x/y dimensions
    """
    (x, y, x_len, y_len) = rectangle_query

    if x < 0 or y < 0 or d < x or d < y:
        raise ValueError('Location is out of range')

    x_end = x + x_len - 1
    y_end = y + y_len - 1
    if x_end < x or d <= x_end or y_end < y or d <= y_end:
        raise ValueError('Invalid length for rectangle')

    x_words = rangeutil.get_best_range_cover(d, x, x_end)
    y_words = rangeutil.get_best_range_cover(d, y, y_end)

    return x_words, y_words


def convert_to_documents_brc(
        db: dict,
        d: int,
        prefix_x=PREFIX_X_DEFAULT,
        prefix_y=PREFIX_Y_DEFAULT):
    """
    Convert an database of location of points (id -> [x, y]) to words id -> [w1, w2, ...] using BRC encoding
    :param db: database
    :param d: dimension
    :param str prefix_x: prefix to add to x dimension
    :param str prefix_y: prefix to add to y dimension
    :return: converted database
    """
    db_words = get_words_for_locations_brc(db, d, prefix_x, prefix_y)

    # x and y words of each item are combined to create 1 document.
    # Then the search should be: contain x_word AND y_word
    converted_db = dict()
    for item_id, (x_words, y_words) in db_words.items():
        converted_db[item_id] = x_words + y_words

    return converted_db


def convert_to_documents(
        db: dict,
        d: int,
        location_encoding: LocationEncoding,
        prefix_x=PREFIX_X_DEFAULT,
        prefix_y=PREFIX_Y_DEFAULT) -> dict:
    """
    Convert an database of location of points (id -> [x, y]) to words of documents (id -> [w1, w2, ...]
    :param db: database
    :param d: dimension
    :param LocationEncoding location_encoding: type of location encoding
    :param str prefix_x: prefix to add to x dimension
    :param str prefix_y: prefix to add to y dimension
    :return: converted database
    :raise: TypeError if location encoding is unknown
    """
    if location_encoding == LocationEncoding.BRC:
        return convert_to_documents_brc(db, d, prefix_x, prefix_y)
    elif location_encoding == LocationEncoding.HIERARCHICAL or location_encoding == LocationEncoding.GRAY:
        return convert_hierarchical_list_to_keywords(hveutil.convert_to_documents(db, d, location_encoding))
    else:
        raise TypeError('Unknown location encoding')


def convert_hierarchical_list_to_keywords(db: dict):
    """
    Convert each list of values into keywords. Applied to HIERARCHIAL or GREY encoding.
    Each 2 consecutive values become part of keyword of that level.
    Example: [0, 0, 1, 0, 1, 1] => ['00', '0010', '001011']
    :param db: database
    :return: converted database
    """
    converted_db = dict()
    for item_id, values in db.items():
        keywords = list()
        i = 0
        keyword = ""
        while i < len(values):
            keyword = keyword + str(values[i]) + str(values[i+1])
            keywords.append(keyword)
            i += 2
        converted_db[item_id] = keywords

    return converted_db


def limit_tree_level(db: dict,
                     h_max: int,
                     location_encoding: LocationEncoding,
                     has_prefix=True) -> dict:
    """
    Limit database to level h_max of the tree
    :param location_encoding:
    :param db: database of documents in BRC encoding of id => words
    :param h_max: the highest allowed level of the tree
    :param has_prefix: whether or not the words are prefixed
    :return: the limited database
    """
    res = dict()
    if location_encoding == LocationEncoding.BRC:
        allowed_len = h_max + 1 if has_prefix else h_max
    else:
        # HIERARCHICAL
        allowed_len = h_max * 2
    for doc_id, words in db.items():
        res[doc_id] = [w for w in words if allowed_len <= len(w)]

    return res


def convert_to_preorder_node_ids_range(keyword_ranges: list, width: int):
    """
    Convert keyword queries from path to preorder id
    :param keyword_ranges: keyword queries as paths
    :param width: domain range
    :return:
    """
    return [(rangeutil.get_to_pre_order_id_from_node_path(x, width),
             rangeutil.get_to_pre_order_id_from_node_path(y, width))
            for x, y in keyword_ranges]


def break_node_to_lower_levels(word: str, h_max: int) -> list:
    """
    Break current node (with id as word) to the nodes of level at least h_max
    :param word: node id as path
    :param h_max: highest allowed level of the tree
    :return: list of node ids (as path) of current broken to the nodes of level at least h_max
    """
    if h_max <= len(word):
        # no need to break this node
        return [word]

    # need to break this node
    w_left = word + rangeutil.TREE_LEFT_BRANCH_CHAR
    w_right = word + rangeutil.TREE_RIGHT_BRANCH_CHAR

    res = list()
    res.extend(break_node_to_lower_levels(w_left, h_max))
    res.extend(break_node_to_lower_levels(w_right, h_max))

    return res


def convert_to_keyword_queries_brc(
        rectangle_query: tuple,
        d: int,
        h_max: int,
        prefix_x=PREFIX_X_DEFAULT, prefix_y=PREFIX_Y_DEFAULT) -> list:
    """
    Convert range query in rectangle to a list of queries in keywords
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param h_max: the highest allowed level of the tree
    :param str prefix_x: prefix to add to x dimension
    :param str prefix_y: prefix to add to y dimension
    :return: list of queries in keywords
    """
    query_x_level, query_y_level = get_brc_words_for_rectangle(rectangle_query, d)

    query_x = [x[0] for x in query_x_level]  # only get words, because the returned also contain level
    query_y = [y[0] for y in query_y_level]  # only get words, because the returned also contain level

    # len must be at least h_max
    query_x_limited = list()
    for word in query_x:
        query_x_limited.extend(break_node_to_lower_levels(word, h_max))
    query_y_limited = list()
    for word in query_y:
        query_y_limited.extend(break_node_to_lower_levels(word, h_max))

    query_x = [prefix_x + word for word in query_x_limited]  # only get words, because the returned also contain level
    query_y = [prefix_y + word for word in query_y_limited]  # only get words, because the returned also contain level

    queries = [[x, y] for x in query_x for y in query_y]

    return queries


def convert_to_keyword_queries_hierarchical(
        rectangle_query: tuple,
        d: int,
        h_max: int) -> list:
    """
    Convert range query in rectangle to a list of queries in keywords
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param h_max: the highest allowed level of the tree
    :return: list of queries in keywords
    """
    queries = list()

    decompositions = hierarchicalencoding.encode_range_query(rectangle_query, d)
    # limit h_max
    for v in decompositions:
        limited_v = hierarchicalencoding.decompose_full_node(v, h_max)
        for q in limited_v:
            queries.append([q])

    return queries


def convert_to_keyword_queries(
        rectangle_query: tuple,
        d: int,
        h_max: int,
        location_encoding: LocationEncoding,
        prefix_x=PREFIX_X_DEFAULT, prefix_y=PREFIX_Y_DEFAULT) -> list:
    """
    Convert range query in rectangle to a list of queries in keywords
    :param location_encoding:
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param h_max: the highest allowed level of the tree
    :param str prefix_x: prefix to add to x dimension
    :param str prefix_y: prefix to add to y dimension
    :return: list of queries in keywords
    """
    if location_encoding == LocationEncoding.BRC:
        return convert_to_keyword_queries_brc(rectangle_query, d, h_max, prefix_x, prefix_y)
    else:
        return convert_to_keyword_queries_hierarchical(rectangle_query, d, h_max)


def search_plaintext_range_query(database: dict, rectangle_query: tuple) -> set:
    """
    Search on plaintext for each word in each item
    :param dict database: the database as id => (x, y)
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :return:
    """
    results = set()
    (x_top_left, y_top_left, x_len, y_len) = rectangle_query
    for item_id, location in database.items():
        x = location[0]
        y = location[1]
        if x_top_left <= x <= x_top_left + x_len - 1:
            if y_top_left <= y <= y_top_left + y_len - 1:
                results.add(item_id)

    return results
