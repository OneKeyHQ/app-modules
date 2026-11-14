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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadataKt;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$AttributeCollectorBubble$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1611:1\n118#2:1612\n87#3:1613\n85#3,8:1614\n94#3:1651\n81#4,6:1622\n88#4,6:1637\n96#4:1650\n402#5,9:1628\n411#5:1643\n412#5,2:1648\n1563#6:1644\n1634#6,3:1645\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$AttributeCollectorBubble$1$1\n*L\n658#1:1612\n656#1:1613\n656#1:1614,8\n656#1:1651\n656#1:1622,6\n656#1:1637,6\n656#1:1650\n656#1:1628,9\n656#1:1643\n656#1:1648,2\n665#1:1644\n665#1:1645,3\n*E\n"
)
final class BubbleMessageRowKt$AttributeCollectorBubble$1$1 implements Function2 {
   // $VF: synthetic field
   final MessageStyle$BubbleStyle $this_with;
   // $VF: synthetic field
   final GroupingPosition $groupingPosition;
   // $VF: synthetic field
   final boolean $suppressAvatarHeader;
   // $VF: synthetic field
   final Part $conversationPart;
   // $VF: synthetic field
   final List $failedAttributeIdentifiers;
   // $VF: synthetic field
   final List $loadingAttributeIdentifiers;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;

   BubbleMessageRowKt$AttributeCollectorBubble$1$1(
      MessageStyle$BubbleStyle var1, GroupingPosition var2, boolean var3, Part var4, List var5, List var6, Function1 var7
   ) {
      this.$this_with = var1;
      this.$groupingPosition = var2;
      this.$suppressAvatarHeader = var3;
      this.$conversationPart = var4;
      this.$failedAttributeIdentifiers = var5;
      this.$loadingAttributeIdentifiers = var6;
      this.$onSubmitAttribute = var7;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C655@30147L1090:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1048297056,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.AttributeCollectorBubble.<anonymous>.<anonymous> (BubbleMessageRow.kt:655)"
            );
         }

         Modifier var3 = PaddingKt.padding(Modifier.Companion, this.$this_with.getPadding());
         int var4 = 8;
         int var5 = 0;
         Vertical var48 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         GroupingPosition var6 = this.$groupingPosition;
         boolean var7 = this.$suppressAvatarHeader;
         Part var8 = this.$conversationPart;
         List var9 = this.$failedAttributeIdentifiers;
         List var10 = this.$loadingAttributeIdentifiers;
         Function1 var11 = this.$onSubmitAttribute;
         int var12 = 48;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var49 = Alignment.Companion.getStart();
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var48, var49, var1, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var3;
         MeasurePolicy var16 = var14;
         Composer var17 = var1;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -1436576310, "C671@30906L317:BubbleMessageRow.kt#hr3bi5");
         var33.startReplaceGroup(369300491);
         ComposerKt.sourceInformation(var33, "*663@30559L290");
         if ((var6 == GroupingPosition.STANDALONE || var6 == GroupingPosition.TOP) && !var7) {
            io.intercom.android.sdk.models.Metadata var10000 = var8.getMetadata();
            if (var10000 != null) {
               io.intercom.android.sdk.models.Metadata var36 = var10000;
               int var37 = 0;
               Iterable var38 = var36.getAvatars();
               int var39 = 0;
               Iterable var40 = var38;
               ArrayList var41 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var38, 10));
               int var42 = 0;

               for (Object var44 : var40) {
                  Avatar var45 = (Avatar)var44;
                  Collection var46 = var41;
                  int var47 = 0;
                  var46.add(new AvatarWrapper(var45, false, 2, null));
               }

               MessageMetadataKt.MessageMetadata((List)var41, var36.getTitle(), null, var36.getSuffix(), var36.getTimestamp(), var33, 0, 4);
            }
         }

         var33.endReplaceGroup();
         BubbleMessageRowKt.access$PartAttributeCollectorCard(var8, var9, var10, var11, null, var33, 0, 16);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
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
