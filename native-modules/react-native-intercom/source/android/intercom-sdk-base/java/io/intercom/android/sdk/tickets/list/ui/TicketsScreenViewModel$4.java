package io.intercom.android.sdk.tickets.list.ui;

import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.SharedFlow;

@DebugMetadata(
   f = "TicketsScreenViewModel.kt",
   l = 77,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel$4"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class TicketsScreenViewModel$4 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final TicketsScreenViewModel this$0;

   TicketsScreenViewModel$4(TicketsScreenViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            SharedFlow var10000 = TicketsScreenViewModel.access$getIntercomDataLayer$p(this.this$0).getUserJwtChanges();
            FlowCollector var10001 = new TicketsScreenViewModel$4$1(this.this$0, var2);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var3) {
               return var3;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      throw new KotlinNothingValueException();
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      TicketsScreenViewModel$4 var3 = new TicketsScreenViewModel$4(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((TicketsScreenViewModel$4)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
