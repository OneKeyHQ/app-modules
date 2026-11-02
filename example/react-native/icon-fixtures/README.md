# Icon fixtures

These 58 SVG samples support SDK examples and tests. The complete canonical
artwork and generator live in app-monorepo `packages/components/svg`. NativeList
coverage includes its legacy metadata aliases; Image additionally exercises
fixed and mixed colors, alpha, gradients, masks and non-square geometry.

`native/icons` contains raw samples plus source digests and classification.
`web/assets` contains the corresponding namespaced external symbols. Example
builds alias `#onekey-icon-assets` to `web/assetUrls.ts`. Production Image npm,
Pod and AAR artifacts must not include this directory or any named SVG.
