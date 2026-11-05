package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material3.ProgressIndicatorKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/ComposableSingletons$TicketsScreenKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,147:1\n118#2:148\n70#3:149\n68#3,8:150\n77#3:183\n81#4,6:158\n88#4,6:173\n96#4:182\n402#5,9:164\n411#5,3:179\n*S KotlinDebug\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/ComposableSingletons$TicketsScreenKt$lambda-1$1\n*L\n135#1:148\n133#1:149\n133#1:150,8\n133#1:183\n133#1:158,6\n133#1:173,6\n133#1:182\n133#1:164,9\n133#1:179,3\n*E\n"
)
final class ComposableSingletons$TicketsScreenKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$TicketsScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketsScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C132@5164L374:TicketsScreen.kt#yl74uv");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1474046781,
               var3,
               -1,
               "io.intercom.android.sdk.tickets.list.ui.ComposableSingletons$TicketsScreenKt.lambda-1.<anonymous> (TicketsScreen.kt:132)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 16;
         int var5 = 0;
         Modifier var31 = SizeKt.fillMaxWidth$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var4), 1, null), 0.0F, 1, null
         );
         Alignment var32 = Alignment.Companion.getCenter();
         byte var7 = 54;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var6 = false;
         MeasurePolicy var9 = BoxKt.maybeCachedBoxMeasurePolicy(var32, var6);
         Modifier var10 = var31;
         MeasurePolicy var11 = var9;
         Composer var12 = var2;
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
         ComposerKt.sourceInformationMarkerStart(var25, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         BoxScope var29 = BoxScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -1899064572, "C139@5471L6,138@5398L122:TicketsScreen.kt#yl74uv");
         ProgressIndicatorKt.recovered$276fbc1f701bb35f(
            null, IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(), 0.0F, 0L, 0, 0.0F, var28, 0, 61
         );
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
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
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
