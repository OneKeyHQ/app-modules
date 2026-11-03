# Recovery provenance

Recovery date: 2026-11-03. This is a source reconstruction, not a claim that the
original private GPChannelSDK_C repository was found intact.

## Original reference

The consumer installed `@onekeyfe/react-native-lite-card@6.7.0-alpha.402`.

| Artifact | SHA-256 |
| --- | --- |
| iOS arm64 `GPChannelSDKCore` | `b54310832a1b594e9ae9eec716f98fb974ee4eb6a459ba8711d79fc94c77782c` |
| Android arm64 `libgpchannelNDK.so` | `f5481ae4cd95908addbcb3ea7267052122ad32f832c2c9a06adfce4f5653a135` |
| Android armv7 `libgpchannelNDK.so` | `0973637e71d4a5b9f228eb754bca8adee765b508b11c9a69b68f1f15e770bd25` |

The published iOS debug-symbol UUID **6CE8D6E1-5AAC-3B76-AAEB-2005E6ED2A75**
exactly matches the consumer's iOS arm64 binary. The historical published
framework's `__text` section is also identical: 193016 bytes, SHA-256
`cf23b593509191c546ece75faa7b026a6633adcc178e909009fa397956b8e907`. The dSYM recovers function names,
parameter and member types, source paths and line information; it does not
contain the original source text. Ghidra 12.1.2 exported 100 relevant functions
for comparison. The original binaries remain only in local verification evidence;
the package builds the recovered source instead.

## Pinned source references

- [ziancube/GPChannel-test at 158975a8972c0e5d5c2e222d0b06607c3ce1090a](https://github.com/ziancube/GPChannel-test/tree/158975a8972c0e5d5c2e222d0b06607c3ce1090a):
  original usage documentation, C/C++ public and context/APDU headers, JNI
  contract, JsonCpp source, debug symbols and historical binary reference.
- [JubiterWallet/JubiterSDK_C at a080593b4896b634ec9f32c53d72ffa793f8d554](https://github.com/JubiterWallet/JubiterSDK_C/tree/a080593b4896b634ec9f32c53d72ffa793f8d554):
  SCP11c/SCP03/TLV implementation, `src/device/ApduBuilder.cpp`, and the vendored
  TrezorCrypto implementation already used by this library family.

## File origins

| Recovered path | Origin and edits |
| --- | --- |
| `include/GPChannelSDK.h` | Exact public header from GPChannel-test; matches installed iOS header. |
| `include/context/BaseContext.h`, `include/device/ApduBuilder.hpp`, `include/utility/Apdu.hpp`, `include/utility/util.h` | GPChannel-test headers. Builder pointers initialized; response bounds guarded; observed Android/iOS short-response difference retained. |
| `src/GPChannelSDK.cpp` | Human-readable ABI reconstruction from matching binary, symbols, headers and callers. Includes allocation ownership and process-global session. |
| `src/ApduBuilder.cpp` | JuBiter SDK source plus recovered `ParseApduResp` at `0x32b74`; unavailable `JUBR_ERROR_ARGS` alias corrected to the existing public arguments code; Android error-status authentication recovered from ELF `ParseSafeApduResp` at `0xdce5c`. |
| `gpc/` | JuBiter SDK source. Added header/value bounds checks, corrected input-end pointers and freed temporary buffers on rejected tags. Cryptographic formulas retained; the Android 32-bit ICV counter encoding was recovered from the installed ELF at `0xdde30`, rather than copied from the older iOS implementation. |
| `deps/trezor-crypto/` | JuBiter's source-vendored TrezorCrypto. Compiled algorithm sources unchanged; original `rand.c` is preserved for provenance but excluded from the build. |
| `src/random.c` | Replacement OS entropy adapter that handles interrupted/short reads and fails closed on entropy failure. |
| `include/mSIGNA/stdutils/uchar_vector.h` | Small reconstructed internal hexadecimal subset; unrelated Bitcoin/TrustWallet helpers are not needed by this SDK. |
| `android/implJni.cpp` | Reconstructed original JNI names/signatures/JSON fields with scoped UTF-string and output ownership. No original logger calls or dangling temporary string pointers. |
| `include/utility/Debug.hpp` | Logging disabled in every build configuration because upstream debug statements include session secrets. |
| `docs/UPSTREAM_USAGE.md` | Exact GPChannel-test README. |
| CMake, tests, version/umbrella header, remaining documentation | Recovery work. |

Original copyright and dependency license notices are retained. The public
JuBiter source snapshot and GPChannel demo do not provide a repository-wide
license grant; the React Native wrapper's MIT notice does not relicense those
vendor files. This recovery does not alter their ownership or licensing.

## Reference execution method

The iOS reference depends only on `libc++` and `libSystem`. For host comparison,
`vtool` changed only platform build metadata on a disposable copy, followed by
ad-hoc signing. No reference protocol instructions or random-number functions
were patched. The original and recovered implementations use fresh ephemeral
keys. Each transcript is checked independently against Python cryptography's
P-256 ECDH, SHA-1, KDF2/SHA-256, AES-CMAC and AES-CBC results.

Android comparison loads the exact installed arm64 `.so` and the rebuilt JNI
library into separate ART `app_process` processes in an isolated API 34 emulator.
The Java proxy uses the original registered class and all original JNI method
signatures. It uses synthetic identities only, without installing the wallet app.
