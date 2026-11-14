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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0011\b\u0007\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\b\u0012\n\b\u0003\u0010\u000f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0003\u0010\u0010\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0015\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u001c\u0010\u0018R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u001d\u0010\u0018¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$ShortTextQuestionModel;",
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
         "enabled",
         "placeHolderStringRes",
         "titleStringRes",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/lang/String;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;Ljava/lang/Integer;ZLjava/lang/Integer;Ljava/lang/Integer;)V",
         "getPlaceholder",
         "()Ljava/lang/String;",
         "getValidationType",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "getCharacterLimit",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getEnabled",
         "()Z",
         "getPlaceHolderStringRes",
         "getTitleStringRes",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$ShortTextQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final String placeholder;
   @NotNull
   private final SurveyData$Step$Question$QuestionValidation$ValidationType validationType;
   @Nullable
   private final Integer characterLimit;
   private final boolean enabled;
   @Nullable
   private final Integer placeHolderStringRes;
   @Nullable
   private final Integer titleStringRes;
   public static int $stable;

   static {}

   public SurveyData$Step$Question$ShortTextQuestionModel(
      @NotNull String var1,
      @NotNull List var2,
      boolean var3,
      @NotNull String var4,
      @NotNull SurveyData$Step$Question$QuestionValidation$ValidationType var5,
      @Nullable Integer var6,
      boolean var7,
      @StringRes @Nullable Integer var8,
      @StringRes @Nullable Integer var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "placeholder");
      Intrinsics.checkNotNullParameter(var5, "validationType");
      super(var1, var2, null, var3, 4, null);
      this.placeholder = var4;
      this.validationType = var5;
      this.characterLimit = var6;
      this.enabled = var7;
      this.placeHolderStringRes = var8;
      this.titleStringRes = var9;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$ShortTextQuestionModel(
      String var1,
      List var2,
      boolean var3,
      String var4,
      SurveyData$Step$Question$QuestionValidation$ValidationType var5,
      Integer var6,
      boolean var7,
      Integer var8,
      Integer var9,
      int var10,
      DefaultConstructorMarker var11
   ) {
      this(var1, var2, var3, (((var10 & 8) != 0) ? (String) ("") : var4), var5, var6, (((var10 & 64) != 0) ? (boolean) (true) : var7), (((var10 & 128) != 0) ? (Integer) (null) : var8), (((var10 & 256) != 0) ? (Integer) (null) : var9));
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

   public final boolean getEnabled() {
      return this.enabled;
   }

   @Nullable
   public final Integer getPlaceHolderStringRes() {
      return this.placeHolderStringRes;
   }

   @Nullable
   public final Integer getTitleStringRes() {
      return this.titleStringRes;
   }
}
