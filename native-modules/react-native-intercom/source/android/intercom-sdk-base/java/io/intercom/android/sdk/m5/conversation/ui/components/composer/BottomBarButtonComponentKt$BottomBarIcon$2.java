package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBottomBarButtonComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BottomBarButtonComponent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/BottomBarButtonComponentKt$BottomBarIcon$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n118#2:129\n*S KotlinDebug\n*F\n+ 1 BottomBarButtonComponent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/BottomBarButtonComponentKt$BottomBarIcon$2\n*L\n77#1:129\n*E\n"
)
final class BottomBarButtonComponentKt$BottomBarIcon$2 implements Function2 {
   // $VF: synthetic field
   final int $icon;

   BottomBarButtonComponentKt$BottomBarIcon$2(int var1) {
      this.$icon = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C77@2672L26,75@2599L149:BottomBarButtonComponent.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2035069272,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarIcon.<anonymous> (BottomBarButtonComponent.kt:75)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 20;
         int var5 = 0;
         Modifier var3 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
         IconKt.recovered$e00adffb7fe45b02(PainterResources_androidKt.painterResource(this.$icon, var1, 0), null, var3, 0L, var1, 432 | Painter.$stable, 8);
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
