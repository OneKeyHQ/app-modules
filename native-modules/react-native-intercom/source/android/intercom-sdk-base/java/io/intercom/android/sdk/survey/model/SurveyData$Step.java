package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0087\b\u0018\u00002\u00020\u0001:\u0001'BO\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0006HÆ\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\b0\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\nHÂ\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\f0\u0003HÆ\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\u0006HÆ\u0003JY\u0010!\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0006HÇ\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010%\u001a\u00020\nH×\u0001J\t\u0010&\u001a\u00020\u0006H×\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0010\u0010\t\u001a\u00020\n8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0018\u0010\r\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013R\u0011\u0010\u0017\u001a\u00020\u00188F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001a¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "id",
         "",
         "questions",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question;",
         "_type",
         "",
         "actions",
         "Lio/intercom/android/sdk/survey/model/SurveyData$SurveyActions;",
         "customButtonText",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;Ljava/util/List;ILjava/util/List;Ljava/lang/String;)V",
         "getBlocks",
         "()Ljava/util/List;",
         "getId",
         "()Ljava/lang/String;",
         "getQuestions",
         "getActions",
         "getCustomButtonText",
         "type",
         "Lio/intercom/android/sdk/survey/model/SurveyData$StepType;",
         "getType",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$StepType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "Question",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step {
   @SerializedName("blocks")
   @NotNull
   private final List blocks;
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("questions")
   @NotNull
   private final List questions;
   @SerializedName("step_type")
   private final int _type;
   @SerializedName("actions")
   @NotNull
   private final List actions;
   @SerializedName("custom_button_text")
   @Nullable
   private final String customButtonText;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step(@NotNull List var1, @NotNull String var2, @NotNull List var3, int var4, @NotNull List var5, @Nullable String var6) {
      super();
      Intrinsics.checkNotNullParameter(var1, "blocks");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "questions");
      Intrinsics.checkNotNullParameter(var5, "actions");
      this.blocks = var1;
      this.id = var2;
      this.questions = var3;
      this._type = var4;
      this.actions = var5;
      this.customButtonText = var6;
   }

   // $VF: synthetic method
   public SurveyData$Step(List var1, String var2, List var3, int var4, List var5, String var6, int var7, DefaultConstructorMarker var8) {
      this(var1, var2, var3, var4, (((var7 & 16) != 0) ? (List) (CollectionsKt.emptyList()) : var5), (((var7 & 32) != 0) ? (String) (null) : var6));
   }

   @NotNull
   public final List getBlocks() {
      return this.blocks;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getQuestions() {
      return this.questions;
   }

   @NotNull
   public final List getActions() {
      return this.actions;
   }

   @Nullable
   public final String getCustomButtonText() {
      return this.customButtonText;
   }

   @NotNull
   public final SurveyData$StepType getType() {
      SurveyData$StepType var10000;
      switch (this._type) {
         case 0:
            var10000 = SurveyData$StepType.CONTENT;
            break;
         case 1:
            var10000 = SurveyData$StepType.QUESTION;
            break;
         case 2:
            var10000 = SurveyData$StepType.INTRO;
            break;
         case 3:
            var10000 = SurveyData$StepType.THANK_YOU;
            break;
         default:
            var10000 = SurveyData$StepType.UNSUPPORTED;
      }

      return var10000;
   }

   @NotNull
   public final List component1() {
      return this.blocks;
   }

   @NotNull
   public final String component2() {
      return this.id;
   }

   @NotNull
   public final List component3() {
      return this.questions;
   }

   private final int component4() {
      return this._type;
   }

   @NotNull
   public final List component5() {
      return this.actions;
   }

   @Nullable
   public final String component6() {
      return this.customButtonText;
   }

   @NotNull
   public final SurveyData$Step copy(@NotNull List var1, @NotNull String var2, @NotNull List var3, int var4, @NotNull List var5, @Nullable String var6) {
      Intrinsics.checkNotNullParameter(var1, "blocks");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "questions");
      Intrinsics.checkNotNullParameter(var5, "actions");
      return new SurveyData$Step(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static SurveyData$Step copy$default(SurveyData$Step var0, List var1, String var2, List var3, int var4, List var5, String var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.blocks;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.id;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.questions;
      }

      if ((var7 & 8) != 0) {
         var4 = var0._type;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.actions;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.customButtonText;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "Step(blocks="
         + this.blocks
         + ", id="
         + this.id
         + ", questions="
         + this.questions
         + ", _type="
         + this._type
         + ", actions="
         + this.actions
         + ", customButtonText="
         + this.customButtonText
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.blocks.hashCode();
      var1 = var1 * 31 + this.id.hashCode();
      var1 = var1 * 31 + this.questions.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this._type);
      var1 = var1 * 31 + this.actions.hashCode();
      return var1 * 31 + (this.customButtonText == null ? 0 : this.customButtonText.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyData$Step)) {
         return false;
      } else {
         SurveyData$Step var2 = (SurveyData$Step)var1;
         if (!Intrinsics.areEqual(this.blocks, var2.blocks)) {
            return false;
         } else if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.questions, var2.questions)) {
            return false;
         } else if (this._type != var2._type) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.actions, var2.actions) ? false : Intrinsics.areEqual(this.customButtonText, var2.customButtonText);
         }
      }
   }
}
