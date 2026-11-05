package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
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
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTypingIndicator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypingIndicator.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$TypingIndicatorKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,174:1\n118#2:175\n87#3:176\n83#3,10:177\n94#3:212\n81#4,6:187\n88#4,6:202\n96#4:211\n402#5,9:193\n411#5,3:208\n*S KotlinDebug\n*F\n+ 1 TypingIndicator.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$TypingIndicatorKt$lambda-1$1\n*L\n129#1:175\n128#1:176\n128#1:177,10\n128#1:212\n128#1:187,6\n128#1:202,6\n128#1:211\n128#1:193,9\n128#1:208,3\n*E\n"
)
final class ComposableSingletons$TypingIndicatorKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TypingIndicatorKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TypingIndicatorKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C127@4537L961:TypingIndicator.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -800155812,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$TypingIndicatorKt.lambda-1.<anonymous> (TypingIndicator.kt:127)"
            );
         }

         int var4 = 16;
         int var5 = 0;
         Vertical var30 = Arrangement.INSTANCE.recovered$6fba629a41b87d9d(Dp.recovered$95b3d103663bf060(var4), Alignment.Companion.getCenterVertically());
         Horizontal var31 = Alignment.Companion.getStart();
         short var6 = 432;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var3 = Modifier.Companion;
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var30, var31, var1, 14 & var6 >> 3 | 112 & var6 >> 3);
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
         ComposerKt.sourceInformationMarkerStart(var27, 1656533268, "C131@4727L370,141@5115L369:TypingIndicator.kt#hr3bi5");
         Avatar var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         TypingIndicatorKt.recovered$891ffcbb9c232a50(
            null, new CurrentlyTypingState(new AvatarWrapper(var10005, false), false, null, TypingIndicatorType.ADMIN, 6, null), 0.0F, var27, 0, 5
         );
         var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         TypingIndicatorKt.recovered$891ffcbb9c232a50(
            null, new CurrentlyTypingState(new AvatarWrapper(var10005, true), false, null, TypingIndicatorType.ADMIN, 6, null), 0.0F, var27, 0, 5
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
