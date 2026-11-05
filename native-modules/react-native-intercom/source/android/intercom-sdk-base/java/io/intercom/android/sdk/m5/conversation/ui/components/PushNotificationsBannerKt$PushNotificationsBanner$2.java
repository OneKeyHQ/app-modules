package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PushNotificationsBannerKt$PushNotificationsBanner$2 implements Function3 {
   // $VF: synthetic field
   final Function0 $onEnable;
   // $VF: synthetic field
   final Function0 $onClose;

   PushNotificationsBannerKt$PushNotificationsBanner$2(Function0 var1, Function0 var2) {
      this.$onEnable = var1;
      this.$onClose = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C56@1991L6,57@2027L1552,54@1916L1663:PushNotificationsBanner.kt#o19g89");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            1923339635, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.PushNotificationsBanner.<anonymous> (PushNotificationsBanner.kt:54)"
         );
      }

      SurfaceKt.recovered$be3e31d24045ea03(
         this.$onEnable,
         null,
         false,
         null,
         IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$e2b74d614aa09788(),
         0L,
         0.0F,
         0.0F,
         null,
         null,
         ComposableLambdaKt.rememberComposableLambda(-1181560824, true, new PushNotificationsBannerKt$PushNotificationsBanner$2$1(this.$onClose), var2, 54),
         var2,
         0,
         6,
         1006
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
