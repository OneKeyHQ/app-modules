package io.intercom.android.sdk.tickets.create.model;

import android.content.Context;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.utils.MediaCompressionKt;
import io.intercom.android.sdk.models.Upload$Builder;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileTooLarge;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UploadFailed;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "CreateTicketViewModel.kt",
   l = {358, 378},
   i = {0, 1, 1},
   s = {"L$2", "L$2", "L$3"},
   n = {"mediaItem", "mediaItem", "compressedMedia"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$compressAndUploadFileAttachments$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$compressAndUploadFileAttachments$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,499:1\n1869#2,2:500\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$compressAndUploadFileAttachments$1$1\n*L\n356#1:500,2\n*E\n"
)
final class CreateTicketViewModel$compressAndUploadFileAttachments$1$1 extends SuspendLambda implements Function2 {
   Object L$0;
   Object L$1;
   Object L$2;
   Object L$3;
   int label;
   // $VF: synthetic field
   final List $itemsToUpload;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;

   CreateTicketViewModel$compressAndUploadFileAttachments$1$1(List var1, CreateTicketViewModel var2, Continuation var3) {
      super(2, var3);
      this.$itemsToUpload = var1;
      this.this$0 = var2;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      CreateTicketViewModel var3;
      Iterator var5;
      Answer$MediaAnswer$MediaItem var17;
      Object var26;
      switch (this.label) {
         case 0: {
            ResultKt.throwOnFailure(var1);
            Iterable var2 = this.$itemsToUpload;
            var3 = this.this$0;
            int var13 = 0;
            var5 = var2.iterator();
            if (!var5.hasNext()) {
               return Unit.INSTANCE;
            }

            Object var14 = var5.next();
            var17 = (Answer$MediaAnswer$MediaItem)var14;
            boolean var20 = false;
            MediaData$Media recoveredInitialInput = var17.getData();
            Context var32 = CreateTicketViewModel.access$getApplicationContext$p(var3);
            this.L$0 = var3;
            this.L$1 = var5;
            this.L$2 = var17;
            this.L$3 = null;
            this.label = 1;
            var26 = MediaCompressionKt.getCompressedMediaData$default(recoveredInitialInput, var32, 0, this, 4, null);
            if (var26 == var11) {
               return var11;
            }
            break;
         }
         case 1: {
            int var12 = 0;
            int var19 = 0;
            var17 = (Answer$MediaAnswer$MediaItem)this.L$2;
            var5 = (Iterator)this.L$1;
            var3 = (CreateTicketViewModel)this.L$0;
            ResultKt.throwOnFailure(var1);
            var26 = var1;
            break;
         }
         case 2: {
            int var4 = 0;
            int var8 = 0;
            MediaData$Media var9 = (MediaData$Media)this.L$3;
            var17 = (Answer$MediaAnswer$MediaItem)this.L$2;
            var5 = (Iterator)this.L$1;
            var3 = (CreateTicketViewModel)this.L$0;
            ResultKt.throwOnFailure(var1);
            NetworkResponse var10 = (NetworkResponse)var1;
            if (!(var10 instanceof NetworkResponse$ClientError)) {
               if (!(var10 instanceof NetworkResponse$NetworkError)) {
                  if (var10 instanceof NetworkResponse$ServerError) {
                     var17.setUploadStatus(
                        new Answer$MediaAnswer$FileUploadStatus$Error(
                           new Answer$MediaAnswer$FileUploadError$UploadFailed(
                              CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null))
                           )
                        )
                     );
                     CreateTicketViewModel.access$updateCtaState(var3);
                     MediaCompressionKt.deleteCompressedMedia(var9.getUri());
                  } else {
                     if (!(var10 instanceof NetworkResponse$Success)) {
                        throw new NoWhenBranchMatchedException();
                     }

                     var17.setUploadStatus(
                        new Answer$MediaAnswer$FileUploadStatus$Success(((Upload$Builder)((NetworkResponse$Success)var10).getBody()).build().getId())
                     );
                     CreateTicketViewModel.access$updateCtaState(var3);
                     MediaCompressionKt.deleteCompressedMedia(var9.getUri());
                  }
               } else {
                  var17.setUploadStatus(
                     new Answer$MediaAnswer$FileUploadStatus$Error(
                        new Answer$MediaAnswer$FileUploadError$UploadFailed(CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null)))
                     )
                  );
                  CreateTicketViewModel.access$updateCtaState(var3);
                  MediaCompressionKt.deleteCompressedMedia(var9.getUri());
               }
            } else {
               var17.setUploadStatus(
                  new Answer$MediaAnswer$FileUploadStatus$Error(
                     new Answer$MediaAnswer$FileUploadError$UploadFailed(CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null)))
                  )
               );
               CreateTicketViewModel.access$updateCtaState(var3);
               MediaCompressionKt.deleteCompressedMedia(var9.getUri());
            }

            if (!var5.hasNext()) {
               return Unit.INSTANCE;
            }

            Object var6 = var5.next();
            var17 = (Answer$MediaAnswer$MediaItem)var6;
            boolean var18 = false;
            MediaData$Media recoveredResumedInput = var17.getData();
            Context var10001 = CreateTicketViewModel.access$getApplicationContext$p(var3);
            this.L$0 = var3;
            this.L$1 = var5;
            this.L$2 = var17;
            this.L$3 = null;
            this.label = 1;
            var26 = MediaCompressionKt.getCompressedMediaData$default(recoveredResumedInput, var10001, 0, this, 4, null);
            if (var26 == var11) {
               return var11;
            }
            break;
         }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (true) {
         MediaData$Media var23 = (MediaData$Media)var26;
         if (CreateTicketViewModel.access$isFileSizeExceeded(var3, var23)) {
            StringRes[] var25 = new StringRes[]{
               new StringRes(string.intercom_upload_failed, null, 2, null),
               new StringRes(
                  string.intercom_upload_max_files_size,
                  CollectionsKt.listOf(TuplesKt.to("limit", CreateTicketViewModel.access$getConfig$p(var3).getAttachmentSettings().getUploadSizeLimitMB()))
               )
            };
            var17.setUploadStatus(
               new Answer$MediaAnswer$FileUploadStatus$Error(new Answer$MediaAnswer$FileUploadError$FileTooLarge(CollectionsKt.listOf(var25)))
            );
            if (!var5.hasNext()) {
               return Unit.INSTANCE;
            }

            Object var16 = var5.next();
            var17 = (Answer$MediaAnswer$MediaItem)var16;
            boolean var22 = false;
            MediaData$Media recoveredOversizedNextInput = var17.getData();
            Context var34 = CreateTicketViewModel.access$getApplicationContext$p(var3);
            this.L$0 = var3;
            this.L$1 = var5;
            this.L$2 = var17;
            this.L$3 = null;
            this.label = 1;
            var26 = MediaCompressionKt.getCompressedMediaData$default(recoveredOversizedNextInput, var34, 0, this, 4, null);
            if (var26 == var11) {
               return var11;
            }
         } else {
            TicketRepository var28 = CreateTicketViewModel.access$getTicketRepository$p(var3);
            this.L$0 = var3;
            this.L$1 = var5;
            this.L$2 = var17;
            this.L$3 = var23;
            this.label = 2;
            Object var29 = var28.uploadFile(var23, this);
            if (var29 == var11) {
               return var11;
            }

            NetworkResponse var24 = (NetworkResponse)var29;
            if (!(var24 instanceof NetworkResponse$ClientError)) {
               if (!(var24 instanceof NetworkResponse$NetworkError)) {
                  if (var24 instanceof NetworkResponse$ServerError) {
                     var17.setUploadStatus(
                        new Answer$MediaAnswer$FileUploadStatus$Error(
                           new Answer$MediaAnswer$FileUploadError$UploadFailed(
                              CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null))
                           )
                        )
                     );
                     CreateTicketViewModel.access$updateCtaState(var3);
                     MediaCompressionKt.deleteCompressedMedia(var23.getUri());
                  } else {
                     if (!(var24 instanceof NetworkResponse$Success)) {
                        throw new NoWhenBranchMatchedException();
                     }

                     var17.setUploadStatus(
                        new Answer$MediaAnswer$FileUploadStatus$Success(((Upload$Builder)((NetworkResponse$Success)var24).getBody()).build().getId())
                     );
                     CreateTicketViewModel.access$updateCtaState(var3);
                     MediaCompressionKt.deleteCompressedMedia(var23.getUri());
                  }
               } else {
                  var17.setUploadStatus(
                     new Answer$MediaAnswer$FileUploadStatus$Error(
                        new Answer$MediaAnswer$FileUploadError$UploadFailed(CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null)))
                     )
                  );
                  CreateTicketViewModel.access$updateCtaState(var3);
                  MediaCompressionKt.deleteCompressedMedia(var23.getUri());
               }
            } else {
               var17.setUploadStatus(
                  new Answer$MediaAnswer$FileUploadStatus$Error(
                     new Answer$MediaAnswer$FileUploadError$UploadFailed(CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null)))
                  )
               );
               CreateTicketViewModel.access$updateCtaState(var3);
               MediaCompressionKt.deleteCompressedMedia(var23.getUri());
            }

            if (!var5.hasNext()) {
               return Unit.INSTANCE;
            }

            Object var15 = var5.next();
            var17 = (Answer$MediaAnswer$MediaItem)var15;
            int var21 = 0;
            MediaData$Media var30 = var17.getData();
            Context var33 = CreateTicketViewModel.access$getApplicationContext$p(var3);
            this.L$0 = var3;
            this.L$1 = var5;
            this.L$2 = var17;
            this.L$3 = null;
            this.label = 1;
            var26 = MediaCompressionKt.getCompressedMediaData$default(var30, var33, 0, this, 4, null);
            if (var26 == var11) {
               return var11;
            }
         }
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketViewModel$compressAndUploadFileAttachments$1$1(this.$itemsToUpload, this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$compressAndUploadFileAttachments$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
