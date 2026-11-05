package io.intercom.android.sdk.m5.preview.ui;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "ZoomableImage.kt",
   l = 269,
   i = {0, 0, 0, 0},
   s = {"L$0", "J$0", "F$0", "F$1"},
   n = {"$this$awaitTapCompletion_u2djO51t88", "pointerId", "touchSlop", "travel"},
   m = "awaitTapCompletion-jO51t88",
   c = "io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ZoomableImageKt$awaitTapCompletion$1 extends ContinuationImpl {
   Object L$0;
   long J$0;
   float F$0;
   float F$1;
   // $VF: synthetic field
   Object result;
   int label;

   ZoomableImageKt$awaitTapCompletion$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return ZoomableImageKt.recovered$407cb96d62b28197(null, 0L, 0.0F, this);
   }
}
