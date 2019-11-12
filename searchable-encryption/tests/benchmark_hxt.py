"""
Benchmark SSE
"""
import multiprocessing
import logging
import csv
import os
from datetime import datetime
from pybloomfilter import BloomFilter
from time import time

from .context import hxt, datasethelper, rangequery, rangeutil, Config, util, hveutil, LocationEncoding

logger = logging.getLogger(__name__)

min_lat = datasethelper.GRID_LOS_ANGELES_MIN_LAT
max_lat = datasethelper.GRID_LOS_ANGELES_MAX_LAT
min_lon = datasethelper.GRID_LOS_ANGELES_MIN_LON
max_lon = datasethelper.GRID_LOS_ANGELES_MAX_LON


def prepare_transformed_checkins_database(checkins_file, num_cells, shifted=False) -> dict:
    """
    Prepare a tranformed check-ins (i.e., from check-ins to grid index)
    :param checkins_file: check-in file
    :param num_cells: grid granularity
    :param shifted: should the index be shifted?
    :return: transformed check-ins
    """
    data = datasethelper.load_dataset_gowalla(checkins_file)
    transformed_data = dict()
    for i in range(len(data)):
        c = data[i]
        x, y = rangeutil.map_point_to_cell(
            c.lon, c.lat,
            min_lon, max_lon, min_lat, max_lat, num_cells)
        if shifted:
            x = x - 1
            y = y - 1
        transformed_data[i] = (int(x), int(y))
    return transformed_data


def optimize_keywords_order(keyword_query):
    """
    Simple optimization for keyword ordering by putting shorter keywords first
    :param keyword_query: list of keywords
    :return: optimized keyword order
    """
    try:
        optimized_query = list(keyword_query)
        for i in range(1, len(optimized_query)):
            if len(optimized_query[0]) < len(optimized_query[i]):
                # if there is a longer query, swap it to the first
                optimized_query[0], optimized_query[i] = optimized_query[i], optimized_query[0]
        return optimized_query
    except TypeError:
        logger.error('Input not compatible with len()')
        return list(keyword_query)


