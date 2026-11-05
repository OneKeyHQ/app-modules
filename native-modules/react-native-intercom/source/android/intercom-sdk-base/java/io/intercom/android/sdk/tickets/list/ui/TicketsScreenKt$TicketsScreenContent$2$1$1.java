package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt$TicketsScreenContent$2$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,147:1\n1047#2,6:148\n118#3:154\n*S KotlinDebug\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt$TicketsScreenContent$2$1$1\n*L\n98#1:148,6\n101#1:154\n*E\n"
)
final class TicketsScreenKt$TicketsScreenContent$2$1$1 implements Function4 {
   // $VF: synthetic field
   final TicketsScreenUiState$Content $uiState;
   // $VF: synthetic field
   final Function1 $onClick;

   TicketsScreenKt$TicketsScreenContent$2$1$1(TicketsScreenUiState$Content var1, Function1 var2) {
      this.$uiState = var1;
      this.$onClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$items");
      ComposerKt.sourceInformation(var3, "C*97@3829L20,96@3768L133,100@3918L64:TicketsScreen.kt#yl74uv");
      int var5 = var4;
      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if ((var5 & 145) == 144 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               264752689, var5, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreenContent.<anonymous>.<anonymous>.<anonymous> (TicketsScreen.kt:95)"
            );
         }

         TicketRowData var10000 = (TicketRowData)this.$uiState.getLazyPagingTickets().get(var2);
         if (var10000 != null) {
            TicketRowData var6 = var10000;
            Function1 var7 = this.$onClick;
            TicketRowData var8 = var6;
            int var9 = 0;
            Modifier var26 = Modifier.Companion;
            boolean var10001 = false;
            Object var10002 = null;
            Object var10003 = null;
            Object var10004 = null;
            var3.startReplaceGroup(-309133317);
            ComposerKt.sourceInformation(var3, "CC(remember):TicketsScreen.kt#9igjgp");
            Composer var10 = var3;
            boolean var11 = var3.changed(var7) | var3.changed(var8);
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
               Modifier var19 = var26;
               int var20 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_1617_0 = var7;
               final io.intercom.android.sdk.tickets.list.ui.TicketRowData recoveredCapture_1617_1 = var8;
               Function0 var21 = ((kotlin.jvm.functions.Function0) () -> TicketsScreenKt$TicketsScreenContent$2$1$1.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_1617_0, recoveredCapture_1617_1));
               var26 = var19;
               var10001 = var18;
               var10002 = var17;
               var10003 = var16;
               var10004 = var15;
               Object var22 = var21;
               var10.updateRememberedValue(var22);
               var10005 = var22;
            }

            Function0 var23 = (Function0)var10005;
            var3.endReplaceGroup();
            TicketRowKt.TicketRow(
               ClickableKt.recovered$1c9660c41fd850dc(var26, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var23, 15, null),
               var8,
               null,
               false,
               var3,
               0,
               12
            );
            Modifier var27 = Modifier.Companion;
            int var25 = 20;
            int var24 = 0;
            IntercomDividerKt.IntercomDivider(PaddingKt.recovered$f3b9f2e94c07c806(var27, Dp.recovered$95b3d103663bf060(var25), 0.0F, 2, null), var3, 6, 0);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Function1 var0, TicketRowData var1) {
      var0.invoke(var1.getId());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
