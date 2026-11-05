package io.intercom.android.sdk.survey;

import io.intercom.android.sdk.survey.model.StatsCtaRequestModel;
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
   f = "SurveyViewModel.kt",
   l = 307,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.SurveyViewModel$onSecondaryCtaClicked$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SurveyViewModel$onSecondaryCtaClicked$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final SurveyViewModel this$0;
   // $VF: synthetic field
   final SurveyState$Content $content;

   SurveyViewModel$onSecondaryCtaClicked$1(SurveyViewModel var1, SurveyState$Content var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$content = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            SurveyRepository var10000 = SurveyViewModel.access$getSurveyRepository$p(this.this$0);
            String var10001 = SurveyViewModel.access$getSurveyData$p(this.this$0).getId();
            StatsCtaRequestModel var10002 = new StatsCtaRequestModel(
               SurveyViewModel.access$getSurveyData$p(this.this$0).getSurveyProgressId(), this.$content.getCurrentStep().getId()
            );
            Continuation var10003 = this;
            this.label = 1;
            if (var10000.submitCtaStat(var10001, var10002, var10003) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SurveyViewModel$onSecondaryCtaClicked$1(this.this$0, this.$content, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SurveyViewModel$onSecondaryCtaClicked$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
