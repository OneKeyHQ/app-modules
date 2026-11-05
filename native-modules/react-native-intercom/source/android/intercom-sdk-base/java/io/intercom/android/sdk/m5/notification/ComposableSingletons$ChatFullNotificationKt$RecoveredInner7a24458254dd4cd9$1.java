package io.intercom.android.sdk.m5.notification;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChatFullNotificationKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$ChatFullNotificationKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$ChatFullNotificationKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C292@11024L667:ChatFullNotification.kt#1zjczn");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2029839298,
               var2,
               -1,
               "io.intercom.android.sdk.m5.notification.ComposableSingletons$ChatFullNotificationKt.lambda-2.<anonymous> (ChatFullNotification.kt:292)"
            );
         }

         ChatFullNotificationKt.ChatFullNotification(
            new Conversation(
               null,
               null,
               false,
               CollectionsKt.listOf(new Participant$Builder().withName("Alex").withIsBot(false).withType("admin")),
               CollectionsKt.listOf(
                  new Part$Builder()
                     .withBlocks(CollectionsKt.listOf(new Block$Builder().withText("Quick question about your recent order.").withType("paragraph")))
               ),
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
