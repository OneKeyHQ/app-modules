package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPushNotificationsBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$PushNotificationsBannerKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,108:1\n118#2:109\n*S KotlinDebug\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$PushNotificationsBannerKt$lambda-1$1\n*L\n89#1:109\n*E\n"
)
final class ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C86@3260L47,87@3354L39,89@3497L6,85@3220L317:PushNotificationsBanner.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               772935366,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$PushNotificationsBannerKt.lambda-1.<anonymous> (PushNotificationsBanner.kt:85)"
            );
         }

         Painter var10000 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var1, 0);
         String var10001 = StringResources_androidKt.stringResource(string.intercom_close, var1, 0);
         Modifier var10002 = Modifier.Companion;
         int var3 = 24;
         int var4 = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var10000,
            var10001,
            SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060(var3)),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
            var1,
            384 | Painter.$stable,
            0
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
