package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Loading;",
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "<init>",
         "(Lio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/TopBarState;)V",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getTopBarState",
         "()Lio/intercom/android/sdk/survey/TopBarState;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyState$Loading extends SurveyState {
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final TopBarState topBarState;
   public static int $stable;

   static {}

   public SurveyState$Loading(@NotNull SurveyUiColors var1, @NotNull TopBarState var2) {
      Intrinsics.checkNotNullParameter(var1, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var2, "topBarState");
      super(null);
      this.surveyUiColors = var1;
      this.topBarState = var2;
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

   @NotNull
   public final SurveyUiColors component1() {
      return this.surveyUiColors;
   }

   @NotNull
   public final TopBarState component2() {
      return this.topBarState;
   }

   @NotNull
   public final SurveyState$Loading copy(@NotNull SurveyUiColors var1, @NotNull TopBarState var2) {
      Intrinsics.checkNotNullParameter(var1, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var2, "topBarState");
      return new SurveyState$Loading(var1, var2);
   }

   // $VF: synthetic method
   public static SurveyState$Loading copy$default(SurveyState$Loading var0, SurveyUiColors var1, TopBarState var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.surveyUiColors;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.topBarState;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Loading(surveyUiColors=" + this.surveyUiColors + ", topBarState=" + this.topBarState + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.surveyUiColors.hashCode();
      return var1 * 31 + this.topBarState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SurveyState$Loading)) {
         return false;
      }

      SurveyState$Loading var2 = (SurveyState$Loading)var1;
      return !Intrinsics.areEqual(this.surveyUiColors, var2.surveyUiColors) ? false : Intrinsics.areEqual(this.topBarState, var2.topBarState);
   }
}
