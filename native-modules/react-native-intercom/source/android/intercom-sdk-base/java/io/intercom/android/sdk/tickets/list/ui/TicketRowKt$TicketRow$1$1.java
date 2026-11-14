package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketRow.kt\nio/intercom/android/sdk/tickets/list/ui/TicketRowKt$TicketRow$1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,121:1\n118#2:122\n*S KotlinDebug\n*F\n+ 1 TicketRow.kt\nio/intercom/android/sdk/tickets/list/ui/TicketRowKt$TicketRow$1$1\n*L\n46#1:122\n*E\n"
)
final class TicketRowKt$TicketRow$1$1 implements Function2 {
   // $VF: synthetic field
   final TicketRowData $data;

   TicketRowKt$TicketRow$1$1(TicketRowData var1) {
      this.$data = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C46@1877L41,44@1794L224:TicketRow.kt#yl74uv");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1329606754, var2, -1, "io.intercom.android.sdk.tickets.list.ui.TicketRow.<anonymous>.<anonymous> (TicketRow.kt:44)");
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 8;
         int var5 = 0;
         Modifier var3 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var4));
         Painter var7 = PainterResources_androidKt.painterResource(this.$data.getStatus().getIconRes(), var1, 0);
         long var8 = this.$data.getStatus().recovered$6f15fcf6c761ad06();
         IconKt.recovered$e00adffb7fe45b02(var7, null, var3, var8, var1, 432 | Painter.$stable, 0);
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
