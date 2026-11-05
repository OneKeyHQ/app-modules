package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fJ\b\u0010\u001c\u001a\u00020\u0001H\u0016J\u0010\u0010\u001d\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\rH\u0016J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0007HÆ\u0003J\t\u0010!\u001a\u00020\tHÆ\u0003J\t\u0010\"\u001a\u00020\u000bHÆ\u0003J\t\u0010#\u001a\u00020\rHÆ\u0003JE\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rHÇ\u0001J\u0013\u0010%\u001a\u00020\t2\b\u0010&\u001a\u0004\u0018\u00010'H×\u0003J\t\u0010(\u001a\u00020)H×\u0001J\t\u0010*\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\n\u001a\u00020\u000bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0014\u0010\f\u001a\u00020\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/survey/TopBarState$SenderTopBarState;",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "senderName",
         "",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "showDismissButton",
         "",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "progressBarState",
         "Lio/intercom/android/sdk/survey/ProgressBarState;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Avatar;Ljava/lang/String;Lio/intercom/android/sdk/identity/AppConfig;ZLio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/ProgressBarState;)V",
         "getAvatar",
         "()Lio/intercom/android/sdk/models/Avatar;",
         "getSenderName",
         "()Ljava/lang/String;",
         "getAppConfig",
         "()Lio/intercom/android/sdk/identity/AppConfig;",
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
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TopBarState$SenderTopBarState extends TopBarState {
   @NotNull
   private final Avatar avatar;
   @NotNull
   private final String senderName;
   @NotNull
   private final AppConfig appConfig;
   private final boolean showDismissButton;
   @NotNull
   private final SurveyUiColors surveyUiColors;
   @NotNull
   private final ProgressBarState progressBarState;
   public static final int $stable;

   static { $stable = 8; }

   public TopBarState$SenderTopBarState(
      @NotNull Avatar var1, @NotNull String var2, @NotNull AppConfig var3, boolean var4, @NotNull SurveyUiColors var5, @NotNull ProgressBarState var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "avatar");
      Intrinsics.checkNotNullParameter(var2, "senderName");
      Intrinsics.checkNotNullParameter(var3, "appConfig");
      Intrinsics.checkNotNullParameter(var5, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var6, "progressBarState");
      super(null);
      this.avatar = var1;
      this.senderName = var2;
      this.appConfig = var3;
      this.showDismissButton = var4;
      this.surveyUiColors = var5;
      this.progressBarState = var6;
   }

   // $VF: synthetic method
   public TopBarState$SenderTopBarState(
      Avatar var1, String var2, AppConfig var3, boolean var4, SurveyUiColors var5, ProgressBarState var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, var3, var4, var5, (((var7 & 32) != 0) ? (ProgressBarState) (new ProgressBarState(false, 0.0F, 3, null)) : var6));
   }

   @NotNull
   public final Avatar getAvatar() {
      return this.avatar;
   }

   @NotNull
   public final String getSenderName() {
      return this.senderName;
   }

   @NotNull
   public final AppConfig getAppConfig() {
      return this.appConfig;
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
      return copy$default(this, null, null, null, true, null, null, 55, null);
   }

   @NotNull
   @Override
   public TopBarState withProgressBarState(@NotNull ProgressBarState var1) {
      Intrinsics.checkNotNullParameter(var1, "progressBarState");
      return copy$default(this, null, null, null, false, null, var1, 31, null);
   }

   @NotNull
   public final Avatar component1() {
      return this.avatar;
   }

   @NotNull
   public final String component2() {
      return this.senderName;
   }

   @NotNull
   public final AppConfig component3() {
      return this.appConfig;
   }

   public final boolean component4() {
      return this.showDismissButton;
   }

   @NotNull
   public final SurveyUiColors component5() {
      return this.surveyUiColors;
   }

   @NotNull
   public final ProgressBarState component6() {
      return this.progressBarState;
   }

   @NotNull
   public final TopBarState$SenderTopBarState copy(
      @NotNull Avatar var1, @NotNull String var2, @NotNull AppConfig var3, boolean var4, @NotNull SurveyUiColors var5, @NotNull ProgressBarState var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "avatar");
      Intrinsics.checkNotNullParameter(var2, "senderName");
      Intrinsics.checkNotNullParameter(var3, "appConfig");
      Intrinsics.checkNotNullParameter(var5, "surveyUiColors");
      Intrinsics.checkNotNullParameter(var6, "progressBarState");
      return new TopBarState$SenderTopBarState(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static TopBarState$SenderTopBarState copy$default(
      TopBarState$SenderTopBarState var0,
      Avatar var1,
      String var2,
      AppConfig var3,
      boolean var4,
      SurveyUiColors var5,
      ProgressBarState var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.avatar;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.senderName;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.appConfig;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.showDismissButton;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.surveyUiColors;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.progressBarState;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "SenderTopBarState(avatar="
         + this.avatar
         + ", senderName="
         + this.senderName
         + ", appConfig="
         + this.appConfig
         + ", showDismissButton="
         + this.showDismissButton
         + ", surveyUiColors="
         + this.surveyUiColors
         + ", progressBarState="
         + this.progressBarState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatar.hashCode();
      var1 = var1 * 31 + this.senderName.hashCode();
      var1 = var1 * 31 + this.appConfig.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.showDismissButton);
      var1 = var1 * 31 + this.surveyUiColors.hashCode();
      return var1 * 31 + this.progressBarState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TopBarState$SenderTopBarState)) {
         return false;
      } else {
         TopBarState$SenderTopBarState var2 = (TopBarState$SenderTopBarState)var1;
         if (!Intrinsics.areEqual(this.avatar, var2.avatar)) {
            return false;
         } else if (!Intrinsics.areEqual(this.senderName, var2.senderName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.appConfig, var2.appConfig)) {
            return false;
         } else if (this.showDismissButton != var2.showDismissButton) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.surveyUiColors, var2.surveyUiColors) ? false : Intrinsics.areEqual(this.progressBarState, var2.progressBarState);
         }
      }
   }
}
