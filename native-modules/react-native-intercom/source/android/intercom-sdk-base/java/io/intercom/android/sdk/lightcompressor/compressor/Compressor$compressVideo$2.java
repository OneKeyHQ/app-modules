package io.intercom.android.sdk.lightcompressor.compressor;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import io.intercom.android.sdk.lightcompressor.CompressionProgressListener;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import io.intercom.android.sdk.lightcompressor.utils.CompressorUtils;
import io.intercom.android.sdk.lightcompressor.video.Result;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "Compressor.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.lightcompressor.compressor.Compressor$compressVideo$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/lightcompressor/video/Result;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class Compressor$compressVideo$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Uri $srcUri;
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final Configuration $configuration;
   // $VF: synthetic field
   final String $destination;
   // $VF: synthetic field
   final String $streamableFile;
   // $VF: synthetic field
   final CompressionProgressListener $listener;

   Compressor$compressVideo$2(
      Context var1, Uri var2, int var3, Configuration var4, String var5, String var6, CompressionProgressListener var7, Continuation var8
   ) {
      super(2, var8);
      this.$context = var1;
      this.$srcUri = var2;
      this.$index = var3;
      this.$configuration = var4;
      this.$destination = var5;
      this.$streamableFile = var6;
      this.$listener = var7;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            MediaExtractor var3 = new MediaExtractor();
            MediaMetadataRetriever var4 = new MediaMetadataRetriever();

            try {
               var4.setDataSource(this.$context, this.$srcUri);
            } catch (IllegalArgumentException var24) {
               CompressorUtils.INSTANCE.printException(var24);
               return new Result(this.$index, false, String.valueOf(var24.getMessage()), 0L, null, 24, null);
            }

            Context var6 = this.$context;
            Uri var7 = this.$srcUri;

            try {
               int var9 = 0;
               var3.setDataSource(var6, var7, null);
               Object var26 = kotlin.Result.recovered$52f8e2dc6aafd1c0(Unit.INSTANCE);
            } catch (Throwable var23) {
               Object var8 = kotlin.Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(var23));
            }

            double var5 = CompressorUtils.INSTANCE.prepareVideoHeight(var4);
            double var25 = CompressorUtils.INSTANCE.prepareVideoWidth(var4);
            String var27 = var4.extractMetadata(24);
            String var10 = var4.extractMetadata(20);
            String var11 = var4.extractMetadata(9);
            CharSequence var12 = var27;
            if (var12 != null && var12.length() != 0) {
               var12 = var10;
               if (var12 != null && var12.length() != 0) {
                  var12 = var11;
                  if (var12 != null && var12.length() != 0) {
                     Triple var13;
                     try {
                        var13 = new Triple(
                           Boxing.boxInt(Integer.parseInt(var27)), Boxing.boxInt(Integer.parseInt(var10)), Boxing.boxLong(Long.parseLong(var11) * 1000)
                        );
                     } catch (Exception var22) {
                        return new Result(this.$index, false, "Failed to extract video meta-data, please try again", 0L, null, 24, null);
                     }

                     Triple var30 = var13;
                     int var31 = ((Number)var30.component1()).intValue();
                     int var14 = ((Number)var30.component2()).intValue();
                     long var15 = ((Number)var30.component3()).longValue();
                     if (this.$configuration.isMinBitrateCheckEnabled() && var14 <= 2000000) {
                        return new Result(
                           this.$index,
                           false,
                           "The provided bitrate is smaller than what is needed for compression try to set isMinBitRateEnabled to false",
                           0L,
                           null,
                           24,
                           null
                        );
                     }

                     int var10000;
                     if (this.$configuration.getVideoBitrateInMbps() == null) {
                        var10000 = CompressorUtils.INSTANCE.getBitrate(var14, this.$configuration.getQuality());
                     } else {
                        Integer var33 = this.$configuration.getVideoBitrateInMbps();
                        Intrinsics.checkNotNull(var33);
                        var10000 = var33 * 1000000;
                     }

                     int var17 = var10000;
                     Pair var34;
                     if (this.$configuration.getVideoHeight() != null) {
                        Double var10002 = this.$configuration.getVideoWidth();
                        Integer var37 = var10002 != null ? Boxing.boxInt((int)var10002.doubleValue()) : null;
                        Double var10003 = this.$configuration.getVideoHeight();
                        var34 = new Pair(var37, var10003 != null ? Boxing.boxInt((int)var10003.doubleValue()) : null);
                     } else {
                        var34 = CompressorUtils.INSTANCE.generateWidthAndHeight(var25, var5, this.$configuration.getKeepOriginalResolution());
                     }

                     Pair var18 = var34;
                     Integer var19 = (Integer)var18.component1();
                     Integer var20 = (Integer)var18.component2();
                     switch (var31) {
                        case 90:
                        case 270:
                           Integer var21 = var20;
                           var20 = var19;
                           var19 = var21;
                           var10000 = 0;
                           break;
                        case 180:
                           var10000 = 0;
                           break;
                        default:
                           var10000 = var31;
                     }

                     int rotationx = var10000;
                     Compressor var36 = Compressor.INSTANCE;
                     int var10001 = this.$index;
                     Intrinsics.checkNotNull(var19);
                     int var38 = var19;
                     Intrinsics.checkNotNull(var20);
                     return Compressor.access$start(
                        var36,
                        var10001,
                        var38,
                        var20,
                        this.$destination,
                        var17,
                        this.$streamableFile,
                        this.$configuration.getDisableAudio(),
                        var3,
                        this.$listener,
                        var15,
                        rotationx
                     );
                  }
               }
            }

            return new Result(this.$index, false, "Failed to extract video meta-data, please try again", 0L, null, 24, null);
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      Compressor$compressVideo$2 var3 = new Compressor$compressVideo$2(
         this.$context, this.$srcUri, this.$index, this.$configuration, this.$destination, this.$streamableFile, this.$listener, var2
      );
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((Compressor$compressVideo$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
