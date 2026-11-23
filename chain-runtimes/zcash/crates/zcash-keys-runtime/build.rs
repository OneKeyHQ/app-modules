fn main() {
    if std::env::var("CARGO_CFG_TARGET_ARCH").as_deref() == Ok("wasm32") {
        // Host wipes the inactive stack using linker-owned bounds, never guessed ones.
        println!("cargo:rustc-link-arg=--export=__stack_low");
        println!("cargo:rustc-link-arg=--export=__stack_high");
    }
}
