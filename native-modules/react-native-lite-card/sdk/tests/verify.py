"""Compare a recovered SDK with a vendor reference and an independent crypto oracle.

Uses only synthetic test identities. Never prints keys, APDUs or returned payloads.
Install cryptography in an isolated test environment, not in the application.
"""
import argparse
import ctypes as c
import hashlib
import json
import random
from pathlib import Path

from cryptography.hazmat.primitives import cmac, hashes, serialization
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes


class Shared(c.Structure):
    _fields_ = [(name, c.c_char_p) for name in (
        "scpID", "keyUsage", "keyType", "keyLength", "hostID", "cardGroupID")]


class SDK:
    def __init__(self, path):
        self.lib = c.CDLL(str(Path(path).resolve()))
        declarations = {
            "JUB_FreeMemory": [c.c_void_p],
            "JUB_GPC_Finalize": [],
            "JUB_GPC_Initialize": [c.POINTER(Shared), c.c_char_p, c.c_char_p],
            "JUB_GPC_BuildMutualAuthData": [c.POINTER(c.c_void_p)],
            "JUB_GPC_OpenSecureChannel": [c.c_char_p],
            "JUB_GPC_TLVDecode": [c.c_char_p, c.POINTER(c.c_ulong), c.POINTER(c.c_void_p)],
            "JUB_GPC_ParseCertificate": [c.c_char_p, c.POINTER(c.c_void_p), c.POINTER(c.c_void_p)],
        }
        for name in ("BuildAPDU", "BuildSafeAPDU"):
            declarations["JUB_GPC_" + name] = [c.c_ulong] * 4 + [c.c_char_p, c.POINTER(c.c_void_p)]
        for name in ("ParseAPDUResponse", "ParseSafeAPDUResponse"):
            declarations["JUB_GPC_" + name] = [c.c_char_p, c.POINTER(c.c_ushort), c.POINTER(c.c_void_p)]
        for name, args in declarations.items():
            function = getattr(self.lib, name)
            function.argtypes, function.restype = args, c.c_ulong

    def take(self, pointer):
        if not pointer.value:
            return None
        value = c.string_at(pointer)
        result = self.lib.JUB_FreeMemory(pointer)
        # The vendor does not register empty output allocations.
        assert result == 0 or (value == b"" and result == 8), "Unexpected allocation ownership"
        return value

    def build(self, name, data, header=(0x80, 0x20, 0, 0)):
        return self.build_hex(name, data.hex(), header)

    def build_hex(self, name, text, header=(0x80, 0x20, 0, 0)):
        output = c.c_void_p()
        result = getattr(self.lib, "JUB_GPC_" + name)(*header, text.encode(), c.byref(output))
        return result, self.take(output)

    def parse(self, name, data):
        output, word = c.c_void_p(), c.c_ushort()
        result = getattr(self.lib, "JUB_GPC_" + name)(data.hex().encode(), c.byref(word), c.byref(output))
        return result, word.value, self.take(output)

    def tlv(self, data):
        output, tag = c.c_void_p(), c.c_ulong()
        result = self.lib.JUB_GPC_TLVDecode(data.hex().encode(), c.byref(tag), c.byref(output))
        return result, tag.value, self.take(output)

    def certificate(self, data):
        serial, subject = c.c_void_p(), c.c_void_p()
        result = self.lib.JUB_GPC_ParseCertificate(data.hex().encode(), c.byref(serial), c.byref(subject))
        return result, self.take(serial), self.take(subject)


def tlv(tag, data):
    encoded_tag = tag.to_bytes((tag.bit_length() + 7) // 8, "big")
    if tag in (0x7F21, 0xBF21):
        length = b"\x81" + bytes([len(data)]) if len(data) < 256 else b"\x82" + len(data).to_bytes(2, "big")
    else:
        length = bytes([len(data)])
    return encoded_tag + length + data


def public(key):
    return key.public_key().public_bytes(serialization.Encoding.X962, serialization.PublicFormat.UncompressedPoint)


def certificate(key):
    body = b"".join((
        tlv(0x93, b"RECOVERY-TEST-1"), tlv(0x42, b"TEST-CA"),
        tlv(0x5F20, b"onekey-sdk-test"), tlv(0x95, b"\x00\x80"),
        tlv(0x5F25, bytes.fromhex("20260101")), tlv(0x5F24, bytes.fromhex("20300101")),
        tlv(0x53, b""), tlv(0x7F49, tlv(0xB0, public(key)) + tlv(0xF0, b"\x00")),
    ))
    signature = key.sign(body, ec.ECDSA(hashes.SHA256()))
    return tlv(0x7F21, body + tlv(0x5F37, signature))


def mac(key, data):
    instance = cmac.CMAC(algorithms.AES(key))
    instance.update(data)
    return instance.finalize()


def aes(key, iv, data):
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv)).encryptor()
    return cipher.update(data) + cipher.finalize()


