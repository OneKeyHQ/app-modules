use std::path::Path;
use std::sync::atomic::{AtomicBool, AtomicI64, Ordering};
use std::sync::{Arc, Condvar, Mutex};
use std::thread::{self, JoinHandle};
use std::time::{Duration, Instant};

use windows::core::{implement, Interface, GUID, HRESULT, HSTRING, PCWSTR};
use windows::Win32::Foundation::{E_ACCESSDENIED, RPC_E_CHANGED_MODE};
use windows::Win32::Media::DirectShow::{
    CameraControl_Exposure, CameraControl_Flags_Auto, CameraControl_Flags_Manual,
    CameraControl_Focus, IAMCameraControl,
};
use windows::Win32::Media::MediaFoundation::{
    IMF2DBuffer, IMFActivate, IMFAttributes, IMFMediaBuffer, IMFMediaSource, IMFMediaType,
    IMFSample, IMFSourceReader, IMFSourceReaderCallback, IMFSourceReaderCallback_Impl,
    MFCreateAttributes, MFCreateMediaType, MFCreateSourceReaderFromMediaSource,
    MFCreateSourceReaderFromURL, MFEnumDeviceSources, MFMediaType_Video, MFShutdown, MFStartup,
    MFVideoFormat_I420, MFVideoFormat_MJPG, MFVideoFormat_NV12, MFVideoFormat_RGB24,
    MFVideoFormat_RGB32, MFVideoFormat_YUY2, MFSTARTUP_LITE, MF_DEVSOURCE_ATTRIBUTE_FRIENDLY_NAME,
    MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE, MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_GUID,
    MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK, MF_MT_DEFAULT_STRIDE,
    MF_MT_FRAME_RATE, MF_MT_FRAME_SIZE, MF_MT_MAJOR_TYPE, MF_MT_SUBTYPE,
    MF_SOURCE_READERF_CURRENTMEDIATYPECHANGED, MF_SOURCE_READERF_ENDOFSTREAM,
    MF_SOURCE_READER_ALL_STREAMS, MF_SOURCE_READER_ASYNC_CALLBACK,
    MF_SOURCE_READER_ENABLE_VIDEO_PROCESSING, MF_SOURCE_READER_FIRST_VIDEO_STREAM, MF_VERSION,
};
use windows::Win32::System::Com::StructuredStorage::PROPVARIANT;
use windows::Win32::System::Com::{
    CoInitializeEx, CoTaskMemFree, CoUninitialize, COINIT_MULTITHREADED,
};

use crate::d3d11_preview::PreviewPool;
use crate::frame_gate::FrameTextGate;
use crate::image_decode::decode_qr_luma;

/// Duration without QR hits before a locked manual focus is released back to Auto.
const FOCUS_UNLOCK_TIMEOUT: Duration = Duration::from_millis(1500);

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum PreferredResolution {
    P720,
    P1080,
}

#[derive(Clone, Debug)]
pub struct CameraDeviceInfo {
    pub id: String,
    pub name: String,
    pub is_default: bool,
}

#[derive(Clone, Debug)]
pub struct CaptureReadyInfo {
    pub width: u32,
    pub height: u32,
    pub fps: f64,
    pub subtype: String,
    pub focus_supported: bool,
    pub auto_focus_supported: bool,
    pub exposure_supported: bool,
}

#[derive(Clone, Debug)]
pub enum SourceConfig {
    Camera {
        device_id: Option<String>,
        preferred_resolution: PreferredResolution,
        lock_focus_on_detect: bool,
    },
    File {
        path: String,
        loop_file: bool,
    },
}

pub enum CaptureEvent {
    Ready {
        generation: i64,
        info: CaptureReadyInfo,
    },
    Text {
        text: String,
        generation: i64,
    },
    Error {
        code: &'static str,
        message: String,
        generation: i64,
    },
    Ended {
        generation: i64,
    },
    Frame {
        token: i64,
        nt_handle: Vec<u8>,
        width: u32,
        height: u32,
        generation: i64,
    },
}

type EventEmitter = Arc<dyn Fn(CaptureEvent) + Send + Sync>;

struct ComMtaGuard {
    should_uninit: bool,
}

