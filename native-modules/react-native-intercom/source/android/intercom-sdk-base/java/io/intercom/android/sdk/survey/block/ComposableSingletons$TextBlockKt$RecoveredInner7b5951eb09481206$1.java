package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
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
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTextBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TextBlockKt$lambda-1$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,523:1\n87#2:524\n84#2,9:525\n94#2:559\n81#3,6:534\n88#3,6:549\n96#3:558\n402#4,9:540\n411#4,3:555\n*S KotlinDebug\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$TextBlockKt$lambda-1$1\n*L\n380#1:524\n380#1:525,9\n380#1:559\n380#1:534,6\n380#1:549,6\n380#1:558\n380#1:540,9\n380#1:555,3\n*E\n"
)
final class ComposableSingletons$TextBlockKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TextBlockKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TextBlockKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C379@13720L863:TextBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -222161121, var2, -1, "io.intercom.android.sdk.survey.block.ComposableSingletons$TextBlockKt.lambda-1.<anonymous> (TextBlock.kt:379)"
            );
         }

         Modifier var3 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var6 = 6;
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
         ComposerKt.sourceInformationMarkerStart(var27, 317531059, "C385@14028L167,389@14212L171,393@14400L169:TextBlock.kt#jxhu3x");
         Block var10002 = invoke$lambda$0$buildBlock("left", "Left");
         Intrinsics.checkNotNullExpressionValue(var10002, "invoke$lambda$0$buildBlock(...)");
         BlockRenderData var30 = new BlockRenderData(var10002, null, null, null, null, 30, null);
         TextBlockKt.TextBlock(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), var30, null, false, null, null, var27, 6, 60);
         var10002 = invoke$lambda$0$buildBlock("center", "Center");
         Intrinsics.checkNotNullExpressionValue(var10002, "invoke$lambda$0$buildBlock(...)");
         var30 = new BlockRenderData(var10002, null, null, null, null, 30, null);
         TextBlockKt.TextBlock(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), var30, null, false, null, null, var27, 6, 60);
         var10002 = invoke$lambda$0$buildBlock("right", "Right");
         Intrinsics.checkNotNullExpressionValue(var10002, "invoke$lambda$0$buildBlock(...)");
         var30 = new BlockRenderData(var10002, null, null, null, null, 30, null);
         TextBlockKt.TextBlock(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), var30, null, false, null, null, var27, 6, 60);
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

   private static final Block invoke$lambda$0$buildBlock(String var0, String var1) {
      return new Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withAlign(var0).withText(var1).build();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
