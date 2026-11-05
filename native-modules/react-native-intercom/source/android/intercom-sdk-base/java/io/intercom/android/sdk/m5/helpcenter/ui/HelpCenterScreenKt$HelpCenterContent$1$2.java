package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterScreenKt$HelpCenterContent$1$2 implements Function3 {
   // $VF: synthetic field
   final List $collectionIds;
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final NavHostController $navController;

   HelpCenterScreenKt$HelpCenterContent$1$2(List var1, HelpCenterViewModel var2, NavHostController var3) {
      this.$collectionIds = var1;
      this.$viewModel = var2;
      this.$navController = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C158@5685L303:HelpCenterScreen.kt#ln9mxm");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2077063271, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterContent.<anonymous>.<anonymous> (HelpCenterScreen.kt:152)"
            );
         }

         String var5 = this.$collectionIds.size() == 1 ? "COLLECTION_DETAILS" : "COLLECTIONS";
         HelpCenterScreenKt.HelpCenterNavGraph(
            this.$viewModel, this.$navController, var5, this.$collectionIds, PaddingKt.padding(Modifier.Companion, var1), var2, 0, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
