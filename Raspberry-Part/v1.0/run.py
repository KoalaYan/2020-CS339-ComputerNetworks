from emulate_tag import *
import threading


def run():
    with nfc.ContactlessFrontend(PN532_PATH) as clf:
        print("**************************************************")
        print("  Emulation started, clf" + str(clf) + ".")
        print("**************************************************\n")

        timeout = lambda: time.time() - started > NFC_WAITINE
        while True:
            started = time.time()
            rv = clf.connect(card={'on-startup': on_startup, 'on-connect': on_connect}, terminate=timeout)
            if rv is None: # time out
                print("\n**************************************************")
                print("                  Timeout                     ")
                print("**************************************************\n")
            elif rv is False: # keyboard interrupt
                break 
            else: # tag released smoothlly
                print("\n--------------------------------------------------")
                print("                  Tag released                    ")
                print("**************************************************\n")


if __name__  == "__main__":
    run()