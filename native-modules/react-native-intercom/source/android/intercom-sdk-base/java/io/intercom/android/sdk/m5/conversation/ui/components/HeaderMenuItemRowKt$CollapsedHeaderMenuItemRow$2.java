package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2 implements Function3 {
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final int $icon;
   // $VF: synthetic field
   final String $label;
   // $VF: synthetic field
   final long $tint;

   HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2(Function0 var1, int var2, String var3, long var4) {
      this.$onClick = var1;
      this.$icon = var2;
      this.$label = var3;
      this.$tint = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BadgedBox");
      ComposerKt.sourceInformation(var2, "C99@3204L219,97@3151L272:HeaderMenuItemRow.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1598560935, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemRow.<anonymous> (HeaderMenuItemRow.kt:97)"
            );
         }

         IconButtonKt.IconButton(
            this.$onClick,
            null,
            false,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               896985929, true, new HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2$1(this.$icon, this.$label, this.$tint), var2, 54
            ),
            var2,
            1572864,
            62
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
