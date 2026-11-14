package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.RoundedCornerShape;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.m5.preview.util.MimeTypeResolverKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$BubbleMessageRow$4\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,1611:1\n118#2:1612\n99#3:1613\n95#3,10:1614\n106#3:1704\n81#4,6:1624\n88#4,6:1639\n81#4,6:1657\n88#4,6:1672\n96#4:1699\n96#4:1703\n402#5,9:1630\n411#5:1645\n402#5,9:1663\n411#5:1678\n412#5,2:1697\n412#5,2:1701\n87#6:1646\n83#6,10:1647\n94#6:1700\n1047#7,6:1679\n1047#7,6:1685\n1047#7,6:1691\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$BubbleMessageRow$4\n*L\n157#1:1612\n156#1:1613\n156#1:1614,10\n156#1:1704\n156#1:1624,6\n156#1:1639,6\n163#1:1657,6\n163#1:1672,6\n163#1:1699\n156#1:1703\n156#1:1630,9\n156#1:1645\n163#1:1663,9\n163#1:1678\n163#1:1697,2\n156#1:1701,2\n163#1:1646\n163#1:1647,10\n163#1:1700\n177#1:1679,6\n183#1:1685,6\n199#1:1691,6\n*E\n"
)
final class BubbleMessageRowKt$BubbleMessageRow$4 implements Function5 {
   // $VF: synthetic field
   final FailedMessage $failedMessage;
   // $VF: synthetic field
   final MessageStyle $messageStyle;
   // $VF: synthetic field
   final List $mediaItems;
   // $VF: synthetic field
   final List $attachmentBlocks;
   // $VF: synthetic field
   final boolean $isAttributeCollector;
   // $VF: synthetic field
   final List $contentBlocks;
   // $VF: synthetic field
   final boolean $isAdminOrAltParticipant;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final GroupingPosition $groupingPosition;
   // $VF: synthetic field
   final boolean $hasMediaContent;
   // $VF: synthetic field
   final List $failedAttributeIdentifiers;
   // $VF: synthetic field
   final List $loadingAttributeIdentifiers;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;
   // $VF: synthetic field
   final SenderMetadata $senderMetadata;
   // $VF: synthetic field
   final long $contentColor;
   // $VF: synthetic field
   final Function1 $onCreateTicket;
   // $VF: synthetic field
   final PendingMessage$FailedMediaUploadData $failedMediaUploadData;
   // $VF: synthetic field
   final Function1 $onRetryMediaClicked;

