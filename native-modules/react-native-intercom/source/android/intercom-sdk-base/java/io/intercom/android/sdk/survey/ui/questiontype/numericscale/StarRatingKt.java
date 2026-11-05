package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import androidx.compose.foundation.CanvasKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.AndroidPath_androidKt;
import androidx.compose.ui.graphics.BlendMode;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.Path;
import androidx.compose.ui.graphics.drawscope.DrawContext;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.drawscope.DrawTransform;
import androidx.compose.ui.graphics.drawscope.Fill;
import androidx.compose.ui.graphics.drawscope.Stroke;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\u001a7\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0005H\u0001¢\u0006\u0004\b\t\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\b\u0010\u000e\u001a\u00020\u000fH\u0000¨\u0006\u0010",
   d2 = {
         "StarRating",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "strokeWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "strokeColor",
         "StarRating-tAjK0ZQ",
         "(Landroidx/compose/ui/Modifier;JFJLandroidx/compose/runtime/Composer;II)V",
         "SelectedStar",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UnSelectedStar",
         "getStarPath",
         "Landroidx/compose/ui/graphics/Path;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nStarRating.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StarRating.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/StarRatingKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 5 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 6 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 7 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n+ 8 DrawScope.kt\nandroidx/compose/ui/graphics/drawscope/DrawScopeKt\n*L\n1#1,99:1\n118#2:100\n118#2:107\n118#2:108\n118#2:109\n118#2:110\n1047#3,6:101\n57#4:111\n61#4:114\n60#5:112\n70#5:115\n53#5,3:118\n23#6:113\n23#6:116\n30#7:117\n172#8:121\n249#8,14:122\n*S KotlinDebug\n*F\n+ 1 StarRating.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/StarRatingKt\n*L\n25#1:100\n65#1:107\n66#1:108\n76#1:109\n77#1:110\n30#1:101,6\n31#1:111\n32#1:114\n31#1:112\n32#1:115\n40#1:118,3\n31#1:113\n32#1:116\n40#1:117\n40#1:121\n40#1:122,14\n*E\n"
)
public final class StarRatingKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$dca724aa12230f37(@Nullable Modifier var0, long var1, float var3, long var4, @Nullable Composer var6, int var7, int var8) {
      var6 = var6.startRestartGroup(-1509251218);
      ComposerKt.sourceInformation(
         var6, "C(StarRating)P(1,0:c#ui.graphics.Color,3:c#ui.unit.Dp,2:c#ui.graphics.Color)29@1124L867,27@1070L928:StarRating.kt#hdztyp"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var3) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var9 |= (var8 & 8) == 0 && var6.changed(var4) ? 2048 : 1024;
      }

      if ((var9 & 1171) == 1170 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         var6.startDefaults();
         if ((var7 & 1) != 0 && !var6.getDefaultsInvalid()) {
            var6.skipToGroupEnd();
            if ((var8 & 8) != 0) {
               var9 &= -7169;
            }
         } else {
            if ((var8 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var8 & 2) != 0) {
               var1 = Color.Companion.recovered$95989221dad41c4d();
            }

            if ((var8 & 4) != 0) {
               int var10 = 1;
               int var11 = 0;
               var3 = Dp.recovered$95b3d103663bf060(var10);
            }

            if ((var8 & 8) != 0) {
               var4 = ColorExtensionsKt.recovered$9902137196ed1ac6(var1);
               var9 &= -7169;
            }
         }

         var6.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1509251218, var9, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.StarRating (StarRating.kt:26)");
         }

         Modifier var10000 = var0;
         var6.startReplaceGroup(-1036119505);
         ComposerKt.sourceInformation(var6, "CC(remember):StarRating.kt#9igjgp");
         Composer var21 = var6;
         boolean var12 = ((var9 & 7168 ^ 3072) > 2048 && var6.changed(var4) || (var9 & 3072) == 2048) | (var9 & 896) == 256 | (var9 & 112) == 32;
         int var13 = 0;
         Object var14 = var21.rememberedValue();
         int var15 = 0;
         kotlin.jvm.functions.Function1 var23;
         if (!var12 && var14 != Composer.Companion.getEmpty()) {
            var23 = (Function1)var14;
         } else {
            Modifier var18 = var0;
            int var16 = 0;
            final long recoveredCapture_1485_0 = var4;
            final float recoveredCapture_1485_1 = var3;
            final long recoveredCapture_1485_2 = var1;
            var23 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1485_0) -> StarRatingKt.StarRating_tAjK0ZQ$lambda$2$lambda$1(recoveredCapture_1485_0, recoveredCapture_1485_1, recoveredCapture_1485_2, (androidx.compose.ui.graphics.drawscope.DrawScope)recoveredArgument_1485_0));
            var10000 = var18;
            Object var17 = var23;
            var21.updateRememberedValue(var17);
            var23 = (Function1)var17;
         }

         Function1 var20 = var23;
         var6.endReplaceGroup();
         CanvasKt.Canvas(var10000, var20, var6, 14 & var9);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var22 = var6.endRestartGroup();
      if (var22 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1486_0 = var0;
         final long recoveredCapture_1486_1 = var1;
         final float recoveredCapture_1486_2 = var3;
         final long recoveredCapture_1486_3 = var4;
         final int recoveredCapture_1486_4 = var7;
         final int recoveredCapture_1486_5 = var8;
         var22.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1486_0, recoveredArgument_1486_1) -> StarRatingKt.StarRating_tAjK0ZQ$lambda$3(recoveredCapture_1486_0, recoveredCapture_1486_1, recoveredCapture_1486_2, recoveredCapture_1486_3, recoveredCapture_1486_4, recoveredCapture_1486_5, (androidx.compose.runtime.Composer)recoveredArgument_1486_0, ((java.lang.Integer)recoveredArgument_1486_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SelectedStar(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-596392123);
      ComposerKt.sourceInformation(var0, "C(SelectedStar)62@2057L139:StarRating.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-596392123, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.SelectedStar (StarRating.kt:61)");
         }

         Modifier var10000 = Modifier.Companion;
         int var2 = 44;
         int var3 = 0;
         var10000 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var2));
         var2 = 44;
         var3 = 0;
         recovered$dca724aa12230f37(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var2)),
            Color.Companion.recovered$ba858dcfc5e04b9c(),
            0.0F,
            0L,
            var0,
            54,
            12
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var8 = var0.endRestartGroup();
      if (var8 != null) {
         final int recoveredCapture_1483_0 = var1;
         var8.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1483_0, recoveredArgument_1483_1) -> StarRatingKt.SelectedStar$lambda$4(recoveredCapture_1483_0, (androidx.compose.runtime.Composer)recoveredArgument_1483_0, ((java.lang.Integer)recoveredArgument_1483_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnSelectedStar(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(843558828);
      ComposerKt.sourceInformation(var0, "C(UnSelectedStar)73@2280L99:StarRating.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(843558828, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.UnSelectedStar (StarRating.kt:72)");
         }

         Modifier var10000 = Modifier.Companion;
         int var2 = 44;
         int var3 = 0;
         var10000 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var2));
         var2 = 44;
         var3 = 0;
         recovered$dca724aa12230f37(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var2)), 0L, 0.0F, 0L, var0, 6, 14);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var8 = var0.endRestartGroup();
      if (var8 != null) {
         final int recoveredCapture_1484_0 = var1;
         var8.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1484_0, recoveredArgument_1484_1) -> StarRatingKt.UnSelectedStar$lambda$5(recoveredCapture_1484_0, (androidx.compose.runtime.Composer)recoveredArgument_1484_0, ((java.lang.Integer)recoveredArgument_1484_1).intValue())));
      }
   }

   @NotNull
   public static final Path getStarPath() {
      Path var0 = AndroidPath_androidKt.Path();
      Path var1 = var0;
      int var2 = 0;
      var1.moveTo(31.9F, 11.25F);
      var1.cubicTo(32.95F, 11.39F, 33.38F, 12.71F, 32.6F, 13.46F);
      var1.lineTo(25.33F, 20.47F);
      var1.lineTo(27.16F, 30.48F);
      var1.cubicTo(27.35F, 31.54F, 26.25F, 32.36F, 25.31F, 31.84F);
      var1.lineTo(16.5F, 26.98F);
      var1.lineTo(7.69F, 31.84F);
      var1.cubicTo(6.75F, 32.36F, 5.65F, 31.54F, 5.84F, 30.48F);
      var1.lineTo(7.67F, 20.47F);
      var1.lineTo(0.4F, 13.46F);
      var1.cubicTo(-0.38F, 12.71F, 0.05F, 11.39F, 1.1F, 11.25F);
      var1.lineTo(11.04F, 9.92F);
      var1.lineTo(15.36F, 0.73F);
      var1.cubicTo(15.82F, -0.24F, 17.18F, -0.24F, 17.64F, 0.73F);
      var1.lineTo(21.96F, 9.92F);
      var1.lineTo(31.9F, 11.25F);
      return var0;
   }

   private static final Unit StarRating_tAjK0ZQ$lambda$2$lambda$1(long var0, float var2, long var3, DrawScope var5) {
      Intrinsics.checkNotNullParameter(var5, "$this$Canvas");
      long var7 = var5.recovered$d1ee80c43f6fa69a();
      int var9 = 0;
      long var10 = var7;
      int var12 = 0;
      int var13 = (int)(var10 >> 32);
      int var14 = 0;
      float var6 = Float.intBitsToFloat(var13);
      long arg0$ivx = var5.recovered$d1ee80c43f6fa69a();
      int var33 = 0;
      long value$iv$ivx = arg0$ivx;
      var13 = 0;
      var14 = (int)(value$iv$ivx & 4294967295L);
      int $i$f$floatFromBitsx = 0;
      float var30 = Float.intBitsToFloat(var14);
      float var31 = 33.0F;
      float var32 = 32.0F;
      float var34 = var6 / var31;
      float var35 = var30 / var32;
      Path var36 = getStarPath();
      DrawScope var38 = var5;
      float var40 = 0.0F;
      float var42 = 0.0F;
      int var16 = 0;
      float var17 = var40;
      float var18 = var42;
      int var19 = 0;
      long var20 = Float.floatToRawIntBits(var17);
      long var22 = Float.floatToRawIntBits(var18);
      long var41 = Offset.recovered$8c837f3e34208708(var20 << 32 | var22 & 4294967295L);
      var16 = 0;
      DrawScope var44 = var38;
      int var45 = 0;
      DrawContext var46 = var44.getDrawContext();
      int var47 = 0;
      long var21 = var46.recovered$d1ee80c43f6fa69a();
      var46.getCanvas().save();

      try {
         DrawTransform var23 = var46.getTransform();
         int var24 = 0;
         var23.recovered$15a54b40504eec78(var34, var35, var41);
         DrawScope var25 = var44;
         int var26 = 0;
         DrawScope.recovered$a35e5c0f927371d1(
            var25, var36, var0, 0.0F, new Stroke(var25.recovered$245d7c913f591981(var2), 0.0F, 0, 0, null, 30, null), null, 0, 52, null
         );
         DrawScope.recovered$a35e5c0f927371d1(
            var25,
            var36,
            var3,
            0.0F,
            Fill.INSTANCE,
            ColorFilter.Companion.recovered$4a1f185b98252775(var3, BlendMode.Companion.recovered$f6e01b54f91ba1c3()),
            0,
            36,
            null
         );
      } finally {
         var46.getCanvas().restore();
         var46.recovered$24a8274fd1fa44b3(var21);
      }

      return Unit.INSTANCE;
   }

   private static final Unit StarRating_tAjK0ZQ$lambda$3(Modifier var0, long var1, float var3, long var4, int var6, int var7, Composer var8, int var9) {
      recovered$dca724aa12230f37(var0, var1, var3, var4, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit SelectedStar$lambda$4(int var0, Composer var1, int var2) {
      SelectedStar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UnSelectedStar$lambda$5(int var0, Composer var1, int var2) {
      UnSelectedStar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
