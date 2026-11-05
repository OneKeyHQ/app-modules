package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.layout.RowScope;
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
final class HelpCenterTopBarKt$HelpCenterTopBar$1 implements Function3 {
   // $VF: synthetic field
   final Function0 $onSearchClick;
   // $VF: synthetic field
   final long $contentColor;

   HelpCenterTopBarKt$HelpCenterTopBar$1(Function0 var1, long var2) {
      this.$onSearchClick = var1;
      this.$contentColor = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TopActionBar");
      ComposerKt.sourceInformation(var2, "C30@1168L263,30@1132L299:HelpCenterTopBar.kt#ngglre");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               736282477, var3, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBar.<anonymous> (HelpCenterTopBar.kt:30)"
            );
         }

         IconButtonKt.IconButton(
            this.$onSearchClick,
            null,
            false,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1957925771, true, new HelpCenterTopBarKt$HelpCenterTopBar$1$1(this.$contentColor), var2, 54),
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
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
