## Bracelet Part v1.0
Simulate the log data generation process of the bracelet and synchronize them to the phone via NFC or Bluetooth.

### Introduction
##### 1. daemon.py daemon_run.py
generate bracelet log to */usr/bracelet-log*
##### 2. bluetooth_server.py 
create a bluetooth server and send bracelet data to the phone
##### 3. emulate_tag.py 
pn532 tag-emulation program
##### 4. param.py
constants and parameters
##### 5. data_reader.py
read log files and store the *(step count, heart beat rate)* pairs into an array
##### 6. run.py 
start tag-emulation

### How to Run 
1. run ``sudo python3 daemon_run.py`` to start producing bracelet log
2. run ``sudo python3 run.py`` to start tag-emulation

### How to Stop
1. run ``sudo python3 daemon_run.py stop`` to stop producing bracelet log 
2. press ``Ctrl + C`` in the terminal of ***run.py*** to stop tag-emulation
