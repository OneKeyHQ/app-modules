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
   l = 73,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadBitmaps$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPushBitmapUtilsKt$loadBitmaps$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final AppConfig $appConfig;
   // $VF: synthetic field
   final String $contentImageUrl;
   // $VF: synthetic field
   final String $avatarImageUrl;
   // $VF: synthetic field
   final String $authorName;
   // $VF: synthetic field
   final boolean $hasContentImage;
   // $VF: synthetic field
   final boolean $hasAvatarUrl;
   // $VF: synthetic field
   final boolean $hasAuthorName;
   // $VF: synthetic field
   final Function2 $onUpdate;

   IntercomPushBitmapUtilsKt$loadBitmaps$2(
      Context var1, AppConfig var2, String var3, String var4, String var5, boolean var6, boolean var7, boolean var8, Function2 var9, Continuation var10
   ) {
      super(2, var10);
      this.$context = var1;
      this.$appConfig = var2;
      this.$contentImageUrl = var3;
      this.$avatarImageUrl = var4;
      this.$authorName = var5;
      this.$hasContentImage = var6;
      this.$hasAvatarUrl = var7;
      this.$hasAuthorName = var8;
      this.$onUpdate = var9;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Context var10000 = this.$context;
            AppConfig var10001 = this.$appConfig;
            String var10002 = this.$contentImageUrl;
            String var10003 = this.$avatarImageUrl;
            String var10004 = this.$authorName;
            boolean var10005 = this.$hasContentImage;
            boolean var10006 = this.$hasAvatarUrl;
            boolean var10007 = this.$hasAuthorName;
            Function2 var10008 = this.$onUpdate;
            Continuation var10009 = this;
            this.label = 1;
            if (IntercomPushBitmapUtilsKt.access$loadAndApplyBitmaps(
                  var10000, var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009
               )
               == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPushBitmapUtilsKt$loadBitmaps$2(
         this.$context,
         this.$appConfig,
         this.$contentImageUrl,
         this.$avatarImageUrl,
         this.$authorName,
         this.$hasContentImage,
         this.$hasAvatarUrl,
         this.$hasAuthorName,
         this.$onUpdate,
         var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadBitmaps$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
