package io.intercom.android.sdk.m5.inbox.data;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "InboxPagingSource.kt",
   l = {18, 32},
   i = {0, 0, 1, 1, 1},
   s = {"L$0", "L$1", "L$0", "L$1", "I$0"},
   n = {"this", "params", "this", "conversationPage", "isFirstLoad"},
   m = "load",
   c = "io.intercom.android.sdk.m5.inbox.data.InboxPagingSource"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxPagingSource$load$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   int I$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final InboxPagingSource this$0;
   int label;

   InboxPagingSource$load$1(InboxPagingSource var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.load(null, this);
   }
}
