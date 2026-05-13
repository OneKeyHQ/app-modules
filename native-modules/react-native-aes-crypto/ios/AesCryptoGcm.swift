import CryptoKit
import Foundation

@objc(AesCryptoGcm)
public final class AesCryptoGcm: NSObject {
  @objc(encryptWithDataHex:keyHex:nonceHex:aadHex:error:)
  public static func encrypt(
    dataHex: String,
    keyHex: String,
    nonceHex: String,
    aadHex: String,
    error: NSErrorPointer
  ) -> String? {
    do {
      let plaintext = try dataFromHex(dataHex)
      let key = SymmetricKey(data: try dataFromHex(keyHex))
      let nonce = try AES.GCM.Nonce(data: dataFromHex(nonceHex))
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
      let encrypted = try dataFromHex(ciphertextWithTagHex)
      if encrypted.count < 16 {
        throw AesCryptoGcmError.invalidCiphertext
      }
      let ciphertext = encrypted.prefix(encrypted.count - 16)
      let tag = encrypted.suffix(16)
      let key = SymmetricKey(data: try dataFromHex(keyHex))
      let nonce = try AES.GCM.Nonce(data: dataFromHex(nonceHex))
      let aad = try dataFromHex(aadHex)
      let sealedBox = try AES.GCM.SealedBox(nonce: nonce, ciphertext: ciphertext, tag: tag)
      let plaintext = try AES.GCM.open(sealedBox, using: key, authenticating: aad)
      return hexFromData(plaintext)
    } catch let caughtError as NSError {
      error?.pointee = caughtError
      return nil
    }
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
