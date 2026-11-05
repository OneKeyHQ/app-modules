package io.intercom.android.sdk.survey;

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
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(f = "SurveyViewModel.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.survey.SurveyViewModel$onUiLoaded$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SurveyViewModel$onUiLoaded$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final SurveyViewModel this$0;

   SurveyViewModel$onUiLoaded$1(SurveyViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            SurveyLaunchMode var2 = SurveyViewModel.access$getLaunchMode$p(this.this$0);
            if (var2 instanceof SurveyLaunchMode$Automatic) {
               SurveyViewModel.access$setSurveyData$p(
                  this.this$0, ((SurveyLaunchMode$Automatic)SurveyViewModel.access$getLaunchMode$p(this.this$0)).getSurveyData()
               );
               if (!SurveyViewModel.access$getSurveyData$p(this.this$0).getSteps().isEmpty()) {
                  SurveyViewModel.access$getMetricTracker$p(this.this$0)
                     .surveyOpened(
                        "opened",
                        "survey",
                        "from_automatic",
                        SurveyViewModel.access$getSurveyData$p(this.this$0).getId(),
                        SurveyViewModel.access$getSurveyData$p(this.this$0).getFormatMetric()
                     );
                  SurveyData$Step var3 = (SurveyData$Step)CollectionsKt.first(SurveyViewModel.access$getSurveyData$p(this.this$0).getSteps());
                  SurveyViewModel.access$setCurrentStep$p(this.this$0, var3);
                  this.this$0
                     .getState()
                     .setValue(SurveyViewModel.access$buildContentState(this.this$0, SurveyViewModel.access$getSurveyData$p(this.this$0), var3));
               } else {
                  SurveyUiColors var5 = SurveyViewModelKt.toSurveyUiColors(SurveyViewModel.access$getSurveyData$p(this.this$0).getCustomization());
                  MutableStateFlow var10000 = this.this$0.getState();
                  TopBarState$NoTopBarState var4 = new TopBarState$NoTopBarState(true, var5, null, 4, null);
                  var10000.setValue(new SurveyState$Error$WithoutCTA(0, var5, var4, 1, null));
               }
            } else {
               if (!(var2 instanceof SurveyLaunchMode$Programmatic)) {
                  throw new NoWhenBranchMatchedException();
               }

               SurveyViewModel.access$fetchAndShowSurvey(
                  this.this$0, ((SurveyLaunchMode$Programmatic)SurveyViewModel.access$getLaunchMode$p(this.this$0)).getSurveyId()
               );
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SurveyViewModel$onUiLoaded$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SurveyViewModel$onUiLoaded$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
