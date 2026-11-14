package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.tickets.TicketStatus;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\t2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0012¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "",
         "id",
         "",
         "title",
         "subTitle",
         "status",
         "Lio/intercom/android/sdk/tickets/TicketStatus;",
         "isRead",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/tickets/TicketStatus;Z)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
         "getSubTitle",
         "getStatus",
         "()Lio/intercom/android/sdk/tickets/TicketStatus;",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class TicketRowData {
   @NotNull
   private final String id;
   @NotNull
   private final String title;
   @NotNull
   private final String subTitle;
   @NotNull
   private final TicketStatus status;
   private final boolean isRead;
   public static int $stable;

   static {}

   public TicketRowData(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull TicketStatus var4, boolean var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "subTitle");
      Intrinsics.checkNotNullParameter(var4, "status");
      this.id = var1;
      this.title = var2;
      this.subTitle = var3;
      this.status = var4;
      this.isRead = var5;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getSubTitle() {
      return this.subTitle;
   }

   @NotNull
   public final TicketStatus getStatus() {
      return this.status;
   }

   public final boolean isRead() {
      return this.isRead;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final String component3() {
      return this.subTitle;
   }

   @NotNull
   public final TicketStatus component4() {
      return this.status;
   }

   public final boolean component5() {
      return this.isRead;
   }

   @NotNull
   public final TicketRowData copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull TicketStatus var4, boolean var5) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "subTitle");
      Intrinsics.checkNotNullParameter(var4, "status");
      return new TicketRowData(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static TicketRowData copy$default(TicketRowData var0, String var1, String var2, String var3, TicketStatus var4, boolean var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.subTitle;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.status;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.isRead;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketRowData(id="
         + this.id
         + ", title="
         + this.title
         + ", subTitle="
         + this.subTitle
         + ", status="
         + this.status
         + ", isRead="
         + this.isRead
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.subTitle.hashCode();
      var1 = var1 * 31 + this.status.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isRead);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketRowData)) {
         return false;
      } else {
         TicketRowData var2 = (TicketRowData)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subTitle, var2.subTitle)) {
            return false;
         } else {
            return this.status != var2.status ? false : this.isRead == var2.isRead;
         }
      }
   }
}
