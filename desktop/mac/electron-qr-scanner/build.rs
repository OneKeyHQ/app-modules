use std::env;
use std::path::{Path, PathBuf};
use std::process::Command;

// Swift sources shared with the iOS package, so image decoding and live-frame
// filtering behave the same on both platforms.
const SHARED_SWIFT: &[&str] = &[
    "../../../native-views/react-native-qr-scanner/ios/QrImageDecodingPipeline.swift",
    "../../../native-views/react-native-qr-scanner/ios/QrFrameTextGate.swift",
];
const LOCAL_SWIFT: &[&str] = &[
    "swift/QrCapture.swift",
    "swift/ZXingEngine.swift",
    "swift/Bridge.swift",
];
const BRIDGING_HEADER: &str = "swift/ZXingBridge.h";
const MACOS_MIN: &str = "12.0";

fn xcrun(args: &[&str]) -> String {
    let output = Command::new("xcrun")
        .args(args)
        .output()
        .expect("xcrun is required to build the macOS module");
    assert!(output.status.success(), "xcrun {args:?} failed");
    String::from_utf8(output.stdout).unwrap().trim().to_owned()
}

fn main() {
    napi_build::setup();

    let manifest = PathBuf::from(env::var("CARGO_MANIFEST_DIR").unwrap());
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let arch = match env::var("CARGO_CFG_TARGET_ARCH").unwrap().as_str() {
        "aarch64" => "arm64",
        "x86_64" => "x86_64",
        other => panic!("unsupported macOS architecture: {other}"),
    };

    let sources: Vec<PathBuf> = SHARED_SWIFT
        .iter()
        .chain(LOCAL_SWIFT)
        .map(|path| manifest.join(path))
        .collect();
    println!("cargo:rerun-if-changed={}", manifest.join(BRIDGING_HEADER).display());
    for source in &sources {
        assert!(source.exists(), "missing Swift source {}", source.display());
        println!("cargo:rerun-if-changed={}", source.display());
    }

    let sdk = xcrun(&["--sdk", "macosx", "--show-sdk-path"]);
    let library = out_dir.join("libOneKeyQrScanner.a");
    let status = Command::new("xcrun")
        .args(["--sdk", "macosx", "swiftc"])
        .args(["-emit-library", "-static", "-parse-as-library", "-O"])
        .args(["-module-name", "OneKeyQrScanner"])
        .args(["-target", &format!("{arch}-apple-macos{MACOS_MIN}")])
        .args(["-sdk", &sdk])
        .arg("-import-objc-header")
        .arg(manifest.join(BRIDGING_HEADER))
        .arg("-o")
        .arg(&library)
        .args(&sources)
        .status()
        .expect("failed to run swiftc");
    assert!(status.success(), "swiftc failed");

    println!("cargo:rustc-link-search=native={}", out_dir.display());
    println!("cargo:rustc-link-lib=static=OneKeyQrScanner");
    for framework in [
        "Foundation",
        "AVFoundation",
        "CoreMedia",
        "CoreVideo",
        "CoreImage",
        "CoreGraphics",
        "ImageIO",
        "Vision",
    ] {
        println!("cargo:rustc-link-lib=framework={framework}");
    }

    // The Swift objects autolink swiftCore and the compatibility shims; the
    // linker needs their directories. The runtime itself ships with macOS.
    let swiftc = PathBuf::from(xcrun(&["--sdk", "macosx", "-f", "swiftc"]));
    let toolchain_lib = swiftc
        .parent()
        .and_then(Path::parent)
        .unwrap()
        .join("lib/swift/macosx");
    println!("cargo:rustc-link-search=native={}", toolchain_lib.display());
    println!("cargo:rustc-link-search=native={sdk}/usr/lib/swift");
    println!("cargo:rustc-link-arg=-Wl,-rpath,/usr/lib/swift");
    println!("cargo:rustc-env=MACOSX_DEPLOYMENT_TARGET={MACOS_MIN}");
}
