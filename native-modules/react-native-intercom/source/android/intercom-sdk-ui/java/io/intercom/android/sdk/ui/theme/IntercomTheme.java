package io.intercom.android.sdk.ui.theme;

import androidx.compose.material3.Shapes;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u00058G¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t8G¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r8G¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/IntercomTheme;",
         "",
         "<init>",
         "()V",
         "colors",
         "Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "getColors",
         "(Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "typography",
         "Lio/intercom/android/sdk/ui/theme/IntercomTypography;",
         "getTypography",
         "(Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/ui/theme/IntercomTypography;",
         "shapes",
         "Landroidx/compose/material3/Shapes;",
         "getShapes",
         "(Landroidx/compose/runtime/Composer;I)Landroidx/compose/material3/Shapes;",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nIntercomTheme.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTheme.kt\nio/intercom/android/sdk/ui/theme/IntercomTheme\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,61:1\n75#2:62\n75#2:63\n75#2:64\n*S KotlinDebug\n*F\n+ 1 IntercomTheme.kt\nio/intercom/android/sdk/ui/theme/IntercomTheme\n*L\n51#1:62\n54#1:63\n57#1:64\n*E\n"
)
public final class IntercomTheme {
   @NotNull
   public static final IntercomTheme INSTANCE = new IntercomTheme();
   public static int $stable;

   static {}

   private IntercomTheme() {
   }

   @Composable
   @JvmName(name = "getColors")
   @NotNull
   public final IntercomColors getColors(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(159743073);
      ComposerKt.sourceInformation(var1, "C50@1901L7:IntercomTheme.kt#o58l0d");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(159743073, var2, -1, "io.intercom.android.sdk.ui.theme.IntercomTheme.<get-colors> (IntercomTheme.kt:50)");
      }

      CompositionLocal var4 = IntercomColorsKt.getLocalIntercomColors();
      int var5 = 6;
      int var6 = 0;
      ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var7 = var1.consume(var4);
      ComposerKt.sourceInformationMarkerEnd(var1);
      IntercomColors var3 = (IntercomColors)var7;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   @Composable
   @JvmName(name = "getTypography")
   @NotNull
   public final IntercomTypography getTypography(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(-989585502);
      ComposerKt.sourceInformation(var1, "C53@2015L7:IntercomTheme.kt#o58l0d");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-989585502, var2, -1, "io.intercom.android.sdk.ui.theme.IntercomTheme.<get-typography> (IntercomTheme.kt:53)");
      }

      CompositionLocal var4 = IntercomTypographyKt.getLocalIntercomTypography();
      int var5 = 6;
      int var6 = 0;
      ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var7 = var1.consume(var4);
      ComposerKt.sourceInformationMarkerEnd(var1);
      IntercomTypography var3 = (IntercomTypography)var7;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   @Composable
   @JvmName(name = "getShapes")
   @NotNull
   public final Shapes getShapes(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(-474718694);
      ComposerKt.sourceInformation(var1, "C56@2101L7:IntercomTheme.kt#o58l0d");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-474718694, var2, -1, "io.intercom.android.sdk.ui.theme.IntercomTheme.<get-shapes> (IntercomTheme.kt:56)");
      }

      CompositionLocal var4 = IntercomThemeKt.getLocalShapes();
      int var5 = 6;
      int var6 = 0;
      ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var7 = var1.consume(var4);
      ComposerKt.sourceInformationMarkerEnd(var1);
      Shapes var3 = (Shapes)var7;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }
}
