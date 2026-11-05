package io.intercom.android.sdk.survey;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B!\u0012\b\b\u0003\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Error$WithoutCTA;",
         "Lio/intercom/android/sdk/survey/SurveyState$Error;",
         "messageResId",
         "",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "<init>",
         "(ILio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/TopBarState;)V",
         "getMessageResId",
         "()I",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getTopBarState",
         "()Lio/intercom/android/sdk/survey/TopBarState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyState$Error$WithoutCTA extends SurveyState$Error {
   private final int messageResId;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final TopBarState topBarState;
   public static int $stable;

   static {}

   public SurveyState$Error$WithoutCTA(@StringRes int var1, @NotNull SurveyUiColors var2, @NotNull TopBarState var3) {
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var3, "topBarState");
      super(null);
      this.messageResId = var1;
      this.surveyUiColors = var2;
      this.topBarState = var3;
   }

   // $VF: synthetic method
   public SurveyState$Error$WithoutCTA(int var1, SurveyUiColors var2, TopBarState var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (int) (string.intercom_inbox_error_state_title) : var1), var2, var3);
   }

   @Override
   public int getMessageResId() {
      return this.messageResId;
   }

   @NotNull
   @Override
   public SurveyUiColors getSurveyUiColors() {
      return this.surveyUiColors;
   }

   @NotNull
   @Override
   public TopBarState getTopBarState() {
      return this.topBarState;
   }
}
