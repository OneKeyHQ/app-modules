use std::path::Path;

use windows::core::{Interface, HSTRING, PCWSTR};
use windows::Win32::Foundation::{E_INVALIDARG, GENERIC_READ, RPC_E_CHANGED_MODE};
use windows::Win32::Graphics::Imaging::{
    CLSID_WICImagingFactory, GUID_WICPixelFormat32bppRGBA, IWICBitmapDecoder,
    IWICBitmapFlipRotator, IWICBitmapFrameDecode, IWICBitmapScaler, IWICBitmapSource,
    IWICFormatConverter, IWICImagingFactory, IWICMetadataQueryReader, WICBitmapDitherTypeNone,
    WICBitmapInterpolationModeFant, WICBitmapPaletteTypeCustom, WICBitmapTransformFlipHorizontal,
    WICBitmapTransformFlipVertical, WICBitmapTransformOptions, WICBitmapTransformRotate0,
    WICBitmapTransformRotate180, WICBitmapTransformRotate270, WICBitmapTransformRotate90,
    WICDecodeMetadataCacheOnDemand,
};
use windows::Win32::System::Com::StructuredStorage::PROPVARIANT;
use windows::Win32::System::Com::{
    CoCreateInstance, CoInitializeEx, CoUninitialize, CLSCTX_INPROC_SERVER, COINIT_MULTITHREADED,
};

use crate::zxing_text::{decode_qr_symbols, decode_qr_text, DecodedSymbol};

/// Sources above this pixel count are refused before decoding.
pub const MAX_SOURCE_PIXELS: u64 = 200_000_000;
/// White border added by the padded pass, as a share of the longer side.
const PADDING_DIVISOR: usize = 10;

struct ComGuard {
    should_uninit: bool,
}

impl ComGuard {
    fn init_mta() -> Result<Self, String> {
        let hr = unsafe { CoInitializeEx(None, COINIT_MULTITHREADED) };
        if hr.is_ok() {
            Ok(Self {
                should_uninit: true,
            })
        } else if hr == RPC_E_CHANGED_MODE {
            // Thread already initialized with another COM apartment; do not uninit.
            Ok(Self {
                should_uninit: false,
            })
        } else {
            Err(format!(
                "decoder-unavailable: CoInitializeEx failed ({hr:?})"
            ))
        }
    }
}

impl Drop for ComGuard {
    fn drop(&mut self) {
        if self.should_uninit {
            unsafe { CoUninitialize() };
        }
    }
}

/// Decodes one QR code from a tightly packed 8-bit luminance buffer.
pub fn decode_qr_luma(luma: &[u8], width: i32, height: i32, try_invert: bool) -> Option<String> {
    decode_qr_text(luma, width, height, false, try_invert)
}

/// Decodes up to `max_symbols` QR codes with normalized top-left coordinates.
pub fn decode_qr_luma_symbols(
    luma: &[u8],
    width: i32,
    height: i32,
    try_invert: bool,
    max_symbols: i32,
) -> Vec<DecodedSymbol> {
    decode_qr_symbols(luma, width, height, false, try_invert, max_symbols)
}

pub struct ImageSymbolsResult {
    pub width: u32,
    pub height: u32,
    pub symbols: Vec<DecodedSymbol>,
}

fn read_exif_orientation(frame: &IWICBitmapFrameDecode) -> u16 {
    let reader: IWICMetadataQueryReader = match unsafe { frame.GetMetadataQueryReader() } {
        Ok(r) => r,
        Err(_) => return 1,
    };
    for query in ["/app1/ifd/{ushort=274}", "/ifd/{ushort=274}"] {
        let wide = HSTRING::from(query);
        let mut prop = PROPVARIANT::default();
        if unsafe { reader.GetMetadataByName(PCWSTR(wide.as_ptr()), &mut prop) }.is_ok() {
            if let Ok(val) = u16::try_from(&prop) {
                if (1..=8).contains(&val) {
                    return val;
                }
            }
        }
    }
    1
}

