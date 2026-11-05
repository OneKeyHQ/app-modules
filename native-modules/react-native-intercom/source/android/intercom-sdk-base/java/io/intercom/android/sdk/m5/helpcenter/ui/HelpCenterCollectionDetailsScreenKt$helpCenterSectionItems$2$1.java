package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow;
import io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleRowComponentKt;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterCollectionDetailsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,96:1\n118#2:97\n118#2:98\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$1\n*L\n77#1:97\n81#1:98\n*E\n"
)
final class HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$1 implements Function3 {
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final CollectionDetailsRow $item;
   // $VF: synthetic field
   final Function1 $onArticleClicked;
   // $VF: synthetic field
   final List $collectionDetailsRows;

   HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$1(int var1, CollectionDetailsRow var2, Function1 var3, List var4) {
      this.$index = var1;
      this.$item = var2;
      this.$onArticleClicked = var3;
      this.$collectionDetailsRows = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C78@3253L66,80@3474L72:HelpCenterCollectionDetailsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -103698696,
               var3,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.helpCenterSectionItems.<anonymous>.<anonymous> (HelpCenterCollectionDetailsScreen.kt:75)"
            );
         }

         var2.startReplaceGroup(153525508);
         ComposerKt.sourceInformation(var2, "76@3177L41");
         if (this.$index == 0) {
            Modifier var10000 = Modifier.Companion;
            int var4 = 16;
            int var5 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var4)), var2, 6);
         }

         var2.endReplaceGroup();
         ArticleRowComponentKt.ArticleRowComponent(null, (CollectionDetailsRow$ArticleRow)this.$item, this.$onArticleClicked, var2, 0, 1);
         if (this.$index != this.$collectionDetailsRows.size() - 1
            && this.$collectionDetailsRows.get(this.$index + 1) instanceof CollectionDetailsRow$ArticleRow) {
            Modifier var10 = Modifier.Companion;
            int var6 = 16;
            int var8 = 0;
            float var10001 = Dp.recovered$95b3d103663bf060(var6);
            var6 = 16;
            var8 = 0;
            IntercomDividerKt.IntercomDivider(
               PaddingKt.recovered$03b481d31d62885a(var10, var10001, 0.0F, Dp.recovered$95b3d103663bf060(var6), 0.0F, 10, null), var2, 6, 0
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
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
