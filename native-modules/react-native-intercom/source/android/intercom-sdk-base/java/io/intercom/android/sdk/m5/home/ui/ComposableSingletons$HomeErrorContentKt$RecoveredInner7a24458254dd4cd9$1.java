package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HomeErrorContentKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$HomeErrorContentKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$HomeErrorContentKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C27@784L49:HomeErrorContent.kt#ac106p");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -394516799, var2, -1, "io.intercom.android.sdk.m5.home.ui.ComposableSingletons$HomeErrorContentKt.lambda-2.<anonymous> (HomeErrorContent.kt:27)"
            );
         }

         HomeErrorContentKt.HomeErrorContent(new ErrorState$WithoutCTA(0, 0, null, 7, null), null, var1, 0, 2);
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
