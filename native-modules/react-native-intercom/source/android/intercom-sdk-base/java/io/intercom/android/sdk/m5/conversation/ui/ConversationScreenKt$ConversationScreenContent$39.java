package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreenContent$39 implements Function2 {
   public static final ConversationScreenKt$ConversationScreenContent$39 INSTANCE = new ConversationScreenKt$ConversationScreenContent$39();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final WindowInsets invoke(Composer var1, int var2) {
      var1.startReplaceGroup(-1068952746);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1068952746, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous> (ConversationScreen.kt:580)"
         );
      }

      WindowInsets var3 = WindowInsetsKt.WindowInsets(0, 0, 0, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((Composer)var1, ((Number)var2).intValue());
   }
}
