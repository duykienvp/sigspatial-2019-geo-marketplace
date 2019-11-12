"""
Benchmark HVE for range query
"""
import csv
import multiprocessing
from datetime import datetime
from time import time
import logging
import os
import random

from searchableencryption.rangequery.rangeutil import LocationEncoding
from .context import Config, datasethelper, rangeutil, pairingcurves, parse_params_from_string, hve, \
    rangequery, util, hveutil
from . import benchmark_hxt
from .benchmark_hxt import get_queries_file_name

logger = logging.getLogger(__name__)

min_lat = benchmark_hxt.min_lat
max_lat = benchmark_hxt.max_lat
min_lon = benchmark_hxt.min_lon
max_lon = benchmark_hxt.max_lon

var_dict = dict()

# selected_levels = [7, 6, 5]  # 380m, 2x380m, 4x380m on longitude


def generate_queries_hve(input_file: str):
    """
    Generate queries by choosing random check-ins, then choose the node of the tree containing those check-ins
    """
    output_file_prefix = Config.queries_file_prefix
    data = datasethelper.load_dataset_gowalla(input_file)

    num_queries = 100

    # randomly pick a data point
    query_levels = Config.benchmark_query_levels
    for width in Config.benchmark_widths:
        for query_level in query_levels:
            remain_indices = [tmp for tmp in range(len(data))]
            selected_nodes = set()
            while len(selected_nodes) < num_queries:
                selected_index = random.randint(0, len(remain_indices)-1)
                pos = remain_indices[selected_index]

                lat = data[pos].lat
                lon = data[pos].lon

                data_x, data_y = rangeutil.map_point_to_cell(
                    lon, lat,
                    min_lon, max_lon, min_lat, max_lat, width)
                data_x, data_y = int(data_x), int(data_y)

                x_nodes = rangeutil.get_covering_nodes(width, data_x)  # e.g. ['0', '01', '010']
                y_nodes = rangeutil.get_covering_nodes(width, data_y)

                if 0 < int(x_nodes[query_level - 1]) and 0 < int(y_nodes[query_level - 1]):
                    # the 0 node does not appear in shifted dataset so we need to ignore it
                    selected_node_x = rangeutil.PREFIX_X_DEFAULT + x_nodes[query_level - 1]
                    selected_node_y = rangeutil.PREFIX_Y_DEFAULT + y_nodes[query_level - 1]

                    selected_node = (selected_node_x, selected_node_y)

                    selected_nodes.add(selected_node)

                del remain_indices[selected_index]

            queries_file = get_queries_file_name(output_file_prefix, width, query_level)
            with open(queries_file, 'w') as f:
                for selected_node_x, selected_node_y in selected_nodes:
                    f.write('{0} {1}\n'.format(selected_node_x, selected_node_y))

            gen_plotting(list(selected_nodes), query_level, width)


def gen_plotting(selected_nodes, selected_level, num_cells):
    """
    Generate script for plotting check-ins using gnuplot later
    """
    # queries_file_name = get_queries_file_name(Config.queries_file_prefix, num_cells, selected_level)
    # queries_file = os.path.join(Config.queries_dir, queries_file_name)
    # queries = read_queries(queries_file)
    for q_id in range(len(selected_nodes)):
        query = selected_nodes[q_id]
        x_range = rangeutil.get_covered_range(query[0][1:], num_cells)
        y_range = rangeutil.get_covered_range(query[1][1:], num_cells)

        x_top_left = x_range[0]
        y_top_left = y_range[0]
        x_bottom_right = x_range[1]
        y_bottom_right = y_range[1]

        # for plotting x,y (see gnuplot)
        out_str_format = 'set object {id} rect ' \
                         'from {x_bottom_left},{y_bottom_left} to {x_top_right},{y_top_right}\n'
        query_plot_out_file = get_queries_file_name(
            Config.queries_plot_file_prefix, num_cells, selected_level)
        with open(query_plot_out_file, 'a+') as f:
            out_str = out_str_format.format(
                id=(q_id + 1),
                x_bottom_left=int(x_top_left),
                y_bottom_left=int(y_top_left),
                x_top_right=int(x_bottom_right),
                y_top_right=int(y_bottom_right)
            )
            f.write(out_str)


