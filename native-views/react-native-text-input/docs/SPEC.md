# React Native Text Input behavioral contract

Status: Implemented in source; iOS and Android device interaction remains to be verified.

## Purpose, scope, and ownership

This package wraps React Native text inputs and emits paste events for text and
images. The native input owns the platform paste action; callers own handling the
emitted event and any referenced image data. The package does not inspect
application-specific clipboard data. Its native paste integration supports
iOS and Android; it does not define Web paste behavior.

## Public API and defaults

`TextInput` accepts React Native `TextInputProps` and an optional `onPaste`
callback. The callback receives `nativeEvent.items`; each reported item has a
MIME `type` and `data`. Text data is a string with type `text/plain`. Image
data is platform-specific: iOS supplies a temporary local file URL, while
Android supplies the clipboard item's URI when its content resolver returns a
MIME type. With no `onPaste` callback, iOS has no JavaScript paste
subscription and Android has no paste watcher. The cache does not add a
public prop or change the callback payload.

## Lifecycle, concurrency, and cache

- iOS starts with the image cache uninitialized, registers for pasteboard
  changes, app activation, and text-input begin-editing, and requests an
  initial refresh. One serial background queue coalesces availability reads.
  A notification during a read requires another read. The process-local cache
  is only a command-availability hint; the paste handler checks the actual
  clipboard content when used.
- Android attaches a watcher to each native input only while `onPaste` is
  enabled. It reads the clipboard when the user selects Paste or Paste as plain
  text, emits a non-coalesced direct event if it finds a supported first item,
  then invokes the underlying text input's plain-text paste action. It has no
  pasteboard availability cache or background refresh.

## Platform behavior

- iOS `RCTUITextField` and `RCTUITextView` expose Paste for an image-only
  clipboard. When an image is pasted, the native observer loads its data and
  emits `OneKeyTextInputPaste` with a MIME type and temporary file URL. Text
  paste events use `text/plain`. If image loading fails, the native paste
  fallback remains available.
- iOS command validation must not read `UIPasteboard` on the main thread.
  While the cache is uninitialized or a refresh is outstanding, Paste remains
  available. Once a refresh completes with no image, normal React Native
  Paste gating applies.
- Android (package default minimum SDK 24) relies on the system for Paste menu
  availability.
  For a clipboard whose description includes `text/plain`, it reports the
  first item's text. Otherwise, if the first item has a URI and the content
  resolver returns a MIME type, it reports that MIME type and the URI string;
  this can include images. It does not copy the URI content into a temporary
  file. Both Paste menu actions continue through the native plain-text paste
  path after the optional event. This iOS cache does not affect Android.

## Failure, fallback, and resource budget

- iOS may briefly allow Paste when the clipboard contains no image. The native
  paste action then falls back to the text input's normal behavior. If loading
  an image fails, the native paste fallback remains available. Command
  validation performs atomic reads only; pasteboard XPC work stays on the
  serial background queue. No image data is retained in the cache.
- Android reports no `onPaste` event when the watcher is absent or the first
  clipboard item has neither reportable text nor a URI with a resolved MIME
  type. The native paste action still runs. It reads at most the first item
  for the event on supported Android versions and does not own a copied image
  file. No explicit clipboard byte limit is enforced here.

## Conformance and acceptance

The iOS implementation is in `ios/OneKeyTextInputPasteObserver.mm`. Focused
native tests cover image-only Paste while a refresh is blocked after first
focus and foreground activation. Simulator interaction should confirm that
Paste appears and emits the image event in both cases.

The Android implementation is in `android/src/main/java/com/textinput/`
(`TextInputView.kt`, `TextInputViewManager.kt`, and
`TextInputPasteEvent.kt`). No focused Android paste test exists yet. Device
acceptance should check text and image-URI clipboard items with `onPaste`
enabled and disabled, missing URI/MIME fallback, and that the underlying
plain-text paste action still runs. Neither platform's interaction cases have
been runtime verified for this change.
