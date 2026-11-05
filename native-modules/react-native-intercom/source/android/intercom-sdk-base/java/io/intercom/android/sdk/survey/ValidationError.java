package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0003\u0004\u0005\u0006B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0003\u0007\b\t¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "",
         "<init>",
         "()V",
         "Unchecked",
         "ValidationStringError",
         "NoValidationError",
         "Lio/intercom/android/sdk/survey/ValidationError$NoValidationError;",
         "Lio/intercom/android/sdk/survey/ValidationError$Unchecked;",
         "Lio/intercom/android/sdk/survey/ValidationError$ValidationStringError;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class ValidationError {
   public static int $stable;

   static {}

   private ValidationError() {
   }

   // $VF: synthetic method
   public ValidationError(DefaultConstructorMarker var1) {
      this();
   }
}
