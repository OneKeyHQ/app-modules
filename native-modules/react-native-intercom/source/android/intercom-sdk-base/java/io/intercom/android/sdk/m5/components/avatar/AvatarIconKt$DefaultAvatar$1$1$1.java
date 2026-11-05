package io.intercom.android.sdk.m5.components.avatar;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import coil.compose.SubcomposeAsyncImageScope;
import coil.compose.AsyncImagePainter.State.Loading;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AvatarIconKt$DefaultAvatar$1$1$1 implements Function4 {
   // $VF: synthetic field
   final AvatarWrapper $avatarWrapper;
   // $VF: synthetic field
   final long $textColor;
   // $VF: synthetic field
   final long $placeHolderTextSize;

   AvatarIconKt$DefaultAvatar$1$1$1(AvatarWrapper var1, long var2, long var4) {
      this.$avatarWrapper = var1;
      this.$textColor = var2;
      this.$placeHolderTextSize = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(SubcomposeAsyncImageScope var1, Loading var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$SubcomposeAsyncImage");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C185@6507L103:AvatarIcon.kt#ssa3oh");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var5 & 131) == 130 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1513639009, var5, -1, "io.intercom.android.sdk.m5.components.avatar.DefaultAvatar.<anonymous>.<anonymous>.<anonymous> (AvatarIcon.kt:185)"
            );
         }

         AvatarIconKt.access$DefaultAvatar_Rd90Nhg$Placeholder(
            this.$avatarWrapper, this.$textColor, this.$placeHolderTextSize, var1.align(Modifier.Companion, Alignment.Companion.getCenter()), var3, 0, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((SubcomposeAsyncImageScope)var1, (Loading)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
