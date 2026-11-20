#![cfg(windows)]

mod crash_prototype;
#[cfg(target_arch = "x86_64")]
mod unwind;

use napi_derive::napi;
use std::{ffi::c_void, mem::size_of, ptr};
use windows::{
    core::{Error, BOOL, PCWSTR, PWSTR},
    Win32::{
        Foundation::{
            CloseHandle, LocalFree, ERROR_INSUFFICIENT_BUFFER, HANDLE, HLOCAL, WIN32_ERROR,
        },
        Security::{
            Authorization::{
                ConvertSidToStringSidW, ConvertStringSecurityDescriptorToSecurityDescriptorW,
                SetSecurityInfo, SDDL_REVISION_1, SE_KERNEL_OBJECT,
            },
            GetSecurityDescriptorDacl, GetTokenInformation, TokenUser, ACL,
            DACL_SECURITY_INFORMATION, PROTECTED_DACL_SECURITY_INFORMATION, PSECURITY_DESCRIPTOR,
            TOKEN_QUERY, TOKEN_USER,
        },
        System::Threading::{
            GetCurrentProcess, OpenProcessToken, ProcessExtensionPointDisablePolicy,
            SetProcessMitigationPolicy,
        },
    },
};

struct TokenHandle(HANDLE);
impl Drop for TokenHandle {
    fn drop(&mut self) {
        if !self.0.is_invalid() {
            // SAFETY: OpenProcessToken returned this owned handle.
            let _ = unsafe { CloseHandle(self.0) };
        }
    }
}

struct LocalAllocation(HLOCAL);
impl Drop for LocalAllocation {
    fn drop(&mut self) {
        if !self.0.is_invalid() {
            // SAFETY: The SDDL/SID conversion allocated this buffer for LocalFree.
            unsafe { LocalFree(Some(self.0)) };
        }
    }
}

#[napi(object)]
pub struct StageResult {
    pub status: String,
    pub win32_error: Option<u32>,
    pub message: Option<String>,
}
impl StageResult {
    fn applied() -> Self {
        Self {
            status: "applied".to_owned(),
            win32_error: None,
            message: None,
        }
    }
    fn failed(api: &str, code: Option<u32>) -> Self {
        Self {
            status: "failed".to_owned(),
            win32_error: code,
            message: Some(format!("{api} failed")),
        }
    }
}

#[napi(object)]
pub struct HardeningResult {
    pub dacl: StageResult,
    pub extension_point: StageResult,
}

fn windows_failure(api: &str, error: Error) -> StageResult {
    StageResult::failed(api, WIN32_ERROR::from_error(&error).map(|code| code.0))
}

pub(crate) fn current_user_sid() -> Result<String, StageResult> {
    let mut token = HANDLE::default();
    // SAFETY: GetCurrentProcess is a pseudo handle, and token is a valid out pointer.
    unsafe { OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, &mut token) }
        .map_err(|error| windows_failure("OpenProcessToken", error))?;
    let token = TokenHandle(token);

    let mut length = 0_u32;
    // SAFETY: A null/zero buffer queries the required TOKEN_USER byte count.
    let size_query = unsafe { GetTokenInformation(token.0, TokenUser, None, 0, &mut length) };
    if let Err(error) = size_query {
        if WIN32_ERROR::from_error(&error) != Some(ERROR_INSUFFICIENT_BUFFER) {
            return Err(windows_failure(
                "GetTokenInformation(TokenUser) size",
                error,
            ));
        }
    } else {
        return Err(StageResult::failed(
            "GetTokenInformation(TokenUser) size",
            None,
        ));
    }
    if length < size_of::<TOKEN_USER>() as u32 {
        return Err(StageResult::failed(
            "GetTokenInformation(TokenUser) length",
            None,
        ));
    }

    // Vec<usize> provides TOKEN_USER's pointer alignment, unlike Vec<u8>.
    let words = (length as usize).div_ceil(size_of::<usize>());
    let mut information = vec![0_usize; words];
    let mut returned_length = 0_u32;
    // SAFETY: The aligned allocation holds at least `length` writable bytes.
    unsafe {
        GetTokenInformation(
            token.0,
            TokenUser,
            Some(information.as_mut_ptr().cast::<c_void>()),
            length,
            &mut returned_length,
        )
    }
    .map_err(|error| windows_failure("GetTokenInformation(TokenUser)", error))?;
    if returned_length < size_of::<TOKEN_USER>() as u32 {
        return Err(StageResult::failed(
            "GetTokenInformation(TokenUser) length",
            None,
        ));
    }
    // SAFETY: The successful call filled an aligned TOKEN_USER at the buffer start.
    let sid = unsafe { (*(information.as_ptr().cast::<TOKEN_USER>())).User.Sid };
    if sid.0.is_null() {
        return Err(StageResult::failed(
            "GetTokenInformation(TokenUser) SID",
            None,
        ));
    }

    let mut sid_string = PWSTR(ptr::null_mut());
    // SAFETY: The borrowed SID points into the still-live TOKEN_USER allocation.
    unsafe { ConvertSidToStringSidW(sid, &mut sid_string) }
        .map_err(|error| windows_failure("ConvertSidToStringSidW", error))?;
    let sid_string_allocation = LocalAllocation(HLOCAL(sid_string.0.cast()));
    if sid_string.0.is_null() {
        return Err(StageResult::failed("ConvertSidToStringSidW output", None));
    }
    let mut units = 0_usize;
    // Windows SID strings fit within 256 UTF-16 code units.
    while units < 256 && unsafe { *sid_string.0.add(units) } != 0 {
        units += 1;
    }
    if units == 256 {
        return Err(StageResult::failed("ConvertSidToStringSidW length", None));
    }
    // SAFETY: The conversion returned a terminated UTF-16 allocation.
    let sid_text = String::from_utf16(unsafe { std::slice::from_raw_parts(sid_string.0, units) })
        .map_err(|_| StageResult::failed("ConvertSidToStringSidW encoding", None))?;
    drop(sid_string_allocation);
    // Keep the TOKEN_USER buffer alive throughout ConvertSidToStringSidW.
    drop(information);
    Ok(sid_text)
}

