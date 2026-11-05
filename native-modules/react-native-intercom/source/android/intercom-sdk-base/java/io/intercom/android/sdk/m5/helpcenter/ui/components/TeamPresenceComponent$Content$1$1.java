package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TeamPresenceComponent$Content$1$1 implements Function2 {
   // $VF: synthetic field
   final TeamPresenceComponent this$0;
   // $VF: synthetic field
   final ArticleViewState$TeamPresenceState $it;

   TeamPresenceComponent$Content$1$1(TeamPresenceComponent var1, ArticleViewState$TeamPresenceState var2) {
      this.this$0 = var1;
      this.$it = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C:TeamPresenceComponent.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1302062926,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponent.Content.<anonymous>.<anonymous> (TeamPresenceComponent.kt:154)"
            );
         }

         if (this.this$0.getNeedsChatBubble()) {
            var1.startReplaceGroup(-364953619);
            ComposerKt.sourceInformation(var1, "155@6326L35");
            TeamPresenceComponentKt.TeamPresenceComponentWithBubble(this.$it, var1, 0);
            var1.endReplaceGroup();
         } else {
            var1.startReplaceGroup(-364873577);
            ComposerKt.sourceInformation(var1, "157@6407L25");
            TeamPresenceComponentKt.TeamPresenceComponent(this.$it, false, null, var1, 0, 6);
            var1.endReplaceGroup();
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