   BubbleMessageRowKt$BubbleMessageRow$4(
      FailedMessage var1,
      MessageStyle var2,
      List var3,
      List var4,
      boolean var5,
      List var6,
      boolean var7,
      Context var8,
      GroupingPosition var9,
      boolean var10,
      List var11,
      List var12,
      Function1 var13,
      SenderMetadata var14,
      long var15,
      Function1 var17,
      PendingMessage$FailedMediaUploadData var18,
      Function1 var19
   ) {
      this.$failedMessage = var1;
      this.$messageStyle = var2;
      this.$mediaItems = var3;
      this.$attachmentBlocks = var4;
      this.$isAttributeCollector = var5;
      this.$contentBlocks = var6;
      this.$isAdminOrAltParticipant = var7;
      this.$context = var8;
      this.$groupingPosition = var9;
      this.$hasMediaContent = var10;
      this.$failedAttributeIdentifiers = var11;
      this.$loadingAttributeIdentifiers = var12;
      this.$onSubmitAttribute = var13;
      this.$senderMetadata = var14;
      this.$contentColor = var15;
      this.$onCreateTicket = var17;
      this.$failedMediaUploadData = var18;
      this.$onRetryMediaClicked = var19;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Part var2, Function0 var3, Composer var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "$this$ClickableMessageRow");
      Intrinsics.checkNotNullParameter(var2, "part");
      Intrinsics.checkNotNullParameter(var3, "onClick");
      ComposerKt.sourceInformation(var4, "CP(1)155@7658L5076:BubbleMessageRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            912974020, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.BubbleMessageRow.<anonymous> (BubbleMessageRow.kt:155)"
         );
      }

      int var7 = 8;
      int var8 = 0;
      Horizontal var94 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var7));
      Vertical var95 = Alignment.Companion.getCenterVertically();
      FailedMessage var9 = this.$failedMessage;
      MessageStyle var10 = this.$messageStyle;
      List var11 = this.$mediaItems;
      List var12 = this.$attachmentBlocks;
      boolean var13 = this.$isAttributeCollector;
      List var14 = this.$contentBlocks;
      boolean var15 = this.$isAdminOrAltParticipant;
      Context var16 = this.$context;
      GroupingPosition var17 = this.$groupingPosition;
      boolean var18 = this.$hasMediaContent;
      List var19 = this.$failedAttributeIdentifiers;
      List var20 = this.$loadingAttributeIdentifiers;
      Function1 var21 = this.$onSubmitAttribute;
      SenderMetadata var22 = this.$senderMetadata;
      long var23 = this.$contentColor;
      Function1 var25 = this.$onCreateTicket;
      PendingMessage$FailedMediaUploadData var26 = this.$failedMediaUploadData;
      Function1 var27 = this.$onRetryMediaClicked;
      int var28 = 432;
      int var29 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var4, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
      );
      Modifier var6 = Modifier.Companion;
      MeasurePolicy var30 = RowKt.rowMeasurePolicy(var94, var95, var4, 14 & var28 >> 3 | 112 & var28 >> 3);
      Modifier var31 = var6;
      MeasurePolicy var32 = var30;
      Composer var33 = var4;
      int var34 = 112 & var28 << 3;
      int var35 = 0;
      ComposerKt.sourceInformationMarkerStart(var33, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int var36 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var33, 0));
      CompositionLocalMap var37 = var33.getCurrentCompositionLocalMap();
      Modifier var38 = ComposedModifierKt.materializeModifier(var33, var31);
      Function0 var39 = ComposeUiNode.Companion.getConstructor();
      Composer var40 = var33;
      int var41 = 6 | 896 & var34 << 6;
      int var42 = 0;
      ComposerKt.sourceInformationMarkerStart(var40, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var40.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var40.startReusableNode();
      if (var40.getInserting()) {
         var40.createNode(var39);
      } else {
         var40.useNode();
      }

      Composer var43 = Updater.recovered$676aa17b44fd6280(var40);
      int var44 = 0;
      Updater.recovered$89682b199a142880(var43, var32, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880(var43, var37, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880(var43, var36, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9(var43, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880(var43, var38, ComposeUiNode.Companion.getSetModifier());
      int var45 = 14 & var41 >> 6;
      Composer var46 = var40;
      int var47 = 0;
      ComposerKt.sourceInformationMarkerStart(var46, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
      int var48 = 6 | 112 & var28 >> 6;
      Composer var49 = var46;
      RowScope var50 = RowScopeInstance.INSTANCE;
      int var51 = 0;
      ComposerKt.sourceInformationMarkerStart(var49, -1377183254, "C162@7902L4822:BubbleMessageRow.kt#hr3bi5");
      var49.startReplaceGroup(-737166762);
      ComposerKt.sourceInformation(var49, "160@7856L19");
      if (var9 != null) {
         BubbleMessageRowKt.access$FailedMessageIcon(null, var49, 0, 1);
      }

      var49.endReplaceGroup();
      androidx.compose.foundation.layout.Arrangement.Vertical var52 = Arrangement.INSTANCE
         .recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
      androidx.compose.ui.Alignment.Horizontal var53 = var10.getRowAlignment();
      byte var54 = 48;
      int var55 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var49, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
      );
      Modifier modifier$ivx = Modifier.Companion;
      MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var52, var53, var49, 14 & var54 >> 3 | 112 & var54 >> 3);
      Modifier var58 = modifier$ivx;
      MeasurePolicy var59 = measurePolicy$ivx;
      Composer var60 = var49;
      int var61 = 112 & var54 << 3;
      int $i$f$Layoutx = 0;
      ComposerKt.sourceInformationMarkerStart(var60, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var60, 0));
      CompositionLocalMap localMap$iv$ivx = var60.getCurrentCompositionLocalMap();
      Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var60, var58);
      Function0 var66 = ComposeUiNode.Companion.getConstructor();
      Composer var67 = var60;
      int var68 = 6 | 896 & var61 << 6;
      int $i$f$ReusableComposeNodex = 0;
      ComposerKt.sourceInformationMarkerStart(var67, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var67.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var67.startReusableNode();
      if (var67.getInserting()) {
         var67.createNode(var66);
      } else {
         var67.useNode();
      }

      Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var67);
      int var71/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var59, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
      int var72 = 14 & var68 >> 6;
      Composer $composer$ivx = var67;
      int var74 = 0;
      ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
      int var75 = 6 | 112 & var54 >> 6;
      Composer var76 = $composer$ivx;
      ColumnScope var77 = ColumnScopeInstance.INSTANCE;
      int var78 = 0;
      ComposerKt.sourceInformationMarkerStart(var76, -1013015956, "C:BubbleMessageRow.kt#hr3bi5");
      var76.startReplaceGroup(1214244528);
      ComposerKt.sourceInformation(var76, "176@8748L236,182@9025L117,167@8137L1028");
      if (!var11.isEmpty()) {
         Modifier var79 = PaddingKt.padding(Modifier.Companion, var10.getBubbleGutter());
         RoundedCornerShape var80 = BubbleMessageRowKt.calculateGroupedCornerShape(
            false, !var12.isEmpty() || var13 || !var14.isEmpty(), var15, var10.getBubbleStyle().getShape()
         );
         List var10000 = var11;
         RoundedCornerShape var10001 = var80;
         Modifier var10002 = var79;
         var76.startReplaceGroup(1214264851);
         ComposerKt.sourceInformation(var76, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var81 = var76;
         boolean var82 = var76.changedInstance(var16);
         int var83 = 0;
         Object var84 = var81.rememberedValue();
         int var85 = 0;
         Object var10003;
         if (!var82 && var84 != Composer.Companion.getEmpty()) {
            var10003 = (Function1)var84;
         } else {
            Modifier var86 = var79;
            RoundedCornerShape var87 = var80;
            List var88 = var11;
            int var89 = 0;
            final android.content.Context recoveredCapture_680_0 = var16;
            Function1 var90 = ((kotlin.jvm.functions.Function1) (recoveredArgument_680_0) -> BubbleMessageRowKt$BubbleMessageRow$4.invoke$lambda$7$lambda$6$lambda$1$lambda$0(recoveredCapture_680_0, (java.lang.String)recoveredArgument_680_0));
            var10000 = var88;
            var10001 = var87;
            var10002 = var86;
            Object var91 = var90;
            var81.updateRememberedValue(var91);
            var10003 = (Function1)var91;
         }

         Function1 var92 = (kotlin.jvm.functions.Function1) var10003;
         var76.endReplaceGroup();
         var10003 = var92;
         var76.startReplaceGroup(1214273596);
         ComposerKt.sourceInformation(var76, "CC(remember):BubbleMessageRow.kt#9igjgp");
         var81 = var76;
         var82 = var76.changedInstance(var16);
         var83 = 0;
         var84 = var81.rememberedValue();
         var85 = 0;
         Object var10004;
         if (!var82 && var84 != Composer.Companion.getEmpty()) {
            var10004 = var84;
         } else {
            Function1 var118 = var92;
            Modifier var110 = var10002;
            RoundedCornerShape var112 = var10001;
            List var114 = var10000;
            int var116 = 0;
            final android.content.Context recoveredCapture_681_0 = var16;
            Function1 var93 = ((kotlin.jvm.functions.Function1) (recoveredArgument_681_0) -> BubbleMessageRowKt$BubbleMessageRow$4.invoke$lambda$7$lambda$6$lambda$3$lambda$2(recoveredCapture_681_0, (java.lang.String)recoveredArgument_681_0));
            var10000 = var114;
            var10001 = var112;
            var10002 = var110;
            var10003 = var118;
            Object var120 = var93;
            var81.updateRememberedValue(var120);
            var10004 = var120;
         }

         var92 = (Function1)var10004;
         var76.endReplaceGroup();
         GroupedMediaKt.GroupedMedia(var10000, var10001, var10002, (kotlin.jvm.functions.Function1) var10003, var92, var76, 0, 0);
      }

      var76.endReplaceGroup();
      var76.startReplaceGroup(1214280272);
      ComposerKt.sourceInformation(var76, "198@9855L466,189@9258L1086");
      if (!var12.isEmpty()) {
         Modifier var96 = PaddingKt.padding(Modifier.Companion, var10.getBubbleGutter());
         RoundedCornerShape var98 = BubbleMessageRowKt.calculateGroupedCornerShape(
            !var11.isEmpty(), var13 || !var14.isEmpty(), var15, var10.getBubbleStyle().getShape()
         );
         List var124 = var12;
         RoundedCornerShape var125 = var98;
         Modifier var126 = var96;
         var76.startReplaceGroup(1214300505);
         ComposerKt.sourceInformation(var76, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var101 = var76;
         boolean var103 = var76.changedInstance(var16);
         int var105 = 0;
         Object var107 = var101.rememberedValue();
         int var109 = 0;
         Object var128;
         if (!var103 && var107 != Composer.Companion.getEmpty()) {
            var128 = var107;
         } else {
            Modifier var111 = var96;
            RoundedCornerShape var113 = var98;
            List var115 = var12;
            int var117 = 0;
            final android.content.Context recoveredCapture_679_0 = var16;
            Function2 var119 = ((kotlin.jvm.functions.Function2) (recoveredArgument_679_0, recoveredArgument_679_1) -> BubbleMessageRowKt$BubbleMessageRow$4.invoke$lambda$7$lambda$6$lambda$5$lambda$4(recoveredCapture_679_0, (java.lang.String)recoveredArgument_679_0, (java.lang.String)recoveredArgument_679_1));
            var124 = var115;
            var125 = var113;
            var126 = var111;
            Object var121 = var119;
            var101.updateRememberedValue(var121);
            var128 = var121;
         }

         Function2 var123 = (Function2)var128;
         var76.endReplaceGroup();
         GroupedFilesKt.GroupedFiles(var124, var125, var126, var123, var76, 0, 0);
      }

      var76.endReplaceGroup();
      var76.startReplaceGroup(1214326961);
      ComposerKt.sourceInformation(var76, "215@10720L712");
      if (var13) {
         BubbleMessageRowKt.access$AttributeCollectorBubble(
            var2, var17, var10, var18, !var14.isEmpty(), var15, var19, var20, var21, null, var22 != null, var76, 14 & var5 >> 3, 0, 512
         );
      }

      var76.endReplaceGroup();
      var76.startReplaceGroup(1214352897);
      ComposerKt.sourceInformation(var76, "234@11892L800");
      if (!var14.isEmpty()) {
         boolean var97 = var18 || var13;
         boolean var99 = !var13 && var22 == null;
         BubbleMessageRowKt.recovered$21a7c0d1d5f487b2(
            var2, var17, var14, var10, var97, var15, var23, var25, var26, var27, var3, var99, var76, 14 & var5 >> 3, 14 & var5 >> 6, 0
         );
      }

      var76.endReplaceGroup();
      ComposerKt.sourceInformationMarkerEnd(var76);
      ComposerKt.sourceInformationMarkerEnd($composer$ivx);
      var67.endNode();
      ComposerKt.sourceInformationMarkerEnd(var67);
      ComposerKt.sourceInformationMarkerEnd(var60);
      ComposerKt.sourceInformationMarkerEnd(var49);
      ComposerKt.sourceInformationMarkerEnd(var49);
      ComposerKt.sourceInformationMarkerEnd(var46);
      var40.endNode();
      ComposerKt.sourceInformationMarkerEnd(var40);
      ComposerKt.sourceInformationMarkerEnd(var33);
      ComposerKt.sourceInformationMarkerEnd(var4);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$1$lambda$0(Context var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "url");
      BubbleMessageRowKt.launchPreviewActivity$default(var0, var1, MimeTypeResolverKt.resolveImageMimeType$default(var1, null, 2, null), 0, 0, 0, 0, 60, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$3$lambda$2(Context var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "url");
      LinkOpener.handleUrl(var1, var0, Injector.get().getApi());
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$5$lambda$4(Context var0, String var1, String var2) {
      Intrinsics.checkNotNullParameter(var1, "url");
      Intrinsics.checkNotNullParameter(var2, "contentType");
      if (ContentTypeExtensionKt.isPdf(var2)) {
         BubbleMessageRowKt.launchPreviewActivity$default(var0, var1, "application/pdf", 0, 0, 0, 0, 60, null);
      } else {
         LinkOpener.handleUrl(var1, var0, Injector.get().getApi());
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4, Object var5) {
      this.invoke((ColumnScope)var1, (Part)var2, (Function0)var3, (Composer)var4, ((Number)var5).intValue());
      return Unit.INSTANCE;
   }
}
