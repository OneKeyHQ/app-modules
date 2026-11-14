package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.MutableFloatState;
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
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationKt$SoundWaveVisualization$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SoundWaveVisualizationKt$SoundWaveVisualization$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final float $elementSpacingPx;
   // $VF: synthetic field
   final WaveformBuffer $waveformBuffer;
   // $VF: synthetic field
   final MutableFloatState $canvasWidth$delegate;

   SoundWaveVisualizationKt$SoundWaveVisualization$1$1(float var1, WaveformBuffer var2, MutableFloatState var3, Continuation var4) {
      super(2, var4);
      this.$elementSpacingPx = var1;
      this.$waveformBuffer = var2;
      this.$canvasWidth$delegate = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$9(this.$canvasWidth$delegate) > 0.0F) {
               int var2 = SoundWaveVisualizationKt.calculateOptimalBufferSize(
                  SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$9(this.$canvasWidth$delegate), this.$elementSpacingPx
               );
               this.$waveformBuffer.resizeBuffer(var2);
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SoundWaveVisualizationKt$SoundWaveVisualization$1$1(this.$elementSpacingPx, this.$waveformBuffer, this.$canvasWidth$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SoundWaveVisualizationKt$SoundWaveVisualization$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
