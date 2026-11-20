//! Local x64 unwind from bounded minidump stacks and matching, local PE images.
//! No minidump path, symbol server, or target ReadProcessMemory fallback is used.

use std::{
    ffi::c_void,
    fs::File,
    io::Read,
    os::windows::{ffi::OsStrExt, io::AsRawHandle},
    path::{Path, PathBuf},
    ptr,
};
use windows::{
    core::{BOOL, PCWSTR, PWSTR},
    Win32::{
        Foundation::{CloseHandle, HANDLE},
        System::{
            Diagnostics::Debug::{
                AddrModeFlat, StackWalkEx, SymCleanup, SymInitializeW, SymSetOptions, ADDRESS64,
                CONTEXT, STACKFRAME_EX, SYMOPT_DISABLE_SRVSTAR_ON_STARTUP,
                SYMOPT_DISABLE_SYMSRV_AUTODETECT, SYMOPT_FAIL_CRITICAL_ERRORS,
                SYMOPT_IGNORE_NT_SYMPATH, SYMOPT_NO_IMAGE_SEARCH, SYMOPT_NO_PROMPTS, SYMOPT_SECURE,
            },
            Memory::{
                CreateFileMappingW, MapViewOfFile, UnmapViewOfFile, FILE_MAP_READ,
                MEMORY_MAPPED_VIEW_ADDRESS, PAGE_READONLY, SEC_IMAGE_NO_EXECUTE,
            },
            SystemInformation::GetSystemDirectoryW,
            Threading::{QueryFullProcessImageNameW, PROCESS_NAME_WIN32},
        },
    },
};

const IMAGE_FILE_MACHINE_AMD64: u32 = 0x8664;
const MAX_THREADS: usize = 128;
const MAX_FRAMES: usize = 32;
const MAX_EXCEPTION_DIRECTORY_BYTES: usize = 16 * 1024 * 1024;

fn valid_exception_directory(rva: usize, size: usize, image_size: usize) -> bool {
    size != 0
        && size.is_multiple_of(12)
        && size <= MAX_EXCEPTION_DIRECTORY_BYTES
        && rva.checked_add(size).is_some_and(|end| end <= image_size)
}

fn at(raw: &[u8], offset: usize, len: usize) -> Option<&[u8]> {
    raw.get(offset..offset.checked_add(len)?)
}
fn dword(raw: &[u8], offset: usize) -> Option<u32> {
    Some(u32::from_le_bytes(at(raw, offset, 4)?.try_into().ok()?))
}
fn word(raw: &[u8], offset: usize) -> Option<u16> {
    Some(u16::from_le_bytes(at(raw, offset, 2)?.try_into().ok()?))
}
fn qword(raw: &[u8], offset: usize) -> Option<u64> {
    Some(u64::from_le_bytes(at(raw, offset, 8)?.try_into().ok()?))
}

fn leaf_return(
    raw: &[u8],
    stack_base: u64,
    stack_size: usize,
    stack_rva: usize,
    sp: u64,
) -> Option<(u64, u64)> {
    if sp & 7 != 0 {
        return None;
    }
    let offset = usize::try_from(sp.checked_sub(stack_base)?).ok()?;
    if offset > stack_size.checked_sub(8)? {
        return None;
    }
    Some((
        qword(raw, stack_rva.checked_add(offset)?)?,
        sp.checked_add(8)?,
    ))
}

fn frame_from_context(context: &CONTEXT) -> STACKFRAME_EX {
    STACKFRAME_EX {
        StackFrameSize: std::mem::size_of::<STACKFRAME_EX>() as u32,
        AddrPC: ADDRESS64 {
            Offset: context.Rip,
            Segment: 0,
            Mode: AddrModeFlat,
        },
        AddrStack: ADDRESS64 {
            Offset: context.Rsp,
            Segment: 0,
            Mode: AddrModeFlat,
        },
        AddrFrame: ADDRESS64 {
            Offset: context.Rbp,
            Segment: 0,
            Mode: AddrModeFlat,
        },
        ..Default::default()
    }
}

