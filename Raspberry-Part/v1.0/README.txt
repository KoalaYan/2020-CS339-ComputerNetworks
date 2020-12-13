1. Introduction
    1) daemon.py daemon_run.py 
            generate bracelet log to /usr/bracelet-log
    2) bluetooth_server.py 
            create a bluetooth server and send bracelet data to the phone
    3) emulate_tag.py 
            pn532 tag-emulation program
    4) param.py
            constants and parameters
    5) run.py 
            entrance file to run

2. How to Run 
    1. run "sudo python3 daemon_run.py" to start producing bracelet log
    2. run "sudo python3 run.py" to start tag-emulation

3. How to Stop
    1. run "sudo python3 daemon_run.py stop" to stop producing bracelet log 
    2. press "Ctrl C" in the terminal for "run.py" to stop tag-emulation