def convert_documents_to_preorder_node_ids_range(documents: dict, width: int, concatenate: bool):
    """
    Convert documents as id => list of words
    to id => list of pairs of preorder_node_ids, sorted by level on the tree (level 1 goes first)
    :param documents:
    :param width:
    :param concatenate: should we concatenate x and y into 1 number or not
    :return:
    """
    results = dict()
    for item_id, words in documents.items():
        x_words = [word for word in words if str(word).startswith(rangeutil.PREFIX_X_DEFAULT)]
        y_words = [word for word in words if str(word).startswith(rangeutil.PREFIX_Y_DEFAULT)]

        # sort level (increasing len)
        for i in range(len(x_words)):
            for j in range(i+1, len(x_words)):
                if len(x_words[j]) < len(x_words[i]):
                    x_words[i], x_words[j] = x_words[j], x_words[i]
                    y_words[i], y_words[j] = y_words[j], y_words[i]

        node_id_pairs = list()
        for x_word in x_words:
            x_node_id = rangeutil.get_to_pre_order_id_from_node_path(x_word, width)
            for y_word in y_words:
                if len(x_word) == len(y_word):
                    # same level on the tree
                    y_node_id = rangeutil.get_to_pre_order_id_from_node_path(y_word, width)
                    if not concatenate:
                        node_id_pairs.append([x_node_id, y_node_id])
                    else:
                        node_id = x_node_id * width * 2 + y_node_id
                        node_id_pairs.append([node_id])
                    break
        assert len(node_id_pairs) == len(x_words), 'Failed to find all word pairs'
        results[item_id] = node_id_pairs

    return results


def prepare_group_params() -> dict:
    params = dict()
    params[512] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_256_SAMPLE)
    params[768] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_384_SAMPLE)
    params[1024] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_512_SAMPLE)
    params[1536] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_768_SAMPLE)
    params[2048] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_1024_SAMPLE)
    params[4096] = parse_params_from_string(pairingcurves.PAIRING_CURVE_TYPE_A1_2048_SAMPLE)
    return params


def init_gen_cipher_parallel(hve_width, pk, group):
    var_dict['hve_width'] = hve_width
    var_dict['pk'] = pk
    var_dict['group'] = group


def gen_cipher_parallel(item_id, node_id_pairs):
    hve_width = var_dict['hve_width']
    pk = var_dict['pk']
    group = var_dict['group']
    return gen_cipher(item_id, node_id_pairs, hve_width, pk, group)


def gen_cipher(item_id, node_id_pairs, hve_width, pk, group):
    ciphers = list()
    for node_id_pair in node_id_pairs:
        # each node_id_pair is [x_node_id, y_node_id]
        cipher_text = hve.encrypt(pk, node_id_pair)
        cipher_text_serialized = hve.serialize_cipher(hve_width, cipher_text, group)
        ciphers.append(cipher_text_serialized)

    return item_id, ciphers


def init_match_token_cipher_parallel(hve_width, cipher_comparison_pos, group):
    var_dict['hve_width'] = hve_width
    var_dict['cipher_comparison_pos'] = cipher_comparison_pos
    var_dict['group'] = group


def match_token_cipher_parallel(token_serialized, item_id_cipher_texts_serialized):
    item_id, cipher_texts_serialized = item_id_cipher_texts_serialized
    hve_width = var_dict['hve_width']
    cipher_comparison_pos = var_dict['cipher_comparison_pos']
    group = var_dict['group']
    ciphers = list()
    for cipher_text_serialized in cipher_texts_serialized:
        cipher_text = hve.deserialize_cipher(hve_width, *cipher_text_serialized, group)
        ciphers.append(cipher_text)

    token = hve.deserialize_token(hve_width, *token_serialized, group)

    return match_token_cipher(token, ciphers, cipher_comparison_pos, group)


def match_token_cipher(token, ciphers, cipher_comparison_pos, group):
    # for cipher in ciphers:
    #     matched = hve.query(token, cipher, predicate_only=True, group=group)
    #     token_cipher_pair_count += 1
    #     if matched:
    #         total_matches += 1
    #         break

    # only match at this level
    cipher = ciphers[cipher_comparison_pos]
    return hve.query(token, cipher, predicate_only=True, group=group)


