package io.intercom.android.sdk.survey;

import androidx.compose.foundation.relocation.BringIntoViewRequester;
import androidx.compose.foundation.relocation.BringIntoViewRequesterKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$MultipleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.utilities.AttributeValidatorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010$\u001a\u00020%2\u0006\u0010\u001c\u001a\u00020\u0015J\u0006\u0010&\u001a\u00020%J\u000e\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0005HÆ\u0003J\u001d\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010.\u001a\u00020/2\b\u00100\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00101\u001a\u000202H×\u0001J\t\u00103\u001a\u000204H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR+\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R+\u0010\u0016\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\u00158B@BX\u0082\u008e\u0002¢\u0006\u0012\n\u0004\b\u001b\u0010\u0014\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001b\u0010\u001c\u001a\u00020\u00158FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b\u001d\u0010\u0018R\u0011\u0010 \u001a\u00020!¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#¨\u00065",
   d2 = {
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "",
         "questionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "<init>",
         "(Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;Lio/intercom/android/sdk/survey/SurveyUiColors;)V",
         "getQuestionModel",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "<set-?>",
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "validationError",
         "getValidationError",
         "()Lio/intercom/android/sdk/survey/ValidationError;",
         "setValidationError",
         "(Lio/intercom/android/sdk/survey/ValidationError;)V",
         "validationError$delegate",
         "Landroidx/compose/runtime/MutableState;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "_answer",
         "get_answer",
         "()Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "set_answer",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer;)V",
         "_answer$delegate",
         "answer",
         "getAnswer",
         "answer$delegate",
         "Landroidx/compose/runtime/State;",
         "bringIntoViewRequester",
         "Landroidx/compose/foundation/relocation/BringIntoViewRequester;",
         "getBringIntoViewRequester",
         "()Landroidx/compose/foundation/relocation/BringIntoViewRequester;",
         "setAnswer",
         "",
         "validate",
         "bringIntoView",
         "Lkotlinx/coroutines/Job;",
         "compositionAwareScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSurveyViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/QuestionState\n+ 2 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,806:1\n85#2:807\n117#2,2:808\n85#2:810\n117#2,2:811\n85#2:813\n1563#3:814\n1634#3,3:815\n808#3,11:818\n*S KotlinDebug\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/QuestionState\n*L\n627#1:807\n627#1:808,2\n628#1:810\n628#1:811,2\n629#1:813\n753#1:814\n753#1:815,3\n754#1:818,11\n*E\n"
)
public final class QuestionState {
   @NotNull
   private final SurveyData$Step$Question$QuestionModel questionModel;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final MutableState validationError$delegate;
   @NotNull
   private final MutableState _answer$delegate;
   @NotNull
   private final State answer$delegate;
   @NotNull
   private final BringIntoViewRequester bringIntoViewRequester;
   public static final int $stable;

   static { $stable = 8; }

