import logging
import os
import random
import csv
from collections import namedtuple, defaultdict
from datetime import datetime
from searchableencryption.config import Config
from searchableencryption.rangequery import rangequery
from searchableencryption.rangequery.rangeutil import LocationEncoding, map_point_to_cell

MIN_LAT = -90
MAX_LAT = 90
MIN_LON = -180
MAX_LON = 190

GRID_LOS_ANGELES_MAX_LAT = 34.342324
GRID_LOS_ANGELES_MIN_LAT = 33.699675
GRID_LOS_ANGELES_MAX_LON = -118.144458
GRID_LOS_ANGELES_MIN_LON = -118.684687
GOWALLA_TIME_PATTERN = '%Y-%m-%dT%H:%M:%S%z'
MAX_CHECKINS_COUNT = 1000

checkin_attributes = ['user_id', 'time', 'lat', 'lon', 'location_id']
Checkin = namedtuple('Checkin', checkin_attributes)


logger = logging.getLogger(__name__)


def create_random_checkin_database(n: int, d: int) -> dict:
    """
    Create a random database with n items, and locations of items in range [0, d-1]
    :param n: number of items
    :param d: dimension
    :return: sample database
    """
    database = dict()
    for i in range(n):
        database[i] = (random.randint(0, d-1), random.randint(0, d-1))

    return database


def load_database(filename: str) -> dict:
    """
    Load database from file in format:
    id w1,w2...
    as database as: id -> list of words
    :param filename: file name
    :return:
    """
    db = dict()
    with open(filename, 'r') as f:
        for line in f:
            line = line.rstrip()
            p = line.split(" ")
            ind = p[0]
            words = p[1].split(",")
            db[ind] = words

    return db


def save_database(database: dict, filename: str) -> bool:
    """
    Save database to file with format: for each documents
    id w1,w2,...
    :param database: database as: id -> list of words
    :param filename: output file name
    :return: True if save successfully
    """
    with open(filename, 'w') as f:
        for i, words in database.items():
            line = '{ind} {ws}'.format(ind=str(i), ws=','.join(str(w) for w in words))
            f.write(line + '\n')

    return True


def save_checkins(checkins: list, out_file: str):
    """
    Save checkins to file
    :param checkins:
    :param out_file:
    :return:
    """
    with open(out_file, 'w') as f:
        csv_writer = csv.writer(f, delimiter='\t')
        for c in checkins:
            row = [str(c.user_id),
                   datetime.strftime(c.time, GOWALLA_TIME_PATTERN),
                   str(c.lat),
                   str(c.lon),
                   str(c.location_id)]
            csv_writer.writerow(row)


def get_los_angeles_rectangle() -> tuple:
    """
    Get the rectangle representing Los Angeles boundary
    as (x_top_left, y_top_left, x_len, y_len)
    with x as longitude and y as latitude
    :return: the rectangle representing Los Angeles boundary
    """
    return (GRID_LOS_ANGELES_MIN_LON,
            GRID_LOS_ANGELES_MAX_LAT,
            abs(GRID_LOS_ANGELES_MAX_LON - GRID_LOS_ANGELES_MIN_LON),
            abs(GRID_LOS_ANGELES_MAX_LAT - GRID_LOS_ANGELES_MIN_LAT))


def check_inside_boundary(checkin: Checkin, rectangle: tuple) -> bool:
    """
    Check whether a checkin is inside a boundary
    :param checkin: the checkin
    :param rectangle: the rectangle representing Los Angeles boundary
    :return: whether a checkin is inside a boundary
    """
    (x_top_left, y_top_left, x_len, y_len) = rectangle  # (min_lon, max_lat, lon_len, lat_len)
    max_lat = y_top_left
    min_lat = max_lat - y_len
    min_lon = x_top_left
    max_lon = min_lon + x_len

    return min_lat <= checkin.lat <= max_lat and min_lon <= checkin.lon <= max_lon


def check_checkin_los_angeles(checkin: Checkin) -> bool:
    """
    Check whether a checkin is inside the Los Angeles boundary
    :param checkin: checkin: the checkin
    :return: whether a checkin is inside the Los Angeles boundary
    """
    return check_inside_boundary(checkin, get_los_angeles_rectangle())


