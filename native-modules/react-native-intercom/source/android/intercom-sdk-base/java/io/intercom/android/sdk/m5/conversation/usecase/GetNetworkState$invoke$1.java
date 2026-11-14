package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ProducerScope;

@DebugMetadata(
   f = "GetNetworkState.kt",
   l = 24,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.usecase.GetNetworkState$invoke$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/channels/ProducerScope;", "Lio/intercom/android/sdk/m5/conversation/states/NetworkState;"}
)
final class GetNetworkState$invoke$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final GetNetworkState this$0;

   GetNetworkState$invoke$1(GetNetworkState var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ProducerScope var2 = (ProducerScope)this.L$0;
            GetNetworkState.access$getNetworkConnectivityMonitor$p(this.this$0).setListener(new GetNetworkState$invoke$1$1(var2));
            final io.intercom.android.sdk.m5.conversation.usecase.GetNetworkState recoveredExpressionCapture_806_0 = ((io.intercom.android.sdk.m5.conversation.usecase.GetNetworkState$invoke$1) this).this$0;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.usecase.GetNetworkState$invoke$1.invokeSuspend$lambda$0(recoveredExpressionCapture_806_0));
            Continuation var10002 = this;
            this.label = 1;
            if (ProduceKt.awaitClose(var2, var10001, var10002) == var3) {
               return var3;
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
      GetNetworkState$invoke$1 var3 = new GetNetworkState$invoke$1(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(ProducerScope var1, Continuation var2) {
      return ((GetNetworkState$invoke$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$0(GetNetworkState var0) {
      GetNetworkState.access$getNetworkConnectivityMonitor$p(var0).setListener(null);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((ProducerScope)var1, (Continuation)var2);
   }
}
