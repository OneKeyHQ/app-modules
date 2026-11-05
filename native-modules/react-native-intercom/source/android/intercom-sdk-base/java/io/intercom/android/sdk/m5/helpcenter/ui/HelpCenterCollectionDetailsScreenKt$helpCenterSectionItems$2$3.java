package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$3 implements Function3 {
   // $VF: synthetic field
   final CollectionDetailsRow $item;

   HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$3(CollectionDetailsRow var1) {
      this.$item = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C90@3986L87:HelpCenterCollectionDetailsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1352146481,
               var3,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.helpCenterSectionItems.<anonymous>.<anonymous> (HelpCenterCollectionDetailsScreen.kt:90)"
            );
         }

         TeamPresenceComponentKt.TeamPresenceComponent(((CollectionDetailsRow$SendMessageRow)this.$item).getTeamPresenceState(), false, null, var2, 48, 4);
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
