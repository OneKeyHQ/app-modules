package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.IntercomPrimaryButtonKt;
import io.intercom.android.sdk.m5.home.data.IconType;
import io.intercom.android.sdk.models.EmptyState;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxEmptyScreenKt$InboxEmptyScreen$1 implements Function2 {
   // $VF: synthetic field
   final boolean $showActionButton;
   // $VF: synthetic field
   final EmptyState $emptyState;
   // $VF: synthetic field
   final Function0 $onActionButtonClick;

   InboxEmptyScreenKt$InboxEmptyScreen$1(boolean var1, EmptyState var2, Function0 var3) {
      this.$showActionButton = var1;
      this.$emptyState = var2;
      this.$onActionButtonClick = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C:InboxEmptyScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2045450098, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxEmptyScreen.<anonymous> (InboxEmptyScreen.kt:27)");
         }

         if (this.$showActionButton) {
            switch (this.$emptyState.getAction().getType()) {
               case MESSAGE:
                  var1.startReplaceGroup(1738022382);
                  ComposerKt.sourceInformation(var1, "29@1049L476");
                  String var10000 = this.$emptyState.getAction().getLabel();
                  IconType var10002 = this.$emptyState.getAction().getIcon();
                  Integer var3;
                  switch (var10002 == null ? -1 : InboxEmptyScreenKt$InboxEmptyScreen$1$WhenMappings.$EnumSwitchMapping$0[var10002.ordinal()]) {
                     case 1:
                        var3 = drawable.intercom_send_message_icon;
                        break;
                     case 2:
                     case 3:
                     case 4:
                        var3 = drawable.intercom_conversation_card_question;
                        break;
                     default:
                        var3 = null;
                  }

                  IntercomPrimaryButtonKt.IntercomPrimaryButton(var10000, null, var3, this.$onActionButtonClick, var1, 0, 2);
                  var1.endReplaceGroup();
                  break;
               case HELP:
                  var1.startReplaceGroup(1738038509);
                  ComposerKt.sourceInformation(var1, "38@1561L219");
                  IntercomPrimaryButtonKt.IntercomPrimaryButton(
                     this.$emptyState.getAction().getLabel(), null, drawable.intercom_article_book_icon, this.$onActionButtonClick, var1, 0, 2
                  );
                  var1.endReplaceGroup();
                  break;
               default:
                  var1.startReplaceGroup(1738020481);
                  var1.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
            }
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
