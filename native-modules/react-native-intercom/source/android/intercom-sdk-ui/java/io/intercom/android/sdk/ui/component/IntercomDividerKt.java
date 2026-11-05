package io.intercom.android.sdk.ui.component;

import androidx.compose.material3.DividerKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\u0004¨\u0006\u0005",
   d2 = {
         "IntercomDivider",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomDivider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomDivider.kt\nio/intercom/android/sdk/ui/component/IntercomDividerKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,17:1\n118#2:18\n*S KotlinDebug\n*F\n+ 1 IntercomDivider.kt\nio/intercom/android/sdk/ui/component/IntercomDividerKt\n*L\n14#1:18\n*E\n"
)
public final class IntercomDividerKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomDivider(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(2071363704);
      ComposerKt.sourceInformation(var1, "C(IntercomDivider)11@392L6,10@343L125:IntercomDivider.kt#yr602f");
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
            ComposerKt.traceEventStart(2071363704, var4, -1, "io.intercom.android.sdk.ui.component.IntercomDivider (IntercomDivider.kt:9)");
         }

         long var5 = IntercomTheme.INSTANCE.getColors(var1, 6).recovered$9e30ba45ae499588();
         int var8 = 1;
         int var9 = 0;
         float var7 = Dp.recovered$95b3d103663bf060(var8);
         DividerKt.recovered$352cda44568e0a7f(var0, var7, var5, var1, 48 | 14 & var4, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_16_0 = var0;
         final int recoveredCapture_16_1 = var2;
         final int recoveredCapture_16_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_16_0, recoveredArgument_16_1) -> IntercomDividerKt.IntercomDivider$lambda$0(recoveredCapture_16_0, recoveredCapture_16_1, recoveredCapture_16_2, (androidx.compose.runtime.Composer)recoveredArgument_16_0, ((java.lang.Integer)recoveredArgument_16_1).intValue())));
      }
   }

   private static final Unit IntercomDivider$lambda$0(Modifier var0, int var1, int var2, Composer var3, int var4) {
      IntercomDivider(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }
}
