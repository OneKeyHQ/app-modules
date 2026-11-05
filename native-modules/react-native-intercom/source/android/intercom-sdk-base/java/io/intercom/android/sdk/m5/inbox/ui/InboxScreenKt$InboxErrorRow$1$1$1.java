package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxErrorRow$1$1$1 implements Function3 {
   // $VF: synthetic field
   final ErrorState $errorState;

   InboxScreenKt$InboxErrorRow$1$1$1(ErrorState var1) {
      this.$errorState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TextButton");
      ComposerKt.sourceInformation(var2, "C286@11017L40,287@11105L10,285@10980L165:InboxScreen.kt#po70xe");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -282010049, var3, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxErrorRow.<anonymous>.<anonymous>.<anonymous> (InboxScreen.kt:285)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(((ErrorState$WithCTA)this.$errorState).getCtaResId(), var2, 0),
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04(),
            var2,
            0,
            0,
            131070
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
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
