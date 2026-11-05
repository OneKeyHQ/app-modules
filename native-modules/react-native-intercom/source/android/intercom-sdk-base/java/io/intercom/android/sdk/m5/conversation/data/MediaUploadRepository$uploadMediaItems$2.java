package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.AwaitKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "MediaUploadRepository.kt",
   l = 41,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$uploadMediaItems$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadResult;", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nMediaUploadRepository.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaUploadRepository.kt\nio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$uploadMediaItems$2\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,147:1\n1563#2:148\n1634#2,3:149\n*S KotlinDebug\n*F\n+ 1 MediaUploadRepository.kt\nio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$uploadMediaItems$2\n*L\n41#1:148\n41#1:149,3\n*E\n"
)
final class MediaUploadRepository$uploadMediaItems$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final List $mediaItems;
   // $VF: synthetic field
   final MediaUploadRepository this$0;
   // $VF: synthetic field
   final int $transparencyBackgroundColor;

   MediaUploadRepository$uploadMediaItems$2(List var1, MediaUploadRepository var2, int var3, Continuation var4) {
      super(2, var4);
      this.$mediaItems = var1;
      this.this$0 = var2;
      this.$transparencyBackgroundColor = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var15 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            Iterable var3 = this.$mediaItems;
            MediaUploadRepository var4 = this.this$0;
            int var5 = this.$transparencyBackgroundColor;
            int var6 = 0;
            Iterable var7 = var3;
            ArrayList var8 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
            int var9 = 0;

            for (Object var11 : var7) {
               MediaData$Media var12 = (MediaData$Media)var11;
               Collection var14 = var8;
               int var13 = 0;
               var14.add(BuildersKt.async$default(var2, null, null, new MediaUploadRepository$uploadMediaItems$2$1$1(var4, var12, var5, null), 3, null));
            }

            Collection recoveredUploadTasks = (Collection & List)var8;
            Continuation var10001 = this;
            this.label = 1;
            var10000 = AwaitKt.awaitAll(recoveredUploadTasks, var10001);
            if (var10000 == var15) {
               return var15;
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
      MediaUploadRepository$uploadMediaItems$2 var3 = new MediaUploadRepository$uploadMediaItems$2(
         this.$mediaItems, this.this$0, this.$transparencyBackgroundColor, var2
      );
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MediaUploadRepository$uploadMediaItems$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
