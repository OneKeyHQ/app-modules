package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$InboxScreenKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$InboxScreenKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$InboxScreenKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C248@9968L49,247@9928L163:InboxScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1973363979, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.ComposableSingletons$InboxScreenKt.lambda-2.<anonymous> (InboxScreen.kt:247)"
            );
         }

         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_ic_edit, var1, 0), null, null, 0L, var1, 48 | Painter.$stable, 12
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
