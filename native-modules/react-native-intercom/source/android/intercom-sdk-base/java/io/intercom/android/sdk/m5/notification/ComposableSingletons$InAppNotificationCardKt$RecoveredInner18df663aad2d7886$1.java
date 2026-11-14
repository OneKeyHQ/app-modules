package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$InAppNotificationCardKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$InAppNotificationCardKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$InAppNotificationCardKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C280@11015L709:InAppNotificationCard.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1952243668,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.ComposableSingletons$InAppNotificationCardKt.lambda-4.<anonymous> (InAppNotificationCard.kt:280)"
            );
         }

         InAppNotificationCardKt.InAppNotificationCard(
            new Conversation(
               null,
               null,
               false,
               CollectionsKt.listOf(
                  new Participant$Builder()
                     .withName("Fin")
                     .withIsBot(true)
                     .withType("bot")
                     .withAvatar(new Avatar$Builder().withInitials("F").withShape(AvatarShape.SQUIRCLE))
               ),
               CollectionsKt.listOf(new Part$Builder().withSummary("Hi! I'm Fin, here to help. What can I do for you?")),
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
            null,
            var1,
            0,
            2
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
