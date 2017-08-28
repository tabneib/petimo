#!/usr/bin/python3
# View module
# do nothing but provide diplay funtionality

from view_module import *

__author__ = "Hoang-Duong Nguyen"
__copyright__ = "Copyright 2016-2017, Petimo"
__credits__ = ["Hoang-Duong Nguyen"]
__license__ = "GPL"
__version__ = "1.0.0"
__maintainer__ = "Hoang-Duong Nguyen"
__email__ = "chuhoangan@gmail.com"
__status__ = "Prototype"

view = None


class View(object):

    # todo: complete the dict. Only use lowercase!
    # todo: this can be moved to a separate python file which stores all global
    # constants.
    OPTIONS_MENU = {"HELP": ["help", "h", "1"]}

    # todo
    MENU_MAIN = ["HELP", "STATUS", "MONITOR", "DATA"]
    MENU_DATA = [""]

    def welcome_msg(self):
        print("Petimo 1.0.0 \n \
        Author: Hoang-Duong Nguyen (chuhoangan@gmail.com)\n \
        Personal time monitoring application \n \
        ----------------------------------------------------------")
        return

    # todo: this is just the first draft!
    def display_menu(self):
        display_list(self.MENU_MAIN)
        # todo: dont use global, move to a separate module!
        global OPTIONS_MENU
        choice = input("Enter an option: ")
        check = check_input(choice, OPTIONS_MENU)
        if check == "INVALID_OPTION_ERROR":
            print("Invalid Option! \n")
            return self.display_menu()
        else:
            return check

    # todo
    def display_option(self):
        return

    # todo
    def display_help(self):
        return

    # todo
    def display_status(self):
        return

    # todo
    def display_day(self):
        return

    # todo
    def display_week(self):
        return

    # todo
    def display_month(self):
        return

    # todo
    def display_year(self):
        return

    def __init__(self):
       return


def create_view():
    global view
    view = View()
    return

