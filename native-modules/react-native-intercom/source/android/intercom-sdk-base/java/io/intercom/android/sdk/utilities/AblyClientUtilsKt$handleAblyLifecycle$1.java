package io.intercom.android.sdk.utilities;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "AblyClientUtils.kt", l = 11, i = {}, s = {}, n = {}, m = "handleAblyLifecycle", c = "io.intercom.android.sdk.utilities.AblyClientUtilsKt")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AblyClientUtilsKt$handleAblyLifecycle$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;

   AblyClientUtilsKt$handleAblyLifecycle$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return AblyClientUtilsKt.handleAblyLifecycle(null, null, this);
   }
}
