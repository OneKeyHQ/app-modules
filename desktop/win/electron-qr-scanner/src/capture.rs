use std::path::Path;
use std::sync::atomic::{AtomicBool, AtomicI64, AtomicU32, AtomicU64, Ordering};
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
    IMF2DBuffer, IMF2DBuffer2, IMFActivate, IMFAttributes, IMFMediaBuffer, IMFMediaSource,
    IMFMediaType, IMFSample, IMFSourceReader, IMFSourceReaderCallback,
    IMFSourceReaderCallback_Impl, MF2DBuffer_LockFlags_Read, MFCreateAttributes, MFCreateMediaType,
    MFCreateSourceReaderFromMediaSource, MFCreateSourceReaderFromURL, MFEnumDeviceSources,
    MFMediaType_Video, MFShutdown, MFStartup, MFVideoFormat_I420, MFVideoFormat_MJPG,
    MFVideoFormat_NV12, MFVideoFormat_RGB24, MFVideoFormat_RGB32, MFVideoFormat_YUY2,
    MFSTARTUP_LITE, MF_DEVSOURCE_ATTRIBUTE_FRIENDLY_NAME, MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE,
    MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_GUID,
    MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK, MF_E_NOT_AVAILABLE,
    MF_MT_DEFAULT_STRIDE, MF_MT_FRAME_RATE, MF_MT_FRAME_SIZE, MF_MT_MAJOR_TYPE, MF_MT_SUBTYPE,
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
use crate::image_decode::{decode_qr_luma, decode_qr_luma_symbols};
use crate::zxing_text::DecodedSymbol;

/// Duration without QR hits before a locked manual focus is released back to Auto.
const FOCUS_UNLOCK_TIMEOUT: Duration = Duration::from_millis(1500);
/// Settling window after a manual autofocus request before QR detections can re-lock focus.
const FOCUS_REFOCUS_SETTLE: Duration = Duration::from_millis(1500);

fn can_lock_focus_on_detect(refocus_until: &mut Option<Instant>, now: Instant) -> bool {
    if refocus_until.is_some_and(|deadline| now < deadline) {
        return false;
    }
    *refocus_until = None;
    true
}

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

#[derive(Clone, Copy, Debug, Default)]
pub struct CaptureStatsSnapshot {
    pub frames_captured: u64,
    pub preview_frames_emitted: u64,
    pub preview_frames_skipped: u64,
    pub decode_attempts: u64,
    pub decode_hits: u64,
    pub max_lent: u32,
}

#[derive(Default)]
struct CaptureStats {
    frames_captured: AtomicU64,
    preview_frames_emitted: AtomicU64,
    preview_frames_skipped: AtomicU64,
    decode_attempts: AtomicU64,
    decode_hits: AtomicU64,
    max_lent: AtomicU32,
}

impl CaptureStats {
    fn reset(&self) {
        self.frames_captured.store(0, Ordering::Relaxed);
        self.preview_frames_emitted.store(0, Ordering::Relaxed);
        self.preview_frames_skipped.store(0, Ordering::Relaxed);
        self.decode_attempts.store(0, Ordering::Relaxed);
        self.decode_hits.store(0, Ordering::Relaxed);
        self.max_lent.store(0, Ordering::Relaxed);
    }

    fn record_lent(&self, lent: usize) {
        let lent = lent as u32;
        let _ = self.max_lent.fetch_max(lent, Ordering::Relaxed);
    }

    fn snapshot(&self) -> CaptureStatsSnapshot {
        CaptureStatsSnapshot {
            frames_captured: self.frames_captured.load(Ordering::Relaxed),
            preview_frames_emitted: self.preview_frames_emitted.load(Ordering::Relaxed),
            preview_frames_skipped: self.preview_frames_skipped.load(Ordering::Relaxed),
            decode_attempts: self.decode_attempts.load(Ordering::Relaxed),
            decode_hits: self.decode_hits.load(Ordering::Relaxed),
            max_lent: self.max_lent.load(Ordering::Relaxed),
        }
    }
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
        analysis_revision: u64,
    },
    Symbols {
        symbols: Vec<DecodedSymbol>,
        frame_width: u32,
        frame_height: u32,
        generation: i64,
        analysis_revision: u64,
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
        analysis_revision: u64,
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
    let row_bytes = width.checked_mul(4).and_then(|n| i32::try_from(n).ok());
    let Some(row_bytes) =
        row_bytes.filter(|n| *n > 0 && i32::try_from(height).is_ok() && height > 0)
    else {
        return Err("decoder-unavailable: invalid frame dimensions".to_owned());
    };
    if unsafe { mt.GetGUID(&MF_MT_SUBTYPE) }.ok() != Some(MFVideoFormat_RGB32) {
        return Err("decoder-unavailable: expected RGB32 output".to_owned());
    }
    let default_stride = unsafe { mt.GetUINT32(&MF_MT_DEFAULT_STRIDE) }
        .map(|v| v as i32)
        .unwrap_or(row_bytes);
    let fps = read_fps(&mt);
    let subtype = native_subtype.unwrap_or("RGB32");
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
    refocus_until: Option<Instant>,
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

        let handle = if caps.auto_focus_supported {
            Some(Self {
                control,
                generation,
                lock_on_detect,
                auto_focus_supported: caps.auto_focus_supported,
                manual_focus_supported: caps.manual_focus_supported,
                default_focus: caps.default_focus,
                locked: false,
                last_detect_at: None,
                refocus_until: None,
            })
        } else {
            None
        };
        (caps, handle)
    }

    fn trigger_auto_focus(&mut self, now: Instant) -> bool {
        if !self.auto_focus_supported {
            return false;
        }
        let ok = unsafe {
            self.control.Set(
                CameraControl_Focus.0,
                self.default_focus,
                CameraControl_Flags_Auto.0,
            )
        }
        .is_ok();
        if ok {
            self.locked = false;
            self.last_detect_at = None;
            self.refocus_until = Some(now + FOCUS_REFOCUS_SETTLE);
        }
        ok
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
        if !can_lock_focus_on_detect(&mut self.refocus_until, now) {
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
            && self.restore_auto().is_err()
        {
            // Retry on a later frame without hammering the camera driver.
            self.last_detect_at = Some(now);
        }
    }

    fn restore_auto(&mut self) -> windows::core::Result<()> {
        if self.locked && self.auto_focus_supported {
            unsafe {
                self.control.Set(
                    CameraControl_Focus.0,
                    self.default_focus,
                    CameraControl_Flags_Auto.0,
                )
            }?;
        }
        self.locked = false;
        self.last_detect_at = None;
        self.refocus_until = None;
        Ok(())
    }
}

