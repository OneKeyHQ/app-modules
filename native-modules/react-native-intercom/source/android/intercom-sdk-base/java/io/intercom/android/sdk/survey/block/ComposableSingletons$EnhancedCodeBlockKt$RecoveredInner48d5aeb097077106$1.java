package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner48d5aeb097077106$1 implements Function2 {
   public static final ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner48d5aeb097077106$1 INSTANCE = new ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner48d5aeb097077106$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C484@18250L364:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               260429005,
               var2,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.lambda-13.<anonymous> (EnhancedCodeBlock.kt:484)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$ce33619ac1c358ef(), var1, 3072, 7);
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
