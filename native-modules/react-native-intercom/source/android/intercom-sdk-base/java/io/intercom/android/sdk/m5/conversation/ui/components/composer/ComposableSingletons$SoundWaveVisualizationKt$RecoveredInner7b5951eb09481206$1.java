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
import kotlinx.coroutines.flow.StateFlowKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSoundWaveVisualization.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,591:1\n1047#2,6:592\n118#3:598\n*S KotlinDebug\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-1$1\n*L\n552#1:592,6\n557#1:598\n*E\n"
)
final class ComposableSingletons$SoundWaveVisualizationKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$SoundWaveVisualizationKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$SoundWaveVisualizationKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C551@22302L35,552@22346L174:SoundWaveVisualization.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1716228299,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.lambda-1.<anonymous> (SoundWaveVisualization.kt:551)"
            );
         }

         var1.startReplaceGroup(362739070);
         ComposerKt.sourceInformation(var1, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10000;
         if (var8 == Composer.Companion.getEmpty()) {
            int var10 = 0;
            Object var13 = StateFlowKt.MutableStateFlow(0.7F);
            var5.updateRememberedValue(var13);
            var10000 = var13;
         } else {
            var10000 = var8;
         }

         MutableStateFlow var4 = (MutableStateFlow)var10000;
         var1.endReplaceGroup();
         MutableStateFlow var3 = var4;
         StateFlow var14 = var3;
         Modifier var10001 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var11 = 16;
         int var12 = 0;
         SoundWaveVisualizationKt.recovered$92b4091f81df4daa(
            var14, PaddingKt.recovered$eb6a744cd98cdbca(var10001, Dp.recovered$95b3d103663bf060(var11)), 0L, var1, 48, 4
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
