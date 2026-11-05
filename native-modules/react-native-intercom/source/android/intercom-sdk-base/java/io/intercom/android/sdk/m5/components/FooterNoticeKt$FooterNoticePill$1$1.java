package io.intercom.android.sdk.m5.components;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FooterNoticeKt$FooterNoticePill$1$1 implements Function3 {
   // $VF: synthetic field
   final String $title;
   // $VF: synthetic field
   final List $avatars;
   // $VF: synthetic field
   final Function0 $onClick;

   FooterNoticeKt$FooterNoticePill$1$1(String var1, List var2, Function0 var3) {
      this.$title = var1;
      this.$avatars = var2;
      this.$onClick = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C56@2121L148:FooterNotice.kt#arqjag");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1063955783, var3, -1, "io.intercom.android.sdk.m5.components.FooterNoticePill.<anonymous>.<anonymous> (FooterNotice.kt:56)"
         );
      }

      FooterNoticeKt.access$FooterNoticePillWithoutAnimation(this.$title, this.$avatars, this.$onClick, var2, 0);
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
