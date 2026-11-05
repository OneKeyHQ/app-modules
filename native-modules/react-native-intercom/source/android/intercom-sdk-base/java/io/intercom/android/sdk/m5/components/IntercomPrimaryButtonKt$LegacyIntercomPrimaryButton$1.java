package io.intercom.android.sdk.m5.components;

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
   "SMAP\nIntercomPrimaryButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButtonKt$LegacyIntercomPrimaryButton$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,131:1\n118#2:132\n118#2:133\n*S KotlinDebug\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButtonKt$LegacyIntercomPrimaryButton$1\n*L\n54#1:132\n58#1:133\n*E\n"
)
final class IntercomPrimaryButtonKt$LegacyIntercomPrimaryButton$1 implements Function3 {
   // $VF: synthetic field
   final String $text;
   // $VF: synthetic field
   final Integer $trailingIconId;

   IntercomPrimaryButtonKt$LegacyIntercomPrimaryButton$1(String var1, Integer var2) {
      this.$text = var1;
      this.$trailingIconId = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$LegacyIntercomPrimaryButton");
      ComposerKt.sourceInformation(var2, "C50@1900L10,50@1945L6,48@1835L137,*53@2015L39,55@2099L19,58@2249L6,54@2067L212:IntercomPrimaryButton.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2099872974, var3, -1, "io.intercom.android.sdk.m5.components.LegacyIntercomPrimaryButton.<anonymous> (IntercomPrimaryButton.kt:48)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            this.$text,
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
            TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04(),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$8a5d9ad2993bccce(),
               0L,
               null,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777214,
               null
            ),
            var2,
            0,
            0,
            131070
         );
         if (this.$trailingIconId != null) {
            int var4 = this.$trailingIconId.intValue();
            int var5 = 0;
            Modifier var10000 = Modifier.Companion;
            int var6 = 6;
            int var7 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var6)), var2, 6);
            Painter var10 = PainterResources_androidKt.painterResource(var4, var2, 0);
            Modifier var10002 = Modifier.Companion;
            var6 = 16;
            var7 = 0;
            IconKt.recovered$e00adffb7fe45b02(
               var10,
               null,
               SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var6)),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$8a5d9ad2993bccce(),
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
