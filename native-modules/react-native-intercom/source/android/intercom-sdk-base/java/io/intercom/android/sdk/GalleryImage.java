package io.intercom.android.sdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
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
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b!\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001Bw\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003¢\u0006\u0004\b\u0011\u0010\u0012J\u0006\u0010 \u001a\u00020\u0003J\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0006HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\nHÆ\u0003J\t\u0010'\u001a\u00020\nHÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003J\t\u0010)\u001a\u00020\u000eHÆ\u0003J\t\u0010*\u001a\u00020\u000eHÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003Jw\u0010,\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u0003HÇ\u0001J\b\u0010-\u001a\u00020\nH\u0007J\u0013\u0010.\u001a\u00020\u000e2\b\u0010/\u001a\u0004\u0018\u000100H×\u0003J\t\u00101\u001a\u00020\nH×\u0001J\t\u00102\u001a\u00020\u0003H×\u0001J\u0018\u00103\u001a\u0002042\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\nH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u000b\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\f\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u001eR\u0011\u0010\u0010\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0014¨\u00068",
   d2 = {
         "Lio/intercom/android/sdk/GalleryImage;",
         "Landroid/os/Parcelable;",
         "fileName",
         "",
         "mimeType",
         "uri",
         "Landroid/net/Uri;",
         "previewPath",
         "attribution",
         "imageWidth",
         "",
         "imageHeight",
         "fileSize",
         "isGif",
         "",
         "isVideo",
         "duration",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;IIIZZLjava/lang/String;)V",
         "getFileName",
         "()Ljava/lang/String;",
         "getMimeType",
         "getUri",
         "()Landroid/net/Uri;",
         "getPreviewPath",
         "getAttribution",
         "getImageWidth",
         "()I",
         "getImageHeight",
         "getFileSize",
         "()Z",
         "getDuration",
         "getImageWidthXHeight",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "copy",
         "describeContents",
         "equals",
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
public final class GalleryImage implements Parcelable {
   @NotNull
   private final String fileName;
   @NotNull
   private final String mimeType;
   @NotNull
   private final Uri uri;
   @NotNull
   private final String previewPath;
   @NotNull
   private final String attribution;
   private final int imageWidth;
   private final int imageHeight;
   private final int fileSize;
   private final boolean isGif;
   private final boolean isVideo;
   @NotNull
   private final String duration;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new GalleryImage$Creator();

