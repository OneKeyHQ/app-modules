package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0002\u0012\u0013B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0010\u001a\u00020\u0000H&J\u0010\u0010\u0011\u001a\u00020\u00002\u0006\u0010\b\u001a\u00020\tH&R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0012\u0010\b\u001a\u00020\tX¦\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0012\u0010\f\u001a\u00020\rX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f\u0082\u0001\u0002\u0014\u0015¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "",
         "<init>",
         "()V",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "progressBarState",
         "Lio/intercom/android/sdk/survey/ProgressBarState;",
         "getProgressBarState",
         "()Lio/intercom/android/sdk/survey/ProgressBarState;",
         "showDismissButton",
         "",
         "getShowDismissButton",
         "()Z",
         "enableDismissButton",
         "withProgressBarState",
         "SenderTopBarState",
         "NoTopBarState",
         "Lio/intercom/android/sdk/survey/TopBarState$NoTopBarState;",
         "Lio/intercom/android/sdk/survey/TopBarState$SenderTopBarState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class TopBarState {
   public static int $stable;

   static {}

   private TopBarState() {
   }

   @NotNull
   public abstract SurveyUiColors getSurveyUiColors();

   @NotNull
   public abstract ProgressBarState getProgressBarState();

   public abstract boolean getShowDismissButton();

   @NotNull
   public abstract TopBarState enableDismissButton();

   @NotNull
   public abstract TopBarState withProgressBarState(@NotNull ProgressBarState var1);

   // $VF: synthetic method
   public TopBarState(DefaultConstructorMarker var1) {
      this();
   }
}
