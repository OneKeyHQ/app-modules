package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0002\u0006\u0007B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016\u0082\u0001\u0002\b\t¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$NoAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "<init>",
         "()V",
         "getLength",
         "",
         "InitialNoAnswer",
         "ResetNoAnswer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$NoAnswer$InitialNoAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$NoAnswer$ResetNoAnswer;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class Answer$NoAnswer extends Answer {
   public static int $stable;

   static {}

   private Answer$NoAnswer() {
      super(null);
   }

   @Override
   public int getLength() {
      return 0;
   }

   // $VF: synthetic method
   public Answer$NoAnswer(DefaultConstructorMarker var1) {
      this();
   }
}
