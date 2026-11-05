package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TextButton");
      ComposerKt.sourceInformation(var2, "C209@9146L45,210@9231L10,208@9117L202:CreateTicketContentScreen.kt#xh6pmz");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1125060472,
               var3,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.lambda-1.<anonymous> (CreateTicketContentScreen.kt:208)"
            );
         }

         String var4 = StringResources_androidKt.stringResource(string.intercom_cancel, var2, 0);
         TextStyle var5 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04Point5();
         FontWeight var6 = FontWeight.Companion.getSemiBold();
         TextKt.recovered$5b114ab32a4f4190(var4, null, 0L, null, 0L, null, var6, null, 0L, null, null, 0L, 0, false, 0, 0, null, var5, var2, 1572864, 0, 131006);
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
