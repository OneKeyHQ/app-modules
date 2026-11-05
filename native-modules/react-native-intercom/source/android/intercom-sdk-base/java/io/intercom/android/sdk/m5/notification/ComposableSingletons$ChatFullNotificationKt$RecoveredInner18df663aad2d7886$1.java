package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.Ticket$Status;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChatFullNotificationKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ChatFullNotificationKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ChatFullNotificationKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C340@12626L869:ChatFullNotification.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1302298593,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.ComposableSingletons$ChatFullNotificationKt.lambda-4.<anonymous> (ChatFullNotification.kt:340)"
            );
         }

         ChatFullNotificationKt.ChatFullNotification(
            new Conversation(
               null,
               null,
               false,
               CollectionsKt.listOf(new Participant$Builder().withName("Santhosh").withIsBot(false).withType("admin")),
               CollectionsKt.listOf(
                  new Part$Builder()
                     .withBlocks(CollectionsKt.listOf(new Block$Builder().withText("Hello There! This is a ticket-related message.").withType("paragraph")))
               ),
               null,
               null,
               null,
               false,
               false,
               null,
               null,
               false,
               new Ticket(
                  null,
                  null,
                  "Feature request",
                  null,
                  null,
                  null,
                  new Ticket$Status("Submitted", "submitted", null, false, 0L, 28, null),
                  null,
                  null,
                  0L,
                  null,
                  null,
                  null,
                  null,
                  0L,
                  32699,
                  null
               ),
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               2088935,
               null
            ),
            ChatFullNotificationKt.access$getPreviewAppConfig$p(),
            null,
            var1,
            0,
            4
         );
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
