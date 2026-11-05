#include "GPChannelSDK.h"
#include <TrezorCrypto/aes.h>
#include <TrezorCrypto/cmac.h>
#include <TrezorCrypto/ecdsa.h>
#include <TrezorCrypto/nist256p1.h>
#include <TrezorCrypto/sha2.h>
#include <algorithm>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <map>
#include <random>
#include <string>
#include <vector>
#ifdef GPCHANNEL_TEST_JNI
#include <jni.h>
#endif

static void require(bool condition, const char* message = "Safety contract failed") {
    if (!condition) {
        std::cerr << message << "\n";
        std::abort();
    }
}

static std::string hex(const unsigned char* data, size_t len) {
    const char* digits = "0123456789abcdef";
    std::string result;
    result.reserve(len * 2);
    for (size_t i = 0; i < len; ++i) {
        unsigned char byte = data[i];
        result += digits[byte >> 4];
        result += digits[byte & 15];
    }
    return result;
}

static std::string hex(const std::string& data) {
    return hex(reinterpret_cast<const unsigned char*>(data.data()), data.size());
}

static std::vector<unsigned char> unhex(const std::string& text) {
    require(text.size() % 2 == 0, "Hex string must have even length");
    std::vector<unsigned char> out(text.size() / 2, 0);
    for (size_t i = 0; i < out.size(); ++i) {
        auto nibble = [](char c) -> unsigned char {
            if (c >= '0' && c <= '9') return static_cast<unsigned char>(c - '0');
            if (c >= 'a' && c <= 'f') return static_cast<unsigned char>(c - 'a' + 10);
            if (c >= 'A' && c <= 'F') return static_cast<unsigned char>(c - 'A' + 10);
            require(false, "Invalid hex digit");
            return 0;
        };
        out[i] = static_cast<unsigned char>((nibble(text[2 * i]) << 4) | nibble(text[2 * i + 1]));
    }
    return out;
}

static std::string makeTlv(unsigned int tag, const std::string& bodyHex) {
    size_t len = bodyHex.size() / 2;
    std::vector<unsigned char> prefix;
    if (tag > 0xff) {
        prefix.push_back(static_cast<unsigned char>((tag >> 8) & 0xff));
        prefix.push_back(static_cast<unsigned char>(tag & 0xff));
    } else {
        prefix.push_back(static_cast<unsigned char>(tag & 0xff));
    }
    if (tag == 0x7f21 || tag == 0xbf21) {
        if (len <= 0xff) {
            prefix.push_back(0x81);
            prefix.push_back(static_cast<unsigned char>(len));
        } else {
            prefix.push_back(0x82);
            prefix.push_back(static_cast<unsigned char>((len >> 8) & 0xff));
            prefix.push_back(static_cast<unsigned char>(len & 0xff));
        }
    } else {
        prefix.push_back(static_cast<unsigned char>(len));
    }
    return hex(prefix.data(), prefix.size()) + bodyHex;
}

static std::string publicKeyHexFromScalar(const std::string& scalarHex) {
    std::vector<unsigned char> priv = unhex(scalarHex);
    require(priv.size() == 32, "Scalar must be 32 bytes");
    unsigned char pub[65] = {0};
    ecdsa_get_public_key65(&nist256p1, priv.data(), pub);
    return hex(pub, sizeof(pub));
}

static std::string buildCertificate(const std::string& pubKeyHex, size_t targetInnerBytes = 0) {
    std::string inner;
    inner += makeTlv(0x93, "30313233343536373839");
    inner += makeTlv(0x42, "43414b4c4f43");
    inner += makeTlv(0x5f20, "5355424a3031");
    inner += makeTlv(0x95, "0080");
    inner += makeTlv(0x5f25, "20240101");
    inner += makeTlv(0x5f24, "20290101");
    inner += makeTlv(0x7f49, makeTlv(0xb0, pubKeyHex) + makeTlv(0xf0, "00"));
    inner += makeTlv(0x5f37, "30440220" + std::string(64, 'a') + "0220" + std::string(64, 'b'));
    if (targetInnerBytes > 0) {
        size_t currentBytes = inner.size() / 2;
        require(targetInnerBytes >= currentBytes + 2, "targetInnerBytes too small");
        size_t padLen = targetInnerBytes - currentBytes - 2;
        inner += makeTlv(0x53, std::string(padLen * 2, '0'));
        require(inner.size() / 2 == targetInnerBytes, "Certificate inner size mismatch");
    } else {
        inner += makeTlv(0x53, "");
    }
    return makeTlv(0x7f21, inner);
}

static GPC_SCP11_SHAREDINFO defaultSharedInfo() {
    GPC_SCP11_SHAREDINFO info;
    info.scpID = const_cast<char*>("1107");
    info.keyUsage = const_cast<char*>("3c");
    info.keyType = const_cast<char*>("88");
    info.keyLength = const_cast<char*>("10");
    info.hostID = const_cast<char*>("8080808080808080");
    info.cardGroupID = const_cast<char*>("5355424a3031");
    return info;
}

