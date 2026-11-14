package io.intercom.android.sdk.m5.data;

import io.intercom.android.sdk.models.OverlayState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "IntercomDataLayer.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.data.IntercomDataLayer$overlayStateUpdates$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "it", "Lio/intercom/android/sdk/models/OverlayState;"}
)
final class IntercomDataLayer$overlayStateUpdates$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   final Function1 $onNewOverlyState;

   IntercomDataLayer$overlayStateUpdates$1$1(Function1 var1, Continuation var2) {
      super(2, var2);
      this.$onNewOverlyState = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            OverlayState var2 = (OverlayState)this.L$0;
            this.$onNewOverlyState.invoke(var2);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      IntercomDataLayer$overlayStateUpdates$1$1 var3 = new IntercomDataLayer$overlayStateUpdates$1$1(this.$onNewOverlyState, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(OverlayState var1, Continuation var2) {
      return ((IntercomDataLayer$overlayStateUpdates$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((OverlayState)var1, (Continuation)var2);
   }
}
