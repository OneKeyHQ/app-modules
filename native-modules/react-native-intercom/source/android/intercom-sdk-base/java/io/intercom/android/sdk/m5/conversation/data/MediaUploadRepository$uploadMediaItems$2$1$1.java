package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
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
   f = "MediaUploadRepository.kt",
   l = 41,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$uploadMediaItems$2$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadResult;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class MediaUploadRepository$uploadMediaItems$2$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MediaUploadRepository this$0;
   // $VF: synthetic field
   final MediaData$Media $it;
   // $VF: synthetic field
   final int $transparencyBackgroundColor;

   MediaUploadRepository$uploadMediaItems$2$1$1(MediaUploadRepository var1, MediaData$Media var2, int var3, Continuation var4) {
      super(2, var4);
      this.this$0 = var1;
      this.$it = var2;
      this.$transparencyBackgroundColor = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            MediaUploadRepository recoveredMediaRepository = this.this$0;
            MediaData$Media var10001 = this.$it;
            int var10002 = this.$transparencyBackgroundColor;
            Continuation var10003 = this;
            this.label = 1;
            var10000 = recoveredMediaRepository.uploadSingleMediaItem(var10001, var10002, var10003);
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
      return new MediaUploadRepository$uploadMediaItems$2$1$1(this.this$0, this.$it, this.$transparencyBackgroundColor, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MediaUploadRepository$uploadMediaItems$2$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
