package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0087\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u001d\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0005HÂ\u0003J\u001f\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\b\u0010\u0010\u001a\u00020\u0003H\u0007J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0003H×\u0001J\t\u0010\u0016\u001a\u00020\u0005H×\u0001J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0003H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/models/Reaction;",
         "Landroid/os/Parcelable;",
         "index",
         "",
         "_imageUrl",
         "",
         "<init>",
         "(ILjava/lang/String;)V",
         "getIndex",
         "()I",
         "imageUrl",
         "getImageUrl",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
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
         "Companion",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class Reaction implements Parcelable {
   @NotNull
   public static final Reaction$Companion Companion = new Reaction$Companion(null);
   @SerializedName("index")
   private final int index;
   @SerializedName("image_url")
   @Nullable
   private final String _imageUrl;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new Reaction$Creator();

   public Reaction(int var1, @Nullable String var2) {
      this.index = var1;
      this._imageUrl = var2;
   }

   // $VF: synthetic method
   public Reaction(int var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (int) (0) : var1), (((var3 & 2) != 0) ? (String) (null) : var2));
   }

   public final int getIndex() {
      return this.index;
   }

   @NotNull
   public final String getImageUrl() {
      String var10000 = this._imageUrl;
      if (this._imageUrl == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(this.index);
      var1.writeString(this._imageUrl);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   public final int component1() {
      return this.index;
   }

   private final String component2() {
      return this._imageUrl;
   }

   @NotNull
   public final Reaction copy(int var1, @Nullable String var2) {
      return new Reaction(var1, var2);
   }

   // $VF: synthetic method
   public static Reaction copy$default(Reaction var0, int var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.index;
      }

      if ((var3 & 2) != 0) {
         var2 = var0._imageUrl;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Reaction(index=" + this.index + ", _imageUrl=" + this._imageUrl + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.index);
      return var1 * 31 + (this._imageUrl == null ? 0 : this._imageUrl.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Reaction)) {
         return false;
      }

      Reaction var2 = (Reaction)var1;
      return this.index != var2.index ? false : Intrinsics.areEqual(this._imageUrl, var2._imageUrl);
   }

   public Reaction() {
      this(0, null, 3, null);
   }

   @JvmStatic
   @NotNull
   public static final Reaction create(int var0, @NotNull String var1) {
      return Companion.create(var0, var1);
   }
}
