package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/TopActionBarKt$TopActionBar$3$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,255:1\n118#2:256\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/TopActionBarKt$TopActionBar$3$2$1\n*L\n80#1:256\n*E\n"
)
final class TopActionBarKt$TopActionBar$3$2$1 implements Function2 {
   // $VF: synthetic field
   final Integer $navIcon;
   // $VF: synthetic field
   final long $contentColor;

   TopActionBarKt$TopActionBar$3$2$1(Integer var1, long var2) {
      this.$navIcon = var1;
      this.$contentColor = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C80@3368L29,81@3444L54,78@3271L295:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1893533585, var2, -1, "io.intercom.android.sdk.m5.components.TopActionBar.<anonymous>.<anonymous>.<anonymous> (TopActionBar.kt:78)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 24;
         int var5 = 0;
         Modifier var3 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
         Painter var6 = PainterResources_androidKt.painterResource(this.$navIcon, var1, 0);
         String var7 = StringResources_androidKt.stringResource(string.intercom_navigation_back, var1, 0);
         IconKt.recovered$e00adffb7fe45b02(var6, var7, var3, this.$contentColor, var1, 384 | Painter.$stable, 0);
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