impl ComMtaGuard {
    fn init() -> Result<Self, String> {
        let hr = unsafe { CoInitializeEx(None, COINIT_MULTITHREADED) };
        if hr.is_ok() {
            Ok(Self {
                should_uninit: true,
            })
        } else if hr == RPC_E_CHANGED_MODE {
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

impl Drop for ComMtaGuard {
    fn drop(&mut self) {
        if self.should_uninit {
            unsafe { CoUninitialize() };
        }
    }
}

struct MfGuard {
    _com: ComMtaGuard,
    mf_started: bool,
}

impl MfGuard {
    fn init() -> Result<Self, String> {
        let com = ComMtaGuard::init()?;
        if let Err(err) = unsafe { MFStartup(MF_VERSION, MFSTARTUP_LITE) } {
            return Err(format!("decoder-unavailable: MFStartup failed ({err:?})"));
        }
        Ok(Self {
            _com: com,
            mf_started: true,
        })
    }
}

impl Drop for MfGuard {
    fn drop(&mut self) {
        if self.mf_started {
            unsafe {
                let _ = MFShutdown();
            }
        }
    }
}

fn read_allocated_string(attrs: &IMFAttributes, key: &GUID) -> Option<String> {
    let mut ptr = windows::core::PWSTR::null();
    let mut len = 0u32;
    unsafe { attrs.GetAllocatedString(key, &mut ptr, &mut len) }.ok()?;
    if ptr.0.is_null() {
        return None;
    }
    let slice = unsafe { std::slice::from_raw_parts(ptr.0, len as usize) };
    let value = String::from_utf16_lossy(slice);
    unsafe { CoTaskMemFree(Some(ptr.0 as *const std::ffi::c_void)) };
    Some(value)
}

fn enum_video_activates() -> Result<Vec<IMFActivate>, String> {
    let mut attrs: Option<IMFAttributes> = None;
    unsafe { MFCreateAttributes(&mut attrs, 1) }
        .map_err(|e| format!("decoder-unavailable: MFCreateAttributes failed ({e:?})"))?;
    let attrs = attrs.ok_or_else(|| "decoder-unavailable: null attributes".to_owned())?;
    unsafe {
        attrs.SetGUID(
            &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE,
            &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_GUID,
        )
    }
    .map_err(|e| format!("decoder-unavailable: SetGUID failed ({e:?})"))?;

    let mut raw_ptr: *mut Option<IMFActivate> = std::ptr::null_mut();
    let mut count = 0u32;
    let res = unsafe { MFEnumDeviceSources(&attrs, &mut raw_ptr, &mut count) };
    if let Err(err) = res {
        return Err(format!(
            "decoder-unavailable: MFEnumDeviceSources failed ({err:?})"
        ));
    }
    if raw_ptr.is_null() || count == 0 {
        if !raw_ptr.is_null() {
            unsafe { CoTaskMemFree(Some(raw_ptr as *const std::ffi::c_void)) };
        }
        return Ok(Vec::new());
    }

    let mut activates = Vec::with_capacity(count as usize);
    unsafe {
        let slice = std::slice::from_raw_parts_mut(raw_ptr, count as usize);
        for item in slice.iter_mut() {
            if let Some(activate) = item.take() {
                activates.push(activate);
            }
        }
        CoTaskMemFree(Some(raw_ptr as *const std::ffi::c_void));
    }
    Ok(activates)
}

/// Enumerates Windows video capture devices (Media Foundation).
pub fn list_cameras() -> Result<Vec<CameraDeviceInfo>, String> {
    let _mf = MfGuard::init()?;
    let activates = enum_video_activates()?;
    let mut devices = Vec::with_capacity(activates.len());
    for (idx, activate) in activates.iter().enumerate() {
        let attrs: IMFAttributes = activate
            .cast()
            .map_err(|e| format!("decoder-unavailable: IMFActivate cast failed ({e:?})"))?;
        let id = read_allocated_string(
            &attrs,
            &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK,
        )
        .unwrap_or_default();
        let name = read_allocated_string(&attrs, &MF_DEVSOURCE_ATTRIBUTE_FRIENDLY_NAME)
            .unwrap_or_else(|| format!("Camera {}", idx + 1));
        if !id.is_empty() {
            devices.push(CameraDeviceInfo {
                id,
                name,
                is_default: devices.is_empty(),
            });
        }
    }
    Ok(devices)
}

fn unpack_u64_pair(value: u64) -> (u32, u32) {
    ((value >> 32) as u32, (value & 0xFFFF_FFFF) as u32)
}

fn pack_u64_pair(hi: u32, lo: u32) -> u64 {
    ((hi as u64) << 32) | (lo as u64)
}

fn subtype_name(guid: GUID) -> &'static str {
    if guid == MFVideoFormat_NV12 {
        "NV12"
    } else if guid == MFVideoFormat_YUY2 {
        "YUY2"
    } else if guid == MFVideoFormat_MJPG {
        "MJPG"
    } else if guid == MFVideoFormat_RGB32 {
        "RGB32"
    } else if guid == MFVideoFormat_RGB24 {
        "RGB24"
    } else if guid == MFVideoFormat_I420 {
        "I420"
    } else {
        "unknown"
    }
}

#[derive(Clone, Copy, Debug)]
struct FrameLayout {
    width: u32,
    height: u32,
    default_stride: i32,
    fps: f64,
    subtype: &'static str,
}

fn read_fps(mt: &IMFMediaType) -> f64 {
    unsafe { mt.GetUINT64(&MF_MT_FRAME_RATE) }
        .map(|r| {
            let (num, den) = unpack_u64_pair(r);
            if den == 0 {
                0.0
            } else {
                num as f64 / den as f64
            }
        })
        .unwrap_or(0.0)
}

fn query_frame_layout(
    reader: &IMFSourceReader,
    native_subtype: Option<&'static str>,
) -> Result<FrameLayout, String> {
    let mt: IMFMediaType =
        unsafe { reader.GetCurrentMediaType(MF_SOURCE_READER_FIRST_VIDEO_STREAM.0 as u32) }
            .map_err(|e| format!("decoder-unavailable: GetCurrentMediaType failed ({e:?})"))?;

    let size_u64 = unsafe { mt.GetUINT64(&MF_MT_FRAME_SIZE) }
        .map_err(|e| format!("decoder-unavailable: missing MF_MT_FRAME_SIZE ({e:?})"))?;
    let (width, height) = unpack_u64_pair(size_u64);
    if width == 0 || height == 0 {
        return Err("decoder-unavailable: invalid frame dimensions".to_owned());
    }
    let default_stride = unsafe { mt.GetUINT32(&MF_MT_DEFAULT_STRIDE) }
        .map(|v| v as i32)
        .unwrap_or((width as i32) * 4);
    let fps = read_fps(&mt);
    let subtype = native_subtype.unwrap_or_else(|| {
        unsafe { mt.GetGUID(&MF_MT_SUBTYPE) }
            .map(subtype_name)
            .unwrap_or("unknown")
    });
    Ok(FrameLayout {
        width,
        height,
        default_stride,
        fps,
        subtype,
    })
}

fn score_native_mode(
    w: u32,
    h: u32,
    fps: f64,
    subtype: GUID,
    preferred: PreferredResolution,
) -> i64 {
    let mut score: i64 = 0;
    let (target_w, target_h) = match preferred {
        PreferredResolution::P720 => (1280i64, 720i64),
        PreferredResolution::P1080 => (1920i64, 1080i64),
    };
    if w as i64 == target_w && h as i64 == target_h {
        score += 10_000;
    } else if (w == 1280 && h == 720) || (w == 1920 && h == 1080) {
        score += 7_000;
    } else if w >= 640 && h >= 480 {
        score += 5_000 - ((w as i64 - target_w).abs() + (h as i64 - target_h).abs());
    } else {
        score += 1_000;
    }

    // High frame rate is essential for ~100 ms animated BC-UR frames; penalize
    // low-fps modes strongly so 1080p@5fps loses to 720p@30fps.
    if fps >= 25.0 {
        score += 4_000;
    } else if fps >= 15.0 {
        score += 1_500;
    } else {
        score -= 6_000;
    }

    // Prefer uncompressed NV12/YUY2 over lossy MJPG at equal fps to avoid JPEG
    // ringing artifacts on dense QR modules.
    if subtype == MFVideoFormat_NV12
        || subtype == MFVideoFormat_YUY2
        || subtype == MFVideoFormat_I420
    {
        score += 1_000;
    } else if subtype == MFVideoFormat_MJPG {
        score += 300;
    }
    score
}

fn configure_camera_media_type(
    reader: &IMFSourceReader,
    preferred: PreferredResolution,
) -> Result<FrameLayout, String> {
    let mut best_native: Option<(i64, u32, u32, &'static str, IMFMediaType)> = None;
    let stream_idx = MF_SOURCE_READER_FIRST_VIDEO_STREAM.0 as u32;

    for idx in 0u32..128 {
        let Ok(mt) = (unsafe { reader.GetNativeMediaType(stream_idx, idx) }) else {
            break;
        };
        let Ok(size_u64) = (unsafe { mt.GetUINT64(&MF_MT_FRAME_SIZE) }) else {
            continue;
        };
        let (w, h) = unpack_u64_pair(size_u64);
        if w < 320 || h < 240 {
            continue;
        }
        let fps = read_fps(&mt);
        let subtype = unsafe { mt.GetGUID(&MF_MT_SUBTYPE) }.unwrap_or_default();
        let score = score_native_mode(w, h, fps, subtype, preferred);
        if best_native.as_ref().is_none_or(|b| score > b.0) {
            best_native = Some((score, w, h, subtype_name(subtype), mt));
        }
    }

    if let Some((_, w, h, native_sub, native_mt)) = best_native {
        let _ = unsafe { reader.SetCurrentMediaType(stream_idx, None, &native_mt) };
        let rgb_mt: IMFMediaType = unsafe { MFCreateMediaType() }
            .map_err(|e| format!("decoder-unavailable: MFCreateMediaType failed ({e:?})"))?;
        let _ = unsafe { rgb_mt.SetGUID(&MF_MT_MAJOR_TYPE, &MFMediaType_Video) };
        let _ = unsafe { rgb_mt.SetGUID(&MF_MT_SUBTYPE, &MFVideoFormat_RGB32) };
        let _ = unsafe { rgb_mt.SetUINT64(&MF_MT_FRAME_SIZE, pack_u64_pair(w, h)) };
        if unsafe { reader.SetCurrentMediaType(stream_idx, None, &rgb_mt) }.is_ok() {
            return query_frame_layout(reader, Some(native_sub));
        }
    }

    // Fallback: ask MF Video Processor for RGB32 at the camera's default resolution.
    let rgb_mt: IMFMediaType = unsafe { MFCreateMediaType() }
        .map_err(|e| format!("decoder-unavailable: MFCreateMediaType failed ({e:?})"))?;
    unsafe { rgb_mt.SetGUID(&MF_MT_MAJOR_TYPE, &MFMediaType_Video) }
        .map_err(|e| format!("decoder-unavailable: SetGUID major failed ({e:?})"))?;
    unsafe { rgb_mt.SetGUID(&MF_MT_SUBTYPE, &MFVideoFormat_RGB32) }
        .map_err(|e| format!("decoder-unavailable: SetGUID subtype failed ({e:?})"))?;
    unsafe { reader.SetCurrentMediaType(stream_idx, None, &rgb_mt) }
        .map_err(|e| map_mf_error_string(e.code(), "SetCurrentMediaType(RGB32)"))?;

    query_frame_layout(reader, None)
}

#[derive(Clone, Copy, Debug, Default)]
struct CameraHardwareCaps {
    focus_supported: bool,
    auto_focus_supported: bool,
    manual_focus_supported: bool,
    default_focus: i32,
    exposure_supported: bool,
}

struct FocusControlHandle {
    control: IAMCameraControl,
    generation: i64,
    lock_on_detect: bool,
    auto_focus_supported: bool,
    manual_focus_supported: bool,
    default_focus: i32,
    locked: bool,
    last_detect_at: Option<Instant>,
}

// Accessed exclusively behind a Mutex on COM MTA threads.
unsafe impl Send for FocusControlHandle {}

impl FocusControlHandle {
    fn probe_and_init(
        media_source: &IMFMediaSource,
        generation: i64,
        lock_on_detect: bool,
    ) -> (CameraHardwareCaps, Option<Self>) {
        let Ok(control) = media_source.cast::<IAMCameraControl>() else {
            return (CameraHardwareCaps::default(), None);
        };

        let mut caps = CameraHardwareCaps::default();
        let (mut min_v, mut max_v, mut step_v, mut def_v, mut flags_v) = (0, 0, 0, 0, 0);
        if unsafe {
            control.GetRange(
                CameraControl_Focus.0,
                &mut min_v,
                &mut max_v,
                &mut step_v,
                &mut def_v,
                &mut flags_v,
            )
        }
        .is_ok()
        {
            caps.focus_supported = true;
            caps.auto_focus_supported = (flags_v & CameraControl_Flags_Auto.0) != 0;
            caps.manual_focus_supported = (flags_v & CameraControl_Flags_Manual.0) != 0;
            caps.default_focus = def_v;
            // Ensure stale manual focus left by another app is reset to Auto.
            if caps.auto_focus_supported {
                let _ = unsafe {
                    control.Set(CameraControl_Focus.0, def_v, CameraControl_Flags_Auto.0)
                };
            }
        }

        let (mut emin, mut emax, mut estep, mut edef, mut eflags) = (0, 0, 0, 0, 0);
        if unsafe {
            control.GetRange(
                CameraControl_Exposure.0,
                &mut emin,
                &mut emax,
                &mut estep,
                &mut edef,
                &mut eflags,
            )
        }
        .is_ok()
        {
            caps.exposure_supported = true;
        }

        let handle = if caps.auto_focus_supported && caps.manual_focus_supported {
            Some(Self {
                control,
                generation,
                lock_on_detect,
                auto_focus_supported: caps.auto_focus_supported,
                manual_focus_supported: caps.manual_focus_supported,
                default_focus: caps.default_focus,
                locked: false,
                last_detect_at: None,
            })
        } else {
            None
        };
        (caps, handle)
    }

    /// Locks focus at the current lens position on QR hit to prevent AF hunting
    /// during animated BC-UR playback.
    fn on_qr_detected(&mut self, generation: i64, now: Instant) {
        if !self.lock_on_detect
            || self.generation != generation
            || !self.manual_focus_supported
            || !self.auto_focus_supported
        {
            return;
        }
        self.last_detect_at = Some(now);
        if self.locked {
            return;
        }
        let mut current_val = self.default_focus;
        let mut current_flags = 0;
        if unsafe {
            self.control
                .Get(CameraControl_Focus.0, &mut current_val, &mut current_flags)
        }
        .is_ok()
            && unsafe {
                self.control.Set(
                    CameraControl_Focus.0,
                    current_val,
                    CameraControl_Flags_Manual.0,
                )
            }
            .is_ok()
        {
            self.locked = true;
        }
    }

    /// Unlocks focus back to Auto if no QR has been seen for `FOCUS_UNLOCK_TIMEOUT`.
    fn on_frame_without_qr(&mut self, generation: i64, now: Instant) {
        if !self.locked || self.generation != generation {
            return;
        }
        if self
            .last_detect_at
            .is_some_and(|t| now.saturating_duration_since(t) >= FOCUS_UNLOCK_TIMEOUT)
        {
            self.restore_auto();
        }
    }

    fn restore_auto(&mut self) {
        if self.locked && self.auto_focus_supported {
            let _ = unsafe {
                self.control.Set(
                    CameraControl_Focus.0,
                    self.default_focus,
                    CameraControl_Flags_Auto.0,
                )
            };
        }
        self.locked = false;
        self.last_detect_at = None;
    }
}

fn map_mf_error(hr: HRESULT) -> &'static str {
    const ERROR_SHARING_VIOLATION_HR: HRESULT = HRESULT(0x8007_0020u32 as i32);
    const ERROR_DEVICE_IN_USE_HR: HRESULT = HRESULT(0x8007_0964u32 as i32);
    const ERROR_GEN_FAILURE_HR: HRESULT = HRESULT(0x8007_001Fu32 as i32);
    const MF_E_VIDEO_RECORDING_DEVICE_INVALIDATED_HR: HRESULT = HRESULT(0xC00D_3EA2u32 as i32);
    const MF_E_HW_MFT_FAILED_START_STREAMING_HR: HRESULT = HRESULT(0xC00D_3704u32 as i32);
    const MF_E_NOT_AVAILABLE_HR: HRESULT = HRESULT(0xC00D_36D3u32 as i32);

    if hr == E_ACCESSDENIED {
        "permission-denied"
    } else if hr == ERROR_SHARING_VIOLATION_HR
        || hr == ERROR_DEVICE_IN_USE_HR
        || hr == ERROR_GEN_FAILURE_HR
        || hr == MF_E_VIDEO_RECORDING_DEVICE_INVALIDATED_HR
        || hr == MF_E_HW_MFT_FAILED_START_STREAMING_HR
        || hr == MF_E_NOT_AVAILABLE_HR
    {
        "camera-busy"
    } else {
        "decoder-unavailable"
    }
}

fn map_mf_error_string(hr: HRESULT, context: &str) -> String {
    format!("{}: {context} failed ({hr:?})", map_mf_error(hr))
}

/// Extracts top-down 8-bit luminance (always) and top-down opaque BGRA (only
/// when `need_bgra` is true) from an `MFVideoFormat_RGB32` sample.
fn extract_rgb32_frame(
    sample: &IMFSample,
    layout: FrameLayout,
    need_bgra: bool,
) -> Option<(Vec<u8>, Option<Vec<u8>>)> {
    let buffer: IMFMediaBuffer = unsafe { sample.ConvertToContiguousBuffer() }.ok()?;
    let w = layout.width as usize;
    let h = layout.height as usize;
    let row_bytes = w * 4;
    let mut luma = vec![0u8; w * h];
    let mut bgra = if need_bgra {
        Some(vec![0u8; w * h * 4])
    } else {
        None
    };

    if let Ok(buf2d) = buffer.cast::<IMF2DBuffer>() {
        let mut scanline0: *mut u8 = std::ptr::null_mut();
        let mut pitch: i32 = 0;
        if unsafe { buf2d.Lock2D(&mut scanline0, &mut pitch) }.is_ok() {
            if !scanline0.is_null() && pitch.unsigned_abs() as usize >= row_bytes {
                // Per Microsoft IMF2DBuffer::Lock2D docs, scanline0 always points to
                // the top row (y = 0) even when pitch is negative.
                for y in 0..h {
                    let src_row = unsafe {
                        std::slice::from_raw_parts(
                            scanline0.offset((y as isize) * (pitch as isize)),
                            row_bytes,
                        )
                    };
                    convert_bgrx_row(
                        src_row,
                        &mut luma[y * w..(y + 1) * w],
                        bgra.as_mut()
                            .map(|b| &mut b[y * row_bytes..(y + 1) * row_bytes]),
                    );
                }
            }
            unsafe {
                let _ = buf2d.Unlock2D();
            }
            return Some((luma, bgra));
        }
    }

    let mut data_ptr: *mut u8 = std::ptr::null_mut();
    let mut current_len = 0u32;
    unsafe { buffer.Lock(&mut data_ptr, None, Some(&mut current_len)) }.ok()?;
    let abs_stride = layout.default_stride.unsigned_abs() as usize;
    let stride = if abs_stride >= row_bytes {
        abs_stride
    } else {
        row_bytes
    };
    let required = stride.saturating_mul(h);
    let ok = !data_ptr.is_null() && (current_len as usize) >= required;
    if ok {
        let bottom_up = layout.default_stride < 0;
        for y in 0..h {
            let src_y = if bottom_up { h - 1 - y } else { y };
            let src_row =
                unsafe { std::slice::from_raw_parts(data_ptr.add(src_y * stride), row_bytes) };
            convert_bgrx_row(
                src_row,
                &mut luma[y * w..(y + 1) * w],
                bgra.as_mut()
                    .map(|b| &mut b[y * row_bytes..(y + 1) * row_bytes]),
            );
        }
    }
    unsafe {
        let _ = buffer.Unlock();
    }
    if ok {
        Some((luma, bgra))
    } else {
        None
    }
}

#[inline]
fn convert_bgrx_row(src_bgrx: &[u8], dst_luma: &mut [u8], mut dst_bgra: Option<&mut [u8]>) {
    for (x, px) in src_bgrx.as_chunks::<4>().0.iter().enumerate() {
        let b = px[0] as u32;
        let g = px[1] as u32;
        let r = px[2] as u32;
        dst_luma[x] = ((77 * r + 150 * g + 29 * b) >> 8) as u8;
        if let Some(ref mut out) = dst_bgra {
            let off = x * 4;
            out[off] = px[0];
            out[off + 1] = px[1];
            out[off + 2] = px[2];
            out[off + 3] = 0xFF;
        }
    }
}

struct PendingFrame {
    luma: Vec<u8>,
    width: u32,
    height: u32,
    generation: i64,
}

/// Bounded 1-slot replaceable mailbox: at most 1 frame actively decoding +
/// 1 latest waiting frame, dropping stale unanalyzed frames under load.
struct DecodeMailbox {
    state: Mutex<(Option<PendingFrame>, bool /* shutdown */)>,
    cvar: Condvar,
}

impl DecodeMailbox {
    fn new() -> Self {
        Self {
            state: Mutex::new((None, false)),
            cvar: Condvar::new(),
        }
    }

    fn push(&self, frame: PendingFrame) {
        let Ok(mut guard) = self.state.lock() else {
            return;
        };
        if guard.1 {
            return;
        }
        guard.0 = Some(frame);
        self.cvar.notify_one();
    }

    fn clear(&self) {
        if let Ok(mut guard) = self.state.lock() {
            guard.0 = None;
        }
    }

    fn shutdown(&self) {
        if let Ok(mut guard) = self.state.lock() {
            guard.0 = None;
            guard.1 = true;
            self.cvar.notify_all();
        }
    }

    fn take(&self) -> Option<PendingFrame> {
        let mut guard = self.state.lock().ok()?;
        loop {
            if let Some(frame) = guard.0.take() {
                return Some(frame);
            }
            if guard.1 {
                return None;
            }
            guard = self.cvar.wait(guard).ok()?;
        }
    }
}

struct SendReader(IMFSourceReader);
unsafe impl Send for SendReader {}

struct AsyncCameraShared {
    generation: i64,
    active: AtomicBool,
    ready_emitted: AtomicBool,
    layout: Mutex<FrameLayout>,
    caps: CameraHardwareCaps,
    reader: Mutex<Option<SendReader>>,
    in_callback: Mutex<usize>,
    in_callback_cvar: Condvar,
    stop_signal: Arc<(Mutex<bool>, Condvar)>,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    current_gen: Arc<AtomicI64>,
}

impl AsyncCameraShared {
    fn emit(&self, event: CaptureEvent) {
        if self.current_gen.load(Ordering::Acquire) != self.generation {
            if let CaptureEvent::Frame { token, .. } = event {
                if let Some(pool) = self.preview.as_ref() {
                    pool.release(token);
                }
            }
            return;
        }
        let emitter = self.emitter.lock().ok().and_then(|g| g.clone());
        if let Some(cb) = emitter {
            cb(event);
        } else if let CaptureEvent::Frame { token, .. } = event {
            if let Some(pool) = self.preview.as_ref() {
                pool.release(token);
            }
        }
    }

    fn signal_stop(&self) {
        self.active.store(false, Ordering::Release);
        let (lock, cvar) = &*self.stop_signal;
        if let Ok(mut stopped) = lock.lock() {
            *stopped = true;
            cvar.notify_all();
        }
    }
}

#[implement(IMFSourceReaderCallback)]
struct AsyncCameraCallback {
    shared: Arc<AsyncCameraShared>,
}

impl IMFSourceReaderCallback_Impl for AsyncCameraCallback_Impl {
    fn OnReadSample(
        &self,
        hrstatus: HRESULT,
        _dwstreamindex: u32,
        dwstreamflags: u32,
        _lltimestamp: i64,
        psample: windows_core::Ref<'_, IMFSample>,
    ) -> windows::core::Result<()> {
        // Track active callback so teardown never races with OnReadSample.
        {
            if let Ok(mut count) = self.shared.in_callback.lock() {
                *count += 1;
            }
        }
        struct CallbackGuard<'a>(&'a AsyncCameraShared);
        impl Drop for CallbackGuard<'_> {
            fn drop(&mut self) {
                if let Ok(mut count) = self.0.in_callback.lock() {
                    *count = count.saturating_sub(1);
                    if *count == 0 {
                        self.0.in_callback_cvar.notify_all();
                    }
                }
            }
        }
        let _guard = CallbackGuard(&self.shared);

        if !self.shared.active.load(Ordering::Acquire)
            || self.shared.current_gen.load(Ordering::Acquire) != self.shared.generation
        {
            return Ok(());
        }

        if hrstatus.is_err() {
            let code = map_mf_error(hrstatus);
            self.shared.emit(CaptureEvent::Error {
                code,
                message: format!("Camera stream failed ({hrstatus:?})"),
                generation: self.shared.generation,
            });
            self.shared.signal_stop();
            return Ok(());
        }

        if (dwstreamflags & (MF_SOURCE_READERF_CURRENTMEDIATYPECHANGED.0 as u32)) != 0 {
            let reader_opt = self
                .shared
                .reader
                .lock()
                .ok()
                .and_then(|g| g.as_ref().map(|r| r.0.clone()));
            if let Some(reader) = reader_opt {
                let prev_sub = self.shared.layout.lock().ok().map(|l| l.subtype);
                if let Ok(new_layout) = query_frame_layout(&reader, prev_sub) {
                    if let Ok(mut layout) = self.shared.layout.lock() {
                        *layout = new_layout;
                    }
                }
            }
        }

        if (dwstreamflags & (MF_SOURCE_READERF_ENDOFSTREAM.0 as u32)) != 0 {
            self.shared.emit(CaptureEvent::Error {
                code: "camera-busy",
                message: "Camera stream ended unexpectedly".to_owned(),
                generation: self.shared.generation,
            });
            self.shared.signal_stop();
            return Ok(());
        }

        if let Some(sample) = psample.as_ref() {
            let layout = self.shared.layout.lock().ok().map(|l| *l);
            if let Some(layout) = layout {
                let need_bgra = self.shared.preview.is_some();
                if let Some((luma, bgra)) = extract_rgb32_frame(sample, layout, need_bgra) {
                    if let (Some(pool), Some(bgra_bytes)) = (self.shared.preview.as_ref(), bgra) {
                        match pool.lend(&bgra_bytes, layout.width, layout.height) {
                            Ok(Some(lent)) => {
                                if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                                    self.shared.emit(CaptureEvent::Ready {
                                        generation: self.shared.generation,
                                        info: CaptureReadyInfo {
                                            width: layout.width,
                                            height: layout.height,
                                            fps: layout.fps,
                                            subtype: layout.subtype.to_owned(),
                                            focus_supported: self.shared.caps.focus_supported,
                                            auto_focus_supported: self
                                                .shared
                                                .caps
                                                .auto_focus_supported,
                                            exposure_supported: self.shared.caps.exposure_supported,
                                        },
                                    });
                                }
                                self.shared.emit(CaptureEvent::Frame {
                                    token: lent.token,
                                    nt_handle: lent.nt_handle_bytes,
                                    width: lent.width,
                                    height: lent.height,
                                    generation: self.shared.generation,
                                });
                            }
                            Ok(None) => {
                                if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                                    self.shared.emit(CaptureEvent::Ready {
                                        generation: self.shared.generation,
                                        info: CaptureReadyInfo {
                                            width: layout.width,
                                            height: layout.height,
                                            fps: layout.fps,
                                            subtype: layout.subtype.to_owned(),
                                            focus_supported: self.shared.caps.focus_supported,
                                            auto_focus_supported: self
                                                .shared
                                                .caps
                                                .auto_focus_supported,
                                            exposure_supported: self.shared.caps.exposure_supported,
                                        },
                                    });
                                }
                            }
                            Err(msg) => {
                                // Stop camera immediately so Web fallback can acquire it.
                                let (code, reason) = split_coded_error(&msg);
                                self.shared.emit(CaptureEvent::Error {
                                    code,
                                    message: reason,
                                    generation: self.shared.generation,
                                });
                                self.shared.signal_stop();
                                return Ok(());
                            }
                        }
                    } else if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                        self.shared.emit(CaptureEvent::Ready {
                            generation: self.shared.generation,
                            info: CaptureReadyInfo {
                                width: layout.width,
                                height: layout.height,
                                fps: layout.fps,
                                subtype: layout.subtype.to_owned(),
                                focus_supported: self.shared.caps.focus_supported,
                                auto_focus_supported: self.shared.caps.auto_focus_supported,
                                exposure_supported: self.shared.caps.exposure_supported,
                            },
                        });
                    }
                    self.shared.mailbox.push(PendingFrame {
                        luma,
                        width: layout.width,
                        height: layout.height,
                        generation: self.shared.generation,
                    });
                }
            }
        }

        if self.shared.active.load(Ordering::Acquire)
            && self.shared.current_gen.load(Ordering::Acquire) == self.shared.generation
        {
            let reader_opt = self
                .shared
                .reader
                .lock()
                .ok()
                .and_then(|g| g.as_ref().map(|r| r.0.clone()));
            if let Some(reader) = reader_opt {
                if let Err(err) = unsafe {
                    reader.ReadSample(
                        MF_SOURCE_READER_FIRST_VIDEO_STREAM.0 as u32,
                        0,
                        None,
                        None,
                        None,
                        None,
                    )
                } {
                    self.shared.emit(CaptureEvent::Error {
                        code: map_mf_error(err.code()),
                        message: format!("ReadSample failed ({err:?})"),
                        generation: self.shared.generation,
                    });
                    self.shared.signal_stop();
                }
            }
        }
        Ok(())
    }

    fn OnFlush(&self, _dwstreamindex: u32) -> windows::core::Result<()> {
        Ok(())
    }

    fn OnEvent(
        &self,
        _dwstreamindex: u32,
        _pevent: windows_core::Ref<'_, windows::Win32::Media::MediaFoundation::IMFMediaEvent>,
    ) -> windows::core::Result<()> {
        Ok(())
    }
}

