#![cfg(windows)]

#[path = "../crash_prototype.rs"]
mod crash_prototype;

#[cfg(target_arch = "x86_64")]
#[path = "../unwind.rs"]
mod unwind;

fn main() {
    if crash_prototype::run_handler().is_err() {
        std::process::exit(1);
    }
}
