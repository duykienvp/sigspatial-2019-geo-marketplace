""" Test SEE range query
"""
import logging

from searchableencryption.rangequery.rangeutil import LocationEncoding
from .context import datasethelper, rangequery, rangeutil
from .context import hxt, hve, hveutil, pairingcurves, parse_params_from_string, util


logger = logging.getLogger(__name__)


def create_hardcoded_database() -> dict:
    """
    Create a sample hardcoded database.

    :return: sample database
    """
    database = dict()
    database[0] = (0, 0)
    database[1] = (1, 6)
    database[2] = (3, 4)
    database[3] = (5, 2)
    database[4] = (5, 5)
    database[5] = (6, 6)

    return database


def get_recovered_ids_set(recovered_ids_dict: dict) -> set:
    """
    Get recovered ids of all items in a dict as set
    :param recovered_ids_dict:
    :return: recovered ids of all items in a dict as set
    """
    recovered_ids = set()
    for word, ids in recovered_ids_dict.items():
        recovered_ids.update(ids)

    return recovered_ids


def test_range_query_hardcoded_hxt():
    """ Test hardcoded range query with HXT
    """
    d = 8  # dimension
    db = create_hardcoded_database()
    rectangle_query = (2, 2, 5, 4)

    expected_results = rangequery.search_plaintext_range_query(db, rectangle_query)

    password = 'test_password'
    results = range_query_hxt(db, rectangle_query, d, password, LocationEncoding.BRC)
    results_int = set([int(r) for r in results])
    if set(expected_results) != results_int:
        print('failed')

    test_ok = set(results_int) <= set(expected_results)
    assert test_ok, 'Error SEE range hardcoded HXT query'
    print('Test range query hardcoded HXT PASSED')


def test_range_query_random_hxt():
    """ Test random range query with HXT
    """
    d = 2 ** 10  # dimension
    n = 20  # number of items
    db = datasethelper.create_random_checkin_database(n, d)
    # db = datasethelper.load_database(Config.dataset_dir + 'gowalla_cells_1024_users_10000_encoding_brc.txt')

    x_top_left = y_top_left = d // 4
    x_len = d // 3
    y_len = d // 3
    rectangle_query = (x_top_left, y_top_left, x_len, y_len)

    expected_results = rangequery.search_plaintext_range_query(db, rectangle_query)

    password = 'test_password'
    results = range_query_hxt(db, rectangle_query, d, password, LocationEncoding.BRC)
    # print(results)
    results_int = set([int(r) for r in results])
    # print(results_int)
    # print(expected_results)
    if set(expected_results) != results_int:
        print('failed')

    test_ok = set(expected_results) <= set(results_int)
    assert test_ok, 'Error SEE range random HXT query'
    print('Test range query random HXT PASSED')


def range_query_hxt(db: dict, rectangle_query: tuple, d: int, password: str, encoding: LocationEncoding) -> set:
    """
    :param db: database
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param password: password to generate keys
    :return: set of results of the query with item ids converted to string
    """
    db = rangequery.convert_to_documents(db, d, encoding)

    key, iv, g_serialized, edb1, bf, bits, shve = hxt.setup(db, password, paralleled=True)

    results = range_query_hxt_encrypted(
        rectangle_query, d, encoding,
        edb1, shve, key, iv, bf, g_serialized)

    return results


def range_query_hxt_encrypted(
        rectangle_query: tuple, d: int, encoding: LocationEncoding,
        edb1: dict, shve: dict, key: tuple, iv: bytes, bf, g_serialized: bytes, h_max=0) -> set:
    """
    :param rectangle_query: tuple of (x_top_left, y_top_left, x_len, y_len)
    :param d: dimension
    :param edb1: OXT encrypted database
    :param shve: SHVE data
    :param key: all keys
    :param iv: IV
    :param bf: bloomfilter
    :param g_serialized: serialized g value
    :param h_max: the highest allowed level of the tree
    :return: set of results of the query with item ids converted to string
    """
    queries = rangequery.convert_to_keyword_queries(rectangle_query, d, h_max, encoding)

    results = set()
    for query in queries:
        result = hxt.query(edb1, shve, query, key, iv, bf, g_serialized, paralleled=True)
        results.update(result)

    return results