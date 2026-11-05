package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class MessageComposerKt$ComposerActionButton$1 implements Function2 {
   // $VF: synthetic field
   final int $iconRes;

   MessageComposerKt$ComposerActionButton$1(int var1) {
      this.$iconRes = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C416@17570L29,414@17486L163:MessageComposer.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1864507480,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerActionButton.<anonymous> (MessageComposer.kt:414)"
            );
         }

         Modifier var3 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, MessageComposerKt.access$getComposerIconSize$p());
         IconKt.recovered$e00adffb7fe45b02(PainterResources_androidKt.painterResource(this.$iconRes, var1, 0), null, var3, 0L, var1, 432 | Painter.$stable, 8);
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
