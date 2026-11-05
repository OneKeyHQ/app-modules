package io.intercom.android.sdk.m5.conversation.errors;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError$FileTooLarge;",
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError;",
         "cause",
         "",
         "<init>",
         "(Ljava/lang/Throwable;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class VoiceTranscriptionError$FileTooLarge extends VoiceTranscriptionError {
   public static int $stable;

   static {}

   public VoiceTranscriptionError$FileTooLarge(@Nullable Throwable var1) {
      super(new StringRes(string.intercom_file_too_large_to_transcribe, null, 2, null), "Audio file exceeds maximum size limit", var1, null);
   }

   // $VF: synthetic method
   public VoiceTranscriptionError$FileTooLarge(Throwable var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Throwable) (null) : var1));
   }

   public VoiceTranscriptionError$FileTooLarge() {
      this(null, 1, null);
   }
}
