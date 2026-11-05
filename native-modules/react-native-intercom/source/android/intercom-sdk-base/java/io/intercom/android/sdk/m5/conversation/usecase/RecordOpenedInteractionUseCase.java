package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.models.Conversation;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0011\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0086\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;)V",
         "invoke",
         "",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "shouldRecordOpenedInteraction",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class RecordOpenedInteractionUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   public static final int $stable;

   static { $stable = 8; }

   public RecordOpenedInteractionUseCase(@NotNull ConversationRepository var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      this.conversationRepository = var1;
   }

   public final void invoke(@NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      if (this.shouldRecordOpenedInteraction(var1)) {
         ConversationRepository var10000 = this.conversationRepository;
         String var10001 = var1.getId();
         String[] var2 = new String[]{"opened"};
         var10000.recordInteractions(var10001, var2);
      }
   }

   private final boolean shouldRecordOpenedInteraction(Conversation var1) {
      String[] var3 = new String[]{"delivered", "renotifying"};
      List var2 = CollectionsKt.listOf(var3);
      return var2.contains(var1.getNotificationStatus());
   }
}
