// Nitro 0.37 emits leaf view managers. Container slots need explicit Fabric
// child ownership, reapplied deterministically after every nitrogen build.
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const android = join(root, 'nitrogen/generated/android/kotlin/com/margelo/nitro/nativelist/views/HybridNativeListManager.kt');
const ios = join(root, 'nitrogen/generated/ios/c++/views/HybridNativeListComponent.mm');
const marker = '// NativeList container slot mounting';
let kotlin = readFileSync(android, 'utf8');
if (!kotlin.includes(marker)) {
  if (!kotlin.includes('SimpleViewManager<View>')) throw new Error('Unexpected Nitro Android manager shape');
  kotlin = kotlin.replace('import android.view.View\n', 'import android.view.View\nimport android.view.ViewGroup\n');
  kotlin = kotlin.replace('import com.facebook.react.uimanager.SimpleViewManager', 'import com.facebook.react.uimanager.ViewGroupManager');
  kotlin = kotlin.replace('SimpleViewManager<View>()', 'ViewGroupManager<ViewGroup>()');
  kotlin = kotlin.replaceAll('view: View', 'view: ViewGroup');
  kotlin = kotlin.replace('): View {', '): ViewGroup {').replace('): View? {', '): ViewGroup? {');
  kotlin = kotlin.replace('val view = hybridView.view', 'val view = hybridView.view as ViewGroup');
  kotlin = kotlin.replace('return hybridView.view', 'return hybridView.view as ViewGroup');
  kotlin = kotlin.replace('  override fun getName(): String {', `  ${marker}
  override fun needsCustomLayoutForChildren(): Boolean = true
  override fun addView(parent: ViewGroup, child: View, index: Int) {
    (parent as NativeListView).mountContainerSlot(child, index)
  }
  override fun getChildCount(parent: ViewGroup): Int = (parent as NativeListView).containerSlotCount()
  override fun getChildAt(parent: ViewGroup, index: Int): View = (parent as NativeListView).containerSlotAt(index)
  override fun removeViewAt(parent: ViewGroup, index: Int) {
    (parent as NativeListView).unmountContainerSlot(index)
  }
  override fun removeAllViews(parent: ViewGroup) {
    while (getChildCount(parent) > 0) removeViewAt(parent, getChildCount(parent) - 1)
  }

  override fun getName(): String {`);
  writeFileSync(android, kotlin);
}
let objc = readFileSync(ios, 'utf8');
if (!objc.includes(marker)) {
  if (!objc.includes('- (void) updateView {')) throw new Error('Unexpected Nitro iOS component shape');
  objc = objc.replace('@implementation HybridNativeListComponent {', `@interface UIView (NativeListContainerSlots)
- (void)mountContainerSlot:(UIView *)view atIndex:(NSInteger)index;
- (void)unmountContainerSlot:(UIView *)view;
@end

@implementation HybridNativeListComponent {`);
  objc = objc.replace('- (void) updateView {', `${marker}
- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index {
  [self.contentView mountContainerSlot:child atIndex:index];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index {
  [self.contentView unmountContainerSlot:child];
}

- (void) updateView {`);
  writeFileSync(ios, objc);
}
