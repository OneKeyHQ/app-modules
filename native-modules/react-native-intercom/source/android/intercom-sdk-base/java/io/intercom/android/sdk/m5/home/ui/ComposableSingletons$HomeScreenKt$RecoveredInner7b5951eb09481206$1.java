package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.animation.AnimatedVisibilityScope;
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
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.LoadingScreenKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/ComposableSingletons$HomeScreenKt$lambda-1$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,275:1\n87#2:276\n83#2,10:277\n94#2:313\n81#3,6:287\n88#3,6:302\n96#3:312\n402#4,9:293\n411#4:308\n412#4,2:310\n118#5:309\n*S KotlinDebug\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/ComposableSingletons$HomeScreenKt$lambda-1$1\n*L\n151#1:276\n151#1:277,10\n151#1:313\n151#1:287,6\n151#1:302,6\n151#1:312\n151#1:293,9\n151#1:308\n151#1:310,2\n152#1:309\n*E\n"
)
final class ComposableSingletons$HomeScreenKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$HomeScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$HomeScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C150@6218L224:HomeScreen.kt#ac106p");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -613291133, var3, -1, "io.intercom.android.sdk.m5.home.ui.ComposableSingletons$HomeScreenKt.lambda-1.<anonymous> (HomeScreen.kt:150)"
         );
      }

      int var7 = 0;
      int var8 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
      );
      Modifier var4 = Modifier.Companion;
      Vertical var5 = Arrangement.INSTANCE.getTop();
      Horizontal var6 = Alignment.Companion.getStart();
      MeasurePolicy var9 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var7 >> 3 | 112 & var7 >> 3);
      Modifier var10 = var4;
      MeasurePolicy var11 = var9;
      Composer var12 = var2;
      int var13 = 112 & var7 << 3;
      int var14 = 0;
      ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
      CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
      Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
      Function0 var18 = ComposeUiNode.Companion.getConstructor();
      Composer var19 = var12;
      int var20 = 6 | 896 & var13 << 6;
      int var21 = 0;
      ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var19.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var19.startReusableNode();
      if (var19.getInserting()) {
         var19.createNode(var18);
      } else {
         var19.useNode();
      }

      Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
      int var23 = 0;
      Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
      int var24 = 14 & var20 >> 6;
      Composer var25 = var19;
      int var26 = 0;
      ComposerKt.sourceInformationMarkerStart(var25, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
      int var27 = 6 | 112 & var7 >> 6;
      Composer var28 = var25;
      ColumnScope var29 = ColumnScopeInstance.INSTANCE;
      int var30 = 0;
      ComposerKt.sourceInformationMarkerStart(var28, 21665121, "C151@6247L41,152@6309L115:HomeScreen.kt#ac106p");
      Modifier var10000 = Modifier.Companion;
      int var31 = 32;
      int var32 = 0;
      SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var31)), var28, 6);
      LoadingScreenKt.LoadingScreen(null, drawable.intercom_content_loading, false, var28, 0, 5);
      ComposerKt.sourceInformationMarkerEnd(var28);
      ComposerKt.sourceInformationMarkerEnd(var25);
      var19.endNode();
      ComposerKt.sourceInformationMarkerEnd(var19);
      ComposerKt.sourceInformationMarkerEnd(var12);
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
