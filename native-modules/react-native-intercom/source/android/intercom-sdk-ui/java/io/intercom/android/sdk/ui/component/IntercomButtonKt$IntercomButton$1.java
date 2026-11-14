package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButtonKt$IntercomButton$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n118#2:129\n118#2:130\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButtonKt$IntercomButton$1\n*L\n47#1:129\n51#1:130\n*E\n"
)
final class IntercomButtonKt$IntercomButton$1 implements Function3 {
   // $VF: synthetic field
   final IntercomButton$Style $style;
   // $VF: synthetic field
   final String $text;
   // $VF: synthetic field
   final Integer $trailingIconId;

   IntercomButtonKt$IntercomButton$1(IntercomButton$Style var1, String var2, Integer var3) {
      this.$style = var1;
      this.$text = var2;
      this.$trailingIconId = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$OutlinedButton");
      ComposerKt.sourceInformation(var2, "C42@1528L10,40@1463L133,*46@1639L39,48@1723L19,47@1691L223:IntercomButton.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(905924137, var3, -1, "io.intercom.android.sdk.ui.component.IntercomButton.<anonymous> (IntercomButton.kt:40)");
         }

         TextStyle var4 = IntercomTheme.INSTANCE.getTypography(var2, 6).getType04();
         long var5 = this.$style.recovered$6557fe71dfd7d063();
         TextKt.recovered$5b114ab32a4f4190(
            this.$text, null, var5, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var4, var2, 0, 0, 131066
         );
         if (this.$trailingIconId != null) {
            Integer var6 = this.$trailingIconId;
            IntercomButton$Style var7 = this.$style;
            int var8 = var6.intValue();
            int var9 = 0;
            Modifier var10000 = Modifier.Companion;
            int var10 = 6;
            int var11 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var10)), var2, 6);
            Painter var14 = PainterResources_androidKt.painterResource(var8, var2, 0);
            Modifier var10002 = Modifier.Companion;
            var10 = 16;
            var11 = 0;
            IconKt.recovered$e00adffb7fe45b02(
               var14,
               null,
               SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var10)),
               var7.recovered$6557fe71dfd7d063(),
               var2,
               432 | Painter.$stable,
               0
            );
         }

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