struct SessionContext {
    generation: i64,
    stop_signal: Arc<(Mutex<bool>, Condvar)>,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    focus_control: Arc<Mutex<Option<FocusControlHandle>>>,
    current_gen: Arc<AtomicI64>,
}

struct CameraSessionOptions {
    device_id: Option<String>,
    preferred_resolution: PreferredResolution,
    lock_focus_on_detect: bool,
}

fn run_camera_session(ctx: SessionContext, opts: CameraSessionOptions) {
    let SessionContext {
        generation,
        stop_signal,
        preview,
        mailbox,
        emitter,
        focus_control,
        current_gen,
    } = ctx;
    let emit_err = |code: &'static str, message: String| {
        if current_gen.load(Ordering::Acquire) != generation {
            return;
        }
        let cb = emitter.lock().ok().and_then(|g| g.clone());
        if let Some(cb) = cb {
            cb(CaptureEvent::Error {
                code,
                message,
                generation,
            });
        }
    };

    let _mf = match MfGuard::init() {
        Ok(guard) => guard,
        Err(msg) => {
            emit_err("decoder-unavailable", msg);
            return;
        }
    };

    let activates = match enum_video_activates() {
        Ok(list) => list,
        Err(msg) => {
            emit_err("decoder-unavailable", msg);
            return;
        }
    };
    if activates.is_empty() {
        emit_err("no-camera", "No camera is available".to_owned());
        return;
    }

    let selected = if let Some(ref wanted) = opts.device_id {
        let mut found = None;
        for activate in activates {
            if let Ok(attrs) = activate.cast::<IMFAttributes>() {
                if let Some(sym) = read_allocated_string(
                    &attrs,
                    &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK,
                ) {
                    if sym.eq_ignore_ascii_case(wanted) {
                        found = Some(activate);
                        break;
                    }
                }
            }
        }
        match found {
            Some(act) => act,
            None => {
                emit_err(
                    "no-camera",
                    "Requested camera device was not found".to_owned(),
                );
                return;
            }
        }
    } else {
        activates.into_iter().next().unwrap()
    };

    let media_source: IMFMediaSource = match unsafe { selected.ActivateObject() } {
        Ok(src) => src,
        Err(err) => {
            emit_err(
                map_mf_error(err.code()),
                format!("Failed to open camera ({err:?})"),
            );
            return;
        }
    };

    let (hw_caps, focus_handle) =
        FocusControlHandle::probe_and_init(&media_source, generation, opts.lock_focus_on_detect);
    if let Ok(mut guard) = focus_control.lock() {
        *guard = focus_handle;
    }

    let clear_focus_handle = |fc: &Arc<Mutex<Option<FocusControlHandle>>>| {
        if let Ok(mut guard) = fc.lock() {
            if let Some(mut handle) = guard.take() {
                handle.restore_auto();
            }
        }
    };

    let shared = Arc::new(AsyncCameraShared {
        generation,
        active: AtomicBool::new(true),
        ready_emitted: AtomicBool::new(false),
        layout: Mutex::new(FrameLayout {
            width: 0,
            height: 0,
            default_stride: 0,
            fps: 0.0,
            subtype: "unknown",
        }),
        caps: hw_caps,
        reader: Mutex::new(None),
        in_callback: Mutex::new(0),
        in_callback_cvar: Condvar::new(),
        stop_signal: Arc::clone(&stop_signal),
        preview,
        mailbox,
        emitter: Arc::clone(&emitter),
        current_gen: Arc::clone(&current_gen),
    });

    let callback: IMFSourceReaderCallback = AsyncCameraCallback {
        shared: Arc::clone(&shared),
    }
    .into();

    let mut reader_attrs: Option<IMFAttributes> = None;
    if let Err(err) = unsafe { MFCreateAttributes(&mut reader_attrs, 2) } {
        clear_focus_handle(&focus_control);
        let _ = unsafe { media_source.Shutdown() };
        emit_err(
            "decoder-unavailable",
            format!("MFCreateAttributes failed ({err:?})"),
        );
        return;
    }
    let reader_attrs = reader_attrs.unwrap();
    let _ = unsafe { reader_attrs.SetUINT32(&MF_SOURCE_READER_ENABLE_VIDEO_PROCESSING, 1) };
    if let Err(err) =
        unsafe { reader_attrs.SetUnknown(&MF_SOURCE_READER_ASYNC_CALLBACK, &callback) }
    {
        clear_focus_handle(&focus_control);
        let _ = unsafe { media_source.Shutdown() };
        emit_err(
            "decoder-unavailable",
            format!("SetUnknown callback failed ({err:?})"),
        );
        return;
    }

    let reader = match unsafe { MFCreateSourceReaderFromMediaSource(&media_source, &reader_attrs) }
    {
        Ok(r) => r,
        Err(err) => {
            clear_focus_handle(&focus_control);
            let _ = unsafe { media_source.Shutdown() };
            emit_err(
                map_mf_error(err.code()),
                format!("Failed to create camera reader ({err:?})"),
            );
            return;
        }
    };

    let layout = match configure_camera_media_type(&reader, opts.preferred_resolution) {
        Ok(l) => l,
        Err(msg) => {
            drop(reader);
            clear_focus_handle(&focus_control);
            let _ = unsafe { media_source.Shutdown() };
            let (code, reason) = split_coded_error(&msg);
            emit_err(code, reason);
            return;
        }
    };
    if let Ok(mut l) = shared.layout.lock() {
        *l = layout;
    }
    if let Ok(mut r) = shared.reader.lock() {
        *r = Some(SendReader(reader.clone()));
    }

    if let Err(err) = unsafe {
        reader.ReadSample(
            MF_SOURCE_READER_FIRST_VIDEO_STREAM.0 as u32,
            0,
            None,
            None,
            None,
            None,
        )
    } {
        shared.active.store(false, Ordering::Release);
        if let Ok(mut r) = shared.reader.lock() {
            *r = None;
        }
        drop(reader);
        clear_focus_handle(&focus_control);
        let _ = unsafe { media_source.Shutdown() };
        emit_err(
            map_mf_error(err.code()),
            format!("Failed to start camera stream ({err:?})"),
        );
        return;
    }

    // Wait until stop(), dispose(), or a fatal stream/preview error.
    let (lock, cvar) = &*stop_signal;
    if let Ok(mut stopped) = lock.lock() {
        while !*stopped && current_gen.load(Ordering::Acquire) == generation {
            let Ok(next) = cvar.wait(stopped) else {
                break;
            };
            stopped = next;
        }
    }

    // Teardown: halt new sample requests, flush, wait for in-flight callback,
    // restore Auto focus, drop reader, and shut down the media source to
    // release camera hardware immediately (including before Web fallback).
    shared.active.store(false, Ordering::Release);
    if let Ok(mut r) = shared.reader.lock() {
        *r = None;
    }
    let _ = unsafe { reader.Flush(MF_SOURCE_READER_ALL_STREAMS.0 as u32) };
    if let Ok(mut count) = shared.in_callback.lock() {
        let deadline = Instant::now() + Duration::from_secs(2);
        while *count > 0 {
            let remaining = deadline.saturating_duration_since(Instant::now());
            if remaining.is_zero() {
                break;
            }
            let Ok((next, _)) = shared.in_callback_cvar.wait_timeout(count, remaining) else {
                break;
            };
            count = next;
        }
    }
    drop(reader);
    clear_focus_handle(&focus_control);
    let _ = unsafe { media_source.Shutdown() };
}

