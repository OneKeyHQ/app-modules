package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPulsatingBox.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$PulsatingBoxKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,79:1\n118#2:80\n*S KotlinDebug\n*F\n+ 1 PulsatingBox.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$PulsatingBoxKt$lambda-1$1\n*L\n73#1:80\n*E\n"
)
final class ComposableSingletons$PulsatingBoxKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$PulsatingBoxKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PulsatingBoxKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$PulsatingBox");
      ComposerKt.sourceInformation(var2, "C70@2226L134:PulsatingBox.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -188171614, var3, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$PulsatingBoxKt.lambda-1.<anonymous> (PulsatingBox.kt:70)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 100;
         int var5 = 0;
         BoxKt.Box(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var4)), Color.Companion.recovered$95989221dad41c4d(), null, 2, null
            ),
            var2,
            6
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
      this.invoke((BoxScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
