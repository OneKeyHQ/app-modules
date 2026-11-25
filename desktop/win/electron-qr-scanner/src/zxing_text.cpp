#include "ReadBarcode.h"

#include <algorithm>
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

extern "C" void qr_decode_symbols(
    const uint8_t* pixels, int width, int height, bool rgba, bool try_invert, int max_symbols,
    void* context,
    void (*emit_symbol)(
        void*, const char*, size_t, double, double, double, double, double, double)) noexcept
{
    try {
        if (width <= 0 || height <= 0 || max_symbols <= 0)
            return;
        const auto image = ZXing::ImageView(
            pixels, width, height, rgba ? ZXing::ImageFormat::RGBA : ZXing::ImageFormat::Lum);
        const auto options = ZXing::ReaderOptions()
            .formats(ZXing::BarcodeFormat::QRCode)
            .tryHarder(true)
            .tryRotate(true)
            .tryInvert(try_invert)
            .maxNumberOfSymbols(max_symbols);
        const auto barcodes = ZXing::ReadBarcodes(image, options);
        const double inv_w = 1.0 / static_cast<double>(width);
        const double inv_h = 1.0 / static_cast<double>(height);
        for (const auto& barcode : barcodes) {
            if (!barcode.isValid())
                continue;
            const auto text = barcode.text(ZXing::TextMode::Plain);
            if (text.empty())
                continue;
            const auto pos = barcode.position();
            const double xs[4] = {
                static_cast<double>(pos.topLeft().x),
                static_cast<double>(pos.topRight().x),
                static_cast<double>(pos.bottomRight().x),
                static_cast<double>(pos.bottomLeft().x),
            };
            const double ys[4] = {
                static_cast<double>(pos.topLeft().y),
                static_cast<double>(pos.topRight().y),
                static_cast<double>(pos.bottomRight().y),
                static_cast<double>(pos.bottomLeft().y),
            };
            const double min_x = *std::min_element(xs, xs + 4);
            const double max_x = *std::max_element(xs, xs + 4);
            const double min_y = *std::min_element(ys, ys + 4);
            const double max_y = *std::max_element(ys, ys + 4);
            const double center_x = (xs[0] + xs[1] + xs[2] + xs[3]) * 0.25 * inv_w;
            const double center_y = (ys[0] + ys[1] + ys[2] + ys[3]) * 0.25 * inv_h;
            emit_symbol(
                context, text.data(), text.size(), center_x, center_y,
                min_x * inv_w, min_y * inv_h,
                std::max(0.0, (max_x - min_x) * inv_w),
                std::max(0.0, (max_y - min_y) * inv_h));
        }
    } catch (...) {
        // A failed ZXing read is a miss. C++ exceptions cannot cross into Rust.
    }
}
