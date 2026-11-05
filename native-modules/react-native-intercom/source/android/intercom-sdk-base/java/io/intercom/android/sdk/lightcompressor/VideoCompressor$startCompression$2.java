package io.intercom.android.sdk.lightcompressor;

import android.content.Context;
import android.net.Uri;
import io.intercom.android.sdk.lightcompressor.compressor.Compressor;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "VideoCompressor.kt",
   l = 208,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.lightcompressor.VideoCompressor$startCompression$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/lightcompressor/video/Result;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class VideoCompressor$startCompression$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Uri $srcUri;
   // $VF: synthetic field
   final String $destPath;
   // $VF: synthetic field
   final String $streamableFile;
   // $VF: synthetic field
   final Configuration $configuration;
   // $VF: synthetic field
   final CompressionListener $listener;

   VideoCompressor$startCompression$2(
      int var1, Context var2, Uri var3, String var4, String var5, Configuration var6, CompressionListener var7, Continuation var8
   ) {
      super(2, var8);
      this.$index = var1;
      this.$context = var2;
      this.$srcUri = var3;
      this.$destPath = var4;
      this.$streamableFile = var5;
      this.$configuration = var6;
      this.$listener = var7;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Compressor recoveredCompressor = Compressor.INSTANCE;
            int var10001 = this.$index;
            Context var10002 = this.$context;
            Uri var10003 = this.$srcUri;
            String var10004 = this.$destPath;
            String var10005 = this.$streamableFile;
            Configuration var10006 = this.$configuration;
            CompressionProgressListener var10007 = new VideoCompressor$startCompression$2$1(this.$listener);
            Continuation var10008 = this;
            this.label = 1;
            var10000 = recoveredCompressor.compressVideo(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008);
            if (var10000 == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return var10000;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new VideoCompressor$startCompression$2(
         this.$index, this.$context, this.$srcUri, this.$destPath, this.$streamableFile, this.$configuration, this.$listener, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((VideoCompressor$startCompression$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
