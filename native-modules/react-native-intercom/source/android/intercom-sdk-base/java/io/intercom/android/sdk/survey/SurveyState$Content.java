package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001:\u0002/0BQ\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0011\u0010\u0012J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\b0\u0005HÆ\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\n0\u0005HÆ\u0003J\t\u0010#\u001a\u00020\fHÆ\u0003J\t\u0010$\u001a\u00020\u000eHÆ\u0003J\t\u0010%\u001a\u00020\u0010HÆ\u0003Ja\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0010HÇ\u0001J\u0013\u0010'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010*H×\u0003J\t\u0010+\u001a\u00020,H×\u0001J\t\u0010-\u001a\u00020.H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\r\u001a\u00020\u000eX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u000f\u001a\u00020\u0010X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e¨\u00061",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Content;",
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "currentStep",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "stepTitle",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "questions",
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "secondaryCtaActions",
         "Lio/intercom/android/sdk/survey/SurveyState$Content$SecondaryCta;",
         "primaryCta",
         "Lio/intercom/android/sdk/survey/SurveyState$Content$PrimaryCta;",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "<init>",
         "(Lio/intercom/android/sdk/survey/model/SurveyData$Step;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lio/intercom/android/sdk/survey/SurveyState$Content$PrimaryCta;Lio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/TopBarState;)V",
         "getCurrentStep",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "getStepTitle",
         "()Ljava/util/List;",
         "getQuestions",
         "getSecondaryCtaActions",
         "getPrimaryCta",
         "()Lio/intercom/android/sdk/survey/SurveyState$Content$PrimaryCta;",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getTopBarState",
         "()Lio/intercom/android/sdk/survey/TopBarState;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "SecondaryCta",
         "PrimaryCta",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyState$Content extends SurveyState {
   @NotNull
   private final SurveyData$Step currentStep;
   @NotNull
   private final List stepTitle;
   @NotNull
   private final List questions;
   @NotNull
   private final List secondaryCtaActions;
   @NotNull
   private final SurveyState$Content$PrimaryCta primaryCta;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final TopBarState topBarState;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyState$Content(
      @NotNull SurveyData$Step var1,
      @NotNull List var2,
      @NotNull List var3,
      @NotNull List var4,
      @NotNull SurveyState$Content$PrimaryCta var5,
      @NotNull SurveyUiColors var6,
      @NotNull TopBarState var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "currentStep");
      Intrinsics.checkNotNullParameter(var2, "stepTitle");
      Intrinsics.checkNotNullParameter(var3, "questions");
      Intrinsics.checkNotNullParameter(var4, "secondaryCtaActions");
      Intrinsics.checkNotNullParameter(var5, "primaryCta");
      Intrinsics.checkNotNullParameter(var6, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var7, "topBarState");
      super(null);
      this.currentStep = var1;
      this.stepTitle = var2;
      this.questions = var3;
      this.secondaryCtaActions = var4;
      this.primaryCta = var5;
      this.surveyUiColors = var6;
      this.topBarState = var7;
   }

   @NotNull
   public final SurveyData$Step getCurrentStep() {
      return this.currentStep;
   }

   @NotNull
   public final List getStepTitle() {
      return this.stepTitle;
   }

   @NotNull
   public final List getQuestions() {
      return this.questions;
   }

   @NotNull
   public final List getSecondaryCtaActions() {
      return this.secondaryCtaActions;
   }

   @NotNull
   public final SurveyState$Content$PrimaryCta getPrimaryCta() {
      return this.primaryCta;
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
   public final SurveyData$Step component1() {
      return this.currentStep;
   }

   @NotNull
   public final List component2() {
      return this.stepTitle;
   }

   @NotNull
   public final List component3() {
      return this.questions;
   }

   @NotNull
   public final List component4() {
      return this.secondaryCtaActions;
   }

   @NotNull
   public final SurveyState$Content$PrimaryCta component5() {
      return this.primaryCta;
   }

   @NotNull
   public final SurveyUiColors component6() {
      return this.surveyUiColors;
   }

   @NotNull
   public final TopBarState component7() {
      return this.topBarState;
   }

   @NotNull
   public final SurveyState$Content copy(
      @NotNull SurveyData$Step var1,
      @NotNull List var2,
      @NotNull List var3,
      @NotNull List var4,
      @NotNull SurveyState$Content$PrimaryCta var5,
      @NotNull SurveyUiColors var6,
      @NotNull TopBarState var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "currentStep");
      Intrinsics.checkNotNullParameter(var2, "stepTitle");
      Intrinsics.checkNotNullParameter(var3, "questions");
      Intrinsics.checkNotNullParameter(var4, "secondaryCtaActions");
      Intrinsics.checkNotNullParameter(var5, "primaryCta");
      Intrinsics.checkNotNullParameter(var6, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var7, "topBarState");
      return new SurveyState$Content(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static SurveyState$Content copy$default(
      SurveyState$Content var0,
      SurveyData$Step var1,
      List var2,
      List var3,
      List var4,
      SurveyState$Content$PrimaryCta var5,
      SurveyUiColors var6,
      TopBarState var7,
      int var8,
      Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.currentStep;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.stepTitle;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.questions;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.secondaryCtaActions;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.primaryCta;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.surveyUiColors;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.topBarState;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(currentStep="
         + this.currentStep
         + ", stepTitle="
         + this.stepTitle
         + ", questions="
         + this.questions
         + ", secondaryCtaActions="
         + this.secondaryCtaActions
         + ", primaryCta="
         + this.primaryCta
         + ", surveyUiColors="
         + this.surveyUiColors
         + ", topBarState="
         + this.topBarState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.currentStep.hashCode();
      var1 = var1 * 31 + this.stepTitle.hashCode();
      var1 = var1 * 31 + this.questions.hashCode();
      var1 = var1 * 31 + this.secondaryCtaActions.hashCode();
      var1 = var1 * 31 + this.primaryCta.hashCode();
      var1 = var1 * 31 + this.surveyUiColors.hashCode();
      return var1 * 31 + this.topBarState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyState$Content)) {
         return false;
      } else {
         SurveyState$Content var2 = (SurveyState$Content)var1;
         if (!Intrinsics.areEqual(this.currentStep, var2.currentStep)) {
            return false;
         } else if (!Intrinsics.areEqual(this.stepTitle, var2.stepTitle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.questions, var2.questions)) {
            return false;
         } else if (!Intrinsics.areEqual(this.secondaryCtaActions, var2.secondaryCtaActions)) {
            return false;
         } else if (!Intrinsics.areEqual(this.primaryCta, var2.primaryCta)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.surveyUiColors, var2.surveyUiColors) ? false : Intrinsics.areEqual(this.topBarState, var2.topBarState);
         }
      }
   }
}
