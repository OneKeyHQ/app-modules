//! Local, test-gated Windows crash capture. The raw dump exists only in this process's memory.
#![allow(dead_code)]

use std::{
    ffi::{c_void, OsString},
    fs::{self, File, OpenOptions},
    io::{Read, Write},
    os::windows::{
        ffi::{OsStrExt, OsStringExt},
        io::{AsRawHandle, FromRawHandle},
    },
    path::{Path, PathBuf},
    ptr,
    sync::atomic::{AtomicBool, AtomicUsize, Ordering},
    time::Instant,
};
use windows::Win32::System::LibraryLoader::GetModuleHandleW;
use windows::{
    core::{BOOL, PCSTR, PCWSTR},
    Win32::{
        Foundation::{
            CloseHandle, CompareObjectHandles, DuplicateHandle, FreeLibrary, GetHandleInformation,
            LocalFree, DUPLICATE_HANDLE_OPTIONS, HANDLE, HLOCAL,
        },
        Security::{
            Authorization::{
                ConvertStringSecurityDescriptorToSecurityDescriptorW, GetSecurityInfo,
                SetSecurityInfo, SDDL_REVISION_1, SE_KERNEL_OBJECT,
            },
            GetSecurityDescriptorControl, GetSecurityDescriptorDacl, GetTokenInformation,
            SetTokenInformation, TokenDefaultDacl, TokenStatistics, ACL, DACL_SECURITY_INFORMATION,
            PROTECTED_DACL_SECURITY_INFORMATION, PSECURITY_DESCRIPTOR, SE_DACL_PROTECTED,
            TOKEN_ACCESS_MASK, TOKEN_DEFAULT_DACL, TOKEN_STATISTICS,
        },
        System::{
            Diagnostics::Debug::{SetUnhandledExceptionFilter, EXCEPTION_POINTERS},
            LibraryLoader::{
                GetProcAddress, LoadLibraryExW, LOAD_LIBRARY_SEARCH_DLL_LOAD_DIR,
                LOAD_LIBRARY_SEARCH_SYSTEM32,
            },
            Threading::{
                GetCurrentProcess, GetCurrentProcessId, GetCurrentThreadId, GetProcessId,
                OpenProcessToken, Sleep, TerminateProcess, WaitForSingleObject,
            },
        },
    },
};

const INIT_MAGIC: u32 = 0x314b4f43;
const CRASH_MAGIC: u32 = 0x324b4f43;
const MAX_DUMP: usize = 128 * 1024 * 1024;
const CAPTURE_DLL_NAME: &str = "onekey-crashpad-capture.win32-x64.dll";
const REPORT_NAME: &str = "report.json";
const STATUS_NAME: &str = "status.json";
static CLIENT_WRITE: AtomicUsize = AtomicUsize::new(0);
static CLIENT_READ: AtomicUsize = AtomicUsize::new(0);
static CLIENT_HANDLER: AtomicUsize = AtomicUsize::new(0);
static CLIENT_IO_EVENT: AtomicUsize = AtomicUsize::new(0);
static CRASH_SENT: AtomicBool = AtomicBool::new(false);

const CAPTURE_ACCESS: u32 = 0x0012_0410; // SYNCHRONIZE | READ_CONTROL | QUERY_INFORMATION | VM_READ
const EXTENDED_STARTUPINFO_PRESENT: u32 = 0x0008_0000;
const CREATE_SUSPENDED: u32 = 0x0000_0004;
const DETACHED_PROCESS: u32 = 0x0000_0008;
const STARTF_USESTDHANDLES: u32 = 0x0000_0100;
const PROC_THREAD_ATTRIBUTE_HANDLE_LIST: usize = 0x0002_0002;
const HANDLE_FLAG_INHERIT: u32 = 1;
const FILE_FLAG_OVERLAPPED: u32 = 0x4000_0000;
const FILE_FLAG_FIRST_PIPE_INSTANCE: u32 = 0x0008_0000;
const PIPE_ACCESS_INBOUND: u32 = 1;
const PIPE_ACCESS_OUTBOUND: u32 = 2;
const PIPE_REJECT_REMOTE_CLIENTS: u32 = 8;
const GENERIC_READ: u32 = 0x8000_0000;
const GENERIC_WRITE: u32 = 0x4000_0000;
const OPEN_EXISTING: u32 = 3;
const ERROR_IO_PENDING: u32 = 997;
static PIPE_SEQUENCE: AtomicUsize = AtomicUsize::new(0);
const JOB_OBJECT_EXTENDED_LIMIT_INFORMATION: u32 = 9;
const JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE: u32 = 0x0000_2000;
const GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS: u32 = 0x0000_0004;
const GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT: u32 = 0x0000_0002;

#[repr(C)]
struct SecurityAttributes {
    length: u32,
    descriptor: *mut c_void,
    inherit: i32,
}

#[repr(C)]
struct StartupInfoW {
    cb: u32,
    reserved: *mut u16,
    desktop: *mut u16,
    title: *mut u16,
    x: u32,
    y: u32,
    x_size: u32,
    y_size: u32,
    x_count_chars: u32,
    y_count_chars: u32,
    fill_attribute: u32,
    flags: u32,
    show_window: u16,
    reserved2_size: u16,
    reserved2: *mut u8,
    stdin: *mut c_void,
    stdout: *mut c_void,
    stderr: *mut c_void,
}

#[repr(C)]
struct StartupInfoExW {
    startup: StartupInfoW,
    attributes: *mut c_void,
}

#[repr(C)]
struct ProcessInformation {
    process: *mut c_void,
    thread: *mut c_void,
    pid: u32,
    tid: u32,
}

#[repr(C)]
struct JobBasicLimitInformation {
    process_time: i64,
    job_time: i64,
    flags: u32,
    minimum_working_set: usize,
    maximum_working_set: usize,
    active_process_limit: u32,
    affinity: usize,
    priority_class: u32,
    scheduling_class: u32,
}

#[repr(C)]
struct JobIoCounters {
    counts: [u64; 6],
}

#[repr(C)]
struct JobExtendedLimitInformation {
    basic: JobBasicLimitInformation,
    io: JobIoCounters,
    process_memory_limit: usize,
    job_memory_limit: usize,
    peak_process_memory_used: usize,
    peak_job_memory_used: usize,
}

#[repr(C)]
struct Overlapped {
    internal: usize,
    internal_high: usize,
    offset: u32,
    offset_high: u32,
    event: *mut c_void,
}

#[link(name = "kernel32")]
unsafe extern "system" {
    fn CreateNamedPipeW(
        name: *const u16,
        open_mode: u32,
        pipe_mode: u32,
        max_instances: u32,
        out_buffer: u32,
        in_buffer: u32,
        timeout: u32,
        attributes: *const SecurityAttributes,
    ) -> *mut c_void;
    fn CreateFileW(
        name: *const u16,
        access: u32,
        share: u32,
        attributes: *const SecurityAttributes,
        creation: u32,
        flags: u32,
        template: *mut c_void,
    ) -> *mut c_void;
    fn GetNamedPipeClientProcessId(pipe: *mut c_void, pid: *mut u32) -> i32;
    fn GetLastError() -> u32;
    fn CreateEventW(
        attributes: *const SecurityAttributes,
        manual_reset: i32,
        initial_state: i32,
        name: *const u16,
    ) -> *mut c_void;
    fn ResetEvent(event: *mut c_void) -> i32;
    #[link_name = "WriteFile"]
    fn WriteFileAsync(
        file: *mut c_void,
        buffer: *const c_void,
        size: u32,
        written: *mut u32,
        overlapped: *mut Overlapped,
    ) -> i32;
    #[link_name = "ReadFile"]
    fn ReadFileAsync(
        file: *mut c_void,
        buffer: *mut c_void,
        size: u32,
        read: *mut u32,
        overlapped: *mut Overlapped,
    ) -> i32;
    fn GetOverlappedResult(
        file: *mut c_void,
        overlapped: *mut Overlapped,
        transferred: *mut u32,
        wait: i32,
    ) -> i32;
    fn CancelIoEx(file: *mut c_void, overlapped: *mut Overlapped) -> i32;
    fn SetHandleInformation(object: *mut c_void, mask: u32, flags: u32) -> i32;
    fn InitializeProcThreadAttributeList(
        list: *mut c_void,
        count: u32,
        flags: u32,
        size: *mut usize,
    ) -> i32;
    fn UpdateProcThreadAttribute(
        list: *mut c_void,
        flags: u32,
        attribute: usize,
        value: *const c_void,
        size: usize,
        previous: *mut c_void,
        return_size: *mut usize,
    ) -> i32;
    fn DeleteProcThreadAttributeList(list: *mut c_void);
    fn CreateProcessW(
        application: *const u16,
        command_line: *mut u16,
        process_attributes: *const SecurityAttributes,
        thread_attributes: *const SecurityAttributes,
        inherit_handles: i32,
        flags: u32,
        environment: *const c_void,
        current_directory: *const u16,
        startup: *const StartupInfoW,
        information: *mut ProcessInformation,
    ) -> i32;
    fn ResumeThread(thread: *mut c_void) -> u32;
    fn CreateJobObjectW(attributes: *const SecurityAttributes, name: *const u16) -> *mut c_void;
    fn SetInformationJobObject(
        job: *mut c_void,
        class: u32,
        information: *const c_void,
        length: u32,
    ) -> i32;
    fn AssignProcessToJobObject(job: *mut c_void, process: *mut c_void) -> i32;
    fn GetModuleHandleExW(flags: u32, address: *const u16, module: *mut *mut c_void) -> i32;
    fn GetModuleFileNameW(module: *mut c_void, path: *mut u16, size: u32) -> u32;
}

