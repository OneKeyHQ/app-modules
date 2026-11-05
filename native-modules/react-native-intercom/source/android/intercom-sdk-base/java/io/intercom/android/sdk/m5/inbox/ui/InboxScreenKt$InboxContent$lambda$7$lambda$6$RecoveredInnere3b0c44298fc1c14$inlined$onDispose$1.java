package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.DisposableEffectResult;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004¸\u0006\u0000",
   d2 = {"androidx/compose/runtime/DisposableEffectScope$onDispose$1", "Landroidx/compose/runtime/DisposableEffectResult;", "dispose", "", "runtime"}
)
@SourceDebugExtension(
   "SMAP\nEffects.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope$onDispose$1\n+ 2 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt\n*L\n1#1,629:1\n156#2,2:630\n*E\n"
)
public final class InboxScreenKt$InboxContent$lambda$7$lambda$6$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1 implements DisposableEffectResult {
   // $VF: synthetic field
   final LifecycleOwner $lifecycleOwner$inlined;
   // $VF: synthetic field
   final LifecycleEventObserver $observer$inlined;

   public InboxScreenKt$InboxContent$lambda$7$lambda$6$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1(LifecycleOwner var1, LifecycleEventObserver var2) {
      this.$lifecycleOwner$inlined = var1;
      this.$observer$inlined = var2;
   }

   @Override
   public void dispose() {
      int var1 = 0;
      this.$lifecycleOwner$inlined.getLifecycle().removeObserver(this.$observer$inlined);
   }
}
