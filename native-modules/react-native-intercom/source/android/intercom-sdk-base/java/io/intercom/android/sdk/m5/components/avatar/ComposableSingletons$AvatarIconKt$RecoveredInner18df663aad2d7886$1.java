package io.intercom.android.sdk.m5.components.avatar;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import io.intercom.android.sdk.m5.shapes.OverlappedAvatarShape;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nAvatarIcon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/ComposableSingletons$AvatarIconKt$lambda-4$1\n+ 2 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,454:1\n99#2:455\n95#2,10:456\n106#2:496\n81#3,6:466\n88#3,6:481\n96#3:495\n402#4,9:472\n411#4:487\n412#4,2:493\n118#5:488\n118#5:489\n118#5:490\n118#5:491\n118#5:492\n*S KotlinDebug\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/ComposableSingletons$AvatarIconKt$lambda-4$1\n*L\n415#1:455\n415#1:456,10\n415#1:496\n415#1:466,6\n415#1:481,6\n415#1:495\n415#1:472,9\n415#1:487\n415#1:493,2\n418#1:488\n420#1:489\n424#1:490\n427#1:491\n429#1:492\n*E\n"
)
final class ComposableSingletons$AvatarIconKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$AvatarIconKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$AvatarIconKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C414@13414L755:AvatarIcon.kt#ssa3oh");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1717030665, var2, -1, "io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.lambda-4.<anonymous> (AvatarIcon.kt:414)"
            );
         }

         Horizontal var4 = Arrangement.INSTANCE.getSpaceEvenly();
         int var6 = 48;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var3 = Modifier.Companion;
         Vertical var5 = Alignment.Companion.getTop();
         MeasurePolicy var8 = RowKt.rowMeasurePolicy(var4, var5, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
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
         ComposerKt.sourceInformationMarkerStart(var24, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var26 = 6 | 112 & var6 >> 6;
         Composer var27 = var24;
         RowScope var28 = RowScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -768424162, "C415@13481L307,423@13801L40,424@13854L305:AvatarIcon.kt#ssa3oh");
         Avatar var10002 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10002, "create(...)");
         AvatarWrapper var30 = new AvatarWrapper(var10002, false, 2, null);
         Modifier var10000 = Modifier.Companion;
         int var31 = 36;
         int var32 = 0;
         Modifier var33 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var31));
         int $this$dp$ivx = 4;
         int $i$f$getDpx = 0;
         float var41 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         RoundedCornerShape var46 = RoundedCornerShapeKt.getCircleShape();
         OverlappedAvatarShape var38 = new OverlappedAvatarShape(var46, null, var41, 2, null);
         AvatarIconKt.recovered$f5ec3c81e3146789(var33, var30, var38, false, 0L, null, var27, 6, 56);
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var27, 6);
         var10002 = Avatar.create("", "");
         Intrinsics.checkNotNullExpressionValue(var10002, "create(...)");
         var30 = new AvatarWrapper(var10002, false, 2, null);
         var10000 = Modifier.Companion;
         var31 = 36;
         var32 = 0;
         var33 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var31));
         $this$dp$ivx = 4;
         $i$f$getDpx = 0;
         float var43 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         RoundedCornerShape var48 = RoundedCornerShapeKt.getCircleShape();
         OverlappedAvatarShape var40 = new OverlappedAvatarShape(var48, null, var43, 2, null);
         AvatarIconKt.recovered$f5ec3c81e3146789(var33, var30, var40, false, 0L, null, var27, 6, 56);
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
