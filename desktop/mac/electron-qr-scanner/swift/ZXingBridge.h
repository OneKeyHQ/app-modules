// C functions implemented in Rust (src/lib.rs) and linked into the same
// dylib; imported into Swift with -import-objc-header.
#include <stdint.h>
#include <stddef.h>

int32_t oqs_zxing_decode(const uint8_t *rgba, int32_t width, int32_t height, uint8_t **out_text, size_t *out_length);
void oqs_zxing_free(uint8_t *text, size_t length);
