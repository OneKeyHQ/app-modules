package io.intercom.android.sdk.m5.conversation.ui.components;

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
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHeaderMenuItemRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,208:1\n118#2:209\n*S KotlinDebug\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2$1\n*L\n102#1:209\n*E\n"
)
final class HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2$1 implements Function2 {
   // $VF: synthetic field
   final int $icon;
   // $VF: synthetic field
   final String $label;
   // $VF: synthetic field
   final long $tint;

   HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2$1(int var1, String var2, long var3) {
      this.$icon = var1;
      this.$label = var2;
      this.$tint = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C102@3299L26,100@3218L195:HeaderMenuItemRow.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               896985929,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemRow.<anonymous>.<anonymous> (HeaderMenuItemRow.kt:100)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 24;
         int var5 = 0;
         Modifier var3 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(this.$icon, var1, 0), this.$label, var3, this.$tint, var1, 384 | Painter.$stable, 0
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
