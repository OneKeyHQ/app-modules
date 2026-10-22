use std::{env, fs, path::PathBuf, process::Command};

const V8_H_SHA256: &str = "e27a005daa45f342845ffb9b33409686c380b1d5a97f46f3ab4749517a213d25";

fn main() {
    napi_build::setup();
    println!("cargo:rustc-check-cfg=cfg(v8_bridge)");
    println!("cargo:rerun-if-env-changed=ELECTRON_44_HEADERS_DIR");
    println!("cargo:rerun-if-changed=src/v8_bridge.cc");
    if env::var("CARGO_CFG_TARGET_ARCH").as_deref() != Ok("x86_64") {
        return;
    }
    let Ok(include_dir) = env::var("ELECTRON_44_HEADERS_DIR") else {
        return;
    };
    let include_dir = PathBuf::from(include_dir);
    let header = include_dir.join("v8.h");
    let hash = Command::new("certutil")
        .arg("-hashfile")
        .arg(&header)
        .arg("SHA256")
        .output()
        .expect("certutil is required to verify Electron 44.5.1 headers");
    assert!(hash.status.success(), "cannot hash Electron v8.h");
    let actual = String::from_utf8_lossy(&hash.stdout)
        .lines()
        .map(|line| line.split_whitespace().collect::<String>())
        .find(|line| line.len() == 64 && line.bytes().all(|c| c.is_ascii_hexdigit()))
        .expect("certutil did not return a SHA-256 digest");
    assert_eq!(
        actual.to_ascii_lowercase(),
        V8_H_SHA256,
        "wrong Electron v8.h"
    );

    let vs_root = env::var("VSINSTALLDIR")
        .map(PathBuf::from)
        .unwrap_or_else(|_| {
            PathBuf::from(r"C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools")
        });
    let dev_cmd = vs_root.join(r"Common7\Tools\VsDevCmd.bat");
    assert!(
        dev_cmd.is_file(),
        "Visual Studio 2022 Build Tools are required"
    );
    let object = PathBuf::from(env::var("OUT_DIR").expect("OUT_DIR")).join("v8_bridge.obj");
    let source = PathBuf::from(env::var("CARGO_MANIFEST_DIR").expect("CARGO_MANIFEST_DIR"))
        .join("src/v8_bridge.cc");
    let command = format!(
        "@echo off\r\ncall \"{}\" -arch=x64 -host_arch=x64 >nul\r\nif errorlevel 1 exit /b 1\r\ncl /nologo /std:c++20 /Zc:__cplusplus /EHsc- /GR- /MD /Z7 /c /I\"{}\" /Fo\"{}\" \"{}\"\r\n",
        dev_cmd.display(), include_dir.display(), object.display(), source.display()
    );
    let batch = object.with_extension("cmd");
    fs::write(&batch, command).expect("cannot write V8 bridge compiler batch");
    let compile = Command::new("cmd")
        .args(["/D", "/C"])
        .arg(&batch)
        .output()
        .expect("cannot start MSVC C++ compiler");
    if !compile.status.success() {
        panic!(
            "V8 bridge compile failed:\n{}\n{}",
            String::from_utf8_lossy(&compile.stdout),
            String::from_utf8_lossy(&compile.stderr)
        );
    }
    assert!(fs::metadata(&object).is_ok(), "V8 bridge object missing");
    println!("cargo:rustc-link-arg-cdylib={}", object.display());
    println!("cargo:rustc-link-arg-cdylib=/DEBUG:FULL");
    println!("cargo:rustc-link-arg-cdylib=/EXPORT:onekey_crash_level_one");
    println!("cargo:rustc-link-arg-cdylib=/EXPORT:onekey_crash_level_two");
    println!("cargo:rustc-link-arg-cdylib=/EXPORT:onekey_crash_level_three");
    println!("cargo:rustc-cfg=v8_bridge");
}