def load_dataset_gowalla(filepath: str, filters=None) -> list:
    """
    Load Gowalla dataset from file
    :param filepath: Gowalla data file
    :param filters: filters to filter checkins
    :return:
    """
    if filters is None:
        filters = []
    data = list()

    with open(filepath, 'r') as csv_file:
        gowalla_reader = csv.reader(csv_file, delimiter='\t')
        for row in gowalla_reader:
            user_id = int(row[0])
            checkin_time = datetime.strptime(row[1], GOWALLA_TIME_PATTERN)
            lat = float(row[2])
            lon = float(row[3])

            # fix out-of-range GPS point
            if lat <= MIN_LAT or MAX_LAT <= lat or lon <= MIN_LON or MAX_LON <= lon:
                continue
            lat = max(lat, MIN_LAT)
            lat = min(lat, MAX_LAT)
            lon = max(lon, MIN_LON)
            lon = min(lon, MAX_LON)

            loc_id = int(row[4])
            c = Checkin(user_id=user_id, time=checkin_time, lat=lat, lon=lon, location_id=loc_id)

            ok = True
            for ft in filters:
                ok = ok and ft(c)

            if ok:
                data.append(c)

    return data


def convert_dataset_to_user_dict(data: list) -> dict:
    """
    Convert dataset from list of Checkins to dict of user_id => checkins of this user
    :param data: dataset
    :return: dict of user_id => checkins of this user
    """
    data_user = defaultdict(list)
    for d in data:
        data_user[d.user_id].append(d)

    return data_user


def get_checkins_count(data_user: dict, max_checkins_count=MAX_CHECKINS_COUNT) -> dict:
    """
    Get number of checkins of each user as dictionary
    :param data_user: dict of user_id => checkins of this user
    :param max_checkins_count: the threshold to cap maximum number of checkins of a user
    :return: dict of count => ids of users who had `count` checkins
    """
    user_data_count = defaultdict(list)
    for user_id, user_data in data_user.items():
        count = min(len(user_data), max_checkins_count)
        user_data_count[count].append(user_id)

    return user_data_count


def get_users_total_max(total: int, data_user: dict) -> list:
    """
    Get total `total` users who had the most numbers of checkins
    :param total: total number of users to get
    :param data_user: dict of user_id => checkins of this user
    :return: list of `total` users who had the most numbers of checkins
    """
    count = MAX_CHECKINS_COUNT
    user_data_count = get_checkins_count(data_user)
    selected_user_ids = []
    while 0 <= count:
        if len(selected_user_ids) + len(user_data_count[count]) < total:
            selected_user_ids.extend(user_data_count[count])
            count -= 1
        else:
            num_users_need = total - len(selected_user_ids)
            selected_user_ids.extend(user_data_count[count][-num_users_need:])
            break

    return selected_user_ids


def get_users_total_random(total: int, user_ids_org: list) -> list:
    """
    Get randomly total `total` users
    :param total: total number of users to get
    :param user_ids_org: list of user ids
    :return: list of `total` users
    """
    user_ids = list(user_ids_org)
    selected_user_ids = []
    count = 0
    while count < total:
        pos = random.randint(0, len(user_ids) - 1)
        selected_user_ids.append(user_ids[pos])
        del user_ids[pos]
        count += 1

    return selected_user_ids


def select_random_checkins_indices(checkins_index: list, num_selected: int) -> list:
    tmp = list(checkins_index)
    selected_ids = []
    count = 0
    while count < num_selected:
        pos = random.randint(0, len(tmp) - 1)
        selected_ids.append(tmp[pos])
        del tmp[pos]
        count += 1

    return selected_ids


def get_documents_file_name(prefix, num_checkins, width, encoding, shifted=False, include_extension=True) -> str:
    """
    Get name for a document files
    :param prefix: file prefix
    :param num_checkins: number of checkins
    :param width: width of grid
    :param encoding: location encoding
    :param shifted: whether this a shifted document or not (default is False)
    :param include_extension: should the extension be included (default is True)
    :return: the document file name
    """
    ext = '.txt' if include_extension else ''
    shift = '_shifted' if shifted else ''

    return (prefix + 'checkins_{0}_width_{1}_{2}' + shift + ext).format(num_checkins, width, encoding.name)


def get_checkins_file_name(prefix: str, num_checkins: int) -> str:
    """
    Get file name for a checkin file
    :param prefix: file name prefix
    :param num_checkins: number of checkins in this file
    :return: file name for a checkin file
    """
    return (prefix + 'checkins_{0}.txt').format(num_checkins)


