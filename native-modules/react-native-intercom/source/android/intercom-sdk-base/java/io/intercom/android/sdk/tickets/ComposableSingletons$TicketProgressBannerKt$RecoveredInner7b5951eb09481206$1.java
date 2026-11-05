package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketProgressBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketProgressBanner.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$TicketProgressBannerKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,113:1\n1047#2,6:114\n*S KotlinDebug\n*F\n+ 1 TicketProgressBanner.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$TicketProgressBannerKt$lambda-1$1\n*L\n102#1:114,6\n*E\n"
)
final class ComposableSingletons$TicketProgressBannerKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketProgressBannerKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketProgressBannerKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@3787L2,101@3736L70:TicketProgressBanner.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -936846583,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$TicketProgressBannerKt.lambda-1.<anonymous> (TicketProgressBanner.kt:101)"
            );
         }

         String var10000 = "API issue";
         var1.startReplaceGroup(57238520);
         ComposerKt.sourceInformation(var1, "CC(remember):TicketProgressBanner.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function0 var12;
         if (var7 == Composer.Companion.getEmpty()) {
            String var10 = "API issue";
            int var9 = 0;
            var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TicketProgressBannerKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            Object var11 = var12;
            var4.updateRememberedValue(var11);
            var12 = (Function0)var11;
         } else {
            var12 = (Function0)var7;
         }

         Function0 var3 = var12;
         var1.endReplaceGroup();
         TicketProgressBannerKt.TicketProgressBanner(var10000, var3, true, null, var1, 438, 8);
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
