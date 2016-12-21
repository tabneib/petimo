#!/usr/bin/python3

__author__ = "Hoang-Duong Nguyen"
__copyright__ = "Copyright 2016-2017, Petimo"
__credits__ = ["Hoang-Duong Nguyen"]
__license__ = "GPL"
__version__ = "1.0.0"
__maintainer__ = "Hoang-Duong Nguyen"
__email__ = "chuhoangan@gmail.com"
__status__ = "Prototype"


class Day(object):
    def __init__(self, duration, time_per_category, time_per_folder):
        self.duration = duration
        self.time_per_category = time_per_category
        self.time_per_folder = time_per_folder


class Week(object):
    def __init__(self, days):
        self.days = days

    # todo
    def get_duration(self):
        return

    # todo
    def get_time_per_category(self):
        return

    # todo
    def get_time_per_folder(self):
        return


class Month(object):
    def __init__(self, days):
        self.days = days

    # todo
    def get_duration(self):
        return

    # todo
    def get_time_per_category(self):
        return

    # todo
    def get_time_per_folder(self):
        return


# This is out of scope for now
class Year(object):
    def __init__(self, months):
        self.months = months
