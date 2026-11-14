package io.intercom.android.sdk.m5.conversation.ui;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@DebugMetadata(
   f = "ConversationScreen.kt",
   l = 50,
   i = {},
   s = {},
   n = {},
   m = "emit",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$$inlined$map$1$2"
)
public final class ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;
   Object L$0;
   // $VF: synthetic field
   final ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2 this$0;

   public ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2$1(
      ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1$2 var1, Continuation var2
   ) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.emit(null, this);
   }
}
