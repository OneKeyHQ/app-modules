package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0005\u0006\u0007\b\t\nB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&\u0082\u0001\u0005\u000b\f\r\u000e\u000f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "",
         "<init>",
         "()V",
         "getLength",
         "",
         "DateTimeAnswer",
         "SingleAnswer",
         "MultipleAnswer",
         "NoAnswer",
         "MediaAnswer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$DateTimeAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$NoAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$SingleAnswer;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class Answer {
   public static int $stable;

   static {}

   private Answer() {
   }

   public abstract int getLength();

   // $VF: synthetic method
   public Answer(DefaultConstructorMarker var1) {
      this();
   }
}
