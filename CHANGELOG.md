# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Documentation
- **native-list**: Add `docs/STYLE_SPEC.md`, the shared style vocabulary for rows, section headers, fixed footers, and empty states. It records the design tokens (aliased to the application's own token names), the per-template style surface keyed by model field, list chrome, the template isolation rules, and a review checklist. Cross-platform divergences — row-height tables, typography, the Android sticky-header renderer, list-wide source scale — are registered rather than changed.

### Chores
- **native-list (Web)**: Remove dead duplicated rules from `WEB_LIST_CSS`. Four blocks (`.ok-native-list-footer`/`-sticky`/`-index-rail`/`-index-button`, `-refresh`, `-warning`, `-subtitle-segments`) were emitted twice and a `prefers-reduced-motion` block three times; every property of the earlier copies was redeclared by the later ones, so removing them changes no rendering.

## [3.0.156] - 2026-09-21

### Features
- **image (iOS and Android)**: Add a `round` prop to OneKeyImage and keep rounded clipping stable during Android navigation snapshots and recycled NativeList rows.

### Bug Fixes
- **native-list (iOS)**: Dispose the native host view tree when Nitro drops a list. Account-selector modals create two NativeLists; the previous drop hook only invalidated action anchors, so every close left both collection views, their visible cells, image hosts, and thousands of constraints reachable until the wrapper was collected. Disposal is now idempotent, detaches delegates and callbacks, releases list data and reusable views, and drops the host view immediately.

### Chores
- Bump all 41 publishable packages to 3.0.156.

## [3.0.152] - 2026-09-20

### Bug Fixes
- **native-list (Android)**: Draw the full 38dp pressed circle behind the account selector row menu (OK-63780). The `DotHorOutline` accessory overhangs its 24dp slot with -7dp margins; the trailing column clipped children and the row clipped to padding, so the press state showed as a 24dp-wide pill while iOS and desktop drew the full circle.

### Chores
- Bump all 41 publishable packages to 3.0.152.

## [3.0.151] - 2026-09-20

### Bug Fixes
- **native-list (iOS)**: Settle the nested row stacks before centering Market empty-state titles (OK-63717).

### Chores
- Bump all 41 publishable packages to 3.0.151, including the NativeList 3.0.149 and NativeSheet 3.0.150 fixes.

## [3.0.150] - 2026-09-20

### Bug Fixes
- **native-sheet (iOS)**: Keep the sheet background transparent while dragging below its detent, and restore the backdrop after a cancelled drag-to-dismiss gesture.

## [3.0.149] - 2026-09-19

### Bug Fixes
- **native-list (iOS)**: Re-measure rows when the list's width changes after its first layout (OK-63486). `NativeListFlowLayout` sizes rows in `sizeForItemAt` from the collection view's width, but the invalidation context UIKit builds for a bounds change leaves `invalidateFlowLayoutDelegateMetrics` off, so the delegate was not asked again. Rows kept their old width, and the flow layout centered a narrower row in the list. In app-monorepo's iPad account selector, react-native-screens lays the sheet content out at the parent stack's width (494 pt in landscape split view) before the sheet frame (840 pt) arrives, so the account list showed as a narrow centered column; rotating with the selector open did the same. A later data update re-measured the rows, which made the bug look intermittent. The layout now invalidates on every cross-axis size change and asks for delegate metrics in that context. Android rows are `MATCH_PARENT` and were not affected.
- **pager-view (Android)**: Stop `CollapsiblePagerView` native tabs from truncating their labels at display densities such as 450 dpi (OK-63273). Tabs with the progress indicator reserved a combined `dp(16)` of padding, which rounds to 45 px there, while the `TextView`'s two 8 dp insets round to 23 px each, so a 90 px label got 89 px and was ellipsized. The tab width now adds the button's actual compound padding.

### Chores
- Bump all 41 publishable packages to 3.0.149.

## [3.0.148] - 2026-09-18

### Features
- **image-crop-picker (iOS and Android)**: Replace the TOCropViewController and uCrop screens with one cropper screen that looks like an app page and is the same on both platforms. The iOS cropper had TOCropViewController's dark toolbar with plain-text Cancel and Done, whatever the app theme, and Android's uCrop screen had a toolbar with a check mark plus aspect, rotate and scale tabs, so the two looked unrelated. The new screen has a header with the title and a rotate button, the crop area, and a footer with capsule Cancel and Confirm buttons, with the same metrics on both platforms. iOS hosts TOCropView in `ImageCropperViewController`; Android hosts uCrop's `UCropView` in `ImageCropperActivity`.
  - Add `cropperAppearance`: `colorScheme`, background, title, icon and button colors, title and button fonts, and a size `scale`. Colors are CSS hex strings, and anything left out falls back to OneKey's light or dark palette.
  - The area outside the crop box shows the page background at 70% opacity instead of TOCropViewController's dark blur or uCrop's black dimming. The crop box border uses the title color, and its corner handles only show when the box can be resized (`freeStyleCropEnabled`).
  - The rule-of-thirds grid shows while the image is moved, on Android as well. Android no longer rotates with two fingers, as iOS never did, and its rotate button animates the turn.
  - Android keeps the calling activity's requested orientation and draws edge to edge, with status and navigation bar icons that follow `colorScheme`.
  - iOS: `TOCropOverlayView` gains `frameColor`, `gridColor` and `cornerHandlesHidden`.

### Bug Fixes
- **image-crop-picker (iOS)**: Reopen the cropper when the same photo is tapped again after cancelling it. `PHPickerViewController` keeps the photo selected when the cropper is dismissed back to it, so the first tap only deselected it and nothing seemed to happen. On iOS 17 and later the picker's selection is now cleared; the configuration passes the shared photo library to get asset identifiers, which asks for no permission.
- **native-logger (iOS)**: Keep log files after the first roll. `DDLogFileManagerDefault` defaults `logFilesDiskQuota` to 20 MB, the same as the configured `maximumFileSize`, so a single rolled file filled the quota and cleanup deleted every log file, including the one being written. The logger then wrote nothing for the rest of the session and exported log bundles had no `.log` files. The quota now follows the retention of 7 files × 20 MB, matching Android's `TOTAL_SIZE_CAP`.
- **lite-card (iOS)**: Stop Lite card callbacks from firing twice when the NFC sheet is cancelled right after a card connects, which crashed the app with SIGABRT in `RCTTurboModule.mm` ("Callback arg cannot be called more than once") in the backup and restore flow on 6.6.0. The user's cancel was reported while the card operation was still running on another thread, and the operation's failed APDUs then reported a connection failure through the same callback. Each completion is now taken out under a lock when delivered, the app's own `invalidateSession` is no longer treated as a user cancel, `connectToTag` failures and non-Lite cards report a connection failure instead of leaving the JS promise pending, and repeated results are logged and dropped instead of aborting.

### Breaking Changes
- **image-crop-picker**: Remove `cropperChooseColor`, `cropperCancelColor`, `cropperActiveWidgetColor`, `cropperToolbarColor`, `cropperToolbarWidgetColor`, `cropperStatusBarLight`, `cropperNavigationBarLight`, `showCropFrame`, `enableRotationGesture`, `hideBottomControls` and `disableCropperColorSetters`. They styled the TOCropViewController and uCrop screens; use `cropperAppearance` instead.

### Chores
- Bump all 41 publishable packages to 3.0.148.

## [3.0.147] - 2026-09-18

### Features
- **image-crop-picker (new)**: Add `@onekeyfe/react-native-image-crop-picker`, a Nitro module that replaces `react-native-image-crop-picker` 0.51.1. app-monorepo installs it under the `react-native-image-crop-picker` npm alias, so imports stay the same. It keeps `openPicker`, `openCropper`, `clean`, `cleanSingle` and the `E_*` rejection codes that OneKey uses, and drops multiple selection, video and the camera.
  - **iOS**: Pick photos with `PHPickerViewController`, which needs no photo library permission (OK-48227). `react-native-image-crop-picker` requested full library access before showing its picker. After a user tapped "Don't Allow" once, iOS never asked again and every later `openPicker` call rejected with `E_NO_LIBRARY_PERMISSION`. The OneKey ID avatar and hardware wallpaper entries swallowed that rejection, so tapping them did nothing.
  - **iOS**: Crop with a vendored TOCropViewController 3.2.0, up from 2.8.0, with the OK-51551 fix that keeps the crop box from shrinking on every rotation re-applied. Upstream 3.2.0 still has that bug. The app no longer needs its TOCropViewController pod override.
  - **Android**: Port the existing flow to Kotlin: the system Photo Picker (no storage or media permission) and uCrop 2.2.11-native, the latest release. Activity results go through the activity's `ActivityResultRegistry` instead of an `ActivityEventListener`.
  - Decode photos at most 4096 px on the long side, so a 48 MP photo no longer needs about 200 MB of memory before cropping.

### Chores
- Bump all 41 publishable packages to 3.0.147.

## [3.0.146] - 2026-09-17

### Bug Fixes
- **auto-size-input (Android)**: Load the input, prefix and suffix font from the app's bundled assets (OK-57825). `makeTypeface()` resolved `fontFamily` with `Typeface.create`, which only knows system font families, so a bundled family such as `Roobert-Medium` silently fell back to the system font on Android while iOS resolved it through `UIAppFonts`. In app-monorepo's send amount input, the digits and the token symbol rendered in Roboto or the OEM system font. The typeface now comes from `ReactFontManager`, the lookup React Native `Text` uses: registered custom fonts, then `assets/fonts/<family>.ttf|otf`, and only then `Typeface.create`. Auto-size measurement uses the same typeface, so the fitted font size follows the real glyph widths. The `fontFamily` and `fontWeight` props are unchanged.

### Chores
- Bump all 40 publishable packages to 3.0.146.

## [3.0.145] - 2026-09-17

### Bug Fixes
- **pager-view (iOS)**: Scroll the `CollapsiblePagerView` native tab bar and native sub-header when a drag starts on a tab or category item (regressed in 3.0.142). The items are `UIButton`s in horizontal scroll views that deliver touches immediately, so an item starts tracking on touch-down, and `UIScrollView` does not cancel `UIControl` touches by default: only a drag that began in the gap between two items scrolled. The bug stayed hidden while the page's NativeList collection view, which hosts the shared headers, kept UIKit's default `delaysContentTouches = true` and held touch-down back for about 150 ms, long enough for the drag to be recognized first. NativeList's quick-tap press fix in 3.0.142 turned that delay off. `touchesShouldCancelInContentView:` now returns `YES` for controls, as NativeList's collection view does, so a drag cancels the item's touch and scrolls the bar while a tap still presses the item.

### Chores
- Bump all 40 publishable packages to 3.0.145.

## [3.0.144] - 2026-09-17

### Features
- **pager-view (iOS and Android)**: Add `nativeTabPressAnimationEnabled` to `CollapsiblePagerView`, default `true`. Set it to `false` to jump straight to the pressed page (OK-63487). A native tab press always dispatched an animated `setPage`, so pressing a distant tab scrolled through every page in between. Pages outside `pageRetentionDistance` are unmounted, so they showed blank while neighboring lists slid past, and app-monorepo's Market home had to send a second, non-animated command from `onNativeTabPress` to override the animation. The pressed page is still retained before the command is dispatched, so it is mounted when the pager moves.

### Chores
- Bump all 40 publishable packages to 3.0.144.

## [3.0.142] - 2026-09-17

### Bug Fixes
- **native-list (iOS)**: Show the pressed row background on quick taps. The list's `UICollectionView` kept UIScrollView's default `delaysContentTouches = true`, so a row received touch-down only after about 150 ms. A tap released inside that window highlighted, unhighlighted and selected the row in one run loop turn, and the pressed color never rendered; Android rows set it on `ACTION_DOWN`. A private `NativeListCollectionView` now delivers touches immediately, and `touchesShouldCancel(in:)` returns `true` for in-row controls so a drag that starts on a tag badge, accessory or checkbox still scrolls the list. `didHighlightItemAt`, and with it `pressInActionKey`, now runs on touch-down, as it does on Android.

### Chores
- Bump all 40 publishable packages to 3.0.142.

## [3.0.141] - 2026-09-16

### Bug Fixes
- **pager-view (Android)**: Load the native tab bar, category item and column label fonts from the app's bundled assets (OK-63273). They resolved `fontFamily` with `Typeface.create`, which only knows system font families, so a bundled family such as `Roobert-Medium` silently fell back to the system font on Android while iOS resolved it through `UIAppFonts`. The typeface now comes from `ReactFontManager`, the lookup React Native `Text` uses: registered custom fonts, then `assets/fonts/<family>.ttf|otf`, and only then `Typeface.create`. The `fontFamily` prop is unchanged. This upstreams the patch-package patch app-monorepo carried for `react-native-pager-view`.

### Chores
- Bump all 40 publishable packages to 3.0.141.

## [3.0.140] - 2026-09-16

### Features
- **tab-view (Android)**: Add `ignoreKeyboardInsets`, default `true`. Set it to `false` to pad the tab bar by the keyboard height so it rises above the soft keyboard. The IME inset is read explicitly instead of through the compat system-window inset, and combined with the navigation bar inset so the prop stays independent of `ignoreBottomInsets`. The value lives in JS, so apps can switch the behavior without a native release.

### Bug Fixes
- **tab-view (Android)**: Keep the bottom tab bar under the soft keyboard (OK-63557). Material's `BottomNavigationView` padded itself with `getSystemWindowInsetBottom()`, which carries the IME height while the window uses `adjustResize`, and react-native-keyboard-controller switches the window to `adjustResize` whenever a `KeyboardAwareScrollView`, `KeyboardStickyView` or keyboard animation hook is mounted. On those screens the bar grew by the keyboard height, its items sat directly on top of the keyboard, and `onTabBarMeasured` reported the inflated height to JS. The bar now pads for system bars and display cutouts only unless `ignoreKeyboardInsets` is turned off.
- **tab-view (Android)**: `setIgnoreBottomInsets(false)` no longer clears the inset listener, which also dropped the navigation bar padding. Both inset flags are re-applied when a uiMode change recreates the bottom navigation view.

### Chores
- Bump all 40 publishable packages to 3.0.140.

## [3.0.139] - 2026-09-16

### Chores
- **release tooling**: The publish workflow trusted npm's exit code, so a package that npm accepted but never made available still turned the run green — exactly how `@onekeyfe/react-native-bundle-crypto@3.0.137` shipped as a silent partial release. Added `scripts/verify-published.mjs`, which reads each published version back from the registry origin (`?write=true`, since the read-through CDN can serve hours-stale documents and would otherwise fail healthy releases) and requires both that the version is listed and that the dist-tag points at it, polling for up to 10 minutes before failing the run.
- **release tooling**: Added an `only_workspace` input to the publish workflow. Retrying one failed package previously meant re-running the whole release, where the packages that already published reject with E403 and turn the run red before proving anything about the one that mattered.
- Bump all 40 publishable packages to 3.0.139. Package contents are unchanged from 3.0.138 — this release exercises the new post-publish verification on a real run.

## [3.0.138] - 2026-09-16

### Bug Fixes
- **bundle-crypto (iOS)**: Drop the x86_64 simulator architecture from the vendored `Gopenpgp.xcframework`. The package was 14.9 MB packed / 42.5 MB unpacked — 9x the next largest package here and ~100x the median — because the framework carries three ~10 MB gomobile static archives, one of them a fat simulator slice holding both arm64 and x86_64. Now 10.7 MB packed / 30.7 MB unpacked. The slice directory is renamed to match its contents and the xcframework manifest updated; the podspec vendors the whole xcframework, so nothing else referenced the old name. **Intel Mac simulator builds are no longer supported by this module**; Apple Silicon simulator, device, and Mac Catalyst are unchanged. Symbol stripping is not an alternative — `strip -S` rejects the gomobile archives with "string table not at the end of the file".

### Chores
- Bump all 40 publishable packages to 3.0.138. 3.0.137 shipped for 39 of them, but npm left `@onekeyfe/react-native-bundle-crypto@3.0.137` in a staged-but-never-committed state — undownloadable, and permanently rejecting republishing with `409 Cannot publish over previously staged version`. The version number is unrecoverable, so the whole set moves to 3.0.138 to stay in lockstep.

## [3.0.137] - 2026-09-16

### Bug Fixes
- **split-bundle-loader (Android)**: Stop the builtin segment extractor from racing itself. The main and background runtimes resolve the same segment independently, and `extractSemaphore` throttles I/O rather than excluding concurrent work on one path, so both could extract the same segment at once into a shared `<name>.tmp` — two `O_TRUNC` writers on one inode, with `renameTo` failing for whichever thread lost. That loser reported `SPLIT_BUNDLE_NOT_FOUND` for a file that was already on disk and complete, and the JS loader caches that code as a permanent failure, so a millisecond-wide race blanked a route for the rest of the process. Seen on the first launch after an APK replace, where the install-stamp wipe forces every segment to re-extract at once. Extraction is now serialized per path, each attempt writes a uniquely named temp file (so no two writers can publish a partially zeroed HBC, across processes too), and a failed rename re-checks the destination before reporting the segment missing.

### Chores
- Bump all 40 publishable packages to 3.0.137. Published for 39 of them; `@onekeyfe/react-native-bundle-crypto@3.0.137` never became available (see 3.0.138).

## [3.0.136] - 2026-09-15

### Bug Fixes
- **native-list (Android)**: Keep token icons visible after a theme change by showing `hideUntilLoaded` images again when they already display the requested source (OK-50498).
- **native-list (iOS and Android)**: Remember recently failed image sources so recycled or rebound rows restore their fallback text or icon immediately instead of flashing an empty slot, and size fallback icons to the image slot, drawing the `GlobusOutline` fallback at 1.2x.
- **native-list (Android)**: Keep the source fallback icon measured while an image loads so an asynchronous failure can reveal it without another layout pass, call the image error handler on every failed load while retries continue, and detach the exact child view before recycling it during rapid nested scrolling.
- **native-list (iOS)**: Call the image error handler on every failed load while retries continue, so a row rebound during the retry delay still records its failed source and restores the fallback instead of rendering an empty slot.
- **native-list (Web)**: Keep the default cursor on the wallet sidebar, hardware wallet group member, account, and Add account row surfaces while icon buttons, checkboxes, and action buttons inside those rows keep their own pointer cursor, and round the Add account row's hover and pressed backgrounds with the 12px account row radius.
- **image**: Use the border overlay and borderless container for every image with rounded corners, not only rounded images that also set a border width.
- **pager-view (iOS)**: Fix the Release archive build failure "no matching function for call to 'RNCPruneReleasedPageStates'" by letting Objective-C++ callers pass typed released-state dictionaries.

### Chores
- Compile the pager-view released-state policy tests as Objective-C++ so the typed-dictionary call site is covered.
- Bump all 40 publishable packages to 3.0.136.

## [3.0.135] - 2026-09-15

### Bug Fixes
- **native-list (iOS)**: Keep row heights in sync while a wallet group is dragged in the wallet sidebar. UIKit interactive movement lays rows out in the in-flight order, but sizes were still resolved from the pre-drag snapshot, which stretched the neighboring row and clipped the passed group's name (OK-62492).

### Chores
- Add the "Native List Wallet Sidebar Reorder" example page that replays the OK-62492 wallet sidebar recording.
- Bump all 40 publishable packages to 3.0.135.

## [3.0.134] - 2026-09-15

### Bug Fixes
- **pager-view (iOS)**: Release pager-owned scroll observers, shared headers, and insets before Fabric removes or reuses a page list, then recover the correct page identity and offset after relevant mounting transactions to avoid stale view ownership and layer cycles.
- **pager-view (iOS and Android)**: Render selected native category filters as full-height pills instead of using a fixed corner radius.

### Chores
- Bump all 40 publishable packages to 3.0.134.

## [3.0.133] - 2026-09-15

### Bug Fixes
- **auto-size-input (iOS)**: Honor the configured keyboard appearance for single-line and multiline inputs.
- **native-list**: Keep passive system rows non-interactive across iOS, Android, and Web; enforce `pressDisabled` for press-in and long-press gestures; center iOS network fallback text; restore Android/Web account-menu feedback; and defer Android section-index reparenting until detach completes.
- **native-sheet**: Stabilize iOS 26 detent backgrounds, presentation sizing, staged content, scale, and shadow suppression, and restore Android text-input focus and keyboard visibility while presenting a sheet.
- **tab-view (iOS)**: Clear recycled child autoresizing masks so reused Fabric views cannot expand to an unrelated parent.

### Chores
- Remove brittle native source-text assertion test suites.
- Bump all 40 publishable packages to 3.0.133.

## [3.0.132] - 2026-09-14

### Bug Fixes
- **auto-size-input (Android)**: Fit the `contentAutoWidth` font against the prefix/suffix labels measured at every candidate size, so a growing font next to a visible side label no longer hands the input a slot narrower than its text (leading digits scrolled out of view after the send-amount fiat toggle). Re-run the fit when `prefixMarginRight`/`suffixMarginLeft` change, because `requestLayout()` alone is swallowed by the React parent.

### Chores
- Bump all 40 publishable packages to 3.0.132.

## [3.0.131] - 2026-09-14

### Bug Fixes
- **image (Android)**: Decode bitmaps at the requested target size without accepting undersized cached resources.
- **native-sheet**: Include the Fabric header in CocoaPods, finalize queued closes before presentation, and preserve nested security-provider blocking ownership.

### Chores
- Bump all 40 publishable packages to 3.0.131.

## [3.0.130] - 2026-09-14

### Features
- **native-list**: Add keyboard dismissal and tap persistence controls for native list interactions.
- **native-sheet**: Add reusable native presentation support.

### Bug Fixes
- **image/native-list (Android)**: Preserve valid image drawables across snapshot rebinds and visible page transitions while guarding asynchronous reuse by image identity.
- **native-list**: Keep item indexes aligned during paging and dismiss the keyboard consistently while dragging on iOS and Android.
- **native-sheet**: Prevent reused presentation views from corrupting lifecycle state and remove the unwanted presentation shadow.

### Chores
- Bump all 40 publishable packages to 3.0.130.

## [3.0.129] - 2026-09-13

### Bug Fixes
- **pager-view (Android)**: Keep the native tab indicator snapped to the selected page instead of letting intermediate scroll progress pull it back after a tab press.
- **pager-view (iOS)**: Detach stale page scroll observers and restore shared headers while a replacement scroll container is mounting.
- **native-sheet (iOS)**: Hit-test backdrop taps against the sheet's container-space frame.

### Chores
- Bump all 40 publishable packages to 3.0.129.

## [3.0.128] - 2026-09-13

### Chores
- Bump all 40 publishable packages to 3.0.128.
- Publish NativeList after the four-job concurrent batch to avoid racing with regenerated image type declarations.

## [3.0.127] - 2026-09-13

### Bug Fixes
- **native-list (iOS)**: Preserve centered UILabel alignment when applying custom line heights to network avatar fallback text.

### Chores
- Bump all 40 publishable packages to 3.0.127.
- Publish workspaces with four concurrent jobs, then publish NativeList after its shared build dependency is stable.

## [3.0.126] - 2026-09-13

### Bug Fixes
- **image**: Render logical start/end borders in rounded image overlays for LTR and RTL layouts.
- **native-list**: Exclude non-draggable wallet-group children from reorder badges and drag initiation, and keep the iOS section index below later native presentations.
- **native-sheet**: Apply the configured dim amount to the iOS presentation backdrop.

### Chores
- Bump all 40 publishable packages to 3.0.126.

## [3.0.125] - 2026-09-13

### Bug Fixes
- **native-sheet**: Publish the new package through the same CI release command as the other workspaces.

### Chores
- Bump all 40 publishable packages to 3.0.125 for a complete CI release.

## [3.0.124] - 2026-09-13

### Features
- **native-sheet**: Add the initial cross-platform native sheet package.
- **pager-view**: Add explicit non-virtualized page scrollers and stabilize retained or replaced page scroll ownership.

### Bug Fixes
- **image**: Draw rounded native image borders above image content on iOS and Android.
- **native-list**: Center section indexes against the application window with RTL parity, preserve iOS account row feedback, and use the active background for Desktop/Web drag sources and reorder previews.
- **pager-view (Android)**: Restore the exact page scroller padding before a page is removed.

### Chores
- Bump all 40 publishable packages to 3.0.124.

## [3.0.122] - 2026-09-12

### Bug Fixes
- **pager-view (Android)**: Keep the horizontally scrollable native Market tab row centered on interpolated pager progress instead of jumping at rounded page boundaries.

### Chores
- Bump all 39 publishable packages to 3.0.122.

## [3.0.121] - 2026-09-12

### Bug Fixes
- **pager-view (Android)**: Complete nested native gesture lifecycles after horizontal header scrolling and keep the native tab indicator correctly sized before the first pager transition.

### Chores
- Bump all 39 publishable packages to 3.0.121.

## [3.0.120] - 2026-09-12

### Features
- **pager-view (Android)**: Add opt-in native primary tabs and a scrollable Stocks subheader with pager-progress-driven selection styling.

### Bug Fixes
- **pager-view (Android)**: Route header vertical drags to the active list, preserve horizontal subheader ownership, cancel presses after directional drags, and log low-frequency pager lifecycle diagnostics.

### Chores
- Bump all 39 publishable packages to 3.0.120.

## [3.0.119] - 2026-09-12

### Features
- **pager-view**: Add iOS native tab and scrollable subheader rendering for collapsible pagers.

### Bug Fixes
- **pager-view**: Coordinate nested horizontal and vertical gestures while cancelling React Native presses after a drag begins.
- **native-list**: Align image fallback, section index positioning, and iOS interactive reorder behavior.

### Chores
- Bump all 39 publishable packages to 3.0.119.

## [3.0.118] - 2026-09-11

### Features
- **image**: Add configurable theme-aware placeholder colors and skip unnecessary image fades for memory-cache hits while respecting reduced-motion settings.
- **native-list**: Expose cross-platform avatar preloading and align selector image fallback and reuse behavior across iOS, Android, and Web.

### Chores
- Bump all 39 publishable packages to 3.0.118.

## [3.0.116] - 2026-09-10

### Bug Fixes
- **native-list**: Complete the published Market row layout, refresh indicator, recycled-cell reset and action-anchor contracts on iOS and Android.

### Chores
- Bump all 39 publishable packages to 3.0.116.

## [3.0.115] - 2026-09-10

### Features
- **pager-view**: Add a collapsible pager with coordinated native scrolling and retained page positions on iOS and Android, plus a Web implementation.
- **native-list**: Add Market row styles, source badges, long-press actions, skeleton loading and pagination spinners.
- **example**: Add the mobile Market replica with complete real-data replay and live filters.

### Bug Fixes
- **native-list**: Align Market typography, image borders, row interactions and scroll insets with the original mobile UI.
- **example**: Lazy-load the Market screen and replay snapshot so other example routes do not evaluate the full capture at startup.

### Chores
- Bump all 39 publishable packages to 3.0.115 for the native Market integration release.

## [3.0.114] - 2026-09-09

### Bug Fixes
- **native-list (Android)**: Allow account-selector network badges to extend outside the avatar frame without leaking clipping state across recycled rows.

### Chores
- Bump all 39 publishable packages to 3.0.114.

## [3.0.112] - 2026-09-09

### Chores
- Bump all publishable packages to 3.0.112 after synchronizing the release branch with the latest main changes.

## [3.0.111] - 2026-09-09

### Bug Fixes
- **text (Android)**: Preserve React Native's dedicated selectable-text host when prepared text layout is enabled.

### Chores
- Bump all publishable packages to 3.0.111.

## [3.0.110] - 2026-09-09

### Bug Fixes
- **native-list (Android)**: Allow token-pair network badges to extend outside the avatar frame while restoring clipping when recycled rows reset.

### Chores
- Bump all publishable packages to 3.0.110.

## [3.0.109] - 2026-09-08

### Features
- **native-list (iOS and Android)**: Add opt-in window-safe-area centering for section index rails.

### Bug Fixes
- **native-list (Android)**: Keep the centered section index stable when the software keyboard opens.

### Chores
- Bump all publishable packages to 3.0.109.

## [3.0.108] - 2026-09-08

### Features
- **text (Android)**: Add an opt-in React Native Text-compatible component with a one-physical-pixel intrinsic-width guard against glyph-end clipping.

### Chores
- Publish `@onekeyfe/react-native-text` for the first time and bump all publishable packages to 3.0.108.

## [3.0.107] - 2026-09-08

### Bug Fixes
- **image**: Select CDN renditions from five layout-size tiers with bounded density choices, reducing requests to eight pixel widths shared by rendering and preloading.
- **image**: Accept layout-size hints in the native view and preserve canonical URL encoding for shared cache identity.

### Chores
- Bump all publishable packages to 3.0.107.

## [3.0.106] - 2026-09-08

### Features
- **native-list**: Sync selector layouts, compact section indexing, touch interactions, and bounded avatar prefetching from the app integration patches.
- **image**: Support local blockie avatar generation and caching on iOS and Android.

### Bug Fixes
- **native-list**: Validate effective snapshot patches, refresh iOS summaries and size-changing rows, preserve pagination generation markers, and correct native visible-range and scrolling state.

### Chores
- Bump all publishable packages to 3.0.106 and replace the integration patches with the published implementation.

## [3.0.105] - 2026-09-07

### Features
- **native-list**: Add template-driven native lists for Web, iOS, and Android, including section indexing, initial scrolling, grouped wallet reordering, and action-anchor geometry.

### Chores
- Publish `@onekeyfe/react-native-native-list` for the first time and bump all publishable packages to 3.0.105.

## [3.0.104] - 2026-09-04

### Bug Fixes
- **image (Android)**: Bind rendered-image Glide requests to the host Activity so temporary ScreenStack Fragment teardown preserves visible images while backgrounding and Activity destruction retain bounded lifecycle cleanup.

### Chores
- Bump all publishable packages to 3.0.104.

## [3.0.103] - 2026-09-04

### Bug Fixes
- **background-thread (Android)**: Load the reusable DevSession common HBC from its restored file path while preserving the packaged asset fallback.

### Chores
- Bump all publishable packages to 3.0.103.

## [3.0.102] - 2026-09-04

### Bug Fixes
- **image (Android)**: Keep Glide requests independent of temporary native-stack Fragment teardown so images retained by React remain visible after closing an opaque task modal.

### Chores
- Bump all publishable packages to 3.0.102.

## [3.0.95] - 2026-08-31

### Features
- **device-utils**: Add `setNavigationBarAppearance` so the Android main UI runtime can keep the system navigation bar aligned with the app theme while preserving a cross-platform no-op on iOS.

### Bug Fixes
- **device-utils (Android)**: Handle bottom and side three-button navigation on Android 15+, remove the protection view in gesture mode, and use a readable black fallback on API 24–25 when dark navigation icons are unavailable.

### Chores
- Bump all publishable packages to 3.0.95.

## [3.0.94] - 2026-08-30

### Bug Fixes
- **tab-view (iOS)**: Use the iOS 18+ tab bar controller visibility API so UIKit keeps its internal hidden state synchronized across rotation, while retaining the direct tab bar fallback on earlier iOS versions.

### Chores
- Bump all publishable packages to 3.0.94.

## [3.0.93] - 2026-08-30

### Bug Fixes
- **text-input (Android)**: Restore React Native's intrinsic multiline measurement for the custom Fabric text input so `numberOfLines` determines the expected height without a JS fallback.

### Chores
- Bump all publishable packages to 3.0.93.

## [3.0.92] - 2026-08-29

### Bug Fixes
- **text-input (Android)**: Resolve the React event dispatcher when a paste occurs and safely skip the custom event when the surface is not ready, avoiding a first-mount null-pointer crash without affecting the system paste action.

### Chores
- Bump all publishable packages to 3.0.92.

## [3.0.91] - 2026-08-28

### Features
- **text-input**: Move `@onekeyfe/react-native-text-input` into app-modules and add package-owned iOS text/image paste handling so consumers no longer patch React Native's TextInput sources.

### Bug Fixes
- **background-thread**: Integrate the app-monorepo `x` runtime-generation, background-HMR teardown, and replacement-runtime lifecycle fixes on Android and iOS.
- **split-bundle-loader (iOS)**: Integrate dev-vendor entry loading, HMR setup, and cold-build synchronization from app-monorepo `x`.

### Chores
- Bump all publishable packages to 3.0.91.

## [3.0.90] - 2026-08-26

### Bug Fixes
- **background-thread (iOS)**: Add an ordered startup API that waits for the main JS runtime's shared bridge installation before creating an Expo-backed background runtime, preventing concurrent Expo module registration crashes.

### Chores
- Bump all publishable packages to 3.0.90.

## [3.0.89] - 2026-08-25

### Bug Fixes
- **background-thread (iOS)**: Initialize secondary runtimes through Expo's React Native factory when Expo is present, while preserving the plain React Native fallback.

### Chores
- Upgrade the development and example app baseline to React Native 0.86.2 and React 19.2.3, with a minimum iOS deployment target of 16.4.
- Bump all publishable packages to 3.0.89.

## [3.0.88] - 2026-08-25

### Bug Fixes
- **sni-connect (iOS)**: Canonicalize equivalent IP spellings for the shared session cache and pinned resolver registry so equivalent IPv6 addresses reuse the same bounded entry.

### Chores
- Bump all publishable packages to 3.0.88.

## [3.0.87] - 2026-08-25

### Chores
- **bundle-update (iOS)**: Align the MMKV pod dependency with `react-native-mmkv` 4.3.2 by upgrading MMKV from the 2.2 line to 2.4.0.
- **Nitro modules**: Upgrade `nitrogen` and `react-native-nitro-modules` from 0.35.2 to 0.36.5 across all Nitro packages, templates, and the example app.
- Bump all publishable packages to 3.0.87.

## [3.0.86] - 2026-08-24

### Features
- **sni-connect**: Add request IDs, per-runtime request cancellation APIs, and native admission debug snapshots.
- **sni-connect (Android/iOS)**: Add process-shared request admission with 64 global active requests, 16 active requests per canonical hostname/IP pair, and a bounded queue of 256 pending requests.

### Bug Fixes
- **sni-connect**: Include queue wait in the request deadline, pass only the remaining timeout to the transport, canonicalize equivalent IP spellings, and make pending/active cancellation and token release race-safe.

### Chores
- Bump all publishable packages to 3.0.86.

## [3.0.73] - 2026-06-30

### Features
- **network-throttle**: Publish the merged `@onekeyfe/react-native-network-throttle` native module from main.

### Chores
- Bump packages to 3.0.73 after 3.0.72 was already published.

## [3.0.72] - 2026-06-30

### Chores
- Bump packages to 3.0.72.

## [3.0.71] - 2026-06-29

### Features
- **sni-connect**: Add `@onekeyfe/react-native-sni-connect`, a native module for direct-IP HTTPS requests with SNI/certificate hostname verification, cancellation APIs, DNS cache cleanup, proxy detection, timeout controls, and multi-value response headers.
- **sni-connect (iOS)**: Implement EMASCurl-backed requests with a custom DNS resolver, request cancellation, DNS cache cleanup on memory warnings, and Pod integration for the example app.
- **sni-connect (Android)**: Implement OkHttp-backed direct-IP requests with hostname validation, pinned DNS resolution, request lifecycle tracking, cancellation, and proxy preflight support.

### Bug Fixes
- **sni-connect**: Harden request validation and safety checks across both platforms, including hostname/IP/path/method/header validation, private/reserved IP rejection, proxy preflight behavior, request-slot cleanup, and structured diagnostic logging.

### Documentation
- **sni-connect**: Add README, SPEC, SwiftPM test harness, and platform validation tests covering the SNI request contract.

### Chores
- **example**: Wire `@onekeyfe/react-native-sni-connect` into the example app and refresh iOS Pods.
- Bump all packages to 3.0.71.

## [3.0.70] - 2026-06-29

### Chores
- Republish packages as 3.0.70; no code changes.

## [3.0.69] - 2026-06-28

### Features
- **network-throttle**: Publish the short-lived `@onekeyfe/react-native-network-throttle` native development helper with `getConfig` / `setConfig` APIs and a slow-4G latency profile for iOS/Android request throttling.
- **sni-connect**: Publish the initial `@onekeyfe/react-native-sni-connect` release line with direct-IP HTTPS request support, cancellation, DNS cache cleanup, proxy detection, and native iOS/Android implementations.

### Chores
- Bump packages to 3.0.69.

## [3.0.68] - 2026-06-24

### Features
- **range-downloader**: Add OCDS v1.1 conformance support for iOS and Android concurrent downloads, including typed outcomes, monotonic progress gating, retry classification, segment artifact sweeping, and pure range-download logic shared with tests.
- **app-update / bundle-update**: Adopt the OCDS concurrent downloader path for APK and JS-bundle downloads, wiring typed failure handling and segment-file resume behavior through the module integrations.
- **tab-view**: Add `selectedIcons` support so native iOS tab-bar items can use a distinct selected image or SF Symbol.

### Bug Fixes
- **app-update (Android)**: Do not block APK update flow when package signing information is unavailable; treat unverifiable existing APK state as indeterminate instead of deleting or rejecting usable downloads.
- **app-update (Android)**: Await concurrent downloader cancellation before cache cleanup deletes `.segN` artifacts, preventing workers from racing deleted segment files.
- **range-downloader**: Reject malformed/multipart range responses, handle transient 5xx and 416 resume paths, guard read-only filesystem failures, and add single-flight run registry coverage.
- **bundle-update**: Carry OCDS-compatible concurrent-download handling through Android and iOS bundle installation paths.

### Documentation
- **range-downloader**: Add the OneKey Concurrent Download Standard (OCDS) spec, conformance README, iOS simulator harness, and Android/iOS unit-test coverage for the downloader contract.

### Chores
- **example**: Refresh iOS `Podfile.lock` after downloader changes.
- Bump all packages to 3.0.68.

## [3.0.67] - 2026-06-14

### Bug Fixes
- **split-bundle-loader (Android)**: Align the native `.so` LOAD segments to 16 KB pages with `target_link_options`, fixing Android 15 / API 35 page-size compatibility for the split-bundle JSI library.

### Chores
- Bump all packages to 3.0.67.

## [3.0.66] - 2026-06-12

### Features
- **range-downloader**: Unified shared `ConcurrentRangeDownloader` implementation used by both `app-update` and `bundle-update`; segments stream into `<partial>.segN` files and are concatenated into the final `.partial`, removing full-file pre-allocation and EROFS/ENOSPC risk on near-full devices.
- **app-update (Android)**: APK downloads moved from `cacheDir/apks` to `filesDir/apks` to avoid system cache reclamation (EROFS/ENOSPC) during downloads; `FileProvider` path updated while keeping legacy cache-path compatibility.

### Bug Fixes
- **app-update / bundle-update / range-downloader**: Harden concurrent range downloader and resume logic — use OkHttp `.use()` to close responses, validate 206 `Content-Range` start bounds, sweep stale sibling `.segN` artifacts, make progress reporting thread-safe via `AtomicInteger` + CAS, and remove unconditional ETag/`If-Range` pinning so final SHA256/GPG verify remains the correctness backstop.
- **app-update / bundle-update / range-downloader**: Clean up partial `.segN` files on promote/discard, close leaked ASC responses, fix duplicate progress events, and treat 206 `Content-Range` mismatches as retryable errors that wipe partial artifacts.
- **app-update (Android)**: Pass absolute path to the concurrent downloader so segment files are written under `filesDir` instead of the read-only root filesystem.

### Chores
- Bump all packages to 3.0.66.

## [3.0.65] - 2026-06-12

### Bug Fixes
- **split-bundle-loader (iOS)**: Replace the bare `dispatch_after` segment-eval watchdog with `SBLActiveWatchdog`, a `CLOCK_UPTIME_RAW` active-time accumulator that pauses on `WillResignActive` and resumes with a 500 ms grace on `DidBecomeActive`, preventing false `SPLIT_BUNDLE_TIMEOUT` white-screens when the app is suspended during cold start.
- **split-bundle-loader (iOS)**: Pause the active-time watchdog synchronously on lifecycle transitions so a pending timer tick cannot fold suspended time into the active interval.

### Chores
- Bump all packages to 3.0.65.

## [3.0.64] - 2026-06-12

### Bug Fixes
- **background-thread (Android)**: Harden coalesced runtime work against five concurrency issues — guard `gBgTimerExecutor` assignment under the timer mutex, keep queue items intact across transient `ptr==0` drains, clear orphaned `gPendingWork` entries after `nativeInvalidateSharedRpc`, re-arm drains on bridge install when the queue is non-empty, and guard against stale-id reschedules on empty queues.
- **background-thread (Android)**: Prevent background segment-eval replay and latch-stall across reload by treating an already-erased `gPendingBgEvals` entry as "drain claimed" and force-arming a drain whenever the coalesced queue is non-empty after bridge install.

### Chores
- Bump all packages to 3.0.64.

## [3.0.63] - 2026-06-12

### Bug Fixes
- **background-thread**: Coalesce background-thread runtime work so multiple `SharedRPC` calls and timer callbacks are batched into a single JS executor drain, reducing native→JS thread hopping and closing races where rapid consecutive calls could overrun the executor.

### Chores
- Bump all packages to 3.0.63.

## [3.0.61] - 2026-06-12

### Features
- **split-bundle-loader / background-thread**: Evaluate split-bundle segments before resolving `loadSegment` promises on iOS main/background and Android main/background, fixing the uncatchable "Requiring unknown module" Hermes fatal when Metro `import()` ran before the segment's `__d` definitions; adds a watchdog, retryable-vs-fatal error codes, and off-JS-thread file reads.

### Chores
- **example**: Wire every native module into the example app for iOS/Android build verification.
- Bump all packages to 3.0.61.

## [3.0.60] - 2026-06-11

### Chores
- Refresh example iOS `Podfile.lock` checksums and clarify bundle directory comments.
- Bump all packages to 3.0.60.

## [3.0.59] - 2026-06-11

### Features
- **app-update (Android) / bundle-update**: Add cache-pruning APIs — `clearApkCache` and `pruneStaleAppVersionBundles` — to safely reclaim disk space from old APK and OTA-bundle artifacts while protecting the current app/bundle versions and in-progress downloads.

### Bug Fixes
- **app-update (Android)**: Protect verified/pending-install APKs during cache cleanup; abort cleanup while a download is active and log skipped files to avoid delete races.
- **bundle-update (Android)**: Make `deleteDirectory` report real success/failure and warn on incomplete deletes instead of always reporting success.
- **bundle-update (iOS)**: Fix `appVersionFromStem` static method scope reference.

### Chores
- Bump all packages to 3.0.59.

## [3.0.58] - 2026-06-10

### Bug Fixes
- **device-utils (iOS)**: Guard `getAndClearColdStartLocalNotification` with `os_unfair_lock` so the deep-link payload is read exactly once even under concurrent calls.
- **chart-webview (Android)**: Harden pooled WebView reuse — use weak owner/warm-driver refs to avoid pinning disposed host contexts, validate `assetHost` to a bare hostname before it becomes the privileged-bridge origin, normalize `localBundle` to avoid double-slash URLs, and retry `forceDetach` before giving up on a stuck parent.

### Chores
- Bump all packages to 3.0.58.

## [3.0.57] - 2026-06-10

### Features
- **chart-webview**: Add configurable `androidAssetHost` prop so the WebView asset-loader domain can be overridden instead of hard-coded.

### Chores
- Bump all packages to 3.0.57.

## [3.0.56] - 2026-06-10

### Features
- **chart-webview**: Add warm-driver support so the pooled offline page boots off-screen and page→native callbacks are routed to the owner or warm-driver; source/bridge setters now apply synchronously, dropping the reconcile loop that caused infinite loops on Android.
- **chart-webview (Android)**: Pause the pooled WebView renderer when no host owns it and resume on claim, stopping off-screen WebView CPU/RAM growth; add `webviewDebuggingEnabled` prop and retry reparenting up to 12 frames.

### Bug Fixes
- **chart-webview (Android)**: Fix pooled WebView re-parenting white-screen/stuck-loading by forcefully clearing the parent on dispose before re-attaching.
- **chart-webview (Android)**: Remove ChartDBG diagnostic instrumentation while keeping operational pool logs.

### Chores
- Bump all packages to 3.0.56.

## [3.0.55] - 2026-06-09

### Features
- **device-utils (iOS)**: Add `getAndClearColdStartLocalNotification()` to read and clear a killed-app local-notification tap payload exactly once, enabling cold-start deep-link routing; the slot is in-memory only so a fresh process cannot replay a stale tap.

### Chores
- Bump all packages to 3.0.55.

## [3.0.54] - 2026-06-09

### Features
- **segment-slider**: Migrate to an uncontrolled model — `value` is replaced by `defaultValue` plus an imperative `setValue(value)` ref method, removing the controlled-value-vs-drag conflict and avoiding Fabric prop commits for programmatic updates.

### Bug Fixes
- **range-downloader / app-update**: Address concurrent-download audit findings — detect concurrent `.partial` via the `.progress` sidecar before size classification, terminate iOS `download()` completion when a segment goes missing/truncated, stream segment concatenation, require 206 + `Content-Range` match before stashing, and add `cancel(channel, taskId)`.
- **bundle-crypto**: Audit fixes — exact-basename skip rules, `removePrefix` relative paths, iOS path-traversal separator boundary, and RFC 4880 cleartext canonicalization/framing.
- **bundle-update**: Audit fixes — fail-closed on unlistable dirs, exact-basename skip, `removePrefix`, path-confinement separator boundary, hold `isDownloading` across skip delay, and signature-first atomic version persistence.
- **chart-webview**: Scope the privileged bridge to trusted origins / main frame only, refcount the pool, and wire teardown so the WebView is destroyed on host drop.
- **zip-archive (iOS)**: Preserve zip unzip error details in thrown messages.

### Chores
- **example**: Remove the TradingView asset fetch script.
- Bump all packages to 3.0.54.

## [3.0.53] - 2026-06-08

### Features
- **range-downloader / bundle-crypto**: New Nitro modules for concurrent multi-range downloading and crypto. Android adds 8-range concurrent download; iOS adds concurrent multi-range download. `bundle-update` and `app-update` now delegate downloading and crypto to the shared modules.
- **chart-webview**: New offline chart host with a pooled WebView reused across mount points; cached frame snapshots mask reparenting flash during switches. Android loads offline bundles from `assets/<localBundle>/`.
- **perp-depth-bar**: New native depth-bar view with per-row price/size text rendering and tap handling. Depth bars and side ratio use continuous exponential-smoothing easing. Added imperative `setDepth` / `setRatio` APIs and native placeholder-row support.
- **segment-slider**: New native Nitro slider view; tap snaps to the nearest mark while dragging remains free.
- **background-thread**: SharedRPC gains inline-value messaging; restart freshness is now validated.
- **zip-archive**: Migrated to a Nitro module.

### Bug Fixes
- **app-update (Android)**: APK download supports Range / `.partial` resume.
- **chart-webview**: Fixed reconcile handing off in one direction only; added attach generation to prevent stale reparenting.
- **perp-depth-bar**: Fixed imperative API races against high-frequency prop updates; iOS text snap alignment; main-thread safety for layout updates.
- **segment-slider**: Fixed `lastEmitted` sync when controlled value changes.
- **skeleton**: Guarded shimmer restart against no-op `afterUpdate`.
- **device-utils**: Exposed `LaunchOptionsStore` singleton to the ObjC runtime.
- **security**: Required SSZipArchive >= 2.5.4 to fix zip-slip CVEs.

### Chores
- Bump all packages to 3.0.53.

## [3.0.36] - 2026-05-14

### Features
- **perf-stats**: Add `addMemoryWarningListener` / `removeMemoryWarningListener` with a normalised `MemoryWarningEvent { level: 'low' | 'critical'; rss; timestamp }`. iOS maps `didReceiveMemoryWarning` → `critical`; Android maps `TRIM_MEMORY_RUNNING_*` and `onLowMemory()` with a 500 ms critical dedup. Auto-registers pre-`Application.onCreate` via a `ContentProvider` on Android.
- **perf-stats (iOS)**: On every warning, drop `URLCache.shared` and the WK HTTP / disk / offline-app caches (cookies / `localStorage` / IndexedDB preserved), then run `malloc_zone_pressure_relief`. Reclaim delta logged.
- **perf-stats (Android)**: On every warning (LOW + CRITICAL for parity), `Runtime.gc()` as an ART hint.
- **perf-stats**: New `cleanupNativeCaches()` to trigger the same reclaim path on demand; iOS does the libmalloc walk on a background queue.
- **perf-stats**: New `forceGarbageCollection()` — feature-detects `HermesInternal.gc` / `globalThis.gc`.

### Bug Fixes
- **perf-stats**: Clamp `intervalMs` to `[200 ms, 24 h]` and reject NaN/Inf at the JS↔native boundary (otherwise iOS `Int(Double)` traps and Android `Double.toLong()` saturates `postDelayed` to never fire).
- **perf-stats**: Move CPU baseline reset from `stop()` to `start()`'s cold-start path — the Android `stop()` reset raced an in-flight tick that could write its captured ticks back.
- **perf-stats**: One-shot `sample()` no longer writes the periodic CPU baseline (`recordBaseline=false`).
- **perf-stats (Android)**: Fix `UiFpsMonitor.start()` double-registering the Choreographer callback under concurrent calls (would fan out exponentially); idempotency check moved inside the main-thread post.
- **perf-stats (Android)**: `JsFpsHolder` switched to `AtomicReference<Pair<>>` so readers can't observe a fresh timestamp paired with a stale fps.
- **perf-stats (Android)**: Overlay drag/clamp uses the Activity `decorView` instead of `displayMetrics`, preventing the overlay from being dragged into the other pane under split-screen / foldable. Re-clamps on `onConfigurationChanged` so `android:configChanges` Activities recover from rotation.
- **perf-stats (iOS)**: Observe `UIScene.didDisconnectNotification` to drop dangling overlay refs on multi-window close. Re-clamp the label after `viewWillTransition` so rotation doesn't strand it off-screen.
- **perf-stats (iOS)**: Register `didReceiveMemoryWarning` observer synchronously inside the listeners-table lock — closes the gap where a warning posted between `add()` returning and `addObserver` running could miss the new listener.
- **perf-stats (iOS)**: `removeMemoryWarningListener` uses `Int(exactly:)` so a JS-passed `NaN` / `Inf` no longer traps.

### Chores
- Bump `@onekeyfe/react-native-perf-stats` to 3.0.36.

## [3.0.35] - 2026-05-13

### Chores
- Republish all packages as 3.0.35; no code changes.

## [3.0.34] - 2026-05-13

### Features
- **aes-crypto**: Add native AES-GCM `aesGcmEncrypt` / `aesGcmDecrypt` (hex I/O, 12-byte nonce, AAD-bound, returns `ciphertext || tag`). Android via `Cipher.getInstance("AES/GCM/NoPadding")`, iOS via CryptoKit `AES.GCM` in new `AesCryptoGcm.swift`.

### Bug Fixes
- **aes-crypto**: Enforce strict 12-byte GCM nonce on both platforms — Android's `GCMParameterSpec` previously accepted arbitrary lengths and produced iOS-incompatible ciphertexts.
- **aes-crypto**: Reject empty / NaN / Infinity / fractional / negative inputs at every entry point (`encrypt`/`decrypt`/`pbkdf2`/`hmac*`/`sha*`/`randomKey`/AES-GCM `key`/`nonce`/`aad`); empty AEAD plaintext is still allowed.
- **aes-crypto (Android)**: Preserve GCM auth tag for empty plaintext (would have mismatched iOS) and reject empty ciphertext (previously silently resolved to `""`, bypassing authentication).
- **aes-crypto (iOS)**: Unify all rejection codes to `"-1"` (CBC/CTR/PBKDF2/HMAC/SHA/randomKey/UUID); previously per-method strings.
- **aes-crypto (iOS)**: `AesCryptoGcmError` conforms to `LocalizedError` so JS gets a real message instead of Cocoa's default `(... error N.)`.
- **aes-crypto (Android)**: Remove dead branches in `encryptImpl` / `decryptImpl` / GCM impls now covered by entry-level guards.

### Documentation
- **aes-crypto**: JSDoc on `aesGcmEncrypt` / `aesGcmDecrypt` documents the contract; header / podspec comments explain the ObjC++ + Swift bridging quirks.

### Chores
- Bump all packages to 3.0.34. Legacy `fromHex` `strtol` leniency tracked as follow-up.

## [3.0.33] - 2026-05-13

### Bug Fixes
- **auto-size-input**: Dispatch `focus()` / `blur()` to the UI thread — Nitro hybrid view methods can run off-main, but `becomeFirstResponder` / `resignFirstResponder` (iOS, SIGTRAP) and `requestFocus` / `InputMethodManager` (Android, `CalledFromWrongThreadException`) must run on the main thread.

### Chores
- Bump `@onekeyfe/react-native-auto-size-input` to 3.0.33.

## [3.0.32] - 2026-05-12

### Features
- **background-thread**: New `restart(mode, reason)` TurboModule — `mode='ui'` reloads only the main runtime (language/currency/devSettings), `mode='all'` reloads both (OTA install, resetData); `reason` is forwarded to RN reload listeners for attribution.
- **background-thread (iOS)**: Two-stage post-reload health check (~3s + ~6s) self-heals integration omissions; self-respawn replays the host's last entry URL (cached as `lastEntryURL`) instead of falling back to the IPA `background.bundle` to avoid moduleId mismatch under OTA. Concurrent restarts gated by a monotonic `restartGeneration` counter.
- **background-thread (Android)**: `mode='ui'` soft-reloads via `ReactHost.reload(reason)`, `mode='all'` does a process restart; `triggerProcessRestart` now returns `Boolean` and propagates intent / `SecurityException` failures to the JS promise as `BG_RESTART_ERROR`.

### Bug Fixes
- **background-thread (iOS)**: Fix `EXC_BAD_ACCESS` on main reload — SharedRPC listener kept a `jsi::Function` tied to a torn-down runtime, and `RPCRuntimeExecutor` lambdas captured `RCTInstance` strongly. Per-listener `alive` flag flipped synchronously in `restart()`; lambdas now capture `RCTInstance` weakly.
- **background-thread**: `SharedRPC::invalidate(runtimeId)` now erases the dead listener entry instead of leaving it with `alive=false`, keeping `listeners_` clean across `mode='all'` restarts that never re-install.

### Chores
- Bump `@onekeyfe/react-native-background-thread` to 3.0.32.

## [3.0.31] - 2026-05-09

### Features
- **bundle-update (iOS)**: Persist `URLSession` resume data to a `<filePath>.resume` sidecar on download failure; next attempt resumes via `downloadTask(withResumeData:)` instead of restarting from byte 0. Targets the ~64% of failures that are `NSURL -1005` / `-1001`.
- **bundle-update (iOS)**: Snapshot resume data on `didEnterBackgroundNotification` via `cancel(byProducingResumeData:)` under a `beginBackgroundTask` window, so force-quit / OOM kills don't lose progress.
- **bundle-update (Android)**: Range-based resume via `<filePath>.partial` sidecar — retry sends `Range: bytes=<offset>-`; 206 resumes, 200 restarts, 416 wipes. Rename to final filename only after SHA256 verifies.
- **bundle-update**: Per-failure SHA256 subtype tagging via thread-local stamp (`SHA256_FILE_TRUNCATED`, `SHA256_OOM`, `SHA256_IO_<class>`, …) so the previously opaque Android `verifyPackage` analytics bucket can be split. iOS swaps raise-prone `readData(ofLength:)` for throwing `read(upToCount:)`.

### Bug Fixes
- **bundle-update**: Drop inner exception text from thrown messages to prevent path leakage — iOS `verifyBundleASC` embedded `SSZipArchive` paths containing install UUID; Android `getMetadata` embedded `org.json` text with local paths. Only an `IO_<class>` tag escapes now; full descriptions still go to OneKeyLog.
- **bundle-update**: Verify-stage SHA failures propagate the subtype (`Bundle SHA256 verification failed: <reason>`) so analytics splits download-stage and verify-stage failures into matching buckets.
- **bundle-update (iOS)**: Protect `activeDownloadFilePath` under `stateQueue` so foreground/background races can't observe torn state with `isDownloading`.
- **bundle-update (iOS)**: Invalidate `URLSession` in `deinit` so the session and `DownloadDelegate` don't outlive the module on hot-reload.
- **bundle-update (Android)**: Recover a crashed-before-rename `.partial` via promote+verify when size matches `expectedSize`, instead of unconditionally deleting it.
- **bundle-update (Android)**: Recover from HTTP 416 when `Content-Range: bytes */<total>` indicates the partial is already byte-complete.
- **bundle-update (Android)**: Sanitize `update/error` payloads through `sanitizeErrorMessageForEvent` so `/data/user/...` paths never leak to JS / analytics.
- **perf-stats (Android)**: Remove the WindowManager overlay on Activity destroy to prevent View/token leak across configuration changes.

### Chores
- Bump all packages to 3.0.31.

## [3.0.28] - 2026-05-08

### Features
- **perf-stats**: Add UI FPS (`Choreographer` / `CADisplayLink`) and JS FPS (rAF, pushed via `setJsFpsHint` with 2s staleness) to `PerfSample`. Anomaly logging extends to sustained `ui_fps <= 45` and `js_fps <= 30`.
- **perf-stats**: `start` / `stop` now auto-manage the JS FPS tracker; manual `startJsFpsTracker` / `stopJsFpsTracker` remain as escape hatches.

### Bug Fixes
- **perf-stats (iOS)**: Keep overlay above modal-presented controllers — switch from a `UILabel` on the key `UIWindow` to a dedicated `windowLevel = .alert + 1` `UIWindow` subclass that forwards taps outside the label via `hitTest`.

### Chores
- Bump all packages to 3.0.28.

## [3.0.25] - 2026-05-07

### Features
- **perf-stats**: New `@onekeyfe/react-native-perf-stats` Nitro module — periodic CPU% and RSS sampler with a debug overlay (Android Kotlin/JNI, iOS Swift).
- **perf-stats**: Log sustained CPU/RSS anomalies (CPU ≥ 150% or RSS ≥ 800 MB for 5 consecutive samples) to native-logger with per-category 30s cooldown.

### Bug Fixes
- **perf-stats**: Close `start` / `stop` race that stranded the sampler — move `running=true` and handler lifecycle into one synchronized block, plus a generation token so a tick whose scheduler was stopped drops itself instead of rescheduling on a quitting handler.
- **perf-stats (Android)**: Use literal `1005` for `ABOVE_SUB_PANEL` — `TYPE_APPLICATION_ABOVE_SUB_PANEL` is `@hide` in the public SDK and fails to compile by name.

### Documentation
- **perf-stats**: Rewrite README with the real API and scoped package name.

### Chores
- Bump all packages to 3.0.25.

## [3.0.24] - 2026-04-28

### Bug Fixes
- **bundle-update (Android)**: Annotate `BundleUpdateStoreAndroid.getCurrentBundle*JSBundle` with `@JvmStatic` so external reflection callers (e.g. `SplitBundleLoaderModule`) don't NPE on null-receiver invocation — without it, fresh OTA installs silently loaded the APK common bundle and crashed on moduleId mismatch.

### Chores
- Bump all packages to 3.0.24.

## [3.0.23] - 2026-04-24

### Bug Fixes
- **bundle-update (Android)**: Rewrite the `validateWebEmbedSha256` KDoc in prose — `web-embed/**` inside `/** ... */` was lexed as a nested comment opener (Kotlin block comments nest), swallowing the rest of the file as comment body and breaking Android release builds.

### Chores
- Bump all packages to 3.0.23.

## [3.0.22] - 2026-04-24

### Documentation
- **background-thread / split-bundle-loader**: Correct stale `common.jsbundle` references in comments to match the actual resource name (`common.bundle`). No runtime change.

### Chores
- Bump all packages to 3.0.22.

## [3.0.21] - 2026-04-23

### Features
- **background-thread (iOS)**: Prefer OTA-installed `common.bundle` / `background.bundle` over IPA built-ins via reflective `BundleUpdateStore` lookup, so a three-bundle OTA doesn't moduleId-mismatch a stale IPA copy. Migrated from `app-monorepo` patch.
- **bundle-update**: Three-bundle metadata support — parse `requiresCommonBundle` and `bundleFormat`; `validateBundleDescriptor` now requires `common.bundle` when set so split-thread OTAs fail closed. Android metadata parser also accepts boolean / numeric scalars.
- **bundle-update**: Add `validatedCurrentBundleInfo` cache keyed by `currentBundleVersion`, invalidated on every mutation, so startup path getters no longer re-run the full signature + sha256 pipeline.
- **bundle-update**: Fast-path entry sha256 at startup — only hash main + common + background (gated by metadata); full-tree validation still runs at install time, per-segment integrity is now enforced at `loadSegment` time.
- **bundle-update (iOS)**: Expose `BundleUpdateStore.currentBundleCommonJSBundle()` for background-runtime lookup.
- **split-bundle-loader**: Verify segment SHA-256 at `resolveSegmentPath` / `loadSegment` time, streamed in 64KB chunks. Empty per-segment hash is a hard fail under the three-bundle layout; older formats keep the back-compat skip. Migrated from `app-monorepo` patch.

### Bug Fixes
- **bundle-update**: Lazy web-embed sha256 verification + cache — walk `web-embed/**` at `getWebEmbedPath` time, reject files-not-in-metadata and metadata-without-files, cache by `bundleVersion`. Both platforms.
- **bundle-update (Android)**: Fail closed when `validateWebEmbedRecursive` gets `listFiles() == null`, instead of silently allowing a tampered/unlistable dir.
- **bundle-update (Android)**: `metadataRequiresCommonBundle` now uses OR semantics so `bundleFormat=three-bundle` is honored even when `requiresCommonBundle=false`, matching iOS.
- **bundle-update**: Close `installBundle` same-version reinstall race — also invalidate the validated-bundle cache at the end of the async body, so a concurrent reader can't re-cache pre-install state.
- **bundle-update (Android)**: Use constant-time `secureCompare` in `validateEntryBundlesSha256`, matching every other sha256 comparison in the file.
- **bundle-update (iOS)**: Lowercase the `bundleFormat` comparand at all three sites for parity with Android's case-insensitive semantic.
- **background-thread (iOS)**: Refuse IPA fallback when OTA main is active — if OTA common / background lookup fails (typical: bundle-update older than the consumer), don't fall back to IPA built-ins that would moduleId-mismatch the OTA main.
- **background-thread (iOS)**: Abort `hostDidStart` when OTA common loaded but OTA background missing, instead of installing SharedStore / SharedRPC against a runtime with no entry bundle. Tracked via new `_otaActiveAtBundleResolve` ivar.

### Refactors
- **bundle-update**: Extract `"three-bundle"` into a constant on both platforms (`bundleFormatThreeBundle` / `BUNDLE_FORMAT_THREE_BUNDLE`) so iOS and Android stay in lockstep.

### Chores
- **split-bundle-loader / background-thread**: Peer-depend on `@onekeyfe/react-native-bundle-update` `>=3.0.21` — both now reflectively call new `BundleUpdateStore` APIs.
- Bump all packages to 3.0.21.

## [3.0.20] - 2026-04-21

### Features
- **device-utils**: Add synchronous `getAndroidChannel()` and `getInstallerPackageName()` Nitro HostObject methods, both typed as closed string unions for compile-time safety (`AndroidChannel`: `direct` / `google` / `huawei` / `unknown`; `InstallerPackageName`: `appStore` / `testFlight` / `other` / `playStore` / `huaweiAppGallery` / `unknown`).
- **device-utils (Android)**: `getAndroidChannel()` reflects `BuildConfig.ANDROID_CHANNEL` with a candidate-package walk so `applicationIdSuffix` / sub-packaged Application classes resolve correctly; ships `consumer-rules.pro` to keep the field through R8.
- **device-utils (Android)**: `getInstallerPackageName()` uses `getInstallSourceInfo()` on API 30+ (deprecated `getInstallerPackageName()` below), mapping `com.android.vending` → `playStore`, `com.huawei.appmarket` → `huaweiAppGallery`, other → `other`, null → `unknown`.
- **device-utils (iOS)**: `getInstallerPackageName()` distinguishes AppStore / TestFlight / Other via `appStoreReceiptURL.lastPathComponent` + `embedded.mobileprovision`; simulator returns `unknown`. `getAndroidChannel()` returns `unknown` for API parity.

### Chores
- Bump all packages to 3.0.20.

## [3.0.19] - 2026-04-21

### Bug Fixes
- **split-bundle-loader (iOS)**: Apply `stringByStandardizingPath` to `otaRoot` / `builtinRoot` so the `hasPrefix` safety check survives the `/var → /private/var` canonicalization that was silently rejecting every candidate. Add diagnostic logs.
- **split-bundle-loader (Android)**: Wipe `onekey-builtin-segments/` whenever `PackageInfo.lastUpdateTime` changes (double-checked locking, called from every segment entry point) so APK overwrite installs don't reuse the previous build's HBC segments against drifted Metro module IDs.
- **split-bundle-loader (Android, NewArch)**: Switch segment registration from `hasCatalystInstance` + reflection fallback to `ReactContext.registerSegment(id, path, callback)` so bridgeless works.
- **background-thread (Android)**: Add a reflection-based, allowlist-gated Activity bridge — replays the current Activity onto the bg ReactContext and forwards lifecycle / Activity-result events only to listeners whose FQCN matches a registered prefix; non-allowlisted modules keep the prior baseline so no resource collisions with the UI host.
- **background-thread (Android, NewArch)**: `registerSegmentInBackground` now uses `ReactContext.registerSegment` so bridge and bridgeless share one code path.
- **background-thread**: Fix SIGSEGV on reload / teardown — make the timer worker joinable, stop+join in `nativeDestroy` before clearing shared state, intentionally leak remaining `jsi::Function` / `std::function` captures so destructors don't run on a dead runtime.
- **background-thread (iOS)**: Skip the `entryURL` override when it's the placeholder `"background.bundle"`, so release builds taking the split-bundle path actually fall through to common + entry.
- **cloud-fs (Android)**: Collapse the inverted `saveFile` ternary to `mimeType ?: guessMimeType(uriOrPath)` so caller-supplied MIME types win and guessing is the fallback.

### Features
- **background-thread**: New allowlist API on `BackgroundThreadManager` — `addBgActivityBridgeListenerClassPrefix(prefix)`, `setBgActivityBridgeListenerClassAllowlist(prefixes)`, `getBgActivityBridgeListenerClassAllowlist()`. Empty by default; host apps must register FQCN prefixes early in `Application.onCreate`.
- **split-bundle-loader**: Diagnostic logging (`[resolveSeg]` / `[extractBuiltin]` / `[install-stamp]` on Android, `[resolveAbs]` on iOS) for production "segment not found" triage.

### Chores
- Inline former `app-monorepo` patches (`@onekeyfe+react-native-background-thread+3.0.18.patch`, `@onekeyfe+react-native-split-bundle-loader+3.0.18.patch`) into source; no `patch-package` needed.
- Bump all packages to 3.0.19.

## [3.0.18] - 2026-04-10

### Features
- **background-thread**: Drain the Hermes microtask queue after every `nativeExecuteWork` so `Promise.then` / `async-await` continuations actually run on the background runtime (RN 0.74+ requires explicit `drainMicrotasks()` — without it, all awaits hang in bg).
- **background-thread**: Add cross-runtime timer primitives in `cpp-adapter.cpp` (timer worker thread, pending work queue, JSI-safe scheduling) underpinning split-bundle + bg-host `setTimeout`/`Promise` behaviour.

### Chores
- Bump all packages to 3.0.18.

## [3.0.17] - 2026-04-10

### Bug Fixes
- **tcp-socket / zip-archive / network-info / ping / async-storage / cloud-fs / dns-lookup**: Align Android TurboModule class names with their TS spec filenames so codegen resolves the native modules correctly.

### Chores
- Bump all packages to 3.0.17.

## [3.0.16] - 2026-04-10

### Bug Fixes
- **async-storage (Android)**: Correct codegen class name in `RNCAsyncStorageModule.kt`.

### Chores
- Bump all packages to 3.0.16.

## [3.0.15] - 2026-04-09

### Features
- **cloud-fs**: Align types and native implementations with the upstream source repo — replace `Object` params with proper TS types across the Spec, port `DriveServiceHelper` and the full `RNCloudFsModule` from Java to a Kotlin TurboModule, add Android Google Drive methods (`loginIfNeeded`, `logout`, `getGoogleDriveDocument`, `getCurrentlySignedInUserData`), add iOS stubs for Android-only methods, fix iOS `syncCloud` to return a boolean, re-add `createFile` to the Spec, and wire the Google Drive dependencies into Android `build.gradle`.

### Bug Fixes
- **async-storage (web)**: Resolve web type errors by adding `DOM` lib to tsconfig and declaring types for `merge-options`.

### Chores
- Patch bump workspaces and fix async-storage module files.
- Bump all packages to 3.0.15.

## [3.0.13] - 2026-04-08

### Features
- **async-storage**: Add web implementation (`NativeAsyncStorage`) backed by `localStorage`.

### Chores
- Bump all packages to 3.0.13.

## [3.0.11] - 2026-04-03

### Features
- **async-storage**: Add `AsyncStorageStatic` compatibility layer for legacy call sites.

### Bug Fixes
- **ping / pbkdf2 / network-info**: Fix compilation errors.
- **aes-crypto**: Sync patch changes — use Hex encoding for all I/O.
- **dns-lookup (iOS)**: Add `CFDataRef` cast in `DnsLookup.mm` to fix compilation.
- Align Android implementations with upstream originals.
- iOS compilation fixes verified with local build.

### Chores
- `gitignore` the `lib/` build output and exclude `.map` files from npm publish.
- Bump all packages through 3.0.11.

## [3.0.4] - 2026-04-03

### Bug Fixes
- **split-bundle-loader**: Fix stale reflection class name for `BundleUpdateStore` in Android `getOtaBundlePath()` — updated from `expo.modules.onekeybundleupdate.BundleUpdateStore` to `com.margelo.nitro.reactnativebundleupdate.BundleUpdateStoreAndroid`
- **native-logger**: Fix dedup logic suppressing error logs — comparison now includes level, tag, and message instead of message-only
- **background-thread**: Fix JNI GlobalRef leak on each `nativeInstallSharedBridge` call — wrap in `shared_ptr` with custom deleter
- **background-thread**: Fix `SharedRPC::reset()` crash from destroying `jsi::Function` on wrong thread — use intentional leak pattern
- **background-thread**: Fix `nativeDestroy` not resetting `SharedStore`, leaving stale data across restarts
- Correct codegen class names to match TS spec file names

### Chores
- Align all package versions to 3.x line (cloud-fs cannot use 1.x since npm already has 2.6.5)
- Bump all packages to 3.0.4

## [1.1.59] - 2026-04-03

### Bug Fixes
- **tcp-socket**: Correct header import to match codegenConfig name

### Chores
- Bump all packages to 1.1.59

## [1.1.58] - 2026-04-03

### Bug Fixes
- **cloud-fs**: Set version to 3.0.0 (npm already has 2.6.5, cannot publish lower)

### Chores
- Bump all packages to 1.1.58

## [1.1.57] - 2026-04-03

### Bug Fixes
- Add missing release scripts for cloud-fs, ping, zip-archive

### Chores
- Bump all packages to 1.1.57

## [1.1.56] - 2026-04-03

### Features
- **aes-crypto / async-storage / cloud-fs / dns-lookup / network-info / ping / tcp-socket / zip-archive**: Add Android TurboModule implementations for legacy bridge module replacements
- **tcp-socket**: Fix type definitions

### Chores
- Bump all packages to 1.1.56

## [1.1.55] - 2026-04-03

### Features
- **aes-crypto / async-storage / cloud-fs / dns-lookup / network-info / ping / tcp-socket / zip-archive**: Add TurboModule replacements for legacy React Native bridge modules (iOS + JS)

### Chores
- Bump all packages to 1.1.55

## [1.1.54] - 2026-04-02

### Chores
- Bump all packages to 1.1.54

## [1.1.53] - 2026-04-02

### Features
- **split-bundle-loader**: Add split-bundle timing instrumentation and update PGP public key
- **split-bundle-loader**: Add comprehensive timing logs for three-bundle split verification

### Chores
- Bump all packages to 1.1.53

## [1.1.52] - 2026-04-02

### Features
- **background-thread**: Add split-bundle common+entry loading strategy for background runtime

### Chores
- Bump all packages to 1.1.52

## [1.1.51] - 2026-04-01

### Features
- **split-bundle-loader**: Add `resolveSegmentPath` API and path traversal protection

### Bug Fixes
- **split-bundle-loader**: Resolve Android `registerSegmentInBackground` race condition
- **split-bundle-loader**: Enhance bridgeless support and robustness improvements

### Chores
- Bump all packages to 1.1.51

## [1.1.49] - 2026-04-01

### Features
- **split-bundle-loader**: Add `react-native-split-bundle-loader` TurboModule with `getRuntimeBundleContext` and `loadSegment` APIs
- **split-bundle-loader**: Expose `loadSegmentInBackground` from TurboModule API
- **bundle-update**: Add `registerSegmentInBackground` for late HBC segment loading

### Chores
- Bump all packages to 1.1.49

## [1.1.48] - 2026-03-31

### Features
- **bundle-update**: Support background bundle pair bootstrap — add `getBackgroundJsBundlePath`, metadata validation for `requiresBackgroundBundle` and `backgroundProtocolVersion`, and bundle pair compatibility checks

### Chores
- Bump all packages to 1.1.48

## [1.1.47] - 2026-03-31

### Features
- **background-thread**: Add SharedBridge JSI HostObject for cross-runtime data transfer between main and background JS runtimes
- **background-thread**: Implement Android background runtime with second ReactHost and SharedBridge
- **background-thread**: Replace SharedBridge with SharedStore + SharedRPC architecture
- **background-thread**: Add onWrite cross-runtime notification, remove legacy messaging
- **native-logger**: Add dedup for identical consecutive log messages

### Bug Fixes
- **background-thread**: Stabilize background thread runtime initialization
- **background-thread**: Initialize Android shared bridge at app startup
- **shared-rpc**: Rename `RuntimeExecutor` to `RPCRuntimeExecutor` to avoid React Native conflict
- **shared-rpc**: Prevent crash on JS reload by deduplicating listeners with runtimeId
- **shared-rpc**: Leak stale `jsi::Function` callback on reload to prevent crash

### Chores
- Bump all packages to 1.1.47

## [1.1.46] - 2026-03-19

### Bug Fixes
- **bundle-update**: Use `scheduledEnvBuildNumber` from task instead of stored `getNativeBuildNumber()` for buildNumber change detection in pre-launch pending task processing

### Chores
- Bump all native modules and views to 1.1.46

## [1.1.45] - 2026-03-19

### Bug Fixes
- **bundle-update**: Use `scheduledEnvBuildNumber` from task instead of stored `getNativeBuildNumber()` for buildNumber change detection in pre-launch pending task processing, so the check works even without a prior successful bundle install
- **react-native-tab-view**: Add `delayedFreeze` prop to control freeze/unfreeze delay on tab switch; defaults to immediate freeze for better iPad sidebar switching

### Chores
- Bump all native modules and views to 1.1.45

## [1.1.44] - 2026-03-19

### Bug Fixes
- **react-native-tab-view (iOS)**: Force iPad tab bar to bottom by overriding `horizontalSizeClass` to `.compact` on iPad (iOS 18+), preventing `UITabBarController` from placing tabs at the top

### Chores
- **react-native-tab-view (iOS)**: Remove verbose debug logging (KVO observers, prop change logs, layout logs, delegate proxy logs)
- Bump all native modules and views to 1.1.44

## [1.1.43] - 2026-03-18

### Features
- **bundle-update**: Add native-layer pre-launch pending task processing for iOS/Android — process pending bundle-switch tasks before JS runtime starts

### Bug Fixes
- **bundle-update**: Harden pre-launch pending task with entry file existence check and synchronous writes (commit instead of apply on Android)

### Chores
- Bump all native modules and views to 1.1.43

## [1.1.42] - 2026-03-18

### Features
- **bundle-update**: Add `clearDownload()` method that only clears the download cache directory
- **bundle-update**: Fix `clearBundle()` to also clear the installed bundle directory, aligning behavior across desktop/iOS/Android

### Chores
- Bump all native modules and views to 1.1.42

## [1.1.41] - 2026-03-17

### Features
- **bundle-update**: Add buildNumber change detection in `getJsBundlePath()` — clears hot-update bundle data and falls back to builtin JS bundle when native buildNumber changes
- **bundle-update**: Add `getNativeBuildNumber()` and `getBuiltinBundleVersion()` APIs on both iOS and Android

### Bug Fixes
- **bundle-update (iOS)**: Guard against empty stored buildNumber to match Android behavior in build number change detection
- **bundle-update (Android)**: Use `get().toString()` instead of `getString()` in `getBuiltinBundleVersion()` to handle numeric meta-data values that AAPT2 stores as Integer

### Chores
- Bump all native modules and views to 1.1.41

## [1.1.39] - 2026-03-17

### Features
- **device-utils**: Add boot recovery APIs and test buttons to DeviceUtilsTestPage

### Documentation
- Add acknowledgment READMEs for forked upstream projects: react-native-tab-view, TOCropViewController, react-native-get-random-values

### Chores
- Bump all native modules and views to 1.1.39

## [1.1.38] - 2026-03-14

### Features
- **scroll-guard**: Add new `@onekeyfe/react-native-scroll-guard` native view module that prevents parent scrollable containers (PagerView/ViewPager2) from intercepting child scroll gestures
- **scroll-guard**: Support `direction` prop with horizontal, vertical, and both modes

### Bug Fixes
- **scroll-guard (Android)**: Use `ViewGroupManager` instead of nitrogen-generated `SimpleViewManager` to properly support child views (fixes `IViewGroupManager` ClassCastException)
- **scroll-guard (iOS)**: Improve gesture blocking reliability

### Chores
- Add `nitrogen/` to `.gitignore` and remove tracked nitrogen files
- Bump all native modules and views to 1.1.38

## [1.1.37] - 2026-03-12

### Features
- **bundle-update**: Add `resetToBuiltInBundle()` API to clear the current bundle version preference, reverting to built-in JS bundle on next restart

### Chores
- Bump all native modules and views to 1.1.37

## [1.1.36] - 2026-03-10

### Features
- **auto-size-input**: Add `contentCentered` prop to center prefix/input/suffix as one visual group in single-line mode

### Bug Fixes
- **auto-size-input (Android)**: Improve baseline centering and centered-layout width calculations for single-line input
- **auto-size-input (iOS)**: Align auto-width sizing behavior with Android so content width and suffix positioning stay consistent

### Chores
- Bump all native modules and views to 1.1.36

## [1.1.35] - 2026-03-10

### Features
- **pager-view**: Add local `@onekeyfe/react-native-pager-view` package in `native-views` with iOS and Android support
- **example**: Add PagerView test page and route integration, including nested pager demos

### Bug Fixes
- **pager-view**: Fix layout metrics/child-view guards and scope refresh-layout callback to the host instance
- **tab-view**: Guard invalid route keys in tab press/long-press callbacks and safely resync selected tab on Android

### Chores
- Bump all native modules and views to 1.1.35

## [1.1.34] - 2026-03-09

### Bug Fixes
- **auto-size-input**: Use `setRawInputType` on Android so the IME shows the correct keyboard layout without restricting accepted characters; JS-side sanitization handles character filtering
- **auto-size-input**: Skip `autoCorrect` and `autoCapitalize` mutations on number/phone input classes to avoid stripping decimal/signed flags and installing a restrictive KeyListener

## [1.1.33] - 2026-03-09

### Bug Fixes
- **tab-view**: Remove `interfaceOnly` option from `codegenNativeComponent` to fix Fabric component registration

## [1.1.32] - 2026-03-09

### Features
- **tab-view**: Add new `@onekeyfe/react-native-tab-view` Fabric Native Component with native iOS tab bar (UITabBarController) and Android bottom navigation (BottomNavigationView)
- **tab-view**: Add `ignoreBottomInsets` prop for controlling safe-area inset behavior
- **tab-view**: Add TabView settings page for Android with shared store
- **tab-view**: Add OneKeyLog debug logging for tab bar visibility debugging
- **tab-view**: Migrate to Fabric Native Component (New Architecture), remove Paper (old arch) code on iOS
- **example**: Add TabView test page and migrate example app routing to `@react-navigation/native`
- **example**: Add floating A-Z alphabet sidebar, emoji icons, and compact iOS Settings-style module list to home screen

### Bug Fixes
- **tab-view**: Prevent React child views from covering tab bar
- **tab-view**: Prevent Fabric from stealing tab childViews, fix scroll and layout issues
- **tab-view**: Forward events from Swift container to Fabric EventEmitter on iOS
- **tab-view**: Resolve Kotlin compilation errors in RCTTabViewManager
- **tab-view**: Wrap BottomNavigationView context with MaterialComponents theme
- **tab-view**: Remove bridging header for framework target compatibility
- **tab-view**: Use ObjC runtime bridge to call OneKeyLog, avoid Nitro C++ header import
- **tab-view**: Resolve iOS Fabric ComponentView build errors
- **tab-view**: Restore `#ifdef RCT_NEW_ARCH_ENABLED` in .h files for Swift module compatibility

### Refactors
- **tab-view**: Migrate from Nitro HybridView to Fabric ViewManager, then to Fabric Native Component

### Chores
- Bump all native modules and views to 1.1.32

## [1.1.30] - 2026-03-06

### Features
- **bundle-update / app-update**: Add synchronous `isSkipGpgVerificationAllowed` API to expose whether build-time skip-GPG capability is enabled

## [1.1.29] - 2026-03-06

### Bug Fixes
- **auto-size-input**: Re-layout immediately in `contentAutoWidth` mode as text changes, and shrink font when max width is reached so content stays visible

## [1.1.28] - 2026-03-05

### Features
- **auto-size-input**: Add new `@onekeyfe/react-native-auto-size-input` native view module with font auto-scaling, prefix/suffix support, multiline support, and example page
- **auto-size-input**: Add `showBorder`, `inputBackgroundColor`, and `contentAutoWidth` props; make composed prefix/input/suffix area tappable to focus input
- **bundle-update / app-update**: Add `testVerification` and `testSkipVerification` testing APIs
- **native-logger**: Add level-based token bucket rate limiting for log writes

### Bug Fixes
- **auto-size-input**: Fix iOS build issue (delegate/`NSObjectProtocol` conformance), fix layout-loop behavior, and align callback typing on test page
- **native-logger**: Harden log rolling behavior and move rate limiting to low-level logger implementation

## [1.1.27] - 2026-03-04

### Features
- **bundle-update / app-update**: Gate skip-GPG logic with build-time env var `ONEKEY_ALLOW_SKIP_GPG_VERIFICATION` — code paths are compiled out (iOS) or short-circuited by immutable constant (Android) when flag is unset

### Bug Fixes
- **bundle-update / app-update**: Treat empty string env var as disabled for `ALLOW_SKIP_GPG_VERIFICATION`

### Chores
- Bump all native modules and views to 1.1.27

## [1.1.26] - 2026-03-04

### Features
- **bundle-update**: Add synchronous `getJsBundlePath`, rename async version to `getJsBundlePathAsync`

## [1.1.24] - 2026-03-04

### Bug Fixes
- Address Copilot review — stack traces, import Security, FileProvider authority

### Chores
- Bump native modules and views version to 1.1.24

## [1.1.23] - 2026-03-04

### Bug Fixes
- **bundle-update / app-update**: Address PR review security and robustness issues
- **bundle-update / app-update**: Use `onekey-app-dev-setting` MMKV instance and require dual check for GPG skip

### Chores
- Bump native modules and views version to 1.1.23

## [1.1.22] - 2026-03-03

### Features
- **bundle-update**: Migrate signature storage from SharedPreferences/UserDefaults to file-based storage
- **bundle-update**: Add `filePath` field to `AscFileInfo` in `listAscFiles` API
- **app-update**: Add Download ASC and Verify ASC steps to AppUpdate pipeline
- **app-update**: Improve download with sandbox path fix, error logging, and APK cache verification
- **bundle-update / app-update**: Add comprehensive logging to native modules
- **bundle-update**: Add download progress logging on both iOS and Android

### Bug Fixes
- **bundle-update**: Expose `BundleUpdateStore` to ObjC runtime
- **bundle-update**: Fix iOS build errors (MMKV and SSZipArchive API)
- **bundle-update / app-update**: Fix Android build errors across native modules
- **app-update**: Fix download progress events and prevent duplicate downloads in native layer
- **app-update**: Fix remaining `params.filePath` reference in `installAPK`
- **app-update**: Skip `packageName` and certificate checks in debug builds, still log results
- **app-update**: Add FileProvider config for APK install intent
- **app-update**: Add BouncyCastle provider and fix conflict with Android built-in BC
- **bundle-update**: Add diagnostic logging for bundle download file creation
- **bundle-update**: Add detailed diagnostic logging to `getJsBundlePath` on both platforms
- **lite-card**: Add BuildConfig import to LogUtil
- **native-logger**: Pass archived path to `super.didArchiveLogFile` to prevent crash
- Use host app `FLAG_DEBUGGABLE` instead of library `BuildConfig.DEBUG`

### Refactors
- **app-update**: Remove `filePath` from AppUpdate API, derive from `downloadUrl`
- **app-update**: Unify AppUpdate event types with `update/` prefix

## [1.1.21] - 2026-03-03

### Features
- **device-utils**: Merge `react-native-webview-checker` into `react-native-device-utils`
- **device-utils**: Add WebView & Play Services availability checks
- **device-utils**: Add `saveDeviceToken` and make `exitApp` a no-op on iOS
- **splash-screen**: Implement Android legacy splash screen with `SplashScreenBridge`
- **get-random-values**: Add logging for invalid `byteLength`
- **bundle-update / app-update**: Replace debug-mode GPG skip with MMKV DevSettings toggle

## [1.1.20] - 2026-02-28

### Features
- **native-logger**: Add `react-native-native-logger` module with file-based logging, log rotation, and CocoaLumberjack/Timber backends
- **native-logger**: Add startup log in native layer for iOS and Android
- **native-logger**: Add timestamp format for log writes and copy button for log dir
- Integrate `OneKeyLog` into all native modules

### Bug Fixes
- **native-logger**: Auto-init OneKeyLog via ContentProvider before `Application.onCreate`
- **native-logger**: Use correct autolinked Gradle project name
- **native-logger**: Security hardening — remove key names from keychain logs
- **lite-card**: Migrate logging to NativeLogger, remove sensitive data from logs
- **background-thread**: Migrate logging to NativeLogger, replace `@import` with dynamic dispatch
- **bundle-update**: Add missing `deepLink` parameter to `LaunchOptions` initializer

### Chores
- Bump version to 1.1.20

## [1.1.19] - 2026-02-24

### Bug Fixes
- **device-utils**: Improve `setUserInterfaceStyle` reliability and code quality
- **device-utils (Android)**: Prevent `ConcurrentModificationException` in foldable device listener
- **device-utils (Android)**: Add null check in `setTopActivity` to prevent `NullPointerException`
- **device-utils (Android)**: Add synchronized blocks to prevent race conditions

## [1.1.18] - 2026-02-03

### Features
- **device-utils**: Add `setUserInterfaceStyle` API with local persistence for dark/light mode control
- **device-utils**: Add comprehensive foldable device detection with manufacturer-specific caching
- **device-utils**: Expand foldable device list from Google Play supported devices
- **cloud-kit**: Correct `fetchRecord` return type on both iOS and Android

### Chores
- Upgrade `react-native-nitro-modules` to 0.33.2

## [1.1.17 and earlier] - 2025-12-11 to 2026-01-29

### Features
- **lite-card**: Initial OneKey Lite NFC card module with full card management (read/write mnemonic, change PIN, reset, get card info)
- **lite-card**: Refactor to Turbo Module architecture
- **check-biometric-auth-changed**: Support biometric authentication change detection on iOS and Android
- **cloud-kit**: Add CloudKit/iCloud module for record CRUD operations
- **keychain-module**: Add secure keychain storage module (iOS Keychain / Android Keystore)
- **background-thread**: Add background JavaScript thread execution module
- **get-random-values**: Add cryptographic random values module (Nitro Module)
- **device-utils**: Add device utilities module — system info, clipboard, haptics, locale, and more
- **skeleton**: Add native skeleton loading view component with shimmer animation

### Bug Fixes
- **skeleton**: Fix `EXC_BAD_ACCESS` crash on iOS
- **skeleton**: Improve memory safety and cleanup in Skeleton component
- **skeleton**: Fix memory leak
- **cloud-kit**: Fix native type errors in CloudKitModule

### Chores
- Upgrade project structure to monorepo with workspaces
- Create CI publish workflow
