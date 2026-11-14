package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketHeaderKt$TicketHeaderLongTextPreview$1 implements Function2 {
   // $VF: synthetic field
   final TicketStatusHeaderArgs $args;

   TicketHeaderKt$TicketHeaderLongTextPreview$1(TicketStatusHeaderArgs var1) {
      this.$args = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C95@2865L80,92@2778L167:TicketHeader.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               337942641, var2, -1, "io.intercom.android.sdk.m5.components.TicketHeaderLongTextPreview.<anonymous> (TicketHeader.kt:92)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.wrapContentSize$default(Modifier.Companion, null, false, 3, null),
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(-177489460, true, new TicketHeaderKt$TicketHeaderLongTextPreview$1$1(this.$args), var1, 54),
            var1,
            12582918,
            126
         );
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
