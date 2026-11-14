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
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChatFullNotificationKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$ChatFullNotificationKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$ChatFullNotificationKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C368@13622L993:ChatFullNotification.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               19293965,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.ComposableSingletons$ChatFullNotificationKt.lambda-5.<anonymous> (ChatFullNotification.kt:368)"
            );
         }

         List var10005 = CollectionsKt.listOf(new Participant$Builder().withName("Maria").withIsBot(false).withType("admin"));
         List var10006 = CollectionsKt.listOf(
            new Part$Builder()
               .withBlocks(
                  CollectionsKt.listOf(
                     new Block$Builder()
                        .withText("I've looked into your request and we'll be implementing this feature in our next release.")
                        .withType("paragraph")
                  )
               )
         );
         Ticket$Status var10023 = new Ticket$Status("In Progress", "in_progress", null, false, 0L, 28, null);
         Participant$Builder var10027 = new Participant$Builder().withName("Maria Johnson");
         Intrinsics.checkNotNullExpressionValue(var10027, "withName(...)");
         ChatFullNotificationKt.ChatFullNotification(
            new Conversation(
               null,
               null,
               false,
               var10005,
               var10006,
               null,
               null,
               null,
               false,
               false,
               null,
               null,
               false,
               new Ticket(null, null, "Feature request", null, null, null, var10023, null, null, 0L, var10027, null, null, null, 0L, 31675, null),
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
