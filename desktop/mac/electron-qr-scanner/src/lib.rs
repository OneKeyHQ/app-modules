#![cfg(target_os = "macos")]

use std::ffi::{c_char, c_void, CStr, CString};
use std::ptr;

use napi::bindgen_prelude::{AsyncTask, Buffer};
use napi::threadsafe_function::{ThreadsafeFunction, ThreadsafeFunctionCallMode};
use napi::{Env, Error, Result, Status, Task};
use napi_derive::napi;

/// Hard ceiling on the caller's `maxPixels`.
const MAX_PIXELS_CEILING: i64 = 50_000_000;
const DEFAULT_MAX_PIXELS: i64 = 12_000_000;

type EventCallback = extern "C" fn(*mut c_void, i32, *const u8, usize, *const c_char, i64);
type FrameCallback = extern "C" fn(*mut c_void, i64, *mut c_void, i32, i32, i64);

extern "C" {
    fn oqs_decode_image(
        path: *const c_char,
        max_pixels: i64,
        engines: i32,
        out_text: *mut *mut u8,
        out_length: *mut usize,
        out_error: *mut *mut c_char,
    ) -> i32;
    fn oqs_decode_image_symbols(
        path: *const c_char,
        max_pixels: i64,
        engines: i32,
        out_json: *mut *mut u8,
        out_length: *mut usize,
        out_error: *mut *mut c_char,
    ) -> i32;
    fn oqs_free_bytes(value: *mut u8);
    fn oqs_free_string(value: *mut c_char);
    fn oqs_list_cameras() -> *mut c_char;
    fn oqs_capture_create(
        context: *mut c_void,
        callback: EventCallback,
        frame_callback: Option<FrameCallback>,
        source_kind: i32,
        path: *const c_char,
        loop_file: bool,
    ) -> *mut c_void;
    fn oqs_capture_release_frame(handle: *mut c_void, token: i64);
    fn oqs_capture_lent_frames(handle: *mut c_void) -> i32;
    fn oqs_capture_start(handle: *mut c_void) -> i64;
    fn oqs_capture_stop(handle: *mut c_void);
    fn oqs_capture_set_multi_symbol(handle: *mut c_void, enabled: bool);
    fn oqs_capture_set_paused(handle: *mut c_void, paused: bool);
    fn oqs_capture_focus(handle: *mut c_void, x: f64, y: f64) -> bool;
    fn oqs_capture_destroy(handle: *mut c_void);
}

/// Takes ownership of a string Swift allocated.
unsafe fn take_string(value: *mut c_char) -> String {
    let text = CStr::from_ptr(value).to_string_lossy().into_owned();
    oqs_free_string(value);
    text
}

/// Engine bitmask shared with swift/Bridge.swift.
const ENGINE_VISION: i32 = 1;
const ENGINE_ZXING: i32 = 2;

/// ZXing-C++ fallback, called from the Swift image pipeline. `rgba` is a
/// tightly packed width * height * 4 buffer. Returns 1 and JSON bytes to free
/// with `oqs_zxing_free` when a QR code is found, 0 otherwise.
#[no_mangle]
pub extern "C" fn oqs_zxing_decode(
    rgba: *const u8,
    width: i32,
    height: i32,
    out_text: *mut *mut u8,
    out_length: *mut usize,
) -> i32 {
    if rgba.is_null() || width <= 0 || height <= 0 || out_text.is_null() || out_length.is_null() {
        return 0;
    }
    let length = width as usize * height as usize * 4;
    let pixels = unsafe { std::slice::from_raw_parts(rgba, length) };
    let Ok(image) =
        zxingcpp::ImageView::from_slice(pixels, width, height, zxingcpp::ImageFormat::RGBA)
    else {
        return 0;
    };
    let reader = zxingcpp::read()
        .formats([zxingcpp::BarcodeFormat::QRCode])
        .try_harder(true)
        .try_rotate(true)
        // The pipeline has its own inverted pass.
        .try_invert(false)
        .max_number_of_symbols(1);
    let Ok(barcodes) = reader.from(image) else {
        return 0;
    };
    let Some(barcode) = barcodes.iter().find(|barcode| barcode.is_valid()) else {
        return 0;
    };
    // The pinned C API's text accessor is NUL-terminated. Its JSON metadata
    // escapes control characters while preserving ZXing's character decoding.
    let bytes = barcode
        .extra_with_key("ALL")
        .into_bytes()
        .into_boxed_slice();
    if bytes.is_empty() {
        return 0;
    }
    unsafe {
        *out_length = bytes.len();
        *out_text = Box::into_raw(bytes).cast::<u8>();
    }
    1
}

