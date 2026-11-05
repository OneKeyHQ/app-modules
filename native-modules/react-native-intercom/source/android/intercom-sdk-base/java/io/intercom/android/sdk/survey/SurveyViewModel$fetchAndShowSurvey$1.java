package io.intercom.android.sdk.survey;

import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.survey.model.FetchSurveyRequest;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
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
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "SurveyViewModel.kt",
   l = 142,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.SurveyViewModel$fetchAndShowSurvey$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SurveyViewModel$fetchAndShowSurvey$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final String $surveyId;
   // $VF: synthetic field
   final SurveyViewModel this$0;
   // $VF: synthetic field
   final SurveyUiColors $uiColors;

   SurveyViewModel$fetchAndShowSurvey$1(String var1, SurveyViewModel var2, SurveyUiColors var3, Continuation var4) {
      super(2, var4);
      this.$surveyId = var1;
      this.this$0 = var2;
      this.$uiColors = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (StringsKt.isBlank(this.$surveyId)) {
               MutableStateFlow var16 = this.this$0.getState();
               TopBarState$NoTopBarState var6 = new TopBarState$NoTopBarState(true, this.$uiColors, null, 4, null);
               var16.setValue(new SurveyState$Error$WithoutCTA(0, this.$uiColors, var6, 1, null));
               return Unit.INSTANCE;
            }

            SurveyViewModel.access$emitLoading(this.this$0);
            SurveyRepository var11 = SurveyViewModel.access$getSurveyRepository$p(this.this$0);
            String var10001 = this.$surveyId;
            Continuation var10002 = this;
            this.label = 1;
            var10000 = var11.fetchSurvey(var10001, var10002);
            if (var10000 == var5) {
               return var5;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      if (var2 instanceof NetworkResponse$Success) {
         SurveyViewModel.access$setSurveyData$p(this.this$0, ((FetchSurveyRequest)((NetworkResponse$Success)var2).getBody()).getSurvey());
         if (!SurveyViewModel.access$getSurveyData$p(this.this$0).getSteps().isEmpty()) {
            SurveyViewModel.access$getMetricTracker$p(this.this$0)
               .surveyOpened(
                  "opened",
                  "survey",
                  "from_programmatic",
                  SurveyViewModel.access$getSurveyData$p(this.this$0).getId(),
                  SurveyViewModel.access$getSurveyData$p(this.this$0).getFormatMetric()
               );
            SurveyData$Step var3 = (SurveyData$Step)CollectionsKt.first(SurveyViewModel.access$getSurveyData$p(this.this$0).getSteps());
            SurveyViewModel.access$setCurrentStep$p(this.this$0, var3);
            this.this$0.getState().setValue(SurveyViewModel.access$buildContentState(this.this$0, SurveyViewModel.access$getSurveyData$p(this.this$0), var3));
         } else {
            MutableStateFlow var12 = this.this$0.getState();
            TopBarState$NoTopBarState var7 = new TopBarState$NoTopBarState(true, this.$uiColors, null, 4, null);
            var12.setValue(new SurveyState$Error$WithoutCTA(0, this.$uiColors, var7, 1, null));
         }
      } else if (var2 instanceof NetworkResponse$ClientError) {
         MutableStateFlow var13 = this.this$0.getState();
         TopBarState$NoTopBarState var8 = new TopBarState$NoTopBarState(true, this.$uiColors, null, 4, null);
         var13.setValue(new SurveyState$Error$WithoutCTA(0, this.$uiColors, var8, 1, null));
      } else if (var2 instanceof NetworkResponse$NetworkError) {
         MutableStateFlow var14 = this.this$0.getState();
         TopBarState$NoTopBarState var9 = new TopBarState$NoTopBarState(true, this.$uiColors, null, 4, null);
         var14.setValue(new SurveyState$Error$WithCTA(0, this.$uiColors, var9, io.intercom.android.sdk.survey.SurveyViewModel$fetchAndShowSurvey$1.recoveredExpressionFactory_1216(((io.intercom.android.sdk.survey.SurveyViewModel$fetchAndShowSurvey$1) this).this$0, ((io.intercom.android.sdk.survey.SurveyViewModel$fetchAndShowSurvey$1) this).$surveyId), 1, null));
      } else {
         if (!(var2 instanceof NetworkResponse$ServerError)) {
            throw new NoWhenBranchMatchedException();
         }

         MutableStateFlow var15 = this.this$0.getState();
         int var10 = string.intercom_page_not_found;
         TopBarState$NoTopBarState var4 = new TopBarState$NoTopBarState(true, this.$uiColors, null, 4, null);
         var15.setValue(new SurveyState$Error$WithoutCTA(var10, this.$uiColors, var4));
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SurveyViewModel$fetchAndShowSurvey$1(this.$surveyId, this.this$0, this.$uiColors, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SurveyViewModel$fetchAndShowSurvey$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(SurveyViewModel var0, String var1) {
      SurveyViewModel.access$fetchAndShowSurvey(var0, var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_1216(io.intercom.android.sdk.survey.SurveyViewModel recoveredExpressionCapture_1216_0, java.lang.String recoveredExpressionCapture_1216_1) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.SurveyViewModel$fetchAndShowSurvey$1.invokeSuspend$lambda$0(recoveredExpressionCapture_1216_0, recoveredExpressionCapture_1216_1));
   }
}
