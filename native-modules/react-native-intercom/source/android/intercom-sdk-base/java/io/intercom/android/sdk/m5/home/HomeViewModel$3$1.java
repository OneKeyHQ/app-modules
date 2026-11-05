package io.intercom.android.sdk.m5.home;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.m5.home.states.HomeClientState;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$3$1\n+ 2 UserJwtExpiredReset.kt\nio/intercom/android/sdk/m5/data/UserJwtExpiredResetKt\n+ 3 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,214:1\n12#2,2:215\n14#2:220\n15#2:224\n17#2:227\n230#3,3:217\n233#3,2:225\n1761#4,3:221\n*S KotlinDebug\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$3$1\n*L\n86#1:215,2\n86#1:220\n86#1:224\n86#1:227\n86#1:217,3\n86#1:225,2\n86#1:221,3\n*E\n"
)
final class HomeViewModel$3$1 implements FlowCollector {
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$3$1(HomeViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(Unit var1, Continuation var2) {
      MutableStateFlow var4 = HomeViewModel.access$getClientState$p(this.this$0);
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
         HomeClientState var12 = (HomeClientState)var10;
         int var13 = 0;
         NetworkResponse[] var14 = new NetworkResponse[]{var12.getOpenMessengerResponse(), var12.getHomeResponse()};
         var var15 = CollectionsKt.listOf(var14);
         int var16 = 0;
         boolean var10000;
         if (var15 instanceof Collection && ((Collection)var15).isEmpty()) {
            var10000 = false;
         } else {
            label51: {
               for (Object var18 : var15) {
                  NetworkResponse var19 = (NetworkResponse)var18;
                  int var20 = 0;
                  if (var19 != null ? NetworkResponseKt.isUserJwtExpired(var19) : false) {
                     var10000 = true;
                     break label51;
                  }
               }

               var10000 = false;
            }
         }

         var21 = var10000;
         Object var25;
         if (var21) {
            HomeClientState itx = (HomeClientState)var10;
            int var24 = 0;
            var25 = HomeClientState.copy$default(itx, null, null, false, 4, null);
         } else {
            var25 = var10;
         }

         var23 = var25;
      } while (!var7.compareAndSet(var9, var23));

      boolean var3 = var21;
      if (var3) {
         HomeViewModel.access$fetchHomeData(this.this$0);
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Unit)var1, var2);
   }
}
