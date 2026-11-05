package io.intercom.android.sdk;

import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApi$DefaultImpls;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "AblyManager.kt",
   l = 258,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.AblyManager$createAuthCallback$1$authResponse$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n",
   d2 = {
         "<anonymous>",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/AblyAuthResponse;",
         "Lkotlinx/coroutines/CoroutineScope;"
   }
)
final class AblyManager$createAuthCallback$1$authResponse$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final AblyManager this$0;

   AblyManager$createAuthCallback$1$authResponse$1(AblyManager var1, Continuation var2) {
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
            MessengerApi recoveredAuthApi = AblyManager.access$getMessengerApi$p(this.this$0);
            Continuation var10002 = this;
            this.label = 1;
            var10000 = MessengerApi$DefaultImpls.getAblyAuthTokenSuspend$default(recoveredAuthApi, null, var10002, 1, null);
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
      return new AblyManager$createAuthCallback$1$authResponse$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AblyManager$createAuthCallback$1$authResponse$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
