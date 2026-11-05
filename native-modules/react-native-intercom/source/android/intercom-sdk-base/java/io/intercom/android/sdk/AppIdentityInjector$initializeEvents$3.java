package io.intercom.android.sdk;

import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.utilities.AblyClientUtilsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "AppIdentityInjector.kt",
   l = 58,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.AppIdentityInjector$initializeEvents$3"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class AppIdentityInjector$initializeEvents$3 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final AppIdentityInjector this$0;

   AppIdentityInjector$initializeEvents$3(AppIdentityInjector var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            IntercomDataLayer var10000 = AppIdentityInjector.access$getInjector(this.this$0).getDataLayer();
            Intrinsics.checkNotNullExpressionValue(var10000, "getDataLayer(...)");
            AblyManager var10001 = this.this$0.getAblyManager();
            Continuation var10002 = this;
            this.label = 1;
            if (AblyClientUtilsKt.handleAblyLifecycle(var10000, var10001, var10002) == var2) {
               return var2;
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
      return new AppIdentityInjector$initializeEvents$3(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AppIdentityInjector$initializeEvents$3)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
