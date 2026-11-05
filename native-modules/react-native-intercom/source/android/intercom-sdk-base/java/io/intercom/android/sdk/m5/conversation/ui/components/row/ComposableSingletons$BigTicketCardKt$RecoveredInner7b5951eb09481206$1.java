package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.tickets.TicketDetailContentKt;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBigTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BigTicketCardKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,198:1\n1047#2,6:199\n*S KotlinDebug\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$BigTicketCardKt$lambda-1$1\n*L\n161#1:199,6\n*E\n"
)
final class ComposableSingletons$BigTicketCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$BigTicketCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$BigTicketCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C160@7163L2,158@7069L135:BigTicketCard.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1700058823,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BigTicketCardKt.lambda-1.<anonymous> (BigTicketCard.kt:158)"
            );
         }

         TicketDetailState$TicketDetailContentState var10000 = TicketDetailContentKt.getSampleTicketDetailState();
         var1.startReplaceGroup(-1025643687);
         ComposerKt.sourceInformation(var1, "CC(remember):BigTicketCard.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function0 var12;
         if (var7 == Composer.Companion.getEmpty()) {
            TicketDetailState$TicketDetailContentState var10 = var10000;
            int var9 = 0;
            var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$BigTicketCardKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            Object var11 = var12;
            var4.updateRememberedValue(var11);
            var12 = (Function0)var11;
         } else {
            var12 = (Function0)var7;
         }

         Function0 var3 = var12;
         var1.endReplaceGroup();
         BigTicketCardKt.BigTicketCard(var10000, var3, true, null, var1, 432, 8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
