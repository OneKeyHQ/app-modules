package io.intercom.android.sdk.m5.home;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.home.states.HomeClientState;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects$NavigateToConversation;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects$NavigateToNewConversation;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "HomeViewModel.kt",
   l = {105, 119},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.home.HomeViewModel$handleOpening$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nHomeViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$handleOpening$2\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,214:1\n230#2,5:215\n*S KotlinDebug\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel$handleOpening$2\n*L\n108#1:215,5\n*E\n"
)
final class HomeViewModel$handleOpening$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$handleOpening$2(HomeViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CommonRepository var10 = HomeViewModel.access$getCommonRepository$p(this.this$0);
            Continuation var10001 = this;
            this.label = 1;
            var10000 = var10.openMessenger(var10001);
            if (var10000 == var8) {
               return var8;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      if (!(var2 instanceof NetworkResponse$Success)) {
         MutableStateFlow var3 = HomeViewModel.access$getClientState$p(this.this$0);
         int var4 = 0;

         Object var5;
         HomeClientState var9;
         do {
            var5 = var3.getValue();
            var9 = (HomeClientState)var5;
            int var7 = 0;
            var9 = HomeClientState.copy$default((HomeClientState)var9, null, var2, false, 5, null);
         } while (!var3.compareAndSet(var5, var9));

         HomeViewModel.access$fetchHomeData(this.this$0);
         return Unit.INSTANCE;
      } else {
         HomeViewModel.access$fetchHomeData(this.this$0);
         MutableSharedFlow var11 = HomeViewModel.access$get_effect$p(this.this$0);
         HomeUiEffects var12 = !((OpenMessengerResponse)((NetworkResponse$Success)var2).getBody()).getOpenInboundConversations().isEmpty()
            ? new HomeUiEffects$NavigateToConversation(
               (String)CollectionsKt.first(((OpenMessengerResponse)((NetworkResponse$Success)var2).getBody()).getOpenInboundConversations())
            )
            : HomeUiEffects$NavigateToNewConversation.INSTANCE;
         Continuation var10002 = this;
         this.label = 2;
         return var11.emit(var12, var10002) == var8 ? var8 : Unit.INSTANCE;
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HomeViewModel$handleOpening$2(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HomeViewModel$handleOpening$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
