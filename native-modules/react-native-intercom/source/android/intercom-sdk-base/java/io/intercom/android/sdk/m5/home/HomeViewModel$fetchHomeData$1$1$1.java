package io.intercom.android.sdk.m5.home;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;

@DebugMetadata(
   f = "HomeViewModel.kt",
   l = {142, 142},
   i = 0,
   s = "L$0",
   n = "openDeferred",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.home.HomeViewModel$fetchHomeData$1$1$1"
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
final class HomeViewModel$fetchHomeData$1$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$fetchHomeData$1$1$1(HomeViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Deferred var4;
      Object var7;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            Deferred var3 = BuildersKt.async$default(var2, null, null, new HomeViewModel$fetchHomeData$1$1$1$homeDeferred$1(this.this$0, null), 3, null);
            var4 = BuildersKt.async$default(var2, null, null, new HomeViewModel$fetchHomeData$1$1$1$openDeferred$1(this.this$0, null), 3, null);
            Continuation var10001 = this;
            this.L$0 = var4;
            this.label = 1;
            var7 = var3.await(var10001);
            if (var7 == var6) {
               return var6;
            }
            break;
         case 1:
            var4 = (Deferred)this.L$0;
            ResultKt.throwOnFailure(var1);
            var7 = var1;
            break;
         case 2:
            Object var5 = this.L$0;
            ResultKt.throwOnFailure(var1);
            return TuplesKt.to(var5, var1);
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Object var9 = var7;
      Continuation var8 = this;
      this.L$0 = var9;
      this.label = 2;
      var7 = var4.await(var8);
      return var7 == var6 ? var6 : TuplesKt.to(var9, var7);
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      HomeViewModel$fetchHomeData$1$1$1 var3 = new HomeViewModel$fetchHomeData$1$1$1(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HomeViewModel$fetchHomeData$1$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
