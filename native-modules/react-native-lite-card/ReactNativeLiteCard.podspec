require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeLiteCard"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/onekeyfe-react-native-lite-card.git", :tag => "#{s.version}" }

  trezor_sources = %w[
    bignum ecdsa curves secp256k1 hmac bip32 bip39 pbkdf2 base58 base32 address
    script ripemd160 sha2 sha3 hasher aes/aescrypt aes/aeskey aes/aestab
    aes/aes_modes ed25519-donna/curve25519-donna-32bit
    ed25519-donna/curve25519-donna-helpers ed25519-donna/modm-donna-32bit
    ed25519-donna/ed25519-donna-basepoint-table
    ed25519-donna/ed25519-donna-32bit-tables ed25519-donna/ed25519-donna-impl-base
    ed25519-donna/ed25519 ed25519-donna/curve25519-donna-scalarmult-base
    ed25519-donna/ed25519-sha3 ed25519-donna/ed25519-keccak
    ed25519-donna/ed25519-blake2b sodium/private/fe_25_5/fe
    sodium/private/ed25519_ref10 sodium/private/ed25519_ref10_fe_25_5
    sodium/keypair monero/base58 monero/serialize monero/xmr monero/range_proof
    blake256 blake2b blake2s chacha20poly1305/chacha20poly1305
    chacha20poly1305/chacha_merged chacha20poly1305/poly1305-donna
    chacha20poly1305/rfc7539 rc4 nano nem cash_addr memzero scrypt
    ripple/base58 nist256p1 groestl schnorr cmac
  ].map { |name| "sdk/deps/trezor-crypto/src/#{name}.c" }

  s.source_files = "ios/**/*.{h,m,mm,swift,cpp}", "keys/**/*.{h,c,m,mm,swift,cpp}",
    "sdk/src/*.{c,cpp}", "sdk/gpc/src/*.cpp", *trezor_sources
  s.private_header_files = "ios/**/*.h"
  s.preserve_paths = "sdk/**/*"
  s.libraries = "c++"
  s.script_phase = {
    :name => 'Generate Lite native source identity',
    :execution_position => :before_compile,
    :script => 'set -e; ruby "${PODS_TARGET_SRCROOT}/sdk/scripts/generate-source-id.rb" "${DERIVED_FILE_DIR}/GPChannelSourceId.h"'
  }
  s.pod_target_xcconfig = {
    "HEADER_SEARCH_PATHS" => '$(inherited) "$(DERIVED_FILE_DIR)" "$(PODS_TARGET_SRCROOT)/sdk/include" "$(PODS_TARGET_SRCROOT)/sdk/gpc/include" "$(PODS_TARGET_SRCROOT)/sdk/deps/trezor-crypto/include" "$(PODS_TARGET_SRCROOT)/sdk/deps/trezor-crypto/src"'
  }


  s.dependency 'ReactNativeNativeLogger'

  install_modules_dependencies(s)
end
