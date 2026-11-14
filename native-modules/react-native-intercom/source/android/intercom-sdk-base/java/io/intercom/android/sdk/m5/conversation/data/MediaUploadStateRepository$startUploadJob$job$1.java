package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error;
import kotlin.ResultKt;
import kotlin.NoWhenBranchMatchedException;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "MediaUploadStateRepository.kt",
   l = 125,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.data.MediaUploadStateRepository$startUploadJob$job$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class MediaUploadStateRepository$startUploadJob$job$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MediaUploadStateRepository this$0;
   // $VF: synthetic field
   final MediaUploadItem $uploadItem;
   // $VF: synthetic field
   final int $transparencyBackgroundColor;

   MediaUploadStateRepository$startUploadJob$job$1(MediaUploadStateRepository var1, MediaUploadItem var2, int var3, Continuation var4) {
      super(2, var4);
      this.this$0 = var1;
      this.$uploadItem = var2;
      this.$transparencyBackgroundColor = var3;
   }

   @Override
   public final Object invokeSuspend(Object result) {
      Object suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      int resumeLabel = this.label;
      if (resumeLabel == 0) {
         ResultKt.throwOnFailure(result);
      } else if (resumeLabel != 1) {
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
      try {
         Object uploaded;
         if (resumeLabel == 1) {
            ResultKt.throwOnFailure(result);
            uploaded = result;
         } else {
            MediaUploadRepository repository = MediaUploadStateRepository.access$getMediaUploadRepository$p(this.this$0);
            io.intercom.android.sdk.m5.upload.data.MediaData$Media media = this.$uploadItem.getMediaData();
            int backgroundColor = this.$transparencyBackgroundColor;
            this.label = 1;
            uploaded = repository.uploadSingleMediaItem(media, backgroundColor, this);
            // The job remains registered while its upload is suspended.
            if (uploaded == suspended) {
               return suspended;
            }
         }
         MediaUploadRepository$UploadResult response = (MediaUploadRepository$UploadResult) uploaded;
         MediaUploadItem$UploadState uploadState;
         if (response instanceof MediaUploadRepository$UploadResult$Success) {
            uploadState = new MediaUploadItem$UploadState$Success(((MediaUploadRepository$UploadResult$Success) response).getBlock());
         } else if (response instanceof MediaUploadRepository$UploadResult$Failure) {
            uploadState = new MediaUploadItem$UploadState$Error(((MediaUploadRepository$UploadResult$Failure) response).getError());
         } else {
            throw new NoWhenBranchMatchedException();
         }
         MediaUploadStateRepository.access$updateItemState(this.this$0, this.$uploadItem.getInstanceId(), uploadState);
      } catch (Throwable failure) {
         // Removal itself is outside the protected upload region and is never retried.
         MediaUploadStateRepository.access$getUploadJobs$p(this.this$0).remove(this.$uploadItem.getInstanceId());
         throw failure;
      }
      MediaUploadStateRepository.access$getUploadJobs$p(this.this$0).remove(this.$uploadItem.getInstanceId());
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new MediaUploadStateRepository$startUploadJob$job$1(this.this$0, this.$uploadItem, this.$transparencyBackgroundColor, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MediaUploadStateRepository$startUploadJob$job$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
