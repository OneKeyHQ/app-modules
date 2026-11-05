package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import coil.compose.SubcomposeAsyncImageScope;
import coil.compose.AsyncImagePainter.State.Error;
import io.intercom.android.sdk.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPdfAttachmentBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PdfAttachmentBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$PdfAttachmentBlockKt$lambda-2$1\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,215:1\n70#2:216\n68#2,8:217\n77#2:251\n81#3,6:225\n88#3,6:240\n96#3:250\n402#4,9:231\n411#4:246\n412#4,2:248\n118#5:247\n*S KotlinDebug\n*F\n+ 1 PdfAttachmentBlock.kt\nio/intercom/android/sdk/survey/block/ComposableSingletons$PdfAttachmentBlockKt$lambda-2$1\n*L\n175#1:216\n175#1:217,8\n175#1:251\n175#1:225,6\n175#1:240,6\n175#1:250\n175#1:231,9\n175#1:246\n175#1:248,2\n180#1:247\n*E\n"
)
final class ComposableSingletons$PdfAttachmentBlockKt$RecoveredInner7a24458254dd4cd9$1 implements Function4 {
   public static final ComposableSingletons$PdfAttachmentBlockKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$PdfAttachmentBlockKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(SubcomposeAsyncImageScope var1, Error var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$SubcomposeAsyncImage");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C174@6826L434:PdfAttachmentBlock.kt#jxhu3x");
      if ((var4 & 129) == 128 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -323165503,
               var4,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$PdfAttachmentBlockKt.lambda-2.<anonymous> (PdfAttachmentBlock.kt:174)"
            );
         }

         Modifier var5 = BackgroundKt.recovered$42a3174180b3fb7a(
            Modifier.Companion, Color.recovered$41aa0ee03c2282fb(Color.Companion.recovered$235b30c090a399c5(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null), null, 2, null
         );
         Alignment var6 = Alignment.Companion.getCenter();
         byte var8 = 54;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var7 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var6, var7);
         Modifier var11 = var5;
         MeasurePolicy var12 = var10;
         Composer var13 = var3;
         int var14 = 112 & var8 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         BoxScope var30 = BoxScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -46306464, "C180@7085L51,178@6996L250:PdfAttachmentBlock.kt#jxhu3x");
         Modifier var10000 = Modifier.Companion;
         int var32 = 24;
         int var33 = 0;
         Modifier var34 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var32));
         Painter var37 = PainterResources_androidKt.painterResource(drawable.intercom_ic_reload, var29, 0);
         long var35 = Color.Companion.recovered$95989221dad41c4d();
         IconKt.recovered$e00adffb7fe45b02(var37, "Reload", var34, var35, var29, 3504 | Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((SubcomposeAsyncImageScope)var1, (Error)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
