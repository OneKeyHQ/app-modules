# @onekeyfe/react-native-image

Nitro HybridView backed by SDWebImage on iOS and Glide on Android. It provides
native loading placeholders, opt-in shared-renderer skeleton, fallback states,
cache controls,
and conservative OneKey TOS URL resizing without depending on Expo Image.

```tsx
<OneKeyImage
  source={{ uri: 'https://example.com/avatar.png' }}
  variant={OneKeyImageVariant.AVATAR}
  style={{ width: 48, height: 48 }}
/>
```

React placeholders and fallbacks are optional overlays. The default path draws
all states in the native image view and never nests SkeletonView. The public
state semantics match the native image libraries:

- `placeholder`: shown while a non-null source is loading.
- `fallback`: shown when `source` is null, cannot be resolved, or fails to load.

`onError` is only emitted for an actual request failure; a null source displays
the same fallback UI without emitting an error event.

The React event payloads follow the Expo Image envelope used by OneKey:

```ts
onLoad={({ source: { url, width, height }, cacheType }) => {}}
onError={({ error }) => {}}
```

`cacheType` is `OneKeyImageCacheType.NONE`, `.MEMORY`, or `.DISK`.
`placeholder` remains visible through decode and is removed by `onDisplay`,
when the decoded image has actually reached the screen. Overlay containers also
clip their contents so image, placeholder, and fallback share the same rounded
corners.

The default loading strategy is a lightweight static native placeholder. Native
skeleton loading is opt-in and replaces the static loading appearance instead
of appearing as a second stage:

```tsx
<OneKeyImage
  source={{ uri }}
  loadingStrategy={OneKeyImageLoadingStrategy.SKELETON}
  fallback={<UnavailableImagePlaceholder />}
/>
```

Passing a React `placeholder` disables the native loading strategy for that
instance so only one loading surface is visible.

Animated images intentionally default to `autoplay={false}` on Android to keep
large lists conservative, and `autoplay={true}` on other native platforms. Pass
`autoplay` explicitly whenever a screen needs the same behavior on both
platforms.

`recyclingKey` is available for recycled list cells. Changing it clears stale
content before the next source is displayed.

Cache clearing methods resolve only after their requested native cache work has
completed, including `OneKeyImageCache.clearMemory()`.

Pass the rendered logical dimensions and device pixel ratio when preloading so
TOS URL selection and decode cache identity match the later render. `preload`
resolves to `true` only when all requested sources complete successfully:

```ts
const loaded = await OneKeyImageCache.preload([
  {
    uri,
    resizeWidth: 48,
    resizeHeight: 64,
    pixelRatio: 3,
    overscan: 1.1,
  },
]);
```

For a CocoaPods static-library integration, declare SDWebImage as a modular
header in the application Podfile:

```ruby
pod 'SDWebImage', '5.21.7', :modular_headers => true
pod 'SDWebImageWebPCoder', '0.14.6', :modular_headers => true
pod 'SDWebImageSVGCoder', '1.7.0', :modular_headers => true
```