def query_hxt_multiple_queries(width, queries_top, checkins_file, edb1, shve, key, iv, bf, g_serialized,
                               limit, location_encoding, shifted, paralleled, num_processes):
    """
    Query HXT with multiple queries
    """
    num_keyword_queries = 0
    num_matched = 0
    num_t_set_stag = 0
    total_time = 0

    # transformed checkins, used to check correctness of the query processing
    transformed_data = prepare_transformed_checkins_database(checkins_file, width, shifted)

    avg_time = None
    avg_num_keywords_queries = None
    avg_t_set_stag_size = None
    avg_num_matches = None
    avg_false_positive_rate = None
    total_expected_results = 0
    total_returned_results = 0
    false_positive_rates = list()
    for i in range(len(queries_top)):
        queries_count = i + 1
        logger.info(('Query ', queries_count))

        rectangle_query = queries_top[i]
        logger.debug(('rectangle_query', rectangle_query))

        expected_results = rangequery.search_plaintext_range_query(transformed_data, rectangle_query)
        # logger.info("Expected results = {}".format(str(expected_results)))
        logger.info("Expected results len = {}".format(len(expected_results)))

        keyword_queries = rangequery.convert_to_keyword_queries(rectangle_query, width, limit, location_encoding)
        # logger.info("keyword_queries = {}".format(keyword_queries))

        # optimize order
        keyword_queries = [optimize_keywords_order(keyword_query) for keyword_query in keyword_queries]

        if Config.crypto_hxt_using_preorder_id:
            keyword_queries = rangequery.convert_to_preorder_node_ids_range(keyword_queries, width)

        logger.info(('Num keywords queries: ', len(keyword_queries)))
        # logger.info("keyword_queries = {}".format(keyword_queries))

        start_time = time()
        query_result = set()
        if paralleled:
            # parallel processing
            with multiprocessing.Pool(
                    processes=num_processes,
                    initializer=init_worker_parallel,
                    initargs=(edb1, shve, key, iv, bf, g_serialized)) as pool:
                nums_tuples = pool.map(query_hxt_parallel, keyword_queries)

                for result_indl, t_set_stag_size_indl in nums_tuples:
                    for v in result_indl:
                        query_result.add(int(v))
                    num_t_set_stag += t_set_stag_size_indl

                global var_dict
                var_dict = {}

        else:
            for keyword_query in keyword_queries:
                result_indl, t_set_stag_size = hxt.query(
                    edb1, shve, keyword_query, key, iv, bf, g_serialized,
                    paralleled=paralleled,
                    benchmarking=True)
                # query_result.update(result_indl)
                for v in result_indl:
                    query_result.add(int(v))
                num_t_set_stag += t_set_stag_size

        total_time += time() - start_time

        # logger.info("Query results    = {}".format(str(query_result)))
        logger.info("Query results len   = {}".format(len(query_result)))

        if not (expected_results <= query_result):
            logger.error('Query result does not contain expected results')

        query_result = set([int(r) for r in query_result])
        logger.debug(('Running... Result len:', len(expected_results), len(query_result)))
        logger.debug(('Expected results:', sorted(expected_results)))
        logger.debug(('Result----------:', sorted(query_result)))

        num_matched += len(query_result)
        num_keyword_queries += len(keyword_queries)

        avg_time = total_time / queries_count
        avg_num_keywords_queries = num_keyword_queries / queries_count
        avg_t_set_stag_size = num_t_set_stag / queries_count
        avg_num_matches = num_matched / queries_count

        false_positive_count = len(query_result) - len(expected_results)
        false_positive_rates.append(false_positive_count / len(expected_results))
        avg_false_positive_rate = sum(false_positive_rates) / len(false_positive_rates)

        total_expected_results += len(expected_results)
        total_returned_results += len(query_result)

        logger.info((limit, 'Running... Avg Time :', avg_time))
        logger.info((limit, 'Running... Avg matched items : ', avg_num_matches))
        logger.info((limit, 'Running... Avg TSet size of stag : ', avg_t_set_stag_size))
        logger.info((limit, 'Running... Avg keywords queries: ', avg_num_keywords_queries))
        logger.info((limit, 'Running... Avg false positive rate : ', avg_false_positive_rate))
        logger.info((limit, 'Running... total_expected_results : ', total_expected_results))
        logger.info((limit, 'Running... total_returned_results : ', total_returned_results))
        logger.info((limit, 'Running... total FPR : ',
                     float(total_returned_results - total_expected_results) / total_expected_results))

    return (avg_time, avg_num_matches, avg_t_set_stag_size, avg_num_keywords_queries, avg_false_positive_rate,
            total_expected_results, total_returned_results)


var_dict = {}


def init_worker_parallel(edb1, shve, key, iv, bf, g_serialized):
    var_dict['edb1'] = edb1
    var_dict['shve'] = shve
    var_dict['key'] = key
    var_dict['iv'] = iv
    var_dict['bf'] = bf
    var_dict['g_serialized'] = g_serialized


def query_hxt_parallel(keyword_query):
    edb1 = var_dict['edb1']
    shve = var_dict['shve']
    key = var_dict['key']
    iv = var_dict['iv']
    bf = var_dict['bf']
    g_serialized = var_dict['g_serialized']
    return query_hxt(edb1, shve, keyword_query, key, iv, bf, g_serialized, paralleled=False, num_processes=None)


def query_hxt(edb1, shve, keyword_query, key, iv, bf, g_serialized, paralleled, num_processes):
    return hxt.query(edb1, shve, keyword_query, key, iv, bf, g_serialized,
                     paralleled=paralleled, num_processes=num_processes, benchmarking=True)


def get_queries_file_name(prefix: str, width: int, limit, shifted=False) -> str:
    shift = '_shifted' if shifted else ''
    return (prefix + '_{0}_{1}' + shift + '.txt').format(width, limit)