#[repr(C, packed)]
struct CrashpadExceptionInformation {
    exception_pointers: u64,
    thread_id: u32,
}

type V8UnhandledCallback = unsafe extern "C" fn(*mut EXCEPTION_POINTERS) -> i32;
#[cfg(v8_bridge)]
unsafe extern "C" {
    fn onekey_install_v8_callback(symbol: *const c_void, callback: V8UnhandledCallback) -> i32;
    fn onekey_crash_level_one() -> i32;
}
const V8_SETTER_EXPORT: &[u8] =
    b"?SetUnhandledExceptionCallback@V8@v8@@SAXP6AHPEAU_EXCEPTION_POINTERS@@@Z@Z\0";

#[cfg(v8_bridge)]
fn resolve_v8_setter() -> Result<*const c_void, &'static str> {
    let module = unsafe { GetModuleHandleW(None) }.map_err(|_| "v8_module")?;
    let symbol = unsafe { GetProcAddress(module, PCSTR(V8_SETTER_EXPORT.as_ptr())) }
        .ok_or("v8_callback_export")?;
    // Fixed Electron 44.5.1 x64 C++ export. The JS gate rejects other builds and architectures.
    Ok(symbol as *const c_void)
}

#[cfg(not(v8_bridge))]
fn resolve_v8_setter() -> Result<*const c_void, &'static str> {
    Err("v8_bridge_unavailable")
}

#[cfg(v8_bridge)]
pub fn trigger_local_crash_for_test() {
    unsafe {
        onekey_crash_level_one();
    }
}

#[cfg(not(v8_bridge))]
pub fn trigger_local_crash_for_test() -> napi::Result<()> {
    Err(napi::Error::from_reason("v8_bridge_unavailable"))
}

fn handle(raw: usize) -> HANDLE {
    HANDLE(raw as *mut c_void)
}
fn hex32(value: u32) -> String {
    format!("0x{value:08x}")
}

unsafe fn terminate_crashed_process(code: u32) -> ! {
    let handler = CLIENT_HANDLER.load(Ordering::Acquire);
    if handler != 0 {
        let _ = TerminateProcess(handle(handler), 1);
    }
    let _ = TerminateProcess(GetCurrentProcess(), if code == 0 { 1 } else { code });
    std::process::abort();
}

// This is an actual unhandled-exception filter. It does not observe FailFast or hangs.
unsafe extern "system" fn unhandled_exception(info: *const EXCEPTION_POINTERS) -> i32 {
    if info.is_null() || CRASH_SENT.swap(true, Ordering::AcqRel) {
        terminate_crashed_process(1);
    }
    let write = CLIENT_WRITE.load(Ordering::Acquire);
    let read = CLIENT_READ.load(Ordering::Acquire);
    if write == 0 || read == 0 {
        terminate_crashed_process(1);
    }
    let code = if (*info).ExceptionRecord.is_null() {
        0
    } else {
        (*(*info).ExceptionRecord).ExceptionCode.0 as u32
    };
    let crashpad_info = CrashpadExceptionInformation {
        exception_pointers: info as usize as u64,
        thread_id: GetCurrentThreadId(),
    };
    let mut packet = [0u8; 32];
    packet[0..4].copy_from_slice(&CRASH_MAGIC.to_le_bytes());
    packet[4..8].copy_from_slice(&GetCurrentProcessId().to_le_bytes());
    packet[8..12].copy_from_slice(&GetCurrentThreadId().to_le_bytes());
    packet[12..20].copy_from_slice(&(info as usize as u64).to_le_bytes());
    packet[20..24].copy_from_slice(&code.to_le_bytes());
    packet[24..32].copy_from_slice(&(&crashpad_info as *const _ as u64).to_le_bytes());
    let deadline = windows::Win32::System::SystemInformation::GetTickCount64() + 30_000;
    let event = CLIENT_IO_EVENT.load(Ordering::Acquire) as *mut c_void;
    if event.is_null() {
        terminate_crashed_process(code);
    }
    let mut ack = [0u8; 1];
    // Both operations use the same absolute deadline. On timeout the callback
    // kills the process, so stack buffers and OVERLAPPED remain live until exit.
    if transfer_crash(
        write as *mut c_void,
        &mut packet,
        true,
        event,
        deadline,
        code,
    )
    .is_err()
        || transfer_crash(read as *mut c_void, &mut ack, false, event, deadline, code).is_err()
    {
        terminate_crashed_process(code);
    }
    std::hint::black_box(&crashpad_info); // Keep target-side exception metadata live through ACK.
                                          // The process has already faulted. Exit immediately after ACK or timeout;
                                          // returning to Windows Error Reporting can exceed the 35-second budget.
    terminate_crashed_process(code);
}

unsafe extern "C" fn v8_unhandled_exception(info: *mut EXCEPTION_POINTERS) -> i32 {
    let code = if info.is_null() || (*info).ExceptionRecord.is_null() {
        1
    } else {
        (*(*info).ExceptionRecord).ExceptionCode.0 as u32
    };
    let _ = unhandled_exception(info);
    // V8's callback returns an SEH disposition, not a Windows UEF action. Never
    // resume the faulting instruction after our local handler has replied.
    let _ = TerminateProcess(GetCurrentProcess(), code);
    std::process::abort();
}

fn creation_sd(sid: &str, access: u32) -> Result<LocalSd, &'static str> {
    let sddl = format!(
        "D:P(D;;0x00040000;;;S-1-3-4)(A;;0x{access:08x};;;{sid})(A;;0x{access:08x};;;S-1-5-18)"
    );
    let wide: Vec<u16> = sddl.encode_utf16().chain(std::iter::once(0)).collect();
    let mut descriptor = PSECURITY_DESCRIPTOR::default();
    unsafe {
        ConvertStringSecurityDescriptorToSecurityDescriptorW(
            PCWSTR(wide.as_ptr()),
            SDDL_REVISION_1,
            &mut descriptor,
            None,
        )
    }
    .map_err(|_| "creation_sd")?;
    Ok(LocalSd(descriptor))
}

fn raw(file: &File) -> *mut c_void {
    file.as_raw_handle()
}

fn named_pipe(parent_reads: bool) -> Result<(File, File), &'static str> {
    let sequence = PIPE_SEQUENCE.fetch_add(1, Ordering::Relaxed);
    let tick = unsafe { windows::Win32::System::SystemInformation::GetTickCount64() };
    let name = format!(
        r"\\.\pipe\OneKeyCrash-{}-{tick:016x}-{sequence:x}",
        unsafe { GetCurrentProcessId() }
    );
    let wide: Vec<u16> = name.encode_utf16().chain(std::iter::once(0)).collect();
    let server_mode = (if parent_reads {
        PIPE_ACCESS_INBOUND
    } else {
        PIPE_ACCESS_OUTBOUND
    }) | FILE_FLAG_OVERLAPPED
        | FILE_FLAG_FIRST_PIPE_INSTANCE;
    let server_raw = unsafe {
        CreateNamedPipeW(
            wide.as_ptr(),
            server_mode,
            PIPE_REJECT_REMOTE_CLIENTS,
            1,
            4096,
            4096,
            0,
            ptr::null(),
        )
    };
    if server_raw == usize::MAX as *mut c_void {
        return Err("handler_pipe_server");
    }
    let server = unsafe { File::from_raw_handle(server_raw) };
    let attributes = SecurityAttributes {
        length: std::mem::size_of::<SecurityAttributes>() as u32,
        descriptor: ptr::null_mut(),
        inherit: 1,
    };
    let client_raw = unsafe {
        CreateFileW(
            wide.as_ptr(),
            if parent_reads {
                GENERIC_WRITE
            } else {
                GENERIC_READ
            },
            0,
            &attributes,
            OPEN_EXISTING,
            0,
            ptr::null_mut(),
        )
    };
    if client_raw == usize::MAX as *mut c_void {
        return Err("handler_pipe_client");
    }
    let client = unsafe { File::from_raw_handle(client_raw) };
    let mut client_pid = 0;
    if unsafe { GetNamedPipeClientProcessId(raw(&server), &mut client_pid) } == 0
        || client_pid != unsafe { GetCurrentProcessId() }
    {
        return Err("handler_pipe_identity");
    }
    Ok((server, client))
}

unsafe fn transfer_crash(
    file: *mut c_void,
    buffer: &mut [u8],
    write: bool,
    event: *mut c_void,
    deadline: u64,
    code: u32,
) -> Result<(), ()> {
    if windows::Win32::System::SystemInformation::GetTickCount64() >= deadline {
        return Err(());
    }
    if ResetEvent(event) == 0 {
        return Err(());
    }
    let mut op = Overlapped {
        internal: 0,
        internal_high: 0,
        offset: 0,
        offset_high: 0,
        event,
    };
    let started = if write {
        WriteFileAsync(
            file,
            buffer.as_ptr().cast(),
            buffer.len() as u32,
            ptr::null_mut(),
            &mut op,
        )
    } else {
        ReadFileAsync(
            file,
            buffer.as_mut_ptr().cast(),
            buffer.len() as u32,
            ptr::null_mut(),
            &mut op,
        )
    };
    if started == 0 && GetLastError() != ERROR_IO_PENDING {
        return Err(());
    }
    if started == 0 {
        let now = windows::Win32::System::SystemInformation::GetTickCount64();
        let remaining = deadline.saturating_sub(now).min(u32::MAX as u64) as u32;
        if remaining == 0 || WaitForSingleObject(handle(event as usize), remaining).0 != 0 {
            CancelIoEx(file, &mut op);
            // The pending I/O still references this stack frame. Do not return.
            terminate_crashed_process(code);
        }
    }
    let mut transferred = 0;
    if GetOverlappedResult(file, &mut op, &mut transferred, 0) == 0 {
        // Cancellation/completion may still be pending. Keep this frame alive
        // until process termination, even when the helper has already died.
        terminate_crashed_process(code);
    }
    if transferred != buffer.len() as u32
        || windows::Win32::System::SystemInformation::GetTickCount64() > deadline
    {
        return Err(());
    }
    Ok(())
}

