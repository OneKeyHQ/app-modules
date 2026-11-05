package io.intercom.android.sdk.m5.conversation.ui.components;

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
final class ComposableSingletons$DeleteFailedMessageDialogKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$DeleteFailedMessageDialogKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$DeleteFailedMessageDialogKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C20@672L58,21@770L10,22@827L6,19@643L218:DeleteFailedMessageDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               800614910,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$DeleteFailedMessageDialogKt.lambda-3.<anonymous> (DeleteFailedMessageDialog.kt:19)"
            );
         }

         String var3 = StringResources_androidKt.stringResource(string.intercom_start_conversation_error, var1, 0);
         TextStyle var4 = IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType04();
         long var5 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
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
