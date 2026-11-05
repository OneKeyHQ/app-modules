package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.ValidationError;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
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
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a}\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0019\b\u0002\u0010\u000e\u001a\u0013\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\t¢\u0006\u0002\b\u00102\u0013\b\u0002\u0010\u0011\u001a\r\u0012\u0004\u0012\u00020\u00010\u0012¢\u0006\u0002\b\u0013H\u0001¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0016¨\u0006\u0017",
   d2 = {
         "LongTextQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "textQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$LongTextQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "validationError",
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "onImeActionNext",
         "Landroidx/compose/foundation/text/KeyboardActionScope;",
         "Lkotlin/ExtensionFunctionType;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$LongTextQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lio/intercom/android/sdk/survey/ValidationError;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "LongTextPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nLongTextQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LongTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/LongTextQuestionKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,103:1\n1047#2,6:104\n1047#2,6:176\n1047#2,6:191\n70#3:110\n67#3,9:111\n77#3:190\n81#4,6:120\n88#4,6:135\n81#4,6:153\n88#4,6:168\n96#4:185\n96#4:189\n402#5,9:126\n411#5:141\n402#5,9:159\n411#5:174\n412#5,2:183\n412#5,2:187\n87#6:142\n83#6,10:143\n94#6:186\n118#7:175\n118#7:182\n*S KotlinDebug\n*F\n+ 1 LongTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/LongTextQuestionKt\n*L\n40#1:104,6\n55#1:176,6\n98#1:191,6\n43#1:110\n43#1:111,9\n43#1:190\n43#1:120,6\n43#1:135,6\n44#1:153,6\n44#1:168,6\n44#1:185\n43#1:189\n43#1:126,9\n43#1:141\n44#1:159,9\n44#1:174\n44#1:183,2\n43#1:187,2\n44#1:142\n44#1:143,10\n44#1:186\n46#1:175\n77#1:182\n*E\n"
)
public final class LongTextQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void LongTextQuestion(
      @Nullable Modifier var0,
      @NotNull SurveyData$Step$Question$LongTextQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @NotNull ValidationError var5,
      @Nullable Function1 var6,
      @Nullable Function2 var7,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var1, "textQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      Intrinsics.checkNotNullParameter(var5, "validationError");
      var8 = var8.startRestartGroup(977241530);
      ComposerKt.sourceInformation(var8, "C(LongTextQuestion)P(2,6!1,3!1,7)39@1864L2,42@1925L1711:LongTextQuestion.kt#z2rnlt");
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var8.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var8.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var8.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var8.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var8.changed(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var8.changed(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var8.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var8.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         if ((var10 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var10 & 4) != 0) {
            var2 = Answer$NoAnswer$InitialNoAnswer.INSTANCE;
         }

         if ((var10 & 64) != 0) {
            var8.startReplaceGroup(-652200194);
            ComposerKt.sourceInformation(var8, "CC(remember):LongTextQuestion.kt#9igjgp");
            Composer var13 = var8;
            boolean var14 = false;
            int var15 = 0;
            Object var16 = var13.rememberedValue();
            int var17 = 0;
            Object var10000;
            if (var16 == Composer.Companion.getEmpty()) {
               int var18 = 0;
               Object var95 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1500_0) -> LongTextQuestionKt.LongTextQuestion$lambda$1$lambda$0((androidx.compose.foundation.text.KeyboardActionScope)recoveredArgument_1500_0));
               var13.updateRememberedValue(var95);
               var10000 = var95;
            } else {
               var10000 = var16;
            }

            Function1 var12 = (Function1)var10000;
            var8.endReplaceGroup();
            var6 = var12;
         }

         if ((var10 & 128) != 0) {
            var7 = ComposableSingletons$LongTextQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(977241530, var11, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.LongTextQuestion (LongTextQuestion.kt:41)");
         }

         int var91 = 14 & var11;
         int var92 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var8, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var89 = Alignment.Companion.getTopStart();
         boolean var90 = false;
         MeasurePolicy var93 = BoxKt.maybeCachedBoxMeasurePolicy(var89, var90);
         Modifier var94 = var0;
         MeasurePolicy var96 = var93;
         Composer var19 = var8;
         int var20 = 112 & var91 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var94);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var96, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var34 = 6 | 112 & var91 >> 6;
         Composer var35 = var32;
         BoxScope var36 = BoxScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -1802125662, "C43@1960L1670:LongTextQuestion.kt#z2rnlt");
         int var38 = 0;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var40 = Modifier.Companion;
         Vertical var41 = Arrangement.INSTANCE.getTop();
         Horizontal var42 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var41, var42, var35, 14 & var38 >> 3 | 112 & var38 >> 3);
         Modifier var44 = var40;
         MeasurePolicy var45 = measurePolicy$ivx;
         Composer var46 = var35;
         int var47 = 112 & var38 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var44);
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
         int var61 = 6 | 112 & var38 >> 6;
         Composer var62 = $composer$ivx;
         ColumnScope var63 = ColumnScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var62, 1742805666, "C44@1996L8,45@2017L40,54@2479L221,51@2311L890:LongTextQuestion.kt#z2rnlt");
         var7.invoke(var62, 14 & var11 >> 21);
         Modifier var104 = Modifier.Companion;
         int var65 = 8;
         int var66 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var104, Dp.recovered$95b3d103663bf060(var65)), var62, 6);
         var62.startReplaceGroup(-497967796);
         ComposerKt.sourceInformation(var62, "47@2158L59");
         String var98 = var1.getPlaceHolderStringRes() != null
            ? StringResources_androidKt.stringResource(var1.getPlaceHolderStringRes(), var62, 0)
            : var1.getPlaceholder();
         var62.endReplaceGroup();
         String var97 = var98;
         String var99 = var2 instanceof Answer$SingleAnswer ? ((Answer$SingleAnswer)var2).getAnswer() : "";
         Color var67 = var5 instanceof ValidationError$ValidationStringError && !(var2 instanceof Answer$NoAnswer$InitialNoAnswer)
            ? Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$176187de5f63aca7())
            : null;
         long var68 = ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8());
         int var70 = ImeAction.Companion.recovered$fdcb0d48f452258d();
         int var71 = var1.getMaxLine();
         float var72 = var1.recovered$36803c329f71b4de();
         String var105 = var99;
         String var10001 = var97;
         var62.startReplaceGroup(-497955273);
         ComposerKt.sourceInformation(var62, "CC(remember):LongTextQuestion.kt#9igjgp");
         Composer var73 = var62;
         boolean var74 = (var11 & 7168) == 2048;
         int var75 = 0;
         Object var76 = var73.rememberedValue();
         int var77 = 0;
         Object var10002;
         if (!var74 && var76 != Composer.Companion.getEmpty()) {
            var10002 = var76;
         } else {
            String var78 = var97;
            String var79 = var99;
            int var80 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_1498_0 = var3;
            Function1 var81 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1498_0) -> LongTextQuestionKt.LongTextQuestion$lambda$5$lambda$4$lambda$3$lambda$2(recoveredCapture_1498_0, (java.lang.String)recoveredArgument_1498_0));
            var105 = var79;
            var10001 = var78;
            Object var82 = var81;
            var73.updateRememberedValue(var82);
            var10002 = var82;
         }

         Function1 var83 = (Function1)var10002;
         var62.endReplaceGroup();
         TextInputPillKt.recovered$1e363f8e6e037ae9(
            var105, var10001, var83, var67, var68, var71, false, var6, 0, var70, false, null, false, var72, var62, 805306368 | 29360128 & var11 << 3, 6, 6464
         );
         var62.startReplaceGroup(-497931568);
         ComposerKt.sourceInformation(var62, "70@3278L328");
         if (var1.getCharacterLimit() != null) {
            String var100 = "" + var2.getLength() + '/' + var1.getCharacterLimit();
            long var84 = TextUnitKt.getSp(14);
            long var86 = ColorKt.Color(4285756278L);
            var104 = var63.align(Modifier.Companion, Alignment.Companion.getEnd());
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            Modifier var101 = PaddingKt.recovered$03b481d31d62885a(var104, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 0.0F, 13, null);
            TextKt.recovered$5b114ab32a4f4190(
               var100, var101, var86, null, var84, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var62, 24960, 0, 262120
            );
         }

         var62.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var107 = var8.endRestartGroup();
      if (var107 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1499_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel recoveredCapture_1499_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1499_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1499_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1499_4 = var4;
         final io.intercom.android.sdk.survey.ValidationError recoveredCapture_1499_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_1499_6 = var6;
         final kotlin.jvm.functions.Function2 recoveredCapture_1499_7 = var7;
         final int recoveredCapture_1499_8 = var9;
         final int recoveredCapture_1499_9 = var10;
         var107.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1499_0, recoveredArgument_1499_1) -> LongTextQuestionKt.LongTextQuestion$lambda$6(recoveredCapture_1499_0, recoveredCapture_1499_1, recoveredCapture_1499_2, recoveredCapture_1499_3, recoveredCapture_1499_4, recoveredCapture_1499_5, recoveredCapture_1499_6, recoveredCapture_1499_7, recoveredCapture_1499_8, recoveredCapture_1499_9, (androidx.compose.runtime.Composer)recoveredArgument_1499_0, ((java.lang.Integer)recoveredArgument_1499_1).intValue())));
      }
   }

   @Preview(name = "LongTextQuestion", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void LongTextPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-479343201);
      ComposerKt.sourceInformation(var0, "C(LongTextPreview)97@4180L2,86@3746L563:LongTextQuestion.kt#z2rnlt");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-479343201, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.LongTextPreview (LongTextQuestion.kt:85)");
         }

         Object var10000 = null;
         String var10001 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var2 = var10001;
         List var3 = CollectionsKt.listOf(new Block$Builder().withText("Is this a preview?"));
         SurveyData$Step$Question$QuestionValidation$ValidationType var4 = SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION;
         SurveyData$Step$Question$LongTextQuestionModel var19 = new SurveyData$Step$Question$LongTextQuestionModel(
            var2, var3, true, "Placeholder text", var4, 2000, 0.0F, 0, null, 448, null
         );
         Object var10002 = null;
         var0.startReplaceGroup(1067245044);
         ComposerKt.sourceInformation(var0, "CC(remember):LongTextQuestion.kt#9igjgp");
         Composer var15 = var0;
         boolean var16 = false;
         int var5 = 0;
         Object var6 = var15.rememberedValue();
         int var7 = 0;
         Object var10003;
         if (var6 == Composer.Companion.getEmpty()) {
            Object var11 = null;
            SurveyData$Step$Question$LongTextQuestionModel var10 = var19;
            Object var9 = null;
            int var8 = 0;
            Function1 var12 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1496_0) -> LongTextQuestionKt.LongTextPreview$lambda$8$lambda$7((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1496_0));
            var10000 = var9;
            var19 = var10;
            var10002 = var11;
            Object var17 = var12;
            var15.updateRememberedValue(var17);
            var10003 = var17;
         } else {
            var10003 = var6;
         }

         Function1 var14 = (Function1)var10003;
         var0.endReplaceGroup();
         LongTextQuestion(
            (Modifier)var10000,
            var19,
            (Answer)var10002,
            var14,
            SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)),
            ValidationError$NoValidationError.INSTANCE,
            null,
            null,
            var0,
            199680,
            197
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var18 = var0.endRestartGroup();
      if (var18 != null) {
         final int recoveredCapture_1497_0 = var1;
         var18.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1497_0, recoveredArgument_1497_1) -> LongTextQuestionKt.LongTextPreview$lambda$9(recoveredCapture_1497_0, (androidx.compose.runtime.Composer)recoveredArgument_1497_0, ((java.lang.Integer)recoveredArgument_1497_1).intValue())));
      }
   }

   private static final Unit LongTextQuestion$lambda$1$lambda$0(KeyboardActionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return Unit.INSTANCE;
   }

   private static final Unit LongTextQuestion$lambda$5$lambda$4$lambda$3$lambda$2(Function1 var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      if (var1.length() > 0) {
         var0.invoke(new Answer$SingleAnswer(var1));
      } else {
         var0.invoke(Answer$NoAnswer$ResetNoAnswer.INSTANCE);
      }

      return Unit.INSTANCE;
   }

   private static final Unit LongTextQuestion$lambda$6(
      Modifier var0,
      SurveyData$Step$Question$LongTextQuestionModel var1,
      Answer var2,
      Function1 var3,
      SurveyUiColors var4,
      ValidationError var5,
      Function1 var6,
      Function2 var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      LongTextQuestion(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit LongTextPreview$lambda$8$lambda$7(Answer var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit LongTextPreview$lambda$9(int var0, Composer var1, int var2) {
      LongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
