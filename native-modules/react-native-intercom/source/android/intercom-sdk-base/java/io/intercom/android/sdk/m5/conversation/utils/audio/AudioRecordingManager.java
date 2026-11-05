package io.intercom.android.sdk.m5.conversation.utils.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Build.VERSION;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.content.FileProvider;
import io.intercom.android.sdk.IntercomFileProviderKt;
import io.intercom.android.sdk.utilities.IoUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\b\u0001\u0018\u0000 12\u00020\u0001:\u00041234B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u001b\u001a\u00020\u001cJ\u0006\u0010\u001d\u001a\u00020\u001eJ\u0006\u0010\u001f\u001a\u00020 J\u0006\u0010!\u001a\u00020 J\u0006\u0010\"\u001a\u00020 J\b\u0010#\u001a\u00020\u000bH\u0002J\b\u0010$\u001a\u00020\tH\u0002J\u0018\u0010%\u001a\u00020 2\u0006\u0010&\u001a\u00020\t2\u0006\u0010'\u001a\u00020\u000bH\u0002J\b\u0010(\u001a\u00020 H\u0002J\u0010\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u000bH\u0002J\u0010\u0010,\u001a\u00020 2\u0006\u0010&\u001a\u00020\tH\u0002J\b\u0010-\u001a\u00020 H\u0002J\u0010\u0010.\u001a\u00020\u00182\u0006\u0010/\u001a\u000200H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0014¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u0014¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016¨\u00065",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager;",
         "",
         "context",
         "Landroid/content/Context;",
         "amplitudeScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "<init>",
         "(Landroid/content/Context;Lkotlinx/coroutines/CoroutineScope;)V",
         "mediaRecorder",
         "Landroid/media/MediaRecorder;",
         "currentRecordingFile",
         "Ljava/io/File;",
         "amplitudeMonitoringJob",
         "Lkotlinx/coroutines/Job;",
         "recordingStartElapsedMs",
         "",
         "_recordingState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingState;",
         "recordingState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getRecordingState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "_amplitudeLevel",
         "",
         "amplitudeLevel",
         "getAmplitudeLevel",
         "startRecording",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingResult;",
         "stopRecording",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager$RecordingStopResult;",
         "cancelRecording",
         "",
         "cleanupCompletedRecording",
         "dispose",
         "createAudioFile",
         "createMediaRecorder",
         "configureRecorder",
         "recorder",
         "outputFile",
         "cleanup",
         "waitForFileAvailability",
         "",
         "file",
         "startAmplitudeMonitoring",
         "stopAmplitudeMonitoring",
         "normalizeAmplitude",
         "rawAmplitude",
         "",
         "Companion",
         "RecordingState",
         "RecordingResult",
         "RecordingStopResult",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AudioRecordingManager {
   @NotNull
   public static final AudioRecordingManager$Companion Companion = new AudioRecordingManager$Companion(null);
   @NotNull
   private final Context context;
   @NotNull
   private final CoroutineScope amplitudeScope;
   @Nullable
   private MediaRecorder mediaRecorder;
   @Nullable
   private File currentRecordingFile;
   @Nullable
   private Job amplitudeMonitoringJob;
   private long recordingStartElapsedMs;
   @NotNull
   private final MutableStateFlow _recordingState;
   @NotNull
   private final StateFlow recordingState;
   @NotNull
   private final MutableStateFlow _amplitudeLevel;
   @NotNull
   private final StateFlow amplitudeLevel;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String AUDIO_RECORDINGS_FOLDER = "audio_recordings";
   @NotNull
   private static final String AUDIO_FILE_EXTENSION = "m4a";
   private static final int AUDIO_SAMPLE_RATE = 44100;
   private static final int AUDIO_BIT_RATE = 128000;
   private static final int FILE_AVAILABILITY_MAX_RETRIES = 10;
   private static final long FILE_AVAILABILITY_BASE_DELAY_MS = 10L;
   private static final long AMPLITUDE_MONITORING_INTERVAL_MS = 60L;
   private static final double MAX_AMPLITUDE_REFERENCE = 32767.0;

   public AudioRecordingManager(@NotNull Context var1, @NotNull CoroutineScope var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "amplitudeScope");
      this.context = var1;
      this.amplitudeScope = var2;
      this._recordingState = StateFlowKt.MutableStateFlow(AudioRecordingManager$RecordingState$Idle.INSTANCE);
      this.recordingState = FlowKt.asStateFlow(this._recordingState);
      this._amplitudeLevel = StateFlowKt.MutableStateFlow(0.0F);
      this.amplitudeLevel = FlowKt.asStateFlow(this._amplitudeLevel);
   }

   @NotNull
   public final StateFlow getRecordingState() {
      return this.recordingState;
   }

   @NotNull
   public final StateFlow getAmplitudeLevel() {
      return this.amplitudeLevel;
   }

   @NotNull
   public final AudioRecordingManager$RecordingResult startRecording() {
      try {
         if (!Intrinsics.areEqual(this._recordingState.getValue(), AudioRecordingManager$RecordingState$Idle.INSTANCE)) {
            return new AudioRecordingManager$RecordingResult$Error("Recording already in progress");
         }

         File var1 = this.createAudioFile();
         this.currentRecordingFile = var1;
         MediaRecorder var2 = this.createMediaRecorder();
         this.mediaRecorder = var2;
         this.configureRecorder(var2, var1);
         var2.prepare();
         var2.start();
         this.recordingStartElapsedMs = SystemClock.elapsedRealtime();
         this.startAmplitudeMonitoring(var2);
         this._recordingState.setValue(new AudioRecordingManager$RecordingState$Recording(var1));
         return AudioRecordingManager$RecordingResult$Success.INSTANCE;
      } catch (IOException var3) {
         this.stopAmplitudeMonitoring();
         this.cleanup();
         this._recordingState.setValue(new AudioRecordingManager$RecordingState$Error("Failed to start recording: " + var3.getMessage()));
         return new AudioRecordingManager$RecordingResult$Error("Failed to start recording: " + var3.getMessage());
      } catch (Exception var4) {
         this.stopAmplitudeMonitoring();
         this.cleanup();
         this._recordingState.setValue(new AudioRecordingManager$RecordingState$Error("Unexpected error: " + var4.getMessage()));
         return new AudioRecordingManager$RecordingResult$Error("Unexpected error: " + var4.getMessage());
      }
   }

   @NotNull
   public final AudioRecordingManager$RecordingStopResult stopRecording() {
      try {
         MediaRecorder var1 = this.mediaRecorder;
         File var2 = this.currentRecordingFile;
         if (var1 != null && var2 != null) {
            this.stopAmplitudeMonitoring();
            long var3 = SystemClock.elapsedRealtime() - this.recordingStartElapsedMs;

            try {
               var1.stop();
            } catch (Exception var7) {
               if (var3 >= 1000L) {
                  throw var7;
               }

               var1.release();
               this.mediaRecorder = null;
               Uri var6 = FileProvider.getUriForFile(this.context, IntercomFileProviderKt.fileProviderAuthority(this.context), var2);
               MutableStateFlow var10000 = this._recordingState;
               Intrinsics.checkNotNull(var6);
               var10000.setValue(new AudioRecordingManager$RecordingState$Completed(var6, var2));
               return new AudioRecordingManager$RecordingStopResult$Success(var6, var2, var3);
            }

            var1.release();
            this.mediaRecorder = null;
            if (!this.waitForFileAvailability(var2)) {
               this.cleanup();
               this._recordingState.setValue(new AudioRecordingManager$RecordingState$Error("Recording file is empty or missing"));
               return new AudioRecordingManager$RecordingStopResult$Error("Recording file is empty or missing");
            } else {
               Uri var5 = FileProvider.getUriForFile(this.context, IntercomFileProviderKt.fileProviderAuthority(this.context), var2);
               MutableStateFlow var9 = this._recordingState;
               Intrinsics.checkNotNull(var5);
               var9.setValue(new AudioRecordingManager$RecordingState$Completed(var5, var2));
               return new AudioRecordingManager$RecordingStopResult$Success(var5, var2, var3);
            }
         } else {
            this._recordingState.setValue(new AudioRecordingManager$RecordingState$Error("No active recording"));
            return new AudioRecordingManager$RecordingStopResult$Error("No active recording");
         }
      } catch (Exception var8) {
         this.stopAmplitudeMonitoring();
         this.cleanup();
         this._recordingState.setValue(new AudioRecordingManager$RecordingState$Error("Failed to stop recording: " + var8.getMessage()));
         return new AudioRecordingManager$RecordingStopResult$Error("Failed to stop recording: " + var8.getMessage());
      }
   }

   public final void cancelRecording() {
      this.stopAmplitudeMonitoring();
      this.cleanup();
      this._recordingState.setValue(AudioRecordingManager$RecordingState$Idle.INSTANCE);
   }

   public final void cleanupCompletedRecording() {
      AudioRecordingManager$RecordingState var1 = (AudioRecordingManager$RecordingState)this._recordingState.getValue();
      if (var1 instanceof AudioRecordingManager$RecordingState$Completed) {
         IoUtils.safelyDelete(((AudioRecordingManager$RecordingState$Completed)var1).getFile());
      }

      this._recordingState.setValue(AudioRecordingManager$RecordingState$Idle.INSTANCE);
   }

   public final void dispose() {
      this.stopAmplitudeMonitoring();
      this.cleanup();
      this.cleanupCompletedRecording();
      this._recordingState.setValue(AudioRecordingManager$RecordingState$Idle.INSTANCE);
   }

   private final File createAudioFile() throws IOException {
      File var10000 = this.context.getExternalCacheDir();
      if (var10000 == null) {
         var10000 = this.context.getCacheDir();
      }

      File var1 = var10000;
      if (var1 == null) {
         throw new IOException("Unable to access cache directories for audio recording");
      }

      File var2 = new File(var1, "audio_recordings");
      var2.mkdirs();
      String var3 = UUID.randomUUID() + ".m4a";
      return new File(var2, var3);
   }

   private final MediaRecorder createMediaRecorder() {
      return VERSION.SDK_INT >= 31 ? new MediaRecorder(this.context) : new MediaRecorder();
   }

   private final void configureRecorder(MediaRecorder var1, File var2) {
      MediaRecorder var3 = var1;
      MediaRecorder var4 = var3;
      int var5 = 0;
      var4.setAudioSource(1);
      var4.setOutputFormat(2);
      var4.setAudioEncoder(3);
      var4.setAudioSamplingRate(44100);
      var4.setAudioEncodingBitRate(128000);
      var4.setOutputFile(var2.getAbsolutePath());
   }

   private final void cleanup() {
      if (this.mediaRecorder != null) {
         MediaRecorder var1 = this.mediaRecorder;
         MediaRecorder var2 = var1;
         int var3 = 0;

         try {
            var2.stop();
         } catch (Exception var5) {
         }

         var2.release();
      }

      this.mediaRecorder = null;
      if (this.currentRecordingFile != null) {
         File var6 = this.currentRecordingFile;
         int var7 = 0;
         IoUtils.safelyDelete(var6);
      }

      this.currentRecordingFile = null;
   }

   private final boolean waitForFileAvailability(File var1) throws InterruptedException {
      byte var2 = 10;

      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var3;
         int var5 = 0;
         if (var1.exists() && var1.length() > 0L) {
            return true;
         }

         if (var4 < 9) {
            long var6 = 10L * (1 << var4);
            Thread.sleep(var6);
         }
      }

      return false;
   }

   private final void startAmplitudeMonitoring(MediaRecorder var1) {
      this.amplitudeMonitoringJob = BuildersKt.launch$default(
         this.amplitudeScope, null, null, new AudioRecordingManager$startAmplitudeMonitoring$1(var1, this, null), 3, null
      );
   }

   private final void stopAmplitudeMonitoring() {
      if (this.amplitudeMonitoringJob != null) {
         DefaultImpls.cancel$default(this.amplitudeMonitoringJob, (CancellationException)null, 1, null);
      }

      this.amplitudeMonitoringJob = null;
      this._amplitudeLevel.setValue(0.0F);
   }

   private final float normalizeAmplitude(int var1) {
      if (var1 <= 0) {
         return 0.0F;
      }

      double var2 = var1 / 32767.0;
      double var4 = 20 * Math.log10(Math.max(var2, 0.001));
      double var6 = Math.max(0.0, (var4 + 60.0) / 60.0);
      return (float)var6;
   }

   // $VF: synthetic method
   public static final float access$normalizeAmplitude(AudioRecordingManager var0, int var1) {
      return var0.normalizeAmplitude(var1);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_amplitudeLevel$p(AudioRecordingManager var0) {
      return var0._amplitudeLevel;
   }
}
