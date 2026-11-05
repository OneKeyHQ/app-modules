package io.intercom.android.sdk.m5.push;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import coil.request.ImageRequest;
import io.intercom.android.sdk.utilities.BitmapUtilsKt;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "IntercomPushBitmapUtils.kt",
   l = 124,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadContentBitmap$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Landroid/graphics/Bitmap;", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nIntercomPushBitmapUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPushBitmapUtils.kt\nio/intercom/android/sdk/m5/push/IntercomPushBitmapUtilsKt$loadContentBitmap$2\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,177:1\n1#2:178\n*E\n"
)
final class IntercomPushBitmapUtilsKt$loadContentBitmap$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final ImageRequest $request;
   // $VF: synthetic field
   final int $width;
   // $VF: synthetic field
   final int $height;

   IntercomPushBitmapUtilsKt$loadContentBitmap$2(Context var1, ImageRequest var2, int var3, int var4, Continuation var5) {
      super(2, var5);
      this.$context = var1;
      this.$request = var2;
      this.$width = var3;
      this.$height = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Context var8 = this.$context;
            ImageRequest var10001 = this.$request;
            Continuation var10002 = this;
            this.label = 1;
            var10000 = IntercomCoilKt.loadIntercomImageSuspend(var8, var10001, var10002);
            if (var10000 == var7) {
               return var7;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Drawable recoveredContentDrawable = (Drawable)var10000;
      Bitmap var10;
      if (recoveredContentDrawable != null) {
         Drawable var2 = recoveredContentDrawable;
         int var3 = this.$width;
         int var4 = this.$height;
         Drawable var5 = var2;
         int var6 = 0;
         var10 = BitmapUtilsKt.drawableToBitmap(var5, var3, var4);
      } else {
         var10 = null;
      }

      return var10;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPushBitmapUtilsKt$loadContentBitmap$2(this.$context, this.$request, this.$width, this.$height, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadContentBitmap$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