fn module_id(raw: &[u8], name_rva: usize) -> Option<&'static str> {
    let count = dword(raw, name_rva)? as usize;
    if count > 1024 || !count.is_multiple_of(2) {
        return None;
    }
    let bytes = at(raw, name_rva + 4, count)?;
    let units: Vec<u16> = bytes
        .as_chunks::<2>()
        .0
        .iter()
        .map(|pair| u16::from_le_bytes(*pair))
        .collect();
    let name = String::from_utf16(&units).ok()?;
    let base = name.rsplit(['\\', '/']).next()?;
    match base.to_ascii_lowercase().as_str() {
        "onekey.exe" => Some("OneKey.exe"),
        "electron.exe" => Some("electron.exe"),
        "electron-windows-process-hardening.win32-x64-msvc.node" => Some("process-hardening.node"),
        "ntdll.dll" => Some("ntdll.dll"),
        "kernelbase.dll" => Some("KERNELBASE.dll"),
        _ => None,
    }
}

fn target_executable(target: HANDLE) -> Option<PathBuf> {
    let mut text = [0u16; 32768];
    let mut len = text.len() as u32;
    unsafe {
        QueryFullProcessImageNameW(
            target,
            PROCESS_NAME_WIN32,
            PWSTR(text.as_mut_ptr()),
            &mut len,
        )
    }
    .ok()?;
    Some(PathBuf::from(
        String::from_utf16(&text[..len as usize]).ok()?,
    ))
}

fn fixed_path(id: &str, exe: &Path, package_dir: &Path) -> Option<PathBuf> {
    match id {
        "OneKey.exe" | "electron.exe"
            if ["OneKey.exe", "electron.exe"].iter().any(|name| {
                exe.file_name()
                    .and_then(|value| value.to_str())
                    .is_some_and(|actual| actual.eq_ignore_ascii_case(name))
            }) =>
        {
            // The same Electron PE is renamed OneKey.exe in the empty-profile test runtime;
            // Crashpad can retain its internal electron.exe module name. RSDS/time/size
            // must still match the target's actual executable before it is mapped.
            Some(exe.to_owned())
        }
        "process-hardening.node" => {
            Some(package_dir.join("electron-windows-process-hardening.win32-x64-msvc.node"))
        }
        "ntdll.dll" | "KERNELBASE.dll" => {
            let mut system = [0u16; 512];
            let len = unsafe { GetSystemDirectoryW(Some(&mut system)) } as usize;
            if len == 0 || len >= system.len() {
                return None;
            }
            Some(PathBuf::from(String::from_utf16(&system[..len]).ok()?).join(id))
        }
        _ => None,
    }
}