fn transfer_startup(
    file: &File,
    bytes: Box<[u8]>,
    write: bool,
    event: *mut c_void,
    deadline: u64,
) -> Result<Box<[u8]>, &'static str> {
    let mut bytes = bytes;
    let mut op = Box::new(Overlapped {
        internal: 0,
        internal_high: 0,
        offset: 0,
        offset_high: 0,
        event,
    });
    unsafe {
        if windows::Win32::System::SystemInformation::GetTickCount64() >= deadline {
            return Err("handler_io_timeout");
        }
        if ResetEvent(event) == 0 {
            return Err("handler_io_event");
        }
        let started = if write {
            WriteFileAsync(
                raw(file),
                bytes.as_ptr().cast(),
                bytes.len() as u32,
                ptr::null_mut(),
                &mut *op,
            )
        } else {
            ReadFileAsync(
                raw(file),
                bytes.as_mut_ptr().cast(),
                bytes.len() as u32,
                ptr::null_mut(),
                &mut *op,
            )
        };
        if started == 0 && GetLastError() != ERROR_IO_PENDING {
            return Err("handler_io_start");
        }
        if started == 0 {
            let now = windows::Win32::System::SystemInformation::GetTickCount64();
            let remaining = deadline.saturating_sub(now).min(u32::MAX as u64) as u32;
            if remaining == 0 || WaitForSingleObject(handle(event as usize), remaining).0 != 0 {
                CancelIoEx(raw(file), &mut *op);
                // Cancellation is asynchronous. Keep the OVERLAPPED and buffer
                // alive instead of waiting without a deadline on app startup.
                std::mem::forget(op);
                std::mem::forget(bytes);
                return Err("handler_io_timeout");
            }
        }
        let mut transferred = 0;
        if GetOverlappedResult(raw(file), &mut *op, &mut transferred, 0) == 0 {
            CancelIoEx(raw(file), &mut *op);
            std::mem::forget(op);
            std::mem::forget(bytes);
            return Err("handler_io_result");
        }
        if transferred != bytes.len() as u32 {
            return Err("handler_io_short");
        }
        if windows::Win32::System::SystemInformation::GetTickCount64() > deadline {
            return Err("handler_io_timeout");
        }
    }
    Ok(bytes)
}

fn gate_before_resume(
    output: &Path,
    pid: u32,
    tid: u32,
    initial: bool,
) -> Result<(), &'static str> {
    if std::env::var("DESKTOP_E2E_MODE").as_deref() != Ok("true")
        || std::env::var("ONEKEY_TEST_LOCAL_CRASH_HANDLER").as_deref() != Ok("1")
        || std::env::var("ONEKEY_TEST_HELPER_CREATE_GATE").as_deref() != Ok("1")
    {
        return Ok(());
    }
    let nonce = format!(
        "{}-{:08x}{:08x}{:016x}",
        if initial { "initial" } else { "secured" },
        unsafe { GetCurrentProcessId() },
        pid,
        unsafe { windows::Win32::System::SystemInformation::GetTickCount64() }
    );
    let prefix = if initial {
        "create-gate-initial"
    } else {
        "create-gate"
    };
    let ready = output.join(format!("{prefix}-ready.json"));
    let ready_pending = output.join(format!("{prefix}-ready.pending"));
    let release = output.join(format!("{prefix}-release.json"));
    let mut file = OpenOptions::new()
        .write(true)
        .create_new(true)
        .open(&ready_pending)
        .map_err(|_| "gate_ready")?;
    write!(
        file,
        "{{\"helperPid\":{pid},\"initialThreadId\":{tid},\"nonce\":\"{nonce}\"}}"
    )
    .and_then(|_| file.sync_all())
    .map_err(|_| "gate_ready_write")?;
    drop(file);
    fs::rename(ready_pending, ready).map_err(|_| "gate_ready_publish")?;
    let start = Instant::now();
    loop {
        if let Ok(metadata) = fs::metadata(&release) {
            if metadata.len() > 128 {
                return Err("gate_release_invalid");
            }
            let value = fs::read_to_string(&release).map_err(|_| "gate_release_read")?;
            if value.trim() == format!("{{\"nonce\":\"{nonce}\"}}") {
                return Ok(());
            }
            return Err("gate_release_invalid");
        }
        if start.elapsed().as_secs() >= 20 {
            return Err("gate_release_timeout");
        }
        unsafe { Sleep(20) };
    }
}

fn binding_directory() -> Result<PathBuf, &'static str> {
    let mut module = ptr::null_mut();
    let flags =
        GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS | GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT;
    if unsafe { GetModuleHandleExW(flags, start as *const () as *const u16, &mut module) } == 0 {
        return Err("binding_module");
    }
    let mut buffer = [0u16; 32768];
    let len =
        unsafe { GetModuleFileNameW(module, buffer.as_mut_ptr(), buffer.len() as u32) } as usize;
    if len == 0 || len >= buffer.len() {
        return Err("binding_path");
    }
    PathBuf::from(OsString::from_wide(&buffer[..len]))
        .parent()
        .map(Path::to_path_buf)
        .ok_or("binding_path")
}

