package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HomeScreenKt$HomeScreen$3$4$2$1 implements Function3 {
   // $VF: synthetic field
   final BoxScope $this_Box;
   // $VF: synthetic field
   final HomeUiState$Content$ContentHeader$CloseButtonColor $this_with;

   HomeScreenKt$HomeScreen$3$4$2$1(BoxScope var1, HomeUiState$Content$ContentHeader$CloseButtonColor var2) {
      this.$this_Box = var1;
      this.$this_with = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C245@10228L278:HomeScreen.kt#ac106p");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1722206090, var3, -1, "io.intercom.android.sdk.m5.home.ui.HomeScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous> (HomeScreen.kt:245)"
         );
      }

      BoxKt.Box(
         BackgroundKt.recovered$42a3174180b3fb7a(
            this.$this_Box.align(SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), Alignment.Companion.getCenter()),
            ColorExtensionsKt.toComposeColor(this.$this_with.getBackgroundColor(), this.$this_with.getBackgroundOpacity()),
            null,
            2,
            null
         ),
         var2,
         0
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
