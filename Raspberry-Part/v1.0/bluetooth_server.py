"""
bluetooth server to send log data
"""

import bluetooth
import time
import os

class BluetoothServer():
    server_sock = None
    port = None 
    uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    client_sock = None
    client_info = None 
    buffer = ["6000#70"]


    def __init__(self, buffer):
        self.buffer = buffer

    def init_server(self):
        self.server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.server_sock.bind(("", bluetooth.PORT_ANY))
        self.server_sock.listen(1)

        self.port = self.server_sock.getsockname()[1]

    def advertise(self):
        bluetooth.advertise_service(self.server_sock, "BluetoothServer", service_id=self.uuid,
                                    service_classes=[self.uuid, bluetooth.SERIAL_PORT_CLASS],
                                    profiles=[bluetooth.SERIAL_PORT_PROFILE],
                                    # protocols=[bluetooth.OBEX_UUID]
                                    )
        print("[BluetoothServer] Waiting for connection on RFCOMM channel", self.port)

    def listen(self):
        self.client_sock, self.client_info = self.server_sock.accept()
        print("[BluetoothServer] Accepted connection from", self.client_info)

    def send_data(self):
            print("[blue sent]:", self.buffer, "\n[blue sent END]")

            self.client_sock.send(self.buffer)

    def run(self):
        try:
            self.init_server()
            self.advertise()
            self.listen()
            self.send_data()

            print("[BluetoothServer] Disconnected.")
            self.client_sock.close()
            self.server_sock.close()
            print("[BluetoothServer] All done.")
            # os.system('hciconfig hci0 down')
        finally:
            self.client_sock.close()
            self.server_sock.close()


    def loop_run(self):
        while True:
            self.run()



if __name__ == "__main__":
    blue_server = BluetoothServer("hello world")
    blue_server.loop_run()
