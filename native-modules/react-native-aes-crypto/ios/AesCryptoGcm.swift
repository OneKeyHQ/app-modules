import CryptoKit
import Foundation

@objc(AesCryptoGcm)
public final class AesCryptoGcm: NSObject {
  // AES-GCM nonce length: NIST SP 800-38D recommends 96 bits (12 bytes), and
  // `CryptoKit.AES.GCM.Nonce(data:)` is the canonical 12-byte path. Locking
  // the bridge layer to 12 bytes keeps Android (where GCMParameterSpec would
  // otherwise accept other lengths) and iOS byte-compatible.
  private static let gcmNonceLengthBytes = 12

  @objc(encryptWithDataHex:keyHex:nonceHex:aadHex:error:)
  public static func encrypt(
    dataHex: String,
    keyHex: String,
    nonceHex: String,
    aadHex: String,
    error: NSErrorPointer
  ) -> String? {
    do {
      try requireNonEmpty(dataHex, method: "aesGcmEncrypt", paramName: "data")
      try requireNonEmpty(keyHex, method: "aesGcmEncrypt", paramName: "key")
      try requireNonEmpty(nonceHex, method: "aesGcmEncrypt", paramName: "nonce")
      try requireNonEmpty(aadHex, method: "aesGcmEncrypt", paramName: "aad")
      let plaintext = try dataFromHex(dataHex)
      let key = SymmetricKey(data: try dataFromHex(keyHex))
      let nonce = try gcmNonce(fromHex: nonceHex, method: "aesGcmEncrypt")
      let aad = try dataFromHex(aadHex)
      let sealedBox = try AES.GCM.seal(plaintext, using: key, nonce: nonce, authenticating: aad)
      var ciphertextWithTag = Data(sealedBox.ciphertext)
      ciphertextWithTag.append(sealedBox.tag)
      return hexFromData(ciphertextWithTag)
    } catch let caughtError as NSError {
      error?.pointee = caughtError
      return nil
    }
  }

  @objc(decryptWithCiphertextWithTagHex:keyHex:nonceHex:aadHex:error:)
  public static func decrypt(
    ciphertextWithTagHex: String,
    keyHex: String,
    nonceHex: String,
    aadHex: String,
    error: NSErrorPointer
  ) -> String? {
    do {
      try requireNonEmpty(ciphertextWithTagHex, method: "aesGcmDecrypt", paramName: "ciphertextWithTag")
      try requireNonEmpty(keyHex, method: "aesGcmDecrypt", paramName: "key")
      try requireNonEmpty(nonceHex, method: "aesGcmDecrypt", paramName: "nonce")
      try requireNonEmpty(aadHex, method: "aesGcmDecrypt", paramName: "aad")
      let encrypted = try dataFromHex(ciphertextWithTagHex)
      if encrypted.count < 16 {
        throw AesCryptoGcmError.invalidCiphertext
      }
      let ciphertext = encrypted.prefix(encrypted.count - 16)
      let tag = encrypted.suffix(16)
      let key = SymmetricKey(data: try dataFromHex(keyHex))
      let nonce = try gcmNonce(fromHex: nonceHex, method: "aesGcmDecrypt")
      let aad = try dataFromHex(aadHex)
      let sealedBox = try AES.GCM.SealedBox(nonce: nonce, ciphertext: ciphertext, tag: tag)
      let plaintext = try AES.GCM.open(sealedBox, using: key, authenticating: aad)
      return hexFromData(plaintext)
    } catch let caughtError as NSError {
      error?.pointee = caughtError
      return nil
    }
  }

  // The GCM entry points enforce a strict non-empty contract for every
  // hex argument (data / ciphertextWithTag, key, nonce, aad). The legacy
  // CBC/CTR paths in AesCrypto.mm have their own ONEKEY_AES_REQUIRE_NON_EMPTY
  // guards at the ObjC++ entry, so dataFromHex never sees an empty string
  // from production callers — the `if hex.isEmpty` branch below is kept
  // only as defence-in-depth.
  private static func requireNonEmpty(_ value: String, method: String, paramName: String) throws {
    if value.isEmpty {
      throw NSError(
        domain: "AesCryptoGcmError",
        code: 1,
        userInfo: [NSLocalizedDescriptionKey: "\(method): \(paramName) must not be empty"]
      )
    }
  }

  private static func gcmNonce(fromHex hex: String, method: String) throws -> AES.GCM.Nonce {
    let bytes = try dataFromHex(hex)
    if bytes.count != gcmNonceLengthBytes {
      throw NSError(
        domain: "AesCryptoGcmError",
        code: 2,
        userInfo: [NSLocalizedDescriptionKey:
          "\(method): nonce must be exactly \(gcmNonceLengthBytes) bytes (got \(bytes.count))"]
      )
    }
    return try AES.GCM.Nonce(data: bytes)
  }

  private static func dataFromHex(_ hex: String) throws -> Data {
    if hex.isEmpty {
      return Data()
    }
    if hex.count % 2 != 0 {
      throw AesCryptoGcmError.invalidHex
    }

    var data = Data(capacity: hex.count / 2)
    var index = hex.startIndex
    while index < hex.endIndex {
      let nextIndex = hex.index(index, offsetBy: 2)
      guard let byte = UInt8(hex[index..<nextIndex], radix: 16) else {
        throw AesCryptoGcmError.invalidHex
      }
      data.append(byte)
      index = nextIndex
    }
    return data
  }

  private static func hexFromData(_ data: Data) -> String {
    data.map { String(format: "%02x", $0) }.joined()
  }
}

private enum AesCryptoGcmError: Error {
  case invalidHex
  case invalidCiphertext
}
