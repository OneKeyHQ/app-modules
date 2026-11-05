package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.MutableState;
import androidx.compose.ui.unit.Dp;
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
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "TicketDetailContent.kt",
   l = 81,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.TicketDetailContentKt$TicketDetailContent$2$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nTicketDetailContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt$TicketDetailContent$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,372:1\n118#2:373\n*S KotlinDebug\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt$TicketDetailContent$2$1\n*L\n79#1:373\n*E\n"
)
final class TicketDetailContentKt$TicketDetailContent$2$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MutableState $submissionCardOffset$delegate;
   // $VF: synthetic field
   final MutableState $submissionCardAlpha$delegate;
   // $VF: synthetic field
   final MutableState $cardState$delegate;

   TicketDetailContentKt$TicketDetailContent$2$1(MutableState var1, MutableState var2, MutableState var3, Continuation var4) {
      super(2, var4);
      this.$submissionCardOffset$delegate = var1;
      this.$submissionCardAlpha$delegate = var2;
      this.$cardState$delegate = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            int var2 = 0;
            int var3 = 0;
            TicketDetailContentKt.access$TicketDetailContent$lambda$8(this.$submissionCardOffset$delegate, Dp.recovered$95b3d103663bf060(var2));
            TicketDetailContentKt.access$TicketDetailContent$lambda$11(this.$submissionCardAlpha$delegate, 1.0F);
            Continuation var10001 = this;
            this.label = 1;
            if (DelayKt.delay(5000L, var10001) == var4) {
               return var4;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      TicketDetailContentKt.access$TicketDetailContent$lambda$5(this.$cardState$delegate, CardState.TimelineCard);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new TicketDetailContentKt$TicketDetailContent$2$1(
         this.$submissionCardOffset$delegate, this.$submissionCardAlpha$delegate, this.$cardState$delegate, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((TicketDetailContentKt$TicketDetailContent$2$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