struct Image {
    id: &'static str,
    base: u64,
    size: u32,
    mapping: HANDLE,
    view: MEMORY_MAPPED_VIEW_ADDRESS,
    pdata_rva: usize,
    pdata_count: usize,
    code_ranges: Vec<(usize, usize)>,
    unwind_ranges: Vec<(usize, usize)>,
}
impl Drop for Image {
    fn drop(&mut self) {
        unsafe {
            let _ = UnmapViewOfFile(self.view);
            let _ = CloseHandle(self.mapping);
        }
    }
}
impl Image {
    fn bytes(&self) -> &[u8] {
        unsafe { std::slice::from_raw_parts(self.view.Value.cast(), self.size as usize) }
    }
    fn contains(&self, address: u64) -> bool {
        address >= self.base && address - self.base < self.size as u64
    }
    fn range_in(ranges: &[(usize, usize)], offset: usize, size: usize) -> bool {
        offset.checked_add(size).is_some_and(|end| {
            ranges
                .iter()
                .any(|&(begin, limit)| offset >= begin && end <= limit)
        })
    }
    fn executable(&self, address: u64) -> bool {
        self.contains(address)
            && Self::range_in(&self.code_ranges, (address - self.base) as usize, 1)
    }
    fn readable_for_unwind(&self, address: u64, size: usize) -> bool {
        self.contains(address)
            && (Self::range_in(&self.code_ranges, (address - self.base) as usize, size)
                || Self::range_in(&self.unwind_ranges, (address - self.base) as usize, size))
    }
    fn function(&self, address: u64) -> Option<*mut c_void> {
        if !self.executable(address) {
            return None;
        }
        let rva = (address - self.base) as u32;
        let bytes = self.bytes();
        let mut low = 0;
        let mut high = self.pdata_count;
        while low < high {
            let mid = low + (high - low) / 2;
            let entry = self.pdata_rva + mid * 12;
            let begin = dword(bytes, entry)?;
            let end = dword(bytes, entry + 4)?;
            if rva < begin {
                high = mid;
            } else if rva >= end {
                low = mid + 1;
            } else {
                return Some(unsafe { self.view.Value.cast::<u8>().add(entry).cast() });
            }
        }
        None
    }
}

fn load_image(raw: &[u8], item: usize, id: &'static str, path: &Path) -> Option<Image> {
    let target_base = qword(raw, item)?;
    let target_size = dword(raw, item + 8)?;
    let target_time = dword(raw, item + 16)?;
    let cv_size = dword(raw, item + 76)? as usize;
    let cv_rva = dword(raw, item + 80)? as usize;
    let target_cv = at(raw, cv_rva, cv_size)?.get(..24)?;
    if &target_cv[..4] != b"RSDS" {
        return None;
    }
    let mut file = File::open(path).ok()?;
    if file.metadata().ok()?.len() > 512 * 1024 * 1024 {
        return None;
    }
    let mut header = [0u8; 4096];
    file.read_exact(&mut header).ok()?;
    if word(&header, 0)? != 0x5a4d {
        return None;
    }
    let pe = dword(&header, 0x3c)? as usize;
    if dword(&header, pe)? != 0x4550 || word(&header, pe + 4)? != 0x8664 {
        return None;
    }
    if dword(&header, pe + 8)? != target_time {
        return None;
    }
    let opt = pe + 24;
    if word(&header, opt)? != 0x20b
        || dword(&header, opt + 56)? != target_size
        || target_size > 512 * 1024 * 1024
    {
        return None;
    }
    let exception_rva = dword(&header, opt + 112 + 3 * 8)? as usize;
    let exception_size = dword(&header, opt + 112 + 3 * 8 + 4)? as usize;
    let debug_rva = dword(&header, opt + 112 + 6 * 8)? as usize;
    let debug_size = dword(&header, opt + 112 + 6 * 8 + 4)? as usize;
    if !valid_exception_directory(exception_rva, exception_size, target_size as usize)
        || debug_size == 0
        || !debug_size.is_multiple_of(28)
        || debug_size > 65536
        || debug_rva.checked_add(debug_size)? > target_size as usize
    {
        return None;
    }
    let sections = word(&header, pe + 6)? as usize;
    let section_table = opt.checked_add(word(&header, pe + 20)? as usize)?;
    if sections == 0 || sections > 96 || section_table.checked_add(sections * 40)? > header.len() {
        return None;
    }
    let mut code_ranges = Vec::new();
    let mut unwind_ranges = Vec::new();
    for i in 0..sections {
        let entry = section_table + i * 40;
        let name = at(&header, entry, 8)?;
        let virtual_size = dword(&header, entry + 8)? as usize;
        let virtual_address = dword(&header, entry + 12)? as usize;
        let raw_size = dword(&header, entry + 16)? as usize;
        let characteristics = dword(&header, entry + 36)?;
        let limit = virtual_address.checked_add(virtual_size.max(raw_size))?;
        if limit > target_size as usize || virtual_size == 0 {
            return None;
        }
        let readable = characteristics & 0x4000_0000 != 0;
        let writable = characteristics & 0x8000_0000 != 0;
        let executable = characteristics & 0x2000_0000 != 0;
        if readable && executable && !writable {
            code_ranges.push((virtual_address, limit));
        } else if readable
            && !executable
            && !writable
            && (name.starts_with(b".pdata")
                || name.starts_with(b".xdata")
                || name.starts_with(b".rdata"))
        {
            unwind_ranges.push((virtual_address, limit));
        }
    }
    if code_ranges.is_empty() || !Image::range_in(&unwind_ranges, exception_rva, exception_size) {
        return None;
    }
    let mapping = unsafe {
        CreateFileMappingW(
            HANDLE(file.as_raw_handle()),
            None,
            PAGE_READONLY | SEC_IMAGE_NO_EXECUTE,
            0,
            0,
            PCWSTR::null(),
        )
    }
    .ok()?;
    let view = unsafe { MapViewOfFile(mapping, FILE_MAP_READ, 0, 0, 0) };
    if view.Value.is_null() {
        unsafe {
            let _ = CloseHandle(mapping);
        }
        return None;
    }
    let image = Image {
        id,
        base: target_base,
        size: target_size,
        mapping,
        view,
        pdata_rva: exception_rva,
        pdata_count: exception_size / 12,
        code_ranges,
        unwind_ranges,
    };
    let bytes = image.bytes();
    let mut matching_cv = false;
    for offset in (debug_rva..debug_rva + debug_size).step_by(28) {
        if dword(bytes, offset + 12) == Some(2) {
            let size = dword(bytes, offset + 16)? as usize;
            let rva = dword(bytes, offset + 20)? as usize;
            if size >= 24 && at(bytes, rva, 24) == Some(target_cv) {
                matching_cv = true;
                break;
            }
        }
    }
    matching_cv.then_some(image)
}