fn run_file_session(ctx: SessionContext, path: String, loop_file: bool) {
    let SessionContext {
        generation,
        stop_signal,
        preview,
        mailbox,
        emitter,
        current_gen,
        ..
    } = ctx;
    let emit = |event: CaptureEvent| {
        if current_gen.load(Ordering::Acquire) != generation {
            if let CaptureEvent::Frame { token, .. } = event {
                if let Some(pool) = preview.as_ref() {
                    pool.release(token);
                }
            }
            return;
        }
        let cb = emitter.lock().ok().and_then(|g| g.clone());
        if let Some(cb) = cb {
            cb(event);
        } else if let CaptureEvent::Frame { token, .. } = event {
            if let Some(pool) = preview.as_ref() {
                pool.release(token);
            }
        }
    };

    let is_stopped = || -> bool {
        if current_gen.load(Ordering::Acquire) != generation {
            return true;
        }
        stop_signal.0.lock().map(|g| *g).unwrap_or(true)
    };

    if path.is_empty() || !Path::new(&path).exists() {
        emit(CaptureEvent::Error {
            code: "invalid-image",
            message: "The video file does not exist".to_owned(),
            generation,
        });
        return;
    }

    let _mf = match MfGuard::init() {
        Ok(guard) => guard,
        Err(msg) => {
            emit(CaptureEvent::Error {
                code: "decoder-unavailable",
                message: msg,
                generation,
            });
            return;
        }
    };

    let mut attrs: Option<IMFAttributes> = None;
    if let Err(err) = unsafe { MFCreateAttributes(&mut attrs, 1) } {
        emit(CaptureEvent::Error {
            code: "decoder-unavailable",
            message: format!("MFCreateAttributes failed ({err:?})"),
            generation,
        });
        return;
    }
    let attrs = attrs.unwrap();
    let _ = unsafe { attrs.SetUINT32(&MF_SOURCE_READER_ENABLE_VIDEO_PROCESSING, 1) };

    let wide = HSTRING::from(&path);
    let reader = match unsafe { MFCreateSourceReaderFromURL(PCWSTR(wide.as_ptr()), &attrs) } {
        Ok(r) => r,
        Err(_) => {
            emit(CaptureEvent::Error {
                code: "invalid-image",
                message: "The file could not be opened as a video".to_owned(),
                generation,
            });
            return;
        }
    };

    let stream_idx = MF_SOURCE_READER_FIRST_VIDEO_STREAM.0 as u32;
    let _ = unsafe { reader.SetStreamSelection(MF_SOURCE_READER_ALL_STREAMS.0 as u32, false) };
    if unsafe { reader.SetStreamSelection(stream_idx, true) }.is_err() {
        emit(CaptureEvent::Error {
            code: "invalid-image",
            message: "The file has no video track".to_owned(),
            generation,
        });
        return;
    }

    let rgb_mt: IMFMediaType = match unsafe { MFCreateMediaType() } {
        Ok(mt) => mt,
        Err(err) => {
            emit(CaptureEvent::Error {
                code: "decoder-unavailable",
                message: format!("MFCreateMediaType failed ({err:?})"),
                generation,
            });
            return;
        }
    };
    let _ = unsafe { rgb_mt.SetGUID(&MF_MT_MAJOR_TYPE, &MFMediaType_Video) };
    let _ = unsafe { rgb_mt.SetGUID(&MF_MT_SUBTYPE, &MFVideoFormat_RGB32) };
    if unsafe { reader.SetCurrentMediaType(stream_idx, None, &rgb_mt) }.is_err() {
        emit(CaptureEvent::Error {
            code: "invalid-image",
            message: "The video track could not be decoded to RGB32".to_owned(),
            generation,
        });
        return;
    }

    let mut layout = match query_frame_layout(&reader, None) {
        Ok(l) => l,
        Err(_) => {
            emit(CaptureEvent::Error {
                code: "invalid-image",
                message: "Could not read video dimensions".to_owned(),
                generation,
            });
            return;
        }
    };

    let mut ready_emitted = false;
    let mut started_at = Instant::now();
    let mut first_pts: Option<i64> = None;

    while !is_stopped() {
        let mut flags = 0u32;
        let mut timestamp = 0i64;
        let mut sample: Option<IMFSample> = None;
        let res = unsafe {
            reader.ReadSample(
                stream_idx,
                0,
                None,
                Some(&mut flags),
                Some(&mut timestamp),
                Some(&mut sample),
            )
        };
        if res.is_err() {
            emit(CaptureEvent::Error {
                code: "invalid-image",
                message: "Failed while reading video frames".to_owned(),
                generation,
            });
            return;
        }

        if (flags & (MF_SOURCE_READERF_CURRENTMEDIATYPECHANGED.0 as u32)) != 0 {
            if let Ok(new_layout) = query_frame_layout(&reader, None) {
                layout = new_layout;
            }
        }

        if (flags & (MF_SOURCE_READERF_ENDOFSTREAM.0 as u32)) != 0 {
            if loop_file && !is_stopped() {
                let zero = PROPVARIANT::from(0i64);
                if unsafe { reader.SetCurrentPosition(&GUID::zeroed(), &zero) }.is_err() {
                    emit(CaptureEvent::Ended { generation });
                    return;
                }
                started_at = Instant::now();
                first_pts = None;
                continue;
            }
            if !is_stopped() {
                emit(CaptureEvent::Ended { generation });
            }
            return;
        }

        if let Some(sample) = sample {
            // Pace playback by presentation time (100-ns ticks) using interruptible wait.
            let base = *first_pts.get_or_insert(timestamp);
            let delta_100ns = (timestamp - base).max(0) as u64;
            let due = started_at + Duration::from_nanos(delta_100ns.saturating_mul(100));
            let wait = due.saturating_duration_since(Instant::now());
            if !wait.is_zero() {
                let (lock, cvar) = &*stop_signal;
                if let Ok(guard) = lock.lock() {
                    if *guard || current_gen.load(Ordering::Acquire) != generation {
                        return;
                    }
                    let _ = cvar.wait_timeout(guard, wait);
                }
                if is_stopped() {
                    return;
                }
            }

            let need_bgra = preview.is_some();
            if let Some((luma, bgra)) = extract_rgb32_frame(&sample, layout, need_bgra) {
                let ready_info = CaptureReadyInfo {
                    width: layout.width,
                    height: layout.height,
                    fps: layout.fps,
                    subtype: layout.subtype.to_owned(),
                    focus_supported: false,
                    auto_focus_supported: false,
                    exposure_supported: false,
                };
                if let (Some(pool), Some(bgra_bytes)) = (preview.as_ref(), bgra) {
                    match pool.lend(&bgra_bytes, layout.width, layout.height) {
                        Ok(Some(lent)) => {
                            if !ready_emitted {
                                ready_emitted = true;
                                emit(CaptureEvent::Ready {
                                    generation,
                                    info: ready_info,
                                });
                            }
                            emit(CaptureEvent::Frame {
                                token: lent.token,
                                nt_handle: lent.nt_handle_bytes,
                                width: lent.width,
                                height: lent.height,
                                generation,
                            });
                        }
                        Ok(None) => {
                            if !ready_emitted {
                                ready_emitted = true;
                                emit(CaptureEvent::Ready {
                                    generation,
                                    info: ready_info,
                                });
                            }
                        }
                        Err(msg) => {
                            let (code, reason) = split_coded_error(&msg);
                            emit(CaptureEvent::Error {
                                code,
                                message: reason,
                                generation,
                            });
                            return;
                        }
                    }
                } else if !ready_emitted {
                    ready_emitted = true;
                    emit(CaptureEvent::Ready {
                        generation,
                        info: ready_info,
                    });
                }
                mailbox.push(PendingFrame {
                    luma,
                    width: layout.width,
                    height: layout.height,
                    generation,
                });
            }
        }
    }
}

