package io.intercom.android.sdk.m5.helpcenter;

import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "HelpCenterViewModel.kt",
   l = 106,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$2$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "it", "Lio/intercom/android/sdk/identity/AppConfig;"}
)
final class HelpCenterViewModel$2$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HelpCenterViewModel this$0;

   HelpCenterViewModel$2$1(HelpCenterViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            HelpCenterViewModel var10000 = this.this$0;
            Continuation var10001 = this;
            this.label = 1;
            if (HelpCenterViewModel.access$onNewConfig(var10000, var10001) == var2) {
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
      return new HelpCenterViewModel$2$1(this.this$0, var2);
   }

   public final Object invoke(AppConfig var1, Continuation var2) {
      return ((HelpCenterViewModel$2$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((AppConfig)var1, (Continuation)var2);
   }
}