struct Walk<'a> {
    raw: &'a [u8],
    stack_base: u64,
    stack_size: usize,
    stack_rva: usize,
    images: &'a [Image],
    table_hit: bool,
    read_misses: usize,
}
impl Walk<'_> {
    fn image(&self, address: u64) -> Option<&Image> {
        self.images.iter().find(|image| image.contains(address))
    }
}
unsafe extern "system" fn read_memory(
    handle: HANDLE,
    address: u64,
    dest: *mut c_void,
    size: u32,
    done: *mut u32,
) -> BOOL {
    let state = &mut *(handle.0 as *mut Walk);
    if !done.is_null() {
        *done = 0;
    }
    if dest.is_null() || size == 0 {
        return BOOL(0);
    }
    let wanted = size as usize;
    let source = if address >= state.stack_base
        && address - state.stack_base <= state.stack_size as u64
        && wanted <= state.stack_size - (address - state.stack_base) as usize
    {
        at(
            state.raw,
            state.stack_rva + (address - state.stack_base) as usize,
            wanted,
        )
    } else if let Some(image) = state
        .image(address)
        .filter(|image| image.readable_for_unwind(address, wanted))
    {
        at(image.bytes(), (address - image.base) as usize, wanted)
    } else {
        None
    };
    let Some(source) = source else {
        state.read_misses += 1;
        return BOOL(0);
    };
    ptr::copy_nonoverlapping(source.as_ptr(), dest.cast::<u8>(), wanted);
    if !done.is_null() {
        *done = size;
    }
    BOOL(1)
}
unsafe extern "system" fn function_table(handle: HANDLE, address: u64) -> *mut c_void {
    let state = &mut *(handle.0 as *mut Walk);
    let result = state
        .image(address)
        .and_then(|image| image.function(address));
    if result.is_some() {
        state.table_hit = true;
    }
    result.unwrap_or(ptr::null_mut())
}
unsafe extern "system" fn module_base(handle: HANDLE, address: u64) -> u64 {
    let state = &*(handle.0 as *const Walk);
    state.image(address).map_or(0, |image| image.base)
}

