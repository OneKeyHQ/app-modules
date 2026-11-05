package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
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
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonElevation;
import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
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
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a]\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\f2\u0006\u0010\r\u001a\u00020\u000eH\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0011¨\u0006\u0014",
   d2 = {
         "SurveyCtaButtonComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "primaryCtaText",
         "",
         "secondaryCtas",
         "",
         "Lio/intercom/android/sdk/survey/SurveyState$Content$SecondaryCta;",
         "onPrimaryCtaClicked",
         "Lkotlin/Function0;",
         "onSecondaryCtaClicked",
         "Lkotlin/Function1;",
         "surveyUiColors",
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/util/List;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/survey/SurveyUiColors;Landroidx/compose/runtime/Composer;II)V",
         "LightButtonPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "DarkButtonPreview",
         "SecondaryCtaPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSurveyCtaButtonComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyCtaButtonComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyCtaButtonComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n*L\n1#1,152:1\n1047#2,6:153\n1047#2,6:159\n1047#2,6:201\n118#3:165\n118#3:166\n118#3:167\n118#3:207\n118#3:209\n118#3:210\n118#3:211\n118#3:212\n118#3:213\n87#4:168\n84#4,9:169\n94#4:217\n81#5,6:178\n88#5,6:193\n96#5:216\n81#5,6:228\n88#5,6:243\n96#5:252\n81#5,6:264\n88#5,6:279\n96#5:288\n81#5,6:300\n88#5,6:315\n96#5:324\n402#6,9:184\n411#6:199\n412#6,2:214\n402#6,9:234\n411#6,3:249\n402#6,9:270\n411#6,3:285\n402#6,9:306\n411#6,3:321\n1869#7:200\n1870#7:208\n70#8:218\n67#8,9:219\n77#8:253\n70#8:254\n67#8,9:255\n77#8:289\n70#8:290\n67#8,9:291\n77#8:325\n*S KotlinDebug\n*F\n+ 1 SurveyCtaButtonComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyCtaButtonComponentKt\n*L\n35#1:153,6\n36#1:159,6\n48#1:201,6\n39#1:165\n40#1:166\n43#1:167\n73#1:207\n86#1:209\n87#1:210\n88#1:211\n89#1:212\n90#1:213\n45#1:168\n45#1:169,9\n45#1:217\n45#1:178,6\n45#1:193,6\n45#1:216\n116#1:228,6\n116#1:243,6\n116#1:252\n127#1:264,6\n127#1:279,6\n127#1:288\n138#1:300,6\n138#1:315,6\n138#1:324\n45#1:184,9\n45#1:199\n45#1:214,2\n116#1:234,9\n116#1:249,3\n127#1:270,9\n127#1:285,3\n138#1:306,9\n138#1:321,3\n46#1:200\n46#1:208\n116#1:218\n116#1:219,9\n116#1:253\n127#1:254\n127#1:255,9\n127#1:289\n138#1:290\n138#1:291,9\n138#1:325\n*E\n"
)
public final class SurveyCtaButtonComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyCtaButtonComponent(
      @Nullable Modifier var0,
      @NotNull String var1,
      @Nullable List var2,
      @Nullable Function0 var3,
      @Nullable Function1 var4,
      @NotNull SurveyUiColors var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "primaryCtaText");
      Intrinsics.checkNotNullParameter(var5, "surveyUiColors");
      var6 = var6.startRestartGroup(-1455595547);
      ComposerKt.sourceInformation(var6, "C(SurveyCtaButtonComponent)P(!1,3,4)34@1517L2,35@1593L2,44@1837L2353:SurveyCtaButtonComponent.kt#bmpoeo");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changed(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = CollectionsKt.emptyList();
         }

         if ((var8 & 8) != 0) {
            var6.startReplaceGroup(49453589);
            ComposerKt.sourceInformation(var6, "CC(remember):SurveyCtaButtonComponent.kt#9igjgp");
            Composer var11 = var6;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var78 = ((kotlin.jvm.functions.Function0) () -> SurveyCtaButtonComponentKt.SurveyCtaButtonComponent$lambda$1$lambda$0());
               var11.updateRememberedValue(var78);
               var10000 = var78;
            } else {
               var10000 = var14;
            }

            Function0 var10 = (Function0)var10000;
            var6.endReplaceGroup();
            var3 = var10;
         }

         if ((var8 & 16) != 0) {
            var6.startReplaceGroup(49456021);
            ComposerKt.sourceInformation(var6, "CC(remember):SurveyCtaButtonComponent.kt#9igjgp");
            Composer var62 = var6;
            boolean var65 = false;
            int var69 = 0;
            Object var73 = var62.rememberedValue();
            int var76 = 0;
            Object var104;
            if (var73 == Composer.Companion.getEmpty()) {
               int var79 = 0;
               Object var80 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1359_0) -> SurveyCtaButtonComponentKt.SurveyCtaButtonComponent$lambda$3$lambda$2((io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta)recoveredArgument_1359_0));
               var62.updateRememberedValue(var80);
               var104 = var80;
            } else {
               var104 = var73;
            }

            Function1 var60 = (Function1)var104;
            var6.endReplaceGroup();
            var4 = var60;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1455595547, var9, -1, "io.intercom.android.sdk.survey.ui.components.SurveyCtaButtonComponent (SurveyCtaButtonComponent.kt:37)"
            );
         }

         int var63 = 1;
         int var66 = 0;
         BorderStroke var61 = BorderStrokeKt.recovered$91879a1bef2fb536(Dp.recovered$95b3d103663bf060(var63), var5.recovered$0a79dc0323a17592());
         var66 = 8;
         int $i$f$getDpx = 0;
         RoundedCornerShape var64 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var66));
         Modifier var105 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         $i$f$getDpx = 56;
         int $i$f$getDpxx = 0;
         Modifier var68 = SizeKt.recovered$3f6c6a76aa292717(var105, Dp.recovered$95b3d103663bf060($i$f$getDpx));
         int var77 = 14 & var9;
         int var81 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var6, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var72 = Arrangement.INSTANCE.getTop();
         Horizontal var75 = Alignment.Companion.getStart();
         MeasurePolicy var17 = ColumnKt.columnMeasurePolicy(var72, var75, var6, 14 & var77 >> 3 | 112 & var77 >> 3);
         Modifier var18 = var0;
         MeasurePolicy var19 = var17;
         Composer var20 = var6;
         int var21 = 112 & var77 << 3;
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
         int var35 = 6 | 112 & var77 >> 6;
         Composer var36 = var33;
         ColumnScope var37 = ColumnScopeInstance.INSTANCE;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 1945218444, "C100@3978L77,103@4067L117,94@3755L429:SurveyCtaButtonComponent.kt#bmpoeo");
         var36.startReplaceGroup(1171126918);
         ComposerKt.sourceInformation(var36, "*51@2126L91,47@1945L29,54@2233L733,46@1911L1055,72@2979L40");
         Iterable var39 = var2;
         int var40 = 0;

         for (Object var42 : var39) {
            SurveyState$Content$SecondaryCta var43 = (SurveyState$Content$SecondaryCta)var42;
            int var44 = 0;
            ButtonColors var45 = ButtonDefaults.INSTANCE
               .recovered$6a8c28fb0b42a2fc(var5.recovered$923dd289d74a1df8(), 0L, 0L, 0L, var36, ButtonDefaults.$stable << 12, 14);
            var36.startReplaceGroup(1934484526);
            ComposerKt.sourceInformation(var36, "CC(remember):SurveyCtaButtonComponent.kt#9igjgp");
            Composer var46 = var36;
            boolean var47 = (var9 & 57344) == 16384 | var6.changed(var43);
            int var48 = 0;
            Object var49 = var46.rememberedValue();
            int var50 = 0;
            Object var106;
            if (!var47 && var49 != Composer.Companion.getEmpty()) {
               var106 = var49;
            } else {
               int var51 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1360_0 = var4;
               final io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta recoveredCapture_1360_1 = var43;
               Object var52 = ((kotlin.jvm.functions.Function0) () -> SurveyCtaButtonComponentKt.SurveyCtaButtonComponent$lambda$7$lambda$6$lambda$5$lambda$4(recoveredCapture_1360_0, recoveredCapture_1360_1));
               var46.updateRememberedValue(var52);
               var106 = var52;
            }

            Function0 var53 = (Function0)var106;
            var36.endReplaceGroup();
            ButtonKt.Button(
               var53,
               var68,
               false,
               var64,
               var45,
               null,
               var61,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  468620518, true, new SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$1$2(var43, var5), var36, 54
               ),
               var36,
               805306416,
               420
            );
            Modifier var107 = Modifier.Companion;
            int $this$dp$ivx = 8;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var107, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var36, 6);
         }

         var36.endReplaceGroup();
         Pair var82 = var2.isEmpty()
            ? new Pair(Color.recovered$f46ef195ad4a2363(var5.recovered$923dd289d74a1df8()), Color.recovered$f46ef195ad4a2363(var5.recovered$9bdc86596139ab10()))
            : new Pair(
               Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$562f21ab17d9b4c7()),
               Color.recovered$f46ef195ad4a2363(var5.recovered$f126774a6443d8d2())
            );
         long var54 = ((Color)var82.component1()).recovered$45f9de7b85e0e0b5();
         long var56 = ((Color)var82.component2()).recovered$45f9de7b85e0e0b5();
         BorderStroke var83 = var2.isEmpty() ? var61 : null;
         var36.startReplaceGroup(1171174724);
         ComposerKt.sourceInformation(var36, "84@3466L236");
         ButtonElevation var108;
         if (var2.isEmpty()) {
            ButtonDefaults var88 = ButtonDefaults.INSTANCE;
            int $this$dp$ivx = 2;
            int $i$f$getDpxxx = 0;
            float var89 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            $i$f$getDpxxx = 8;
            int $i$f$getDpxxxx = 0;
            float var91 = Dp.recovered$95b3d103663bf060($i$f$getDpxxx);
            $i$f$getDpxxxx = 0;
            int $i$f$getDpxxxxx = 0;
            float var94 = Dp.recovered$95b3d103663bf060($i$f$getDpxxxx);
            $i$f$getDpxxxxx = 4;
            int $i$f$getDpxxxxxx = 0;
            float var97 = Dp.recovered$95b3d103663bf060($i$f$getDpxxxxx);
            $i$f$getDpxxxxxx = 4;
            int $i$f$getDpxxxxxxx = 0;
            float var100 = Dp.recovered$95b3d103663bf060($i$f$getDpxxxxxx);
            var108 = var88.recovered$41d66489ad09ffde(var89, var91, var100, var97, var94, var36, 28086 | ButtonDefaults.$stable << 15, 0);
         } else {
            var108 = null;
         }

         ButtonElevation var86 = var108;
         var36.endReplaceGroup();
         ButtonElevation var85 = var86;
         ButtonColors var87 = ButtonDefaults.INSTANCE.recovered$6a8c28fb0b42a2fc(var54, 0L, 0L, 0L, var36, ButtonDefaults.$stable << 12, 14);
         ButtonKt.Button(
            var3,
            var68,
            false,
            var64,
            var87,
            var85,
            var83,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(767351755, true, new SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$2(var1, var56), var36, 54),
            var36,
            805306416 | 14 & var9 >> 9,
            388
         );
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var33);
         var27.endNode();
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var109 = var6.endRestartGroup();
      if (var109 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1357_0 = var0;
         final java.lang.String recoveredCapture_1357_1 = var1;
         final java.util.List recoveredCapture_1357_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_1357_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_1357_4 = var4;
         final io.intercom.android.sdk.survey.SurveyUiColors recoveredCapture_1357_5 = var5;
         final int recoveredCapture_1357_6 = var7;
         final int recoveredCapture_1357_7 = var8;
         var109.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1357_0, recoveredArgument_1357_1) -> SurveyCtaButtonComponentKt.SurveyCtaButtonComponent$lambda$8(recoveredCapture_1357_0, recoveredCapture_1357_1, recoveredCapture_1357_2, recoveredCapture_1357_3, recoveredCapture_1357_4, recoveredCapture_1357_5, recoveredCapture_1357_6, recoveredCapture_1357_7, (androidx.compose.runtime.Composer)recoveredArgument_1357_0, ((java.lang.Integer)recoveredArgument_1357_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void LightButtonPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1401512691);
      ComposerKt.sourceInformation(var0, "C(LightButtonPreview)115@4255L186:SurveyCtaButtonComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1401512691, var1, -1, "io.intercom.android.sdk.survey.ui.components.LightButtonPreview (SurveyCtaButtonComponent.kt:114)"
            );
         }

         Modifier var2 = Modifier.Companion;
         int var5 = 6;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var0, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var3 = Alignment.Companion.getTopStart();
         boolean var4 = false;
         MeasurePolicy var7 = BoxKt.maybeCachedBoxMeasurePolicy(var3, var4);
         Modifier var8 = var2;
         MeasurePolicy var9 = var7;
         Composer var10 = var0;
         int var11 = 112 & var5 << 3;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var10, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var13 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var10, 0));
         CompositionLocalMap var14 = var10.getCurrentCompositionLocalMap();
         Modifier var15 = ComposedModifierKt.materializeModifier(var10, var8);
         Function0 var16 = ComposeUiNode.Companion.getConstructor();
         Composer var17 = var10;
         int var18 = 6 | 896 & var11 << 6;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var17.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var17.startReusableNode();
         if (var17.getInserting()) {
            var17.createNode(var16);
         } else {
            var17.useNode();
         }

         Composer var20 = Updater.recovered$676aa17b44fd6280(var17);
         int var21 = 0;
         Updater.recovered$89682b199a142880(var20, var9, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var20, var14, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var20, var13, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var20, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var20, var15, ComposeUiNode.Companion.getSetModifier());
         int var22 = 14 & var18 >> 6;
         Composer var23 = var17;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var25 = 6 | 112 & var5 >> 6;
         Composer var26 = var23;
         BoxScope var27 = BoxScopeInstance.INSTANCE;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1984328629, "C116@4290L145:SurveyCtaButtonComponent.kt#bmpoeo");
         SurveyCtaButtonComponent(
            null, "Submit", null, null, null, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), var26, 48, 29
         );
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var23);
         var17.endNode();
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var10);
         ComposerKt.sourceInformationMarkerEnd(var0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1355_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1355_0, recoveredArgument_1355_1) -> SurveyCtaButtonComponentKt.LightButtonPreview$lambda$10(recoveredCapture_1355_0, (androidx.compose.runtime.Composer)recoveredArgument_1355_0, ((java.lang.Integer)recoveredArgument_1355_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DarkButtonPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-41399177);
      ComposerKt.sourceInformation(var0, "C(DarkButtonPreview)126@4505L209:SurveyCtaButtonComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-41399177, var1, -1, "io.intercom.android.sdk.survey.ui.components.DarkButtonPreview (SurveyCtaButtonComponent.kt:125)");
         }

         Modifier var2 = Modifier.Companion;
         int var5 = 6;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var0, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var3 = Alignment.Companion.getTopStart();
         boolean var4 = false;
         MeasurePolicy var7 = BoxKt.maybeCachedBoxMeasurePolicy(var3, var4);
         Modifier var8 = var2;
         MeasurePolicy var9 = var7;
         Composer var10 = var0;
         int var11 = 112 & var5 << 3;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var10, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var13 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var10, 0));
         CompositionLocalMap var14 = var10.getCurrentCompositionLocalMap();
         Modifier var15 = ComposedModifierKt.materializeModifier(var10, var8);
         Function0 var16 = ComposeUiNode.Companion.getConstructor();
         Composer var17 = var10;
         int var18 = 6 | 896 & var11 << 6;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var17.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var17.startReusableNode();
         if (var17.getInserting()) {
            var17.createNode(var16);
         } else {
            var17.useNode();
         }

         Composer var20 = Updater.recovered$676aa17b44fd6280(var17);
         int var21 = 0;
         Updater.recovered$89682b199a142880(var20, var9, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var20, var14, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var20, var13, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var20, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var20, var15, ComposeUiNode.Companion.getSetModifier());
         int var22 = 14 & var18 >> 6;
         Composer var23 = var17;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var25 = 6 | 112 & var5 >> 6;
         Composer var26 = var23;
         BoxScope var27 = BoxScopeInstance.INSTANCE;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -1417719078, "C127@4540L168:SurveyCtaButtonComponent.kt#bmpoeo");
         SurveyCtaButtonComponent(
            null, "Submit", null, null, null, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, "#222222", 1, null)), var26, 48, 29
         );
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var23);
         var17.endNode();
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var10);
         ComposerKt.sourceInformationMarkerEnd(var0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1354_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1354_0, recoveredArgument_1354_1) -> SurveyCtaButtonComponentKt.DarkButtonPreview$lambda$12(recoveredCapture_1354_0, (androidx.compose.runtime.Composer)recoveredArgument_1354_0, ((java.lang.Integer)recoveredArgument_1354_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SecondaryCtaPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1826494403);
      ComposerKt.sourceInformation(var0, "C(SecondaryCtaPreview)137@4780L457:SurveyCtaButtonComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1826494403, var1, -1, "io.intercom.android.sdk.survey.ui.components.SecondaryCtaPreview (SurveyCtaButtonComponent.kt:136)"
            );
         }

         Modifier var2 = Modifier.Companion;
         int var5 = 6;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var0, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var3 = Alignment.Companion.getTopStart();
         boolean var4 = false;
         MeasurePolicy var7 = BoxKt.maybeCachedBoxMeasurePolicy(var3, var4);
         Modifier var8 = var2;
         MeasurePolicy var9 = var7;
         Composer var10 = var0;
         int var11 = 112 & var5 << 3;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var10, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var13 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var10, 0));
         CompositionLocalMap var14 = var10.getCurrentCompositionLocalMap();
         Modifier var15 = ComposedModifierKt.materializeModifier(var10, var8);
         Function0 var16 = ComposeUiNode.Companion.getConstructor();
         Composer var17 = var10;
         int var18 = 6 | 896 & var11 << 6;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var17.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var17.startReusableNode();
         if (var17.getInserting()) {
            var17.createNode(var16);
         } else {
            var17.useNode();
         }

         Composer var20 = Updater.recovered$676aa17b44fd6280(var17);
         int var21 = 0;
         Updater.recovered$89682b199a142880(var20, var9, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var20, var14, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var20, var13, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var20, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var20, var15, ComposeUiNode.Companion.getSetModifier());
         int var22 = 14 & var18 >> 6;
         Composer var23 = var17;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var25 = 6 | 112 & var5 >> 6;
         Composer var26 = var23;
         BoxScope var27 = BoxScopeInstance.INSTANCE;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -1432808010, "C138@4815L416:SurveyCtaButtonComponent.kt#bmpoeo");
         SurveyUiColors var29 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         List var30 = CollectionsKt.listOf(new SurveyState$Content$SecondaryCta("Open website", "https://www.google.com", true));
         SurveyCtaButtonComponent(null, "Submit", var30, null, null, var29, var26, 48, 25);
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var23);
         var17.endNode();
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var10);
         ComposerKt.sourceInformationMarkerEnd(var0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1356_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1356_0, recoveredArgument_1356_1) -> SurveyCtaButtonComponentKt.SecondaryCtaPreview$lambda$14(recoveredCapture_1356_0, (androidx.compose.runtime.Composer)recoveredArgument_1356_0, ((java.lang.Integer)recoveredArgument_1356_1).intValue())));
      }
   }

   private static final Unit SurveyCtaButtonComponent$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit SurveyCtaButtonComponent$lambda$3$lambda$2(SurveyState$Content$SecondaryCta var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyCtaButtonComponent$lambda$7$lambda$6$lambda$5$lambda$4(Function1 var0, SurveyState$Content$SecondaryCta var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit SurveyCtaButtonComponent$lambda$8(
      Modifier var0, String var1, List var2, Function0 var3, Function1 var4, SurveyUiColors var5, int var6, int var7, Composer var8, int var9
   ) {
      SurveyCtaButtonComponent(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit LightButtonPreview$lambda$10(int var0, Composer var1, int var2) {
      LightButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DarkButtonPreview$lambda$12(int var0, Composer var1, int var2) {
      DarkButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SecondaryCtaPreview$lambda$14(int var0, Composer var1, int var2) {
      SecondaryCtaPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
