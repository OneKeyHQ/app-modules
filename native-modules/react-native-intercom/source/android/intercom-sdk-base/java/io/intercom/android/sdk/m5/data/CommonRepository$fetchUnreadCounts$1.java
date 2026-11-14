package io.intercom.android.sdk.m5.data;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "CommonRepository.kt", l = 39, i = {}, s = {}, n = {}, m = "fetchUnreadCounts", c = "io.intercom.android.sdk.m5.data.CommonRepository")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class CommonRepository$fetchUnreadCounts$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final CommonRepository this$0;
   int label;

   CommonRepository$fetchUnreadCounts$1(CommonRepository var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.fetchUnreadCounts(this);
   }
}
