package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.ValidationError;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$MultipleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UnsupportedQuestion;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.choice.MultipleChoiceQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.text.LongTextQuestionKt;
import io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nQuestionComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuestionComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionComponentKt$QuestionComponent$2\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,202:1\n87#2:203\n83#2,10:204\n94#2:239\n81#3,6:214\n88#3,6:229\n96#3:238\n402#4,9:220\n411#4,3:235\n*S KotlinDebug\n*F\n+ 1 QuestionComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionComponentKt$QuestionComponent$2\n*L\n97#1:203\n97#1:204,10\n97#1:239\n97#1:214,6\n97#1:229,6\n97#1:238\n97#1:220,9\n97#1:235,3\n*E\n"
)
final class QuestionComponentKt$QuestionComponent$2 implements Function3 {
   // $VF: synthetic field
   final QuestionState $questionState;
   // $VF: synthetic field
   final Modifier $contentModifier;
   // $VF: synthetic field
   final Function1 $onAnswer;
   // $VF: synthetic field
   final SurveyUiColors $surveyUiColors;
   // $VF: synthetic field
   final Function2 $questionHeader;
   // $VF: synthetic field
   final Function1 $onImeActionNext;
   // $VF: synthetic field
   final Function1 $onAnswerClick;
   // $VF: synthetic field
   final FontWeight $questionFontWeight;
   // $VF: synthetic field
   final long $questionFontSize;

   QuestionComponentKt$QuestionComponent$2(
      QuestionState var1, Modifier var2, Function1 var3, SurveyUiColors var4, Function2 var5, Function1 var6, Function1 var7, FontWeight var8, long var9
   ) {
      this.$questionState = var1;
      this.$contentModifier = var2;
      this.$onAnswer = var3;
      this.$surveyUiColors = var4;
      this.$questionHeader = var5;
      this.$onImeActionNext = var6;
      this.$onAnswerClick = var7;
      this.$questionFontWeight = var8;
      this.$questionFontSize = var9;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Card");
      ComposerKt.sourceInformation(var2, "C96@4861L4116:QuestionComponent.kt#bmpoeo");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2001737844, var3, -1, "io.intercom.android.sdk.survey.ui.components.QuestionComponent.<anonymous> (QuestionComponent.kt:96)"
            );
         }

         QuestionState var7 = this.$questionState;
         Modifier var8 = this.$contentModifier;
         Function1 var9 = this.$onAnswer;
         SurveyUiColors var10 = this.$surveyUiColors;
         Function2 var11 = this.$questionHeader;
         Function1 var12 = this.$onImeActionNext;
         Function1 var13 = this.$onAnswerClick;
         FontWeight var14 = this.$questionFontWeight;
         long var15 = this.$questionFontSize;
         int var17 = 0;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var19 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var17 >> 3 | 112 & var17 >> 3);
         Modifier var20 = var4;
         MeasurePolicy var21 = var19;
         Composer var22 = var2;
         int var23 = 112 & var17 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var17 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 1477490711, "C:QuestionComponent.kt#bmpoeo");
         SurveyData$Step$Question$QuestionModel var41 = var7.getQuestionModel();
         if (var41 instanceof SurveyData$Step$Question$DropDownQuestionModel) {
            var38.startReplaceGroup(1477468483);
            ComposerKt.sourceInformation(var38, "99@4997L352");
            SurveyData$Step$Question$DropDownQuestionModel var42 = (SurveyData$Step$Question$DropDownQuestionModel)var41;
            Answer var43 = var7.getAnswer();
            DropDownQuestionKt.DropDownQuestion(var8, var42, var43, var9, var10, var11, var38, 196608, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$ShortTextQuestionModel) {
            var38.startReplaceGroup(1477907970);
            ComposerKt.sourceInformation(var38, "110@5436L481");
            SurveyData$Step$Question$ShortTextQuestionModel var45 = (SurveyData$Step$Question$ShortTextQuestionModel)var41;
            Answer var51 = var7.getAnswer();
            ValidationError var44 = var7.getValidationError();
            ShortTextQuestionKt.ShortTextQuestion(var8, var45, var51, var9, var10, var44, var12, var11, var38, 12582912, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$LongTextQuestionModel) {
            var38.startReplaceGroup(1478470403);
            ComposerKt.sourceInformation(var38, "123@6003L480");
            SurveyData$Step$Question$LongTextQuestionModel var46 = (SurveyData$Step$Question$LongTextQuestionModel)var41;
            Answer var52 = var7.getAnswer();
            ValidationError var57 = var7.getValidationError();
            LongTextQuestionKt.LongTextQuestion(var8, var46, var52, var9, var10, var57, var12, var11, var38, 12582912, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$NumericRatingQuestionModel) {
            var38.startReplaceGroup(1479033177);
            ComposerKt.sourceInformation(var38, "136@6574L362");
            SurveyData$Step$Question$NumericRatingQuestionModel var47 = (SurveyData$Step$Question$NumericRatingQuestionModel)var41;
            Answer var53 = var7.getAnswer();
            NumericRatingQuestionKt.NumericRatingQuestion(var8, var47, var53, var9, var10, var11, var38, 196608, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$SingleChoiceQuestionModel) {
            var38.startReplaceGroup(1479481499);
            ComposerKt.sourceInformation(var38, "147@7026L360");
            SurveyData$Step$Question$SingleChoiceQuestionModel var48 = (SurveyData$Step$Question$SingleChoiceQuestionModel)var41;
            Answer var54 = var7.getAnswer();
            SingleChoiceQuestionKt.SingleChoiceQuestion(var8, var48, var54, var9, var10, var11, var38, 196608, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$MultipleChoiceQuestionModel) {
            var38.startReplaceGroup(1479930007);
            ComposerKt.sourceInformation(var38, "158@7478L364");
            SurveyData$Step$Question$MultipleChoiceQuestionModel var49 = (SurveyData$Step$Question$MultipleChoiceQuestionModel)var41;
            Answer var55 = var7.getAnswer();
            MultipleChoiceQuestionKt.MultipleChoiceQuestion(var8, var49, var55, var9, var10, var11, var38, 196608, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$DatePickerQuestionModel) {
            var38.startReplaceGroup(1480376314);
            ComposerKt.sourceInformation(var38, "169@7930L297");
            SurveyData$Step$Question$DatePickerQuestionModel var50 = (SurveyData$Step$Question$DatePickerQuestionModel)var41;
            Answer var56 = var7.getAnswer();
            DatePickerQuestionKt.DatePickerQuestion(var8, var50, var56, var9, var11, var38, 24576, 0);
            var38.endReplaceGroup();
         } else if (var41 instanceof SurveyData$Step$Question$UploadFileQuestionModel) {
            var38.startReplaceGroup(1480765519);
            ComposerKt.sourceInformation(var38, "185@8611L236,179@8315L532");
            UploadFileQuestionKt.UploadFileQuestion(
               var8,
               (SurveyData$Step$Question$UploadFileQuestionModel)var41,
               var7.getAnswer(),
               var9,
               var13,
               ComposableLambdaKt.rememberComposableLambda(-1590070470, true, new QuestionComponentKt$QuestionComponent$2$1$1(var7, var14, var15), var38, 54),
               var38,
               196608,
               0
            );
            var38.endReplaceGroup();
         } else if (Intrinsics.areEqual(var41, SurveyData$Step$Question$UnsupportedQuestion.INSTANCE)) {
            var38.startReplaceGroup(1481357340);
            var38.endReplaceGroup();
         } else {
            var38.startReplaceGroup(1481416395);
            var38.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
