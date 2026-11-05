package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$MessageComposerKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$MessageComposerKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$MessageComposerKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public final void invoke(Function2 var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "innerTextField");
      ComposerKt.sourceInformation(var2, "C304@13319L16:MessageComposer.kt#yb6g9");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1048047703,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.lambda-1.<anonymous> (MessageComposer.kt:304)"
            );
         }

         var1.invoke(var2, 14 & var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((Function2)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