fn exif_to_wic_transform(orientation: u16) -> WICBitmapTransformOptions {
    match orientation {
        2 => WICBitmapTransformFlipHorizontal,
        3 => WICBitmapTransformRotate180,
        4 => WICBitmapTransformFlipVertical,
        5 => WICBitmapTransformOptions(
            WICBitmapTransformRotate90.0 | WICBitmapTransformFlipHorizontal.0,
        ),
        6 => WICBitmapTransformRotate90,
        7 => WICBitmapTransformOptions(
            WICBitmapTransformRotate270.0 | WICBitmapTransformFlipHorizontal.0,
        ),
        8 => WICBitmapTransformRotate270,
        _ => WICBitmapTransformRotate0,
    }
}

/// Loads a local image via WIC, applies EXIF orientation, enforces pixel
/// limits, and returns tightly packed RGBA bytes + `(width, height)`.
fn load_oriented_rgba(path: &str, max_pixels: u64) -> Result<(Vec<u8>, u32, u32), String> {
    if path.is_empty() || !Path::new(path).exists() {
        return Err("invalid-image: unreadable or missing image file".to_owned());
    }
    let _com = ComGuard::init_mta()?;
    let factory: IWICImagingFactory =
        unsafe { CoCreateInstance(&CLSID_WICImagingFactory, None, CLSCTX_INPROC_SERVER) }
            .map_err(|e| format!("decoder-unavailable: WIC factory init failed ({e:?})"))?;

    let wide_path = HSTRING::from(path);
    let decoder: IWICBitmapDecoder = unsafe {
        factory.CreateDecoderFromFilename(
            PCWSTR(wide_path.as_ptr()),
            None,
            GENERIC_READ,
            WICDecodeMetadataCacheOnDemand,
        )
    }
    .map_err(|_| "invalid-image: unreadable or unsupported image".to_owned())?;

    let frame: IWICBitmapFrameDecode = unsafe { decoder.GetFrame(0) }
        .map_err(|_| "invalid-image: image has no readable frame".to_owned())?;

    let mut width = 0u32;
    let mut height = 0u32;
    unsafe { frame.GetSize(&mut width, &mut height) }
        .map_err(|_| "invalid-image: missing image dimensions".to_owned())?;
    if width == 0 || height == 0 {
        return Err("invalid-image: missing image dimensions".to_owned());
    }

    let source_pixels = (width as u64).saturating_mul(height as u64);
    if source_pixels > MAX_SOURCE_PIXELS {
        return Err(format!("resource-limit: image has {width}x{height} pixels"));
    }

    let orientation = read_exif_orientation(&frame);
    let mut source: IWICBitmapSource = frame
        .cast()
        .map_err(|_| "invalid-image: bitmap cast failed".to_owned())?;

    if orientation != 1 {
        let rotator: IWICBitmapFlipRotator = unsafe { factory.CreateBitmapFlipRotator() }
            .map_err(|e| format!("decoder-unavailable: WIC rotator failed ({e:?})"))?;
        unsafe { rotator.Initialize(&source, exif_to_wic_transform(orientation)) }
            .map_err(|_| "invalid-image: failed to apply EXIF orientation".to_owned())?;
        source = rotator
            .cast()
            .map_err(|_| "invalid-image: rotator cast failed".to_owned())?;
        unsafe { source.GetSize(&mut width, &mut height) }
            .map_err(|_| "invalid-image: failed to read rotated dimensions".to_owned())?;
    }

    let oriented_pixels = (width as u64).saturating_mul(height as u64);
    if oriented_pixels > max_pixels {
        let scale = (max_pixels as f64 / oriented_pixels as f64).sqrt();
        // Rounding either axis up to one pixel can exceed the budget on thin images.
        let target_w = (((width as f64) * scale).floor().max(1.0) as u64).min(max_pixels) as u32;
        let target_h = (((height as f64) * scale).floor().max(1.0) as u64)
            .min(max_pixels / u64::from(target_w)) as u32;
        let scaler: IWICBitmapScaler = unsafe { factory.CreateBitmapScaler() }
            .map_err(|e| format!("decoder-unavailable: WIC scaler failed ({e:?})"))?;
        unsafe { scaler.Initialize(&source, target_w, target_h, WICBitmapInterpolationModeFant) }
            .map_err(|_| "invalid-image: failed to downscale image".to_owned())?;
        source = scaler
            .cast()
            .map_err(|_| "invalid-image: scaler cast failed".to_owned())?;
        width = target_w;
        height = target_h;
    }

    let converter: IWICFormatConverter = unsafe { factory.CreateFormatConverter() }
        .map_err(|e| format!("decoder-unavailable: WIC converter failed ({e:?})"))?;
    unsafe {
        converter.Initialize(
            &source,
            &GUID_WICPixelFormat32bppRGBA,
            WICBitmapDitherTypeNone,
            None,
            0.0,
            WICBitmapPaletteTypeCustom,
        )
    }
    .map_err(|e| {
        if e.code() == E_INVALIDARG {
            "invalid-image: unsupported pixel format".to_owned()
        } else {
            "invalid-image: image could not be converted to RGBA".to_owned()
        }
    })?;

    let stride = width
        .checked_mul(4)
        .ok_or_else(|| "resource-limit: image stride overflow".to_owned())?;
    let buf_size = (stride as usize)
        .checked_mul(height as usize)
        .ok_or_else(|| "resource-limit: image buffer overflow".to_owned())?;
    let mut rgba = reserve_image_buffer(buf_size)?;
    rgba.resize(buf_size, 0);
    unsafe { converter.CopyPixels(std::ptr::null(), stride, &mut rgba) }
        .map_err(|_| "invalid-image: image pixels could not be decoded".to_owned())?;

    Ok((rgba, width, height))
}

