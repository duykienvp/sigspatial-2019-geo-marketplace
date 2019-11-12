""" Test HVE with hierarchical encoding
"""
import random  # noqa: E402
from .context import hve, grayencoding, pairingcurves, GT,\
    parse_params_from_string, WILDCARD, binexprminimizer


def test_hve_ge_simple():
    """ A simple test
    """
    print("Start test_hve_ge_simple")
    group_param = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_256_SAMPLE)

    size = 2 ** 2

    (pk, sk) = hve.setup(width=size, group_param=group_param)
    print('Done setup')

    print('Testing predicate only')
    # I = [0, 0, 1, 0, 0]  # noqa: E741
    I = grayencoding.encode_cell_id(size, 2, 3)  # noqa: E741
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
    cell_bins = [grayencoding.encode_cell_id(size, cell[0], cell[1]) for cell in cells]
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

    I = grayencoding.encode_cell_id(size, 2, 0)  # noqa: E741
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

    print("Done test_hve_ge_simple")
