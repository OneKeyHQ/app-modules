package io.intercom.android.sdk.m5.push;

import android.content.Context;
import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "IntercomPushBitmapUtils.kt",
   l = 96,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Landroid/graphics/Bitmap;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final String $avatarImageUrl;
   // $VF: synthetic field
   final AppConfig $appConfig;

   IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1(Context var1, String var2, AppConfig var3, Continuation var4) {
      super(2, var4);
      this.$context = var1;
      this.$avatarImageUrl = var2;
      this.$appConfig = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Context recoveredAvatarContext = this.$context;
            String var10001 = this.$avatarImageUrl;
            AppConfig var10002 = this.$appConfig;
            Continuation var10003 = this;
            this.label = 1;
            var10000 = IntercomPushBitmapUtilsKt.access$loadAvatarBitmap(recoveredAvatarContext, var10001, var10002, var10003);
            if (var10000 == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return var10000;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1(this.$context, this.$avatarImageUrl, this.$appConfig, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
