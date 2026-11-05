package io.intercom.android.sdk.m5.conversation.utils.audio;

import android.net.Uri;
import androidx.compose.runtime.internal.StabilityInferred;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u001bH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingStopResult$Success;",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingStopResult;",
         "uri",
         "Landroid/net/Uri;",
         "file",
         "Ljava/io/File;",
         "durationMs",
         "",
         "<init>",
         "(Landroid/net/Uri;Ljava/io/File;J)V",
         "getUri",
         "()Landroid/net/Uri;",
         "getFile",
         "()Ljava/io/File;",
         "getDurationMs",
         "()J",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AudioRecordingManager$RecordingStopResult$Success implements AudioRecordingManager$RecordingStopResult {
   @NotNull
   private final Uri uri;
   @NotNull
   private final File file;
   private final long durationMs;
   public static final int $stable;

   static { $stable = 8; }

   public AudioRecordingManager$RecordingStopResult$Success(@NotNull Uri var1, @NotNull File var2, long var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "uri");
      Intrinsics.checkNotNullParameter(var2, "file");
      this.uri = var1;
      this.file = var2;
      this.durationMs = var3;
   }

   @NotNull
   public final Uri getUri() {
      return this.uri;
   }

   @NotNull
   public final File getFile() {
      return this.file;
   }

   public final long getDurationMs() {
      return this.durationMs;
   }

   @NotNull
   public final Uri component1() {
      return this.uri;
   }

   @NotNull
   public final File component2() {
      return this.file;
   }

   public final long component3() {
      return this.durationMs;
   }

   @NotNull
   public final AudioRecordingManager$RecordingStopResult$Success copy(@NotNull Uri var1, @NotNull File var2, long var3) {
      Intrinsics.checkNotNullParameter(var1, "uri");
      Intrinsics.checkNotNullParameter(var2, "file");
      return new AudioRecordingManager$RecordingStopResult$Success(var1, var2, var3);
   }

   // $VF: synthetic method
   public static AudioRecordingManager$RecordingStopResult$Success copy$default(
      AudioRecordingManager$RecordingStopResult$Success var0, Uri var1, File var2, long var3, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.uri;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.file;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.durationMs;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Success(uri=" + this.uri + ", file=" + this.file + ", durationMs=" + this.durationMs + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.uri.hashCode();
      var1 = var1 * 31 + this.file.hashCode();
      return var1 * 31 + Long.hashCode(this.durationMs);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AudioRecordingManager$RecordingStopResult$Success)) {
         return false;
      } else {
         AudioRecordingManager$RecordingStopResult$Success var2 = (AudioRecordingManager$RecordingStopResult$Success)var1;
         if (!Intrinsics.areEqual(this.uri, var2.uri)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.file, var2.file) ? false : this.durationMs == var2.durationMs;
         }
      }
   }
}
