package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSoundWaveVisualization.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-3$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,591:1\n118#2:592\n*S KotlinDebug\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-3$1$1\n*L\n586#1:592\n*E\n"
)
final class ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1$1 implements Function2 {
   // $VF: synthetic field
   final MutableStateFlow $amplitudeFlow;

   ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1$1(MutableStateFlow var1) {
      this.$amplitudeFlow = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C581@23156L194:SoundWaveVisualization.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -328642071,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.lambda-3.<anonymous>.<anonymous> (SoundWaveVisualization.kt:581)"
            );
         }

         StateFlow var10000 = this.$amplitudeFlow;
         Modifier var10001 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var3 = 16;
         int var4 = 0;
         SoundWaveVisualizationKt.recovered$92b4091f81df4daa(
            var10000, PaddingKt.recovered$eb6a744cd98cdbca(var10001, Dp.recovered$95b3d103663bf060(var3)), 0L, var1, 48, 4
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
