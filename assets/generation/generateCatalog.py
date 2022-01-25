import os
import re
import sys
import assets

catalog = {
    "$schema": "../jsonSchemas/schemaCatalog.json",
    "catalogYear": "2020-2021",
    "departments": []
}

print(os.listdir())
for filename in os.listdir():
    p = re.compile("(.+)\.json")
    if not p.match(filename):
        continue

    print(filename)
    departmentName = p.match(filename).group(1)
    departmentIn = assets.loads(open(filename, "r").read())
    departmentOut = {
        "name": departmentName,
        "description": "",
        "courses": []
    }
    for courseIn in departmentIn:
        p = re.compile("(\d+)(\w?)")
        m = p.match(courseIn['courseCode'])
        courseOut = {
            "courseID": courseIn['departmentCode'] + '-' + courseIn['courseCode'],
            "name": courseIn['title'],
            "prefix": courseIn['departmentCode'],
            "number": int(m.group(1)),
            "description": courseIn['description']
        }
        departmentOut['courses'].append(courseOut)
    catalog['departments'].append(departmentOut)

open(sys.argv[1], "w").write(assets.dumps(catalog, indent=2))