fn frame_json(images: &[Image], address: u64, trust: &str) -> Option<String> {
    let image = images.iter().find(|image| image.executable(address))?;
    Some(format!(
        "{{\"moduleId\":\"{}\",\"moduleRva\":{},\"trust\":\"{}\"}}",
        image.id,
        address - image.base,
        trust
    ))
}

fn context_rejection(
    images: &[Image],
    raw: &[u8],
    modules_rva: usize,
    module_count: usize,
    address: u64,
) -> &'static str {
    if images.iter().any(|image| image.contains(address)) {
        return "context_not_executable";
    }
    for i in 0..module_count {
        let item = modules_rva + 4 + i * 108;
        let (Some(base), Some(size)) = (qword(raw, item), dword(raw, item + 8)) else {
            continue;
        };
        if address >= base && address - base < size as u64 {
            return if dword(raw, item + 20)
                .and_then(|rva| module_id(raw, rva as usize))
                .is_some()
            {
                "context_image_unverified"
            } else {
                "context_module_unapproved"
            };
        }
    }
    "context_module_unlisted"
}

#[allow(clippy::too_many_arguments)] // Explicit bounded stream locations and the fixed process/file roots.
pub fn threads_json(
    raw: &[u8],
    threads_rva: usize,
    threads_size: usize,
    modules_rva: usize,
    modules_size: usize,
    expected_tid: u32,
    exception_context_rva: usize,
    exception_context_size: usize,
    target: HANDLE,
    package_dir: &Path,
) -> Result<String, &'static str> {
    let count = dword(raw, threads_rva).ok_or("threads")? as usize;
    let module_count = dword(raw, modules_rva).ok_or("modules")? as usize;
    if count == 0
        || count > MAX_THREADS
        || threads_size < 4 + count * 48
        || module_count == 0
        || module_count > 4096
        || modules_size < 4 + module_count * 108
    {
        return Err("bounds");
    }
    let target_path = target_executable(target).ok_or("target_path")?;
    let mut images = Vec::new();
    for i in 0..module_count {
        let item = modules_rva + 4 + i * 108;
        let Some(id) = dword(raw, item + 20).and_then(|rva| module_id(raw, rva as usize)) else {
            continue;
        };
        let Some(path) = fixed_path(id, &target_path, package_dir) else {
            continue;
        };
        if let Some(image) = load_image(raw, item, id, &path) {
            images.push(image);
        }
    }
    if images.is_empty() {
        return Err("no_verified_images");
    }
    let old_options = unsafe {
        SymSetOptions(
            SYMOPT_IGNORE_NT_SYMPATH
                | SYMOPT_NO_PROMPTS
                | SYMOPT_FAIL_CRITICAL_ERRORS
                | SYMOPT_SECURE
                | SYMOPT_NO_IMAGE_SEARCH
                | SYMOPT_DISABLE_SRVSTAR_ON_STARTUP
                | SYMOPT_DISABLE_SYMSRV_AUTODETECT,
        )
    };
    let mut state = Walk {
        raw,
        stack_base: 0,
        stack_size: 0,
        stack_rva: 0,
        images: &images,
        table_hit: false,
        read_misses: 0,
    };
    let symbol_handle = HANDLE((&mut state as *mut Walk).cast());
    let search_path: Vec<u16> = package_dir
        .as_os_str()
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    unsafe { SymInitializeW(symbol_handle, PCWSTR(search_path.as_ptr()), false) }
        .map_err(|_| "sym_initialize")?;
    let mut result = String::from("[");
    let mut exception_context_valid = false;
    let mut exception_unwound = 0;
    let mut exception_failure = "exception_unwind";
    for i in 0..count {
        let item = threads_rva + 4 + i * 48;
        let tid = dword(raw, item).ok_or("thread_id")?;
        let stack_base = qword(raw, item + 24).ok_or("stack")?;
        let stack_size = dword(raw, item + 32).ok_or("stack")? as usize;
        let stack_rva = dword(raw, item + 36).ok_or("stack")? as usize;
        let mut context_size = dword(raw, item + 40).ok_or("context")? as usize;
        let mut context_rva = dword(raw, item + 44).ok_or("context")? as usize;
        if tid == expected_tid {
            // ThreadList can record the blocked crash callback. ExceptionStream retains the fault site.
            context_size = exception_context_size;
            context_rva = exception_context_rva;
        }
        let mut frames = Vec::new();
        let mut has_context_frame = false;
        let mut context_reject_reason = None;
        let mut unwound = 0;
        if at(raw, stack_rva, stack_size).is_some()
            && context_size >= std::mem::size_of::<CONTEXT>()
        {
            if let Some(context_bytes) = at(raw, context_rva, std::mem::size_of::<CONTEXT>()) {
                let mut context = CONTEXT::default();
                unsafe {
                    ptr::copy_nonoverlapping(
                        context_bytes.as_ptr(),
                        (&mut context as *mut CONTEXT).cast(),
                        context_bytes.len(),
                    );
                }
                if let Some(first) = frame_json(&images, context.Rip, "context") {
                    frames.push(first);
                    has_context_frame = true;
                } else if tid == expected_tid {
                    context_reject_reason = Some(context_rejection(
                        &images,
                        raw,
                        modules_rva,
                        module_count,
                        context.Rip,
                    ));
                }
                let mut frame = frame_from_context(&context);
                state.stack_base = stack_base;
                state.stack_size = stack_size;
                state.stack_rva = stack_rva;
                state.read_misses = 0;
                let mut previous_pc = context.Rip;
                let mut previous_sp = context.Rsp;
                let mut unchanged_once = false;
                for _ in 1..MAX_FRAMES {
                    let Some(image) = state.image(previous_pc) else {
                        if tid == expected_tid && unwound == 0 {
                            exception_failure = "current_pc_unverified";
                        }
                        break;
                    };
                    if !image.executable(previous_pc) {
                        if tid == expected_tid && unwound == 0 {
                            exception_failure = "current_pc_unverified";
                        }
                        break;
                    }
                    if image.function(previous_pc).is_none() {
                        // Windows x64 leaf functions have no runtime-function entry and unwind by
                        // simulating exactly one return from the captured stack's current RSP.
                        let Some((next_pc, next_sp)) =
                            leaf_return(raw, stack_base, stack_size, stack_rva, previous_sp)
                        else {
                            if tid == expected_tid && unwound == 0 {
                                exception_failure = "leaf_return_missing";
                            }
                            break;
                        };
                        let Some(next) = images
                            .iter()
                            .find(|image| image.executable(next_pc))
                            .and_then(|_| {
                                frame_json(&images, next_pc.saturating_sub(1), "leaf_stack")
                            })
                        else {
                            if tid == expected_tid && unwound == 0 {
                                exception_failure = "leaf_return_untrusted";
                            }
                            break;
                        };
                        context.Rip = next_pc;
                        context.Rsp = next_sp;
                        frame = frame_from_context(&context);
                        previous_pc = next_pc;
                        previous_sp = next_sp;
                        frames.push(next);
                        unwound += 1;
                        unchanged_once = false;
                        continue;
                    }
                    state.table_hit = false;
                    let ok = unsafe {
                        StackWalkEx(
                            IMAGE_FILE_MACHINE_AMD64,
                            symbol_handle,
                            HANDLE::default(),
                            &mut frame,
                            (&mut context as *mut CONTEXT).cast(),
                            Some(read_memory),
                            Some(function_table),
                            Some(module_base),
                            None,
                            0,
                        )
                    };
                    if !ok.as_bool() || frame.AddrPC.Offset == 0 {
                        if tid == expected_tid && unwound == 0 {
                            exception_failure = if state.read_misses > 0 {
                                "stackwalk_read_miss"
                            } else {
                                "stackwalk_failed"
                            };
                        }
                        break;
                    }
                    if frame.AddrPC.Offset == previous_pc && frame.AddrStack.Offset == previous_sp {
                        if unchanged_once {
                            if tid == expected_tid && unwound == 0 {
                                exception_failure = "no_progress";
                            }
                            break;
                        }
                        unchanged_once = true;
                        continue;
                    }
                    if !state.table_hit {
                        if tid == expected_tid && unwound == 0 {
                            exception_failure = "function_table_unseen";
                        }
                        break;
                    }
                    previous_pc = frame.AddrPC.Offset;
                    previous_sp = frame.AddrStack.Offset;
                    let Some(next) =
                        frame_json(&images, previous_pc.saturating_sub(1), "stackwalk")
                    else {
                        if tid == expected_tid && unwound == 0 {
                            exception_failure = "next_pc_untrusted";
                        }
                        break;
                    };
                    frames.push(next);
                    unwound += 1;
                }
            }
        }
        if tid == expected_tid {
            exception_context_valid = has_context_frame;
            exception_unwound = unwound;
            if !has_context_frame {
                exception_failure = context_reject_reason.unwrap_or("context_pc_untrusted");
            }
        }
        if i != 0 {
            result.push(',');
        }
        result.push_str(&format!(
            "{{\"threadId\":{tid},\"frames\":[{}]}}",
            frames.join(",")
        ));
    }
    result.push(']');
    unsafe {
        let _ = SymCleanup(symbol_handle);
        SymSetOptions(old_options);
    }
    if !exception_context_valid || exception_unwound == 0 {
        return Err(exception_failure);
    }
    Ok(result)
}