static std::string buildCardMutualAuthResponse(
    const std::string& cardPrivHex,
    const std::string& ocePubHex,
    const std::string& mutualAuthCmdHex,
    std::vector<unsigned char>* derivedKeysOut = nullptr) {
    std::vector<unsigned char> cardPriv = unhex(cardPrivHex);
    std::vector<unsigned char> oceStaticPub = unhex(ocePubHex);
    std::vector<unsigned char> maData = unhex(mutualAuthCmdHex);
    require(maData.size() >= 65, "Mutual auth data too short");
    std::vector<unsigned char> oceEphPub(maData.end() - 65, maData.end());

    unsigned char cardPub[65] = {0};
    ecdsa_get_public_key65(&nist256p1, cardPriv.data(), cardPub);

    unsigned char sharedStatic[65] = {0};
    unsigned char sharedEph[65] = {0};
    require(ecdh_multiply(&nist256p1, cardPriv.data(), oceStaticPub.data(), sharedStatic) == 0);
    require(ecdh_multiply(&nist256p1, cardPriv.data(), oceEphPub.data(), sharedEph) == 0);

    unsigned char shSss[20] = {0};
    unsigned char shSes[20] = {0};
    SHA1_CTX sha1;
    sha1_Init(&sha1);
    sha1_Update(&sha1, sharedStatic + 1, 32);
    sha1_Final(&sha1, shSss);

    sha1_Init(&sha1);
    sha1_Update(&sha1, sharedEph + 1, 32);
    sha1_Final(&sha1, shSes);

    std::vector<unsigned char> z;
    z.insert(z.end(), shSes, shSes + 20);
    z.insert(z.end(), shSss, shSss + 20);

    std::vector<unsigned char> sharedInfo = unhex("3c88100880808080808080805355424a3031");
    std::vector<unsigned char> derived(80, 0);
    for (unsigned int counter = 1; counter <= 3; ++counter) {
        unsigned char ctrBe[4] = {
            static_cast<unsigned char>((counter >> 24) & 0xff),
            static_cast<unsigned char>((counter >> 16) & 0xff),
            static_cast<unsigned char>((counter >> 8) & 0xff),
            static_cast<unsigned char>(counter & 0xff),
        };
        unsigned char digest[32] = {0};
        SHA256_CTX s256;
        sha256_Init(&s256);
        sha256_Update(&s256, z.data(), z.size());
        sha256_Update(&s256, ctrBe, 4);
        sha256_Update(&s256, sharedInfo.data(), sharedInfo.size());
        sha256_Final(&s256, digest);
        size_t offset = (counter - 1) * 32;
        size_t copyLen = std::min<size_t>(32, 80 - offset);
        std::memcpy(derived.data() + offset, digest, copyLen);
    }
    if (derivedKeysOut) {
        *derivedKeysOut = derived;
    }

    std::string cardTlvHex = makeTlv(0x5f49, hex(cardPub, sizeof(cardPub)));
    std::vector<unsigned char> cardTlv = unhex(cardTlvHex);
    std::vector<unsigned char> receiptInput = maData;
    receiptInput.insert(receiptInput.end(), cardTlv.begin(), cardTlv.end());

    unsigned char receipt[16] = {0};
    AES_CMAC(derived.data(), receiptInput.data(), static_cast<unsigned>(receiptInput.size()), receipt);
    return cardTlvHex + makeTlv(0x86, hex(receipt, sizeof(receipt)));
}

#ifdef GPCHANNEL_TEST_JNI
struct MockJniRuntime {
    JNINativeInterface_ envTable;
    JNIEnv env;
    JNIInvokeInterface_ vmTable;
    JavaVM vm;
    std::map<std::string, void*> methods;
    std::vector<std::string> allocatedStrings;

    static MockJniRuntime*& active() {
        static MockJniRuntime* instance = nullptr;
        return instance;
    }

    static jint JNICALL mockGetEnv(JavaVM*, void** penv, jint) {
        *penv = &active()->env;
        return JNI_OK;
    }

    static jclass JNICALL mockFindClass(JNIEnv*, const char* name) {
        require(name && std::string(name) == "org/haobtc/onekey/card/gpchannel/GPChannelNatives",
                "Unexpected JNI class lookup");
        return reinterpret_cast<jclass>(0x1);
    }

    static jint JNICALL mockRegisterNatives(JNIEnv*, jclass, const JNINativeMethod* methods, jint count) {
        for (jint i = 0; i < count; ++i) {
            active()->methods[methods[i].name] = methods[i].fnPtr;
        }
        return JNI_OK;
    }

    static void JNICALL mockDeleteLocalRef(JNIEnv*, jobject) {}

    static const char* JNICALL mockGetStringUTFChars(JNIEnv*, jstring str, jboolean* isCopy) {
        if (isCopy) *isCopy = JNI_FALSE;
        return str ? reinterpret_cast<const std::string*>(str)->c_str() : nullptr;
    }

