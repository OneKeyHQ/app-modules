//! Links the wasm SQLite build.
//!
//! rusqlite is built without `bundled` on wasm32, so libsqlite3-sys emits `-l sqlite3`.
//! This script copies `sqlite-wasm-rs`'s `libwsqlite3.a` into OUT_DIR as `libsqlite3.a`
//! and adds it to the search path. Keep in sync with the zcash-runtime build.rs.

use std::path::{Path, PathBuf};

fn main() {
    println!("cargo::rerun-if-changed=build.rs");

    let target = std::env::var("TARGET").unwrap_or_default();
    if !target.starts_with("wasm32") {
        // Native targets use system or bundled SQLite.
        return;
    }

    let out_dir = PathBuf::from(std::env::var("OUT_DIR").expect("OUT_DIR is set by cargo"));

    let Some(src) = find_wasm_sqlite_archive(&out_dir) else {
        // Warn instead of panicking so a later link error has context.
        println!(
            "cargo::warning=libwsqlite3.a from sqlite-wasm-rs not found; \
             if linking fails with `unable to find library -lsqlite3`, check that \
             sqlite-wasm-rs is in the dependency graph and the target is wasm32"
        );
        return;
    };

    let dst = out_dir.join("libsqlite3.a");
    if let Err(e) = std::fs::copy(&src, &dst) {
        println!("cargo::warning=failed to copy {}: {e}", src.display());
        return;
    }

    println!("cargo::rustc-link-search=native={}", out_dir.display());
    println!("cargo::rerun-if-changed={}", src.display());
}

/// Locate `sqlite-wasm-rs-*/out/libwsqlite3.a` under the `.../build/` directory above OUT_DIR.
///
/// Dependency build scripts run first, so the archive already exists.
fn find_wasm_sqlite_archive(out_dir: &Path) -> Option<PathBuf> {
    // OUT_DIR is target/<triple>/<profile>/build/<pkg>-<hash>/out
    let build_root = out_dir.parent()?.parent()?;
    for entry in std::fs::read_dir(build_root).ok()? {
        let Ok(entry) = entry else { continue };
        let name = entry.file_name();
        let name = name.to_string_lossy();
        if !name.starts_with("sqlite-wasm-rs-") {
            continue;
        }
        let candidate = entry.path().join("out").join("libwsqlite3.a");
        if candidate.is_file() {
            return Some(candidate);
        }
    }
    None
}
