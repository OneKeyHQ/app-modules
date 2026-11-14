package io.intercom.android.sdk.m5.navigation;

import androidx.compose.runtime.MutableState;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$TicketSideEffect;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$TicketSideEffect$AnswerClicked;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$TicketSideEffect$Finish;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class CreateTicketDestinationKt$createTicketDestination$4$1$1$1 implements FlowCollector {
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final CoroutineScope $scope;
   // $VF: synthetic field
   final MutableState $answerClickedData;

   CreateTicketDestinationKt$createTicketDestination$4$1$1$1(NavHostController var1, CoroutineScope var2, MutableState var3) {
      this.$navController = var1;
      this.$scope = var2;
      this.$answerClickedData = var3;
   }

   public final Object emit(CreateTicketViewModel$TicketSideEffect var1, Continuation var2) {
      CreateTicketViewModel$TicketSideEffect var3 = var1;
      if (Intrinsics.areEqual(var3, CreateTicketViewModel$TicketSideEffect$Finish.INSTANCE)) {
         this.$navController.navigateUp();
         IntercomRouterKt.openTicketDetailScreen$default(this.$navController, true, null, false, 6, null);
      } else {
         if (!(var3 instanceof CreateTicketViewModel$TicketSideEffect$AnswerClicked)) {
            throw new NoWhenBranchMatchedException();
         }

         CreateTicketDestinationKt$createTicketDestination$4.access$invoke$showSheet(
            this.$scope, this.$answerClickedData, ((CreateTicketViewModel$TicketSideEffect$AnswerClicked)var1).getAnswerClickData()
         );
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((CreateTicketViewModel$TicketSideEffect)var1, var2);
   }
}
