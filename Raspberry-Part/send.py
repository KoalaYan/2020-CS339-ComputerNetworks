import io
import random
import ndef
import time
import nfc
import logging

log = logging.getLogger('main')
filetype = file if sys.version_info.major < 3 else io.IOBase  # noqa: F821

def send_message(args, llc, message):
    t0 = time.time() if args.timeit else None
    if not nfc.snep.SnepClient(llc).put_records(message):
        log.error("failed to send message")
    if t0 is not None:
        transfer_time = time.time() - t0
        message_size = len(b''.join(ndef.message_encoder(message)))
        print("message sent in {0:.3f} seconds ({1} byte @ {2:.0f} byte/sec)"
              .format(transfer_time, message_size,
                      message_size / transfer_time))

def run_send_ndef_action(args, llc):
    if isinstance(args.ndef, filetype):
        octets = io.BytesIO(args.ndef.read())
        args.selected = -1
        args.ndef = list()
        records = list(ndef.message_decoder(octets, known_types={}))
        while records:
            args.ndef.append(records)
            records = list(ndef.message_decoder(octets, known_types={}))

    if args.select == "first":
        args.selected = 0
    elif args.select == "last":
        args.selected = len(args.ndef) - 1
    elif args.select == "next":
        args.selected = args.selected + 1
    elif args.select == "cycle":
        args.selected = (args.selected + 1) % len(args.ndef)
    elif args.select == "random":
        args.selected = random.choice(range(len(args.ndef)))

    if 0 <= args.selected < len(args.ndef):
        log.info("send {}".format(args.ndef[args.selected]))
        send_message(args, llc, args.ndef[args.selected])