def plaintext_search(queries_preorder_id, documents_node_ids):
    query_count = 0
    for q in queries_preorder_id:
        matched_count = 0
        for item_id, node_ids in documents_node_ids.items():
            matched = False
            for node_id in node_ids:
                if q == node_id:
                    matched = True
                    break

            matched_count += 1 if matched else 0
        query_count += 1
        logger.info('Query ' + str(query_count) + ' matched ' + str(matched_count))


def benchmark_hve(width, queries, data_file, cipher_folder, token_folder,
                  query_level, location_encoding, group_param, index_limit_level,
                  start_item_idx, end_item_idx,
                  concatenate,
                  paralleled, num_processes=None) -> tuple:
    """
    Benchmark HVE with single configuration
    """
    global var_dict
    hve_width = 1 if concatenate else 2
    documents = datasethelper.load_database(data_file)

    documents_tmp = dict()
    for item_id, words in documents.items():
        if start_item_idx <= int(item_id) < end_item_idx:
            documents_tmp[item_id] = words
    documents = documents_tmp

    documents = rangequery.limit_tree_level(documents, index_limit_level, location_encoding, True)
    documents_node_pairs = convert_documents_to_preorder_node_ids_range(documents, width, concatenate)

    queries_preorder_id = rangequery.convert_to_preorder_node_ids_range(queries, width)
    queries_preorder_id = [[q_x, q_y] for q_x, q_y in queries_preorder_id]
    if concatenate:
        queries_preorder_id = [[q_x * width * 2 + q_y] for q_x, q_y in queries_preorder_id]

    plaintext_search(queries_preorder_id, documents_node_pairs)

    pk_file = os.path.join(cipher_folder, Config.crypto_hve_pk_file)
    sk_file = os.path.join(cipher_folder, Config.crypto_hve_sk_file)
    cipher_file = os.path.join(cipher_folder, Config.crypto_hve_cipher_file)
    token_file = os.path.join(token_folder, Config.crypto_hve_token_file)

    time_setup = 0
    if not (os.path.isfile(pk_file) and os.path.isfile(sk_file)):
        # do not have keys yet => generate them
        logger.info('HVE setup')
        start_time = time()

        pk, sk = hve.setup(width=hve_width, group_param=group_param)

        time_setup = time() - start_time

        util.pickle_dump(hve.serialize_public_key(hve_width, pk, group_param), pk_file)
        util.pickle_dump(hve.serialize_secret_key(hve_width, sk, group_param), sk_file)

    # generate cipher
    time_gen_cipher = 0
    avg_time_gen_cipher = 0
    cipher_count = 0
    if os.path.isfile(pk_file) and not os.path.isfile(cipher_file):
        logger.info('Generating cipher text')
        pk = hve.deserialize_public_key(hve_width, *(util.pickle_load(pk_file)))
        group = pk['group']

        doc_ciphers = dict()
        if paralleled:
            # parallel processing
            with multiprocessing.Pool(
                    processes=num_processes,
                    initializer=init_gen_cipher_parallel,
                    initargs=(hve_width, pk, group)) as pool:
                ciphers_tuples_list = pool.starmap(gen_cipher_parallel, documents_node_pairs.items())

                for item_id, ciphers in ciphers_tuples_list:
                    doc_ciphers[item_id] = ciphers

                var_dict = {}
            cipher_count += len(documents_node_pairs)
        else:

            for item_id, node_id_pairs in documents_node_pairs.items():
                start_time = time()

                _, ciphers = gen_cipher(item_id, node_id_pairs, hve_width, pk, group)
                doc_ciphers[item_id] = ciphers

                end_time = time()
                time_gen_cipher += end_time - start_time

                cipher_count += 1
                avg_time_gen_cipher = time_gen_cipher / cipher_count
                logger.info((query_level, 'Running..... avg_time_gen_cipher', avg_time_gen_cipher))

        avg_time_gen_cipher = time_gen_cipher / cipher_count

        util.pickle_dump(doc_ciphers, cipher_file)

    # generate tokens
    time_gen_tokens = 0
    avg_time_gen_tokens = 0
    token_count = 0
    if os.path.isfile(sk_file) and not os.path.isfile(token_file):
        logger.info('Generating tokens')
        sk = hve.deserialize_secret_key(hve_width, *(util.pickle_load(sk_file)))

        tokens = list()
        for q in queries_preorder_id:
            start_time = time()
            token = hve.gen_token(sk, q)
            token_serialized = hve.serialize_token(hve_width, token, sk['group'])
            tokens.append(token_serialized)

            time_gen_tokens += time() - start_time
            token_count += 1
            avg_time_gen_tokens = time_gen_tokens / token_count
            logger.info((query_level, 'Running..... avg_time_gen_tokens', avg_time_gen_tokens))

        util.pickle_dump(tokens, token_file)

    # matching
    time_matching = 0
    avg_time_matching_per_token = 0
    avg_time_matching_per_token_cipher_pair = 0
    total_matches = 0
    avg_matches = 0
    token_count = 0
    token_cipher_pair_count = 0
    token_item_pair_count = 0
    avg_time_matching_per_token_item_pair = 0
    if os.path.isfile(cipher_file) and os.path.isfile(token_file) and os.path.isfile(pk_file):
        pk = hve.deserialize_public_key(hve_width, *(util.pickle_load(pk_file)))
        group = pk['group']

        logger.info('Deserializing ciphers')
        t0 = time()

        doc_ciphers_serialized_all = util.pickle_load(cipher_file)

        doc_ciphers_serialized = dict()
        doc_ciphers = dict()
        for item_id, cipher_texts_serialized in doc_ciphers_serialized_all.items():
            if start_item_idx <= int(item_id) < end_item_idx:
                doc_ciphers_serialized[item_id] = cipher_texts_serialized

        for item_id, cipher_texts_serialized in doc_ciphers_serialized.items():
            cipher_texts = list()
            for cipher_text_serialized in cipher_texts_serialized:
                cipher_text = hve.deserialize_cipher(hve_width, *cipher_text_serialized, group)
                cipher_texts.append(cipher_text)
            doc_ciphers[item_id] = cipher_texts
        t1 = time()
        logger.info(('Time deserialize cipher', t1 - t0))

        logger.info('Deserializing token')
        tokens_serialized = util.pickle_load(token_file)
        tokens = list()
        for token_serialized in tokens_serialized:
            token = hve.deserialize_token(hve_width, *token_serialized, group)
            tokens.append(token)

        logger.info('Matching....')

        cipher_comparison_pos = query_level - index_limit_level
        if index_limit_level == 0:
            cipher_comparison_pos -= 1

        for t_i in range(len(tokens_serialized)):
            # for token in tokens:
            start_time = time()
            item_count = 0
            if paralleled:
                # parallel
                logger.info('Parallel match token')
                doc_ciphers_len = len(doc_ciphers)

                # parallel processing
                with multiprocessing.Pool(
                        processes=num_processes,
                        initializer=init_match_token_cipher_parallel,
                        initargs=(hve_width, cipher_comparison_pos, group)) as pool:
                    matches = pool.starmap(match_token_cipher_parallel,
                                           list(zip([tokens_serialized[t_i]] * doc_ciphers_len,
                                                    doc_ciphers_serialized.items())))

                    for m in matches:
                        total_matches += 1 if m else 0

                    var_dict = {}

            else:
                token = tokens[t_i]
                # sequential
                for item_id, ciphers in doc_ciphers.items():
                    matched = match_token_cipher(token, ciphers, cipher_comparison_pos, group)
                    if matched:
                        total_matches += 1

            item_count += len(doc_ciphers)
            token_cipher_pair_count += len(doc_ciphers)
            token_item_pair_count += item_count

            end_time = time()
            time_matching += end_time - start_time
            token_count += 1

            avg_time_matching_per_token = time_matching / token_count
            avg_time_matching_per_token_item_pair = time_matching / token_item_pair_count
            avg_time_matching_per_token_cipher_pair = time_matching / token_cipher_pair_count
            avg_matches = total_matches / token_count

            logger.info((query_level, 'Running..... avg_time_matching_per_token', avg_time_matching_per_token))
            logger.info((query_level, 'Running..... avg_time_matching_per_token_item_pair', avg_time_matching_per_token_item_pair))
            logger.info((query_level, 'Running..... avg_time_matching_per_token_cipher_pair', avg_time_matching_per_token_cipher_pair))
            logger.info((query_level, 'Running..... avg_matches', avg_matches))

            # if token_count == 2:
            #     break   # quick test

    return time_setup, avg_time_gen_cipher, avg_time_gen_tokens, avg_time_matching_per_token, avg_time_matching_per_token_item_pair, avg_time_matching_per_token_cipher_pair, avg_matches


