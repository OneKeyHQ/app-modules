package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
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
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.models.Part;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$SegmentedBubbleContent$1$2$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,1611:1\n118#2:1612\n118#2:1644\n87#3:1613\n85#3,8:1614\n94#3:1648\n81#4,6:1622\n88#4,6:1637\n96#4:1647\n402#5,9:1628\n411#5:1643\n412#5,2:1645\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$SegmentedBubbleContent$1$2$1$1\n*L\n344#1:1612\n360#1:1644\n342#1:1613\n342#1:1614,8\n342#1:1648\n342#1:1622,6\n342#1:1637,6\n342#1:1647\n342#1:1628,9\n342#1:1643\n342#1:1645,2\n*E\n"
)
final class BubbleMessageRowKt$SegmentedBubbleContent$1$2$1$1 implements Function2 {
   // $VF: synthetic field
   final MessageStyle$BubbleStyle $this_with;
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final boolean $showMetadataOnFirst;
   // $VF: synthetic field
   final Function2 $metadata;
   // $VF: synthetic field
   final ContentSegment $segment;
   // $VF: synthetic field
   final Part $conversationPart;
   // $VF: synthetic field
   final long $contentColor;
   // $VF: synthetic field
   final MessageStyle $messageStyle;
   // $VF: synthetic field
   final boolean $enabled;
   // $VF: synthetic field
   final boolean $isAdminOrAltParticipant;
   // $VF: synthetic field
   final PendingMessage$FailedMediaUploadData $failedMediaUploadData;
   // $VF: synthetic field
   final Function1 $onRetryMediaClicked;
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final Function1 $onCreateTicket;

   BubbleMessageRowKt$SegmentedBubbleContent$1$2$1$1(
      MessageStyle$BubbleStyle var1,
      int var2,
      boolean var3,
      Function2 var4,
      ContentSegment var5,
      Part var6,
      long var7,
      MessageStyle var9,
      boolean var10,
      boolean var11,
      PendingMessage$FailedMediaUploadData var12,
      Function1 var13,
      Function0 var14,
      Function1 var15
   ) {
      this.$this_with = var1;
      this.$index = var2;
      this.$showMetadataOnFirst = var3;
      this.$metadata = var4;
      this.$segment = var5;
      this.$conversationPart = var6;
      this.$contentColor = var7;
      this.$messageStyle = var9;
      this.$enabled = var10;
      this.$isAdminOrAltParticipant = var11;
      this.$failedMediaUploadData = var12;
      this.$onRetryMediaClicked = var13;
      this.$onClick = var14;
      this.$onCreateTicket = var15;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C341@17043L1225:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               686874094,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedBubbleContent.<anonymous>.<anonymous>.<anonymous>.<anonymous> (BubbleMessageRow.kt:341)"
            );
         }

         Modifier var3 = PaddingKt.padding(Modifier.Companion, this.$this_with.getPadding());
         int var4 = 8;
         int var5 = 0;
         Vertical var46 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         int var6 = this.$index;
         boolean var7 = this.$showMetadataOnFirst;
         Function2 var8 = this.$metadata;
         ContentSegment var9 = this.$segment;
         Part var10 = this.$conversationPart;
         long var11 = this.$contentColor;
         MessageStyle var13 = this.$messageStyle;
         boolean var14 = this.$enabled;
         boolean var15 = this.$isAdminOrAltParticipant;
         PendingMessage$FailedMediaUploadData var16 = this.$failedMediaUploadData;
         Function1 var17 = this.$onRetryMediaClicked;
         Function0 var18 = this.$onClick;
         Function1 var19 = this.$onCreateTicket;
         int var20 = 48;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var47 = Alignment.Companion.getStart();
         MeasurePolicy var22 = ColumnKt.columnMeasurePolicy(var46, var47, var1, 14 & var20 >> 3 | 112 & var20 >> 3);
         Modifier var23 = var3;
         MeasurePolicy var24 = var22;
         Composer var25 = var1;
         int var26 = 112 & var20 << 3;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var28 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var25, 0));
         CompositionLocalMap var29 = var25.getCurrentCompositionLocalMap();
         Modifier var30 = ComposedModifierKt.materializeModifier(var25, var23);
         Function0 var31 = ComposeUiNode.Companion.getConstructor();
         Composer var32 = var25;
         int var33 = 6 | 896 & var26 << 6;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var32.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var32.startReusableNode();
         if (var32.getInserting()) {
            var32.createNode(var31);
         } else {
            var32.useNode();
         }

         Composer var35 = Updater.recovered$676aa17b44fd6280(var32);
         int var36 = 0;
         Updater.recovered$89682b199a142880(var35, var24, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var35, var29, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var35, var28, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var35, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var35, var30, ComposeUiNode.Companion.getSetModifier());
         int var37 = 14 & var33 >> 6;
         Composer var38 = var32;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var40 = 6 | 112 & var20 >> 6;
         Composer var41 = var38;
         ColumnScope var42 = ColumnScopeInstance.INSTANCE;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, -1930897897, "C348@17421L817:BubbleMessageRow.kt#hr3bi5");
         var41.startReplaceGroup(1046090778);
         ComposerKt.sourceInformation(var41, "346@17344L10");
         if (var6 == 0 && var7) {
            var8.invoke(var41, 6);
         }

         var41.endReplaceGroup();
         List var10000 = ((ContentSegment$TextRun)var9).getBlocks();
         Shape var10003 = var13.getContentShape();
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         BubbleMessageRowKt.recovered$19fe6bd444e58e9d(
            var10000, var10, var11, var10003, var14, var15, var16, var17, var18, var19, Dp.recovered$95b3d103663bf060($this$dp$ivx), null, var41, 0, 6, 2048
         );
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var38);
         var32.endNode();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var25);
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
