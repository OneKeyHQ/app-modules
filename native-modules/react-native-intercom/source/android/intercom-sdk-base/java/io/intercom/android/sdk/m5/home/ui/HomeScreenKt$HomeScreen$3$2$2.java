package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.ScrollState;
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
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.AlphaKt;
import androidx.compose.ui.layout.LayoutCoordinates;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.layout.OnGloballyPositionedModifierKt;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.m5.home.states.HomeUiState;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content;
import io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt$HomeScreen$3$2$2\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 IntSize.kt\nandroidx/compose/ui/unit/IntSize\n+ 7 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n*L\n1#1,275:1\n87#2:276\n83#2,10:277\n94#2:318\n81#3,6:287\n88#3,6:302\n96#3:317\n402#4,9:293\n411#4:308\n412#4,2:315\n1047#5,6:309\n59#6:319\n90#7:320\n*S KotlinDebug\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt$HomeScreen$3$2$2\n*L\n171#1:276\n171#1:277,10\n171#1:318\n171#1:287,6\n171#1:302,6\n171#1:317\n171#1:293,9\n171#1:308\n171#1:315,2\n180#1:309,6\n181#1:319\n181#1:320\n*E\n"
)
final class HomeScreenKt$HomeScreen$3$2$2 implements Function3 {
   // $VF: synthetic field
   final HomeUiState $homeState;
   // $VF: synthetic field
   final ScrollState $scrollState;
   // $VF: synthetic field
   final MutableFloatState $headerHeightPx;
   // $VF: synthetic field
   final Function0 $onMessagesClicked;
   // $VF: synthetic field
   final Function0 $onHelpClicked;
   // $VF: synthetic field
   final Function0 $onTicketsClicked;
   // $VF: synthetic field
   final Function1 $onTicketItemClicked;
   // $VF: synthetic field
   final Function0 $onNewConversationClicked;
   // $VF: synthetic field
   final Function1 $onConversationClicked;
   // $VF: synthetic field
   final Function1 $onTicketLinkClicked;

   HomeScreenKt$HomeScreen$3$2$2(
      HomeUiState var1,
      ScrollState var2,
      MutableFloatState var3,
      Function0 var4,
      Function0 var5,
      Function0 var6,
      Function1 var7,
      Function0 var8,
      Function1 var9,
      Function1 var10
   ) {
      this.$homeState = var1;
      this.$scrollState = var2;
      this.$headerHeightPx = var3;
      this.$onMessagesClicked = var4;
      this.$onHelpClicked = var5;
      this.$onTicketsClicked = var6;
      this.$onTicketItemClicked = var7;
      this.$onNewConversationClicked = var8;
      this.$onConversationClicked = var9;
      this.$onTicketLinkClicked = var10;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C170@6993L1289:HomeScreen.kt#ac106p");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(21007876, var3, -1, "io.intercom.android.sdk.m5.home.ui.HomeScreen.<anonymous>.<anonymous>.<anonymous> (HomeScreen.kt:169)");
      }

      if (this.$homeState instanceof HomeUiState$Content) {
         ScrollState var7 = this.$scrollState;
         MutableFloatState var8 = this.$headerHeightPx;
         HomeUiState var9 = this.$homeState;
         Function0 var10 = this.$onMessagesClicked;
         Function0 var11 = this.$onHelpClicked;
         Function0 var12 = this.$onTicketsClicked;
         Function1 var13 = this.$onTicketItemClicked;
         Function0 var14 = this.$onNewConversationClicked;
         Function1 var15 = this.$onConversationClicked;
         Function1 var16 = this.$onTicketLinkClicked;
         int var17 = 0;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var19 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var17 >> 3 | 112 & var17 >> 3);
         Modifier var20 = var4;
         MeasurePolicy var21 = var19;
         Composer var22 = var2;
         int var23 = 112 & var17 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var17 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 287561613, "C179@7447L124,171@7026L627,184@7678L582:HomeScreen.kt#ac106p");
         Modifier var10000 = AlphaKt.alpha(Modifier.Companion, HomeScreenKt.access$getHeaderContentOpacity(var7.getValue(), var8.getFloatValue()));
         var38.startReplaceGroup(1117667200);
         ComposerKt.sourceInformation(var38, "CC(remember):HomeScreen.kt#9igjgp");
         Composer var41 = var38;
         boolean var42 = false;
         int var43 = 0;
         Object var44 = var41.rememberedValue();
         int var45 = 0;
         kotlin.jvm.functions.Function1 var51;
         if (var44 == Composer.Companion.getEmpty()) {
            Modifier var46 = var10000;
            int var47 = 0;
            final androidx.compose.runtime.MutableFloatState recoveredCapture_907_0 = var8;
            var51 = ((kotlin.jvm.functions.Function1) (recoveredArgument_907_0) -> HomeScreenKt$HomeScreen$3$2$2.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_907_0, (androidx.compose.ui.layout.LayoutCoordinates)recoveredArgument_907_0));
            var10000 = var46;
            Object var48 = var51;
            var41.updateRememberedValue(var48);
            var51 = (Function1)var48;
         } else {
            var51 = (Function1)var44;
         }

         Function1 var49 = var51;
         var38.endReplaceGroup();
         HomeHeaderKt.HomeContentHeader(
            OnGloballyPositionedModifierKt.onGloballyPositioned(var10000, var49), ((HomeUiState$Content)var9).getHeader(), var38, 0, 0
         );
         HomeUiState$Content var50 = (HomeUiState$Content)var9;
         HomeContentScreenKt.HomeContentScreen(null, var50, var10, var11, var12, var13, var14, var15, var16, var38, 0, 1);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var2);
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(MutableFloatState var0, LayoutCoordinates var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      long var2 = var1.recovered$244b35ad2fcbefb2();
      int var4 = 0;
      long var5 = var2;
      int var7 = 0;
      var0.setFloatValue((int)(var5 & 4294967295L));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
