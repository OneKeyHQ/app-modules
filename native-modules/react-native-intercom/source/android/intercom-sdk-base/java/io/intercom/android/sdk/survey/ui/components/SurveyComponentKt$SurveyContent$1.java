package io.intercom.android.sdk.survey.ui.components;

import android.content.Context;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyState$Content;
import io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta;
import io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Custom;
import io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.survey.block.ImageRenderType;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSurveyComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyComponentKt$SurveyContent$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 9 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,367:1\n1047#2,6:368\n1047#2,6:454\n1047#2,6:467\n118#3:374\n118#3:407\n118#3:408\n118#3:410\n118#3:450\n118#3:460\n118#3:466\n118#3:473\n87#4:375\n84#4,9:376\n87#4:412\n84#4,9:413\n94#4:465\n94#4:477\n81#5,6:385\n88#5,6:400\n81#5,6:422\n88#5,6:437\n96#5:464\n96#5:476\n402#6,9:391\n411#6:406\n402#6,9:428\n411#6:443\n412#6,2:462\n412#6,2:474\n52#7:409\n52#7:411\n1563#8:444\n1634#8,3:445\n1869#8,2:448\n1878#8,2:451\n1880#8:461\n75#9:453\n*S KotlinDebug\n*F\n+ 1 SurveyComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyComponentKt$SurveyContent$1\n*L\n144#1:368,6\n194#1:454,6\n211#1:467,6\n152#1:374\n155#1:407\n156#1:408\n158#1:410\n180#1:450\n197#1:460\n204#1:466\n215#1:473\n149#1:375\n149#1:376,9\n160#1:412\n160#1:413,9\n160#1:465\n149#1:477\n149#1:385,6\n149#1:400,6\n160#1:422,6\n160#1:437,6\n160#1:464\n149#1:476\n149#1:391,9\n149#1:406\n160#1:428,9\n160#1:443\n160#1:462,2\n149#1:474,2\n156#1:409\n158#1:411\n162#1:444\n162#1:445,3\n162#1:448,2\n182#1:451,2\n182#1:461\n186#1:453\n*E\n"
)
final class SurveyComponentKt$SurveyContent$1 implements Function3 {
   // $VF: synthetic field
   final io.intercom.android.sdk.survey.SurveyState$Content $state;
   // $VF: synthetic field
   final Function1 $onContinue;
   // $VF: synthetic field
   final CoroutineScope $coroutineScope;
   // $VF: synthetic field
   final Function1 $onSecondaryCtaClicked;
   // $VF: synthetic field
   final Function1 $onAnswerUpdated;

   SurveyComponentKt$SurveyContent$1(io.intercom.android.sdk.survey.SurveyState$Content var1, Function1 var2, CoroutineScope var3, Function1 var4, Function1 var5) {
      this.$state = var1;
      this.$onContinue = var2;
      this.$coroutineScope = var3;
      this.$onSecondaryCtaClicked = var4;
      this.$onAnswerUpdated = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C139@6047L21,143@6192L55,141@6133L125,148@6268L3005:SurveyComponent.kt#bmpoeo");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1844267539, var4, -1, "io.intercom.android.sdk.survey.ui.components.SurveyContent.<anonymous> (SurveyComponent.kt:138)");
         }

         float var5 = var1.recovered$1760a9074021b1b8();
         ScrollState var6 = ScrollKt.rememberScrollState(0, var2, 0, 1);
         String var10000 = "";
         var2.startReplaceGroup(1705502327);
         ComposerKt.sourceInformation(var2, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var8 = var2;
         boolean var9 = var2.changed(var6);
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var170;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var170 = var11;
         } else {
            String var95 = "";
            int var13 = 0;
            Function2 var10001 = new SurveyComponentKt$SurveyContent$1$1$1(var6, null);
            var10000 = var95;
            Object var14 = var10001;
            var8.updateRememberedValue(var14);
            var170 = var14;
         }

