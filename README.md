# app-modules

## Create new package

### Create Nitro Module

```shell
npx create-react-native-library@latest new-lib
```

```shell
yarn package:setup new-lib
```


## Publish all package

To update the versions of all workspace packages, run the following command in the project root directory:

```shell
yarn version:bump
yarn version:apply
```
Commit version changes and push to GitHub.

Run publish package actions on GitHub.