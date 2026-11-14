package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.Context;
import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.components.PoweredByBadgeKt;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$PoweredByClicked;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$None;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PoweredByBadgeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PrivacyNoticeBadgeState;
import io.intercom.android.sdk.models.PoweredBy;
import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.LinkOpener;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt$ConversationBottomBar$10$1$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,297:1\n1047#2,6:298\n118#3:304\n*S KotlinDebug\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt$ConversationBottomBar$10$1$4\n*L\n200#1:298,6\n219#1:304\n*E\n"
)
final class ConversationBottomBarKt$ConversationBottomBar$10$1$4 implements Function3 {
   // $VF: synthetic field
   final BottomBarUiState $bottomBarUiState;
   // $VF: synthetic field
   final Function0 $onPrivacyNoticeDismissed;
   // $VF: synthetic field
   final ColumnScope $this_Column;
   // $VF: synthetic field
   final Function1 $trackMetric;
   // $VF: synthetic field
   final Context $context;

   ConversationBottomBarKt$ConversationBottomBar$10$1$4(BottomBarUiState var1, Function0 var2, ColumnScope var3, Function1 var4, Context var5) {
      this.$bottomBarUiState = var1;
      this.$onPrivacyNoticeDismissed = var2;
      this.$this_Column = var3;
      this.$trackMetric = var4;
      this.$context = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C:ConversationBottomBar.kt#yb6g9");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -2117134110,
            var3,
            -1,
            "io.intercom.android.sdk.m5.conversation.ui.components.composer.ConversationBottomBar.<anonymous>.<anonymous>.<anonymous> (ConversationBottomBar.kt:194)"
         );
      }

      BottomBarUiState$BottomBadgeState var4 = this.$bottomBarUiState.getBottomBadge();
      if (var4 instanceof BottomBarUiState$BottomBadgeState$PoweredByBadgeState) {
         var2.startReplaceGroup(-248218357);
         ComposerKt.sourceInformation(var2, "*208@10586L6,199@10052L394,197@9897L839");
         PoweredBy var5 = ((BottomBarUiState$BottomBadgeState$PoweredByBadgeState)this.$bottomBarUiState.getBottomBadge()).getPoweredBy();
         ColumnScope var6 = this.$this_Column;
         Function1 var7 = this.$trackMetric;
         Context var8 = this.$context;
         PoweredBy var9 = var5;
         int var10 = 0;
         Modifier var11 = var6.align(Modifier.Companion, Alignment.Companion.getCenterHorizontally());
         long var12 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var14 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         String var16 = var9.getText();
         PoweredBy$PoweredByIconType var17 = var9.getIcon();
         String var10000 = var16;
         PoweredBy$PoweredByIconType var10001 = var17;
         var2.startReplaceGroup(-809150988);
         ComposerKt.sourceInformation(var2, "CC(remember):ConversationBottomBar.kt#9igjgp");
         Composer var18 = var2;
         boolean var19 = var2.changed(var7) | var2.changed(var9) | var2.changedInstance(var8);
         int var20 = 0;
         Object var21 = var18.rememberedValue();
         int var22 = 0;
         Object var10002;
         if (!var19 && var21 != Composer.Companion.getEmpty()) {
            var10002 = var21;
         } else {
            PoweredBy$PoweredByIconType var23 = var17;
            String var24 = var16;
            int var25 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_574_0 = var7;
            final io.intercom.android.sdk.models.PoweredBy recoveredCapture_574_1 = var9;
            final android.content.Context recoveredCapture_574_2 = var8;
            Function0 var26 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt$ConversationBottomBar$10$1$4.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_574_0, recoveredCapture_574_1, recoveredCapture_574_2));
            var10000 = var24;
            var10001 = var23;
            Object var27 = var26;
            var18.updateRememberedValue(var27);
            var10002 = var27;
         }

         Function0 var28 = (Function0)var10002;
         var2.endReplaceGroup();
         PoweredByBadgeKt.recovered$9b47502c50dedaf9(var10000, var10001, var28, var11, var12, var14, var2, 24576, 0);
         var2.endReplaceGroup();
      } else if (var4 instanceof BottomBarUiState$BottomBadgeState$PrivacyNoticeBadgeState) {
         var2.startReplaceGroup(-247153476);
         ComposerKt.sourceInformation(var2, "216@10912L291");
         String var33 = ((BottomBarUiState$BottomBadgeState$PrivacyNoticeBadgeState)this.$bottomBarUiState.getBottomBadge()).getText();
         Modifier var34 = Modifier.Companion;
         int var29 = 16;
         int var31 = 0;
         float var35 = Dp.recovered$95b3d103663bf060(var29);
         var29 = 4;
         var31 = 0;
         PrivacyPolicyKt.PrivacyPolicy(
            var33, PaddingKt.recovered$904403cea4c49d95(var34, var35, Dp.recovered$95b3d103663bf060(var29)), this.$onPrivacyNoticeDismissed, var2, 48, 0
         );
         var2.endReplaceGroup();
      } else {
         if (!(var4 instanceof BottomBarUiState$BottomBadgeState$None)) {
            var2.startReplaceGroup(-1254936283);
            var2.endReplaceGroup();
            throw new NoWhenBranchMatchedException();
         }

         var2.startReplaceGroup(-246749515);
         var2.endReplaceGroup();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Function1 var0, PoweredBy var1, Context var2) {
      var0.invoke(MetricData$PoweredByClicked.INSTANCE);
      LinkOpener.handleUrl(var1.getLinkUrl(), var2, Injector.get().getApi());
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