fn map_mf_error(hr: HRESULT) -> &'static str {
    const ERROR_SHARING_VIOLATION_HR: HRESULT = HRESULT(0x8007_0020u32 as i32);
    const ERROR_DEVICE_IN_USE_HR: HRESULT = HRESULT(0x8007_0964u32 as i32);
    const ERROR_GEN_FAILURE_HR: HRESULT = HRESULT(0x8007_001Fu32 as i32);
    const MF_E_VIDEO_RECORDING_DEVICE_INVALIDATED_HR: HRESULT = HRESULT(0xC00D_3EA2u32 as i32);
    const MF_E_HW_MFT_FAILED_START_STREAMING_HR: HRESULT = HRESULT(0xC00D_3704u32 as i32);

    if hr == E_ACCESSDENIED {
        "permission-denied"
    } else if hr == ERROR_SHARING_VIOLATION_HR
        || hr == ERROR_DEVICE_IN_USE_HR
        || hr == ERROR_GEN_FAILURE_HR
        || hr == MF_E_VIDEO_RECORDING_DEVICE_INVALIDATED_HR
        || hr == MF_E_HW_MFT_FAILED_START_STREAMING_HR
        || hr == MF_E_NOT_AVAILABLE
    {
        "camera-busy"
    } else {
        "decoder-unavailable"
    }
}

fn map_mf_error_string(hr: HRESULT, context: &str) -> String {
    format!("{}: {context} failed ({hr:?})", map_mf_error(hr))
}

type ExtractedFrame = (Vec<u8>, Option<Vec<u8>>);

/// Validates the first and last rows, which bound the entire signed-stride span.
fn rgb32_row_stride(
    first: usize,
    pitch: i32,
    length: usize,
    row_bytes: usize,
    height: usize,
) -> Option<usize> {
    let stride = pitch.unsigned_abs() as usize;
    if row_bytes == 0 || stride < row_bytes {
        return None;
    }
    let displacement = height.checked_sub(1)?.checked_mul(stride)?;
    let highest = if pitch < 0 {
        first.checked_sub(displacement)?;
        first
    } else {
        first.checked_add(displacement)?
    };
    (highest.checked_add(row_bytes)? <= length).then_some(stride)
}

/// The buffer must remain locked and readable for the duration of the copy.
unsafe fn copy_locked_rgb32(
    data: *const u8,
    length: usize,
    first: usize,
    pitch: i32,
    layout: FrameLayout,
    need_bgra: bool,
) -> Option<ExtractedFrame> {
    if data.is_null() {
        return None;
    }
    let w = layout.width as usize;
    let h = layout.height as usize;
    let row_bytes = w.checked_mul(4)?;
    let stride = rgb32_row_stride(first, pitch, length, row_bytes, h)?;

    let mut luma = Vec::new();
    let pixels = w.checked_mul(h)?;
    luma.try_reserve_exact(pixels).ok()?;
    luma.resize(pixels, 0);
    let mut bgra = None;
    if need_bgra {
        let mut bytes = Vec::new();
        let size = pixels.checked_mul(4)?;
        bytes.try_reserve_exact(size).ok()?;
        bytes.resize(size, 0);
        bgra = Some(bytes);
    }

    for (y, luma_row) in luma.chunks_exact_mut(w).enumerate() {
        let row_offset = if pitch < 0 {
            first - y * stride
        } else {
            first + y * stride
        };
        // The span check guarantees every row is within this live buffer.
        let src_row = unsafe { std::slice::from_raw_parts(data.add(row_offset), row_bytes) };
        convert_bgrx_row(
            src_row,
            luma_row,
            bgra.as_mut()
                .map(|b| &mut b[y * row_bytes..(y + 1) * row_bytes]),
        );
    }
    Some((luma, bgra))
}

