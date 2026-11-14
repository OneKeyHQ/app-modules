package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$IntercomTopBarKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$IntercomTopBarKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$IntercomTopBarKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C144@4906L470:IntercomTopBar.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1858201762, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomTopBarKt.lambda-5.<anonymous> (IntercomTopBar.kt:144)"
            );
         }

         IntercomTopBarKt.recovered$4ca9f42ae4e1cbf3(
            null, "TopBar Title", null, null, 0L, 0L, null, ComposableSingletons$IntercomTopBarKt.INSTANCE.recovered$a0b9f494198612b5(), var1, 12582960, 125
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
