use std::collections::HashMap;
use std::sync::Mutex;

use windows::core::{Interface, HRESULT};
use windows::Win32::Foundation::{CloseHandle, HANDLE, S_OK, WAIT_ABANDONED, WAIT_TIMEOUT};
use windows::Win32::Graphics::Direct3D::{
    D3D_DRIVER_TYPE_HARDWARE, D3D_DRIVER_TYPE_WARP, D3D_FEATURE_LEVEL, D3D_FEATURE_LEVEL_11_0,
    D3D_FEATURE_LEVEL_11_1,
};
use windows::Win32::Graphics::Direct3D11::{
    D3D11CreateDevice, ID3D11Device, ID3D11DeviceContext, ID3D11Texture2D,
    D3D11_BIND_RENDER_TARGET, D3D11_BIND_SHADER_RESOURCE, D3D11_CREATE_DEVICE_BGRA_SUPPORT,
    D3D11_RESOURCE_MISC_SHARED_KEYEDMUTEX, D3D11_RESOURCE_MISC_SHARED_NTHANDLE, D3D11_SDK_VERSION,
    D3D11_TEXTURE2D_DESC, D3D11_USAGE_DEFAULT,
};
use windows::Win32::Graphics::Dxgi::Common::{DXGI_FORMAT_B8G8R8A8_UNORM, DXGI_SAMPLE_DESC};
use windows::Win32::Graphics::Dxgi::{
    IDXGIKeyedMutex, IDXGIResource1, DXGI_SHARED_RESOURCE_READ, DXGI_SHARED_RESOURCE_WRITE,
};

/// Maximum number of shared preview textures lent to the JS consumer at once.
pub const SLOT_COUNT: usize = 3;

pub struct LentPreviewFrame {
    pub token: i64,
    pub nt_handle_bytes: Vec<u8>,
    pub width: u32,
    pub height: u32,
}

struct SharedSlot {
    texture: ID3D11Texture2D,
    keyed_mutex: Option<IDXGIKeyedMutex>,
    nt_handle: HANDLE,
    width: u32,
    height: u32,
    lent_token: Option<i64>,
}

impl Drop for SharedSlot {
    fn drop(&mut self) {
        if !self.nt_handle.is_invalid() && !self.nt_handle.0.is_null() {
            unsafe {
                let _ = CloseHandle(self.nt_handle);
            }
            self.nt_handle = HANDLE(std::ptr::null_mut());
        }
    }
}

struct PreviewPoolInner {
    device: Option<ID3D11Device>,
    context: Option<ID3D11DeviceContext>,
    slots: Vec<SharedSlot>,
    /// Old-resolution slots still held by the consumer; closed as soon as released.
    retired: HashMap<i64, SharedSlot>,
    next_token: i64,
}

// All D3D11 interfaces and handles are accessed exclusively under `PreviewPool.inner`.
unsafe impl Send for PreviewPoolInner {}

/// Fixed-capacity pool of 3 D3D11 BGRA shared textures backed by Windows NT
/// handles for Electron `sharedTexture.importSharedTexture`.
pub struct PreviewPool {
    inner: Mutex<PreviewPoolInner>,
}

impl PreviewPool {
    pub fn new() -> Self {
        Self {
            inner: Mutex::new(PreviewPoolInner {
                device: None,
                context: None,
                slots: Vec::with_capacity(SLOT_COUNT),
                retired: HashMap::new(),
                next_token: 1,
            }),
        }
    }

    pub fn lent_count(&self) -> usize {
        let Ok(guard) = self.inner.lock() else {
            return 0;
        };
        guard
            .slots
            .iter()
            .filter(|s| s.lent_token.is_some())
            .count()
            + guard.retired.len()
    }

