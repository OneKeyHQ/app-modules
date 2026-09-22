# app-modules

## Create new package

### Create Nitro Module

```shell
npx create-react-native-library@latest new-lib
```

```shell
yarn package:setup new-lib
```


## Release automation

Every merged same-repository PR is classified by whether it changes a publishable workspace version. A change without version updates gets an ephemeral next-patch `-alpha.<workflow run number>` version across all publishable packages and is published with the npm `next` tag; those preview versions are not committed back to `main` and do not update app-monorepo. A change that updates package versions publishes the exact synchronized stable version from the PR with the npm `latest` tag, without another automatic version or changelog change. After registry verification, a latest release opens an app-monorepo PR against `x` for the mobile, components, root dependency pins, and lockfile updates. Source PR approval is therefore also release approval when that PR changes versions.

Merged fork PRs are intentionally skipped with a workflow summary notice; run `package-publish` manually when ready. The manual action remains available for `next`, `latest`, and single-workspace recovery.

Before enabling the workflow, install a GitHub App on `app-monorepo` with repository Contents (write) and Pull requests (write) permissions. Set repository variable `APP_RELEASE_APP_ID` and secret `APP_RELEASE_PRIVATE_KEY` in `app-modules`, and retain the existing `NPM_TOKEN` secret for npm publishing. The app-monorepo PR is never merged automatically.

The app-monorepo job checks the existing module-ID registry but does not regenerate it: `module-id:update` requires a fresh Union Build module-ID map, which is unavailable in a clean dependency-update job. If a release adds native runtime modules, generate that map and update the registry during app-monorepo PR validation before merging it.
