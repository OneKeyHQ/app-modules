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
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-2$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,1611:1\n87#2:1612\n84#2,9:1613\n94#2:1660\n81#3,6:1622\n88#3,6:1637\n96#3:1659\n402#4,9:1628\n411#4:1643\n412#4,2:1657\n118#5:1644\n118#5:1645\n118#5:1646\n118#5:1647\n118#5:1654\n118#5:1655\n118#5:1656\n1047#6,6:1648\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-2$1\n*L\n707#1:1612\n707#1:1613,9\n707#1:1660\n707#1:1622,6\n707#1:1637,6\n707#1:1659\n707#1:1628,9\n707#1:1643\n707#1:1657,2\n712#1:1644\n720#1:1645\n728#1:1646\n736#1:1647\n748#1:1654\n756#1:1655\n764#1:1656\n745#1:1648,6\n*E\n"
)
final class ComposableSingletons$BubbleMessageRowKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$BubbleMessageRowKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$BubbleMessageRowKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C709@32217L6,706@32115L2479:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1805447849,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BubbleMessageRowKt.lambda-2.<anonymous> (BubbleMessageRow.kt:706)"
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
            1659809347,
            "C711@32260L41,712@32314L293,719@32620L40,720@32673L276,727@32962L40,728@33015L276,735@33304L41,744@33785L2,736@33358L463,747@33834L40,748@33887L300,755@34200L41,756@34254L276,763@34543L41:BubbleMessageRow.kt#hr3bi5"
         );
         Modifier var10000 = Modifier.Companion;
         int var30 = 16;
         int var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part$Builder var70 = new Part$Builder();
         Block$Builder[] var59 = new Block$Builder[]{BubbleMessageRowKt.getParagraphBlock(), BubbleMessageRowKt.getLongParagraphBlock()};
         Part var71 = var70.withBlocks(CollectionsKt.listOf(var59)).build();
         Intrinsics.checkNotNullExpressionValue(var71, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var71, GroupingPosition.TOP, false, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var73 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var73, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var73, GroupingPosition.MIDDLE, false, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var75 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var75, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var75, GroupingPosition.BOTTOM, false, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part$Builder var77 = new Part$Builder();
         Block$Builder[] var63 = new Block$Builder[]{
            BubbleMessageRowKt.getImageBlock(), BubbleMessageRowKt.getImageBlock(), BubbleMessageRowKt.getVideoBlock()
         };
         Part var78 = var77.withBlocks(CollectionsKt.listOf(var63)).build();
         Intrinsics.checkNotNullExpressionValue(var78, "build(...)");
         GroupingPosition var10001 = GroupingPosition.TOP;
         boolean var10002 = false;
         Object var10003 = null;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         Object var10007 = null;
         Object var10008 = null;
         Object var10009 = null;
         Object var10010 = null;
         String var10011 = "Failed to send message";
         var27.startReplaceGroup(-916242610);
         ComposerKt.sourceInformation(var27, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var64 = var27;
         boolean var32 = false;
         int var33 = 0;
         Object var34 = var64.rememberedValue();
         int var35 = 0;
         Object var10012;
         if (var34 == Composer.Companion.getEmpty()) {
            String var36 = "Failed to send message";
            Object var37 = null;
            Object var38 = null;
            Object var39 = null;
            Object var40 = null;
            Object var41 = null;
            Object var42 = null;
            Object var43 = null;
            Object var44 = null;
            boolean var45 = false;
            GroupingPosition var46 = var10001;
            Part var47 = var78;
            int var48 = 0;
            Function0 var49 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$BubbleMessageRowKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$2$lambda$1$lambda$0());
            var78 = var47;
            var10001 = var46;
            var10002 = var45;
            var10003 = var44;
            var10004 = var43;
            var10005 = var42;
            var10006 = var41;
            var10007 = var40;
            var10008 = var39;
            var10009 = var38;
            var10010 = var37;
            var10011 = var36;
            Object var69 = var49;
            var64.updateRememberedValue(var69);
            var10012 = var69;
         } else {
            var10012 = var34;
         }

         Function0 var55 = (Function0)var10012;
         var27.endReplaceGroup();
         Function0 var50 = var55;
         String var51 = var10011;
         BubbleMessageRowKt.BubbleMessageRow(
            var78,
            var10001,
            var10002,
            (Modifier)var10003,
            (String)var10004,
            (SenderMetadata)var10005,
            (Function1)var10006,
            (List)var10007,
            (List)var10008,
            (Function1)var10009,
            (PendingMessage$FailedMediaUploadData)var10010,
            new FailedMessage(var51, var50),
            null,
            var27,
            432,
            0,
            6136
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part$Builder var80 = new Part$Builder();
         Block$Builder[] var66 = new Block$Builder[]{
            BubbleMessageRowKt.getParagraphBlock(), BubbleMessageRowKt.getImageBlock(), BubbleMessageRowKt.getVideoBlock()
         };
         Part var81 = var80.withBlocks(CollectionsKt.listOf(var66)).build();
         Intrinsics.checkNotNullExpressionValue(var81, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var81, GroupingPosition.BOTTOM, false, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var83 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getImageBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var83, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var83, GroupingPosition.STANDALONE, false, null, null, null, null, null, null, null, null, null, null, var27, 432, 0, 8184
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

   private static final Unit invoke$lambda$2$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
