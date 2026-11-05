package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionSummaryComponentKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$1 implements Function3 {
   // $VF: synthetic field
   final CollectionDetailsUiState$Content $state;

   HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$1(CollectionDetailsUiState$Content var1) {
      this.$state = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C70@2882L41:HelpCenterCollectionDetailsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1491252145, var3, -1, "io.intercom.android.sdk.m5.helpcenter.ui.helpCenterSectionItems.<anonymous> (HelpCenterCollectionDetailsScreen.kt:70)"
            );
         }

         CollectionSummaryComponentKt.CollectionSummaryComponent(this.$state, null, var2, 0, 2);
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
