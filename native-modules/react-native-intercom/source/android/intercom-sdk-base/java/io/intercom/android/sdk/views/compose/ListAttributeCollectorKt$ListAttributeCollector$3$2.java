package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nListAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,224:1\n118#2:225\n118#2:226\n118#2:258\n118#2:259\n70#3:227\n68#3,8:228\n77#3:263\n81#4,6:236\n88#4,6:251\n96#4:262\n402#5,9:242\n411#5:257\n412#5,2:260\n*S KotlinDebug\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3$2\n*L\n70#1:225\n71#1:226\n83#1:258\n84#1:259\n68#1:227\n68#1:228,8\n68#1:263\n68#1:236,6\n68#1:251,6\n68#1:262\n68#1:242,9\n68#1:257\n68#1:260,2\n*E\n"
)
final class ListAttributeCollectorKt$ListAttributeCollector$3$2 implements Function2 {
   // $VF: synthetic field
   final boolean $submitted;
   // $VF: synthetic field
   final boolean $loading;

   ListAttributeCollectorKt$ListAttributeCollector$3$2(boolean var1, boolean var2) {
      this.$submitted = var1;
      this.$loading = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C67@2677L1052:ListAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1903726860, var2, -1, "io.intercom.android.sdk.views.compose.ListAttributeCollector.<anonymous>.<anonymous> (ListAttributeCollector.kt:67)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 8;
         int var4 = 0;
         var10000 = PaddingKt.recovered$03b481d31d62885a(var10000, Dp.recovered$95b3d103663bf060(var3), 0.0F, 0.0F, 0.0F, 14, null);
         var3 = 40;
         var4 = 0;
         Modifier var39 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var3));
         Alignment var41 = Alignment.Companion.getCenter();
         boolean var6 = this.$submitted;
         boolean var7 = this.$loading;
         int var8 = 54;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var5 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var41, var5);
         Modifier var11 = var39;
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
         ComposerKt.sourceInformationMarkerStart(var26, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         BoxScope var30 = BoxScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 175582784, "C:ListAttributeCollector.kt#h0e4f2");
         if (var6) {
            var29.startReplaceGroup(-1795450501);
            ComposerKt.sourceInformation(var29, "75@2992L65,77@3163L6,74@2948L255");
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_attribute_verified_tick, var29, 0),
               null,
               null,
               IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).recovered$b549188d2697e772(),
               var29,
               48 | Painter.$stable,
               4
            );
            var29.endReplaceGroup();
         } else if (var7) {
            var29.startReplaceGroup(175898270);
            ComposerKt.sourceInformation(var29, "81@3317L6,80@3240L232");
            long var32 = IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 20;
            int $i$f$getDpx = 0;
            Modifier var36 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            $i$f$getDpx = 3;
            int $i$f$getDpxx = 0;
            float var42 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var36, var32, var42, 0L, 0, 0.0F, var29, 390, 56);
            var29.endReplaceGroup();
         } else {
            var29.startReplaceGroup(-1795432717);
            ComposerKt.sourceInformation(var29, "87@3550L57,86@3506L183");
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_list_arrow_down, var29, 0), null, null, 0L, var29, 48 | Painter.$stable, 12
            );
            var29.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
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
