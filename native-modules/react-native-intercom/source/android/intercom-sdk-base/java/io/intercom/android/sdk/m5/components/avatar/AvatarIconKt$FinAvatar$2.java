package io.intercom.android.sdk.m5.components.avatar;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import coil.compose.SubcomposeAsyncImageScope;
import coil.compose.AsyncImagePainter.State.Error;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AvatarIconKt$FinAvatar$2 implements Function4 {
   // $VF: synthetic field
   final AvatarWrapper $avatarWrapper;
   // $VF: synthetic field
   final Modifier $roundedModifier;

   AvatarIconKt$FinAvatar$2(AvatarWrapper var1, Modifier var2) {
      this.$avatarWrapper = var1;
      this.$roundedModifier = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(SubcomposeAsyncImageScope var1, Error var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$SubcomposeAsyncImage");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C242@8459L150:AvatarIcon.kt#ssa3oh");
      if ((var4 & 129) == 128 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-818047861, var4, -1, "io.intercom.android.sdk.m5.components.avatar.FinAvatar.<anonymous> (AvatarIcon.kt:242)");
         }

         AvatarIconKt.access$FinAvatarPlaceholder(this.$avatarWrapper, this.$roundedModifier, 0.0F, var3, 0, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((SubcomposeAsyncImageScope)var1, (Error)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
