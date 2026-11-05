package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0018\b\u0081\b\u0018\u0000 :2\u00020\u0001:\u00056789:BY\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0010¢\u0006\u0004\b\u0012\u0010\u0013J\t\u0010(\u001a\u00020\u0003HÆ\u0003J\t\u0010)\u001a\u00020\u0003HÆ\u0003J\u000f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\nHÆ\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\fHÆ\u0003J\t\u0010.\u001a\u00020\u000eHÆ\u0003J\t\u0010/\u001a\u00020\u0010HÆ\u0003J\t\u00100\u001a\u00020\u0010HÆ\u0003Jk\u00101\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0010HÇ\u0001J\u0013\u00102\u001a\u00020\u00102\b\u00103\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00104\u001a\u00020\u000eH×\u0001J\t\u00105\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0018\u0010\u000b\u001a\u0004\u0018\u00010\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0016\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0016\u0010\u000f\u001a\u00020\u00108\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010 R\u0016\u0010\u0011\u001a\u00020\u00108\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\"\u001a\u00020#8F¢\u0006\u0006\u001a\u0004\b$\u0010%R\u0011\u0010&\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b'\u0010\u0015¨\u0006;",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData;",
         "",
         "_format",
         "",
         "id",
         "steps",
         "",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "surveyProgressId",
         "customization",
         "Lio/intercom/android/sdk/survey/model/SurveyCustomization;",
         "sender",
         "Lio/intercom/android/sdk/survey/model/SurveySenderData;",
         "stepCount",
         "",
         "isDismissible",
         "",
         "showProgressBar",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Lio/intercom/android/sdk/survey/model/SurveyCustomization;Lio/intercom/android/sdk/survey/model/SurveySenderData;IZZ)V",
         "get_format",
         "()Ljava/lang/String;",
         "getId",
         "getSteps",
         "()Ljava/util/List;",
         "getSurveyProgressId",
         "getCustomization",
         "()Lio/intercom/android/sdk/survey/model/SurveyCustomization;",
         "getSender",
         "()Lio/intercom/android/sdk/survey/model/SurveySenderData;",
         "getStepCount",
         "()I",
         "()Z",
         "getShowProgressBar",
         "format",
         "Lio/intercom/android/sdk/survey/model/SurveyData$SurveyFormat;",
         "getFormat",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$SurveyFormat;",
         "formatMetric",
         "getFormatMetric",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "StepType",
         "SurveyFormat",
         "SurveyActions",
         "Step",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData {
   @NotNull
   public static final SurveyData$Companion Companion = new SurveyData$Companion(null);
   @SerializedName("format")
   @NotNull
   private final String _format;
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("steps")
   @NotNull
   private final List steps;
   @SerializedName("survey_progress_id")
   @NotNull
   private final String surveyProgressId;
   @SerializedName("customization_options")
   @NotNull
   private final SurveyCustomization customization;
   @SerializedName("sender")
   @Nullable
   private final SurveySenderData sender;
   @SerializedName("step_count")
   private final int stepCount;
   @SerializedName("dismissible")
   private final boolean isDismissible;
   @SerializedName("show_progress_bar")
   private final boolean showProgressBar;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final SurveyData NULL = new SurveyData(
      "1",
      "",
      CollectionsKt.emptyList(),
      "0",
      new SurveyCustomization(null, null, 3, null),
      new SurveySenderData(null, null, null, null, 15, null),
      0,
      true,
      false,
      256,
      null
   );

   public SurveyData(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull List var3,
      @NotNull String var4,
      @NotNull SurveyCustomization var5,
      @Nullable SurveySenderData var6,
      int var7,
      boolean var8,
      boolean var9
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "_format");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "steps");
      Intrinsics.checkNotNullParameter(var4, "surveyProgressId");
      Intrinsics.checkNotNullParameter(var5, "customization");
      this._format = var1;
      this.id = var2;
      this.steps = var3;
      this.surveyProgressId = var4;
      this.customization = var5;
      this.sender = var6;
      this.stepCount = var7;
      this.isDismissible = var8;
      this.showProgressBar = var9;
   }

   // $VF: synthetic method
   public SurveyData(
      String var1,
      String var2,
      List var3,
      String var4,
      SurveyCustomization var5,
      SurveySenderData var6,
      int var7,
      boolean var8,
      boolean var9,
      int var10,
      DefaultConstructorMarker var11
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, (((var10 & 256) != 0) ? (boolean) (true) : var9));
   }

   @NotNull
   public final String get_format() {
      return this._format;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getSteps() {
      return this.steps;
   }

   @NotNull
   public final String getSurveyProgressId() {
      return this.surveyProgressId;
   }

   @NotNull
   public final SurveyCustomization getCustomization() {
      return this.customization;
   }

   @Nullable
   public final SurveySenderData getSender() {
      return this.sender;
   }

   public final int getStepCount() {
      return this.stepCount;
   }

   public final boolean isDismissible() {
      return this.isDismissible;
   }

   public final boolean getShowProgressBar() {
      return this.showProgressBar;
   }

   @NotNull
   public final SurveyData$SurveyFormat getFormat() {
      return Integer.parseInt(this._format) == 0 ? SurveyData$SurveyFormat.SMALL_FORMAT : SurveyData$SurveyFormat.LARGE_FORMAT;
   }

   @NotNull
   public final String getFormatMetric() {
      String var10000;
      switch (this.getFormat()) {
         case SMALL_FORMAT:
            var10000 = "small_full_screen";
            break;
         case LARGE_FORMAT:
            var10000 = "large_full_screen";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @NotNull
   public final String component1() {
      return this._format;
   }

   @NotNull
   public final String component2() {
      return this.id;
   }

   @NotNull
   public final List component3() {
      return this.steps;
   }

   @NotNull
   public final String component4() {
      return this.surveyProgressId;
   }

   @NotNull
   public final SurveyCustomization component5() {
      return this.customization;
   }

   @Nullable
   public final SurveySenderData component6() {
      return this.sender;
   }

   public final int component7() {
      return this.stepCount;
   }

   public final boolean component8() {
      return this.isDismissible;
   }

   public final boolean component9() {
      return this.showProgressBar;
   }

   @NotNull
   public final SurveyData copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull List var3,
      @NotNull String var4,
      @NotNull SurveyCustomization var5,
      @Nullable SurveySenderData var6,
      int var7,
      boolean var8,
      boolean var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "_format");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "steps");
      Intrinsics.checkNotNullParameter(var4, "surveyProgressId");
      Intrinsics.checkNotNullParameter(var5, "customization");
      return new SurveyData(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   // $VF: synthetic method
   public static SurveyData copy$default(
      SurveyData var0,
      String var1,
      String var2,
      List var3,
      String var4,
      SurveyCustomization var5,
      SurveySenderData var6,
      int var7,
      boolean var8,
      boolean var9,
      int var10,
      Object var11
   ) {
      if ((var10 & 1) != 0) {
         var1 = var0._format;
      }

      if ((var10 & 2) != 0) {
         var2 = var0.id;
      }

      if ((var10 & 4) != 0) {
         var3 = var0.steps;
      }

      if ((var10 & 8) != 0) {
         var4 = var0.surveyProgressId;
      }

      if ((var10 & 16) != 0) {
         var5 = var0.customization;
      }

      if ((var10 & 32) != 0) {
         var6 = var0.sender;
      }

      if ((var10 & 64) != 0) {
         var7 = var0.stepCount;
      }

      if ((var10 & 128) != 0) {
         var8 = var0.isDismissible;
      }

      if ((var10 & 256) != 0) {
         var9 = var0.showProgressBar;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @NotNull
   @Override
   public String toString() {
      return "SurveyData(_format="
         + this._format
         + ", id="
         + this.id
         + ", steps="
         + this.steps
         + ", surveyProgressId="
         + this.surveyProgressId
         + ", customization="
         + this.customization
         + ", sender="
         + this.sender
         + ", stepCount="
         + this.stepCount
         + ", isDismissible="
         + this.isDismissible
         + ", showProgressBar="
         + this.showProgressBar
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._format.hashCode();
      var1 = var1 * 31 + this.id.hashCode();
      var1 = var1 * 31 + this.steps.hashCode();
      var1 = var1 * 31 + this.surveyProgressId.hashCode();
      var1 = var1 * 31 + this.customization.hashCode();
      var1 = var1 * 31 + (this.sender == null ? 0 : this.sender.hashCode());
      var1 = var1 * 31 + Integer.hashCode(this.stepCount);
      var1 = var1 * 31 + Boolean.hashCode(this.isDismissible);
      return var1 * 31 + Boolean.hashCode(this.showProgressBar);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyData)) {
         return false;
      } else {
         SurveyData var2 = (SurveyData)var1;
         if (!Intrinsics.areEqual(this._format, var2._format)) {
            return false;
         } else if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.steps, var2.steps)) {
            return false;
         } else if (!Intrinsics.areEqual(this.surveyProgressId, var2.surveyProgressId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.customization, var2.customization)) {
            return false;
         } else if (!Intrinsics.areEqual(this.sender, var2.sender)) {
            return false;
         } else if (this.stepCount != var2.stepCount) {
            return false;
         } else {
            return this.isDismissible != var2.isDismissible ? false : this.showProgressBar == var2.showProgressBar;
         }
      }
   }

   // $VF: synthetic method
   public static final SurveyData access$getNULL$cp() {
      return NULL;
   }
}