def benchmark_hve_multiple():
    """
    Main HVE benchmark
    """
    location_encoding = LocationEncoding[Config.index_encoding]
    group_params = prepare_group_params()

    for width in Config.benchmark_widths:
        for n in Config.benchmark_num_checkins:
            for query_level in Config.benchmark_query_levels:
                for index_limit_level in Config.benchmark_index_limit_levels:
                    for key_size in Config.benchmark_hve_key_sizes:
                        document_file = os.path.join(
                            Config.documents_dir,
                            datasethelper.get_documents_file_name(
                                Config.data_file_prefix,
                                n,
                                width,
                                location_encoding)
                        )

                        cipher_folder = os.path.join(
                            Config.index_dataset_dir,
                            datasethelper.get_index_dir_name(
                                Config.data_file_prefix,
                                n,
                                width,
                                index_limit_level,
                                location_encoding,
                                Config.benchmark_shifted)
                        )

                        cipher_folder = os.path.join(cipher_folder, 'hve')
                        cipher_folder = os.path.join(cipher_folder, str(key_size))
                        token_folder = cipher_folder
                        os.makedirs(cipher_folder, exist_ok=True)

                        logger.info('Processing:')
                        logger.info(('----------------document_file', document_file))
                        logger.info(('----------------cipher_folder: ', cipher_folder))
                        logger.info(('----------------query_level', query_level))
                        logger.info(('----------------key_size', key_size))

                        queries_file = os.path.join(Config.queries_dir,
                                                    get_queries_file_name(Config.queries_file_prefix, width, query_level))

                        queries = hveutil.read_rectangle_queries(queries_file)

                        (time_setup, avg_time_gen_cipher, avg_time_gen_tokens,
                         avg_time_matching_per_token,
                         avg_time_matching_per_token_item_pair,
                         avg_time_matching_per_token_cipher_pair, avg_matches) = benchmark_hve(
                            width=width,
                            queries=queries,
                            data_file=document_file,
                            cipher_folder=cipher_folder,
                            token_folder=token_folder,
                            query_level=query_level,
                            location_encoding=location_encoding,
                            group_param=group_params[key_size],
                            index_limit_level=index_limit_level,
                            start_item_idx=Config.benchmark_hve_item_start,
                            end_item_idx=Config.benchmark_hve_item_end,
                            concatenate=Config.benchmark_hve_concatenate,
                            paralleled=Config.benchmark_paralleled,
                            num_processes=Config.benchmark_paralleled_num_processes
                        )

                        # write header
                        if not os.path.isfile(Config.benchmark_output_file):
                            # file not exist, create it and add header
                            with open(Config.benchmark_output_file, 'a+') as f:
                                csv_writer = csv.writer(f, delimiter='\t')
                                header = ['time',
                                          'width',
                                          'n_checkins',
                                          'query_level',
                                          'key_size',
                                          'time_setup',
                                          'avg_time_gen_cipher',
                                          'avg_time_gen_tokens',
                                          'avg_time_matching_per_token',
                                          'avg_time_matching_per_token_item_pair',
                                          'avg_time_matching_per_token_cipher_pair',
                                          'avg_matches']
                                csv_writer.writerow(header)

                        with open(Config.benchmark_output_file, 'a+') as f:
                            csv_writer = csv.writer(f, delimiter='\t')
                            row = [datetime.strftime(datetime.now(), datasethelper.GOWALLA_TIME_PATTERN),
                                   str(width),
                                   str(n),
                                   str(query_level),
                                   str(key_size),
                                   '{0:3f}'.format(time_setup),
                                   '{0:3f}'.format(avg_time_gen_cipher),
                                   '{0:3f}'.format(avg_time_gen_tokens),
                                   '{0:3f}'.format(avg_time_matching_per_token),
                                   '{0:3f}'.format(avg_time_matching_per_token_item_pair),
                                   '{0:3f}'.format(avg_time_matching_per_token_cipher_pair),
                                   '{0:3f}'.format(avg_matches)]
                            csv_writer.writerow(row)
