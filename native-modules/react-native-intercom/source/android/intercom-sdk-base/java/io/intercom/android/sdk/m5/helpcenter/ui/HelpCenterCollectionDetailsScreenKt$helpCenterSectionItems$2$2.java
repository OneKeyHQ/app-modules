package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$CollectionRow;
import io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$2 implements Function3 {
   // $VF: synthetic field
   final CollectionDetailsRow $item;
   // $VF: synthetic field
   final Function1 $onCollectionClicked;

   HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$2(CollectionDetailsRow var1, Function1 var2) {
      this.$item = var1;
      this.$onCollectionClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C87@3817L77:HelpCenterCollectionDetailsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1175818224,
               var3,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.helpCenterSectionItems.<anonymous>.<anonymous> (HelpCenterCollectionDetailsScreen.kt:87)"
            );
         }

         CollectionRowComponentKt.CollectionRowComponent(
            ((CollectionDetailsRow$CollectionRow)this.$item).getRowData(), this.$onCollectionClicked, null, var2, 0, 4
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
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
