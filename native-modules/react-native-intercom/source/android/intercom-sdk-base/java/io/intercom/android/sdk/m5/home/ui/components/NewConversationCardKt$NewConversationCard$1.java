package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class NewConversationCardKt$NewConversationCard$1 implements Function3 {
   // $VF: synthetic field
   final HomeCards$HomeNewConversationData $newConversation;
   // $VF: synthetic field
   final Function0 $onNewConversationClicked;
   // $VF: synthetic field
   final List $adminAvatars;
   // $VF: synthetic field
   final AvatarWrapper $botAvatar;

   NewConversationCardKt$NewConversationCard$1(HomeCards$HomeNewConversationData var1, Function0 var2, List var3, AvatarWrapper var4) {
      this.$newConversation = var1;
      this.$onNewConversationClicked = var2;
      this.$adminAvatars = var3;
      this.$botAvatar = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C:NewConversationCard.kt#utrtal");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1464624655, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCard.<anonymous> (NewConversationCard.kt:33)"
            );
         }

         if (this.$newConversation.getHomeCard() != null) {
            var2.startReplaceGroup(995075445);
            ComposerKt.sourceInformation(var2, "34@1583L158");
            NewConversationCardKt.access$NewConversationCardV2(this.$newConversation.getHomeCard(), this.$onNewConversationClicked, var2, 0);
            var2.endReplaceGroup();
         } else {
            var2.startReplaceGroup(995264607);
            ComposerKt.sourceInformation(var2, "39@1771L244");
            NewConversationCardKt.access$NewConversationCardV1(
               this.$newConversation, this.$adminAvatars, this.$botAvatar, this.$onNewConversationClicked, var2, 0, 0
            );
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
