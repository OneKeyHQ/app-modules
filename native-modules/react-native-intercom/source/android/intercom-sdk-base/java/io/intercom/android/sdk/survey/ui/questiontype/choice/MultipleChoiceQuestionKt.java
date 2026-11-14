package io.intercom.android.sdk.survey.ui.questiontype.choice;

import android.content.Context;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$MultipleChoiceQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer$OtherAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer$OtherAnswer$NotSelected;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer$OtherAnswer$SelectedNoText;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer$OtherAnswer$SelectedWithText;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.SetsKt;
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
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001aZ\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0013\b\u0002\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0011\u001a\u0015\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\u0015¨\u0006\u0016",
   d2 = {
         "MultipleChoiceQuestion",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "multipleChoiceQuestionModel",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$MultipleChoiceQuestionModel;",
         "answer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "onAnswer",
         "Lkotlin/Function1;",
         "colors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "questionHeader",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$MultipleChoiceQuestionModel;Lio/intercom/android/sdk/survey/ui/models/Answer;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "MultipleChoiceQuestionPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "MultipleChoiceQuestionPreviewDark",
         "PreviewQuestion",
         "surveyUiColors",
         "(Lio/intercom/android/sdk/survey/SurveyUiColors;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMultipleChoiceQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/MultipleChoiceQuestionKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,189:1\n70#2:190\n67#2,9:191\n77#2:289\n81#3,6:200\n88#3,6:215\n81#3,6:233\n88#3,6:248\n96#3:284\n96#3:288\n402#4,9:206\n411#4:221\n402#4,9:239\n411#4:254\n412#4,2:282\n412#4,2:286\n87#5:222\n83#5,10:223\n94#5:285\n1869#6:255\n1870#6:264\n118#7:256\n118#7:257\n118#7:265\n118#7:266\n118#7:280\n118#7:281\n1047#8,6:258\n1047#8,6:267\n1047#8,6:273\n75#9:279\n*S KotlinDebug\n*F\n+ 1 MultipleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/MultipleChoiceQuestionKt\n*L\n41#1:190\n41#1:191,9\n41#1:289\n41#1:200,6\n41#1:215,6\n42#1:233,6\n42#1:248,6\n42#1:284\n41#1:288\n41#1:206,9\n41#1:221\n42#1:239,9\n42#1:254\n42#1:282,2\n41#1:286,2\n42#1:222\n42#1:223,10\n42#1:285\n44#1:255\n44#1:264\n50#1:256\n55#1:257\n97#1:265\n101#1:266\n132#1:280\n140#1:281\n65#1:258,6\n108#1:267,6\n115#1:273,6\n127#1:279\n*E\n"
)
public final class MultipleChoiceQuestionKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void MultipleChoiceQuestion(
      @Nullable Modifier var0,
      @NotNull SurveyData$Step$Question$MultipleChoiceQuestionModel var1,
      @Nullable Answer var2,
      @NotNull Function1 var3,
      @NotNull SurveyUiColors var4,
      @Nullable Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "multipleChoiceQuestionModel");
      Intrinsics.checkNotNullParameter(var3, "onAnswer");
      Intrinsics.checkNotNullParameter(var4, "colors");
      var6 = var6.startRestartGroup(928787358);
      ComposerKt.sourceInformation(var6, "C(MultipleChoiceQuestion)P(2,3!1,4)40@1808L4462:MultipleChoiceQuestion.kt#w8l19x");
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
            var5 = ComposableSingletons$MultipleChoiceQuestionKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               928787358, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.MultipleChoiceQuestion (MultipleChoiceQuestion.kt:39)"
            );
         }

         int var12 = 14 & var9;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var6, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var10 = Alignment.Companion.getTopStart();
         boolean var11 = false;
         MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var10, var11);
         Modifier var15 = var0;
         MeasurePolicy var16 = var14;
         Composer var17 = var6;
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
         ComposerKt.sourceInformationMarkerStart(var33, 644871567, "C41@1843L4421:MultipleChoiceQuestion.kt#w8l19x");
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
         ComposerKt.sourceInformationMarkerStart(var60, -1833832561, "C42@1879L8,139@6214L40:MultipleChoiceQuestion.kt#w8l19x");
         var5.invoke(var60, 14 & var9 >> 15);
         var60.startReplaceGroup(1187769307);
         ComposerKt.sourceInformation(var60, "*49@2156L40,64@2915L281,57@2601L614");
         Iterable var63 = var1.getOptions();
         int var64 = 0;

         for (Object var66 : var63) {
            String var67 = (String)var66;
            int var68 = 0;
            boolean var69 = var2 instanceof Answer$MultipleAnswer ? ((Answer$MultipleAnswer)var2).recoveredReturn$bf3409efca2cfb1e().contains(var67) : false;
            Modifier var10000 = Modifier.Companion;
            int var70 = 8;
            int var71 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var70)), var60, 6);
            var60.startReplaceGroup(1187778355);
            ComposerKt.sourceInformation(var60, "52@2341L6");
            long var72 = var69
               ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
               : IntercomTheme.INSTANCE.getColors(var60, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var60.endReplaceGroup();
            long var74 = var72;
            var72 = ColorExtensionsKt.recovered$9902137196ed1ac6(var74);
            float var150;
            if (var69) {
               int $this$dp$ivx = 2;
               int $i$f$getDpx = 0;
               var150 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            } else {
               int $this$dp$ivx = 1;
               int $i$f$getDpx = 0;
               var150 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            }

            float var78 = var150;
            FontWeight var134 = var69 ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal();
            boolean var151 = var69;
            var60.startReplaceGroup(-1655251130);
            ComposerKt.sourceInformation(var60, "CC(remember):MultipleChoiceQuestion.kt#9igjgp");
            Composer var79 = var60;
            boolean var80 = (var9 & 896) == 256 | (var9 & 7168) == 2048 | var6.changed(var67);
            int var81 = 0;
            Object var82 = var79.rememberedValue();
            int var83 = 0;
            Object var10001;
            if (!var80 && var82 != Composer.Companion.getEmpty()) {
               var10001 = var82;
            } else {
               boolean var84 = var69;
               int var85 = 0;
               final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1400_0 = var2;
               final kotlin.jvm.functions.Function1 recoveredCapture_1400_1 = var3;
               final java.lang.String recoveredCapture_1400_2 = var67;
               Function1 var86 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1400_0) -> MultipleChoiceQuestionKt.MultipleChoiceQuestion$lambda$8$lambda$7$lambda$2$lambda$1$lambda$0(recoveredCapture_1400_0, recoveredCapture_1400_1, recoveredCapture_1400_2, (java.lang.String)recoveredArgument_1400_0));
               var151 = var84;
               Object var87 = var86;
               var79.updateRememberedValue(var87);
               var10001 = var87;
            }

            Function1 var138 = (Function1)var10001;
            var60.endReplaceGroup();
            ChoicePillKt.recovered$b36a01a674e93ba3(var151, var138, var67, var72, var78, var74, var134, 0L, var60, 0, 128);
         }

         var60.endReplaceGroup();
         var60.startReplaceGroup(1187812082);
         ComposerKt.sourceInformation(var60, "96@4099L40,107@4798L310,114@5146L114,103@4564L909");
         if (var1.getIncludeOther()) {
            boolean var106 = var2 instanceof Answer$MultipleAnswer
               ? !Intrinsics.areEqual(((Answer$MultipleAnswer)var2).getOtherAnswer(), Answer$MultipleAnswer$OtherAnswer$NotSelected.INSTANCE)
               : false;
            Modifier var152 = Modifier.Companion;
            var64 = 8;
            int var113 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var152, Dp.recovered$95b3d103663bf060(var64)), var60, 6);
            var60.startReplaceGroup(1187840506);
            ComposerKt.sourceInformation(var60, "98@4290L6");
            long var88 = var106
               ? ColorExtensionsKt.recovered$93702c38c1546c4a(var4.recovered$923dd289d74a1df8())
               : IntercomTheme.INSTANCE.getColors(var60, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var60.endReplaceGroup();
            long var90 = var88;
            var88 = ColorExtensionsKt.recovered$9902137196ed1ac6(var90);
            float var153;
            if (var106) {
               int var122 = 2;
               int $i$f$getDpx = 0;
               var153 = Dp.recovered$95b3d103663bf060(var122);
            } else {
               int var123 = 1;
               int $i$f$getDpx = 0;
               var153 = Dp.recovered$95b3d103663bf060(var123);
            }

            float var121 = var153;
            FontWeight var124 = var106 ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal();
            boolean var154 = var106;
            SurveyUiColors var158 = var4;
            String var10002 = var2 instanceof Answer$MultipleAnswer ? ((Answer$MultipleAnswer)var2).getOtherAnswer().toString() : "";
            var60.startReplaceGroup(1187859908);
            ComposerKt.sourceInformation(var60, "CC(remember):MultipleChoiceQuestion.kt#9igjgp");
            Composer var129 = var60;
            boolean var92 = var6.changed(var106) | (var9 & 896) == 256 | (var9 & 7168) == 2048;
            int var93 = 0;
            Object var94 = var129.rememberedValue();
            int var95 = 0;
            Object var10003;
            if (!var92 && var94 != Composer.Companion.getEmpty()) {
               var10003 = (Function0)var94;
            } else {
               String var96 = var10002;
               SurveyUiColors var97 = var4;
               boolean var98 = var106;
               int var135 = 0;
               final boolean recoveredCapture_1397_0 = var106;
               final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1397_1 = var2;
               final kotlin.jvm.functions.Function1 recoveredCapture_1397_2 = var3;
               Function0 var99 = ((kotlin.jvm.functions.Function0) () -> MultipleChoiceQuestionKt.MultipleChoiceQuestion$lambda$8$lambda$7$lambda$4$lambda$3(recoveredCapture_1397_0, recoveredCapture_1397_1, recoveredCapture_1397_2));
               var154 = var98;
               var158 = var97;
               var10002 = var96;
               Object var139 = var99;
               var129.updateRememberedValue(var139);
               var10003 = (Function0)var139;
            }

            Function0 var127 = (kotlin.jvm.functions.Function0) var10003;
            var60.endReplaceGroup();
            var10003 = var127;
            var60.startReplaceGroup(1187870848);
            ComposerKt.sourceInformation(var60, "CC(remember):MultipleChoiceQuestion.kt#9igjgp");
            var129 = var60;
            var92 = (var9 & 896) == 256 | (var9 & 7168) == 2048;
            var93 = 0;
            var94 = var129.rememberedValue();
            var95 = 0;
            Object var10004;
            if (!var92 && var94 != Composer.Companion.getEmpty()) {
               var10004 = var94;
            } else {
               Function0 var149 = var127;
               String var146 = var10002;
               SurveyUiColors var147 = var158;
               boolean var148 = var154;
               int var136 = 0;
               final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1398_0 = var2;
               final kotlin.jvm.functions.Function1 recoveredCapture_1398_1 = var3;
               Function1 var100 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1398_0) -> MultipleChoiceQuestionKt.MultipleChoiceQuestion$lambda$8$lambda$7$lambda$6$lambda$5(recoveredCapture_1398_0, recoveredCapture_1398_1, (java.lang.String)recoveredArgument_1398_0));
               var154 = var148;
               var158 = var147;
               var10002 = var146;
               var10003 = var149;
               Object var140 = var100;
               var129.updateRememberedValue(var140);
               var10004 = var140;
            }

            Function1 var128 = (Function1)var10004;
            var60.endReplaceGroup();
            OtherOptionKt.recovered$ceeec0b30e6e12f1(var154, var158, var10002, (kotlin.jvm.functions.Function0) var10003, var128, var88, var121, var90, var124, 0L, var60, 112 & var9 >> 9, 512);
         }

         var60.endReplaceGroup();
         var60.startReplaceGroup(1187882794);
         ComposerKt.sourceInformation(var60, "126@5635L7,133@6021L10,130@5850L337");
         if (var1.getMinSelection() > 1) {
            CompositionLocal var110 = AndroidCompositionLocals_androidKt.getLocalContext();
            int $changed$ivx = 0;
            int var117 = 0;
            ComposerKt.sourceInformationMarkerStart(var60, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var120 = var60.consume(var110);
            ComposerKt.sourceInformationMarkerEnd(var60);
            Phrase var107 = Phrase.from((Context)var120, string.intercom_surveys_multi_select_too_few_responses);
            var107.put("response_count", var1.getMinSelection());
            Modifier var155 = Modifier.Companion;
            $changed$ivx = 8;
            var117 = 0;
            Modifier var111 = PaddingKt.recovered$03b481d31d62885a(var155, 0.0F, Dp.recovered$95b3d103663bf060($changed$ivx), 0.0F, 0.0F, 13, null);
            String var116 = var107.format().toString();
            TextStyle var119 = IntercomTheme.INSTANCE.getTypography(var60, IntercomTheme.$stable).getType05();
            long var101 = Color.Companion.recovered$e94007a61f9d70c2();
            long var103 = TextUnitKt.getSp(11);
            FontWeight var131 = FontWeight.Companion.getNormal();
            TextKt.recovered$5b114ab32a4f4190(
               var116, var111, var101, null, var103, null, var131, null, 0L, null, null, 0L, 0, false, 0, 0, null, var119, var60, 1597872, 0, 130984
            );
         }

         var60.endReplaceGroup();
         Modifier var156 = Modifier.Companion;
         int var108 = 8;
         var64 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var156, Dp.recovered$95b3d103663bf060(var108)), var60, 6);
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
         ComposerKt.sourceInformationMarkerEnd(var6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var157 = var6.endRestartGroup();
      if (var157 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1399_0 = var0;
         final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$MultipleChoiceQuestionModel recoveredCapture_1399_1 = var1;
         final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1399_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1399_3 = var3;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1399_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredCapture_1399_5 = var5;
         final int recoveredCapture_1399_6 = var7;
         final int recoveredCapture_1399_7 = var8;
         var157.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1399_0, recoveredArgument_1399_1) -> MultipleChoiceQuestionKt.MultipleChoiceQuestion$lambda$9(recoveredCapture_1399_0, recoveredCapture_1399_1, recoveredCapture_1399_2, recoveredCapture_1399_3, recoveredCapture_1399_4, recoveredCapture_1399_5, recoveredCapture_1399_6, recoveredCapture_1399_7, (androidx.compose.runtime.Composer)recoveredArgument_1399_0, ((java.lang.Integer)recoveredArgument_1399_1).intValue())));
      }
   }

   @Preview(name = "Multiple choice question", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MultipleChoiceQuestionPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1537454351);
      ComposerKt.sourceInformation(var0, "C(MultipleChoiceQuestionPreview)147@6402L74:MultipleChoiceQuestion.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1537454351, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.MultipleChoiceQuestionPreview (MultipleChoiceQuestion.kt:146)"
            );
         }

         PreviewQuestion(SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), var0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1401_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1401_0, recoveredArgument_1401_1) -> MultipleChoiceQuestionKt.MultipleChoiceQuestionPreview$lambda$10(recoveredCapture_1401_0, (androidx.compose.runtime.Composer)recoveredArgument_1401_0, ((java.lang.Integer)recoveredArgument_1401_1).intValue())));
      }
   }

   @Preview(name = "Multiple choice question dark", showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MultipleChoiceQuestionPreviewDark(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(756027931);
      ComposerKt.sourceInformation(var0, "C(MultipleChoiceQuestionPreviewDark)153@6617L115:MultipleChoiceQuestion.kt#w8l19x");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               756027931, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.MultipleChoiceQuestionPreviewDark (MultipleChoiceQuestion.kt:152)"
            );
         }

         PreviewQuestion(
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
         final int recoveredCapture_1402_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1402_0, recoveredArgument_1402_1) -> MultipleChoiceQuestionKt.MultipleChoiceQuestionPreviewDark$lambda$11(recoveredCapture_1402_0, (androidx.compose.runtime.Composer)recoveredArgument_1402_0, ((java.lang.Integer)recoveredArgument_1402_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewQuestion(@NotNull SurveyUiColors var0, @Nullable Composer var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "surveyUiColors");
      var1 = var1.startRestartGroup(-1753720526);
      ComposerKt.sourceInformation(var1, "C(PreviewQuestion)160@6829L890,160@6815L904:MultipleChoiceQuestion.kt#w8l19x");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1753720526, var3, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.PreviewQuestion (MultipleChoiceQuestion.kt:159)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-245477028, true, new MultipleChoiceQuestionKt$PreviewQuestion$1(var0), var1, 54),
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
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1403_0 = var0;
         final int recoveredCapture_1403_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1403_0, recoveredArgument_1403_1) -> MultipleChoiceQuestionKt.PreviewQuestion$lambda$12(recoveredCapture_1403_0, recoveredCapture_1403_1, (androidx.compose.runtime.Composer)recoveredArgument_1403_0, ((java.lang.Integer)recoveredArgument_1403_1).intValue())));
      }
   }

   private static final Unit MultipleChoiceQuestion$lambda$8$lambda$7$lambda$2$lambda$1$lambda$0(Answer var0, Function1 var1, String var2, String var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      if (var0 instanceof Answer$MultipleAnswer) {
         var1.invoke(((Answer$MultipleAnswer)var0).copyWithAnswerToggled(var2));
      } else {
         var1.invoke(new Answer$MultipleAnswer(SetsKt.setOf(var2), null, 2, null));
      }

      return Unit.INSTANCE;
   }

   private static final void MultipleChoiceQuestion$lambda$8$lambda$7$switchOtherAnswer(Answer var0, Function1 var1, Answer$MultipleAnswer$OtherAnswer var2) {
      if (var0 instanceof Answer$MultipleAnswer) {
         var1.invoke(((Answer$MultipleAnswer)var0).copyWithOther(var2));
      } else {
         var1.invoke(new Answer$MultipleAnswer(SetsKt.emptySet(), var2));
      }
   }

   private static final Unit MultipleChoiceQuestion$lambda$8$lambda$7$lambda$4$lambda$3(boolean var0, Answer var1, Function1 var2) {
      if (!var0) {
         MultipleChoiceQuestion$lambda$8$lambda$7$switchOtherAnswer(var1, var2, Answer$MultipleAnswer$OtherAnswer$SelectedNoText.INSTANCE);
      } else {
         MultipleChoiceQuestion$lambda$8$lambda$7$switchOtherAnswer(var1, var2, Answer$MultipleAnswer$OtherAnswer$NotSelected.INSTANCE);
      }

      return Unit.INSTANCE;
   }

   private static final Unit MultipleChoiceQuestion$lambda$8$lambda$7$lambda$6$lambda$5(Answer var0, Function1 var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      MultipleChoiceQuestion$lambda$8$lambda$7$switchOtherAnswer(var0, var1, new Answer$MultipleAnswer$OtherAnswer$SelectedWithText(var2));
      return Unit.INSTANCE;
   }

   private static final Unit MultipleChoiceQuestion$lambda$9(
      Modifier var0,
      SurveyData$Step$Question$MultipleChoiceQuestionModel var1,
      Answer var2,
      Function1 var3,
      SurveyUiColors var4,
      Function2 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      MultipleChoiceQuestion(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit MultipleChoiceQuestionPreview$lambda$10(int var0, Composer var1, int var2) {
      MultipleChoiceQuestionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MultipleChoiceQuestionPreviewDark$lambda$11(int var0, Composer var1, int var2) {
      MultipleChoiceQuestionPreviewDark(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewQuestion$lambda$12(SurveyUiColors var0, int var1, Composer var2, int var3) {
      PreviewQuestion(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
