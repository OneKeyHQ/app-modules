package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomArticleSearchActivity$subscribeToStates$1 implements Function2 {
   // $VF: synthetic field
   final IntercomArticleSearchActivity this$0;

   IntercomArticleSearchActivity$subscribeToStates$1(IntercomArticleSearchActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C86@3532L977,86@3454L1055:IntercomArticleSearchActivity.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -779899693,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.subscribeToStates.<anonymous> (IntercomArticleSearchActivity.kt:86)"
            );
         }

         Object var10000 = Injector.get().getAppConfigProvider().get();
         Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
         ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
            (AppConfig)var10000,
            ComposableLambdaKt.rememberComposableLambda(-1191058574, true, new IntercomArticleSearchActivity$subscribeToStates$1$1(this.this$0), var1, 54),
            var1,
            48
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
