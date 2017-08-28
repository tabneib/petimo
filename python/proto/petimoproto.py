#!/usr/bin/python3
# prototype for petimo, focus on processing XML db
# no clear structure at all, just play around!

import xml.etree.ElementTree as eT
from termcolor import colored
import datetime

__author__ = "Hoang-Duong Nguyen"
__status__ = "Prototype"


# todo

data_file = "proto.xml"
tree = eT.parse(data_file)
root = tree.getroot()

# Use Xpath to search in the xml data
# https://docs.python.org/3/library/xml.etree.elementtree.html#elementtree-xpath


def print_day(date, month, year):
    for day in root.findall(".//day[@date='"+date+"'][@month='"+month+"'][@year='"+year+"']"):
        print(day.attrib)
        data = day.attrib
        print("Total working time: " + data["total"])
    return


# traverse the db and calculate all not yet calculated durations
def calculate_all_durations():
    for block in root.findall(".//block[@duration='0']"):
        d = str(calculate_duration(block.attrib["start"], block.attrib["end"]))
        print("duration:" + d)
        block.set('duration', d)
    global tree
    tree.write('proto.xml')
    return


# traverse the db and calculate all not yet calculated total (day)
def calculate_all_day_total():
    for day in root.findall(".//day[@total='0']"):
        total = 0
        for block in day:
            total += int(block.attrib["duration"])
        day.set("total", str(total))
    global tree
    tree.write("proto.xml")
    return


# calculate total working time of the given day
def calculate_day_total(date, month, year):
    for day in root.findall(".//day[@date='"+date+"'][@month='"+month+"'][@year='"+year+"']"):
        total = 0
        for block in day:
            total += int(block.attrib["duration"])
        day.set("total", str(total))
    global tree
    tree.write("proto.xml")
    return


# Calculate the duration of a block given its start and end time
def calculate_duration(start, end):
    if end == "open":
        return 0
    start = start.split(":")
    start_h = int(start[0])
    start_m = int(start[1])
    end = end.split(":")
    end_h = int(end[0])
    end_m = int(end[1])
    return (end_h - start_h)*60 + (end_m - start_m)

# <----------------------------------------------------------------------------
#   Program main functionality
# ---------------------------------------------------------------------------->


def main():
    exit_bool = False
    print_header("PETIMO Prototype")
    while not exit_bool:
        try:
            opt = input("1 - Add week\n" +
                        "2 - Add day\n" +
                        "3 - Add block\n" +
                        "4 - Update block\n" +
                        "5 - Auto update day\n" +
                        "6 - List all open block\n" +
                        "7 - Exit\n")
            if opt == "1":
                add_week()
            elif opt == "2":
                add_day()
            elif opt == "3":
                add_block()
            elif opt == "4":
                update_block()
            elif opt == "5":
                update_day()
            elif opt == "6":
                print_err_msg("Not yet implemented!")
                continue
            elif opt == "7":
                exit_bool = True
            else:
                print_err_msg("Invalid option!\n")
        except SyntaxError:
            print_err_msg("Invalid option!\n")
    return


# Manually add a week
def add_week():
    print_header("ADD WEEK")
    y = input("Year: ")
    years = []
    # Find all matched years
    for year in root.findall(".//year[@value='"+y+"']"):
        years.append(year)

    if len(years) == 0:
        print_err_msg("No matched year found!")
    elif len(years) > 1:
        print_err_msg("More than one matched years found!")
    else:
        w_id = input("ID: ")
        weeks = []
        # Find all matched weeks to determine if the given week already exits
        for week in years[0].findall(".//week[@id='"+w_id+"']"):
            weeks.append(week)
        if len(weeks) == 0:
            if confirm_msg('Add Week[id = "'+w_id+'", year = "'+y+'"]?'):
                years[0].append(eT.Element("week", {"id": w_id, "total": "",
                                                    "year": y}))
                global tree
                tree.write(data_file)
        else:
            print_err_msg("Week with id " + w_id + " already exits!")
    return


def add_day():



    # year | month | week | date | name | id
    print_header("ADD DAY")
    y = input("Year: ")
    m = input("Month: ")
    w = input("Week: ")
    weeks = []
    # Find all matched weeks
    for week in root.findall(".//week[@year='" + y + "'][@id='" + w + "']"):
        weeks.append(week)

    if len(weeks) == 0:
        print_err_msg("No matched week found!")
    elif len(weeks) > 1:
        print_err_msg("More than one matched weeks found!")
    else:
        d = input("Date: ")
        n = input("Name: ")
        d_id = input("ID: ")
        days = []
        # Find all matched weeks to determine if the given week already exits
        for day in weeks[0].findall(".//day[@id='" + d_id + "']\
                                        [@date='" + d + "'][@name='" + n + "']\
                                        [@month='" + m + "']"):
            days.append(day)
        if len(days) == 0:
            if confirm_msg('Add Day[date = "' + d + '", week = "' + w + '\
                            , month = "'+m+'",  year= "'+y+'"\
                            , name = "'+n+'", id = "'+d_id+'", ]?'):
                weeks[0].append(eT.Element("day", {"date": d, "id": d_id,
                                                   "month": m, "name": n,
                                                   "total": "0", "week": w,
                                                   "year": y}))
                global tree
                tree.write(data_file)
        else:
            print_err_msg("Day already exits!")
    return


def add_block():
    print_header("ADD BLOCK")
    opt = input("1 - Add a block for Today\n2 - Manually\n")
    if opt == "1":
        now = datetime.datetime.now()
        # todo
        y = str(now.year)
        m = str(now.month)
        d = str(now.day)
        print("year: " + y + ", month: "+m+", date: "+d)
        days = []
        # Find all matched days
        xpath = \
            ".//day[@year='" + y + "'][@month='" + m + "'][@date='" + d + "']"
        for day in root.findall(xpath):
            days.append(day)
        if len(days) == 0:
            print_err_msg("Day entry for today not found!")
        elif len(days) > 1:
            print_err_msg("More than one matched day entries for today found!")
        else:
            cat = input("Category: ")
            s = input("Start: ")
            if s.lower() == "now":
                s = str(now.hour) + ":" + str(now.minute)
            e = input("End: ")
            if confirm_msg('Add Block[category = "' +cat+ '", start = "'\
                                   + s + '", end = "'+e+'"]?'):
                days[0].append(eT.Element("block", {"category": d, "duration": "0",
                                                   "end": e, "start": s}))
                global tree
                tree.write(data_file)
    elif opt == "2":
        print("")
    else:
        print_err_msg("Invalid option!")

    return


def update_block():
    return


def update_day():
    return


def print_header(msg):
    print("|------------------------------------------------|")
    print("|\t" + msg)
    print("|------------------------------------------------|")
    return


# todo: use red font!
def print_err_msg(msg):
    print(colored("\nERROR: " + msg + "\n", "red"))

    return


# Ask for confirmation from user
def confirm_msg(msg):
    while True:
        opt = input(msg + "\n[Y/N]")
        if opt.lower() == "y" or opt.lower() == "yes":
            return True
        elif opt.lower() == "n" or opt.lower() == "no":
            return False
        else:
            continue

# print_day("25", "10", "2016")
# calculate_all_durations()
# calculate_all_day_total()

main()