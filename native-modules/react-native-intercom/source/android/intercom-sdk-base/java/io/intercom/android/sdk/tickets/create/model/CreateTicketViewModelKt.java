package io.intercom.android.sdk.tickets.create.model;

import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0006",
   d2 = {"formatAnswerForServer", "", "questionState", "Lio/intercom/android/sdk/survey/QuestionState;", "EmailId", "", "intercom-sdk-base_release"}
)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModelKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,499:1\n1563#2:500\n1634#2,3:501\n1869#2,2:504\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModelKt\n*L\n489#1:500\n489#1:501,3\n489#1:504,2\n*E\n"
)
public final class CreateTicketViewModelKt {
   @NotNull
   public static final String EmailId = "-1";

   private static final Object formatAnswerForServer(QuestionState var0) {
      Answer var1 = var0.getAnswer();
      Object var10000;
      if (var1 instanceof Answer$DateTimeAnswer) {
         var10000 = ((Answer$DateTimeAnswer)var1).getAnswer();
      } else if (var1 instanceof Answer$MultipleAnswer) {
         var10000 = ((Answer$MultipleAnswer)var1).recoveredReturn$bf3409efca2cfb1e().toString();
      } else if (Intrinsics.areEqual(var1, Answer$NoAnswer$InitialNoAnswer.INSTANCE) || Intrinsics.areEqual(var1, Answer$NoAnswer$ResetNoAnswer.INSTANCE)) {
         var10000 = Unit.INSTANCE;
      } else if (var1 instanceof Answer$SingleAnswer) {
         SurveyData$Step$Question$QuestionModel var2 = var0.getQuestionModel();
         if (var2 instanceof SurveyData$Step$Question$SingleChoiceQuestionModel) {
            var10000 = Boolean.parseBoolean(((Answer$SingleAnswer)var1).getAnswer());
         } else if (var2 instanceof SurveyData$Step$Question$ShortTextQuestionModel) {
            switch (((SurveyData$Step$Question$ShortTextQuestionModel)var2).getValidationType()) {
               case NUMBER:
                  var10000 = Integer.parseInt(((Answer$SingleAnswer)var1).getAnswer());
                  break;
               case FLOAT:
                  var10000 = Float.parseFloat(((Answer$SingleAnswer)var1).getAnswer());
                  break;
               case INTEGER:
                  var10000 = Integer.parseInt(((Answer$SingleAnswer)var1).getAnswer());
                  break;
               default:
                  var10000 = ((Answer$SingleAnswer)var1).getAnswer();
            }
         } else {
            var10000 = ((Answer$SingleAnswer)var1).getAnswer();
         }
      } else {
         if (!(var1 instanceof Answer$MediaAnswer)) {
            throw new NoWhenBranchMatchedException();
         }

         ArrayList var13 = new ArrayList();
         Iterable var3 = ((Answer$MediaAnswer)var1).getMediaItems();
         int var4 = 0;
         Iterable var5 = var3;
         ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
         int var7 = 0;

         for (Object var9 : var5) {
            Answer$MediaAnswer$MediaItem var10 = (Answer$MediaAnswer$MediaItem)var9;
            Collection var12 = var6;
            int var11 = 0;
            var12.add(var10.getUploadStatus());
         }

         var3 = (Iterable & List)var6;
         var4 = 0;

         for (Object var17 : var3) {
            Answer$MediaAnswer$FileUploadStatus var18 = (Answer$MediaAnswer$FileUploadStatus)var17;
            int var19 = 0;
            if (var18 instanceof Answer$MediaAnswer$FileUploadStatus$Success) {
               var13.add(((Answer$MediaAnswer$FileUploadStatus$Success)var18).getId());
            }
         }

         var10000 = var13;
      }

      return var10000;
   }

   // $VF: synthetic method
   public static final Object access$formatAnswerForServer(QuestionState var0) {
      return formatAnswerForServer(var0);
   }
}
