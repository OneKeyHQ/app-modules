#!/usr/bin/env python3
"""Run the production Swift/EMASCurl client in a dedicated native simulator process."""
import argparse
import hashlib
import http.server
import json
import os
import platform
import signal
import time
from pathlib import Path
import socket
import ssl
import subprocess
import tempfile
import threading


HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[3]
PACKAGE = HERE.parents[1]
OUT = ROOT / ".tmp/ios-sni-replay"


def run(*args, **kwargs):
    return subprocess.run([str(arg) for arg in args], check=True, text=True, **kwargs)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


class FixtureServer(http.server.ThreadingHTTPServer):
    allow_reuse_address = True
    daemon_threads = True

    def __init__(self, port, context, mode):
        super().__init__(("127.0.0.1", port), Handler)
        self.context, self.mode = context, mode
        self.events, self.complete, self.connections = [], [], 0
        self.warm_connection = None
        self.lock = threading.Lock()

    def get_request(self):
        sock, address = super().get_request()
        self.connections += 1
        tls = self.context.wrap_socket(sock, server_side=True)
        tls.settimeout(5)
        tls.fixture_id = self.connections
        return tls, address


class Handler(http.server.BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, *_args):
        pass

    def handle_request(self):
        server = self.server
        connection = self.connection.fixture_id
        if self.path == "/target":
            server.events.append({"event": "headers", "connection": connection, "method": self.command})
        try:
            if self.headers.get("Transfer-Encoding", "").lower() == "chunked":
                chunks = []
                while True:
                    size = int(self.rfile.readline().split(b";", 1)[0], 16)
                    if size == 0:
                        while self.rfile.readline() not in (b"\r\n", b"\n", b""):
                            pass
                        break
                    chunk = self.rfile.read(size)
                    if len(chunk) != size or self.rfile.read(2) != b"\r\n":
                        raise IOError("Incomplete chunk")
                    chunks.append(chunk)
                body = b"".join(chunks)
            else:
                length = int(self.headers.get("Content-Length", "0"))
                body = self.rfile.read(length)
                if len(body) != length:
                    raise IOError("Incomplete body")
            if self.path == "/warm":
                server.warm_connection = connection
                self.reply(200, b"warm")
                return
            record = {"connection": connection, "method": self.command,
                      "bodyBytes": len(body), "bodySha256": hashlib.sha256(body).hexdigest(),
                      "host": self.headers.get("Host")}
            with server.lock:
                server.complete.append(record)
                first = len(server.complete) == 1
            if first and server.mode in ("drop", "close-listener"):
                if server.mode == "close-listener":
                    server.shutdown()
                    server.server_close()
                self.close_connection = True
                self.connection.shutdown(socket.SHUT_RDWR)
                self.connection.close()
            else:
                self.reply(int(server.mode) if first and server.mode.isdigit() else 200, b"response")
        except (OSError, ValueError) as error:
            server.events.append({"event": "read-error", "error": str(error), "connection": connection})
            self.close_connection = True

    def reply(self, status, body):
        self.send_response(status)
        self.send_header("Content-Length", str(len(body)))
        if status in (503, 408):
            self.send_header("Retry-After", "0")
        self.end_headers()
        if self.command != "HEAD":
            self.wfile.write(body)
        self.wfile.flush()

    do_GET = do_POST = do_PUT = do_DELETE = do_HEAD = do_OPTIONS = do_PATCH = handle_request


