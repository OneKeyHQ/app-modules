package io.intercom.android.sdk.m5.shapes;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.AndroidPath_androidKt;
import androidx.compose.ui.graphics.Outline;
import androidx.compose.ui.graphics.OutlineKt;
import androidx.compose.ui.graphics.Path;
import androidx.compose.ui.graphics.PathOperation;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.Outline.Generic;
import androidx.compose.ui.unit.Density;
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
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0001\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J'\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u001f\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\r\u001a\u00020\u000eH\u0002¢\u0006\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\b¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/shapes/OverlappedAvatarShape;",
         "Landroidx/compose/ui/graphics/Shape;",
         "currentAvatarShape",
         "previousAvatarShape",
         "cut",
         "Landroidx/compose/ui/unit/Dp;",
         "<init>",
         "(Landroidx/compose/ui/graphics/Shape;Landroidx/compose/ui/graphics/Shape;FLkotlin/jvm/internal/DefaultConstructorMarker;)V",
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
         "xOffset",
         "",
         "getOffset-dBAh8RU",
         "(FLandroidx/compose/ui/unit/LayoutDirection;)J",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nOverlappedAvatarShape.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OverlappedAvatarShape.kt\nio/intercom/android/sdk/m5/shapes/OverlappedAvatarShape\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 4 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 5 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 6 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,43:1\n1#2:44\n57#3:45\n60#4:46\n53#4,3:49\n53#4,3:53\n23#5:47\n30#6:48\n30#6:52\n*S KotlinDebug\n*F\n+ 1 OverlappedAvatarShape.kt\nio/intercom/android/sdk/m5/shapes/OverlappedAvatarShape\n*L\n27#1:45\n27#1:46\n39#1:49,3\n40#1:53,3\n27#1:47\n39#1:48\n40#1:52\n*E\n"
)
public final class OverlappedAvatarShape implements Shape {
   @NotNull
   private final Shape currentAvatarShape;
   @NotNull
   private final Shape previousAvatarShape;
   private final float cut;
   public static int $stable;

   static {}

   private OverlappedAvatarShape(Shape var1, Shape var2, float var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "currentAvatarShape");
      Intrinsics.checkNotNullParameter(var2, "previousAvatarShape");
      this.currentAvatarShape = var1;
      this.previousAvatarShape = var2;
      this.cut = var3;
   }

   // $VF: synthetic method
   public OverlappedAvatarShape(Shape var1, Shape var2, float var3, int var4, DefaultConstructorMarker var5) {
      if ((var4 & 2) != 0) {
         var2 = var1;
      }

      this(var1, var2, var3, null);
   }

   @NotNull
   @Override
   public Outline recovered$1652fd31305e14ee(long var1, @NotNull LayoutDirection var3, @NotNull Density var4) {
      Intrinsics.checkNotNullParameter(var3, "layoutDirection");
      Intrinsics.checkNotNullParameter(var4, "density");
      Density var6 = var4;
      int var7 = 0;
      float var5 = var6.recovered$245d7c913f591981(this.cut);
      Path var20 = AndroidPath_androidKt.Path();
      Path var8 = var20;
      int var9 = 0;
      OutlineKt.addOutline(var8, this.currentAvatarShape.recovered$1652fd31305e14ee(var1, var3, var4));
      Path var19 = var20;
      var8 = AndroidPath_androidKt.Path();
      Path var24 = var8;
      int var10 = 0;
      OutlineKt.addOutline(var24, this.previousAvatarShape.recovered$1652fd31305e14ee(var1, var3, var4));
      Path var21 = var8;
      Path var25 = AndroidPath_androidKt.Path();
      Path var27 = var25;
      int var11 = 0;
      int var12 = 0;
      long var13 = var1;
      int var15 = 0;
      int var16 = (int)(var13 >> 32);
      int var17 = 0;
      var27.recovered$a578e49b852de0a1(var21, this.recovered$7371419e8610fc8a(var5 - Float.intBitsToFloat(var16), var3));
      var8 = var25;
      Path var26 = AndroidPath_androidKt.Path();
      Path var28 = var26;
      var11 = 0;
      var28.recovered$a1da2cc43fc3e948(var19, var8, PathOperation.Companion.recovered$bf48c24b2ab98fda());
      Path var18 = var26;
      return new Generic(var18);
   }

   private final long recovered$7371419e8610fc8a(float var1, LayoutDirection var2) {
      long var10000;
      switch (var2) {
         case Ltr: {
            float var13 = 0.0F;
            int var14 = 0;
            float var15 = var1;
            float var16 = var13;
            int var17 = 0;
            long var18 = Float.floatToRawIntBits(var15);
            long var10 = Float.floatToRawIntBits(var16);
            var10000 = Offset.recovered$8c837f3e34208708(var18 << 32 | var10 & 4294967295L);
            break;
         }
         case Rtl: {
            float var3 = -var1;
            float var4 = 0.0F;
            int var5 = 0;
            float var6 = var3;
            float var7 = var4;
            int var8 = 0;
            long var9 = Float.floatToRawIntBits(var6);
            long var11 = Float.floatToRawIntBits(var7);
            var10000 = Offset.recovered$8c837f3e34208708(var9 << 32 | var11 & 4294967295L);
            break;
         }
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   // $VF: synthetic method
   public OverlappedAvatarShape(Shape var1, Shape var2, float var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }
}
