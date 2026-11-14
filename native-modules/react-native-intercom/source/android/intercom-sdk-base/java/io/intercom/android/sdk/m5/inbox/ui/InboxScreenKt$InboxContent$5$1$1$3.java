package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState;
import io.intercom.android.sdk.m5.inbox.states.InboxUiState$Empty;
import io.intercom.android.sdk.models.EmptyState;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxContent$5$1$1$3 implements Function3 {
   // $VF: synthetic field
   final InboxUiState $uiState;
   // $VF: synthetic field
   final Function0 $onSendMessageButtonClick;
   // $VF: synthetic field
   final Function0 $onBrowseHelpCenterButtonClick;

   InboxScreenKt$InboxContent$5$1$1$3(InboxUiState var1, Function0 var2, Function0 var3) {
      this.$uiState = var1;
      this.$onSendMessageButtonClick = var2;
      this.$onBrowseHelpCenterButtonClick = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C212@8389L529:InboxScreen.kt#po70xe");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1427381856, var4, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous>.<anonymous>.<anonymous>.<anonymous> (InboxScreen.kt:212)"
            );
         }

         EmptyState var10000 = ((InboxUiState$Empty)this.$uiState).getEmptyState();
         boolean var10001 = ((InboxUiState$Empty)this.$uiState).getShowActionButton();
         Function0 var10002;
         switch (((InboxUiState$Empty)this.$uiState).getEmptyState().getAction().getType()) {
            case MESSAGE:
               var10002 = this.$onSendMessageButtonClick;
               break;
            case HELP:
               var10002 = this.$onBrowseHelpCenterButtonClick;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         InboxEmptyScreenKt.InboxEmptyScreen(
            var10000, var10001, var10002, LazyItemScope.fillParentMaxHeight$default(var1, Modifier.Companion, 0.0F, 1, null), var2, 0, 0
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
