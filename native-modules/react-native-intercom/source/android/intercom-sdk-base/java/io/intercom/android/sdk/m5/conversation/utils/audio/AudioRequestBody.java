package io.intercom.android.sdk.m5.conversation.utils.audio;

import androidx.compose.runtime.internal.StabilityInferred;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRequestBody;",
         "Lokhttp3/RequestBody;",
         "audioFile",
         "Ljava/io/File;",
         "<init>",
         "(Ljava/io/File;)V",
         "fileRequestBody",
         "contentType",
         "Lokhttp3/MediaType;",
         "contentLength",
         "",
         "writeTo",
         "",
         "sink",
         "Lokio/BufferedSink;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AudioRequestBody extends RequestBody {
   @NotNull
   private final File audioFile;
   @NotNull
   private final RequestBody fileRequestBody;
   public static final int $stable;

   static { $stable = 8; }

   public AudioRequestBody(@NotNull File var1) {
      Intrinsics.checkNotNullParameter(var1, "audioFile");
      super();
      this.audioFile = var1;
      this.fileRequestBody = RequestBody.Companion.create(this.audioFile, MediaType.Companion.parse("audio/m4a"));
   }

   @Nullable
   @Override
   public MediaType contentType() {
      return this.fileRequestBody.contentType();
   }

   @Override
   public long contentLength() {
      return this.fileRequestBody.contentLength();
   }

   @Override
   public void writeTo(@NotNull BufferedSink var1) throws java.io.IOException {
      Intrinsics.checkNotNullParameter(var1, "sink");
      this.fileRequestBody.writeTo(var1);
   }
}
