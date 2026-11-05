package io.intercom.android.sdk.utilities;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "IntercomCoil.kt", l = 15, i = {}, s = {}, n = {}, m = "loadIntercomImageSuspend", c = "io.intercom.android.sdk.utilities.IntercomCoilKt")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomCoilKt$loadIntercomImageSuspend$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;

   IntercomCoilKt$loadIntercomImageSuspend$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return IntercomCoilKt.loadIntercomImageSuspend(null, null, this);
   }
}