fn reserve_image_buffer(len: usize) -> Result<Vec<u8>, String> {
    let mut buffer = Vec::new();
    buffer
        .try_reserve_exact(len)
        .map_err(|_| "resource-limit: image buffer allocation failed".to_owned())?;
    Ok(buffer)
}

fn padded_layout(
    width: usize,
    height: usize,
    max_pixels: u64,
) -> Option<(usize, usize, usize, usize)> {
    let margin = (width.max(height) / PADDING_DIVISOR).max(16);
    let border = margin.checked_mul(2)?;
    let out_w = width.checked_add(border)?;
    let out_h = height.checked_add(border)?;
    let pixels = out_w.checked_mul(out_h)?;
    if pixels as u64 > max_pixels {
        return None;
    }
    Some((margin, out_w, out_h, pixels.checked_mul(4)?))
}

fn rgba_to_luma(rgba: &[u8]) -> Result<Vec<u8>, String> {
    let mut out = reserve_image_buffer(rgba.len() / 4)?;
    for px in rgba.as_chunks::<4>().0 {
        let a = px[3] as u32;
        // Composite over white so transparent PNG quiet zones stay light.
        let r = if a == 255 {
            px[0] as u32
        } else {
            (px[0] as u32 * a + 255 * (255 - a)) / 255
        };
        let g = if a == 255 {
            px[1] as u32
        } else {
            (px[1] as u32 * a + 255 * (255 - a)) / 255
        };
        let b = if a == 255 {
            px[2] as u32
        } else {
            (px[2] as u32 * a + 255 * (255 - a)) / 255
        };
        let luma = ((77 * r + 150 * g + 29 * b) >> 8) as u8;
        out.push(luma);
    }
    Ok(out)
}

/// Runs the cross-platform 4-pass image decoding pipeline (`original`,
/// `padded`, `grayscale`, `inverted`) with ZXing-C++, stopping on first hit.
pub fn decode_image_file(path: &str, max_pixels: u64) -> Result<Option<String>, String> {
    let (rgba, width, height) = load_oriented_rgba(path, max_pixels)?;
    let w = width as usize;
    let h = height as usize;

    // Pass 1: original
    if let Some(text) = decode_qr_text(&rgba, width as i32, height as i32, true, false) {
        return Ok(Some(text));
    }

    // Pass 2: padded white quiet zone (handles edge-to-edge / no-margin codes)
    if let Some((margin, pw, ph, bytes)) = padded_layout(w, h, max_pixels) {
        let mut padded = reserve_image_buffer(bytes)?;
        padded.resize(bytes, 255);
        for (y, row) in rgba.chunks_exact(w * 4).enumerate() {
            let start = ((y + margin) * pw + margin) * 4;
            padded[start..start + row.len()].copy_from_slice(row);
        }
        if let Some(text) = decode_qr_text(&padded, pw as i32, ph as i32, true, false) {
            return Ok(Some(text));
        }
    }

    // Pass 3: grayscale
    let mut gray = rgba_to_luma(&rgba)?;
    drop(rgba);
    if let Some(text) = decode_qr_luma(&gray, width as i32, height as i32, false) {
        return Ok(Some(text));
    }

    // Pass 4: inverted grayscale (handles light-on-dark QR codes)
    for pixel in &mut gray {
        *pixel = 255 - *pixel;
    }
    if let Some(text) = decode_qr_luma(&gray, width as i32, height as i32, false) {
        return Ok(Some(text));
    }

    Ok(None)
}

