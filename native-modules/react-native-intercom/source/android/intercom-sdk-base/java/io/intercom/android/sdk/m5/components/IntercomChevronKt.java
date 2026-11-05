package io.intercom.android.sdk.m5.components;

import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.RotateKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import androidx.compose.ui.unit.LayoutDirection;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004¨\u0006\u0005",
   d2 = {
         "IntercomChevron",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomChevron.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomChevron.kt\nio/intercom/android/sdk/m5/components/IntercomChevronKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,33:1\n75#2:34\n*S KotlinDebug\n*F\n+ 1 IntercomChevron.kt\nio/intercom/android/sdk/m5/components/IntercomChevronKt\n*L\n20#1:34\n*E\n"
)
public final class IntercomChevronKt {
   @Composable
   @Container({@Preview(name = "RTL", locale = "ar"), @Preview(name = "LTR", locale = "en")})
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomChevron(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(467059601);
      ComposerKt.sourceInformation(var1, "C(IntercomChevron)19@696L7,27@860L49,28@940L6,24@777L231:IntercomChevron.kt#arqjag");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(467059601, var4, -1, "io.intercom.android.sdk.m5.components.IntercomChevron (IntercomChevron.kt:18)");
         }

         CompositionLocal var6 = CompositionLocalsKt.getLocalLayoutDirection();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var1.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var1);
         float var5 = var9 == LayoutDirection.Rtl ? 180.0F : 0.0F;
         Modifier var11 = RotateKt.rotate(var0, var5);
         Painter var12 = PainterResources_androidKt.painterResource(drawable.intercom_chevron, var1, 0);
         long var13 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
         IconKt.recovered$e00adffb7fe45b02(var12, null, var11, var13, var1, 48 | Painter.$stable, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_218_0 = var0;
         final int recoveredCapture_218_1 = var2;
         final int recoveredCapture_218_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_218_0, recoveredArgument_218_1) -> IntercomChevronKt.IntercomChevron$lambda$0(recoveredCapture_218_0, recoveredCapture_218_1, recoveredCapture_218_2, (androidx.compose.runtime.Composer)recoveredArgument_218_0, ((java.lang.Integer)recoveredArgument_218_1).intValue())));
      }
   }

   private static final Unit IntercomChevron$lambda$0(Modifier var0, int var1, int var2, Composer var3, int var4) {
      IntercomChevron(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }
}
