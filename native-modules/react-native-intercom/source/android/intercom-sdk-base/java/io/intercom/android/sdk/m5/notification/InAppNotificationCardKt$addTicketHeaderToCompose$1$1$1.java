package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.m5.components.TicketHeaderKt;
import io.intercom.android.sdk.m5.components.TicketStatusHeaderArgs;
import io.intercom.android.sdk.models.Conversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InAppNotificationCardKt$addTicketHeaderToCompose$1$1$1 implements Function2 {
   // $VF: synthetic field
   final Conversation $conversation;

   InAppNotificationCardKt$addTicketHeaderToCompose$1$1$1(Conversation var1) {
      this.$conversation = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C59@2614L290:InAppNotificationCard.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1860903769,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.addTicketHeaderToCompose.<anonymous>.<anonymous>.<anonymous> (InAppNotificationCard.kt:58)"
            );
         }

         if (this.$conversation.getTicket() != null) {
            TicketHeaderKt.TicketHeader(
               null,
               new TicketStatusHeaderArgs(
                  this.$conversation.getTicket().getTitle(),
                  this.$conversation.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold()
               ),
               var1,
               0,
               1
            );
         }

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