    static void JNICALL mockReleaseStringUTFChars(JNIEnv*, jstring, const char*) {}

    static jstring JNICALL mockNewStringUTF(JNIEnv*, const char* utf) {
        if (!utf) return nullptr;
        active()->allocatedStrings.push_back(std::string(utf));
        return reinterpret_cast<jstring>(&active()->allocatedStrings.back());
    }

    MockJniRuntime() {
        std::memset(&envTable, 0, sizeof(envTable));
        std::memset(&vmTable, 0, sizeof(vmTable));
        allocatedStrings.reserve(256);
        envTable.FindClass = &mockFindClass;
        envTable.RegisterNatives = &mockRegisterNatives;
        envTable.DeleteLocalRef = &mockDeleteLocalRef;
        envTable.GetStringUTFChars = &mockGetStringUTFChars;
        envTable.ReleaseStringUTFChars = &mockReleaseStringUTFChars;
        envTable.NewStringUTF = &mockNewStringUTF;
        env.functions = &envTable;
        vmTable.GetEnv = &mockGetEnv;
        vm.functions = &vmTable;
        active() = this;
    }

    jstring makeJString(const std::string& s) {
        allocatedStrings.push_back(s);
        return reinterpret_cast<jstring>(&allocatedStrings.back());
    }

    static std::string readJString(jstring str) {
        require(str != nullptr, "Expected non-null jstring");
        return *reinterpret_cast<const std::string*>(str);
    }
};
#endif

