package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Initial;",
         "Lio/intercom/android/sdk/survey/SurveyState;",
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
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyState$Initial extends SurveyState {
   @NotNull
   public static final SurveyState$Initial INSTANCE = new SurveyState$Initial();
   @NotNull
   private static final SurveyUiColors surveyUiColors = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
   @NotNull
   private static final TopBarState topBarState = new TopBarState$NoTopBarState(true, INSTANCE.getSurveyUiColors(), null, 4, null);
   public static int $stable;

   static {}

   private SurveyState$Initial() {
      super(null);
   }

   @NotNull
   @Override
   public SurveyUiColors getSurveyUiColors() {
      return surveyUiColors;
   }

   @NotNull
   @Override
   public TopBarState getTopBarState() {
      return topBarState;
   }
}