pub fn start(
    handler_path: &Path,
    profile: &Path,
    user_sid: &str,
    version: &str,
) -> Result<u32, &'static str> {
    if !cfg!(target_arch = "x86_64") {
        return Err("unsupported_arch");
    }
    if CLIENT_WRITE.load(Ordering::Acquire) != 0 {
        return Err("already_started");
    }
    if !handler_path.is_absolute() || !profile.is_absolute() {
        return Err("path_not_absolute");
    }
    let arch = if cfg!(target_arch = "x86_64") {
        "x64"
    } else {
        "arm64"
    };
    let expected = format!("onekey-crash-prototype.win32-{arch}-msvc.exe");
    let package_directory =
        std::fs::canonicalize(binding_directory()?).map_err(|_| "binding_path")?;
    let executable_path = std::fs::canonicalize(package_directory.join(&expected))
        .map_err(|_| "wrong_handler_path")?;
    if executable_path.parent() != Some(package_directory.as_path())
        || std::fs::canonicalize(handler_path).map_err(|_| "wrong_handler_path")? != executable_path
    {
        return Err("wrong_handler_path");
    }
    if !package_directory.join(CAPTURE_DLL_NAME).is_file() {
        return Err("capture_component_missing");
    }
    if !profile.is_dir()
        || !version
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b".-".contains(&b))
        || version.len() > 32
    {
        return Err("invalid_profile_or_version");
    }
    let profile_text = profile.to_str().ok_or("profile_encoding")?.as_bytes();
    if profile_text.len() > 2048 || user_sid.len() > 256 {
        return Err("init_length");
    }
    let v8_setter = resolve_v8_setter()?;
    #[cfg(not(v8_bridge))]
    let _ = v8_setter;

    // The directory is created before the helper starts. Its filesystem ACL
    // remains the profile's ordinary ACL even if helper token defaults change.
    let output = profile.join("local-crash-prototype");
    fs::create_dir(&output).map_err(|_| "output_directory_not_new")?;

    // Creation-time process DACL has no QUERY_LIMITED_INFORMATION, so a
    // same-user observer cannot open the new process token during setup.
    let process_sd = creation_sd(user_sid, 0x0010_0001)?;
    let thread_sd = creation_sd(user_sid, 0x0010_0800)?;
    let process_attributes = SecurityAttributes {
        length: std::mem::size_of::<SecurityAttributes>() as u32,
        descriptor: process_sd.0 .0,
        inherit: 0,
    };
    let thread_attributes = SecurityAttributes {
        length: std::mem::size_of::<SecurityAttributes>() as u32,
        descriptor: thread_sd.0 .0,
        inherit: 0,
    };
    let (parent_stdin, child_stdin) = named_pipe(false)?;
    let (parent_stdout, child_stdout) = named_pipe(true)?;
    let io_event_raw = unsafe { CreateEventW(ptr::null(), 1, 0, ptr::null()) };
    if io_event_raw.is_null() {
        return Err("handler_io_event_create");
    }
    // Retained for the callback; on startup cancellation it must also remain
    // alive because the kernel may still signal a pending OVERLAPPED request.
    let stderr = OpenOptions::new()
        .write(true)
        .open("NUL")
        .map_err(|_| "handler_stderr")?;
    for parent in [&parent_stdin, &parent_stdout] {
        if unsafe { SetHandleInformation(raw(parent), HANDLE_FLAG_INHERIT, 0) } == 0 {
            return Err("handler_pipe_inherit");
        }
    }
    if unsafe { SetHandleInformation(raw(&stderr), HANDLE_FLAG_INHERIT, HANDLE_FLAG_INHERIT) } == 0
    {
        return Err("handler_stderr_inherit");
    }
    let mut inherited = [raw(&child_stdin), raw(&child_stdout), raw(&stderr)];
    let mut attributes_size = 0usize;
    unsafe { InitializeProcThreadAttributeList(ptr::null_mut(), 1, 0, &mut attributes_size) };
    if attributes_size == 0 {
        return Err("handler_attributes_size");
    }
    let mut attributes = vec![0usize; attributes_size.div_ceil(std::mem::size_of::<usize>())];
    let attribute_ptr = attributes.as_mut_ptr().cast::<c_void>();
    if unsafe { InitializeProcThreadAttributeList(attribute_ptr, 1, 0, &mut attributes_size) } == 0
    {
        return Err("handler_attributes_init");
    }
    let updated = unsafe {
        UpdateProcThreadAttribute(
            attribute_ptr,
            0,
            PROC_THREAD_ATTRIBUTE_HANDLE_LIST,
            inherited.as_mut_ptr().cast(),
            std::mem::size_of_val(&inherited),
            ptr::null_mut(),
            ptr::null_mut(),
        )
    };
    if updated == 0 {
        unsafe { DeleteProcThreadAttributeList(attribute_ptr) };
        return Err("handler_attributes_update");
    }
    let mut startup: StartupInfoExW = unsafe { std::mem::zeroed() };
    startup.startup.cb = std::mem::size_of::<StartupInfoExW>() as u32;
    startup.startup.flags = STARTF_USESTDHANDLES;
    startup.startup.stdin = raw(&child_stdin);
    startup.startup.stdout = raw(&child_stdout);
    startup.startup.stderr = raw(&stderr);
    startup.attributes = attribute_ptr;
    let executable: Vec<u16> = executable_path
        .as_os_str()
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    let directory: Vec<u16> = executable_path
        .parent()
        .ok_or("handler_path")?
        .as_os_str()
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    let mut information: ProcessInformation = unsafe { std::mem::zeroed() };
    let created = unsafe {
        CreateProcessW(
            executable.as_ptr(),
            ptr::null_mut(),
            &process_attributes,
            &thread_attributes,
            1,
            DETACHED_PROCESS | CREATE_SUSPENDED | EXTENDED_STARTUPINFO_PRESENT,
            ptr::null(),
            directory.as_ptr(),
            &startup.startup,
            &mut information,
        )
    };
    unsafe { DeleteProcThreadAttributeList(attribute_ptr) };
    if created == 0 {
        return Err("handler_spawn");
    }
    let child_process = unsafe { File::from_raw_handle(information.process) };
    let child_thread = unsafe { File::from_raw_handle(information.thread) };
    drop(child_stdin);
    drop(child_stdout);
    drop(stderr);

    // Only the helper belongs to this anonymous job. The parent alone retains
    // its non-inheritable handle, so parent exit kills a hung helper in-kernel.
    let job_raw = unsafe { CreateJobObjectW(ptr::null(), ptr::null()) };
    if job_raw.is_null() {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("handler_job_create");
    }
    let job = unsafe { File::from_raw_handle(job_raw) };
    let mut limits: JobExtendedLimitInformation = unsafe { std::mem::zeroed() };
    limits.basic.flags = JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE;
    if unsafe {
        SetInformationJobObject(
            raw(&job),
            JOB_OBJECT_EXTENDED_LIMIT_INFORMATION,
            (&limits as *const JobExtendedLimitInformation).cast(),
            std::mem::size_of::<JobExtendedLimitInformation>() as u32,
        )
    } == 0
        || unsafe { AssignProcessToJobObject(raw(&job), raw(&child_process)) } == 0
    {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("handler_job_assign");
    }
    // The creation handle has PROCESS_DUP_HANDLE but not WRITE_DAC under the
    // initial no-QLI DACL. Duplicate the child's pseudo process handle into
    // this parent with only READ_CONTROL|WRITE_DAC for the final transition.
    let mut control_raw = HANDLE::default();
    if unsafe {
        DuplicateHandle(
            handle(raw(&child_process) as usize),
            GetCurrentProcess(),
            GetCurrentProcess(),
            &mut control_raw,
            0x0006_0000,
            false,
            DUPLICATE_HANDLE_OPTIONS(0),
        )
    }
    .is_err()
    {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("helper_control_duplicate");
    }
    let control = unsafe { File::from_raw_handle(control_raw.0) };
    if !unsafe { CompareObjectHandles(control_raw, handle(raw(&child_process) as usize)) }.as_bool()
    {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("helper_control_identity");
    }
    let mut control_flags = 0u32;
    if unsafe { GetHandleInformation(control_raw, &mut control_flags) }.is_err()
        || control_flags & HANDLE_FLAG_INHERIT != 0
    {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("helper_control_inherit");
    }
    if let Err(stage) = gate_before_resume(&output, information.pid, information.tid, true) {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err(stage);
    }
    if let Err(stage) = secure_helper_token(&child_process, &control, user_sid) {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err(stage);
    }
    drop(control);
    if let Err(stage) = gate_before_resume(&output, information.pid, information.tid, false) {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err(stage);
    }
    if unsafe { ResumeThread(raw(&child_thread)) } == u32::MAX {
        let _ = unsafe { TerminateProcess(handle(raw(&child_process) as usize), 1) };
        return Err("handler_resume");
    }
    drop(child_thread);

    let mut init = Vec::with_capacity(28 + profile_text.len() + user_sid.len() + version.len());
    init.extend_from_slice(&INIT_MAGIC.to_le_bytes());
    init.extend_from_slice(&unsafe { GetCurrentProcessId() }.to_le_bytes());
    init.extend_from_slice(&0u64.to_le_bytes());
    init.extend_from_slice(&(profile_text.len() as u32).to_le_bytes());
    init.extend_from_slice(&(user_sid.len() as u32).to_le_bytes());
    init.extend_from_slice(&(version.len() as u32).to_le_bytes());
    init.extend_from_slice(profile_text);
    init.extend_from_slice(user_sid.as_bytes());
    init.extend_from_slice(version.as_bytes());
    let init_deadline =
        unsafe { windows::Win32::System::SystemInformation::GetTickCount64() } + 5_000;
    transfer_startup(
        &parent_stdin,
        init.into_boxed_slice(),
        true,
        io_event_raw,
        init_deadline,
    )?;
    wait_ack(&parent_stdout, b'H', io_event_raw, init_deadline)?;
    if unsafe { GetProcessId(handle(raw(&child_process) as usize)) } != information.pid {
        return Err("handler_identity");
    }
    let mut duplicate = HANDLE::default();
    unsafe {
        DuplicateHandle(
            GetCurrentProcess(),
            GetCurrentProcess(),
            handle(raw(&child_process) as usize),
            &mut duplicate,
            CAPTURE_ACCESS,
            false,
            DUPLICATE_HANDLE_OPTIONS(0),
        )
    }
    .map_err(|_| "duplicate_process_handle")?;
    transfer_startup(
        &parent_stdin,
        Box::new((duplicate.0 as usize as u64).to_le_bytes()),
        true,
        io_event_raw,
        init_deadline,
    )?;
    wait_ack(&parent_stdout, b'R', io_event_raw, init_deadline)?;
    // Make the crash IPC valid before installing either callback. A crash
    // between the two registrations still has a runnable, bounded path.
    CLIENT_WRITE.store(raw(&parent_stdin) as usize, Ordering::Release);
    CLIENT_READ.store(raw(&parent_stdout) as usize, Ordering::Release);
    CLIENT_HANDLER.store(raw(&child_process) as usize, Ordering::Release);
    CLIENT_IO_EVENT.store(io_event_raw as usize, Ordering::Release);
    unsafe {
        #[cfg(v8_bridge)]
        if onekey_install_v8_callback(v8_setter, v8_unhandled_exception) != 1 {
            CLIENT_WRITE.store(0, Ordering::Release);
            CLIENT_READ.store(0, Ordering::Release);
            CLIENT_HANDLER.store(0, Ordering::Release);
            CLIENT_IO_EVENT.store(0, Ordering::Release);
            return Err("v8_callback_install");
        }
        SetUnhandledExceptionFilter(Some(unhandled_exception));
    }
    std::mem::forget(parent_stdin);
    std::mem::forget(parent_stdout);
    std::mem::forget(child_process);
    std::mem::forget(job);
    Ok(information.pid)
}

fn wait_ack(
    stdout: &File,
    expected: u8,
    event: *mut c_void,
    deadline: u64,
) -> Result<(), &'static str> {
    let ack = transfer_startup(stdout, Box::new([0u8; 1]), false, event, deadline)?;
    if ack[0] != expected {
        return Err("handler_not_ready");
    }
    Ok(())
}

struct LocalSd(PSECURITY_DESCRIPTOR);
impl Drop for LocalSd {
    fn drop(&mut self) {
        unsafe {
            LocalFree(Some(HLOCAL(self.0 .0)));
        }
    }
}

