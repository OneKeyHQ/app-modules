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
import androidx.compose.ui.graphics.RectangleShapeKt;
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
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J'\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016¢\u0006\u0004\b\u000f\u0010\u0010J'\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u000b\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0016\u0010\u0017R\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0006¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/shapes/CutIconWithIndicatorShape;",
         "Landroidx/compose/ui/graphics/Shape;",
         "indicatorSize",
         "Landroidx/compose/ui/unit/Dp;",
         "<init>",
         "(FLkotlin/jvm/internal/DefaultConstructorMarker;)V",
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
         "width",
         "",
         "dotSize",
         "getOffset-P-0qjgQ",
         "(FFLandroidx/compose/ui/unit/LayoutDirection;)J",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nCutIconWithIndicatorShape.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CutIconWithIndicatorShape.kt\nio/intercom/android/sdk/m5/shapes/CutIconWithIndicatorShape\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 5 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 6 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 7 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 8 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,52:1\n118#2:53\n1#3:54\n33#4:55\n53#5,3:56\n60#5:60\n53#5,3:63\n53#5,3:67\n57#6:59\n23#7:61\n30#8:62\n30#8:66\n*S KotlinDebug\n*F\n+ 1 CutIconWithIndicatorShape.kt\nio/intercom/android/sdk/m5/shapes/CutIconWithIndicatorShape\n*L\n17#1:53\n27#1:55\n27#1:56,3\n36#1:60\n48#1:63,3\n49#1:67,3\n36#1:59\n36#1:61\n48#1:62\n49#1:66\n*E\n"
)
public final class CutIconWithIndicatorShape implements Shape {
   private final float indicatorSize;
   public static int $stable;

   static {}

   private CutIconWithIndicatorShape(float var1) {
      this.indicatorSize = var1;
   }

   // $VF: synthetic method
   public CutIconWithIndicatorShape(float var1, int var2, DefaultConstructorMarker var3) {
      if ((var2 & 1) != 0) {
         int var4 = 8;
         int var5 = 0;
         var1 = Dp.recovered$95b3d103663bf060(var4);
      }

      this(var1, null);
   }

   @NotNull
   @Override
   public Outline recovered$1652fd31305e14ee(long var1, @NotNull LayoutDirection var3, @NotNull Density var4) {
      Intrinsics.checkNotNullParameter(var3, "layoutDirection");
      Intrinsics.checkNotNullParameter(var4, "density");
      Density var6 = var4;
      int var7 = 0;
      float var5 = var6.recovered$245d7c913f591981(this.indicatorSize);
      Path var21 = AndroidPath_androidKt.Path();
      Path var8 = var21;
      int var9 = 0;
      OutlineKt.addOutline(var8, RectangleShapeKt.getRectangleShape().recovered$1652fd31305e14ee(var1, var3, var4));
      Path var20 = var21;
      var8 = AndroidPath_androidKt.Path();
      Path var25 = var8;
      int var10 = 0;
      RoundedCornerShape var10001 = RoundedCornerShapeKt.getCircleShape();
      int var11 = 0;
      float var12 = var5;
      float var13 = var5;
      int var14 = 0;
      long var15 = Float.floatToRawIntBits(var12);
      long var17 = Float.floatToRawIntBits(var13);
      OutlineKt.addOutline(var25, var10001.recovered$1652fd31305e14ee(Size.recovered$8c837f3e34208708(var15 << 32 | var17 & 4294967295L), var3, var4));
      Path var22 = var8;
      Path var26 = AndroidPath_androidKt.Path();
      Path var28 = var26;
      var11 = 0;
      int var32 = 0;
      long var33 = var1;
      int var34 = 0;
      int var16 = (int)(var33 >> 32);
      int var35 = 0;
      var28.recovered$a578e49b852de0a1(var22, this.recovered$d8d1b84a7edca5df(Float.intBitsToFloat(var16), var5, var3));
      var8 = var26;
      Path var27 = AndroidPath_androidKt.Path();
      Path var29 = var27;
      var11 = 0;
      var29.recovered$a1da2cc43fc3e948(var20, var8, PathOperation.Companion.recovered$bf48c24b2ab98fda());
      Path var19 = var27;
      return new Generic(var19);
   }

   private final long recovered$d8d1b84a7edca5df(float var1, float var2, LayoutDirection var3) {
      long var10000;
      switch (var3) {
         case Ltr: {
            float var14 = var1 - var2;
            float var15 = 0.0F;
            int var16 = 0;
            float var17 = var14;
            float var18 = var15;
            int var19 = 0;
            long var20 = Float.floatToRawIntBits(var17);
            long var21 = Float.floatToRawIntBits(var18);
            var10000 = Offset.recovered$8c837f3e34208708(var20 << 32 | var21 & 4294967295L);
            break;
         }
         case Rtl: {
            float var4 = 0.0F;
            float var5 = 0.0F;
            int var6 = 0;
            float var7 = var4;
            float var8 = var5;
            int var9 = 0;
            long var10 = Float.floatToRawIntBits(var7);
            long var12 = Float.floatToRawIntBits(var8);
            var10000 = Offset.recovered$8c837f3e34208708(var10 << 32 | var12 & 4294967295L);
            break;
         }
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   // $VF: synthetic method
   public CutIconWithIndicatorShape(float var1, DefaultConstructorMarker var2) {
      this(var1);
   }
}
