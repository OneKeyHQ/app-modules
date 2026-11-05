package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.State;
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
   f = "SoundWaveVisualization.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationKt$SoundWaveVisualization$2$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SoundWaveVisualizationKt$SoundWaveVisualization$2$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final DrawingConstants $drawingConstants;
   // $VF: synthetic field
   final State $amplitude$delegate;
   // $VF: synthetic field
   final MutableFloatState $lastRawAmplitude$delegate;
   // $VF: synthetic field
   final MutableFloatState $smoothedAmplitude$delegate;

   SoundWaveVisualizationKt$SoundWaveVisualization$2$1(DrawingConstants var1, State var2, MutableFloatState var3, MutableFloatState var4, Continuation var5) {
      super(2, var5);
      this.$drawingConstants = var1;
      this.$amplitude$delegate = var2;
      this.$lastRawAmplitude$delegate = var3;
      this.$smoothedAmplitude$delegate = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            float var2 = SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$3(this.$amplitude$delegate);
            float var3 = Math.abs(var2 - SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$22(this.$lastRawAmplitude$delegate));
            if (var3 >= this.$drawingConstants.getAmplitudeDeadZone()) {
               SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$20(
                  this.$smoothedAmplitude$delegate,
                  this.$drawingConstants.getAmplitudeSmoothingFactor() * var2
                     + (1.0F - this.$drawingConstants.getAmplitudeSmoothingFactor())
                        * SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$19(this.$smoothedAmplitude$delegate)
               );
               SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$23(this.$lastRawAmplitude$delegate, var2);
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SoundWaveVisualizationKt$SoundWaveVisualization$2$1(
         this.$drawingConstants, this.$amplitude$delegate, this.$lastRawAmplitude$delegate, this.$smoothedAmplitude$delegate, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SoundWaveVisualizationKt$SoundWaveVisualization$2$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
