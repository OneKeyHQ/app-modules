package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$HelpCenterSearchTopBarKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C96@3889L54,97@3991L10,98@4056L6,95@3852L244:HelpCenterSearchTopBar.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -456122237,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.search.ComposableSingletons$HelpCenterSearchTopBarKt.lambda-1.<anonymous> (HelpCenterSearchTopBar.kt:95)"
            );
         }

         String var3 = StringResources_androidKt.stringResource(string.intercom_search_for_help, var1, 0);
         TextStyle var4 = IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType04();
         long var5 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextKt.recovered$5b114ab32a4f4190(var3, null, var5, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var4, var1, 0, 0, 131066);
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
