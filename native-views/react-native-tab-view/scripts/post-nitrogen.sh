#!/bin/bash
# Post-nitrogen script: replaces generated files with custom ones
# that add child view management support.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# iOS: Replace TabView component view with custom one (adds mountChildComponentView)
IOS_GENERATED_DIR="$PROJECT_DIR/nitrogen/generated/ios/c++/views"
IOS_CUSTOM_DIR="$PROJECT_DIR/ios/custom"

if [ -f "$IOS_GENERATED_DIR/HybridTabViewComponent.mm" ]; then
  echo "[post-nitrogen] Replacing iOS HybridTabViewComponent.mm with custom version..."
  cp "$IOS_CUSTOM_DIR/HybridTabViewComponent.mm" "$IOS_GENERATED_DIR/HybridTabViewComponent.mm"
fi

# Android: Replace TabView Manager with custom ViewGroupManager
ANDROID_GENERATED_DIR="$PROJECT_DIR/nitrogen/generated/android/kotlin/com/margelo/nitro/tabview/views"
ANDROID_CUSTOM_DIR="$PROJECT_DIR/android/src/main/java/com/margelo/nitro/tabview/views"

if [ -f "$ANDROID_GENERATED_DIR/HybridTabViewManager.kt" ]; then
  echo "[post-nitrogen] Replacing Android HybridTabViewManager.kt with custom version..."
  cp "$ANDROID_CUSTOM_DIR/HybridTabViewManager.kt" "$ANDROID_GENERATED_DIR/HybridTabViewManager.kt"
fi

echo "[post-nitrogen] Done."
