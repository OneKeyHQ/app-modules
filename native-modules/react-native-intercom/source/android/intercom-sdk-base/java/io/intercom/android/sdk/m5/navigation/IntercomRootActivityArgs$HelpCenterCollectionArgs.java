package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\b\u0010\u000f\u001a\u00020\u0010H\u0007J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0010H×\u0001J\t\u0010\u0016\u001a\u00020\u0003H×\u0001J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0010H\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0014\u0010\n\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\b¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$HelpCenterCollectionArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "collectionId",
         "",
         "metricPlace",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getCollectionId",
         "()Ljava/lang/String;",
         "getMetricPlace",
         "route",
         "getRoute",
         "component1",
         "component2",
         "copy",
         "describeContents",
         "",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomRootActivityArgs$HelpCenterCollectionArgs extends IntercomRootActivityArgs {
   @NotNull
   private final String collectionId;
   @NotNull
   private final String metricPlace;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new IntercomRootActivityArgs$HelpCenterCollectionArgs$Creator();

   public IntercomRootActivityArgs$HelpCenterCollectionArgs(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      super(null);
      this.collectionId = var1;
      this.metricPlace = var2;
   }

   @NotNull
   public final String getCollectionId() {
      return this.collectionId;
   }

   @NotNull
   public final String getMetricPlace() {
      return this.metricPlace;
   }

   @NotNull
   @Override
   public String getRoute() {
      return "HELP_CENTER";
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.collectionId);
      var1.writeString(this.metricPlace);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.collectionId;
   }

   @NotNull
   public final String component2() {
      return this.metricPlace;
   }

   @NotNull
   public final IntercomRootActivityArgs$HelpCenterCollectionArgs copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      return new IntercomRootActivityArgs$HelpCenterCollectionArgs(var1, var2);
   }

   // $VF: synthetic method
   public static IntercomRootActivityArgs$HelpCenterCollectionArgs copy$default(
      IntercomRootActivityArgs$HelpCenterCollectionArgs var0, String var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.collectionId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.metricPlace;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterCollectionArgs(collectionId=" + this.collectionId + ", metricPlace=" + this.metricPlace + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.collectionId.hashCode();
      return var1 * 31 + this.metricPlace.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomRootActivityArgs$HelpCenterCollectionArgs)) {
         return false;
      }

      IntercomRootActivityArgs$HelpCenterCollectionArgs var2 = (IntercomRootActivityArgs$HelpCenterCollectionArgs)var1;
      return !Intrinsics.areEqual(this.collectionId, var2.collectionId) ? false : Intrinsics.areEqual(this.metricPlace, var2.metricPlace);
   }
}
