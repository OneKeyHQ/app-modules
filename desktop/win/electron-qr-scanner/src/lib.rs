#![cfg(target_os = "windows")]

mod capture;
mod d3d11_preview;
mod frame_gate;
mod image_decode;

use std::sync::Arc;

use napi::bindgen_prelude::{AsyncTask, Buffer, Function};
use napi::threadsafe_function::{ThreadsafeFunction, ThreadsafeFunctionCallMode};
use napi::{Env, Error, Result, Status, Task};
use napi_derive::napi;

use crate::capture::{CameraDeviceInfo, CaptureController, CaptureEvent, SourceConfig};
use crate::image_decode::decode_image_file;

/// Hard ceiling on the caller's `maxPixels`.
const MAX_PIXELS_CEILING: u64 = 50_000_000;
const DEFAULT_MAX_PIXELS: u64 = 12_000_000;

pub struct DecodeImageTask {
    path: String,
    max_pixels: u64,
}

impl Task for DecodeImageTask {
    type Output = Option<String>;
    type JsValue = Option<String>;

    fn compute(&mut self) -> Result<Self::Output> {
        decode_image_file(&self.path, self.max_pixels)
            .map_err(|msg| Error::new(Status::GenericFailure, msg))
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output)
    }
}

/// Raw text of the first QR symbol in a local image, or null. `engines`
/// (diagnostics and tests) limits the engines: Windows supports `"zxing"`.
#[napi]
pub fn decode_image(
    path: String,
    max_pixels: Option<f64>,
    engines: Option<Vec<String>>,
) -> Result<AsyncTask<DecodeImageTask>> {
    if let Some(names) = engines {
        if names.is_empty() {
            return Err(Error::new(
                Status::InvalidArg,
                "decoder-unavailable: engines must not be empty",
            ));
        }
        for name in names {
            if name != "zxing" {
                return Err(Error::new(
                    Status::InvalidArg,
                    format!("decoder-unavailable: unknown engine: {name}"),
                ));
            }
        }
    }

    let max_pixels = match max_pixels {
        None => DEFAULT_MAX_PIXELS,
        Some(value) if value.is_finite() && value >= 1.0 => {
            (value.floor() as u64).min(MAX_PIXELS_CEILING)
        }
        Some(_) => {
            return Err(Error::new(
                Status::InvalidArg,
                "invalid-image: maxPixels must be a positive number",
            ));
        }
    };

    if path.is_empty() {
        return Err(Error::new(
            Status::InvalidArg,
            "invalid-image: path must be a non-empty string",
        ));
    }

    Ok(AsyncTask::new(DecodeImageTask { path, max_pixels }))
}

#[napi(object)]
pub struct JsCameraDevice {
    pub id: String,
    pub name: String,
    pub is_default: bool,
}

pub struct ListCamerasTask;

impl Task for ListCamerasTask {
    type Output = Vec<CameraDeviceInfo>;
    type JsValue = Vec<JsCameraDevice>;

    fn compute(&mut self) -> Result<Self::Output> {
        capture::list_cameras().map_err(|msg| Error::new(Status::GenericFailure, msg))
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output
            .into_iter()
            .map(|d| JsCameraDevice {
                id: d.id,
                name: d.name,
                is_default: d.is_default,
            })
            .collect())
    }
}

/// Enumerates Windows video capture devices (Media Foundation).
#[napi]
pub fn list_cameras() -> AsyncTask<ListCamerasTask> {
    AsyncTask::new(ListCamerasTask)
}

#[napi(object)]
pub struct JsCaptureEvent {
    pub kind: String,
    pub generation: i64,
    pub text: Option<String>,
    pub code: Option<String>,
    pub message: Option<String>,
    pub token: Option<i64>,
    pub nt_handle: Option<Buffer>,
    pub width: Option<u32>,
    pub height: Option<u32>,
}

