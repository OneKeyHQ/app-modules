package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.utilities.NullSafety;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kotlin.Deprecated;

public class Part implements Parcelable {
   private final String id;
   private final List attachments;
   private final String summary;
   private final ReactionReply reactionReply;
   private final boolean isInitialMessage;
   private final long createdAt;
   private final boolean participantIsAdmin;
   private final MessageStyle messageStyle;
   private final DeliveryOption deliveryOption;
   private final List blocks;
   private final EventData eventData;
   private final List replyOptions;
   private final Form form;
   private String clientAssignedUuid;
   private final List sources;
   private final AiAnswerInfo aiAnswerInfo;
   private String participantId;
   private String seenByAdmin;
   @Nullable
   private final Metadata metadata;
   private Conversation parentConversation;
   private Part$MessageState messageState;
   private Participant participant;
   public static final Part NULL = new Part(new Part$Builder());
   public static final Creator CREATOR = new Part$1();

   public Part() {
      this(new Part$Builder());
   }

   private Part(Part$Builder var1) {
      this.id = var1.id == null ? UUID.randomUUID().toString() : var1.id;
      this.participantId = NullSafety.valueOrEmpty(var1.participant_id);
      this.participantIsAdmin = var1.participant_is_admin;
      this.summary = NullSafety.valueOrEmpty(var1.summary);
      this.createdAt = var1.created_at;
      this.messageStyle = var1.message_style == null ? MessageStyle.CHAT : var1.message_style;
      this.deliveryOption = var1.delivery_option == null ? DeliveryOption.SUMMARY : var1.delivery_option;
      this.blocks = new ArrayList();
      if (var1.body != null) {
         for (Object recoveredBlockBuilder : var1.body) {
            Block$Builder var3 = (Block$Builder)recoveredBlockBuilder;
            this.blocks.add(var3.build());
         }
      }

      this.reactionReply = var1.reactions_reply == null ? ReactionReply.NULL : var1.reactions_reply.build();
      this.seenByAdmin = var1.seen_by_admin == null ? "hide" : var1.seen_by_admin;
      this.attachments = new ArrayList();
      if (var1.attachments != null) {
         for (Object recoveredAttachment : var1.attachments) {
            Attachments var5 = (Attachments)recoveredAttachment;
            if (var5 != null) {
               this.attachments.add(var5);
            }
         }
      }

      this.eventData = var1.event_data == null ? EventData.NULL : var1.event_data;
      this.parentConversation = new Conversation();
      this.participant = Participant.NULL;
      this.isInitialMessage = var1.is_initial_message;
      this.messageState = Part$MessageState.NORMAL;
      this.replyOptions = new ArrayList();
      if (var1.reply_options != null) {
         this.replyOptions.addAll(var1.reply_options);
      }

      this.form = var1.form == null ? new Form("", Collections.emptyList(), false) : var1.form;
      this.clientAssignedUuid = NullSafety.valueOrEmpty(var1.client_assigned_uuid);
      this.sources = new ArrayList();
      if (var1.sources != null) {
         this.sources.addAll(var1.sources);
      }

      this.aiAnswerInfo = var1.ai_answer_info == null ? new AiAnswerInfo("") : var1.ai_answer_info;
      this.metadata = var1.metadata;
   }

   public DeliveryOption getDeliveryOption() {
      return this.deliveryOption;
   }

   public String getId() {
      return this.id;
   }

   public String getParticipantId() {
      return this.participantId;
   }

   public boolean isAdmin() {
      return this.participantIsAdmin;
   }

   public boolean isUser() {
      return !this.isAdmin();
   }

   public List getBlocks() {
      return this.blocks;
   }

   public List getAttachments() {
      return this.attachments;
   }

   public List getReplyOptions() {
      return this.replyOptions;
   }

   public Form getForm() {
      return this.form;
   }

   public List getSources() {
      return this.sources;
   }

   public AiAnswerInfo getAiAnswerInfo() {
      return this.aiAnswerInfo;
   }

   public boolean hasAttachments() {
      return !this.attachments.isEmpty();
   }