fn descriptor_acl(sd: &LocalSd) -> Result<(*mut ACL, Vec<u8>), &'static str> {
    let mut present = BOOL(0);
    let mut defaulted = BOOL(0);
    let mut acl = ptr::null_mut::<ACL>();
    unsafe { GetSecurityDescriptorDacl(sd.0, &mut present, &mut acl, &mut defaulted) }
        .map_err(|_| "helper_sd_acl")?;
    if !present.as_bool() || acl.is_null() {
        return Err("helper_sd_acl_missing");
    }
    let size = unsafe { (*acl).AclSize as usize };
    if !(8..=65535).contains(&size) {
        return Err("helper_sd_acl_size");
    }
    let bytes = unsafe { std::slice::from_raw_parts(acl.cast::<u8>(), size) }.to_vec();
    Ok((acl, bytes))
}

fn set_verified_dacl(object: HANDLE, sd: &LocalSd) -> Result<(), &'static str> {
    let (acl, expected) = descriptor_acl(sd)?;
    let code = unsafe {
        SetSecurityInfo(
            object,
            SE_KERNEL_OBJECT,
            DACL_SECURITY_INFORMATION | PROTECTED_DACL_SECURITY_INFORMATION,
            None,
            None,
            Some(acl.cast_const()),
            None,
        )
    };
    if code.0 != 0 {
        return Err("helper_set_dacl");
    }
    let actual = DaclSnapshot::read(object).map_err(|_| "helper_verify_dacl")?;
    if !actual.protected || actual.acl != expected {
        return Err("helper_dacl_mismatch");
    }
    Ok(())
}

fn token_id(token: HANDLE) -> Result<(u32, i32), &'static str> {
    let mut stats = TOKEN_STATISTICS::default();
    let mut returned = 0u32;
    unsafe {
        GetTokenInformation(
            token,
            TokenStatistics,
            Some((&mut stats as *mut TOKEN_STATISTICS).cast()),
            std::mem::size_of::<TOKEN_STATISTICS>() as u32,
            &mut returned,
        )
    }
    .map_err(|_| "helper_token_statistics")?;
    if returned < std::mem::size_of::<TOKEN_STATISTICS>() as u32 {
        return Err("helper_token_statistics_size");
    }
    Ok((stats.TokenId.LowPart, stats.TokenId.HighPart))
}

fn token_default_dacl(token: HANDLE) -> Result<Vec<u8>, &'static str> {
    let mut required = 0u32;
    let _ = unsafe { GetTokenInformation(token, TokenDefaultDacl, None, 0, &mut required) };
    if required < std::mem::size_of::<TOKEN_DEFAULT_DACL>() as u32 || required > 65544 {
        return Err("helper_token_default_size");
    }
    let mut aligned = vec![0usize; (required as usize).div_ceil(std::mem::size_of::<usize>())];
    let mut returned = 0u32;
    unsafe {
        GetTokenInformation(
            token,
            TokenDefaultDacl,
            Some(aligned.as_mut_ptr().cast()),
            required,
            &mut returned,
        )
    }
    .map_err(|_| "helper_token_default_read")?;
    if returned < std::mem::size_of::<TOKEN_DEFAULT_DACL>() as u32
        || returned as usize > aligned.len() * std::mem::size_of::<usize>()
    {
        return Err("helper_token_default_length");
    }
    let acl = unsafe { (*aligned.as_ptr().cast::<TOKEN_DEFAULT_DACL>()).DefaultDacl };
    if acl.is_null() {
        return Err("helper_token_default_null");
    }
    let start = aligned.as_ptr() as usize;
    let end = start
        .checked_add(returned as usize)
        .ok_or("helper_token_default_range")?;
    let acl_start = acl as usize;
    if acl_start < start
        || acl_start
            .checked_add(8)
            .ok_or("helper_token_default_range")?
            > end
    {
        return Err("helper_token_default_range");
    }
    let size = unsafe { (*acl).AclSize as usize };
    if !(8..=65535).contains(&size)
        || acl_start
            .checked_add(size)
            .ok_or("helper_token_default_range")?
            > end
    {
        return Err("helper_token_default_range");
    }
    Ok(unsafe { std::slice::from_raw_parts(acl.cast::<u8>(), size) }.to_vec())
}

fn secure_helper_token(
    child_process: &File,
    process_control: &File,
    sid: &str,
) -> Result<(), &'static str> {
    let process = handle(raw(child_process) as usize);
    let initial_sd = creation_sd(sid, 0x0010_0001)?;
    let (_, expected_initial) = descriptor_acl(&initial_sd)?;
    let initial = DaclSnapshot::read(process).map_err(|_| "helper_initial_dacl_read")?;
    if !initial.protected || initial.acl != expected_initial {
        return Err("helper_initial_dacl_mismatch");
    }

    let mut parent_token_handle = HANDLE::default();
    unsafe {
        OpenProcessToken(
            GetCurrentProcess(),
            TOKEN_ACCESS_MASK(0x0000_0008),
            &mut parent_token_handle,
        )
    }
    .map_err(|_| "parent_token_open")?;
    let parent_token = unsafe { File::from_raw_handle(parent_token_handle.0) };
    let mut child_token_handle = HANDLE::default();
    unsafe {
        OpenProcessToken(
            process,
            TOKEN_ACCESS_MASK(0x0006_0088),
            &mut child_token_handle,
        )
    }
    .map_err(|_| "helper_token_open")?;
    let child_token = unsafe { File::from_raw_handle(child_token_handle.0) };
    let parent = handle(raw(&parent_token) as usize);
    let child = handle(raw(&child_token) as usize);
    if token_id(parent)? == token_id(child)? {
        return Err("helper_token_not_independent");
    }
    let parent_default = token_default_dacl(parent)?;

    // The token default ACL also applies to future helper-created objects.
    // The parent created the output directory before this transition.
    let thread_default_sd = creation_sd(sid, 0x0010_0800)?;
    let (thread_acl, expected_thread_default) = descriptor_acl(&thread_default_sd)?;
    let default_info = TOKEN_DEFAULT_DACL {
        DefaultDacl: thread_acl,
    };
    unsafe {
        SetTokenInformation(
            child,
            TokenDefaultDacl,
            (&default_info as *const TOKEN_DEFAULT_DACL).cast(),
            std::mem::size_of::<TOKEN_DEFAULT_DACL>() as u32,
        )
    }
    .map_err(|_| "helper_token_default_set")?;
    if token_default_dacl(child)? != expected_thread_default {
        return Err("helper_token_default_mismatch");
    }

    // External same-user callers may query the token after QLI is granted,
    // but cannot alter its default ACL or object security descriptor.
    let token_sd = creation_sd(sid, 0x0000_0008)?;
    set_verified_dacl(child, &token_sd)?;
    if token_default_dacl(parent)? != parent_default {
        return Err("parent_token_default_changed");
    }

    // Only after both token ACLs are verified do we expose QLI to probes.
    let final_process_sd = creation_sd(sid, 0x0010_1001)?;
    set_verified_dacl(handle(raw(process_control) as usize), &final_process_sd)?;
    Ok(())
}

fn apply_handler_dacl(sid: &str) -> Result<(), u32> {
    let sddl =
        format!("D:P(D;;0x00040000;;;S-1-3-4)(A;;0x00101001;;;{sid})(A;;0x00101001;;;S-1-5-18)");
    let wide: Vec<u16> = sddl.encode_utf16().chain(std::iter::once(0)).collect();
    let mut sd = PSECURITY_DESCRIPTOR::default();
    unsafe {
        ConvertStringSecurityDescriptorToSecurityDescriptorW(
            PCWSTR(wide.as_ptr()),
            SDDL_REVISION_1,
            &mut sd,
            None,
        )
    }
    .map_err(|_| 1u32)?;
    let _owned = LocalSd(sd);
    let mut present = BOOL(0);
    let mut defaulted = BOOL(0);
    let mut acl = ptr::null_mut::<ACL>();
    unsafe { GetSecurityDescriptorDacl(sd, &mut present, &mut acl, &mut defaulted) }
        .map_err(|_| 2u32)?;
    if !present.as_bool() || acl.is_null() {
        return Err(3);
    }
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
    if code.0 != 0 {
        return Err(code.0);
    }
    Ok(())
}

struct DaclSnapshot {
    acl: Vec<u8>,
    protected: bool,
}
impl DaclSnapshot {
    fn read(process: HANDLE) -> Result<Self, u32> {
        let mut acl = ptr::null_mut::<ACL>();
        let mut sd = PSECURITY_DESCRIPTOR::default();
        let code = unsafe {
            GetSecurityInfo(
                process,
                SE_KERNEL_OBJECT,
                DACL_SECURITY_INFORMATION,
                None,
                None,
                Some(&mut acl),
                None,
                Some(&mut sd),
            )
        };
        if code.0 != 0 {
            return Err(code.0);
        }
        let sd = LocalSd(sd);
        if acl.is_null() {
            return Err(4);
        }
        let size = unsafe { (*acl).AclSize as usize };
        if !(8..=65535).contains(&size) {
            return Err(5);
        }
        let acl = unsafe { std::slice::from_raw_parts(acl.cast::<u8>(), size) }.to_vec();
        let mut control = 0u16;
        let mut revision = 0u32;
        unsafe { GetSecurityDescriptorControl(sd.0, &mut control, &mut revision) }
            .map_err(|_| 6u32)?;
        let protected = control & SE_DACL_PROTECTED.0 != 0;
        Ok(Self { acl, protected })
    }
    fn matches_current(&self, process: HANDLE) -> Result<bool, u32> {
        let after = Self::read(process)?;
        Ok(self.acl == after.acl && self.protected == after.protected)
    }
}

