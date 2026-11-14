package io.intercom.android.sdk.lightcompressor.compressor;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaCodec.BufferInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Log;
import io.intercom.android.sdk.lightcompressor.CompressionProgressListener;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import io.intercom.android.sdk.lightcompressor.utils.CompressorUtils;
import io.intercom.android.sdk.lightcompressor.utils.StreamableVideo;
import io.intercom.android.sdk.lightcompressor.video.InputSurface;
import io.intercom.android.sdk.lightcompressor.video.OutputSurface;
import io.intercom.android.sdk.lightcompressor.video.Result;
import java.io.File;
import java.nio.ByteBuffer;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JH\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00072\b\u0010\u0018\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@¢\u0006\u0002\u0010\u001dJb\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u00052\b\u0010\u0018\u001a\u0004\u0018\u00010\u00072\u0006\u0010#\u001a\u00020\f2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u001c2\u0006\u0010'\u001a\u00020\t2\u0006\u0010(\u001a\u00020\u0005H\u0002J \u0010)\u001a\u00020\u00052\u0006\u0010*\u001a\u00020+2\u0006\u0010#\u001a\u00020\f2\u0006\u0010$\u001a\u00020%H\u0002J0\u0010,\u001a\u00020-2\u0006\u0010*\u001a\u00020+2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u0002002\u0006\u0010#\u001a\u00020\f2\u0006\u0010$\u001a\u00020%H\u0002J\u0018\u00101\u001a\u0002022\u0006\u00103\u001a\u0002042\u0006\u00105\u001a\u00020\fH\u0002J\u0018\u00106\u001a\u0002022\u0006\u00107\u001a\u0002042\u0006\u00108\u001a\u000209H\u0002J8\u0010:\u001a\u00020-2\u0006\u0010;\u001a\u00020\u00052\u0006\u0010<\u001a\u0002022\u0006\u0010=\u001a\u0002022\u0006\u0010>\u001a\u00020?2\u0006\u00108\u001a\u0002092\u0006\u0010$\u001a\u00020%H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006@",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/compressor/Compressor;",
         "",
         "<init>",
         "()V",
         "MIN_BITRATE",
         "",
         "MIME_TYPE",
         "",
         "MEDIACODEC_TIMEOUT_DEFAULT",
         "",
         "INVALID_BITRATE",
         "isRunning",
         "",
         "()Z",
         "setRunning",
         "(Z)V",
         "compressVideo",
         "Lio/intercom/android/sdk/lightcompressor/video/Result;",
         "index",
         "context",
         "Landroid/content/Context;",
         "srcUri",
         "Landroid/net/Uri;",
         "destination",
         "streamableFile",
         "configuration",
         "Lio/intercom/android/sdk/lightcompressor/config/Configuration;",
         "listener",
         "Lio/intercom/android/sdk/lightcompressor/CompressionProgressListener;",
         "(ILandroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/lightcompressor/config/Configuration;Lio/intercom/android/sdk/lightcompressor/CompressionProgressListener;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "start",
         "id",
         "newWidth",
         "newHeight",
         "newBitrate",
         "disableAudio",
         "extractor",
         "Landroid/media/MediaExtractor;",
         "compressionProgressListener",
         "duration",
         "rotation",
         "addAudioTrack",
         "mediaMuxer",
         "Landroid/media/MediaMuxer;",
         "processAudio",
         "",
         "audioTrackIndex",
         "bufferInfo",
         "Landroid/media/MediaCodec$BufferInfo;",
         "prepareEncoder",
         "Landroid/media/MediaCodec;",
         "outputFormat",
         "Landroid/media/MediaFormat;",
         "hasQTI",
         "prepareDecoder",
         "inputFormat",
         "outputSurface",
         "Lio/intercom/android/sdk/lightcompressor/video/OutputSurface;",
         "dispose",
         "videoIndex",
         "decoder",
         "encoder",
         "inputSurface",
         "Lio/intercom/android/sdk/lightcompressor/video/InputSurface;",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class Compressor {
   @NotNull
   public static final Compressor INSTANCE = new Compressor();
   private static final int MIN_BITRATE = 2000000;
   @NotNull
   private static final String MIME_TYPE = "video/avc";
   private static final long MEDIACODEC_TIMEOUT_DEFAULT = 100L;
   @NotNull
   private static final String INVALID_BITRATE = "The provided bitrate is smaller than what is needed for compression try to set isMinBitRateEnabled to false";
   private static boolean isRunning = true;

   private Compressor() {
   }

   public final boolean isRunning() {
      return isRunning;
   }

   public final void setRunning(boolean var1) {
      isRunning = var1;
   }

   @Nullable
   public final Object compressVideo(
      int var1,
      @NotNull Context var2,
      @NotNull Uri var3,
      @NotNull String var4,
      @Nullable String var5,
      @NotNull Configuration var6,
      @NotNull CompressionProgressListener var7,
      @NotNull Continuation var8
   ) {
      return BuildersKt.withContext(Dispatchers.getDefault(), new Compressor$compressVideo$2(var2, var3, var1, var6, var4, var5, var7, null), var8);
   }

   private final Result start(
      int var1,
      int var2,
      int var3,
      String var4,
      int var5,
      String var6,
      boolean var7,
      MediaExtractor var8,
      CompressionProgressListener var9,
      long var10,
      int var12
   ) {
      if (var2 != 0 && var3 != 0) {
         File var13 = new File(var4);

         try {
            BufferInfo var14 = new BufferInfo();
            MediaMuxer var15 = new MediaMuxer(var4, 0);
            if (var12 != 0) {
               var15.setOrientationHint(var12);
            }

            int var16 = CompressorUtils.INSTANCE.findTrack(var8, true);
            var8.selectTrack(var16);
            var8.seekTo(0L, 0);
            MediaFormat var10000 = var8.getTrackFormat(var16);
            Intrinsics.checkNotNullExpressionValue(var10000, "getTrackFormat(...)");
            MediaFormat var17 = var10000;
            MediaFormat var56 = MediaFormat.createVideoFormat("video/avc", var2, var3);
            Intrinsics.checkNotNullExpressionValue(var56, "createVideoFormat(...)");
            MediaFormat var18 = var56;
            CompressorUtils.INSTANCE.setOutputFileParameters(var17, var18, var5);
            MediaCodec var19 = null;
            boolean var20 = CompressorUtils.INSTANCE.hasQTI();
            MediaCodec var21 = this.prepareEncoder(var18, var20);
            InputSurface var22 = null;
            OutputSurface var23 = null;
            boolean var24 = false;
            int var25 = -1;

            MediaCodec var46;
            InputSurface var47;
            OutputSurface var48;
            try {
               boolean var26 = false;
               boolean var27 = false;
               int var28 = -5;
               var47 = new InputSurface(var21.createInputSurface());
               var47.makeCurrent();
               var21.start();
               var48 = new OutputSurface();
               var46 = this.prepareDecoder(var17, var48);
               var46.start();

               while (!var27) {
                  if (!var26) {
                     int var29 = var8.getSampleTrackIndex();
                     if (var29 == var16) {
                        int var30 = var46.dequeueInputBuffer(100L);
                        if (var30 >= 0) {
                           ByteBuffer var31 = var46.getInputBuffer(var30);
                           Intrinsics.checkNotNull(var31);
                           int var32 = var8.readSampleData(var31, 0);
                           if (var32 < 0) {
                              var46.queueInputBuffer(var30, 0, 0, 0L, 4);
                              var26 = true;
                           } else {
                              var46.queueInputBuffer(var30, 0, var32, var8.getSampleTime(), 0);
                              var8.advance();
                           }
                        }
                     } else if (var29 == -1) {
                        int var50 = var46.dequeueInputBuffer(100L);
                        if (var50 >= 0) {
                           var46.queueInputBuffer(var50, 0, 0, 0L, 4);
                           var26 = true;
                        }
                     }
                  }

                  boolean var49 = true;
                  boolean var51 = true;

                  while (var49 || var51) {
                     if (!isRunning) {
                        this.dispose(var16, var46, var21, var47, var48, var8);
                        var9.onProgressCancelled(var1);
                        return new Result(var1, false, "The compression has stopped!", 0L, null, 24, null);
                     }

                     int var52 = var21.dequeueOutputBuffer(var14, 100L);
                     if (var52 == -1) {
                        var51 = false;
                     } else if (var52 == -2) {
                        MediaFormat var57 = var21.getOutputFormat();
                        Intrinsics.checkNotNullExpressionValue(var57, "getOutputFormat(...)");
                        MediaFormat var53 = var57;
                        if (var28 == -5) {
                           var28 = var15.addTrack(var53);
                           if (var25 == -1) {
                              var25 = this.addAudioTrack(var15, var7, var8);
                           }

                           if (!var24) {
                              var15.start();
                              var24 = true;
                           }
                        }
                     } else if (var52 != -3) {
                        if (var52 < 0) {
                           throw new RuntimeException("unexpected result from encoder.dequeueOutputBuffer: " + var52);
                        }

                        ByteBuffer var58 = var21.getOutputBuffer(var52);
                        if (var58 == null) {
                           throw new RuntimeException("encoderOutputBuffer " + var52 + " was null");
                        }

                        ByteBuffer var54 = var58;
                        if (var14.size > 1 && (var14.flags & 2) == 0 && var24) {
                           var15.writeSampleData(var28, var54, var14);
                        }

                        var27 = (var14.flags & 4) != 0;
                        var21.releaseOutputBuffer(var52, false);
                     }

                     if (var52 == -1) {
                        int var55 = var46.dequeueOutputBuffer(var14, 100L);
                        if (var55 == -1) {
                           var49 = false;
                        } else if (var55 != -3 && var55 != -2) {
                           if (var55 < 0) {
                              throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + var55);
                           }

                           boolean var33 = var14.size != 0;
                           var46.releaseOutputBuffer(var55, var33);
                           if (var33) {
                              boolean var34 = false;

                              try {
                                 var48.awaitNewImage();
                              } catch (Exception var38) {
                                 var34 = true;
                                 String var10001 = var38.getMessage();
                                 if (var10001 == null) {
                                    var10001 = "Compression failed at swapping buffer";
                                 }

                                 Log.e("Compressor", var10001);
                              }

                              if (!var34) {
                                 var48.drawImage();
                                 var47.setPresentationTime(var14.presentationTimeUs * 1000);
                                 var9.onProgressChanged(var1, (float)var14.presentationTimeUs / (float)var10 * 100);
                                 var47.swapBuffers();
                              }
                           }

                           if ((var14.flags & 4) != 0) {
                              var49 = false;
                              var21.signalEndOfInputStream();
                           }
                        }
                     }
                  }
               }
            } catch (Exception var39) {
               CompressorUtils.INSTANCE.printException(var39);
               return new Result(var1, false, var39.getMessage(), 0L, null, 24, null);
            }

            this.dispose(var16, var46, var21, var47, var48, var8);
            if (var24) {
               this.processAudio(var15, var25, var14, var7, var8);
            }

            var8.release();

            try {
               var15.stop();
               var15.release();
            } catch (Exception var37) {
               CompressorUtils.INSTANCE.printException(var37);
            }
         } catch (Exception var40) {
            CompressorUtils.INSTANCE.printException(var40);
         }

         Object var41 = null;
         File var42 = var13;
         if (var6 != null) {
            String var43 = var6;
            int var44 = 0;

            try {
               boolean var45 = StreamableVideo.INSTANCE.start(var13, new File(var43));
               var42 = new File(var43);
               if (var45 && var13.exists()) {
                  var13.delete();
               }
            } catch (Exception var36) {
               CompressorUtils.INSTANCE.printException(var36);
            }
         }

         return new Result(var1, true, null, var42.length(), var42.getPath());
      } else {
         return new Result(var1, false, "Something went wrong, please try again", 0L, null, 24, null);
      }
   }

   private final int addAudioTrack(MediaMuxer var1, boolean var2, MediaExtractor var3) {
      int var4 = CompressorUtils.INSTANCE.findTrack(var3, false);
      int var10000;
      if (var4 >= 0 && !var2) {
         MediaFormat var6 = var3.getTrackFormat(var4);
         Intrinsics.checkNotNullExpressionValue(var6, "getTrackFormat(...)");
         MediaFormat var5 = var6;
         var10000 = var1.addTrack(var5);
      } else {
         var10000 = -1;
      }

      return var10000;
   }

   private final void processAudio(MediaMuxer var1, int var2, BufferInfo var3, boolean var4, MediaExtractor var5) {
      int var6 = CompressorUtils.INSTANCE.findTrack(var5, false);
      if (var6 >= 0 && !var4 && var2 >= 0) {
         var5.selectTrack(var6);
         MediaFormat var10000 = var5.getTrackFormat(var6);
         Intrinsics.checkNotNullExpressionValue(var10000, "getTrackFormat(...)");
         MediaFormat var7 = var10000;
         int var8 = var7.getInteger("max-input-size");
         if (var8 <= 0) {
            var8 = 65536;
         }

         ByteBuffer var17 = ByteBuffer.allocateDirect(var8);
         Intrinsics.checkNotNullExpressionValue(var17, "allocateDirect(...)");
         ByteBuffer var9 = var17;
         if (VERSION.SDK_INT >= 28) {
            int var10 = (int)var5.getSampleSize();
            if (var10 > var8) {
               int var15 = var10 + 1024;
               var9 = ByteBuffer.allocateDirect(var15);
            }
         }

         boolean var16 = false;
         var5.seekTo(0L, 0);

         while (!var16) {
            int var11 = var5.getSampleTrackIndex();
            if (var11 == var6) {
               var3.size = var5.readSampleData(var9, 0);
               if (var3.size >= 0) {
                  BufferInfo var12 = var3;
                  BufferInfo var13 = var12;
                  int var14 = 0;
                  var13.presentationTimeUs = var5.getSampleTime();
                  var13.offset = 0;
                  var13.flags = 1;
                  var1.writeSampleData(var2, var9, var3);
                  var5.advance();
               } else {
                  var3.size = 0;
                  var16 = true;
               }
            } else if (var11 == -1) {
               var16 = true;
            }
         }

         var5.unselectTrack(var6);
      }
   }

   private final MediaCodec prepareEncoder(MediaFormat var1, boolean var2) {
      MediaCodec var4 = var2 ? MediaCodec.createByCodecName("c2.android.avc.encoder") : MediaCodec.createEncoderByType("video/avc");
      Intrinsics.checkNotNull(var4);
      MediaCodec var3 = var4;

      try {
         var3.configure(var1, null, null, 1);
      } catch (Exception var5) {
         var3 = MediaCodec.createEncoderByType("video/avc");
         var3.configure(var1, null, null, 1);
      }

      return var3;
   }

   private final MediaCodec prepareDecoder(MediaFormat var1, OutputSurface var2) {
      String var10000 = var1.getString("mime");
      Intrinsics.checkNotNull(var10000);
      MediaCodec var4 = MediaCodec.createDecoderByType(var10000);
      Intrinsics.checkNotNullExpressionValue(var4, "createDecoderByType(...)");
      MediaCodec var3 = var4;
      var3.configure(var1, var2.getSurface(), null, 0);
      return var3;
   }

   private final void dispose(int var1, MediaCodec var2, MediaCodec var3, InputSurface var4, OutputSurface var5, MediaExtractor var6) {
      var6.unselectTrack(var1);
      var2.stop();
      var2.release();
      var3.stop();
      var3.release();
      var4.release();
      var5.release();
   }

   // $VF: synthetic method
   public static final Result access$start(
      Compressor var0,
      int var1,
      int var2,
      int var3,
      String var4,
      int var5,
      String var6,
      boolean var7,
      MediaExtractor var8,
      CompressionProgressListener var9,
      long var10,
      int var12
   ) {
      return var0.start(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12);
   }
}
