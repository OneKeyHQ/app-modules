package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.Ticket$Status;
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
   "SMAP\nRecentTicketsCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$RecentTicketsCardKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,81:1\n1047#2,6:82\n*S KotlinDebug\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$RecentTicketsCardKt$lambda-1$1\n*L\n77#1:82,6\n*E\n"
)
final class ComposableSingletons$RecentTicketsCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$RecentTicketsCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$RecentTicketsCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C76@2737L2,43@1406L1344:RecentTicketsCard.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1530175639, var2, -1, "io.intercom.android.sdk.tickets.ComposableSingletons$RecentTicketsCardKt.lambda-1.<anonymous> (RecentTicketsCard.kt:43)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         String var10001 = "Recent tickets";
         Ticket[] var3 = new Ticket[4];
         Ticket$Status var4 = new Ticket$Status("Waiting on you", "waiting_on_customer", null, false, 0L, 28, null);
         var3[0] = new Ticket("1", "1200", "This is a ticket title", null, null, null, var4, null, null, 0L, null, null, null, false, 0L, 24504, null);
         var4 = new Ticket$Status("Submitted", "submitted", null, false, 0L, 28, null);
         var3[1] = new Ticket("2", "1201", "Bug", null, null, null, var4, null, null, 0L, null, null, null, true, 0L, 24504, null);
         var4 = new Ticket$Status("In progress", "in_progress", null, false, 0L, 28, null);
         var3[2] = new Ticket("3", "1202", "Feature Request", null, null, null, var4, null, null, 0L, null, null, null, true, 0L, 24504, null);
         var4 = new Ticket$Status("Resolved", "resolved", null, false, 0L, 28, null);
         var3[3] = new Ticket("4", "1204", "Unresolvable", null, null, null, var4, null, null, 0L, null, null, null, false, 0L, 24504, null);
         List var10002 = CollectionsKt.listOf(var3);
         var1.startReplaceGroup(673428457);
         ComposerKt.sourceInformation(var1, "CC(remember):RecentTicketsCard.kt#9igjgp");
         Composer var18 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var18.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            List var12 = var10002;
            String var11 = "Recent tickets";
            Modifier var10 = var10000;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1517_0) -> ComposableSingletons$RecentTicketsCardKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_1517_0));
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var19 = var13;
            var18.updateRememberedValue(var19);
            var10003 = var19;
         } else {
            var10003 = var7;
         }

         Function1 var14 = (Function1)var10003;
         var1.endReplaceGroup();
         RecentTicketsCardKt.RecentTicketsCard(var10000, var10001, var10002, var14, var1, 3126, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0) {
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
