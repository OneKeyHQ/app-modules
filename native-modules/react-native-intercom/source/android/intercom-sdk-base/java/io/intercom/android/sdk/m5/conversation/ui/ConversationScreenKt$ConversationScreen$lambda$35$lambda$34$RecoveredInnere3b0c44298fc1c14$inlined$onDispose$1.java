package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.runtime.DisposableEffectResult;
import io.intercom.android.sdk.m5.home.ui.helpers.InMemoryWebViewCacheKt;
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
   "SMAP\nEffects.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope$onDispose$1\n+ 2 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt\n*L\n1#1,629:1\n222#2,2:630\n*E\n"
)
public final class ConversationScreenKt$ConversationScreen$lambda$35$lambda$34$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1
   implements DisposableEffectResult {
   @Override
   public void dispose() {
      int var1 = 0;
      InMemoryWebViewCacheKt.clearWebViewCache();
   }
}
