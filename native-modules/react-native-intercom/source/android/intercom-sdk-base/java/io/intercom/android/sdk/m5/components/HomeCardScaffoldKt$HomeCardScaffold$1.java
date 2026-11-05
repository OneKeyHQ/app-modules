package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeCardScaffold.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeCardScaffold.kt\nio/intercom/android/sdk/m5/components/HomeCardScaffoldKt$HomeCardScaffold$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,57:1\n118#2:58\n118#2:91\n118#2:92\n87#3:59\n84#3,9:60\n94#3:96\n81#4,6:69\n88#4,6:84\n96#4:95\n402#5,9:75\n411#5:90\n412#5,2:93\n*S KotlinDebug\n*F\n+ 1 HomeCardScaffold.kt\nio/intercom/android/sdk/m5/components/HomeCardScaffoldKt$HomeCardScaffold$1\n*L\n24#1:58\n28#1:91\n29#1:92\n23#1:59\n23#1:60,9\n23#1:96\n23#1:69,6\n23#1:84,6\n23#1:95\n23#1:75,9\n23#1:90\n23#1:93,2\n*E\n"
)
final class HomeCardScaffoldKt$HomeCardScaffold$1 implements Function3 {
   // $VF: synthetic field
   final String $cardTitle;
   // $VF: synthetic field
   final Function2 $content;

   HomeCardScaffoldKt$HomeCardScaffold$1(String var1, Function2 var2) {
      this.$cardTitle = var1;
      this.$content = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C22@722L381:HomeCardScaffold.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1218435015, var3, -1, "io.intercom.android.sdk.m5.components.HomeCardScaffold.<anonymous> (HomeCardScaffold.kt:22)");
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 4;
         int var5 = 0;
         Modifier var36 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var4), 7, null);
         String var7 = this.$cardTitle;
         Function2 var8 = this.$content;
         int var9 = 6;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var37 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var37, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var36;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 918792351, "C30@1031L10,25@810L261,32@1084L9:HomeCardScaffold.kt#arqjag");
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var10002 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         $this$dp$ivx = 6;
         $i$f$getDpx = 0;
         var10000 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 5, null);
         $this$dp$ivx = 20;
         $i$f$getDpx = 0;
         Modifier var35 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 2, null);
         TextStyle var40 = IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04SemiBold();
         TextKt.recovered$5b114ab32a4f4190(var7, var35, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var40, var30, 48, 0, 131068);
         var8.invoke(var30, 0);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
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
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
