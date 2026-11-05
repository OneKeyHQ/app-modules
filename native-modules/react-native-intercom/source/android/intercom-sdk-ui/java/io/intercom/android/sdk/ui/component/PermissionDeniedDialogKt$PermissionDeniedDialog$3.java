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
final class PermissionDeniedDialogKt$PermissionDeniedDialog$3 implements Function2 {
   // $VF: synthetic field
   final Function0 $onConfirm;
   // $VF: synthetic field
   final String $confirmText;

   PermissionDeniedDialogKt$PermissionDeniedDialog$3(Function0 var1, String var2) {
      this.$onConfirm = var1;
      this.$confirmText = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C50@1648L234,48@1585L297:PermissionDeniedDialog.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1474258584, var2, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog.<anonymous> (PermissionDeniedDialog.kt:48)"
            );
         }

         ButtonKt.TextButton(
            this.$onConfirm,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(2118877259, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$3$1(this.$confirmText), var1, 54),
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
