package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingResult;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingResult$Error;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingResult$Success;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingState$Idle;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingStopResult;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingStopResult$Error;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$RecordingStopResult$Success;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0004\b\u000b\u0010\fJ\u0013\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t¢\u0006\u0004\b\u000f\u0010\fJ\u0006\u0010\u0010\u001a\u00020\nJ\u0006\u0010\u0011\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/AudioRecordingUseCase;",
         "",
         "audioRecordingManager",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager;Lcom/intercom/twig/Twig;)V",
         "startRecording",
         "Lkotlin/Result;",
         "",
         "startRecording-d1pmJ48",
         "()Ljava/lang/Object;",
         "stopRecording",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordedAudioFile;",
         "stopRecording-d1pmJ48",
         "cancelRecording",
         "cleanupCompletedRecording",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AudioRecordingUseCase {
   @NotNull
   private final AudioRecordingManager audioRecordingManager;
   @NotNull
   private final Twig twig;
   public static final int $stable;

   static { $stable = 8; }

   public AudioRecordingUseCase(@NotNull AudioRecordingManager var1, @NotNull Twig var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "audioRecordingManager");
      Intrinsics.checkNotNullParameter(var2, "twig");
      this.audioRecordingManager = var1;
      this.twig = var2;
   }

   // $VF: synthetic method
   public AudioRecordingUseCase(AudioRecordingManager var1, Twig var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (Twig) (LumberMill.getLogger()) : var2));
   }

   @NotNull
   public final Object recovered$60440b42dcfc0e3e() {
      Object recoveredRecordingResult;
      try {
         if (!Intrinsics.areEqual(this.audioRecordingManager.getRecordingState().getValue(), AudioRecordingManager$RecordingState$Idle.INSTANCE)) {
            this.audioRecordingManager.cancelRecording();
         }

         AudioRecordingManager$RecordingResult recordingResult = this.audioRecordingManager.startRecording();
         Object var10000;
         if (recordingResult instanceof AudioRecordingManager$RecordingResult$Success) {
            var10000 = Result.recovered$52f8e2dc6aafd1c0(Unit.INSTANCE);
         } else {
            if (!(recordingResult instanceof AudioRecordingManager$RecordingResult$Error)) {
               throw new NoWhenBranchMatchedException();
            }

            this.twig.e("Failed to start recording: " + ((AudioRecordingManager$RecordingResult$Error)recordingResult).getMessage());
            this.audioRecordingManager.cancelRecording();
            var10000 = Result.recovered$52f8e2dc6aafd1c0(
               ResultKt.createFailure(new RecordingException(((AudioRecordingManager$RecordingResult$Error)recordingResult).getMessage(), null, 2, null))
            );
         }

         recoveredRecordingResult = var10000;
      } catch (Exception var3) {
         this.twig.e(var3, "Failed to start recording");
         this.audioRecordingManager.cancelRecording();
         recoveredRecordingResult = Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(var3));
      }

      return recoveredRecordingResult;
   }

   @NotNull
   public final Object recovered$d404386be90d0798() {
      Object var1;
      try {
         AudioRecordingManager$RecordingStopResult var4 = this.audioRecordingManager.stopRecording();
         Object var10000;
         if (var4 instanceof AudioRecordingManager$RecordingStopResult$Success) {
            var10000 = Result.recovered$52f8e2dc6aafd1c0(
               new RecordedAudioFile(
                  ((AudioRecordingManager$RecordingStopResult$Success)var4).getFile(),
                  ((AudioRecordingManager$RecordingStopResult$Success)var4).getUri(),
                  ((AudioRecordingManager$RecordingStopResult$Success)var4).getDurationMs()
               )
            );
         } else {
            if (!(var4 instanceof AudioRecordingManager$RecordingStopResult$Error)) {
               throw new NoWhenBranchMatchedException();
            }

            this.twig.e("Failed to stop recording: " + ((AudioRecordingManager$RecordingStopResult$Error)var4).getMessage());
            this.audioRecordingManager.cancelRecording();
            var10000 = Result.recovered$52f8e2dc6aafd1c0(
               ResultKt.createFailure(new RecordingException(((AudioRecordingManager$RecordingStopResult$Error)var4).getMessage(), null, 2, null))
            );
         }

         var1 = var10000;
      } catch (Exception var3) {
         this.twig.e(var3, "Failed to stop recording");
         this.audioRecordingManager.cancelRecording();
         var1 = Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(var3));
      }

      return var1;
   }

   public final void cancelRecording() {
      this.audioRecordingManager.cancelRecording();
   }

   public final void cleanupCompletedRecording() {
      this.audioRecordingManager.cleanupCompletedRecording();
   }
}
