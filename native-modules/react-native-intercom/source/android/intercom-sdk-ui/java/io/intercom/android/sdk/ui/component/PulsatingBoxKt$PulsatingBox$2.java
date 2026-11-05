package io.intercom.android.sdk.ui.component;

import androidx.compose.animation.core.Animatable;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ScaleKt;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPulsatingBox.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/PulsatingBoxKt$PulsatingBox$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,79:1\n118#2:80\n*S KotlinDebug\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/PulsatingBoxKt$PulsatingBox$2\n*L\n52#1:80\n*E\n"
)
final class PulsatingBoxKt$PulsatingBox$2 implements Function3 {
   // $VF: synthetic field
   final boolean $enabled;
   // $VF: synthetic field
   final Animatable $animatedScale;
   // $VF: synthetic field
   final Shape $pulseShape;
   // $VF: synthetic field
   final long $pulseColor;
   // $VF: synthetic field
   final Function3 $content;

   PulsatingBoxKt$PulsatingBox$2(boolean var1, Animatable var2, Shape var3, long var4, Function3 var6) {
      this.$enabled = var1;
      this.$animatedScale = var2;
      this.$pulseShape = var3;
      this.$pulseColor = var4;
      this.$content = var6;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C58@2026L9:PulsatingBox.kt#yr602f");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(526284572, var4, -1, "io.intercom.android.sdk.ui.component.PulsatingBox.<anonymous> (PulsatingBox.kt:45)");
         }

         var2.startReplaceGroup(1154915079);
         ComposerKt.sourceInformation(var2, "46@1619L388");
         if (this.$enabled) {
            Modifier var10001 = ScaleKt.scale(
               SizeKt.recovered$dd5685bd56c7f636(Modifier.Companion, var1.recovered$4e84a3b22c31a649(), var1.recovered$1760a9074021b1b8()),
               ((Number)this.$animatedScale.getValue()).floatValue()
            );
            int var5 = 2;
            int var6 = 0;
            BoxKt.Box(
               var1.align(
                  BackgroundKt.recovered$60bc47fb440df483(
                     ShadowKt.recovered$04904c26f7ded95f(var10001, Dp.recovered$95b3d103663bf060(var5), this.$pulseShape, false, 0L, 0L, 28, null),
                     this.$pulseColor,
                     this.$pulseShape
                  ),
                  Alignment.Companion.getCenter()
               ),
               var2,
               0
            );
         }

         var2.endReplaceGroup();
         this.$content.invoke(var1, var2, 14 & var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
