package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\bHÆ\u0003J\t\u0010\u001a\u001a\u00020\nHÆ\u0003J;\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nHÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001f\u001a\u00020\bH×\u0001J\t\u0010 \u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/TicketLink;",
         "",
         "ticketTypeId",
         "",
         "ticketTypeName",
         "",
         "iconUrl",
         "displayOrder",
         "",
         "ticketType",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "<init>",
         "(JLjava/lang/String;Ljava/lang/String;ILio/intercom/android/sdk/blocks/lib/models/TicketType;)V",
         "getTicketTypeId",
         "()J",
         "getTicketTypeName",
         "()Ljava/lang/String;",
         "getIconUrl",
         "getDisplayOrder",
         "()I",
         "getTicketType",
         "()Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketLink {
   @SerializedName("ticket_type_id")
   private final long ticketTypeId;
   @SerializedName("ticket_type_name")
   @NotNull
   private final String ticketTypeName;
   @SerializedName("icon_url")
   @NotNull
   private final String iconUrl;
   @SerializedName("display_order")
   private final int displayOrder;
   @SerializedName("ticket_type")
   @NotNull
   private final TicketType ticketType;
   public static final int $stable;

   static { $stable = 8; }

   public TicketLink(long var1, @NotNull String var3, @NotNull String var4, int var5, @NotNull TicketType var6) {
      super();
      Intrinsics.checkNotNullParameter(var3, "ticketTypeName");
      Intrinsics.checkNotNullParameter(var4, "iconUrl");
      Intrinsics.checkNotNullParameter(var6, "ticketType");
      this.ticketTypeId = var1;
      this.ticketTypeName = var3;
      this.iconUrl = var4;
      this.displayOrder = var5;
      this.ticketType = var6;
   }

   public final long getTicketTypeId() {
      return this.ticketTypeId;
   }

   @NotNull
   public final String getTicketTypeName() {
      return this.ticketTypeName;
   }

   @NotNull
   public final String getIconUrl() {
      return this.iconUrl;
   }

   public final int getDisplayOrder() {
      return this.displayOrder;
   }

   @NotNull
   public final TicketType getTicketType() {
      return this.ticketType;
   }

   public final long component1() {
      return this.ticketTypeId;
   }

   @NotNull
   public final String component2() {
      return this.ticketTypeName;
   }

   @NotNull
   public final String component3() {
      return this.iconUrl;
   }

   public final int component4() {
      return this.displayOrder;
   }

   @NotNull
   public final TicketType component5() {
      return this.ticketType;
   }

   @NotNull
   public final TicketLink copy(long var1, @NotNull String var3, @NotNull String var4, int var5, @NotNull TicketType var6) {
      Intrinsics.checkNotNullParameter(var3, "ticketTypeName");
      Intrinsics.checkNotNullParameter(var4, "iconUrl");
      Intrinsics.checkNotNullParameter(var6, "ticketType");
      return new TicketLink(var1, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static TicketLink copy$default(TicketLink var0, long var1, String var3, String var4, int var5, TicketType var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.ticketTypeId;
      }

      if ((var7 & 2) != 0) {
         var3 = var0.ticketTypeName;
      }

      if ((var7 & 4) != 0) {
         var4 = var0.iconUrl;
      }

      if ((var7 & 8) != 0) {
         var5 = var0.displayOrder;
      }

      if ((var7 & 16) != 0) {
         var6 = var0.ticketType;
      }

      return var0.copy(var1, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketLink(ticketTypeId="
         + this.ticketTypeId
         + ", ticketTypeName="
         + this.ticketTypeName
         + ", iconUrl="
         + this.iconUrl
         + ", displayOrder="
         + this.displayOrder
         + ", ticketType="
         + this.ticketType
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.ticketTypeId);
      var1 = var1 * 31 + this.ticketTypeName.hashCode();
      var1 = var1 * 31 + this.iconUrl.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.displayOrder);
      return var1 * 31 + this.ticketType.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketLink)) {
         return false;
      } else {
         TicketLink var2 = (TicketLink)var1;
         if (this.ticketTypeId != var2.ticketTypeId) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticketTypeName, var2.ticketTypeName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.iconUrl, var2.iconUrl)) {
            return false;
         } else {
            return this.displayOrder != var2.displayOrder ? false : Intrinsics.areEqual(this.ticketType, var2.ticketType);
         }
      }
   }
}
