package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Header$Expanded$Body;
import io.intercom.android.sdk.models.Header$Expanded$Footer;
import io.intercom.android.sdk.models.Header$Expanded$SocialAccount;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$ExpandedTeamPresenceLayoutKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C265@10745L2952:ExpandedTeamPresenceLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1317404560,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$ExpandedTeamPresenceLayoutKt.lambda-3.<anonymous> (ExpandedTeamPresenceLayout.kt:265)"
            );
         }

         Header$Expanded$Body[] var3 = new Header$Expanded$Body[2];
         Header$Expanded$Style var4 = Header$Expanded$Style.PARAGRAPH;
         var3[0] = new Header$Expanded$Body(var4, "Hi there! I'm a member of the Intercom team. How can I help you today?", "#000000");
         var4 = Header$Expanded$Style.PARAGRAPH;
         var3[1] = new Header$Expanded$Body(var4, "I'm here to answer any questions you have about Intercom.", "#000000");
         List var10003 = CollectionsKt.listOf(var3);
         AvatarType var10004 = AvatarType.LAYERED_BUBBLES;
         Avatar var10007 = Avatar.create("https://images.intercom-mail-1.com/avatars/144859/square_128/IMG_20210331_110724.jpg?1617196064", "F");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         List var10005 = CollectionsKt.listOf(new AvatarWrapper(var10007, false, 2, null));
         Header$Expanded$Style var6 = Header$Expanded$Style.PARAGRAPH;
         AvatarType var10008 = AvatarType.LAYERED_BUBBLES;
         Avatar$Builder[] var5 = new Avatar$Builder[]{
            new Avatar$Builder()
               .withImageUrl("https://images.intercom-mail-1.com/avatars/144859/square_128/IMG_20210331_110724.jpg?1617196064")
               .withInitials("SK"),
            new Avatar$Builder()
               .withImageUrl("https://images.intercom-mail-1.com/avatars/144859/square_128/IMG_20210331_110724.jpg?1617196064")
               .withInitials("RH")
         };
         AvatarDetails var8 = new AvatarDetails(var10008, CollectionsKt.listOf(var5));
         ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayout(
            new ExpandedTeamPresenceState(
               "SDKTestApp",
               var10003,
               var10004,
               var10005,
               CollectionsKt.listOf(new Header$Expanded$Footer(var6, "Powered by Intercom", null, var8)),
               CollectionsKt.listOf(
                  new Header$Expanded$SocialAccount(
                     "twitter",
                     "https://twitter.com/intercom",
                     "https://images.intercom-mail-1.com/avatars/144859/square_128/IMG_20210331_110724.jpg?1617196064",
                     "intercom"
                  )
               ),
               false
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
