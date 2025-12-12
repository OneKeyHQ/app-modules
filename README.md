# app-modules

## Create new package

## Publish all package

To update the versions of all workspace packages, run the following command in the project root directory:

```shell
yarn workspaces foreach --all  --no-private --topological version --deferred patch
yarn version apply --all
```
Commit version changes and push to GitHub.

Run publish package actions on GitHub.