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

Merging a same-repository PR into `main` creates or updates a release PR with synchronized package versions, `yarn.lock`, and a `CHANGELOG.md` entry. A merged fork PR is intentionally skipped with a workflow summary notice; run `prepare-release` manually when ready. Write detailed release notes under `Unreleased` in the change PR when needed; the release PR also lists every merged commit since the previous release. Once the release PR merges, `package-publish.yml` verifies the release PR was created by the configured App, publishes missing packages with the npm `latest` tag, verifies all published versions on npm, and opens an app-monorepo PR against `x` for the mobile, components, root dependency pins, and lockfile updates. The existing manual publish action remains available for `next`, `latest`, and single-workspace recovery.

Before enabling the workflows, install a GitHub App on both `app-modules` and `app-monorepo` with repository Contents (write), Pull requests (write), and Packages (read) permissions. Set repository variable `APP_RELEASE_APP_ID` and secret `APP_RELEASE_PRIVATE_KEY` in `app-modules`, and retain the existing `NPM_TOKEN` secret for npm publishing. The App creates release and dependency PRs; its token allows the PR checks to run without the approval gate applied to PRs created with `GITHUB_TOKEN`.

The current `main` ruleset requires one approval and has no bypass actor, so the release PR must be approved before automatic publication can continue. Granting a dedicated App a narrowly scoped ruleset exception is an administrator decision, not part of this workflow. The app-monorepo PR is never merged automatically.

The app-monorepo job checks the existing module-ID registry but does not regenerate it: `module-id:update` requires a fresh Union Build module-ID map, which is unavailable in a clean dependency-update job. If a release adds native runtime modules, generate that map and update the registry during app-monorepo PR validation before merging it.
