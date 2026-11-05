package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u0001:\u0001\fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationResponse;",
         "Lio/intercom/android/sdk/models/BaseResponse;",
         "builder",
         "Lio/intercom/android/sdk/models/ConversationResponse$Builder;",
         "<init>",
         "(Lio/intercom/android/sdk/models/ConversationResponse$Builder;)V",
         "getBuilder",
         "()Lio/intercom/android/sdk/models/ConversationResponse$Builder;",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "getConversation",
         "()Lio/intercom/android/sdk/models/Conversation;",
         "Builder",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConversationResponse extends BaseResponse {
   @NotNull
   private final ConversationResponse$Builder builder;
   @NotNull
   private final Conversation conversation;
   public static final int $stable;

   static { $stable = 8; }

   public ConversationResponse(@NotNull ConversationResponse$Builder var1) {
      Intrinsics.checkNotNullParameter(var1, "builder");
      super(var1);
      this.builder = var1;
      Conversation var10001;
      String var10003 = this.builder.getId();
      if (var10003 == null) {
         var10003 = "";
      }

      boolean var10005 = this.builder.getRead();
      List var10006 = this.builder.getParticipants();
      List var10007 = this.builder.getConversation_parts();
      List var10008 = this.builder.getGroup_conversation_participant_ids();
      LastParticipatingAdmin$Builder var10009 = this.builder.getLast_participating_admin();
      ComposerState var10010 = this.builder.getComposer_state();
      if (var10010 == null) {
         var10010 = ComposerState.Companion.getNULL();
      }

      boolean var10011 = this.builder.getPrevent_end_user_replies();
      boolean var10012 = this.builder.getInbound_conversations_disabled();
      String var10013 = this.builder.getNotification_status();
      if (var10013 == null) {
         var10013 = "";
      }

      String var10014 = this.builder.getState();
      if (var10014 == null) {
         var10014 = "";
      }

      boolean var10015 = this.builder.is_inbound();
      Ticket var10016 = this.builder.getTicket();
      ConversationUiFlags var10017 = this.builder.getUi_flags();
      if (var10017 == null) {
         var10017 = ConversationUiFlags.Companion.getDEFAULT();
      }

      var10001 = new Conversation(
         var10003,
         null,
         var10005,
         var10006,
         var10007,
         var10008,
         var10009,
         var10010,
         var10011,
         var10012,
         var10013,
         var10014,
         var10015,
         var10016,
         var10017,
         null,
         null,
         null,
         null,
         null,
         null,
         2064386,
         null
      );
      this.conversation = var10001;
   }

   @NotNull
   public final ConversationResponse$Builder getBuilder() {
      return this.builder;
   }

   @NotNull
   public final Conversation getConversation() {
      return this.conversation;
   }
}
