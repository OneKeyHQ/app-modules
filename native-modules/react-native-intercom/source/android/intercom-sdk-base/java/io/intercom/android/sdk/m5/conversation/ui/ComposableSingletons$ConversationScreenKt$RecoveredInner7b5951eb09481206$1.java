package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.material3.SnackbarData;
import androidx.compose.material3.SnackbarKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ConversationScreenKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$ConversationScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ConversationScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(SnackbarData var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "it");
      ComposerKt.sourceInformation(var2, "C652@28600L6,653@28666L6,650@28501L238:ConversationScreen.kt#fnibcj");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1373546622,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.ComposableSingletons$ConversationScreenKt.lambda-1.<anonymous> (ConversationScreen.kt:650)"
            );
         }

         SnackbarKt.recovered$f239bf84b88b2b3e(
            var1,
            null,
            false,
            IntercomTheme.INSTANCE.getShapes(var2, IntercomTheme.$stable).getSmall(),
            0L,
            0L,
            ColorExtensionsKt.recovered$4576896f541dc33e(IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$202acdf3dbb5c020()),
            0L,
            0L,
            var2,
            14 & var4,
            438
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((SnackbarData)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