int main() {
    // 1. Basic lifecycle & allocation checks
    require(JUB_GPC_Finalize() == 0);
    require(JUB_GPC_Finalize() == 0);
    require(JUB_FreeMemory(nullptr) == JUBR_MEMORY_NULL_PTR);
    require(JUB_GPC_BuildAPDU(0, 0, 0, 0, "", nullptr) == JUBR_MEMORY_NULL_PTR);
    char* output = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&output) == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_OpenSecureChannel("") == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_BuildSafeAPDU(0, 0, 0, 0, "", &output) == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_BuildAPDU(0, 0, 0, 0, "zz", &output) == JUBR_ARGUMENTS_BAD);
    for (const auto& text : {"0", "123", "AbC", "0123", "ABCDEF", ""}) {
        require(JUB_GPC_BuildAPDU(0x80, 0x3b, 0, 0, text, &output) == JUBR_OK && output,
                "Valid odd and even command inputs must build");
        const std::string input(text);
        const std::string normalized = input.size() % 2 ? "0" + input : input;
        char* expected = nullptr;
        require(JUB_GPC_BuildAPDU(0x80, 0x3b, 0, 0, normalized.c_str(), &expected) == JUBR_OK);
        require(std::string(output) == expected, "Odd command must equal whole-input leading-zero encoding");
        require(JUB_FreeMemory(output) == JUBR_OK);
        require(JUB_FreeMemory(expected) == JUBR_OK);
    }
    for (const auto& text : {"z", "12z", "0x1", " 1", "+1", "1\n"}) {
        output = reinterpret_cast<char*>(1);
        require(JUB_GPC_BuildAPDU(0, 0, 0, 0, text, &output) == JUBR_ARGUMENTS_BAD && !output,
                "Invalid odd/even command must fail with null output");
    }
    for (size_t digits : {size_t(131069), size_t(131070), size_t(131071)}) {
        const std::string input(digits, 'a');
        const auto result = JUB_GPC_BuildAPDU(0x80, 0x3b, 0, 0, input.c_str(), &output);
        require(result == (digits <= 131070 ? JUBR_OK : JUBR_ARGUMENTS_BAD),
                "Plain command limits count decoded bytes after leading-zero normalization");
        if (output) require(JUB_FreeMemory(output) == JUBR_OK);
    }
    JUB_UINT16 status = 0;
    require(JUB_GPC_ParseAPDUResponse("900", &status, &output) == JUBR_ARGUMENTS_BAD && !output,
            "Odd response input must remain rejected");
    require(JUB_GPC_ParseAPDUResponse("9000", &status, &output) == 0);
    require(status == 0x9000 && output && !*output);
    require(JUB_FreeMemory(output) == 0);
    require(JUB_FreeMemory(output) == JUBR_INVALID_MEMORY_PTR);

    // 2. Scalar & public key curve validation (LITE-08)
    const std::string validOcePriv = "0000000000000000000000000000000000000000000000000000000000000001";
    const std::string validCardPriv = "0000000000000000000000000000000000000000000000000000000000000002";
    const std::string zeroPriv = "0000000000000000000000000000000000000000000000000000000000000000";
    const std::string orderPriv = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    const std::string allOnesPriv = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    const std::string offCurvePub = "04" + std::string(128, '1');

    GPC_SCP11_SHAREDINFO info = defaultSharedInfo();
    std::string ocePub = publicKeyHexFromScalar(validOcePriv);
    std::string validCert = buildCertificate(ocePub);
    std::string offCurveCert = buildCertificate(offCurvePub);

    char* strictSerial = nullptr;
    char* strictSubject = nullptr;
    const std::string oddCert = validCert.substr(1);
    require(JUB_GPC_ParseCertificate(oddCert.c_str(), &strictSerial, &strictSubject) == JUBR_ARGUMENTS_BAD &&
            !strictSerial && !strictSubject, "Odd certificate must remain rejected");
    JUB_ULONG strictTag = 0;
    require(JUB_GPC_TLVDecode("53100", &strictTag, &output) == JUBR_ARGUMENTS_BAD && !output,
            "Odd TLV input must remain rejected");

    require(JUB_GPC_Initialize(info, validCert.c_str(), validOcePriv.c_str()) == JUBR_OK,
            "Valid scalar and certificate should initialize");
    require(JUB_GPC_OpenSecureChannel("5f490") == JUBR_ARGUMENTS_BAD,
            "Odd handshake response must remain rejected");
    require(JUB_GPC_Finalize() == JUBR_OK);

    require(JUB_GPC_Initialize(info, validCert.c_str(), zeroPriv.c_str()) == JUBR_ARGUMENTS_BAD,
            "All-zero private scalar must be rejected");
    require(JUB_GPC_Initialize(info, validCert.c_str(), orderPriv.c_str()) == JUBR_ARGUMENTS_BAD,
            "Private scalar == curve order must be rejected");
    require(JUB_GPC_Initialize(info, validCert.c_str(), allOnesPriv.c_str()) == JUBR_ARGUMENTS_BAD,
            "Private scalar > curve order must be rejected");
    require(JUB_GPC_Initialize(info, offCurveCert.c_str(), validOcePriv.c_str()) == JUBR_ARGUMENTS_BAD,
            "Off-curve certificate public key must be rejected in Initialize");

    char* serial = nullptr;
    char* subject = nullptr;
    require(JUB_GPC_ParseCertificate(offCurveCert.c_str(), &serial, &subject) == JUBR_ARGUMENTS_BAD,
            "Off-curve certificate public key must be rejected in ParseCertificate");
    require(!serial && !subject);

    // Off-curve card ephemeral public key in OpenSecureChannel must be rejected
    require(JUB_GPC_Initialize(info, validCert.c_str(), validOcePriv.c_str()) == JUBR_OK);
    output = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&output) == JUBR_OK && output);
    require(JUB_FreeMemory(output) == JUBR_OK);
    std::string offCurveAuthResp = makeTlv(0x5f49, offCurvePub) + makeTlv(0x86, std::string(32, '0'));
    require(JUB_GPC_OpenSecureChannel(offCurveAuthResp.c_str()) == JUBR_ARGUMENTS_BAD,
            "Off-curve 5F49 public key must be rejected in OpenSecureChannel");
    require(JUB_GPC_Finalize() == JUBR_OK);

    // 3. Recursive TLV parser skip wraparound & bounds (LITE-03)
    // 1-byte tag 0x93 with length 0xFE (254): skip = 2 + 254 = 256 (wrapped uint8_t to 0 before fix)
    std::string certSkipWrap256 = makeTlv(0x7f21, "93fe" + std::string(254 * 2, '0'));
    require(JUB_GPC_ParseCertificate(certSkipWrap256.c_str(), &serial, &subject) == JUBR_ARGUMENTS_BAD,
            "TLV skip == 256 in certificate must not wrap or crash");
    require(!serial && !subject);

    // 2-byte tag 0x5F20 with length 0xFD (253): skip = 3 + 253 = 256 (wrapped uint8_t to 0 before fix)
    std::string certTwoByteTagWrap256 = makeTlv(0x7f21, "5f20fd" + std::string(253 * 2, '0'));
    require(JUB_GPC_ParseCertificate(certTwoByteTagWrap256.c_str(), &serial, &subject) == JUBR_ARGUMENTS_BAD,
            "TLV skip == 256 on 2-byte certificate tag must not wrap or crash");
    require(!serial && !subject);

    // Nested public-key tag 0xB0 with length 0xFE (254) inside 0x7F49 (length 0xFF = 255):
    // tests scp11_pk::decode when skip = 2 + 254 = 256
    std::string pkSkipWrap256 = makeTlv(0x7f21, "7f49ffb0fe" + std::string(253 * 2, '0'));
    require(JUB_GPC_ParseCertificate(pkSkipWrap256.c_str(), &serial, &subject) == JUBR_ARGUMENTS_BAD,
            "TLV skip == 256 in scp11_pk::decode must not wrap or crash");
    require(!serial && !subject);

    // 2-byte tag 0x5F49 with length 0xFD (253): skip = 3 + 253 = 256 (wrapped uint8_t to 0 before fix)
    require(JUB_GPC_Initialize(info, validCert.c_str(), validOcePriv.c_str()) == JUBR_OK);
    output = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&output) == JUBR_OK && output);
    require(JUB_FreeMemory(output) == JUBR_OK);
    std::string maSkipWrap256 = "5f49fd" + std::string(253 * 2, '0');
    require(JUB_GPC_OpenSecureChannel(maSkipWrap256.c_str()) == JUBR_ARGUMENTS_BAD,
            "TLV skip == 256 in OpenSecureChannel must not wrap or crash");
    require(JUB_GPC_Finalize() == JUBR_OK);

    // 4. 7F21 certificate 256-byte boundary & trailing TLV smuggling (LITE-07)
    for (size_t innerLen : {static_cast<size_t>(255), static_cast<size_t>(256), static_cast<size_t>(257)}) {
        std::string sizedCert = buildCertificate(ocePub, innerLen);
        serial = nullptr;
        subject = nullptr;
        require(JUB_GPC_ParseCertificate(sizedCert.c_str(), &serial, &subject) == JUBR_OK,
                "Valid certificate with 255/256/257-byte inner body must succeed");
        require(serial && std::string(serial) == "0123456789");
        require(subject && std::string(subject) == "5355424a3031");
        require(JUB_FreeMemory(serial) == JUBR_OK);
        require(JUB_FreeMemory(subject) == JUBR_OK);
    }

    std::string smuggledCert =
        "7f2181025300" +
        makeTlv(0x93, "534d5547474c45445f534e") +
        makeTlv(0x42, "4341") +
        makeTlv(0x5f20, "deadbeef") +
        makeTlv(0x95, "0080") +
        makeTlv(0x5f25, "20240101") +
        makeTlv(0x5f24, "20290101") +
        makeTlv(0x7f49, makeTlv(0xb0, ocePub) + makeTlv(0xf0, "00")) +
        makeTlv(0x5f37, "30440220" + std::string(64, 'a') + "0220" + std::string(64, 'b'));
    serial = nullptr;
    subject = nullptr;
    require(JUB_GPC_ParseCertificate(smuggledCert.c_str(), &serial, &subject) == JUBR_ARGUMENTS_BAD,
            "7F21 certificate with trailing smuggled TLV tags must be rejected");
    require(!serial && !subject);

    // 5. Session state lifecycle & full SCP11c + SCP03 roundtrip (LITE-09)
    require(JUB_GPC_Initialize(info, validCert.c_str(), validOcePriv.c_str()) == JUBR_OK);
    char* maCmdHex = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&maCmdHex) == JUBR_OK && maCmdHex);
    std::string maCmdStr(maCmdHex);
    require(JUB_FreeMemory(maCmdHex) == JUBR_OK);

    std::vector<unsigned char> derivedKeys;
    std::string maRespHex = buildCardMutualAuthResponse(validCardPriv, ocePub, maCmdStr, &derivedKeys);
    require(JUB_GPC_OpenSecureChannel(maRespHex.c_str()) == JUBR_OK,
            "Valid mutual authentication response must open secure channel");

    for (const auto& input : {std::string("xyz"), std::string(131023, 'a'), std::string(131024, 'a')}) {
        output = reinterpret_cast<char*>(1);
        require(JUB_GPC_BuildSafeAPDU(0x80, 0x3b, 0, 0, input.c_str(), &output) == JUBR_ARGUMENTS_BAD && !output,
                "Invalid and oversized safe command inputs must not build");
    }

    // Encrypted APDU build must succeed while session is open
    char* safeApduHex = nullptr;
    const std::string oddBackup = "12345678901234567890123456789012345678900000001";
    require(JUB_GPC_BuildSafeAPDU(0x80, 0x20, 0x00, 0x00, oddBackup.c_str(), &safeApduHex) == JUBR_OK && safeApduHex);
    std::vector<unsigned char> safeApduBytes = unhex(safeApduHex);
    require(JUB_FreeMemory(safeApduHex) == JUBR_OK);
    require(safeApduBytes.size() == 5 + 32 + 8 && safeApduBytes[0] == 0x84);
    aes_encrypt_ctx commandEnc;
    aes_encrypt_key128(derivedKeys.data() + 16, &commandEnc);
    unsigned char commandCounter[16] = {0};
    commandCounter[15] = 1;
    unsigned char commandIv[16] = {0};
    aes_ecb_encrypt(commandCounter, commandIv, 16, &commandEnc);
    aes_decrypt_ctx commandDec;
    aes_decrypt_key128(derivedKeys.data() + 16, &commandDec);
    unsigned char commandPlain[32] = {0};
    aes_cbc_decrypt(safeApduBytes.data() + 5, commandPlain, 32, commandIv, &commandDec);
    const auto expectedCommand = unhex("0" + oddBackup);
    require(std::equal(expectedCommand.begin(), expectedCommand.end(), commandPlain) &&
            commandPlain[expectedCommand.size()] == 0x80 &&
            std::all_of(commandPlain + expectedCommand.size() + 1, commandPlain + 32,
                        [](unsigned char byte) { return byte == 0; }),
            "45-digit backup must encrypt whole-input leading-zero bytes with unchanged padding");

    // Verify JUB_GPC_ParseSafeAPDUResponse with valid RMAC, tampered RMAC, and status-only error:
    // derivedKeys layout: [0..16]=key_dek, [16..32]=s_enc, [32..48]=s_mac, [48..64]=s_rmac
    // After BuildSafeAPDU #1 (counter == 1), the SDK's 16-byte chaining CMAC is computed over:
    // receipt(16B) || header/Lc || ciphertext
    std::vector<unsigned char> maRespBytes = unhex(maRespHex);
    std::vector<unsigned char> receiptBytes(maRespBytes.end() - 16, maRespBytes.end());
    std::vector<unsigned char> cmacInput = receiptBytes;
    cmacInput.insert(cmacInput.end(), safeApduBytes.begin(), safeApduBytes.end() - 8);
    unsigned char cmdChain[16] = {0};
    AES_CMAC(derivedKeys.data() + 32, cmacInput.data(), static_cast<unsigned>(cmacInput.size()), cmdChain);
    require(std::memcmp(cmdChain, safeApduBytes.data() + safeApduBytes.size() - 8, 8) == 0,
            "Command CMAC prefix must match independent AES-CMAC");

    // Construct encrypted response for counter == 2 (incCounter() pre-increments 1 -> 2):
    // Response ICV = AES_ENC(s_enc, 80 00 ... 00 02)
    aes_encrypt_ctx encCtx;
    aes_encrypt_key128(derivedKeys.data() + 16, &encCtx);
    unsigned char respCounterBlock[16] = {0};
    respCounterBlock[0] = 0x80;
    respCounterBlock[15] = 0x02;
    unsigned char respIcv[16] = {0};
    aes_ecb_encrypt(respCounterBlock, respIcv, 16, &encCtx);

    // Plaintext response: "deadbeef" padded with 80 00... to 16 bytes
    unsigned char respPadded[16] = {0xde, 0xad, 0xbe, 0xef, 0x80};
    unsigned char respCipher[16] = {0};
    unsigned char cbcIv[16] = {0};
    std::memcpy(cbcIv, respIcv, 16);
    aes_cbc_encrypt(respPadded, respCipher, 16, cbcIv, &encCtx);

    // Response RMAC input: cmdChain(16B) || respCipher(16B) || 90 00
    std::vector<unsigned char> rmacInput(cmdChain, cmdChain + 16);
    rmacInput.insert(rmacInput.end(), respCipher, respCipher + 16);
    rmacInput.push_back(0x90);
    rmacInput.push_back(0x00);
    unsigned char rmacFull[16] = {0};
    AES_CMAC(derivedKeys.data() + 48, rmacInput.data(), static_cast<unsigned>(rmacInput.size()), rmacFull);

    std::vector<unsigned char> validSafeResp(respCipher, respCipher + 16);
    validSafeResp.insert(validSafeResp.end(), rmacFull, rmacFull + 8);
    validSafeResp.push_back(0x90);
    validSafeResp.push_back(0x00);

    // Tampered 1-bit RMAC must be rejected with JUBR_ERROR
    std::vector<unsigned char> tamperedSafeResp = validSafeResp;
    tamperedSafeResp[tamperedSafeResp.size() - 3] ^= 0x01;
    output = nullptr;
    status = 0;
    require(JUB_GPC_ParseSafeAPDUResponse(hex(tamperedSafeResp.data(), tamperedSafeResp.size()).c_str(), &status, &output) == JUBR_ERROR,
            "Tampered RMAC response must be rejected");
    require(!output);

    require(JUB_GPC_ParseSafeAPDUResponse("900", &status, &output) == JUBR_ARGUMENTS_BAD && !output,
            "Odd protected response input must remain rejected");

    // Unauthenticated plain "9000" on safe channel must be rejected
    require(JUB_GPC_ParseSafeAPDUResponse("9000", &status, &output) != JUBR_OK,
            "Unauthenticated 9000 response on safe channel must be rejected");
    require(!output);

    // Valid authenticated response must decrypt to "deadbeef" and status 0x9000
    require(JUB_GPC_ParseSafeAPDUResponse(hex(validSafeResp.data(), validSafeResp.size()).c_str(), &status, &output) == JUBR_OK,
            "Valid authenticated safe response must succeed");
    require(status == 0x9000 && output && std::string(output) == "deadbeef");
    require(JUB_FreeMemory(output) == JUBR_OK);

    // Status-only card error "63c2" must return JUBR_OK with status 0x63c2 and empty output
    output = nullptr;
    status = 0;
    require(JUB_GPC_ParseSafeAPDUResponse("63c2", &status, &output) == JUBR_OK,
            "Status-only card error 63c2 must parse cleanly");
    require(status == 0x63c2 && output && std::string(output).empty());
    require(JUB_FreeMemory(output) == JUBR_OK);

    for (size_t digits : {size_t(131021), size_t(131022)}) {
        const std::string input(digits, 'a');
        require(JUB_GPC_BuildSafeAPDU(0x80, 0x3b, 0, 0, input.c_str(), &output) == JUBR_OK && output,
                "Safe command accepts both parities at the decoded-byte limit");
        require(JUB_FreeMemory(output) == JUBR_OK);
    }

    // Failed re-initialization must clear the open session
    require(JUB_GPC_Initialize(info, "deadbeef", validOcePriv.c_str()) == JUBR_ARGUMENTS_BAD);
    output = nullptr;
    require(JUB_GPC_BuildSafeAPDU(0x80, 0x20, 0x00, 0x00, "06313233343536", &output) == JUBR_CONTEXT_NOT_SATISFIED,
            "Failed JUB_GPC_Initialize must clear previous open session");
    require(!output);

    // Re-open valid session, then verify failed JUB_GPC_OpenSecureChannel clears it
    require(JUB_GPC_Initialize(info, validCert.c_str(), validOcePriv.c_str()) == JUBR_OK);
    maCmdHex = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&maCmdHex) == JUBR_OK && maCmdHex);
    maCmdStr = maCmdHex;
    require(JUB_FreeMemory(maCmdHex) == JUBR_OK);
    maRespHex = buildCardMutualAuthResponse(validCardPriv, ocePub, maCmdStr, &derivedKeys);
    require(JUB_GPC_OpenSecureChannel(maRespHex.c_str()) == JUBR_OK);

    // Corrupt receipt in a second OpenSecureChannel call
    std::string corruptedResp = maRespHex;
    corruptedResp[corruptedResp.size() - 1] = (corruptedResp.back() == '0') ? '1' : '0';
    require(JUB_GPC_OpenSecureChannel(corruptedResp.c_str()) == JUBR_ERROR);
    output = nullptr;
    require(JUB_GPC_BuildSafeAPDU(0x80, 0x20, 0x00, 0x00, "06313233343536", &output) == JUBR_CONTEXT_NOT_SATISFIED,
            "Failed JUB_GPC_OpenSecureChannel must clear previous open session");
    require(!output);
    require(JUB_GPC_Finalize() == JUBR_OK);

