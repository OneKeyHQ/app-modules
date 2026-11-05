package io.intercom.android.sdk.m5.conversation.utils.audio;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioConstants;",
         "",
         "<init>",
         "()V",
         "AUDIO_MEDIA_TYPE",
         "",
         "MAX_AUDIO_FILE_SIZE_MB",
         "",
         "MAX_RECORDING_DURATION_MS",
         "",
         "MIN_RECORDING_DURATION_MS",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class AudioConstants {
   @NotNull
   public static final AudioConstants INSTANCE = new AudioConstants();
   @NotNull
   public static final String AUDIO_MEDIA_TYPE = "audio/m4a";
   public static final int MAX_AUDIO_FILE_SIZE_MB = 10;
   public static final long MAX_RECORDING_DURATION_MS = 300000L;
   public static final long MIN_RECORDING_DURATION_MS = 1000L;
   public static int $stable;

   static {}

   private AudioConstants() {
   }
}
