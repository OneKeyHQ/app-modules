package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSegmentedFinLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt$FinBubble$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,168:1\n118#2:169\n87#3:170\n85#3,8:171\n94#3:204\n81#4,6:179\n88#4,6:194\n96#4:203\n402#5,9:185\n411#5,3:200\n*S KotlinDebug\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt$FinBubble$1\n*L\n116#1:169\n114#1:170\n114#1:171,8\n114#1:204\n114#1:179,6\n114#1:194,6\n114#1:203\n114#1:185,9\n114#1:200,3\n*E\n"
)
final class SegmentedFinLayoutKt$FinBubble$1 implements Function2 {
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;
   // $VF: synthetic field
   final Function2 $content;

   SegmentedFinLayoutKt$FinBubble$1(FinRowStyle var1, Function2 var2) {
      this.$finAnswerStyle = var1;
      this.$content = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C113@5045L191:SegmentedFinLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2000397323, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinBubble.<anonymous> (SegmentedFinLayout.kt:113)"
            );
         }

         Modifier var3 = PaddingKt.padding(Modifier.Companion, this.$finAnswerStyle.getBubbleStyle().getPadding());
         int var4 = 16;
         int var5 = 0;
         Vertical var31 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         Function2 var6 = this.$content;
         int var7 = 48;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var32 = Alignment.Companion.getStart();
         MeasurePolicy var9 = ColumnKt.columnMeasurePolicy(var31, var32, var1, 14 & var7 >> 3 | 112 & var7 >> 3);
         Modifier var10 = var3;
         MeasurePolicy var11 = var9;
         Composer var12 = var1;
         int var13 = 112 & var7 << 3;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
         CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
         Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
         Function0 var18 = ComposeUiNode.Companion.getConstructor();
         Composer var19 = var12;
         int var20 = 6 | 896 & var13 << 6;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var19.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var19.startReusableNode();
         if (var19.getInserting()) {
            var19.createNode(var18);
         } else {
            var19.useNode();
         }

         Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
         int var23 = 0;
         Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
         int var24 = 14 & var20 >> 6;
         Composer var25 = var19;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         ColumnScope var29 = ColumnScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 499935120, "C117@5217L9:SegmentedFinLayout.kt#hr3bi5");
         var6.invoke(var28, 0);
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
