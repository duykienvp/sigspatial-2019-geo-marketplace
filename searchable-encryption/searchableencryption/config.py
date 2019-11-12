"""
Configuration handler: loading logging and program configuration
NOTE: This file must be included to be able to use the service correctly
"""

import logging
import logging.config
import os
import sys

import yaml

CONFIG_FILE = 'config.yml'
LOG_CONFIG_FILE = 'logging.yml'


def parse_dashed_list(value: str):
    """ Parse a list in dashed notation to a list
    Args:
        value: string with 2 numbers separated by 1 dash
    Returns:
        list from those 2 numbers or `None` if error occurred
    Examples:
    >>> parse_dashed_list('1-5')
    [1, 2, 3, 4, 5]
    """
    dash_pos = value.find('-')
    if dash_pos != -1:
        s = int(value[:dash_pos])
        t = int(value[dash_pos + 1:])
        return list(range(s, t + 1))
    return None


class Config(object):
    """ Configuration
    """
    is_config_loaded = False

    project_dir = None

    # data
    data_dir = None
    dataset_dir = None
    checkins_dir = None
    documents_dir = None
    data_file_prefix = None

    queries_dir = None
    queries_file_prefix = None
    queries_plot_file_prefix = None

    # index
    index_dir = None
    index_dataset_dir = None
    index_encoding = None

    # benchmark
    benchmark_shifted = None
    benchmark_out_dir = None
    benchmark_output_file = None
    benchmark_paralleled = None
    benchmark_paralleled_num_processes = None
    benchmark_save_index = None
    benchmark_widths = None
    benchmark_num_checkins = None
    benchmark_query_levels = None
    benchmark_index_limit_levels = None

    benchmark_hve_key_sizes = None
    benchmark_hve_item_start = None
    benchmark_hve_item_end = None
    benchmark_hve_concatenate = None

    benchmark_vector_commitment_key_sizes = None
    benchmark_vector_commitment_message_elem_bit_len = None
    benchmark_vector_commitment_vector_lens = None

    # crypto
    password = None
    crypto_hxt_using_preorder_id = None
    crypto_bloom_filter_false_positive_rate = None
    crypto_hxt_key_file = None
    crypto_hxt_oxt_edb_file = None
    crypto_hxt_shve_file = None
    crypto_hxt_bloomfilter_file = None

    crypto_hve_pk_file = None
    crypto_hve_sk_file = None
    crypto_hve_cipher_file = None
    crypto_hve_token_file = None

    @staticmethod
    def load_config(file, reload_config=False):
        """ Load config from a file.
        Args:
            file: config file in YAML format
            reload_config: should we reload config
        """
        logger = logging.getLogger(__name__)
        # Load the configuration file
        if file is None:
            logger.error('No config file provided')
            return

        if not Config.is_config_loaded or reload_config:
            with open(file, 'r') as conf_file:
                cfg = yaml.load(conf_file)
                # print(cfg)

                # Load config to variables

                if 'project_dir' in cfg:
                    Config.project_dir = cfg['project_dir']

                # data files
                if 'data_files' in cfg:
                    data_files_cfg = cfg['data_files']

                    if 'data_dir' in data_files_cfg:
                        Config.data_dir = os.path.join(
                            Config.project_dir, data_files_cfg['data_dir'])

                    if 'datasets_dir' in data_files_cfg:
                        Config.dataset_dir = os.path.join(
                            Config.data_dir, data_files_cfg['datasets_dir'])

                    if 'checkins_dir' in data_files_cfg:
                        Config.checkins_dir = os.path.join(
                            Config.dataset_dir, data_files_cfg['checkins_dir'])

                    if 'documents_dir' in data_files_cfg:
                        Config.documents_dir = os.path.join(
                            Config.dataset_dir, data_files_cfg['documents_dir'])

                    if 'data_file_prefix' in data_files_cfg:
                        Config.data_file_prefix = data_files_cfg['data_file_prefix']

                if 'index' in cfg:
                    index_cfg = cfg['index']
                    if 'index_dir' in index_cfg:
                        Config.index_dir = os.path.join(
                            Config.project_dir, index_cfg['index_dir'])

                    if 'datasets_dir' in index_cfg:
                        Config.index_dataset_dir = os.path.join(
                            Config.index_dir, index_cfg['datasets_dir'])

                    if 'encoding' in index_cfg:
                        Config.index_encoding = index_cfg['encoding'].upper()

                if 'benchmark' in cfg:
                    benchmark_cfg = cfg['benchmark']

                    if 'shifted' in benchmark_cfg:
                        Config.benchmark_shifted = benchmark_cfg['shifted']

                    if 'queries_dir' in benchmark_cfg:
                        Config.queries_dir = os.path.join(
                            Config.data_dir, benchmark_cfg['queries_dir'])

                    if 'queries_file_prefix' in benchmark_cfg:
                        Config.queries_file_prefix = benchmark_cfg['queries_file_prefix']

                    if 'queries_plot_file_prefix' in benchmark_cfg:
                        Config.queries_plot_file_prefix = benchmark_cfg['queries_plot_file_prefix']

                    if 'out_dir' in benchmark_cfg:
                        Config.benchmark_out_dir = benchmark_cfg['out_dir']

                    if 'out_file' in benchmark_cfg:
                        Config.benchmark_output_file = os.path.join(
                            Config.benchmark_out_dir, benchmark_cfg['out_file'])

                    if 'paralleled' in benchmark_cfg:
                        Config.benchmark_paralleled = benchmark_cfg['paralleled']

                    if 'paralleled_num_process' in benchmark_cfg:
                        Config.benchmark_paralleled_num_processes = benchmark_cfg['paralleled_num_process']

                    if 'save_index' in benchmark_cfg:
                        Config.benchmark_save_index = benchmark_cfg['save_index']

                    if 'widths' in benchmark_cfg:
                        Config.benchmark_widths = benchmark_cfg['widths']

                    if 'num_checkins' in benchmark_cfg:
                        Config.benchmark_num_checkins = benchmark_cfg['num_checkins']

                    if 'query_levels' in benchmark_cfg:
                        Config.benchmark_query_levels = benchmark_cfg['query_levels']

                    if 'index_limit_levels' in benchmark_cfg:
                        Config.benchmark_index_limit_levels = benchmark_cfg['index_limit_levels']

                    if 'hve_key_sizes' in benchmark_cfg:
                        Config.benchmark_hve_key_sizes = benchmark_cfg['hve_key_sizes']

                    if 'hve_item_start' in benchmark_cfg:
                        Config.benchmark_hve_item_start = benchmark_cfg['hve_item_start']

                    if 'hve_item_end' in benchmark_cfg:
                        Config.benchmark_hve_item_end = benchmark_cfg['hve_item_end']

                    if 'hve_concatenate' in benchmark_cfg:
                        Config.benchmark_hve_concatenate = benchmark_cfg['hve_concatenate']

                    if 'vector_commitment' in benchmark_cfg:
                        vector_commitment_cfg = benchmark_cfg['vector_commitment']

                        if 'key_sizes' in vector_commitment_cfg:
                            Config.benchmark_vector_commitment_key_sizes = vector_commitment_cfg['key_sizes']

                        if 'message_elem_bit_len' in vector_commitment_cfg:
                            Config.benchmark_vector_commitment_message_elem_bit_len = \
                                vector_commitment_cfg['message_elem_bit_len']

                        if 'vector_lens' in vector_commitment_cfg:
                            Config.benchmark_vector_commitment_vector_lens = vector_commitment_cfg['vector_lens']

                if 'crypto' in cfg:
                    crypto_cfg = cfg['crypto']

                    if 'password' in crypto_cfg:
                        Config.password = crypto_cfg['password']

                    if 'hxt' in crypto_cfg:
                        hxt_config = crypto_cfg['hxt']

                        if 'using_preorder_id' in hxt_config:
                            Config.crypto_hxt_using_preorder_id = hxt_config['using_preorder_id']

                        if 'bloom_filter_false_positive_rate' in hxt_config:
                            Config.crypto_bloom_filter_false_positive_rate = float(
                                hxt_config['bloom_filter_false_positive_rate'])

                        if 'key_file' in hxt_config:
                            Config.crypto_hxt_key_file = hxt_config['key_file']
                        if 'oxt_edb_file' in hxt_config:
                            Config.crypto_hxt_oxt_edb_file = hxt_config['oxt_edb_file']
                        if 'shve_file' in hxt_config:
                            Config.crypto_hxt_shve_file = hxt_config['shve_file']
                        if 'bloomfilter_file' in hxt_config:
                            Config.crypto_hxt_bloomfilter_file = hxt_config['bloomfilter_file']

                    if 'hve' in crypto_cfg:
                        hve_config = crypto_cfg['hve']

                        if 'pk_file' in hve_config:
                            Config.crypto_hve_pk_file = hve_config['pk_file']

                        if 'sk_file' in hve_config:
                            Config.crypto_hve_sk_file = hve_config['sk_file']

                        if 'cipher_file' in hve_config:
                            Config.crypto_hve_cipher_file = hve_config['cipher_file']

                        if 'token_file' in hve_config:
                            Config.crypto_hve_token_file = hve_config['token_file']

    @staticmethod
    def get_config_str() -> str:
        """ Get string representation of all configuration
        """
        values = []
        for k, v in Config.__dict__.items():
            tmp = str(k) + '=' + str(v) + '\n'
            values.append(tmp)
        values.sort()
        res = ''.join(values)
        return res


def setup_logging(default_path='logging.yml', default_level=logging.INFO, env_key='LOG_CFG'):
    """Setup logging configuration
    """
    path = default_path
    value = os.getenv(env_key, None)
    if value:
        path = value
    if os.path.exists(path):
        with open(path, 'rt', encoding=sys.getfilesystemencoding()) as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)
    else:
        logging.basicConfig(level=default_level)


dir_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
logging_config_file = os.path.join(dir_path, LOG_CONFIG_FILE)
setup_logging(default_path=logging_config_file)


Config.load_config(os.path.join(dir_path, CONFIG_FILE))