/// Multi-symbol ZXing-C++ fallback (up to 8 symbols) with normalized top-left
/// coordinates. Returns 1 and a UTF-8 JSON array to free with `oqs_zxing_free`.
#[no_mangle]
pub extern "C" fn oqs_zxing_decode_symbols(
    rgba: *const u8,
    width: i32,
    height: i32,
    out_json: *mut *mut u8,
    out_length: *mut usize,
) -> i32 {
    if rgba.is_null() || width <= 0 || height <= 0 || out_json.is_null() || out_length.is_null() {
        return 0;
    }
    let length = width as usize * height as usize * 4;
    let pixels = unsafe { std::slice::from_raw_parts(rgba, length) };
    let Ok(image) =
        zxingcpp::ImageView::from_slice(pixels, width, height, zxingcpp::ImageFormat::RGBA)
    else {
        return 0;
    };
    let reader = zxingcpp::read()
        .formats([zxingcpp::BarcodeFormat::QRCode])
        .try_harder(true)
        .try_rotate(true)
        .try_invert(false)
        .max_number_of_symbols(8);
    let Ok(barcodes) = reader.from(image) else {
        return 0;
    };
    let inv_w = 1.0 / (width as f64);
    let inv_h = 1.0 / (height as f64);
    let mut entries = Vec::new();
    for barcode in barcodes.iter().filter(|b| b.is_valid()) {
        let meta = barcode.extra_with_key("ALL");
        if meta.is_empty() {
            continue;
        }
        let pos = barcode.position();
        let xs = [
            pos.top_left.x as f64,
            pos.top_right.x as f64,
            pos.bottom_right.x as f64,
            pos.bottom_left.x as f64,
        ];
        let ys = [
            pos.top_left.y as f64,
            pos.top_right.y as f64,
            pos.bottom_right.y as f64,
            pos.bottom_left.y as f64,
        ];
        let min_x = xs.into_iter().fold(f64::INFINITY, f64::min);
        let max_x = xs.into_iter().fold(f64::NEG_INFINITY, f64::max);
        let min_y = ys.into_iter().fold(f64::INFINITY, f64::min);
        let max_y = ys.into_iter().fold(f64::NEG_INFINITY, f64::max);
        let cx = (xs[0] + xs[1] + xs[2] + xs[3]) * 0.25 * inv_w;
        let cy = (ys[0] + ys[1] + ys[2] + ys[3]) * 0.25 * inv_h;
        let bx = min_x * inv_w;
        let by = min_y * inv_h;
        let bw = ((max_x - min_x) * inv_w).max(0.0);
        let bh = ((max_y - min_y) * inv_h).max(0.0);
        entries.push(format!(
            r#"{{"meta":{meta},"cx":{cx},"cy":{cy},"x":{bx},"y":{by},"w":{bw},"h":{bh}}}"#
        ));
    }
    if entries.is_empty() {
        return 0;
    }
    let json = format!("[{}]", entries.join(","));
    let bytes = json.into_bytes().into_boxed_slice();
    unsafe {
        *out_length = bytes.len();
        *out_json = Box::into_raw(bytes).cast::<u8>();
    }
    1
}

#[no_mangle]
pub extern "C" fn oqs_zxing_free(text: *mut u8, length: usize) {
    if !text.is_null() {
        drop(unsafe { Box::from_raw(ptr::slice_from_raw_parts_mut(text, length)) });
    }
}

pub struct DecodeImageTask {
    path: CString,
    max_pixels: i64,
    engines: i32,
}

impl Task for DecodeImageTask {
    type Output = Option<String>;
    type JsValue = Option<String>;

