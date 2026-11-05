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
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\n\u001a\u00020\b¢\u0006\u0004\b\u000b\u0010\fR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\n\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$SingleChoiceQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "isRequired",
         "",
         "options",
         "includeOther",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/util/List;Z)V",
         "getOptions",
         "()Ljava/util/List;",
         "getIncludeOther",
         "()Z",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$SingleChoiceQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final List options;
   private final boolean includeOther;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$SingleChoiceQuestionModel(@NotNull String var1, @NotNull List var2, boolean var3, @NotNull List var4, boolean var5) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "options");
      super(var1, var2, null, var3, 4, null);
      this.options = var4;
      this.includeOther = var5;
   }

   @NotNull
   public final List getOptions() {
      return this.options;
   }

   public final boolean getIncludeOther() {
      return this.includeOther;
   }
}