   public MessageStyle getMessageStyle() {
      return this.messageStyle;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   public String getSummary() {
      return this.summary;
   }

   public ReactionReply getReactionReply() {
      return this.reactionReply;
   }

   public String getSeenByAdmin() {
      return this.participantIsAdmin ? "hide" : this.seenByAdmin;
   }

   public Participant getParticipant() {
      return this.participant;
   }

   public void setParticipant(Participant var1) {
      this.participant = var1;
      this.participantId = var1.getId();
   }

   @Deprecated(
      message = "This was added for use in tickets. With conversation rewrite, we should use {@link ContentRow.MessageRow.PartWrapper )} instead to add in data that we require from the parent conversation object."
   )
   public Conversation getParentConversation() {
      return this.parentConversation;
   }

   public void setParentConversation(Conversation var1) {
      this.parentConversation = var1;
   }

   public Part$MessageState getMessageState() {
      return this.messageState;
   }

   public void setMessageState(Part$MessageState var1) {
      this.messageState = var1;
   }

   public boolean isInitialMessage() {
      return this.isInitialMessage;
   }

   public boolean isReply() {
      return !this.isInitialMessage;
   }

   public String getUuid() {
      return this.clientAssignedUuid;
   }

   public EventData getEventData() {
      return this.eventData;
   }

   @Nullable
   public Metadata getMetadata() {
      return this.metadata;
   }

   public Boolean isEvent() {
      return !this.eventData.getEventAsPlainText().isEmpty()
         || !this.eventData.getEventContent().isEmpty()
         || this.eventData.getCustomStateLabel() != null && !this.eventData.getCustomStateLabel().isEmpty()
         || this.eventData.getMergedConversationId() != null && !this.eventData.getMergedConversationId().isEmpty();
   }

   private Part(Parcel var1) {
      this.id = var1.readString();
      this.participantId = var1.readString();
      this.participantIsAdmin = var1.readByte() != 0;
      this.blocks = new ArrayList();
      var1.readList(this.blocks, Block.class.getClassLoader());
      this.attachments = new ArrayList();
      var1.readList(this.attachments, Attachments.class.getClassLoader());
      this.replyOptions = new ArrayList();
      var1.readList(this.replyOptions, ReplyOption.class.getClassLoader());
      this.messageStyle = (MessageStyle)var1.readValue(MessageStyle.class.getClassLoader());
      this.createdAt = var1.readLong();
      this.summary = var1.readString();
      boolean var2 = var1.readByte() == 0;
      if (var2) {
         this.reactionReply = ReactionReply.NULL;
      } else {
         this.reactionReply = (ReactionReply)var1.readValue(ReactionReply.class.getClassLoader());
      }

      this.seenByAdmin = var1.readString();
      this.participant = (Participant)var1.readValue(Participant.class.getClassLoader());
      this.eventData = (EventData)var1.readValue(EventData.class.getClassLoader());
      this.isInitialMessage = var1.readByte() != 0;
      this.deliveryOption = (DeliveryOption)var1.readValue(DeliveryOption.class.getClassLoader());
      this.form = (Form)var1.readValue(Form.class.getClassLoader());
      this.sources = new ArrayList();
      var1.readList(this.sources, Source.class.getClassLoader());
      this.aiAnswerInfo = (AiAnswerInfo)var1.readValue(AiAnswerInfo.class.getClassLoader());
      this.metadata = (Metadata)var1.readValue(Metadata.class.getClassLoader());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.id);
      var1.writeString(this.participantId);
      var1.writeByte((byte)(this.participantIsAdmin ? 1 : 0));
      var1.writeList(this.blocks);
      var1.writeList(this.attachments);
      var1.writeList(this.replyOptions);
      var1.writeValue(this.messageStyle);
      var1.writeLong(this.createdAt);
      var1.writeString(this.summary);
      if (ReactionReply.isNull(this.reactionReply)) {
         var1.writeByte((byte)0);
      } else {
         var1.writeByte((byte)1);
         var1.writeValue(this.reactionReply);
      }

      var1.writeString(this.seenByAdmin);
      var1.writeValue(this.participant);
      var1.writeValue(this.eventData);
      var1.writeByte((byte)(this.isInitialMessage ? 1 : 0));
      var1.writeValue(this.deliveryOption);
      var1.writeValue(this.form);
      var1.writeList(this.sources);
      var1.writeValue(this.aiAnswerInfo);
      var1.writeValue(this.metadata);
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Part)) {
         return false;
      }

      Part var2 = (Part)var1;
      return (
            this.id.equals(var2.id) && this.form.equals(var2.form)
               || !this.clientAssignedUuid.equals("") && this.clientAssignedUuid.equals(var2.clientAssignedUuid)
         )
         && this.seenByAdmin.equals(var2.seenByAdmin);
   }

   // $VF: synthetic method
   Part(Part$Builder var1, Part$1 var2) {
      this(var1);
   }

   // $VF: synthetic method
   Part(Parcel var1, Part$1 var2) {
      this(var1);
   }
}