         Function2 var7 = (Function2)var170;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var7, var2, 6);
         Modifier var161 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         int var96 = 16;
         int var98 = 0;
         Modifier var97 = ScrollKt.verticalScroll$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var161, Dp.recovered$95b3d103663bf060(var96), 0.0F, 2, null), var6, true, null, false, 12, null
         );
         io.intercom.android.sdk.survey.SurveyState$Content var101 = this.$state;
         Function1 var102 = this.$onContinue;
         CoroutineScope var103 = this.$coroutineScope;
         Function1 var104 = this.$onSecondaryCtaClicked;
         Function1 var105 = this.$onAnswerUpdated;
         int var15 = 0;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var99 = Arrangement.INSTANCE.getTop();
         Horizontal var100 = Alignment.Companion.getStart();
         MeasurePolicy var17 = ColumnKt.columnMeasurePolicy(var99, var100, var2, 14 & var15 >> 3 | 112 & var15 >> 3);
         Modifier var18 = var97;
         MeasurePolicy var19 = var17;
         Composer var20 = var2;
         int var21 = 112 & var15 << 3;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var23 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var20, 0));
         CompositionLocalMap var24 = var20.getCurrentCompositionLocalMap();
         Modifier var25 = ComposedModifierKt.materializeModifier(var20, var18);
         Function0 var26 = ComposeUiNode.Companion.getConstructor();
         Composer var27 = var20;
         int var28 = 6 | 896 & var21 << 6;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var27.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var27.startReusableNode();
         if (var27.getInserting()) {
            var27.createNode(var26);
         } else {
            var27.useNode();
         }

         Composer var30 = Updater.recovered$676aa17b44fd6280(var27);
         int var31 = 0;
         Updater.recovered$89682b199a142880(var30, var19, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var30, var24, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var30, var23, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var30, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var30, var25, ComposeUiNode.Companion.getSetModifier());
         int var32 = 14 & var28 >> 6;
         Composer var33 = var27;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var35 = 6 | 112 & var15 >> 6;
         Composer var36 = var33;
         ColumnScope var37 = ColumnScopeInstance.INSTANCE;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var36, -1000627823, "C154@6460L41,159@6683L1967,203@8664L40,210@9046L30,204@8717L492,214@9222L41:SurveyComponent.kt#bmpoeo"
         );
         Modifier var162 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var162, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var36, 6);
         float var106 = 0.0F;
         int $this$dp$ivxx = 96;
         int $i$f$getDpxx = 0;
         float var109 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         int var116 = 0;
         float var107 = Dp.recovered$95b3d103663bf060(var5 - var109);
         int var110 = var101.getSecondaryCtaActions().size();

         for (int var117 = 0; var117 < var110; var117++) {
            int var43 = 0;
            float var44 = var107;
            int $this$dp$ivxxx = 64;
            int $i$f$getDpxxx = 0;
            float var128 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
            int var131/* $VF was: $i$f$minus-5rwHm24x */ = 0;
            var107 = Dp.recovered$95b3d103663bf060(var44 - var128);
         }

         Modifier var111 = SizeKt.recovered$da9aba2b92020277(Modifier.Companion, 0.0F, var107, 1, null);
         int $changed$ivx = 0;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var36, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, horizontalAlignment$ivx, var36, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3
         );
         Modifier var132 = var111;
         MeasurePolicy var47 = measurePolicy$ivx;
         Composer var48 = var36;
         int var49 = 112 & $changed$ivx << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var48, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var48, 0));
         CompositionLocalMap localMap$iv$ivx = var48.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var48, var132);
         Function0 var54 = ComposeUiNode.Companion.getConstructor();
         Composer var55 = var48;
         int var56 = 6 | 896 & var49 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var55.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var55.startReusableNode();
         if (var55.getInserting()) {
            var55.createNode(var54);
         } else {
            var55.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var55);
         int var59/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var47, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var60 = 14 & var56 >> 6;
         Composer $composer$ivx = var55;
         int var62/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var63 = 6 | 112 & $changed$ivx >> 6;
         Composer var64 = $composer$ivx;
         ColumnScope var65 = ColumnScopeInstance.INSTANCE;
         int var66 = 0;
         ComposerKt.sourceInformationMarkerStart(var64, -93922538, "C179@7629L40:SurveyComponent.kt#bmpoeo");
         var64.startReplaceGroup(412612297);
         ComposerKt.sourceInformation(var64, "*162@6862L731");
         Iterable var67 = var101.getStepTitle();
         int var68 = 0;
         Iterable var69 = var67;
         ArrayList var70 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var67, 10));
         int var71 = 0;

         for (Object var73 : var69) {
            io.intercom.android.sdk.blocks.lib.models.Block$Builder var74 = (io.intercom.android.sdk.blocks.lib.models.Block$Builder)var73;
            Collection var75 = var70;
            int var76 = 0;
            var75.add(var74.build());
         }

         var var135 = (Iterable & List)var70;
         int var138 = 0;

         for (Object var143 : var135) {
            Block var145 = (Block)var143;
            int var147 = 0;
            Modifier var171 = Modifier.Companion;
            BlockAlignment var10002 = var145.getAlign();
            Horizontal var175;
            switch (var10002 == null ? -1 : SurveyComponentKt$SurveyContent$1$WhenMappings.$EnumSwitchMapping$0[var10002.ordinal()]) {
               case 1:
                  var175 = Alignment.Companion.getCenterHorizontally();
                  break;
               case 2:
                  var175 = Alignment.Companion.getEnd();
                  break;
               default:
                  var175 = Alignment.Companion.getStart();
            }

            Modifier var149 = var65.align(var171, var175);
            Intrinsics.checkNotNull(var145);
            BlockRenderData var151 = new BlockRenderData(
               var145, Color.recovered$f46ef195ad4a2363(var101.getSurveyUiColors().recovered$f126774a6443d8d2()), null, null, null, 28, null
            );
            long var77 = Color.Companion.recovered$562f21ab17d9b4c7();
            ImageRenderType var79 = ImageRenderType.WITH_MAX_SIZE;
            BlockViewKt.recovered$5709084683a88fd9(
               var149, var151, false, null, false, null, var79, var77, null, null, false, null, null, false, null, var64, 14155776, 0, 32572
            );
         }

         var64.endReplaceGroup();
         Modifier var163 = Modifier.Companion;
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var163, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var64, 6);
         var64.startReplaceGroup(412641470);
         ComposerKt.sourceInformation(var64, "*185@7888L7,193@8331L107,191@8204L414");
         Iterable var137 = var101.getQuestions();
         int var140 = 0;
         int var142 = 0;

         for (Object var146 : var137) {
            int var148 = var142++;
            if (var148 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            QuestionState var150 = (QuestionState)var146;
            int var152 = var148;
            int var153 = 0;
            CompositionLocal var80 = AndroidCompositionLocals_androidKt.getLocalContext();
            int $changed$ivxx = 0;
            int var81 = 0;
            ComposerKt.sourceInformationMarkerStart(var64, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var82 = var64.consume(var80);
            ComposerKt.sourceInformationMarkerEnd(var64);
            CharSequence var83 = Phrase.from((Context)var82, string.intercom_surveys_question_question_number_of_question_count)
               .put("questioin_number", var152 + 1)
               .put("question_count", var101.getQuestions().size())
               .format();
            Modifier var165 = Modifier.Companion;
            boolean var172 = true;
            var64.startReplaceGroup(-1598208888);
            ComposerKt.sourceInformation(var64, "CC(remember):SurveyComponent.kt#9igjgp");
            Composer var155 = var64;
            boolean var159 = var64.changedInstance(var83);
            int $i$f$cachex = 0;
            Object it$ivx = var155.rememberedValue();
            int var85/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var176;
            if (!var159 && it$ivx != Composer.Companion.getEmpty()) {
               var176 = it$ivx;
            } else {
               boolean var86 = true;
               Modifier var87 = var165;
               int var88 = 0;
               final java.lang.CharSequence recoveredExpressionCapture_1352_0 = var83;
               Function1 var89 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1352_0) -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt$SurveyContent$1.invoke$lambda$10$lambda$7$lambda$6$lambda$5$lambda$4(recoveredExpressionCapture_1352_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1352_0)));
               var165 = var87;
               var172 = var86;
               Object var90 = var89;
               var155.updateRememberedValue(var90);
               var176 = var90;
            }

            Function1 var157 = (Function1)var176;
            var64.endReplaceGroup();
            Modifier var166 = SemanticsModifierKt.semantics(var165, var172, var157);
            int $this$dp$ivxxxx = 8;
            int $i$f$getDpxxxx = 0;
            QuestionComponentKt.recovered$3f1b8998319da4be(
               PaddingKt.recovered$f3b9f2e94c07c806(var166, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 1, null),
               null,
               var150,
               null,
               var105,
               0L,
               0.0F,
               null,
               0L,
               null,
               var64,
               0,
               1002
            );
         }

         var64.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var64);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var55.endNode();
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var36);
         Modifier var167 = Modifier.Companion;
         int $this$dp$ivxxxx = 8;
         int $i$f$getDpxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var167, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), var36, 6);
         Object var168 = null;
         io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta var113 = var101.getPrimaryCta();
         var36.startReplaceGroup(-1140585494);
         ComposerKt.sourceInformation(var36, "207@8889L40");
         String var173;
         if (var113 instanceof io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Custom) {
            var173 = ((io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Custom)var113).getText();
         } else {
            if (!(var113 instanceof io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback)) {
               throw new NoWhenBranchMatchedException();
            }

            var173 = StringResources_androidKt.stringResource(((io.intercom.android.sdk.survey.SurveyState$Content$PrimaryCta$Fallback)var113).getFallbackTextRes(), var36, 0);
         }

         String var120 = var173;
         var36.endReplaceGroup();
         String var174 = var120;
         List var177 = var101.getSecondaryCtaActions();
         var36.startReplaceGroup(-1140576995);
         ComposerKt.sourceInformation(var36, "CC(remember):SurveyComponent.kt#9igjgp");
         Composer var121 = var36;
         boolean var123 = var36.changed(var102) | var36.changedInstance(var103);
         int $i$f$cachex = 0;
         Object it$ivx = var121.rememberedValue();
         int var130/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10003;
         if (!var123 && it$ivx != Composer.Companion.getEmpty()) {
            var10003 = it$ivx;
         } else {
            List var91 = var177;
            String var92 = var120;
            Object var93 = null;
            int var133 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1353_0 = var102;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1353_1 = var103;
            Function0 var94 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyComponentKt$SurveyContent$1.invoke$lambda$10$lambda$9$lambda$8(recoveredExpressionCapture_1353_0, recoveredExpressionCapture_1353_1));
            var168 = var93;
            var174 = var92;
            var177 = var91;
            Object var134 = var94;
            var121.updateRememberedValue(var134);
            var10003 = var134;
         }

         Function0 var114 = (Function0)var10003;
         var36.endReplaceGroup();
         SurveyCtaButtonComponentKt.SurveyCtaButtonComponent((Modifier)var168, var174, var177, var114, var104, var101.getSurveyUiColors(), var36, 0, 1);
         Modifier var169 = Modifier.Companion;
         int $this$dp$ivxxxxx = 16;
         int $i$f$getDpxxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var169, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx)), var36, 6);
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var33);
         var27.endNode();
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$10$lambda$7$lambda$6$lambda$5$lambda$4(CharSequence var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, var0.toString());
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$10$lambda$9$lambda$8(Function1 var0, CoroutineScope var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
