"""Run the crypto oracle against actual Android JNI in an isolated emulator.

Push the proxy dex JAR and both .so files into a task-specific /data/local/tmp
folder first. No application installation or private wallet data is needed.
"""
import argparse
import ctypes as c
import json
import subprocess
from pathlib import Path
from verify import verify


class AndroidAPI:
    def __init__(self, sdk):
        self.sdk = sdk

    def JUB_GPC_Finalize(self):
        return self.sdk.request("finalize")["code"]

    def JUB_GPC_Initialize(self, info, certificate, private_key):
        value = info._obj
        fields = {name: getattr(value, name).decode() for name, _ in value._fields_}
        fields["keyLength"] = int(fields["keyLength"], 16)
        fields.update(crt=certificate.decode(), sk=private_key.decode())
        return self.sdk.request("init", info=fields)["code"]

    def JUB_GPC_BuildMutualAuthData(self, output):
        response = self.sdk.request("mutual")
        self.sdk.assign(output, response["output"])
        return response["code"]

    def JUB_GPC_OpenSecureChannel(self, data):
        return self.sdk.request("open", data=data.decode())["code"]

    def JUB_GPC_TLVDecode(self, data, tag, output):
        response = self.sdk.request("tlv", data=data.decode())
        result = json.loads(response["output"]) if response["output"] else {}
        tag._obj.value = result.get("tag", 0)
        self.sdk.assign(output, result.get("value"))
        return response["code"]


class AndroidSDK:
    accept_short_plain_response = True
    wide_counter = True
    authenticated_errors = True
    def __init__(self, adb, serial, jar, library):
        self.process = subprocess.Popen([
            adb, "-s", serial, "shell", f"CLASSPATH={jar}", "app_process", "/system/bin",
            "org.haobtc.onekey.card.gpchannel.GPChannelProxy", library,
        ], stdin=subprocess.PIPE, stdout=subprocess.PIPE, text=True, bufsize=1)
        self.lib = AndroidAPI(self)
        self.buffers = {}

    def request(self, operation, **kwargs):
        self.process.stdin.write(json.dumps(dict(operation=operation, **kwargs)) + "\n")
        self.process.stdin.flush()
        line = self.process.stdout.readline()
        if not line:
            raise RuntimeError("Android JNI proxy terminated")
        return json.loads(line)

    def assign(self, pointer, value):
        if value is None:
            pointer._obj.value = None
        else:
            buffer = c.create_string_buffer(value.encode())
            address = c.addressof(buffer)
            self.buffers[address] = buffer
            pointer._obj.value = address

    def take(self, pointer):
        return c.string_at(self.buffers.pop(pointer.value)) if pointer.value else None

    def build(self, name, data, header=(0x80, 0x20, 0, 0)):
        return self.build_hex(name, data.hex(), header)

    def build_hex(self, name, text, header=(0x80, 0x20, 0, 0)):
        result = self.request("safe" if name == "BuildSafeAPDU" else "build",
                              data=text, **dict(zip(("cla", "ins", "p1", "p2"), header)))
        return result["code"], result["output"].encode() if result["output"] is not None else None

    def parse(self, name, data):
        result = self.request("parseSafe" if name == "ParseSafeAPDUResponse" else "parse", data=data.hex())
        output = json.loads(result["output"]) if result["output"] is not None else {}
        return result["code"], output.get("wRet", 0), output.get("response", "").encode() if output else None

    def tlv(self, data):
        result = self.request("tlv", data=data.hex())
        output = json.loads(result["output"]) if result["output"] is not None else {}
        return result["code"], output.get("tag", 0), output.get("value", "").encode() if output else None

    def certificate(self, data):
        result = self.request("certificate", data=data.hex())
        output = json.loads(result["output"]) if result["output"] is not None else {}
        return result["code"], output.get("sn", "").encode() if output else None, output.get("subjectID", "").encode() if output else None

    def close(self):
        self.process.stdin.close()
        self.process.wait(timeout=10)
        if self.process.returncode:
            raise RuntimeError("Android JNI proxy failed")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--adb", required=True)
    parser.add_argument("--serial", required=True)
    parser.add_argument("--jar", required=True)
    parser.add_argument("--library", required=True)
    parser.add_argument("--reference", required=True)
    parser.add_argument("--report", required=True)
    args = parser.parse_args()
    recovered = AndroidSDK(args.adb, args.serial, args.jar, args.library)
    original = AndroidSDK(args.adb, args.serial, args.jar, args.reference)
    try:
        report = verify(recovered, original)
        report.update(transport="Android ART/JNI via app_process", c_allocation_checks=False)
        Path(args.report).write_text(json.dumps(report, indent=2) + "\n")
        print(json.dumps(report, indent=2))
    finally:
        recovered.close()
        original.close()
