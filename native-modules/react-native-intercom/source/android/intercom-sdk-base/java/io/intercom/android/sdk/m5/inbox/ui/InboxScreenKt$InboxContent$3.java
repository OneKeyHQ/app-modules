package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxContent$3 implements Function2 {
   // $VF: synthetic field
   final InboxUiState $uiState;
   // $VF: synthetic field
   final Function0 $onBackButtonClick;

   InboxScreenKt$InboxContent$3(InboxUiState var1, Function0 var2) {
      this.$uiState = var1;
      this.$onBackButtonClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C174@6642L420:InboxScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-398771051, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous> (InboxScreen.kt:174)");
         }

         String var10001 = this.$uiState.getIntercomTopBarState().getTitle();
         if (var10001 == null) {
            var10001 = "";
         }

         Function0 var10005 = this.$onBackButtonClick;
         Integer var10006 = this.$uiState.getIntercomTopBarState().getNavIcon();
         Color var3 = this.$uiState.getIntercomTopBarState().recovered$4d97b5da718de5d3();
         var1.startReplaceGroup(1124473084);
         ComposerKt.sourceInformation(var1, "178@6928L6");
         long var4 = var3 == null
            ? IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4()
            : var3.recovered$45f9de7b85e0e0b5();
         var1.endReplaceGroup();
         var3 = this.$uiState.getIntercomTopBarState().recovered$7cce2490cba0e529();
         var1.startReplaceGroup(1124476507);
         ComposerKt.sourceInformation(var1, "179@7032L6");
         long var7 = var3 == null
            ? IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5()
            : var3.recovered$45f9de7b85e0e0b5();
         var1.endReplaceGroup();
         TopActionBarKt.recovered$e7704aa1c1b2200b(null, var10001, null, null, null, var10005, var10006, false, var4, var7, 0L, null, null, var1, 0, 0, 7325);
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
