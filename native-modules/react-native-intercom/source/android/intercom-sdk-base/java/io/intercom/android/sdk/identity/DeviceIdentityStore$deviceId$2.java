package io.intercom.android.sdk.identity;

import android.content.Context;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;

@DebugMetadata(
   f = "DeviceIdentityStore.kt",
   l = 42,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.identity.DeviceIdentityStore$deviceId$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\u0010\u0000\u001a\u00070\u0001¢\u0006\u0002\b\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlin/jvm/internal/EnhancedNullability;"}
)
final class DeviceIdentityStore$deviceId$2 extends SuspendLambda implements Function1 {
   int label;
   // $VF: synthetic field
   final DeviceIdentityStore this$0;
   // $VF: synthetic field
   final Context $context;

   DeviceIdentityStore$deviceId$2(DeviceIdentityStore var1, Context var2, Continuation var3) {
      super(1, var3);
      this.this$0 = var1;
      this.$context = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            DeviceIdentityStore recoveredIdentityStore = this.this$0;
            Context var10001 = this.$context;
            Continuation var10002 = this;
            this.label = 1;
            var10000 = DeviceIdentityStore.access$loadDeviceIdentifier(recoveredIdentityStore, var10001, var10002);
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
      return new DeviceIdentityStore$deviceId$2(this.this$0, this.$context, var1);
   }

   public final Object invoke(Continuation var1) {
      return ((DeviceIdentityStore$deviceId$2)this.create(var1)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke((Continuation)var1);
   }
}
