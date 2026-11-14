package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003JI\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 H×\u0003J\t\u0010!\u001a\u00020\"H×\u0001J\t\u0010#\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0014\u0010\u0014\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\r¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$TicketStatusRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "ticketEventStatus",
         "",
         "ticketStatusText",
         "createdAt",
         "",
         "customStateLabel",
         "customStatePrefix",
         "partId",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getTicketEventStatus",
         "()Ljava/lang/String;",
         "getTicketStatusText",
         "getCreatedAt",
         "()J",
         "getCustomStateLabel",
         "getCustomStatePrefix",
         "getPartId",
         "key",
         "getKey",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ContentRow$TicketStatusRow implements ContentRow {
   @NotNull
   private final String ticketEventStatus;
   @NotNull
   private final String ticketStatusText;
   private final long createdAt;
   @Nullable
   private final String customStateLabel;
   @Nullable
   private final String customStatePrefix;
   @NotNull
   private final String partId;
   public static int $stable;

   static {}

   public ContentRow$TicketStatusRow(@NotNull String var1, @NotNull String var2, long var3, @Nullable String var5, @Nullable String var6, @NotNull String var7) {
      super();
      Intrinsics.checkNotNullParameter(var1, "ticketEventStatus");
      Intrinsics.checkNotNullParameter(var2, "ticketStatusText");
      Intrinsics.checkNotNullParameter(var7, "partId");
      this.ticketEventStatus = var1;
      this.ticketStatusText = var2;
      this.createdAt = var3;
      this.customStateLabel = var5;
      this.customStatePrefix = var6;
      this.partId = var7;
   }

   @NotNull
   public final String getTicketEventStatus() {
      return this.ticketEventStatus;
   }

   @NotNull
   public final String getTicketStatusText() {
      return this.ticketStatusText;
   }

   public final long getCreatedAt() {
      return this.createdAt;
   }

   @Nullable
   public final String getCustomStateLabel() {
      return this.customStateLabel;
   }

   @Nullable
   public final String getCustomStatePrefix() {
      return this.customStatePrefix;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   @NotNull
   @Override
   public String getKey() {
      return "ticket_status_row_" + this.partId;
   }

   @NotNull
   public final String component1() {
      return this.ticketEventStatus;
   }

   @NotNull
   public final String component2() {
      return this.ticketStatusText;
   }

   public final long component3() {
      return this.createdAt;
   }

   @Nullable
   public final String component4() {
      return this.customStateLabel;
   }

   @Nullable
   public final String component5() {
      return this.customStatePrefix;
   }

   @NotNull
   public final String component6() {
      return this.partId;
   }

   @NotNull
   public final ContentRow$TicketStatusRow copy(
      @NotNull String var1, @NotNull String var2, long var3, @Nullable String var5, @Nullable String var6, @NotNull String var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "ticketEventStatus");
      Intrinsics.checkNotNullParameter(var2, "ticketStatusText");
      Intrinsics.checkNotNullParameter(var7, "partId");
      return new ContentRow$TicketStatusRow(var1, var2, var3, var5, var6, var7);
   }

   // $VF: synthetic method
   public static ContentRow$TicketStatusRow copy$default(
      ContentRow$TicketStatusRow var0, String var1, String var2, long var3, String var5, String var6, String var7, int var8, Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.ticketEventStatus;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.ticketStatusText;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.createdAt;
      }

      if ((var8 & 8) != 0) {
         var5 = var0.customStateLabel;
      }

      if ((var8 & 16) != 0) {
         var6 = var0.customStatePrefix;
      }

      if ((var8 & 32) != 0) {
         var7 = var0.partId;
      }

      return var0.copy(var1, var2, var3, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketStatusRow(ticketEventStatus="
         + this.ticketEventStatus
         + ", ticketStatusText="
         + this.ticketStatusText
         + ", createdAt="
         + this.createdAt
         + ", customStateLabel="
         + this.customStateLabel
         + ", customStatePrefix="
         + this.customStatePrefix
         + ", partId="
         + this.partId
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.ticketEventStatus.hashCode();
      var1 = var1 * 31 + this.ticketStatusText.hashCode();
      var1 = var1 * 31 + Long.hashCode(this.createdAt);
      var1 = var1 * 31 + (this.customStateLabel == null ? 0 : this.customStateLabel.hashCode());
      var1 = var1 * 31 + (this.customStatePrefix == null ? 0 : this.customStatePrefix.hashCode());
      return var1 * 31 + this.partId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$TicketStatusRow)) {
         return false;
      } else {
         ContentRow$TicketStatusRow var2 = (ContentRow$TicketStatusRow)var1;
         if (!Intrinsics.areEqual(this.ticketEventStatus, var2.ticketEventStatus)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticketStatusText, var2.ticketStatusText)) {
            return false;
         } else if (this.createdAt != var2.createdAt) {
            return false;
         } else if (!Intrinsics.areEqual(this.customStateLabel, var2.customStateLabel)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.customStatePrefix, var2.customStatePrefix) ? false : Intrinsics.areEqual(this.partId, var2.partId);
         }
      }
   }
}
