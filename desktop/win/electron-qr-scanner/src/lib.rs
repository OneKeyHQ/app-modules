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

use crate::capture::{
    CameraDeviceInfo, CaptureController, CaptureEvent, CaptureReadyInfo, CaptureStatsSnapshot,
    PreferredResolution, SourceConfig,
};
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
pub struct JsCaptureReadyInfo {
    pub device_id: Option<String>,
    pub width: u32,
    pub height: u32,
    pub fps: f64,
    pub subtype: String,
    pub camera_count: u32,
    pub camera_index: Option<u32>,
    pub is_default_camera: bool,
    pub focus_supported: bool,
    pub auto_focus_supported: bool,
    pub exposure_supported: bool,
}

impl From<CaptureReadyInfo> for JsCaptureReadyInfo {
    fn from(info: CaptureReadyInfo) -> Self {
        Self {
            width: info.width,
            height: info.height,
            fps: info.fps,
            subtype: info.subtype,
            camera_count: info.camera_count,
            camera_index: info.camera_index,
            device_id: info.device_id,
            is_default_camera: info.is_default_camera,
            focus_supported: info.focus_supported,
            auto_focus_supported: info.auto_focus_supported,
            exposure_supported: info.exposure_supported,
        }
    }
}

#[napi(object)]
pub struct JsCaptureStats {
    pub frames_captured: f64,
    pub preview_frames_emitted: f64,
    pub preview_frames_skipped: f64,
    pub decode_attempts: f64,
    pub decode_hits: f64,
    pub max_lent: u32,
}

impl From<CaptureStatsSnapshot> for JsCaptureStats {
    fn from(stats: CaptureStatsSnapshot) -> Self {
        Self {
            frames_captured: stats.frames_captured as f64,
            preview_frames_emitted: stats.preview_frames_emitted as f64,
            preview_frames_skipped: stats.preview_frames_skipped as f64,
            decode_attempts: stats.decode_attempts as f64,
            decode_hits: stats.decode_hits as f64,
            max_lent: stats.max_lent,
        }
    }
}

#[napi(object)]
pub struct JsCaptureEvent {
    pub kind: String,
    pub generation: i64,
    pub info: Option<JsCaptureReadyInfo>,
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
        CaptureEvent::Ready { generation, info } => JsCaptureEvent {
            kind: "ready".to_owned(),
            generation,
            info: Some(info.into()),
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
            info: None,
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
            info: None,
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
            info: None,
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
            info: None,
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

#[napi(object)]
pub struct NativeCaptureOptions {
    pub source_kind: String,
    pub file_path: Option<String>,
    pub loop_file: Option<bool>,
    pub device_id: Option<String>,
    pub preferred_resolution: Option<String>,
    pub lock_focus_on_detect: Option<bool>,
    pub with_preview: bool,
}

#[napi]
pub struct NativeCapture {
    controller: Option<Arc<CaptureController>>,
}

#[napi]
impl NativeCapture {
    #[napi(constructor)]
    pub fn new(
        options: NativeCaptureOptions,
        callback: Function<JsCaptureEvent, ()>,
    ) -> Result<Self> {
        let source = match options.source_kind.as_str() {
            "camera" => {
                let preferred_resolution = match options.preferred_resolution.as_deref() {
                    None | Some("720p") => PreferredResolution::P720,
                    Some("1080p") => PreferredResolution::P1080,
                    Some(other) => {
                        return Err(Error::new(
                            Status::InvalidArg,
                            format!(
                                "decoder-unavailable: unsupported preferredResolution: {other}"
                            ),
                        ));
                    }
                };
                SourceConfig::Camera {
                    device_id: options.device_id,
                    preferred_resolution,
                    lock_focus_on_detect: options.lock_focus_on_detect.unwrap_or(false),
                }
            }
            "file" => SourceConfig::File {
                path: options.file_path.unwrap_or_default(),
                loop_file: options.loop_file.unwrap_or(false),
            },
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
            options.with_preview,
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
    pub fn stats(&self) -> JsCaptureStats {
        self.controller
            .as_ref()
            .map_or_else(CaptureStatsSnapshot::default, |ctrl| ctrl.stats())
            .into()
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
