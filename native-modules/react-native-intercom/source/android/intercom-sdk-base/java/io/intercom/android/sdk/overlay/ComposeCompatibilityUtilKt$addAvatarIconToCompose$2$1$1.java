package io.intercom.android.sdk.overlay;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposeCompatibilityUtilKt$addAvatarIconToCompose$2$1$1 implements Function2 {
   // $VF: synthetic field
   final Part $part;

   ComposeCompatibilityUtilKt$addAvatarIconToCompose$2$1$1(Part var1) {
      this.$part = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C72@2355L56:ComposeCompatibilityUtil.kt#7idvz0");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1854253020,
               var2,
               -1,
               "io.intercom.android.sdk.overlay.addAvatarIconToCompose.<anonymous>.<anonymous>.<anonymous> (ComposeCompatibilityUtil.kt:72)"
            );
         }

         AvatarIconKt.recovered$f5ec3c81e3146789(null, PartExtensionsKt.resolvedAvatarWrapper(this.$part), null, false, 0L, null, var1, 0, 61);
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
