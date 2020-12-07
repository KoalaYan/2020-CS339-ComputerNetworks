# 2020-CS339-ComputerNetworks
This is the guide for using our NFC bracelet. Some tutorial information can be found on
https://nfcpy.readthedocs.io/en/latest/topics/get-started.html
## Preparation
1. First, please make sure your NFC-module has loaded on your Raspberry Pi. 
2. Second, you'd better check out whether your ubuntu has installed **libusb(Linux)** and  **nfcpy** pypi module.
3. Verify your installation. ``python -m nfc`` If nothing is going wrong, **nfcpy** can find your contactless reader. If all goes well, the output will tell you that your reader was found.
The output will be as follows: \
``saas`` 
4. Achieve your NFC device's information. 
* If device is attached to a Linux computer on USB. Use ``lsusb`` to get **USB bus number** and **device number**. 
* If device is connected either directly or through a USB UART adapter. ``ls -l /dev/tty[ASU]* /dev/serial?`` to get Uart interface number.
* A special kind of device bus that does not require any physical hardware is provided for testing and application prototyping. 
It works by sending NFC communication frames across a UDP/IP connection and can be used to connect two processes running an nfcpy application either locally or remote.
## NFC usage classification
We will focus on ***work with a peer*** part.
### Read and write tags
to do
### Emulate a card
to do 
### Work with a peer
***LLCP(Logical Link Control Protocol)***: Allows multiplexed communications 
between two NFC Forum Devices with either peer able to send protocol data units 
at any time and no restriction to a single application run in one direction.
* An LLCP link between two NFC devices is requested with the llcp argument to ``clf.connect()``
```
import nfc
clf = ContactlessFrontend('usb')
clf.connect(llcp={})
# this will return True/False to represent connection status
```
* Use callback functions to add some useful stuff.
```
def on_connect(llc):
    print llc
    return True

clf.connect(llcp={'on-connect': connected})
# it will firstly print the llc info
# and return True/False to represent connection status
```
* Start a thread in the callback to execute the llc.run* loop and return with False. 
This tells ``clf.connect()`` to return immediately with the llc instance.
```
import threading
def on_connect(llc):
    threading.Thread(target=llc.run).start()
    return False

llc = clf.connect(llcp={'on-connect': on_connect})
print llc
```
