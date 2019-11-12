""" Binary Expression Minimization using PyEDA package,
which in turn uses a C extension to the famous Berkeley Espresso library.

See: https://pyeda.readthedocs.io/en/latest/2llm.html
"""
import logging

from pyeda.boolalg.expr import Expression, Or, And
from pyeda.inter import exprvars, expr, espresso_exprs

# ReadTheDocs doesn't build C extensions
# See http://docs.readthedocs.org/en/latest/faq.html for details
import os
if os.getenv('READTHEDOCS') == 'True':
    pass
else:
    from pyeda.boolalg.espresso import FTYPE
    from pyeda.boolalg.espresso import set_config, espresso

CONFIG = dict(
    single_expand=True,
    remove_essential=True,
    force_irredundant=False,
    unwrap_onset=True,
    recompute_onset=False,
    use_super_gasp=False,
)

logger = logging.getLogger(__name__)


def prepare_expressions(base_tokens: list, X):
    dim = len(base_tokens[0])
    f = expr(0)  # start of an OR
    for baseToken in base_tokens:
        prod = expr(1)  # start of an AND
        for i in range(dim):
            if baseToken[i] == 1:
                prod = prod & X[i]
            else:
                prod = prod & ~X[i]
        f = f | prod

    return f


def espresso_exprs_fast(*exprs):
    """Return a tuple of expressions optimized using Espresso ('fast' mode).

    The variadic *exprs* argument is a sequence of expressions.

    Cloned from PyEDA code
    """
    for f in exprs:
        if not (isinstance(f, Expression) and f.is_dnf()):
            raise ValueError("expected a DNF expression")

    support = frozenset.union(*[f.support for f in exprs])
    inputs = sorted(support)

    # Gather all cubes in the cover of input functions
    fscover = set()
    for f in exprs:
        fscover.update(f.cover)

    ninputs = len(inputs)
    noutputs = len(exprs)

    cover = set()
    for fscube in fscover:
        invec = list()
        for v in inputs:
            if ~v in fscube:
                invec.append(1)
            elif v in fscube:
                invec.append(2)
            else:
                invec.append(3)
        outvec = list()
        for f in exprs:
            for fcube in f.cover:
                if fcube <= fscube:
                    outvec.append(1)
                    break
            else:
                outvec.append(0)

        cover.add((tuple(invec), tuple(outvec)))

    set_config(**CONFIG)

    cover = espresso(ninputs, noutputs, cover, intype=FTYPE)
    return _cover2exprs(inputs, noutputs, cover)


def _cover2exprs(inputs, noutputs, cover):
    """Convert a cover to a tuple of Expression instances."""
    fs = list()
    for i in range(noutputs):
        terms = list()
        for invec, outvec in cover:
            if outvec[i]:
                term = list()
                for j, v in enumerate(inputs):
                    if invec[j] == 1:
                        term.append(~v)
                    elif invec[j] == 2:
                        term.append(v)
                terms.append(term)
        fs.append(Or(*[And(*term) for term in terms]))

    return tuple(fs)


def perform_bin_expr_min(base_tokens: list, wildcard='*', fast=False):
    """ Perform binary expression minimization for a list of base tokens

    :param fast:
    :param list base_tokens: list of base tokens that have same length.
        Each base token is a list of '0' or '1'.
    :param wildcard: the element to denote "do not care" character

    :returns: list of minimized tokens of '0', '1' or 'wildcard'
    """
    # logger.debug('perform_bin_expr_min 1')
    if not base_tokens:
        return []

    dim = len(base_tokens[0])
    X = exprvars('x', dim)
    f = prepare_expressions(base_tokens, X)

    # minimize
    # logger.debug('perform_bin_expr_min 2')
    f_dnf = f.to_dnf()
    # logger.debug('perform_bin_expr_min 3')
    if fast:
        fm, = espresso_exprs_fast(f_dnf)
    else:
        fm, = espresso_exprs(f_dnf)
    # logger.debug('perform_bin_expr_min 4')

    min_tokens = []
    for s in fm.cover:
        min_token = []
        for i in range(dim):
            if X[i] in s:
                min_token.append(1)
            elif ~X[i] in s:
                min_token.append(0)
            else:
                min_token.append(wildcard)

        min_tokens.append(min_token)
    # logger.debug('perform_bin_expr_min 5')

    return min_tokens
