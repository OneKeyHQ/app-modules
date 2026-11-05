package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.ErrorState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxContent$5$1$1$2$1 implements Function3 {
   // $VF: synthetic field
   final ErrorState $errorState;

   InboxScreenKt$InboxContent$5$1$1$2$1(ErrorState var1) {
      this.$errorState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C201@8014L25:InboxScreen.kt#po70xe");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1670986031,
               var3,
               -1,
               "io.intercom.android.sdk.m5.inbox.ui.InboxContent.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (InboxScreen.kt:201)"
            );
         }

         InboxScreenKt.access$InboxErrorRow(this.$errorState, var2, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
