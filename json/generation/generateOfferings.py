import os
import re
import sys
import json


def time_convert(armyTime):
    armyTime = int(armyTime)
    hours = (armyTime % 1200) // 100
    mins = armyTime % 100
    pm = armyTime >= 1200
    return "{}:{:02d} {}".format(hours, mins, "PM" if pm else "AM")


def defaultInt(val, default=0):
    try:
        return default if val is None else int(val)
    except (ValueError, TypeError):
        return default


def defaultStr(val, default=""):
    return default if val is None else int(val)


offerings = {
    "$schema": "../jsonSchemas/schemaOfferings.json",
}

for filename in os.listdir():
    p = re.compile("(.+)\.json")
    if not p.match(filename):
        continue
    else:
        print(filename)

    fileContents = json.loads(open(filename, "r").read())
    for sectionIn in fileContents['data']:
        p = re.compile("(\w+) (\d+)")
        m = p.match(sectionIn['termDesc'])
        termYear = m.group(1).lower() + " " + m.group(2)
        if m.group(1).lower() == "interterm":
            termYear = "jterm " + m.group(2)

        courseID = sectionIn['subject'] + '-' + sectionIn['courseNumber']
        professors = []
        for faculty in sectionIn['faculty']:
            professors.append(faculty['displayName'])

        location = None
        startDate = None
        endDate = None

        if sectionIn['meetingsFaculty']:
            meetingTime = sectionIn['meetingsFaculty'][0]['meetingTime']
            location = {
                'building': meetingTime['building'],
                'roomNumber': meetingTime['room'],
            }

            datePattern = re.compile("(\d{1,2})/(\d{1,2})/(\d{4})")
            startDate = datePattern.match(meetingTime['startDate'])
            startDate = "{}-{}-{}".format(startDate.group(3), startDate.group(1), startDate.group(2))
            endDate = datePattern.match(meetingTime['endDate'])
            endDate = "{}-{}-{}".format(endDate.group(3), endDate.group(1), endDate.group(2))

        meetings = []
        for meetingFaculty in sectionIn['meetingsFaculty']:
            meeting = {
                'days': [],
                'startTime': time_convert(defaultInt(meetingFaculty['meetingTime']['beginTime'])),
                'endTime': time_convert(defaultInt(meetingFaculty['meetingTime']['endTime']))}
            for day in ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday']:
                if meetingFaculty['meetingTime'][day]:
                    meeting['days'].append(day)
            meetings.append(meeting)
        hours = sectionIn['creditHours']
        sectionOut = {
            "crn": str(sectionIn['courseReferenceNumber']),
            "section": defaultInt(sectionIn['sequenceNumber']),
            "type": sectionIn['scheduleTypeDescription'],
            "credits": 0 if hours == None else defaultInt(hours),
            "professors": professors,
            "location": location,
            "numEnrolled": defaultInt(sectionIn['enrollment']),
            "maximumEnrollment": defaultInt(sectionIn['maximumEnrollment']),
            "startDate": startDate,
            "endDate": endDate,
            "meetings": meetings
        }

        if not termYear in offerings:
            offerings[termYear] = {}
        if not courseID in offerings[termYear]:
            offerings[termYear][courseID] = []
        offerings[termYear][courseID].append(sectionOut)

open(sys.argv[1], "w").write(json.dumps(offerings, indent=2))
