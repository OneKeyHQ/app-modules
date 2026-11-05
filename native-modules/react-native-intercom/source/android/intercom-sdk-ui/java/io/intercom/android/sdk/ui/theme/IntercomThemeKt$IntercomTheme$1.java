package io.intercom.android.sdk.ui.theme;

import androidx.compose.material3.ColorScheme;
import androidx.compose.material3.MaterialThemeKt;
import androidx.compose.material3.Shapes;
import androidx.compose.material3.Typography;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomThemeKt$IntercomTheme$1 implements Function2 {
   // $VF: synthetic field
   final IntercomColors $intercomColors;
   // $VF: synthetic field
   final IntercomTypography $typography;
   // $VF: synthetic field
   final Shapes $shapes;
   // $VF: synthetic field
   final Function2 $content;

   IntercomThemeKt$IntercomTheme$1(IntercomColors var1, IntercomTypography var2, Shapes var3, Function2 var4) {
      this.$intercomColors = var1;
      this.$typography = var2;
      this.$shapes = var3;
      this.$content = var4;
   }

   @Composable
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C35@1455L205:IntercomTheme.kt#o58l0d");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1689661311, var2, -1, "io.intercom.android.sdk.ui.theme.IntercomTheme.<anonymous> (IntercomTheme.kt:35)");
         }

         ColorScheme var3 = IntercomColorsKt.toMaterialColors(this.$intercomColors);
         Typography var4 = IntercomTypographyKt.toMaterialTypography(this.$typography);
         MaterialThemeKt.MaterialTheme(var3, this.$shapes, var4, this.$content, var1, 0, 0);
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
