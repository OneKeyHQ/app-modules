package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B9\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00072\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0010R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$Status;",
         "",
         "title",
         "",
         "type",
         "statusDetail",
         "isCurrentStatus",
         "",
         "createdDate",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZJ)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getType",
         "getStatusDetail",
         "()Z",
         "getCreatedDate",
         "()J",
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
public final class Ticket$Status {
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("type")
   @NotNull
   private final String type;
   @SerializedName("status_detail")
   @NotNull
   private final String statusDetail;
   @SerializedName("is_current_status")
   private final boolean isCurrentStatus;
   @SerializedName("created_date")
   private final long createdDate;
   public static int $stable;

   static {}

   public Ticket$Status(@NotNull String var1, @NotNull String var2, @NotNull String var3, boolean var4, long var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "statusDetail");
      this.title = var1;
      this.type = var2;
      this.statusDetail = var3;
      this.isCurrentStatus = var4;
      this.createdDate = var5;
   }

   // $VF: synthetic method
   public Ticket$Status(String var1, String var2, String var3, boolean var4, long var5, int var7, DefaultConstructorMarker var8) {
      this((((var7 & 1) != 0) ? (String) ("") : var1), (((var7 & 2) != 0) ? (String) ("") : var2), (((var7 & 4) != 0) ? (String) ("") : var3), (((var7 & 8) != 0) ? (boolean) (false) : var4), (((var7 & 16) != 0) ? (long) (0L) : var5));
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   @NotNull
   public final String getStatusDetail() {
      return this.statusDetail;
   }

   public final boolean isCurrentStatus() {
      return this.isCurrentStatus;
   }

   public final long getCreatedDate() {
      return this.createdDate;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final String component2() {
      return this.type;
   }

   @NotNull
   public final String component3() {
      return this.statusDetail;
   }

   public final boolean component4() {
      return this.isCurrentStatus;
   }

   public final long component5() {
      return this.createdDate;
   }

   @NotNull
   public final Ticket$Status copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, boolean var4, long var5) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "statusDetail");
      return new Ticket$Status(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static Ticket$Status copy$default(Ticket$Status var0, String var1, String var2, String var3, boolean var4, long var5, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.type;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.statusDetail;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.isCurrentStatus;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.createdDate;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Status(title="
         + this.title
         + ", type="
         + this.type
         + ", statusDetail="
         + this.statusDetail
         + ", isCurrentStatus="
         + this.isCurrentStatus
         + ", createdDate="
         + this.createdDate
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + this.type.hashCode();
      var1 = var1 * 31 + this.statusDetail.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isCurrentStatus);
      return var1 * 31 + Long.hashCode(this.createdDate);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Ticket$Status)) {
         return false;
      } else {
         Ticket$Status var2 = (Ticket$Status)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.type, var2.type)) {
            return false;
         } else if (!Intrinsics.areEqual(this.statusDetail, var2.statusDetail)) {
            return false;
         } else {
            return this.isCurrentStatus != var2.isCurrentStatus ? false : this.createdDate == var2.createdDate;
         }
      }
   }

   public Ticket$Status() {
      this(null, null, null, false, 0L, 31, null);
   }
}
