#!/usr/bin/python3
# Provides the model accessing XML database

import xml.etree.ElementTree as eT
import conf
from calender_objects import *

__author__ = "Hoang-Duong Nguyen"
__copyright__ = "Copyright 2016-2017, Petimo"
__credits__ = ["Hoang-Duong Nguyen"]
__license__ = "GPL"
__version__ = "1.0.0"
__maintainer__ = "Hoang-Duong Nguyen"
__email__ = "chuhoangan@gmail.com"
__status__ = "Prototype"


class XMLdb(object):
    data_file = conf.get(conf.XML_FILE)
    tree = eT.parse(data_file)
    root = tree.getroot()

    # Use Xpath to search in the xml data
    # https://docs.python.org/3/library/xml.etree.elementtree.html#elementtree-xpath

    # ------------------------------------------------------------------------>
    #   Getters
    # <------------------------------------------------------------------------

    # retrive general info of the program
    # todo
    def get_info(self):
        # todo: update view
        return

    # retrieve a day object
    # todo
    def get_day(self, day, month, year):
        d = Day()
        # todo: update view
        return

    # retrieve a week object
    # todo
    def get_week(self, week, year):
        w = Week()
        # todo: update view
        return

    # retrieve a month object
    # todo
    def get_month(self, month, year):
        m = Month()
        # todo: update view
        return

    # retrieve a year object
    # todo
    def get_year(self, year):
        y = Year()
        # todo: update view
        return

    # ------------------------------------------------------------------------>
    #   Setters
    # <------------------------------------------------------------------------

    # todo
    def set_block(self, category, start):
        return

    # todo
    def set_day(self, date, month, year, id):
        return

    # todo
    def set_week(self, week, year):
        return

    # todo
    def set_year(self, year):
        return

    def __init__(self, view):
        self.view = view
        return

    # ------------------------------------------------------------------------>
    #   monitoring ops
    # <------------------------------------------------------------------------

    # todo
    # return 0 if not startable, 1 otherwise
    # (this return value is used by Controller to maintain the program state)
    def start(self):
        # todo: update view
        return

    # todo
    # return 0 if not stoppable, 1 otherwise
    def stop(self):
        # todo: update view
        return