/// Extracts top-down luminance and optional opaque BGRA from a bounds-bearing lock.
fn extract_rgb32_frame(
    sample: &IMFSample,
    layout: FrameLayout,
    need_bgra: bool,
) -> Option<ExtractedFrame> {
    let buffer: IMFMediaBuffer = unsafe { sample.ConvertToContiguousBuffer() }.ok()?;
    let mut data = std::ptr::null_mut();
    let mut length = 0;
    if let Ok(buf2d) = buffer.cast::<IMF2DBuffer2>() {
        let mut scanline0 = std::ptr::null_mut();
        let mut pitch = 0;
        if unsafe {
            buf2d.Lock2DSize(
                MF2DBuffer_LockFlags_Read,
                &mut scanline0,
                &mut pitch,
                &mut data,
                &mut length,
            )
        }
        .is_ok()
        {
            let frame = (!scanline0.is_null())
                .then(|| scanline0.addr().checked_sub(data.addr()))
                .flatten()
                .and_then(|first| unsafe {
                    copy_locked_rgb32(data, length as usize, first, pitch, layout, need_bgra)
                });
            unsafe {
                let _ = buf2d.Unlock2D();
            }
            return frame;
        }
    }

    // IMFMediaBuffer::Lock presents 2D surfaces in their packed representation.
    let pitch = if buffer.cast::<IMF2DBuffer>().is_ok() {
        (layout.width as i32 * 4) * if layout.default_stride < 0 { -1 } else { 1 }
    } else {
        layout.default_stride
    };
    let first = if pitch < 0 {
        (layout.height as usize)
            .checked_sub(1)?
            .checked_mul(pitch.unsigned_abs() as usize)?
    } else {
        0
    };
    unsafe { buffer.Lock(&mut data, None, Some(&mut length)) }.ok()?;
    let frame =
        unsafe { copy_locked_rgb32(data, length as usize, first, pitch, layout, need_bgra) };
    unsafe {
        let _ = buffer.Unlock();
    }
    frame
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
    analysis_revision: u64,
}

enum DecodeWork {
    Frame(PendingFrame),
    Ended(i64),
}

#[derive(Default)]
struct DecodeMailboxState {
    frame: Option<PendingFrame>,
    ended: Option<i64>,
    shutdown: bool,
}

/// Bounded 1-slot replaceable mailbox: at most 1 frame actively decoding +
/// 1 latest waiting frame, dropping stale unanalyzed frames under load.
struct DecodeMailbox {
    state: Mutex<DecodeMailboxState>,
    cvar: Condvar,
}

impl DecodeMailbox {
    fn new() -> Self {
        Self {
            state: Mutex::new(DecodeMailboxState::default()),
            cvar: Condvar::new(),
        }
    }

    fn push(&self, frame: PendingFrame) {
        let Ok(mut guard) = self.state.lock() else {
            return;
        };
        if guard.shutdown {
            return;
        }
        guard.frame = Some(frame);
        self.cvar.notify_one();
    }

    fn finish(&self, generation: i64) {
        if let Ok(mut state) = self.state.lock() {
            if !state.shutdown {
                state.ended = Some(generation);
                self.cvar.notify_one();
            }
        }
    }

    fn clear(&self) {
        if let Ok(mut guard) = self.state.lock() {
            guard.frame = None;
            guard.ended = None;
        }
    }

    fn shutdown(&self) {
        if let Ok(mut guard) = self.state.lock() {
            guard.frame = None;
            guard.ended = None;
            guard.shutdown = true;
            self.cvar.notify_all();
        }
    }

    fn take(&self) -> Option<DecodeWork> {
        let mut guard = self.state.lock().ok()?;
        loop {
            if let Some(frame) = guard.frame.take() {
                return Some(DecodeWork::Frame(frame));
            }
            if let Some(generation) = guard.ended.take() {
                return Some(DecodeWork::Ended(generation));
            }
            if guard.shutdown {
                return None;
            }
            guard = self.cvar.wait(guard).ok()?;
        }
    }
}

struct SendReader(IMFSourceReader);
unsafe impl Send for SendReader {}

#[derive(Default)]
struct SessionSignalState {
    stopped: bool,
    accepting_focus: bool,
    focus_requests: Vec<std::sync::mpsc::SyncSender<bool>>,
}

struct SessionSignal {
    state: Mutex<SessionSignalState>,
    cvar: Condvar,
}

impl SessionSignal {
    fn new() -> Self {
        Self {
            state: Mutex::new(SessionSignalState::default()),
            cvar: Condvar::new(),
        }
    }

    fn stop(&self) {
        if let Ok(mut state) = self.state.lock() {
            state.stopped = true;
            state.accepting_focus = false;
            for reply in state.focus_requests.drain(..) {
                let _ = reply.send(false);
            }
            self.cvar.notify_all();
        }
    }

    fn is_stopped(&self) -> bool {
        self.state.lock().map(|s| s.stopped).unwrap_or(true)
    }

