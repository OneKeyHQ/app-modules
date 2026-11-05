package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class CreateTicketContentScreenKt$CreateTicketScreen$1 implements Function2 {
   // $VF: synthetic field
   final CreateTicketViewModel$CreateTicketFormUiState $uiState;
   // $VF: synthetic field
   final Function0 $onBackClick;

   CreateTicketContentScreenKt$CreateTicketScreen$1(CreateTicketViewModel$CreateTicketFormUiState var1, Function0 var2) {
      this.$uiState = var1;
      this.$onBackClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C74@3681L54:CreateTicketContentScreen.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2106967777, var2, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketScreen.<anonymous> (CreateTicketContentScreen.kt:72)"
            );
         }

         String var3 = this.$uiState instanceof CreateTicketViewModel$CreateTicketFormUiState$Content
            ? ((CreateTicketViewModel$CreateTicketFormUiState$Content)this.$uiState).getTitle()
            : "";
         TopActionBarKt.recovered$e7704aa1c1b2200b(null, var3, null, null, null, this.$onBackClick, null, false, 0L, 0L, 0L, null, null, var1, 0, 0, 8157);
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
