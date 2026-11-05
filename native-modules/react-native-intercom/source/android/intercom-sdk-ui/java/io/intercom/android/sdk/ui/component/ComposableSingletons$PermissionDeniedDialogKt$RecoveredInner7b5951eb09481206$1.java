package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$PermissionDeniedDialogKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$PermissionDeniedDialogKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PermissionDeniedDialogKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C79@2438L276:PermissionDeniedDialog.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -593234401,
               var2,
               -1,
               "io.intercom.android.sdk.ui.component.ComposableSingletons$PermissionDeniedDialogKt.lambda-1.<anonymous> (PermissionDeniedDialog.kt:79)"
            );
         }

         PermissionDeniedDialogKt.PermissionDeniedDialog(
            null,
            "Audio permission is required to use voice input. Please enable it in the app settings.",
            "Settings",
            "Not now",
            drawable.intercom_ic_microphone,
            null,
            null,
            var1,
            3504,
            97
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
