package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
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
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\b\u0010\u0017\u001a\u00020\u0018H\u0007J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH×\u0003J\t\u0010\u001d\u001a\u00020\u0018H×\u0001J\t\u0010\u001e\u001a\u00020\u0003H×\u0001J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0018H\u0007R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0006\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0014\u0010\u0007\u001a\u00020\bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Other;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "mimeType",
         "",
         "size",
         "",
         "fileName",
         "uri",
         "Landroid/net/Uri;",
         "<init>",
         "(Ljava/lang/String;JLjava/lang/String;Landroid/net/Uri;)V",
         "getMimeType",
         "()Ljava/lang/String;",
         "getSize",
         "()J",
         "getFileName",
         "getUri",
         "()Landroid/net/Uri;",
         "component1",
         "component2",
         "component3",
         "component4",
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
@Parcelize
@StabilityInferred(parameters = 0)
public final class MediaData$Media$Other implements MediaData$Media {
   @NotNull
   private final String mimeType;
   private final long size;
   @NotNull
   private final String fileName;
   @NotNull
   private final Uri uri;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new MediaData$Media$Other$Creator();

   public MediaData$Media$Other(@NotNull String var1, long var2, @NotNull String var4, @NotNull Uri var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      Intrinsics.checkNotNullParameter(var4, "fileName");
      Intrinsics.checkNotNullParameter(var5, "uri");
      this.mimeType = var1;
      this.size = var2;
      this.fileName = var4;
      this.uri = var5;
   }

   @NotNull
   @Override
   public String getMimeType() {
      return this.mimeType;
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

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.mimeType);
      var1.writeLong(this.size);
      var1.writeString(this.fileName);
      var1.writeParcelable(this.uri, var2);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.mimeType;
   }

   public final long component2() {
      return this.size;
   }

   @NotNull
   public final String component3() {
      return this.fileName;
   }

   @NotNull
   public final Uri component4() {
      return this.uri;
   }

   @NotNull
   public final MediaData$Media$Other copy(@NotNull String var1, long var2, @NotNull String var4, @NotNull Uri var5) {
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      Intrinsics.checkNotNullParameter(var4, "fileName");
      Intrinsics.checkNotNullParameter(var5, "uri");
      return new MediaData$Media$Other(var1, var2, var4, var5);
   }

   // $VF: synthetic method
   public static MediaData$Media$Other copy$default(MediaData$Media$Other var0, String var1, long var2, String var4, Uri var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.mimeType;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.size;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.fileName;
      }

      if ((var6 & 8) != 0) {
         var5 = var0.uri;
      }

      return var0.copy(var1, var2, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Other(mimeType=" + this.mimeType + ", size=" + this.size + ", fileName=" + this.fileName + ", uri=" + this.uri + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.mimeType.hashCode();
      var1 = var1 * 31 + Long.hashCode(this.size);
      var1 = var1 * 31 + this.fileName.hashCode();
      return var1 * 31 + this.uri.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MediaData$Media$Other)) {
         return false;
      } else {
         MediaData$Media$Other var2 = (MediaData$Media$Other)var1;
         if (!Intrinsics.areEqual(this.mimeType, var2.mimeType)) {
            return false;
         } else if (this.size != var2.size) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.fileName, var2.fileName) ? false : Intrinsics.areEqual(this.uri, var2.uri);
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
