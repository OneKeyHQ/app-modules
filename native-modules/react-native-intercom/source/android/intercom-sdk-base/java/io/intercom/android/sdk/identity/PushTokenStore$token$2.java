package io.intercom.android.sdk.identity;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;

@DebugMetadata(f = "PushTokenStore.kt", l = 40, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.identity.PushTokenStore$token$2")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48, d1 = "\u0000\u0006\n\u0000\n\u0002\u0010\u000e\u0010\u0000\u001a\u00020\u0001H\n", d2 = {"<anonymous>", ""})
final class PushTokenStore$token$2 extends SuspendLambda implements Function1 {
   int label;
   // $VF: synthetic field
   final PushTokenStore this$0;

   PushTokenStore$token$2(PushTokenStore var1, Continuation var2) {
      super(1, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            PushTokenStore recoveredTokenStore = this.this$0;
            Continuation var10001 = this;
            this.label = 1;
            var10000 = PushTokenStore.access$loadPushToken(recoveredTokenStore, var10001);
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
   public final Continuation create(Continuation var1) {
      return new PushTokenStore$token$2(this.this$0, var1);
   }

   public final Object invoke(Continuation var1) {
      return ((PushTokenStore$token$2)this.create(var1)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke((Continuation)var1);
   }
}
