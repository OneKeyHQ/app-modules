package io.intercom.android.sdk.m5.conversation.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import io.intercom.android.sdk.IntercomFileProviderKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000@\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0003\u001a\u0004\u0018\u00010\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0000\u001a,\u0010\n\u001a\u00020\u000b*\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a4\u0010\u0013\u001a\u00020\u0014*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\b\u001a\u00020\tH\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0015",
   d2 = {
         "DEFAULT_IMAGE_HEIGHT",
         "",
         "DEFAULT_IMAGE_WIDTH",
         "getMediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "Landroid/net/Uri;",
         "context",
         "Landroid/content/Context;",
         "generateThumbnailForVideo",
         "",
         "getImageData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Image;",
         "contentResolver",
         "Landroid/content/ContentResolver;",
         "fileName",
         "",
         "mimeType",
         "size",
         "",
         "getVideoData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Video;",
         "intercom-sdk-base_release"
   }
)
public final class URIExtensionsKt {
   private static final int DEFAULT_IMAGE_HEIGHT = 480;
   private static final int DEFAULT_IMAGE_WIDTH = 640;

   @Nullable
   public static final io.intercom.android.sdk.m5.upload.data.MediaData$Media getMediaData(@NotNull Uri var0, @NotNull Context var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "context");
      ContentResolver var3 = var1.getContentResolver();
      Cursor var4 = var3.query(var0, null, null, null, null);
      io.intercom.android.sdk.m5.upload.data.MediaData$Media var19;
      if (var4 != null) {
         var var5 = var4;
         Throwable var6 = null;

         io.intercom.android.sdk.m5.upload.data.MediaData$Media var17;
         try {
            Cursor var7 = (Cursor)var5;
            int var8 = 0;
            io.intercom.android.sdk.m5.upload.data.MediaData$Media var10000;
            if (!var7.moveToFirst()) {
               var10000 = null;
            } else {
               String var9 = var7.getString(var7.getColumnIndexOrThrow("_display_name"));
               String var18;
               if (var7.getColumnIndex("mime_type") != -1) {
                  var18 = var7.getString(var7.getColumnIndexOrThrow("mime_type"));
               } else {
                  Intrinsics.checkNotNull(var9);
                  var18 = StringsKt.contains$default(var9, ".jpg", false, 2, null)
                     ? "image/jpg"
                     : (StringsKt.contains$default(var9, ".mp4", false, 2, null) ? "video/mp4" : "");
               }

               String var10 = var18;
               long var11 = var7.getLong(var7.getColumnIndexOrThrow("_size"));
               Intrinsics.checkNotNull(var10);
               if (StringsKt.contains$default(var10, "video", false, 2, null)) {
                  Intrinsics.checkNotNull(var9);
                  var10000 = getVideoData(var0, var1, var9, var10, var11, var2);
               } else if (StringsKt.contains$default(var10, "image", false, 2, null)) {
                  Intrinsics.checkNotNull(var3);
                  Intrinsics.checkNotNull(var9);
                  var10000 = getImageData(var0, var3, var9, var10, var11);
               } else {
                  Intrinsics.checkNotNull(var9);
                  var10000 = new io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other(var10, var11, var9, var0);
               }
            }

            var17 = var10000;
         } catch (Throwable var15) {
            var6 = var15;
            throw var15;
         } finally {
            CloseableKt.closeFinally(var5, var6);
         }

         var19 = var17;
      } else {
         var19 = null;
      }

