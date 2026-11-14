package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.FloatingActionButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Content;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxContent$4\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,295:1\n118#2:296\n*S KotlinDebug\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxContent$4\n*L\n245#1:296\n*E\n"
)
final class InboxScreenKt$InboxContent$4 implements Function2 {
   // $VF: synthetic field
   final InboxUiState $uiState;
   // $VF: synthetic field
   final Function0 $onSendMessageButtonClick;

   InboxScreenKt$InboxContent$4(InboxUiState var1, Function0 var2) {
      this.$uiState = var1;
      this.$onSendMessageButtonClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C242@9701L6,243@9765L6,240@9572L537:InboxScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1074822478, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous> (InboxScreen.kt:239)");
         }

         if (this.$uiState instanceof InboxUiState$Content && ((InboxUiState$Content)this.$uiState).getShowSendMessageFab()) {
            long var3 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$202acdf3dbb5c020();
            long var5 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
            Modifier var10000 = Modifier.Companion;
            int var8 = 16;
            int var9 = 0;
            Modifier var7 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var8), 7, null);
            RoundedCornerShape var10 = RoundedCornerShapeKt.getCircleShape();
            FloatingActionButtonKt.recovered$b101bfb21479b857(
               this.$onSendMessageButtonClick,
               var7,
               var10,
               var3,
               var5,
               null,
               null,
               ComposableSingletons$InboxScreenKt.INSTANCE.recovered$1400af5e34386102(),
               var1,
               12582960,
               96
            );
         }

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