    fn compute(&mut self) -> Result<Self::Output> {
        let mut text: *mut u8 = ptr::null_mut();
        let mut text_length = 0;
        let mut error: *mut c_char = ptr::null_mut();
        let status = unsafe {
            oqs_decode_image(
                self.path.as_ptr(),
                self.max_pixels,
                self.engines,
                &mut text,
                &mut text_length,
                &mut error,
            )
        };
        match status {
            1 => {
                let value = unsafe {
                    let value =
                        String::from_utf8_lossy(std::slice::from_raw_parts(text, text_length))
                            .into_owned();
                    oqs_free_bytes(text);
                    value
                };
                Ok(Some(value))
            }
            0 => Ok(None),
            // "<code>: <reason>"; index.cjs turns it into a coded Error.
            _ => Err(Error::new(Status::GenericFailure, unsafe {
                take_string(error)
            })),
        }
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output)
    }
}

/// Raw text of the first QR symbol in a local image, or null. `engines`
/// (diagnostics and tests) limits the engines: "vision" and/or "zxing".
#[napi]
pub fn decode_image(
    path: String,
    max_pixels: Option<f64>,
    engines: Option<Vec<String>>,
) -> Result<AsyncTask<DecodeImageTask>> {
    let engines = match engines {
        None => ENGINE_VISION | ENGINE_ZXING,
        Some(names) => {
            let mut mask = 0;
            for name in names {
                mask |= match name.as_str() {
                    "vision" => ENGINE_VISION,
                    "zxing" => ENGINE_ZXING,
                    other => {
                        return Err(Error::new(
                            Status::InvalidArg,
                            format!("decoder-unavailable: unknown engine {other}"),
                        ))
                    }
                };
            }
            if mask == 0 {
                return Err(Error::new(
                    Status::InvalidArg,
                    "decoder-unavailable: no engine",
                ));
            }
            mask
        }
    };
    let max_pixels = match max_pixels {
        None => DEFAULT_MAX_PIXELS,
        Some(value) if value.is_finite() && value >= 1.0 => (value as i64).min(MAX_PIXELS_CEILING),
        Some(_) => {
            return Err(Error::new(
                Status::InvalidArg,
                "invalid-image: maxPixels must be at least 1",
            ))
        }
    };
    let path = CString::new(path)
        .map_err(|_| Error::new(Status::InvalidArg, "invalid-image: path contains NUL"))?;
    Ok(AsyncTask::new(DecodeImageTask {
        path,
        max_pixels,
        engines,
    }))
}

pub struct DecodeImageSymbolsTask {
    path: CString,
    max_pixels: i64,
    engines: i32,
}

impl Task for DecodeImageSymbolsTask {
    type Output = String;
    type JsValue = String;

    fn compute(&mut self) -> Result<Self::Output> {
        let mut json: *mut u8 = ptr::null_mut();
        let mut json_length = 0;
        let mut error: *mut c_char = ptr::null_mut();
        let status = unsafe {
            oqs_decode_image_symbols(
                self.path.as_ptr(),
                self.max_pixels,
                self.engines,
                &mut json,
                &mut json_length,
                &mut error,
            )
        };
        match status {
            1 => {
                let value = unsafe {
                    let value =
                        String::from_utf8_lossy(std::slice::from_raw_parts(json, json_length))
                            .into_owned();
                    oqs_free_bytes(json);
                    value
                };
                Ok(value)
            }
            _ => Err(Error::new(Status::GenericFailure, unsafe {
                take_string(error)
            })),
        }
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output)
    }
}

