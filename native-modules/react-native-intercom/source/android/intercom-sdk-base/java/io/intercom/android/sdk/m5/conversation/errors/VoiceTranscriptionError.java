package io.intercom.android.sdk.m5.conversation.errors;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0003\u0010\u0011\u0012B%\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u0082\u0001\u0003\u0013\u0014\u0015¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError;",
         "",
         "userMessage",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "debugMessage",
         "",
         "cause",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/StringProvider;Ljava/lang/String;Ljava/lang/Throwable;)V",
         "getUserMessage",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getDebugMessage",
         "()Ljava/lang/String;",
         "getCause",
         "()Ljava/lang/Throwable;",
         "TranscriptionFailed",
         "FileTooLarge",
         "RecordingFailed",
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError$FileTooLarge;",
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError$RecordingFailed;",
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError$TranscriptionFailed;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public abstract class VoiceTranscriptionError {
   @NotNull
   private final StringProvider userMessage;
   @NotNull
   private final String debugMessage;
   @Nullable
   private final Throwable cause;
   public static final int $stable;

   static { $stable = 8; }

   private VoiceTranscriptionError(StringProvider var1, String var2, Throwable var3) {
      this.userMessage = var1;
      this.debugMessage = var2;
      this.cause = var3;
   }

   // $VF: synthetic method
   public VoiceTranscriptionError(StringProvider var1, String var2, Throwable var3, int var4, DefaultConstructorMarker var5) {
      if ((var4 & 4) != 0) {
         var3 = null;
      }

      this(var1, var2, var3, null);
   }

   @NotNull
   public final StringProvider getUserMessage() {
      return this.userMessage;
   }

   @NotNull
   public final String getDebugMessage() {
      return this.debugMessage;
   }

   @Nullable
   public final Throwable getCause() {
      return this.cause;
   }

   // $VF: synthetic method
   public VoiceTranscriptionError(StringProvider var1, String var2, Throwable var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }
}
