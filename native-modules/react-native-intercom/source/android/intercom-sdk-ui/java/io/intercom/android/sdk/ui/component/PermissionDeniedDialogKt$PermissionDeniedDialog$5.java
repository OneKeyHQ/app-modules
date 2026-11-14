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
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPermissionDeniedDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PermissionDeniedDialog.kt\nio/intercom/android/sdk/ui/component/PermissionDeniedDialogKt$PermissionDeniedDialog$5\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,88:1\n118#2:89\n*S KotlinDebug\n*F\n+ 1 PermissionDeniedDialog.kt\nio/intercom/android/sdk/ui/component/PermissionDeniedDialogKt$PermissionDeniedDialog$5\n*L\n36#1:89\n*E\n"
)
final class PermissionDeniedDialogKt$PermissionDeniedDialog$5 implements Function2 {
   // $VF: synthetic field
   final Integer $iconId;

   PermissionDeniedDialogKt$PermissionDeniedDialog$5(Integer var1) {
      this.$iconId = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C*32@1050L24,34@1184L6,31@1024L251:PermissionDeniedDialog.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -565596411, var2, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog.<anonymous> (PermissionDeniedDialog.kt:30)"
            );
         }

         if (this.$iconId != null) {
            int var3 = this.$iconId.intValue();
            int var4 = 0;
            Painter var5 = PainterResources_androidKt.painterResource(var3, var1, 0);
            long var6 = IntercomTheme.INSTANCE.getColors(var1, 6).getIcon().recovered$e01a1d3b7b68e21a();
            Modifier var10000 = Modifier.Companion;
            int var8 = 24;
            int var9 = 0;
            Modifier var10 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var8));
            IconKt.recovered$e00adffb7fe45b02(var5, "", var10, var6, var1, 432 | Painter.$stable, 0);
         }

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