/// JSON `{ width, height, symbols }` of all QR symbols in a local image.
#[napi]
pub fn decode_image_symbols(
    path: String,
    max_pixels: Option<f64>,
    engines: Option<Vec<String>>,
) -> Result<AsyncTask<DecodeImageSymbolsTask>> {
    let engines = match engines {
        None => ENGINE_VISION | ENGINE_ZXING,
        Some(names) => {
            let mut mask = 0;
            for name in names {
                mask |= match name.as_str() {
                    "vision" => ENGINE_VISION,
                    "zxing" => ENGINE_ZXING,
                    other => {
                        return Err(Error::new(
                            Status::InvalidArg,
                            format!("decoder-unavailable: unknown engine {other}"),
                        ))
                    }
                };
            }
            if mask == 0 {
                return Err(Error::new(
                    Status::InvalidArg,
                    "decoder-unavailable: no engine",
                ));
            }
            mask
        }
    };
    let max_pixels = match max_pixels {
        None => DEFAULT_MAX_PIXELS,
        Some(value) if value.is_finite() && value >= 1.0 => (value as i64).min(MAX_PIXELS_CEILING),
        Some(_) => {
            return Err(Error::new(
                Status::InvalidArg,
                "invalid-image: maxPixels must be at least 1",
            ))
        }
    };
    let path = CString::new(path)
        .map_err(|_| Error::new(Status::InvalidArg, "invalid-image: path contains NUL"))?;
    Ok(AsyncTask::new(DecodeImageSymbolsTask {
        path,
        max_pixels,
        engines,
    }))
}

#[napi(object)]
pub struct CaptureEvent {
    /// `ready`, `text`, `error`, `ended` or `frame`.
    pub kind: String,
    pub text: Option<String>,
    pub device_id: Option<String>,
    pub code: Option<String>,
    pub message: Option<String>,
    pub generation: f64,
    /// `frame`: pass to `releaseFrame` once the consumer is done with it.
    pub token: Option<f64>,
    /// `frame`: the IOSurfaceRef as the 8-byte buffer Electron's
    /// `sharedTexture` handle expects. Valid until the token is released.
    pub io_surface: Option<Buffer>,
    pub width: Option<u32>,
    pub height: Option<u32>,
}

type EventTsfn = ThreadsafeFunction<CaptureEvent, (), CaptureEvent, Status, false>;

fn event(kind: &str, generation: i64) -> CaptureEvent {
    CaptureEvent {
        kind: kind.into(),
        text: None,
        device_id: None,
        code: None,
        message: None,
        generation: generation as f64,
        token: None,
        io_surface: None,
        width: None,
        height: None,
    }
}

extern "C" fn on_frame(
    context: *mut c_void,
    token: i64,
    surface: *mut c_void,
    width: i32,
    height: i32,
    generation: i64,
) {
    if context.is_null() {
        return;
    }
    let tsfn = unsafe { &*(context as *const EventTsfn) };
    let payload = CaptureEvent {
        token: Some(token as f64),
        io_surface: Some(Buffer::from((surface as usize).to_ne_bytes().to_vec())),
        width: Some(width as u32),
        height: Some(height as u32),
        ..event("frame", generation)
    };
    tsfn.call(payload, ThreadsafeFunctionCallMode::NonBlocking);
}

extern "C" fn on_event(
    context: *mut c_void,
    kind: i32,
    first: *const u8,
    first_length: usize,
    second: *const c_char,
    generation: i64,
) {
    if context.is_null() {
        return;
    }
    // Valid until oqs_capture_destroy returns; Swift never calls after that.
    let tsfn = unsafe { &*(context as *const EventTsfn) };
    let first = if first_length == 0 {
        String::new()
    } else {
        String::from_utf8_lossy(unsafe { std::slice::from_raw_parts(first, first_length) })
            .into_owned()
    };
    let second = unsafe { CStr::from_ptr(second) }
        .to_string_lossy()
        .into_owned();
    let payload = match kind {
        0 => CaptureEvent {
            device_id: (!first.is_empty()).then_some(first),
            ..event("ready", generation)
        },
        1 => CaptureEvent {
            text: Some(first),
            ..event("text", generation)
        },
        2 => CaptureEvent {
            code: Some(first),
            message: Some(second),
            ..event("error", generation)
        },
        4 => CaptureEvent {
            text: Some(first),
            ..event("symbols", generation)
        },
        _ => event("ended", generation),
    };
    tsfn.call(payload, ThreadsafeFunctionCallMode::NonBlocking);
}

/// JSON array of `{ id, position, label }`; `index.cjs` parses it.
#[napi]
pub fn list_cameras() -> String {
    unsafe {
        let raw = oqs_list_cameras();
        if raw.is_null() {
            return "[]".to_string();
        }
        let json = CStr::from_ptr(raw).to_string_lossy().into_owned();
        oqs_free_string(raw);
        json
    }
}

