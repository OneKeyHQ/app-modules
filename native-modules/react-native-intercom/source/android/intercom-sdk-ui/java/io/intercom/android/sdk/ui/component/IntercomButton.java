package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bÇ\u0002\u0018\u00002\u00020\u0001:\u0001\u000fB\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J-\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007¢\u0006\u0004\b\u000b\u0010\fJ-\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007¢\u0006\u0004\b\u000e\u0010\f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/ui/component/IntercomButton;",
         "",
         "<init>",
         "()V",
         "primaryStyle",
         "Lio/intercom/android/sdk/ui/component/IntercomButton$Style;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "border",
         "Landroidx/compose/foundation/BorderStroke;",
         "primaryStyle-Klgx-Pg",
         "(JJLandroidx/compose/foundation/BorderStroke;Landroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/ui/component/IntercomButton$Style;",
         "outlinedStyle",
         "outlinedStyle-Klgx-Pg",
         "Style",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButton\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n118#2:129\n118#2:130\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/IntercomButton\n*L\n71#1:129\n82#1:130\n*E\n"
)
public final class IntercomButton {
   @NotNull
   public static final IntercomButton INSTANCE = new IntercomButton();
   public static int $stable;

   static {}

   private IntercomButton() {
   }

   @Composable
   @NotNull
   public final IntercomButton$Style recovered$bae4000e51c7826f(long var1, long var3, @Nullable BorderStroke var5, @Nullable Composer var6, int var7, int var8) {
      var6.startReplaceGroup(-155594647);
      ComposerKt.sourceInformation(var6, "C(primaryStyle)P(0:c#ui.graphics.Color,2:c#ui.graphics.Color)68@2231L6,69@2293L6,70@2386L6:IntercomButton.kt#yr602f");
      if ((var8 & 1) != 0) {
         var1 = IntercomTheme.INSTANCE.getColors(var6, 6).getBase().recovered$00296b21a1edce05();
      }

      if ((var8 & 2) != 0) {
         var3 = IntercomTheme.INSTANCE.getColors(var6, 6).getText().recovered$e01a1d3b7b68e21a();
      }

      if ((var8 & 4) != 0) {
         int var9 = 1;
         int var10 = 0;
         var5 = BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060(var9), IntercomTheme.INSTANCE.getColors(var6, 6).getBorder().recovered$e2b74d614aa09788()
         );
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-155594647, var7, -1, "io.intercom.android.sdk.ui.component.IntercomButton.primaryStyle (IntercomButton.kt:71)");
      }

      IntercomButton$Style var11 = new IntercomButton$Style(var1, var3, var5, null);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var6.endReplaceGroup();
      return var11;
   }

   @Composable
   @NotNull
   public final IntercomButton$Style recovered$6b6eaea65c87a6df(long var1, long var3, @Nullable BorderStroke var5, @Nullable Composer var6, int var7, int var8) {
      var6.startReplaceGroup(-1228695891);
      ComposerKt.sourceInformation(var6, "C(outlinedStyle)P(0:c#ui.graphics.Color,2:c#ui.graphics.Color)79@2637L6,80@2699L6,81@2792L6:IntercomButton.kt#yr602f");
      if ((var8 & 1) != 0) {
         var1 = IntercomTheme.INSTANCE.getColors(var6, 6).getBase().recovered$00296b21a1edce05();
      }

      if ((var8 & 2) != 0) {
         var3 = IntercomTheme.INSTANCE.getColors(var6, 6).getText().recovered$e01a1d3b7b68e21a();
      }

      if ((var8 & 4) != 0) {
         int var9 = 1;
         int var10 = 0;
         var5 = BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060(var9), IntercomTheme.INSTANCE.getColors(var6, 6).getBorder().recovered$e2b74d614aa09788()
         );
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1228695891, var7, -1, "io.intercom.android.sdk.ui.component.IntercomButton.outlinedStyle (IntercomButton.kt:82)");
      }

      IntercomButton$Style var11 = new IntercomButton$Style(var1, var3, var5, null);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var6.endReplaceGroup();
      return var11;
   }
}
