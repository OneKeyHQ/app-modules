package io.intercom.android.sdk.m5.navigation;

import androidx.navigation.NavHostController;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics.Kotlin;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketDetailDestinationKt$ticketDetailDestination$17$2$1 extends FunctionReferenceImpl implements Function2 {
   // $VF: synthetic field
   final NavHostController $navController;

   TicketDetailDestinationKt$ticketDetailDestination$17$2$1(NavHostController var1) {
      super(
         2,
         Kotlin.class,
         "onConversationCTAClicked",
         "ticketDetailDestination$onConversationCTAClicked(Landroidx/navigation/NavHostController;Ljava/lang/String;Z)V",
         0
      );
      this.$navController = var1;
   }

   public final void invoke(String var1, boolean var2) {
      TicketDetailDestinationKt.access$ticketDetailDestination$onConversationCTAClicked(this.$navController, var1, var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((String)var1, ((Boolean)var2).booleanValue());
      return Unit.INSTANCE;
   }
}