def benchmark_hxt(width: int,
                  queries_top: list,
                  checkins_file: str,
                  index_folder: str,
                  location_encoding: LocationEncoding,
                  limit,
                  shifted,
                  paralleled=False,
                  num_processes=None):
    """
    Benchmark HXT
    """
    key_file = os.path.join(index_folder, Config.crypto_hxt_key_file)
    oxt_edb_file = os.path.join(index_folder, Config.crypto_hxt_oxt_edb_file)
    shve_file = os.path.join(index_folder, Config.crypto_hxt_shve_file)
    bloomfilter_file = os.path.join(index_folder, Config.crypto_hxt_bloomfilter_file)

    if os.path.isfile(shve_file):
        key, iv, g_serialized, bits = util.pickle_load(key_file)
        edb1 = util.pickle_load(oxt_edb_file)
        shve = util.pickle_load(shve_file)
        bf = BloomFilter.open(bloomfilter_file)

        result = query_hxt_multiple_queries(
            width, queries_top, checkins_file, edb1, shve, key, iv, bf, g_serialized, limit, location_encoding, shifted,
            paralleled, num_processes)

    else:
        logger.info(('Index for HXT NOT existed in', index_folder))
        result = None

    return result


def convert_queries_from_hve():
    """
    Convert queries from HVE form to HXT form.
    Each line of HVE form is: x_node_id y_node_id (using path).
    Each line of HXT form is: x_top_left y_top_left x_len y_len.
    """
    for width in Config.benchmark_widths:
        for query_level in Config.benchmark_query_levels:
            queries_file_name = get_queries_file_name(Config.queries_file_prefix, width, query_level)
            queries_file_name_shifted = get_queries_file_name(Config.queries_file_prefix, width, query_level,
                                                              shifted=True)
            queries_file = os.path.join(Config.queries_dir, queries_file_name)

            hve_queries = hveutil.read_rectangle_queries(queries_file)
            for q_id in range(len(hve_queries)):
                hve_query = hve_queries[q_id]
                x_range = rangeutil.get_covered_range(hve_query[0][1:], width)
                y_range = rangeutil.get_covered_range(hve_query[1][1:], width)

                # From here on
                # because GPS system, x increases from left to right, y increases from BOTTOM to TOP
                # while in my system, x increases from left to right, y increases from TOP to BOTTOM,
                # means: OUR TOP_LEFT = GPS BOTTOM_LEFT
                # and  : OUR BOTTOM_RIGHT = GPS TOP_RIGHT

                x_top_left = int(x_range[0])
                y_top_left = int(y_range[0])
                x_bottom_right = int(x_range[1])
                y_bottom_right = int(y_range[1])

                x_len = int(abs(x_bottom_right - x_top_left + 1))
                y_len = int(abs(y_bottom_right - y_top_left + 1))

                with open(queries_file_name, 'a+') as f:
                    f.write('{0} {1} {2} {3}\n'.format(x_top_left, y_top_left, x_len, y_len))

                # because we shift dataset (x, y) => (x+1, y+1), the query in shifted coordinate will need to subtract 1
                with open(queries_file_name_shifted, 'a+') as f:
                    f.write('{0} {1} {2} {3}\n'.format(x_top_left-1, y_top_left-1, x_len, y_len))

                # for plotting x,y (see gnuplot)
                out_str_format = 'set object {id} rect ' \
                                 'from {x_bottom_left},{y_bottom_left} to {x_top_right},{y_top_right}\n'
                query_plot_out_file = get_queries_file_name(
                    Config.queries_plot_file_prefix, width, query_level)
                with open(query_plot_out_file, 'a+') as f:
                    out_str = out_str_format.format(
                        id=(q_id + 1),
                        x_bottom_left=x_top_left,
                        y_bottom_left=y_top_left,
                        x_top_right=x_bottom_right,
                        y_top_right=y_bottom_right
                    )
                    f.write(out_str)


def read_queries_top_left(file_path: str) -> list:
    """
    Read rectangle queries from file
    :param file_path:
    :return:
    """
    queries = list()
    with open(file_path, 'r') as f:
        for line in f:
            line = line.rstrip()
            queries.append(tuple([int(v) for v in line.split(' ')]))
    return queries


def convert_documents_to_preorder_node_ids_range(documents: dict, width: int):
    """
    Convert documents as id => list of words
    to id => list of pairs of preorder_node_ids, sorted by level on the tree (level 1 goes first)
    :param documents:
    :param width:
    :return:
    """
    results = dict()
    for item_id, words in documents.items():
        results[item_id] = [rangeutil.get_to_pre_order_id_from_node_path(w, width) for w in words]

    return results