#[derive(Default)]
struct DumpBuffer(Vec<u8>);
fn prepare_dump_buffer() -> Result<DumpBuffer, u32> {
    let mut buffer = DumpBuffer::default();
    buffer
        .0
        .try_reserve_exact(MAX_DUMP)
        .map_err(|_| 0x8007000e_u32)?;
    buffer.0.resize(MAX_DUMP, 0);
    Ok(buffer)
}

type CaptureFn = unsafe extern "C" fn(HANDLE, u64, *mut u8, usize, *mut usize) -> i32;
fn capture_dump(process: HANDLE, exception_info: u64, buffer: &mut DumpBuffer) -> Result<(), u32> {
    let executable = std::env::current_exe().map_err(|_| 10u32)?;
    let dll = executable.parent().ok_or(10u32)?.join(CAPTURE_DLL_NAME);
    let wide: Vec<u16> = dll
        .as_os_str()
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    let module = unsafe {
        LoadLibraryExW(
            PCWSTR(wide.as_ptr()),
            None,
            LOAD_LIBRARY_SEARCH_DLL_LOAD_DIR | LOAD_LIBRARY_SEARCH_SYSTEM32,
        )
    }
    .map_err(|_| 11u32)?;
    let proc = unsafe { GetProcAddress(module, PCSTR(c"onekey_crashpad_capture".as_ptr().cast())) };
    let result = if let Some(proc) = proc {
        let capture: CaptureFn = unsafe { std::mem::transmute(proc) };
        let mut written = 0usize;
        let code = unsafe {
            capture(
                process,
                exception_info,
                buffer.0.as_mut_ptr(),
                buffer.0.len(),
                &mut written,
            )
        };
        if code == 0 && (32..=MAX_DUMP).contains(&written) {
            buffer.0.truncate(written);
            Ok(())
        } else {
            Err(if code == 0 { 12 } else { code as u32 })
        }
    } else {
        Err(13)
    };
    unsafe { FreeLibrary(module) }.map_err(|_| 14u32)?;
    result?;
    Ok(())
}

fn create_output_file(path: &Path, sd: &LocalSd) -> Result<File, &'static str> {
    let wide: Vec<u16> = path
        .as_os_str()
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    let attributes = SecurityAttributes {
        length: std::mem::size_of::<SecurityAttributes>() as u32,
        descriptor: sd.0 .0,
        inherit: 0,
    };
    let file = unsafe {
        CreateFileW(
            wide.as_ptr(),
            GENERIC_WRITE | 0x0001_0000,
            0,
            &attributes,
            1,
            0x0000_0080,
            ptr::null_mut(),
        )
    };
    if file == usize::MAX as *mut c_void {
        return Err("output_create");
    }
    Ok(unsafe { File::from_raw_handle(file) })
}

fn test_fault_stage(file_sd: &LocalSd, output: &Path, stage: &'static str) {
    if std::env::var("DESKTOP_E2E_MODE").as_deref() != Ok("true")
        || std::env::var("ONEKEY_TEST_LOCAL_CRASH_HANDLER").as_deref() != Ok("1")
        || std::env::var("ONEKEY_TEST_CRASH_HANG_STAGE").as_deref() != Ok(stage)
    {
        return;
    }
    let suspend = std::env::var("ONEKEY_TEST_CRASH_SUSPEND_HELPER").as_deref() == Ok("1");
    let mode = if suspend { "suspend" } else { "sleep" };
    let marker = output.join("fault-stage.json");
    let pending = output.join("fault-stage.pending");
    let published = create_output_file(&pending, file_sd)
        .and_then(|mut file| {
            write!(
                file,
                "{{\"stage\":\"{stage}\",\"helperPid\":{},\"mode\":\"{mode}\"}}",
                unsafe { GetCurrentProcessId() }
            )
            .and_then(|_| file.sync_all())
            .map_err(|_| "fault_marker_write")
        })
        .and_then(|_| fs::rename(&pending, &marker).map_err(|_| "fault_marker_publish"));
    if published.is_err() {
        write_status(
            file_sd,
            output,
            "fault_marker_failed",
            0,
            unsafe { GetCurrentProcessId() },
            false,
            None,
        );
        std::process::exit(90);
    }
    if suspend {
        type NtCreateProcessStateChange =
            unsafe extern "system" fn(*mut HANDLE, u32, *const c_void, HANDLE, u32) -> i32;
        type NtChangeProcessState =
            unsafe extern "system" fn(HANDLE, HANDLE, u32, *const c_void, usize, u32) -> i32;
        let name: Vec<u16> = "ntdll.dll"
            .encode_utf16()
            .chain(std::iter::once(0))
            .collect();
        let exports = unsafe { GetModuleHandleW(PCWSTR(name.as_ptr())) }
            .ok()
            .and_then(|module| unsafe {
                Some((
                    GetProcAddress(module, PCSTR(c"NtCreateProcessStateChange".as_ptr().cast()))?,
                    GetProcAddress(module, PCSTR(c"NtChangeProcessState".as_ptr().cast()))?,
                ))
            });
        let Some((create, change)) = exports else {
            write_status(
                file_sd,
                output,
                "suspend_failed",
                0,
                unsafe { GetCurrentProcessId() },
                false,
                None,
            );
            std::process::exit(91);
        };
        let create_state: NtCreateProcessStateChange = unsafe { std::mem::transmute(create) };
        let change_state: NtChangeProcessState = unsafe { std::mem::transmute(change) };
        let mut state = HANDLE::default();
        // Unnamed, non-inheritable object using the existing narrow token default DACL.
        let mut status =
            unsafe { create_state(&mut state, 1, ptr::null(), GetCurrentProcess(), 0) };
        if status >= 0 {
            // Keep the handle alive: closing it would undo the process freeze.
            status = unsafe { change_state(state, GetCurrentProcess(), 0, ptr::null(), 0, 0) };
        }
        write_status(
            file_sd,
            output,
            if status < 0 {
                "suspend_failed"
            } else {
                "suspend_unexpected_resume"
            },
            0,
            unsafe { GetCurrentProcessId() },
            false,
            Some(status as u32),
        );
        std::process::exit(if status < 0 { 91 } else { 92 });
    }
    unsafe { Sleep(60_000) };
}

fn range(bytes: &[u8], offset: usize, len: usize) -> Option<&[u8]> {
    bytes.get(offset..offset.checked_add(len)?)
}
fn u32_at(bytes: &[u8], offset: usize) -> Option<u32> {
    Some(u32::from_le_bytes(
        range(bytes, offset, 4)?.try_into().ok()?,
    ))
}
fn u64_at(bytes: &[u8], offset: usize) -> Option<u64> {
    Some(u64::from_le_bytes(
        range(bytes, offset, 8)?.try_into().ok()?,
    ))
}

