package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b-\n\u0002\u0010\b\n\u0002\b\u0006\b\u0081\b\u0018\u0000 J2\u00020\u0001:\u0004GHIJB±\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\f\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0010¢\u0006\u0004\b\u0019\u0010\u001aJ\t\u00101\u001a\u00020\u0003HÆ\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u00103\u001a\u00020\u0003HÆ\u0003J\t\u00104\u001a\u00020\u0003HÆ\u0003J\t\u00105\u001a\u00020\u0003HÆ\u0003J\t\u00106\u001a\u00020\u0003HÆ\u0003J\t\u00107\u001a\u00020\nHÆ\u0003J\u000f\u00108\u001a\b\u0012\u0004\u0012\u00020\n0\fHÆ\u0003J\u000f\u00109\u001a\b\u0012\u0004\u0012\u00020\u000e0\fHÆ\u0003J\t\u0010:\u001a\u00020\u0010HÆ\u0003J\t\u0010;\u001a\u00020\u0012HÆ\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010=\u001a\u0004\u0018\u00010\u0015HÆ\u0003J\u0010\u0010>\u001a\u0004\u0018\u00010\u0017HÆ\u0003¢\u0006\u0002\u0010.J\t\u0010?\u001a\u00020\u0010HÆ\u0003J¸\u0001\u0010@\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\f2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0010HÇ\u0001¢\u0006\u0002\u0010AJ\u0013\u0010B\u001a\u00020\u00172\b\u0010C\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010D\u001a\u00020EH×\u0001J\t\u0010F\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001cR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001cR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001cR\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001cR\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b&\u0010%R\u0016\u0010\u000f\u001a\u00020\u00108\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0016\u0010\u0011\u001a\u00020\u00128\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001cR\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00158\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00178\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010/\u001a\u0004\b\u0016\u0010.R\u0016\u0010\u0018\u001a\u00020\u00108\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b0\u0010(¨\u0006K",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket;",
         "",
         "id",
         "",
         "publicId",
         "title",
         "description",
         "iconUrl",
         "emoji",
         "currentStatus",
         "Lio/intercom/android/sdk/models/Ticket$Status;",
         "statusList",
         "",
         "attributes",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute;",
         "ticketTypeId",
         "",
         "assignee",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "conversationId",
         "conversationButton",
         "Lio/intercom/android/sdk/models/Ticket$ConversationButton;",
         "isRead",
         "",
         "latestStatusUpdatedAt",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Ticket$Status;Ljava/util/List;Ljava/util/List;JLio/intercom/android/sdk/models/Participant$Builder;Ljava/lang/String;Lio/intercom/android/sdk/models/Ticket$ConversationButton;Ljava/lang/Boolean;J)V",
         "getId",
         "()Ljava/lang/String;",
         "getPublicId",
         "getTitle",
         "getDescription",
         "getIconUrl",
         "getEmoji",
         "getCurrentStatus",
         "()Lio/intercom/android/sdk/models/Ticket$Status;",
         "getStatusList",
         "()Ljava/util/List;",
         "getAttributes",
         "getTicketTypeId",
         "()J",
         "getAssignee",
         "()Lio/intercom/android/sdk/models/Participant$Builder;",
         "getConversationId",
         "getConversationButton",
         "()Lio/intercom/android/sdk/models/Ticket$ConversationButton;",
         "()Ljava/lang/Boolean;",
         "Ljava/lang/Boolean;",
         "getLatestStatusUpdatedAt",
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
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Ticket$Status;Ljava/util/List;Ljava/util/List;JLio/intercom/android/sdk/models/Participant$Builder;Ljava/lang/String;Lio/intercom/android/sdk/models/Ticket$ConversationButton;Ljava/lang/Boolean;J)Lio/intercom/android/sdk/models/Ticket;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "Status",
         "TicketAttribute",
         "ConversationButton",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Ticket {
   @NotNull
   public static final Ticket$Companion Companion = new Ticket$Companion(null);
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("public_ticket_id")
   @Nullable
   private final String publicId;
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("description")
   @NotNull
   private final String description;
   @SerializedName("icon_url")
   @NotNull
   private final String iconUrl;
   @SerializedName("emoji")
   @NotNull
   private final String emoji;
   @SerializedName("current_status")
   @NotNull
   private final Ticket$Status currentStatus;
   @SerializedName("status_list")
   @NotNull
   private final List statusList;
   @SerializedName("attributes")
   @NotNull
   private final List attributes;
   @SerializedName("ticket_type_id")
   private final long ticketTypeId;
   @SerializedName("assignee")
   @NotNull
   private final Participant$Builder assignee;
   @SerializedName("conversation_id")
   @Nullable
   private final String conversationId;
   @SerializedName("conversation_button")
   @Nullable
   private final Ticket$ConversationButton conversationButton;
   @SerializedName("read")
   @Nullable
   private final Boolean isRead;
   @SerializedName("latest_status_updated_at")
   private final long latestStatusUpdatedAt;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final Ticket NULL = new Ticket(null, null, null, null, null, null, null, null, null, 0L, null, null, null, null, 0L, 32767, null);

   public Ticket(
      @NotNull String var1,
      @Nullable String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull Ticket$Status var7,
      @NotNull List var8,
      @NotNull List var9,
      long var10,
      @NotNull Participant$Builder var12,
      @Nullable String var13,
      @Nullable Ticket$ConversationButton var14,
      @Nullable Boolean var15,
      long var16
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "description");
      Intrinsics.checkNotNullParameter(var5, "iconUrl");
      Intrinsics.checkNotNullParameter(var6, "emoji");
      Intrinsics.checkNotNullParameter(var7, "currentStatus");
      Intrinsics.checkNotNullParameter(var8, "statusList");
      Intrinsics.checkNotNullParameter(var9, "attributes");
      Intrinsics.checkNotNullParameter(var12, "assignee");
      this.id = var1;
      this.publicId = var2;
      this.title = var3;
      this.description = var4;
      this.iconUrl = var5;
      this.emoji = var6;
      this.currentStatus = var7;
      this.statusList = var8;
      this.attributes = var9;
      this.ticketTypeId = var10;
      this.assignee = var12;
      this.conversationId = var13;
      this.conversationButton = var14;
      this.isRead = var15;
      this.latestStatusUpdatedAt = var16;
   }

   // $VF: synthetic method
   public Ticket(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      Ticket$Status var7,
      List var8,
      List var9,
      long var10,
      Participant$Builder var12,
      String var13,
      Ticket$ConversationButton var14,
      Boolean var15,
      long var16,
      int var18,
      DefaultConstructorMarker var19
   ) {
      this((((var18 & 1) != 0) ? (String) ("") : var1), (((var18 & 2) != 0) ? (String) (null) : var2), (((var18 & 4) != 0) ? (String) ("") : var3), (((var18 & 8) != 0) ? (String) ("") : var4), (((var18 & 16) != 0) ? (String) ("") : var5), (((var18 & 32) != 0) ? (String) ("") : var6), (((var18 & 64) != 0) ? (Ticket$Status) (new Ticket$Status(null, null, null, false, 0L, 31, null)) : var7), (((var18 & 128) != 0) ? (List) (CollectionsKt.emptyList()) : var8), (((var18 & 256) != 0) ? (List) (CollectionsKt.emptyList()) : var9), (((var18 & 512) != 0) ? (long) (0L) : var10), (((var18 & 1024) != 0) ? (Participant$Builder) (new Participant$Builder()) : var12), (((var18 & 2048) != 0) ? (String) (null) : var13), (((var18 & 4096) != 0) ? (Ticket$ConversationButton) (null) : var14), (((var18 & 8192) != 0) ? (Boolean) (null) : var15), (((var18 & 16384) != 0) ? (long) (0L) : var16));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @Nullable
   public final String getPublicId() {
      return this.publicId;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getDescription() {
      return this.description;
   }

   @NotNull
   public final String getIconUrl() {
      return this.iconUrl;
   }

   @NotNull
   public final String getEmoji() {
      return this.emoji;
   }

   @NotNull
   public final Ticket$Status getCurrentStatus() {
      return this.currentStatus;
   }

   @NotNull
   public final List getStatusList() {
      return this.statusList;
   }

   @NotNull
   public final List getAttributes() {
      return this.attributes;
   }

   public final long getTicketTypeId() {
      return this.ticketTypeId;
   }

   @NotNull
   public final Participant$Builder getAssignee() {
      return this.assignee;
   }

   @Nullable
   public final String getConversationId() {
      return this.conversationId;
   }

   @Nullable
   public final Ticket$ConversationButton getConversationButton() {
      return this.conversationButton;
   }

   @Nullable
   public final Boolean isRead() {
      return this.isRead;
   }

   public final long getLatestStatusUpdatedAt() {
      return this.latestStatusUpdatedAt;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @Nullable
   public final String component2() {
      return this.publicId;
   }

   @NotNull
   public final String component3() {
      return this.title;
   }

   @NotNull
   public final String component4() {
      return this.description;
   }

   @NotNull
   public final String component5() {
      return this.iconUrl;
   }

   @NotNull
   public final String component6() {
      return this.emoji;
   }

   @NotNull
   public final Ticket$Status component7() {
      return this.currentStatus;
   }

   @NotNull
   public final List component8() {
      return this.statusList;
   }

   @NotNull
   public final List component9() {
      return this.attributes;
   }

   public final long component10() {
      return this.ticketTypeId;
   }

   @NotNull
   public final Participant$Builder component11() {
      return this.assignee;
   }

   @Nullable
   public final String component12() {
      return this.conversationId;
   }

   @Nullable
   public final Ticket$ConversationButton component13() {
      return this.conversationButton;
   }

   @Nullable
   public final Boolean component14() {
      return this.isRead;
   }

   public final long component15() {
      return this.latestStatusUpdatedAt;
   }

   @NotNull
   public final Ticket copy(
      @NotNull String var1,
      @Nullable String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull Ticket$Status var7,
      @NotNull List var8,
      @NotNull List var9,
      long var10,
      @NotNull Participant$Builder var12,
      @Nullable String var13,
      @Nullable Ticket$ConversationButton var14,
      @Nullable Boolean var15,
      long var16
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "description");
      Intrinsics.checkNotNullParameter(var5, "iconUrl");
      Intrinsics.checkNotNullParameter(var6, "emoji");
      Intrinsics.checkNotNullParameter(var7, "currentStatus");
      Intrinsics.checkNotNullParameter(var8, "statusList");
      Intrinsics.checkNotNullParameter(var9, "attributes");
      Intrinsics.checkNotNullParameter(var12, "assignee");
      return new Ticket(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, var13, var14, var15, var16);
   }

   // $VF: synthetic method
   public static Ticket copy$default(
      Ticket var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      Ticket$Status var7,
      List var8,
      List var9,
      long var10,
      Participant$Builder var12,
      String var13,
      Ticket$ConversationButton var14,
      Boolean var15,
      long var16,
      int var18,
      Object var19
   ) {
      if ((var18 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var18 & 2) != 0) {
         var2 = var0.publicId;
      }

      if ((var18 & 4) != 0) {
         var3 = var0.title;
      }

      if ((var18 & 8) != 0) {
         var4 = var0.description;
      }

      if ((var18 & 16) != 0) {
         var5 = var0.iconUrl;
      }

      if ((var18 & 32) != 0) {
         var6 = var0.emoji;
      }

      if ((var18 & 64) != 0) {
         var7 = var0.currentStatus;
      }

      if ((var18 & 128) != 0) {
         var8 = var0.statusList;
      }

      if ((var18 & 256) != 0) {
         var9 = var0.attributes;
      }

      if ((var18 & 512) != 0) {
         var10 = var0.ticketTypeId;
      }

      if ((var18 & 1024) != 0) {
         var12 = var0.assignee;
      }

      if ((var18 & 2048) != 0) {
         var13 = var0.conversationId;
      }

      if ((var18 & 4096) != 0) {
         var14 = var0.conversationButton;
      }

      if ((var18 & 8192) != 0) {
         var15 = var0.isRead;
      }

      if ((var18 & 16384) != 0) {
         var16 = var0.latestStatusUpdatedAt;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, var13, var14, var15, var16);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Ticket(id=")
         .append(this.id)
         .append(", publicId=")
         .append(this.publicId)
         .append(", title=")
         .append(this.title)
         .append(", description=")
         .append(this.description)
         .append(", iconUrl=")
         .append(this.iconUrl)
         .append(", emoji=")
         .append(this.emoji)
         .append(", currentStatus=")
         .append(this.currentStatus)
         .append(", statusList=")
         .append(this.statusList)
         .append(", attributes=")
         .append(this.attributes)
         .append(", ticketTypeId=")
         .append(this.ticketTypeId)
         .append(", assignee=")
         .append(this.assignee)
         .append(", conversationId=");
      var1.append(this.conversationId)
         .append(", conversationButton=")
         .append(this.conversationButton)
         .append(", isRead=")
         .append(this.isRead)
         .append(", latestStatusUpdatedAt=")
         .append(this.latestStatusUpdatedAt)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + (this.publicId == null ? 0 : this.publicId.hashCode());
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.description.hashCode();
      var1 = var1 * 31 + this.iconUrl.hashCode();
      var1 = var1 * 31 + this.emoji.hashCode();
      var1 = var1 * 31 + this.currentStatus.hashCode();
      var1 = var1 * 31 + this.statusList.hashCode();
      var1 = var1 * 31 + this.attributes.hashCode();
      var1 = var1 * 31 + Long.hashCode(this.ticketTypeId);
      var1 = var1 * 31 + this.assignee.hashCode();
      var1 = var1 * 31 + (this.conversationId == null ? 0 : this.conversationId.hashCode());
      var1 = var1 * 31 + (this.conversationButton == null ? 0 : this.conversationButton.hashCode());
      var1 = var1 * 31 + (this.isRead == null ? 0 : this.isRead.hashCode());
      return var1 * 31 + Long.hashCode(this.latestStatusUpdatedAt);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Ticket)) {
         return false;
      } else {
         Ticket var2 = (Ticket)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.publicId, var2.publicId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.description, var2.description)) {
            return false;
         } else if (!Intrinsics.areEqual(this.iconUrl, var2.iconUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.emoji, var2.emoji)) {
            return false;
         } else if (!Intrinsics.areEqual(this.currentStatus, var2.currentStatus)) {
            return false;
         } else if (!Intrinsics.areEqual(this.statusList, var2.statusList)) {
            return false;
         } else if (!Intrinsics.areEqual(this.attributes, var2.attributes)) {
            return false;
         } else if (this.ticketTypeId != var2.ticketTypeId) {
            return false;
         } else if (!Intrinsics.areEqual(this.assignee, var2.assignee)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationButton, var2.conversationButton)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.isRead, var2.isRead) ? false : this.latestStatusUpdatedAt == var2.latestStatusUpdatedAt;
         }
      }
   }

   public Ticket() {
      this(null, null, null, null, null, null, null, null, null, 0L, null, null, null, null, 0L, 32767, null);
   }

   // $VF: synthetic method
   public static final Ticket access$getNULL$cp() {
      return NULL;
   }
}
