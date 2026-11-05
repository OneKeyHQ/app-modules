package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$IconWithIndicator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HomeItemKt$RecoveredInner0c8347b84394e1a9$1 implements Function2 {
   public static final ComposableSingletons$HomeItemKt$RecoveredInner0c8347b84394e1a9$1 INSTANCE = new ComposableSingletons$HomeItemKt$RecoveredInner0c8347b84394e1a9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C161@5618L183:HomeItem.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1852008205, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$HomeItemKt.lambda-6.<anonymous> (HomeItem.kt:161)"
            );
         }

         HomeItemKt.HomeItem(
            null, drawable.intercom_ticket_detail_icon, null, null, null, "Tickets", null, HomeItemBadge$IconWithIndicator.INSTANCE, null, var1, 12779520, 349
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
