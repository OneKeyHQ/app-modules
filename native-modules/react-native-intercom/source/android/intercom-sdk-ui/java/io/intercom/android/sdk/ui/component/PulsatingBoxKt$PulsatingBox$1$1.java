package io.intercom.android.sdk.ui.component;

import androidx.compose.animation.core.Animatable;
import androidx.compose.animation.core.AnimationSpec;
import androidx.compose.animation.core.AnimationSpecKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "PulsatingBox.kt",
   l = 36,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.ui.component.PulsatingBoxKt$PulsatingBox$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PulsatingBoxKt$PulsatingBox$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Animatable $animatedScale;
   // $VF: synthetic field
   final float $pulseSize;

   PulsatingBoxKt$PulsatingBox$1$1(Animatable var1, float var2, Continuation var3) {
      super(2, var3);
      this.$animatedScale = var1;
      this.$pulseSize = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Animatable var10000 = this.$animatedScale;
            Float var10001 = Boxing.boxFloat(this.$pulseSize);
            AnimationSpec var10002 = AnimationSpecKt.tween$default(100, 0, null, 6, null);
            Continuation var10005 = this;
            this.label = 1;
            if (Animatable.animateTo$default(var10000, var10001, var10002, null, null, var10005, 12, null) == var2) {
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
      return new PulsatingBoxKt$PulsatingBox$1$1(this.$animatedScale, this.$pulseSize, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PulsatingBoxKt$PulsatingBox$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