fn split_coded_error(msg: &str) -> (&'static str, String) {
    if let Some((code, rest)) = msg.split_once(':') {
        let code_str = match code.trim() {
            "permission-denied" => "permission-denied",
            "no-camera" => "no-camera",
            "camera-busy" => "camera-busy",
            "preview-unavailable" => "preview-unavailable",
            "invalid-image" => "invalid-image",
            "resource-limit" => "resource-limit",
            _ => "decoder-unavailable",
        };
        (code_str, rest.trim().to_owned())
    } else {
        ("decoder-unavailable", msg.to_owned())
    }
}

struct ActiveSession {
    stop_signal: Arc<(Mutex<bool>, Condvar)>,
    thread: JoinHandle<()>,
}

pub struct CaptureController {
    source: Mutex<SourceConfig>,
    generation: Arc<AtomicI64>,
    disposed: AtomicBool,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    focus_control: Arc<Mutex<Option<FocusControlHandle>>>,
    session: Mutex<Option<ActiveSession>>,
    decode_thread: Mutex<Option<JoinHandle<()>>>,
}

impl CaptureController {
    pub fn new(source: SourceConfig, with_preview: bool, emitter: EventEmitter) -> Self {
        let generation = Arc::new(AtomicI64::new(0));
        let preview = if with_preview {
            Some(Arc::new(PreviewPool::new()))
        } else {
            None
        };
        let mailbox = Arc::new(DecodeMailbox::new());
        let emitter_arc = Arc::new(Mutex::new(Some(emitter)));
        let focus_control: Arc<Mutex<Option<FocusControlHandle>>> = Arc::new(Mutex::new(None));

        let decode_mailbox = Arc::clone(&mailbox);
        let decode_emitter = Arc::clone(&emitter_arc);
        let decode_focus = Arc::clone(&focus_control);
        let decode_gen = Arc::clone(&generation);
        let decode_thread = thread::Builder::new()
            .name("qr-decode".to_owned())
            .spawn(move || {
                // COM MTA so optional IAMCameraControl focus lock calls are safe.
                let _com = ComMtaGuard::init().ok();
                let mut gate = FrameTextGate::default_gate();
                while let Some(frame) = decode_mailbox.take() {
                    if decode_gen.load(Ordering::Acquire) != frame.generation {
                        continue;
                    }
                    let now = Instant::now();
                    if let Some(text) =
                        decode_qr_luma(&frame.luma, frame.width as i32, frame.height as i32, true)
                    {
                        if decode_gen.load(Ordering::Acquire) != frame.generation {
                            continue;
                        }
                        if let Ok(mut fc) = decode_focus.lock() {
                            if let Some(handle) = fc.as_mut() {
                                handle.on_qr_detected(frame.generation, now);
                            }
                        }
                        if gate.accept(frame.generation, &text, now) {
                            let cb = decode_emitter.lock().ok().and_then(|g| g.clone());
                            if let Some(cb) = cb {
                                cb(CaptureEvent::Text {
                                    text,
                                    generation: frame.generation,
                                });
                            }
                        }
                    } else if let Ok(mut fc) = decode_focus.lock() {
                        if let Some(handle) = fc.as_mut() {
                            handle.on_frame_without_qr(frame.generation, now);
                        }
                    }
                }
            })
            .expect("failed to spawn qr-decode thread");

        Self {
            source: Mutex::new(source),
            generation,
            disposed: AtomicBool::new(false),
            preview,
            mailbox,
            emitter: emitter_arc,
            focus_control,
            session: Mutex::new(None),
            decode_thread: Mutex::new(Some(decode_thread)),
        }
    }