    fn request_focus(&self) -> Option<std::sync::mpsc::Receiver<bool>> {
        let mut state = self.state.lock().ok()?;
        if state.stopped || !state.accepting_focus {
            return None;
        }
        let (tx, rx) = std::sync::mpsc::sync_channel(1);
        state.focus_requests.push(tx);
        self.cvar.notify_all();
        Some(rx)
    }
}

struct SessionStopGuard(Arc<SessionSignal>);

impl Drop for SessionStopGuard {
    fn drop(&mut self) {
        self.0.stop();
    }
}

struct AsyncCameraShared {
    device_id: Option<String>,
    generation: i64,
    active: Arc<AtomicBool>,
    ready_emitted: AtomicBool,
    layout: Mutex<FrameLayout>,
    camera_count: u32,
    camera_index: u32,
    caps: CameraHardwareCaps,
    reader: Mutex<Option<SendReader>>,
    in_callback: Mutex<usize>,
    in_callback_cvar: Condvar,
    stop_signal: Arc<SessionSignal>,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    stats: Arc<CaptureStats>,
    current_gen: Arc<AtomicI64>,
    analysis_revision: Arc<AtomicU64>,
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

    fn fail(&self, code: &'static str, message: String) {
        if !self.active.swap(false, Ordering::AcqRel) {
            return;
        }
        self.emit(CaptureEvent::Error {
            code,
            message,
            generation: self.generation,
        });
        self.stop_signal.stop();
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
            self.shared.fail(
                map_mf_error(hrstatus),
                format!("Camera stream failed ({hrstatus:?})"),
            );
            return Ok(());
        }

        if (dwstreamflags & (MF_SOURCE_READERF_CURRENTMEDIATYPECHANGED.0 as u32)) != 0 {
            let reader = self
                .shared
                .reader
                .lock()
                .ok()
                .and_then(|g| g.as_ref().map(|r| r.0.clone()));
            let Some(layout) = reader.and_then(|reader| {
                let subtype = self.shared.layout.lock().ok()?.subtype;
                query_frame_layout(&reader, Some(subtype)).ok()
            }) else {
                self.shared.fail(
                    "decoder-unavailable",
                    "Could not refresh the camera frame layout".to_owned(),
                );
                return Ok(());
            };
            if let Ok(mut current) = self.shared.layout.lock() {
                *current = layout;
            } else {
                self.shared.fail(
                    "decoder-unavailable",
                    "Could not store the camera frame layout".to_owned(),
                );
                return Ok(());
            }
        }

        if (dwstreamflags & (MF_SOURCE_READERF_ENDOFSTREAM.0 as u32)) != 0 {
            self.shared
                .fail("camera-busy", "Camera stream ended unexpectedly".to_owned());
            return Ok(());
        }

