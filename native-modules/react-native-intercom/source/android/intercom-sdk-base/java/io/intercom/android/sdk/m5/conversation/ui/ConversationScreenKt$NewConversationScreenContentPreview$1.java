package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$NewConversationScreenContentPreview$1 implements Function2 {
   // $VF: synthetic field
   final ExpandedTeamPresenceState $expandedTeamPresenceState;

   ConversationScreenKt$NewConversationScreenContentPreview$1(ExpandedTeamPresenceState var1) {
      this.$expandedTeamPresenceState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C988@45972L2327,988@45964L2335:ConversationScreen.kt#fnibcj");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2080970892, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.NewConversationScreenContentPreview.<anonymous> (ConversationScreen.kt:988)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -1434887623, true, new ConversationScreenKt$NewConversationScreenContentPreview$1$1(this.$expandedTeamPresenceState), var1, 54
            ),
            var1,
            12582912,
            127
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
