package io.intercom.android.sdk.m5.utils;

import android.net.Uri;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.lightcompressor.CompressionListener;
import java.io.File;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000/\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\"\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0018\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\nH\u0016J\u0018\u0010\r\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0011",
   d2 = {
         "io/intercom/android/sdk/m5/utils/MediaCompressionKt$getCompressedVideoData$2$1",
         "Lio/intercom/android/sdk/lightcompressor/CompressionListener;",
         "onStart",
         "",
         "index",
         "",
         "onSuccess",
         "size",
         "",
         "path",
         "",
         "onFailure",
         "failureMessage",
         "onProgress",
         "percent",
         "",
         "onCancelled",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaCompression.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaCompression.kt\nio/intercom/android/sdk/m5/utils/MediaCompressionKt$getCompressedVideoData$2$1\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,215:1\n1#2:216\n*E\n"
)
public final class MediaCompressionKt$getCompressedVideoData$2$1 implements CompressionListener {
   // $VF: synthetic field
   final Twig $twig;
   // $VF: synthetic field
   final Continuation $continuation;

   MediaCompressionKt$getCompressedVideoData$2$1(Twig var1, Continuation var2) {
      this.$twig = var1;
      this.$continuation = var2;
   }

   @Override
   public void onStart(int var1) {
      this.$twig.internal("Video compression started.");
   }

   @Override
   public void onSuccess(int var1, long var2, String var4) {
      this.$twig.internal("Video compression succeeded. Video saved in " + var4 + '.');
      File var10000;
      if (var4 != null) {
         String var6 = var4;
         int var7 = 0;
         var10000 = new File(var6);
      } else {
         var10000 = null;
      }

      Uri var5 = Uri.fromFile(var10000);
      this.$continuation.resumeWith(Result.recovered$52f8e2dc6aafd1c0(TuplesKt.to(var5, var2)));
   }

   @Override
   public void onFailure(int var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "failureMessage");
      this.$twig.internal("Video compression failed: " + var2 + '.');
      this.$continuation.resumeWith(Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(new Throwable(var2))));
   }

   @Override
   public void onProgress(int var1, float var2) {
      this.$twig.internal("Video compression in progress: " + var2 + '.');
   }

   @Override
   public void onCancelled(int var1) {
      this.$twig.internal("Video compression cancelled.");
      this.$continuation.resumeWith(Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(new CancellationException())));
   }
}
