# Zcash WASM releases

Zcash versions are independent of the native modules' 3.x release train. Do not
run the repository-wide `version:patch` or `version:publish` for a Zcash release.

## Version source

Edit only `[workspace.package].version` in `Cargo.toml`. All three crates inherit
it, and wasm-pack generates the npm versions from those crates. Update the three
workspace entries in `Cargo.lock` with `cargo check --workspace --offline` and
review the lockfile diff; dependency upgrades are a separate change.

Release candidates use `X.Y.Z-rc.N` on the npm `rc` tag; final releases use
`latest`. Runtime and keys always share a version and source commit. The
benchmark ships as a runtime subdirectory to keep the App developer tools' import
path; it is not a third package, and production App bundles exclude it.

## Prepare and verify

Use Python 3.11+ and the pinned Rust/WASM tools described in README.md.

1. Run `python3 -m unittest discover -s scripts -p 'test_*.py'` and
   `cargo test --workspace --locked --offline` with already prepared dependencies.
2. Commit the release changes. A release build must come from a clean checkout.
3. Run `bash scripts/build-wasm.sh --offline`. This builds all three WASM
   artifacts, prepares npm metadata and LICENSE, and writes the source manifest.
4. Run `python3 scripts/package-release.py pack --version 0.1.0-rc.1 --tag rc
   --revision <full-commit-sha>` (as one command). It checks clean-source
   provenance, Cargo.lock, all artifact hashes, versions, and npm archive contents.
   `dist/` contains the two verified tarballs and source manifest.

`--allow-dirty` is only for local packaging diagnostics. Never publish those
archives or manually rewrite the manifest to make an old artifact pass.

## Publish

Merge the release changes to `main` first. The **Publish Zcash runtime** workflow
is manual, accepts the exact version and `rc`/`latest` tag, and refuses other
branches. It reuses the build/test workflow, verifies the downloaded artifact
again, and publishes only these two npm packages. The native-module publish
workflow is unchanged.

Repository maintainers must configure `NPM_TOKEN` with permission to publish
`onekey-zcash-runtime` and `onekey-zcash-keys`. Configure required reviewers for
the `npm-zcash` GitHub environment if a separate release approval is desired.
The environment name alone does not enable approval protection.

npm cannot publish two packages atomically. If publishing one succeeds and the
other fails, do not consume the version until both exist. Restore the exact
verified artifact from that workflow run and publish only the missing package;
never overwrite an existing version or rebuild different bytes under that version.

## App cutover

Until the pair exists on npm, keep the App's local `portal:` dependencies. Once
both packages have been published, replace both entries in
`packages/core/package.json` with the same exact version and update `yarn.lock`
through the App repository's dependency workflow. Do not use a version range.
