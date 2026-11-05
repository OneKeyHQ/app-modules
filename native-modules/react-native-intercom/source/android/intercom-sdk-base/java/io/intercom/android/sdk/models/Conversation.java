package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b#\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001Bù\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d\u0012\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f\u0012\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!\u0012\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b$\u0010%J\u0012\u0010D\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020F0EJ\f\u0010G\u001a\b\u0012\u0004\u0012\u00020H0\bJ\f\u0010I\u001a\b\u0012\u0004\u0012\u00020F0\bJ\u0006\u0010J\u001a\u00020KJ\u0006\u0010L\u001a\u00020HJ\u0006\u0010M\u001a\u00020HJ\t\u0010N\u001a\u00020\u0003HÆ\u0003J\t\u0010O\u001a\u00020\u0003HÆ\u0003J\t\u0010P\u001a\u00020\u0006HÆ\u0003J\u000f\u0010Q\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003J\u000f\u0010R\u001a\b\u0012\u0004\u0012\u00020\u000b0\bHÆ\u0003J\u000f\u0010S\u001a\b\u0012\u0004\u0012\u00020\u00030\bHÆ\u0003J\t\u0010T\u001a\u00020\u000eHÆ\u0003J\t\u0010U\u001a\u00020\u0010HÆ\u0003J\t\u0010V\u001a\u00020\u0006HÆ\u0003J\t\u0010W\u001a\u00020\u0006HÆ\u0003J\t\u0010X\u001a\u00020\u0003HÆ\u0003J\t\u0010Y\u001a\u00020\u0003HÆ\u0003J\t\u0010Z\u001a\u00020\u0006HÆ\u0003J\u000b\u0010[\u001a\u0004\u0018\u00010\u0017HÆ\u0003J\t\u0010\\\u001a\u00020\u0019HÆ\u0003J\u000b\u0010]\u001a\u0004\u0018\u00010\u001bHÆ\u0003J\u000b\u0010^\u001a\u0004\u0018\u00010\u001dHÆ\u0003J\u000b\u0010_\u001a\u0004\u0018\u00010\u001fHÆ\u0003J\u000b\u0010`\u001a\u0004\u0018\u00010!HÆ\u0003J\u000b\u0010a\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010b\u001a\u0004\u0018\u00010\u0003HÆ\u0003Jû\u0001\u0010c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00062\b\b\u0002\u0010\u0012\u001a\u00020\u00062\b\b\u0002\u0010\u0013\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00062\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010d\u001a\u00020\u00062\b\u0010e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010f\u001a\u00020gH×\u0001J\t\u0010h\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b(\u0010'R\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010)R\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b,\u0010+R\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b-\u0010+R\u0016\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0016\u0010\u000f\u001a\u00020\u00108\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0016\u0010\u0011\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b2\u0010)R\u0016\u0010\u0012\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b3\u0010)R\u0016\u0010\u0013\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b4\u0010'R\u0011\u0010\u0014\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b5\u0010'R\u0016\u0010\u0015\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010)R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0017¢\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0016\u0010\u0018\u001a\u00020\u00198\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u001b¢\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0018\u0010\u001c\u001a\u0004\u0018\u00010\u001d8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b<\u0010=R\u0018\u0010\u001e\u001a\u0004\u0018\u00010\u001f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b>\u0010?R\u0018\u0010 \u001a\u0004\u0018\u00010!8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b@\u0010AR\u0018\u0010\"\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bB\u0010'R\u0018\u0010#\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bC\u0010'¨\u0006i",
   d2 = {
         "Lio/intercom/android/sdk/models/Conversation;",
         "",
         "id",
         "",
         "title",
         "isRead",
         "",
         "participantBuilderList",
         "",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "partBuilderList",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "groupConversationParticipantIds",
         "lastParticipatingAdminBuilder",
         "Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;",
         "composerState",
         "Lio/intercom/android/sdk/models/ComposerState;",
         "preventEndUserReplies",
         "inboundConversationsDisabled",
         "notificationStatus",
         "state",
         "isInbound",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "uiFlags",
         "Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "header",
         "Lio/intercom/android/sdk/models/Header;",
         "conversationEndedButton",
         "Lio/intercom/android/sdk/models/ConversationEndedButton;",
         "footerNotice",
         "Lio/intercom/android/sdk/models/FooterNotice;",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "teamIntro",
         "specialNotice",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;ZLjava/util/List;Ljava/util/List;Ljava/util/List;Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;Lio/intercom/android/sdk/models/ComposerState;ZZLjava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/Ticket;Lio/intercom/android/sdk/models/ConversationUiFlags;Lio/intercom/android/sdk/models/Header;Lio/intercom/android/sdk/models/ConversationEndedButton;Lio/intercom/android/sdk/models/FooterNotice;Lio/intercom/android/sdk/models/PoweredBy;Ljava/lang/String;Ljava/lang/String;)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
         "()Z",
         "getParticipantBuilderList",
         "()Ljava/util/List;",
         "getPartBuilderList",
         "getGroupConversationParticipantIds",
         "getLastParticipatingAdminBuilder",
         "()Lio/intercom/android/sdk/models/LastParticipatingAdmin$Builder;",
         "getComposerState",
         "()Lio/intercom/android/sdk/models/ComposerState;",
         "getPreventEndUserReplies",
         "getInboundConversationsDisabled",
         "getNotificationStatus",
         "getState",
         "getTicket",
         "()Lio/intercom/android/sdk/models/Ticket;",
         "getUiFlags",
         "()Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "getHeader",
         "()Lio/intercom/android/sdk/models/Header;",
         "getConversationEndedButton",
         "()Lio/intercom/android/sdk/models/ConversationEndedButton;",
         "getFooterNotice",
         "()Lio/intercom/android/sdk/models/FooterNotice;",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "getTeamIntro",
         "getSpecialNotice",
         "getParticipants",
         "",
         "Lio/intercom/android/sdk/models/Participant;",
         "parts",
         "Lio/intercom/android/sdk/models/Part;",
         "groupConversationParticipants",
         "lastParticipatingAdmin",
         "Lio/intercom/android/sdk/models/LastParticipatingAdmin;",
         "lastPart",
         "getLastAdminPart",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "component12",
         "component13",
         "component14",
         "component15",
         "component16",
         "component17",
         "component18",
         "component19",
         "component20",
         "component21",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nConversation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Conversation.kt\nio/intercom/android/sdk/models/Conversation\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,75:1\n1193#2,2:76\n1267#2,4:78\n1563#2:82\n1634#2,2:83\n1636#2:86\n1617#2,9:87\n1869#2:96\n1870#2:98\n1626#2:99\n543#2,6:100\n1#3:85\n1#3:97\n*S KotlinDebug\n*F\n+ 1 Conversation.kt\nio/intercom/android/sdk/models/Conversation\n*L\n44#1:76,2\n44#1:78,4\n47#1:82\n47#1:83,2\n47#1:86\n53#1:87,9\n53#1:96\n53#1:98\n53#1:99\n59#1:100,6\n53#1:97\n*E\n"
)
public final class Conversation {
   @NotNull
   private final String id;
   @NotNull
   private final String title;
   @SerializedName("read")
   private final boolean isRead;
   @SerializedName("participants")
   @NotNull
   private final List participantBuilderList;
   @SerializedName("conversation_parts")
   @NotNull
   private final List partBuilderList;
   @SerializedName("group_conversation_participant_ids")
   @NotNull
   private final List groupConversationParticipantIds;
   @SerializedName("last_participating_admin")
   @NotNull
   private final LastParticipatingAdmin$Builder lastParticipatingAdminBuilder;
   @SerializedName("composer_state")
   @NotNull
   private final ComposerState composerState;
   @SerializedName("prevent_end_user_replies")
   private final boolean preventEndUserReplies;
   @SerializedName("inbound_conversations_disabled")
   private final boolean inboundConversationsDisabled;
   @SerializedName("notification_status")
   @NotNull
   private final String notificationStatus;
   @NotNull
   private final String state;
   @SerializedName("is_inbound")
   private final boolean isInbound;
   @Nullable
   private final Ticket ticket;
   @SerializedName("ui_flags")
   @NotNull
   private final ConversationUiFlags uiFlags;
   @Nullable
   private final Header header;
   @SerializedName("conversation_ended_button")
   @Nullable
   private final ConversationEndedButton conversationEndedButton;
   @SerializedName("footer_notice")
   @Nullable
   private final FooterNotice footerNotice;
   @SerializedName("powered_by")
   @Nullable
   private final PoweredBy poweredBy;
   @SerializedName("team_intro")
   @Nullable
   private final String teamIntro;
   @SerializedName("special_notice")
   @Nullable
   private final String specialNotice;
   public static final int $stable;

