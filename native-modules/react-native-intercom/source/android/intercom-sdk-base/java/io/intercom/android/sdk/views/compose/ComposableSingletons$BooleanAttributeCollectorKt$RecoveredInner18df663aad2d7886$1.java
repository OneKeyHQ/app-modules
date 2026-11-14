package io.intercom.android.sdk.views.compose;

import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C199@6614L528:BooleanAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               89644228,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.lambda-4.<anonymous> (BooleanAttributeCollector.kt:199)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$BooleanAttributeCollectorKt.INSTANCE.recovered$fd838d44303763da(), var1, 12582912, 127
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
