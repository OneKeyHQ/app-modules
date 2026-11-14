package io.intercom.android.sdk.tickets;

import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "TicketDetailViewModel.kt",
   l = 111,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.TicketDetailViewModel$fetchTicketDetail$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class TicketDetailViewModel$fetchTicketDetail$1 extends SuspendLambda implements Function2 {
   Object L$0;
   int label;
   // $VF: synthetic field
   final TicketDetailViewModel this$0;
   // $VF: synthetic field
   final String $ticketId;

   TicketDetailViewModel$fetchTicketDetail$1(TicketDetailViewModel var1, String var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$ticketId = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      MutableStateFlow var4;
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var4 = TicketDetailViewModel.access$get_stateFlow$p(this.this$0);
            TicketRepository var6 = TicketDetailViewModel.access$getRepository$p(this.this$0);
            String var10001 = this.$ticketId;
            Continuation var10002 = this;
            this.L$0 = var4;
            this.label = 1;
            var10000 = var6.fetchTicketDetail(var10001, var10002);
            if (var10000 == var5) {
               return var5;
            }
            break;
         case 1:
            var4 = (MutableStateFlow)this.L$0;
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      TicketDetailState var7;
      if (var2 instanceof NetworkResponse$ClientError || var2 instanceof NetworkResponse$ServerError) {
         var7 = new TicketDetailState$Error(new ErrorState$WithoutCTA(0, 0, Boxing.boxInt(string.intercom_error_loading_ticket), 3, null));
      } else if (var2 instanceof NetworkResponse$NetworkError) {
         var7 = new TicketDetailState$Error(
            new ErrorState$WithCTA(
               0, 0, Boxing.boxInt(string.intercom_error_loading_ticket), 0, io.intercom.android.sdk.tickets.TicketDetailViewModel$fetchTicketDetail$1.recoveredExpressionFactory_1541(((io.intercom.android.sdk.tickets.TicketDetailViewModel$fetchTicketDetail$1) this).this$0, ((io.intercom.android.sdk.tickets.TicketDetailViewModel$fetchTicketDetail$1) this).$ticketId), 11, null
            )
         );
      } else {
         if (!(var2 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         Ticket var3 = (Ticket)((NetworkResponse$Success)var2).getBody();
         TicketDetailViewModel.access$fireMetricIfNecessary(this.this$0, var3);
         TicketDetailViewModel.access$markAsReadIfNecessary(this.this$0, var3);
         TicketDetailViewModel.access$setTicketId$p(this.this$0, var3.getId());
         var7 = TicketDetailReducerKt.computeTicketViewState(
            var3,
            TicketDetailViewModel.access$getUser$p(this.this$0),
            TicketDetailViewModel.access$getActiveAdminsAvatars(this.this$0),
            TicketDetailViewModel.access$getLaunchedFrom$p(this.this$0)
         );
      }

      var4.setValue(var7);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new TicketDetailViewModel$fetchTicketDetail$1(this.this$0, this.$ticketId, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((TicketDetailViewModel$fetchTicketDetail$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(TicketDetailViewModel var0, String var1) {
      var0.fetchTicketDetail$intercom_sdk_base_release(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_1541(io.intercom.android.sdk.tickets.TicketDetailViewModel recoveredExpressionCapture_1541_0, java.lang.String recoveredExpressionCapture_1541_1) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.TicketDetailViewModel$fetchTicketDetail$1.invokeSuspend$lambda$0(recoveredExpressionCapture_1541_0, recoveredExpressionCapture_1541_1));
   }
}
