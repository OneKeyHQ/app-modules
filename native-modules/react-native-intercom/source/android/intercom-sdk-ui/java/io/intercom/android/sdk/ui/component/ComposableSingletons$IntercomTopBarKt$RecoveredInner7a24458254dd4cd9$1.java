package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$IntercomTopBarKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$IntercomTopBarKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$IntercomTopBarKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C134@4726L61:IntercomTopBar.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               697934641, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomTopBarKt.lambda-2.<anonymous> (IntercomTopBar.kt:134)"
            );
         }

         IntercomTopBarKt.recovered$4ca9f42ae4e1cbf3(null, "TopBar Title", null, null, 0L, 0L, null, null, var1, 48, 253);
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
