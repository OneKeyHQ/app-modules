package io.intercom.android.sdk.m5.conversation.utils.audio;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÇ\n\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H×\u0003J\t\u0010\b\u001a\u00020\tH×\u0001J\t\u0010\n\u001a\u00020\u000bH×\u0001¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingState$Idle;",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingState;",
         "<init>",
         "()V",
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
@StabilityInferred(parameters = 1)
public final class AudioRecordingManager$RecordingState$Idle implements AudioRecordingManager$RecordingState {
   @NotNull
   public static final AudioRecordingManager$RecordingState$Idle INSTANCE = new AudioRecordingManager$RecordingState$Idle();
   public static int $stable;

   static {}

   private AudioRecordingManager$RecordingState$Idle() {
   }

   @NotNull
   @Override
   public String toString() {
      return "Idle";
   }

   @Override
   public int hashCode() {
      return -114329640;
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      return this == var1 ? true : var1 instanceof AudioRecordingManager$RecordingState$Idle;
   }
}
