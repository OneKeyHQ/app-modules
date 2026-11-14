package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$CreateTicketCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$CreateTicketCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@4235L214:CreateTicketCard.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1627499370,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketCardKt.lambda-1.<anonymous> (CreateTicketCard.kt:101)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         Block var10003 = CreateTicketCardKt.access$getSampleBlock$p();
         Intrinsics.checkNotNullExpressionValue(var10003, "access$getSampleBlock$p(...)");
         CreateTicketCardKt.CreateTicketCard(var10000, new BlockRenderData(var10003, null, null, null, null, 30, null), true, null, var1, 390, 8);
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
