package io.intercom.android.sdk.ui.theme;

import android.view.View;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.material3.ContentColorKt;
import androidx.compose.material3.Shapes;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a@\u0010\u0000\u001a\u00020\u00012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0011\u0010\b\u001a\r\u0012\u0004\u0012\u00020\u00010\t¢\u0006\u0002\b\nH\u0007¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\rH\u0003¢\u0006\u0002\u0010\u000e\"\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u0013",
   d2 = {
         "IntercomTheme",
         "",
         "colors",
         "Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "typography",
         "Lio/intercom/android/sdk/ui/theme/IntercomTypography;",
         "shapes",
         "Landroidx/compose/material3/Shapes;",
         "content",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Lio/intercom/android/sdk/ui/theme/IntercomColors;Lio/intercom/android/sdk/ui/theme/IntercomTypography;Landroidx/compose/material3/Shapes;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "isDarkThemeInEditMode",
         "",
         "(Landroidx/compose/runtime/Composer;I)Z",
         "LocalShapes",
         "Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "getLocalShapes",
         "()Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomTheme.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTheme.kt\nio/intercom/android/sdk/ui/theme/IntercomThemeKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,61:1\n75#2:62\n*S KotlinDebug\n*F\n+ 1 IntercomTheme.kt\nio/intercom/android/sdk/ui/theme/IntercomThemeKt\n*L\n46#1:62\n*E\n"
)
public final class IntercomThemeKt {
   @NotNull
   private static final ProvidableCompositionLocal LocalShapes = CompositionLocalKt.staticCompositionLocalOf(((kotlin.jvm.functions.Function0) () -> IntercomThemeKt.LocalShapes$lambda$1()));

   @Composable
   @ComposableInferredTarget(scheme = "[0[0]]")
   public static final void IntercomTheme(
      @Nullable IntercomColors var0,
      @Nullable IntercomTypography var1,
      @Nullable Shapes var2,
      @NotNull Function2 var3,
      @Nullable Composer var4,
      int var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var3, "content");
      var4 = var4.startRestartGroup(163228735);
      ComposerKt.sourceInformation(var4, "C(IntercomTheme)P(!1,3,2)14@567L10,15@614L6,34@1445L221,30@1243L423:IntercomTheme.kt#o58l0d");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var7 |= (var6 & 2) == 0 && var4.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         var4.startDefaults();
         if ((var5 & 1) != 0 && !var4.getDefaultsInvalid()) {
            var4.skipToGroupEnd();
            if ((var6 & 2) != 0) {
               var7 &= -113;
            }

            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 1) != 0) {
               var0 = null;
            }

            if ((var6 & 2) != 0) {
               var1 = IntercomTheme.INSTANCE.getTypography(var4, 6);
               var7 &= -113;
            }

            if ((var6 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getShapes(var4, 6);
               var7 &= -897;
            }
         }

         var4.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(163228735, var7, -1, "io.intercom.android.sdk.ui.theme.IntercomTheme (IntercomTheme.kt:17)");
         }

         IntercomColors var9 = var0;
         var4.startReplaceGroup(-541037209);
         ComposerKt.sourceInformation(var4, "20@798L23,24@1046L21");
         IntercomColors var10000 = var9;
         if (var9 == null) {
            if (isDarkThemeInEditMode(var4, 0)) {
               var10000 = IntercomColorsKt.intercomDarkColors();
            } else {
               ThemeMode var11 = ThemeManager.INSTANCE.getCurrentThemeMode();
               if (var11 == ThemeMode.DARK) {
                  var10000 = IntercomColorsKt.intercomDarkColors();
               } else if (var11 == ThemeMode.LIGHT) {
                  var10000 = IntercomColorsKt.intercomLightColors();
               } else {
                  if (var11 != ThemeMode.SYSTEM) {
                     throw new NoWhenBranchMatchedException();
                  }

                  var10000 = DarkThemeKt.isSystemInDarkTheme(var4, 0) ? IntercomColorsKt.intercomDarkColors() : IntercomColorsKt.intercomLightColors();
               }
            }
         }

         IntercomColors var10 = var10000;
         var4.endReplaceGroup();
         IntercomColors var8 = var10;
         ProvidedValue[] var13 = new ProvidedValue[]{
            IntercomColorsKt.getLocalIntercomColors().provides(var8),
            IntercomTypographyKt.getLocalIntercomTypography().provides(var1),
            ContentColorKt.getLocalContentColor().provides(Color.recovered$f46ef195ad4a2363(var8.getText().recovered$e01a1d3b7b68e21a()))
         };
         CompositionLocalKt.CompositionLocalProvider(
            var13,
            ComposableLambdaKt.rememberComposableLambda(1689661311, true, new IntercomThemeKt$IntercomTheme$1(var8, var1, var2, var3), var4, 54),
            var4,
            48 | ProvidedValue.$stable
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var14 = var4.endRestartGroup();
      if (var14 != null) {
         final io.intercom.android.sdk.ui.theme.IntercomColors recoveredCapture_34_0 = var0;
         final io.intercom.android.sdk.ui.theme.IntercomTypography recoveredCapture_34_1 = var1;
         final androidx.compose.material3.Shapes recoveredCapture_34_2 = var2;
         final kotlin.jvm.functions.Function2 recoveredCapture_34_3 = var3;
         final int recoveredCapture_34_4 = var5;
         final int recoveredCapture_34_5 = var6;
         var14.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_34_0, recoveredArgument_34_1) -> IntercomThemeKt.IntercomTheme$lambda$0(recoveredCapture_34_0, recoveredCapture_34_1, recoveredCapture_34_2, recoveredCapture_34_3, recoveredCapture_34_4, recoveredCapture_34_5, (androidx.compose.runtime.Composer)recoveredArgument_34_0, ((java.lang.Integer)recoveredArgument_34_1).intValue())));
      }
   }

   @Composable
   private static final boolean isDarkThemeInEditMode(Composer var0, int var1) {
      var0.startReplaceGroup(-320047698);
      ComposerKt.sourceInformation(var0, "C(isDarkThemeInEditMode)45@1720L21,45@1755L7:IntercomTheme.kt#o58l0d");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-320047698, var1, -1, "io.intercom.android.sdk.ui.theme.isDarkThemeInEditMode (IntercomTheme.kt:45)");
      }

      boolean var10000;
      label19: {
         if (DarkThemeKt.isSystemInDarkTheme(var0, 0)) {
            CompositionLocal var3 = AndroidCompositionLocals_androidKt.getLocalView();
            int var4 = 0;
            int var5 = 0;
            ComposerKt.sourceInformationMarkerStart(var0, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var6 = var0.consume(var3);
            ComposerKt.sourceInformationMarkerEnd(var0);
            if (((View)var6).isInEditMode()) {
               var10000 = true;
               break label19;
            }
         }

         var10000 = false;
      }

      boolean var2 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var0.endReplaceGroup();
      return var2;
   }

   @NotNull
   public static final ProvidableCompositionLocal getLocalShapes() {
      return LocalShapes;
   }

   private static final Unit IntercomTheme$lambda$0(
      IntercomColors var0, IntercomTypography var1, Shapes var2, Function2 var3, int var4, int var5, Composer var6, int var7
   ) {
      IntercomTheme(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Shapes LocalShapes$lambda$1() {
      return new Shapes(null, null, null, null, null, 31, null);
   }
}