   static { $stable = 8; }

   public Conversation(
      @NotNull String var1,
      @NotNull String var2,
      boolean var3,
      @NotNull List var4,
      @NotNull List var5,
      @NotNull List var6,
      @NotNull LastParticipatingAdmin$Builder var7,
      @NotNull ComposerState var8,
      boolean var9,
      boolean var10,
      @NotNull String var11,
      @NotNull String var12,
      boolean var13,
      @Nullable Ticket var14,
      @NotNull ConversationUiFlags var15,
      @Nullable Header var16,
      @Nullable ConversationEndedButton var17,
      @Nullable FooterNotice var18,
      @Nullable PoweredBy var19,
      @Nullable String var20,
      @Nullable String var21
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "participantBuilderList");
      Intrinsics.checkNotNullParameter(var5, "partBuilderList");
      Intrinsics.checkNotNullParameter(var6, "groupConversationParticipantIds");
      Intrinsics.checkNotNullParameter(var7, "lastParticipatingAdminBuilder");
      Intrinsics.checkNotNullParameter(var8, "composerState");
      Intrinsics.checkNotNullParameter(var11, "notificationStatus");
      Intrinsics.checkNotNullParameter(var12, "state");
      Intrinsics.checkNotNullParameter(var15, "uiFlags");
      this.id = var1;
      this.title = var2;
      this.isRead = var3;
      this.participantBuilderList = var4;
      this.partBuilderList = var5;
      this.groupConversationParticipantIds = var6;
      this.lastParticipatingAdminBuilder = var7;
      this.composerState = var8;
      this.preventEndUserReplies = var9;
      this.inboundConversationsDisabled = var10;
      this.notificationStatus = var11;
      this.state = var12;
      this.isInbound = var13;
      this.ticket = var14;
      this.uiFlags = var15;
      this.header = var16;
      this.conversationEndedButton = var17;
      this.footerNotice = var18;
      this.poweredBy = var19;
      this.teamIntro = var20;
      this.specialNotice = var21;
   }

   // $VF: synthetic method
   public Conversation(
      String var1,
      String var2,
      boolean var3,
      List var4,
      List var5,
      List var6,
      LastParticipatingAdmin$Builder var7,
      ComposerState var8,
      boolean var9,
      boolean var10,
      String var11,
      String var12,
      boolean var13,
      Ticket var14,
      ConversationUiFlags var15,
      Header var16,
      ConversationEndedButton var17,
      FooterNotice var18,
      PoweredBy var19,
      String var20,
      String var21,
      int var22,
      DefaultConstructorMarker var23
   ) {
      this((((var22 & 1) != 0) ? (String) ("") : var1), (((var22 & 2) != 0) ? (String) ("") : var2), (((var22 & 4) != 0) ? (boolean) (false) : var3), (((var22 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4), (((var22 & 16) != 0) ? (List) (CollectionsKt.emptyList()) : var5), (((var22 & 32) != 0) ? (List) (CollectionsKt.emptyList()) : var6), (((var22 & 64) != 0) ? (LastParticipatingAdmin$Builder) (new LastParticipatingAdmin$Builder()) : var7), (((var22 & 128) != 0) ? (ComposerState) (ComposerState.Companion.getNULL()) : var8), (((var22 & 256) != 0) ? (boolean) (false) : var9), (((var22 & 512) != 0) ? (boolean) (false) : var10), (((var22 & 1024) != 0) ? (String) ("") : var11), (((var22 & 2048) != 0) ? (String) ("") : var12), (((var22 & 4096) != 0) ? (boolean) (false) : var13), (((var22 & 8192) != 0) ? (Ticket) (null) : var14), (((var22 & 16384) != 0) ? (ConversationUiFlags) (ConversationUiFlags.Companion.getDEFAULT()) : var15), (((var22 & 32768) != 0) ? (Header) (null) : var16), (((var22 & 65536) != 0) ? (ConversationEndedButton) (null) : var17), (((var22 & 131072) != 0) ? (FooterNotice) (null) : var18), (((var22 & 262144) != 0) ? (PoweredBy) (null) : var19), (((var22 & 524288) != 0) ? (String) (null) : var20), (((var22 & 1048576) != 0) ? (String) (null) : var21));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   public final boolean isRead() {
      return this.isRead;
   }

   @NotNull
   public final List getParticipantBuilderList() {
      return this.participantBuilderList;
   }

   @NotNull
   public final List getPartBuilderList() {
      return this.partBuilderList;
   }

   @NotNull
   public final List getGroupConversationParticipantIds() {
      return this.groupConversationParticipantIds;
   }

   @NotNull
   public final LastParticipatingAdmin$Builder getLastParticipatingAdminBuilder() {
      return this.lastParticipatingAdminBuilder;
   }

   @NotNull
   public final ComposerState getComposerState() {
      return this.composerState;
   }

   public final boolean getPreventEndUserReplies() {
      return this.preventEndUserReplies;
   }

   public final boolean getInboundConversationsDisabled() {
      return this.inboundConversationsDisabled;
   }

   @NotNull
   public final String getNotificationStatus() {
      return this.notificationStatus;
   }

   @NotNull
   public final String getState() {
      return this.state;
   }

   public final boolean isInbound() {
      return this.isInbound;
   }

   @Nullable
   public final Ticket getTicket() {
      return this.ticket;
   }

   @NotNull
   public final ConversationUiFlags getUiFlags() {
      return this.uiFlags;
   }

   @Nullable
   public final Header getHeader() {
      return this.header;
   }

   @Nullable
   public final ConversationEndedButton getConversationEndedButton() {
      return this.conversationEndedButton;
   }

   @Nullable
   public final FooterNotice getFooterNotice() {
      return this.footerNotice;
   }

   @Nullable
   public final PoweredBy getPoweredBy() {
      return this.poweredBy;
   }

   @Nullable
   public final String getTeamIntro() {
      return this.teamIntro;
   }

   @Nullable
   public final String getSpecialNotice() {
      return this.specialNotice;
   }

   @NotNull
   public final Map getParticipants() {
      Iterable var1 = this.participantBuilderList;
      int var2 = 0;
      int var3 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var1, 10)), 16);
      Iterable var4 = var1;
      Map var5 = new LinkedHashMap(var3);
      int var6 = 0;

      for (Object var8 : var4) {
         Map var9 = var5;
         Participant$Builder var10 = (Participant$Builder)var8;
         int var11 = 0;
         Pair var12 = TuplesKt.to(var10.build().getId(), var10.build());
         var9.put(var12.getFirst(), var12.getSecond());
      }

      return var5;
   }

   @NotNull
   public final List parts() {
      Iterable var1 = this.partBuilderList;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         Part$Builder var8 = (Part$Builder)var7;
         Collection var15 = var4;
         int var9 = 0;
         Part var10 = var8.build();
         Part var11 = var10;
         int var12 = 0;
         Participant var10000 = (Participant)this.getParticipants().get(var11.getParticipantId());
         if (var10000 != null) {
            Participant var13 = var10000;
            int var14 = 0;
            var11.setParticipant(var13);
         }

         var15.add(var10);
      }

      return (List)var4;
   }

   @NotNull
   public final List groupConversationParticipants() {
      Iterable var1 = this.groupConversationParticipantIds;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;
      Iterable var6 = var3;
      int var7 = 0;

      for (Object var9 : var6) {
         Object var10 = var9;
         int var11 = 0;
         String var12 = (String)var10;
         int var13 = 0;
         Participant var10000 = (Participant)this.getParticipants().get(var12);
         if (var10000 != null) {
            Object var14 = var10000;
            int var15 = 0;
            var4.add(var14);
         }
      }

      return (List)var4;
   }

   @NotNull
   public final LastParticipatingAdmin lastParticipatingAdmin() {
      LastParticipatingAdmin var10000 = this.lastParticipatingAdminBuilder.build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   @NotNull
   public final Part lastPart() {
      Part var10000 = (Part)CollectionsKt.lastOrNull(this.parts());
      if (var10000 == null) {
         var10000 = Part.NULL;
         Intrinsics.checkNotNullExpressionValue(Part.NULL, "NULL");
      }

      return var10000;
   }

   @NotNull
   public final Part getLastAdminPart() {
      List var1 = this.parts();
      int var2 = 0;
      ListIterator var3 = var1.listIterator(var1.size());

      Object var10000;
      while (true) {
         if (var3.hasPrevious()) {
            Object var4 = var3.previous();
            Part var5 = (Part)var4;
            int var6 = 0;
            if (!var5.isAdmin()) {
               continue;
            }

            var10000 = (Part)var4;
            break;
         }

         var10000 = null;
         break;
      }

      Part recoveredLastAdminPart = (Part)var10000;
      if (recoveredLastAdminPart == null) {
         recoveredLastAdminPart = Part.NULL;
         Intrinsics.checkNotNullExpressionValue(Part.NULL, "NULL");
      }

      return recoveredLastAdminPart;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   public final boolean component3() {
      return this.isRead;
   }

   @NotNull
   public final List component4() {
      return this.participantBuilderList;
   }

   @NotNull
   public final List component5() {
      return this.partBuilderList;
   }

   @NotNull
   public final List component6() {
      return this.groupConversationParticipantIds;
   }

   @NotNull
   public final LastParticipatingAdmin$Builder component7() {
      return this.lastParticipatingAdminBuilder;
   }

   @NotNull
   public final ComposerState component8() {
      return this.composerState;
   }

   public final boolean component9() {
      return this.preventEndUserReplies;
   }

   public final boolean component10() {
      return this.inboundConversationsDisabled;
   }

   @NotNull
   public final String component11() {
      return this.notificationStatus;
   }

   @NotNull
   public final String component12() {
      return this.state;
   }

   public final boolean component13() {
      return this.isInbound;
   }

   @Nullable
   public final Ticket component14() {
      return this.ticket;
   }

   @NotNull
   public final ConversationUiFlags component15() {
      return this.uiFlags;
   }

   @Nullable
   public final Header component16() {
      return this.header;
   }

   @Nullable
   public final ConversationEndedButton component17() {
      return this.conversationEndedButton;
   }

   @Nullable
   public final FooterNotice component18() {
      return this.footerNotice;
   }

   @Nullable
   public final PoweredBy component19() {
      return this.poweredBy;
   }

   @Nullable
   public final String component20() {
      return this.teamIntro;
   }

   @Nullable
   public final String component21() {
      return this.specialNotice;
   }

   @NotNull
   public final Conversation copy(
      @NotNull String var1,
      @NotNull String var2,
      boolean var3,
      @NotNull List var4,
      @NotNull List var5,
      @NotNull List var6,
      @NotNull LastParticipatingAdmin$Builder var7,
      @NotNull ComposerState var8,
      boolean var9,
      boolean var10,
      @NotNull String var11,
      @NotNull String var12,
      boolean var13,
      @Nullable Ticket var14,
      @NotNull ConversationUiFlags var15,
      @Nullable Header var16,
      @Nullable ConversationEndedButton var17,
      @Nullable FooterNotice var18,
      @Nullable PoweredBy var19,
      @Nullable String var20,
      @Nullable String var21
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "participantBuilderList");
      Intrinsics.checkNotNullParameter(var5, "partBuilderList");
      Intrinsics.checkNotNullParameter(var6, "groupConversationParticipantIds");
      Intrinsics.checkNotNullParameter(var7, "lastParticipatingAdminBuilder");
      Intrinsics.checkNotNullParameter(var8, "composerState");
      Intrinsics.checkNotNullParameter(var11, "notificationStatus");
      Intrinsics.checkNotNullParameter(var12, "state");
      Intrinsics.checkNotNullParameter(var15, "uiFlags");
      return new Conversation(
         var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21
      );
   }

   // $VF: synthetic method
   public static Conversation copy$default(
      Conversation var0,
      String var1,
      String var2,
      boolean var3,
      List var4,
      List var5,
      List var6,
      LastParticipatingAdmin$Builder var7,
      ComposerState var8,
      boolean var9,
      boolean var10,
      String var11,
      String var12,
      boolean var13,
      Ticket var14,
      ConversationUiFlags var15,
      Header var16,
      ConversationEndedButton var17,
      FooterNotice var18,
      PoweredBy var19,
      String var20,
      String var21,
      int var22,
      Object var23
   ) {
      if ((var22 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var22 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var22 & 4) != 0) {
         var3 = var0.isRead;
      }

      if ((var22 & 8) != 0) {
         var4 = var0.participantBuilderList;
      }

      if ((var22 & 16) != 0) {
         var5 = var0.partBuilderList;
      }

      if ((var22 & 32) != 0) {
         var6 = var0.groupConversationParticipantIds;
      }

      if ((var22 & 64) != 0) {
         var7 = var0.lastParticipatingAdminBuilder;
      }

      if ((var22 & 128) != 0) {
         var8 = var0.composerState;
      }

      if ((var22 & 256) != 0) {
         var9 = var0.preventEndUserReplies;
      }

      if ((var22 & 512) != 0) {
         var10 = var0.inboundConversationsDisabled;
      }

      if ((var22 & 1024) != 0) {
         var11 = var0.notificationStatus;
      }

      if ((var22 & 2048) != 0) {
         var12 = var0.state;
      }

      if ((var22 & 4096) != 0) {
         var13 = var0.isInbound;
      }

      if ((var22 & 8192) != 0) {
         var14 = var0.ticket;
      }

      if ((var22 & 16384) != 0) {
         var15 = var0.uiFlags;
      }

      if ((var22 & 32768) != 0) {
         var16 = var0.header;
      }

      if ((var22 & 65536) != 0) {
         var17 = var0.conversationEndedButton;
      }

      if ((var22 & 131072) != 0) {
         var18 = var0.footerNotice;
      }

      if ((var22 & 262144) != 0) {
         var19 = var0.poweredBy;
      }

      if ((var22 & 524288) != 0) {
         var20 = var0.teamIntro;
      }

      if ((var22 & 1048576) != 0) {
         var21 = var0.specialNotice;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Conversation(id=")
         .append(this.id)
         .append(", title=")
         .append(this.title)
         .append(", isRead=")
         .append(this.isRead)
         .append(", participantBuilderList=")
         .append(this.participantBuilderList)
         .append(", partBuilderList=")
         .append(this.partBuilderList)
         .append(", groupConversationParticipantIds=")
         .append(this.groupConversationParticipantIds)
         .append(", lastParticipatingAdminBuilder=")
         .append(this.lastParticipatingAdminBuilder)
         .append(", composerState=")
         .append(this.composerState)
         .append(", preventEndUserReplies=")
         .append(this.preventEndUserReplies)
         .append(", inboundConversationsDisabled=")
         .append(this.inboundConversationsDisabled)
         .append(", notificationStatus=")
         .append(this.notificationStatus)
         .append(", state=");
      var1.append(this.state)
         .append(", isInbound=")
         .append(this.isInbound)
         .append(", ticket=")
         .append(this.ticket)
         .append(", uiFlags=")
         .append(this.uiFlags)
         .append(", header=")
         .append(this.header)
         .append(", conversationEndedButton=")
         .append(this.conversationEndedButton)
         .append(", footerNotice=")
         .append(this.footerNotice)
         .append(", poweredBy=")
         .append(this.poweredBy)
         .append(", teamIntro=")
         .append(this.teamIntro)
         .append(", specialNotice=")
         .append(this.specialNotice)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isRead);
      var1 = var1 * 31 + this.participantBuilderList.hashCode();
      var1 = var1 * 31 + this.partBuilderList.hashCode();
      var1 = var1 * 31 + this.groupConversationParticipantIds.hashCode();
      var1 = var1 * 31 + this.lastParticipatingAdminBuilder.hashCode();
      var1 = var1 * 31 + this.composerState.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.preventEndUserReplies);
      var1 = var1 * 31 + Boolean.hashCode(this.inboundConversationsDisabled);
      var1 = var1 * 31 + this.notificationStatus.hashCode();
      var1 = var1 * 31 + this.state.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isInbound);
      var1 = var1 * 31 + (this.ticket == null ? 0 : this.ticket.hashCode());
      var1 = var1 * 31 + this.uiFlags.hashCode();
      var1 = var1 * 31 + (this.header == null ? 0 : this.header.hashCode());
      var1 = var1 * 31 + (this.conversationEndedButton == null ? 0 : this.conversationEndedButton.hashCode());
      var1 = var1 * 31 + (this.footerNotice == null ? 0 : this.footerNotice.hashCode());
      var1 = var1 * 31 + (this.poweredBy == null ? 0 : this.poweredBy.hashCode());
      var1 = var1 * 31 + (this.teamIntro == null ? 0 : this.teamIntro.hashCode());
      return var1 * 31 + (this.specialNotice == null ? 0 : this.specialNotice.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Conversation)) {
         return false;
      } else {
         Conversation var2 = (Conversation)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (this.isRead != var2.isRead) {
            return false;
         } else if (!Intrinsics.areEqual(this.participantBuilderList, var2.participantBuilderList)) {
            return false;
         } else if (!Intrinsics.areEqual(this.partBuilderList, var2.partBuilderList)) {
            return false;
         } else if (!Intrinsics.areEqual(this.groupConversationParticipantIds, var2.groupConversationParticipantIds)) {
            return false;
         } else if (!Intrinsics.areEqual(this.lastParticipatingAdminBuilder, var2.lastParticipatingAdminBuilder)) {
            return false;
         } else if (!Intrinsics.areEqual(this.composerState, var2.composerState)) {
            return false;
         } else if (this.preventEndUserReplies != var2.preventEndUserReplies) {
            return false;
         } else if (this.inboundConversationsDisabled != var2.inboundConversationsDisabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.notificationStatus, var2.notificationStatus)) {
            return false;
         } else if (!Intrinsics.areEqual(this.state, var2.state)) {
            return false;
         } else if (this.isInbound != var2.isInbound) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticket, var2.ticket)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uiFlags, var2.uiFlags)) {
            return false;
         } else if (!Intrinsics.areEqual(this.header, var2.header)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationEndedButton, var2.conversationEndedButton)) {
            return false;
         } else if (!Intrinsics.areEqual(this.footerNotice, var2.footerNotice)) {
            return false;
         } else if (!Intrinsics.areEqual(this.poweredBy, var2.poweredBy)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.teamIntro, var2.teamIntro) ? false : Intrinsics.areEqual(this.specialNotice, var2.specialNotice);
         }
      }
   }

   public Conversation() {
      this(null, null, false, null, null, null, null, null, false, false, null, null, false, null, null, null, null, null, null, null, null, 2097151, null);
   }
}
