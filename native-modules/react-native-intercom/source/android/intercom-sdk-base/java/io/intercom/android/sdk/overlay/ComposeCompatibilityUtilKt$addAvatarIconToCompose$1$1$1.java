package io.intercom.android.sdk.overlay;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Participant;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1$1 implements Function2 {
   // $VF: synthetic field
   final Participant $lastAdmin;

   ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1$1(Participant var1) {
      this.$lastAdmin = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C51@1834L203:ComposeCompatibilityUtil.kt#7idvz0");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2111948784,
               var2,
               -1,
               "io.intercom.android.sdk.overlay.addAvatarIconToCompose.<anonymous>.<anonymous>.<anonymous> (ComposeCompatibilityUtil.kt:51)"
            );
         }

         Avatar var10003 = this.$lastAdmin.getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10003, "getAvatar(...)");
         Boolean var10004 = this.$lastAdmin.isBot();
         Intrinsics.checkNotNullExpressionValue(var10004, "isBot(...)");
         AvatarIconKt.recovered$f5ec3c81e3146789(null, new AvatarWrapper(var10003, var10004), null, false, 0L, null, var1, 0, 61);
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
