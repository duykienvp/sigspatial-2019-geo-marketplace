""" Some utilities that are used in different modules
"""
import os
import pickle

PICKLE_MAX_BYTES = 2 ** 31 - 1  # used to write big file using pickle


def shift_left_bit_length(x: int) -> int:
    """ Shift 1 left bit length of x

    :param int x: value to get bit length
    :returns: 1 shifted left bit length of x
    """
    return 1 << (x - 1).bit_length()


def next_power_2(x: int) -> int:
    """ Get the next number that is power of 2 and bigger than x

    :param int x: value to evaluate
    :returns: the next number that is power of 2 and bigger than x or 0 if x < 1
    """
    return 0 if x < 1 else shift_left_bit_length(x)


def pickle_dump(data, file_path, delete_after_dumps=False):
    """
    Dump data for a file
    :param delete_after_dumps:
    :param data: data
    :param file_path: file to dump
    :return: number of bytes dumped
    """
    bytes_out = pickle.dumps(data)
    if delete_after_dumps:
        del data
    with open(file_path, 'wb') as f_out:
        for idx in range(0, len(bytes_out), PICKLE_MAX_BYTES):
            f_out.write(bytes_out[idx:idx + PICKLE_MAX_BYTES])
            f_out.flush()
            os.fsync(f_out.fileno())

    return len(bytes_out)


def pickle_load(file_path):
    """
    Load data from file
    :param file_path: file to load
    :return: loaded data
    """
    bytes_in = bytearray(0)
    input_size = os.path.getsize(file_path)
    with open(file_path, 'rb') as f_in:
        for _ in range(0, input_size, PICKLE_MAX_BYTES):
            bytes_in += f_in.read(PICKLE_MAX_BYTES)
    return pickle.loads(bytes_in)
