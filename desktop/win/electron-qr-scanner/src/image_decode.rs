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

/// Sources above this pixel count are refused before decoding.
pub const MAX_SOURCE_PIXELS: u64 = 200_000_000;
/// White border added by the padded pass, as a share of the longer side.
const PADDING_RATIO: f64 = 0.1;

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

/// Decodes one QR code from an RGBA buffer using ZXing-C++ (explicit inversion
/// is handled by the caller's `inverted` pass).
pub fn decode_qr_rgba(rgba: &[u8], width: i32, height: i32) -> Option<String> {
    if width <= 0 || height <= 0 || rgba.len() < (width as usize) * (height as usize) * 4 {
        return None;
    }
    let image =
        zxingcpp::ImageView::from_slice(rgba, width, height, zxingcpp::ImageFormat::RGBA).ok()?;
    let reader = zxingcpp::read()
        .formats([zxingcpp::BarcodeFormat::QRCode])
        .try_harder(true)
        .try_rotate(true)
        .try_invert(false)
        .max_number_of_symbols(1);
    let barcodes = reader.from(image).ok()?;
    barcodes
        .into_iter()
        .map(|b| b.text())
        .find(|t| !t.is_empty())
}

/// Decodes one QR code from a tightly packed 8-bit luminance buffer.
pub fn decode_qr_luma(luma: &[u8], width: i32, height: i32, try_invert: bool) -> Option<String> {
    if width <= 0 || height <= 0 || luma.len() < (width as usize) * (height as usize) {
        return None;
    }
    let image =
        zxingcpp::ImageView::from_slice(luma, width, height, zxingcpp::ImageFormat::Lum).ok()?;
    let reader = zxingcpp::read()
        .formats([zxingcpp::BarcodeFormat::QRCode])
        .try_harder(true)
        .try_rotate(true)
        .try_invert(try_invert)
        .max_number_of_symbols(1);
    let barcodes = reader.from(image).ok()?;
    barcodes
        .into_iter()
        .map(|b| b.text())
        .find(|t| !t.is_empty())
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
        let target_w = ((width as f64) * scale).floor().max(1.0) as u32;
        let target_h = ((height as f64) * scale).floor().max(1.0) as u32;
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
    let mut rgba = vec![0u8; buf_size];
    unsafe { converter.CopyPixels(std::ptr::null(), stride, &mut rgba) }
        .map_err(|_| "invalid-image: image pixels could not be decoded".to_owned())?;

    Ok((rgba, width, height))
}

fn pad_rgba_white(rgba: &[u8], width: usize, height: usize) -> (Vec<u8>, usize, usize) {
    let longer = width.max(height);
    let margin = ((longer as f64) * PADDING_RATIO).floor().max(16.0) as usize;
    let out_w = width + margin * 2;
    let out_h = height + margin * 2;
    let mut out = vec![255u8; out_w * out_h * 4];
    for y in 0..height {
        let src_row = &rgba[y * width * 4..(y + 1) * width * 4];
        let dst_start = ((y + margin) * out_w + margin) * 4;
        out[dst_start..dst_start + width * 4].copy_from_slice(src_row);
    }
    (out, out_w, out_h)
}

fn rgba_to_luma(rgba: &[u8], width: usize, height: usize, inverted: bool) -> Vec<u8> {
    let count = width * height;
    let mut out = Vec::with_capacity(count);
    for px in rgba.as_chunks::<4>().0.iter().take(count) {
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
        out.push(if inverted { 255 - luma } else { luma });
    }
    out
}

/// Runs the cross-platform 4-pass image decoding pipeline (`original`,
/// `padded`, `grayscale`, `inverted`) with ZXing-C++, stopping on first hit.
pub fn decode_image_file(path: &str, max_pixels: u64) -> Result<Option<String>, String> {
    let (rgba, width, height) = load_oriented_rgba(path, max_pixels)?;
    let w = width as usize;
    let h = height as usize;

    // Pass 1: original
    if let Some(text) = decode_qr_rgba(&rgba, width as i32, height as i32) {
        return Ok(Some(text));
    }

    // Pass 2: padded white quiet zone (handles edge-to-edge / no-margin codes)
    let (padded, pw, ph) = pad_rgba_white(&rgba, w, h);
    if let Some(text) = decode_qr_rgba(&padded, pw as i32, ph as i32) {
        return Ok(Some(text));
    }

    // Pass 3: grayscale
    let gray = rgba_to_luma(&rgba, w, h, false);
    if let Some(text) = decode_qr_luma(&gray, width as i32, height as i32, false) {
        return Ok(Some(text));
    }

    // Pass 4: inverted grayscale (handles light-on-dark QR codes)
    let inv = rgba_to_luma(&rgba, w, h, true);
    if let Some(text) = decode_qr_luma(&inv, width as i32, height as i32, false) {
        return Ok(Some(text));
    }

    Ok(None)
}
