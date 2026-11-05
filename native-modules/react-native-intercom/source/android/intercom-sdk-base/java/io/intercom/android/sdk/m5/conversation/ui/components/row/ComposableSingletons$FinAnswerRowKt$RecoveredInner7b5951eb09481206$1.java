package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFinAnswerRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,538:1\n118#2:539\n*S KotlinDebug\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$FinAnswerRowKt$lambda-1$1\n*L\n286#1:539\n*E\n"
)
final class ComposableSingletons$FinAnswerRowKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$FinAnswerRowKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$FinAnswerRowKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C286@11569L49,287@11661L6,288@11721L60,284@11480L320:FinAnswerRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               333413702,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.lambda-1.<anonymous> (FinAnswerRow.kt:284)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 14;
         int var5 = 0;
         Modifier var3 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4));
         Painter var8 = PainterResources_androidKt.painterResource(drawable.intercom_ic_info, var1, 0);
         long var9 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122();
         String var7 = StringResources_androidKt.stringResource(string.intercom_ai_answer_information, var1, 0);
         IconKt.recovered$e00adffb7fe45b02(var8, var7, var3, var9, var1, 384 | Painter.$stable, 0);
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
