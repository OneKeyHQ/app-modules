package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.LayoutDirection;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSoundWaveVisualization.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,591:1\n1047#2,6:592\n*S KotlinDebug\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$SoundWaveVisualizationKt$lambda-3$1\n*L\n580#1:592,6\n*E\n"
)
final class ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C579@23022L35,580@23142L218,580@23066L294:SoundWaveVisualization.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1607955159,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.lambda-3.<anonymous> (SoundWaveVisualization.kt:579)"
            );
         }

         var1.startReplaceGroup(-917101568);
         ComposerKt.sourceInformation(var1, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10000;
         if (var8 == Composer.Companion.getEmpty()) {
            int var10 = 0;
            Object var11 = StateFlowKt.MutableStateFlow(0.7F);
            var5.updateRememberedValue(var11);
            var10000 = var11;
         } else {
            var10000 = var8;
         }

         MutableStateFlow var4 = (MutableStateFlow)var10000;
         var1.endReplaceGroup();
         MutableStateFlow var3 = var4;
         CompositionLocalKt.CompositionLocalProvider(
            CompositionLocalsKt.getLocalLayoutDirection().provides(LayoutDirection.Rtl),
            ComposableLambdaKt.rememberComposableLambda(
               -328642071, true, new ComposableSingletons$SoundWaveVisualizationKt$RecoveredInnerb8048fd051efd9e2$1$1(var3), var1, 54
            ),
            var1,
            48 | ProvidedValue.$stable
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
