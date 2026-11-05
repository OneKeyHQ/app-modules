package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C148@6321L51,149@6415L54,150@6512L6,147@6285L265:HelpCenterSearchTopBar.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -420729176,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.search.ComposableSingletons$HelpCenterSearchTopBarKt.lambda-3.<anonymous> (HelpCenterSearchTopBar.kt:147)"
            );
         }

         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_ic_back, var1, 0),
            StringResources_androidKt.stringResource(string.intercom_navigation_back, var1, 0),
            null,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getIcon().recovered$e01a1d3b7b68e21a(),
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
