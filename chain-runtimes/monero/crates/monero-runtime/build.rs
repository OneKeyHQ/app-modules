//! 把 wasm 版 SQLite 接到链接器上。
//!
//! 背景：我们用 `rusqlite` 但不开 `bundled` feature —— 那会在构建期编译 SQLite
//! 的 C 源码，而 wasm32 没有 libc sysroot。关掉之后 libsqlite3-sys 转而发出
//! `-l sqlite3`，让链接器去找现成的库。
//!
//! **这份是 `chain-runtimes/zcash/crates/zcash-runtime/build.rs` 的副本**（实测
//! 仅 8 行注释差异）。与链无关，本该抽成一个共享 crate —— 但两条链目前在各自
//! 未合并的分支上，抽了没有共同祖先可放。**两条都落 main 之后第一件事就是抽它**，
//! 改这里记得同步改另一份。
//!
//! 在 wasm32 上这份现成的库由 `sqlite-wasm-rs` 提供，但它产出的归档叫
//! `libwsqlite3.a`（名字里有个 w）。本脚本把它按链接器要的名字复制一份到我们
//! 自己的 OUT_DIR，并把该目录加进搜索路径。
//!
//! 这样调用方不需要任何手工的 RUSTFLAGS —— `cargo build` 直接可用。

use std::path::{Path, PathBuf};

fn main() {
    println!("cargo::rerun-if-changed=build.rs");

    let target = std::env::var("TARGET").unwrap_or_default();
    if !target.starts_with("wasm32") {
        // 原生目标用系统或 bundled 的 SQLite，无需干预。
        return;
    }

    let out_dir = PathBuf::from(std::env::var("OUT_DIR").expect("OUT_DIR 应由 cargo 提供"));

    let Some(src) = find_wasm_sqlite_archive(&out_dir) else {
        // 不直接 panic：留一条可读的提示，让链接期的报错有据可查。
        println!(
            "cargo::warning=未找到 sqlite-wasm-rs 的 libwsqlite3.a；\
             若链接期报 `unable to find library -lsqlite3`，请确认 sqlite-wasm-rs \
             在依赖图中且目标为 wasm32"
        );
        return;
    };

    let dst = out_dir.join("libsqlite3.a");
    if let Err(e) = std::fs::copy(&src, &dst) {
        println!("cargo::warning=复制 {} 失败: {e}", src.display());
        return;
    }

    println!("cargo::rustc-link-search=native={}", out_dir.display());
    println!("cargo::rerun-if-changed={}", src.display());
}

/// 从我们的 OUT_DIR 往上找到 `.../build/` 目录，再在里面定位
/// `sqlite-wasm-rs-*/out/libwsqlite3.a`。
///
/// 依赖的构建脚本先于依赖方运行，所以这个文件此刻必然已经存在。
fn find_wasm_sqlite_archive(out_dir: &Path) -> Option<PathBuf> {
    // OUT_DIR 形如 target/<triple>/<profile>/build/<pkg>-<hash>/out
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
