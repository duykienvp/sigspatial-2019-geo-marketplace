""" Test HVE with hierarchical encoding
"""
import random  # noqa: E402

from .context import hierarchicalencoding

def test_hierarchical_encoding_decomposition():
    """
    :return:
    """
    results = hierarchicalencoding.encode_range_query((0, 1, 3, 3), 8)
    expected_results = {'000001', '000011', '0001', '001001', '001100', '001101'}
    assert expected_results == set(results)

    results = hierarchicalencoding.encode_range_query((0, 1, 6, 3), 8)
    expected_results = {'000001', '000011', '0001', '001001', '001011', '0011', '100001', '100011', '1001'}
    assert expected_results == set(results)

    results = hierarchicalencoding.encode_range_query((0, 1, 3, 6), 8)
    expected_results = {'000001', '000011', '0001', '001001', '001100', '001101',
                        '0100', '011000', '011001', '010100', '010110', '011100'}
    assert expected_results == set(results)

    results = hierarchicalencoding.encode_range_query((0, 1, 6, 6), 8)
    expected_results = {'000001', '000011', '0001', '001001', '001011', '0011', '100001', '100011', '1001',
                        '0100', '010100', '010110', '0110', '011100', '011110',
                        '1100', '110100', '110110'}
    assert expected_results == set(results)

    results = hierarchicalencoding.encode_range_query((0, 0, 4, 8), 8)
    expected_results = {'00', '01'}
    assert expected_results == set(results)
    results = hierarchicalencoding.decompose_full_node('00', 2)
    expected_results = {'0000', '0001', '0010', '0011'}
    assert expected_results == set(results)

    print('DONE')
