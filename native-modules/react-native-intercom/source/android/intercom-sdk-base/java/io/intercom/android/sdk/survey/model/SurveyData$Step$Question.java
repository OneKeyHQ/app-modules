package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0011\b\u0087\b\u0018\u00002\u00020\u0001:\r\u001d\u001e\u001f !\"#$%&'()B-\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u0006\u0010\u0011\u001a\u00020\u0012J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÂ\u0003J\t\u0010\u0016\u001a\u00020\nHÂ\u0003J7\u0010\u0017\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nHÇ\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\bH×\u0001J\t\u0010\u001c\u001a\u00020\u0006H×\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\u0007\u001a\u00020\b8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\n8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question;",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "id",
         "",
         "questionType",
         "",
         "questionData",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData;",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;ILio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData;)V",
         "getBlocks",
         "()Ljava/util/List;",
         "getId",
         "()Ljava/lang/String;",
         "getQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "QuestionType",
         "QuestionValidation",
         "QuestionData",
         "QuestionModel",
         "DropDownQuestionModel",
         "SingleChoiceQuestionModel",
         "MultipleChoiceQuestionModel",
         "ShortTextQuestionModel",
         "DatePickerQuestionModel",
         "LongTextQuestionModel",
         "NumericRatingQuestionModel",
         "UploadFileQuestionModel",
         "UnsupportedQuestion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSurveyData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyData.kt\nio/intercom/android/sdk/survey/model/SurveyData$Step$Question\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,411:1\n1563#2:412\n1634#2,3:413\n1563#2:416\n1634#2,3:417\n1563#2:420\n1634#2,3:421\n1563#2:424\n1634#2,3:425\n1563#2:428\n1634#2,3:429\n*S KotlinDebug\n*F\n+ 1 SurveyData.kt\nio/intercom/android/sdk/survey/model/SurveyData$Step$Question\n*L\n194#1:412\n194#1:413,3\n231#1:416\n231#1:417,3\n235#1:420\n235#1:421,3\n253#1:424\n253#1:425,3\n261#1:428\n261#1:429,3\n*E\n"
)
public final class SurveyData$Step$Question {
   @SerializedName("blocks")
   @NotNull
   private final List blocks;
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("question_type")
   private final int questionType;
   @SerializedName("data")
   @NotNull
   private final SurveyData$Step$Question$QuestionData questionData;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question(@NotNull List var1, @NotNull String var2, int var3, @NotNull SurveyData$Step$Question$QuestionData var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "blocks");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var4, "questionData");
      this.blocks = var1;
      this.id = var2;
      this.questionType = var3;
      this.questionData = var4;
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
   public final SurveyData$Step$Question$QuestionModel getQuestionModel() {
      int var1 = this.questionType;
      SurveyData$Step$Question$QuestionModel var10000;
      if (var1 == SurveyData$Step$Question$QuestionType.DROPDOWN.ordinal()) {
         String var2 = this.id;
         List var3 = this.blocks;
         boolean var4 = this.questionData.getRequired();
         String var5 = this.questionData.getPlaceholder();
         Iterable var7 = this.questionData.getOptions();
         int var8 = 0;
         Iterable var9 = var7;
         ArrayList var10 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
         int var11 = 0;

         for (Object var13 : var9) {
            JsonElement var14 = (JsonElement)var13;
            Collection var24 = var10;
            int var15 = 0;
            var24.add(var14.getAsString());
         }

         List var6 = (List)var10;
         var10000 = new SurveyData$Step$Question$DropDownQuestionModel(var2, var3, var4, var6, var5, null, 32, null);
      } else if (var1 == SurveyData$Step$Question$QuestionType.INPUT.ordinal()) {
         var10000 = new SurveyData$Step$Question$ShortTextQuestionModel(
            this.id,
            this.blocks,
            this.questionData.getRequired(),
            this.questionData.getPlaceholder(),
            this.questionData.getValidation() != null
               ? this.questionData.getValidation().getValidationType()
               : SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION,
            this.questionData.getCharacterLimit(),
            false,
            null,
            null,
            448,
            null
         );
      } else if (var1 == SurveyData$Step$Question$QuestionType.TEXT.ordinal()) {
         var10000 = new SurveyData$Step$Question$LongTextQuestionModel(
            this.id,
            this.blocks,
            this.questionData.getRequired(),
            this.questionData.getPlaceholder(),
            this.questionData.getValidation() != null
               ? this.questionData.getValidation().getValidationType()
               : SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION,
            this.questionData.getCharacterLimit(),
            0.0F,
            0,
            null,
            448,
            null
         );
      } else if (var1 == SurveyData$Step$Question$QuestionType.RATING_SCALE.ordinal()) {
         String var41 = this.id;
         List var44 = this.blocks;
         boolean var47 = this.questionData.getRequired();
         String var50 = this.questionData.getLowerLabel();
         String var53 = this.questionData.getUpperLabel();
         int var56 = this.questionData.getScaleStart();
         int var59 = this.questionData.getScaleEnd();
         List var89;
         if (this.questionData.getQuestionSubtype() == SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI.ordinal()) {
            Gson var71 = new Gson();
            int var73 = 0;
            Iterable var75 = this.questionData.getOptions();
            int var77 = 0;
            Iterable var78 = var75;
            ArrayList var79 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var75, 10));
            int var80 = 0;

            for (Object var20 : var78) {
               JsonElement var21 = (JsonElement)var20;
               Collection var22 = var79;
               int var23 = 0;
               var22.add(
                  (SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption)var71.fromJson(
                     var21, SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption.class
                  )
               );
            }

            var89 = (List)var79;
         } else {
            Iterable var68 = new IntRange(this.questionData.getScaleStart(), this.questionData.getScaleEnd());
            int var70 = 0;
            Iterable var72 = var68;
            ArrayList var74 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var68, 10));
            int var76 = 0;
            Iterator var16 = var72.iterator();

            while (var16.hasNext()) {
               int var17 = ((IntIterator)var16).nextInt();
               int var18 = var17;
               Collection var82 = var74;
               int var19 = 0;
               var82.add(new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption(var18));
            }

            var89 = (List)var74;
         }

         List var62 = var89;
         int var69 = this.questionData.getQuestionSubtype();
         SurveyData$Step$Question$QuestionData$QuestionSubType var65 = var69 == SurveyData$Step$Question$QuestionData$QuestionSubType.NUMERIC.ordinal()
            ? SurveyData$Step$Question$QuestionData$QuestionSubType.NUMERIC
            : (
               var69 == SurveyData$Step$Question$QuestionData$QuestionSubType.STARS.ordinal()
                  ? SurveyData$Step$Question$QuestionData$QuestionSubType.STARS
                  : (
                     var69 == SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI.ordinal()
                        ? SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI
                        : (
                           var69 == SurveyData$Step$Question$QuestionData$QuestionSubType.NPS.ordinal()
                              ? SurveyData$Step$Question$QuestionData$QuestionSubType.NPS
                              : SurveyData$Step$Question$QuestionData$QuestionSubType.UNSUPPORTED
                        )
                  )
            );
         var10000 = new SurveyData$Step$Question$NumericRatingQuestionModel(var41, var44, var47, var62, var50, var53, var56, var59, var65);
      } else if (var1 == SurveyData$Step$Question$QuestionType.MULTI_SELECT.ordinal()) {
         if (this.questionData.getMaxSelection() == 1) {
            String var90 = this.id;
            List var10001 = this.blocks;
            Iterable var42 = this.questionData.getOptions();
            boolean var27 = this.questionData.getRequired();
            List var26 = var10001;
            String var25 = var90;
            int var45 = 0;
            Iterable var48 = var42;
            ArrayList var51 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var42, 10));
            int var54 = 0;

            for (Object var60 : var48) {
               JsonElement var63 = (JsonElement)var60;
               Collection var28 = var51;
               int var66 = 0;
               var28.add(var63.getAsString());
            }

            List var86 = (List)var51;
            boolean var29 = this.questionData.getIncludeOther();
            List var30 = var86;
            boolean var31 = var27;
            List var32 = var26;
            String var33 = var25;
            var10000 = new SurveyData$Step$Question$SingleChoiceQuestionModel(var33, var32, var31, var30, var29);
         } else {
            String var91 = this.id;
            List var92 = this.blocks;
            Iterable var43 = this.questionData.getOptions();
            boolean var85 = this.questionData.getRequired();
            List var84 = var92;
            String var83 = var91;
            int var46 = 0;
            Iterable var49 = var43;
            ArrayList var52 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var43, 10));
            int var55 = 0;

            for (Object var61 : var49) {
               JsonElement var64 = (JsonElement)var61;
               Collection var87 = var52;
               int var67 = 0;
               var87.add(var64.getAsString());
            }

            List var88 = (List)var52;
            boolean var10004 = this.questionData.getIncludeOther();
            int var34 = this.questionData.getMaxSelection();
            int var35 = this.questionData.getMinSelection();
            boolean var36 = var10004;
            List var37 = var88;
            boolean var38 = var85;
            List var39 = var84;
            String var40 = var83;
            var10000 = new SurveyData$Step$Question$MultipleChoiceQuestionModel(var40, var39, var38, var37, var36, var35, var34);
         }
      } else {
         var10000 = SurveyData$Step$Question$UnsupportedQuestion.INSTANCE;
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

   private final int component3() {
      return this.questionType;
   }

   private final SurveyData$Step$Question$QuestionData component4() {
      return this.questionData;
   }

   @NotNull
   public final SurveyData$Step$Question copy(@NotNull List var1, @NotNull String var2, int var3, @NotNull SurveyData$Step$Question$QuestionData var4) {
      Intrinsics.checkNotNullParameter(var1, "blocks");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var4, "questionData");
      return new SurveyData$Step$Question(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static SurveyData$Step$Question copy$default(
      SurveyData$Step$Question var0, List var1, String var2, int var3, SurveyData$Step$Question$QuestionData var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.blocks;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.id;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.questionType;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.questionData;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Question(blocks=" + this.blocks + ", id=" + this.id + ", questionType=" + this.questionType + ", questionData=" + this.questionData + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.blocks.hashCode();
      var1 = var1 * 31 + this.id.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.questionType);
      return var1 * 31 + this.questionData.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyData$Step$Question)) {
         return false;
      } else {
         SurveyData$Step$Question var2 = (SurveyData$Step$Question)var1;
         if (!Intrinsics.areEqual(this.blocks, var2.blocks)) {
            return false;
         } else if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else {
            return this.questionType != var2.questionType ? false : Intrinsics.areEqual(this.questionData, var2.questionData);
         }
      }
   }
}
