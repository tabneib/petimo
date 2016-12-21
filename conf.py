#!/usr/bin/python3
# Reading and initializing configuration data
# author: Hoang-Duong Nguyen

import configparser as cP

__author__ = "Hoang-Duong Nguyen"
__copyright__ = "Copyright 2016-2017, Petimo"
__credits__ = ["Hoang-Duong Nguyen"]
__license__ = "GPL"
__version__ = "1.0.0"
__maintainer__ = "Hoang-Duong Nguyen"
__email__ = "chuhoangan@gmail.com"
__status__ = "Prototype"

# relative path to configuration file
config_file = "configuration.conf"

# configuration constants
XML_FILE = "XML_FILE"

# xml_db = conf.
# todo: making use of the library to read configuration file
constant_dict = {"XML_FILE": ""}


# Return the value of the given configuration constant
def get(self, constant):
    if not (constant in constant_dict):
        return "UNKNOWN CONSTANT"
    else:
        return constant_dict[constant]