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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingState$Completed;",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingState;",
         "uri",
         "Landroid/net/Uri;",
         "file",
         "Ljava/io/File;",
         "<init>",
         "(Landroid/net/Uri;Ljava/io/File;)V",
         "getUri",
         "()Landroid/net/Uri;",
         "getFile",
         "()Ljava/io/File;",
         "component1",
         "component2",
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
public final class AudioRecordingManager$RecordingState$Completed implements AudioRecordingManager$RecordingState {
   @NotNull
   private final Uri uri;
   @NotNull
   private final File file;
   public static final int $stable;

   static { $stable = 8; }

   public AudioRecordingManager$RecordingState$Completed(@NotNull Uri var1, @NotNull File var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "uri");
      Intrinsics.checkNotNullParameter(var2, "file");
      this.uri = var1;
      this.file = var2;
   }

   @NotNull
   public final Uri getUri() {
      return this.uri;
   }

   @NotNull
   public final File getFile() {
      return this.file;
   }

   @NotNull
   public final Uri component1() {
      return this.uri;
   }

   @NotNull
   public final File component2() {
      return this.file;
   }

   @NotNull
   public final AudioRecordingManager$RecordingState$Completed copy(@NotNull Uri var1, @NotNull File var2) {
      Intrinsics.checkNotNullParameter(var1, "uri");
      Intrinsics.checkNotNullParameter(var2, "file");
      return new AudioRecordingManager$RecordingState$Completed(var1, var2);
   }

   // $VF: synthetic method
   public static AudioRecordingManager$RecordingState$Completed copy$default(
      AudioRecordingManager$RecordingState$Completed var0, Uri var1, File var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.uri;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.file;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Completed(uri=" + this.uri + ", file=" + this.file + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.uri.hashCode();
      return var1 * 31 + this.file.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AudioRecordingManager$RecordingState$Completed)) {
         return false;
      }

      AudioRecordingManager$RecordingState$Completed var2 = (AudioRecordingManager$RecordingState$Completed)var1;
      return !Intrinsics.areEqual(this.uri, var2.uri) ? false : Intrinsics.areEqual(this.file, var2.file);
   }
}
