package io.intercom.android.sdk.m5.home.ui.header;

import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HomeHeaderKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$HomeHeaderKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C181@6919L1716:HomeHeader.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2058941199, var2, -1, "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderKt.lambda-2.<anonymous> (HomeHeader.kt:181)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            Color.Companion.recovered$06f5b36abee3ec99(),
            0L,
            0.0F,
            0.0F,
            null,
            ComposableSingletons$HomeHeaderKt.INSTANCE.recovered$eac478aa65045e5d(),
            var1,
            12583296,
            123
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
