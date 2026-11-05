package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Conversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ChatFullNotificationKt$addChatFullNotificationToView$1$1$1 implements Function2 {
   // $VF: synthetic field
   final Conversation $conversation;
   // $VF: synthetic field
   final AppConfig $appConfig;

   ChatFullNotificationKt$addChatFullNotificationToView$1$1$1(Conversation var1, AppConfig var2) {
      this.$conversation = var1;
      this.$appConfig = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C69@3063L131:ChatFullNotification.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               583177563,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.addChatFullNotificationToView.<anonymous>.<anonymous>.<anonymous> (ChatFullNotification.kt:69)"
            );
         }

         ChatFullNotificationKt.ChatFullNotification(this.$conversation, this.$appConfig, null, var1, 0, 4);
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
