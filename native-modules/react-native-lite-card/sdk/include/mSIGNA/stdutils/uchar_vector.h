// Recovered subset used by GPChannel; hexadecimal data has no wallet semantics.
#pragma once
#include <cstddef>
#include <stdexcept>
#include <string>
#include <vector>

class uchar_vector : public std::vector<unsigned char> {
public:
    using std::vector<unsigned char>::vector;
    uchar_vector(const std::vector<unsigned char>& bytes)
        : std::vector<unsigned char>(bytes) {}
    explicit uchar_vector(const std::string& hex) {
        if (hex.size() % 2) throw std::invalid_argument("Odd hexadecimal length");
        reserve(hex.size() / 2);
        for (size_t i = 0; i < hex.size(); i += 2) {
            push_back(static_cast<unsigned char>((nibble(hex[i]) << 4) | nibble(hex[i + 1])));
        }
    }
    std::string getHex(bool upper = false) const {
        const char* digits = upper ? "0123456789ABCDEF" : "0123456789abcdef";
        std::string hex(size() * 2, '0');
        for (size_t i = 0; i < size(); ++i) {
            hex[i * 2] = digits[(*this)[i] >> 4];
            hex[i * 2 + 1] = digits[(*this)[i] & 15];
        }
        return hex;
    }
private:
    static unsigned char nibble(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        throw std::invalid_argument("Invalid hexadecimal character");
    }
};
