package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.home.data.HomeCardType;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData;
import io.intercom.android.sdk.m5.home.data.TicketLink;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketLinksCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketLinksCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ComposableSingletons$TicketLinksCardKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,115:1\n1047#2,6:116\n*S KotlinDebug\n*F\n+ 1 TicketLinksCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ComposableSingletons$TicketLinksCardKt$lambda-1$1\n*L\n112#1:116,6\n*E\n"
)
final class ComposableSingletons$TicketLinksCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketLinksCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketLinksCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C111@4565L2,90@3754L813:TicketLinksCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -249385569,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$TicketLinksCardKt.lambda-1.<anonymous> (TicketLinksCard.kt:90)"
            );
         }

         TicketLink[] var4 = new TicketLink[]{
            new TicketLink(1L, "Billing issue", "", 0, TicketType.Companion.getNULL()), new TicketLink(2L, "Bug", "", 1, TicketType.Companion.getNULL())
         };
         List var3 = CollectionsKt.listOf(var4);
         HomeCardType var12 = HomeCardType.TICKET_LINKS;
         HomeCards$HomeTicketLinksData var10000 = new HomeCards$HomeTicketLinksData("Create a ticket", var12, var3);
         var1.startReplaceGroup(-1344773304);
         ComposerKt.sourceInformation(var1, "CC(remember):TicketLinksCard.kt#9igjgp");
         Composer var13 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var13.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function1 var15;
         if (var7 == Composer.Companion.getEmpty()) {
            HomeCards$HomeTicketLinksData var10 = var10000;
            int var9 = 0;
            var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_912_0) -> ComposableSingletons$TicketLinksCardKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_912_0));
            var10000 = var10;
            Object var14 = var15;
            var13.updateRememberedValue(var14);
            var15 = (Function1)var14;
         } else {
            var15 = (Function1)var7;
         }

         Function1 var11 = var15;
         var1.endReplaceGroup();
         TicketLinksCardKt.TicketLinksCard(var10000, var11, var1, 48);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
