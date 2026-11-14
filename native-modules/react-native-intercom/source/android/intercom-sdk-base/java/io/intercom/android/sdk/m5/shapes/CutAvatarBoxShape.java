package io.intercom.android.sdk.m5.shapes;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0001\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0018\u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u00070\u0006¢\u0006\u0004\b\b\u0010\tJ'\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016¢\u0006\u0004\b\u0013\u0010\u0014J/\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u000f\u001a\u00020\u0010H\u0002¢\u0006\u0004\b\u001a\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\nR \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/shapes/CutAvatarBoxShape;",
         "Landroidx/compose/ui/graphics/Shape;",
         "shape",
         "cut",
         "Landroidx/compose/ui/unit/Dp;",
         "cutsOffsets",
         "",
         "Lkotlin/Pair;",
         "<init>",
         "(Landroidx/compose/ui/graphics/Shape;FLjava/util/List;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
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
         "",
         "xOffset",
         "yOffset",
         "getOffset-Rc2DDho",
         "(FFFLandroidx/compose/ui/unit/LayoutDirection;)J",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nCutAvatarBoxShape.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CutAvatarBoxShape.kt\nio/intercom/android/sdk/m5/shapes/CutAvatarBoxShape\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 4 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 5 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 6 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,69:1\n1#2:70\n57#3:71\n61#3:74\n60#4:72\n70#4:75\n53#4,3:77\n53#4,3:85\n53#4,3:89\n23#5:73\n33#6:76\n1563#7:80\n1634#7,3:81\n30#8:84\n30#8:88\n*S KotlinDebug\n*F\n+ 1 CutAvatarBoxShape.kt\nio/intercom/android/sdk/m5/shapes/CutAvatarBoxShape\n*L\n32#1:71\n32#1:74\n32#1:72\n32#1:75\n32#1:77,3\n65#1:85,3\n66#1:89,3\n32#1:73\n32#1:76\n39#1:80\n39#1:81,3\n65#1:84\n66#1:88\n*E\n"
)
public final class CutAvatarBoxShape implements Shape {
   @NotNull
   private final Shape shape;
   private final float cut;
   @NotNull
   private final List cutsOffsets;
   public static int $stable;

   static {}

   private CutAvatarBoxShape(Shape var1, float var2, List var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "shape");
      Intrinsics.checkNotNullParameter(var3, "cutsOffsets");
      this.shape = var1;
      this.cut = var2;
      this.cutsOffsets = var3;
   }

   @NotNull
   @Override
   public Outline recovered$1652fd31305e14ee(long var1, @NotNull LayoutDirection var3, @NotNull Density var4) {
      Intrinsics.checkNotNullParameter(var3, "layoutDirection");
      Intrinsics.checkNotNullParameter(var4, "density");
      Density var6 = var4;
      int var7 = 0;
      float var5 = var6.recovered$245d7c913f591981(this.cut);
      Path var34 = AndroidPath_androidKt.Path();
      Path var8 = var34;
      int var9 = 0;
      OutlineKt.addOutline(var8, this.shape.recovered$1652fd31305e14ee(var1, var3, var4));
      Path var33 = var34;
      var8 = AndroidPath_androidKt.Path();
      Path var38 = var8;
      int var10 = 0;
      Shape var10001 = this.shape;
      int var11 = 0;
      long var12 = var1;
      int var14 = 0;
      int var15 = (int)(var12 >> 32);
      int var16 = 0;
      float var43 = Float.intBitsToFloat(var15) + var5;
      int var17 = 0;
      long value$iv$ivx = var1;
      var15 = 0;
      var16 = (int)(value$iv$ivx & 4294967295L);
      int $i$f$floatFromBitsx = 0;
      float var55 = Float.intBitsToFloat(var16) + var5;
      int var21 = 0;
      float var47 = var43;
      float var50 = var55;
      var16 = 0;
      long var22 = Float.floatToRawIntBits(var47);
      long var24 = Float.floatToRawIntBits(var50);
      OutlineKt.addOutline(var38, var10001.recovered$1652fd31305e14ee(Size.recovered$8c837f3e34208708(var22 << 32 | var24 & 4294967295L), var3, var4));
      Path var35 = var8;
      Path var39 = AndroidPath_androidKt.Path();
      Path var41 = var39;
      var11 = 0;
      Iterable var46 = this.cutsOffsets;
      int var13 = 0;
      Iterable var48 = var46;
      Collection var51 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var46, 10));
      var16 = 0;

      for (Object var57 : var48) {
         Pair var19 = (Pair)var57;
         Collection var58 = var51;
         var21 = 0;
         float var60 = ((Dp)var19.component1()).recovered$3537cef520440dc0();
         float var23 = ((Dp)var19.component2()).recovered$3537cef520440dc0();
         float var10003 = var5 / 2;
         Density var61 = var4;
         float var25 = var10003;
         CutAvatarBoxShape var26 = this;
         Path var27 = var35;
         Path var28 = var41;
         int var29 = 0;
         float var30 = var61.recovered$245d7c913f591981(var60);
         Density var62 = var4;
         var30 = var30;
         var25 = var25;
         var26 = var26;
         var27 = var27;
         var28 = var28;
         var29 = 0;
         float var31 = var62.recovered$245d7c913f591981(var23);
         var28.recovered$a578e49b852de0a1(var27, var26.recovered$a47e7b750e7a05d6(var25, var30, var31, var3));
         var58.add(Unit.INSTANCE);
      }

      var8 = var39;
      Path var40 = AndroidPath_androidKt.Path();
      Path var42 = var40;
      var11 = 0;
      var42.recovered$a1da2cc43fc3e948(var33, var8, PathOperation.Companion.recovered$bf48c24b2ab98fda());
      Path var32 = var40;
      return new Generic(var32);
   }

   private final long recovered$a47e7b750e7a05d6(float var1, float var2, float var3, LayoutDirection var4) {
      long var10000;
      switch (var4) {
         case Ltr: {
            float var15 = var2 - var1;
            float var16 = var3 - var1;
            int var17 = 0;
            float var18 = var15;
            float var19 = var16;
            int var20 = 0;
            long var21 = Float.floatToRawIntBits(var18);
            long var22 = Float.floatToRawIntBits(var19);
            var10000 = Offset.recovered$8c837f3e34208708(var21 << 32 | var22 & 4294967295L);
            break;
         }
         case Rtl: {
            float var5 = -var2 - var1;
            float var6 = var3 - var1;
            int var7 = 0;
            float var8 = var5;
            float var9 = var6;
            int var10 = 0;
            long var11 = Float.floatToRawIntBits(var8);
            long var13 = Float.floatToRawIntBits(var9);
            var10000 = Offset.recovered$8c837f3e34208708(var11 << 32 | var13 & 4294967295L);
            break;
         }
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   // $VF: synthetic method
   public CutAvatarBoxShape(Shape var1, float var2, List var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }
}