def generate_hxt_index():
    """
    Generate HXY index. This is also index generation benchmark
    """
    location_encoding = LocationEncoding[Config.index_encoding]
    shifted = Config.benchmark_shifted
    bf_false_positive_rate = Config.crypto_bloom_filter_false_positive_rate
    paralleled = Config.benchmark_paralleled
    for i in range(len(Config.benchmark_widths)):
        width = Config.benchmark_widths[i]
        for n in Config.benchmark_num_checkins:
            for index_limit_level in Config.benchmark_index_limit_levels:
                document_file = os.path.join(
                    Config.documents_dir,
                    datasethelper.get_documents_file_name(
                        Config.data_file_prefix,
                        n,
                        width,
                        location_encoding,
                        shifted=shifted)
                )
                index_folder = os.path.join(
                    Config.index_dataset_dir,
                    datasethelper.get_index_dir_name(
                        Config.data_file_prefix,
                        n,
                        width,
                        index_limit_level,
                        location_encoding,
                        shifted=shifted)
                )
                index_folder = os.path.join(index_folder, 'hxt')
                os.makedirs(index_folder, exist_ok=True)

                password = Config.password

                key_file = os.path.join(index_folder, Config.crypto_hxt_key_file)
                oxt_edb_file = os.path.join(index_folder, Config.crypto_hxt_oxt_edb_file)
                shve_file = os.path.join(index_folder, Config.crypto_hxt_shve_file)
                bloomfilter_file = os.path.join(index_folder, Config.crypto_hxt_bloomfilter_file)

                if os.path.isfile(shve_file):
                    logger.info(('Index for HXT existed in', index_folder))
                else:
                    database = datasethelper.load_database(document_file)
                    database = rangequery.limit_tree_level(database, index_limit_level, location_encoding, True)

                    if Config.crypto_hxt_using_preorder_id:
                        database = convert_documents_to_preorder_node_ids_range(database, width)

                    key, iv, g_serialized, edb1, bf, bits, shve, time_gen_oxt, time_gen_hxt_only, bits_size = hxt.setup(
                        database,
                        password,
                        bloomfilter_file=bloomfilter_file,
                        bf_false_positive_rate=bf_false_positive_rate,
                        paralleled=paralleled,
                        num_processes=Config.benchmark_paralleled_num_processes,
                        benchmark=True)

                    if Config.benchmark_save_index:
                        util.pickle_dump((key, iv, g_serialized, bits), key_file, delete_after_dumps=True)
                        bf.sync()
                        edb1_size = util.pickle_dump(edb1, oxt_edb_file, delete_after_dumps=True)
                        shve_size = util.pickle_dump(shve, shve_file, delete_after_dumps=True)
                    else:
                        edb1_size = -1
                        shve_size = -1

                    index_time = time_gen_oxt + time_gen_hxt_only
                    logger.info(('Gen HXT index ', index_folder, ':', index_time))

                    # write header
                    if not os.path.isfile(Config.benchmark_output_file):
                        # file not exist, create it and add header
                        with open(Config.benchmark_output_file, 'a+') as f:
                            csv_writer = csv.writer(f, delimiter='\t')
                            header = ['time',
                                      'scheme',
                                      'width',
                                      'n_checkins',
                                      'index_limit_level',
                                      'encoding',
                                      'shifted',
                                      'paralleled',
                                      'total_time',
                                      'oxt_time',
                                      'hxt_time',
                                      'total_size',
                                      'oxt_size',
                                      'hxt_size',
                                      'bf_bit_size',
                                      'bf_false_positive_rate']
                            csv_writer.writerow(header)

                    with open(Config.benchmark_output_file, 'a+') as f:
                        csv_writer = csv.writer(f, delimiter='\t')
                        row = [datetime.strftime(datetime.now(), datasethelper.GOWALLA_TIME_PATTERN),
                               str('HXT'),
                               str(width),
                               str(n),
                               str(index_limit_level),
                               str(location_encoding.name),
                               str(shifted),
                               str(paralleled),
                               '{0:.2f}'.format(index_time),
                               '{0:.2f}'.format(time_gen_oxt),
                               '{0:.2f}'.format(time_gen_hxt_only),
                               str(edb1_size + shve_size),
                               str(edb1_size),
                               str(shve_size),
                               str(bits.length()),
                               str(bf_false_positive_rate)]
                        csv_writer.writerow(row)


