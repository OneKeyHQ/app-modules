package io.intercom.android.sdk.m5.push;

import android.content.Context;
import android.graphics.drawable.Drawable;
import coil.request.ImageRequest;
import io.intercom.android.sdk.utilities.BitmapUtilsKt;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.views.AvatarDefaultDrawable;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "IntercomPushBitmapUtils.kt",
   l = 139,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadAvatarBitmap$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Landroid/graphics/Bitmap;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPushBitmapUtilsKt$loadAvatarBitmap$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final ImageRequest $request;
   // $VF: synthetic field
   final AvatarDefaultDrawable $defaultDrawable;
   // $VF: synthetic field
   final int $iconSize;

   IntercomPushBitmapUtilsKt$loadAvatarBitmap$2(Context var1, ImageRequest var2, AvatarDefaultDrawable var3, int var4, Continuation var5) {
      super(2, var5);
      this.$context = var1;
      this.$request = var2;
      this.$defaultDrawable = var3;
      this.$iconSize = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Context var4 = this.$context;
            ImageRequest var10001 = this.$request;
            Continuation var10002 = this;
            this.label = 1;
            var10000 = IntercomCoilKt.loadIntercomImageSuspend(var4, var10001, var10002);
            if (var10000 == var3) {
               return var3;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Drawable recoveredAvatarDrawable = (Drawable)var10000;
      if (recoveredAvatarDrawable == null) {
         recoveredAvatarDrawable = this.$defaultDrawable;
      }

      Drawable var2 = recoveredAvatarDrawable;
      Intrinsics.checkNotNull(var2);
      return BitmapUtilsKt.drawableToBitmap(var2, this.$iconSize, this.$iconSize);
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPushBitmapUtilsKt$loadAvatarBitmap$2(this.$context, this.$request, this.$defaultDrawable, this.$iconSize, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadAvatarBitmap$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
