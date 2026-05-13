import NativeAesCrypto from './NativeAesCrypto';

export const encrypt = NativeAesCrypto.encrypt.bind(NativeAesCrypto);
export const decrypt = NativeAesCrypto.decrypt.bind(NativeAesCrypto);

/**
 * AES-256-GCM authenticated encryption.
 *
 * Contract (intentionally tighter than RFC 5116 / NIST SP 800-38D):
 * - `data`, `key`, `nonce`, `aad` are lowercase hex strings.
 * - `key` must be non-empty hex and decode to a valid AES key length
 *   (16 / 24 / 32 bytes).
 * - `nonce` must be **exactly** 12 bytes (24 hex chars). The standard
 *   permits other lengths, but `CryptoKit.AES.GCM.Nonce` on iOS is the
 *   12-byte canonical path; Android is locked to the same length to keep
 *   ciphertexts byte-compatible across platforms.
 * - `aad` must be non-empty. AEAD allows 0-byte AAD, but every consumer
 *   of this module supplies an explicit context binding (envelope header
 *   bytes / keyless AAD constants); empty AAD almost always means a
 *   forgotten parameter and is rejected.
 * - `data` MAY be empty — that yields a 16-byte tag with no ciphertext.
 *
 * @returns hex string `ciphertext || tag` (tag is always the last 16 bytes).
 *
 * @throws Promise rejects with code `"-1"` for any contract violation
 *   (empty key / nonce / aad, wrong nonce length, malformed hex) or for
 *   the underlying CryptoKit / JCE error.
 */
export const aesGcmEncrypt =
  NativeAesCrypto.aesGcmEncrypt.bind(NativeAesCrypto);

/**
 * AES-256-GCM authenticated decryption with the same contract as
 * {@link aesGcmEncrypt}.
 *
 * - `ciphertextWithTag` is the hex form of `ciphertext || tag` produced
 *   by {@link aesGcmEncrypt}; its decoded length must be `>= 16` bytes
 *   (i.e. at least the auth tag).
 * - `key` / `nonce` / `aad` must match the encryption call **byte for
 *   byte**; any mismatch surfaces as a tag-verification rejection.
 *
 * @returns hex string of the recovered plaintext (may be empty).
 *
 * @throws Promise rejects with code `"-1"` for contract violations or
 *   GCM tag mismatch (tampered ciphertext / tag / AAD / wrong key).
 */
export const aesGcmDecrypt =
  NativeAesCrypto.aesGcmDecrypt.bind(NativeAesCrypto);
export const pbkdf2 = NativeAesCrypto.pbkdf2.bind(NativeAesCrypto);
export const hmac256 = NativeAesCrypto.hmac256.bind(NativeAesCrypto);
export const hmac512 = NativeAesCrypto.hmac512.bind(NativeAesCrypto);
export const sha1 = NativeAesCrypto.sha1.bind(NativeAesCrypto);
export const sha256 = NativeAesCrypto.sha256.bind(NativeAesCrypto);
export const sha512 = NativeAesCrypto.sha512.bind(NativeAesCrypto);
export const randomUuid = NativeAesCrypto.randomUuid.bind(NativeAesCrypto);
export const randomKey = NativeAesCrypto.randomKey.bind(NativeAesCrypto);

export default NativeAesCrypto;
export type { Spec as AesCryptoSpec } from './NativeAesCrypto';