def benchmark_hxt_limit_levels():
    """
    Benchmark HXT with different index limit levels
    """
    location_encoding = LocationEncoding[Config.index_encoding]
    shifted = Config.benchmark_shifted
    bf_false_positive_rate = Config.crypto_bloom_filter_false_positive_rate
    paralleled = Config.benchmark_paralleled

    for i in range(len(Config.benchmark_widths)):
        width = Config.benchmark_widths[i]
        for n in Config.benchmark_num_checkins:
            for index_limit_level in Config.benchmark_index_limit_levels:
                checkins_file = os.path.join(
                    Config.checkins_dir,
                    datasethelper.get_checkins_file_name(Config.data_file_prefix, n))

                index_folder = os.path.join(
                    Config.index_dataset_dir,
                    datasethelper.get_index_dir_name(
                        Config.data_file_prefix,
                        n,
                        width,
                        index_limit_level,
                        location_encoding,
                        shifted=shifted)
                )
                index_folder = os.path.join(index_folder, 'hxt')

                for query_level in Config.benchmark_query_levels:
                    queries_file = os.path.join(Config.queries_dir,
                                                get_queries_file_name(
                                                    Config.queries_file_prefix, width, query_level, shifted=shifted))

                    logger.info(('width, n, index_limit_level, query_level',
                                 width, n, index_limit_level, query_level))

                    queries_top = read_queries_top_left(queries_file)

                    result = benchmark_hxt(
                        width=width,
                        queries_top=queries_top,
                        checkins_file=checkins_file,
                        index_folder=index_folder,
                        location_encoding=location_encoding,
                        limit=index_limit_level,
                        shifted=shifted,
                        paralleled=paralleled,
                        num_processes=Config.benchmark_paralleled_num_processes
                    )

                    (avg_time, avg_num_matches, avg_t_set_stag_size, avg_num_keywords_queries, avg_false_positive_rate,
                     total_expected_results, total_returned_results) = result

                    # write header
                    if not os.path.isfile(Config.benchmark_output_file):
                        # file not exist, create it and add header
                        with open(Config.benchmark_output_file, 'a+') as f:
                            csv_writer = csv.writer(f, delimiter='\t')
                            header = ['time',
                                      'scheme',
                                      'width',
                                      'n_checkins',
                                      'index_limit_level',
                                      'query_level',
                                      'encoding',
                                      'shifted',
                                      'paralleled',
                                      'bf_false_positive_rate',
                                      'avg_time',
                                      'avg_num_matches',
                                      'avg_t_set_stag_size',
                                      'avg_num_keywords_queries',
                                      'avg_false_positive_rate',
                                      'total_expected_results',
                                      'total_returned_results',
                                      'false_positive_rate_by_total']
                            csv_writer.writerow(header)

                    with open(Config.benchmark_output_file, 'a+') as f:
                        csv_writer = csv.writer(f, delimiter='\t')
                        row = [datetime.strftime(datetime.now(), datasethelper.GOWALLA_TIME_PATTERN),
                               str('HXT'),
                               str(width),
                               str(n),
                               str(index_limit_level),
                               str(query_level),
                               str(location_encoding.name),
                               str(shifted),
                               str(paralleled),
                               str(bf_false_positive_rate),
                               '{0:.3f}'.format(avg_time),
                               '{0:.3f}'.format(avg_num_matches),
                               '{0:.3f}'.format(avg_t_set_stag_size),
                               '{0:.3f}'.format(avg_num_keywords_queries),
                               '{0:.3f}'.format(avg_false_positive_rate),
                               str(total_expected_results),
                               str(total_returned_results),
                               '{0:.3f}'.format(
                                   float(total_returned_results - total_expected_results) / total_expected_results)]
                        csv_writer.writerow(row)
