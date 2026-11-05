package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b-\b\u0087\b\u0018\u00002\u00020\u0001:\u0001>Bw\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u0005\u0012\u0006\u0010\u000e\u001a\u00020\u000b\u0012\u0006\u0010\u000f\u001a\u00020\u000b\u0012\u0006\u0010\u0010\u001a\u00020\u000b\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\b\u0010\u0013\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\u0014\u0010\u0015J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\t\u0010,\u001a\u00020\u0005HÆ\u0003J\t\u0010-\u001a\u00020\u0007HÆ\u0003J\t\u0010.\u001a\u00020\u0003HÆ\u0003J\t\u0010/\u001a\u00020\u0003HÆ\u0003J\t\u00100\u001a\u00020\u000bHÆ\u0003J\t\u00101\u001a\u00020\u000bHÆ\u0003J\t\u00102\u001a\u00020\u0005HÆ\u0003J\t\u00103\u001a\u00020\u000bHÆ\u0003J\t\u00104\u001a\u00020\u000bHÆ\u0003J\t\u00105\u001a\u00020\u000bHÆ\u0003J\u000b\u00106\u001a\u0004\u0018\u00010\u0012HÆ\u0003J\u0010\u00107\u001a\u0004\u0018\u00010\u000bHÆ\u0003¢\u0006\u0002\u0010'J\u0096\u0001\u00108\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000b2\b\b\u0002\u0010\u000f\u001a\u00020\u000b2\b\b\u0002\u0010\u0010\u001a\u00020\u000b2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u000bHÇ\u0001¢\u0006\u0002\u00109J\u0013\u0010:\u001a\u00020\u00052\b\u0010;\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010<\u001a\u00020\u000bH×\u0001J\t\u0010=\u001a\u00020\u0003H×\u0001R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0016\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\f\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0016\u0010\r\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0017R\u0016\u0010\u000e\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001eR\u0016\u0010\u000f\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001eR\u0016\u0010\u0010\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001eR\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00128\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u001a\u0010\u0013\u001a\u0004\u0018\u00010\u000b8\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010(\u001a\u0004\b&\u0010'R\u0011\u0010)\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b*\u0010\u001b¨\u0006?",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData;",
         "",
         "_placeholder",
         "",
         "required",
         "",
         "options",
         "Lcom/google/gson/JsonArray;",
         "lowerLabel",
         "upperLabel",
         "scaleStart",
         "",
         "scaleEnd",
         "includeOther",
         "maxSelection",
         "minSelection",
         "questionSubtype",
         "validation",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation;",
         "characterLimit",
         "<init>",
         "(Ljava/lang/String;ZLcom/google/gson/JsonArray;Ljava/lang/String;Ljava/lang/String;IIZIIILio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation;Ljava/lang/Integer;)V",
         "getRequired",
         "()Z",
         "getOptions",
         "()Lcom/google/gson/JsonArray;",
         "getLowerLabel",
         "()Ljava/lang/String;",
         "getUpperLabel",
         "getScaleStart",
         "()I",
         "getScaleEnd",
         "getIncludeOther",
         "getMaxSelection",
         "getMinSelection",
         "getQuestionSubtype",
         "getValidation",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation;",
         "getCharacterLimit",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "placeholder",
         "getPlaceholder",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "component12",
         "component13",
         "copy",
         "(Ljava/lang/String;ZLcom/google/gson/JsonArray;Ljava/lang/String;Ljava/lang/String;IIZIIILio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation;Ljava/lang/Integer;)Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "QuestionSubType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$QuestionData {
   @SerializedName("placeholder")
   @Nullable
   private final String _placeholder;
   @SerializedName("required")
   private final boolean required;
   @SerializedName("options")
   @NotNull
   private final JsonArray options;
   @SerializedName("lower_label")
   @NotNull
   private final String lowerLabel;
   @SerializedName("upper_label")
   @NotNull
   private final String upperLabel;
   @SerializedName("scale_start")
   private final int scaleStart;
   @SerializedName("scale_end")
   private final int scaleEnd;
   @SerializedName("include_other")
   private final boolean includeOther;
   @SerializedName("maximum_selection")
   private final int maxSelection;
   @SerializedName("minimum_selection")
   private final int minSelection;
   @SerializedName("type")
   private final int questionSubtype;
   @SerializedName("validation")
   @Nullable
   private final SurveyData$Step$Question$QuestionValidation validation;
   @SerializedName("character_limit")
   @Nullable
   private final Integer characterLimit;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$QuestionData(
      @Nullable String var1,
      boolean var2,
      @NotNull JsonArray var3,
      @NotNull String var4,
      @NotNull String var5,
      int var6,
      int var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      @Nullable SurveyData$Step$Question$QuestionValidation var12,
      @Nullable Integer var13
   ) {
      super();
      Intrinsics.checkNotNullParameter(var3, "options");
      Intrinsics.checkNotNullParameter(var4, "lowerLabel");
      Intrinsics.checkNotNullParameter(var5, "upperLabel");
      this._placeholder = var1;
      this.required = var2;
      this.options = var3;
      this.lowerLabel = var4;
      this.upperLabel = var5;
      this.scaleStart = var6;
      this.scaleEnd = var7;
      this.includeOther = var8;
      this.maxSelection = var9;
      this.minSelection = var10;
      this.questionSubtype = var11;
      this.validation = var12;
      this.characterLimit = var13;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$QuestionData(
      String var1,
      boolean var2,
      JsonArray var3,
      String var4,
      String var5,
      int var6,
      int var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      SurveyData$Step$Question$QuestionValidation var12,
      Integer var13,
      int var14,
      DefaultConstructorMarker var15
   ) {
      this(var1, var2, (((var14 & 4) != 0) ? (JsonArray) (new JsonArray()) : var3), var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public final boolean getRequired() {
      return this.required;
   }

   @NotNull
   public final JsonArray getOptions() {
      return this.options;
   }

   @NotNull
   public final String getLowerLabel() {
      return this.lowerLabel;
   }

   @NotNull
   public final String getUpperLabel() {
      return this.upperLabel;
   }

   public final int getScaleStart() {
      return this.scaleStart;
   }

   public final int getScaleEnd() {
      return this.scaleEnd;
   }

   public final boolean getIncludeOther() {
      return this.includeOther;
   }

   public final int getMaxSelection() {
      return this.maxSelection;
   }

   public final int getMinSelection() {
      return this.minSelection;
   }

   public final int getQuestionSubtype() {
      return this.questionSubtype;
   }

   @Nullable
   public final SurveyData$Step$Question$QuestionValidation getValidation() {
      return this.validation;
   }

   @Nullable
   public final Integer getCharacterLimit() {
      return this.characterLimit;
   }

   @NotNull
   public final String getPlaceholder() {
      String var10000 = this._placeholder;
      if (this._placeholder == null) {
         var10000 = "";
      }

      return var10000;
   }

   private final String component1() {
      return this._placeholder;
   }

   public final boolean component2() {
      return this.required;
   }

   @NotNull
   public final JsonArray component3() {
      return this.options;
   }

   @NotNull
   public final String component4() {
      return this.lowerLabel;
   }

   @NotNull
   public final String component5() {
      return this.upperLabel;
   }

   public final int component6() {
      return this.scaleStart;
   }

   public final int component7() {
      return this.scaleEnd;
   }

   public final boolean component8() {
      return this.includeOther;
   }

   public final int component9() {
      return this.maxSelection;
   }

   public final int component10() {
      return this.minSelection;
   }

   public final int component11() {
      return this.questionSubtype;
   }

   @Nullable
   public final SurveyData$Step$Question$QuestionValidation component12() {
      return this.validation;
   }

   @Nullable
   public final Integer component13() {
      return this.characterLimit;
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionData copy(
      @Nullable String var1,
      boolean var2,
      @NotNull JsonArray var3,
      @NotNull String var4,
      @NotNull String var5,
      int var6,
      int var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      @Nullable SurveyData$Step$Question$QuestionValidation var12,
      @Nullable Integer var13
   ) {
      Intrinsics.checkNotNullParameter(var3, "options");
      Intrinsics.checkNotNullParameter(var4, "lowerLabel");
      Intrinsics.checkNotNullParameter(var5, "upperLabel");
      return new SurveyData$Step$Question$QuestionData(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   // $VF: synthetic method
   public static SurveyData$Step$Question$QuestionData copy$default(
      SurveyData$Step$Question$QuestionData var0,
      String var1,
      boolean var2,
      JsonArray var3,
      String var4,
      String var5,
      int var6,
      int var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      SurveyData$Step$Question$QuestionValidation var12,
      Integer var13,
      int var14,
      Object var15
   ) {
      if ((var14 & 1) != 0) {
         var1 = var0._placeholder;
      }

      if ((var14 & 2) != 0) {
         var2 = var0.required;
      }

      if ((var14 & 4) != 0) {
         var3 = var0.options;
      }

      if ((var14 & 8) != 0) {
         var4 = var0.lowerLabel;
      }

      if ((var14 & 16) != 0) {
         var5 = var0.upperLabel;
      }

      if ((var14 & 32) != 0) {
         var6 = var0.scaleStart;
      }

      if ((var14 & 64) != 0) {
         var7 = var0.scaleEnd;
      }

      if ((var14 & 128) != 0) {
         var8 = var0.includeOther;
      }

      if ((var14 & 256) != 0) {
         var9 = var0.maxSelection;
      }

      if ((var14 & 512) != 0) {
         var10 = var0.minSelection;
      }

      if ((var14 & 1024) != 0) {
         var11 = var0.questionSubtype;
      }

      if ((var14 & 2048) != 0) {
         var12 = var0.validation;
      }

      if ((var14 & 4096) != 0) {
         var13 = var0.characterLimit;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("QuestionData(_placeholder=")
         .append(this._placeholder)
         .append(", required=")
         .append(this.required)
         .append(", options=")
         .append(this.options)
         .append(", lowerLabel=")
         .append(this.lowerLabel)
         .append(", upperLabel=")
         .append(this.upperLabel)
         .append(", scaleStart=")
         .append(this.scaleStart)
         .append(", scaleEnd=")
         .append(this.scaleEnd)
         .append(", includeOther=")
         .append(this.includeOther)
         .append(", maxSelection=")
         .append(this.maxSelection)
         .append(", minSelection=")
         .append(this.minSelection)
         .append(", questionSubtype=")
         .append(this.questionSubtype)
         .append(", validation=");
      var1.append(this.validation).append(", characterLimit=").append(this.characterLimit).append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this._placeholder == null ? 0 : this._placeholder.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.required);
      var1 = var1 * 31 + this.options.hashCode();
      var1 = var1 * 31 + this.lowerLabel.hashCode();
      var1 = var1 * 31 + this.upperLabel.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.scaleStart);
      var1 = var1 * 31 + Integer.hashCode(this.scaleEnd);
      var1 = var1 * 31 + Boolean.hashCode(this.includeOther);
      var1 = var1 * 31 + Integer.hashCode(this.maxSelection);
      var1 = var1 * 31 + Integer.hashCode(this.minSelection);
      var1 = var1 * 31 + Integer.hashCode(this.questionSubtype);
      var1 = var1 * 31 + (this.validation == null ? 0 : this.validation.hashCode());
      return var1 * 31 + (this.characterLimit == null ? 0 : this.characterLimit.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyData$Step$Question$QuestionData)) {
         return false;
      } else {
         SurveyData$Step$Question$QuestionData var2 = (SurveyData$Step$Question$QuestionData)var1;
         if (!Intrinsics.areEqual(this._placeholder, var2._placeholder)) {
            return false;
         } else if (this.required != var2.required) {
            return false;
         } else if (!Intrinsics.areEqual(this.options, var2.options)) {
            return false;
         } else if (!Intrinsics.areEqual(this.lowerLabel, var2.lowerLabel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.upperLabel, var2.upperLabel)) {
            return false;
         } else if (this.scaleStart != var2.scaleStart) {
            return false;
         } else if (this.scaleEnd != var2.scaleEnd) {
            return false;
         } else if (this.includeOther != var2.includeOther) {
            return false;
         } else if (this.maxSelection != var2.maxSelection) {
            return false;
         } else if (this.minSelection != var2.minSelection) {
            return false;
         } else if (this.questionSubtype != var2.questionSubtype) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.validation, var2.validation) ? false : Intrinsics.areEqual(this.characterLimit, var2.characterLimit);
         }
      }
   }
}
