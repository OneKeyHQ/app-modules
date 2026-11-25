use std::{env, path::PathBuf, process::Command};

fn main() {
    napi_build::setup();
    if env::var("CARGO_CFG_TARGET_OS").as_deref() != Ok("windows") {
        return;
    }

    // Use the headers from the same locked crate that supplies the static library.
    let metadata = Command::new(env::var_os("CARGO").expect("Cargo executable"))
        .args([
            "metadata",
            "--format-version=1",
            "--offline",
            "--locked",
            "--filter-platform",
            &env::var("TARGET").expect("Cargo target"),
        ])
        .output()
        .expect("locate bundled ZXing headers");
    assert!(
        metadata.status.success(),
        "cargo metadata failed: {}",
        String::from_utf8_lossy(&metadata.stderr)
    );
    let metadata: serde_json::Value = serde_json::from_slice(&metadata.stdout).unwrap();
    let zxing = metadata["packages"]
        .as_array()
        .unwrap()
        .iter()
        .find(|package| package["name"] == "zxing-cpp")
        .expect("locked zxing-cpp dependency");
    let manifest = PathBuf::from(zxing["manifest_path"].as_str().unwrap());

    println!("cargo:rerun-if-changed=Cargo.lock");
    println!("cargo:rerun-if-changed=CMakeLists.txt");
    println!("cargo:rerun-if-changed=src/zxing_text.cpp");
    let install_dir = cmake::Config::new(".")
        .define("ZXING_CORE_DIR", manifest.parent().unwrap().join("core"))
        .define("CMAKE_INSTALL_LIBDIR", "lib")
        .build();
    println!(
        "cargo:rustc-link-search=native={}",
        install_dir.join("lib").display()
    );
    println!("cargo:rustc-link-lib=static=ZXing");
}
