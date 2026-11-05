package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.RowScope;
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
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$UploadSizeLimitDialogKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$UploadSizeLimitDialogKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$UploadSizeLimitDialogKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TextButton");
      ComposerKt.sourceInformation(var2, "C44@1343L36,45@1423L10,46@1492L6,43@1310L227:UploadSizeLimitDialog.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               133212014,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$UploadSizeLimitDialogKt.lambda-1.<anonymous> (UploadSizeLimitDialog.kt:43)"
            );
         }

         String var4 = StringResources_androidKt.stringResource(string.intercom_ok, var2, 0);
         TextStyle var5 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04SemiBold();
         long var6 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
         TextKt.recovered$5b114ab32a4f4190(var4, null, var6, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var5, var2, 0, 0, 131066);
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
