package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.AiAnswerInfo;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nAnswerInfoDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AnswerInfoDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/AnswerInfoDialogKt$AnswerInfoDialog$3\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,110:1\n118#2:111\n118#2:144\n87#3:112\n84#3,9:113\n94#3:154\n81#4,6:122\n88#4,6:137\n96#4:153\n402#5,9:128\n411#5:143\n412#5,2:151\n1047#6,6:145\n*S KotlinDebug\n*F\n+ 1 AnswerInfoDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/AnswerInfoDialogKt$AnswerInfoDialog$3\n*L\n39#1:111\n44#1:144\n35#1:112\n35#1:113,9\n35#1:154\n35#1:122,6\n35#1:137,6\n35#1:153\n35#1:128,9\n35#1:143\n35#1:151,2\n52#1:145,6\n*E\n"
)
final class AnswerInfoDialogKt$AnswerInfoDialog$3 implements Function2 {
   // $VF: synthetic field
   final AiAnswerInfo $info;
   // $VF: synthetic field
   final boolean $showReportAiAnswerButton;
   // $VF: synthetic field
   final Function0 $onDismiss;
   // $VF: synthetic field
   final Function0 $onReportAiAnswer;

   AnswerInfoDialogKt$AnswerInfoDialog$3(AiAnswerInfo var1, boolean var2, Function0 var3, Function0 var4) {
      this.$info = var1;
      this.$showReportAiAnswerButton = var2;
      this.$onDismiss = var3;
      this.$onReportAiAnswer = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C37@1429L6,34@1306L943:AnswerInfoDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1632439278, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialog.<anonymous> (AnswerInfoDialog.kt:34)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         long var10001 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         int var3 = 10;
         int var4 = 0;
         Modifier var43 = BackgroundKt.recovered$60bc47fb440df483(
            var10000, var10001, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var3))
         );
         Horizontal var5 = Alignment.Companion.getCenterHorizontally();
         AiAnswerInfo var6 = this.$info;
         boolean var7 = this.$showReportAiAnswerButton;
         Function0 var8 = this.$onDismiss;
         Function0 var9 = this.$onReportAiAnswer;
         int var10 = 384;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var44 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var44, var5, var1, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var43;
         MeasurePolicy var14 = var12;
         Composer var15 = var1;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -273545572, "C45@1802L10,42@1629L209:AnswerInfoDialog.kt#o19g89");
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 24;
         int $i$f$getDpx = 0;
         float var54 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         $this$dp$ivx = 16;
         $i$f$getDpx = 0;
         Modifier var36 = PaddingKt.recovered$904403cea4c49d95(var10000, var54, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         String var46 = var6.getText();
         TextStyle var49 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04();
         TextKt.recovered$5b114ab32a4f4190(var46, var36, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var49, var31, 48, 0, 131068);
         var31.startReplaceGroup(1930845605);
         ComposerKt.sourceInformation(var31, "48@1907L17,50@1994L50,51@2080L114,49@1945L272");
         if (var7) {
            IntercomDividerKt.IntercomDivider(null, var31, 0, 1);
            String var53 = StringResources_androidKt.stringResource(string.intercom_report_ai_answer, var31, 0);
            var31.startReplaceGroup(1930852539);
            ComposerKt.sourceInformation(var31, "CC(remember):AnswerInfoDialog.kt#9igjgp");
            Composer var47 = var31;
            boolean var50 = var31.changed(var8) | var31.changed(var9);
            int var37 = 0;
            Object var38 = var47.rememberedValue();
            int var39 = 0;
            kotlin.jvm.functions.Function0 var56;
            if (!var50 && var38 != Composer.Companion.getEmpty()) {
               var56 = (Function0)var38;
            } else {
               String var40 = var53;
               int var41 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_405_0 = var8;
               final kotlin.jvm.functions.Function0 recoveredCapture_405_1 = var9;
               var56 = ((kotlin.jvm.functions.Function0) () -> AnswerInfoDialogKt$AnswerInfoDialog$3.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_405_0, recoveredCapture_405_1));
               var53 = var40;
               Object var42 = var56;
               var47.updateRememberedValue(var42);
               var56 = (Function0)var42;
            }

            Function0 var51 = var56;
            var31.endReplaceGroup();
            AnswerInfoDialogKt.access$AnswerInfoButton(var53, var51, null, var31, 0, 4);
         }

         var31.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Function0 var0, Function0 var1) {
      var0.invoke();
      var1.invoke();
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