#ifdef GPCHANNEL_TEST_JNI
    // 6. Native JNI bridge tests (implJni.cpp: JSON type validation, nullptr returns, error codes)
    {
        MockJniRuntime jni;
        require(JNI_OnLoad(&jni.vm, nullptr) == JNI_VERSION_1_6, "JNI_OnLoad must succeed");
        require(jni.methods.size() == 11, "All 11 GPChannelNatives JNI methods must be registered");

        typedef jint (*GetErrorFn)(JNIEnv*, jobject);
        typedef jint (*InitFn)(JNIEnv*, jobject, jstring);
        typedef jint (*FinalizeFn)(JNIEnv*, jobject);
        typedef jint (*OpenFn)(JNIEnv*, jobject, jstring);
        typedef jstring (*MutualFn)(JNIEnv*, jobject);
        typedef jstring (*ParseFn)(JNIEnv*, jobject, jstring);

        GetErrorFn jniGetError = reinterpret_cast<GetErrorFn>(jni.methods["nativeGetErrorCode"]);
        InitFn jniInit = reinterpret_cast<InitFn>(jni.methods["nativeGPCInitialize"]);
        FinalizeFn jniFinalize = reinterpret_cast<FinalizeFn>(jni.methods["nativeGPCFinalize"]);
        OpenFn jniOpen = reinterpret_cast<OpenFn>(jni.methods["nativeGPCOpenSecureChannel"]);
        MutualFn jniMutual = reinterpret_cast<MutualFn>(jni.methods["nativeGPCBuildMutualAuthData"]);
        ParseFn jniParseSafe = reinterpret_cast<ParseFn>(jni.methods["nativeGPCParseSafeAPDUResponse"]);
        ParseFn jniParseCert = reinterpret_cast<ParseFn>(jni.methods["nativeGPCParseCertificate"]);

        require(jniFinalize(&jni.env, nullptr) == JUBR_OK);
        require(jniInit(&jni.env, nullptr, nullptr) == JUBR_ARGUMENTS_BAD,
                "JNI initialize(nullptr) must return JUBR_ARGUMENTS_BAD");

        // Malformed JSON and wrong JSON field types (LITE-04) must return JUBR_ARGUMENTS_BAD without throwing
        std::string badJsonSyntax = "{not_json";
        std::string intScpIdJson =
            std::string("{\"scpID\":1107,\"keyUsage\":\"3c\",\"keyType\":\"88\",\"keyLength\":16,") +
            "\"hostID\":\"8080808080808080\",\"cardGroupID\":\"5355424a3031\",\"crt\":\"" +
            validCert + "\",\"sk\":\"" + validOcePriv + "\"}";
        std::string strKeyLenJson =
            std::string("{\"scpID\":\"1107\",\"keyUsage\":\"3c\",\"keyType\":\"88\",\"keyLength\":\"16\",") +
            "\"hostID\":\"8080808080808080\",\"cardGroupID\":\"5355424a3031\",\"crt\":\"" +
            validCert + "\",\"sk\":\"" + validOcePriv + "\"}";
        std::string negKeyLenJson =
            std::string("{\"scpID\":\"1107\",\"keyUsage\":\"3c\",\"keyType\":\"88\",\"keyLength\":-1,") +
            "\"hostID\":\"8080808080808080\",\"cardGroupID\":\"5355424a3031\",\"crt\":\"" +
            validCert + "\",\"sk\":\"" + validOcePriv + "\"}";
        std::string validInitJson =
            std::string("{\"scpID\":\"1107\",\"keyUsage\":\"3c\",\"keyType\":\"88\",\"keyLength\":16,") +
            "\"hostID\":\"8080808080808080\",\"cardGroupID\":\"5355424a3031\",\"crt\":\"" +
            validCert + "\",\"sk\":\"" + validOcePriv + "\"}";

        require(jniInit(&jni.env, nullptr, jni.makeJString(badJsonSyntax)) == JUBR_ARGUMENTS_BAD);
        require(jniInit(&jni.env, nullptr, jni.makeJString(intScpIdJson)) == JUBR_ARGUMENTS_BAD,
                "Integer scpID in JNI JSON must be rejected without throwing");
        require(jniInit(&jni.env, nullptr, jni.makeJString(strKeyLenJson)) == JUBR_ARGUMENTS_BAD,
                "String keyLength in JNI JSON must be rejected without throwing");
        require(jniInit(&jni.env, nullptr, jni.makeJString(negKeyLenJson)) == JUBR_ARGUMENTS_BAD,
                "Negative keyLength in JNI JSON must be rejected without throwing");

        // Calling parseSafe when channel is closed must return nullptr and set errorCode = 6
        require(jniParseSafe(&jni.env, nullptr, jni.makeJString("6a80")) == nullptr,
                "JNI parseSafe must return nullptr when channel is not open");
        require(jniGetError(&jni.env, nullptr) == static_cast<jint>(JUBR_CONTEXT_NOT_SATISFIED));

        // Valid JNI certificate parse and handshake
        jstring certJson = jniParseCert(&jni.env, nullptr, jni.makeJString(validCert));
        require(certJson != nullptr && MockJniRuntime::readJString(certJson).find("0123456789") != std::string::npos);

        require(jniInit(&jni.env, nullptr, jni.makeJString(validInitJson)) == JUBR_OK);
        jstring jniMa = jniMutual(&jni.env, nullptr);
        require(jniMa != nullptr);
        std::string jniMaResp = buildCardMutualAuthResponse(validCardPriv, ocePub, MockJniRuntime::readJString(jniMa));
        require(jniOpen(&jni.env, nullptr, jni.makeJString(jniMaResp)) == JUBR_OK);

        // Unauthenticated "9000" safe response through JNI must return nullptr and set errorCode
        require(jniParseSafe(&jni.env, nullptr, jni.makeJString("9000")) == nullptr,
                "JNI parseSafe must reject unauthenticated 9000 response with nullptr");
        require(jniGetError(&jni.env, nullptr) == static_cast<jint>(JUBR_ARGUMENTS_BAD));
        require(jniFinalize(&jni.env, nullptr) == JUBR_OK);
    }
