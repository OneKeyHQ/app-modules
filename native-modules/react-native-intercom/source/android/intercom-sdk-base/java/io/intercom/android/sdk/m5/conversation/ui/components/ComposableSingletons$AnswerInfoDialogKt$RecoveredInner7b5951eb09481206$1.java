package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.AiAnswerInfo;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$AnswerInfoDialogKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$AnswerInfoDialogKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$AnswerInfoDialogKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C88@2976L233:AnswerInfoDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               125395865,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$AnswerInfoDialogKt.lambda-1.<anonymous> (AnswerInfoDialog.kt:88)"
            );
         }

         AnswerInfoDialogKt.AnswerInfoDialog(
            new AiAnswerInfo("This answer was auto generated using AI. There’s a chance it may not be fully correct."), true, null, null, var1, 48, 12
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
