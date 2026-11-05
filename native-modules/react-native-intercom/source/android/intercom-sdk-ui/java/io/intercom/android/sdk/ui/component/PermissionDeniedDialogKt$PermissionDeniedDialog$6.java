package io.intercom.android.sdk.ui.component;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.TextStyle;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PermissionDeniedDialogKt$PermissionDeniedDialog$6 implements Function2 {
   // $VF: synthetic field
   final String $message;

   PermissionDeniedDialogKt$PermissionDeniedDialog$6(String var1) {
      this.$message = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C42@1406L10,43@1463L6,40@1330L167:PermissionDeniedDialog.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               40178371, var2, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog.<anonymous> (PermissionDeniedDialog.kt:40)"
            );
         }

         TextStyle var3 = IntercomTheme.INSTANCE.getTypography(var1, 6).getType04();
         long var4 = IntercomTheme.INSTANCE.getColors(var1, 6).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            this.$message, null, var4, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var3, var1, 0, 0, 131066
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