#endif

    // 7. 5000 malformed-input fuzz cases
    std::mt19937 random(20261103);
    const unsigned char prefixes[] = {0x42, 0x53, 0x73, 0x86, 0x93, 0x95, 0xb0, 0xf0, 0x5f, 0x7f, 0xbf};
    for (size_t i = 0; i < 5000; ++i) {
        std::string data(random() % 256, '\0');
        for (char& byte : data) byte = static_cast<char>(random());
        if (!data.empty()) data[0] = static_cast<char>(prefixes[i % sizeof(prefixes)]);
        auto encoded = hex(data);
        JUB_ULONG tag = 0;
        output = nullptr;
        auto code = JUB_GPC_TLVDecode(encoded.c_str(), &tag, &output);
        if (code == 0) {
            require(output);
            require(JUB_FreeMemory(output) == 0);
        } else {
            require(!output);
        }
        serial = nullptr;
        subject = nullptr;
        code = JUB_GPC_ParseCertificate(encoded.c_str(), &serial, &subject);
        if (serial) require(JUB_FreeMemory(serial) == 0);
        if (subject) require(JUB_FreeMemory(subject) == 0);
        output = nullptr;
        code = JUB_GPC_ParseAPDUResponse(encoded.c_str(), &status, &output);
        if (output) require(JUB_FreeMemory(output) == 0);
    }
    std::cout << "Native security regression tests + 5000 malformed-input cases passed\n";
}