    pub fn start(&self) -> Result<(), String> {
        if self.disposed.load(Ordering::Acquire) {
            return Err("decoder-unavailable: The capture was disposed".to_owned());
        }
        // Stop any running session cleanly before starting a new generation.
        self.stop_inner();

        let next_gen = self.generation.fetch_add(1, Ordering::AcqRel) + 1;
        let source = self
            .source
            .lock()
            .map_err(|_| "decoder-unavailable: source lock poisoned".to_owned())?
            .clone();

        let stop_signal = Arc::new((Mutex::new(false), Condvar::new()));
        let ctx = SessionContext {
            generation: next_gen,
            stop_signal: Arc::clone(&stop_signal),
            preview: self.preview.clone(),
            mailbox: Arc::clone(&self.mailbox),
            emitter: Arc::clone(&self.emitter),
            focus_control: Arc::clone(&self.focus_control),
            current_gen: Arc::clone(&self.generation),
        };

        let handle = thread::Builder::new()
            .name(format!("qr-capture-{next_gen}"))
            .spawn(move || match source {
                SourceConfig::Camera {
                    device_id,
                    preferred_resolution,
                    lock_focus_on_detect,
                } => run_camera_session(
                    ctx,
                    CameraSessionOptions {
                        device_id,
                        preferred_resolution,
                        lock_focus_on_detect,
                    },
                ),
                SourceConfig::File { path, loop_file } => run_file_session(ctx, path, loop_file),
            })
            .map_err(|e| format!("decoder-unavailable: failed to spawn capture thread ({e})"))?;

        if let Ok(mut guard) = self.session.lock() {
            *guard = Some(ActiveSession {
                stop_signal,
                thread: handle,
            });
        }
        Ok(())
    }

