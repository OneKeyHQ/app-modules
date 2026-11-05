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
   l = 409,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationKt$SoundWaveVisualization$3$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class SoundWaveVisualizationKt$SoundWaveVisualization$3$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final long $elementGenerationIntervalMs;
   // $VF: synthetic field
   final WaveformBuffer $waveformBuffer;
   // $VF: synthetic field
   final MutableFloatState $canvasWidth$delegate;
   // $VF: synthetic field
   final MutableLongState $lastElementGenerationTime$delegate;
   // $VF: synthetic field
   final MutableFloatState $smoothedAmplitude$delegate;

   SoundWaveVisualizationKt$SoundWaveVisualization$3$1(
      long var1, WaveformBuffer var3, MutableFloatState var4, MutableLongState var5, MutableFloatState var6, Continuation var7
   ) {
      super(2, var7);
      this.$elementGenerationIntervalMs = var1;
      this.$waveformBuffer = var3;
      this.$canvasWidth$delegate = var4;
      this.$lastElementGenerationTime$delegate = var5;
      this.$smoothedAmplitude$delegate = var6;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
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
         long var2 = System.currentTimeMillis();
         if (SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$9(this.$canvasWidth$delegate) > 0.0F
            && (
               SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$12(this.$lastElementGenerationTime$delegate) == 0L
                  || var2 - SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$12(this.$lastElementGenerationTime$delegate)
                     >= this.$elementGenerationIntervalMs
            )) {
            float var4 = SoundWaveVisualizationKt.access$scaleAmplitudeForVisualization(
               SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$19(this.$smoothedAmplitude$delegate)
            );
            this.$waveformBuffer.addElement(new WaveformElement(0.0F, var4, var2));
            SoundWaveVisualizationKt.access$SoundWaveVisualization_FNF3uiM$lambda$13(this.$lastElementGenerationTime$delegate, var2);
         }

         var10000 = this.$elementGenerationIntervalMs;
         var10001 = this;
         this.label = 1;
      } while (DelayKt.delay(var10000, var10001) != var5);

      return var5;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new SoundWaveVisualizationKt$SoundWaveVisualization$3$1(
         this.$elementGenerationIntervalMs,
         this.$waveformBuffer,
         this.$canvasWidth$delegate,
         this.$lastElementGenerationTime$delegate,
         this.$smoothedAmplitude$delegate,
         var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((SoundWaveVisualizationKt$SoundWaveVisualization$3$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
