package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-4$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,1611:1\n87#2:1612\n84#2,9:1613\n94#2:1653\n81#3,6:1622\n88#3,6:1637\n96#3:1652\n402#4,9:1628\n411#4:1643\n412#4,2:1650\n118#5:1644\n118#5:1645\n118#5:1646\n118#5:1647\n118#5:1648\n118#5:1649\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-4$1\n*L\n811#1:1612\n811#1:1613,9\n811#1:1653\n811#1:1622,6\n811#1:1637,6\n811#1:1652\n811#1:1628,9\n811#1:1643\n811#1:1650,2\n816#1:1644\n825#1:1645\n833#1:1646\n841#1:1647\n850#1:1648\n859#1:1649\n*E\n"
)
final class ComposableSingletons$BubbleMessageRowKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$BubbleMessageRowKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$BubbleMessageRowKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C813@36249L6,810@36147L2083:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1349597280,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BubbleMessageRowKt.lambda-4.<anonymous> (BubbleMessageRow.kt:810)"
            );
         }

         Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         byte var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var4 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var4, var5, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
         Modifier var9 = var3;
         MeasurePolicy var10 = var8;
         Composer var11 = var1;
         int var12 = 112 & var6 << 3;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var11, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var14 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var11, 0));
         CompositionLocalMap var15 = var11.getCurrentCompositionLocalMap();
         Modifier var16 = ComposedModifierKt.materializeModifier(var11, var9);
         Function0 var17 = ComposeUiNode.Companion.getConstructor();
         Composer var18 = var11;
         int var19 = 6 | 896 & var12 << 6;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var18.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var18.startReusableNode();
         if (var18.getInserting()) {
            var18.createNode(var17);
         } else {
            var18.useNode();
         }

         Composer var21 = Updater.recovered$676aa17b44fd6280(var18);
         int var22 = 0;
         Updater.recovered$89682b199a142880(var21, var10, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var21, var15, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var21, var14, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var21, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var21, var16, ComposeUiNode.Companion.getSetModifier());
         int var23 = 14 & var19 >> 6;
         Composer var24 = var18;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var26 = 6 | 112 & var6 >> 6;
         Composer var27 = var24;
         ColumnScope var28 = ColumnScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var27,
            1154323725,
            "C815@36292L41,816@36346L346,824@36705L40,825@36758L275,832@37046L40,833@37099L279,840@37391L41,841@37445L328,849@37786L40,850@37839L327,858@38179L41:BubbleMessageRow.kt#hr3bi5"
         );
         Modifier var10000 = Modifier.Companion;
         int var30 = 16;
         int var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part$Builder var43 = new Part$Builder();
         Block$Builder[] var37 = new Block$Builder[]{BubbleMessageRowKt.getParagraphBlock(), BubbleMessageRowKt.getLongParagraphBlock()};
         Part var44 = var43.withBlocks(CollectionsKt.listOf(var37)).withMetadata(BubbleMessageRowKt.access$getBotPreviewMetadata$p()).build();
         Intrinsics.checkNotNullExpressionValue(var44, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(var44, GroupingPosition.TOP, true, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184);
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var46 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var46, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var46, GroupingPosition.MIDDLE, true, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var48 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getLongParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var48, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var48, GroupingPosition.BOTTOM, true, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var50 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock()))
            .withMetadata(BubbleMessageRowKt.access$getHumanPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var50, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(var50, GroupingPosition.TOP, true, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184);
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var52 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getImageBlock()))
            .withMetadata(BubbleMessageRowKt.access$getHumanPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var52, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var52, GroupingPosition.BOTTOM, true, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var24);
         var18.endNode();
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var11);
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
