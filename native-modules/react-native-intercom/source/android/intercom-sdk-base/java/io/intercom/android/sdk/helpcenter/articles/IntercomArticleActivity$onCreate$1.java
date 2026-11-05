package io.intercom.android.sdk.helpcenter.articles;

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
final class IntercomArticleActivity$onCreate$1 implements Function2 {
   // $VF: synthetic field
   final IntercomArticleActivity this$0;

   IntercomArticleActivity$onCreate$1(IntercomArticleActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C76@3318L10002,76@3304L10016:IntercomArticleActivity.kt#1wdz33");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1674700077, var2, -1, "io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.onCreate.<anonymous> (IntercomArticleActivity.kt:76)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-199442729, true, new IntercomArticleActivity$onCreate$1$1(this.this$0), var1, 54),
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