def pad(data):
    return data + b"\x80" + b"\x00" * (15 - len(data) % 16)


def session(sdk, cert, oce, card, corrupt=False):
    host, group = b"\x80" * 8, b"onekey-sdk-test"
    info = Shared(b"1107", b"3c", b"88", b"10", host.hex().encode(), group.hex().encode())
    assert sdk.lib.JUB_GPC_Finalize() == 0
    assert sdk.lib.JUB_GPC_Initialize(c.byref(info), cert.hex().encode(), f"{oce.private_numbers().private_value:064x}".encode()) == 0, "Initialization"
    output = c.c_void_p()
    assert sdk.lib.JUB_GPC_BuildMutualAuthData(c.byref(output)) == 0
    mutual = bytes.fromhex(sdk.take(output).decode())
    shared = tlv(0xA6, tlv(0x90, b"\x11\x07") + tlv(0x95, b"\x3c") + tlv(0x80, b"\x88") + tlv(0x81, b"\x10") + tlv(0x84, host))
    assert mutual.startswith(shared) and len(mutual) == len(shared) + 68, "Mutual authenticate structure"
    assert getattr(sdk, "last_ephemeral", None) != mutual[-65:], "Fresh ephemeral key on reinitialize"
    sdk.last_ephemeral = mutual[-65:]
    ephemeral = ec.EllipticCurvePublicKey.from_encoded_point(ec.SECP256R1(), mutual[-65:])
    # The legacy protocol hashes each ECDH X coordinate with SHA-1 before KDF2/SHA-256.
    z = hashlib.sha1(card.exchange(ec.ECDH(), ephemeral)).digest() + hashlib.sha1(card.exchange(ec.ECDH(), oce.public_key())).digest()
    shared_kdf = b"\x3c\x88\x10" + bytes([len(host)]) + host + group
    material = b"".join(hashlib.sha256(z + index.to_bytes(4, "big") + shared_kdf).digest() for index in range(1, 4))[:80]
    dek, enc, cmac_key, rmac_key, _ = [material[i:i+16] for i in range(0, 80, 16)]
    card_tlv = tlv(0x5F49, public(card))
    receipt = mac(dek, mutual + card_tlv)
    sent_receipt = bytes([receipt[0] ^ 1]) + receipt[1:] if corrupt else receipt
    result = sdk.lib.JUB_GPC_OpenSecureChannel((card_tlv + tlv(0x86, sent_receipt)).hex().encode())
    assert result == (1 if corrupt else 0), "Receipt verification"
    return enc, cmac_key, rmac_key, receipt


