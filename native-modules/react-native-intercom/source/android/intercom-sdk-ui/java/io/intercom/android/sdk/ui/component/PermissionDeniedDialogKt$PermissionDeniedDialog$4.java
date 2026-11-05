package io.intercom.android.sdk.ui.component;

import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PermissionDeniedDialogKt$PermissionDeniedDialog$4 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDismiss;
   // $VF: synthetic field
   final String $dismissText;

   PermissionDeniedDialogKt$PermissionDeniedDialog$4(Function0 var1, String var2) {
      this.$onDismiss = var1;
      this.$dismissText = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C61@1995L234,59@1932L297:PermissionDeniedDialog.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -868483802, var2, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog.<anonymous> (PermissionDeniedDialog.kt:59)"
            );
         }

         ButtonKt.TextButton(
            this.$onDismiss,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1570315255, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$4$1(this.$dismissText), var1, 54),
            var1,
            805306368,
            510
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
