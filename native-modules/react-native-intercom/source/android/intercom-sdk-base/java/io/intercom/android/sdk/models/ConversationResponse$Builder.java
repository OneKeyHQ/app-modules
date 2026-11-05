package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010D\u001a\u00020EH\u0016R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR \u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R \u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0014\"\u0004\b\u001a\u0010\u0016R \u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0014\"\u0004\b\u001d\u0010\u0016R\u001a\u0010\u001e\u001a\u00020\u001fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u001c\u0010$\u001a\u0004\u0018\u00010%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u001a\u0010*\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\r\"\u0004\b,\u0010\u000fR\u001a\u0010-\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010\r\"\u0004\b/\u0010\u000fR\u001c\u00100\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\u0007\"\u0004\b2\u0010\tR\u001c\u00103\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\u0007\"\u0004\b5\u0010\tR\u001a\u00106\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b6\u0010\r\"\u0004\b7\u0010\u000fR\u001c\u00108\u001a\u0004\u0018\u000109X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b:\u0010;\"\u0004\b<\u0010=R\u001c\u0010>\u001a\u0004\u0018\u00010?X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b@\u0010A\"\u0004\bB\u0010C¨\u0006F",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationResponse$Builder;",
         "Lio/intercom/android/sdk/models/BaseResponse$Builder;",
         "<init>",
         "()V",
         "id",
         "",
         "getId",
         "()Ljava/lang/String;",
         "setId",
         "(Ljava/lang/String;)V",
         "read",
         "",
         "getRead",
         "()Z",
         "setRead",
         "(Z)V",
         "participants",
         "",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "getParticipants",
         "()Ljava/util/List;",
         "setParticipants",
         "(Ljava/util/List;)V",
         "conversation_parts",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "getConversation_parts",
         "setConversation_parts",
         "group_conversation_participant_ids",
         "getGroup_conversation_participant_ids",
         "setGroup_conversation_participant_ids",
         "last_participating_admin",
         "Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;",
         "getLast_participating_admin",
         "()Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;",
         "setLast_participating_admin",
         "(Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;)V",
         "composer_state",
         "Lio/intercom/android/sdk/models/ComposerState;",
         "getComposer_state",
         "()Lio/intercom/android/sdk/models/ComposerState;",
         "setComposer_state",
         "(Lio/intercom/android/sdk/models/ComposerState;)V",
         "prevent_end_user_replies",
         "getPrevent_end_user_replies",
         "setPrevent_end_user_replies",
         "inbound_conversations_disabled",
         "getInbound_conversations_disabled",
         "setInbound_conversations_disabled",
         "notification_status",
         "getNotification_status",
         "setNotification_status",
         "state",
         "getState",
         "setState",
         "is_inbound",
         "set_inbound",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "getTicket",
         "()Lio/intercom/android/sdk/models/Ticket;",
         "setTicket",
         "(Lio/intercom/android/sdk/models/Ticket;)V",
         "ui_flags",
         "Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "getUi_flags",
         "()Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "setUi_flags",
         "(Lio/intercom/android/sdk/models/ConversationUiFlags;)V",
         "build",
         "Lio/intercom/android/sdk/models/ConversationResponse;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConversationResponse$Builder extends BaseResponse$Builder {
   @Nullable
   private String id;
   private boolean read;
   @NotNull
   private List participants = CollectionsKt.emptyList();
   @NotNull
   private List conversation_parts = CollectionsKt.emptyList();
   @NotNull
   private List group_conversation_participant_ids = CollectionsKt.emptyList();
   @NotNull
   private LastParticipatingAdmin$Builder last_participating_admin = new LastParticipatingAdmin$Builder();
   @Nullable
   private ComposerState composer_state;
   private boolean prevent_end_user_replies;
   private boolean inbound_conversations_disabled;
   @Nullable
   private String notification_status;
   @Nullable
   private String state;
   private boolean is_inbound;
   @Nullable
   private Ticket ticket;
   @Nullable
   private ConversationUiFlags ui_flags;
   public static final int $stable;

   static { $stable = 8; }

   @Nullable
   public final String getId() {
      return this.id;
   }

   public final void setId(@Nullable String var1) {
      this.id = var1;
   }

   public final boolean getRead() {
      return this.read;
   }

   public final void setRead(boolean var1) {
      this.read = var1;
   }

   @NotNull
   public final List getParticipants() {
      return this.participants;
   }

   public final void setParticipants(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.participants = var1;
   }

   @NotNull
   public final List getConversation_parts() {
      return this.conversation_parts;
   }

   public final void setConversation_parts(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.conversation_parts = var1;
   }

   @NotNull
   public final List getGroup_conversation_participant_ids() {
      return this.group_conversation_participant_ids;
   }

   public final void setGroup_conversation_participant_ids(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.group_conversation_participant_ids = var1;
   }

   @NotNull
   public final LastParticipatingAdmin$Builder getLast_participating_admin() {
      return this.last_participating_admin;
   }

   public final void setLast_participating_admin(@NotNull LastParticipatingAdmin$Builder var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.last_participating_admin = var1;
   }

   @Nullable
   public final ComposerState getComposer_state() {
      return this.composer_state;
   }

   public final void setComposer_state(@Nullable ComposerState var1) {
      this.composer_state = var1;
   }

   public final boolean getPrevent_end_user_replies() {
      return this.prevent_end_user_replies;
   }

   public final void setPrevent_end_user_replies(boolean var1) {
      this.prevent_end_user_replies = var1;
   }

   public final boolean getInbound_conversations_disabled() {
      return this.inbound_conversations_disabled;
   }

   public final void setInbound_conversations_disabled(boolean var1) {
      this.inbound_conversations_disabled = var1;
   }

   @Nullable
   public final String getNotification_status() {
      return this.notification_status;
   }

   public final void setNotification_status(@Nullable String var1) {
      this.notification_status = var1;
   }

   @Nullable
   public final String getState() {
      return this.state;
   }

   public final void setState(@Nullable String var1) {
      this.state = var1;
   }

   public final boolean is_inbound() {
      return this.is_inbound;
   }

   public final void set_inbound(boolean var1) {
      this.is_inbound = var1;
   }

   @Nullable
   public final Ticket getTicket() {
      return this.ticket;
   }

   public final void setTicket(@Nullable Ticket var1) {
      this.ticket = var1;
   }

   @Nullable
   public final ConversationUiFlags getUi_flags() {
      return this.ui_flags;
   }

   public final void setUi_flags(@Nullable ConversationUiFlags var1) {
      this.ui_flags = var1;
   }

   @NotNull
   public ConversationResponse build() {
      return new ConversationResponse(this);
   }

}
