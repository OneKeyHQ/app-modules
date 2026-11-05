package io.intercom.android.sdk.m5.upload.data;

import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import java.io.InputStream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
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
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\n\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/UploadRequestBody;",
         "Lokhttp3/RequestBody;",
         "context",
         "Landroid/content/Context;",
         "media",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "<init>",
         "(Landroid/content/Context;Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;)V",
         "contentType",
         "Lokhttp3/MediaType;",
         "contentLength",
         "",
         "writeTo",
         "",
         "sink",
         "Lokio/BufferedSink;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UploadRequestBody extends RequestBody {
   @NotNull
   public static final UploadRequestBody$Companion Companion = new UploadRequestBody$Companion(null);
   @NotNull
   private final Context context;
   @NotNull
   private final MediaData$Media media;
   public static final int $stable;

   static { $stable = 8; }
   private static final int BUFFER_SIZE = 2048;

   public UploadRequestBody(@NotNull Context var1, @NotNull MediaData$Media var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "media");
      super();
      this.context = var1;
      this.media = var2;
   }

   @Nullable
   @Override
   public MediaType contentType() {
      return MediaType.Companion.parse(this.media.getMimeType());
   }

   @Override
   public long contentLength() {
      return this.media.getSize();
   }

   @Override
   public void writeTo(@NotNull BufferedSink var1) throws java.io.IOException {
      Intrinsics.checkNotNullParameter(var1, "sink");
      InputStream var2 = this.context.getContentResolver().openInputStream(this.media.getUri());
      if (var2 != null) {
         var var3 = var2;
         Throwable var4 = null;

         try {
            InputStream var5 = var3;
            int var6 = 0;
            byte[] var7 = new byte[2048];

            while (true) {
               int var8 = var5.read(var7);
               if (var8 == -1) {
                  Unit var13 = Unit.INSTANCE;
                  break;
               }

               var1.write(var7, 0, var8);
            }
         } catch (Throwable var11) {
            var4 = var11;
            throw var11;
         } finally {
            CloseableKt.closeFinally(var3, var4);
         }
      }
   }
}
