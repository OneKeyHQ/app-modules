package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0010\u001a\u00020\u0001H\u0016J\u0010\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u001cH×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/survey/TopBarState$NoTopBarState;",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "showDismissButton",
         "",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "progressBarState",
         "Lio/intercom/android/sdk/survey/ProgressBarState;",
         "<init>",
         "(ZLio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/ProgressBarState;)V",
         "getShowDismissButton",
         "()Z",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getProgressBarState",
         "()Lio/intercom/android/sdk/survey/ProgressBarState;",
         "enableDismissButton",
         "withProgressBarState",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
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
public final class TopBarState$NoTopBarState extends TopBarState {
   private final boolean showDismissButton;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final ProgressBarState progressBarState;
   public static int $stable;

   static {}

   public TopBarState$NoTopBarState(boolean var1, @NotNull SurveyUiColors var2, @NotNull ProgressBarState var3) {
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var3, "progressBarState");
      super(null);
      this.showDismissButton = var1;
      this.surveyUiColors = var2;
      this.progressBarState = var3;
   }

   // $VF: synthetic method
   public TopBarState$NoTopBarState(boolean var1, SurveyUiColors var2, ProgressBarState var3, int var4, DefaultConstructorMarker var5) {
      this(var1, var2, (((var4 & 4) != 0) ? (ProgressBarState) (new ProgressBarState(false, 0.0F, 3, null)) : var3));
   }

   @Override
   public boolean getShowDismissButton() {
      return this.showDismissButton;
   }

   @NotNull
   @Override
   public SurveyUiColors getSurveyUiColors() {
      return this.surveyUiColors;
   }

   @NotNull
   @Override
   public ProgressBarState getProgressBarState() {
      return this.progressBarState;
   }

   @NotNull
   @Override
   public TopBarState enableDismissButton() {
      return copy$default(this, true, null, null, 6, null);
   }

   @NotNull
   @Override
   public TopBarState withProgressBarState(@NotNull ProgressBarState var1) {
      Intrinsics.checkNotNullParameter(var1, "progressBarState");
      return copy$default(this, false, null, var1, 3, null);
   }

   public final boolean component1() {
      return this.showDismissButton;
   }

   @NotNull
   public final SurveyUiColors component2() {
      return this.surveyUiColors;
   }

   @NotNull
   public final ProgressBarState component3() {
      return this.progressBarState;
   }

   @NotNull
   public final TopBarState$NoTopBarState copy(boolean var1, @NotNull SurveyUiColors var2, @NotNull ProgressBarState var3) {
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var3, "progressBarState");
      return new TopBarState$NoTopBarState(var1, var2, var3);
   }

   // $VF: synthetic method
   public static TopBarState$NoTopBarState copy$default(
      TopBarState$NoTopBarState var0, boolean var1, SurveyUiColors var2, ProgressBarState var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.showDismissButton;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.surveyUiColors;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.progressBarState;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "NoTopBarState(showDismissButton="
         + this.showDismissButton
         + ", surveyUiColors="
         + this.surveyUiColors
         + ", progressBarState="
         + this.progressBarState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.showDismissButton);
      var1 = var1 * 31 + this.surveyUiColors.hashCode();
      return var1 * 31 + this.progressBarState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TopBarState$NoTopBarState)) {
         return false;
      } else {
         TopBarState$NoTopBarState var2 = (TopBarState$NoTopBarState)var1;
         if (this.showDismissButton != var2.showDismissButton) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.surveyUiColors, var2.surveyUiColors) ? false : Intrinsics.areEqual(this.progressBarState, var2.progressBarState);
         }
      }
   }
}
