use std::path::{Path, PathBuf};

fn main() {
    println!("cargo::rerun-if-changed=build.rs");
    if !std::env::var("TARGET")
        .unwrap_or_default()
        .starts_with("wasm32")
    {
        return;
    }

    let out_dir = PathBuf::from(std::env::var("OUT_DIR").expect("Cargo must set OUT_DIR"));
    let Some(source) = find_wasm_sqlite_archive(&out_dir) else {
        println!("cargo::warning=sqlite-wasm-rs libwsqlite3.a was not found; linking may fail");
        return;
    };
    let destination = out_dir.join("libsqlite3.a");
    if let Err(error) = std::fs::copy(&source, &destination) {
        println!(
            "cargo::warning=failed to copy {}: {error}",
            source.display()
        );
        return;
    }
    println!("cargo::rustc-link-search=native={}", out_dir.display());
    println!("cargo::rerun-if-changed={}", source.display());
}

fn find_wasm_sqlite_archive(out_dir: &Path) -> Option<PathBuf> {
    let build_root = out_dir.parent()?.parent()?;
    for entry in std::fs::read_dir(build_root).ok()? {
        let Ok(entry) = entry else { continue };
        if !entry
            .file_name()
            .to_string_lossy()
            .starts_with("sqlite-wasm-rs-")
        {
            continue;
        }
        let candidate = entry.path().join("out").join("libwsqlite3.a");
        if candidate.is_file() {
            return Some(candidate);
        }
    }
    None
}
