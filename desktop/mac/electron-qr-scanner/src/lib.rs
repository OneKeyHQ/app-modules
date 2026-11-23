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

type EventCallback = extern "C" fn(*mut c_void, i32, *const c_char, *const c_char, i64);
type FrameCallback = extern "C" fn(*mut c_void, i64, *mut c_void, i32, i32, i64);

extern "C" {
    fn oqs_decode_image(
        path: *const c_char,
        max_pixels: i64,
        engines: i32,
        out_text: *mut *mut c_char,
        out_error: *mut *mut c_char,
    ) -> i32;
    fn oqs_free_string(value: *mut c_char);
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
    fn oqs_capture_start(handle: *mut c_void);
    fn oqs_capture_stop(handle: *mut c_void);
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
/// tightly packed width * height * 4 buffer. Returns 1 and a string to free
/// with `oqs_zxing_free` when a QR code is found, 0 otherwise.
#[no_mangle]
pub extern "C" fn oqs_zxing_decode(
    rgba: *const u8,
    width: i32,
    height: i32,
    out_text: *mut *mut c_char,
) -> i32 {
    if rgba.is_null() || width <= 0 || height <= 0 || out_text.is_null() {
        return 0;
    }
    let length = width as usize * height as usize * 4;
    let pixels = unsafe { std::slice::from_raw_parts(rgba, length) };
    let Ok(image) = zxingcpp::ImageView::from_slice(pixels, width, height, zxingcpp::ImageFormat::RGBA)
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
    let Some(text) = barcodes.iter().map(|b| b.text()).find(|t| !t.is_empty()) else {
        return 0;
    };
    match CString::new(text) {
        Ok(text) => {
            unsafe { *out_text = text.into_raw() };
            1
        }
        Err(_) => 0,
    }
}

#[no_mangle]
pub extern "C" fn oqs_zxing_free(text: *mut c_char) {
    if !text.is_null() {
        drop(unsafe { CString::from_raw(text) });
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
        let mut text: *mut c_char = ptr::null_mut();
        let mut error: *mut c_char = ptr::null_mut();
        let status =
            unsafe {
            oqs_decode_image(self.path.as_ptr(), self.max_pixels, self.engines, &mut text, &mut error)
        };
        match status {
            1 => Ok(Some(unsafe { take_string(text) })),
            0 => Ok(None),
            // "<code>: <reason>"; index.cjs turns it into a coded Error.
            _ => Err(Error::new(Status::GenericFailure, unsafe { take_string(error) })),
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
                return Err(Error::new(Status::InvalidArg, "decoder-unavailable: no engine"));
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
    Ok(AsyncTask::new(DecodeImageTask { path, max_pixels, engines }))
}

#[napi(object)]
pub struct CaptureEvent {
    /// `ready`, `text`, `error`, `ended` or `frame`.
    pub kind: String,
    pub text: Option<String>,
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
    first: *const c_char,
    second: *const c_char,
    generation: i64,
) {
    if context.is_null() {
        return;
    }
    // Valid until oqs_capture_destroy returns; Swift never calls after that.
    let tsfn = unsafe { &*(context as *const EventTsfn) };
    let first = unsafe { CStr::from_ptr(first) }.to_string_lossy().into_owned();
    let second = unsafe { CStr::from_ptr(second) }.to_string_lossy().into_owned();
    let payload = match kind {
        0 => event("ready", generation),
        1 => CaptureEvent { text: Some(first), ..event("text", generation) },
        2 => CaptureEvent {
            code: Some(first),
            message: Some(second),
            ..event("error", generation)
        },
        _ => event("ended", generation),
    };
    tsfn.call(payload, ThreadsafeFunctionCallMode::NonBlocking);
}

#[napi]
pub struct NativeCapture {
    handle: *mut c_void,
    context: *mut EventTsfn,
}

#[napi]
impl NativeCapture {
    /// `source`: "camera" or "file" (with `path`).
    #[napi(constructor)]
    pub fn new(
        source: String,
        path: Option<String>,
        loop_file: Option<bool>,
        preview: Option<bool>,
        callback: EventTsfn,
    ) -> Result<Self> {
        let (kind, path) = match source.as_str() {
            "camera" => (0, None),
            "file" => {
                let path = path
                    .ok_or_else(|| Error::new(Status::InvalidArg, "A file source needs a path"))?;
                let path = CString::new(path)
                    .map_err(|_| Error::new(Status::InvalidArg, "path contains NUL"))?;
                (1, Some(path))
            }
            other => {
                return Err(Error::new(Status::InvalidArg, format!("Unknown source: {other}")))
            }
        };
        let context = Box::into_raw(Box::new(callback));
        let handle = unsafe {
            oqs_capture_create(
                context as *mut c_void,
                on_event,
                if preview.unwrap_or(false) { Some(on_frame) } else { None },
                kind,
                path.as_ref().map_or(ptr::null(), |p| p.as_ptr()),
                loop_file.unwrap_or(false),
            )
        };
        Ok(Self { handle, context })
    }

    #[napi]
    pub fn start(&self) -> Result<()> {
        self.live()?;
        unsafe { oqs_capture_start(self.handle) };
        Ok(())
    }

    #[napi]
    pub fn stop(&self) -> Result<()> {
        self.live()?;
        unsafe { oqs_capture_stop(self.handle) };
        Ok(())
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
            Err(Error::new(Status::GenericFailure, "The capture was disposed"))
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
