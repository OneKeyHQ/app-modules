package io.intercom.android.sdk.m5;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomRootActivity$onCreate$1 implements Function2 {
   // $VF: synthetic field
   final IntercomRootActivity this$0;

   IntercomRootActivity$onCreate$1(IntercomRootActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C36@1547L89,34@1438L198:IntercomRootActivity.kt#uy8qak");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1535831366, var2, -1, "io.intercom.android.sdk.m5.IntercomRootActivity.onCreate.<anonymous> (IntercomRootActivity.kt:34)"
            );
         }

         Object var10000 = Injector.get().getAppConfigProvider().get();
         Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
         ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
            (AppConfig)var10000,
            ComposableLambdaKt.rememberComposableLambda(-1535408283, true, new IntercomRootActivity$onCreate$1$1(this.this$0), var1, 54),
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
