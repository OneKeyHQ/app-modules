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
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.models.Form;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
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
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-6$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,1611:1\n87#2:1612\n84#2,9:1613\n94#2:1653\n81#3,6:1622\n88#3,6:1637\n96#3:1652\n402#4,9:1628\n411#4:1643\n412#4,2:1650\n118#5:1644\n118#5:1645\n118#5:1646\n118#5:1647\n118#5:1648\n118#5:1649\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BubbleMessageRowKt$lambda-6$1\n*L\n876#1:1612\n876#1:1613,9\n876#1:1653\n876#1:1622,6\n876#1:1637,6\n876#1:1652\n876#1:1628,9\n876#1:1643\n876#1:1650,2\n881#1:1644\n892#1:1645\n903#1:1646\n914#1:1647\n923#1:1648\n956#1:1649\n*E\n"
)
final class ComposableSingletons$BubbleMessageRowKt$RecoveredInner0c8347b84394e1a9$1 implements Function2 {
   public static final ComposableSingletons$BubbleMessageRowKt$RecoveredInner0c8347b84394e1a9$1 INSTANCE = new ComposableSingletons$BubbleMessageRowKt$RecoveredInner0c8347b84394e1a9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C878@38712L6,875@38610L3660:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2040406284,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BubbleMessageRowKt.lambda-6.<anonymous> (BubbleMessageRow.kt:875)"
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
            647342818,
            "C880@38755L41,881@38809L447,891@39269L40,892@39322L431,902@39766L40,903@39819L438,913@40270L41,914@40324L319,922@40656L41,924@40803L1403,955@42219L41:BubbleMessageRow.kt#hr3bi5"
         );
         Modifier var10000 = Modifier.Companion;
         int var30 = 16;
         int var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var45 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock()))
            .withMetadata(BubbleMessageRowKt.access$getBotPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var45, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var45,
            GroupingPosition.STANDALONE,
            true,
            null,
            "2:34 PM",
            new SenderMetadata("Bot • AI Agent", false, 2, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            25008,
            0,
            8136
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var47 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock()))
            .withMetadata(BubbleMessageRowKt.access$getHumanPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var47, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var47,
            GroupingPosition.TOP,
            true,
            null,
            "2:35 PM",
            new SenderMetadata("Bob", false, 2, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            25008,
            0,
            8136
         );
         var10000 = Modifier.Companion;
         var30 = 2;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var49 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getLongParagraphBlock()))
            .withMetadata(BubbleMessageRowKt.access$getHumanPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var49, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var49,
            GroupingPosition.BOTTOM,
            true,
            null,
            "2:35 PM",
            new SenderMetadata("Bob", false, 2, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            25008,
            0,
            8136
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var51 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var51, "build(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var51, GroupingPosition.STANDALONE, false, null, "2:36 PM", null, null, null, null, null, null, null, null, var27, 25008, 0, 8168
         );
         var10000 = Modifier.Companion;
         var30 = 16;
         var31 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var30)), var27, 6);
         Part var43 = new Part$Builder()
            .withParticipantIsAdmin(true)
            .withStyle(io.intercom.android.sdk.models.MessageStyle.ATTRIBUTE_COLLECTOR)
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock()))
            .withForm(
               new Form(
                  "attribute_collector",
                  CollectionsKt.listOf(new Attribute("text", "What's your name?", "string", false, null, null, null, null, 240, null)),
                  false
               )
            )
            .build();
         Part var32 = var43;
         int var33 = 0;
         var32.setParticipant(new Participant$Builder().withName("Sarah").withType("admin").build());
         Intrinsics.checkNotNullExpressionValue(var43, "apply(...)");
         BubbleMessageRowKt.BubbleMessageRow(
            var43,
            GroupingPosition.STANDALONE,
            true,
            null,
            "2:37 PM",
            new SenderMetadata("Sarah", true),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var27,
            25008,
            0,
            8136
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
