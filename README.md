# app-modules

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
yarn install             # Refresh the lockfile after changing versions
(cd example/react-native/ios && bundle exec pod install) # Refresh pod versions/checksums
```

The version scripts update package versions and exact internal dependency pins;
they do not publish. A numeric release must be chosen explicitly after a preview.
App baselines and counters within each channel cannot move backwards. Choose the
baseline for the intended app-monorepo release; it is not inferred from a moving
remote branch.

Every merged same-repository PR without version changes gets an ephemeral
`<app-version>-alpha.N` version across all publishable packages,
published with `next`; the App baseline is preserved and preview versions are not
committed back to `main`. A PR that updates numeric release versions publishes the
exact synchronized version with `latest`, without another automatic version or
changelog change. Alpha version changes also select `next`. The automatic counter
is at least the workflow run number and greater than the committed same-baseline
alpha counter and every package's published `next` counter. Isolated `pr-145`
previews also check that tag. Registry errors abort before manifests are changed;
missing packages (HTTP 404) have no published counter.
Manual all-package `next` runs also generate previews. Single-workspace recovery
publishes the checked-out version exactly, so it must already match the requested
channel. After registry verification, both preview and latest releases open an
app-monorepo PR against `x` for the mobile, components, core, kit, playground, root dependency pins, and
lockfile updates. Source PR approval is therefore also release approval.

Both formats are prereleases to npm/SemVer, even though numeric revisions are
official releases by this repository's convention. SemVer sorts `6.7.0-alpha.N`
above `6.7.0-1`; app-monorepo must keep exact dependency pins. The sync script
compares App baselines first and counters within the same channel, while allowing
explicit numeric/alpha channel changes on the same baseline. Existing plain
versions are accepted only when comparing old registry tags or consumer pins
during migration, never as new release targets.

Merged fork PRs are intentionally skipped with a workflow summary notice; run `package-publish` manually when ready. The manual action remains available for `next`, `latest`, and single-workspace recovery.

Before enabling the workflow, install a GitHub App on `app-monorepo` with repository Contents (write) and Pull requests (write) permissions. Set repository variable `APP_RELEASE_APP_ID` and secret `APP_RELEASE_PRIVATE_KEY` in `app-modules`, and retain the existing `NPM_TOKEN` secret for npm publishing. The app-monorepo PR is never merged automatically.

The app-monorepo job checks the existing module-ID registry but does not regenerate it: `module-id:update` requires a fresh Union Build module-ID map, which is unavailable in a clean dependency-update job. If a release adds native runtime modules, generate that map and update the registry during app-monorepo PR validation before merging it.

## Chain runtimes

Rust chain libraries (Zcash and Monero) live in [chain-runtimes](./chain-runtimes/README.md),
separately from React Native workspaces; see their build and test commands there.
