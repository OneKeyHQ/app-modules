package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$AddFileButtonKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$AddFileButtonKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$AddFileButtonKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C58@2082L56:AddFileButton.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1360176596,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$AddFileButtonKt.lambda-1.<anonymous> (AddFileButton.kt:58)"
            );
         }

         AddFileButtonKt.AddFileButton(null, string.intercom_tickets_add_file, var1, 0, 1);
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
