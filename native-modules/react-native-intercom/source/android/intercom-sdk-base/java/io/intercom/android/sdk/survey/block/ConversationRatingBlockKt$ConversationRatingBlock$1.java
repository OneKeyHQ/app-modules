package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationRatingBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationRatingBlock.kt\nio/intercom/android/sdk/survey/block/ConversationRatingBlockKt$ConversationRatingBlock$1\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,38:1\n70#2:39\n68#2,8:40\n77#2:74\n81#3,6:48\n88#3,6:63\n96#3:73\n402#4,9:54\n411#4:69\n412#4,2:71\n118#5:70\n*S KotlinDebug\n*F\n+ 1 ConversationRatingBlock.kt\nio/intercom/android/sdk/survey/block/ConversationRatingBlockKt$ConversationRatingBlock$1\n*L\n25#1:39\n25#1:40,8\n25#1:74\n25#1:48,6\n25#1:63,6\n25#1:73\n25#1:54,9\n25#1:69\n25#1:71,2\n33#1:70\n*E\n"
)
final class ConversationRatingBlockKt$ConversationRatingBlock$1 implements Function3 {
   // $VF: synthetic field
   final BlockRenderData $blockRenderData;
   // $VF: synthetic field
   final long $textColor;
   // $VF: synthetic field
   final String $conversationId;

   ConversationRatingBlockKt$ConversationRatingBlock$1(BlockRenderData var1, long var2, String var4) {
      this.$blockRenderData = var1;
      this.$textColor = var2;
      this.$conversationId = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C24@824L356:ConversationRatingBlock.kt#jxhu3x");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1828616789, var3, -1, "io.intercom.android.sdk.survey.block.ConversationRatingBlock.<anonymous> (ConversationRatingBlock.kt:24)"
            );
         }

         Modifier var4 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         Alignment var5 = Alignment.Companion.getCenter();
         BlockRenderData var7 = this.$blockRenderData;
         long var8 = this.$textColor;
         String var10 = this.$conversationId;
         int var11 = 54;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var6 = false;
         MeasurePolicy var13 = BoxKt.maybeCachedBoxMeasurePolicy(var5, var6);
         Modifier var14 = var4;
         MeasurePolicy var15 = var13;
         Composer var16 = var2;
         int var17 = 112 & var11 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         BoxScope var33 = BoxScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -1711001187, "C28@950L220:ConversationRatingBlock.kt#jxhu3x");
         Block var35 = var7.getBlock();
         Modifier var10000 = Modifier.Companion;
         int var36 = 16;
         int var37 = 0;
         Modifier var38 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var36));
         BlockViewKt.recovered$3dc71cc6f7fc8f68(var35, var8, var38, var10, var32, 384, 0);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
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
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
