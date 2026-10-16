#![cfg(windows)]

use napi::{bindgen_prelude::AsyncTask, bindgen_prelude::Buffer, Env, Error, Result, Status, Task};
use napi_derive::napi;
use windows::{
    core::{factory, HSTRING},
    Security::Credentials::UI::{UserConsentVerificationResult, UserConsentVerifier},
    Win32::{
        Foundation::HWND,
        System::WinRT::{
            IUserConsentVerifierInterop, RoInitialize, RoUninitialize, RO_INIT_MULTITHREADED,
        },
        UI::WindowsAndMessaging::{AllowSetForegroundWindow, IsWindow},
    },
};
use windows_future::IAsyncOperation;

fn windows_error(error: windows::core::Error) -> Error {
    Error::from_reason(format!(
        "Windows Hello: {error} (HRESULT 0x{:08X})",
        error.code().0 as u32
    ))
}

struct Apartment;

impl Apartment {
    fn initialize() -> Result<Self> {
        unsafe { RoInitialize(RO_INIT_MULTITHREADED) }.map_err(windows_error)?;
        Ok(Self)
    }
}

impl Drop for Apartment {
    fn drop(&mut self) {
        unsafe { RoUninitialize() };
    }
}

pub struct AvailabilityTask;

impl Task for AvailabilityTask {
    type Output = i32;
    type JsValue = i32;

    fn compute(&mut self) -> Result<Self::Output> {
        let _apartment = Apartment::initialize()?;
        let _verifier =
            factory::<UserConsentVerifier, IUserConsentVerifierInterop>().map_err(windows_error)?;
        UserConsentVerifier::CheckAvailabilityAsync()
            .and_then(|operation| operation.join())
            .map(|availability| availability.0)
            .map_err(windows_error)
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output)
    }
}

pub struct VerificationTask {
    window_handle: usize,
    message: String,
}

impl Task for VerificationTask {
    type Output = i32;
    type JsValue = i32;

    fn compute(&mut self) -> Result<Self::Output> {
        let hwnd = HWND(self.window_handle as *mut std::ffi::c_void);
        if self.window_handle == 0 || !unsafe { IsWindow(Some(hwnd)) }.as_bool() {
            return Err(Error::new(
                Status::InvalidArg,
                "The owner window is unavailable",
            ));
        }
        let _apartment = Apartment::initialize()?;
        let verifier =
            factory::<UserConsentVerifier, IUserConsentVerifierInterop>().map_err(windows_error)?;
        let operation: IAsyncOperation<UserConsentVerificationResult> = unsafe {
            verifier.RequestVerificationForWindowAsync(hwnd, &HSTRING::from(&self.message))
        }
        .map_err(windows_error)?;
        operation
            .join()
            .map(|verification| verification.0)
            .map_err(windows_error)
    }

    fn resolve(&mut self, _env: Env, output: Self::Output) -> Result<Self::JsValue> {
        Ok(output)
    }
}

#[napi]
pub fn check_availability() -> AsyncTask<AvailabilityTask> {
    AsyncTask::new(AvailabilityTask)
}

#[napi]
pub fn request_verification(
    window_handle: Buffer,
    message: String,
) -> Result<AsyncTask<VerificationTask>> {
    let handle_bytes: [u8; std::mem::size_of::<usize>()] = window_handle
        .as_ref()
        .try_into()
        .map_err(|_| Error::new(Status::InvalidArg, "Invalid owner window handle length"))?;
    Ok(AsyncTask::new(VerificationTask {
        window_handle: usize::from_le_bytes(handle_bytes),
        message,
    }))
}

#[napi]
pub fn allow_set_foreground_window(process_id: u32) -> Result<bool> {
    if process_id == 0 || process_id == u32::MAX {
        return Err(Error::new(
            Status::InvalidArg,
            "An explicit process ID is required",
        ));
    }
    Ok(unsafe { AllowSetForegroundWindow(process_id) }.is_ok())
}
