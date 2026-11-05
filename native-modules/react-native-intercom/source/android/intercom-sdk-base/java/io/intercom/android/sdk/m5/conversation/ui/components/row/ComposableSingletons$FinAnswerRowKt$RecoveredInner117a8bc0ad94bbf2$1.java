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
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
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
   "SMAP\nFinAnswerRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-5$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,538:1\n118#2:539\n87#3:540\n84#3,9:541\n94#3:581\n81#4,6:550\n88#4,6:565\n96#4:580\n402#5,9:556\n411#5:571\n412#5,2:578\n1047#6,6:572\n*S KotlinDebug\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-5$1\n*L\n395#1:539\n391#1:540\n391#1:541,9\n391#1:581\n391#1:550,6\n391#1:565,6\n391#1:580\n391#1:556,9\n391#1:571\n391#1:578,2\n404#1:572,6\n*E\n"
)
final class ComposableSingletons$FinAnswerRowKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$FinAnswerRowKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$FinAnswerRowKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C393@15525L6,390@15411L612:FinAnswerRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -871685745,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.lambda-5.<anonymous> (FinAnswerRow.kt:390)"
            );
         }

         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         int var3 = 16;
         int var4 = 0;
         Modifier var43 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var3), 1, null);
         byte var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var44 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var44, var5, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
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
         ComposerKt.sourceInformationMarkerStart(var27, -679005004, "C403@15996L2,396@15611L402:FinAnswerRow.kt#hr3bi5");
         Part$Builder var47 = new Part$Builder().withParticipantIsAdmin(true);
         Block$Builder[] var30 = new Block$Builder[]{
            BubbleMessageRowKt.getAnswerBlock(), BubbleMessageRowKt.getCodeBlock(), BubbleMessageRowKt.getLongParagraphBlock()
         };
         Part var48 = var47.withBlocks(CollectionsKt.listOf(var30)).withMetadata(FinAnswerRowKt.access$getFinPreviewMetadata$p()).build();
         Intrinsics.checkNotNullExpressionValue(var48, "build(...)");
         GroupingPosition var10001 = GroupingPosition.STANDALONE;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var27.startReplaceGroup(-1823006785);
         ComposerKt.sourceInformation(var27, "CC(remember):FinAnswerRow.kt#9igjgp");
         Composer var45 = var27;
         boolean var31 = false;
         int var32 = 0;
         Object var33 = var45.rememberedValue();
         int var34 = 0;
         Object var10005;
         if (var33 == Composer.Companion.getEmpty()) {
            Object var35 = null;
            Object var36 = null;
            Object var37 = null;
            GroupingPosition var38 = var10001;
            Part var39 = var48;
            int var40 = 0;
            Function1 var41 = ((kotlin.jvm.functions.Function1) (recoveredArgument_691_0) -> ComposableSingletons$FinAnswerRowKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$2$lambda$1$lambda$0((java.util.List)recoveredArgument_691_0));
            var48 = var39;
            var10001 = var38;
            var10002 = var37;
            var10003 = var36;
            var10004 = var35;
            Object var46 = var41;
            var45.updateRememberedValue(var46);
            var10005 = var46;
         } else {
            var10005 = var33;
         }

         Function1 var42 = (Function1)var10005;
         var27.endReplaceGroup();
         FinAnswerRowKt.FinAnswerRow(var48, var10001, (Modifier)var10002, (String)var10003, (SenderMetadata)var10004, var42, false, null, var27, 196656, 220);
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

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(List var0) {
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
