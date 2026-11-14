package io.intercom.android.sdk.tickets.create.model;

import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UnsupportedFileType;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$None;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Queued;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "CreateTicketViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$onAnswerUpdated$1$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$onAnswerUpdated$1$2\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,499:1\n774#2:500\n865#2,2:501\n1869#2:503\n1878#2,3:504\n1870#2:507\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$onAnswerUpdated$1$2\n*L\n197#1:500\n197#1:501,2\n198#1:503\n204#1:504,3\n198#1:507\n*E\n"
)
final class CreateTicketViewModel$onAnswerUpdated$1$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final CreateTicketViewModel$CreateTicketFormUiState$Content $content;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;

   CreateTicketViewModel$onAnswerUpdated$1$2(CreateTicketViewModel$CreateTicketFormUiState$Content var1, CreateTicketViewModel var2, Continuation var3) {
      super(2, var3);
      this.$content = var1;
      this.this$0 = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Iterable var2 = this.$content.getQuestions();
            int var3 = 0;
            Iterable var4 = var2;
            ArrayList var5 = new ArrayList();
            int var6 = 0;

            for (Object var8 : var4) {
               QuestionState var9 = (QuestionState)var8;
               int var10 = 0;
               if (var9.getQuestionModel() instanceof SurveyData$Step$Question$UploadFileQuestionModel) {
                  var5.add(var8);
               }
            }

            var2 = (Iterable & List)var5;
            CreateTicketViewModel var22 = this.this$0;
            int var23 = 0;

            for (Object var25 : var2) {
               QuestionState var26 = (QuestionState)var25;
               int var27 = 0;
               SurveyData$Step$Question$QuestionModel var10000 = var26.getQuestionModel();
               Intrinsics.checkNotNull(
                  var10000, "null cannot be cast to non-null type io.intercom.android.sdk.survey.model.SurveyData.Step.Question.UploadFileQuestionModel"
               );
               SurveyData$Step$Question$UploadFileQuestionModel var28 = (SurveyData$Step$Question$UploadFileQuestionModel)var10000;
               Answer var29 = var26.getAnswer();
               if (var29 instanceof Answer$MediaAnswer) {
                  Iterable var11 = ((Answer$MediaAnswer)var29).getMediaItems();
                  int var12 = 0;
                  int var13 = 0;

                  for (Object var15 : var11) {
                     int var16 = var13++;
                     if (var16 < 0) {
                        CollectionsKt.throwIndexOverflow();
                     }

                     Answer$MediaAnswer$MediaItem var17 = (Answer$MediaAnswer$MediaItem)var15;
                     int var18 = var16;
                     int var19 = 0;
                     if (var18 >= var28.getMaxSelection()) {
                        StringRes[] var20 = new StringRes[]{
                           new StringRes(string.intercom_upload_failed, null, 2, null),
                           new StringRes(
                              string.intercom_upload_max_files_allowed, CollectionsKt.listOf(TuplesKt.to("limit", String.valueOf(var28.getMaxSelection())))
                           )
                        };
                        var17.setUploadStatus(
                           new Answer$MediaAnswer$FileUploadStatus$Error(new Answer$MediaAnswer$FileUploadError$FileLimitExceeded(CollectionsKt.listOf(var20)))
                        );
                     } else if (CreateTicketViewModel.access$isUnsupportedFileType(var22, var17.getData(), var28.getSupportedFileType())) {
                        var17.setUploadStatus(
                           new Answer$MediaAnswer$FileUploadStatus$Error(
                              new Answer$MediaAnswer$FileUploadError$UnsupportedFileType(
                                 CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null))
                              )
                           )
                        );
                     } else if (Intrinsics.areEqual(var17.getUploadStatus(), Answer$MediaAnswer$FileUploadStatus$None.INSTANCE)) {
                        var17.setUploadStatus(Answer$MediaAnswer$FileUploadStatus$Queued.INSTANCE);
                     } else if (CreateTicketViewModel.access$canRetryFileLimitExceededError(var22, var17, var18, var28.getMaxSelection())) {
                        var17.setUploadStatus(Answer$MediaAnswer$FileUploadStatus$Queued.INSTANCE);
                     }
                  }

                  CreateTicketViewModel.access$compressAndUploadFileAttachments(var22, ((Answer$MediaAnswer)var29).getMediaItems());
                  var26.validate();
               }
            }

            CreateTicketViewModel.access$updateCtaState(this.this$0);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketViewModel$onAnswerUpdated$1$2(this.$content, this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$onAnswerUpdated$1$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
