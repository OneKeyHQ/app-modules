package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0000\u001a \u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0000\u001a \u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0001H\u0000\u001a$\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0000¨\u0006\u0015",
   d2 = {
         "drawableToBitmap",
         "Landroid/graphics/Bitmap;",
         "drawable",
         "Landroid/graphics/drawable/Drawable;",
         "defaultWidth",
         "",
         "defaultHeight",
         "calculateInSampleSize",
         "options",
         "Landroid/graphics/BitmapFactory$Options;",
         "reqWidth",
         "reqHeight",
         "determineBitmapRotation",
         "context",
         "Landroid/content/Context;",
         "uri",
         "Landroid/net/Uri;",
         "bitmap",
         "extractExifData",
         "",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBitmapUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BitmapUtils.kt\nio/intercom/android/sdk/utilities/BitmapUtilsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,114:1\n1#2:115\n1869#3,2:116\n*S KotlinDebug\n*F\n+ 1 BitmapUtils.kt\nio/intercom/android/sdk/utilities/BitmapUtilsKt\n*L\n98#1:116,2\n*E\n"
)
public final class BitmapUtilsKt {
   @NotNull
   public static final Bitmap drawableToBitmap(@NotNull Drawable var0, int var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "drawable");
      if (var0 instanceof BitmapDrawable && ((BitmapDrawable)var0).getBitmap() != null) {
         Bitmap var7 = ((BitmapDrawable)var0).getBitmap();
         Intrinsics.checkNotNullExpressionValue(var7, "getBitmap(...)");
         return var7;
      }

      Bitmap var10000;
      if (var0.getIntrinsicWidth() > 0 && var0.getIntrinsicHeight() > 0) {
         Bitmap var5 = Bitmap.createBitmap(var0.getIntrinsicWidth(), var0.getIntrinsicHeight(), Config.ARGB_8888);
         Intrinsics.checkNotNull(var5);
         var10000 = var5;
      } else {
         Bitmap var4 = Bitmap.createBitmap(var1, var2, Config.ARGB_8888);
         Intrinsics.checkNotNull(var4);
         var10000 = var4;
      }

      Bitmap var3 = var10000;
      Canvas var6 = new Canvas(var3);
      var0.setBounds(0, 0, var6.getWidth(), var6.getHeight());
      var0.draw(var6);
      return var3;
   }

   public static final int calculateInSampleSize(@NotNull Options var0, int var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "options");
      Options var5 = var0;
      int var6 = 0;
      Pair var3 = TuplesKt.to(var5.outHeight, var5.outWidth);
      int var4 = ((Number)var3.component1()).intValue();
      int var9 = ((Number)var3.component2()).intValue();
      var6 = 1;
      if (var4 > var2 || var9 > var1) {
         int var7 = var4 / 2;
         int var8 = var9 / 2;

         while (var7 / var6 >= var2 && var8 / var6 >= var1) {
            var6 *= 2;
         }
      }

      return var6;
   }

   @NotNull
   public static final Bitmap determineBitmapRotation(@NotNull Context var0, @NotNull Uri var1, @NotNull Bitmap var2) throws java.io.IOException {
      InputStream var3;
      Bitmap var10000;
      label23: {
         Intrinsics.checkNotNullParameter(var0, "context");
         Intrinsics.checkNotNullParameter(var1, "uri");
         Intrinsics.checkNotNullParameter(var2, "bitmap");
         var3 = var0.getContentResolver().openInputStream(var1);
         if (var3 != null) {
            InputStream var5 = var3;
            int var6 = 0;
            ExifInterface var7 = new ExifInterface(var5);
            int var8 = var7.getAttributeInt("Orientation", 0);
            Matrix var9 = new Matrix();
            switch (var8) {
               case 3:
                  var9.postRotate(180.0F);
               case 4:
               case 5:
               case 7:
               default:
                  break;
               case 6:
                  var9.postRotate(90.0F);
                  break;
               case 8:
                  var9.postRotate(270.0F);
            }

            var10000 = Bitmap.createBitmap(var2, 0, 0, var2.getWidth(), var2.getHeight(), var9, true);
            if (var10000 != null) {
               break label23;
            }
         }

         var10000 = var2;
      }

      Bitmap var4 = var10000;
      if (var3 != null) {
         var3.close();
      }

      return var4;
   }

   @NotNull
   public static final Map extractExifData(@NotNull Context var0, @NotNull Uri var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uri");
      Map var2 = new LinkedHashMap();

      try {
         InputStream var10000 = var0.getContentResolver().openInputStream(var1);
         if (var10000 != null) {
            var var3 = var10000;
            Throwable var4 = null;

            try {
               InputStream var5 = var3;
               int var6 = 0;
               ExifInterface var7 = new ExifInterface(var5);
               String[] var8 = new String[]{"DateTime", "DateTimeOriginal", "DateTimeDigitized"};
               List var9 = CollectionsKt.listOf(var8);
               Iterable var26 = var9;
               int var10 = 0;

               for (Object var12 : var26) {
                  String var13 = (String)var12;
                  int var14 = 0;
                  String var27 = var7.getAttribute(var13);
                  if (var27 != null) {
                     String var15 = var27;
                     int var16 = 0;
                     if (var15.length() > 0) {
                        var2.put(var13, var15);
                     }
                  }
               }

               Unit var25 = Unit.INSTANCE;
            } catch (Throwable var21) {
               var4 = var21;
               throw var21;
            } finally {
               CloseableKt.closeFinally(var3, var4);
            }
         }
      } catch (IOException var23) {
         Log.w("ExifExtractor", "Failed to extract EXIF data: " + var23.getMessage());
      } catch (NullPointerException var24) {
         Log.w("ExifExtractor", "Failed to extract EXIF data (null pointer): " + var24.getMessage());
      }

      return var2;
   }
}
