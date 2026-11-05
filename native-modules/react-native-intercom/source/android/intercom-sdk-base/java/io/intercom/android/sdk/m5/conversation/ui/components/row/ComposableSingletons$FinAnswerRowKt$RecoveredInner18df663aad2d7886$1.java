package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
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
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant$Builder;
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
   "SMAP\nFinAnswerRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-4$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,538:1\n118#2:539\n118#2:540\n87#3:541\n85#3,8:542\n94#3:587\n81#4,6:550\n88#4,6:565\n96#4:586\n402#5,9:556\n411#5:571\n412#5,2:584\n1047#6,6:572\n1047#6,6:578\n*S KotlinDebug\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-4$1\n*L\n353#1:539\n354#1:540\n349#1:541\n349#1:542,8\n349#1:587\n349#1:550,6\n349#1:565,6\n349#1:586\n349#1:556,9\n349#1:571\n349#1:584,2\n364#1:572,6\n380#1:578,6\n*E\n"
)
final class ComposableSingletons$FinAnswerRowKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$FinAnswerRowKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$FinAnswerRowKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C351@13896L6,348@13794L1426:FinAnswerRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1709238604,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.lambda-4.<anonymous> (FinAnswerRow.kt:348)"
            );
         }

         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         int var3 = 16;
         int var4 = 0;
         Modifier var43 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         var4 = 8;
         int $i$f$getDpx = 0;
         Vertical var45 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         byte var6 = 48;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var46 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var45, var46, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
         Modifier var9 = var43;
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
         ComposerKt.sourceInformationMarkerStart(var27, -425809810, "C363@14451L2,355@14033L435,379@15193L2,366@14574L636:FinAnswerRow.kt#hr3bi5");
         Part var65 = new Part$Builder()
            .withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getAnswerBlock()))
            .withMetadata(FinAnswerRowKt.access$getFinPreviewMetadata$p())
            .build();
         Intrinsics.checkNotNullExpressionValue(var65, "build(...)");
         GroupingPosition var10001 = GroupingPosition.STANDALONE;
         Object var10002 = null;
         String var10003 = "2:34 PM";
         SenderMetadata var10004 = new SenderMetadata("Bot • AI Agent", false, 2, null);
         var27.startReplaceGroup(1510297054);
         ComposerKt.sourceInformation(var27, "CC(remember):FinAnswerRow.kt#9igjgp");
         Composer var30 = var27;
         boolean var31 = false;
         int var32 = 0;
         Object var33 = var30.rememberedValue();
         int var34 = 0;
         Object var10005;
         if (var33 == Composer.Companion.getEmpty()) {
            SenderMetadata var35 = var10004;
            String var36 = "2:34 PM";
            Object var37 = null;
            GroupingPosition var38 = var10001;
            Part var39 = var65;
            int var40 = 0;
            Function1 var41 = ((kotlin.jvm.functions.Function1) (recoveredArgument_692_0) -> ComposableSingletons$FinAnswerRowKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$5$lambda$1$lambda$0((java.util.List)recoveredArgument_692_0));
            var65 = var39;
            var10001 = var38;
            var10002 = var37;
            var10003 = var36;
            var10004 = var35;
            Object var60 = var41;
            var30.updateRememberedValue(var60);
            var10005 = var60;
         } else {
            var10005 = var33;
         }

         Function1 var42 = (Function1)var10005;
         var27.endReplaceGroup();
         FinAnswerRowKt.FinAnswerRow(var65, var10001, (Modifier)var10002, var10003, var10004, var42, false, null, var27, 199728, 196);
         Part var47 = new Part$Builder().withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getAnswerBlock())).build();
         Part var49 = var47;
         var32 = 0;
         var49.setParticipant(new Participant$Builder().withName("Fin").withType("admin").build());
         Part var66 = var47;
         Intrinsics.checkNotNullExpressionValue(var47, "apply(...)");
         var10001 = GroupingPosition.MIDDLE;
         var10002 = null;
         var10003 = "2:35 PM";
         var10004 = new SenderMetadata("Fin", true);
         var27.startReplaceGroup(1510320798);
         ComposerKt.sourceInformation(var27, "CC(remember):FinAnswerRow.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            SenderMetadata var55 = var10004;
            String var56 = "2:35 PM";
            Object var57 = null;
            GroupingPosition var58 = var10001;
            Part var59 = var47;
            int var61 = 0;
            Function1 var63 = ((kotlin.jvm.functions.Function1) (recoveredArgument_693_0) -> ComposableSingletons$FinAnswerRowKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$5$lambda$4$lambda$3((java.util.List)recoveredArgument_693_0));
            var66 = var59;
            var10001 = var58;
            var10002 = var57;
            var10003 = var56;
            var10004 = var55;
            Object var62 = var63;
            var30.updateRememberedValue(var62);
            var10005 = var62;
         } else {
            var10005 = var33;
         }

         var42 = (Function1)var10005;
         var27.endReplaceGroup();
         FinAnswerRowKt.FinAnswerRow(var66, var10001, (Modifier)var10002, var10003, var10004, var42, false, null, var27, 199728, 196);
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

   private static final Unit invoke$lambda$5$lambda$1$lambda$0(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4$lambda$3(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