      return var19;
   }

   // $VF: synthetic method
   public static io.intercom.android.sdk.m5.upload.data.MediaData$Media getMediaData$default(Uri var0, Context var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      return getMediaData(var0, var1, var2);
   }

   private static final io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image getImageData(Uri var0, ContentResolver var1, String var2, String var3, long var4) {
      int var6 = 0;
      int var23 = 480;
      int var7 = 0;
      int var24 = 640;

      try {
         InputStream var10000 = var1.openInputStream(var0);
         if (var10000 != null) {
            var var8 = var10000;
            Throwable var9 = null;

            try {
               InputStream var10 = var8;
               int var11 = 0;
               ExifInterface var12 = new ExifInterface(var10);
               int var13 = var12.getAttributeInt("ImageLength", 480);
               int var14 = var12.getAttributeInt("ImageWidth", 640);
               int var15 = var12.getAttributeInt("Orientation", 1);
               boolean var16 = var15 == 6 || var15 == 8;
               var23 = var16 ? var14 : var13;
               var24 = var16 ? var13 : var14;
               Unit var25 = Unit.INSTANCE;
            } catch (Throwable var20) {
               var9 = var20;
               throw var20;
            } finally {
               CloseableKt.closeFinally(var8, var9);
            }
         }
      } catch (FileNotFoundException var22) {
      }

      return new io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image(var3, var24, var23, var4, var2, var0, null, 64, null);
   }

   @NotNull
   public static final <E extends Exception> io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video getVideoData(@NotNull Uri var0, @NotNull Context var1, @NotNull String var2, @NotNull String var3, long var4, boolean var6) throws E {
      MediaMetadataRetriever var7;
      long var10000;
      label47: {
         Intrinsics.checkNotNullParameter(var0, "<this>");
         Intrinsics.checkNotNullParameter(var1, "context");
         Intrinsics.checkNotNullParameter(var2, "fileName");
         Intrinsics.checkNotNullParameter(var3, "mimeType");
         var7 = new MediaMetadataRetriever();
         var7.setDataSource(var1, var0);
         String var10 = var7.extractMetadata(9);
         if (var10 != null) {
            Long var11 = StringsKt.toLongOrNull(var10);
            if (var11 != null) {
               var10000 = var11;
               break label47;
            }
         }

         var10000 = 0L;
      }

      long var8;
      int var38;
      label42: {
         var8 = var10000;
         String var33 = var7.extractMetadata(18);
         if (var33 != null) {
            Integer var12 = StringsKt.toIntOrNull(var33);
            if (var12 != null) {
               var38 = var12;
               break label42;
            }
         }

         var38 = 0;
      }

      int var32;
      int var39;
      label37: {
         var32 = var38;
         String var35 = var7.extractMetadata(19);
         if (var35 != null) {
            Integer var13 = StringsKt.toIntOrNull(var35);
            if (var13 != null) {
               var39 = var13;
               break label37;
            }
         }

         var39 = 0;
      }

      int var34 = var39;
      io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image var41;
      if (var6) {
         String var37 = "thumbnail_" + StringsKt.substringBefore$default(var2, ".", null, 2, null) + ".jpg";
         Pair var14 = var34 > var32 ? TuplesKt.to(320, 240) : TuplesKt.to(240, 320);
         int var15 = ((Number)var14.component1()).intValue();
         int var16 = ((Number)var14.component2()).intValue();
         int var17 = Math.max(var32 / 2, var16);
         int var18 = Math.max(var34 / 2, var15);
         Bitmap var19 = var7.getScaledFrameAtTime(0L, 2, var17, var18);
         if (var19 != null) {
            Bitmap var20 = var19;
            int var21 = 0;
            File var40 = var1.getExternalCacheDir();
            if (var40 == null) {
               var40 = var1.getCacheDir();
            }

            File var22 = var40;
            File var23 = new File(var22, "images");
            var23.mkdir();
            File var24 = new File(var23, var37);
            try {
               FileOutputStream var25 = new FileOutputStream(var24);
               var20.compress(CompressFormat.JPEG, 100, var25);
               var25.flush();
               var25.close();
            } catch (java.io.IOException sourceFailure) {
               throw (E)sourceFailure;
            }
            Uri var26 = FileProvider.getUriForFile(var1, IntercomFileProviderKt.fileProviderAuthority(var1), var24);
            Intrinsics.checkNotNull(var26);
            Uri var27 = var26;
            long var28 = var24.length();
            int var30 = var20.getWidth();
            int var31 = var20.getHeight();
            var41 = new io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image("image/jpg", var30, var31, var28, var37, var27, null, 64, null);
         } else {
            var41 = null;
         }
      } else {
         var41 = null;
      }

      io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image var36 = var41;
      var7.close();
      return new io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video(var3, var32, var34, var4, var2, var0, var8, var36);
   }
}
