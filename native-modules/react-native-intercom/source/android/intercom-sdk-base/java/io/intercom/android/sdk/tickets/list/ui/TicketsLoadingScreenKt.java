package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.LoadingScreenKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a!\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "TicketsLoadingScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "animateShimmer",
         "",
         "(Landroidx/compose/ui/Modifier;ZLandroidx/compose/runtime/Composer;II)V",
         "TicketsLoadingScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class TicketsLoadingScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsLoadingScreen(@Nullable Modifier var0, boolean var1, @Nullable Composer var2, int var3, int var4) {
      var2 = var2.startRestartGroup(-2019002374);
      ComposerKt.sourceInformation(var2, "C(TicketsLoadingScreen)P(1)14@490L141:TicketsLoadingScreen.kt#yl74uv");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var4 & 2) != 0) {
            var1 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2019002374, var5, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsLoadingScreen (TicketsLoadingScreen.kt:14)");
         }

         LoadingScreenKt.LoadingScreen(var0, drawable.intercom_inbox_loading_state, var1, var2, 14 & var5 | 896 & var5 << 3, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1610_0 = var0;
         final boolean recoveredCapture_1610_1 = var1;
         final int recoveredCapture_1610_2 = var3;
         final int recoveredCapture_1610_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1610_0, recoveredArgument_1610_1) -> TicketsLoadingScreenKt.TicketsLoadingScreen$lambda$0(recoveredCapture_1610_0, recoveredCapture_1610_1, recoveredCapture_1610_2, recoveredCapture_1610_3, (androidx.compose.runtime.Composer)recoveredArgument_1610_0, ((java.lang.Integer)recoveredArgument_1610_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsLoadingScreenPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-880557955);
      ComposerKt.sourceInformation(var0, "C(TicketsLoadingScreenPreview)23@703L106:TicketsLoadingScreen.kt#yl74uv");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-880557955, var1, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsLoadingScreenPreview (TicketsLoadingScreen.kt:22)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketsLoadingScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1611_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1611_0, recoveredArgument_1611_1) -> TicketsLoadingScreenKt.TicketsLoadingScreenPreview$lambda$1(recoveredCapture_1611_0, (androidx.compose.runtime.Composer)recoveredArgument_1611_0, ((java.lang.Integer)recoveredArgument_1611_1).intValue())));
      }
   }

   private static final Unit TicketsLoadingScreen$lambda$0(Modifier var0, boolean var1, int var2, int var3, Composer var4, int var5) {
      TicketsLoadingScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TicketsLoadingScreenPreview$lambda$1(int var0, Composer var1, int var2) {
      TicketsLoadingScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