        if let Some(sample) = psample.as_ref() {
            let analysis_revision = self.shared.analysis_revision.load(Ordering::Acquire);
            let layout = self.shared.layout.lock().ok().map(|l| *l);
            if let Some(layout) = layout {
                let need_bgra = self.shared.preview.is_some();
                if let Some((luma, bgra)) = extract_rgb32_frame(sample, layout, need_bgra) {
                    self.shared
                        .stats
                        .frames_captured
                        .fetch_add(1, Ordering::Relaxed);
                    let make_ready_info = || CaptureReadyInfo {
                        device_id: self.shared.device_id.clone(),
                        width: layout.width,
                        height: layout.height,
                        fps: layout.fps,
                        subtype: layout.subtype.to_owned(),
                        camera_count: self.shared.camera_count,
                        camera_index: Some(self.shared.camera_index),
                        is_default_camera: self.shared.camera_index == 0,
                        focus_supported: self.shared.caps.focus_supported,
                        auto_focus_supported: self.shared.caps.auto_focus_supported,
                        exposure_supported: self.shared.caps.exposure_supported,
                    };
                    if let (Some(pool), Some(bgra_bytes)) = (self.shared.preview.as_ref(), bgra) {
                        match pool.lend(&bgra_bytes, layout.width, layout.height) {
                            Ok(Some(lent)) => {
                                self.shared
                                    .stats
                                    .preview_frames_emitted
                                    .fetch_add(1, Ordering::Relaxed);
                                self.shared.stats.record_lent(pool.lent_count());
                                if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                                    self.shared.emit(CaptureEvent::Ready {
                                        generation: self.shared.generation,
                                        info: make_ready_info(),
                                    });
                                }
                                self.shared.emit(CaptureEvent::Frame {
                                    token: lent.token,
                                    nt_handle: lent.nt_handle_bytes,
                                    width: lent.width,
                                    height: lent.height,
                                    generation: self.shared.generation,
                                    analysis_revision,
                                });
                            }
                            Ok(None) => {
                                self.shared
                                    .stats
                                    .preview_frames_skipped
                                    .fetch_add(1, Ordering::Relaxed);
                                if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                                    self.shared.emit(CaptureEvent::Ready {
                                        generation: self.shared.generation,
                                        info: make_ready_info(),
                                    });
                                }
                            }
                            Err(msg) => {
                                // Stop camera immediately so Web fallback can acquire it.
                                let (code, reason) = split_coded_error(&msg);
                                self.shared.fail(code, reason);
                                return Ok(());
                            }
                        }
                    } else if !self.shared.ready_emitted.swap(true, Ordering::AcqRel) {
                        self.shared.emit(CaptureEvent::Ready {
                            generation: self.shared.generation,
                            info: make_ready_info(),
                        });
                    }
                    self.shared.mailbox.push(PendingFrame {
                        luma,
                        width: layout.width,
                        height: layout.height,
                        generation: self.shared.generation,
                        analysis_revision,
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
                    self.shared.fail(
                        map_mf_error(err.code()),
                        format!("ReadSample failed ({err:?})"),
                    );
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
    running: Arc<AtomicBool>,
    stop_signal: Arc<SessionSignal>,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    focus_control: Arc<Mutex<Option<FocusControlHandle>>>,
    stats: Arc<CaptureStats>,
    current_gen: Arc<AtomicI64>,
    analysis_revision: Arc<AtomicU64>,
}

struct CameraSessionOptions {
    device_id: Option<String>,
    preferred_resolution: PreferredResolution,
    lock_focus_on_detect: bool,
}

fn run_camera_session(ctx: SessionContext, opts: CameraSessionOptions) {
    let SessionContext {
        generation,
        running,
        stop_signal,
        preview,
        mailbox,
        emitter,
        focus_control,
        stats,
        current_gen,
        analysis_revision,
    } = ctx;
    let _stop_guard = SessionStopGuard(Arc::clone(&stop_signal));
    let emit_err = |code: &'static str, message: String| {
        running.store(false, Ordering::Release);
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

    let camera_count = activates.len() as u32;
    let (camera_index, selected) = if let Some(ref wanted) = opts.device_id {
        let mut found = None;
        for (idx, activate) in activates.into_iter().enumerate() {
            if let Ok(attrs) = activate.cast::<IMFAttributes>() {
                if let Some(sym) = read_allocated_string(
                    &attrs,
                    &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK,
                ) {
                    if sym.eq_ignore_ascii_case(wanted) {
                        found = Some((idx as u32, activate));
                        break;
                    }
                }
            }
        }
        match found {
            Some(pair) => pair,
            None => {
                emit_err(
                    "no-camera",
                    "Requested camera device was not found".to_owned(),
                );
                return;
            }
        }
    } else {
        (0u32, activates.into_iter().next().unwrap())
    };

    let device_id = selected
        .cast::<IMFAttributes>()
        .ok()
        .and_then(|attributes| {
            read_allocated_string(
                &attributes,
                &MF_DEVSOURCE_ATTRIBUTE_SOURCE_TYPE_VIDCAP_SYMBOLIC_LINK,
            )
        });
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
                if let Err(error) = handle.restore_auto() {
                    eprintln!(
                        "Windows QR scanner: failed to restore Auto focus ({:#010x})",
                        error.code().0
                    );
                }
            }
        }
    };

