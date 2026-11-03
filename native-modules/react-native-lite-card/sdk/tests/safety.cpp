#include "GPChannelSDK.h"
#include <cstdlib>
#include <iostream>
#include <random>
#include <string>

static void require(bool condition) {
    if (!condition) { std::cerr << "Safety contract failed\n"; std::abort(); }
}
static std::string hex(const std::string& data) {
    const char* digits = "0123456789abcdef";
    std::string result;
    for (unsigned char byte : data) {
        result += digits[byte >> 4]; result += digits[byte & 15];
    }
    return result;
}
int main() {
    require(JUB_GPC_Finalize() == 0);
    require(JUB_GPC_Finalize() == 0);
    require(JUB_FreeMemory(nullptr) == JUBR_MEMORY_NULL_PTR);
    require(JUB_GPC_BuildAPDU(0,0,0,0,"",nullptr) == JUBR_MEMORY_NULL_PTR);
    char* output = nullptr;
    require(JUB_GPC_BuildMutualAuthData(&output) == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_OpenSecureChannel("") == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_BuildSafeAPDU(0,0,0,0,"",&output) == JUBR_CONTEXT_NOT_SATISFIED);
    require(JUB_GPC_BuildAPDU(0,0,0,0,"zz",&output) == JUBR_ARGUMENTS_BAD);
    JUB_UINT16 status = 0;
    require(JUB_GPC_ParseAPDUResponse("9000",&status,&output) == 0);
    require(status == 0x9000 && output && !*output);
    require(JUB_FreeMemory(output) == 0);
    require(JUB_FreeMemory(output) == JUBR_INVALID_MEMORY_PTR);
    std::mt19937 random(20261103);
    const unsigned char prefixes[] = {0x42,0x53,0x73,0x86,0x93,0x95,0xb0,0xf0,0x5f,0x7f,0xbf};
    for (size_t i = 0; i < 5000; ++i) {
        std::string data(random() % 256, '\0');
        for (char& byte : data) byte = static_cast<char>(random());
        if (!data.empty()) data[0] = prefixes[i % sizeof(prefixes)];
        auto encoded = hex(data);
        JUB_ULONG tag = 0;
        output = nullptr;
        auto code = JUB_GPC_TLVDecode(encoded.c_str(),&tag,&output);
        if (code == 0) { require(output); require(JUB_FreeMemory(output) == 0); }
        else require(!output);
        char* serial = nullptr;
        char* subject = nullptr;
        code = JUB_GPC_ParseCertificate(encoded.c_str(),&serial,&subject);
        if (serial) require(JUB_FreeMemory(serial) == 0);
        if (subject) require(JUB_FreeMemory(subject) == 0);
        output = nullptr;
        code = JUB_GPC_ParseAPDUResponse(encoded.c_str(),&status,&output);
        if (output) require(JUB_FreeMemory(output) == 0);
    }
    std::cout << "5000 malformed-input cases and lifecycle/allocation checks passed\n";
}
