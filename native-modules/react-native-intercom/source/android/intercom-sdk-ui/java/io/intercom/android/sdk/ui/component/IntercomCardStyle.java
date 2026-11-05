package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\bÇ\u0002\u0018\u00002\u00020\u0001:\u0001\u0015B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003JK\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\tH\u0007¢\u0006\u0004\b\u0010\u0010\u0011JA\u0010\u0012\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007¢\u0006\u0004\b\u0013\u0010\u0014¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/ui/component/IntercomCardStyle;",
         "",
         "<init>",
         "()V",
         "defaultStyle",
         "Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "elevation",
         "Landroidx/compose/ui/unit/Dp;",
         "border",
         "Landroidx/compose/foundation/BorderStroke;",
         "shadowColor",
         "defaultStyle-qUnfpCA",
         "(Landroidx/compose/ui/graphics/Shape;JJFLandroidx/compose/foundation/BorderStroke;JLandroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;",
         "conversationCardStyle",
         "conversationCardStyle-PEIptTM",
         "(Landroidx/compose/ui/graphics/Shape;JJFLandroidx/compose/foundation/BorderStroke;Landroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;",
         "Style",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nIntercomCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomCard.kt\nio/intercom/android/sdk/ui/component/IntercomCardStyle\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n118#2:129\n118#2:130\n118#2:131\n118#2:132\n118#2:133\n*S KotlinDebug\n*F\n+ 1 IntercomCard.kt\nio/intercom/android/sdk/ui/component/IntercomCardStyle\n*L\n97#1:129\n98#1:130\n114#1:131\n117#1:132\n118#1:133\n*E\n"
)
public final class IntercomCardStyle {
   @NotNull
   public static final IntercomCardStyle INSTANCE = new IntercomCardStyle();
   public static int $stable;

   static {}

   private IntercomCardStyle() {
   }

   @Composable
   @NotNull
   public final IntercomCardStyle$Style recovered$24b4efdd2276d096(
      @Nullable Shape var1, long var2, long var4, float var6, @Nullable BorderStroke var7, long var8, @Nullable Composer var10, int var11, int var12
   ) {
      var10.startReplaceGroup(-952876659);
      ComposerKt.sourceInformation(
         var10,
         "C(defaultStyle)P(5,0:c#ui.graphics.Color,2:c#ui.graphics.Color,3:c#ui.unit.Dp!,4:c#ui.graphics.Color)93@3245L6,94@3307L6,95@3369L6,97@3492L6,98@3578L6:IntercomCard.kt#yr602f"
      );
      if ((var12 & 1) != 0) {
         var1 = IntercomTheme.INSTANCE.getShapes(var10, 6).getMedium();
      }

      if ((var12 & 2) != 0) {
         var2 = IntercomTheme.INSTANCE.getColors(var10, 6).getBase().recovered$00296b21a1edce05();
      }

      if ((var12 & 4) != 0) {
         var4 = IntercomTheme.INSTANCE.getColors(var10, 6).getText().recovered$e01a1d3b7b68e21a();
      }

      if ((var12 & 8) != 0) {
         int var13 = 6;
         int var14 = 0;
         var6 = Dp.recovered$95b3d103663bf060(var13);
      }

      if ((var12 & 16) != 0) {
         int var15 = 1;
         int var17 = 0;
         var7 = BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060(var15),
            Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var10, 6).getBorder().recovered$e2b74d614aa09788(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null
            )
         );
      }

      if ((var12 & 32) != 0) {
         var8 = IntercomTheme.INSTANCE.getColors(var10, 6).recovered$f6e9b4ba3d2c0c59();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-952876659, var11, -1, "io.intercom.android.sdk.ui.component.IntercomCardStyle.defaultStyle (IntercomCard.kt:99)");
      }

      IntercomCardStyle$Style var16 = new IntercomCardStyle$Style(var1, var2, var4, var6, var7, Color.recovered$f46ef195ad4a2363(var8), null);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var10.endReplaceGroup();
      return var16;
   }

   @Composable
   @NotNull
   public final IntercomCardStyle$Style recovered$b8822edb5fda39fd(
      @Nullable Shape var1, long var2, long var4, float var6, @Nullable BorderStroke var7, @Nullable Composer var8, int var9, int var10
   ) {
      var8.startReplaceGroup(-1707188824);
      ComposerKt.sourceInformation(
         var8, "C(conversationCardStyle)P(4,0:c#ui.graphics.Color,2:c#ui.graphics.Color,3:c#ui.unit.Dp)114@4043L6,115@4105L6,117@4228L6:IntercomCard.kt#yr602f"
      );
      if ((var10 & 1) != 0) {
         int var11 = 20;
         int var12 = 0;
         var1 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var11));
      }

      if ((var10 & 2) != 0) {
         var2 = IntercomTheme.INSTANCE.getColors(var8, 6).getBase().recovered$00296b21a1edce05();
      }

      if ((var10 & 4) != 0) {
         var4 = IntercomTheme.INSTANCE.getColors(var8, 6).getText().recovered$e01a1d3b7b68e21a();
      }

      if ((var10 & 8) != 0) {
         int var13 = 0;
         int var16 = 0;
         var6 = Dp.recovered$95b3d103663bf060(var13);
      }

      if ((var10 & 16) != 0) {
         int var14 = 1;
         int var17 = 0;
         var7 = BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060(var14), IntercomTheme.INSTANCE.getColors(var8, 6).getBorder().recovered$e2b74d614aa09788()
         );
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1707188824, var9, -1, "io.intercom.android.sdk.ui.component.IntercomCardStyle.conversationCardStyle (IntercomCard.kt:118)");
      }

      IntercomCardStyle$Style var15 = new IntercomCardStyle$Style(var1, var2, var4, var6, var7, null, null);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var8.endReplaceGroup();
      return var15;
   }
}
