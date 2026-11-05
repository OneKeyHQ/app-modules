package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.TextStyle;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PermissionDeniedDialogKt$PermissionDeniedDialog$3$1 implements Function3 {
   // $VF: synthetic field
   final String $confirmText;

   PermissionDeniedDialogKt$PermissionDeniedDialog$3$1(String var1) {
      this.$confirmText = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TextButton");
      ComposerKt.sourceInformation(var2, "C53@1754L10,54@1823L6,51@1666L202:PermissionDeniedDialog.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2118877259, var3, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog.<anonymous>.<anonymous> (PermissionDeniedDialog.kt:51)"
            );
         }

         TextStyle var4 = IntercomTheme.INSTANCE.getTypography(var2, 6).getType04SemiBold();
         long var5 = IntercomTheme.INSTANCE.getColors(var2, 6).recovered$d5d3a0ddb8f4248d();
         TextKt.recovered$5b114ab32a4f4190(
            this.$confirmText, null, var5, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var4, var2, 0, 0, 131066
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
