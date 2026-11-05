package io.intercom.android.sdk.lightcompressor;

import kotlin.Metadata;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\t",
   d2 = {
         "io/intercom/android/sdk/lightcompressor/VideoCompressor$startCompression$2$1",
         "Lio/intercom/android/sdk/lightcompressor/CompressionProgressListener;",
         "onProgressChanged",
         "",
         "index",
         "",
         "percent",
         "",
         "onProgressCancelled",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class VideoCompressor$startCompression$2$1 implements CompressionProgressListener {
   // $VF: synthetic field
   final CompressionListener $listener;

   VideoCompressor$startCompression$2$1(CompressionListener var1) {
      this.$listener = var1;
   }

   @Override
   public void onProgressChanged(int var1, float var2) {
      this.$listener.onProgress(var1, var2);
   }

   @Override
   public void onProgressCancelled(int var1) {
      this.$listener.onCancelled(var1);
   }
}
