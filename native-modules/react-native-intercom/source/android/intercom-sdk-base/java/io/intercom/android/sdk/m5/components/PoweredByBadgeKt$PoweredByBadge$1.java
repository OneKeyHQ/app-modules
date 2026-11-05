package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPoweredByBadge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/PoweredByBadgeKt$PoweredByBadge$1\n+ 2 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,122:1\n99#2,6:123\n106#2:155\n81#3,6:129\n88#3,6:144\n96#3:154\n402#4,9:135\n411#4:150\n412#4,2:152\n118#5:151\n*S KotlinDebug\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/PoweredByBadgeKt$PoweredByBadge$1\n*L\n55#1:123,6\n55#1:155\n55#1:129,6\n55#1:144,6\n55#1:154\n55#1:135,9\n55#1:150\n55#1:152,2\n70#1:151\n*E\n"
)
final class PoweredByBadgeKt$PoweredByBadge$1 implements Function3 {
   // $VF: synthetic field
   final int $iconId;
   // $VF: synthetic field
   final long $contentColor;
   // $VF: synthetic field
   final String $text;

   PoweredByBadgeKt$PoweredByBadge$1(int var1, long var2, String var4) {
      this.$iconId = var1;
      this.$contentColor = var2;
      this.$text = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Card");
      ComposerKt.sourceInformation(var2, "C54@2076L730:PoweredByBadge.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(803643315, var3, -1, "io.intercom.android.sdk.m5.components.PoweredByBadge.<anonymous> (PoweredByBadge.kt:54)");
         }

         Modifier var4 = PaddingKt.recovered$904403cea4c49d95(
            Modifier.Companion, PoweredByBadgeKt.access$getHorizontalPadding$p(), PoweredByBadgeKt.access$getVerticalPadding$p()
         );
         Horizontal var5 = Arrangement.INSTANCE.getCenter();
         Vertical var6 = Alignment.Companion.getCenterVertically();
         int var7 = this.$iconId;
         long var8 = this.$contentColor;
         String var10 = this.$text;
         int var11 = 438;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var13 = RowKt.rowMeasurePolicy(var5, var6, var2, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var4;
         MeasurePolicy var15 = var13;
         Composer var16 = var2;
         int var17 = 112 & var11 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -1070488119, "C65@2482L28,63@2398L207,69@2618L39,73@2767L14,70@2670L126:PoweredByBadge.kt#arqjag");
         Modifier var35 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, PoweredByBadgeKt.access$getIconSize$p());
         IconKt.recovered$e00adffb7fe45b02(PainterResources_androidKt.painterResource(var7, var32, 0), null, var35, var8, var32, 432 | Painter.$stable, 0);
         Modifier var10000 = Modifier.Companion;
         int var37 = 8;
         int var36 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var37)), var32, 6);
         TextKt.recovered$5b114ab32a4f4190(
            var10,
            null,
            var8,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            PoweredByBadgeKt.access$getTextStyle(var32, 0),
            var32,
            0,
            0,
            131066
         );
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
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