def build(emas, port, sdk_root, arch):
    ios = sdk_root / "ios"
    sources = [ios / f"SniConnect{name}.swift" for name in ("Client", "PinnedTransport", "Validation", "Core", "Log")]
    hashes = {str(path): digest(path) for path in sources}
    print(json.dumps({"sourceSha256": hashes, "emasRoot": str(emas)}), flush=True)
    client = sources[0].read_text()
    anchor = "    components.host = hostname\n"
    assert client.count(anchor) == 1, "Production port fixture anchor changed"
    client = client.replace(anchor, anchor + f"    components.port = {port}\n")
    copied = OUT / "SniConnectClient.swift"
    copied.write_text(client + "\n" + (HERE / "ClientFixture.swift").read_text())
    include = OUT / "include"
    include.mkdir(exist_ok=True)
    link = include / "EMASCurl"
    if link.is_symlink() and link.resolve() != emas / "EMASCurl":
        link.unlink()
    if not link.exists():
        link.symlink_to(emas / "EMASCurl", target_is_directory=True)
    bridge = include / "Fixture.h"
    bridge.write_text('''#import <EMASCurl/EMASCurlProtocol.h>
#import <EMASCurl/EMASCurlConfigurationManager.h>
static inline EMASCurlConfiguration *ReplayConfiguration(NSString *identifier) {
  return [[EMASCurlConfigurationManager sharedManager] configurationForID:identifier];
}
static inline void ReplayStoreConfiguration(NSString *identifier, EMASCurlConfiguration *configuration) {
  [[EMASCurlConfigurationManager sharedManager] setConfiguration:configuration forID:identifier];
}
''')
    (include / "module.modulemap").write_text('module EMASCurl { header "Fixture.h" export * }\n')
    sdk = subprocess.check_output(["xcrun", "--sdk", "iphonesimulator", "--show-sdk-path"], text=True).strip()
    curl = emas / "precompiled/libcurl-HTTP2.xcframework/ios-arm64_x86_64-simulator"
    objects = []
    for source in sorted((emas / "EMASCurl").glob("*.m")):
        obj = OUT / (source.stem + ".o")
        run("xcrun", "clang", "-target", f"{arch}-apple-ios15.0-simulator", "-isysroot", sdk,
            "-fobjc-arc", "-fmodules", "-I", include, "-I", curl / "Headers", "-c", source, "-o", obj)
        objects.append(obj)
    binary = OUT / "SniReplay"
    run("xcrun", "swiftc", "-swift-version", "5", "-parse-as-library", "-target", f"{arch}-apple-ios15.0-simulator",
        "-sdk", sdk, "-I", include, "-Xcc", "-I" + str(curl / "Headers"),
        copied, *sources[1:], HERE / "ReplayMain.swift", *objects, curl / "libcurl.a",
        "-framework", "Foundation", "-framework", "UIKit", "-framework", "CFNetwork",
        "-framework", "CoreTelephony", "-framework", "NetworkExtension", "-lz", "-o", binary)
    run("codesign", "--force", "--sign", "-", binary)
    assert hashes == {str(path): digest(path) for path in sources}, "Production sources changed"
    return binary, hashes


def select_simulator(runtime, device_type):
    inventory = json.loads(subprocess.check_output(["xcrun", "simctl", "list", "--json"], text=True))
    runtimes = [item for item in inventory["runtimes"] if item.get("isAvailable") and ".iOS-" in item["identifier"] and int(item["version"].split(".")[0]) >= 15]
    runtimes.sort(key=lambda item: tuple(int(part) for part in item["version"].split(".")), reverse=True)
    for candidate in runtimes:
        if runtime and candidate["identifier"] != runtime:
            continue
        for device in inventory["devices"].get(candidate["identifier"], []):
            identifier = device.get("deviceTypeIdentifier")
            if device.get("isAvailable") and device["name"].startswith("iPhone") and identifier and (not device_type or identifier == device_type):
                return candidate["identifier"], identifier
    raise RuntimeError("No available iOS runtime with an iPhone device; replay tests cannot be skipped")


def terminate(_signum, _frame):
    raise SystemExit(143)


