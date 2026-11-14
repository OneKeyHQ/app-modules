package io.intercom.android.sdk.m5.navigation;

import androidx.compose.runtime.MutableState;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
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
   f = "CreateTicketDestination.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.navigation.CreateTicketDestinationKt$createTicketDestination$4$showSheet$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class CreateTicketDestinationKt$createTicketDestination$4$showSheet$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MutableState $answerClickedData;
   // $VF: synthetic field
   final AnswerClickData $data;

   CreateTicketDestinationKt$createTicketDestination$4$showSheet$1(MutableState var1, AnswerClickData var2, Continuation var3) {
      super(2, var3);
      this.$answerClickedData = var1;
      this.$data = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            this.$answerClickedData.setValue(this.$data);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketDestinationKt$createTicketDestination$4$showSheet$1(this.$answerClickedData, this.$data, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketDestinationKt$createTicketDestination$4$showSheet$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