#[cfg(test)]
mod tests {
    use super::{fixed_path, leaf_return, valid_exception_directory};
    use std::path::Path;

    #[test]
    fn internal_electron_module_name_uses_only_the_fixed_target_executable() {
        let exe = Path::new(r"C:\fixed-test\OneKey.exe");
        let package = Path::new(r"C:\fixed-test\package");
        assert_eq!(
            fixed_path("electron.exe", exe, package).as_deref(),
            Some(exe)
        );
        assert_eq!(fixed_path("OneKey.exe", exe, package).as_deref(), Some(exe));
        assert!(fixed_path(
            "electron.exe",
            Path::new(r"C:\fixed-test\other.exe"),
            package
        )
        .is_none());
    }

    #[test]
    fn electron_sized_runtime_function_table_is_bounded_and_accepted() {
        assert!(valid_exception_directory(
            240_185_344,
            5_589_156,
            0x0ebb_4000
        ));
        assert!(!valid_exception_directory(
            0x1000,
            16 * 1024 * 1024 + 8,
            64 * 1024 * 1024
        ));
        assert!(!valid_exception_directory(0x1000, 13, 64 * 1024 * 1024));
        assert!(!valid_exception_directory(usize::MAX - 4, 12, usize::MAX));
    }

    #[test]
    fn leaf_return_reads_only_the_current_captured_stack_slot() {
        let mut raw = [0u8; 24];
        raw[8..16].copy_from_slice(&0x1234_5678u64.to_le_bytes());
        assert_eq!(
            leaf_return(&raw, 0x1000, 8, 8, 0x1000),
            Some((0x1234_5678, 0x1008))
        );
        assert_eq!(leaf_return(&raw, 0x1000, 8, 8, 0x0fff), None);
        assert_eq!(leaf_return(&raw, 0x1000, 8, 8, 0x1001), None);
        assert_eq!(leaf_return(&raw, 0x1000, 16, 8, 0x1001), None);
        assert_eq!(leaf_return(&raw[..12], 0x1000, 8, 8, 0x1000), None);
    }
}
