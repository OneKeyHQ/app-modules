package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomTopBarKt$lambda-3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,160:1\n118#2:161\n*S KotlinDebug\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomTopBarKt$lambda-3$1\n*L\n151#1:161\n*E\n"
)
final class ComposableSingletons$IntercomTopBarKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$IntercomTopBarKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$IntercomTopBarKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C149@5087L53,151@5244L6,148@5047L286:IntercomTopBar.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               323337919, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomTopBarKt.lambda-3.<anonymous> (IntercomTopBar.kt:148)"
            );
         }

         Painter var3 = PainterResources_androidKt.painterResource(drawable.intercom_ic_download, var1, 0);
         Modifier var10000 = Modifier.Companion;
         int var5 = 24;
         int var6 = 0;
         Modifier var4 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var5));
         long var7 = IntercomTheme.INSTANCE.getColors(var1, 6).recovered$156c30ff4ea0f1e5();
         IconKt.recovered$e00adffb7fe45b02(var3, null, var4, var7, var1, 432 | Painter.$stable, 0);
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
