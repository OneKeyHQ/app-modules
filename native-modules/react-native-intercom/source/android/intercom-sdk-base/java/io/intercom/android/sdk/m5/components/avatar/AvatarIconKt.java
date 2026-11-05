package io.intercom.android.sdk.m5.components.avatar;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.CanvasKt;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.SubcomposeAsyncImageKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000P\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\u001aK\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0001¢\u0006\u0004\b\u0013\u0010\u0014\u001aK\u0010\u0015\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0003¢\u0006\u0004\b\u0016\u0010\u0017\u001a)\u0010\u0018\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003¢\u0006\u0002\u0010\u0019\u001a)\u0010\u001a\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0003¢\u0006\u0002\u0010\u001d\u001a)\u0010\u001e\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0003¢\u0006\u0002\u0010\u001d\u001a\u001c\u0010\u001f\u001a\u00020\b*\u00020\b2\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0000\u001a\u0017\u0010!\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003¢\u0006\u0002\u0010\"\u001a9\u0010#\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u00122\u0006\u0010'\u001a\u00020\u00102\u0006\u0010(\u001a\u00020%H\u0003¢\u0006\u0004\b)\u0010*\u001a\r\u0010+\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010,\u001a\r\u0010-\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010,\u001a\r\u0010.\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010,\u001a\r\u0010/\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010,\u001a\r\u00100\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010,\"\u0018\u0010\u0000\u001a\u00020\u0001*\u00020\u00028@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004¨\u00061²\u0006\n\u00102\u001a\u00020\u0012X\u008a\u008e\u0002²\u0006\n\u00103\u001a\u000204X\u008a\u008e\u0002²\u0006\n\u00105\u001a\u00020\fX\u008a\u008e\u0002",
   d2 = {
         "composeShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarShape;",
         "getComposeShape",
         "(Lio/intercom/android/sdk/m5/components/avatar/AvatarShape;)Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "AvatarIcon",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "avatarWrapper",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "isActive",
         "",
         "placeHolderTextSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "customBackgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "AvatarIcon-Rd90Nhg",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Landroidx/compose/ui/graphics/Shape;ZJLandroidx/compose/ui/graphics/Color;Landroidx/compose/runtime/Composer;II)V",
         "DefaultAvatar",
         "DefaultAvatar-Rd90Nhg",
         "(Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Landroidx/compose/ui/Modifier;Landroidx/compose/ui/graphics/Shape;ZJLandroidx/compose/ui/graphics/Color;Landroidx/compose/runtime/Composer;II)V",
         "FinAvatar",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Landroidx/compose/ui/graphics/Shape;Landroidx/compose/runtime/Composer;II)V",
         "FinAvatarPlaceholder",
         "alpha",
         "",
         "(Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Landroidx/compose/ui/Modifier;FLandroidx/compose/runtime/Composer;II)V",
         "BotAvatarPlaceholder",
         "avatarBorder",
         "shouldDrawBorder",
         "AvatarActiveIndicator",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "AvatarPlaceholder",
         "avatarInitials",
         "",
         "textColor",
         "textSize",
         "contentDescription",
         "AvatarPlaceholder-jxWH9Kg",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;JJLjava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "AvatarIconPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "AvatarIconActivePreview",
         "AvatarIconSquirclePreview",
         "AvatarIconCutPreview",
         "BotAvatarPreview",
         "intercom-sdk-base_release",
         "backgroundColor",
         "indicatorSize",
         "Landroidx/compose/ui/unit/Dp;",
         "cutShape"
   }
)
@SourceDebugExtension(
   "SMAP\nAvatarIcon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarIconKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,454:1\n1047#2,6:455\n1047#2,3:461\n1050#2,3:465\n1047#2,6:468\n1047#2,6:513\n1047#2,6:550\n118#3:464\n118#3:506\n123#3:511\n118#3:512\n118#3:556\n118#3:570\n75#4:474\n70#5:475\n68#5,8:476\n77#5:510\n70#5:519\n68#5,8:520\n77#5:560\n81#6,6:484\n88#6,6:499\n96#6:509\n81#6,6:528\n88#6,6:543\n96#6:559\n402#7,9:490\n411#7:505\n412#7,2:507\n402#7,9:534\n411#7:549\n412#7,2:557\n85#8:561\n117#8,2:562\n85#8:564\n117#8,2:565\n85#8:567\n117#8,2:568\n*S KotlinDebug\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarIconKt\n*L\n134#1:455,6\n138#1:461,3\n138#1:465,3\n139#1:468,6\n317#1:513,6\n334#1:550,6\n138#1:464\n272#1:506\n302#1:511\n316#1:512\n347#1:556\n147#1:570\n234#1:474\n267#1:475\n267#1:476,8\n267#1:510\n331#1:519\n331#1:520,8\n331#1:560\n267#1:484,6\n267#1:499,6\n267#1:509\n331#1:528,6\n331#1:543,6\n331#1:559\n267#1:490,9\n267#1:505\n267#1:507,2\n331#1:534,9\n331#1:549\n331#1:557,2\n134#1:561\n134#1:562,2\n138#1:564\n138#1:565,2\n139#1:567\n139#1:568,2\n*E\n"
)
public final class AvatarIconKt {
   @NotNull
   public static final RoundedCornerShape getComposeShape(@NotNull AvatarShape var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      RoundedCornerShape var10000;
      switch (var0) {
         case CIRCLE:
            var10000 = RoundedCornerShapeKt.RoundedCornerShape(50);
            break;
         case SQUIRCLE:
            var10000 = RoundedCornerShapeKt.RoundedCornerShape(16);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$f5ec3c81e3146789(
      @Nullable Modifier var0,
      @NotNull AvatarWrapper var1,
      @Nullable Shape var2,
      boolean var3,
      long var4,
      @Nullable Color var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "avatarWrapper");
      Composer var11 = var7.startRestartGroup(462320907);
      ComposerKt.sourceInformation(var11, "C(AvatarIcon)P(3!1,5,2,4:c#ui.unit.TextUnit,1:c#ui.graphics.Color)101@3675L10:AvatarIcon.kt#ssa3oh");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var11.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var11.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 384) == 0) {
         var10 |= (var9 & 4) == 0 && var11.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var11.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 24576) == 0) {
         var10 |= (var9 & 16) == 0 && var11.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var11.changed(var6) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         var11.startDefaults();
         if ((var8 & 1) != 0 && !var11.getDefaultsInvalid()) {
            var11.skipToGroupEnd();
            if ((var9 & 4) != 0) {
               var10 &= -897;
            }

            if ((var9 & 16) != 0) {
               var10 &= -57345;
            }
         } else {
            if ((var9 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var9 & 4) != 0) {
               AvatarShape var10000 = var1.getAvatar().getShape();
               Intrinsics.checkNotNullExpressionValue(var10000, "getShape(...)");
               var2 = getComposeShape(var10000);
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var3 = false;
            }

            if ((var9 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getTypography(var11, IntercomTheme.$stable).getType04Point5().recovered$8f8bc2178e9073c3();
               var10 &= -57345;
            }

            if ((var9 & 32) != 0) {
               var6 = null;
            }
         }

         var11.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(462320907, var10, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarIcon (AvatarIcon.kt:103)");
         }

         if (Intrinsics.areEqual(var2, getComposeShape(AvatarShape.SQUIRCLE))) {
            var11.startReplaceGroup(1816789334);
            ComposerKt.sourceInformation(var11, "105@3817L123");
            FinAvatar(var0, var1, var2, var11, 14 & var10 | 112 & var10 | 896 & var10, 0);
            var11.endReplaceGroup();
         } else {
            var11.startReplaceGroup(1816937855);
            ComposerKt.sourceInformation(var11, "111@3962L274");
            recovered$366294016de51a70(
               var1, var0, var2, var3, var4, var6, var11, 14 & var10 >> 3 | 112 & var10 << 3 | 896 & var10 | 7168 & var10 | 57344 & var10 | 458752 & var10, 0
            );
            var11.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var11.endRestartGroup();
      if (var12 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_278_0 = var0;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredExpressionCapture_278_1 = var1;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_278_2 = var2;
         final boolean recoveredExpressionCapture_278_3 = var3;
         final long recoveredExpressionCapture_278_4 = var4;
         final androidx.compose.ui.graphics.Color recoveredExpressionCapture_278_5 = var6;
         final int recoveredExpressionCapture_278_6 = var8;
         final int recoveredExpressionCapture_278_7 = var9;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_278_0, recoveredExpressionArgument_278_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarIcon_Rd90Nhg$lambda$0(recoveredExpressionCapture_278_0, recoveredExpressionCapture_278_1, recoveredExpressionCapture_278_2, recoveredExpressionCapture_278_3, recoveredExpressionCapture_278_4, recoveredExpressionCapture_278_5, recoveredExpressionCapture_278_6, recoveredExpressionCapture_278_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_278_0), ((java.lang.Integer)recoveredExpressionArgument_278_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$366294016de51a70(
      AvatarWrapper var0, Modifier var1, Shape var2, boolean var3, long var4, Color var6, Composer var7, int var8, int var9
   ) {
      Composer var27 = var7.startRestartGroup(386725315);
      ComposerKt.sourceInformation(
         var27,
         "C(DefaultAvatar)P(!1,3,5,2,4:c#ui.unit.TextUnit,1:c#ui.graphics.Color)128@4487L10,131@4614L6,133@4656L85,137@4881L33,138@4935L34,162@5613L2016,160@5558L2071:AvatarIcon.kt#ssa3oh"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var27.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var27.changed(var1) ? 32 : 16;
      }

      if ((var8 & 384) == 0) {
         var10 |= (var9 & 4) == 0 && var27.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var27.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 24576) == 0) {
         var10 |= (var9 & 16) == 0 && var27.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var27.changed(var6) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var27.getSkipping()) {
         var27.skipToGroupEnd();
      } else {
         var27.startDefaults();
         if ((var8 & 1) != 0 && !var27.getDefaultsInvalid()) {
            var27.skipToGroupEnd();
            if ((var9 & 4) != 0) {
               var10 &= -897;
            }

            if ((var9 & 16) != 0) {
               var10 &= -57345;
            }
         } else {
            if ((var9 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var9 & 4) != 0) {
               var2 = getComposeShape(AvatarShape.CIRCLE);
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var3 = false;
            }

            if ((var9 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getTypography(var27, IntercomTheme.$stable).getType04Point5().recovered$8f8bc2178e9073c3();
               var10 &= -57345;
            }

            if ((var9 & 32) != 0) {
               var6 = null;
            }
         }

         var27.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(386725315, var10, -1, "io.intercom.android.sdk.m5.components.avatar.DefaultAvatar (AvatarIcon.kt:130)");
         }

         long var11 = IntercomTheme.INSTANCE.getColors(var27, IntercomTheme.$stable).recovered$202acdf3dbb5c020();
         var27.startReplaceGroup(870365600);
         ComposerKt.sourceInformation(var27, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var15 = var27;
         boolean var16 = false;
         int var17 = 0;
         Object var18 = var15.rememberedValue();
         int var19 = 0;
         Object var10000;
         if (var18 == Composer.Companion.getEmpty()) {
            int var20 = 0;
            Object var21 = SnapshotStateKt.mutableStateOf$default(
               Color.recovered$f46ef195ad4a2363(var6 != null ? var6.recovered$45f9de7b85e0e0b5() : ColorExtensionsKt.recovered$da172df9fccf1b21(var11)),
               null,
               2,
               null
            );
            var15.updateRememberedValue(var21);
            var10000 = var21;
         } else {
            var10000 = var18;
         }

         MutableState var14 = (MutableState)var10000;
         var27.endReplaceGroup();
         MutableState var13 = var14;
         long var28 = ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(DefaultAvatar_Rd90Nhg$lambda$2(var13));
         boolean var29 = ColorExtensionsKt.recovered$41e561cae61044c5(DefaultAvatar_Rd90Nhg$lambda$2(var13));
         var27.startReplaceGroup(870372748);
         ComposerKt.sourceInformation(var27, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var33 = var27;
         boolean var35 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var33.rememberedValue();
         int var23/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var45;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var24 = 0;
            int var25 = 8;
            int var26 = 0;
            Object var41 = SnapshotStateKt.mutableStateOf$default(Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var25)), null, 2, null);
            var33.updateRememberedValue(var41);
            var45 = var41;
         } else {
            var45 = it$ivx;
         }

         MutableState var31 = (MutableState)var45;
         var27.endReplaceGroup();
         MutableState var30 = var31;
         var27.startReplaceGroup(870374477);
         ComposerKt.sourceInformation(var27, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var36 = var27;
         boolean var38 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var36.rememberedValue();
         int var42/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var46;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var43 = 0;
            Object var44 = SnapshotStateKt.mutableStateOf$default(var2, null, 2, null);
            var36.updateRememberedValue(var44);
            var46 = var44;
         } else {
            var46 = it$ivxx;
         }

         MutableState var34 = (MutableState)var46;
         var27.endReplaceGroup();
         MutableState var32 = var34;
         BoxWithConstraintsKt.BoxWithConstraints(
            var1,
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(
               -1891463123, true, new AvatarIconKt$DefaultAvatar$1(var3, var2, var29, var30, var32, var13, var0, var11, var6, var28, var4), var27, 54
            ),
            var27,
            3072 | 14 & var10 >> 3,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var47 = var27.endRestartGroup();
      if (var47 != null) {
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredExpressionCapture_275_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_275_1 = var1;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_275_2 = var2;
         final boolean recoveredExpressionCapture_275_3 = var3;
         final long recoveredExpressionCapture_275_4 = var4;
         final androidx.compose.ui.graphics.Color recoveredExpressionCapture_275_5 = var6;
         final int recoveredExpressionCapture_275_6 = var8;
         final int recoveredExpressionCapture_275_7 = var9;
         var47.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_275_0, recoveredExpressionArgument_275_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.DefaultAvatar_Rd90Nhg$lambda$10(recoveredExpressionCapture_275_0, recoveredExpressionCapture_275_1, recoveredExpressionCapture_275_2, recoveredExpressionCapture_275_3, recoveredExpressionCapture_275_4, recoveredExpressionCapture_275_5, recoveredExpressionCapture_275_6, recoveredExpressionCapture_275_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_275_0), ((java.lang.Integer)recoveredExpressionArgument_275_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FinAvatar(Modifier var0, AvatarWrapper var1, Shape var2, Composer var3, int var4, int var5) {
      Composer var14 = var3.startRestartGroup(-1375245291);
      ComposerKt.sourceInformation(var14, "C(FinAvatar)P(1)227@7890L13:AvatarIcon.kt#ssa3oh");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var14.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var14.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var6 |= (var5 & 4) == 0 && var14.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var14.getSkipping()) {
         var14.skipToGroupEnd();
      } else {
         var14.startDefaults();
         if ((var4 & 1) != 0 && !var14.getDefaultsInvalid()) {
            var14.skipToGroupEnd();
            if ((var5 & 4) != 0) {
               var6 &= -897;
            }
         } else {
            if ((var5 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var5 & 4) != 0) {
               var2 = getComposeShape(AvatarShape.SQUIRCLE);
               var6 &= -897;
            }
         }

         var14.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1375245291, var6, -1, "io.intercom.android.sdk.m5.components.avatar.FinAvatar (AvatarIcon.kt:225)");
         }

         Modifier var7 = var0.then(ClipKt.clip(Modifier.Companion, var2));
         String var8 = var1.getImageUrl(var14, 14 & var6 >> 3);
         if (!StringsKt.isBlank(var8)) {
            var14.startReplaceGroup(-446445875);
            ComposerKt.sourceInformation(var14, "233@8120L7,235@8221L190,241@8437L190,230@7962L680");
            CompositionLocal var10 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var11 = 0;
            int var12 = 0;
            ComposerKt.sourceInformationMarkerStart(var14, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var13 = var14.consume(var10);
            ComposerKt.sourceInformationMarkerEnd(var14);
            ImageLoader var9 = IntercomImageLoaderKt.getImageLoader((Context)var13);
            String var15 = var1.getAvatar().getLabel();
            SubcomposeAsyncImageKt.recovered$9e62f4c19284676e(
               var8,
               var15,
               var9,
               var7,
               null,
               ComposableLambdaKt.rememberComposableLambda(-1294140715, true, new AvatarIconKt$FinAvatar$1(var1, var7), var14, 54),
               null,
               ComposableLambdaKt.rememberComposableLambda(-818047861, true, new AvatarIconKt$FinAvatar$2(var1, var7), var14, 54),
               null,
               null,
               null,
               null,
               null,
               0.0F,
               null,
               0,
               false,
               null,
               var14,
               12779520,
               0,
               261968
            );
            var14.endReplaceGroup();
         } else {
            var14.startReplaceGroup(-445746825);
            ComposerKt.sourceInformation(var14, "251@8684L126");
            FinAvatarPlaceholder(var1, var7, 0.0F, var14, 14 & var6 >> 3, 4);
            var14.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var14.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_273_0 = var0;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredExpressionCapture_273_1 = var1;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_273_2 = var2;
         final int recoveredExpressionCapture_273_3 = var4;
         final int recoveredExpressionCapture_273_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_273_0, recoveredExpressionArgument_273_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.FinAvatar$lambda$11(recoveredExpressionCapture_273_0, recoveredExpressionCapture_273_1, recoveredExpressionCapture_273_2, recoveredExpressionCapture_273_3, recoveredExpressionCapture_273_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_273_0), ((java.lang.Integer)recoveredExpressionArgument_273_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FinAvatarPlaceholder(AvatarWrapper var0, Modifier var1, float var2, Composer var3, int var4, int var5) {
      Composer var39 = var3.startRestartGroup(-427803587);
      ComposerKt.sourceInformation(var39, "C(FinAvatarPlaceholder)P(1,2)265@9012L6,266@9039L277:AvatarIcon.kt#ssa3oh");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var39.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var39.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var39.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var39.getSkipping()) {
         var39.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var2 = 1.0F;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-427803587, var6, -1, "io.intercom.android.sdk.m5.components.avatar.FinAvatarPlaceholder (AvatarIcon.kt:264)");
         }

         long var7 = ColorExtensionsKt.recovered$da172df9fccf1b21(IntercomTheme.INSTANCE.getColors(var39, IntercomTheme.$stable).recovered$202acdf3dbb5c020());
         Modifier var9 = BackgroundKt.recovered$42a3174180b3fb7a(var1, var7, null, 2, null);
         Alignment var10 = Alignment.Companion.getCenter();
         byte var12 = 48;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var39, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var11 = false;
         MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var10, var11);
         Modifier var15 = var9;
         MeasurePolicy var16 = var14;
         Composer var17 = var39;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         BoxScope var34 = BoxScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 504463730, "C270@9162L148:AvatarIcon.kt#ssa3oh");
         Modifier var10000 = Modifier.Companion;
         int var36 = 4;
         int var37 = 0;
         Modifier var38 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var36));
         BotAvatarPlaceholder(var0, var38, var2, var33, 48 | 14 & var6 | 896 & var6, 0);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var39);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var40 = var39.endRestartGroup();
      if (var40 != null) {
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredExpressionCapture_274_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_274_1 = var1;
         final float recoveredExpressionCapture_274_2 = var2;
         final int recoveredExpressionCapture_274_3 = var4;
         final int recoveredExpressionCapture_274_4 = var5;
         var40.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_274_0, recoveredExpressionArgument_274_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.FinAvatarPlaceholder$lambda$13(recoveredExpressionCapture_274_0, recoveredExpressionCapture_274_1, recoveredExpressionCapture_274_2, recoveredExpressionCapture_274_3, recoveredExpressionCapture_274_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_274_0), ((java.lang.Integer)recoveredExpressionArgument_274_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BotAvatarPlaceholder(AvatarWrapper var0, Modifier var1, float var2, Composer var3, int var4, int var5) {
      Composer var9 = var3.startRestartGroup(1859249921);
      ComposerKt.sourceInformation(var9, "C(BotAvatarPlaceholder)P(1,2)286@9520L61,284@9466L202:AvatarIcon.kt#ssa3oh");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var9.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var9.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var9.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var2 = 1.0F;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1859249921, var6, -1, "io.intercom.android.sdk.m5.components.avatar.BotAvatarPlaceholder (AvatarIcon.kt:283)");
         }

         Painter var7 = PainterResources_androidKt.painterResource(drawable.intercom_default_avatar_icon, var9, 0);
         String var8 = var0.getAvatar().getLabel();
         ImageKt.Image(var7, var8, var1, null, null, var2, null, var9, Painter.$stable | 896 & var6 << 3 | 458752 & var6 << 9, 88);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredExpressionCapture_271_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_271_1 = var1;
         final float recoveredExpressionCapture_271_2 = var2;
         final int recoveredExpressionCapture_271_3 = var4;
         final int recoveredExpressionCapture_271_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_271_0, recoveredExpressionArgument_271_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.BotAvatarPlaceholder$lambda$14(recoveredExpressionCapture_271_0, recoveredExpressionCapture_271_1, recoveredExpressionCapture_271_2, recoveredExpressionCapture_271_3, recoveredExpressionCapture_271_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_271_0), ((java.lang.Integer)recoveredExpressionArgument_271_1).intValue())));
      }
   }

   @NotNull
   public static final Modifier avatarBorder(@NotNull Modifier var0, boolean var1, @NotNull Shape var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var2, "shape");
      Modifier var10000;
      if (var1) {
         double var3 = 0.5;
         int var5 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060((float)var3);
         Companion var10002 = Brush.Companion;
         Color[] var6 = new Color[]{Color.recovered$f46ef195ad4a2363(ColorKt.Color(872415231)), Color.recovered$f46ef195ad4a2363(ColorKt.Color(872415231))};
         var10000 = BorderKt.recovered$39c134d83b2500ef(
            var0, var10001, Companion.recovered$613a33dedc04a927(var10002, CollectionsKt.listOf(var6), 0.0F, 0.0F, 0, 14, null), var2
         );
      } else {
         var10000 = var0;
      }

      return var10000;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarActiveIndicator(Modifier var0, Composer var1, int var2, int var3) {
      Composer var16 = var1.startRestartGroup(-1051352444);
      ComposerKt.sourceInformation(var16, "C(AvatarActiveIndicator)313@10380L6,316@10463L55,314@10398L127:AvatarIcon.kt#ssa3oh");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var16.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var16.getSkipping()) {
         var16.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1051352444, var4, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarActiveIndicator (AvatarIcon.kt:312)");
         }

         long var5 = IntercomTheme.INSTANCE.getColors(var16, IntercomTheme.$stable).recovered$b549188d2697e772();
         int var7 = 8;
         int var8 = 0;
         Modifier var10000 = SizeKt.recovered$111ef94c93e78c04(var0, Dp.recovered$95b3d103663bf060(var7));
         var16.startReplaceGroup(821186808);
         ComposerKt.sourceInformation(var16, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var18 = var16;
         boolean var9 = var16.changed(var5);
         int var10 = 0;
         Object var11 = var18.rememberedValue();
         int var12 = 0;
         Object var20;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var20 = var11;
         } else {
            Modifier var15 = var10000;
            int var13 = 0;
            final long recoveredExpressionCapture_265_0 = var5;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_265_0) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarActiveIndicator$lambda$16$lambda$15(recoveredExpressionCapture_265_0, (androidx.compose.ui.graphics.drawscope.DrawScope) ((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredExpressionArgument_265_0)));
            var10000 = var15;
            Object var14 = var10001;
            var18.updateRememberedValue(var14);
            var20 = var14;
         }

         Function1 var17 = (Function1)var20;
         var16.endReplaceGroup();
         CanvasKt.Canvas(var10000, var17, var16, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var19 = var16.endRestartGroup();
      if (var19 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_266_0 = var0;
         final int recoveredExpressionCapture_266_1 = var2;
         final int recoveredExpressionCapture_266_2 = var3;
         var19.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_266_0, recoveredExpressionArgument_266_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarActiveIndicator$lambda$17(recoveredExpressionCapture_266_0, recoveredExpressionCapture_266_1, recoveredExpressionCapture_266_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_266_0), ((java.lang.Integer)recoveredExpressionArgument_266_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$e6846e68e373b473(Modifier var0, String var1, long var2, long var4, String var6, Composer var7, int var8, int var9) {
      Composer var49 = var7.startRestartGroup(-99590848);
      ComposerKt.sourceInformation(var49, "C(AvatarPlaceholder)P(2!1,3:c#ui.graphics.Color,4:c#ui.unit.TextUnit)330@10721L737:AvatarIcon.kt#ssa3oh");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var49.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var49.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var49.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var49.changed(var4) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var49.changed(var6) ? 16384 : 8192;
      }

      if ((var10 & 9363) == 9362 && var49.getSkipping()) {
         var49.skipToGroupEnd();
      } else {
         if ((var9 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-99590848, var10, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarPlaceholder (AvatarIcon.kt:329)");
         }

         Alignment var11 = Alignment.Companion.getCenter();
         int var13 = 48 | 14 & var10;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var49, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var12 = false;
         MeasurePolicy var15 = BoxKt.maybeCachedBoxMeasurePolicy(var11, var12);
         Modifier var16 = var0;
         MeasurePolicy var17 = var15;
         Composer var18 = var49;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         BoxScope var35 = BoxScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, -2026585018, "C:AvatarIcon.kt#ssa3oh");
         if (!StringsKt.isBlank(var1)) {
            var34.startReplaceGroup(-2026561893);
            ComposerKt.sourceInformation(var34, "333@10892L84,338@11090L10,332@10840L325");
            Modifier var10000 = Modifier.Companion;
            boolean var10001 = false;
            var34.startReplaceGroup(-758107778);
            ComposerKt.sourceInformation(var34, "CC(remember):AvatarIcon.kt#9igjgp");
            Composer var50 = var34;
            boolean var51 = (var10 & 57344) == 16384;
            int var39 = 0;
            Object var40 = var50.rememberedValue();
            int var41 = 0;
            Object var10002;
            if (!var51 && var40 != Composer.Companion.getEmpty()) {
               var10002 = var40;
            } else {
               boolean var42 = false;
               Modifier var43 = var10000;
               int var44 = 0;
               final java.lang.String recoveredExpressionCapture_276_0 = var6;
               Function1 var45 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_276_0) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarPlaceholder_jxWH9Kg$lambda$20$lambda$19$lambda$18(recoveredExpressionCapture_276_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_276_0)));
               var10000 = var43;
               var10001 = var42;
               Object var46 = var45;
               var50.updateRememberedValue(var46);
               var10002 = var46;
            }

            Function1 var52 = (Function1)var10002;
            var34.endReplaceGroup();
            Modifier var54 = SemanticsModifierKt.semantics$default(var10000, var10001, var52, 1, null);
            TextStyle var53 = IntercomTheme.INSTANCE.getTypography(var34, IntercomTheme.$stable).getType04Point5();
            TextKt.recovered$5b114ab32a4f4190(
               var1,
               var54,
               var2,
               null,
               var4,
               null,
               null,
               null,
               0L,
               null,
               null,
               0L,
               0,
               false,
               0,
               0,
               null,
               var53,
               var34,
               14 & var10 >> 3 | 896 & var10 | 57344 & var10 << 3,
               0,
               131048
            );
            var34.endReplaceGroup();
         } else {
            var34.startReplaceGroup(-2026212151);
            ComposerKt.sourceInformation(var34, "343@11227L58,342@11195L247");
            Painter var48 = PainterResources_androidKt.painterResource(drawable.intercom_ic_avatar_person, var34, 0);
            int var37 = 8;
            int var38 = 0;
            Modifier var47 = PaddingKt.recovered$eb6a744cd98cdbca(var0, Dp.recovered$95b3d103663bf060(var37));
            IconKt.recovered$e00adffb7fe45b02(var48, var6, var47, var2, var34, Painter.$stable | 112 & var10 >> 9 | 7168 & var10 << 3, 0);
            var34.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var49);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var55 = var49.endRestartGroup();
      if (var55 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_277_0 = var0;
         final java.lang.String recoveredExpressionCapture_277_1 = var1;
         final long recoveredExpressionCapture_277_2 = var2;
         final long recoveredExpressionCapture_277_3 = var4;
         final java.lang.String recoveredExpressionCapture_277_4 = var6;
         final int recoveredExpressionCapture_277_5 = var8;
         final int recoveredExpressionCapture_277_6 = var9;
         var55.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_277_0, recoveredExpressionArgument_277_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarPlaceholder_jxWH9Kg$lambda$21(recoveredExpressionCapture_277_0, recoveredExpressionCapture_277_1, recoveredExpressionCapture_277_2, recoveredExpressionCapture_277_3, recoveredExpressionCapture_277_4, recoveredExpressionCapture_277_5, recoveredExpressionCapture_277_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_277_0), ((java.lang.Integer)recoveredExpressionArgument_277_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarIconPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1461886463);
      ComposerKt.sourceInformation(var2, "C(AvatarIconPreview)355@11530L481:AvatarIcon.kt#ssa3oh");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1461886463, var1, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarIconPreview (AvatarIcon.kt:354)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_269_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_269_0, recoveredExpressionArgument_269_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarIconPreview$lambda$22(recoveredExpressionCapture_269_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_269_0), ((java.lang.Integer)recoveredExpressionArgument_269_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarIconActivePreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-382759013);
      ComposerKt.sourceInformation(var2, "C(AvatarIconActivePreview)373@12089L547:AvatarIcon.kt#ssa3oh");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-382759013, var1, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarIconActivePreview (AvatarIcon.kt:372)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_267_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_267_0, recoveredExpressionArgument_267_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarIconActivePreview$lambda$23(recoveredExpressionCapture_267_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_267_0), ((java.lang.Integer)recoveredExpressionArgument_267_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarIconSquirclePreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1626854011);
      ComposerKt.sourceInformation(var2, "C(AvatarIconSquirclePreview)393@12716L599:AvatarIcon.kt#ssa3oh");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1626854011, var1, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarIconSquirclePreview (AvatarIcon.kt:392)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_270_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_270_0, recoveredExpressionArgument_270_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarIconSquirclePreview$lambda$24(recoveredExpressionCapture_270_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_270_0), ((java.lang.Integer)recoveredExpressionArgument_270_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarIconCutPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1591864993);
      ComposerKt.sourceInformation(var2, "C(AvatarIconCutPreview)413@13390L785:AvatarIcon.kt#ssa3oh");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1591864993, var1, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarIconCutPreview (AvatarIcon.kt:412)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.INSTANCE.recovered$54bdcf11cafa5012(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_268_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_268_0, recoveredExpressionArgument_268_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.AvatarIconCutPreview$lambda$25(recoveredExpressionCapture_268_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_268_0), ((java.lang.Integer)recoveredExpressionArgument_268_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BotAvatarPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1158049743);
      ComposerKt.sourceInformation(var2, "C(BotAvatarPreview)439@14246L509:AvatarIcon.kt#ssa3oh");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1158049743, var1, -1, "io.intercom.android.sdk.m5.components.avatar.BotAvatarPreview (AvatarIcon.kt:438)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.components.avatar.ComposableSingletons$AvatarIconKt.INSTANCE.recovered$265b6f46b79d310a(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_272_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_272_0, recoveredExpressionArgument_272_1) -> io.intercom.android.sdk.m5.components.avatar.AvatarIconKt.BotAvatarPreview$lambda$26(recoveredExpressionCapture_272_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_272_0), ((java.lang.Integer)recoveredExpressionArgument_272_1).intValue())));
      }
   }

   private static final Unit AvatarIcon_Rd90Nhg$lambda$0(
      Modifier var0, AvatarWrapper var1, Shape var2, boolean var3, long var4, Color var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$f5ec3c81e3146789(var0, var1, var2, var3, var4, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final long DefaultAvatar_Rd90Nhg$lambda$2(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Color)var1.getValue()).recovered$45f9de7b85e0e0b5();
   }

   private static final void DefaultAvatar_Rd90Nhg$lambda$3(MutableState var0, long var1) {
      MutableState var3 = var0;
      Object var4 = null;
      Object var5 = null;
      Object var6 = Color.recovered$f46ef195ad4a2363(var1);
      int var7 = 0;
      var3.setValue(var6);
   }

   private static final float DefaultAvatar_Rd90Nhg$lambda$5(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Dp)var1.getValue()).recovered$3537cef520440dc0();
   }

   private static final void DefaultAvatar_Rd90Nhg$lambda$6(MutableState var0, float var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = Dp.recovered$89bc2ea8129b5eb8(var1);
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Shape DefaultAvatar_Rd90Nhg$lambda$8(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Shape)var1.getValue();
   }

   private static final void DefaultAvatar_Rd90Nhg$lambda$9(MutableState var0, Shape var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DefaultAvatar_Rd90Nhg$Placeholder(AvatarWrapper var0, long var1, long var3, Modifier var5, Composer var6, int var7, int var8) {
      var6.startReplaceGroup(1593692287);
      ComposerKt.sourceInformation(var6, "C(Placeholder):AvatarIcon.kt#ssa3oh");
      if ((var8 & 1) != 0) {
         var5 = Modifier.Companion;
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1593692287, var7, -1, "io.intercom.android.sdk.m5.components.avatar.DefaultAvatar.Placeholder (AvatarIcon.kt:143)");
      }

      if (var0.isBot()) {
         var6.startReplaceGroup(-730785003);
         ComposerKt.sourceInformation(var6, "145@5102L133");
         int var10 = 8;
         int var11 = 0;
         Modifier var9 = PaddingKt.recovered$eb6a744cd98cdbca(var5, Dp.recovered$95b3d103663bf060(var10));
         BotAvatarPlaceholder(var0, var9, 0.0F, var6, 0, 4);
         var6.endReplaceGroup();
      } else {
         var6.startReplaceGroup(-730619029);
         ComposerKt.sourceInformation(var6, "150@5265L271");
         recovered$e6846e68e373b473(var5, var0.getInitials(), var1, var3, var0.getLabel(), var6, 14 & var7, 0);
         var6.endReplaceGroup();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var6.endReplaceGroup();
   }

   private static final Unit DefaultAvatar_Rd90Nhg$lambda$10(
      AvatarWrapper var0, Modifier var1, Shape var2, boolean var3, long var4, Color var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$366294016de51a70(var0, var1, var2, var3, var4, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit FinAvatar$lambda$11(Modifier var0, AvatarWrapper var1, Shape var2, int var3, int var4, Composer var5, int var6) {
      FinAvatar(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit FinAvatarPlaceholder$lambda$13(AvatarWrapper var0, Modifier var1, float var2, int var3, int var4, Composer var5, int var6) {
      FinAvatarPlaceholder(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit BotAvatarPlaceholder$lambda$14(AvatarWrapper var0, Modifier var1, float var2, int var3, int var4, Composer var5, int var6) {
      BotAvatarPlaceholder(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarActiveIndicator$lambda$16$lambda$15(long var0, DrawScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$Canvas");
      DrawScope.recovered$34dce0a89fdcb963(var2, var0, 0.0F, 0L, 0.0F, null, null, 0, 126, null);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarActiveIndicator$lambda$17(Modifier var0, int var1, int var2, Composer var3, int var4) {
      AvatarActiveIndicator(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarPlaceholder_jxWH9Kg$lambda$20$lambda$19$lambda$18(String var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, var0);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarPlaceholder_jxWH9Kg$lambda$21(
      Modifier var0, String var1, long var2, long var4, String var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$e6846e68e373b473(var0, var1, var2, var4, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarIconPreview$lambda$22(int var0, Composer var1, int var2) {
      AvatarIconPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AvatarIconActivePreview$lambda$23(int var0, Composer var1, int var2) {
      AvatarIconActivePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AvatarIconSquirclePreview$lambda$24(int var0, Composer var1, int var2) {
      AvatarIconSquirclePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AvatarIconCutPreview$lambda$25(int var0, Composer var1, int var2) {
      AvatarIconCutPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BotAvatarPreview$lambda$26(int var0, Composer var1, int var2) {
      BotAvatarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$DefaultAvatar_Rd90Nhg$lambda$6(MutableState var0, float var1) {
      DefaultAvatar_Rd90Nhg$lambda$6(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$DefaultAvatar_Rd90Nhg$lambda$9(MutableState var0, Shape var1) {
      DefaultAvatar_Rd90Nhg$lambda$9(var0, var1);
   }

   // $VF: synthetic method
   public static final float access$DefaultAvatar_Rd90Nhg$lambda$5(MutableState var0) {
      return DefaultAvatar_Rd90Nhg$lambda$5(var0);
   }

   // $VF: synthetic method
   public static final long access$DefaultAvatar_Rd90Nhg$lambda$2(MutableState var0) {
      return DefaultAvatar_Rd90Nhg$lambda$2(var0);
   }

   // $VF: synthetic method
   public static final Shape access$DefaultAvatar_Rd90Nhg$lambda$8(MutableState var0) {
      return DefaultAvatar_Rd90Nhg$lambda$8(var0);
   }

   // $VF: synthetic method
   public static final void access$AvatarActiveIndicator(Modifier var0, Composer var1, int var2, int var3) {
      AvatarActiveIndicator(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$DefaultAvatar_Rd90Nhg$lambda$3(MutableState var0, long var1) {
      DefaultAvatar_Rd90Nhg$lambda$3(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$DefaultAvatar_Rd90Nhg$Placeholder(AvatarWrapper var0, long var1, long var3, Modifier var5, Composer var6, int var7, int var8) {
      DefaultAvatar_Rd90Nhg$Placeholder(var0, var1, var3, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static final void access$FinAvatarPlaceholder(AvatarWrapper var0, Modifier var1, float var2, Composer var3, int var4, int var5) {
      FinAvatarPlaceholder(var0, var1, var2, var3, var4, var5);
   }
}
