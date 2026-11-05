package io.intercom.android.sdk.post;

import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.survey.block.ImageRenderType;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$4\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,370:1\n118#2:371\n118#2:404\n118#2:406\n87#3:372\n84#3,9:373\n94#3:411\n81#4,6:382\n88#4,6:397\n96#4:410\n402#5,9:388\n411#5:403\n412#5,2:408\n1869#6:405\n1870#6:407\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$4\n*L\n212#1:371\n214#1:404\n244#1:406\n208#1:372\n208#1:373,9\n208#1:411\n208#1:382,6\n208#1:397,6\n208#1:410\n208#1:388,9\n208#1:403\n208#1:408,2\n216#1:405\n216#1:407\n*E\n"
)
final class IntercomPostActivity$onCreate$2$1$4 implements Function3 {
   // $VF: synthetic field
   final ScrollState $scrollState;
   // $VF: synthetic field
   final Part $part;

   IntercomPostActivity$onCreate$2$1$4(ScrollState var1, Part var2) {
      this.$scrollState = var1;
      this.$part = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "contentPadding");
      ComposerKt.sourceInformation(var2, "C207@9856L2172:IntercomPostActivity.kt#hv1x2k");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -76912331,
               var4,
               -1,
               "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous>.<anonymous>.<anonymous> (IntercomPostActivity.kt:207)"
            );
         }

         Modifier var10000 = PaddingKt.padding(ScrollKt.verticalScroll$default(Modifier.Companion, this.$scrollState, true, null, false, 12, null), var1);
         int var5 = 16;
         int var6 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var5);
         var5 = 16;
         var6 = 0;
         float var10003 = Dp.recovered$95b3d103663bf060(var5);
         var5 = 16;
         var6 = 0;
         Modifier var51 = PaddingKt.recovered$03b481d31d62885a(var10000, var10001, 0.0F, var10003, Dp.recovered$95b3d103663bf060(var5), 2, null);
         Part var8 = this.$part;
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var54 = Arrangement.INSTANCE.getTop();
         Horizontal var7 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var54, var7, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var51;
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
         ComposerKt.sourceInformationMarkerStart(var30, -875859440, "C213@10154L40:IntercomPostActivity.kt#hv1x2k");
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var30, 6);
         List var66 = var8.getBlocks();
         if (var66 == null) {
            var66 = CollectionsKt.emptyList();
         }

         List var55 = var66;
         var30.startReplaceGroup(1495771339);
         ComposerKt.sourceInformation(var30, "*216@10337L1510,243@11939L41");
         Iterable var56 = var55;
         int var35 = 0;

         for (Object var37 : var56) {
            Block var38 = (Block)var37;
            int var39 = 0;
            var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            Intrinsics.checkNotNull(var38);
            Color var10004 = Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$95989221dad41c4d());
            long var40 = Color.Companion.recovered$95989221dad41c4d();
            FontWeight var42 = FontWeight.Companion.getBold();
            long var43 = TextUnitKt.getSp(24);
            long var45 = TextUnitKt.getSp(36);
            BlockRenderTextStyle var10005 = new BlockRenderTextStyle(var43, var42, var45, Color.recovered$f46ef195ad4a2363(var40), null, null, 48, null);
            var40 = Color.Companion.recovered$95989221dad41c4d();
            var42 = FontWeight.Companion.getNormal();
            var43 = TextUnitKt.getSp(16);
            var45 = TextUnitKt.getSp(36);
            BlockRenderTextStyle var10006 = new BlockRenderTextStyle(var43, var42, var45, Color.recovered$f46ef195ad4a2363(var40), null, null, 48, null);
            var40 = Color.Companion.recovered$95989221dad41c4d();
            var42 = FontWeight.Companion.getNormal();
            var43 = TextUnitKt.getSp(16);
            var45 = TextUnitKt.getSp(24);
            BlockViewKt.recovered$5709084683a88fd9(
               var10000,
               new BlockRenderData(
                  var38,
                  var10004,
                  var10005,
                  var10006,
                  new BlockRenderTextStyle(var43, var42, var45, Color.recovered$f46ef195ad4a2363(var40), null, null, 48, null),
                  null
               ),
               false,
               null,
               false,
               null,
               ImageRenderType.FULL,
               0L,
               null,
               null,
               false,
               null,
               null,
               false,
               null,
               var30,
               1572870,
               0,
               32700
            );
            var10000 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var30, 6);
         }

         var30.endReplaceGroup();
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
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
