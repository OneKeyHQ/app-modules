package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nQuickReplies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$QuickReplies$1$1$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,144:1\n118#2:145\n*S KotlinDebug\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$QuickReplies$1$1$2\n*L\n54#1:145\n*E\n"
)
final class QuickRepliesKt$QuickReplies$1$1$2 implements Function3 {
   // $VF: synthetic field
   final QuickReply $it;

   QuickRepliesKt$QuickReplies$1$1$2(QuickReply var1) {
      this.$it = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C55@2426L6,56@2489L10,52@2255L326:QuickReplies.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1399332631,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReplies.<anonymous>.<anonymous>.<anonymous> (QuickReplies.kt:52)"
            );
         }

         Companion var5 = Modifier.Companion;
         int var7 = 12;
         int var8 = 0;
         float var6 = Dp.recovered$95b3d103663bf060(var7);
         var8 = 16;
         int $i$f$getDpx = 0;
         float var12 = Dp.recovered$95b3d103663bf060(var8);
         Modifier var4 = PaddingKt.recovered$904403cea4c49d95(var5, var12, var6);
         String var10 = this.$it.getText();
         long var11 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextStyle var14 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04Point5();
         $i$f$getDpx = TextAlign.Companion.recovered$d707b9dd64876179();
         TextKt.recovered$5b114ab32a4f4190(
            var10,
            var4,
            var11,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd($i$f$getDpx),
            0L,
            0,
            false,
            0,
            0,
            null,
            var14,
            var2,
            0,
            0,
            130040
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
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
