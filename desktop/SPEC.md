# Desktop native modules

## Ownership and scope

Desktop native source lives under `desktop/{win,mac,linux}/<module>`.
These modules are not root Yarn workspaces and never participate in mobile
version bumps, npm publishing, or mobile dependency synchronization.
The first Windows module is `electron-windows-hello`; macOS and Linux have
no release jobs or modules yet.

## Version and validation baseline

`release.json` is the reviewed release baseline. Its exact stable version
uses the Electron major as its major: Electron 36 -> 36.0.1, Electron 37 ->
37.0.1. Minor versions add desktop functionality; patches fix behavior or
change build inputs. Published versions and tags must never be replaced.
The initial baseline is 44.0.1 because app-monorepo currently pins Electron
44.5.1. No Electron downgrade is implied.

`electronVersion` records the exact Electron runtime tested, not a claim that
every patch of that major is compatible. `rustToolchain` pins the compiler;
Cargo.lock and each module's package-lock.json pin build dependencies.
Hosted runner images, MSVC and SDK versions are recorded in build metadata,
but are not byte-for-byte reproducibility guarantees. Rebuild under a new
desktop version when changing build inputs. Never fetch a moving upstream
branch during a release.

## Windows release contract

`desktop-win-check.yml` runs baseline validation, native x64/ARM64 tests and
packaging checks on desktop pull requests, with read-only repository permissions.
It has no publication, synchronization or attestation-writing jobs, so PRs do
not show skipped release/synchronization checks. Its artifacts are unsigned
validation outputs, not approved releases.
`desktop-win-release.yml` runs only on manual dispatch from main in
OneKeyHQ/app-modules. It builds and tests all modules listed in `modules.win`,
each on native x64 and ARM64 runners, then attests, releases and synchronizes.
Additional Windows modules must define their SPEC, locked build, test and
pack contracts before being added. Unknown modules fail validation.

The tag is `desktop-win-v<version>`. Each module ships a dependency-free
runtime tarball with the desktop release version, without install scripts.
No npm package is published. A signed `desktop-win.json` lists the source
commit, baseline, tarball SHA-256, and every runtime file's SHA-256.
Native binaries are attested in their build jobs; the packaging job verifies
those attestations before signing the tarballs and release manifest.
Actions use full commit pins. Build jobs have no release or cross-repository
write credentials. Release creation and app synchronization are separate jobs.
Release assets are retained as GitHub Release assets, not an expiring CI cache.
Existing tags/releases are never overwritten. After a successful publication,
rerun only failed jobs to retry synchronization; do not rebuild that version.

## Consumer and synchronization contract

After publishing and verifying the downloaded release, create or reuse a PR
against app-monorepo's x branch. Never merge it or push directly to x.
The Windows Hello integration must already be merged there. The sync refuses
an Electron baseline mismatch and version downgrades. It updates both desktop
dependency manifests and lockfiles to explicit GitHub Release tarball URLs,
copies the reviewed verifier, and records the source commit and manifest digest.
No mobile dependencies or Electron version are changed.

The app's install-app-deps flow verifies the release manifest's pinned digest,
repository, workflow, source commit, main ref, and hosted-runner attestation.
It hashes the installed runtime files without loading native code and rejects
missing, modified or unexpected files. Missing proofs or GitHub outages fail
closed; there is no npm fallback. Non-Windows builds do not invoke this gate.
The root development copy uses the same pinned tarball; the packaged appDir
copy is checked after dependency installation and runtime patches.
Consumers review the sync PR before accepting a new version.
The manifest and portable Sigstore bundle are downloaded from the explicit
public release URL. Verification requires GitHub CLI with attestation support,
but does not require a GitHub login or CI token: gh verifies the bundled proof.
Existing synchronization PRs, including closed ones, are reported and never
overwritten or silently reopened.

## Security and verification limits

Provenance establishes origin, not the absence of malicious source, dependency
code, or a compromised compiler/runner. Independent reproducibility has not
been established. Protect main, release tags, and workflow changes with review.
Keep the existing APP_RELEASE_APP_ID / APP_RELEASE_PRIVATE_KEY GitHub App
limited to app-monorepo contents and pull requests; these are required for
automatic synchronization. No npm credential is needed by this workflow.

Automated tests cover version/schema validation, package boundaries,
tampering, attestation policy arguments, sync safety and retry behavior.
Native tests and Electron utility-process smoke tests run on both architectures.
Headless tests do not establish Hello focus or user interaction correctness.
Windows 11 owner/focus behavior was manually verified in the original module;
Windows 10 interactive verification remains pending.
