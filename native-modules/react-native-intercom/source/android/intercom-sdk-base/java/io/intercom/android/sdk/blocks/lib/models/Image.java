package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001BM\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\t\u0010\u001c\u001a\u00020\bHÆ\u0003J\t\u0010\u001d\u001a\u00020\bHÆ\u0003JM\u0010\u001e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\bHÇ\u0001J\b\u0010\u001f\u001a\u00020\bH\u0007J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#H×\u0003J\t\u0010$\u001a\u00020\bH×\u0001J\t\u0010%\u001a\u00020\u0003H×\u0001J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\bH\u0007R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u000f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0014\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u0016\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0011¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/Image;",
         "Landroid/os/Parcelable;",
         "_alt",
         "",
         "_url",
         "_previewUrl",
         "_attribution",
         "width",
         "",
         "height",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)V",
         "getWidth",
         "()I",
         "getHeight",
         "alt",
         "getAlt",
         "()Ljava/lang/String;",
         "url",
         "getUrl",
         "previewUrl",
         "getPreviewUrl",
         "attribution",
         "getAttribution",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
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
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class Image implements Parcelable {
   @SerializedName("alt")
   @Nullable
   private final String _alt;
   @SerializedName("url")
   @Nullable
   private final String _url;
   @SerializedName("previewUrl")
   @Nullable
   private final String _previewUrl;
   @SerializedName("attribution")
   @Nullable
   private final String _attribution;
   @SerializedName("width")
   private final int width;
   @SerializedName("height")
   private final int height;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new Image$Creator();

   @JvmOverloads
   public Image(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, int var5, int var6) {
      this._alt = var1;
      this._url = var2;
      this._previewUrl = var3;
      this._attribution = var4;
      this.width = var5;
      this.height = var6;
   }

   // $VF: synthetic method
   public Image(String var1, String var2, String var3, String var4, int var5, int var6, int var7, DefaultConstructorMarker var8) {
      this((((var7 & 1) != 0) ? (String) (null) : var1), (((var7 & 2) != 0) ? (String) (null) : var2), (((var7 & 4) != 0) ? (String) (null) : var3), (((var7 & 8) != 0) ? (String) (null) : var4), (((var7 & 16) != 0) ? (int) (0) : var5), (((var7 & 32) != 0) ? (int) (0) : var6));
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   @NotNull
   public final String getAlt() {
      String var10000 = this._alt;
      if (this._alt == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getUrl() {
      String var10000 = this._url;
      if (this._url == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getPreviewUrl() {
      String var10000 = this._previewUrl;
      if (this._previewUrl == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getAttribution() {
      String var10000 = this._attribution;
      if (this._attribution == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this._alt);
      var1.writeString(this._url);
      var1.writeString(this._previewUrl);
      var1.writeString(this._attribution);
      var1.writeInt(this.width);
      var1.writeInt(this.height);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   private final String component1() {
      return this._alt;
   }

   private final String component2() {
      return this._url;
   }

   private final String component3() {
      return this._previewUrl;
   }

   private final String component4() {
      return this._attribution;
   }

   public final int component5() {
      return this.width;
   }

   public final int component6() {
      return this.height;
   }

   @NotNull
   public final Image copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, int var5, int var6) {
      return new Image(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static Image copy$default(Image var0, String var1, String var2, String var3, String var4, int var5, int var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0._alt;
      }

      if ((var7 & 2) != 0) {
         var2 = var0._url;
      }

      if ((var7 & 4) != 0) {
         var3 = var0._previewUrl;
      }

      if ((var7 & 8) != 0) {
         var4 = var0._attribution;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.width;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.height;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "Image(_alt="
         + this._alt
         + ", _url="
         + this._url
         + ", _previewUrl="
         + this._previewUrl
         + ", _attribution="
         + this._attribution
         + ", width="
         + this.width
         + ", height="
         + this.height
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._alt == null ? 0 : this._alt.hashCode();
      var1 = var1 * 31 + (this._url == null ? 0 : this._url.hashCode());
      var1 = var1 * 31 + (this._previewUrl == null ? 0 : this._previewUrl.hashCode());
      var1 = var1 * 31 + (this._attribution == null ? 0 : this._attribution.hashCode());
      var1 = var1 * 31 + Integer.hashCode(this.width);
      return var1 * 31 + Integer.hashCode(this.height);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Image)) {
         return false;
      } else {
         Image var2 = (Image)var1;
         if (!Intrinsics.areEqual(this._alt, var2._alt)) {
            return false;
         } else if (!Intrinsics.areEqual(this._url, var2._url)) {
            return false;
         } else if (!Intrinsics.areEqual(this._previewUrl, var2._previewUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this._attribution, var2._attribution)) {
            return false;
         } else {
            return this.width != var2.width ? false : this.height == var2.height;
         }
      }
   }

   @JvmOverloads
   public Image(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, int var5) {
      this(var1, var2, var3, var4, var5, 0, 32, null);
   }

   @JvmOverloads
   public Image(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
      this(var1, var2, var3, var4, 0, 0, 48, null);
   }

   @JvmOverloads
   public Image(@Nullable String var1, @Nullable String var2, @Nullable String var3) {
      this(var1, var2, var3, null, 0, 0, 56, null);
   }

   @JvmOverloads
   public Image(@Nullable String var1, @Nullable String var2) {
      this(var1, var2, null, null, 0, 0, 60, null);
   }

   @JvmOverloads
   public Image(@Nullable String var1) {
      this(var1, null, null, null, 0, 0, 62, null);
   }

   @JvmOverloads
   public Image() {
      this(null, null, null, null, 0, 0, 63, null);
   }
}
