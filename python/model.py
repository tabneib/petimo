#!/usr/bin/python3
# Model part of MVC
# Maintaining the program state and processing data
# Choose between XML and DB mode
# (for consistency this should be chosen at installation and then kept fixed)
# (for now the option is stored in configuration file)

import model_state_machine
import db_xml


__author__ = "Hoang-Duong Nguyen"
__copyright__ = "Copyright 2016-2017, Petimo"
__credits__ = ["Hoang-Duong Nguyen"]
__license__ = "GPL"
__version__ = "1.0.0"
__maintainer__ = "Hoang-Duong Nguyen"
__email__ = "chuhoangan@gmail.com"
__status__ = "Prototype"

# the Model instant
model = None


def create_model():
    # todo: retrieve the db option from configuration file
    global model
    model = db_xml.XMLdb()

# ---------------------------------------------------------------------------->
#   Funtions for View updating
# <----------------------------------------------------------------------------


