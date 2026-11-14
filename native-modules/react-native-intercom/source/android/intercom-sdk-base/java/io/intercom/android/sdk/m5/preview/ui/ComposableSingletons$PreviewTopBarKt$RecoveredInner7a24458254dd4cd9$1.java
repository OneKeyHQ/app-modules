package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPreviewTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$PreviewTopBarKt$lambda-2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,108:1\n118#2:109\n*S KotlinDebug\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$PreviewTopBarKt$lambda-2$1\n*L\n61#1:109\n*E\n"
)
final class ComposableSingletons$PreviewTopBarKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$PreviewTopBarKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$PreviewTopBarKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C58@2013L46,57@1973L221:PreviewTopBar.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -853830478, var2, -1, "io.intercom.android.sdk.m5.preview.ui.ComposableSingletons$PreviewTopBarKt.lambda-2.<anonymous> (PreviewTopBar.kt:57)"
            );
         }

         Painter var10000 = PainterResources_androidKt.painterResource(drawable.intercom_ic_delete, var1, 0);
         Modifier var10002 = Modifier.Companion;
         int var3 = 24;
         int var4 = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var10000, "Delete", SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var3)), 0L, var1, 432 | Painter.$stable, 8
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