    pub fn stop(&self) {
        if self.disposed.load(Ordering::Acquire) {
            return;
        }
        self.stop_inner();
    }

    fn stop_inner(&self) {
        // Invalidate current generation so stale frames/events are dropped immediately.
        self.generation.fetch_add(1, Ordering::AcqRel);
        self.mailbox.clear();
        let active = self.session.lock().ok().and_then(|mut g| g.take());
        if let Some(session) = active {
            let (lock, cvar) = &*session.stop_signal;
            if let Ok(mut stopped) = lock.lock() {
                *stopped = true;
                cvar.notify_all();
            }
            let _ = session.thread.join();
        }
    }

    pub fn set_device(&self, device_id: Option<String>) -> Result<(), String> {
        if self.disposed.load(Ordering::Acquire) {
            return Err("decoder-unavailable: The capture was disposed".to_owned());
        }
        let was_running = self.session.lock().map(|g| g.is_some()).unwrap_or(false);
        {
            let mut src = self
                .source
                .lock()
                .map_err(|_| "decoder-unavailable: source lock poisoned".to_owned())?;
            match &mut *src {
                SourceConfig::Camera {
                    device_id: slot, ..
                } => {
                    *slot = device_id;
                }
                SourceConfig::File { .. } => {
                    return Err(
                        "decoder-unavailable: setDevice is only supported for camera sources"
                            .to_owned(),
                    );
                }
            }
        }
        if was_running {
            self.start()?;
        }
        Ok(())
    }

