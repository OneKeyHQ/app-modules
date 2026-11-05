package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterTopBarKt$HelpCenterTopBar$1$1 implements Function2 {
   // $VF: synthetic field
   final long $contentColor;

   HelpCenterTopBarKt$HelpCenterTopBar$1$1(long var1) {
      this.$contentColor = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C32@1214L57,33@1310L59,31@1182L239:HelpCenterTopBar.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1957925771, var2, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBar.<anonymous>.<anonymous> (HelpCenterTopBar.kt:31)"
            );
         }

         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_gif_search_icon, var1, 0),
            StringResources_androidKt.stringResource(string.intercom_search_help_articles, var1, 0),
            null,
            this.$contentColor,
            var1,
            Painter.$stable,
            4
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
