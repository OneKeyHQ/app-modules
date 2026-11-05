package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.foundation.layout.PaddingKt;
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
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTextInputPill.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/TextInputPillKt$TextInputPill$4\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,191:1\n118#2:192\n118#2:193\n*S KotlinDebug\n*F\n+ 1 TextInputPill.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/TextInputPillKt$TextInputPill$4\n*L\n103#1:192\n104#1:193\n*E\n"
)
final class TextInputPillKt$TextInputPill$4 implements Function2 {
   // $VF: synthetic field
   final boolean $showTrailingIcon;

   TextInputPillKt$TextInputPill$4(boolean var1) {
      this.$showTrailingIcon = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C99@4318L65,104@4600L6,98@4282L350:TextInputPill.kt#z2rnlt");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               583948524, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.text.TextInputPill.<anonymous> (TextInputPill.kt:97)"
            );
         }

         if (this.$showTrailingIcon) {
            Painter var10000 = PainterResources_androidKt.painterResource(drawable.intercom_attribute_verified_tick, var1, 0);
            Modifier var10002 = Modifier.Companion;
            int var3 = 16;
            int var4 = 0;
            var10002 = PaddingKt.recovered$eb6a744cd98cdbca(var10002, Dp.recovered$95b3d103663bf060(var3));
            var3 = 24;
            var4 = 0;
            IconKt.recovered$e00adffb7fe45b02(
               var10000,
               "Looks good!",
               SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var3)),
               IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$b549188d2697e772(),
               var1,
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
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
