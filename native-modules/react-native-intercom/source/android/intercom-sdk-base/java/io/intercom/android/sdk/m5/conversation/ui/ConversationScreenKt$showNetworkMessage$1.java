package io.intercom.android.sdk.m5.conversation.ui;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = {427, 435},
   i = 1,
   s = "L$0",
   n = "onDismiss",
   m = "showNetworkMessage",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$showNetworkMessage$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   int label;

   ConversationScreenKt$showNetworkMessage$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return ConversationScreenKt.access$showNetworkMessage(null, null, null, null, this);
   }
}
