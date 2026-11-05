package io.intercom.android.sdk.survey;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.survey.model.SubmitSurveyRequest;
import io.intercom.android.sdk.survey.model.SubmitSurveyRequestItem;
import io.intercom.android.sdk.survey.model.SubmitSurveyResponse;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
import io.intercom.android.sdk.survey.model.SurveyData$StepType;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;

@DebugMetadata(
   f = "SurveyViewModel.kt",
   l = {328, 368, 415},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nSurveyViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/SurveyViewModel$submitSurvey$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,806:1\n1869#2,2:807\n1740#2,3:809\n230#2,2:812\n1563#2:815\n1634#2,3:816\n1#3:814\n*S KotlinDebug\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/SurveyViewModel$submitSurvey$1\n*L\n333#1:807,2\n337#1:809,3\n339#1:812,2\n349#1:815\n349#1:816,3\n*E\n"
)
final class SurveyViewModel$submitSurvey$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final SurveyState$Content $surveyContent;
   // $VF: synthetic field
   final SurveyViewModel this$0;
   // $VF: synthetic field
   final CoroutineScope $compositionAwareScope;

   SurveyViewModel$submitSurvey$1(SurveyState$Content var1, SurveyViewModel var2, CoroutineScope var3, Continuation var4) {
      super(2, var4);
      this.$surveyContent = var1;
      this.this$0 = var2;
      this.$compositionAwareScope = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var16 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (this.$surveyContent.getCurrentStep().getType() == SurveyData$StepType.THANK_YOU) {
               SurveyViewModel.access$sendDismissedEvent(this.this$0, CloseEventTrigger.CTA);
               SurveyViewModel.access$getIntercomDataLayer$p(this.this$0).clearSurveyData();
               MutableSharedFlow var58 = this.this$0.getEffects();
               SurveyEffects$ExitSurvey var59 = SurveyEffects$ExitSurvey.INSTANCE;
               Continuation var62 = this;
               this.label = 1;
               if (var58.emit(var59, var62) == var16) {
                  return var16;
               }

               return Unit.INSTANCE;
            }

            List var2 = this.$surveyContent.getQuestions();
            if (!var2.isEmpty()) {
               Iterable var3 = var2;
               int var4 = 0;

               for (Object var6 : var3) {
                  QuestionState var7 = (QuestionState)var6;
                  int var8 = 0;
                  var7.validate();
               }

               var var19 = var2;
               int var24 = 0;
               boolean var54;
               if (var19 instanceof Collection && ((Collection)var19).isEmpty()) {
                  var54 = true;
               } else {
                  Iterator var29 = var19.iterator();

                  while (true) {
                     if (!var29.hasNext()) {
                        var54 = true;
                        break;
                     }

                     Object var34 = var29.next();
                     QuestionState var40 = (QuestionState)var34;
                     int var9 = 0;
                     if (!(var40.getValidationError() instanceof ValidationError$NoValidationError)) {
                        var54 = false;
                        break;
                     }
                  }
               }

               boolean var17 = var54;
               if (!var17) {
                  Iterable var22 = var2;
                  var24 = 0;

                  for (Object var38 : var22) {
                     QuestionState var43 = (QuestionState)var38;
                     int var47 = 0;
                     if (!(var43.getValidationError() instanceof ValidationError$NoValidationError)) {
                        Object var23 = var38;
                        CoroutineScope var28 = this.$compositionAwareScope;
                        QuestionState var33 = (QuestionState)var23;
                        boolean var39 = false;
                        if (var28 != null) {
                           CoroutineScope var44 = var28;
                           var47 = 0;
                           var33.bringIntoView(var44);
                        }

                        return Unit.INSTANCE;
                     }
                  }

                  throw new NoSuchElementException("Collection contains no element matching the predicate.");
               }
            }

            SurveyViewModel.access$emitLoading(this.this$0);
            Iterable var20 = this.$surveyContent.getQuestions();
            int var25 = 0;
            Iterable var30 = var20;
            ArrayList var35 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var20, 10));
            int var41 = 0;

            for (Object var10 : var30) {
               QuestionState var11 = (QuestionState)var10;
               Collection var14 = var35;
               int var12 = 0;
               SubmitSurveyRequestItem var55;
               String var10002 = var11.getQuestionModel().getId();
               Answer var13 = var11.getAnswer();
               List var10003;
               if (var13 instanceof Answer$MultipleAnswer) {
                  var10003 = ((Answer$MultipleAnswer)var13).getAnswers();
               } else if (var13 instanceof Answer$NoAnswer) {
                  var10003 = CollectionsKt.emptyList();
               } else if (var13 instanceof Answer$SingleAnswer) {
                  var10003 = CollectionsKt.listOf(((Answer$SingleAnswer)var13).getAnswer());
               } else if (var13 instanceof Answer$DateTimeAnswer) {
                  var10003 = CollectionsKt.emptyList();
               } else {
                  if (!(var13 instanceof Answer$MediaAnswer)) {
                     throw new NoWhenBranchMatchedException();
                  }

                  var10003 = CollectionsKt.emptyList();
               }

               var55 = new SubmitSurveyRequestItem(var10002, var10003);
               var14.add(var55);
            }

            List var18 = (List)var35;
            SurveyViewModel.access$getCompletedResponses$p(this.this$0).addAll(var18);
            SubmitSurveyRequest var21 = new SubmitSurveyRequest(
               SurveyViewModel.access$getSurveyData$p(this.this$0).getSurveyProgressId(),
               this.$surveyContent.getCurrentStep().getId(),
               SurveyViewModel.access$getCompletedResponses$p(this.this$0)
            );
            SurveyRepository var56 = SurveyViewModel.access$getSurveyRepository$p(this.this$0);
            String var60 = SurveyViewModel.access$getSurveyData$p(this.this$0).getId();
            Continuation var63 = this;
            this.label = 2;
            var10000 = var56.submitSurvey(var21, var60, var63);
            if (var10000 == var16) {
               return var16;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         case 2:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         case 3:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var26 = (NetworkResponse)var10000;
      if (var26 instanceof NetworkResponse$Success) {
         SubmitSurveyResponse var31 = (SubmitSurveyResponse)((NetworkResponse$Success)var26).getBody();
         if (this.$surveyContent.getCurrentStep().getType() == SurveyData$StepType.QUESTION
            || this.$surveyContent.getCurrentStep().getType() == SurveyData$StepType.CONTENT) {
            SurveyViewModel.access$getCompletedSteps$p(this.this$0).add(this.$surveyContent.getCurrentStep());
         }

         if (var31.getComplete()) {
            Iterable var42 = SurveyViewModel.access$getCompletedSteps$p(this.this$0);
            int var46 = 0;

            for (Object var50 : var42) {
               SurveyData$Step var51 = (SurveyData$Step)var50;
               int var53 = var46;
               int var52 = 0;
               int var15 = var51.getQuestions().size();
               var46 = var53 + var15;
            }

            int var36 = var46;
            SurveyViewModel.access$getMetricTracker$p(this.this$0)
               .surveyCompleted(
                  "completed",
                  "survey",
                  "from_cta",
                  SurveyViewModel.access$getSurveyData$p(this.this$0).getId(),
                  SurveyViewModel.access$getSurveyData$p(this.this$0).getFormatMetric(),
                  SurveyViewModel.access$getCompletedSteps$p(this.this$0).size(),
                  SurveyViewModel.access$getSurveyData$p(this.this$0).getStepCount(),
                  var36
               );
         }

         SurveyData$Step nextStepx = var31.getNextStep();
         SurveyViewModel.access$setCurrentStep$p(this.this$0, nextStepx);
         this.this$0.getState().setValue(SurveyViewModel.access$buildContentState(this.this$0, SurveyViewModel.access$getSurveyData$p(this.this$0), nextStepx));
      } else if (var26 instanceof NetworkResponse$NetworkError) {
         this.this$0
            .getState()
            .setValue(
               new SurveyState$Error$WithCTA(
                  0,
                  this.$surveyContent.getSurveyUiColors(),
                  this.$surveyContent
                     .getTopBarState()
                     .withProgressBarState(ProgressBarState.copy$default(this.$surveyContent.getTopBarState().getProgressBarState(), false, 0.0F, 2, null))
                     .enableDismissButton(),
                  io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1.recoveredExpressionFactory_1217(((io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1) this).this$0, ((io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1) this).$surveyContent, ((io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1) this).$compositionAwareScope),
                  1,
                  null
               )
            );
      } else {
         if (!(var26 instanceof NetworkResponse$ServerError) && !(var26 instanceof NetworkResponse$ClientError)) {
            throw new NoWhenBranchMatchedException();
         }

         this.this$0
            .getState()
            .setValue(
               new SurveyState$Error$WithoutCTA(
                  0,
                  this.$surveyContent.getSurveyUiColors(),
                  this.$surveyContent
                     .getTopBarState()
                     .withProgressBarState(ProgressBarState.copy$default(this.$surveyContent.getTopBarState().getProgressBarState(), false, 0.0F, 2, null))
                     .enableDismissButton(),
                  1,
                  null
               )
            );
         if (var26 instanceof NetworkResponse$ClientError) {
            SurveyViewModel var57 = this.this$0;
            String var10001 = SurveyViewModel.access$getSurveyData$p(this.this$0).getId();
            String var61 = SurveyViewModel.access$getSurveyData$p(this.this$0).getSurveyProgressId();
            Continuation var64 = this;
            this.label = 3;
            if (SurveyViewModel.access$reportFailure(var57, var10001, var61, var64) == var16) {
               return var16;
            }
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SurveyViewModel$submitSurvey$1(this.$surveyContent, this.this$0, this.$compositionAwareScope, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SurveyViewModel$submitSurvey$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$7(SurveyViewModel var0, SurveyState$Content var1, CoroutineScope var2) {
      SurveyViewModel.access$submitSurvey(var0, var1, var2);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_1217(io.intercom.android.sdk.survey.SurveyViewModel recoveredExpressionCapture_1217_0, io.intercom.android.sdk.survey.SurveyState$Content recoveredExpressionCapture_1217_1, kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1217_2) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.SurveyViewModel$submitSurvey$1.invokeSuspend$lambda$7(recoveredExpressionCapture_1217_0, recoveredExpressionCapture_1217_1, recoveredExpressionCapture_1217_2));
   }
}
