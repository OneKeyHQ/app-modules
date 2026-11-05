package io.intercom.android.sdk.m5.conversation.usecase;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator;
import io.intercom.android.sdk.m5.conversation.states.JumpToBottomButtonState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001a4\u0010\u0000\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u00012\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002¨\u0006\u000b",
   d2 = {
         "calculateJumpToBottomButtonState",
         "Lkotlin/Pair;",
         "Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;",
         "",
         "currentClientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "newConversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "newAdminMessages",
         "",
         "Lio/intercom/android/sdk/models/Part;",
         "intercom-sdk-base_release"
   }
)
public final class RefreshConversationUseCaseKt {
   private static final Pair calculateJumpToBottomButtonState(ConversationClientState var0, Conversation var1, List var2) {
      Pair var6;
      if (var0.getFloatingIndicatorState() instanceof FloatingIndicatorState$JumpToBottomIndicator) {
         String var10000 = var0.getNewMessageId();
         if (var10000 == null) {
            Part var5 = (Part)CollectionsKt.firstOrNull(var2);
            var10000 = var5 != null ? var5.getId() : null;
         }

         String var3 = var10000;
         var6 = TuplesKt.to(
            new FloatingIndicatorState$JumpToBottomIndicator(
               JumpToBottomButtonState.copy$default(
                  ((FloatingIndicatorState$JumpToBottomIndicator)var0.getFloatingIndicatorState()).getJumpToBottomButtonState(),
                  ((FloatingIndicatorState$JumpToBottomIndicator)var0.getFloatingIndicatorState()).getJumpToBottomButtonState().getUnreadMessages()
                     + var2.size(),
                  0,
                  2,
                  null
               )
            ),
            var3
         );
      } else {
         Conversation var7 = var0.getConversation();
         List var8 = var7 != null ? var7.parts() : null;
         if (var8 == null) {
            var8 = CollectionsKt.emptyList();
         }

         boolean var4 = var8.size() == var1.parts().size();
         var6 = TuplesKt.to(var0.getFloatingIndicatorState(), var4 ? var0.getNewMessageId() : null);
      }

      return var6;
   }

   // $VF: synthetic method
   public static final Pair access$calculateJumpToBottomButtonState(ConversationClientState var0, Conversation var1, List var2) {
      return calculateJumpToBottomButtonState(var0, var1, var2);
   }
}