def select_checkins_from_gowalla(input_file: str):
    """
    Select check-ins from Gowalla dataset
    :param input_file: Gowalla check-in file
    """
    filters = [check_checkin_los_angeles]
    gowalla_data = load_dataset_gowalla(filepath=input_file, filters=filters)
    logger.info(('Loaded', len(gowalla_data), ' check-ins'))

    output_dir = Config.checkins_dir
    os.makedirs(output_dir, exist_ok=True)
    output_prefix = Config.data_file_prefix

    num_checkins_totals = Config.benchmark_num_checkins
    num_checkins_totals = sorted(num_checkins_totals, key=int, reverse=True)  # make sure we get bigger set first
    selected_indices = list()

    prev_indices = set()

    for i in range(len(num_checkins_totals)):
        num_checkins_total = int(num_checkins_totals[i])
        if not selected_indices:
            checkins_index = [i for i in range(len(gowalla_data))]
            selected_indices = select_random_checkins_indices(checkins_index, num_checkins_total)
        else:
            selected_indices = select_random_checkins_indices(selected_indices, num_checkins_total)
            assert set(selected_indices) <= prev_indices

        prev_indices = set(selected_indices)

        checkins = list()
        for checkin_index in selected_indices:
            checkins.append(gowalla_data[checkin_index])

        file_name = os.path.join(output_dir, get_checkins_file_name(output_prefix, num_checkins_total))

        save_checkins(checkins, file_name)

        logger.info(('Saved ', file_name))


def convert_checkins_to_documents():
    """
    Convert checkins to documents
    :return:
    """
    encoding = LocationEncoding[Config.index_encoding]
    os.makedirs(Config.documents_dir, exist_ok=True)

    # num_checkins_totals = [50000, 20000, 10000]
    num_checkins_totals = Config.benchmark_num_checkins
    num_checkins_totals = sorted(num_checkins_totals, key=int, reverse=True)  # make sure we get bigger set first

    for i in range(len(num_checkins_totals)):
        # load checkins from files
        num_checkins_total = int(num_checkins_totals[i])
        input_file = os.path.join(
            Config.checkins_dir, get_checkins_file_name(Config.data_file_prefix, num_checkins_total))

        checkins = load_dataset_gowalla(filepath=input_file, filters=[])
        logger.info(('Loaded', len(checkins), ' check-ins'))

        # boundary
        min_lat = GRID_LOS_ANGELES_MIN_LAT
        max_lat = GRID_LOS_ANGELES_MAX_LAT
        min_lon = GRID_LOS_ANGELES_MIN_LON
        max_lon = GRID_LOS_ANGELES_MAX_LON

        # the shifted grid is where cell_x, cell_y are shifted 1 cell, used to test query alignment

        num_cells_list = sorted(Config.benchmark_widths)
        for num_cells in num_cells_list:
            dataset = dict()  # original dataset
            dataset_shifted = dict()  # shifted dataset
            for item_id in range(len(checkins)):
                c = checkins[item_id]
                cell_x, cell_y = map_point_to_cell(c.lon, c.lat, min_lon, max_lon, min_lat, max_lat, num_cells)
                cell_x, cell_y = int(cell_x), int(cell_y)
                dataset[item_id] = (cell_x, cell_y)
                if 0 <= cell_x - 1 and 0 <= cell_y - 1:
                    # we will not take this item if it is out of space
                    dataset_shifted[item_id] = (cell_x - 1, cell_y - 1)

            dataset_encoded = rangequery.convert_to_documents(dataset, num_cells, encoding)
            dataset_shifted_encoded = rangequery.convert_to_documents(dataset_shifted, num_cells, encoding)

            document_file = os.path.join(
                Config.documents_dir,
                get_documents_file_name(
                    Config.data_file_prefix,
                    num_checkins_total,
                    num_cells,
                    encoding,
                    shifted=False,
                    include_extension=True)
            )
            save_database(dataset_encoded, document_file)
            logger.info(('Save data to ' + document_file))

            document_file_shifted = os.path.join(
                Config.documents_dir,
                get_documents_file_name(
                    Config.data_file_prefix,
                    num_checkins_total,
                    num_cells,
                    encoding,
                    shifted=True,
                    include_extension=True)
            )
            save_database(dataset_shifted_encoded, document_file_shifted)

            logger.info(('Save data to ' + document_file_shifted))


def get_index_dir_name(prefix: str, num_checkins: int, width: int, level, encoding: LocationEncoding, shifted) -> str:
    """
    Get name for a document files
    :param shifted:
    :param prefix: file prefix
    :param num_checkins: number of checkins
    :param width: width of grid
    :param level: level of the tree
    :param encoding: location encoding
    :return: the document file name
    """
    shift = '_shifted' if shifted else ''
    return (prefix + 'checkins_{0}_width_{1}_level_{2}_{3}' + shift).format(num_checkins, width, level, encoding.name)
