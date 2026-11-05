package io.intercom.android.sdk.post;

import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomPostActivity$onCreate$2 implements Function2 {
   // $VF: synthetic field
   final IntercomPostActivity this$0;

   IntercomPostActivity$onCreate$2(IntercomPostActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C131@5409L21,132@5457L6603,132@5443L6617:IntercomPostActivity.kt#hv1x2k");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               309457934, var2, -1, "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous> (IntercomPostActivity.kt:131)"
            );
         }

         ScrollState var3 = ScrollKt.rememberScrollState(0, var1, 0, 1);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1094919204, true, new IntercomPostActivity$onCreate$2$1(this.this$0, var3), var1, 54),
            var1,
            3072,
            7
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
