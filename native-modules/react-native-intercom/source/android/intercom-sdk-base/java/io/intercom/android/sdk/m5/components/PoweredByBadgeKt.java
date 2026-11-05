package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.CardColors;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardKt;
import androidx.compose.material3.InteractiveComponentSizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u001aK\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000bH\u0001¢\u0006\u0004\b\r\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0010H\u0003¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0017\u001a\u00020\u0013H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001a\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001a\"\u0010\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0014\"\u0010\u0010\u0015\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0014\"\u0010\u0010\u0016\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0014¨\u0006\u001c",
   d2 = {
         "PoweredByBadge",
         "",
         "text",
         "",
         "icon",
         "Lio/intercom/android/sdk/models/PoweredBy$PoweredByIconType;",
         "onClick",
         "Lkotlin/Function0;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "PoweredByBadge-wBJOh4Y",
         "(Ljava/lang/String;Lio/intercom/android/sdk/models/PoweredBy$PoweredByIconType;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;JJLandroidx/compose/runtime/Composer;II)V",
         "getTextStyle",
         "Landroidx/compose/ui/text/TextStyle;",
         "(Landroidx/compose/runtime/Composer;I)Landroidx/compose/ui/text/TextStyle;",
         "VerticalPadding",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "HorizontalPadding",
         "IconSize",
         "getPoweredByBadgeHeight",
         "(Landroidx/compose/runtime/Composer;I)F",
         "PoweredByBadgePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TransparentPoweredByBadgePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPoweredByBadge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/PoweredByBadgeKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/Dp\n*L\n1#1,122:1\n75#2:123\n138#3:124\n118#3:126\n118#3:127\n118#3:128\n49#4:125\n*S KotlinDebug\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/PoweredByBadgeKt\n*L\n88#1:123\n91#1:124\n82#1:126\n83#1:127\n84#1:128\n91#1:125\n*E\n"
)
public final class PoweredByBadgeKt {
   private static final float VerticalPadding;
   private static final float HorizontalPadding;
   private static final float IconSize;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$9b47502c50dedaf9(
      @NotNull String var0,
      @NotNull PoweredBy$PoweredByIconType var1,
      @NotNull Function0 var2,
      @Nullable Modifier var3,
      long var4,
      long var6,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var0, "text");
      Intrinsics.checkNotNullParameter(var1, "icon");
      Intrinsics.checkNotNullParameter(var2, "onClick");
      var8 = var8.startRestartGroup(546776392);
      ComposerKt.sourceInformation(
         var8,
         "C(PoweredByBadge)P(5,2,4,3,0:c#ui.graphics.Color,1:c#ui.graphics.Color)35@1420L6,37@1545L6,48@1920L67,51@2019L6,53@2066L746,46@1821L991:PoweredByBadge.kt#arqjag"
      );
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var8.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var8.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var8.changedInstance(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var8.changed(var3) ? 2048 : 1024;
      }

