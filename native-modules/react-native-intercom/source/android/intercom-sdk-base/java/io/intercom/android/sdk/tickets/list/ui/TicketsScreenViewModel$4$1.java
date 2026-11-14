package io.intercom.android.sdk.tickets.list.ui;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenEffects$RefreshTickets;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketsScreenViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$4$1\n+ 2 UserJwtExpiredReset.kt\nio/intercom/android/sdk/m5/data/UserJwtExpiredResetKt\n+ 3 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,120:1\n12#2,2:121\n14#2:126\n15#2:130\n17#2:133\n230#3,3:123\n233#3,2:131\n1761#4,3:127\n*S KotlinDebug\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$4$1\n*L\n78#1:121,2\n78#1:126\n78#1:130\n78#1:133\n78#1:123,3\n78#1:131,2\n78#1:127,3\n*E\n"
)
final class TicketsScreenViewModel$4$1 implements FlowCollector {
   // $VF: synthetic field
   final TicketsScreenViewModel this$0;
   // $VF: synthetic field
   final CoroutineScope $$this$launch;

   TicketsScreenViewModel$4$1(TicketsScreenViewModel var1, CoroutineScope var2) {
      this.this$0 = var1;
      this.$$this$launch = var2;
   }

   public final Object emit(Unit var1, Continuation var2) {
      MutableStateFlow var3 = TicketsScreenViewModel.access$get_openState$p(this.this$0);
      int var4 = 0;
      boolean var5 = false;
      MutableStateFlow var6 = var3;
      int var7 = 0;

      Object var8;
      boolean var20;
      Object var22;
      do {
         var8 = var6.getValue();
         Object var9 = var8;
         int var10 = 0;
         NetworkResponse var11 = (NetworkResponse)var9;
         int var12 = 0;
         var var13 = CollectionsKt.listOf(var11);
         int var14 = 0;
         boolean var10000;
         if (var13 instanceof Collection && ((Collection)var13).isEmpty()) {
            var10000 = false;
         } else {
            label56: {
               for (Object var16 : var13) {
                  NetworkResponse var17 = (NetworkResponse)var16;
                  int var18 = 0;
                  if (var17 != null ? NetworkResponseKt.isUserJwtExpired(var17) : false) {
                     var10000 = true;
                     break label56;
                  }
               }

               var10000 = false;
            }
         }

         var20 = var10000;
         Object var23;
         if (var20) {
            NetworkResponse itx = (NetworkResponse)var9;
            int var19 = 0;
            var23 = null;
         } else {
            var23 = var9;
         }

         var22 = var23;
      } while (!var6.compareAndSet(var8, var22));

      if (var20) {
         BuildersKt.launch$default(
            this.$$this$launch, TicketsScreenViewModel.access$getDispatcher$p(this.this$0), null, new TicketsScreenViewModel$4$1$3(this.this$0, null), 2, null
         );
         return Unit.INSTANCE;
      } else {
         Object var24 = TicketsScreenViewModel.access$get_effect$p(this.this$0).emit(io.intercom.android.sdk.tickets.list.ui.TicketsScreenEffects$RefreshTickets.INSTANCE, var2);
         return var24 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var24 : Unit.INSTANCE;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Unit)var1, var2);
   }
}
