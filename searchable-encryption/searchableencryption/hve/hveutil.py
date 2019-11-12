""" Some utility functions
"""
import logging

from searchableencryption.hve import hierarchicalencoding, grayencoding
from searchableencryption.toolbox import binexprminimizer
from searchableencryption.rangequery.rangeutil import LocationEncoding

WILDCARD = '*'
PARAM_KEY_N0 = 'n0'
PARAM_KEY_N1 = 'n1'

logger = logging.getLogger(__name__)


def check_size(indices: list, queries: list) -> int:
    """ Check whether size of all indices and queries are the same

    :param list indices: list of all indices
    :param list queries: list of all queries
    :returns: the size when size of all indices and queries are the same or -1
              if lists does not have same size
    """
    width = 0
    # get the first length if any
    if indices:
        width = len(indices[0])
    elif queries:
        width = len(queries[0])

    # at this point, width will be the length of 1 of the indices or queries,
    # or will still be 0 if there is no index or query
    for index in indices:
        if len(index) != width:
            print('Indices are not the same width')
            return -1

    for query in queries:
        if len(query) != width:
            print('Queries and indices not the same width')
            return -1

    return width


def get_unit_element(group, component):
    """ Get unit element of a group component (ZR, G1, G2, or GT)

    :param PairingGroup group: a group object
    :param int component: a group component (ZR, G1, G2, or GT)
    """
    return group.random(component) ** 0


def convert_to_documents(
        database: dict,
        d: int,
        location_encoding: LocationEncoding) -> dict:
    """
    Convert an database of location of points (id -> [x, y]) to encoding of documents (e.g. id -> [0, 1, 0, ...]
    :param database: database
    :param d: dimension
    :param location_encoding: type of encoding, HIERARCHICAL_ENCODING or GRAY_ENCODING
    :return: converted database
    """
    if location_encoding == LocationEncoding.HIERARCHICAL:
        cell_id_encode_func = hierarchicalencoding.encode_cell_id
    elif location_encoding == LocationEncoding.GRAY:
        cell_id_encode_func = grayencoding.encode_cell_id
    else:
        raise TypeError('Unknown location encoding')

    converted_db = dict()
    for item_id, location in database.items():
        x = location[0]
        y = location[1]
        if x < 0 or y < 0 or d <= x or d <= y:
            raise ValueError('Location is out of range')

        encoded_loc = cell_id_encode_func(d, x, y)
        converted_db[item_id] = encoded_loc

    return converted_db


def perform_bin_expr_min(cell_bins):
    """
    Perform binary expression minimization.
    This method simply calls another method in binary expression minimization module.
    However, this exists so that other HVE related modules can focus on this
    :param cell_bins: encoded cells as binary
    :return: minimized tokens
    """
    return binexprminimizer.perform_bin_expr_min(cell_bins, wildcard=WILDCARD)


def encode_cell_queries(cell_queries, d: int, location_encoding: LocationEncoding):
    """
    Encoding cell queries according to the location encoding
    :param cell_queries:
    :param d:
    :param location_encoding:
    :return:
    """
    if location_encoding == LocationEncoding.HIERARCHICAL:
        cell_id_encode_func = hierarchicalencoding.encode_cell_id
    elif location_encoding == LocationEncoding.GRAY:
        cell_id_encode_func = grayencoding.encode_cell_id
    else:
        raise TypeError('Unknown location encoding')

    # logger.debug('encoding cell bins')
    cell_bins = list()
    cell_bins.extend([cell_id_encode_func(d, cell[0], cell[1]) for cell in cell_queries])

    # logger.debug('encoding cell bins DONE')

    return cell_bins


def convert_range_query_to_predicate_queries(
        rectangle_query: tuple,
        d: int,
        location_encoding: LocationEncoding) -> list:
    """
    Convert range query in rectangle to a list of queries in keywords
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param location_encoding: type of encoding, HIERARCHICAL_ENCODING or GRAY_ENCODING
    :return: list of queries in keywords
    """
    x_top_left, y_top_left, x_len, y_len = rectangle_query

    cells = [[x_top_left + x, y_top_left + y]for x in range(x_len) for y in range(y_len)]

    cell_bins = encode_cell_queries(cells, d, location_encoding)

    return perform_bin_expr_min(cell_bins)


def convert_multiple_range_queries_to_predicate_queries(
        rectangle_queries: list,
        d: int,
        location_encoding: LocationEncoding) -> list:
    """
    Convert a range as multiple rectangle range queries to a list of queries in keywords
    :param rectangle_queries: list of tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param location_encoding: type of encoding, HIERARCHICAL_ENCODING or GRAY_ENCODING
    :return: list of queries in keywords
    """
    cells = list()

    for rectangle_query in rectangle_queries:

        x_top_left, y_top_left, x_len, y_len = rectangle_query

        cells.extend([[x_top_left + x, y_top_left + y] for x in range(x_len) for y in range(y_len)])

    cell_bins = encode_cell_queries(cells, d, location_encoding)

    return perform_bin_expr_min(cell_bins)


def convert_multiple_cell_queries_to_predicate_queries(
        cell_queries: list,
        d: int,
        location_encoding: LocationEncoding) -> list:
    """
    Convert cell queries into a list of queries in keywords
    :param cell_queries: list of query cells as (x, y)
    :param d: dimension
    :param location_encoding: type of encoding, HIERARCHICAL_ENCODING or GRAY_ENCODING
    :return: list of queries in keywords
    """
    cell_bins = encode_cell_queries(cell_queries, d, location_encoding)

    return perform_bin_expr_min(cell_bins)


def read_rectangle_queries(file_path: str) -> list:
    """
    Read queries from file where each line is a list of elements
    :param file_path:
    :return:
    """
    queries = list()
    with open(file_path, 'r') as f:
        for line in f:
            line = line.rstrip()
            queries.append(tuple(line.split(' ')))
    return queries


def count_non_wildcard(I_stars: list) -> int:
    """
    Count the number of non-wildcard values in list of queries
    :param I_stars: list of queries
    :return: the number of non-wildcard values
    """
    non_wildcard_count = 0
    for I_star in I_stars:
        for i in range(len(I_star)):
            non_wildcard_count += 1 if I_star[i] != WILDCARD else 0
    return non_wildcard_count


def count_num_pairing(I_stars: list) -> int:
    """
    Count the number of pairing needed for comparing this list of queries
    :param I_stars: list of queries
    :return: the number of pairing
    """
    pairing_count = 0
    for I_star in I_stars:
        non_wildcard_count = 0
        for i in range(len(I_star)):
            non_wildcard_count += 1 if I_star[i] != WILDCARD else 0
        pairing_count += 1 + 2 * non_wildcard_count
    return pairing_count
