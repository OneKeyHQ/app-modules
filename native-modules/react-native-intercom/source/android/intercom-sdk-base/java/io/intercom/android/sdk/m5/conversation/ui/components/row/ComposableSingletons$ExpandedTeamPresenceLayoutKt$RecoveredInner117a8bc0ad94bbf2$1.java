package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Header$Expanded$Body;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C332@13864L1375:ExpandedTeamPresenceLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1099388065,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$ExpandedTeamPresenceLayoutKt.lambda-5.<anonymous> (ExpandedTeamPresenceLayout.kt:332)"
            );
         }

         Header$Expanded$Body[] var3 = new Header$Expanded$Body[2];
         Header$Expanded$Style var4 = Header$Expanded$Style.PARAGRAPH;
         var3[0] = new Header$Expanded$Body(var4, "Hi there! I'm a member of the Intercom team. How can I help you today?", "#000000");
         var4 = Header$Expanded$Style.PARAGRAPH;
         var3[1] = new Header$Expanded$Body(var4, "I'm here to answer any questions you have about Intercom.", "#000000");
         List var10003 = CollectionsKt.listOf(var3);
         AvatarType var10004 = AvatarType.LAYERED_BUBBLES;
         Avatar var10007 = Avatar.create("https://images.intercom-mail-1.com/avatars/144859/square_128/IMG_20210331_110724.jpg?1617196064", "SK");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayout(
            new ExpandedTeamPresenceState(
               "SDKTestApp",
               var10003,
               var10004,
               CollectionsKt.listOf(new AvatarWrapper(var10007, false, 2, null)),
               CollectionsKt.emptyList(),
               CollectionsKt.emptyList(),
               true
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
