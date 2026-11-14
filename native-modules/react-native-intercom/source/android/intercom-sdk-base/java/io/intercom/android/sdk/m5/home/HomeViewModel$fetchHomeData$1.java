package io.intercom.android.sdk.m5.home;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeRecentConversationData;
import io.intercom.android.sdk.m5.home.data.HomeV2Response;
import io.intercom.android.sdk.m5.home.states.HomeClientState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "HomeViewModel.kt",
   l = 138,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.home.HomeViewModel$fetchHomeData$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nHomeViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$fetchHomeData$1\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,214:1\n230#2,5:215\n808#3,11:220\n2756#3:231\n1#4:232\n*S KotlinDebug\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$fetchHomeData$1\n*L\n145#1:215,5\n154#1:220,11\n155#1:231\n155#1:232\n*E\n"
)
final class HomeViewModel$fetchHomeData$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$fetchHomeData$1(HomeViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var16 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineContext var30 = HomeViewModel.access$getDispatcher$p(this.this$0);
            Function2 var10001 = new HomeViewModel$fetchHomeData$1$1(this.this$0, null);
            Continuation var10002 = this;
            this.label = 1;
            var10000 = BuildersKt.withContext(var30, var10001, var10002);
            if (var10000 == var16) {
               return var16;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Pair var2 = (Pair)var10000;
      NetworkResponse var3 = (NetworkResponse)var2.component1();
      NetworkResponse var4 = (NetworkResponse)var2.component2();
      MutableStateFlow var5 = HomeViewModel.access$getClientState$p(this.this$0);
      int var6 = 0;

      Object var7;
      Object var10;
      do {
         var7 = var5.getValue();
         HomeClientState var8 = (HomeClientState)var7;
         int var9 = 0;
         var10 = HomeClientState.copy$default(var8, var3, var4, false, 4, null);
      } while (!var5.compareAndSet(var7, var10));

      if (var3 instanceof NetworkResponse$Success) {
         Iterable var17 = ((HomeV2Response)((NetworkResponse$Success)var3).getBody()).getCards();
         var6 = 0;
         Iterable var21 = var17;
         ArrayList var23 = new ArrayList();
         int var25 = 0;

         for (Object var11 : var21) {
            if (var11 instanceof HomeCards$HomeRecentConversationData) {
               var23.add(var11);
            }
         }

         var var18 = (Iterable & List)var23;
         HomeViewModel var20 = this.this$0;
         int var22 = 0;
         Iterable var24 = var18;
         Iterable var26 = var24;
         var10 = 0;

         for (Object var12 : var26) {
            HomeCards$HomeRecentConversationData var13 = (HomeCards$HomeRecentConversationData)var12;
            int var14 = 0;
            List var15 = var13.getConversations();
            HomeViewModel.access$getIntercomDataLayer$p(var20).addConversations(var15);
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HomeViewModel$fetchHomeData$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HomeViewModel$fetchHomeData$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
