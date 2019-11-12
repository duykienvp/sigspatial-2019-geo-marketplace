""" Execute tasks
"""
import argparse
import sys

TASK_TEST = 'test'
TASK_BENCHMARK = 'benchmark'
TASK_GEN = 'gen'
TASKS = [TASK_TEST, TASK_BENCHMARK, TASK_GEN]

SCHEME_HVE = 'hve'
SCHEME_HVE_HE = 'hvehe'
SCHEME_HVE_GE = 'hvege'
SCHEME_HIERARCHICAL = 'hierarchical'
SCHEME_RANGE = 'range'
SCHEME_SSE_OXT = 'oxt'
SCHEME_SSE_HXT = 'hxt'
SCHEME_SELECT_CHECKINS_GOWALLA = 'select_checkins_gowalla'
SCHEME_CONVERT_CHECKINS_TO_DOC = 'convert_checkins_to_doc'
SCHEME_GEN_HXT_INDEX = 'generate_hxt_index'
SCHEME_RUN_GEN_QUERIES_GOWALLA_HVE = 'gen_queries_gowalla_hve'
SCHEME_RUN_CONVERT_QUERIES_GOWALLA_HVE_TO_HXT = 'convert_queries_gowalla_hve_to_hxt'
SCHEME_VECTOR_COMMITMENT = 'vc'

SCHEMES = [SCHEME_HVE, SCHEME_HVE_HE, SCHEME_HVE_GE, SCHEME_HIERARCHICAL,
           SCHEME_RANGE, SCHEME_SSE_OXT, SCHEME_SSE_HXT,
           SCHEME_SELECT_CHECKINS_GOWALLA,
           SCHEME_CONVERT_CHECKINS_TO_DOC,
           SCHEME_GEN_HXT_INDEX,
           SCHEME_RUN_GEN_QUERIES_GOWALLA_HVE,
           SCHEME_RUN_CONVERT_QUERIES_GOWALLA_HVE_TO_HXT,
           SCHEME_VECTOR_COMMITMENT]


class MyParser(argparse.ArgumentParser):
    """ An parse to print help whenever an error occurred to the parsing process
    """
    def error(self, message):
        sys.stderr.write('error: %s\n' % message)
        self.print_help()
        sys.exit(2)


if __name__ == '__main__':
    parser = MyParser(description='Execute a task')
    parser.add_argument('-t',
                        '--task',
                        help='Task name',
                        choices=TASKS,
                        required=True)
    parser.add_argument('-s',
                        '--scheme',
                        help='Encryption scheme',
                        choices=SCHEMES,
                        required=True)
    parser.add_argument('--input-file',
                        help='Input file',
                        required=False)

    parser.add_argument('-v',
                        '--verbose',
                        help='increase output verbosity',
                        action='store_true')

    # print help if no argument is provided.
    # This may be necessary if no provisional arguement is declared
    # if len(sys.argv) == 1:
    #     parser.print_help(sys.stderr)
    #     sys.exit(1)
    args = parser.parse_args()

    if args.task == TASK_GEN:
        # generate
        if args.scheme == SCHEME_SELECT_CHECKINS_GOWALLA:
            from searchableencryption.toolbox import datasethelper
            datasethelper.select_checkins_from_gowalla(args.input_file)

        if args.scheme == SCHEME_CONVERT_CHECKINS_TO_DOC:
            from searchableencryption.toolbox import datasethelper
            datasethelper.convert_checkins_to_documents()

        if args.scheme == SCHEME_RUN_GEN_QUERIES_GOWALLA_HVE:
            from tests import benchmark_hve
            benchmark_hve.generate_queries_hve(args.input_file)

        if args.scheme == SCHEME_RUN_CONVERT_QUERIES_GOWALLA_HVE_TO_HXT:
            from tests import benchmark_hxt
            benchmark_hxt.convert_queries_from_hve()

    if args.task == TASK_BENCHMARK:
        # benchmark
        if args.scheme == SCHEME_HVE:
            from tests import benchmark_hve
            benchmark_hve.benchmark_hve_multiple()

        if args.scheme == SCHEME_GEN_HXT_INDEX:
            from tests import benchmark_hxt
            benchmark_hxt.generate_hxt_index()

        if args.scheme == SCHEME_SSE_HXT:
            from tests import benchmark_hxt
            benchmark_hxt.benchmark_hxt_limit_levels()

        if args.scheme == SCHEME_VECTOR_COMMITMENT:
            from tests import benchmark_vc
            benchmark_vc.benchmark_vc_multiple()

    if args.task == TASK_TEST:
        # test
        if args.scheme == SCHEME_HVE:
            import tests.test_hve as test
            test.test_hve_simple()
            test.test_hve_multiple()

        if args.scheme == SCHEME_HVE_HE:
            import tests.test_hvehe as test
            test.test_hve_he_simple()

        if args.scheme == SCHEME_HVE_GE:
            import tests.test_hvege as test
            test.test_hve_ge_simple()

        if args.scheme == SCHEME_HIERARCHICAL:
            import tests.test_hvehe as test
            test.test_hierarchical_encoding_decomposition()

        if args.scheme == SCHEME_SSE_OXT:
            import tests.test_hxt as test
            test.test_oxt_construction()

        if args.scheme == SCHEME_SSE_HXT:
            import tests.test_hxt as test
            test.test_hxt_construction()

        if args.scheme == SCHEME_RANGE:
            import tests.test_range_query as test
            # test.test_range_query_hardcoded_hxt()
            # test.test_range_query_random_hxt()
            # test.test_range_query_hardcoded_hve()
            test.test_range_query_random_hve()
            test.test_multiple_range_queries_hve()

        if args.scheme == SCHEME_VECTOR_COMMITMENT:
            from tests import test_vector_commitment
            test_vector_commitment.test_vector_commitment()
