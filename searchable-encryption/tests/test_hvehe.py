""" Test HVE with hierarchical encoding
"""
import random  # noqa: E402

from .context import hve, hierarchicalencoding, pairingcurves, GT,\
    parse_params_from_string, WILDCARD, binexprminimizer, util


def test_hve_he_simple():
    """ A simple test
    """
    print("Start test_hve_he_simple")
    group_param = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_256_SAMPLE)

    dim = 4
    width = util.next_power_2(dim)

    (pk, sk) = hve.setup(width=width, group_param=group_param)
    print('Done setup')

    print('Testing predicate only')
    # I = [0, 0, 1, 0, 0]  # noqa: E741
    I = hierarchicalencoding.encode_cell_id(width, 2, 3)  # noqa: E741
    cipher = hve.encrypt(pk, I)
    print('Done encrypt')

    cells = [
        [0, 1],
        [0, 2],
        [0, 3],
        [1, 2],
        [1, 3],
        [2, 3],
        [3, 3]
    ]
    cell_bins = [hierarchicalencoding.encode_cell_id(width, cell[0], cell[1]) for cell in cells]
    # print(cell_bins)

    I_stars = binexprminimizer.perform_bin_expr_min(cell_bins, wildcard=WILDCARD)
    tokens = [hve.gen_token(sk, I_star) for I_star in I_stars]
    print('Done gen token')

    matched = False
    for token in tokens:
        matched = matched | hve.query(token, cipher, predicate_only=True, group=pk['group'])
        if matched:
            break

    if matched:
        print('Test PASSED')
    else:
        print('Test FAILED.')
    assert matched, 'Results not matched'

    I = hierarchicalencoding.encode_cell_id(width, 2, 0)  # noqa: E741
    cipher = hve.encrypt(pk, I)
    print('Done encrypt')

    matched = False
    for token in tokens:
        matched = matched | hve.query(token, cipher, predicate_only=True, group=pk['group'])
        if matched:
            break

    if not matched:
        print('Test PASSED')
    else:
        print('Test FAILED.')

    assert (not matched), 'Results matched when it should not'

    print("Done test_hve_he_simple")


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
