package io.intercom.android.sdk.survey.model;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001BI\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\r\u0010\u000eR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$DropDownQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "isRequired",
         "",
         "options",
         "placeholder",
         "placeHolderStringRes",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/util/List;Ljava/lang/String;Ljava/lang/Integer;)V",
         "getOptions",
         "()Ljava/util/List;",
         "getPlaceholder",
         "()Ljava/lang/String;",
         "getPlaceHolderStringRes",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$DropDownQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final List options;
   @NotNull
   private final String placeholder;
   @Nullable
   private final Integer placeHolderStringRes;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$DropDownQuestionModel(
      @NotNull String var1, @NotNull List var2, boolean var3, @NotNull List var4, @NotNull String var5, @StringRes @Nullable Integer var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "options");
      Intrinsics.checkNotNullParameter(var5, "placeholder");
      super(var1, var2, null, var3, 4, null);
      this.options = var4;
      this.placeholder = var5;
      this.placeHolderStringRes = var6;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$DropDownQuestionModel(
      String var1, List var2, boolean var3, List var4, String var5, Integer var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, var3, var4, (((var7 & 16) != 0) ? (String) ("") : var5), (((var7 & 32) != 0) ? (Integer) (null) : var6));
   }

   @NotNull
   public final List getOptions() {
      return this.options;
   }

   @NotNull
   public final String getPlaceholder() {
      return this.placeholder;
   }

   @Nullable
   public final Integer getPlaceHolderStringRes() {
      return this.placeHolderStringRes;
   }
}
