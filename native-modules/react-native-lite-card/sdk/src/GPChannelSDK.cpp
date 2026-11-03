// ABI implementation reconstructed from the UUID-matched vendor binary.
#include "GPChannelSDK.h"
#include "context/BaseContext.h"
#include "device/ApduBuilder.hpp"
#include "mSIGNA/stdutils/uchar_vector.h"
#include <algorithm>
#include <array>
#include <cstring>
#include <memory>
#include <mutex>
#include <set>
#include <string>

namespace {
std::mutex mutex;
std::unique_ptr<jub::context::BaseContext> context;
std::set<const char*> allocations;

bool decodeHex(const char* text, uchar_vector& result, size_t maxBytes = 65535) {
    if (!text) return false;
    const auto length = strnlen(text, maxBytes * 2 + 1);
    if (length > maxBytes * 2 || length % 2) return false;
    try { result = uchar_vector(std::string(text, length)); }
    catch (const std::invalid_argument&) { return false; }
    return true;
}
JUB_RV allocate(char** output, const std::string& value) {
    if (!output) return JUBR_MEMORY_NULL_PTR;
    auto memory = std::unique_ptr<char[]>(new char[value.size() + 1]());
    std::copy(value.begin(), value.end(), memory.get());
    allocations.insert(memory.get());
    *output = memory.release();
    return JUBR_OK;
}
JUB_RV configure(jub::JubApduBuiler& builder) {
    if (!context || !context->getSCP11cInstance().isOpen())
        return JUBR_CONTEXT_NOT_SATISFIED;
    builder.SetSCP03(&context->getSCP03Instance());
    return builder.SetSCP11(&context->getSCP11cInstance());
}
JUB_RV build(bool safe, JUB_ULONG cla, JUB_ULONG ins, JUB_ULONG p1,
             JUB_ULONG p2, const char* hex, char** output) {
    if (!output) return JUBR_MEMORY_NULL_PTR;
    *output = nullptr;
    jub::JubApduBuiler builder;
    if (safe) {
        auto result = configure(builder);
        if (result != JUBR_OK) return result;
    }
    uchar_vector data;
    if (!decodeHex(hex, data)) return JUBR_ARGUMENTS_BAD;
    // Encryption adds up to one AES block and an eight-byte MAC to Lc.
    if (safe && data.size() > 65511) return JUBR_ARGUMENTS_BAD;
    jub::APDU apdu(cla, ins, p1, p2, data.size(), data.data());
    uchar_vector encoded;
    auto result = safe ? builder.BuildSafeApdu(&apdu, encoded)
                       : builder.BuildApdu(&apdu, encoded);
    return result == JUBR_OK ? allocate(output, encoded.getHex()) : result;
}
JUB_RV parse(bool safe, const char* hex, JUB_UINT16* status, char** output) {
    if (!status || !output) return JUBR_MEMORY_NULL_PTR;
    *output = nullptr;
    jub::JubApduBuiler builder;
    if (safe) {
        auto result = configure(builder);
        if (result != JUBR_OK) return result;
    }
    uchar_vector response;
    if (!decodeHex(hex, response, 65537) || response.size() < 2)
        return JUBR_ARGUMENTS_BAD;
    if (safe && response.size() != 2 &&
        (response.size() < 26 || (response.size() - 10) % 16))
        return JUBR_ARGUMENTS_BAD;
    if (safe && response[response.size()-2] == 0x90 && response.back() == 0 && response.size() < 26)
        return JUBR_ARGUMENTS_BAD;
    std::array<unsigned char, 1024> buffer{};
    JUB_ULONG length = buffer.size();
    JUB_UINT16 word = 0;
    // Error-only safe responses contain no plaintext; the vendor left length unchanged.
#ifndef __ANDROID__
    if (safe && !(response[response.size()-2] == 0x90 && response.back() == 0))
        length = 0;
#endif
    auto result = safe
        ? builder.ParseSafeApduResp(response.data(), response.size(), buffer.data(), &length, word)
        : builder.ParseApduResp(response.data(), response.size(), buffer.data(), &length, word);
    if (result != JUBR_OK) return result;
    *status = word;
    return allocate(output, uchar_vector(buffer.begin(), buffer.begin() + length).getHex());
}
}

