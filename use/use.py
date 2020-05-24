from os import environ
from sys import argv, stderr

PYTHON_BINARY_DIR_ROOT = "/Library/Frameworks/Python.framework/Versions/"


def remove_all_python_from_path():
    path = environ["PATH"]
    path_arr = path.split(":")
    new_path = ''
    for p in path_arr:
        if not p.startswith(PYTHON_BINARY_DIR_ROOT):
            new_path = new_path + ':' + p
    return new_path


version_replacements = {
    "11": "11.0.1",
    "8": "1.8",
    "7": "1.7",
    "6": "1.6",
    "5": "1.5",
    "4": "1.4",
    "3": "1.3",
    "2": "1.2",
    "1": "1.0",
    "3.5": "3.5",
    "3.7": "3.7",
    "3.8": "3.8",
}

if len(argv) < 2:
    stderr.write("Usage: use [java|graal|python] version")
    stderr.write("\n")
    stderr.write("Version can be: ")
    sep = ""
    for version in version_replacements:
        stderr.write("%s%s -> %s" % (sep, version, version_replacements[version]))
        sep = " ,"
    stderr.write("Not all versions are available for all programs.")
    print("echo ")
    exit(1)
else:
    what = argv[1]

if what != "java" and what != "graal" and what != "python":
    version = what
    what = "java"
else:
    version = argv[2] if len(argv) > 2 else "11.0.6" if what == "graal" else "14"

if version in version_replacements:
    version = version_replacements[version]

if what == "python":

    print("export PATH=" + PYTHON_BINARY_DIR_ROOT + version + "/bin" + remove_all_python_from_path())
else:
    print("export JAVA_HOME=$(/usr/libexec/java_home -v %s)" % version)
