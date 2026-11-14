package io.intercom.android.sdk.m5.shapes;

import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.geometry.Size;
import androidx.compose.ui.graphics.AndroidPath_androidKt;
import androidx.compose.ui.graphics.Outline;
import androidx.compose.ui.graphics.OutlineKt;
import androidx.compose.ui.graphics.Path;
import androidx.compose.ui.graphics.PathOperation;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.Outline.Generic;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J'\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016¢\u0006\u0004\b\u0010\u0010\u0011J7\u0010\u0012\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\rH\u0002¢\u0006\u0004\b\u0018\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/shapes/CutAvatarWithIndicatorShape;",
         "Landroidx/compose/ui/graphics/Shape;",
         "shape",
         "indicatorSize",
         "Landroidx/compose/ui/unit/Dp;",
         "<init>",
         "(Landroidx/compose/ui/graphics/Shape;FLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "F",
         "createOutline",
         "Landroidx/compose/ui/graphics/Outline;",
         "size",
         "Landroidx/compose/ui/geometry/Size;",
         "layoutDirection",
         "Landroidx/compose/ui/unit/LayoutDirection;",
         "density",
         "Landroidx/compose/ui/unit/Density;",
         "createOutline-Pq9zytI",
         "(JLandroidx/compose/ui/unit/LayoutDirection;Landroidx/compose/ui/unit/Density;)Landroidx/compose/ui/graphics/Outline;",
         "getOffset",
         "Landroidx/compose/ui/geometry/Offset;",
         "indicatorCutSizePx",
         "",
         "cutDifference",
         "yOffset",
         "getOffset-XPhi94U",
         "(JFFFLandroidx/compose/ui/unit/LayoutDirection;)J",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nCutAvatarWithIndicatorShape.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CutAvatarWithIndicatorShape.kt\nio/intercom/android/sdk/m5/shapes/CutAvatarWithIndicatorShape\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 5 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 6 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 7 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 8 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,73:1\n1#2:74\n118#3:75\n33#4:76\n53#5,3:77\n70#5:81\n60#5:84\n53#5,3:87\n53#5,3:91\n61#6:80\n57#6:83\n23#7:82\n23#7:85\n30#8:86\n30#8:90\n*S KotlinDebug\n*F\n+ 1 CutAvatarWithIndicatorShape.kt\nio/intercom/android/sdk/m5/shapes/CutAvatarWithIndicatorShape\n*L\n26#1:75\n36#1:76\n36#1:77,3\n49#1:81\n69#1:84\n69#1:87,3\n70#1:91,3\n49#1:80\n69#1:83\n49#1:82\n69#1:85\n69#1:86\n70#1:90\n*E\n"
)
public final class CutAvatarWithIndicatorShape implements Shape {
   @NotNull
   private final Shape shape;
   private final float indicatorSize;
   public static int $stable;

   static {}

   private CutAvatarWithIndicatorShape(Shape var1, float var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "shape");
      this.shape = var1;
      this.indicatorSize = var2;
   }

   @NotNull
   @Override
   public Outline recovered$1652fd31305e14ee(long var1, @NotNull LayoutDirection var3, @NotNull Density var4) {
      Intrinsics.checkNotNullParameter(var3, "layoutDirection");
      Intrinsics.checkNotNullParameter(var4, "density");
      Density var6 = var4;
      int var7 = 0;
      int var8 = 2;
      int var9 = 0;
      float var5 = var6.recovered$245d7c913f591981(Dp.recovered$95b3d103663bf060(var8));
      Density var24 = var4;
      var8 = 0;
      float var23 = var24.recovered$245d7c913f591981(this.indicatorSize);
      float var25 = var23 + var5 * 2;
      RoundedCornerShape var27 = RoundedCornerShapeKt.getCircleShape();
      Path var10 = AndroidPath_androidKt.Path();
      Path var11 = var10;
      int var12 = 0;
      OutlineKt.addOutline(var11, this.shape.recovered$1652fd31305e14ee(var1, var3, var4));
      Path var28 = var10;
      var11 = AndroidPath_androidKt.Path();
      Path var32 = var11;
      int var13 = 0;
      int var14 = 0;
      float var15 = var25;
      float var16 = var25;
      int var17 = 0;
      long var18 = Float.floatToRawIntBits(var15);
      long var20 = Float.floatToRawIntBits(var16);
      OutlineKt.addOutline(var32, var27.recovered$1652fd31305e14ee(Size.recovered$8c837f3e34208708(var18 << 32 | var20 & 4294967295L), var3, var4));
      var10 = var11;
      Path var33 = AndroidPath_androidKt.Path();
      Path var35 = var33;
      var14 = 0;
      int var39 = 0;
      long var40 = var1;
      int var41 = 0;
      int var19 = (int)(var40 & 4294967295L);
      int var42 = 0;
      var35.recovered$a578e49b852de0a1(var10, this.recovered$fd0a5f75d5dad6b4(var1, var25, var5, Float.intBitsToFloat(var19) - var25 + var5, var3));
      var11 = var33;
      Path var34 = AndroidPath_androidKt.Path();
      Path var36 = var34;
      var14 = 0;
      var36.recovered$a1da2cc43fc3e948(var28, var11, PathOperation.Companion.recovered$bf48c24b2ab98fda());
      Path var22 = var34;
      return new Generic(var22);
   }

   private final long recovered$fd0a5f75d5dad6b4(long var1, float var3, float var4, float var5, LayoutDirection var6) {
      long var10000;
      switch (var6) {
         case Ltr: {
            int var16 = 0;
            long var18 = var1;
            int var21 = 0;
            int var23 = (int)(var18 >> 32);
            int var25 = 0;
            float var17 = Float.intBitsToFloat(var23) - var3 + var4;
            int var19 = 0;
            float var20 = var17;
            float var22 = var5;
            var23 = 0;
            long var26 = Float.floatToRawIntBits(var20);
            long var27 = Float.floatToRawIntBits(var22);
            var10000 = Offset.recovered$8c837f3e34208708(var26 << 32 | var27 & 4294967295L);
            break;
         }
         case Rtl: {
            float var7 = 0.0F - var4;
            int var8 = 0;
            float var9 = var7;
            float var10 = var5;
            int var11 = 0;
            long var12 = Float.floatToRawIntBits(var9);
            long var14 = Float.floatToRawIntBits(var10);
            var10000 = Offset.recovered$8c837f3e34208708(var12 << 32 | var14 & 4294967295L);
            break;
         }
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   // $VF: synthetic method
   public CutAvatarWithIndicatorShape(Shape var1, float var2, DefaultConstructorMarker var3) {
      this(var1, var2);
   }
}