fn unpad_symbol(
    sym: DecodedSymbol,
    orig_w: usize,
    orig_h: usize,
    margin: usize,
    padded_w: usize,
    padded_h: usize,
) -> DecodedSymbol {
    let ow = orig_w as f64;
    let oh = orig_h as f64;
    let pw = padded_w as f64;
    let ph = padded_h as f64;
    let m = margin as f64;
    let map_x = |x: f64| (x * pw - m) / ow;
    let map_y = |y: f64| (y * ph - m) / oh;
    DecodedSymbol {
        text: sym.text,
        center_x: map_x(sym.center_x),
        center_y: map_y(sym.center_y),
        bounds_x: map_x(sym.bounds_x),
        bounds_y: map_y(sym.bounds_y),
        bounds_width: (sym.bounds_width * pw) / ow,
        bounds_height: (sym.bounds_height * ph) / oh,
    }
}

/// Runs the 4-pass image decoding pipeline returning all QR symbols (up to 8)
/// with normalized top-left coordinates in EXIF-oriented image space.
pub fn decode_image_file_symbols(
    path: &str,
    max_pixels: u64,
) -> Result<ImageSymbolsResult, String> {
    let (rgba, width, height) = load_oriented_rgba(path, max_pixels)?;
    let w = width as usize;
    let h = height as usize;

    // Pass 1: original
    let symbols = decode_qr_symbols(&rgba, width as i32, height as i32, true, false, 8);
    if !symbols.is_empty() {
        return Ok(ImageSymbolsResult {
            width,
            height,
            symbols,
        });
    }

    // Pass 2: padded white quiet zone
    if let Some((margin, pw, ph, bytes)) = padded_layout(w, h, max_pixels) {
        let mut padded = reserve_image_buffer(bytes)?;
        padded.resize(bytes, 255);
        for (y, row) in rgba.chunks_exact(w * 4).enumerate() {
            let start = ((y + margin) * pw + margin) * 4;
            padded[start..start + row.len()].copy_from_slice(row);
        }
        let symbols = decode_qr_symbols(&padded, pw as i32, ph as i32, true, false, 8);
        if !symbols.is_empty() {
            let mapped = symbols
                .into_iter()
                .map(|s| unpad_symbol(s, w, h, margin, pw, ph))
                .collect();
            return Ok(ImageSymbolsResult {
                width,
                height,
                symbols: mapped,
            });
        }
    }

    // Pass 3: grayscale
    let mut gray = rgba_to_luma(&rgba)?;
    drop(rgba);
    let symbols = decode_qr_luma_symbols(&gray, width as i32, height as i32, false, 8);
    if !symbols.is_empty() {
        return Ok(ImageSymbolsResult {
            width,
            height,
            symbols,
        });
    }

    // Pass 4: inverted grayscale
    for pixel in &mut gray {
        *pixel = 255 - *pixel;
    }
    let symbols = decode_qr_luma_symbols(&gray, width as i32, height as i32, false, 8);
    Ok(ImageSymbolsResult {
        width,
        height,
        symbols,
    })
}

#[cfg(test)]
mod tests {
    use super::padded_layout;

    #[test]
    fn padded_pass_respects_the_pixel_budget() {
        let budget = 12_000_000;
        assert_eq!(padded_layout(1, 1_000_000, budget), None);
        assert_eq!(padded_layout(519, 23_094, budget), None);
        assert_eq!(padded_layout(usize::MAX, 1, budget), None);
        assert_eq!(padded_layout(64, 64, 9_215), None);
        assert_eq!(padded_layout(64, 64, 9_216), Some((16, 96, 96, 36_864)));
    }
}