   @JvmOverloads
   public GalleryImage(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull Uri var3,
      @NotNull String var4,
      @NotNull String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      boolean var10,
      @NotNull String var11
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "fileName");
      Intrinsics.checkNotNullParameter(var2, "mimeType");
      Intrinsics.checkNotNullParameter(var3, "uri");
      Intrinsics.checkNotNullParameter(var4, "previewPath");
      Intrinsics.checkNotNullParameter(var5, "attribution");
      Intrinsics.checkNotNullParameter(var11, "duration");
      this.fileName = var1;
      this.mimeType = var2;
      this.uri = var3;
      this.previewPath = var4;
      this.attribution = var5;
      this.imageWidth = var6;
      this.imageHeight = var7;
      this.fileSize = var8;
      this.isGif = var9;
      this.isVideo = var10;
      this.duration = var11;
   }

   // $VF: synthetic method
   public GalleryImage(
      String var1,
      String var2,
      Uri var3,
      String var4,
      String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      boolean var10,
      String var11,
      int var12,
      DefaultConstructorMarker var13
   ) {
      this((((var12 & 1) != 0) ? (String) ("") : var1), (((var12 & 2) != 0) ? (String) ("") : var2), (((var12 & 4) != 0) ? (Uri) (Uri.EMPTY) : var3), (((var12 & 8) != 0) ? (String) ("") : var4), (((var12 & 16) != 0) ? (String) ("") : var5), (((var12 & 32) != 0) ? (int) (0) : var6), (((var12 & 64) != 0) ? (int) (0) : var7), (((var12 & 128) != 0) ? (int) (0) : var8), (((var12 & 256) != 0) ? (boolean) (false) : var9), (((var12 & 512) != 0) ? (boolean) (false) : var10), (((var12 & 1024) != 0) ? (String) ("") : var11));
   }

   @NotNull
   public final String getFileName() {
      return this.fileName;
   }

   @NotNull
   public final String getMimeType() {
      return this.mimeType;
   }

   @NotNull
   public final Uri getUri() {
      return this.uri;
   }

   @NotNull
   public final String getPreviewPath() {
      return this.previewPath;
   }

   @NotNull
   public final String getAttribution() {
      return this.attribution;
   }

   public final int getImageWidth() {
      return this.imageWidth;
   }

   public final int getImageHeight() {
      return this.imageHeight;
   }

   public final int getFileSize() {
      return this.fileSize;
   }

   public final boolean isGif() {
      return this.isGif;
   }

   public final boolean isVideo() {
      return this.isVideo;
   }

   @NotNull
   public final String getDuration() {
      return this.duration;
   }

   @NotNull
   public final String getImageWidthXHeight() {
      return "" + this.imageWidth + 'x' + this.imageHeight;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.fileName);
      var1.writeString(this.mimeType);
      var1.writeParcelable(this.uri, var2);
      var1.writeString(this.previewPath);
      var1.writeString(this.attribution);
      var1.writeInt(this.imageWidth);
      var1.writeInt(this.imageHeight);
      var1.writeInt(this.fileSize);
      var1.writeInt(this.isGif ? 1 : 0);
      var1.writeInt(this.isVideo ? 1 : 0);
      var1.writeString(this.duration);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.fileName;
   }

   @NotNull
   public final String component2() {
      return this.mimeType;
   }

   @NotNull
   public final Uri component3() {
      return this.uri;
   }

   @NotNull
   public final String component4() {
      return this.previewPath;
   }

   @NotNull
   public final String component5() {
      return this.attribution;
   }

   public final int component6() {
      return this.imageWidth;
   }

   public final int component7() {
      return this.imageHeight;
   }

   public final int component8() {
      return this.fileSize;
   }

   public final boolean component9() {
      return this.isGif;
   }

   public final boolean component10() {
      return this.isVideo;
   }

   @NotNull
   public final String component11() {
      return this.duration;
   }

   @NotNull
   public final GalleryImage copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull Uri var3,
      @NotNull String var4,
      @NotNull String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      boolean var10,
      @NotNull String var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "fileName");
      Intrinsics.checkNotNullParameter(var2, "mimeType");
      Intrinsics.checkNotNullParameter(var3, "uri");
      Intrinsics.checkNotNullParameter(var4, "previewPath");
      Intrinsics.checkNotNullParameter(var5, "attribution");
      Intrinsics.checkNotNullParameter(var11, "duration");
      return new GalleryImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   // $VF: synthetic method
   public static GalleryImage copy$default(
      GalleryImage var0,
      String var1,
      String var2,
      Uri var3,
      String var4,
      String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      boolean var10,
      String var11,
      int var12,
      Object var13
   ) {
      if ((var12 & 1) != 0) {
         var1 = var0.fileName;
      }

      if ((var12 & 2) != 0) {
         var2 = var0.mimeType;
      }

      if ((var12 & 4) != 0) {
         var3 = var0.uri;
      }

      if ((var12 & 8) != 0) {
         var4 = var0.previewPath;
      }

      if ((var12 & 16) != 0) {
         var5 = var0.attribution;
      }

      if ((var12 & 32) != 0) {
         var6 = var0.imageWidth;
      }

      if ((var12 & 64) != 0) {
         var7 = var0.imageHeight;
      }

      if ((var12 & 128) != 0) {
         var8 = var0.fileSize;
      }

      if ((var12 & 256) != 0) {
         var9 = var0.isGif;
      }

      if ((var12 & 512) != 0) {
         var10 = var0.isVideo;
      }

      if ((var12 & 1024) != 0) {
         var11 = var0.duration;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("GalleryImage(fileName=")
         .append(this.fileName)
         .append(", mimeType=")
         .append(this.mimeType)
         .append(", uri=")
         .append(this.uri)
         .append(", previewPath=")
         .append(this.previewPath)
         .append(", attribution=")
         .append(this.attribution)
         .append(", imageWidth=")
         .append(this.imageWidth)
         .append(", imageHeight=")
         .append(this.imageHeight)
         .append(", fileSize=")
         .append(this.fileSize)
         .append(", isGif=")
         .append(this.isGif)
         .append(", isVideo=")
         .append(this.isVideo)
         .append(", duration=")
         .append(this.duration)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.fileName.hashCode();
      var1 = var1 * 31 + this.mimeType.hashCode();
      var1 = var1 * 31 + this.uri.hashCode();
      var1 = var1 * 31 + this.previewPath.hashCode();
      var1 = var1 * 31 + this.attribution.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.imageWidth);
      var1 = var1 * 31 + Integer.hashCode(this.imageHeight);
      var1 = var1 * 31 + Integer.hashCode(this.fileSize);
      var1 = var1 * 31 + Boolean.hashCode(this.isGif);
      var1 = var1 * 31 + Boolean.hashCode(this.isVideo);
      return var1 * 31 + this.duration.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof GalleryImage)) {
         return false;
      } else {
         GalleryImage var2 = (GalleryImage)var1;
         if (!Intrinsics.areEqual(this.fileName, var2.fileName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.mimeType, var2.mimeType)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uri, var2.uri)) {
            return false;
         } else if (!Intrinsics.areEqual(this.previewPath, var2.previewPath)) {
            return false;
         } else if (!Intrinsics.areEqual(this.attribution, var2.attribution)) {
            return false;
         } else if (this.imageWidth != var2.imageWidth) {
            return false;
         } else if (this.imageHeight != var2.imageHeight) {
            return false;
         } else if (this.fileSize != var2.fileSize) {
            return false;
         } else if (this.isGif != var2.isGif) {
            return false;
         } else {
            return this.isVideo != var2.isVideo ? false : Intrinsics.areEqual(this.duration, var2.duration);
         }
      }
   }

   @JvmOverloads
   public GalleryImage(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull Uri var3,
      @NotNull String var4,
      @NotNull String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      boolean var10
   ) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, var6, var7, var8, var9, var10, null, 1024, null);
   }

   @JvmOverloads
   public GalleryImage(
      @NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4, @NotNull String var5, int var6, int var7, int var8, boolean var9
   ) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, var6, var7, var8, var9, false, null, 1536, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4, @NotNull String var5, int var6, int var7, int var8) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, var6, var7, var8, false, false, null, 1792, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4, @NotNull String var5, int var6, int var7) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, var6, var7, 0, false, false, null, 1920, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4, @NotNull String var5, int var6) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, var6, 0, 0, false, false, null, 1984, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4, @NotNull String var5) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, var5, 5), var2, var3, var4, var5, 0, 0, 0, false, false, null, 2016, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3, @NotNull String var4) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, var4, null, 4), var2, var3, var4, null, 0, 0, 0, false, false, null, 2032, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2, @NotNull Uri var3) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, var3, null, null, 3), var2, var3, null, null, 0, 0, 0, false, false, null, 2040, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1, @NotNull String var2) {
      
      this(recoveredCheckedGalleryArguments(var1, var2, null, null, null, 2), var2, null, null, null, 0, 0, 0, false, false, null, 2044, null);
   }

   @JvmOverloads
   public GalleryImage(@NotNull String var1) {
      
      this(recoveredCheckedGalleryArguments(var1, null, null, null, null, 1), null, null, null, null, 0, 0, 0, false, false, null, 2046, null);
   }

   @JvmOverloads
   public GalleryImage() {
      this(null, null, null, null, null, 0, 0, 0, false, false, null, 2047, null);
   }

   private static String recoveredCheckedGalleryArguments(String fileName, String mimeType, Uri uri, String previewPath, String attribution, int count) {
      try {
         Intrinsics.checkNotNullParameter(fileName, "fileName");
         if (count >= 2) Intrinsics.checkNotNullParameter(mimeType, "mimeType");
         if (count >= 3) Intrinsics.checkNotNullParameter(uri, "uri");
         if (count >= 4) Intrinsics.checkNotNullParameter(previewPath, "previewPath");
         if (count >= 5) Intrinsics.checkNotNullParameter(attribution, "attribution");
         return fileName;
      } catch (NullPointerException validationFailure) {
         // Kotlin derives this diagnostic from its caller. Preserve the original
         // constructor boundary when Java requires these checks in an argument helper.
         String message = validationFailure.getMessage();
         NullPointerException compatibleFailure = new NullPointerException(message == null ? null : message.replace(
               "io.intercom.android.sdk.GalleryImage.recoveredCheckedGalleryArguments", "io.intercom.android.sdk.GalleryImage.<init>"));
         StackTraceElement[] frames = validationFailure.getStackTrace();
         int helperFrames = 0;
         for (StackTraceElement frame : frames) {
            if (frame.getClassName().equals("io.intercom.android.sdk.GalleryImage")
                  && frame.getMethodName().equals("recoveredCheckedGalleryArguments")) helperFrames++;
         }
         StackTraceElement[] constructorFrames = new StackTraceElement[frames.length - helperFrames];
         int next = 0;
         for (StackTraceElement frame : frames) {
            if (!(frame.getClassName().equals("io.intercom.android.sdk.GalleryImage")
                  && frame.getMethodName().equals("recoveredCheckedGalleryArguments"))) constructorFrames[next++] = frame;
         }
         compatibleFailure.setStackTrace(constructorFrames);
         throw compatibleFailure;
      }
   }
}