// Rebuild a new JSON object from numeric minidump metadata. No raw stream or string is copied.
fn sanitize_dump(
    raw: &[u8],
    version: &str,
    expected_tid: u32,
    expected_code: u32,
) -> Result<Vec<u8>, &'static str> {
    if raw.len() < 32 || raw.len() > MAX_DUMP || u32_at(raw, 0) != Some(0x504d444d) {
        return Err("bad_header");
    }
    let count = u32_at(raw, 8).ok_or("bad_directory")? as usize;
    let directory = u32_at(raw, 12).ok_or("bad_directory")? as usize;
    if count == 0 || count > 64 {
        return Err("bad_directory");
    }
    range(
        raw,
        directory,
        count.checked_mul(12).ok_or("bad_directory")?,
    )
    .ok_or("bad_directory")?;
    let mut exception = None;
    let mut threads = None;
    let mut modules = None;
    for index in 0..count {
        let entry = directory + index * 12;
        let kind = u32_at(raw, entry).ok_or("bad_directory")?;
        let size = u32_at(raw, entry + 4).ok_or("bad_directory")? as usize;
        let rva = u32_at(raw, entry + 8).ok_or("bad_directory")? as usize;
        range(raw, rva, size).ok_or("bad_stream_range")?;
        let slot = match kind {
            6 => &mut exception,
            3 => &mut threads,
            4 => &mut modules,
            _ => continue,
        };
        if slot.replace((rva, size)).is_some() {
            return Err("duplicate_required_stream");
        }
    }
    let (e, esize) = exception.ok_or("missing_exception")?;
    let (t, tsize) = threads.ok_or("missing_threads")?;
    let (m, msize) = modules.ok_or("missing_modules")?;
    if esize < 168 {
        return Err("short_exception");
    }
    let tid = u32_at(raw, e).ok_or("bad_exception")?;
    let code = u32_at(raw, e + 8).ok_or("bad_exception")?;
    if tid != expected_tid || code != expected_code {
        return Err("exception_mismatch");
    }
    let address = u64_at(raw, e + 24).ok_or("bad_exception")?;
    let context_size = u32_at(raw, e + 160).ok_or("bad_context")? as usize;
    let context_rva = u32_at(raw, e + 164).ok_or("bad_context")? as usize;
    if context_size == 0 {
        return Err("missing_context");
    }
    range(raw, context_rva, context_size).ok_or("bad_context")?;
    let thread_count = u32_at(raw, t).ok_or("bad_threads")? as usize;
    if thread_count == 0
        || thread_count > 2048
        || tsize < 4 + thread_count.checked_mul(48).ok_or("bad_threads")?
    {
        return Err("bad_threads");
    }
    let mut found_thread = false;
    for i in 0..thread_count {
        let item = t + 4 + i * 48;
        if u32_at(raw, item) == Some(tid) {
            found_thread = true;
        }
        for loc in [32, 40] {
            let size = u32_at(raw, item + loc).ok_or("bad_thread_location")? as usize;
            let rva = u32_at(raw, item + loc + 4).ok_or("bad_thread_location")? as usize;
            if size > 0 {
                range(raw, rva, size).ok_or("bad_thread_location")?;
            }
        }
    }
    if !found_thread {
        return Err("unknown_exception_thread");
    }
    let module_count = u32_at(raw, m).ok_or("bad_modules")? as usize;
    if module_count == 0
        || module_count > 4096
        || msize < 4 + module_count.checked_mul(108).ok_or("bad_modules")?
    {
        return Err("bad_modules");
    }
    let mut module = None;
    let mut relative = None;
    for i in 0..module_count {
        let item = m + 4 + i * 108;
        let base = u64_at(raw, item).ok_or("bad_module")?;
        let size = u32_at(raw, item + 8).ok_or("bad_module")? as u64;
        if size == 0 {
            return Err("bad_module");
        }
        let name_rva = u32_at(raw, item + 20).ok_or("bad_module")? as usize;
        let text_bytes = u32_at(raw, name_rva).ok_or("bad_module_name")? as usize;
        if text_bytes > 1024 || !text_bytes.is_multiple_of(2) {
            return Err("bad_module_name");
        }
        let text = range(raw, name_rva + 4, text_bytes).ok_or("bad_module_name")?;
        if address < base || address >= base.saturating_add(size) {
            continue;
        }
        let name: Vec<u16> = text
            .as_chunks::<2>()
            .0
            .iter()
            .map(|chunk| u16::from_le_bytes([chunk[0], chunk[1]]))
            .collect();
        let path = String::from_utf16(&name).map_err(|_| "bad_module_name")?;
        let basename = path.rsplit(['\\', '/']).next().unwrap_or("");
        module = match basename.to_ascii_lowercase().as_str() {
            "onekey.exe" => Some("OneKey.exe"),
            "electron.exe" => Some("electron.exe"),
            "electron-windows-process-hardening.win32-x64-msvc.node" => {
                Some("process-hardening.node")
            }
            "ntdll.dll" => Some("ntdll.dll"),
            "kernelbase.dll" => Some("KERNELBASE.dll"),
            _ => None,
        };
        if module.is_some() {
            relative = Some(address - base);
        }
        break;
    }
    let module_json = module.map_or("null".to_owned(), |value| format!("\"{value}\""));
    let relative_json = relative.map_or("null".to_owned(), |value| value.to_string());
    let created = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map_err(|_| "clock")?
        .as_millis();
    // Version was validated at startup; all other values are fixed labels or numbers.
    Ok(format!("{{\"schema\":1,\"createdAtUnixMs\":{created},\"appVersion\":\"{version}\",\"exceptionCode\":\"{}\",\"moduleId\":{module_json},\"moduleRva\":{relative_json}}}", hex32(code)).into_bytes())
}

#[cfg(target_arch = "x86_64")]
fn sanitize_dump_for_report(
    raw: &[u8],
    version: &str,
    expected_tid: u32,
    expected_code: u32,
    target: HANDLE,
    output: &Path,
    file_sd: &LocalSd,
) -> Result<Vec<u8>, &'static str> {
    let mut report = sanitize_dump(raw, version, expected_tid, expected_code)?;
    let count = u32_at(raw, 8).ok_or("directory")? as usize;
    let directory = u32_at(raw, 12).ok_or("directory")? as usize;
    let mut exception = None;
    let mut threads = None;
    let mut modules = None;
    for i in 0..count {
        let item = directory + i * 12;
        let location = (
            u32_at(raw, item + 8).ok_or("directory")? as usize,
            u32_at(raw, item + 4).ok_or("directory")? as usize,
        );
        match u32_at(raw, item).ok_or("directory")? {
            6 => exception = Some(location),
            3 => threads = Some(location),
            4 => modules = Some(location),
            _ => {}
        }
    }
    let (e, _) = exception.ok_or("exception")?;
    let (t, ts) = threads.ok_or("threads")?;
    let (m, ms) = modules.ok_or("modules")?;
    let context_size = u32_at(raw, e + 160).ok_or("context")? as usize;
    let context_rva = u32_at(raw, e + 164).ok_or("context")? as usize;
    let package_dir = std::env::current_exe().map_err(|_| "handler_path")?;
    let package_dir = package_dir.parent().ok_or("handler_path")?;
    test_fault_stage(file_sd, output, "unwind");
    let frames = crate::unwind::threads_json(
        raw,
        t,
        ts,
        m,
        ms,
        expected_tid,
        context_rva,
        context_size,
        target,
        package_dir,
    )?;
    if report.pop() != Some(b'}') {
        return Err("json_shape");
    }
    report.extend_from_slice(
        b",\"captureBackend\":\"crashpad-snapshot\",\"daclUnchanged\":true,\"threads\":",
    );
    report.extend_from_slice(frames.as_bytes());
    report.push(b'}');
    Ok(report)
}

fn write_status(
    file_sd: &LocalSd,
    output: &Path,
    stage: &str,
    target: u32,
    handler: u32,
    dacl_unchanged: bool,
    error: Option<u32>,
) {
    write_status_reason(
        file_sd,
        output,
        stage,
        target,
        handler,
        dacl_unchanged,
        error,
        None,
    );
}

#[allow(clippy::too_many_arguments)]
fn write_status_reason(
    file_sd: &LocalSd,
    output: &Path,
    stage: &str,
    target: u32,
    handler: u32,
    dacl_unchanged: bool,
    error: Option<u32>,
    reason: Option<&'static str>,
) {
    let error = error.map_or("null".to_owned(), |code| code.to_string());
    let reason = match reason {
        Some("no_verified_images") => "\"no_verified_images\"",
        Some("exception_unwind") => "\"exception_unwind\"",
        Some("no_runtime_function") => "\"no_runtime_function\"",
        Some("current_pc_unverified") => "\"current_pc_unverified\"",
        Some("leaf_return_missing") => "\"leaf_return_missing\"",
        Some("leaf_return_untrusted") => "\"leaf_return_untrusted\"",
        Some("stackwalk_read_miss") => "\"stackwalk_read_miss\"",
        Some("stackwalk_failed") => "\"stackwalk_failed\"",
        Some("no_progress") => "\"no_progress\"",
        Some("function_table_unseen") => "\"function_table_unseen\"",
        Some("next_pc_untrusted") => "\"next_pc_untrusted\"",
        Some("context_pc_untrusted") => "\"context_pc_untrusted\"",
        Some("context_not_executable") => "\"context_not_executable\"",
        Some("context_image_unverified") => "\"context_image_unverified\"",
        Some("context_module_unapproved") => "\"context_module_unapproved\"",
        Some("context_module_unlisted") => "\"context_module_unlisted\"",
        Some("sym_initialize") => "\"sym_initialize\"",
        Some("target_path") => "\"target_path\"",
        Some("injected") => "\"injected\"",
        Some(_) => "\"sanitize_failed\"",
        None => "null",
    };
    let content = format!("{{\"stage\":\"{stage}\",\"targetPid\":{target},\"handlerPid\":{handler},\"daclUnchanged\":{dacl_unchanged},\"captureBackend\":\"crashpad-snapshot\",\"win32Error\":{error},\"reason\":{reason}}}");
    if let Ok(mut file) = create_output_file(&output.join(STATUS_NAME), file_sd) {
        let _ = file.write_all(content.as_bytes());
        let _ = file.sync_all();
    }
}

fn read_init(reader: &mut impl Read) -> Result<(u32, PathBuf, String, String), &'static str> {
    let mut header = [0u8; 28];
    reader.read_exact(&mut header).map_err(|_| "init_read")?;
    if u32_at(&header, 0) != Some(INIT_MAGIC) {
        return Err("init_magic");
    }
    let target = u32_at(&header, 4).ok_or("init_pid")?;
    if u64_at(&header, 8) != Some(0) {
        return Err("early_handle");
    }
    let path_len = u32_at(&header, 16).ok_or("init_path")? as usize;
    let sid_len = u32_at(&header, 20).ok_or("init_sid")? as usize;
    let version_len = u32_at(&header, 24).ok_or("init_version")? as usize;
    if path_len == 0 || path_len > 2048 || sid_len == 0 || sid_len > 256 || version_len > 32 {
        return Err("init_length");
    }
    let mut body = vec![0u8; path_len + sid_len + version_len];
    reader.read_exact(&mut body).map_err(|_| "init_body")?;
    let profile =
        PathBuf::from(std::str::from_utf8(&body[..path_len]).map_err(|_| "init_path_encoding")?);
    let sid = std::str::from_utf8(&body[path_len..path_len + sid_len])
        .map_err(|_| "init_sid_encoding")?
        .to_owned();
    let version = std::str::from_utf8(&body[path_len + sid_len..])
        .map_err(|_| "init_version_encoding")?
        .to_owned();
    if !profile.is_absolute()
        || !profile.is_dir()
        || !sid.starts_with("S-1-")
        || !sid[4..].bytes().all(|b| b.is_ascii_digit() || b == b'-')
        || !version
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b".-".contains(&b))
    {
        return Err("init_invalid");
    }
    Ok((target, profile, sid, version))
}

