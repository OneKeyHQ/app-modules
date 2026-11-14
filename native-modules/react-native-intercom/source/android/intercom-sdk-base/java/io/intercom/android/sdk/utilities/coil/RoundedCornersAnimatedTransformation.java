package io.intercom.android.sdk.utilities.coil;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import androidx.compose.runtime.internal.StabilityInferred;
import coil.transform.AnimatedTransformation;
import coil.transform.PixelOpacity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/utilities/coil/RoundedCornersAnimatedTransformation;",
         "Lcoil/transform/AnimatedTransformation;",
         "radius",
         "",
         "<init>",
         "(F)V",
         "getRadius",
         "()F",
         "transform",
         "Lcoil/transform/PixelOpacity;",
         "canvas",
         "Landroid/graphics/Canvas;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class RoundedCornersAnimatedTransformation implements AnimatedTransformation {
   private final float radius;
   public static int $stable;

   static {}

   public RoundedCornersAnimatedTransformation(float var1) {
      this.radius = var1;
   }

   public final float getRadius() {
      return this.radius;
   }

   @NotNull
   @Override
   public PixelOpacity transform(@NotNull Canvas var1) {
      Intrinsics.checkNotNullParameter(var1, "canvas");
      Paint var3 = new Paint(3);
      Paint var4 = var3;
      int var5 = 0;
      var4.setColor(0);
      var4.setXfermode(new PorterDuffXfermode(Mode.SRC));
      Paint var2 = var3;
      Path var8 = new Path();
      Path var10 = var8;
      int var6 = 0;
      var10.setFillType(FillType.INVERSE_EVEN_ODD);
      Path var7 = var8;
      float var9 = var1.getWidth();
      float var11 = var1.getHeight();
      var7.addRoundRect(0.0F, 0.0F, var9, var11, this.radius, this.radius, Direction.CW);
      var1.drawPath(var7, var2);
      return PixelOpacity.TRANSLUCENT;
   }
}
