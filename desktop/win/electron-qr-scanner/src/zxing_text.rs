use std::ffi::{c_char, c_void};
use std::slice;

unsafe extern "C" {
    fn qr_decode_text(
        pixels: *const u8,
        width: i32,
        height: i32,
        rgba: bool,
        try_invert: bool,
        context: *mut c_void,
        emit_text: unsafe extern "C" fn(*mut c_void, *const c_char, usize),
    );
}

unsafe extern "C" fn receive_text(context: *mut c_void, data: *const c_char, len: usize) {
    // The shim invokes this synchronously with the full, live ZXing UTF-8 buffer.
    let bytes = unsafe { slice::from_raw_parts(data.cast::<u8>(), len) };
    let output = unsafe { &mut *context.cast::<Option<String>>() };
    *output = Some(String::from_utf8_lossy(bytes).into_owned());
}

pub fn decode_qr_text(
    pixels: &[u8],
    width: i32,
    height: i32,
    rgba: bool,
    try_invert: bool,
) -> Option<String> {
    let row_bytes = width.checked_mul(if rgba { 4 } else { 1 })?;
    let image_bytes = row_bytes.checked_mul(height)?;
    if width <= 0 || height <= 0 || pixels.len() < image_bytes as usize {
        return None;
    }

    let mut output: Option<String> = None;
    unsafe {
        qr_decode_text(
            pixels.as_ptr(),
            width,
            height,
            rgba,
            try_invert,
            (&mut output as *mut Option<String>).cast(),
            receive_text,
        );
    }
    output
}

#[cfg(test)]
mod tests {
    use super::decode_qr_text;
    use zxingcpp::{create, write, Barcode, BarcodeFormat};

    fn assert_text_in_both_formats(barcode: Barcode, expected: &str) {
        let image = barcode.to_image_with(&write().scale(4)).unwrap();
        let luma = image.data();
        let rgba: Vec<u8> = luma
            .iter()
            .flat_map(|gray| [*gray, *gray, *gray, 255])
            .collect();
        for (pixels, is_rgba) in [(&luma, false), (&rgba, true)] {
            assert_eq!(
                decode_qr_text(pixels, image.width(), image.height(), is_rgba, !is_rgba).as_deref(),
                Some(expected)
            );
        }
    }

    #[test]
    fn preserves_nul_and_unicode() {
        let expected = "before\0café\\<NUL>二维码";
        let barcode = create(BarcodeFormat::QRCode)
            .options("eci:26")
            .from_slice(expected.as_bytes())
            .unwrap();
        assert_text_in_both_formats(barcode, expected);
    }

    #[test]
    fn preserves_gs1_group_separator() {
        let barcode = create(BarcodeFormat::QRCode)
            .options("gs1:true")
            .from_str("[01]09506000134352[10]LOT42[21]SERIAL")
            .unwrap();
        assert_text_in_both_formats(barcode, "010950600013435210LOT42\x1d21SERIAL");
    }
}
