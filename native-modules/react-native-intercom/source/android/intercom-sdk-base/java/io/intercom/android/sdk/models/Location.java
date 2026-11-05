package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B-\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0002\u0010\nJ2\u0010\u0014\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÇ\u0001¢\u0006\u0002\u0010\u0015J\b\u0010\u0016\u001a\u00020\u0006H\u0007J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH×\u0003J\t\u0010\u001b\u001a\u00020\u0006H×\u0001J\t\u0010\u001c\u001a\u00020\u0003H×\u0001J\u0018\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0006H\u0007R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0011\u0010\f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u000e¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/models/Location;",
         "Landroid/os/Parcelable;",
         "_cityName",
         "",
         "_countryName",
         "timezoneOffset",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)V",
         "getTimezoneOffset",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "cityName",
         "getCityName",
         "()Ljava/lang/String;",
         "countryName",
         "getCountryName",
         "component1",
         "component2",
         "component3",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)Lio/intercom/android/sdk/models/Location;",
         "describeContents",
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
@Parcelize
@StabilityInferred(parameters = 1)
public final class Location implements Parcelable {
   @SerializedName("city_name")
   @Nullable
   private final String _cityName;
   @SerializedName("country_name")
   @Nullable
   private final String _countryName;
   @SerializedName("timezone_offset")
   @Nullable
   private final Integer timezoneOffset;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new Location$Creator();

   @JvmOverloads
   public Location(@Nullable String var1, @Nullable String var2, @Nullable Integer var3) {
      this._cityName = var1;
      this._countryName = var2;
      this.timezoneOffset = var3;
   }

   // $VF: synthetic method
   public Location(String var1, String var2, Integer var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) (null) : var1), (((var4 & 2) != 0) ? (String) (null) : var2), (((var4 & 4) != 0) ? (Integer) (null) : var3));
   }

   @Nullable
   public final Integer getTimezoneOffset() {
      return this.timezoneOffset;
   }

   @NotNull
   public final String getCityName() {
      String var10000 = this._cityName;
      if (this._cityName == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getCountryName() {
      String var10000 = this._countryName;
      if (this._countryName == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this._cityName);
      var1.writeString(this._countryName);
      Integer var3 = this.timezoneOffset;
      if (var3 == null) {
         var1.writeInt(0);
      } else {
         var1.writeInt(1);
         var1.writeInt(var3);
      }
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   private final String component1() {
      return this._cityName;
   }

   private final String component2() {
      return this._countryName;
   }

   @Nullable
   public final Integer component3() {
      return this.timezoneOffset;
   }

   @NotNull
   public final Location copy(@Nullable String var1, @Nullable String var2, @Nullable Integer var3) {
      return new Location(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Location copy$default(Location var0, String var1, String var2, Integer var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0._cityName;
      }

      if ((var4 & 2) != 0) {
         var2 = var0._countryName;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.timezoneOffset;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Location(_cityName=" + this._cityName + ", _countryName=" + this._countryName + ", timezoneOffset=" + this.timezoneOffset + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._cityName == null ? 0 : this._cityName.hashCode();
      var1 = var1 * 31 + (this._countryName == null ? 0 : this._countryName.hashCode());
      return var1 * 31 + (this.timezoneOffset == null ? 0 : this.timezoneOffset.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Location)) {
         return false;
      } else {
         Location var2 = (Location)var1;
         if (!Intrinsics.areEqual(this._cityName, var2._cityName)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this._countryName, var2._countryName) ? false : Intrinsics.areEqual(this.timezoneOffset, var2.timezoneOffset);
         }
      }
   }

   @JvmOverloads
   public Location(@Nullable String var1, @Nullable String var2) {
      this(var1, var2, null, 4, null);
   }

   @JvmOverloads
   public Location(@Nullable String var1) {
      this(var1, null, null, 6, null);
   }

   @JvmOverloads
   public Location() {
      this(null, null, null, 7, null);
   }
}
