package io.intercom.android.sdk.ui.component;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.FloatingActionButtonKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nJumpToBottom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,137:1\n118#2:138\n118#2:139\n118#2:172\n70#3:140\n67#3,9:141\n77#3:176\n81#4,6:150\n88#4,6:165\n96#4:175\n402#5,9:156\n411#5:171\n412#5,2:173\n*S KotlinDebug\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$2$1\n*L\n74#1:138\n76#1:139\n82#1:172\n71#1:140\n71#1:141,9\n71#1:176\n71#1:150,6\n71#1:165,6\n71#1:175\n71#1:156,9\n71#1:171\n71#1:173,2\n*E\n"
)
final class JumpToBottomKt$JumpToBottom$2$1 implements Function3 {
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final long $backgroundColor;
   // $VF: synthetic field
   final long $contentColor;

   JumpToBottomKt$JumpToBottom$2$1(Function0 var1, long var2, long var4) {
      this.$onClick = var1;
      this.$backgroundColor = var2;
      this.$contentColor = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C76@3001L6,70@2766L939:JumpToBottom.kt#yr602f");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(600504953, var3, -1, "io.intercom.android.sdk.ui.component.JumpToBottom.<anonymous>.<anonymous> (JumpToBottom.kt:70)");
      }

      Modifier var10000 = PaddingKt.recovered$eb6a744cd98cdbca(Modifier.Companion, JumpToBottomKt.getJumpBottomPadding());
      int var4 = 48;
      int var5 = 0;
      var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
      var4 = 1;
      var5 = 0;
      Modifier var40 = BorderKt.recovered$1227359600bc50fd(
         var10000,
         Dp.recovered$95b3d103663bf060(var4),
         IntercomTheme.INSTANCE.getColors(var2, 6).recovered$9e30ba45ae499588(),
         RoundedCornerShapeKt.RoundedCornerShape(50)
      );
      Function0 var7 = this.$onClick;
      long var8 = this.$backgroundColor;
      long var10 = this.$contentColor;
      int var12 = 0;
      int var13 = 0;
      ComposerKt.sourceInformationMarkerStart(var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
      Alignment var42 = Alignment.Companion.getTopStart();
      boolean var6 = false;
      MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var42, var6);
      Modifier var15 = var40;
      MeasurePolicy var16 = var14;
      Composer var17 = var2;
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
      ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
      int var32 = 6 | 112 & var12 >> 6;
      Composer var33 = var30;
      BoxScope var34 = BoxScopeInstance.INSTANCE;
      int var35 = 0;
      ComposerKt.sourceInformationMarkerStart(var33, -341607096, "C85@3366L325,80@3128L563:JumpToBottom.kt#yr602f");
      var10000 = Modifier.Companion;
      int $this$dp$ivx = 48;
      int $i$f$getDpx = 0;
      Modifier var38 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
      RoundedCornerShape var43 = RoundedCornerShapeKt.RoundedCornerShape(50);
      FloatingActionButtonKt.recovered$b101bfb21479b857(
         var7,
         var38,
         var43,
         var8,
         0L,
         null,
         null,
         ComposableLambdaKt.rememberComposableLambda(2122957953, true, new JumpToBottomKt$JumpToBottom$2$1$1$1(var10), var33, 54),
         var33,
         12582960,
         112
      );
      ComposerKt.sourceInformationMarkerEnd(var33);
      ComposerKt.sourceInformationMarkerEnd(var30);
      var24.endNode();
      ComposerKt.sourceInformationMarkerEnd(var24);
      ComposerKt.sourceInformationMarkerEnd(var17);
      ComposerKt.sourceInformationMarkerEnd(var2);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
