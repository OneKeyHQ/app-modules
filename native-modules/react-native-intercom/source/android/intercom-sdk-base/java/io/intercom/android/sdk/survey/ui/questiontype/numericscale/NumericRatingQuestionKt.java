package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import android.content.res.Configuration;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Absolute;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aZ\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0013\b\u0002\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a-\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0006\u001a\u00020\u0007H\u0003¢\u0006\u0002\u0010\u001a¨\u0006\u001b",
   d2 = {
         "NumericRatingQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "numericRatingQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "NPSQuestionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "StarQuestionPreview",
         "EmojiRatingQuestionPreview",
         "GeneratePreview",
         "start",
         "",
         "end",
         "questionSubType",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;",
         "(IILio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;Lio/intercom/android/sdk/survey/ui/models/Answer;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNumericRatingQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NumericRatingQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingQuestionKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,237:1\n70#2:238\n67#2,9:239\n77#2:446\n81#3,6:248\n88#3,6:263\n81#3,6:281\n88#3,6:296\n81#3,6:315\n88#3,6:330\n96#3:349\n81#3,6:361\n88#3,6:376\n96#3:397\n81#3,6:413\n88#3,6:428\n96#3:437\n96#3:441\n96#3:445\n402#4,9:254\n411#4:269\n402#4,9:287\n411#4:302\n402#4,9:321\n411#4:336\n412#4,2:347\n402#4,9:367\n411#4:382\n412#4,2:395\n402#4,9:419\n411#4,3:434\n412#4,2:439\n412#4,2:443\n87#5:270\n83#5,10:271\n94#5:442\n118#6:303\n118#6:338\n118#6:339\n118#6:384\n118#6:385\n118#6:386\n118#6:387\n118#6:403\n75#7:304\n1869#8:305\n1869#8:337\n1870#8:346\n1870#8:351\n1869#8:383\n1870#8:394\n1563#8:399\n1634#8,3:400\n99#9:306\n97#9,8:307\n106#9:350\n99#9:352\n97#9,8:353\n106#9:398\n99#9:404\n97#9,8:405\n106#9:438\n1047#10,6:340\n1047#10,6:388\n*S KotlinDebug\n*F\n+ 1 NumericRatingQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingQuestionKt\n*L\n53#1:238\n53#1:239,9\n53#1:446\n53#1:248,6\n53#1:263,6\n54#1:281,6\n54#1:296,6\n69#1:315,6\n69#1:330,6\n69#1:349\n101#1:361,6\n101#1:376,6\n101#1:397\n142#1:413,6\n142#1:428,6\n142#1:437\n54#1:441\n53#1:445\n53#1:254,9\n53#1:269\n54#1:287,9\n54#1:302\n69#1:321,9\n69#1:336\n69#1:347,2\n101#1:367,9\n101#1:382\n101#1:395,2\n142#1:419,9\n142#1:434,3\n54#1:439,2\n53#1:443,2\n54#1:270\n54#1:271,10\n54#1:442\n57#1:303\n80#1:338\n86#1:339\n113#1:384\n120#1:385\n121#1:386\n122#1:387\n145#1:403\n63#1:304\n68#1:305\n73#1:337\n73#1:346\n68#1:351\n106#1:383\n106#1:394\n133#1:399\n133#1:400,3\n69#1:306\n69#1:307,8\n69#1:350\n101#1:352\n101#1:353,8\n101#1:398\n142#1:404\n142#1:405,8\n142#1:438\n87#1:340,6\n123#1:388,6\n*E\n"
)
public final class NumericRatingQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void NumericRatingQuestion(
      @Nullable Modifier var0,
      @NotNull io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @Nullable Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "numericRatingQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      Composer var141 = var6.startRestartGroup(-1325570147);
      ComposerKt.sourceInformation(var141, "C(NumericRatingQuestion)P(2,3!1,4)52@2948L5874:NumericRatingQuestion.kt#hdztyp");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var141.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var141.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var141.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var141.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var141.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var141.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var141.getSkipping()) {
         var141.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var8 & 32) != 0) {
            var5 = io.intercom.android.sdk.survey.ui.questiontype.numericscale.ComposableSingletons$NumericRatingQuestionKt.INSTANCE
               .recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1325570147, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestion (NumericRatingQuestion.kt:51)"
            );
         }

         int var12 = 14 & var9;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var141, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var10 = Alignment.Companion.getTopStart();
         boolean var11 = false;
         MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var10, var11);
         Modifier var15 = var0;
         MeasurePolicy var16 = var14;
         Composer var17 = var141;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         BoxScope var34 = BoxScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 321624264, "C53@2983L5833:NumericRatingQuestion.kt#hdztyp");
         int var36 = 0;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var33, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var38 = Modifier.Companion;
         Vertical var39 = Arrangement.INSTANCE.getTop();
         Horizontal var40 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var39, var40, var33, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var42 = var38;
         MeasurePolicy var43 = measurePolicy$ivx;
         Composer var44 = var33;
         int var45 = 112 & var36 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var44, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var44, 0));
         CompositionLocalMap localMap$iv$ivx = var44.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var44, var42);
         Function0 var50 = ComposeUiNode.Companion.getConstructor();
         Composer var51 = var44;
         int var52 = 6 | 896 & var45 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var51, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var51.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var51.startReusableNode();
         if (var51.getInserting()) {
            var51.createNode(var50);
         } else {
            var51.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var51);
         int var55/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var43, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var56 = 14 & var52 >> 6;
         Composer $composer$ivx = var51;
         int var58 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var59 = 6 | 112 & var36 >> 6;
         Composer var60 = $composer$ivx;
         ColumnScope var61 = ColumnScopeInstance.INSTANCE;
         int var62 = 0;
         ComposerKt.sourceInformationMarkerStart(var60, 122212326, "C55@3049L8,56@3070L41:NumericRatingQuestion.kt#hdztyp");
         var5.invoke(var60, 14 & var9 >> 15);
         Modifier var10000 = Modifier.Companion;
         int var63 = 16;
         int var64 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var63)), var60, 6);
         switch (var1.getQuestionSubType()) {
            case NPS:
            case UNSUPPORTED:
            case NUMERIC:
               var60.startReplaceGroup(122317012);
               ComposerKt.sourceInformation(var60, "62@3387L7,*68@3793L1805");
               int var147 = var1.getOptions().size();
               CompositionLocal var150 = AndroidCompositionLocals_androidKt.getLocalConfiguration();
               int $changed$ivx = 0;
               int var158 = 0;
               ComposerKt.sourceInformationMarkerStart(var60, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var162 = var60.consume(var150);
               ComposerKt.sourceInformationMarkerEnd(var60);
               int var166 = (((Configuration)var162).screenWidthDp - 60) / 60;
               int var151 = (int)Math.ceil((double)var147 / var166);
               int var155 = (int)Math.ceil((double)var147 / var151);
               Iterable var159 = CollectionsKt.chunked(var1.getOptions(), var155);
               int var163 = 0;

               for (Object var172 : var159) {
                  List var175 = (List)var172;
                  int var177 = 0;
                  Modifier var179 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
                  androidx.compose.foundation.layout.Arrangement.Horizontal var181 = Absolute.INSTANCE.getCenter();
                  byte var183 = 54;
                  int var185 = 0;
                  ComposerKt.sourceInformationMarkerStart(
                     var60, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
                  );
                  androidx.compose.ui.Alignment.Vertical var187 = Alignment.Companion.getTop();
                  MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var181, var187, var60, 14 & var183 >> 3 | 112 & var183 >> 3);
                  Modifier var191 = var179;
                  MeasurePolicy var193 = measurePolicy$ivxx;
                  Composer var195 = var60;
                  int var197 = 112 & var183 << 3;
                  int $i$f$Layoutxx = 0;
                  ComposerKt.sourceInformationMarkerStart(
                     var195, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh"
                  );
                  int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var195, 0));
                  CompositionLocalMap localMap$iv$ivxx = var195.getCurrentCompositionLocalMap();
                  Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var195, var191);
                  Function0 var207 = ComposeUiNode.Companion.getConstructor();
                  Composer var209 = var195;
                  int var213 = 6 | 896 & var197 << 6;
                  int $i$f$ReusableComposeNodexx = 0;
                  ComposerKt.sourceInformationMarkerStart(
                     var209, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp"
                  );
                  if (!(var209.getApplier() instanceof Applier)) {
                     ComposablesKt.invalidApplier();
                  }

                  var209.startReusableNode();
                  if (var209.getInserting()) {
                     var209.createNode(var207);
                  } else {
                     var209.useNode();
                  }

                  Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var209);
                  int var218/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
                  Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var193, ComposeUiNode.Companion.getSetMeasurePolicy());
                  Updater.recovered$89682b199a142880(
                     $this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
                  );
                  Updater.recovered$89682b199a142880(
                     $this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
                  );
                  Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                  Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
                  int var219 = 14 & var213 >> 6;
                  Composer $composer$ivxx = var209;
                  int var221 = 0;
                  ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
                  int var222 = 6 | 112 & var183 >> 6;
                  Composer var223 = $composer$ivxx;
                  RowScope var100 = RowScopeInstance.INSTANCE;
                  int var101 = 0;
                  ComposerKt.sourceInformationMarkerStart(var223, -263278386, "C:NumericRatingQuestion.kt#hdztyp");
                  var223.startReplaceGroup(268602154);
                  ComposerKt.sourceInformation(var223, "*86@5078L143,82@4810L724");
                  Iterable var102 = var175;
                  int $i$f$forEachx = 0;

                  for (Object element$ivx : var102) {
                     io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption var232 = (io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption)element$ivx;
                     int var236 = 0;
                     Intrinsics.checkNotNull(
                        var232,
                        "null cannot be cast to non-null type io.intercom.android.sdk.survey.model.SurveyData.Step.Question.NumericRatingQuestionModel.RatingOption.NumericRatingOption"
                     );
                     io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption var237 = (io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption)var232;
                     boolean var238 = var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer && Intrinsics.areEqual(((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var2).getAnswer(), String.valueOf(var237.getValue()));
                     var223.startReplaceGroup(268611604);
                     ComposerKt.sourceInformation(var223, "77@4431L6");
                     long var110 = var238
                        ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
                        : IntercomTheme.INSTANCE.getColors(var223, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
                     var223.endReplaceGroup();
                     long var112 = var110;
                     long var239 = ColorExtensionsKt.recovered$9902137196ed1ac6(var112);
                     float var258;
                     if (var238) {
                        int $this$dp$ivx = 2;
                        int $i$f$getDpx = 0;
                        var258 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
                     } else {
                        int $this$dp$ivx = 1;
                        int $i$f$getDpx = 0;
                        var258 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
                     }

                     float var246 = var258;
                     FontWeight var242 = var238 ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal();
                     String var245 = String.valueOf(var237.getValue());
                     Modifier var259 = Modifier.Companion;
                     int $this$dp$ivx = 4;
                     int $i$f$getDpx = 0;
                     Modifier var260 = PaddingKt.recovered$eb6a744cd98cdbca(var259, Dp.recovered$95b3d103663bf060($this$dp$ivx));
                     boolean var264 = false;
                     Object var265 = null;
                     Object var266 = null;
                     Object var267 = null;
                     var223.startReplaceGroup(-1805377731);
                     ComposerKt.sourceInformation(var223, "CC(remember):NumericRatingQuestion.kt#9igjgp");
                     Composer var250 = var223;
                     boolean var251 = (var9 & 7168) == 2048 | var141.changed(var237);
                     int var120 = 0;
                     Object var121 = var250.rememberedValue();
                     int var122 = 0;
                     Object var268;
                     if (!var251 && var121 != Composer.Companion.getEmpty()) {
                        var268 = var121;
                     } else {
                        Object var123 = null;
                        Object var124 = null;
                        Object var125 = null;
                        boolean var126 = false;
                        Modifier var127 = var260;
                        int var128 = 0;
                        final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1478_0 = var3;
                        final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption recoveredExpressionCapture_1478_1 = var237;
                        Function0 var129 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.NumericRatingQuestion$lambda$12$lambda$11$lambda$4$lambda$3$lambda$2$lambda$1$lambda$0(recoveredExpressionCapture_1478_0, recoveredExpressionCapture_1478_1));
                        var260 = var127;
                        var264 = var126;
                        var265 = var125;
                        var266 = var124;
                        var267 = var123;
                        Object var130 = var129;
                        var250.updateRememberedValue(var130);
                        var268 = var130;
                     }

                     Function0 var248 = (Function0)var268;
                     var223.endReplaceGroup();
                     Modifier var131 = ClickableKt.recovered$1c9660c41fd850dc(
                        var260, var264, (String)var265, (Role)var266, (MutableInteractionSource)var267, var248, 15, null
                     );
                     NumericRatingCellKt.recovered$7a9e16f91d684c97(var245, var131, var239, var246, var112, var242, 0L, 0L, var223, 0, 192);
                  }

                  var223.endReplaceGroup();
                  ComposerKt.sourceInformationMarkerEnd(var223);
                  ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
                  var209.endNode();
                  ComposerKt.sourceInformationMarkerEnd(var209);
                  ComposerKt.sourceInformationMarkerEnd(var195);
                  ComposerKt.sourceInformationMarkerEnd(var60);
               }

               var60.endReplaceGroup();
               break;
            case STARS:
               var60.startReplaceGroup(124699982);
               ComposerKt.sourceInformation(var60, "100@5691L1659");
               Modifier var146 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               androidx.compose.foundation.layout.Arrangement.Horizontal var165 = Arrangement.INSTANCE.getCenter();
               byte var153 = 54;
               int var157 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var60, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
               );
               androidx.compose.ui.Alignment.Vertical var149 = Alignment.Companion.getTop();
               MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var165, var149, var60, 14 & var153 >> 3 | 112 & var153 >> 3);
               Modifier var168 = var146;
               MeasurePolicy var171 = measurePolicy$ivxx;
               Composer var174 = var60;
               int var74 = 112 & var153 << 3;
               int $i$f$Layoutxx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var174, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh"
               );
               int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var174, 0));
               CompositionLocalMap localMap$iv$ivxx = var174.getCurrentCompositionLocalMap();
               Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var174, var168);
               Function0 var79 = ComposeUiNode.Companion.getConstructor();
               Composer var80 = var174;
               int var81 = 6 | 896 & var74 << 6;
               int $i$f$ReusableComposeNodexx = 0;
               ComposerKt.sourceInformationMarkerStart(var80, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
               if (!(var80.getApplier() instanceof Applier)) {
                  ComposablesKt.invalidApplier();
               }

               var80.startReusableNode();
               if (var80.getInserting()) {
                  var80.createNode(var79);
               } else {
                  var80.useNode();
               }

               Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var80);
               int var84/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var171, ComposeUiNode.Companion.getSetMeasurePolicy());
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
               );
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
               );
               Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
               int var85 = 14 & var81 >> 6;
               Composer $composer$ivxx = var80;
               int var87 = 0;
               ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
               int var88 = 6 | 112 & var153 >> 6;
               Composer var89 = $composer$ivxx;
               RowScope var90 = RowScopeInstance.INSTANCE;
               int var91 = 0;
               ComposerKt.sourceInformationMarkerStart(var89, -1111299613, "C:NumericRatingQuestion.kt#hdztyp");
               var89.startReplaceGroup(-1421319712);
               ComposerKt.sourceInformation(var89, "*122@7055L208,114@6575L723");
               Iterable var92 = var1.getOptions();
               int var93 = 0;

               for (Object var95 : var92) {
                  io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption var96 = (io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption)var95;
                  int var97 = 0;
                  Intrinsics.checkNotNull(
                     var96,
                     "null cannot be cast to non-null type io.intercom.android.sdk.survey.model.SurveyData.Step.Question.NumericRatingQuestionModel.RatingOption.NumericRatingOption"
                  );
                  io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption var98 = (io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption)var96;
                  boolean var99 = var2 instanceof io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer && var98.getValue() <= Integer.parseInt(((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var2).getAnswer());
                  var89.startReplaceGroup(-1421310379);
                  ComposerKt.sourceInformation(var89, "110@6353L6");
                  long var132 = var99
                     ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
                     : IntercomTheme.INSTANCE.getColors(var89, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
                  var89.endReplaceGroup();
                  long var134 = var132;
                  long var252 = ColorExtensionsKt.recovered$9902137196ed1ac6(var134);
                  float var253;
                  if (var99) {
                     int $this$dp$ivx = 2;
                     int $i$f$getDpx = 0;
                     var253 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
                  } else {
                     int $this$dp$ivx = 1;
                     int $i$f$getDpx = 0;
                     var253 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
                  }

                  float var106 = var253;
                  Modifier var254 = Modifier.Companion;
                  int $this$dp$ivx = 44;
                  int $i$f$getDpx = 0;
                  Modifier var255 = SizeKt.recovered$4f2dac42b6e775a9(var254, Dp.recovered$95b3d103663bf060($this$dp$ivx));
                  int $this$dp$ivxx = 44;
                  int $i$f$getDpxx = 0;
                  Modifier var256 = SizeKt.recovered$3f6c6a76aa292717(var255, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
                  int $this$dp$ivxxx = 8;
                  int $i$f$getDpxxx = 0;
                  Modifier var257 = PaddingKt.recovered$eb6a744cd98cdbca(var256, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
                  boolean var10001 = false;
                  Object var10002 = null;
                  Object var10003 = null;
                  Object var10004 = null;
                  var89.startReplaceGroup(268698399);
                  ComposerKt.sourceInformation(var89, "CC(remember):NumericRatingQuestion.kt#9igjgp");
                  Composer var235 = var89;
                  boolean var108 = var141.changed(var98) | (var9 & 7168) == 2048;
                  int var109 = 0;
                  Object var136 = var235.rememberedValue();
                  int var137 = 0;
                  Object var10005;
                  if (!var108 && var136 != Composer.Companion.getEmpty()) {
                     var10005 = var136;
                  } else {
                     Object var138 = null;
                     Object var139 = null;
                     Object var114 = null;
                     boolean var115 = false;
                     Modifier var116 = var257;
                     int var117 = 0;
                     final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption recoveredExpressionCapture_1479_0 = var98;
                     final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1479_1 = var3;
                     Function0 var118 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.NumericRatingQuestion$lambda$12$lambda$11$lambda$8$lambda$7$lambda$6$lambda$5(recoveredExpressionCapture_1479_0, recoveredExpressionCapture_1479_1));
                     var257 = var116;
                     var10001 = var115;
                     var10002 = var114;
                     var10003 = var139;
                     var10004 = var138;
                     Object var119 = var118;
                     var235.updateRememberedValue(var119);
                     var10005 = var119;
                  }

                  Function0 var230 = (Function0)var10005;
                  var89.endReplaceGroup();
                  StarRatingKt.recovered$dca724aa12230f37(
                     ClickableKt.recovered$1c9660c41fd850dc(
                        var257, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var230, 15, null
                     ),
                     var134,
                     var106,
                     var252,
                     var89,
                     0,
                     0
                  );
               }

               var89.endReplaceGroup();
               ComposerKt.sourceInformationMarkerEnd(var89);
               ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
               var80.endNode();
               ComposerKt.sourceInformationMarkerEnd(var80);
               ComposerKt.sourceInformationMarkerEnd(var174);
               ComposerKt.sourceInformationMarkerEnd(var60);
               var60.endReplaceGroup();
               break;
            case EMOJI:
               var60.startReplaceGroup(126366697);
               ComposerKt.sourceInformation(var60, "131@7416L224");
               Iterable var65 = var1.getOptions();
               int var70 = 0;
               Iterable var66 = var65;
               ArrayList var67 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var65, 10));
               int var68 = 0;

               for (Object var71 : var66) {
                  io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption var72 = (io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption)var71;
                  Collection var140 = var67;
                  int var73 = 0;
                  Intrinsics.checkNotNull(
                     var72,
                     "null cannot be cast to non-null type io.intercom.android.sdk.survey.model.SurveyData.Step.Question.NumericRatingQuestionModel.RatingOption.EmojiRatingOption"
                  );
                  var140.add((io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption)var72);
               }

               EmojiQuestionKt.EmojiQuestion((List)var67, var2, var3, var60, 112 & var9 >> 3 | 896 & var9 >> 3);
               var60.endReplaceGroup();
               break;
            default:
               var60.startReplaceGroup(3944733);
               var60.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
         }

         var60.startReplaceGroup(4087227);
         ComposerKt.sourceInformation(var60, "141@7837L955");
         if (!StringsKt.isBlank(var1.getLowerLabel()) & !StringsKt.isBlank(var1.getUpperLabel())) {
            Modifier var261 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int var142 = 8;
            int var144 = 0;
            Modifier var143 = PaddingKt.recovered$eb6a744cd98cdbca(var261, Dp.recovered$95b3d103663bf060(var142));
            androidx.compose.foundation.layout.Arrangement.Horizontal var145 = Arrangement.INSTANCE.getSpaceBetween();
            byte var167 = 54;
            int var152 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var60, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.ui.Alignment.Vertical var148 = Alignment.Companion.getTop();
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var145, var148, var60, 14 & var167 >> 3 | 112 & var167 >> 3);
            Modifier var160 = var143;
            MeasurePolicy var164 = measurePolicy$ivxx;
            Composer var170 = var60;
            int var173 = 112 & var167 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var170, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var170, 0));
            CompositionLocalMap localMap$iv$ivxx = var170.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var170, var160);
            Function0 var184 = ComposeUiNode.Companion.getConstructor();
            Composer var186 = var170;
            int var188 = 6 | 896 & var173 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var186, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var186.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var186.startReusableNode();
            if (var186.getInserting()) {
               var186.createNode(var184);
            } else {
               var186.useNode();
            }

            Composer recoveredRatingRowUpdater = Updater.recovered$676aa17b44fd6280(var186);
            int var194/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880(recoveredRatingRowUpdater, var164, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(recoveredRatingRowUpdater, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(recoveredRatingRowUpdater, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(recoveredRatingRowUpdater, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(recoveredRatingRowUpdater, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var196 = 14 & var188 >> 6;
            Composer recoveredRatingRowComposer = var186;
            int var200 = 0;
            ComposerKt.sourceInformationMarkerStart(recoveredRatingRowComposer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var202 = 6 | 112 & var167 >> 6;
            Composer var204 = recoveredRatingRowComposer;
            RowScope var206 = RowScopeInstance.INSTANCE;
            int var208 = 0;
            ComposerKt.sourceInformationMarkerStart(var204, -1109140587, "C158@8709L23,159@8753L21:NumericRatingQuestion.kt#hdztyp");
            List var262;
            if (var1.getQuestionSubType() == io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI) {
               String[] var210 = new String[]{var1.getLowerLabel(), var1.getUpperLabel()};
               var262 = CollectionsKt.listOf(var210);
            } else {
               String[] var211 = new String[]{var1.getScaleStart() + " - " + var1.getLowerLabel(), var1.getScaleEnd() + " - " + var1.getUpperLabel()};
               var262 = CollectionsKt.listOf(var211);
            }

            List var214 = var262;
            String var212 = (String)var214.get(0);
            String var216 = (String)var214.get(1);
            TextKt.recovered$5b114ab32a4f4190(
               var212, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var204, 0, 0, 262142
            );
            TextKt.recovered$5b114ab32a4f4190(
               var216, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var204, 0, 0, 262142
            );
            ComposerKt.sourceInformationMarkerEnd(var204);
            ComposerKt.sourceInformationMarkerEnd(recoveredRatingRowComposer);
            var186.endNode();
            ComposerKt.sourceInformationMarkerEnd(var186);
            ComposerKt.sourceInformationMarkerEnd(var170);
            ComposerKt.sourceInformationMarkerEnd(var60);
         }

         var60.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var60);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var51.endNode();
         ComposerKt.sourceInformationMarkerEnd(var51);
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var141);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var263 = var141.endRestartGroup();
      if (var263 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1480_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel recoveredExpressionCapture_1480_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1480_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1480_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredExpressionCapture_1480_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1480_5 = var5;
         final int recoveredExpressionCapture_1480_6 = var7;
         final int recoveredExpressionCapture_1480_7 = var8;
         var263.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1480_0, recoveredExpressionArgument_1480_1) -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.NumericRatingQuestion$lambda$13(recoveredExpressionCapture_1480_0, recoveredExpressionCapture_1480_1, recoveredExpressionCapture_1480_2, recoveredExpressionCapture_1480_3, recoveredExpressionCapture_1480_4, recoveredExpressionCapture_1480_5, recoveredExpressionCapture_1480_6, recoveredExpressionCapture_1480_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1480_0), ((java.lang.Integer)recoveredExpressionArgument_1480_1).intValue())));
      }
   }

   @Preview(name = "NPS Rating", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NPSQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-752808306);
      ComposerKt.sourceInformation(var2, "C(NPSQuestionPreview)169@8929L126:NumericRatingQuestion.kt#hdztyp");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -752808306, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.NPSQuestionPreview (NumericRatingQuestion.kt:168)"
            );
         }

         GeneratePreview(0, 10, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType.NPS, new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer("4"), var2, 438);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1477_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1477_0, recoveredExpressionArgument_1477_1) -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.NPSQuestionPreview$lambda$14(recoveredExpressionCapture_1477_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1477_0), ((java.lang.Integer)recoveredExpressionArgument_1477_1).intValue())));
      }
   }

   @Preview(name = "Star Rating", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StarQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(1791167217);
      ComposerKt.sourceInformation(var3, "C(StarQuestionPreview)180@9164L141:NumericRatingQuestion.kt#hdztyp");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1791167217, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.StarQuestionPreview (NumericRatingQuestion.kt:179)"
            );
         }

         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType var10002 = io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType.STARS;
         String[] var2 = new String[]{"1", "2"};
         GeneratePreview(1, 5, var10002, new io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer(SetsKt.setOf(var2), null, 2, null), var3, 438);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1481_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1481_0, recoveredExpressionArgument_1481_1) -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.StarQuestionPreview$lambda$15(recoveredExpressionCapture_1481_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1481_0), ((java.lang.Integer)recoveredExpressionArgument_1481_1).intValue())));
      }
   }

   @Preview(name = "Emoji Rating", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EmojiRatingQuestionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1678291132);
      ComposerKt.sourceInformation(var2, "C(EmojiRatingQuestionPreview)191@9422L127:NumericRatingQuestion.kt#hdztyp");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1678291132, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.EmojiRatingQuestionPreview (NumericRatingQuestion.kt:190)"
            );
         }

         GeneratePreview(1, 5, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI, new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer("4"), var2, 438);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1475_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1475_0, recoveredExpressionArgument_1475_1) -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.EmojiRatingQuestionPreview$lambda$16(recoveredExpressionCapture_1475_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1475_0), ((java.lang.Integer)recoveredExpressionArgument_1475_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void GeneratePreview(int var0, int var1, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType var2, Answer var3, Composer var4, int var5) {
      Composer var7 = var4.startRestartGroup(-1397971036);
      ComposerKt.sourceInformation(var7, "C(GeneratePreview)P(3,1,2)206@9704L1599,206@9690L1613:NumericRatingQuestion.kt#hdztyp");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var7.changed(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var7.changed(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var7.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1397971036, var6, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.GeneratePreview (NumericRatingQuestion.kt:205)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-2103292486, true, new NumericRatingQuestionKt$GeneratePreview$1(var2, var0, var1, var3), var7, 54),
            var7,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var7.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1476_0 = var0;
         final int recoveredExpressionCapture_1476_1 = var1;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType recoveredExpressionCapture_1476_2 = var2;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredExpressionCapture_1476_3 = var3;
         final int recoveredExpressionCapture_1476_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1476_0, recoveredExpressionArgument_1476_1) -> io.intercom.android.sdk.survey.ui.questiontype.numericscale.NumericRatingQuestionKt.GeneratePreview$lambda$17(recoveredExpressionCapture_1476_0, recoveredExpressionCapture_1476_1, recoveredExpressionCapture_1476_2, recoveredExpressionCapture_1476_3, recoveredExpressionCapture_1476_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1476_0), ((java.lang.Integer)recoveredExpressionArgument_1476_1).intValue())));
      }
   }

   private static final Unit NumericRatingQuestion$lambda$12$lambda$11$lambda$4$lambda$3$lambda$2$lambda$1$lambda$0(Function1 var0, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption var1) {
      var0.invoke(new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer(String.valueOf(var1.getValue())));
      return Unit.INSTANCE;
   }

   private static final Unit NumericRatingQuestion$lambda$12$lambda$11$lambda$8$lambda$7$lambda$6$lambda$5(io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption var0, Function1 var1) {
      String var2 = String.valueOf(var0.getValue());
      var1.invoke(new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer(var2));
      return Unit.INSTANCE;
   }

   private static final Unit NumericRatingQuestion$lambda$13(
      Modifier var0,
      io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel var1,
      Answer var2,
      Function1 var3,
      SurveyUiColors var4,
      Function2 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      NumericRatingQuestion(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit NPSQuestionPreview$lambda$14(int var0, Composer var1, int var2) {
      NPSQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit StarQuestionPreview$lambda$15(int var0, Composer var1, int var2) {
      StarQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmojiRatingQuestionPreview$lambda$16(int var0, Composer var1, int var2) {
      EmojiRatingQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GeneratePreview$lambda$17(int var0, int var1, io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType var2, Answer var3, int var4, Composer var5, int var6) {
      GeneratePreview(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }
}
