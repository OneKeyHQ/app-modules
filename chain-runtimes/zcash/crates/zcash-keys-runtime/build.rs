fn main() {
    if std::env::var("CARGO_CFG_TARGET_ARCH").as_deref() == Ok("wasm32") {
        // The host wipes inactive stack frames after synchronous key operations.
        // Export linker-owned bounds so it never guesses a memory layout or
        // overwrites static data and live heap allocations.
        println!("cargo:rustc-link-arg=--export=__stack_low");
        println!("cargo:rustc-link-arg=--export=__stack_high");
    }
}
