package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.material3.IconButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$5 implements Function2 {
   // $VF: synthetic field
   final Function0 $onBackClick;

   HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$5(Function0 var1) {
      this.$onBackClick = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C146@6233L331:HelpCenterSearchTopBar.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1996576886, var2, -1, "io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBar.<anonymous> (HelpCenterSearchTopBar.kt:146)"
            );
         }

         IconButtonKt.IconButton(
            this.$onBackClick,
            null,
            false,
            null,
            null,
            null,
            ComposableSingletons$HelpCenterSearchTopBarKt.INSTANCE.recovered$fd838d44303763da(),
            var1,
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
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
