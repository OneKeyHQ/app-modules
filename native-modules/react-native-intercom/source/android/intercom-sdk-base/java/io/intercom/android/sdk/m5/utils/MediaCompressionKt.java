package io.intercom.android.sdk.m5.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.lightcompressor.VideoCompressor;
import io.intercom.android.sdk.lightcompressor.VideoQuality;
import io.intercom.android.sdk.lightcompressor.config.AppSpecificStorageConfiguration;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.conversation.utils.URIExtensionsKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import io.intercom.android.sdk.utilities.BitmapUtilsKt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.SafeContinuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000>\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a0\u0010\u0003\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0012\u0004\u0012\u00020\u00060\u0004*\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0001H\u0082@¢\u0006\u0002\u0010\n\u001a(\u0010\u000b\u001a\u0004\u0018\u00010\f*\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u00012\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0002\u001a(\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0080@¢\u0006\u0002\u0010\u0013\u001a\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0005H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0017",
   d2 = {
         "compressedVideosFolderName",
         "",
         "compressedImagesFolderName",
         "getCompressedVideoData",
         "Lkotlin/Pair;",
         "Landroid/net/Uri;",
         "",
         "context",
         "Landroid/content/Context;",
         "videoName",
         "(Landroid/net/Uri;Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "getCompressedImageData",
         "Lio/intercom/android/sdk/m5/utils/CompressedImageData;",
         "imageName",
         "transparencyBackgroundColor",
         "",
         "getCompressedMediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "uncompressedMediaData",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Landroid/content/Context;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "deleteCompressedMedia",
         "",
         "uri",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaCompression.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaCompression.kt\nio/intercom/android/sdk/m5/utils/MediaCompressionKt\n+ 2 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,215:1\n83#2,6:216\n1#3:222\n*S KotlinDebug\n*F\n+ 1 MediaCompression.kt\nio/intercom/android/sdk/m5/utils/MediaCompressionKt\n*L\n107#1:216,6\n*E\n"
)
public final class MediaCompressionKt {
   @NotNull
   private static final String compressedVideosFolderName = "compressed_videos";
   @NotNull
   private static final String compressedImagesFolderName = "compressed_images";

   private static final Object getCompressedVideoData(Uri var0, Context var1, String var2, Continuation var3) {
      Continuation var4 = var3;
      SafeContinuation var5 = new SafeContinuation(IntrinsicsKt.intercepted(var4));
      Continuation var6 = var5;
      int var7 = 0;
      Twig var8 = LumberMill.getLogger();
      VideoCompressor.start$default(
         var1,
         CollectionsKt.listOf(var0),
         false,
         null,
         new AppSpecificStorageConfiguration("compressed_videos"),
         new Configuration(VideoQuality.LOW, false, null, false, false, null, null, CollectionsKt.listOf(var2), 126, null),
         new MediaCompressionKt$getCompressedVideoData$2$1(var8, var6),
         8,
         null
      );
      Object var10000 = var5.getOrThrow();
      if (var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(var3);
      }

      return var10000;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final CompressedImageData getCompressedImageData(Uri var0, Context var1, String var2, int var3) {
      CompressedImageData var4;
      try {
         Options var6 = new Options();
         int var7 = 0;
         var6.inJustDecodeBounds = true;
         InputStream var8 = var1.getContentResolver().openInputStream(var0);
         BitmapFactory.decodeStream(var8, null, var6);
         if (var8 != null) {
            var8.close();
         }

         var6.inSampleSize = BitmapUtilsKt.calculateInSampleSize(var6, 612, 816);
         var6.inJustDecodeBounds = false;
         InputStream var26 = var1.getContentResolver().openInputStream(var0);
         Bitmap var9 = BitmapFactory.decodeStream(var26, null, var6);
         if (var26 != null) {
            var26.close();
         }

         Bitmap var23 = var9;
         CompressedImageData var40;
         if (var23 != null) {
            Bitmap bitmapx = var23;
            int var25 = 0;
            Bitmap var27 = BitmapUtilsKt.determineBitmapRotation(var1, var0, bitmapx);
            File var28 = new File(var1.getCacheDir(), "compressed_images");
            var28.mkdirs();
            File var10 = new File(var28, var2 + ".jpg");
            Bitmap var10000;
            if (var27.hasAlpha()) {
               int var11 = var27.getWidth();
               int var12 = var27.getHeight();
               Config var13 = Config.ARGB_8888;
               int var14 = 0;
               Bitmap var29 = Bitmap.createBitmap(var11, var12, var13);
               Bitmap var31 = var29;
               int var33 = 0;
               Canvas var35 = new Canvas(var31);
               Canvas var15 = var35;
               int var16 = 0;
               var15.drawColor(var3);
               var15.drawBitmap(var27, 0.0F, 0.0F, null);
               var10000 = var29;
            } else {
               var10000 = var27;
            }

            Bitmap var17 = var10000;
            FileOutputStream var30 = null;
            boolean var20 = false /* VF: Semaphore variable */;

            Uri var34;
            try {
               boolean var38 = true;
               var30 = new FileOutputStream(var10);
               var17.compress(CompressFormat.JPEG, 80, var30);
               var34 = Uri.fromFile(var10);
               boolean var39 = false;
            } finally {
               if (var20) {
                  if (var30 != null) {
                     var30.flush();
                  }

                  if (var30 != null) {
                     var30.close();
                  }
               }
            }

            var30.flush();
            var30.close();
            Uri var32 = var34;
            if (var32 != null) {
               Uri var36 = var32;
               int var37 = 0;
               var40 = new CompressedImageData(var36, var27.getWidth(), var27.getHeight(), var10.length());
            } else {
               var40 = null;
            }
         } else {
            var40 = null;
         }

         var4 = var40;
      } catch (IOException var22) {
         LumberMill.getLogger().internal("MediaCompression", "Failed to compress image: " + var22.getMessage());
         var4 = null;
      }

      return var4;
   }

   // $VF: synthetic method
   static CompressedImageData getCompressedImageData$default(Uri var0, Context var1, String var2, int var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var3 = -1;
      }

      return getCompressedImageData(var0, var1, var2, var3);
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Nullable
   public static final Object getCompressedMediaData(@NotNull io.intercom.android.sdk.m5.upload.data.MediaData$Media var0, @NotNull Context var1, int var2, @NotNull Continuation var3) {
      MediaCompressionKt$getCompressedMediaData$1 var19;
      label71: {
         if (var3 instanceof MediaCompressionKt$getCompressedMediaData$1) {
            var19 = (MediaCompressionKt$getCompressedMediaData$1)var3;
            if ((var19.label & -2147483648) != 0) {
               var19.label -= Integer.MIN_VALUE;
               break label71;
            }
         }

         var19 = new MediaCompressionKt$getCompressedMediaData$1(var3);
      }

      Object var18 = var19.result;
      Object var20 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      String var5;
      Object var10000;
      switch (var19.label) {
         case 0:
            ResultKt.throwOnFailure(var18);
            io.intercom.android.sdk.m5.upload.data.MediaData$Media var4 = var0;
            if (!(var4 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)) {
               io.intercom.android.sdk.m5.upload.data.MediaData$Media var34;
               if (var4 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image) {
                  CharSequence var35 = ((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image)var0).getFileName();
                  String[] var26 = new String[]{"."};
                  String var25 = (String)CollectionsKt.first(StringsKt.split$default(var35, var26, false, 0, 6, null));
                  CompressedImageData var27 = getCompressedImageData(((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image)var0).getUri(), var1, var25, var2);
                  io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image var36;
                  if (var27 != null) {
                     CompressedImageData var28 = var27;
                     int var29 = 0;
                     String var30 = var25 + ".jpg";
                     int var12 = var28.getWidth();
                     int var13 = var28.getHeight();
                     long var14 = var28.getSize();
                     Uri var16 = var28.getUri();
                     Map var17 = ((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image)var0).getExifData();
                     var36 = new io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image("image/jpg", var12, var13, var14, var30, var16, var17);
                  } else {
                     var36 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image)var0;
                  }

                  var34 = var36;
               } else {
                  if (!(var4 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other)) {
                     throw new NoWhenBranchMatchedException();
                  }

                  var34 = var0;
               }

               return var34;
            }

            try {
               CharSequence var31 = ((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0).getFileName();
               String[] var6 = new String[]{"."};
               var5 = (String)CollectionsKt.first(StringsKt.split$default(var31, var6, false, 0, 6, null));
               Uri var32 = ((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0).getUri();
               var19.L$0 = var0;
               var19.L$1 = var1;
               var19.L$2 = var5;
               var19.label = 1;
               var10000 = getCompressedVideoData(var32, var1, var5, var19);
            } catch (Throwable var22) {
               io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var37 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0;
               return var37;
            }

            if (var10000 == var20) {
               return var20;
            }
            break;
         case 1:
            var5 = (String)var19.L$2;
            var1 = (Context)var19.L$1;
            var0 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media)var19.L$0;

            try {
               ResultKt.throwOnFailure(var18);
               var10000 = var18;
               break;
            } catch (Throwable var23) {
               io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var24 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0;
               return var24;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var38;
      try {
         io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var33;
         label52: {
            Pair var7 = (Pair)var10000;
            int var8 = 0;
            Uri var9 = (Uri)var7.getFirst();
            if (var9 != null) {
               io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var10 = URIExtensionsKt.getVideoData(var9, var1, var5 + ".mp4", "video/mp4", ((Number)var7.getSecond()).longValue(), false);
               if (var10 != null) {
                  io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video var11 = io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video.copy$default(var10, null, 0, 0, 0L, null, null, 0L, ((io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0).getThumbnail(), 127, null);
                  if (var11 != null) {
                     var33 = var11;
                     break label52;
                  }
               }
            }

            var33 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0;
         }

         var38 = var33;
      } catch (Throwable var21) {
         var38 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)var0;
      }

      return var38;
   }

   // $VF: synthetic method
   public static Object getCompressedMediaData$default(io.intercom.android.sdk.m5.upload.data.MediaData$Media var0, Context var1, int var2, Continuation var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var2 = -1;
      }

      return getCompressedMediaData(var0, var1, var2, var3);
   }

   public static final void deleteCompressedMedia(@NotNull Uri var0) {
      Intrinsics.checkNotNullParameter(var0, "uri");
      if (var0.getPathSegments().contains("compressed_videos") || var0.getPathSegments().contains("compressed_images")) {
         String var10000 = var0.getPath();
         if (var10000 != null) {
            String var1 = var10000;
            int var2 = 0;
            new File(var1).delete();
         }
      }
   }

   // $VF: synthetic method
   public static final Object access$getCompressedVideoData(Uri var0, Context var1, String var2, Continuation var3) {
      return getCompressedVideoData(var0, var1, var2, var3);
   }
}
