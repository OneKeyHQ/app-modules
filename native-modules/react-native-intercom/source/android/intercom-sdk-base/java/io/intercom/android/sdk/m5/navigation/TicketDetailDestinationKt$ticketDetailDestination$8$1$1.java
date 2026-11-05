package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.navigation.NavHostController;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics.Kotlin;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketDetailDestinationKt$ticketDetailDestination$8$1$1 extends FunctionReferenceImpl implements Function0 {
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final ComponentActivity $rootActivity;

   TicketDetailDestinationKt$ticketDetailDestination$8$1$1(NavHostController var1, ComponentActivity var2) {
      super(
         0,
         Kotlin.class,
         "onBackClicked",
         "ticketDetailDestination$onBackClicked(Landroidx/navigation/NavHostController;Landroidx/activity/ComponentActivity;)V",
         0
      );
      this.$navController = var1;
      this.$rootActivity = var2;
   }

   public final void recoveredVoidInvoke$a56415f42ed8f4e3() {
      TicketDetailDestinationKt.access$ticketDetailDestination$onBackClicked(this.$navController, this.$rootActivity);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$a56415f42ed8f4e3();
      return Unit.INSTANCE;
   }
}
