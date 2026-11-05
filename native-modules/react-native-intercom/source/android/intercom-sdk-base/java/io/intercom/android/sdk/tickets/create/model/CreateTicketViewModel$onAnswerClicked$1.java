package io.intercom.android.sdk.tickets.create.model;

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
import kotlinx.coroutines.flow.MutableSharedFlow;

@DebugMetadata(
   f = "CreateTicketViewModel.kt",
   l = 413,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$onAnswerClicked$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class CreateTicketViewModel$onAnswerClicked$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;
   // $VF: synthetic field
   final AnswerClickData $answerClickData;

   CreateTicketViewModel$onAnswerClicked$1(CreateTicketViewModel var1, AnswerClickData var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$answerClickData = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            MutableSharedFlow var10000 = CreateTicketViewModel.access$get_effect$p(this.this$0);
            CreateTicketViewModel$TicketSideEffect$AnswerClicked var10001 = new CreateTicketViewModel$TicketSideEffect$AnswerClicked(this.$answerClickData);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.emit(var10001, var10002) == var2) {
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
      return new CreateTicketViewModel$onAnswerClicked$1(this.this$0, this.$answerClickData, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$onAnswerClicked$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