    let shared = Arc::new(AsyncCameraShared {
        device_id,
        generation,
        active: Arc::clone(&running),
        ready_emitted: AtomicBool::new(false),
        layout: Mutex::new(FrameLayout {
            width: 0,
            height: 0,
            default_stride: 0,
            fps: 0.0,
            subtype: "unknown",
        }),
        camera_count,
        camera_index,
        caps: hw_caps,
        reader: Mutex::new(None),
        in_callback: Mutex::new(0),
        in_callback_cvar: Condvar::new(),
        stop_signal: Arc::clone(&stop_signal),
        preview,
        mailbox,
        emitter: Arc::clone(&emitter),
        stats,
        current_gen: Arc::clone(&current_gen),
        analysis_revision,
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
            clear_focus_handle(&focus_control);
            drop(reader);
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
        clear_focus_handle(&focus_control);
        drop(reader);
        let _ = unsafe { media_source.Shutdown() };
        emit_err(
            map_mf_error(err.code()),
            format!("Failed to start camera stream ({err:?})"),
        );
        return;
    }

    // Wait until stop(), dispose(), or a fatal stream/preview error, while
    // servicing focus requests on this COM-initialized camera thread.
    if let Ok(mut state) = stop_signal.state.lock() {
        state.accepting_focus = true;
        loop {
            let requests: Vec<_> = state.focus_requests.drain(..).collect();
            let should_stop = state.stopped || current_gen.load(Ordering::Acquire) != generation;
            if !requests.is_empty() {
                drop(state);
                let focused = if !should_stop {
                    focus_control
                        .lock()
                        .ok()
                        .and_then(|mut guard| {
                            guard
                                .as_mut()
                                .map(|handle| handle.trigger_auto_focus(Instant::now()))
                        })
                        .unwrap_or(false)
                } else {
                    false
                };
                for reply in requests {
                    let _ = reply.send(focused);
                }
                let Ok(reacquired) = stop_signal.state.lock() else {
                    break;
                };
                state = reacquired;
                continue;
            }
            if should_stop {
                break;
            }
            let Ok(next) = stop_signal.cvar.wait(state) else {
                break;
            };
            state = next;
        }
    }
    stop_signal.stop();

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
    clear_focus_handle(&focus_control);
    drop(reader);
    let _ = unsafe { media_source.Shutdown() };
}

fn run_file_session(ctx: SessionContext, path: String, loop_file: bool) {
    let SessionContext {
        generation,
        stop_signal,
        preview,
        mailbox,
        emitter,
        stats,
        current_gen,
        analysis_revision,
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

    let _stop_guard = SessionStopGuard(Arc::clone(&stop_signal));
    let is_stopped = || -> bool {
        if current_gen.load(Ordering::Acquire) != generation {
            return true;
        }
        stop_signal.is_stopped()
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
            match query_frame_layout(&reader, None) {
                Ok(new_layout) => layout = new_layout,
                Err(_) => {
                    emit(CaptureEvent::Error {
                        code: "invalid-image",
                        message: "Could not refresh the video frame layout".to_owned(),
                        generation,
                    });
                    return;
                }
            }
        }

        if let Some(sample) = sample {
            // Pace playback by presentation time (100-ns ticks) using interruptible wait.
            let base = *first_pts.get_or_insert(timestamp);
            let delta_100ns = (timestamp - base).max(0) as u64;
            let due = started_at + Duration::from_nanos(delta_100ns.saturating_mul(100));
            let wait = due.saturating_duration_since(Instant::now());
            if !wait.is_zero() {
                if let Ok(guard) = stop_signal.state.lock() {
                    if guard.stopped || current_gen.load(Ordering::Acquire) != generation {
                        return;
                    }
                    let _ = stop_signal.cvar.wait_timeout(guard, wait);
                }
                if is_stopped() {
                    return;
                }
            }

            let frame_revision = analysis_revision.load(Ordering::Acquire);
            let need_bgra = preview.is_some();
            if let Some((luma, bgra)) = extract_rgb32_frame(&sample, layout, need_bgra) {
                stats.frames_captured.fetch_add(1, Ordering::Relaxed);
                let ready_info = CaptureReadyInfo {
                    device_id: None,
                    width: layout.width,
                    height: layout.height,
                    fps: layout.fps,
                    subtype: layout.subtype.to_owned(),
                    camera_count: 0,
                    camera_index: None,
                    is_default_camera: false,
                    focus_supported: false,
                    auto_focus_supported: false,
                    exposure_supported: false,
                };
                if let (Some(pool), Some(bgra_bytes)) = (preview.as_ref(), bgra) {
                    match pool.lend(&bgra_bytes, layout.width, layout.height) {
                        Ok(Some(lent)) => {
                            stats.preview_frames_emitted.fetch_add(1, Ordering::Relaxed);
                            stats.record_lent(pool.lent_count());
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
                                analysis_revision: frame_revision,
                            });
                        }
                        Ok(None) => {
                            stats.preview_frames_skipped.fetch_add(1, Ordering::Relaxed);
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
                    analysis_revision: frame_revision,
                });
            }
        }

        if (flags & (MF_SOURCE_READERF_ENDOFSTREAM.0 as u32)) != 0 {
            if loop_file && !is_stopped() {
                let zero = PROPVARIANT::from(0i64);
                if unsafe { reader.SetCurrentPosition(&GUID::zeroed(), &zero) }.is_err() {
                    mailbox.finish(generation);
                    return;
                }
                started_at = Instant::now();
                first_pts = None;
                continue;
            }
            if !is_stopped() {
                mailbox.finish(generation);
            }
            return;
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
    running: Arc<AtomicBool>,
    stop_signal: Arc<SessionSignal>,
    thread: JoinHandle<()>,
}

pub struct CaptureController {
    source: Mutex<SourceConfig>,
    generation: Arc<AtomicI64>,
    disposed: AtomicBool,
    multi_symbol: Arc<AtomicBool>,
    // Even revisions run analysis and odd revisions pause it. One atomic
    // change invalidates in-flight work and updates the pause state.
    analysis_revision: Arc<AtomicU64>,
    preview: Option<Arc<PreviewPool>>,
    mailbox: Arc<DecodeMailbox>,
    emitter: Arc<Mutex<Option<EventEmitter>>>,
    focus_control: Arc<Mutex<Option<FocusControlHandle>>>,
    stats: Arc<CaptureStats>,
    session: Mutex<Option<ActiveSession>>,
    decode_thread: Mutex<Option<JoinHandle<()>>>,
}

impl CaptureController {
    pub fn new(
        source: SourceConfig,
        with_preview: bool,
        multi_symbol: bool,
        emitter: EventEmitter,
    ) -> Self {
        let generation = Arc::new(AtomicI64::new(0));
        let multi_symbol_flag = Arc::new(AtomicBool::new(multi_symbol));
        let analysis_revision = Arc::new(AtomicU64::new(0));
        let preview = if with_preview {
            Some(Arc::new(PreviewPool::new()))
        } else {
            None
        };
        let mailbox = Arc::new(DecodeMailbox::new());
        let emitter_arc = Arc::new(Mutex::new(Some(emitter)));
        let focus_control: Arc<Mutex<Option<FocusControlHandle>>> = Arc::new(Mutex::new(None));
        let stats = Arc::new(CaptureStats::default());

        let decode_mailbox = Arc::clone(&mailbox);
        let decode_emitter = Arc::clone(&emitter_arc);
        let decode_focus = Arc::clone(&focus_control);
        let decode_stats = Arc::clone(&stats);
        let decode_gen = Arc::clone(&generation);
        let decode_multi = Arc::clone(&multi_symbol_flag);
        let decode_revision = Arc::clone(&analysis_revision);
        let decode_thread = thread::Builder::new()
            .name("qr-decode".to_owned())
            .spawn(move || {
                // COM MTA so optional IAMCameraControl focus lock calls are safe.
                let _com = ComMtaGuard::init().ok();
                let mut gate = FrameTextGate::default_gate();
                while let Some(work) = decode_mailbox.take() {
                    let frame = match work {
                        DecodeWork::Frame(frame) => frame,
                        DecodeWork::Ended(generation) => {
                            if decode_gen.load(Ordering::Acquire) == generation {
                                let cb = decode_emitter.lock().ok().and_then(|g| g.clone());
                                if let Some(cb) = cb {
                                    cb(CaptureEvent::Ended { generation });
                                }
                            }
                            continue;
                        }
                    };
                    if frame.analysis_revision & 1 != 0
                        || decode_gen.load(Ordering::Acquire) != frame.generation
                        || decode_revision.load(Ordering::Acquire) != frame.analysis_revision
                    {
                        continue;
                    }
                    decode_stats.decode_attempts.fetch_add(1, Ordering::Relaxed);
                    let now = Instant::now();
                    if decode_multi.load(Ordering::Acquire) {
                        let symbols = decode_qr_luma_symbols(
                            &frame.luma,
                            frame.width as i32,
                            frame.height as i32,
                            true,
                            8,
                        );
                        if decode_gen.load(Ordering::Acquire) != frame.generation
                            || decode_revision.load(Ordering::Acquire) != frame.analysis_revision
                        {
                            continue;
                        }
                        if !symbols.is_empty() {
                            if let Ok(mut fc) = decode_focus.lock() {
                                if let Some(handle) = fc.as_mut() {
                                    handle.on_qr_detected(frame.generation, now);
                                }
                            }
                            decode_stats.decode_hits.fetch_add(1, Ordering::Relaxed);
                            let cb = decode_emitter.lock().ok().and_then(|g| g.clone());
                            if let Some(cb) = cb {
                                cb(CaptureEvent::Symbols {
                                    symbols,
                                    frame_width: frame.width,
                                    frame_height: frame.height,
                                    generation: frame.generation,
                                    analysis_revision: frame.analysis_revision,
                                });
                            }
                        } else if let Ok(mut fc) = decode_focus.lock() {
                            if let Some(handle) = fc.as_mut() {
                                handle.on_frame_without_qr(frame.generation, now);
                            }
                        }
                        continue;
                    }

                    let text =
                        decode_qr_luma(&frame.luma, frame.width as i32, frame.height as i32, true);
                    if decode_gen.load(Ordering::Acquire) != frame.generation
                        || decode_revision.load(Ordering::Acquire) != frame.analysis_revision
                    {
                        continue;
                    }
                    if let Some(text) = text {
                        if let Ok(mut fc) = decode_focus.lock() {
                            if let Some(handle) = fc.as_mut() {
                                handle.on_qr_detected(frame.generation, now);
                            }
                        }
                        if gate.accept(frame.generation, &text, now) {
                            decode_stats.decode_hits.fetch_add(1, Ordering::Relaxed);
                            let cb = decode_emitter.lock().ok().and_then(|g| g.clone());
                            if let Some(cb) = cb {
                                cb(CaptureEvent::Text {
                                    text,
                                    generation: frame.generation,
                                    analysis_revision: frame.analysis_revision,
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
            multi_symbol: multi_symbol_flag,
            analysis_revision,
            preview,
            mailbox,
            emitter: emitter_arc,
            focus_control,
            stats,
            session: Mutex::new(None),
            decode_thread: Mutex::new(Some(decode_thread)),
        }
    }

    pub fn set_multi_symbol(&self, enabled: bool) {
        self.multi_symbol.store(enabled, Ordering::Release);
    }

    pub fn set_paused(&self, paused: bool) -> u64 {
        self.analysis_revision
            .fetch_update(Ordering::AcqRel, Ordering::Acquire, |revision| {
                ((revision & 1 != 0) != paused).then_some(revision + 1)
            })
            .map_or_else(|revision| revision, |previous| previous + 1)
    }

    pub fn start(&self) -> Result<i64, String> {
        if self.disposed.load(Ordering::Acquire) {
            return Err("decoder-unavailable: The capture was disposed".to_owned());
        }
        // Stop any running session cleanly before starting a new generation.
        self.stop_inner();
        self.stats.reset();

        let next_gen = self.generation.fetch_add(1, Ordering::AcqRel) + 1;
        let source = self
            .source
            .lock()
            .map_err(|_| "decoder-unavailable: source lock poisoned".to_owned())?
            .clone();

        let stop_signal = Arc::new(SessionSignal::new());
        let running = Arc::new(AtomicBool::new(true));
        let ctx = SessionContext {
            generation: next_gen,
            running: Arc::clone(&running),
            stop_signal: Arc::clone(&stop_signal),
            preview: self.preview.clone(),
            mailbox: Arc::clone(&self.mailbox),
            emitter: Arc::clone(&self.emitter),
            focus_control: Arc::clone(&self.focus_control),
            stats: Arc::clone(&self.stats),
            current_gen: Arc::clone(&self.generation),
            analysis_revision: Arc::clone(&self.analysis_revision),
        };

        let session_running = Arc::clone(&running);
        let handle = thread::Builder::new()
            .name(format!("qr-capture-{next_gen}"))
            .spawn(move || {
                match source {
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
                    SourceConfig::File { path, loop_file } => {
                        run_file_session(ctx, path, loop_file)
                    }
                }
                session_running.store(false, Ordering::Release);
            })
            .map_err(|e| format!("decoder-unavailable: failed to spawn capture thread ({e})"))?;

        if let Ok(mut guard) = self.session.lock() {
            *guard = Some(ActiveSession {
                running,
                stop_signal,
                thread: handle,
            });
        }
        Ok(next_gen)
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
        let active = self.session.lock().ok().and_then(|mut g| g.take());
        if let Some(session) = active {
            session.stop_signal.stop();
            let _ = session.thread.join();
        }
        self.mailbox.clear();
    }

    pub fn set_device(&self, device_id: Option<String>) -> Result<Option<i64>, String> {
        if self.disposed.load(Ordering::Acquire) {
            return Err("decoder-unavailable: The capture was disposed".to_owned());
        }
        let was_running = self
            .session
            .lock()
            .map(|session| {
                session.as_ref().is_some_and(|session| {
                    session.running.load(Ordering::Acquire) && !session.thread.is_finished()
                })
            })
            .unwrap_or(false);
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
            self.start().map(Some)
        } else {
            Ok(None)
        }
    }

    pub fn release_frame(&self, token: i64) {
        if let Some(pool) = self.preview.as_ref() {
            pool.release(token);
        }
    }

    pub fn lent_frames(&self) -> usize {
        self.preview.as_ref().map_or(0, |p| p.lent_count())
    }

    pub fn focus(&self) -> bool {
        if self.disposed.load(Ordering::Acquire) {
            return false;
        }
        let is_camera = self
            .source
            .lock()
            .map(|src| matches!(*src, SourceConfig::Camera { .. }))
            .unwrap_or(false);
        if !is_camera {
            return false;
        }
        let rx = self.session.lock().ok().and_then(|guard| {
            let session = guard.as_ref()?;
            if !session.running.load(Ordering::Acquire) || session.thread.is_finished() {
                return None;
            }
            session.stop_signal.request_focus()
        });
        rx.and_then(|receiver| receiver.recv_timeout(Duration::from_secs(2)).ok())
            .unwrap_or(false)
    }

    pub fn stats(&self) -> CaptureStatsSnapshot {
        self.stats.snapshot()
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
    fn validates_full_signed_rgb32_span() {
        assert_eq!(rgb32_row_stride(4, 12, 36, 8, 3), Some(12));
        assert_eq!(rgb32_row_stride(28, -12, 36, 8, 3), Some(12));
        assert_eq!(rgb32_row_stride(4, 12, 35, 8, 3), None);
        assert_eq!(rgb32_row_stride(20, -12, 36, 8, 3), None);
        assert_eq!(rgb32_row_stride(0, 4, 36, 8, 3), None);
        assert_eq!(rgb32_row_stride(0, 12, 36, 8, 0), None);
    }

    #[test]
    fn mailbox_orders_end_after_the_latest_frame() {
        let mailbox = DecodeMailbox::new();
        for value in [1, 2] {
            mailbox.push(PendingFrame {
                luma: vec![value],
                width: 1,
                height: 1,
                generation: 3,
                analysis_revision: 0,
            });
        }
        mailbox.finish(3);
        assert!(matches!(mailbox.take(), Some(DecodeWork::Frame(frame)) if frame.luma == [2]));
        assert!(matches!(mailbox.take(), Some(DecodeWork::Ended(3))));
    }

    #[test]
    fn maps_media_foundation_availability_to_camera_busy() {
        use windows::Win32::Media::MediaFoundation::MF_E_UNSUPPORTED_RATE_TRANSITION;

        assert_eq!(map_mf_error(MF_E_NOT_AVAILABLE), "camera-busy");
        assert_eq!(
            map_mf_error(MF_E_UNSUPPORTED_RATE_TRANSITION),
            "decoder-unavailable"
        );
    }

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

    #[test]
    fn skips_detection_focus_lock_until_manual_refocus_settles() {
        let start = Instant::now();
        let in_flight_frame_at = start;
        let focus_requested_at = start + Duration::from_millis(25);
        let mut refocus_until = Some(focus_requested_at + FOCUS_REFOCUS_SETTLE);

        assert!(!can_lock_focus_on_detect(
            &mut refocus_until,
            in_flight_frame_at
        ));
        assert!(!can_lock_focus_on_detect(
            &mut refocus_until,
            focus_requested_at + Duration::from_millis(500)
        ));
        assert!(refocus_until.is_some());

        assert!(can_lock_focus_on_detect(
            &mut refocus_until,
            focus_requested_at + FOCUS_REFOCUS_SETTLE
        ));
        assert!(refocus_until.is_none());
    }
}
