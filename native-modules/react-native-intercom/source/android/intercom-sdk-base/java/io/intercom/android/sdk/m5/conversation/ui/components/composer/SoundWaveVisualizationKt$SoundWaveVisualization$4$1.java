package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableLongState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "SoundWaveVisualization.kt",
   l = 427,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationKt$SoundWaveVisualization$4$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SoundWaveVisualizationKt$SoundWaveVisualization$4$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final float $elementSpacingPx;
   // $VF: synthetic field
   final float $animationSpeedPxPerMs;
   // $VF: synthetic field
   final WaveformBuffer $waveformBuffer;
   // $VF: synthetic field
   final long $animationFrameRateMs;
   // $VF: synthetic field
   final MutableFloatState $canvasWidth$delegate;
   // $VF: synthetic field
   final MutableLongState $animationFrameCounter$delegate;

   SoundWaveVisualizationKt$SoundWaveVisualization$4$1(
      float var1, float var2, WaveformBuffer var3, long var4, MutableFloatState var6, MutableLongState var7, Continuation var8
   ) {
      super(2, var8);
      this.$elementSpacingPx = var1;
      this.$animationSpeedPxPerMs = var2;
      this.$waveformBuffer = var3;
      this.$animationFrameRateMs = var4;
      this.$canvasWidth$delegate = var6;
      this.$animationFrameCounter$delegate = var7;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      long var10000;
      Continuation var10001;
      do {
         if (SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$9(this.$canvasWidth$delegate) > 0.0F) {
            long var2 = System.currentTimeMillis();
            long var4 = (long)(
               (SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$9(this.$canvasWidth$delegate) + this.$elementSpacingPx)
                  / this.$animationSpeedPxPerMs
            );
            this.$waveformBuffer.cleanupOldElements(var2, var4);
            SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$16(this.$animationFrameCounter$delegate, var2);
         }

         var10000 = this.$animationFrameRateMs;
         var10001 = this;
         this.label = 1;
      } while (DelayKt.delay(var10000, var10001) != var6);

      return var6;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SoundWaveVisualizationKt$SoundWaveVisualization$4$1(
         this.$elementSpacingPx,
         this.$animationSpeedPxPerMs,
         this.$waveformBuffer,
         this.$animationFrameRateMs,
         this.$canvasWidth$delegate,
         this.$animationFrameCounter$delegate,
         var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SoundWaveVisualizationKt$SoundWaveVisualization$4$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
