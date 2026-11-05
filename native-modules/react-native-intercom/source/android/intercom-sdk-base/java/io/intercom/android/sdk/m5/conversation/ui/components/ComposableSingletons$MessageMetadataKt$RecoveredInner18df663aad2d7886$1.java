package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
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
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMessageMetadata.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageMetadata.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$MessageMetadataKt$lambda-4$1\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,133:1\n70#2:134\n67#2,9:135\n77#2:169\n81#3,6:144\n88#3,6:159\n96#3:168\n402#4,9:150\n411#4,3:165\n*S KotlinDebug\n*F\n+ 1 MessageMetadata.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$MessageMetadataKt$lambda-4$1\n*L\n117#1:134\n117#1:135,9\n117#1:169\n117#1:144,6\n117#1:159,6\n117#1:168\n117#1:150,9\n117#1:165,3\n*E\n"
)
final class ComposableSingletons$MessageMetadataKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$MessageMetadataKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$MessageMetadataKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C116@3822L569:MessageMetadata.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               764966992,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MessageMetadataKt.lambda-4.<anonymous> (MessageMetadata.kt:116)"
            );
         }

         Modifier var3 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var6 = 6;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var4 = Alignment.Companion.getTopStart();
         boolean var5 = false;
         MeasurePolicy var8 = BoxKt.maybeCachedBoxMeasurePolicy(var4, var5);
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
         ComposerKt.sourceInformationMarkerStart(var24, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var26 = 6 | 112 & var6 >> 6;
         Composer var27 = var24;
         BoxScope var28 = BoxScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -1966701524, "C117@3865L516:MessageMetadata.kt#o19g89");
         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$MessageMetadataKt.INSTANCE.recovered$fd838d44303763da(), var27, 12582912, 127
         );
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
