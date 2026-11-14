package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Empty;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Error;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Initial;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Loading;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsScreenKt$TicketsScreen$3 implements Function3 {
   // $VF: synthetic field
   final TicketsScreenUiState $uiState;
   // $VF: synthetic field
   final Function1 $onTicketClick;

   TicketsScreenKt$TicketsScreen$3(TicketsScreenUiState var1, Function1 var2) {
      this.$uiState = var1;
      this.$onTicketClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C:TicketsScreen.kt#yl74uv");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1690440366, var4, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreen.<anonymous> (TicketsScreen.kt:58)");
         }

         TicketsScreenUiState var5 = this.$uiState;
         if (var5 instanceof TicketsScreenUiState$Content) {
            var2.startReplaceGroup(1161790631);
            ComposerKt.sourceInformation(var2, "59@2632L174");
            TicketsScreenKt.TicketsScreenContent(
               (TicketsScreenUiState$Content)this.$uiState, var1, this.$onTicketClick, var2, LazyPagingItems.$stable | 112 & var4 << 3, 0
            );
            var2.endReplaceGroup();
         } else if (var5 instanceof TicketsScreenUiState$Empty) {
            var2.startReplaceGroup(1161992379);
            ComposerKt.sourceInformation(var2, "65@2836L154");
            Modifier var6 = PaddingKt.padding(Modifier.Companion, var1);
            TicketsEmptyScreenKt.TicketsEmptyScreen(((TicketsScreenUiState$Empty)this.$uiState).getEmptyState(), var6, var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var5 instanceof TicketsScreenUiState$Error) {
            var2.startReplaceGroup(1162174752);
            ComposerKt.sourceInformation(var2, "70@3020L149");
            Modifier var7 = PaddingKt.padding(Modifier.Companion, var1);
            TicketsErrorScreenKt.TicketsErrorScreen(((TicketsScreenUiState$Error)this.$uiState).getErrorState(), var7, var2, 0, 0);
            var2.endReplaceGroup();
         } else {
            if (!(var5 instanceof TicketsScreenUiState$Initial) && !(var5 instanceof TicketsScreenUiState$Loading)) {
               var2.startReplaceGroup(2115686166);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(2115705620);
            ComposerKt.sourceInformation(var2, "75@3213L103");
            TicketsLoadingScreenKt.TicketsLoadingScreen(PaddingKt.padding(Modifier.Companion, var1), false, var2, 0, 2);
            var2.endReplaceGroup();
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
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
