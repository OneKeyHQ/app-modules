package io.intercom.android.sdk.m5.push;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "IntercomPushBitmapUtils.kt",
   l = 138,
   i = {0, 0},
   s = {"L$0", "I$0"},
   n = {"defaultDrawable", "iconSize"},
   m = "loadAvatarBitmap",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomPushBitmapUtilsKt$loadAvatarBitmap$1 extends ContinuationImpl {
   int I$0;
   Object L$0;
   // $VF: synthetic field
   Object result;
   int label;

   IntercomPushBitmapUtilsKt$loadAvatarBitmap$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return IntercomPushBitmapUtilsKt.access$loadAvatarBitmap(null, null, null, this);
   }
}
