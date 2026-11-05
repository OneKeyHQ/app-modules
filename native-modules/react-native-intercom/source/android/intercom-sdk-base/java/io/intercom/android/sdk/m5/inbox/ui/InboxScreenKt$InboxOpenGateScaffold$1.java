package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxOpenGateScaffold$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onBackButtonClick;
   // $VF: synthetic field
   final int $navIcon;

   InboxScreenKt$InboxOpenGateScaffold$1(Function0 var1, int var2) {
      this.$onBackButtonClick = var1;
      this.$navIcon = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C108@4360L58,111@4552L6,112@4612L6,107@4322L320:InboxScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-152365511, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxOpenGateScaffold.<anonymous> (InboxScreen.kt:107)");
         }

         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            StringResources_androidKt.stringResource(string.intercom_messages_space_title, var1, 0),
            null,
            null,
            null,
            this.$onBackButtonClick,
            this.$navIcon,
            false,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4(),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5(),
            0L,
            null,
            null,
            var1,
            0,
            0,
            7325
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
