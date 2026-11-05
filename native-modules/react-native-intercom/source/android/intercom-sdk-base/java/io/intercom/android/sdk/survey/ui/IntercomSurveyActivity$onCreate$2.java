package io.intercom.android.sdk.survey.ui;

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
final class IntercomSurveyActivity$onCreate$2 implements Function2 {
   // $VF: synthetic field
   final IntercomSurveyActivity this$0;

   IntercomSurveyActivity$onCreate$2(IntercomSurveyActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C62@2540L885,60@2431L994:IntercomSurveyActivity.kt#gv1svo");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -179321000, var2, -1, "io.intercom.android.sdk.survey.ui.IntercomSurveyActivity.onCreate.<anonymous> (IntercomSurveyActivity.kt:60)"
            );
         }

         Object var10000 = Injector.get().getAppConfigProvider().get();
         Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
         ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
            (AppConfig)var10000,
            ComposableLambdaKt.rememberComposableLambda(-2107771943, true, new IntercomSurveyActivity$onCreate$2$1(this.this$0), var1, 54),
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