fn handle_crash(
    process: HANDLE,
    target: u32,
    version: &str,
    output: &Path,
    file_sd: &LocalSd,
    message: &[u8; 32],
) -> bool {
    let handler = unsafe { GetCurrentProcessId() };
    if u32_at(message, 0) != Some(CRASH_MAGIC) || u32_at(message, 4) != Some(target) {
        write_status(
            file_sd,
            output,
            "request_failed",
            target,
            handler,
            false,
            None,
        );
        return false;
    }
    let tid = u32_at(message, 8).unwrap_or(0);
    let exception_ptr = u64_at(message, 12).unwrap_or(0);
    let expected_code = u32_at(message, 20).unwrap_or(0);
    #[cfg(not(target_arch = "x86_64"))]
    let _ = (version, expected_code);
    let exception_info = u64_at(message, 24).unwrap_or(0);
    if tid == 0 || exception_ptr == 0 || exception_info == 0 {
        write_status(
            file_sd,
            output,
            "request_failed",
            target,
            handler,
            false,
            None,
        );
        return false;
    }
    let injection = if std::env::var("DESKTOP_E2E_MODE").as_deref() == Ok("true") {
        std::env::var("ONEKEY_TEST_CRASH_FAIL_STAGE").unwrap_or_default()
    } else {
        String::new()
    };
    test_fault_stage(file_sd, output, "pre_capture");
    let mut buffer = match prepare_dump_buffer() {
        Ok(buffer) => buffer,
        Err(code) => {
            write_status(
                file_sd,
                output,
                "dump_failed",
                target,
                handler,
                false,
                Some(code),
            );
            return false;
        }
    };
    let before = match DaclSnapshot::read(process) {
        Ok(snapshot) => snapshot,
        Err(code) => {
            write_status(
                file_sd,
                output,
                "dacl_failed",
                target,
                handler,
                false,
                Some(code),
            );
            return false;
        }
    };
    let dump = if injection == "dump" {
        Err(0x80004005)
    } else {
        test_fault_stage(file_sd, output, "read");
        capture_dump(process, exception_info, &mut buffer)
    };
    let unchanged = before.matches_current(process);
    if unchanged != Ok(true) {
        buffer.0.fill(0);
        write_status(
            file_sd,
            output,
            "dacl_failed",
            target,
            handler,
            false,
            unchanged.err(),
        );
        return false;
    }
    if let Err(code) = dump {
        buffer.0.fill(0);
        write_status(
            file_sd,
            output,
            "dump_failed",
            target,
            handler,
            true,
            Some(code),
        );
        return true;
    }
    let sanitized: Result<Vec<u8>, &'static str> = if injection == "scrub" || injection == "unwind"
    {
        Err("injected")
    } else {
        test_fault_stage(file_sd, output, "scrub");
        #[cfg(target_arch = "x86_64")]
        {
            sanitize_dump_for_report(
                &buffer.0,
                version,
                tid,
                expected_code,
                process,
                output,
                file_sd,
            )
        }
        #[cfg(not(target_arch = "x86_64"))]
        {
            Err("unsupported_arch")
        }
    };
    buffer.0.fill(0);
    let report = match sanitized {
        Ok(report) => report,
        Err(reason) => {
            write_status_reason(
                file_sd,
                output,
                "scrub_failed",
                target,
                handler,
                true,
                None,
                Some(reason),
            );
            return true;
        }
    };
    let pending = output.join("report.pending");
    test_fault_stage(file_sd, output, "write");
    let mut file = match create_output_file(&pending, file_sd) {
        Ok(file) => file,
        Err(_) => {
            write_status(file_sd, output, "write_failed", target, handler, true, None);
            return true;
        }
    };
    if file
        .write_all(&report)
        .and_then(|_| file.sync_all())
        .is_err()
    {
        drop(file);
        let _ = fs::remove_file(pending); // Only this branch created the file.
        write_status(file_sd, output, "write_failed", target, handler, true, None);
        return true;
    }
    drop(file);
    if fs::rename(&pending, output.join(REPORT_NAME)).is_err() {
        let _ = fs::remove_file(pending);
        write_status(file_sd, output, "write_failed", target, handler, true, None);
        return true;
    }
    write_status(file_sd, output, "complete", target, handler, true, None);
    true
}

pub fn run_handler() -> Result<(), &'static str> {
    let stdin = std::io::stdin();
    let stdout = std::io::stdout();
    let mut input = stdin.lock();
    let mut output_pipe = stdout.lock();
    let (target, profile, sid, version) = read_init(&mut input)?;
    let output = profile.join("local-crash-prototype");
    if !output.is_dir() {
        return Err("output_directory_missing");
    }
    let file_sd = creation_sd(&sid, 0x0013_019f)?;
    apply_handler_dacl(&sid).map_err(|_| "handler_dacl")?;
    test_fault_stage(&file_sd, &output, "pre_handshake");
    output_pipe.write_all(b"H").map_err(|_| "hardened_write")?;
    output_pipe.flush().map_err(|_| "hardened_flush")?;
    let mut raw_handle = [0u8; 8];
    input
        .read_exact(&mut raw_handle)
        .map_err(|_| "handle_read")?;
    let process = handle(u64::from_le_bytes(raw_handle) as usize);
    let target_from_handle = unsafe { GetProcessId(process) };
    if target_from_handle == 0 || target_from_handle != target {
        return Err("wrong_target_handle");
    }
    output_pipe.write_all(b"R").map_err(|_| "ready_write")?;
    output_pipe.flush().map_err(|_| "ready_flush")?;
    let mut message = [0u8; 32];
    if input.read_exact(&mut message).is_ok() {
        let restored = handle_crash(process, target, &version, &output, &file_sd, &message);
        let _ = output_pipe.write_all(if restored { b"R" } else { b"F" });
        let _ = output_pipe.flush();
    }
    unsafe {
        let _ = CloseHandle(process);
    }
    Ok(())
}

#[cfg(test)]
mod tests {
    use super::sanitize_dump;

    fn put32(bytes: &mut [u8], offset: usize, value: u32) {
        bytes[offset..offset + 4].copy_from_slice(&value.to_le_bytes());
    }
    fn put64(bytes: &mut [u8], offset: usize, value: u64) {
        bytes[offset..offset + 8].copy_from_slice(&value.to_le_bytes());
    }

    fn fixture() -> Vec<u8> {
        let mut dump = vec![0u8; 700];
        put32(&mut dump, 0, 0x504d444d);
        put32(&mut dump, 8, 3);
        put32(&mut dump, 12, 32);
        for (index, kind, size, rva) in [(0, 3, 52, 68), (1, 4, 112, 120), (2, 6, 168, 232)] {
            let at = 32 + index * 12;
            put32(&mut dump, at, kind);
            put32(&mut dump, at + 4, size);
            put32(&mut dump, at + 8, rva);
        }
        put32(&mut dump, 68, 1);
        put32(&mut dump, 72, 42);
        put32(&mut dump, 112, 64);
        put32(&mut dump, 116, 400);
        put32(&mut dump, 120, 1);
        put64(&mut dump, 124, 0x1000_0000);
        put32(&mut dump, 132, 0x2000);
        put32(&mut dump, 144, 464);
        put32(&mut dump, 232, 42);
        put32(&mut dump, 240, 0xc000_0005);
        put64(&mut dump, 256, 0x1000_0123);
        put32(&mut dump, 392, 64);
        put32(&mut dump, 396, 400);
        let name: Vec<u16> = "C:\\Users\\CANARY-PRIVATE-PATH\\OneKey.exe"
            .encode_utf16()
            .collect();
        put32(&mut dump, 464, (name.len() * 2) as u32);
        for (i, unit) in name.iter().enumerate() {
            dump[468 + i * 2..470 + i * 2].copy_from_slice(&unit.to_le_bytes());
        }
        dump[600..620].copy_from_slice(b"CANARY-PRIVATE-STACK");
        dump
    }

    #[test]
    fn rebuilds_only_allowed_fields() {
        let json = String::from_utf8(sanitize_dump(&fixture(), "0.1.0", 42, 0xc000_0005).unwrap())
            .unwrap();
        for required in [
            "\"schema\":1",
            "\"appVersion\":\"0.1.0\"",
            "\"exceptionCode\":\"0xc0000005\"",
            "\"moduleId\":\"OneKey.exe\"",
            "\"moduleRva\":291",
        ] {
            assert!(json.contains(required), "missing {required}");
        }
        assert!(!json.contains("CANARY") && !json.contains("Users") && !json.contains("STACK"));
        assert_eq!(json.matches('\"').count(), 18); // Exactly six fixed JSON keys and three string values.
    }

    #[test]
    fn rejects_missing_or_mismatched_crash_data() {
        let mut dump = fixture();
        assert!(sanitize_dump(&dump, "0.1.0", 43, 0xc000_0005).is_err());
        assert!(sanitize_dump(&dump, "0.1.0", 42, 0xc000_0409).is_err());
        put32(&mut dump, 32, 0); // Required ThreadList stream missing.
        assert!(sanitize_dump(&dump, "0.1.0", 42, 0xc000_0005).is_err());
    }

    #[test]
    fn rejects_truncation_and_out_of_range_descriptors() {
        let dump = fixture();
        assert!(sanitize_dump(&dump[..300], "0.1.0", 42, 0xc000_0005).is_err());
        let mut bad = dump;
        put32(&mut bad, 116, 0xffff_fff0); // Thread context RVA outside the file.
        assert!(sanitize_dump(&bad, "0.1.0", 42, 0xc000_0005).is_err());
    }
}
