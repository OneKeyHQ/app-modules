package io.intercom.android.sdk.m5.upload.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0010\u001a\u00020\u0006H\u0016J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006HÇ\u0001J\b\u0010\u0016\u001a\u00020\u0003H\u0007J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH×\u0003J\t\u0010\u001b\u001a\u00020\u0003H×\u0001J\t\u0010\u001c\u001a\u00020\u0006H×\u0001J\u0018\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0003H\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000e¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData;",
         "width",
         "",
         "height",
         "url",
         "",
         "attribution",
         "<init>",
         "(IILjava/lang/String;Ljava/lang/String;)V",
         "getWidth",
         "()I",
         "getHeight",
         "getUrl",
         "()Ljava/lang/String;",
         "getAttribution",
         "getDataSource",
         "component1",
         "component2",
         "component3",
         "component4",
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
public final class MediaData$Gif implements MediaData {
   private final int width;
   private final int height;
   @NotNull
   private final String url;
   @NotNull
   private final String attribution;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new MediaData$Gif$Creator();

   public MediaData$Gif(int var1, int var2, @NotNull String var3, @NotNull String var4) {
      super();
      Intrinsics.checkNotNullParameter(var3, "url");
      Intrinsics.checkNotNullParameter(var4, "attribution");
      this.width = var1;
      this.height = var2;
      this.url = var3;
      this.attribution = var4;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @NotNull
   public final String getAttribution() {
      return this.attribution;
   }

   @NotNull
   public String getDataSource() {
      return this.url;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(this.width);
      var1.writeInt(this.height);
      var1.writeString(this.url);
      var1.writeString(this.attribution);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   public final int component1() {
      return this.width;
   }

   public final int component2() {
      return this.height;
   }

   @NotNull
   public final String component3() {
      return this.url;
   }

   @NotNull
   public final String component4() {
      return this.attribution;
   }

   @NotNull
   public final MediaData$Gif copy(int var1, int var2, @NotNull String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var3, "url");
      Intrinsics.checkNotNullParameter(var4, "attribution");
      return new MediaData$Gif(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static MediaData$Gif copy$default(MediaData$Gif var0, int var1, int var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.width;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.height;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.url;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.attribution;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Gif(width=" + this.width + ", height=" + this.height + ", url=" + this.url + ", attribution=" + this.attribution + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.width);
      var1 = var1 * 31 + Integer.hashCode(this.height);
      var1 = var1 * 31 + this.url.hashCode();
      return var1 * 31 + this.attribution.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MediaData$Gif)) {
         return false;
      } else {
         MediaData$Gif var2 = (MediaData$Gif)var1;
         if (this.width != var2.width) {
            return false;
         } else if (this.height != var2.height) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.url, var2.url) ? false : Intrinsics.areEqual(this.attribution, var2.attribution);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
