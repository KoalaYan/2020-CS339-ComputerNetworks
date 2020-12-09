import nfc
import threading
import ndef
import nfc.snep


CLF_PATH = "tty:USB0:pn532"


def work_with_a_peer():
    def on_connect(llc):
        threading.Thread(target=llc.run).start(); return False

    clf = nfc.ContactlessFrontend()
    assert clf.open(CLF_PATH) is True
    llc = clf.connect(llcp={'on-connect': on_connect})
    print (llc)

    # socket = nfc.llcp.Socket(llc, nfc.llcp.DATA_LINK_CONNECTION)
    # socket.connect('urn:nfc:sn:snep')
    # records = [ndef.UriRecord("http://nfcpy.org")]
    # message = b''.join(ndef.message_encoder(records))
    # socket.send(b"\x10\x02\x00\x00\x00" + chr(len(message)) + message)
    # socket.recv()
    # socket.close() be simplified to this:
    """ the commented code above can be simplified to the two lines below """
    snep = nfc.snep.SnepClient(llc)
    snep.put_records([ndef.UriRecord("http://nfcpy.org")])


if __name__ == "__main__":
    work_with_a_peer()