import time
import random
import os
import sys
import threading

class DataReader(object):
    
    data_buffer = []
    filepath='/usr/test/'
    pos = 0

    def  __init__(self):
       pass


    def run(self):
        self.data_buffer = []
        file_list = [f for f in os.listdir(self.filepath) if os.path.isfile(os.path.join(self.filepath, f))]
        file_list.sort()
        file_list = file_list[self.pos: ]
        data_list = []
        for fp in file_list:
            fpath = os.path.join(self.filepath, fp)
            mf = open(fpath)
            data = mf.read()
            data_list.append(data)
        for entry in data_list:
            tmp = entry.split("#")
            if len(tmp) >= 3:
                tmp = [tmp[1],tmp[2]]
                self.data_buffer.append(tmp)
        #print(self.data_buffer)


    def read(self):
        self.run()
        self.pos += len(self.data_buffer)
        return self.data_buffer


if __name__ == "__main__":
    dr = DataReader()
    dr.run()