    /// Uploads a tightly packed top-down BGRA frame (with opaque alpha) into a
    /// free shared texture slot and returns its NT handle bytes + lease token.
    /// Returns `Ok(None)` when all 3 slots are currently lent.
    pub fn lend(
        &self,
        bgra: &[u8],
        width: u32,
        height: u32,
    ) -> Result<Option<LentPreviewFrame>, String> {
        if width == 0 || height == 0 || bgra.len() < (width as usize) * (height as usize) * 4 {
            return Ok(None);
        }
        let mut guard = self
            .inner
            .lock()
            .map_err(|_| "preview-unavailable: pool lock poisoned".to_owned())?;

        // Check total lent cap (active + retired).
        let lent = guard
            .slots
            .iter()
            .filter(|s| s.lent_token.is_some())
            .count()
            + guard.retired.len();
        if lent >= SLOT_COUNT {
            return Ok(None);
        }

        // Handle GPU reset / device removal by recreating device and unlent slots.
        if let Some(device) = guard.device.as_ref() {
            if unsafe { device.GetDeviceRemovedReason() }.is_err() {
                guard.reset_device_and_unlent_slots();
            }
        }
        guard.ensure_device()?;

        // If existing slots have different dimensions, retire lent ones and drop free ones.
        let needs_recreate = guard
            .slots
            .iter()
            .any(|s| s.width != width || s.height != height);
        if needs_recreate {
            let old_slots = std::mem::take(&mut guard.slots);
            for slot in old_slots {
                if let Some(token) = slot.lent_token {
                    guard.retired.insert(token, slot);
                }
            }
        }

        // Find an idle slot or allocate a new one up to SLOT_COUNT - retired.len().
        let free_index = guard.slots.iter().position(|s| s.lent_token.is_none());
        let slot_index = match free_index {
            Some(idx) => idx,
            None => {
                if guard.slots.len() + guard.retired.len() >= SLOT_COUNT {
                    return Ok(None);
                }
                let device = guard.device.as_ref().unwrap().clone();
                let slot = create_shared_slot(&device, width, height)?;
                guard.slots.push(slot);
                guard.slots.len() - 1
            }
        };

        let token = guard.next_token;
        guard.next_token += 1;

        let context = guard.context.as_ref().unwrap().clone();
        let slot = &mut guard.slots[slot_index];

        // Acquire keyed mutex if present, upload pixels, flush, and release before lending.
        if let Some(km) = slot.keyed_mutex.as_ref() {
            // windows-rs maps nonnegative HRESULTs to Ok, including wait statuses.
            let hr = unsafe { (km.vtable().AcquireSync)(km.as_raw(), 0, 100) };
            if hr == HRESULT(WAIT_TIMEOUT.0 as i32) {
                return Ok(None);
            }
            if hr == HRESULT(WAIT_ABANDONED.0 as i32) {
                // Remove the idle slot so the next lend creates a fresh surface.
                guard.slots.swap_remove(slot_index);
                return Ok(None);
            }
            if hr != S_OK {
                return Err(format!("preview-unavailable: AcquireSync failed ({hr:?})"));
            }
        }

        unsafe {
            context.UpdateSubresource(
                &slot.texture,
                0,
                None,
                bgra.as_ptr() as *const std::ffi::c_void,
                width * 4,
                0,
            );
            context.Flush();
        }

        if let Some(km) = slot.keyed_mutex.as_ref() {
            if let Err(error) = unsafe { km.ReleaseSync(0) } {
                guard.slots.swap_remove(slot_index);
                return Err(format!(
                    "preview-unavailable: ReleaseSync failed ({error:?})"
                ));
            }
        }

        let handle_bytes = (slot.nt_handle.0 as usize).to_ne_bytes().to_vec();
        slot.lent_token = Some(token);

        Ok(Some(LentPreviewFrame {
            token,
            nt_handle_bytes: handle_bytes,
            width,
            height,
        }))
    }

    /// Marks a lent slot free again (idempotent).
    pub fn release(&self, token: i64) {
        let Ok(mut guard) = self.inner.lock() else {
            return;
        };
        if guard.retired.remove(&token).is_some() {
            return;
        }
        for slot in &mut guard.slots {
            if slot.lent_token == Some(token) {
                slot.lent_token = None;
                return;
            }
        }
    }

    /// Clears all slots and releases the D3D11 device (called on dispose).
    pub fn clear(&self) {
        let Ok(mut guard) = self.inner.lock() else {
            return;
        };
        guard.slots.clear();
        guard.retired.clear();
        guard.context = None;
        guard.device = None;
    }
}

impl PreviewPoolInner {
    fn reset_device_and_unlent_slots(&mut self) {
        let old_slots = std::mem::take(&mut self.slots);
        for slot in old_slots {
            if let Some(token) = slot.lent_token {
                self.retired.insert(token, slot);
            }
        }
        self.context = None;
        self.device = None;
    }

