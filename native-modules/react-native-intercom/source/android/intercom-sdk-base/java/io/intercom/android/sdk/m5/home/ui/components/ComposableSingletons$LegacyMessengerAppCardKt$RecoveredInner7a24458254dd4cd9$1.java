package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C125@5213L97:LegacyMessengerAppCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -570136522,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$LegacyMessengerAppCardKt.lambda-2.<anonymous> (LegacyMessengerAppCard.kt:125)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$LegacyMessengerAppCardKt.INSTANCE.recovered$eac478aa65045e5d(), var1, 3072, 7);
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