def main():
    global OUT
    started = time.monotonic()
    parser = argparse.ArgumentParser()
    parser.add_argument("--emascurl-root", type=Path, required=True)
    parser.add_argument("--sdk-root", type=Path, default=PACKAGE)
    parser.add_argument("--runtime")
    parser.add_argument("--device-type")
    parser.add_argument("--output-dir", type=Path, default=OUT)
    parser.add_argument("--prepared-dependency", action="store_true")
    args = parser.parse_args()
    OUT = args.output_dir.resolve()
    args.runtime, args.device_type = select_simulator(args.runtime, args.device_type)
    arch = platform.machine()
    assert arch in ("arm64", "x86_64"), "Unsupported simulator architecture"
    OUT.mkdir(parents=True, exist_ok=True)
    original_emas = args.emascurl_root.resolve()
    original_protocol = original_emas / "EMASCurl/EMASCurlProtocol.m"
    original_hash = digest(original_protocol)
    sdk_root = args.sdk_root.resolve()
    for name in ("Client", "PinnedTransport", "Validation", "Core", "Log"):
        relative = f"ios/SniConnect{name}.swift"
        assert digest(sdk_root / relative) == digest(PACKAGE / relative), "Packaged Swift source differs"
    # Compile the exact dependency patched by the distributed helper, never the live Pods copy.
    dependency = original_emas if args.prepared_dependency else Path(tempfile.mkdtemp(prefix="packaged-dependency-", dir=OUT)) / "EMASCurl"
    helper = sdk_root / "ios/apply_emascurl_no_replay.rb"
    if not args.prepared_dependency:
        run("cp", "-cR", original_emas, dependency)
        run("ruby", helper, "--pod-root", dependency, "--version", "1.5.5")
    run("ruby", helper, "--pod-root", dependency, "--version", "1.5.5", "--verify-only")
    print(json.dumps({"packagedHelper": str(helper), "helperSha256": digest(helper),
                      "originalProtocolSha256": original_hash,
                      "patchedProtocolSha256": digest(dependency / "EMASCurl/EMASCurlProtocol.m")}), flush=True)
    cert, key = OUT / "fixture.pem", OUT / "fixture.key"
    ca, ca_key = OUT / "ca.pem", OUT / "ca.key"
    config = OUT / "openssl.cnf"
    config.write_text("[req]\nprompt=no\ndistinguished_name=dn\nx509_extensions=root\n[dn]\nCN=SNI Replay Test CA\n[root]\nbasicConstraints=critical,CA:TRUE\nkeyUsage=critical,keyCertSign,cRLSign\n[leaf]\nsubjectAltName=DNS:sni-replay.local\nbasicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature,keyEncipherment\nextendedKeyUsage=serverAuth\n")
    run("openssl", "req", "-x509", "-newkey", "rsa:2048", "-nodes", "-sha256", "-days", "1",
        "-keyout", ca_key, "-out", ca, "-config", config, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    csr = OUT / "leaf.csr"
    run("openssl", "req", "-new", "-newkey", "rsa:2048", "-nodes", "-sha256", "-subj", "/CN=sni-replay.local",
        "-keyout", key, "-out", csr, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    run("openssl", "x509", "-req", "-in", csr, "-CA", ca, "-CAkey", ca_key, "-CAcreateserial",
        "-days", "1", "-sha256", "-extfile", config, "-extensions", "leaf", "-out", cert,
        stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    chain = OUT / "chain.pem"
    chain.write_bytes(cert.read_bytes() + ca.read_bytes())
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(chain, key)
    with socket.socket() as sock:
        sock.bind(("127.0.0.1", 0))
        port = sock.getsockname()[1]
    binary, hashes = build(dependency, port, sdk_root, arch)
    device_set = OUT / "devices"
    device_set.mkdir(exist_ok=True)
    simctl = ["xcrun", "simctl", "--set", str(device_set)]
    device_record = OUT / "device.json"
    device = json.loads(device_record.read_text()) if device_record.exists() else None
    if device and device["runtime"] == args.runtime and device["type"] == args.device_type:
        udid = device["udid"]
    else:
        udid = subprocess.check_output(simctl + ["create", "SNI replay native tests", args.device_type, args.runtime], text=True).strip()
        device_record.write_text(json.dumps({"udid": udid, "runtime": args.runtime, "type": args.device_type}))
    signal.signal(signal.SIGTERM, terminate)
    results = []
    file = OUT / "upload.txt"
    file.write_bytes(b"demo")
    cases = [("warm-post", "POST", "demo", True, "drop"),
             ("closed-listener", "POST", "demo", True, "close-listener"),
             ("fresh-post", "POST", "demo", False, "drop"),
             ("empty-post", "POST", "empty", True, "drop"),
             ("file-post", "POST", "file", True, "drop"),
             ("post-503", "POST", "demo", True, "503"),
             ("post-408", "POST", "demo", True, "408"),
             ("get-503", "GET", "none", True, "503"),
             ("get-408", "GET", "none", True, "408"),
             ("warm-get", "GET", "none", True, "drop"),
             ("warm-put", "PUT", "demo", True, "drop"),
             ("warm-delete", "DELETE", "none", True, "drop"),
             ("warm-head", "HEAD", "none", True, "drop"),
             ("warm-options", "OPTIONS", "none", True, "drop"),
             ("warm-patch", "PATCH", "demo", True, "drop")]
    try:
        run(*simctl, "boot", udid)
        run(*simctl, "bootstatus", udid, "-b", timeout=180)
        for name, method, body, warm, mode in cases:
            server = FixtureServer(port, context, mode)
            thread = threading.Thread(target=server.serve_forever, daemon=True)
            thread.start()
            try:
                completed = subprocess.run(simctl + ["spawn", udid, str(binary), str(ca), method, body,
                    "warm" if warm else "fresh", str(file)], capture_output=True, text=True, timeout=25)
                (OUT / f"{name}.log").write_text(completed.stdout + completed.stderr)
                lines = [line.removeprefix("REPLAY_RESULT ") for line in completed.stdout.splitlines() if line.startswith("REPLAY_RESULT ")]
                result = json.loads(lines[-1]) if lines else {"fixtureError": "No native result"}
                expected = b"" if body in ("empty", "none") else b"demo"
                valid = completed.returncode == 0 and not result.get("fixtureError") and bool(server.complete)
                valid = valid and all(r["bodySha256"] == hashlib.sha256(expected).hexdigest() for r in server.complete)
                valid = valid and all(r["method"] == method and r["host"] == "sni-replay.local" for r in server.complete)
                reused = bool(server.complete) and server.complete[0]["connection"] == server.warm_connection
                valid = valid and (not warm or (result.get("warmStatus") == 200 and reused))
                valid = valid and len(server.complete) == 1
                valid = valid and len([event for event in server.events if event["event"] == "headers"]) == 1
                if mode.isdigit():
                    valid = valid and result.get("status") == int(mode)
                    valid = valid and result.get("data") == "response"
                    valid = valid and result.get("headers", {}).get("retry-after") == "0"
                else:
                    valid = valid and "requestError" in result
                    valid = valid and result.get("requestErrorCode") != "SNI_CANCELLED"
                record = {"case": name, "pass": bool(valid), "native": result, "complete": server.complete,
                          "headers": server.events, "acceptedConnections": server.connections, "reusedWarmSocket": reused}
                results.append(record)
                print(json.dumps(record), flush=True)
            except Exception as error:
                results.append({"case": name, "pass": False, "fixtureError": str(error)})
                print(json.dumps(results[-1]), flush=True)
            finally:
                server.shutdown()
                server.server_close()
                thread.join(timeout=5)
    finally:
        subprocess.run(simctl + ["shutdown", udid], check=False)
        assert hashes == {path: digest(Path(path)) for path in hashes}, "Production source changed"
        assert digest(original_protocol) == original_hash, "Original Pods source changed"
        (OUT / "results.json").write_text(json.dumps(results, indent=2))
        passed = len(results) == len(cases) == 15 and all(result["pass"] for result in results)
        summary = {"passed": passed, "totalCases": len(results), "passedCases": sum(result["pass"] for result in results),
                   "failedCases": sum(not result["pass"] for result in results), "skippedCases": 0,
                   "runtime": args.runtime, "deviceType": args.device_type, "architecture": arch,
                   "sdkRoot": str(sdk_root), "sourceSha256": hashes, "protocolSha256": digest(original_protocol),
                   "durationSeconds": round(time.monotonic() - started, 3)}
        (OUT / "summary.json").write_text(json.dumps(summary, indent=2))
        print(json.dumps(summary), flush=True)
    raise SystemExit(0 if passed else 1)


if __name__ == "__main__":
    main()