    fn ensure_device(&mut self) -> Result<(), String> {
        if self.device.is_some() && self.context.is_some() {
            return Ok(());
        }
        let feature_levels: [D3D_FEATURE_LEVEL; 2] =
            [D3D_FEATURE_LEVEL_11_1, D3D_FEATURE_LEVEL_11_0];
        let mut device: Option<ID3D11Device> = None;
        let mut context: Option<ID3D11DeviceContext> = None;

        let hr = unsafe {
            D3D11CreateDevice(
                None,
                D3D_DRIVER_TYPE_HARDWARE,
                Default::default(),
                D3D11_CREATE_DEVICE_BGRA_SUPPORT,
                Some(&feature_levels),
                D3D11_SDK_VERSION,
                Some(&mut device),
                None,
                Some(&mut context),
            )
        };
        if hr.is_err() {
            // Fall back to WARP software adapter on headless VMs without a GPU.
            unsafe {
                D3D11CreateDevice(
                    None,
                    D3D_DRIVER_TYPE_WARP,
                    Default::default(),
                    D3D11_CREATE_DEVICE_BGRA_SUPPORT,
                    Some(&feature_levels),
                    D3D11_SDK_VERSION,
                    Some(&mut device),
                    None,
                    Some(&mut context),
                )
            }
            .map_err(|e| format!("preview-unavailable: D3D11CreateDevice failed ({e:?})"))?;
        }
        self.device = device;
        self.context = context;
        Ok(())
    }
}

fn create_shared_slot(
    device: &ID3D11Device,
    width: u32,
    height: u32,
) -> Result<SharedSlot, String> {
    // Prefer NT handle + keyed mutex for strict cross-device GPU synchronization
    // with Chromium's D3DImageBacking; fall back to NT handle alone if a driver
    // refuses keyed mutex creation.
    let mut use_keyed_mutex = true;
    let make_desc = |with_mutex: bool| D3D11_TEXTURE2D_DESC {
        Width: width,
        Height: height,
        MipLevels: 1,
        ArraySize: 1,
        Format: DXGI_FORMAT_B8G8R8A8_UNORM,
        SampleDesc: DXGI_SAMPLE_DESC {
            Count: 1,
            Quality: 0,
        },
        Usage: D3D11_USAGE_DEFAULT,
        BindFlags: (D3D11_BIND_SHADER_RESOURCE.0 | D3D11_BIND_RENDER_TARGET.0) as u32,
        CPUAccessFlags: 0,
        MiscFlags: if with_mutex {
            (D3D11_RESOURCE_MISC_SHARED_NTHANDLE.0 | D3D11_RESOURCE_MISC_SHARED_KEYEDMUTEX.0) as u32
        } else {
            D3D11_RESOURCE_MISC_SHARED_NTHANDLE.0 as u32
        },
    };

    let mut texture: Option<ID3D11Texture2D> = None;
    let desc = make_desc(true);
    let hr = unsafe { device.CreateTexture2D(&desc, None, Some(&mut texture)) };
    if hr.is_err() {
        use_keyed_mutex = false;
        let desc_no_mutex = make_desc(false);
        unsafe { device.CreateTexture2D(&desc_no_mutex, None, Some(&mut texture)) }
            .map_err(|e| format!("preview-unavailable: CreateTexture2D failed ({e:?})"))?;
    }
    let texture =
        texture.ok_or_else(|| "preview-unavailable: CreateTexture2D returned null".to_owned())?;

    let keyed_mutex = if use_keyed_mutex {
        texture.cast::<IDXGIKeyedMutex>().ok()
    } else {
        None
    };

    let resource1: IDXGIResource1 = texture.cast().map_err(|e| {
        format!("preview-unavailable: QueryInterface IDXGIResource1 failed ({e:?})")
    })?;

    let access = if keyed_mutex.is_some() {
        DXGI_SHARED_RESOURCE_READ.0 | DXGI_SHARED_RESOURCE_WRITE.0
    } else {
        DXGI_SHARED_RESOURCE_READ.0
    };
    let nt_handle = unsafe { resource1.CreateSharedHandle(None, access, None) }
        .map_err(|e| format!("preview-unavailable: CreateSharedHandle failed ({e:?})"))?;

    Ok(SharedSlot {
        texture,
        keyed_mutex,
        nt_handle,
        width,
        height,
        lent_token: None,
    })
}
