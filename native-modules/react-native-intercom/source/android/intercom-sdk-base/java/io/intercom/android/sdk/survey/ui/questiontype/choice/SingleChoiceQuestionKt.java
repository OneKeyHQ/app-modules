package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.survey.ui.questiontype.DatePickerQuestionKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\u001aZ\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0013\b\u0002\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\u0015\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\u0015\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\u0019¨\u0006\u001a",
   d2 = {
         "SingleChoiceQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "singleChoiceQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$SingleChoiceQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$SingleChoiceQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "getTranslatedOption",
         "",
         "unParsedOption",
         "(Ljava/lang/String;Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "SingleChoiceQuestionPreviewLight",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SingleChoiceQuestionPreviewDark",
         "SingleChoiceQuestionPreview",
         "surveyUiColors",
         "(Lio/intercom/android/sdk/survey/SurveyUiColors;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSingleChoiceQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SingleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/SingleChoiceQuestionKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,145:1\n75#2:146\n70#3:147\n67#3,9:148\n77#3:249\n81#4,6:157\n88#4,6:172\n81#4,6:196\n88#4,6:211\n96#4:244\n96#4:248\n402#5,9:163\n411#5:178\n402#5,9:202\n411#5:217\n412#5,2:242\n412#5,2:246\n1047#6,6:179\n1047#6,6:221\n1047#6,6:230\n1047#6,6:236\n87#7:185\n83#7,10:186\n94#7:245\n1869#8:218\n1870#8:227\n118#9:219\n118#9:220\n118#9:228\n118#9:229\n*S KotlinDebug\n*F\n+ 1 SingleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/SingleChoiceQuestionKt\n*L\n42#1:146\n43#1:147\n43#1:148,9\n43#1:249\n43#1:157,6\n43#1:172,6\n45#1:196,6\n45#1:211,6\n45#1:244\n43#1:248\n43#1:163,9\n43#1:178\n45#1:202,9\n45#1:217\n45#1:242,2\n43#1:246,2\n44#1:179,6\n61#1:221,6\n88#1:230,6\n92#1:236,6\n45#1:185\n45#1:186,10\n45#1:245\n47#1:218\n47#1:227\n48#1:219\n54#1:220\n75#1:228\n81#1:229\n*E\n"
)
public final class SingleChoiceQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void SingleChoiceQuestion(
      @Nullable Modifier var0,
      @NotNull SurveyData$Step$Question$SingleChoiceQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @Nullable Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "singleChoiceQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      var6 = var6.startRestartGroup(-538592394);
      ComposerKt.sourceInformation(var6, "C(SingleChoiceQuestion)P(2,5!1,3)41@1911L7,42@1923L2817:SingleChoiceQuestion.kt#w8l19x");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var8 & 32) != 0) {
            var5 = ComposableSingletons$SingleChoiceQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -538592394, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestion (SingleChoiceQuestion.kt:40)"
            );
         }

         CompositionLocal var11 = CompositionLocalsKt.getLocalFocusManager();
         int var12 = 0;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var6, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var14 = var6.consume(var11);
         ComposerKt.sourceInformationMarkerEnd(var6);
         FocusManager var10 = (FocusManager)var14;
         var13 = 14 & var9;
         int var110 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var6, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var107 = Alignment.Companion.getTopStart();
         boolean var108 = false;
         MeasurePolicy var15 = BoxKt.maybeCachedBoxMeasurePolicy(var107, var108);
         Modifier var16 = var0;
         MeasurePolicy var17 = var15;
         Composer var18 = var6;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         BoxScope var35 = BoxScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, -1417165180, "C43@1990L34,44@2033L2701:SingleChoiceQuestion.kt#w8l19x");
         var34.startReplaceGroup(924114601);
         ComposerKt.sourceInformation(var34, "CC(remember):SingleChoiceQuestion.kt#9igjgp");
         Composer var37 = var34;
         boolean var38 = false;
         int var39 = 0;
         Object var40 = var37.rememberedValue();
         int var41 = 0;
         Object var10000;
         if (var40 == Composer.Companion.getEmpty()) {
            int var42 = 0;
            Object var116 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var37.updateRememberedValue(var116);
            var10000 = var116;
         } else {
            var10000 = var40;
         }

         MutableState var43 = (MutableState)var10000;
         var34.endReplaceGroup();
         MutableState var44 = var43;
         byte var113 = 0;
         var40 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var118 = Modifier.Companion;
         Vertical var111 = Arrangement.INSTANCE.getTop();
         Horizontal var112 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var111, var112, var34, 14 & var113 >> 3 | 112 & var113 >> 3);
         Modifier var117 = var118;
         MeasurePolicy var45 = measurePolicy$ivx;
         Composer var46 = var34;
         int var47 = 112 & var113 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var117);
         Function0 var52 = ComposeUiNode.Companion.getConstructor();
         Composer var53 = var46;
         int var54 = 6 | 896 & var47 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var53, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var53.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var53.startReusableNode();
         if (var53.getInserting()) {
            var53.createNode(var52);
         } else {
            var53.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var53);
         int var57/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var45, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var58 = 14 & var54 >> 6;
         Composer $composer$ivx = var53;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var61 = 6 | 112 & var113 >> 6;
         Composer var62 = $composer$ivx;
         ColumnScope var63 = ColumnScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var62, 1877954063, "C45@2069L8:SingleChoiceQuestion.kt#w8l19x");
         var5.invoke(var62, 14 & var9 >> 15);
         var62.startReplaceGroup(891864023);
         ComposerKt.sourceInformation(var62, "*47@2168L40,52@2507L6,55@2744L22,59@2865L52,60@2951L200,57@2785L622");
         Iterable var65 = var1.getOptions();
         int var66 = 0;

         for (Object var68 : var65) {
            String var69 = (String)var68;
            int var70 = 0;
            Modifier var148 = Modifier.Companion;
            int var71 = 8;
            int var72 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var148, Dp.recovered$95b3d103663bf060(var71)), var62, 6);
            boolean var123 = var2 instanceof Answer$SingleAnswer && Intrinsics.areEqual(((Answer$SingleAnswer)var2).getAnswer(), var69);
            var62.startReplaceGroup(891870283);
            ComposerKt.sourceInformation(var62, "51@2442L6");
            long var73 = var123
               ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
               : IntercomTheme.INSTANCE.getColors(var62, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var62.endReplaceGroup();
            long var75 = var73;
            var73 = Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var62, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
            );
            int $this$dp$ivx = 1;
            int $i$f$getDpx = 0;
            float var79 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            FontWeight var132 = var123 ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal();
            long var80 = Color.recovered$41aa0ee03c2282fb(
               ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var75), DatePickerQuestionKt.contentAlpha(var123, var62, 0), 0.0F, 0.0F, 0.0F, 14, null
            );
            String var82 = getTranslatedOption(var69, var62, 0);
            boolean var149 = var123;
            var62.startReplaceGroup(-201412627);
            ComposerKt.sourceInformation(var62, "CC(remember):SingleChoiceQuestion.kt#9igjgp");
            Composer var83 = var62;
            boolean var84 = var6.changedInstance(var10) | (var9 & 7168) == 2048 | var6.changed(var69);
            int $i$f$cachex = 0;
            Object it$ivx = var83.rememberedValue();
            int var87/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10001;
            if (!var84 && it$ivx != Composer.Companion.getEmpty()) {
               var10001 = it$ivx;
            } else {
               boolean var88 = var123;
               int var89 = 0;
               final androidx.compose.ui.focus.FocusManager recoveredCapture_1408_0 = var10;
               final androidx.compose.runtime.MutableState recoveredCapture_1408_1 = var44;
               final kotlin.jvm.functions.Function1 recoveredCapture_1408_2 = var3;
               final java.lang.String recoveredCapture_1408_3 = var69;
               Function1 var90 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1408_0) -> SingleChoiceQuestionKt.SingleChoiceQuestion$lambda$9$lambda$8$lambda$3$lambda$2$lambda$1(recoveredCapture_1408_0, recoveredCapture_1408_1, recoveredCapture_1408_2, recoveredCapture_1408_3, (java.lang.String)recoveredArgument_1408_0));
               var149 = var88;
               Object var91 = var90;
               var83.updateRememberedValue(var91);
               var10001 = var91;
            }

            Function1 var92 = (Function1)var10001;
            var62.endReplaceGroup();
            ChoicePillKt.recovered$b36a01a674e93ba3(var149, var92, var82, var73, var79, var75, var132, var80, var62, 24576, 0);
         }

         var62.endReplaceGroup();
         var62.startReplaceGroup(891905967);
         ComposerKt.sourceInformation(var62, "74@3497L40,87@4214L171,91@4423L74,83@4005L705");
         if (var1.getIncludeOther()) {
            Modifier var150 = Modifier.Companion;
            int var119 = 8;
            var66 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var150, Dp.recovered$95b3d103663bf060(var119)), var62, 6);
            boolean var120 = (Boolean)var44.getValue();
            var62.startReplaceGroup(891911979);
            ComposerKt.sourceInformation(var62, "78@3745L6");
            long var93 = var120
               ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
               : IntercomTheme.INSTANCE.getColors(var62, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var62.endReplaceGroup();
            long var95 = var93;
            var93 = ColorExtensionsKt.recovered$9902137196ed1ac6(var95);
            float var151;
            if (var120) {
               int $this$dp$ivx = 2;
               int var127 = 0;
               var151 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            } else {
               int $this$dp$ivx = 1;
               int var128 = 0;
               var151 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            }

            float var122 = var151;
            FontWeight var126 = var120 ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal();
            boolean var152 = var120;
            SurveyUiColors var154 = var4;
            String var10002 = var2 instanceof Answer$SingleAnswer ? ((Answer$SingleAnswer)var2).getAnswer() : "";
            var62.startReplaceGroup(891929777);
            ComposerKt.sourceInformation(var62, "CC(remember):SingleChoiceQuestion.kt#9igjgp");
            Composer var97 = var62;
            boolean var98 = (var9 & 7168) == 2048;
            int $i$f$cachex = 0;
            Object it$ivx = var97.rememberedValue();
            int var133/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10003;
            if (!var98 && it$ivx != Composer.Companion.getEmpty()) {
               var10003 = (Function0)it$ivx;
            } else {
               String var101 = var10002;
               SurveyUiColors var102 = var4;
               boolean var103 = var120;
               int var135 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1409_0 = var3;
               final androidx.compose.runtime.MutableState recoveredCapture_1409_1 = var44;
               Function0 var104 = ((kotlin.jvm.functions.Function0) () -> SingleChoiceQuestionKt.SingleChoiceQuestion$lambda$9$lambda$8$lambda$5$lambda$4(recoveredCapture_1409_0, recoveredCapture_1409_1));
               var152 = var103;
               var154 = var102;
               var10002 = var101;
               Object var137 = var104;
               var97.updateRememberedValue(var137);
               var10003 = (Function0)var137;
            }

            Function0 var129 = (kotlin.jvm.functions.Function0) var10003;
            var62.endReplaceGroup();
            var10003 = var129;
            var62.startReplaceGroup(891936368);
            ComposerKt.sourceInformation(var62, "CC(remember):SingleChoiceQuestion.kt#9igjgp");
            var97 = var62;
            var98 = (var9 & 7168) == 2048;
            $i$f$cachex = 0;
            it$ivx = var97.rememberedValue();
            var133/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10004;
            if (!var98 && it$ivx != Composer.Companion.getEmpty()) {
               var10004 = it$ivx;
            } else {
               Function0 var147 = var129;
               String var144 = var10002;
               SurveyUiColors var145 = var154;
               boolean var146 = var152;
               int var136 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1410_0 = var3;
               Function1 var105 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1410_0) -> SingleChoiceQuestionKt.SingleChoiceQuestion$lambda$9$lambda$8$lambda$7$lambda$6(recoveredCapture_1410_0, (java.lang.String)recoveredArgument_1410_0));
               var152 = var146;
               var154 = var145;
               var10002 = var144;
               var10003 = var147;
               Object var138 = var105;
               var97.updateRememberedValue(var138);
               var10004 = var138;
            }

            Function1 var130 = (Function1)var10004;
            var62.endReplaceGroup();
            OtherOptionKt.recovered$ceeec0b30e6e12f1(var152, var154, var10002, (kotlin.jvm.functions.Function0) var10003, var130, var93, var122, var95, var126, 0L, var62, 112 & var9 >> 9, 512);
         }

         var62.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var153 = var6.endRestartGroup();
      if (var153 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1411_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel recoveredCapture_1411_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1411_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1411_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1411_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredCapture_1411_5 = var5;
         final int recoveredCapture_1411_6 = var7;
         final int recoveredCapture_1411_7 = var8;
         var153.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1411_0, recoveredArgument_1411_1) -> SingleChoiceQuestionKt.SingleChoiceQuestion$lambda$10(recoveredCapture_1411_0, recoveredCapture_1411_1, recoveredCapture_1411_2, recoveredCapture_1411_3, recoveredCapture_1411_4, recoveredCapture_1411_5, recoveredCapture_1411_6, recoveredCapture_1411_7, (androidx.compose.runtime.Composer)recoveredArgument_1411_0, ((java.lang.Integer)recoveredArgument_1411_1).intValue())));
      }
   }

   @Composable
   private static final String getTranslatedOption(String var0, Composer var1, int var2) {
      var1.startReplaceGroup(-1189227411);
      ComposerKt.sourceInformation(var1, "C(getTranslatedOption):SingleChoiceQuestion.kt#w8l19x");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1189227411, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.getTranslatedOption (SingleChoiceQuestion.kt:105)"
         );
      }

      String var4 = var0;
      String var10000;
      if (Intrinsics.areEqual(var4, "true")) {
         var1.startReplaceGroup(-1210053781);
         ComposerKt.sourceInformation(var1, "106@4852L67");
         String var5 = StringResources_androidKt.stringResource(string.intercom_attribute_collector_positive, var1, 0);
         var1.endReplaceGroup();
         var10000 = var5;
      } else if (Intrinsics.areEqual(var4, "false")) {
         var1.startReplaceGroup(-1210051125);
         ComposerKt.sourceInformation(var1, "107@4935L67");
         String var6 = StringResources_androidKt.stringResource(string.intercom_attribute_collector_negative, var1, 0);
         var1.endReplaceGroup();
         var10000 = var6;
      } else {
         var1.startReplaceGroup(-1210048618);
         var1.endReplaceGroup();
         var10000 = var0;
      }

      String var3 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   @Preview(name = "Light button color", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SingleChoiceQuestionPreviewLight(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1626655857);
      ComposerKt.sourceInformation(var0, "C(SingleChoiceQuestionPreviewLight)114@5160L86:SingleChoiceQuestion.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1626655857, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestionPreviewLight (SingleChoiceQuestion.kt:113)"
            );
         }

         SingleChoiceQuestionPreview(SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), var0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1414_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1414_0, recoveredArgument_1414_1) -> SingleChoiceQuestionKt.SingleChoiceQuestionPreviewLight$lambda$11(recoveredCapture_1414_0, (androidx.compose.runtime.Composer)recoveredArgument_1414_0, ((java.lang.Integer)recoveredArgument_1414_1).intValue())));
      }
   }

   @Preview(name = "Dark button color", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SingleChoiceQuestionPreviewDark(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(567326043);
      ComposerKt.sourceInformation(var0, "C(SingleChoiceQuestionPreviewDark)120@5375L127:SingleChoiceQuestion.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               567326043, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestionPreviewDark (SingleChoiceQuestion.kt:119)"
            );
         }

         SingleChoiceQuestionPreview(
            SurveyUiColors.recovered$4e1a9ef58cd7c1b4(
               SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)),
               0L,
               0L,
               Color.Companion.recovered$ff90a8751967f2f7(),
               0L,
               null,
               27,
               null
            ),
            var0,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1413_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1413_0, recoveredArgument_1413_1) -> SingleChoiceQuestionKt.SingleChoiceQuestionPreviewDark$lambda$12(recoveredCapture_1413_0, (androidx.compose.runtime.Composer)recoveredArgument_1413_0, ((java.lang.Integer)recoveredArgument_1413_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SingleChoiceQuestionPreview(@NotNull SurveyUiColors var0, @Nullable Composer var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "surveyUiColors");
      var1 = var1.startRestartGroup(1547860655);
      ComposerKt.sourceInformation(var1, "C(SingleChoiceQuestionPreview)127@5611L570,127@5597L584:SingleChoiceQuestion.kt#w8l19x");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1547860655, var3, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestionPreview (SingleChoiceQuestion.kt:126)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1452787289, true, new SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1(var0), var1, 54),
            var1,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1412_0 = var0;
         final int recoveredCapture_1412_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1412_0, recoveredArgument_1412_1) -> SingleChoiceQuestionKt.SingleChoiceQuestionPreview$lambda$13(recoveredCapture_1412_0, recoveredCapture_1412_1, (androidx.compose.runtime.Composer)recoveredArgument_1412_0, ((java.lang.Integer)recoveredArgument_1412_1).intValue())));
      }
   }

   private static final Unit SingleChoiceQuestion$lambda$9$lambda$8$lambda$3$lambda$2$lambda$1(
      FocusManager var0, MutableState var1, Function1 var2, String var3, String var4
   ) {
      Intrinsics.checkNotNullParameter(var4, "it");
      FocusManager.clearFocus$default(var0, false, 1, null);
      var1.setValue(false);
      var2.invoke(new Answer$SingleAnswer(var3));
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestion$lambda$9$lambda$8$lambda$5$lambda$4(Function1 var0, MutableState var1) {
      var0.invoke(Answer$NoAnswer$ResetNoAnswer.INSTANCE);
      var1.setValue(!(Boolean)var1.getValue());
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestion$lambda$9$lambda$8$lambda$7$lambda$6(Function1 var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke(new Answer$SingleAnswer(var1));
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestion$lambda$10(
      Modifier var0,
      SurveyData$Step$Question$SingleChoiceQuestionModel var1,
      Answer var2,
      Function1 var3,
      SurveyUiColors var4,
      Function2 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      SingleChoiceQuestion(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestionPreviewLight$lambda$11(int var0, Composer var1, int var2) {
      SingleChoiceQuestionPreviewLight(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestionPreviewDark$lambda$12(int var0, Composer var1, int var2) {
      SingleChoiceQuestionPreviewDark(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SingleChoiceQuestionPreview$lambda$13(SurveyUiColors var0, int var1, Composer var2, int var3) {
      SingleChoiceQuestionPreview(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