extern "C" {
JUB_RV JUB_FreeMemory(JUB_CHAR_CPTR memory) {
    std::lock_guard<std::mutex> lock(mutex);
    if (!memory) return JUBR_MEMORY_NULL_PTR;
    if (!allocations.erase(memory)) return JUBR_INVALID_MEMORY_PTR;
    delete[] memory;
    return JUBR_OK;
}
JUB_RV JUB_GPC_Finalize() {
    std::lock_guard<std::mutex> lock(mutex);
    if (context) context->clear();
    context.reset();
    return JUBR_OK;
}
JUB_RV JUB_GPC_Initialize(GPC_SCP11_SHAREDINFO& info, JUB_CHAR_CPTR certificate,
                         JUB_CHAR_CPTR privateKey) {
    std::lock_guard<std::mutex> lock(mutex);
    uchar_vector crt, key, id, usage, type, length, host, group;
    if (!decodeHex(certificate, crt, 1024) || crt.empty() ||
        !decodeHex(privateKey, key, 32) || key.size() != 32 ||
        !decodeHex(info.scpID, id, 2) || id.size() != 2 ||
        !decodeHex(info.keyUsage, usage, 1) || usage.size() != 1 ||
        !decodeHex(info.keyType, type, 1) || type.size() != 1 || type[0] != 0x88 ||
        !decodeHex(info.keyLength, length, 1) || length.size() != 1 || length[0] != 0x10 ||
        !decodeHex(info.hostID, host, 255) || host.empty() ||
        !decodeHex(info.cardGroupID, group, 255) || group.empty())
        return JUBR_ARGUMENTS_BAD;
    scp11_crt parsed(crt);
    if (!parsed.decode() || parsed.empty()) return JUBR_ARGUMENTS_BAD;
    scp11_sharedInfo shared(id, usage, type, length, host, group);
    context.reset(new jub::context::BaseContext(shared, parsed, key));
    return context->getSCP11cInstance().initialize() ? JUBR_OK : JUBR_ARGUMENTS_BAD;
}
JUB_RV JUB_GPC_BuildMutualAuthData(JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    if (!output) return JUBR_MEMORY_NULL_PTR;
    *output = nullptr;
    if (!context) return JUBR_CONTEXT_NOT_SATISFIED;
    return allocate(output, uchar_vector(context->getSCP11cInstance().getMutualAuthData()).getHex());
}
JUB_RV JUB_GPC_OpenSecureChannel(JUB_CHAR_CPTR hex) {
    std::lock_guard<std::mutex> lock(mutex);
    if (!context) return JUBR_CONTEXT_NOT_SATISFIED;
    uchar_vector response;
    if (!decodeHex(hex, response, 256)) return JUBR_ARGUMENTS_BAD;
    scp11_response_msg parsed;
    if (!parsed.decode(response) || parsed.sd_pk.value.size() != 65 ||
        parsed.receipt.value.size() != 16) return JUBR_ARGUMENTS_BAD;
    auto& scp = context->getSCP11cInstance();
    if (!scp.openSecureChannel(response)) return JUBR_ERROR;
    context->getSCP03Instance().macChaining(scp.getMacChain());
    return JUBR_OK;
}
JUB_RV JUB_GPC_BuildAPDU(JUB_ULONG cla, JUB_ULONG ins, JUB_ULONG p1,
                        JUB_ULONG p2, JUB_CHAR_CPTR data, JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    return build(false, cla, ins, p1, p2, data, output);
}
JUB_RV JUB_GPC_BuildSafeAPDU(JUB_ULONG cla, JUB_ULONG ins, JUB_ULONG p1,
                            JUB_ULONG p2, JUB_CHAR_CPTR data, JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    return build(true, cla, ins, p1, p2, data, output);
}
JUB_RV JUB_GPC_ParseAPDUResponse(JUB_CHAR_CPTR data, JUB_UINT16* status,
                                JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    return parse(false, data, status, output);
}
JUB_RV JUB_GPC_ParseSafeAPDUResponse(JUB_CHAR_CPTR data, JUB_UINT16* status,
                                    JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    return parse(true, data, status, output);
}
JUB_RV JUB_GPC_TLVDecode(JUB_CHAR_CPTR hex, JUB_ULONG* tag, JUB_CHAR_PTR_PTR output) {
    std::lock_guard<std::mutex> lock(mutex);
    if (!tag || !output) return JUBR_MEMORY_NULL_PTR;
    *output = nullptr;
    uchar_vector data;
    if (!decodeHex(hex, data, 65535) || data.size() < 2) return JUBR_ARGUMENTS_BAD;
    size_t pos = 1;
    JUB_ULONG decodedTag = data[0];
    if ((data[0] & 31) == 31) {
        if (data.size() < 3) return JUBR_ARGUMENTS_BAD;
        decodedTag = (decodedTag << 8) | data[pos++];
    }
    size_t length = data[pos++];
    if (decodedTag == 0x7f21 || decodedTag == 0xbf21) {
        if (length != 0x81 && length != 0x82) return JUBR_ARGUMENTS_BAD;
        size_t count = length & 127;
        if (pos + count > data.size()) return JUBR_ARGUMENTS_BAD;
        length = 0;
        while (count--) length = (length << 8) | data[pos++];
    }
    if (length > data.size() - pos) return JUBR_ARGUMENTS_BAD;
    switch (decodedTag) {
        case 0x42: case 0x53: case 0x73: case 0x86: case 0x93: case 0x95:
        case 0xb0: case 0xf0: case 0x5f20: case 0x5f24: case 0x5f25:
        case 0x5f37: case 0x5f49: case 0x7f21: case 0x7f49: case 0xbf21:
            break;
        default: return JUBR_ARGUMENTS_BAD;
    }
    *tag = decodedTag;
    return allocate(output, uchar_vector(data.begin() + pos, data.begin() + pos + length).getHex());
}
JUB_RV JUB_GPC_ParseCertificate(JUB_CHAR_CPTR hex, JUB_CHAR_PTR_PTR serial,
                               JUB_CHAR_PTR_PTR subject) {
    std::lock_guard<std::mutex> lock(mutex);
    if (!serial || !subject || serial == subject) return JUBR_MEMORY_NULL_PTR;
    *serial = *subject = nullptr;
    uchar_vector bytes;
    if (!decodeHex(hex, bytes, 1024) || bytes.empty()) return JUBR_ARGUMENTS_BAD;
    scp11_crt cert(bytes);
    if (!cert.decode() || cert.empty()) return JUBR_ARGUMENTS_BAD;
    std::string name(cert.serial.value.begin(), cert.serial.value.end());
    auto result = allocate(serial, name);
    return result == JUBR_OK ? allocate(subject, uchar_vector(cert.subject_id.value).getHex()) : result;
}
}
