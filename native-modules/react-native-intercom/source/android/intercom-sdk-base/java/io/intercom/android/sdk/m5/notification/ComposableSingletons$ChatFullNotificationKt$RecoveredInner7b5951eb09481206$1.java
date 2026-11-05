package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChatFullNotificationKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ChatFullNotificationKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ChatFullNotificationKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C256@9413L1486:ChatFullNotification.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               424986995,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.ComposableSingletons$ChatFullNotificationKt.lambda-1.<anonymous> (ChatFullNotification.kt:256)"
            );
         }

         List var10005 = CollectionsKt.listOf(new Participant$Builder().withName("Santhosh").withIsBot(false).withType("admin"));
         Part$Builder var10006 = new Part$Builder();
         Block$Builder[] var3 = new Block$Builder[]{
            new Block$Builder().withText("<b>Hello There!</b>").withType("paragraph"),
            new Block$Builder()
               .withText(
                  "This is a longer message that should wrap to multiple lines in the chat full notification. We want to make sure it displays correctly."
               )
               .withType("paragraph"),
            new Block$Builder()
               .withText("We've looked into your request and we'll be implementing this feature in our <b>next release.</b>")
               .withType("paragraph"),
            new Block$Builder().withText("All the best").withType("paragraph"),
            new Block$Builder().withText("The team").withType("paragraph")
         };
         ChatFullNotificationKt.ChatFullNotification(
            new Conversation(
               null,
               null,
               false,
               var10005,
               CollectionsKt.listOf(var10006.withBlocks(CollectionsKt.listOf(var3))),
               null,
               null,
               null,
               false,
               false,
               null,
               null,
               false,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               2097127,
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
