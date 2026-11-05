package io.intercom.android.sdk.tickets.list.ui;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

@DebugMetadata(
   f = "TicketsScreenViewModel.kt",
   l = 72,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel$3"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nTicketsScreenViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$3\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,120:1\n32#2:121\n17#2:122\n19#2:126\n17#2:127\n19#2:131\n46#3:123\n51#3:125\n46#3:128\n51#3:130\n105#4:124\n105#4:129\n*S KotlinDebug\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$3\n*L\n70#1:121\n70#1:122\n70#1:126\n71#1:127\n71#1:131\n70#1:123\n70#1:125\n71#1:128\n71#1:130\n70#1:124\n71#1:129\n*E\n"
)
final class TicketsScreenViewModel$3 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final TicketsScreenViewModel this$0;

   TicketsScreenViewModel$3(TicketsScreenViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = TicketsScreenViewModel.access$getRepository$p(this.this$0).realTimeEvents();
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            Flow var6 = var4;
            int var7 = 0;
            int var8 = 0;
            var2 = new TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var6);
            var3 = 0;
            var4 = var2;
            var5 = 0;
            int $i$f$unsafeFlowx = 0;
            Flow var10000 = new TicketsScreenViewModel$3$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filter$1(var4);
            FlowCollector var10001 = new TicketsScreenViewModel$3$2(this.this$0);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var9) {
               return var9;
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
      return new TicketsScreenViewModel$3(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((TicketsScreenViewModel$3)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
