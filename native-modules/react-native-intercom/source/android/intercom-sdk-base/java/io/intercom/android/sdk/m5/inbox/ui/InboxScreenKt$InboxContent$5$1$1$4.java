package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Error;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxContent$5$1$1$4 implements Function3 {
   // $VF: synthetic field
   final InboxUiState $uiState;

   InboxScreenKt$InboxContent$5$1$1$4(InboxUiState var1) {
      this.$uiState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C224@9018L170:InboxScreen.kt#po70xe");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -81627649, var4, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous>.<anonymous>.<anonymous>.<anonymous> (InboxScreen.kt:224)"
            );
         }

         Modifier var5 = LazyItemScope.fillParentMaxHeight$default(var1, Modifier.Companion, 0.0F, 1, null);
         InboxErrorScreenKt.InboxErrorScreen(((InboxUiState$Error)this.$uiState).getErrorState(), var5, var2, 0, 0);
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
