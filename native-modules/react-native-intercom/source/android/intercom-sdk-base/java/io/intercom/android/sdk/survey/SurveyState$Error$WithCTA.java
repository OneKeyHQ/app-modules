package io.intercom.android.sdk.survey;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u000b\b\u0007\u0018\u00002\u00020\u0001B/\u0012\b\b\u0003\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0004\b\u000b\u0010\fR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Error$WithCTA;",
         "Lio/intercom/android/sdk/survey/SurveyState$Error;",
         "messageResId",
         "",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "onClick",
         "Lkotlin/Function0;",
         "",
         "<init>",
         "(ILio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/TopBarState;Lkotlin/jvm/functions/Function0;)V",
         "getMessageResId",
         "()I",
         "getSurveyUiColors",
         "()Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "getTopBarState",
         "()Lio/intercom/android/sdk/survey/TopBarState;",
         "getOnClick",
         "()Lkotlin/jvm/functions/Function0;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyState$Error$WithCTA extends SurveyState$Error {
   private final int messageResId;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final TopBarState topBarState;
   @NotNull
   private final Function0 onClick;
   public static int $stable;

   static {}

   public SurveyState$Error$WithCTA(@StringRes int var1, @NotNull SurveyUiColors var2, @NotNull TopBarState var3, @NotNull Function0 var4) {
      Intrinsics.checkNotNullParameter(var2, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var3, "topBarState");
      Intrinsics.checkNotNullParameter(var4, "onClick");
      super(null);
      this.messageResId = var1;
      this.surveyUiColors = var2;
      this.topBarState = var3;
      this.onClick = var4;
   }

   // $VF: synthetic method
   public SurveyState$Error$WithCTA(int var1, SurveyUiColors var2, TopBarState var3, Function0 var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (int) (string.intercom_inbox_error_state_title) : var1), var2, var3, var4);
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

   @NotNull
   public final Function0 getOnClick() {
      return this.onClick;
   }
}
