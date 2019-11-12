"""
Test OXT
"""
from collections import defaultdict
from time import time
from .context import oxt, hxt, Config


def create_sample_database(n: int) -> dict:
    """
    Create a sample database with n items from 0 to n-1.

    the i-th item contains list if numbers from 0 to i-1

    :param int n: number of items
    :return: sample database
    """
    database = dict()
    for i in range(n):
        database[i] = [j for j in range(i)]

    return database


def prepare_expected_results(database: dict) -> dict:
    """
    Search on plaintext for each word in each item
    :param database:
    :return:
    """
    results = defaultdict(list)
    for item_id, value in database.items():
        for word in value:
            results[word].append(item_id)

    return results


def test_oxt_construction():
    """ Test OXT
    """
    n = 100
    database = create_sample_database(n)
    expected_results = prepare_expected_results(database)

    password = 'test_password'

    start = time()
    key, iv, g_serialized, edb1, bf, bits = oxt.setup(
        database,
        password,
        bloomfilter_file=None,
        bf_false_positive_rate=Config.crypto_bloom_filter_false_positive_rate,
        paralleled=False,
        num_processes=None)
    end1 = time()
    keywords = ['x1010', 'y001011']
    # keywords = ['1', '2']
    result = oxt.query(edb1, keywords, key, iv, bf, g_serialized)

    end = time()

    print(result)

    print('Time gen:', end1 - start)
    print('Time query:', end - end1)
    print('Time all:', end - start)


def test_hxt_construction():
    """
    Test HXT
    """
    n = 100
    database = create_sample_database(n)
    expected_results = prepare_expected_results(database)

    password = 'test_password'

    start = time()
    key, iv, g_serialized, edb1, bf, bits, shve = hxt.setup(database, password)
    end1 = time()

    keywords = [1, 2]
    # keywords = ['1', '2']
    result = hxt.query(edb1, shve, keywords, key, iv, bf, g_serialized)

    end = time()

    print(result)
    print('Time gen:', end1 - start)
    print('Time query:', end - end1)
    print('Time all:', end - start)


