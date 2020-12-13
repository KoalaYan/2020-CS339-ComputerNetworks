# python -m pip install ndeflib
# python -m pip install nfcpy

import ndef
import nfc
import struct
import sys
import random
import threading
import time
from bluetooth_server import BluetoothServer
from data_reader import DataReader
import os
from param import *

ndef_data_area = bytearray(64 * 16) # ndef data in nfc tag
ndef_message = None
data_reader = DataReader() # reader for log in /usr/test

# corresponding ndef text record
blue_record = ndef.TextRecord("bluetooth")
nfc_record = ndef.TextRecord("nfc")
mac_record = ndef.TextRecord(MAC_ADDR)
uuid_record = ndef.TextRecord(UUID)




def HEX(s):
    return bytearray.fromhex(s)


def create_ndef_message():
    global ndef_message

    data_buffer = data_reader.read()

    # If (step count, heart beat rit) records to send is more than $THRESHOLD,
    # then use bluetooth. Otherwise use NFC.
    is_bluetooth  = len(data_buffer) > NFC_THRESHOLD
    if is_bluetooth:
        print("[hand over to bluetooth]")
    else:
        print("[transmit directly with nfc]")

    # create ndef message
    if is_bluetooth:
        ndef_message = [blue_record, mac_record, uuid_record]
    else:
        ndef_message = [nfc_record]
        for data in data_buffer:
            step_record = ndef.TextRecord(data[0])
            heart_record = ndef.TextRecord(data[1])

            ndef_message.append(step_record)
            ndef_message.append(heart_record)

    # display def message
    print("[ndef message]")
    for record in ndef_message:
        print("      ", record)
    print("[message END]")

    return is_bluetooth


def generate_card():
    is_bluetooth = create_ndef_message()

    global ndef_data_area
    ndef_data_area[0] = 0x10  # NDEF mapping version '1.0'
    ndef_data_area[1] = 3    # Number of blocks that may be read at once
    ndef_data_area[2] = 3     # Number of blocks that may be written at once
    ndef_data_area[4] = 3    # Number of blocks available for NDEF data
    ndef_data_area[10] = 1    # NDEF read and write operations are allowed
    ndef_data_area[13] = 0    # length?
    ndef_data_area[14:16] = struct.pack('>H', sum(ndef_data_area[0:14]))  # Checksum

    blob = b''.join(ndef.message_encoder(ndef_message))
    ndef_data_area[16:16+len(blob)] = blob

    ndef_data_area[13] = len(blob)    # length
    ndef_data_area[14:16] = struct.pack('>H', sum(ndef_data_area[0:14]))  # Checksum

    return is_bluetooth


def ndef_read(block_number, rb, re):
    #print("    ndef_read: ", block_number, rb, re)
    if block_number < len(ndef_data_area) / 16:
        first, last = block_number*16, (block_number+1)*16
        block_data = ndef_data_area[first:last]
        return block_data


def ndef_write(block_number, block_data, wb, we):
    # print("    ndef_write")
    global ndef_data_area
    if block_number < len(ndef_data_area) / 16:
        first, last = block_number*16, (block_number+1)*16
        ndef_data_area[first:last] = block_data
        return True


def on_startup(target):
    idm, pmm, sys = '03FEFFE011223344', '01E0000000FFFF00', '12FC'
    target.sensf_res = bytearray.fromhex('01' + idm + pmm + sys)
    target.brty = "212F"

    return target

"""
Merge (step count, heart bit rate) records to a single string 
and send to the remote phone via bluetooth. i.e.
(6000, 64), (6100, 70), (6300, 66) -->
"6000#64;6100#70;6300#66"
"""
def data_list2str(data_list):
    data_string = ""
    for data in data_list[:-1]:
        if len(data) >=2:
            tmp = data[0] + "#" + data[1]
            data_string = data_string + tmp + ";"
    data = data_list[-1]
    tmp = data[0] + "#" + data[1] 
    data_string += tmp
    return data_string

"""
Activated when the phone touches this device and nfc connection is established.
When connected, prepare log data to send. If bluetooth is needed, created a 
bluetooth server.
"""
def on_connect(tag):
    print("**************************************************")
    print("                Tag activated                  ")
    print("--------------------------------------------------\n")

    is_bluetooth = generate_card()
    if(is_bluetooth):
        os.system('hciconfig hci0 up')
        os.system('hciconfig hci0 piscan')
        data_string = data_list2str(data_reader.data_buffer)
        bs = BluetoothServer(data_string)
        threading.Thread(target=bs.run).start()


    tag.add_service(0x0009, ndef_read, ndef_write)
    tag.add_service(0x000B, ndef_read, lambda: False)

    return True


if __name__ == "__main__":

    with nfc.ContactlessFrontend(PN532_PATH) as clf:
        print("**************************************************")
        print("  Emulation started, clf" + str(clf) + ".")
        print("**************************************************")

        timeout = lambda: time.time() - started > NFC_WAITINE
        while True:
            started = time.time()
            rv = clf.connect(card={'on-startup': on_startup, 'on-connect': on_connect}, terminate=timeout)
            if rv is None:
                print("**************************************************")
                print("                  Timeout                     ")
                print("**************************************************")
            elif rv is False: # keyboard interrupt
                break 
            else:
                print("**************************************************")
                print("                  Tag released                    ")
                print("**************************************************")
