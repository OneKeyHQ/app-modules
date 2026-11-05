package io.intercom.android.sdk.survey;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
import io.intercom.android.sdk.survey.model.SurveyData$StepType;
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
   l = {277, 282},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.SurveyViewModel$onCloseClicked$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SurveyViewModel$onCloseClicked$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final SurveyViewModel this$0;

   SurveyViewModel$onCloseClicked$1$1(SurveyViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (SurveyViewModel.access$getSurveyData$p(this.this$0).getSteps().isEmpty()) {
               return Unit.INSTANCE;
            }

            SurveyData$Step var4 = SurveyViewModel.access$getCurrentStep$p(this.this$0);
            if ((var4 != null ? var4.getType() : null) == SurveyData$StepType.THANK_YOU) {
               return Unit.INSTANCE;
            }

            SurveyRepository var5 = SurveyViewModel.access$getSurveyRepository$p(this.this$0);
            String var10001 = SurveyViewModel.access$getSurveyData$p(this.this$0).getId();
            String var10002 = SurveyViewModel.access$getSurveyData$p(this.this$0).getSurveyProgressId();
            Continuation var10003 = this;
            this.label = 1;
            var10000 = var5.dismissSurvey(var10001, var10002, var10003);
            if (var10000 == var3) {
               return var3;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      if (var2 instanceof NetworkResponse$ClientError) {
         SurveyViewModel var6 = this.this$0;
         String var7 = SurveyViewModel.access$getSurveyData$p(this.this$0).getId();
         String var8 = SurveyViewModel.access$getSurveyData$p(this.this$0).getSurveyProgressId();
         Continuation var9 = this;
         this.label = 2;
         if (SurveyViewModel.access$reportFailure(var6, var7, var8, var9) == var3) {
            return var3;
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SurveyViewModel$onCloseClicked$1$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SurveyViewModel$onCloseClicked$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
