# Desktop native modules

## Ownership and scope

Desktop native source lives under `desktop/{win,mac,linux}/<module>`.
These modules are not root Yarn workspaces and never participate in mobile
version bumps, mobile npm publishing, or mobile dependency synchronization.
The first Windows module is `electron-windows-hello`, published as
`@onekeyfe/electron-windows-hello`; macOS and Linux have
no release jobs or modules yet.

## Version and validation baseline

`release.json` is the reviewed release baseline. Its exact stable version
uses the Electron major as its major: Electron 36 -> 36.0.1, Electron 37 ->
37.0.1. Minor versions add desktop functionality; patches fix behavior or
change build inputs. Published versions must never be replaced.
The npm preview baseline is 44.0.2-alpha.1 because app-monorepo currently pins Electron
44.5.1. The new packaging contract does not reuse the previously distributed
44.0.1 version. No Electron downgrade is implied.

`electronVersion` records the exact Electron runtime tested, not a claim that
every patch of that major is compatible. `rustToolchain` pins the compiler;
Cargo.lock and each module's package-lock.json pin build dependencies.
Hosted runner images, MSVC and SDK versions are recorded in build metadata,
but are not byte-for-byte reproducibility guarantees. Rebuild under a new
desktop version when changing build inputs. Never fetch a moving upstream
branch during a release.

## Windows release contract

`desktop-win-check.yml` runs baseline validation, native x64/ARM64 tests and
unsigned runtime inventory checks on desktop pull requests, with read-only repository permissions.
It has no publication, synchronization or attestation-writing jobs, so PRs do
not show skipped release/synchronization checks. Its artifacts are unsigned
validation outputs, not approved releases. Packaging tests exercise the archive
boundary with fixture proofs, which are not valid attestations.
`desktop-win-release.yml` runs only on manual dispatch in OneKeyHQ/app-modules.
Main may publish stable versions to `latest` or alpha previews to `next`.
Other source branches may publish only `<version>-alpha.N` (N >= 1) to `next`;
stable versions from other branches and tag/PR refs are rejected. A preview
never moves `latest`. It builds and tests all modules listed in `modules.win`,
each on native x64 and ARM64 runners, then attests and publishes npm packages.
Additional Windows modules must define their SPEC, locked build, test and
pack contracts before being added. Unknown modules fail validation.

Each module ships a dependency-free npm package with the desktop release
version under the `@onekeyfe` scope, without install scripts. Local module
directories and native binary names retain the unscoped module identifier.
The signed inventory requires the exact scoped `packageName` and npm archive
name (`onekeyfe-<module>-<version>.tgz`); unscoped substitutes are rejected.
npm is the only distribution channel:
the workflow creates no GitHub Release or release tag and consumers never
download GitHub Release assets. Historical publications are not deleted.

Native binaries and build metadata are attested in their build jobs. The
packaging job verifies those attestations against the exact dispatched branch ref and commit,
workflow and hosted-runner policy before preparing the runtime inventory.
It signs the schema-2 `desktop-win.json` inventory and embeds that file and
its portable `desktop-win.sigstore.json` proof in each npm archive alongside
the seven runtime files. The inventory records source branch ref, source commit, baseline,
build environments, archive name and each runtime file's SHA-256. It does
not hash itself, its proof or the enclosing archive, avoiding circular hashes.
The completed archive is separately attested before the publish job accepts it.

The isolated publish job verifies the archive attestation before extraction,
then verifies the embedded inventory proof, source, baseline and all files.
It uses the existing `runtime-npm-publish` environment and `NPM_TOKEN` convention
to publish with npm provenance, public access and the validated dist-tag.
The token must have publish permission for `@onekeyfe/electron-windows-hello`;
permission failures require maintainer action, not an authentication fallback.
Registry/network errors fail closed. Existing npm versions are rejected before
building and again before publication; npm also rejects version replacement.
After publication, the existing registry-visibility checker confirms the version
and selected dist-tag. The workflow then checks registry SHA-512 against the
exact approved archive, repository identity and npm provenance metadata.

Actions use full commit pins. Native and packaging jobs have no npm publishing
credential. The token is exposed only to the isolated publication step. The
workflow does not access or modify consumer repositories.

## Consumer integration contract

Consumer integration is manual and reviewed in the consumer repository.
There is no desktop synchronization script, cross-repository job or automatic PR.
After npm publication, consumers verify the packaged proof, pin both desktop
dependency manifests to the exact npm version, and generate both lockfiles
from the registry. They copy the reviewed verifier and record the actual
source branch ref, source commit and embedded inventory digest. Never invent future lockfile
checksums, commits or inventory digests before publication. The Electron baseline
must match the tested release. No mobile dependencies or Electron version change.

The app's install-app-deps flow reads proof metadata from the installed npm
package, not a separate download. It verifies the inventory's pinned digest,
repository, workflow, exact source commit, exact approved source branch ref,
and hosted-runner attestation. Both source and signer digests must match the
approved commit. Branch previews require an explicit reviewed source-ref pin;
there is no default-to-main or accept-any-branch verification policy.
It hashes the installed runtime files without loading native code and rejects
missing, modified or unexpected files, allowing only the two required proof files
in addition to the signed seven-file runtime inventory. Missing proofs fail
closed; there is no legacy npm or GitHub Release fallback. Non-Windows builds do
not invoke this gate. The root development copy uses the same pinned npm version; the packaged appDir
copy is checked after dependency installation and runtime patches.
Consumers review the integration changes before accepting a new version.
The inventory and portable Sigstore bundle ship inside the npm package.
Verification requires GitHub CLI with attestation support,
but does not require a GitHub login or CI token: gh verifies the bundled proof.
Trust-root verification may require network access.

Consumer Release Age and Trivy workflows and policies remain unchanged. A new
npm version must satisfy the normal release-age window and vulnerability checks
before the consuming PR is mergeable. No age exceptions, source allowlists,
scanner skips or policy bypasses are introduced.

## Security and verification limits

Provenance establishes origin, not the absence of malicious source, dependency
code, or a compromised compiler/runner. Independent reproducibility has not
been established. Protect main and workflow changes with review.
No consumer-repository GitHub App credentials are required. npm publishing
requires the existing CI credential to have access to the desktop package.

Automated tests cover version/schema validation, package boundaries,
registry errors, tampering and attestation policy arguments.
Native tests and Electron utility-process smoke tests run on both architectures.
Headless tests do not establish Hello focus or user interaction correctness.
Windows 11 owner/focus behavior was manually verified in the original module;
Windows 10 interactive verification remains pending.
