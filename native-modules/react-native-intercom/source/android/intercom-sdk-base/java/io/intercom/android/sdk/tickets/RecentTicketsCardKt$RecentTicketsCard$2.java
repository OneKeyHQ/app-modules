package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.list.reducers.TicketRowReducerKt;
import io.intercom.android.sdk.tickets.list.ui.TicketRowData;
import io.intercom.android.sdk.tickets.list.ui.TicketRowKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nRecentTicketsCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/RecentTicketsCardKt$RecentTicketsCard$2\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,81:1\n1869#2:82\n1870#2:90\n1047#3,6:83\n118#4:89\n*S KotlinDebug\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/RecentTicketsCardKt$RecentTicketsCard$2\n*L\n27#1:82\n27#1:90\n31#1:83,6\n32#1:89\n*E\n"
)
final class RecentTicketsCardKt$RecentTicketsCard$2 implements Function2 {
   // $VF: synthetic field
   final List $tickets;
   // $VF: synthetic field
   final Function1 $onClick;

   RecentTicketsCardKt$RecentTicketsCard$2(List var1, Function1 var2) {
      this.$tickets = var1;
      this.$onClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C*30@1065L18,27@951L345:RecentTicketsCard.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1499488214, var2, -1, "io.intercom.android.sdk.tickets.RecentTicketsCard.<anonymous> (RecentTicketsCard.kt:26)");
         }

         Iterable var3 = this.$tickets;
         Function1 var4 = this.$onClick;
         int var5 = 0;

         for (Object var7 : var3) {
            Ticket var8 = (Ticket)var7;
            int var9 = 0;
            Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            boolean var10001 = false;
            Object var10002 = null;
            Object var10003 = null;
            Object var10004 = null;
            var1.startReplaceGroup(1295134481);
            ComposerKt.sourceInformation(var1, "CC(remember):RecentTicketsCard.kt#9igjgp");
            Composer var10 = var1;
            boolean var11 = var1.changed(var4) | var1.changedInstance(var8);
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10005;
            if (!var11 && var13 != Composer.Companion.getEmpty()) {
               var10005 = var13;
            } else {
               Object var15 = null;
               Object var16 = null;
               Object var17 = null;
               boolean var18 = false;
               Modifier var19 = var10000;
               int var20 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1529_0 = var4;
               final io.intercom.android.sdk.models.Ticket recoveredExpressionCapture_1529_1 = var8;
               Function0 var21 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.RecentTicketsCardKt$RecentTicketsCard$2.invoke$lambda$2$lambda$1$lambda$0(recoveredExpressionCapture_1529_0, recoveredExpressionCapture_1529_1));
               var10000 = var19;
               var10001 = var18;
               var10002 = var17;
               var10003 = var16;
               var10004 = var15;
               Object var22 = var21;
               var10.updateRememberedValue(var22);
               var10005 = var22;
            }

            Function0 var23 = (Function0)var10005;
            var1.endReplaceGroup();
            Modifier var24 = ClickableKt.recovered$1c9660c41fd850dc(
               var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var23, 15, null
            );
            int var27 = 8;
            int var29 = 0;
            float var25 = Dp.recovered$95b3d103663bf060(var27);
            int $this$dp$ivx = 20;
            int $i$f$getDpx = 0;
            PaddingValues var32 = PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060($this$dp$ivx), var25);
            TicketRowData var26 = TicketRowReducerKt.reduceTicketRowData(var8);
            Boolean var33 = var8.isRead();
            boolean var28 = var33 != null ? !var33 : false;
            TicketRowKt.TicketRow(var24, var26, var32, var28, var1, 0, 0);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Function1 var0, Ticket var1) {
      var0.invoke(var1.getId());
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
