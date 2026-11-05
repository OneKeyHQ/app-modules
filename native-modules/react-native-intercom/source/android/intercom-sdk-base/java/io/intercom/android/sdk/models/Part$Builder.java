package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import java.util.List;

public final class Part$Builder {
   @Nullable
   String id;
   @Nullable
   String participant_id;
   @Nullable
   List body;
   @Nullable
   List attachments;
   @Nullable
   MessageStyle message_style;
   @Nullable
   String summary;
   @Nullable
   ReactionReply$Builder reactions_reply;
   @Nullable
   DeliveryOption delivery_option;
   @Nullable
   String seen_by_admin;
   @Nullable
   EventData event_data;
   @Nullable
   List reply_options;
   @Nullable
   Form form;
   @Nullable
   String client_assigned_uuid;
   @Nullable
   List sources;
   @Nullable
   AiAnswerInfo ai_answer_info;
   @Nullable
   Metadata metadata;
   boolean participant_is_admin;
   long created_at;
   boolean is_initial_message;

   public Part$Builder withId(String var1) {
      this.id = var1;
      return this;
   }

   public Part$Builder withIsInitialMessage(Boolean var1) {
      this.is_initial_message = var1;
      return this;
   }

   public Part$Builder withEventData(EventData var1) {
      this.event_data = var1;
      return this;
   }

   public Part$Builder withStyle(MessageStyle var1) {
      this.message_style = var1;
      return this;
   }

   public Part$Builder withSummary(String var1) {
      this.summary = var1;
      return this;
   }

   public Part$Builder withCreatedAt(long var1) {
      this.created_at = var1;
      return this;
   }

   public Part$Builder withParticipantIsAdmin(boolean var1) {
      this.participant_is_admin = var1;
      return this;
   }

   public Part$Builder withParticipantId(String var1) {
      this.participant_id = var1;
      return this;
   }

   public Part$Builder withBlocks(List var1) {
      this.body = var1;
      return this;
   }

   public Part$Builder withReplyOptions(List var1) {
      this.reply_options = var1;
      return this;
   }

   public Part$Builder withForm(Form var1) {
      this.form = var1;
      return this;
   }

   public Part$Builder withSources(List var1) {
      this.sources = var1;
      return this;
   }

   public Part$Builder withAiAnswerInfo(AiAnswerInfo var1) {
      this.ai_answer_info = var1;
      return this;
   }

   public Part$Builder withClientAssignedUuid(String var1) {
      this.client_assigned_uuid = var1;
      return this;
   }

   public Part$Builder withAttachments(List var1) {
      this.attachments = var1;
      return this;
   }

   public Part$Builder withSeenByAdmin(String var1) {
      this.seen_by_admin = var1;
      return this;
   }

   public Part$Builder withMetadata(Metadata var1) {
      this.metadata = var1;
      return this;
   }

   public Part$Builder withDeliveryOptions(DeliveryOption var1) {
      this.delivery_option = var1;
      return this;
   }

   public Part build() {
      return new Part(this, null);
   }
}
