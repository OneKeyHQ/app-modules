package io.intercom.android.sdk.post;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.DividerKt;
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
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$3\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,370:1\n87#2:371\n83#2,10:372\n94#2:408\n81#3,6:382\n88#3,6:397\n96#3:407\n402#4,9:388\n411#4:403\n412#4,2:405\n123#5:404\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$3\n*L\n158#1:371\n158#1:372,10\n158#1:408\n158#1:382,6\n158#1:397,6\n158#1:407\n158#1:388,9\n158#1:403\n158#1:405,2\n159#1:404\n*E\n"
)
final class IntercomPostActivity$onCreate$2$1$3 implements Function2 {
   // $VF: synthetic field
   final IntercomPostActivity this$0;
   // $VF: synthetic field
   final Part $part;

   IntercomPostActivity$onCreate$2$1$3(IntercomPostActivity var1, Part var2) {
      this.this$0 = var1;
      this.$part = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C157@6538L3210:IntercomPostActivity.kt#hv1x2k");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1401728063,
               var2,
               -1,
               "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous>.<anonymous>.<anonymous> (IntercomPostActivity.kt:156)"
            );
         }

         if (IntercomPostActivity.access$isPreview(this.this$0)) {
            Part var6 = this.$part;
            IntercomPostActivity var7 = this.this$0;
            int var8 = 0;
            int var9 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Modifier var3 = Modifier.Companion;
            Vertical var4 = Arrangement.INSTANCE.getTop();
            Horizontal var5 = Alignment.Companion.getStart();
            MeasurePolicy var10 = ColumnKt.columnMeasurePolicy(var4, var5, var1, 14 & var8 >> 3 | 112 & var8 >> 3);
            Modifier var11 = var3;
            MeasurePolicy var12 = var10;
            Composer var13 = var1;
            int var14 = 112 & var8 << 3;
            int var15 = 0;
            ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
            CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
            Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
            Function0 var19 = ComposeUiNode.Companion.getConstructor();
            Composer var20 = var13;
            int var21 = 6 | 896 & var14 << 6;
            int var22 = 0;
            ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var20.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var20.startReusableNode();
            if (var20.getInserting()) {
               var20.createNode(var19);
            } else {
               var20.useNode();
            }

            Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
            int var24 = 0;
            Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
            int var25 = 14 & var21 >> 6;
            Composer var26 = var20;
            int var27 = 0;
            ComposerKt.sourceInformationMarkerStart(var26, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var28 = 6 | 112 & var8 >> 6;
            Composer var29 = var26;
            ColumnScope var30 = ColumnScopeInstance.INSTANCE;
            int var31 = 0;
            ComposerKt.sourceInformationMarkerStart(var29, -879365943, "C158@6579L65,159@6715L3003,159@6677L3041:IntercomPostActivity.kt#hv1x2k");
            long var32 = ColorKt.Color(2594086558L);
            double var34 = 0.65;
            int var36 = 0;
            float var37 = Dp.recovered$95b3d103663bf060((float)var34);
            DividerKt.recovered$352cda44568e0a7f(null, var37, var32, var29, 432, 1);
            IntercomPostActivityKt.BottomBarContent(
               Modifier.Companion,
               ComposableLambdaKt.rememberComposableLambda(-1915412601, true, new IntercomPostActivity$onCreate$2$1$3$1$1(var6, var7), var29, 54),
               var29,
               54
            );
            ComposerKt.sourceInformationMarkerEnd(var29);
            ComposerKt.sourceInformationMarkerEnd(var26);
            var20.endNode();
            ComposerKt.sourceInformationMarkerEnd(var20);
            ComposerKt.sourceInformationMarkerEnd(var13);
            ComposerKt.sourceInformationMarkerEnd(var1);
         }

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
