package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0004\f\r\u000e\u000fB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0012\u0010\b\u001a\u00020\tX¦\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b\u0082\u0001\u0004\u0010\u0011\u0012\u0013¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "",
         "<init>",
         "()V",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "getTopBarState",
         "()Lio/intercom/android/sdk/survey/TopBarState;",
         "Initial",
         "Loading",
         "Error",
         "Content",
         "Lio/intercom/android/sdk/survey/SurveyState$Content;",
         "Lio/intercom/android/sdk/survey/SurveyState$Error;",
         "Lio/intercom/android/sdk/survey/SurveyState$Initial;",
         "Lio/intercom/android/sdk/survey/SurveyState$Loading;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class SurveyState {
   public static int $stable;

   static {}

   private SurveyState() {
   }

   @NotNull
   public abstract SurveyUiColors getSurveyUiColors();

   @NotNull
   public abstract TopBarState getTopBarState();

   // $VF: synthetic method
   public SurveyState(DefaultConstructorMarker var1) {
      this();
   }
}
