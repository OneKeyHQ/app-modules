#include "ReadBarcode.h"

#include <cstddef>
#include <cstdint>

extern "C" void qr_decode_text(
    const uint8_t* pixels, int width, int height, bool rgba, bool try_invert,
    void* context, void (*emit_text)(void*, const char*, size_t)) noexcept
{
    try {
        const auto image = ZXing::ImageView(
            pixels, width, height, rgba ? ZXing::ImageFormat::RGBA : ZXing::ImageFormat::Lum);
        const auto options = ZXing::ReaderOptions()
            .formats(ZXing::BarcodeFormat::QRCode)
            .tryHarder(true)
            .tryRotate(true)
            .tryInvert(try_invert)
            .maxNumberOfSymbols(1);
        const auto barcode = ZXing::ReadBarcode(image, options);
        if (barcode.isValid()) {
            const auto text = barcode.text(ZXing::TextMode::Plain);
            if (!text.empty())
                emit_text(context, text.data(), text.size());
        }
    } catch (...) {
        // A failed ZXing read is a miss. C++ exceptions cannot cross into Rust.
    }
}