#[napi]
pub struct NativeCapture {
    handle: *mut c_void,
    context: *mut EventTsfn,
}

#[napi]
impl NativeCapture {
    /// `source`: "camera" (`path`: optional device id) or "file" (with `path`).
    #[napi(constructor)]
    pub fn new(
        source: String,
        path: Option<String>,
        loop_file: Option<bool>,
        preview: Option<bool>,
        callback: EventTsfn,
    ) -> Result<Self> {
        let (kind, path) = match source.as_str() {
            "camera" => (
                0,
                path.map(CString::new)
                    .transpose()
                    .map_err(|_| Error::new(Status::InvalidArg, "deviceId contains NUL"))?,
            ),
            "file" => {
                let path = path
                    .ok_or_else(|| Error::new(Status::InvalidArg, "A file source needs a path"))?;
                let path = CString::new(path)
                    .map_err(|_| Error::new(Status::InvalidArg, "path contains NUL"))?;
                (1, Some(path))
            }
            other => {
                return Err(Error::new(
                    Status::InvalidArg,
                    format!("Unknown source: {other}"),
                ))
            }
        };
        let context = Box::into_raw(Box::new(callback));
        let handle = unsafe {
            oqs_capture_create(
                context as *mut c_void,
                on_event,
                if preview.unwrap_or(false) {
                    Some(on_frame)
                } else {
                    None
                },
                kind,
                path.as_ref().map_or(ptr::null(), |p| p.as_ptr()),
                loop_file.unwrap_or(false),
            )
        };
        Ok(Self { handle, context })
    }

    #[napi]
    pub fn start(&self) -> Result<f64> {
        self.live()?;
        Ok(unsafe { oqs_capture_start(self.handle) } as f64)
    }

    #[napi]
    pub fn stop(&self) -> Result<()> {
        self.live()?;
        unsafe { oqs_capture_stop(self.handle) };
        Ok(())
    }

    #[napi]
    pub fn set_multi_symbol(&self, enabled: bool) -> Result<()> {
        self.live()?;
        unsafe { oqs_capture_set_multi_symbol(self.handle, enabled) };
        Ok(())
    }

    #[napi]
    pub fn set_paused(&self, paused: bool) -> Result<()> {
        self.live()?;
        unsafe { oqs_capture_set_paused(self.handle, paused) };
        Ok(())
    }

    #[napi]
    pub fn focus(&self, x: Option<f64>, y: Option<f64>) -> bool {
        if self.handle.is_null() {
            false
        } else {
            unsafe { oqs_capture_focus(self.handle, x.unwrap_or(0.5), y.unwrap_or(0.5)) }
        }
    }

    /// Returns a preview frame's slot. Unknown or repeated tokens are ignored.
    #[napi]
    pub fn release_frame(&self, token: f64) {
        if !self.handle.is_null() {
            unsafe { oqs_capture_release_frame(self.handle, token as i64) };
        }
    }

    /// Preview frames currently lent out (at most 3).
    #[napi]
    pub fn lent_frames(&self) -> u32 {
        if self.handle.is_null() {
            0
        } else {
            unsafe { oqs_capture_lent_frames(self.handle) as u32 }
        }
    }

    /// Idempotent. No event is delivered after this returns.
    #[napi]
    pub fn dispose(&mut self) {
        self.release();
    }

    fn live(&self) -> Result<()> {
        if self.handle.is_null() {
            Err(Error::new(
                Status::GenericFailure,
                "The capture was disposed",
            ))
        } else {
            Ok(())
        }
    }

    fn release(&mut self) {
        if !self.handle.is_null() {
            unsafe { oqs_capture_destroy(self.handle) };
            self.handle = ptr::null_mut();
        }
        if !self.context.is_null() {
            drop(unsafe { Box::from_raw(self.context) });
            self.context = ptr::null_mut();
        }
    }
}

impl Drop for NativeCapture {
    fn drop(&mut self) {
        self.release();
    }
}
