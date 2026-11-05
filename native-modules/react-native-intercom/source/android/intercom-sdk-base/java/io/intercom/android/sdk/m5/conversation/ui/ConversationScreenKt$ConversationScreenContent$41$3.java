package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.material3.SnackbarHostKt;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreenContent$41$3 implements Function2 {
   // $VF: synthetic field
   final SnackbarHostState $snackbarHostState;

   ConversationScreenKt$ConversationScreenContent$41$3(SnackbarHostState var1) {
      this.$snackbarHostState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C649@28435L322:ConversationScreen.kt#fnibcj");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               672061103, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous>.<anonymous> (ConversationScreen.kt:649)"
            );
         }

         SnackbarHostKt.SnackbarHost(
            this.$snackbarHostState, null, ComposableSingletons$ConversationScreenKt.INSTANCE.recovered$b26556cd5c386b55(), var1, 384, 2
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
