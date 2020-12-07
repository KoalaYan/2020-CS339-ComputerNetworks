# 2020-CS339-ComputerNetworks
## What is NFC?
Near-Field-Communication (NFC) is a set of communication protocols for communication 
between two electronic devices over a distance of 4 cm (1.5 in) or less.
NFC offers a low-speed connection with simple setup that can be used to bootstrap more-capable wireless connections.
###Work Mode
Basic mode of communication is half duplex in NFC, where in one NFC device transmits while other device receives. 
This is also referred as “Listen before Talk”. Here one of the two NFC devices will function as initiator which 
first listen on channel and transmits only when no other signal is there on channel. Here Initiator polls 
the other devices which comes closer to it. The other NFC device referred as target listens and responds to initiator as per requested message.
1. ***NFC active-active mode***\
   When working in active mode, the initiator and target both have power supplies, so it isn’t necessary for the initiator 
   to send power to the target to allow the target to perform useful tasks. The two devices use alternate signal transmissions to send data to each other. 
   In other words, both devices generate an RF field and send data by modulating that RF field.\
   When in active mode, the two devices use an Amplitude Shift Keying (ASK) modulation scheme. To avoid collisions, 
   the receiving device turns off its field, so only one device is transmitting at any given time. Advantages of using active mode are that the data rate is usually higher 
   and it’s theoretically possible to work at longer distances.
   
2. ***NFC active-passive mode***\
   When working in passive mode, the initiator sends RF energy to the target to power it. The target modulates this energy 
   in order to send data back to the initiator. Unlike active mode, the target relies on load modulation (making changes to 
   the amplitude of the original signal) to transmit data. It doesn’t generate a field of its own, but rather changes the field of the initiator to transfer data.

## Our Attempts
This is the guide for using our NFC bracelet.

### Preparation
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
### NFC usage classification
We will focus on ***work with a peer*** part.
#### Read and write tags
to do
#### Emulate a card
to do 
#### Work with a peer
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
Output: ``LLC: Local(MIU=128, LTO=100ms) Remote(MIU=1024, LTO=500ms)``
and ``True``
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
Output: ``LLC: Local(MIU=128, LTO=100ms) Remote(MIU=1024, LTO=500ms)``
* The Application code is not supposed to work directly with the llc object 
but use it to create Socket objects for the actual communication. The code 
is simplified as follows:
```
import nfc.snep
nep = nfc.snep.SnepClient(llc)
snep.put_records([ndef.UriRecord("http://nfcpy.org")])
```
Output: ``True``

## Reference
- [1] [NFC Tutorial](https://iotpoint.wordpress.com/nfc-tutorial/)
- [2] [nfcpy](https://nfcpy.readthedocs.io/en/latest/topics/get-started.html)