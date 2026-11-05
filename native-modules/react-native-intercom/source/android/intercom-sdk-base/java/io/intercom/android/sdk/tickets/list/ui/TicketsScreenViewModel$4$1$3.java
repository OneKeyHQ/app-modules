package io.intercom.android.sdk.tickets.list.ui;

import io.intercom.android.sdk.m5.data.CommonRepository;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "TicketsScreenViewModel.kt",
   l = 79,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel$4$1$3"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class TicketsScreenViewModel$4$1$3 extends SuspendLambda implements Function2 {
   Object L$0;
   int label;
   // $VF: synthetic field
   final TicketsScreenViewModel this$0;

   TicketsScreenViewModel$4$1$3(TicketsScreenViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      MutableStateFlow var2;
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var2 = TicketsScreenViewModel.access$get_openState$p(this.this$0);
            CommonRepository recoveredOpenMessengerRepository = TicketsScreenViewModel.access$getCommonRepository$p(this.this$0);
            Continuation var10001 = this;
            this.L$0 = var2;
            this.label = 1;
            var10000 = recoveredOpenMessengerRepository.openMessenger(var10001);
            if (var10000 == var3) {
               return var3;
            }
            break;
         case 1:
            var2 = (MutableStateFlow)this.L$0;
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      var2.setValue(var10000);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new TicketsScreenViewModel$4$1$3(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((TicketsScreenViewModel$4$1$3)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
