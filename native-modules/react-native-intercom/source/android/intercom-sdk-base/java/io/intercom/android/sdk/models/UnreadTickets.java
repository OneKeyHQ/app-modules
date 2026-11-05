package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0081\b\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J#\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0003H×\u0001J\t\u0010\u0014\u001a\u00020\u0006H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/models/UnreadTickets;",
         "",
         "totalCount",
         "",
         "unreadTicketIds",
         "",
         "",
         "<init>",
         "(ILjava/util/List;)V",
         "getTotalCount",
         "()I",
         "getUnreadTicketIds",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UnreadTickets {
   @NotNull
   public static final UnreadTickets$Companion Companion = new UnreadTickets$Companion(null);
   @SerializedName("total_count")
   private final int totalCount;
   @SerializedName("unread_ticket_ids")
   @NotNull
   private final List unreadTicketIds;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final UnreadTickets NULL = new UnreadTickets(0, CollectionsKt.emptyList());

   public UnreadTickets(int var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "unreadTicketIds");
      this.totalCount = var1;
      this.unreadTicketIds = var2;
   }

   public final int getTotalCount() {
      return this.totalCount;
   }

   @NotNull
   public final List getUnreadTicketIds() {
      return this.unreadTicketIds;
   }

   public final int component1() {
      return this.totalCount;
   }

   @NotNull
   public final List component2() {
      return this.unreadTicketIds;
   }

   @NotNull
   public final UnreadTickets copy(int var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var2, "unreadTicketIds");
      return new UnreadTickets(var1, var2);
   }

   // $VF: synthetic method
   public static UnreadTickets copy$default(UnreadTickets var0, int var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.totalCount;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.unreadTicketIds;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "UnreadTickets(totalCount=" + this.totalCount + ", unreadTicketIds=" + this.unreadTicketIds + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.totalCount);
      return var1 * 31 + this.unreadTicketIds.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof UnreadTickets)) {
         return false;
      }

      UnreadTickets var2 = (UnreadTickets)var1;
      return this.totalCount != var2.totalCount ? false : Intrinsics.areEqual(this.unreadTicketIds, var2.unreadTicketIds);
   }

   // $VF: synthetic method
   public static final UnreadTickets access$getNULL$cp() {
      return NULL;
   }
}