fn apply_dacl() -> StageResult {
    let user_sid = match current_user_sid() {
        Ok(sid) => sid,
        Err(error) => return error,
    };
    let sddl = format!(
        "D:P(D;;0x00040000;;;S-1-3-4)(A;;0x00101001;;;{user_sid})(A;;0x00101001;;;S-1-5-18)"
    );
    let sddl_wide: Vec<u16> = sddl.encode_utf16().chain(std::iter::once(0)).collect();
    let mut descriptor = PSECURITY_DESCRIPTOR::default();
    // SAFETY: The SDDL is terminated and descriptor is a valid out pointer.
    if let Err(error) = unsafe {
        ConvertStringSecurityDescriptorToSecurityDescriptorW(
            PCWSTR(sddl_wide.as_ptr()),
            SDDL_REVISION_1,
            &mut descriptor,
            None,
        )
    } {
        return windows_failure(
            "ConvertStringSecurityDescriptorToSecurityDescriptorW",
            error,
        );
    }
    let descriptor_allocation = LocalAllocation(HLOCAL(descriptor.0));
    if descriptor.0.is_null() {
        return StageResult::failed(
            "ConvertStringSecurityDescriptorToSecurityDescriptorW output",
            None,
        );
    }

    let mut present = BOOL(0);
    let mut defaulted = BOOL(0);
    let mut acl = ptr::null_mut::<ACL>();
    // SAFETY: The descriptor remains allocated and all output pointers are valid.
    if let Err(error) =
        unsafe { GetSecurityDescriptorDacl(descriptor, &mut present, &mut acl, &mut defaulted) }
    {
        return windows_failure("GetSecurityDescriptorDacl", error);
    }
    if !present.as_bool() || acl.is_null() {
        return StageResult::failed("GetSecurityDescriptorDacl empty DACL", None);
    }

    // SAFETY: The ACL borrows the live descriptor; the process pseudo handle is never closed.
    let code = unsafe {
        SetSecurityInfo(
            GetCurrentProcess(),
            SE_KERNEL_OBJECT,
            DACL_SECURITY_INFORMATION | PROTECTED_DACL_SECURITY_INFORMATION,
            None,
            None,
            Some(acl.cast_const()),
            None,
        )
    };
    drop(descriptor_allocation);
    if code.0 == 0 {
        StageResult::applied()
    } else {
        StageResult::failed("SetSecurityInfo", Some(code.0))
    }
}

fn apply_extension_point_policy() -> StageResult {
    let flags = 1_u32;
    // SAFETY: This policy takes a four-byte DWORD with DisableExtensionPoints set.
    match unsafe {
        SetProcessMitigationPolicy(
            ProcessExtensionPointDisablePolicy,
            (&flags as *const u32).cast(),
            size_of::<u32>(),
        )
    } {
        Ok(()) => StageResult::applied(),
        Err(error) => windows_failure("SetProcessMitigationPolicy", error),
    }
}

#[napi]
pub fn harden_current_process() -> HardeningResult {
    // Each stage is attempted even when the other reports a Win32 failure.
    let dacl = apply_dacl();
    let extension_point = apply_extension_point_policy();
    HardeningResult {
        dacl,
        extension_point,
    }
}

#[napi(object)]
pub struct CrashHandlerStartResult {
    pub status: String,
    pub handler_pid: Option<u32>,
    pub message: Option<String>,
}

#[napi]
pub fn start_local_crash_handler(
    handler_path: String,
    profile_path: String,
    version: String,
) -> CrashHandlerStartResult {
    let sid = match current_user_sid() {
        Ok(sid) => sid,
        Err(_) => {
            return CrashHandlerStartResult {
                status: "failed".into(),
                handler_pid: None,
                message: Some("user_sid".into()),
            }
        }
    };
    match crash_prototype::start(
        std::path::Path::new(&handler_path),
        std::path::Path::new(&profile_path),
        &sid,
        &version,
    ) {
        Ok(pid) => CrashHandlerStartResult {
            status: "ready".into(),
            handler_pid: Some(pid),
            message: None,
        },
        Err(stage) => CrashHandlerStartResult {
            status: "failed".into(),
            handler_pid: None,
            message: Some(stage.into()),
        },
    }
}

#[cfg(v8_bridge)]
#[napi]
pub fn trigger_local_crash_for_test() {
    crash_prototype::trigger_local_crash_for_test();
}

#[cfg(not(v8_bridge))]
#[napi]
pub fn trigger_local_crash_for_test() -> napi::Result<()> {
    crash_prototype::trigger_local_crash_for_test()
}
