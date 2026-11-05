package io.intercom.android.sdk.m5.inbox;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@DebugMetadata(
   f = "InboxViewModel.kt",
   l = 50,
   i = {},
   s = {},
   n = {},
   m = "emit",
   c = "io.intercom.android.sdk.m5.inbox.InboxViewModel$4$invokeSuspend$$inlined$filterIsInstance$1$2"
)
public final class InboxViewModel$4$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   final InboxViewModel$4$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2 this$0;

   public InboxViewModel$4$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2$1(
      InboxViewModel$4$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2 var1, Continuation var2
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
