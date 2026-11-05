package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconKt;
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
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt$MediaIconButton$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,438:1\n118#2:439\n118#2:471\n70#3:440\n68#3,8:441\n77#3:475\n81#4,6:449\n88#4,6:464\n96#4:474\n402#5,9:455\n411#5:470\n412#5,2:472\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt$MediaIconButton$1\n*L\n348#1:439\n356#1:471\n346#1:440\n346#1:441,8\n346#1:475\n346#1:449,6\n346#1:464,6\n346#1:474\n346#1:455,9\n346#1:470\n346#1:472,2\n*E\n"
)
final class MediaItemsKt$MediaIconButton$1 implements Function2 {
   // $VF: synthetic field
   final long $backgroundColor;
   // $VF: synthetic field
   final int $iconRes;
   // $VF: synthetic field
   final long $iconColor;

   MediaItemsKt$MediaIconButton$1(long var1, int var3, long var4) {
      this.$backgroundColor = var1;
      this.$iconRes = var3;
      this.$iconColor = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C345@11969L485:MediaItems.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1466983418, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaIconButton.<anonymous> (MediaItems.kt:345)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 24;
         int var4 = 0;
         Modifier var36 = BackgroundKt.recovered$60bc47fb440df483(
            SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var3)), this.$backgroundColor, RoundedCornerShapeKt.getCircleShape()
         );
         Alignment var37 = Alignment.Companion.getCenter();
         int var6 = this.$iconRes;
         long var7 = this.$iconColor;
         int var9 = 48;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var5 = false;
         MeasurePolicy var11 = BoxKt.maybeCachedBoxMeasurePolicy(var37, var5);
         Modifier var12 = var36;
         MeasurePolicy var13 = var11;
         Composer var14 = var1;
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
         ComposerKt.sourceInformationMarkerStart(var27, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         BoxScope var31 = BoxScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1315354851, "C356@12323L29,354@12242L202:MediaItems.kt#yb6g9");
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         Modifier var35 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         IconKt.recovered$e00adffb7fe45b02(PainterResources_androidKt.painterResource(var6, var30, 0), null, var35, var7, var30, 432 | Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
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
