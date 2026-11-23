# Package README Guide

Every package under `native-modules/**` and `native-views/**` is published to npm
on its own, so its `README.md` is the package's front door. This guide keeps those
READMEs consistent, scannable and honest. It follows the structure used by
well-maintained React Native libraries: a centered hero with live npm/license
badges, a short pitch, feature bullets, a minimal install/usage path, an API
table, an explicit platform matrix, and links to the authoritative contract.

The README is not the contract. A package's `docs/SPEC.md` (where it exists)
authoritative for behavior, limits, lifecycle and failure modes. The README
should tell a reader what the package is for and how to start; it must not
contradict the spec.

## Required structure

Use this order. Omit a section only when it cannot apply (for example, there is
no `docs/SPEC.md`).

1. **Hero** — centered `#` package name, one-sentence pitch, badge row.
2. **Overview** — two to four sentences: the problem, what the package exposes,
   the platform shape, and the main boundary or limitation.
3. **Features** — three to six bullets, each naming the benefit first.
4. **Installation** — the `yarn add` command and the native rebuild steps,
   required peer dependencies (including every `@onekeyfe/*` native package the
   podspec or Gradle file links at build time), permissions/Info.plist entries
   when needed.
5. **Usage** — one small, real example (use the package's tests or example app
   code rather than inventing one); one or two sentences about ownership.
6. **API** — a table of the public exports, signatures and platform support.
7. **Platform support** — a matrix, or a single sentence when every capability
   matches across platforms.
8. **Caveats** — zero to three GitHub alert blocks for real constraints only.
9. **References** — the spec, the example app screen, and the changelog.
10. **Upstream attribution** — only for forked packages; keep the existing text
    and links, including the upstream repository and license notice.
11. **License** — `MIT`, linking the package `LICENSE`.

Keep a package README under about 150 lines. `react-native-native-list` is the
exception: it also documents the row/layout model, and that deep material stays
in the README below the standard sections.

## Hero

```markdown
<div align="center">

# @onekeyfe/react-native-<name>

**<One-line pitch: what it does and for whom.>**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-<name>?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-<name>)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-<name>?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>
```

- Use the dynamic npm badge rather than a hardcoded version; the version moves on
  every release.
- Keep only the platform badges that the package actually supports. The Web badge
  is:

  ```markdown
  ![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
  ```

- Use the badge for the package's actual native technology:
  - `Nitro_Modules` for Nitro packages,
  - `Fabric` (`![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)`) for Fabric views,
  - `TurboModule` (`![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)`)
    for packages that still use the legacy TurboModule bridge,
  - `NativeModule` (`![NativeModule](https://img.shields.io/badge/NativeModule-4D6BFE)`)
    for packages that use the older `NativeModules` bridge without a codegen spec.

  Do not label a TurboModule or NativeModule package as Nitro.
- Link the license badge to the package `LICENSE` file.

## Platform support

Use a table with one row per observable capability:

```markdown
| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Render the view | ✅ | ✅ | ✅ |
| Native animation | ✅ | ✅ | — |
```

Use `✅` for fully supported, `—` for unsupported. If a platform is supported
with a caveat, write the caveat in the cell (for example `⚠️ no animation`) and
explain it under Caveats.

## Writing rules

- Verify every claim against `package.json`, `src/**`, `ios/**`, `android/**`,
  `docs/SPEC.md`, tests or the example app. Do not invent props, defaults,
  benchmarks, screenshots or acceptance results.
- Prefer concrete, current information over history. Move process detail,
  ownership notes and long explanations to `docs/SPEC.md` or the root docs.
- Use `> [!NOTE]`, `> [!IMPORTANT]` and `> [!WARNING]` for caveats; reserve them
  for behavior a caller can get wrong.
- Code fences need language tags (`sh`, `ts`, `tsx`, `xml`, `ruby`, `groovy`).
- Link with relative paths inside the repository, for example
  [`docs/SPEC.md`](docs/SPEC.md), [`src/index.tsx`](src/index.tsx) and
  [`example page`](../../example/react-native/pages/NativeListRowStylePage.tsx).
- Keep upstream attribution and license text intact for forked packages. You may
  move it into a collapsed `<details>` block, but do not drop or rewrite it.
- Do not add project-wide badges (downloads, stars, CI) to package READMEs; those
  belong to the root README.
- When adding or removing a package, update the package tables in the root
  `README.md`.
