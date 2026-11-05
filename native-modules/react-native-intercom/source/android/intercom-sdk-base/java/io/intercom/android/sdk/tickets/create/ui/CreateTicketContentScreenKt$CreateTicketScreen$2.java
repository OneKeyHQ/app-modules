package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.m5.components.LoadingScreenKt;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Error;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Initial;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Loading;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class CreateTicketContentScreenKt$CreateTicketScreen$2 implements Function3 {
   // $VF: synthetic field
   final CreateTicketViewModel$CreateTicketFormUiState $uiState;
   // $VF: synthetic field
   final Function0 $onCreateTicket;
   // $VF: synthetic field
   final Function0 $onCancel;
   // $VF: synthetic field
   final Function1 $onAnswerUpdated;
   // $VF: synthetic field
   final Function1 $onAnswerClick;

   CreateTicketContentScreenKt$CreateTicketScreen$2(
      CreateTicketViewModel$CreateTicketFormUiState var1, Function0 var2, Function0 var3, Function1 var4, Function1 var5
   ) {
      this.$uiState = var1;
      this.$onCreateTicket = var2;
      this.$onCancel = var3;
      this.$onAnswerUpdated = var4;
      this.$onAnswerClick = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "contentPadding");
      ComposerKt.sourceInformation(var2, "C:CreateTicketContentScreen.kt#xh6pmz");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               426563690, var4, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketScreen.<anonymous> (CreateTicketContentScreen.kt:77)"
            );
         }

         CreateTicketViewModel$CreateTicketFormUiState var5 = this.$uiState;
         if (Intrinsics.areEqual(var5, CreateTicketViewModel$CreateTicketFormUiState$Initial.INSTANCE)) {
            var2.startReplaceGroup(-1277561462);
            var2.endReplaceGroup();
         } else if (var5 instanceof CreateTicketViewModel$CreateTicketFormUiState$Content) {
            var2.startReplaceGroup(-1277521038);
            ComposerKt.sourceInformation(var2, "80@3914L346");
            Modifier var6 = PaddingKt.padding(Modifier.Companion, var1);
            CreateTicketViewModel$CreateTicketFormUiState$Content var7 = (CreateTicketViewModel$CreateTicketFormUiState$Content)this.$uiState;
            CreateTicketContentScreenKt.CreateTicketContentScreen(
               var6, var7, this.$onCreateTicket, this.$onCancel, this.$onAnswerUpdated, this.$onAnswerClick, var2, 0, 0
            );
            var2.endReplaceGroup();
         } else if (var5 instanceof CreateTicketViewModel$CreateTicketFormUiState$Error) {
            var2.startReplaceGroup(-1277082047);
            ComposerKt.sourceInformation(var2, "90@4346L139");
            Modifier var8 = PaddingKt.padding(Modifier.Companion, var1);
            IntercomErrorScreenKt.IntercomErrorScreen(((CreateTicketViewModel$CreateTicketFormUiState$Error)this.$uiState).getErrorState(), var8, var2, 0, 0);
            var2.endReplaceGroup();
         } else {
            if (!Intrinsics.areEqual(var5, CreateTicketViewModel$CreateTicketFormUiState$Loading.INSTANCE)) {
               var2.startReplaceGroup(97333863);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(97357878);
            ComposerKt.sourceInformation(var2, "95@4556L160");
            LoadingScreenKt.LoadingScreen(PaddingKt.padding(Modifier.Companion, var1), drawable.intercom_content_loading, false, var2, 0, 4);
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
