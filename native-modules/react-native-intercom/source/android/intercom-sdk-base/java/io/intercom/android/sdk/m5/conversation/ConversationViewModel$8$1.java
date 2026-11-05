package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Reconnected;
import io.intercom.android.sdk.m5.conversation.usecase.OpenConversationUseCase;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$8$1\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,864:1\n230#2,5:865\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$8$1\n*L\n404#1:865,5\n*E\n"
)
final class ConversationViewModel$8$1 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$8$1(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(NetworkState var1, Continuation var2) {
      ConversationViewModel$8$1 recoveredThis = this;
      ConversationViewModel$8$1$emit$1 var9;
      label37: {
         if (var2 instanceof ConversationViewModel$8$1$emit$1) {
            var9 = (ConversationViewModel$8$1$emit$1)var2;
            if ((var9.label & -2147483648) != 0) {
               var9.label -= Integer.MIN_VALUE;
               break label37;
            }
         }

         var9 = new ConversationViewModel$8$1$emit$1(recoveredThis, var2);
      }

      Object var8 = var9.result;
      Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var9.label) {
         case 0:
            ResultKt.throwOnFailure(var8);
            if (Intrinsics.areEqual(var1, NetworkState$Reconnected.INSTANCE)) {
               OpenConversationUseCase var10000 = ConversationViewModel.access$getOpenConversationUseCase$p(recoveredThis.this$0);
               MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(recoveredThis.this$0);
               GetConversationReason var10002 = GetConversationReason.NETWORK_CONNECTED;
               var9.L$0 = recoveredThis;
               var9.L$1 = var1;
               var9.label = 1;
               if (var10000.invoke(var10001, var10002, var9) == var10) {
                  return var10;
               }
            }
            break;
         case 1:
            var1 = (NetworkState)var9.L$1;
            recoveredThis = (ConversationViewModel$8$1)var9.L$0;
            ResultKt.throwOnFailure(var8);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      MutableStateFlow var3 = ConversationViewModel.access$getClientState$p(recoveredThis.this$0);
      int var4 = 0;

      Object var5;
      ConversationClientState var11;
      do {
         var5 = var3.getValue();
         var11 = (ConversationClientState)var5;
         int var7 = 0;
         var11 = ConversationClientState.copy$default(
            (ConversationClientState)var11,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var1,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388095,
            null
         );
      } while (!var3.compareAndSet(var5, var11));

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((NetworkState)var1, var2);
   }
}
