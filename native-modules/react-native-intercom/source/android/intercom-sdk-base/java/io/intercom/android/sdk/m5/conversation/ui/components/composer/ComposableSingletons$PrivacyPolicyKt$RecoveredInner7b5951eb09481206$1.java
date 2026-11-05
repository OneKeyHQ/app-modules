package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$PrivacyPolicyKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$PrivacyPolicyKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PrivacyPolicyKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C73@2766L278:PrivacyPolicy.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -9267072,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$PrivacyPolicyKt.lambda-1.<anonymous> (PrivacyPolicy.kt:73)"
            );
         }

         PrivacyPolicyKt.PrivacyPolicy(
            "By chatting with us, you agree to the monitoring and recording of this chat to improve its service as described in our <a href=\"https://google.com\" rel=\"nofollow noopener noreferrer\" target=\"_blank\">Privacy Policy</a>",
            null,
            null,
            var1,
            6,
            6
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
