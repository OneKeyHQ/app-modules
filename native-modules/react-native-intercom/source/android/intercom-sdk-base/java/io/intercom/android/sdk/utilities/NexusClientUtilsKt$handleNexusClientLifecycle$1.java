package io.intercom.android.sdk.utilities;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "NexusClientUtils.kt",
   l = 11,
   i = {},
   s = {},
   n = {},
   m = "handleNexusClientLifecycle",
   c = "io.intercom.android.sdk.utilities.NexusClientUtilsKt"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class NexusClientUtilsKt$handleNexusClientLifecycle$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;

   NexusClientUtilsKt$handleNexusClientLifecycle$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return NexusClientUtilsKt.handleNexusClientLifecycle(null, null, this);
   }
}
