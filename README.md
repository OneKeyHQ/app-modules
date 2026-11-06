# app-modules

[![Stable version](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-list/latest?label=stable)](https://www.npmjs.com/package/@onekeyfe/react-native-native-list?activeTab=versions)
[![Test version](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-list/next?label=test)](https://www.npmjs.com/package/@onekeyfe/react-native-native-list?activeTab=versions)

The badges track the npm `latest` (stable) and `next` (test) dist-tags of
`@onekeyfe/react-native-native-list`, which shares the synchronized package version.

## Create new package

Native package work must follow the
[Native Module Development Requirements](docs/NATIVE_MODULE_DEVELOPMENT.md),
including a package-level `docs/SPEC.md` and the post-change conformance audit.

### Create Nitro Module

```shell
yarn create:module <package-name>
```

### Create Nitro View

```shell
yarn create:view <view-name>
```

## Release automation

All publishable packages use one synchronized version tied to the target
app-monorepo version:

- Official releases: `<app-version>-N`, starting at 1, published with `latest`
  (for example, `6.7.0-1`, `6.7.0-2`).
- Test previews: `<app-version>-alpha.N`, published with `next`
  (for example, `6.7.0-alpha.248`). Counters are positive integers without leading
  zeroes. Plain versions, four-part versions, other suffixes, mixed workspace
  versions, and incorrect dist-tags fail validation. The existing isolated
  `pr-145` tag remains available for alpha previews only.

```sh
yarn version:set 6.7.0-1  # Set the target App baseline and official revision
yarn version:patch        # 6.7.0-1 -> 6.7.0-2; does not increment the App patch
yarn version:set 6.7.1-1  # Switch to the next App baseline
node scripts/validate-npm-dist-tag.mjs  # Check synchronized versions and channel
```

Run the version commands on macOS with Xcode and the Ruby version in
`example/react-native/.ruby-version`; install the project's gems with
`(cd example/react-native && bundle install)` first. Both commands check native
prerequisites before changing versions, then update package versions, run Yarn
install and Nitro codegen, and run `bundle exec pod install` to synchronize
`yarn.lock` and `example/react-native/ios/Podfile.lock`. Commit the generated
lockfiles with the package versions. They report success only after every step
passes; if synchronization fails, fix the error and rerun `yarn version:set`
with the same target version to finish without incrementing again.

The version scripts do not publish. References
between publishable `@onekeyfe/*` workspaces use `*` in dependency, peer, optional
and development sections, and version changes preserve those wildcards. Private
example `workspace:*` links stay local. App consumers keep exact dependency pins.
App baselines and counters within each channel cannot move backwards. Choose the
baseline for the intended app-monorepo release; it is not inferred from a moving
remote branch.

Every merged same-repository PR targeting `main` automatically publishes a stable
release with `latest`, whether or not the PR changes package versions. CI preserves
the checked-out App baseline and chooses a numeric revision greater than the source
numeric revision and every package's published `latest` revision on that baseline
(for example, `6.7.0-26` becomes `6.7.0-27`). Alpha counters do not affect stable
revision numbers. An alpha source with no existing stable revision starts at `-1`.
A rerun chooses a new revision above any already published packages, including a
partially published release. Registry errors abort before manifests are changed;
missing packages (HTTP 404) have no published counter.

Development branches publish alpha previews only when `package-publish` is run
manually with `next` (or the existing isolated `pr-145` tag). Select the development
branch in **Run workflow**. The alpha counter is at least the workflow run number
and greater than the source and every package's published same-baseline `next`
counter. Isolated `pr-145` previews also check that tag. Merging development work
into `main` promotes the code through a newly numbered stable release.

CI prepares synchronized versions and refreshes the Yarn lockfile for all-package
runs; generated versions are not committed back to the branch. For single-workspace
recovery, provide `only_workspace`, `recovery_version` and the full `recovery_commit`
from the failed release's summary. CI checks out that exact source commit, verifies
it belongs to the selected branch and matches any packages already published at
that version, and prepares the same synchronized version without incrementing it.
It publishes only the selected package, safely skipping an existing version or
restoring its dist-tag. Recovery refuses to move newer tags backwards.
Manual `latest` runs are restricted to `main`; alpha runs require
a development branch. Releases finish after npm registry verification, allowing
10 minutes for versions and dist-tags to become visible. Consumer dependency
updates are managed separately. Source PR approval is also stable release approval.

Both formats are prereleases to npm/SemVer, even though numeric revisions are
official releases by this repository's convention. SemVer sorts `6.7.0-alpha.N`
above `6.7.0-1`; app-monorepo must keep exact dependency pins. Existing plain
versions are accepted only when comparing old registry tags or consumer pins
during migration, never as new release targets.

Merged fork PRs are intentionally skipped with a workflow summary notice. Run
`package-publish` manually on `main` with `latest` when ready. The manual action
remains available for development previews and single-workspace recovery.

Retain the existing `NPM_TOKEN` secret for npm publishing.

## Chain runtimes

Rust chain libraries (Zcash and Monero) live in [chain-runtimes](./chain-runtimes/README.md),
separately from React Native workspaces; see their build and test commands there.
