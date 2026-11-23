#![cfg(target_os = "macos")]

use std::ffi::{c_char, c_void, CStr, CString};
use std::ptr;

use napi::bindgen_prelude::AsyncTask;
use napi::threadsafe_function::{ThreadsafeFunction, ThreadsafeFunctionCallMode};
use napi::{Env, Error, Result, Status, Task};
use napi_derive::napi;

/// Hard ceiling on the caller's `maxPixels`.
const MAX_PIXELS_CEILING: i64 = 50_000_000;
const DEFAULT_MAX_PIXELS: i64 = 12_000_000;

type EventCallback = extern "C" fn(*mut c_void, i32, *const c_char, *const c_char, i64);

extern "C" {
    fn oqs_decode_image(
        path: *const c_char,
        max_pixels: i64,
        out_text: *mut *mut c_char,
        out_error: *mut *mut c_char,
    ) -> i32;
    fn oqs_free_string(value: *mut c_char);
    fn oqs_capture_create(
        context: *mut c_void,
        callback: EventCallback,
        source_kind: i32,
        path: *const c_char,
        loop_file: bool,
    ) -> *mut c_void;
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

pub struct DecodeImageTask {
    path: CString,
    max_pixels: i64,
}

impl Task for DecodeImageTask {
    type Output = Option<String>;
    type JsValue = Option<String>;

    fn compute(&mut self) -> Result<Self::Output> {
        let mut text: *mut c_char = ptr::null_mut();
        let mut error: *mut c_char = ptr::null_mut();
        let status =
            unsafe { oqs_decode_image(self.path.as_ptr(), self.max_pixels, &mut text, &mut error) };
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

/// Raw text of the first QR symbol in a local image, or null.
#[napi]
pub fn decode_image(path: String, max_pixels: Option<f64>) -> Result<AsyncTask<DecodeImageTask>> {
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
    Ok(AsyncTask::new(DecodeImageTask { path, max_pixels }))
}

#[napi(object)]
pub struct CaptureEvent {
    /// `ready`, `text`, `error` or `ended`.
    pub kind: String,
    pub text: Option<String>,
    pub code: Option<String>,
    pub message: Option<String>,
    pub generation: f64,
}

type EventTsfn = ThreadsafeFunction<CaptureEvent, (), CaptureEvent, Status, false>;

fn event(kind: &str, generation: i64) -> CaptureEvent {
    CaptureEvent {
        kind: kind.into(),
        text: None,
        code: None,
        message: None,
        generation: generation as f64,
    }
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