fn to_js_event(event: CaptureEvent) -> JsCaptureEvent {
    match event {
        CaptureEvent::Ready { generation } => JsCaptureEvent {
            kind: "ready".to_owned(),
            generation,
            text: None,
            code: None,
            message: None,
            token: None,
            nt_handle: None,
            width: None,
            height: None,
        },
        CaptureEvent::Text { text, generation } => JsCaptureEvent {
            kind: "text".to_owned(),
            generation,
            text: Some(text),
            code: None,
            message: None,
            token: None,
            nt_handle: None,
            width: None,
            height: None,
        },
        CaptureEvent::Error {
            code,
            message,
            generation,
        } => JsCaptureEvent {
            kind: "error".to_owned(),
            generation,
            text: None,
            code: Some(code.to_owned()),
            message: Some(message),
            token: None,
            nt_handle: None,
            width: None,
            height: None,
        },
        CaptureEvent::Ended { generation } => JsCaptureEvent {
            kind: "ended".to_owned(),
            generation,
            text: None,
            code: None,
            message: None,
            token: None,
            nt_handle: None,
            width: None,
            height: None,
        },
        CaptureEvent::Frame {
            token,
            nt_handle,
            width,
            height,
            generation,
        } => JsCaptureEvent {
            kind: "frame".to_owned(),
            generation,
            text: None,
            code: None,
            message: None,
            token: Some(token),
            nt_handle: Some(Buffer::from(nt_handle)),
            width: Some(width),
            height: Some(height),
        },
    }
}

#[napi]
pub struct NativeCapture {
    controller: Option<Arc<CaptureController>>,
}

#[napi]
impl NativeCapture {
    #[napi(constructor)]
    pub fn new(
        source_kind: String,
        file_path: Option<String>,
        loop_file: Option<bool>,
        device_id: Option<String>,
        with_preview: bool,
        callback: Function<JsCaptureEvent, ()>,
    ) -> Result<Self> {
        let source = match source_kind.as_str() {
            "camera" => SourceConfig::Camera { device_id },
            "file" => {
                let path = file_path.unwrap_or_default();
                SourceConfig::File {
                    path,
                    loop_file: loop_file.unwrap_or(false),
                }
            }
            other => {
                return Err(Error::new(
                    Status::InvalidArg,
                    format!("decoder-unavailable: unknown source: {other}"),
                ));
            }
        };

        let tsfn: ThreadsafeFunction<JsCaptureEvent, (), JsCaptureEvent, Status, false> =
            callback.build_threadsafe_function().build()?;

        let controller = Arc::new(CaptureController::new(
            source,
            with_preview,
            Arc::new(move |event: CaptureEvent| {
                let js_event = to_js_event(event);
                let _ = tsfn.call(js_event, ThreadsafeFunctionCallMode::NonBlocking);
            }),
        ));

        Ok(Self {
            controller: Some(controller),
        })
    }

    #[napi]
    pub fn start(&self) -> Result<()> {
        let Some(ctrl) = self.controller.as_ref() else {
            return Err(Error::new(
                Status::GenericFailure,
                "decoder-unavailable: The capture was disposed",
            ));
        };
        ctrl.start()
            .map_err(|msg| Error::new(Status::GenericFailure, msg))
    }

    #[napi]
    pub fn stop(&self) {
        if let Some(ctrl) = self.controller.as_ref() {
            ctrl.stop();
        }
    }

    #[napi]
    pub fn set_device(&self, device_id: Option<String>) -> Result<()> {
        let Some(ctrl) = self.controller.as_ref() else {
            return Err(Error::new(
                Status::GenericFailure,
                "decoder-unavailable: The capture was disposed",
            ));
        };
        ctrl.set_device(device_id)
            .map_err(|msg| Error::new(Status::GenericFailure, msg))
    }

    #[napi]
    pub fn release_frame(&self, token: i64) {
        if let Some(ctrl) = self.controller.as_ref() {
            ctrl.release_frame(token);
        }
    }

    #[napi]
    pub fn lent_frames(&self) -> u32 {
        self.controller
            .as_ref()
            .map_or(0, |ctrl| ctrl.lent_frames() as u32)
    }

    #[napi]
    pub fn dispose(&mut self) {
        if let Some(ctrl) = self.controller.take() {
            ctrl.dispose();
        }
    }
}

impl Drop for NativeCapture {
    fn drop(&mut self) {
        self.dispose();
    }
}