      if ((var9 & 24576) == 0) {
         var11 |= (var10 & 16) == 0 && var8.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 196608) == 0) {
         var11 |= (var10 & 32) == 0 && var8.changed(var6) ? 131072 : 65536;
      }

      if ((var11 & 74899) == 74898 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         var8.startDefaults();
         if ((var9 & 1) != 0 && !var8.getDefaultsInvalid()) {
            var8.skipToGroupEnd();
            if ((var10 & 16) != 0) {
               var11 &= -57345;
            }

            if ((var10 & 32) != 0) {
               var11 &= -458753;
            }
         } else {
            if ((var10 & 8) != 0) {
               var3 = Modifier.Companion;
            }

            if ((var10 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getColors(var8, IntercomTheme.$stable).recovered$c65d7201b2ad76fa();
               var11 &= -57345;
            }

            if ((var10 & 32) != 0) {
               var6 = Color.recovered$9eab66ea8c46e11c(var4, Color.Companion.recovered$562f21ab17d9b4c7())
                  ? IntercomTheme.INSTANCE.getColors(var8, IntercomTheme.$stable).getText().recovered$0aef7413383f6122()
                  : ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var4);
               var11 &= -458753;
            }
         }

         var8.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(546776392, var11, -1, "io.intercom.android.sdk.m5.components.PoweredByBadge (PoweredByBadge.kt:41)");
         }

         int var10000;
         switch (var1) {
            case INTERCOM:
               var10000 = drawable.intercom_logo;
               break;
            case FIN:
               var10000 = drawable.intercom_fin_ai_agent;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         int var12 = var10000;
         Modifier var13 = InteractiveComponentSizeKt.minimumInteractiveComponentSize(var3);
         CardColors var14 = CardDefaults.INSTANCE.recovered$da184985090a28d4(var4, 0L, 0L, 0L, var8, 14 & var11 >> 12 | CardDefaults.$stable << 12, 14);
         CornerBasedShape var15 = IntercomTheme.INSTANCE.getShapes(var8, IntercomTheme.$stable).getSmall();
         CardKt.Card(
            var2,
            var13,
            false,
            var15,
            var14,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(803643315, true, new PoweredByBadgeKt$PoweredByBadge$1(var12, var6, var0), var8, 54),
            var8,
            100663296 | 14 & var11 >> 6,
            228
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var8.endRestartGroup();
      if (var17 != null) {
         final java.lang.String recoveredCapture_234_0 = var0;
         final io.intercom.android.sdk.models.PoweredBy$PoweredByIconType recoveredCapture_234_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_234_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_234_3 = var3;
         final long recoveredCapture_234_4 = var4;
         final long recoveredCapture_234_5 = var6;
         final int recoveredCapture_234_6 = var9;
         final int recoveredCapture_234_7 = var10;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_234_0, recoveredArgument_234_1) -> PoweredByBadgeKt.PoweredByBadge_wBJOh4Y$lambda$0(recoveredCapture_234_0, recoveredCapture_234_1, recoveredCapture_234_2, recoveredCapture_234_3, recoveredCapture_234_4, recoveredCapture_234_5, recoveredCapture_234_6, recoveredCapture_234_7, (androidx.compose.runtime.Composer)recoveredArgument_234_0, ((java.lang.Integer)recoveredArgument_234_1).intValue())));
      }
   }

   @Composable
   private static final TextStyle getTextStyle(Composer var0, int var1) {
      var0.startReplaceGroup(634566382);
      ComposerKt.sourceInformation(var0, "C(getTextStyle)80@2871L10:PoweredByBadge.kt#arqjag");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(634566382, var1, -1, "io.intercom.android.sdk.m5.components.getTextStyle (PoweredByBadge.kt:80)");
      }

      TextStyle var2 = IntercomTheme.INSTANCE.getTypography(var0, IntercomTheme.$stable).getType05();
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var0.endReplaceGroup();
      return var2;
   }

   @Composable
   public static final float getPoweredByBadgeHeight(@Nullable Composer var0, int var1) {
      var0.startReplaceGroup(-1418088879);
      ComposerKt.sourceInformation(var0, "C(getPoweredByBadgeHeight)*87@3088L7,88@3107L14:PoweredByBadge.kt#arqjag");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1418088879, var1, -1, "io.intercom.android.sdk.m5.components.getPoweredByBadgeHeight (PoweredByBadge.kt:86)");
      }

      CompositionLocal var4 = CompositionLocalsKt.getLocalDensity();
      int var5 = 0;
      int var6 = 0;
      ComposerKt.sourceInformationMarkerStart(var0, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var7 = var0.consume(var4);
      ComposerKt.sourceInformationMarkerEnd(var0);
      Density var10 = (Density)var7;
      var6 = 0;
      float var2 = var10.recovered$f7556ea49f9f71e9(getTextStyle(var0, 0).recovered$8f8bc2178e9073c3());
      float var8 = VerticalPadding;
      float var11 = IconSize;
      var6 = 0;
      float var12 = Dp.recovered$95b3d103663bf060(Math.max(var2, var11));
      var6 = 0;
      float var9 = Dp.recovered$95b3d103663bf060(var8 + var12);
      float var13 = VerticalPadding;
      var6 = 0;
      float var3 = Dp.recovered$95b3d103663bf060(var9 + var13);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var0.endReplaceGroup();
      return var3;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PoweredByBadgePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(233774014);
      ComposerKt.sourceInformation(var0, "C(PoweredByBadgePreview)96@3293L228:PoweredByBadge.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(233774014, var1, -1, "io.intercom.android.sdk.m5.components.PoweredByBadgePreview (PoweredByBadge.kt:95)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PoweredByBadgeKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_232_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_232_0, recoveredArgument_232_1) -> PoweredByBadgeKt.PoweredByBadgePreview$lambda$2(recoveredCapture_232_0, (androidx.compose.runtime.Composer)recoveredArgument_232_0, ((java.lang.Integer)recoveredArgument_232_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TransparentPoweredByBadgePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1988629996);
      ComposerKt.sourceInformation(var0, "C(TransparentPoweredByBadgePreview)110@3609L280:PoweredByBadge.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1988629996, var1, -1, "io.intercom.android.sdk.m5.components.TransparentPoweredByBadgePreview (PoweredByBadge.kt:109)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PoweredByBadgeKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_233_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_233_0, recoveredArgument_233_1) -> PoweredByBadgeKt.TransparentPoweredByBadgePreview$lambda$3(recoveredCapture_233_0, (androidx.compose.runtime.Composer)recoveredArgument_233_0, ((java.lang.Integer)recoveredArgument_233_1).intValue())));
      }
   }

   private static final Unit PoweredByBadge_wBJOh4Y$lambda$0(
      String var0, PoweredBy$PoweredByIconType var1, Function0 var2, Modifier var3, long var4, long var6, int var8, int var9, Composer var10, int var11
   ) {
      recovered$9b47502c50dedaf9(var0, var1, var2, var3, var4, var6, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit PoweredByBadgePreview$lambda$2(int var0, Composer var1, int var2) {
      PoweredByBadgePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TransparentPoweredByBadgePreview$lambda$3(int var0, Composer var1, int var2) {
      TransparentPoweredByBadgePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$getHorizontalPadding$p() {
      return HorizontalPadding;
   }

   // $VF: synthetic method
   public static final float access$getVerticalPadding$p() {
      return VerticalPadding;
   }

   // $VF: synthetic method
   public static final float access$getIconSize$p() {
      return IconSize;
   }

   // $VF: synthetic method
   public static final TextStyle access$getTextStyle(Composer var0, int var1) {
      return getTextStyle(var0, var1);
   }

   static {
      int var0 = 6;
      int var1 = 0;
      VerticalPadding = Dp.recovered$95b3d103663bf060(var0);
      var0 = 16;
      var1 = 0;
      HorizontalPadding = Dp.recovered$95b3d103663bf060(var0);
      var0 = 16;
      var1 = 0;
      IconSize = Dp.recovered$95b3d103663bf060(var0);
   }
}