   public QuestionState(@NotNull SurveyData$Step$Question$QuestionModel var1, @NotNull SurveyUiColors var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "questionModel");
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      this.questionModel = var1;
      this.surveyUiColors = var2;
      this.validationError$delegate = SnapshotStateKt.mutableStateOf$default(ValidationError$Unchecked.INSTANCE, null, 2, null);
      this._answer$delegate = SnapshotStateKt.mutableStateOf$default(Answer$NoAnswer$InitialNoAnswer.INSTANCE, null, 2, null);
      final io.intercom.android.sdk.survey.QuestionState recoveredExpressionCapture_1215_0 = this;
      this.answer$delegate = SnapshotStateKt.derivedStateOf(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.QuestionState.answer_delegate$lambda$0(recoveredExpressionCapture_1215_0)));
      this.bringIntoViewRequester = BringIntoViewRequesterKt.BringIntoViewRequester();
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionModel getQuestionModel() {
      return this.questionModel;
   }

   @NotNull
   public final SurveyUiColors getSurveyUiColors() {
      return this.surveyUiColors;
   }

   @NotNull
   public final ValidationError getValidationError() {
      State var1 = this.validationError$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (ValidationError)var1.getValue();
   }

   public final void setValidationError(@NotNull ValidationError var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      MutableState var2 = this.validationError$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   private final Answer get_answer() {
      State var1 = this._answer$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Answer)var1.getValue();
   }

   private final void set_answer(Answer var1) {
      MutableState var2 = this._answer$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   @NotNull
   public final Answer getAnswer() {
      State var1 = this.answer$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Answer)var1.getValue();
   }

   @NotNull
   public final BringIntoViewRequester getBringIntoViewRequester() {
      return this.bringIntoViewRequester;
   }

   public final void setAnswer(@NotNull Answer var1) {
      Intrinsics.checkNotNullParameter(var1, "answer");
      this.set_answer(var1);
      if (var1 instanceof Answer$NoAnswer$ResetNoAnswer) {
         this.setValidationError(ValidationError$Unchecked.INSTANCE);
      }
   }

   public final void validate() {
      boolean var40;
      label149: {
         Answer var3 = this.getAnswer();
         Answer$MediaAnswer var10000 = var3 instanceof Answer$MediaAnswer ? (Answer$MediaAnswer)var3 : null;
         if ((var3 instanceof Answer$MediaAnswer ? (Answer$MediaAnswer)var3 : null) != null) {
            List var39 = var10000.getMediaItems();
            if (var39 != null) {
               var40 = var39.isEmpty();
               break label149;
            }
         }

         var40 = false;
      }

      boolean var1 = var40;
      if ((this.getAnswer() instanceof Answer$NoAnswer || var1) && this.questionModel.isRequired()) {
         this.setValidationError(new ValidationError$ValidationStringError(string.intercom_surveys_required_response, null, 2, null));
      } else if (!(this.getAnswer() instanceof Answer$NoAnswer) && !var1) {
         SurveyData$Step$Question$QuestionModel var2 = this.questionModel;
         if (var2 instanceof SurveyData$Step$Question$MultipleChoiceQuestionModel) {
            if (this.getAnswer() instanceof Answer$MultipleAnswer) {
               Answer var41 = this.getAnswer();
               Intrinsics.checkNotNull(var41, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MultipleAnswer");
               Answer$MultipleAnswer var16 = (Answer$MultipleAnswer)var41;
               int var4 = var16.getAnswers().size();
               if (var4 < ((SurveyData$Step$Question$MultipleChoiceQuestionModel)this.questionModel).getMinSelection()) {
                  this.setValidationError(
                     new ValidationError$ValidationStringError(
                        string.intercom_surveys_multi_select_too_few_responses,
                        CollectionsKt.listOf(
                           TuplesKt.to(
                              "response_count", String.valueOf(((SurveyData$Step$Question$MultipleChoiceQuestionModel)this.questionModel).getMinSelection())
                           )
                        )
                     )
                  );
               } else if (var4 > ((SurveyData$Step$Question$MultipleChoiceQuestionModel)this.questionModel).getMaxSelection()) {
                  this.setValidationError(
                     new ValidationError$ValidationStringError(
                        string.intercom_surveys_multi_select_too_many_responses,
                        CollectionsKt.listOf(
                           TuplesKt.to(
                              "response_count", String.valueOf(((SurveyData$Step$Question$MultipleChoiceQuestionModel)this.questionModel).getMaxSelection())
                           )
                        )
                     )
                  );
               } else {
                  this.setValidationError(ValidationError$NoValidationError.INSTANCE);
               }
            }
         } else if (var2 instanceof SurveyData$Step$Question$ShortTextQuestionModel) {
            QuestionState var42 = this;
            ValidationError var46;
            if (!((SurveyData$Step$Question$ShortTextQuestionModel)this.questionModel).getEnabled()) {
               var46 = ValidationError$NoValidationError.INSTANCE;
            } else if (((SurveyData$Step$Question$ShortTextQuestionModel)this.questionModel).getCharacterLimit() != null
               && this.getAnswer().getLength() > ((SurveyData$Step$Question$ShortTextQuestionModel)this.questionModel).getCharacterLimit()) {
               var46 = new ValidationError$ValidationStringError(
                  string.intercom_surveys_response_too_long,
                  CollectionsKt.listOf(
                     TuplesKt.to("character_count", String.valueOf(((SurveyData$Step$Question$ShortTextQuestionModel)this.questionModel).getCharacterLimit()))
                  )
               );
            } else {
               String var10001;
               switch (((SurveyData$Step$Question$ShortTextQuestionModel)this.questionModel).getValidationType()) {
                  case TEXT:
                  case NO_VALIDATION:
                     var10001 = "text";
                     break;
                  case NUMBER:
                     var10001 = "number";
                     break;
                  case EMAIL:
                     var10001 = "email";
                     break;
                  case PHONE:
                     var10001 = "phone";
                     break;
                  case FLOAT:
                     var10001 = "float";
                     break;
                  case INTEGER:
                     var10001 = "integer";
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }

               String var17 = var10001;
               Answer var5 = this.getAnswer();
               QuestionState var14 = this;
               int var6 = 0;
               String var45 = var5 instanceof Answer$SingleAnswer ? ((Answer$SingleAnswer)var5).getAnswer() : "";
               var42 = var14;
               int var20 = AttributeValidatorUtils.validateAttribute(var45, var17);
               switch (var20) {
                  case 1:
                     var46 = new ValidationError$ValidationStringError(string.intercom_string_is_incorrect, null, 2, null);
                     break;
                  case 2:
                     var46 = new ValidationError$ValidationStringError(string.intercom_surveys_email_error, null, 2, null);
                     break;
                  case 3:
                     var46 = new ValidationError$ValidationStringError(string.intercom_number_is_incorrect, null, 2, null);
                     break;
                  case 4:
                     var46 = new ValidationError$ValidationStringError(string.intercom_that_number_needs_a_prefix, null, 2, null);
                     break;
                  case 5:
                     var46 = new ValidationError$ValidationStringError(string.intercom_that_number_has_too_many_digits, null, 2, null);
                     break;
                  case 6:
                     var46 = new ValidationError$ValidationStringError(string.intercom_that_country_code_doesnt_look_quite_right, null, 2, null);
                     break;
                  case 7:
                     var46 = new ValidationError$ValidationStringError(string.intercom_that_number_is_missing_a_few_digits, null, 2, null);
                     break;
                  default:
                     var46 = ValidationError$NoValidationError.INSTANCE;
               }
            }

            var42.setValidationError(var46);
         } else if (var2 instanceof SurveyData$Step$Question$LongTextQuestionModel) {
            this.setValidationError(
               ((SurveyData$Step$Question$LongTextQuestionModel)this.questionModel).getCharacterLimit() != null
                     && this.getAnswer().getLength() > ((SurveyData$Step$Question$LongTextQuestionModel)this.questionModel).getCharacterLimit()
                  ? new ValidationError$ValidationStringError(
                     string.intercom_surveys_response_too_long,
                     CollectionsKt.listOf(
                        TuplesKt.to("character_count", String.valueOf(((SurveyData$Step$Question$LongTextQuestionModel)this.questionModel).getCharacterLimit()))
                     )
                  )
                  : ValidationError$NoValidationError.INSTANCE
            );
         } else if (var2 instanceof SurveyData$Step$Question$DatePickerQuestionModel) {
            if (this.getAnswer() instanceof Answer$DateTimeAnswer) {
               Answer var43 = this.getAnswer();
               Intrinsics.checkNotNull(var43, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.DateTimeAnswer");
               Answer$DateTimeAnswer var18 = (Answer$DateTimeAnswer)var43;
               this.setValidationError(
                  StringsKt.isBlank(var18.getDateAsString())
                     ? new ValidationError$ValidationStringError(string.intercom_choose_the_date, null, 2, null)
                     : ValidationError$NoValidationError.INSTANCE
               );
            }
         } else if (var2 instanceof SurveyData$Step$Question$UploadFileQuestionModel) {
            ValidationError var48;
            QuestionState var44;
            label117: {
               var44 = this;
               Answer var21 = this.getAnswer();
               Answer$MediaAnswer var19 = var21 instanceof Answer$MediaAnswer ? (Answer$MediaAnswer)var21 : null;
               if (var19 != null) {
                  List var22 = var19.getMediaItems();
                  if (var22 != null) {
                     Iterable var23 = var22;
                     QuestionState var36 = this;
                     int var24 = 0;
                     Iterable var7 = var23;
                     ArrayList var8 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var23, 10));
                     int var9 = 0;

                     for (Object var11 : var7) {
                        Answer$MediaAnswer$MediaItem var12 = (Answer$MediaAnswer$MediaItem)var11;
                        Collection var15 = var8;
                        int var13 = 0;
                        var15.add(var12.getUploadStatus());
                     }

                     var var25 = (Iterable & List)var8;
                     var36 = var36;
                     int var27 = 0;
                     Iterable recoveredStatuses = var25;
                     var destination$iv$ivx = new ArrayList();
                     int var31 = 0;

                     for (Object var35 : recoveredStatuses) {
                        if (var35 instanceof Answer$MediaAnswer$FileUploadStatus$Error) {
                           destination$iv$ivx.add(var35);
                        }
                     }

                     List var47 = (List)destination$iv$ivx;
                     var44 = var36;
                     Answer$MediaAnswer$FileUploadStatus$Error var26 = (Answer$MediaAnswer$FileUploadStatus$Error)CollectionsKt.firstOrNull(var47);
                     if (var26 != null) {
                        Answer$MediaAnswer$FileUploadStatus$Error var30 = var26;
                        var36 = var36;
                        var31 = 0;
                        StringRes var34 = (StringRes)CollectionsKt.first(var30.getError().getErrorMessages());
                        var48 = new ValidationError$ValidationStringError(var34.getStringRes(), var34.getParams());
                        var44 = var36;
                        var48 = var48;
                        break label117;
                     }
                  }
               }

               var48 = ValidationError$NoValidationError.INSTANCE;
            }

            var44.setValidationError(var48);
         } else {
            this.setValidationError(ValidationError$NoValidationError.INSTANCE);
         }
      } else {
         this.setValidationError(ValidationError$NoValidationError.INSTANCE);
      }
   }

   @NotNull
   public final Job bringIntoView(@NotNull CoroutineScope var1) {
      Intrinsics.checkNotNullParameter(var1, "compositionAwareScope");
      return BuildersKt.launch$default(var1, null, null, new QuestionState$bringIntoView$1(this, null), 3, null);
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionModel component1() {
      return this.questionModel;
   }

   @NotNull
   public final SurveyUiColors component2() {
      return this.surveyUiColors;
   }

   @NotNull
   public final QuestionState copy(@NotNull SurveyData$Step$Question$QuestionModel var1, @NotNull SurveyUiColors var2) {
      Intrinsics.checkNotNullParameter(var1, "questionModel");
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      return new QuestionState(var1, var2);
   }

   // $VF: synthetic method
   public static QuestionState copy$default(QuestionState var0, SurveyData$Step$Question$QuestionModel var1, SurveyUiColors var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.questionModel;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.surveyUiColors;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "QuestionState(questionModel=" + this.questionModel + ", surveyUiColors=" + this.surveyUiColors + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.questionModel.hashCode();
      return var1 * 31 + this.surveyUiColors.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof QuestionState)) {
         return false;
      }

      QuestionState var2 = (QuestionState)var1;
      return !Intrinsics.areEqual(this.questionModel, var2.questionModel) ? false : Intrinsics.areEqual(this.surveyUiColors, var2.surveyUiColors);
   }

   private static final Answer answer_delegate$lambda$0(QuestionState var0) {
      return var0.get_answer();
   }
}
