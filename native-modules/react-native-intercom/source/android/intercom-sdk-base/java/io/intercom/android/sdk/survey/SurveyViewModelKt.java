package io.intercom.android.sdk.survey;

import androidx.compose.ui.graphics.ColorKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.ColorUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000¨\u0006\u0003",
   d2 = {
         "toSurveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "Lio/intercom/android/sdk/survey/model/SurveyCustomization;",
         "intercom-sdk-base_release"
   }
)
public final class SurveyViewModelKt {
   @NotNull
   public static final SurveyUiColors toSurveyUiColors(@NotNull SurveyCustomization var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      long var1 = ColorKt.Color(ColorUtils.parseColor(var0.getBackgroundColor()));
      long var3 = ColorKt.Color(ColorUtils.parseColor(var0.getButtonColor()));
      return new SurveyUiColors(
         var1, ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var1), var3, ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var3), null, 16, null
      );
   }
}
