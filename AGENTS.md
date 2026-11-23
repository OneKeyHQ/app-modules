# Repository instructions

## Native modules and views

For every task that creates or changes files under `native-modules/**` or
`native-views/**`:

1. Before editing, read [`docs/NATIVE_MODULE_DEVELOPMENT.md`](docs/NATIVE_MODULE_DEVELOPMENT.md)
   and the affected package's `docs/SPEC.md` when it exists.
2. Create new packages with the repository's `yarn create:module` or
   `yarn create:view` template command. Do not hand-build or copy a package
   skeleton.
3. Define the contract in `docs/SPEC.md` before implementing new behavior. Keep
   the spec and code in the same change whenever a public contract, lifecycle,
   boundary, platform behavior, limit, or failure mode changes.
4. After editing, audit the implementation against the spec, decide explicitly
   whether the spec needs another update, run the smallest relevant validation,
   and report the result.
