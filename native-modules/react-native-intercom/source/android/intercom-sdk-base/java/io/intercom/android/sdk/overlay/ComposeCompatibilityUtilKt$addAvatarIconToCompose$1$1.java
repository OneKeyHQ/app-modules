package io.intercom.android.sdk.overlay;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import io.intercom.android.sdk.models.Participant;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1 implements Function2 {
   // $VF: synthetic field
   final AppConfig $appConfig;
   // $VF: synthetic field
   final Participant $lastAdmin;

   ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1(AppConfig var1, Participant var2) {
      this.$appConfig = var1;
      this.$lastAdmin = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C50@1816L235,50@1767L284:ComposeCompatibilityUtil.kt#7idvz0");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               421691537, var2, -1, "io.intercom.android.sdk.overlay.addAvatarIconToCompose.<anonymous>.<anonymous> (ComposeCompatibilityUtil.kt:50)"
            );
         }

         ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
            this.$appConfig,
            ComposableLambdaKt.rememberComposableLambda(
               2111948784, true, new ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1$1(this.$lastAdmin), var1, 54
            ),
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
