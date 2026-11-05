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
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Landroid/graphics/Bitmap;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final String $authorName;
   // $VF: synthetic field
   final AppConfig $appConfig;

   IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2(Context var1, String var2, AppConfig var3, Continuation var4) {
      super(2, var4);
      this.$context = var1;
      this.$authorName = var2;
      this.$appConfig = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            return IntercomPushBitmapUtilsKt.access$generateInitialsAvatar(this.$context, this.$authorName, this.$appConfig);
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2(this.$context, this.$authorName, this.$appConfig, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
