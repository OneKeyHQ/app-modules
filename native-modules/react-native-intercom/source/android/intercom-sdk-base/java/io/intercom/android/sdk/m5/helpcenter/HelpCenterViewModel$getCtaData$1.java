package io.intercom.android.sdk.m5.helpcenter;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "HelpCenterViewModel.kt",
   l = 401,
   i = 0,
   s = "L$0",
   n = "this",
   m = "getCtaData",
   c = "io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterViewModel$getCtaData$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final HelpCenterViewModel this$0;
   int label;

   HelpCenterViewModel$getCtaData$1(HelpCenterViewModel var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return HelpCenterViewModel.access$getCtaData(this.this$0, this);
   }
}
