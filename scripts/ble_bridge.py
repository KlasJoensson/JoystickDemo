import asyncio
import sys

from bleak import BleakClient


def log(*args):
    print(*args, flush=True)


def make_notify_handler(uuid):
    def handler(_sender, data: bytearray):
        log(f"NOTIFY {uuid} {data.hex()}")
    return handler


MAX_CONNECT_ATTEMPTS = 6
RETRY_DELAY_SECONDS = 2


async def connect_with_retries(address: str, disconnected_callback) -> BleakClient:
    for attempt in range(1, MAX_CONNECT_ATTEMPTS + 1):
        client = BleakClient(address, disconnected_callback=disconnected_callback)
        try:
            await client.connect()
            return client
        except Exception as e:
            log(f"ERROR connect attempt {attempt}/{MAX_CONNECT_ATTEMPTS} failed: {e}")
            if attempt == MAX_CONNECT_ATTEMPTS:
                raise
            await asyncio.sleep(RETRY_DELAY_SECONDS)


async def run(address: str):
    disconnected = asyncio.Event()

    def on_disconnect(_client):
        disconnected.set()

    client = await connect_with_retries(address, on_disconnect)
    try:
        log(f"CONNECTED {address}")

        notify_uuids = []
        for service in client.services:
            log(f"SERVICE {service.uuid}")
            for char in service.characteristics:
                log(f"CHAR {char.uuid} {','.join(char.properties)}")
                if "notify" in char.properties or "indicate" in char.properties:
                    notify_uuids.append(char.uuid)

        for uuid in notify_uuids:
            try:
                await client.start_notify(uuid, make_notify_handler(uuid))
            except Exception as e:
                log(f"ERROR could not subscribe to {uuid}: {e}")

        log("READY")
        await disconnected.wait()
        log("DISCONNECTED")
    finally:
        await client.disconnect()


def main():
    if len(sys.argv) < 2:
        print("Usage: ble_bridge.py <device-address>", file=sys.stderr, flush=True)
        sys.exit(1)

    address = sys.argv[1]
    try:
        asyncio.run(run(address))
    except Exception as e:
        print(f"ERROR {e}", flush=True)
        sys.exit(1)


if __name__ == "__main__":
    main()
