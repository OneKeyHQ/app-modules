package io.intercom.android.sdk.survey.model;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.unit.Dp;
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
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\b\u0007\u0018\u00002\u00020\u0001Ba\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\r\u0012\n\b\u0003\u0010\u0011\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0015\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u000e\u001a\u00020\u000f¢\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0015\u0010\u0011\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b \u0010\u0019¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$LongTextQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "isRequired",
         "",
         "placeholder",
         "validationType",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "characterLimit",
         "",
         "minHeight",
         "Landroidx/compose/ui/unit/Dp;",
         "maxLine",
         "placeHolderStringRes",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/lang/String;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;Ljava/lang/Integer;FILjava/lang/Integer;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getPlaceholder",
         "()Ljava/lang/String;",
         "getValidationType",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "getCharacterLimit",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getMinHeight-D9Ej5fM",
         "()F",
         "F",
         "getMaxLine",
         "()I",
         "getPlaceHolderStringRes",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$LongTextQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final String placeholder;
   @NotNull
   private final SurveyData$Step$Question$QuestionValidation$ValidationType validationType;
   @Nullable
   private final Integer characterLimit;
   private final float minHeight;
   private final int maxLine;
   @Nullable
   private final Integer placeHolderStringRes;
   public static int $stable;

   static {}

   private SurveyData$Step$Question$LongTextQuestionModel(
      String var1,
      List var2,
      boolean var3,
      String var4,
      SurveyData$Step$Question$QuestionValidation$ValidationType var5,
      Integer var6,
      float var7,
      int var8,
      Integer var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "placeholder");
      Intrinsics.checkNotNullParameter(var5, "validationType");
      super(var1, var2, null, var3, 4, null);
      this.placeholder = var4;
      this.validationType = var5;
      this.characterLimit = var6;
      this.minHeight = var7;
      this.maxLine = var8;
      this.placeHolderStringRes = var9;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$LongTextQuestionModel(
      String var1,
      List var2,
      boolean var3,
      String var4,
      SurveyData$Step$Question$QuestionValidation$ValidationType var5,
      Integer var6,
      float var7,
      int var8,
      Integer var9,
      int var10,
      DefaultConstructorMarker var11
   ) {
      if ((var10 & 8) != 0) {
         var4 = "";
      }

      if ((var10 & 64) != 0) {
         var7 = Dp.Companion.recovered$70ffd418071648ea();
      }

      if ((var10 & 128) != 0) {
         var8 = 6;
      }

      if ((var10 & 256) != 0) {
         var9 = null;
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, null);
   }

   @NotNull
   public final String getPlaceholder() {
      return this.placeholder;
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionValidation$ValidationType getValidationType() {
      return this.validationType;
   }

   @Nullable
   public final Integer getCharacterLimit() {
      return this.characterLimit;
   }

   public final float recovered$36803c329f71b4de() {
      return this.minHeight;
   }

   public final int getMaxLine() {
      return this.maxLine;
   }

   @Nullable
   public final Integer getPlaceHolderStringRes() {
      return this.placeHolderStringRes;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$LongTextQuestionModel(
      String var1,
      List var2,
      boolean var3,
      String var4,
      SurveyData$Step$Question$QuestionValidation$ValidationType var5,
      Integer var6,
      float var7,
      int var8,
      @StringRes Integer var9,
      DefaultConstructorMarker var10
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
