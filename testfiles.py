#!/usr/bin/python
# -*- coding: UTF-8 -*-
import sys
import os

def create():
    for i in range(0,10):
        fn = "/usr/test/test_"+str(i)+".txt"
        fo = open(fn, "w+")
        print("Create file, filename is: ", fo.name)
        str_in = 'This is the ' + str(i) + ' test file.'
        fo.write(str_in)
        fo.close()

def delete():
    FilePath = '/usr/test/'
    for f in os.listdir(FilePath):
        fn = os.path.join(FilePath, f)
        if os.path.isfile(fn):
            os.remove(fn)
            print("Delete file, filename is: ", fn)

def add():
    FilePath = '/usr/test/'
    num = len(os.listdir(FilePath))
    for i in range(0+num,10+num):
        fn = "./test/test_"+str(i)+".txt"
        fo = open(fn, "w+")
        print("Add file, filename is: ", fo.name)
        str_in = 'This is the ' + str(i) + ' test file.'
        fo.write(str_in)
        fo.close()

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('Usage: [create|delete|add')
        raise SystemExit(1)

    if 'create' == sys.argv[1]:
        create()
    elif 'delete' == sys.argv[1]:
        delete()
    elif 'add' == sys.argv[1]:
        add()
    else:
        print('Unknown command')
        raise SystemExit(1)

    
