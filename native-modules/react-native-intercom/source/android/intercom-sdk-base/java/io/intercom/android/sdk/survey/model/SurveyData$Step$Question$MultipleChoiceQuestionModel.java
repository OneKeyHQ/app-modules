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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000b\b\u0007\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f¢\u0006\u0004\b\u000e\u0010\u000fR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\n\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\r\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$MultipleChoiceQuestionModel;",
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
         "minSelection",
         "",
         "maxSelection",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/util/List;ZII)V",
         "getOptions",
         "()Ljava/util/List;",
         "getIncludeOther",
         "()Z",
         "getMinSelection",
         "()I",
         "getMaxSelection",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$MultipleChoiceQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final List options;
   private final boolean includeOther;
   private final int minSelection;
   private final int maxSelection;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$MultipleChoiceQuestionModel(
      @NotNull String var1, @NotNull List var2, boolean var3, @NotNull List var4, boolean var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "options");
      super(var1, var2, null, var3, 4, null);
      this.options = var4;
      this.includeOther = var5;
      this.minSelection = var6;
      this.maxSelection = var7;
   }

   @NotNull
   public final List getOptions() {
      return this.options;
   }

   public final boolean getIncludeOther() {
      return this.includeOther;
   }

   public final int getMinSelection() {
      return this.minSelection;
   }

   public final int getMaxSelection() {
      return this.maxSelection;
   }
}
