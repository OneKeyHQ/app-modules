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
        sock.settimeout(20)
        tls = self.context.wrap_socket(sock, server_side=True)
        tls.settimeout(20)
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


def require_hosted_ci():
    assert os.environ.get("GITHUB_ACTIONS") == "true" and os.environ.get("RUNNER_ENVIRONMENT") == "github-hosted", "Preinstalled simulator reuse is restricted to GitHub-hosted CI"


def simulator_inventory():
    return json.loads(subprocess.check_output(["xcrun", "simctl", "list", "--json"], text=True))


def select_simulator(runtime, device_type):
    inventory = simulator_inventory()
    runtimes = [item for item in inventory["runtimes"] if item.get("isAvailable") and ".iOS-" in item["identifier"] and int(item["version"].split(".")[0]) >= 15]
    runtimes.sort(key=lambda item: tuple(int(part) for part in item["version"].split(".")), reverse=True)
    eligible = []
    for candidate in runtimes:
        if runtime and candidate["identifier"] != runtime:
            continue
        devices = sorted(inventory["devices"].get(candidate["identifier"], []), key=lambda item: item["state"] != "Booted")
        for device in devices:
            identifier = device.get("deviceTypeIdentifier")
            if device.get("isAvailable") and device["name"].startswith("iPhone") and identifier and device["state"] in ("Booted", "Shutdown") and (not device_type or identifier == device_type):
                eligible.append((candidate["identifier"], identifier, device))
    if eligible:
        return min(eligible, key=lambda item: item[2]["state"] != "Booted")
    raise RuntimeError("No available iOS runtime with an iPhone device; replay tests cannot be skipped")


def prepare_ci_device(output):
    require_hosted_ci()
    runtime, device_type, device = select_simulator(None, None)
    output.mkdir(parents=True, exist_ok=True)
    record = output / "device.json"
    assert not record.exists(), "CI simulator preparation requires a fresh output directory"
    state = {"udid": device["udid"], "runtime": runtime, "type": device_type,
             "deviceSet": None, "bootOwned": device["state"] == "Shutdown", "initialState": device["state"],
             "ciPrepared": True, "preparedAt": time.time()}
    record.write_text(json.dumps(state))
    if state["bootOwned"]:
        # simctl boot starts the boot; readiness is checked after the other host suites.
        with (output / "preboot.log").open("w") as log:
            run("xcrun", "simctl", "boot", device["udid"], stdout=log, stderr=subprocess.STDOUT)
    print(json.dumps({"preparedDevice": state}), flush=True)


def device_simctl(device):
    return ["xcrun", "simctl"] + (["--set", device["deviceSet"]] if device["deviceSet"] else [])


def cleanup_device(record):
    if not record.exists():
        return
    device = json.loads(record.read_text())
    if device["deviceSet"] is None:
        require_hosted_ci()
    if device["bootOwned"]:
        subprocess.run(device_simctl(device) + ["shutdown", device["udid"]], check=False)


def validate_ci_device(device):
    require_hosted_ci()
    assert device["ciPrepared"] and device["deviceSet"] is None
    assert device["initialState"] in ("Booted", "Shutdown")
    assert device["bootOwned"] == (device["initialState"] == "Shutdown")
    inventory = simulator_inventory()
    assert any(item["identifier"] == device["runtime"] and item.get("isAvailable") and int(item["version"].split(".")[0]) >= 15 for item in inventory["runtimes"])
    assert any(item["udid"] == device["udid"] and item.get("isAvailable") and item["name"].startswith("iPhone") and item.get("deviceTypeIdentifier") == device["type"] for item in inventory["devices"].get(device["runtime"], []))


def terminate(_signum, _frame):
    raise SystemExit(143)


def native_request(simctl, udid, binary, ca, method, body, warm, file, name, server):
    command = simctl + ["spawn", udid, str(binary), str(ca), method, body, "warm" if warm else "fresh", str(file)]
    try:
        completed = subprocess.run(command, capture_output=True, text=True, timeout=60)
    except subprocess.TimeoutExpired as error:
        def partial(value):
            return value.decode("utf8", errors="replace") if isinstance(value, bytes) else (value or "")
        (OUT / f"{name}.log").write_text(partial(error.stdout) + partial(error.stderr))
        (OUT / f"{name}-timeout.json").write_text(json.dumps({"case": name, "timeoutSeconds": 60,
            "complete": server.complete, "headers": server.events, "acceptedConnections": server.connections}, indent=2))
        raise
    (OUT / f"{name}.log").write_text(completed.stdout + completed.stderr)
    lines = [line.removeprefix("REPLAY_RESULT ") for line in completed.stdout.splitlines() if line.startswith("REPLAY_RESULT ")]
    result = json.loads(lines[-1]) if len(lines) == 1 else {"fixtureError": "Expected one native result"}
    return completed, result


