package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DatePickerQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "isRequired",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Z)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$DatePickerQuestionModel extends SurveyData$Step$Question$QuestionModel {
   public static int $stable;

   static {}

   public SurveyData$Step$Question$DatePickerQuestionModel(@NotNull String var1, @NotNull List var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      super(var1, var2, null, var3, 4, null);
   }
}
