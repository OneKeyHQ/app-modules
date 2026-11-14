package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.TextKt;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBigTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt$BigTicketCard$3\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,198:1\n87#2:199\n83#2,10:200\n94#2:236\n81#3,6:210\n88#3,6:225\n96#3:235\n402#4,9:216\n411#4:231\n412#4,2:233\n118#5:232\n*S KotlinDebug\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt$BigTicketCard$3\n*L\n82#1:199\n82#1:200,10\n82#1:236\n82#1:210,6\n82#1:225,6\n82#1:235\n82#1:216,9\n82#1:231\n82#1:233,2\n90#1:232\n*E\n"
)
final class BigTicketCardKt$BigTicketCard$3 implements Function3 {
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final TicketDetailState$TicketDetailContentState $ticketDetailState;
   // $VF: synthetic field
   final Context $context;

   BigTicketCardKt$BigTicketCard$3(Function0 var1, TicketDetailState$TicketDetailContentState var2, Context var3) {
      this.$onClick = var1;
      this.$ticketDetailState = var2;
      this.$context = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C81@3350L3613:BigTicketCard.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -915811879, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCard.<anonymous> (BigTicketCard.kt:81)"
         );
      }

      Horizontal var6 = Alignment.Companion.getCenterHorizontally();
      Function0 var7 = this.$onClick;
      TicketDetailState$TicketDetailContentState var8 = this.$ticketDetailState;
      Context var9 = this.$context;
      int var10 = 384;
      int var11 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
      );
      Modifier var4 = Modifier.Companion;
      Vertical var5 = Arrangement.INSTANCE.getTop();
      MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var10 >> 3 | 112 & var10 >> 3);
      Modifier var13 = var4;
      MeasurePolicy var14 = var12;
      Composer var15 = var2;
      int var16 = 112 & var10 << 3;
      int var17 = 0;
      ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
      int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
      CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
      Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
      Function0 var21 = ComposeUiNode.Companion.getConstructor();
      Composer var22 = var15;
      int var23 = 6 | 896 & var16 << 6;
      int var24 = 0;
      ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
      if (!(var22.getApplier() instanceof Applier)) {
         ComposablesKt.invalidApplier();
      }

      var22.startReusableNode();
      if (var22.getInserting()) {
         var22.createNode(var21);
      } else {
         var22.useNode();
      }

      Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
      int var26 = 0;
      Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
      Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
      Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
      Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
      Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
      int var27 = 14 & var23 >> 6;
      Composer var28 = var22;
      int var29 = 0;
      ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
      int var30 = 6 | 112 & var10 >> 6;
      Composer var31 = var28;
      ColumnScope var32 = ColumnScopeInstance.INSTANCE;
      int var33 = 0;
      ComposerKt.sourceInformationMarkerStart(
         var31, -597068031, "C85@3475L50,86@3565L10,84@3446L151,91@3784L23,92@3823L3130,88@3610L3343:BigTicketCard.kt#hr3bi5"
      );
      TextKt.recovered$5b114ab32a4f4190(
         StringResources_androidKt.stringResource(string.intercom_your_ticket, var31, 0),
         null,
         0L,
         null,
         0L,
         null,
         null,
         null,
         0L,
         null,
         null,
         0L,
         0,
         false,
         0,
         0,
         null,
         IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04(),
         var31,
         0,
         0,
         131070
      );
      Modifier var10000 = Modifier.Companion;
      int var34 = 14;
      int var35 = 0;
      float var10001 = Dp.recovered$95b3d103663bf060(var34);
      var34 = 12;
      var35 = 0;
      Modifier var36 = PaddingKt.recovered$904403cea4c49d95(var10000, var10001, Dp.recovered$95b3d103663bf060(var34));
      Style var38 = IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var31, IntercomCardStyle.$stable << 15, 31);
      IntercomCardKt.IntercomCard(
         var7,
         var36,
         false,
         var38,
         null,
         ComposableLambdaKt.rememberComposableLambda(-1554241908, true, new BigTicketCardKt$BigTicketCard$3$1$1(var8, var9), var31, 54),
         var31,
         196656 | Style.$stable << 9,
         20
      );
      ComposerKt.sourceInformationMarkerEnd(var31);
      ComposerKt.sourceInformationMarkerEnd(var28);
      var22.endNode();
      ComposerKt.sourceInformationMarkerEnd(var22);
      ComposerKt.sourceInformationMarkerEnd(var15);
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
