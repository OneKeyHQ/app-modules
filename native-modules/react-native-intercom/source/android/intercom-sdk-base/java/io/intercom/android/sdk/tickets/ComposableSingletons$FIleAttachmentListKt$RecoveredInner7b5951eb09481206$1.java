package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.layout.RowScope;
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
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFIleAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$FIleAttachmentListKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,146:1\n118#2:147\n*S KotlinDebug\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/ComposableSingletons$FIleAttachmentListKt$lambda-1$1\n*L\n109#1:147\n*E\n"
)
final class ComposableSingletons$FIleAttachmentListKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$FIleAttachmentListKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$FIleAttachmentListKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$FileAttachment");
      ComposerKt.sourceInformation(var2, "C109@3582L57,111@3729L6,106@3480L276:FIleAttachmentList.kt#3ofz5f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1721837306,
               var3,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$FIleAttachmentListKt.lambda-1.<anonymous> (FIleAttachmentList.kt:106)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var5 = 16;
         int var6 = 0;
         Modifier var4 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var5));
         Painter var8 = PainterResources_androidKt.painterResource(drawable.intercom_ic_alert_circle, var2, 0);
         long var9 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$2bcfe565407cdfb8();
         IconKt.recovered$e00adffb7fe45b02(var8, "Error Icon", var4, var9, var2, 432 | Painter.$stable, 0);
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
