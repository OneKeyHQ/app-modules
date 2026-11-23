// C functions implemented in Rust (src/lib.rs) and linked into the same
// dylib; imported into Swift with -import-objc-header.
#include <stdint.h>

int32_t oqs_zxing_decode(const uint8_t *rgba, int32_t width, int32_t height, char **out_text);
void oqs_zxing_free(char *text);
