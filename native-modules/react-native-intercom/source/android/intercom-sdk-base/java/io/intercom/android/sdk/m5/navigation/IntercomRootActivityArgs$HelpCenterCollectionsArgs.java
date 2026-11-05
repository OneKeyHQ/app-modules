package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0004HÆ\u0003J#\u0010\u0010\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0004HÇ\u0001J\b\u0010\u0011\u001a\u00020\u0012H\u0007J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H×\u0003J\t\u0010\u0017\u001a\u00020\u0012H×\u0001J\t\u0010\u0018\u001a\u00020\u0004H×\u0001J\u0018\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0012H\u0007R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\u00048VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000b¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$HelpCenterCollectionsArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "collectionIds",
         "",
         "",
         "metricPlace",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;)V",
         "getCollectionIds",
         "()Ljava/util/List;",
         "getMetricPlace",
         "()Ljava/lang/String;",
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
@StabilityInferred(parameters = 0)
public final class IntercomRootActivityArgs$HelpCenterCollectionsArgs extends IntercomRootActivityArgs {
   @NotNull
   private final List collectionIds;
   @NotNull
   private final String metricPlace;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new IntercomRootActivityArgs$HelpCenterCollectionsArgs$Creator();

   public IntercomRootActivityArgs$HelpCenterCollectionsArgs(@NotNull List var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      super(null);
      this.collectionIds = var1;
      this.metricPlace = var2;
   }

   // $VF: synthetic method
   public IntercomRootActivityArgs$HelpCenterCollectionsArgs(List var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), var2);
   }

   @NotNull
   public final List getCollectionIds() {
      return this.collectionIds;
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
      var1.writeStringList(this.collectionIds);
      var1.writeString(this.metricPlace);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final List component1() {
      return this.collectionIds;
   }

   @NotNull
   public final String component2() {
      return this.metricPlace;
   }

   @NotNull
   public final IntercomRootActivityArgs$HelpCenterCollectionsArgs copy(@NotNull List var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      return new IntercomRootActivityArgs$HelpCenterCollectionsArgs(var1, var2);
   }

   // $VF: synthetic method
   public static IntercomRootActivityArgs$HelpCenterCollectionsArgs copy$default(
      IntercomRootActivityArgs$HelpCenterCollectionsArgs var0, List var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.collectionIds;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.metricPlace;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterCollectionsArgs(collectionIds=" + this.collectionIds + ", metricPlace=" + this.metricPlace + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.collectionIds.hashCode();
      return var1 * 31 + this.metricPlace.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomRootActivityArgs$HelpCenterCollectionsArgs)) {
         return false;
      }

      IntercomRootActivityArgs$HelpCenterCollectionsArgs var2 = (IntercomRootActivityArgs$HelpCenterCollectionsArgs)var1;
      return !Intrinsics.areEqual(this.collectionIds, var2.collectionIds) ? false : Intrinsics.areEqual(this.metricPlace, var2.metricPlace);
   }
}