def verify(recovered, reference=None):
    rng = random.Random(20261103)
    checks = 0
    peers = [recovered] + ([reference] if reference else [])
    for size in (0, 1, 6, 7, 15, 16, 17, 127, 128, 254, 255, 256, 257, 1024, 65535):
        data = rng.randbytes(size)
        header = tuple(rng.randrange(256) for _ in range(4))
        length = b"" if not data else (bytes([size]) if size <= 255 else b"\x00" + size.to_bytes(2, "big"))
        expected = (bytes(header) + length + data).hex().encode()
        for sdk in peers:
            assert sdk.build("BuildAPDU", data, header) == (0, expected), "Plain APDU bytes"
            checks += 1
    for text in ("1", "123", "AbC", "12345678901234567890123456789012345678900000001",
                 "0" * 131069, "0" * 131070):
        data = bytes.fromhex(("0" if len(text) % 2 else "") + text)
        size = len(data)
        length = bytes([size]) if size <= 255 else b"\x00" + size.to_bytes(2, "big")
        expected = (bytes.fromhex("803b0000") + length + data).hex().encode()
        for sdk in peers:
            assert sdk.build_hex("BuildAPDU", text, (0x80, 0x3b, 0, 0)) == (0, expected), "Raw odd command compatibility"
            checks += 1
    for size in (0, 1, 5, 6, 7, 16, 255, 1024, 1025):
        for word in (b"\x90\x00", b"\x63\xc2", b"\x6a\x80"):
            data = rng.randbytes(size)
            expected = (3, 0, None) if 0 < size < 6 else ((10, 0, None) if size > 1024 else (0, int.from_bytes(word, "big"), data.hex().encode()))
            for sdk in peers:
                platform_expected = expected
                if 0 < size < 6 and getattr(sdk, "accept_short_plain_response", False):
                    platform_expected = (0, int.from_bytes(word, "big"), data.hex().encode())
                assert sdk.parse("ParseAPDUResponse", data + word) == platform_expected, "Plain response and bounds"
                checks += 1
    oce = ec.derive_private_key(1, ec.SECP256R1())
    card = ec.derive_private_key(2, ec.SECP256R1())
    cert = certificate(oce)
    for sdk in peers:
        assert sdk.certificate(cert) == (0, b"RECOVERY-TEST-1", b"onekey-sdk-test".hex().encode()), "Certificate fields"
        assert sdk.tlv(tlv(0xBF21, cert)) == (0, 0xBF21, cert.hex().encode()), "Certificate envelope"
        checks += 2
        for tag in (0x42, 0x53, 0x73, 0x86, 0x93, 0x95, 0xB0, 0xF0, 0x5F20, 0x5F24, 0x5F25, 0x5F37, 0x5F49, 0x7F49):
            data = rng.randbytes(16)
            assert sdk.tlv(tlv(tag, data)) == (0, tag, data.hex().encode()), "TLV fields"
            checks += 1
        session(sdk, cert, oce, card, corrupt=True)
        assert sdk.build("BuildSafeAPDU", b"test")[0] == 6, "Rejected receipt did not open a channel"
        checks += 2
        enc, cmac_key, rmac_key, chain = session(sdk, cert, oce, card)
        counter = 1
        if sdk is recovered:
            assert sdk.parse("ParseSafeAPDUResponse", bytes(8) + bytes.fromhex("63c2"))[0] == 3, "Reject missing encrypted response block"
            checks += 1
        command_inputs = [rng.randbytes(size).hex() for size in
                          (0, 1, 15, 16, 17, 127, 128, 239, 240, 255, 256, 1024)]
        command_inputs += ["12345678901234567890123456789012345678900000001", "AbC", "1", "12"]
        command_inputs += [rng.randbytes(1).hex() for _ in range(250)]
        for text in command_inputs:
            data = bytes.fromhex(("0" if len(text) % 2 else "") + text)
            size = len(data)
            counter_bytes = counter.to_bytes(4, "big") if getattr(sdk, "wide_counter", False) else bytes([counter & 255])
            iv = aes(enc, bytes(16), counter_bytes.rjust(16, b"\0"))
            encrypted = aes(enc, iv, pad(data))
            length = len(encrypted) + 8
            header = bytes([0x84, 0x20, 0, 0])
            chain = mac(cmac_key, chain + header + bytes([length & 255]) + encrypted)
            length_bytes = bytes([length]) if length <= 255 else b"\x00" + length.to_bytes(2, "big")
            expected = (header + length_bytes + encrypted + chain[:8]).hex().encode()
            assert sdk.build_hex("BuildSafeAPDU", text) == (0, expected), "Raw command encrypted APDU and chained CMAC"
            checks += 1
            reply = rng.randbytes(min(size, 1008))
            counter += 1
            counter_bytes = counter.to_bytes(4, "big") if getattr(sdk, "wide_counter", False) else bytes([counter & 255])
            reply_iv = aes(enc, bytes(16), b"\x80" + counter_bytes.rjust(15, b"\0"))
            ciphertext = aes(enc, reply_iv, pad(reply))
            response_mac = mac(rmac_key, chain + ciphertext + b"\x90\x00")[:8]
            response = ciphertext + response_mac + b"\x90\x00"
            tampered = bytearray(response)
            tampered[-3] ^= 1
            assert sdk.parse("ParseSafeAPDUResponse", tampered)[0] == 1, "Reject modified response MAC"
            parsed_reply = sdk.parse("ParseSafeAPDUResponse", response)
            assert parsed_reply == (0, 0x9000, reply.hex().encode()), ("Response authentication/decryption", peers.index(sdk), size, counter, parsed_reply[0], parsed_reply[1], None if parsed_reply[2] is None else len(parsed_reply[2]))
            checks += 2
        # Verify status-only errors advance the counter but release no plaintext.
        for after_error in (False, True):
            data = b"synthetic-error-path"
            counter_bytes = counter.to_bytes(4, "big") if getattr(sdk, "wide_counter", False) else bytes([counter & 255])
            encrypted = aes(enc, aes(enc, bytes(16), counter_bytes.rjust(16, b"\0")), pad(data))
            length = len(encrypted) + 8
            header = bytes([0x84, 0x20, 0, 0])
            chain = mac(cmac_key, chain + header + bytes([length]) + encrypted)
            expected = (header + bytes([length]) + encrypted + chain[:8]).hex().encode()
            assert sdk.build("BuildSafeAPDU", data) == (0, expected), "Counter after status-only error"
            checks += 1
            if not after_error:
                code, word, error_data = sdk.parse("ParseSafeAPDUResponse", bytes.fromhex("63c2"))
                if getattr(sdk, "authenticated_errors", False):
                    assert (code, word, error_data) == (1, 0, None), "Reject unauthenticated Android error"
                    error_text = b"synthetic-card-error"
                    next_counter = (counter + 1).to_bytes(4, "big")
                    reply_iv = aes(enc, bytes(16), b"\x80" + next_counter.rjust(15, b"\0"))
                    ciphertext = aes(enc, reply_iv, pad(error_text))
                    error_sw = bytes.fromhex("63c2")
                    error_mac = mac(rmac_key, chain + ciphertext + error_sw)[:8]
                    assert sdk.parse("ParseSafeAPDUResponse", ciphertext + error_mac + error_sw) == (0, 0x63C2, error_text.hex().encode()), "Authenticated Android error payload"
                    checks += 1
                else:
                    assert (code, word) == (0, 0x63C2), "Preserve non-success card status"
                    if sdk is recovered:
                        assert error_data == b"", "No scratch-buffer data on a card error"
                    else:
                        assert error_data in (b"", b"0" * 2048), "Vendor status-only output"
                counter += 1
                checks += 2
        assert sdk.lib.JUB_GPC_Finalize() == 0
        checks += 1
    # Additional failure paths are tested only on recovery, since the old ABI aborts.
    for malformed in (b"", b"0", b"zz", b"7f21", b"7f2182", b"930fff"):
        output = c.c_void_p()
        tag = c.c_ulong()
        assert recovered.lib.JUB_GPC_TLVDecode(malformed, c.byref(tag), c.byref(output)) != 0
        assert not output.value
        assert recovered.certificate(bytes.fromhex(malformed.decode()) if malformed and len(malformed) % 2 == 0 and malformed != b"zz" else b"")[0] != 0
        checks += 2
    output = c.c_void_p()
    assert recovered.lib.JUB_GPC_BuildMutualAuthData(c.byref(output)) == 6
    assert recovered.build("BuildSafeAPDU", b"test")[0] == 6
    if hasattr(recovered.lib, "JUB_FreeMemory"):
        assert recovered.lib.JUB_FreeMemory(None) == 5
        checks += 1
    checks += 2
    return {"checks_passed": checks, "reference_compared": reference is not None,
            "crypto_oracle": "Python cryptography: P-256 ECDH, SHA-1, KDF2/SHA-256, AES-CMAC, AES-CBC",
            "physical_card_verified": False}


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--library", required=True)
    parser.add_argument("--reference")
    parser.add_argument("--report")
    args = parser.parse_args()
    result = verify(SDK(args.library), SDK(args.reference) if args.reference else None)
    text = json.dumps(result, indent=2) + "\n"
    if args.report:
        Path(args.report).write_text(text)
    print(text, end="")