    pub fn release_frame(&self, token: i64) {
        if let Some(pool) = self.preview.as_ref() {
            pool.release(token);
        }
    }

    pub fn lent_frames(&self) -> usize {
        self.preview.as_ref().map_or(0, |p| p.lent_count())
    }

    pub fn dispose(&self) {
        if self.disposed.swap(true, Ordering::AcqRel) {
            return;
        }
        // Clear emitter first so no new callback runs after dispose begins tearing down.
        if let Ok(mut em) = self.emitter.lock() {
            *em = None;
        }
        self.stop_inner();
        self.mailbox.shutdown();
        if let Some(handle) = self.decode_thread.lock().ok().and_then(|mut g| g.take()) {
            let _ = handle.join();
        }
        if let Some(pool) = self.preview.as_ref() {
            pool.clear();
        }
    }
}

impl Drop for CaptureController {
    fn drop(&mut self) {
        self.dispose();
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn prefers_uncompressed_720p_over_mjpg_and_low_fps_1080p() {
        let nv12_720_30 = score_native_mode(
            1280,
            720,
            30.0,
            MFVideoFormat_NV12,
            PreferredResolution::P720,
        );
        let mjpg_720_30 = score_native_mode(
            1280,
            720,
            30.0,
            MFVideoFormat_MJPG,
            PreferredResolution::P720,
        );
        let nv12_1080_10 = score_native_mode(
            1920,
            1080,
            10.0,
            MFVideoFormat_NV12,
            PreferredResolution::P1080,
        );
        let nv12_720_30_when_1080 = score_native_mode(
            1280,
            720,
            30.0,
            MFVideoFormat_NV12,
            PreferredResolution::P1080,
        );
        let nv12_1080_30_when_1080 = score_native_mode(
            1920,
            1080,
            30.0,
            MFVideoFormat_NV12,
            PreferredResolution::P1080,
        );

        assert!(nv12_720_30 > mjpg_720_30);
        assert!(nv12_720_30_when_1080 > nv12_1080_10);
        assert!(nv12_1080_30_when_1080 > nv12_720_30_when_1080);
    }
}
