package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$NetworkError;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$UploadErrorDialogKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$UploadErrorDialogKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$UploadErrorDialogKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C99@3023L177:UploadErrorDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               443341389,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$UploadErrorDialogKt.lambda-4.<anonymous> (UploadErrorDialog.kt:99)"
            );
         }

         UploadErrorDialogKt.UploadErrorDialog(
            new MediaUploadRepository$UploadError$NetworkError(new IOException("Network error")), null, null, null, var1, 0, 14
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
