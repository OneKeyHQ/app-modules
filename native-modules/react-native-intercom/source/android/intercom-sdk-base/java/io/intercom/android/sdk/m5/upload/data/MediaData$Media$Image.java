package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\r¢\u0006\u0004\b\u000e\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001f\u001a\u00020\bHÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\t\u0010!\u001a\u00020\u000bHÆ\u0003J\u0015\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\rHÆ\u0003J[\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\rHÇ\u0001J\b\u0010$\u001a\u00020\u0005H\u0007J\u0013\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010(H×\u0003J\t\u0010)\u001a\u00020\u0005H×\u0001J\t\u0010*\u001a\u00020\u0003H×\u0001J\u0018\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\u0005H\u0007R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0014\u0010\u0007\u001a\u00020\bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\t\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0014\u0010\n\u001a\u00020\u000bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001d\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\r¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u00060",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Image;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "mimeType",
         "",
         "width",
         "",
         "height",
         "size",
         "",
         "fileName",
         "uri",
         "Landroid/net/Uri;",
         "exifData",
         "",
         "<init>",
         "(Ljava/lang/String;IIJLjava/lang/String;Landroid/net/Uri;Ljava/util/Map;)V",
         "getMimeType",
         "()Ljava/lang/String;",
         "getWidth",
         "()I",
         "getHeight",
         "getSize",
         "()J",
         "getFileName",
         "getUri",
         "()Landroid/net/Uri;",
         "getExifData",
         "()Ljava/util/Map;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
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
@StabilityInferred(parameters = 0)
public final class MediaData$Media$Image implements MediaData$Media {
   @NotNull
   private final String mimeType;
   private final int width;
   private final int height;
   private final long size;
   @NotNull
   private final String fileName;
   @NotNull
   private final Uri uri;
   @NotNull
   private final Map exifData;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new MediaData$Media$Image$Creator();

   public MediaData$Media$Image(@NotNull String var1, int var2, int var3, long var4, @NotNull String var6, @NotNull Uri var7, @NotNull Map var8) {
      super();
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      Intrinsics.checkNotNullParameter(var6, "fileName");
      Intrinsics.checkNotNullParameter(var7, "uri");
      Intrinsics.checkNotNullParameter(var8, "exifData");
      this.mimeType = var1;
      this.width = var2;
      this.height = var3;
      this.size = var4;
      this.fileName = var6;
      this.uri = var7;
      this.exifData = var8;
   }

   // $VF: synthetic method
   public MediaData$Media$Image(String var1, int var2, int var3, long var4, String var6, Uri var7, Map var8, int var9, DefaultConstructorMarker var10) {
      this(var1, var2, var3, var4, var6, var7, (((var9 & 64) != 0) ? (Map) (MapsKt.emptyMap()) : var8));
   }

   @NotNull
   @Override
   public String getMimeType() {
      return this.mimeType;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   @Override
   public long getSize() {
      return this.size;
   }

   @NotNull
   @Override
   public String getFileName() {
      return this.fileName;
   }

   @NotNull
   @Override
   public Uri getUri() {
      return this.uri;
   }

   @NotNull
   public final Map getExifData() {
      return this.exifData;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.mimeType);
      var1.writeInt(this.width);
      var1.writeInt(this.height);
      var1.writeLong(this.size);
      var1.writeString(this.fileName);
      var1.writeParcelable(this.uri, var2);
      Map var3 = this.exifData;
      var1.writeInt(var3.size());

      for (Object var5 : var3.entrySet()) {
         var1.writeString((String)((Entry)var5).getKey());
         var1.writeString((String)((Entry)var5).getValue());
      }
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.mimeType;
   }

   public final int component2() {
      return this.width;
   }

   public final int component3() {
      return this.height;
   }

   public final long component4() {
      return this.size;
   }

   @NotNull
   public final String component5() {
      return this.fileName;
   }

   @NotNull
   public final Uri component6() {
      return this.uri;
   }

   @NotNull
   public final Map component7() {
      return this.exifData;
   }

   @NotNull
   public final MediaData$Media$Image copy(@NotNull String var1, int var2, int var3, long var4, @NotNull String var6, @NotNull Uri var7, @NotNull Map var8) {
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      Intrinsics.checkNotNullParameter(var6, "fileName");
      Intrinsics.checkNotNullParameter(var7, "uri");
      Intrinsics.checkNotNullParameter(var8, "exifData");
      return new MediaData$Media$Image(var1, var2, var3, var4, var6, var7, var8);
   }

   // $VF: synthetic method
   public static MediaData$Media$Image copy$default(
      MediaData$Media$Image var0, String var1, int var2, int var3, long var4, String var6, Uri var7, Map var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.mimeType;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.width;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.height;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.size;
      }

      if ((var9 & 16) != 0) {
         var6 = var0.fileName;
      }

      if ((var9 & 32) != 0) {
         var7 = var0.uri;
      }

      if ((var9 & 64) != 0) {
         var8 = var0.exifData;
      }

      return var0.copy(var1, var2, var3, var4, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "Image(mimeType="
         + this.mimeType
         + ", width="
         + this.width
         + ", height="
         + this.height
         + ", size="
         + this.size
         + ", fileName="
         + this.fileName
         + ", uri="
         + this.uri
         + ", exifData="
         + this.exifData
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.mimeType.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.width);
      var1 = var1 * 31 + Integer.hashCode(this.height);
      var1 = var1 * 31 + Long.hashCode(this.size);
      var1 = var1 * 31 + this.fileName.hashCode();
      var1 = var1 * 31 + this.uri.hashCode();
      return var1 * 31 + this.exifData.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MediaData$Media$Image)) {
         return false;
      } else {
         MediaData$Media$Image var2 = (MediaData$Media$Image)var1;
         if (!Intrinsics.areEqual(this.mimeType, var2.mimeType)) {
            return false;
         } else if (this.width != var2.width) {
            return false;
         } else if (this.height != var2.height) {
            return false;
         } else if (this.size != var2.size) {
            return false;
         } else if (!Intrinsics.areEqual(this.fileName, var2.fileName)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.uri, var2.uri) ? false : Intrinsics.areEqual(this.exifData, var2.exifData);
         }
      }
   }

   @NotNull
   @Override
   public Uri getDataSource() {
      return MediaData$Media$DefaultImpls.getDataSource(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