def main():
    global OUT
    started = time.monotonic()
    parser = argparse.ArgumentParser()
    parser.add_argument("--emascurl-root", type=Path)
    parser.add_argument("--sdk-root", type=Path, default=PACKAGE)
    parser.add_argument("--runtime")
    parser.add_argument("--device-type")
    parser.add_argument("--output-dir", type=Path, default=OUT)
    parser.add_argument("--prepared-dependency", action="store_true")
    modes = parser.add_mutually_exclusive_group()
    modes.add_argument("--prepare-ci-device", action="store_true")
    modes.add_argument("--ci-prepared-device", action="store_true")
    modes.add_argument("--cleanup-device", action="store_true")
    args = parser.parse_args()
    OUT = args.output_dir.resolve()
    if args.prepare_ci_device:
        prepare_ci_device(OUT)
        return
    if args.cleanup_device:
        cleanup_device(OUT / "device.json")
        return
    device_record = OUT / "device.json"
    device = None
    preboot_elapsed = 0
    if args.ci_prepared_device:
        assert device_record.is_file(), "CI device must be prepared before host suites"
        device = json.loads(device_record.read_text())
        validate_ci_device(device)
        preboot_elapsed = round(time.time() - device["preparedAt"], 3)
        args.runtime, args.device_type = device["runtime"], device["type"]
    else:
        args.runtime, args.device_type, _source = select_simulator(args.runtime, args.device_type)
    assert args.emascurl_root, "--emascurl-root is required for replay tests"
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
    if not args.ci_prepared_device:
        device_set = OUT / "devices"
        device_set.mkdir(exist_ok=True)
        previous = json.loads(device_record.read_text()) if device_record.exists() else None
        if previous and previous["runtime"] == args.runtime and previous["type"] == args.device_type:
            udid = previous["udid"]
        else:
            udid = subprocess.check_output(["xcrun", "simctl", "--set", str(device_set), "create", "SNI replay native tests", args.device_type, args.runtime], text=True).strip()
        device = {"udid": udid, "runtime": args.runtime, "type": args.device_type,
                  "deviceSet": str(device_set), "bootOwned": True, "initialState": "Shutdown"}
        device_record.write_text(json.dumps(device))
    udid = device["udid"]
    simctl = device_simctl(device)
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
    boot_started = time.monotonic()
    boot_seconds = None
    preflight_seconds = None
    preflight_passed = False
    cases_started = None
    try:
        if not args.ci_prepared_device:
            run(*simctl, "boot", udid)
        run(*simctl, "bootstatus", udid, "-b", timeout=180)
        boot_seconds = round(time.monotonic() - boot_started, 3)
        preflight_started = time.monotonic()
        server = FixtureServer(port, context, "preflight")
        thread = threading.Thread(target=server.serve_forever, daemon=True)
        thread.start()
        try:
            completed, result = native_request(simctl, udid, binary, ca, "GET", "warmup-only", True, file, "preflight", server)
            preflight_passed = completed.returncode == 0 and result.get("preflight") is True and result.get("warmStatus") == 200 and server.warm_connection is not None and not server.complete and not server.events
            (OUT / "preflight.json").write_text(json.dumps({"passed": preflight_passed, "native": result,
                "complete": server.complete, "headers": server.events}, indent=2))
            assert preflight_passed, "Native warmup-only preflight failed"
        finally:
            preflight_seconds = round(time.monotonic() - preflight_started, 3)
            server.shutdown()
            server.server_close()
            thread.join(timeout=5)
        cases_started = time.monotonic()
        for name, method, body, warm, mode in cases:
            server = FixtureServer(port, context, mode)
            thread = threading.Thread(target=server.serve_forever, daemon=True)
            thread.start()
            try:
                completed, result = native_request(simctl, udid, binary, ca, method, body, warm, file, name, server)
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
            except subprocess.TimeoutExpired as error:
                results.append({"case": name, "pass": False, "fixtureError": str(error),
                    "complete": server.complete, "headers": server.events, "acceptedConnections": server.connections})
                print(json.dumps(results[-1]), flush=True)
                break
            except Exception as error:
                results.append({"case": name, "pass": False, "fixtureError": str(error),
                    "complete": server.complete, "headers": server.events, "acceptedConnections": server.connections})
                print(json.dumps(results[-1]), flush=True)
            finally:
                server.shutdown()
                server.server_close()
                thread.join(timeout=5)
    finally:
        cleanup_device(device_record)
        assert hashes == {path: digest(Path(path)) for path in hashes}, "Production source changed"
        assert digest(original_protocol) == original_hash, "Original Pods source changed"
        (OUT / "results.json").write_text(json.dumps(results, indent=2))
        passed = preflight_passed and len(results) == len(cases) == 15 and all(result["pass"] for result in results)
        summary = {"passed": passed, "totalCases": len(results), "passedCases": sum(result["pass"] for result in results),
                   "failedCases": sum(not result["pass"] for result in results), "skippedCases": 0, "unexecutedCases": len(cases) - len(results),
                   "runtime": args.runtime, "deviceType": args.device_type, "architecture": arch,
                   "deviceUDID": udid, "deviceSet": device["deviceSet"], "initialState": device["initialState"], "bootOwned": device["bootOwned"],
                   "prebootElapsedSeconds": preboot_elapsed,
                   "bootSeconds": boot_seconds if boot_seconds is not None else round(time.monotonic() - boot_started, 3),
                   "preflightPassed": preflight_passed, "preflightSeconds": preflight_seconds,
                   "casesSeconds": round(time.monotonic() - cases_started, 3) if cases_started else 0,
                   "sdkRoot": str(sdk_root), "sourceSha256": hashes, "protocolSha256": digest(original_protocol),
                   "durationSeconds": round(time.monotonic() - started, 3)}
        (OUT / "summary.json").write_text(json.dumps(summary, indent=2))
        print(json.dumps(summary), flush=True)
    raise SystemExit(0 if passed else 1)


if __name__ == "__main__":
    main()
