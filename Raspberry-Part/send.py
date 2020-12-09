import io
import random
import ndef

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
