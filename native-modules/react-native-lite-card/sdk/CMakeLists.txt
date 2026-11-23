cmake_minimum_required(VERSION 3.16)
project(GPChannelSDKCoreRecovered LANGUAGES C CXX)
set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
include(source-id.cmake)

# Restore the original source dependencies without linking a vendor binary.
set(TREZOR_FILES
  deps/trezor-crypto/src/bignum.c
  deps/trezor-crypto/src/ecdsa.c
  deps/trezor-crypto/src/curves.c
  deps/trezor-crypto/src/secp256k1.c
  deps/trezor-crypto/src/hmac.c
  deps/trezor-crypto/src/bip32.c
  deps/trezor-crypto/src/bip39.c
  deps/trezor-crypto/src/pbkdf2.c
  deps/trezor-crypto/src/base58.c
  deps/trezor-crypto/src/base32.c
  deps/trezor-crypto/src/address.c
  deps/trezor-crypto/src/script.c
  deps/trezor-crypto/src/ripemd160.c
  deps/trezor-crypto/src/sha2.c
  deps/trezor-crypto/src/sha3.c
  deps/trezor-crypto/src/hasher.c
  deps/trezor-crypto/src/aes/aescrypt.c
  deps/trezor-crypto/src/aes/aeskey.c
  deps/trezor-crypto/src/aes/aestab.c
  deps/trezor-crypto/src/aes/aes_modes.c
  deps/trezor-crypto/src/ed25519-donna/curve25519-donna-32bit.c
  deps/trezor-crypto/src/ed25519-donna/curve25519-donna-helpers.c
  deps/trezor-crypto/src/ed25519-donna/modm-donna-32bit.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-donna-basepoint-table.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-donna-32bit-tables.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-donna-impl-base.c
  deps/trezor-crypto/src/ed25519-donna/ed25519.c
  deps/trezor-crypto/src/ed25519-donna/curve25519-donna-scalarmult-base.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-sha3.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-keccak.c
  deps/trezor-crypto/src/ed25519-donna/ed25519-blake2b.c
  deps/trezor-crypto/src/sodium/private/fe_25_5/fe.c
  deps/trezor-crypto/src/sodium/private/ed25519_ref10.c
  deps/trezor-crypto/src/sodium/private/ed25519_ref10_fe_25_5.c
  deps/trezor-crypto/src/sodium/keypair.c
  deps/trezor-crypto/src/monero/base58.c
  deps/trezor-crypto/src/monero/serialize.c
  deps/trezor-crypto/src/monero/xmr.c
  deps/trezor-crypto/src/monero/range_proof.c
  deps/trezor-crypto/src/blake256.c
  deps/trezor-crypto/src/blake2b.c
  deps/trezor-crypto/src/blake2s.c
  deps/trezor-crypto/src/chacha20poly1305/chacha20poly1305.c
  deps/trezor-crypto/src/chacha20poly1305/chacha_merged.c
  deps/trezor-crypto/src/chacha20poly1305/poly1305-donna.c
  deps/trezor-crypto/src/chacha20poly1305/rfc7539.c
  deps/trezor-crypto/src/rc4.c
  deps/trezor-crypto/src/nano.c
  deps/trezor-crypto/src/nem.c
  deps/trezor-crypto/src/cash_addr.c
  deps/trezor-crypto/src/memzero.c
  deps/trezor-crypto/src/scrypt.c
  deps/trezor-crypto/src/ripple/base58.c
  deps/trezor-crypto/src/nist256p1.c
  deps/trezor-crypto/src/groestl.c
  deps/trezor-crypto/src/schnorr.c
  deps/trezor-crypto/src/cmac.c
)
add_library(gpchannel_core STATIC
  src/GPChannelSDK.cpp src/ApduBuilder.cpp src/random.c src/version.c
  gpc/src/scp03.cpp gpc/src/scp11.cpp gpc/src/scp11c.cpp gpc/src/tlv.cpp
  ${TREZOR_FILES})
target_include_directories(gpchannel_core PUBLIC include
  PRIVATE gpc/include deps/trezor-crypto/include deps/trezor-crypto/src "${GPCHANNEL_GENERATED_INCLUDE}")
add_library(GPChannelSDKCore SHARED
  include/GPChannelSDK.h include/GPChannelSDKCore.h)
target_include_directories(GPChannelSDKCore PUBLIC include)
set_target_properties(GPChannelSDKCore PROPERTIES LINKER_LANGUAGE CXX)
# Export the complete vendor ABI even when no application links it at build time.
if(APPLE)
  target_link_libraries(GPChannelSDKCore PRIVATE
    "-Wl,-force_load,$<TARGET_FILE:gpchannel_core>" gpchannel_core)
else()
  target_link_libraries(GPChannelSDKCore PRIVATE
    "-Wl,--whole-archive" gpchannel_core "-Wl,--no-whole-archive")
endif()
if(CMAKE_SYSTEM_NAME STREQUAL "iOS")
  set_target_properties(GPChannelSDKCore PROPERTIES FRAMEWORK TRUE
    MACOSX_FRAMEWORK_IDENTIFIER "so.onekey.GPChannelSDKCoreRecovered"
    PUBLIC_HEADER "include/GPChannelSDKCore.h;include/GPChannelSDK.h"
    XCODE_ATTRIBUTE_DEFINES_MODULE YES
    XCODE_ATTRIBUTE_CLANG_ENABLE_MODULES YES
    XCODE_ATTRIBUTE_CODE_SIGNING_ALLOWED NO)
endif()

option(GPCHANNEL_HOST_JNI "Build JNI for a host-JVM contract check" OFF)
if(ANDROID OR GPCHANNEL_HOST_JNI)
  add_library(gpchannelNDK SHARED android/implJni.cpp
    deps/jsoncpp/src/lib_json/json_reader.cpp
    deps/jsoncpp/src/lib_json/json_value.cpp
    deps/jsoncpp/src/lib_json/json_writer.cpp)
  target_include_directories(gpchannelNDK PRIVATE deps/jsoncpp/include)
  target_link_libraries(gpchannelNDK PRIVATE gpchannel_core)
  if(GPCHANNEL_HOST_JNI AND NOT ANDROID)
    find_package(JNI REQUIRED)
    target_include_directories(gpchannelNDK PRIVATE ${JNI_INCLUDE_DIRS})
  endif()
endif()

include(CTest)
if(BUILD_TESTING AND NOT CMAKE_CROSSCOMPILING)
  add_executable(gpchannel_safety tests/safety.cpp)
  target_include_directories(gpchannel_safety PRIVATE include deps/trezor-crypto/include)
  target_link_libraries(gpchannel_safety PRIVATE GPChannelSDKCore)
  if(GPCHANNEL_HOST_JNI AND NOT ANDROID)
    target_compile_definitions(gpchannel_safety PRIVATE GPCHANNEL_TEST_JNI=1)
    target_include_directories(gpchannel_safety PRIVATE ${JNI_INCLUDE_DIRS})
    target_link_libraries(gpchannel_safety PRIVATE gpchannelNDK)
  endif()
  add_test(NAME gpchannel_safety COMMAND gpchannel_safety)
endif()
