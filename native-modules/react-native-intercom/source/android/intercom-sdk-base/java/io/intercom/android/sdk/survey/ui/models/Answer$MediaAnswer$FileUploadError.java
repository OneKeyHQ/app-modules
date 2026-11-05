package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0004\t\n\u000b\fB\u0017\b\u0004\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u0082\u0001\u0004\r\u000e\u000f\u0010¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError;",
         "",
         "errorMessages",
         "",
         "Lio/intercom/android/sdk/ui/common/StringProvider$StringRes;",
         "<init>",
         "(Ljava/util/List;)V",
         "getErrorMessages",
         "()Ljava/util/List;",
         "UploadFailed",
         "FileLimitExceeded",
         "UnsupportedFileType",
         "FileTooLarge",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError$FileLimitExceeded;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError$FileTooLarge;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError$UnsupportedFileType;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError$UploadFailed;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public abstract class Answer$MediaAnswer$FileUploadError {
   @NotNull
   private final List errorMessages;
   public static final int $stable;

   static { $stable = 8; }

   private Answer$MediaAnswer$FileUploadError(List var1) {
      this.errorMessages = var1;
   }

   @NotNull
   public final List getErrorMessages() {
      return this.errorMessages;
   }

   // $VF: synthetic method
   public Answer$MediaAnswer$FileUploadError(List var1, DefaultConstructorMarker var2) {
      this(var1);
   }
}
