package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.usecase.OpenConversationUseCase;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$1$1\n+ 2 UserJwtExpiredReset.kt\nio/intercom/android/sdk/m5/data/UserJwtExpiredResetKt\n+ 3 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,864:1\n12#2,2:865\n14#2:870\n15#2:874\n17#2:877\n230#3,3:867\n233#3,2:875\n1761#4,3:871\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$1$1\n*L\n267#1:865,2\n267#1:870\n267#1:874\n267#1:877\n267#1:867,3\n267#1:875,2\n267#1:871,3\n*E\n"
)
final class ConversationViewModel$1$1 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$1$1(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(Unit var1, Continuation var2) {
      MutableStateFlow var4 = ConversationViewModel.access$getClientState$p(this.this$0);
      int var5 = 0;
      boolean var6 = false;
      MutableStateFlow var7 = var4;
      int var8 = 0;

      Object var9;
      boolean var21;
      Object var23;
      do {
         var9 = var7.getValue();
         Object var10 = var9;
         int var11 = 0;
         ConversationClientState var12 = (ConversationClientState)var10;
         int var13 = 0;
         var var14 = CollectionsKt.listOf(var12.getLastNetworkCall());
         int var15 = 0;
         boolean var10000;
         if (var14 instanceof Collection && ((Collection)var14).isEmpty()) {
            var10000 = false;
         } else {
            label55: {
               for (Object var17 : var14) {
                  NetworkResponse var18 = (NetworkResponse)var17;
                  int var19 = 0;
                  if (var18 != null ? NetworkResponseKt.isUserJwtExpired(var18) : false) {
                     var10000 = true;
                     break label55;
                  }
               }

               var10000 = false;
            }
         }

         var21 = var10000;
         Object var24;
         if (var21) {
            ConversationClientState itx = (ConversationClientState)var10;
            int var20 = 0;
            var24 = ConversationClientState.copy$default(
               itx,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
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
               8388479,
               null
            );
         } else {
            var24 = var10;
         }

         var23 = var24;
      } while (!var7.compareAndSet(var9, var23));

      boolean var3 = var21;
      if (var3) {
         Object var25 = OpenConversationUseCase.invoke$default(
            ConversationViewModel.access$getOpenConversationUseCase$p(this.this$0),
            ConversationViewModel.access$getClientState$p(this.this$0),
            null,
            var2,
            2,
            null
         );
         return var25 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var25 : Unit.INSTANCE;
      } else {
         return Unit.INSTANCE;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Unit)var1, var2);
   }
}
