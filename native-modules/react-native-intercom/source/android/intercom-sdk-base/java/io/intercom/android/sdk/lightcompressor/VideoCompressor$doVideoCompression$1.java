package io.intercom.android.sdk.lightcompressor;

import android.content.Context;
import android.net.Uri;
import io.intercom.android.sdk.lightcompressor.compressor.Compressor;
import io.intercom.android.sdk.lightcompressor.config.AppSpecificStorageConfiguration;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import io.intercom.android.sdk.lightcompressor.config.SharedStorageConfiguration;
import io.intercom.android.sdk.lightcompressor.video.Result;
import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "VideoCompressor.kt",
   l = 156,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.lightcompressor.VideoCompressor$doVideoCompression$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class VideoCompressor$doVideoCompression$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final List $uris;
   // $VF: synthetic field
   final int $i;
   // $VF: synthetic field
   final SharedStorageConfiguration $sharedStorageConfiguration;
   // $VF: synthetic field
   final AppSpecificStorageConfiguration $appSpecificStorageConfiguration;
   // $VF: synthetic field
   final boolean $isStreamable;
   // $VF: synthetic field
   final Configuration $configuration;
   // $VF: synthetic field
   final ObjectRef $streamableFile;
   // $VF: synthetic field
   final CompressionListener $listener;

   VideoCompressor$doVideoCompression$1(
      Context var1,
      List var2,
      int var3,
      SharedStorageConfiguration var4,
      AppSpecificStorageConfiguration var5,
      boolean var6,
      Configuration var7,
      ObjectRef var8,
      CompressionListener var9,
      Continuation var10
   ) {
      super(2, var10);
      this.$context = var1;
      this.$uris = var2;
      this.$i = var3;
      this.$sharedStorageConfiguration = var4;
      this.$appSpecificStorageConfiguration = var5;
      this.$isStreamable = var6;
      this.$configuration = var7;
      this.$streamableFile = var8;
      this.$listener = var9;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      CompressionListener var10000;
      int var10001;
      String var10002;
      label99: {
         label91: {
            Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            Object var18;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(var1);

                  try {
                     String var3 = VideoCompressor.access$getMediaPath(VideoCompressor.INSTANCE, this.$context, (Uri)this.$uris.get(this.$i));
                     File var19 = VideoCompressor.access$saveVideoFile(
                        VideoCompressor.INSTANCE,
                        this.$context,
                        var3,
                        this.$sharedStorageConfiguration,
                        this.$appSpecificStorageConfiguration,
                        Boxing.boxBoolean(this.$isStreamable),
                        (String)this.$configuration.getVideoNames().get(this.$i),
                        Boxing.boxBoolean(false)
                     );
                     if (var19 == null) {
                        return Unit.INSTANCE;
                     }

                     File var4 = var19;
                     if (this.$isStreamable) {
                        this.$streamableFile.element = VideoCompressor.access$saveVideoFile(
                           VideoCompressor.INSTANCE,
                           this.$context,
                           var3,
                           this.$sharedStorageConfiguration,
                           this.$appSpecificStorageConfiguration,
                           null,
                           (String)this.$configuration.getVideoNames().get(this.$i),
                           Boxing.boxBoolean(false)
                        );
                     }

                     Compressor.INSTANCE.setRunning(true);
                     this.$listener.onStart(this.$i);
                     VideoCompressor var20 = VideoCompressor.INSTANCE;
                     var10001 = this.$i;
                     Context var26 = this.$context;
                     Uri var10003 = (Uri)this.$uris.get(this.$i);
                     String var5 = var4.getPath();
                     Intrinsics.checkNotNullExpressionValue(var5, "getPath(...)");
                     String var10005 = (File)this.$streamableFile.element != null ? ((File)this.$streamableFile.element).getPath() : null;
                     Configuration var10006 = this.$configuration;
                     CompressionListener var10007 = this.$listener;
                     Continuation var10008 = this;
                     this.label = 1;
                     var18 = VideoCompressor.access$startCompression(var20, var10001, var26, var10003, var5, var10005, var10006, var10007, var10008);
                  } catch (CancellationException var13) {
                     throw var13;
                  } catch (Exception var14) {
                     var10000 = this.$listener;
                     var10001 = this.$i;
                     var10002 = var14.getMessage();
                     if (var10002 != null) {
                        break label99;
                     }
                     break label91;
                  }

                  if (var18 == var6) {
                     return var6;
                  }
                  break;
               case 1:
                  try {
                     ResultKt.throwOnFailure(var1);
                     var18 = var1;
                     break;
                  } catch (CancellationException var11) {
                     throw var11;
                  } catch (Exception var12) {
                     var10000 = this.$listener;
                     var10001 = this.$i;
                     var10002 = var12.getMessage();
                     if (var10002 != null) {
                        break label99;
                     }
                     break label91;
                  }
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            Result var15;
            try {
               var15 = (Result)var18;
            } catch (CancellationException var9) {
               throw var9;
            } catch (Exception var10) {
               var10000 = this.$listener;
               var10001 = this.$i;
               var10002 = var10.getMessage();
               if (var10002 != null) {
                  break label99;
               }
               break label91;
            }

            Result var2 = var15;
            if (!var2.getSuccess()) {
               CompressionListener var22 = this.$listener;
               var10001 = this.$i;
               var10002 = var2.getFailureMessage();
               if (var10002 == null) {
                  var10002 = "An error has occurred!";
               }

               var22.onFailure(var10001, var10002);
               return Unit.INSTANCE;
            }

            File var17;
            try {
               var17 = VideoCompressor.access$saveVideoFile(
                  VideoCompressor.INSTANCE,
                  this.$context,
                  var2.getPath(),
                  this.$sharedStorageConfiguration,
                  this.$appSpecificStorageConfiguration,
                  Boxing.boxBoolean(this.$isStreamable),
                  (String)this.$configuration.getVideoNames().get(this.$i),
                  Boxing.boxBoolean(true)
               );
            } catch (CancellationException var7) {
               throw var7;
            } catch (Exception var8) {
               CompressionListener var21 = this.$listener;
               var10001 = this.$i;
               var10002 = var8.getMessage();
               if (var10002 == null) {
                  var10002 = "An error has occurred!";
               }

               var21.onFailure(var10001, var10002);
               return Unit.INSTANCE;
            }

            File var16 = var17;
            this.$listener.onSuccess(this.$i, var2.getSize(), var16 != null ? var16.getPath() : null);
            return Unit.INSTANCE;
         }

         var10002 = "An error has occurred!";
      }

      var10000.onFailure(var10001, var10002);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new VideoCompressor$doVideoCompression$1(
         this.$context,
         this.$uris,
         this.$i,
         this.$sharedStorageConfiguration,
         this.$appSpecificStorageConfiguration,
         this.$isStreamable,
         this.$configuration,
         this.$streamableFile,
         this.$listener,
         var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((VideoCompressor$doVideoCompression$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
