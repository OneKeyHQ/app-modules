package io.intercom.android.sdk.tickets.create.model;

import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.TicketTypeV2;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import io.intercom.android.sdk.tickets.create.reducers.CreateTicketReducerKt;
import java.util.List;
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
   f = "CreateTicketViewModel.kt",
   l = 105,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$fetchTicketType$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class CreateTicketViewModel$fetchTicketType$1 extends SuspendLambda implements Function2 {
   Object L$0;
   int label;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;
   // $VF: synthetic field
   final List $attributes;

   CreateTicketViewModel$fetchTicketType$1(CreateTicketViewModel var1, List var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$attributes = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      MutableStateFlow var4;
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var4 = CreateTicketViewModel.access$get_uiState$p(this.this$0);
            TicketRepository var6 = CreateTicketViewModel.access$getTicketRepository$p(this.this$0);
            long var10001 = CreateTicketViewModel.access$getTicketTypeId$p(this.this$0);
            List var10002 = this.$attributes;
            Continuation var10003 = this;
            this.L$0 = var4;
            this.label = 1;
            var10000 = var6.fetchTicketType(var10001, var10002, var10003);
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
      CreateTicketViewModel$CreateTicketFormUiState var7;
      if (var2 instanceof NetworkResponse$Success) {
         TicketTypeV2 var3 = (TicketTypeV2)((NetworkResponse$Success)var2).getBody();
         CreateTicketViewModel.access$setCurrentTicketType$p(this.this$0, var3);
         var7 = CreateTicketReducerKt.convertToTicketFormUiState(
            var3,
            CreateTicketViewModel.access$getUserIdentity$p(this.this$0),
            CreateTicketViewModel.access$getConfig$p(this.this$0),
            CreateTicketViewModel.access$getLastKnownContent(this.this$0)
         );
      } else if (!(var2 instanceof NetworkResponse$ClientError) && !(var2 instanceof NetworkResponse$ServerError)) {
         if (!(var2 instanceof NetworkResponse$NetworkError)) {
            throw new NoWhenBranchMatchedException();
         }

         var7 = new CreateTicketViewModel$CreateTicketFormUiState$Error(
            new ErrorState$WithCTA(
               0, 0, Boxing.boxInt(string.intercom_error_loading_ticket), 0, io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$fetchTicketType$1.recoveredExpressionFactory_1567(((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$fetchTicketType$1) this).this$0, ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$fetchTicketType$1) this).$attributes), 11, null
            )
         );
      } else {
         var7 = new CreateTicketViewModel$CreateTicketFormUiState$Error(
            new ErrorState$WithoutCTA(0, 0, Boxing.boxInt(string.intercom_error_loading_ticket), 3, null)
         );
      }

      var4.setValue(var7);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketViewModel$fetchTicketType$1(this.this$0, this.$attributes, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$fetchTicketType$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(CreateTicketViewModel var0, List var1) {
      CreateTicketViewModel.access$fetchTicketType(var0, var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function0 recoveredExpressionFactory_1567(io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1567_0, java.util.List recoveredExpressionCapture_1567_1) {
      return ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$fetchTicketType$1.invokeSuspend$lambda$0(recoveredExpressionCapture_1567_0, recoveredExpressionCapture_1567_1));
   }
}
