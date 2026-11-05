package io.intercom.android.sdk.lightcompressor.utils;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build.VERSION;
import android.util.Log;
import io.intercom.android.sdk.lightcompressor.VideoQuality;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bJ\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\bJ\u0010\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002J\u0010\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002J\u0017\u0010\u0016\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002¢\u0006\u0002\u0010\u0017J\u0017\u0010\u0018\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002¢\u0006\u0002\u0010\u0017J\u0017\u0010\u0019\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002¢\u0006\u0002\u0010\u0017J\u0016\u0010\u001a\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eJ\u0012\u0010\u001f\u001a\u00020\u000e2\n\u0010 \u001a\u00060!j\u0002`\"J\u0016\u0010#\u001a\u00020\b2\u0006\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020&J*\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0(2\u0006\u0010)\u001a\u00020\u00052\u0006\u0010*\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u001eJ\u0006\u0010,\u001a\u00020\u001eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000¨\u0006-",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/utils/CompressorUtils;",
         "",
         "<init>",
         "()V",
         "MIN_HEIGHT",
         "",
         "MIN_WIDTH",
         "I_FRAME_INTERVAL",
         "",
         "prepareVideoWidth",
         "mediaMetadataRetriever",
         "Landroid/media/MediaMetadataRetriever;",
         "prepareVideoHeight",
         "setOutputFileParameters",
         "",
         "inputFormat",
         "Landroid/media/MediaFormat;",
         "outputFormat",
         "newBitrate",
         "getFrameRate",
         "format",
         "getIFrameIntervalRate",
         "getColorStandard",
         "(Landroid/media/MediaFormat;)Ljava/lang/Integer;",
         "getColorTransfer",
         "getColorRange",
         "findTrack",
         "extractor",
         "Landroid/media/MediaExtractor;",
         "isVideo",
         "",
         "printException",
         "exception",
         "Ljava/lang/Exception;",
         "Lkotlin/Exception;",
         "getBitrate",
         "bitrate",
         "quality",
         "Lio/intercom/android/sdk/lightcompressor/VideoQuality;",
         "generateWidthAndHeight",
         "Lkotlin/Pair;",
         "width",
         "height",
         "keepOriginalResolution",
         "hasQTI",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class CompressorUtils {
   @NotNull
   public static final CompressorUtils INSTANCE = new CompressorUtils();
   private static final double MIN_HEIGHT = 640.0;
   private static final double MIN_WIDTH = 368.0;
   private static final int I_FRAME_INTERVAL = 1;

   private CompressorUtils() {
   }

   public final double prepareVideoWidth(@NotNull MediaMetadataRetriever var1) {
      Intrinsics.checkNotNullParameter(var1, "mediaMetadataRetriever");
      String var2 = var1.extractMetadata(18);
      CharSequence var3 = var2;
      return var3 == null || var3.length() == 0 ? 368.0 : Double.parseDouble(var2);
   }

   public final double prepareVideoHeight(@NotNull MediaMetadataRetriever var1) {
      Intrinsics.checkNotNullParameter(var1, "mediaMetadataRetriever");
      String var2 = var1.extractMetadata(19);
      CharSequence var3 = var2;
      return var3 == null || var3.length() == 0 ? 640.0 : Double.parseDouble(var2);
   }

   public final void setOutputFileParameters(@NotNull MediaFormat var1, @NotNull MediaFormat var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "inputFormat");
      Intrinsics.checkNotNullParameter(var2, "outputFormat");
      int var4 = this.getFrameRate(var1);
      int var5 = this.getIFrameIntervalRate(var1);
      MediaFormat var6 = var2;
      MediaFormat var7 = var6;
      int var8 = 0;
      var7.setInteger("color-format", 2130708361);
      var7.setInteger("frame-rate", var4);
      var7.setInteger("i-frame-interval", var5);
      var7.setInteger("bitrate", var3);
      var7.setInteger("bitrate-mode", 2);
      if (VERSION.SDK_INT >= 24) {
         Integer var10000 = INSTANCE.getColorStandard(var1);
         if (var10000 != null) {
            int var9 = var10000.intValue();
            int var10 = 0;
            var7.setInteger("color-standard", var9);
         }

         var10000 = INSTANCE.getColorTransfer(var1);
         if (var10000 != null) {
            int var11 = var10000.intValue();
            int var13 = 0;
            var7.setInteger("color-transfer", var11);
         }

         var10000 = INSTANCE.getColorRange(var1);
         if (var10000 != null) {
            int var12 = var10000.intValue();
            int var14 = 0;
            var7.setInteger("color-range", var12);
         }
      }

      Log.i("Output file parameters", "videoFormat: " + var7);
   }

   private final int getFrameRate(MediaFormat var1) {
      return var1.containsKey("frame-rate") ? var1.getInteger("frame-rate") : 30;
   }

   private final int getIFrameIntervalRate(MediaFormat var1) {
      return var1.containsKey("i-frame-interval") ? var1.getInteger("i-frame-interval") : 1;
   }

   private final Integer getColorStandard(MediaFormat var1) {
      return VERSION.SDK_INT >= 24 && var1.containsKey("color-standard") ? var1.getInteger("color-standard") : null;
   }

   private final Integer getColorTransfer(MediaFormat var1) {
      return VERSION.SDK_INT >= 24 && var1.containsKey("color-transfer") ? var1.getInteger("color-transfer") : null;
   }

   private final Integer getColorRange(MediaFormat var1) {
      return VERSION.SDK_INT >= 24 && var1.containsKey("color-range") ? var1.getInteger("color-range") : null;
   }

   public final int findTrack(@NotNull MediaExtractor var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "extractor");
      int var3 = var1.getTrackCount();

      for (int var4 = 0; var4 < var3; var4++) {
         MediaFormat var10000 = var1.getTrackFormat(var4);
         Intrinsics.checkNotNullExpressionValue(var10000, "getTrackFormat(...)");
         MediaFormat var5 = var10000;
         String var6 = var5.getString("mime");
         if (var2) {
            Boolean var7 = var6 != null ? StringsKt.startsWith$default(var6, "video/", false, 2, null) : null;
            Intrinsics.checkNotNull(var7);
            if (var7) {
               return var4;
            }
         } else {
            Boolean var8 = var6 != null ? StringsKt.startsWith$default(var6, "audio/", false, 2, null) : null;
            Intrinsics.checkNotNull(var8);
            if (var8) {
               return var4;
            }
         }
      }

      return -5;
   }

   public final void printException(@NotNull Exception var1) {
      Intrinsics.checkNotNullParameter(var1, "exception");
      Object var2 = null;
      var2 = "An error has occurred!";
      String var10000 = var1.getLocalizedMessage();
      if (var10000 != null) {
         String var3 = var10000;
         int var4 = 0;
         var2 = var3;
      }

      Log.e("Compressor", (String)var2, var1);
   }

   public final int getBitrate(int var1, @NotNull VideoQuality var2) {
      Intrinsics.checkNotNullParameter(var2, "quality");
      int var10000;
      switch (var2) {
         case VERY_LOW:
            var10000 = MathKt.roundToInt(var1 * 0.1);
            break;
         case LOW:
            var10000 = MathKt.roundToInt(var1 * 0.2);
            break;
         case MEDIUM:
            var10000 = MathKt.roundToInt(var1 * 0.3);
            break;
         case HIGH:
            var10000 = MathKt.roundToInt(var1 * 0.4);
            break;
         case VERY_HIGH:
            var10000 = MathKt.roundToInt(var1 * 0.6);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @NotNull
   public final Pair generateWidthAndHeight(double var1, double var3, boolean var5) {
      if (var5) {
         return new Pair(MathKt.roundToInt(var1), MathKt.roundToInt(var3));
      }

      int var6 = 0;
      int var7 = 0;
      if (var1 >= 1920.0 || var3 >= 1920.0) {
         var6 = NumbersUtilsKt.generateWidthHeightValue(var1, 0.5);
         var7 = NumbersUtilsKt.generateWidthHeightValue(var3, 0.5);
      } else if (var1 >= 1280.0 || var3 >= 1280.0) {
         var6 = NumbersUtilsKt.generateWidthHeightValue(var1, 0.75);
         var7 = NumbersUtilsKt.generateWidthHeightValue(var3, 0.75);
      } else if (!(var1 >= 960.0) && !(var3 >= 960.0)) {
         var6 = NumbersUtilsKt.generateWidthHeightValue(var1, 0.9);
         var7 = NumbersUtilsKt.generateWidthHeightValue(var3, 0.9);
      } else {
         var6 = NumbersUtilsKt.generateWidthHeightValue(var1, 0.95);
         var7 = NumbersUtilsKt.generateWidthHeightValue(var3, 0.95);
      }

      return new Pair(var6, var7);
   }

   public final boolean hasQTI() {
      MediaCodecInfo[] var1 = new MediaCodecList(0).getCodecInfos();
      Iterator var2 = ArrayIteratorKt.iterator(var1);

      while (var2.hasNext()) {
         MediaCodecInfo var3 = (MediaCodecInfo)var2.next();
         Log.i("CODECS: ", var3.getName());
         String var10000 = var3.getName();
         Intrinsics.checkNotNullExpressionValue(var10000, "getName(...)");
         if (StringsKt.contains$default(var10000, "qti.avc", false, 2, null)) {
            return true;
         }
      }

      return false;
   }
}
