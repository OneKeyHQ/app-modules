package io.intercom.android.sdk.m5.utils;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "MediaCompression.kt",
   l = 159,
   i = {0, 0, 0},
   s = {"L$0", "L$1", "L$2"},
   n = {"uncompressedMediaData", "context", "videoNameWithoutExtension"},
   m = "getCompressedMediaData",
   c = "io.intercom.android.sdk.m5.utils.MediaCompressionKt"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class MediaCompressionKt$getCompressedMediaData$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   Object L$2;
   // $VF: synthetic field
   Object result;
   int label;

   MediaCompressionKt$getCompressedMediaData$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return MediaCompressionKt.getCompressedMediaData(null, null, 0, this);
   }
}
