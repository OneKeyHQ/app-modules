package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChoicePillKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$ChoicePillKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$ChoicePillKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C105@3308L123:ChoicePill.kt#w8l19x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               238848687,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.choice.ComposableSingletons$ChoicePillKt.lambda-3.<anonymous> (ChoicePill.kt:105)"
            );
         }

         long var3 = Color.Companion.recovered$ff90a8751967f2f7();
         ChoicePillKt.recovered$b36a01a674e93ba3(true, null, "Option 1", 0L, 0.0F, var3, null, 0L, var1, 196998, 218);
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
