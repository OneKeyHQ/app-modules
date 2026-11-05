package io.intercom.android.sdk.ui.component;

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
   "SMAP\nIntercomTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/IntercomTopBarKt$IntercomTopBar$2$2$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,160:1\n118#2:161\n*S KotlinDebug\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/IntercomTopBarKt$IntercomTopBar$2$2$2\n*L\n88#1:161\n*E\n"
)
final class IntercomTopBarKt$IntercomTopBar$2$2$2 implements Function2 {
   // $VF: synthetic field
   final IntercomTopBarIcon $navIcon;
   // $VF: synthetic field
   final long $contentColor;

   IntercomTopBarKt$IntercomTopBar$2$2$2(IntercomTopBarIcon var1, long var2) {
      this.$navIcon = var1;
      this.$contentColor = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C84@3334L37,83@3294L275:IntercomTopBar.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -423583748, var2, -1, "io.intercom.android.sdk.ui.component.IntercomTopBar.<anonymous>.<anonymous>.<anonymous> (IntercomTopBar.kt:83)"
            );
         }

         Painter var3 = PainterResources_androidKt.painterResource(this.$navIcon.getIconRes(), var1, 0);
         String var4 = this.$navIcon.getContentDescription();
         Modifier var10000 = Modifier.Companion;
         int var6 = 24;
         int var7 = 0;
         Modifier var5 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var6));
         IconKt.recovered$e00adffb7fe45b02(var3, var4, var5, this.$contentColor, var1, 384 | Painter.$stable, 0);
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
