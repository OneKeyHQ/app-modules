package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleResultRowComponentKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1$1$1$1 implements Function4 {
   // $VF: synthetic field
   final ArticleSearchState $state;
   // $VF: synthetic field
   final Function1 $onArticleClicked;

   IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1$1$1$1(ArticleSearchState var1, Function1 var2) {
      this.$state = var1;
      this.$onArticleClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$items");
      ComposerKt.sourceInformation(var3, "C:IntercomArticleSearchScreen.kt#fpck24");
      int var5 = var4;
      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if ((var5 & 145) == 144 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1308683973,
               var5,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous> (IntercomArticleSearchScreen.kt:53)"
            );
         }

         ArticleSearchResultRow var6 = (ArticleSearchResultRow)((ArticleSearchState$Content)this.$state).getSearchResults().get(var2);
         if (var6 instanceof ArticleSearchResultRow$ArticleResultRow) {
            var3.startReplaceGroup(-1577698655);
            ComposerKt.sourceInformation(var3, "55@2630L66");
            ArticleResultRowComponentKt.ArticleResultRowComponent((ArticleSearchResultRow$ArticleResultRow)var6, this.$onArticleClicked, null, var3, 0, 4);
            var3.endReplaceGroup();
         } else {
            if (!(var6 instanceof ArticleSearchResultRow$TeammateHelpRow)) {
               var3.startReplaceGroup(-1577703200);
               var3.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var3.startReplaceGroup(-1577693120);
            ComposerKt.sourceInformation(var3, "58@2803L65");
            TeamPresenceComponentKt.TeamPresenceComponent(((ArticleSearchResultRow$TeammateHelpRow)var6).getTeamPresenceState(), false, null, var3, 0, 6);
            var3.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
