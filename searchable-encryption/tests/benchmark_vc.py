"""
Benchmark Vector Commitments
"""
import csv
import math
import os
from datetime import datetime
from time import time
import logging
from charm.core.math.integer import randomBits

from searchableencryption.toolbox import datasethelper
from .context import vectorcommitment, Config

logger = logging.getLogger(__name__)


def benchmark_vc_multiple():
    """
    Benchmark multiple configuration
    """
    num_runs = 1000
    assert 0 < num_runs
    for k in Config.benchmark_vector_commitment_key_sizes:
        for q in Config.benchmark_vector_commitment_vector_lens:
            l = Config.benchmark_vector_commitment_message_elem_bit_len

            logging.info(('Benchmark k = {0}, q = {1}, l = {2}'.format(k, q, l)))

            time_gen = 0
            num_runs_gen = min(num_runs, 10)
            for _ in range(num_runs_gen):
                start_key_gen = time()
                n, a, s, e, p_1, p_2 = vectorcommitment.key_gen(k, l, q)
                end_key_gen = time()

                time_gen += end_key_gen - start_key_gen
            avg_time_gen = time_gen / num_runs_gen

            n, a, s, e, p_1, p_2 = vectorcommitment.key_gen(k, l, q)

            m = list()

            for i in range(q):
                m.append(int(randomBits(l)))

            time_commit = 0

            for num_run in range(num_runs):
                start_commit = time()
                c, aux = vectorcommitment.commit(m, s, n)
                end_commit = time()
                time_commit += end_commit - start_commit
            avg_time_commit = time_commit / num_runs

            c, aux = vectorcommitment.commit(m, s, n)
            commitment_size = int(math.ceil(c.bit_length() / 8))


            time_open = 0
            time_verify = 0
            open_size = 0

            for i in range(q):
                for num_run in range(num_runs):
                    start_open = time()
                    lamda_i = vectorcommitment.open(m, i, e[i], s, p_1, p_2)
                    end_open = time()
                    time_open += end_open - start_open
                lamda_i = vectorcommitment.open(m, i, e[i], s, p_1, p_2)
                open_size = int(math.ceil(lamda_i.bit_length() / 8))

                for num_run in range(num_runs):
                    start_verify = time()
                    verification_result = vectorcommitment.verify(c, m[i], i, s, lamda_i, e, n, l)
                    end_verify = time()
                    time_verify += end_verify - start_verify

                    assert verification_result

            avg_time_open = time_open / (num_runs * q)
            avg_time_verify = time_verify / (num_runs * q)

            # write header
            if not os.path.isfile(Config.benchmark_output_file):
                # file not exist, create it and add header
                with open(Config.benchmark_output_file, 'a+') as f:
                    csv_writer = csv.writer(f, delimiter='\t')
                    header = ['time',
                              'key_size',
                              'vector_len',
                              'message_elem_bit_len',
                              'avg_time_gen',
                              'avg_time_commit',
                              'avg_time_open',
                              'avg_time_verify',
                              'commitment_size',
                              'open_size']
                    csv_writer.writerow(header)

            with open(Config.benchmark_output_file, 'a+') as f:
                csv_writer = csv.writer(f, delimiter='\t')
                row = [datetime.strftime(datetime.now(), datasethelper.GOWALLA_TIME_PATTERN),
                       str(k),
                       str(q),
                       str(l),
                       '{0:.6f}'.format(avg_time_gen),
                       '{0:.6f}'.format(avg_time_commit),
                       '{0:.6f}'.format(avg_time_open),
                       '{0:.6f}'.format(avg_time_verify),
                       str(commitment_size),
                       str(open_size)]
                logging.info(str(row))
                csv_writer.writerow(row)
