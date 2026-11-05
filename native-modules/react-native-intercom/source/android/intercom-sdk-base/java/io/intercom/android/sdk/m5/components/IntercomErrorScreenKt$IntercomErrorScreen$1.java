package io.intercom.android.sdk.m5.components;

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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomErrorScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomErrorScreen.kt\nio/intercom/android/sdk/m5/components/IntercomErrorScreenKt$IntercomErrorScreen$1\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,100:1\n70#2:101\n66#2,10:102\n77#2:178\n81#3,6:112\n88#3,6:127\n81#3,6:145\n88#3,6:160\n96#3:173\n96#3:177\n402#4,9:118\n411#4:133\n402#4,9:151\n411#4:166\n412#4,2:171\n412#4,2:175\n87#5:134\n83#5,10:135\n94#5:174\n118#6:167\n118#6:168\n118#6:169\n118#6:170\n*S KotlinDebug\n*F\n+ 1 IntercomErrorScreen.kt\nio/intercom/android/sdk/m5/components/IntercomErrorScreenKt$IntercomErrorScreen$1\n*L\n32#1:101\n32#1:102,10\n32#1:178\n32#1:112,6\n32#1:127,6\n33#1:145,6\n33#1:160,6\n33#1:173\n32#1:177\n32#1:118,9\n32#1:133\n33#1:151,9\n33#1:166\n33#1:171,2\n32#1:175,2\n33#1:134\n33#1:135,10\n33#1:174\n39#1:167\n41#1:168\n46#1:169\n53#1:170\n*E\n"
)
final class IntercomErrorScreenKt$IntercomErrorScreen$1 implements Function2 {
   // $VF: synthetic field
   final ErrorState $state;

   IntercomErrorScreenKt$IntercomErrorScreen$1(ErrorState var1) {
      this.$state = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C31@1100L1211:IntercomErrorScreen.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(847888321, var2, -1, "io.intercom.android.sdk.m5.components.IntercomErrorScreen.<anonymous> (IntercomErrorScreen.kt:31)");
         }

         Alignment var4 = Alignment.Companion.getCenter();
         ErrorState var6 = this.$state;
         int var7 = 48;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var3 = Modifier.Companion;
         boolean var5 = false;
         MeasurePolicy var9 = BoxKt.maybeCachedBoxMeasurePolicy(var4, var5);
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
         ComposerKt.sourceInformationMarkerStart(var25, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         BoxScope var29 = BoxScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -63341886, "C32@1155L1146:IntercomErrorScreen.kt#arqjag");
         Horizontal var31 = Alignment.Companion.getCenterHorizontally();
         short var32 = 384;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var28, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier modifier$ivx = Modifier.Companion;
         Vertical var35 = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var35, var31, var28, 14 & var32 >> 3 | 112 & var32 >> 3);
         Modifier var37 = modifier$ivx;
         MeasurePolicy var38 = measurePolicy$ivx;
         Composer var39 = var28;
         int var40 = 112 & var32 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var39, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var39, 0));
         CompositionLocalMap localMap$iv$ivx = var39.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var39, var37);
         Function0 var45 = ComposeUiNode.Companion.getConstructor();
         Composer var46 = var39;
         int var47 = 6 | 896 & var40 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var46.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var46.startReusableNode();
         if (var46.getInserting()) {
            var46.createNode(var45);
         } else {
            var46.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var46);
         int var50/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var38, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var51 = 14 & var47 >> 6;
         Composer $composer$ivx = var46;
         int var53 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var54 = 6 | 112 & var32 >> 6;
         Composer var55 = $composer$ivx;
         ColumnScope var56 = ColumnScopeInstance.INSTANCE;
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var55, -1686548771, "C36@1299L34,35@1263L189,40@1469L41,42@1560L39,43@1643L10,41@1527L152,45@1696L40,52@1992L41:IntercomErrorScreen.kt#arqjag"
         );
         Painter var10000 = PainterResources_androidKt.painterResource(var6.getIconId(), var55, 0);
         Modifier var10002 = Modifier.Companion;
         int var58 = 32;
         int var59 = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var10000, null, SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var58)), 0L, var55, 432 | Painter.$stable, 8
         );
         Modifier var69 = Modifier.Companion;
         var58 = 16;
         var59 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var69, Dp.recovered$95b3d103663bf060(var58)), var55, 6);
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(var6.getMessageResId(), var55, 0),
            null,
            0L,
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
            IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType03(),
            var55,
            0,
            0,
            131070
         );
         Modifier var70 = Modifier.Companion;
         var58 = 8;
         var59 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var70, Dp.recovered$95b3d103663bf060(var58)), var55, 6);
         Integer var64 = var6.getAdditionalMessageResId();
         var55.startReplaceGroup(1192536998);
         ComposerKt.sourceInformation(var55, "*48@1846L23,49@1917L10,47@1809L148");
         if (var64 != null) {
            int var60 = var64.intValue();
            int var61 = 0;
            TextKt.recovered$5b114ab32a4f4190(
               StringResources_androidKt.stringResource(var60, var55, 0),
               null,
               0L,
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
               IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
               var55,
               0,
               0,
               131070
            );
         }

         var55.endReplaceGroup();
         Modifier var71 = Modifier.Companion;
         int $this$dp$ivx = 16;
         var59 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var71, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var55, 6);
         var55.startReplaceGroup(1192545587);
         ComposerKt.sourceInformation(var55, "55@2159L35,54@2105L164");
         if (var6 instanceof ErrorState$WithCTA) {
            IntercomPrimaryButtonKt.IntercomPrimaryButton(
               StringResources_androidKt.stringResource(((ErrorState$WithCTA)var6).getCtaResId(), var55, 0),
               null,
               null,
               ((ErrorState$WithCTA)var6).getOnCtaClick(),
               var55,
               0,
               6
            );
         }

         var55.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var46.endNode();
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var28);
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
