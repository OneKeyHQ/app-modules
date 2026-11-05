package io.intercom.android.sdk.m5.home;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;

@DebugMetadata(
   f = "HomeViewModel.kt",
   l = 139,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.home.HomeViewModel$fetchHomeData$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00020\u0001*\u00020\u0005H\n",
   d2 = {
         "<anonymous>",
         "Lkotlin/Pair;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/m5/home/data/HomeV2Response;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "Lkotlinx/coroutines/CoroutineScope;"
   }
)
final class HomeViewModel$fetchHomeData$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$fetchHomeData$1$1(HomeViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Function2 recoveredHomeTask = new HomeViewModel$fetchHomeData$1$1$1(this.this$0, null);
            Continuation var10001 = this;
            this.label = 1;
            var10000 = CoroutineScopeKt.coroutineScope(recoveredHomeTask, var10001);
            if (var10000 == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return var10000;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HomeViewModel$fetchHomeData$1$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HomeViewModel$fetchHomeData$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
