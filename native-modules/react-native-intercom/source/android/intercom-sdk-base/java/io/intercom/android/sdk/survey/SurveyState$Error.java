package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0002\b\tB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0002\n\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Error;",
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "<init>",
         "()V",
         "messageResId",
         "",
         "getMessageResId",
         "()I",
         "WithCTA",
         "WithoutCTA",
         "Lio/intercom/android/sdk/survey/SurveyState$Error$WithCTA;",
         "Lio/intercom/android/sdk/survey/SurveyState$Error$WithoutCTA;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class SurveyState$Error extends SurveyState {
   public static int $stable;

   static {}

   private SurveyState$Error() {
      super(null);
   }

   public abstract int getMessageResId();

   // $VF: synthetic method
   public SurveyState$Error(DefaultConstructorMarker var1) {
      this();
   }
}
