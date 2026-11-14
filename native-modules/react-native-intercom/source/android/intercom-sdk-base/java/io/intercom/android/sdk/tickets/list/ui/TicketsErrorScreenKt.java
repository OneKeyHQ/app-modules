package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\n",
   d2 = {
         "TicketsErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketsErrorScreenWithCTAPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TicketsErrorScreenWithoutCTAPreview",
         "intercom-sdk-base_release"
   }
)
public final class TicketsErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsErrorScreen(@NotNull ErrorState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(1763181519);
      ComposerKt.sourceInformation(var2, "C(TicketsErrorScreen)P(1)13@462L55:TicketsErrorScreen.kt#yl74uv");
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
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1763181519, var5, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsErrorScreen (TicketsErrorScreen.kt:13)");
         }

         IntercomErrorScreenKt.IntercomErrorScreen(var0, var1, var2, 14 & var5 | 112 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_1607_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1607_1 = var1;
         final int recoveredCapture_1607_2 = var3;
         final int recoveredCapture_1607_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1607_0, recoveredArgument_1607_1) -> TicketsErrorScreenKt.TicketsErrorScreen$lambda$0(recoveredCapture_1607_0, recoveredCapture_1607_1, recoveredCapture_1607_2, recoveredCapture_1607_3, (androidx.compose.runtime.Composer)recoveredArgument_1607_0, ((java.lang.Integer)recoveredArgument_1607_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsErrorScreenWithCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1833782159);
      ComposerKt.sourceInformation(var0, "C(TicketsErrorScreenWithCTAPreview)18@594L79:TicketsErrorScreen.kt#yl74uv");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1833782159, var1, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsErrorScreenWithCTAPreview (TicketsErrorScreen.kt:17)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketsErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1608_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1608_0, recoveredArgument_1608_1) -> TicketsErrorScreenKt.TicketsErrorScreenWithCTAPreview$lambda$1(recoveredCapture_1608_0, (androidx.compose.runtime.Composer)recoveredArgument_1608_0, ((java.lang.Integer)recoveredArgument_1608_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsErrorScreenWithoutCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1606879941);
      ComposerKt.sourceInformation(var0, "C(TicketsErrorScreenWithoutCTAPreview)26@755L81:TicketsErrorScreen.kt#yl74uv");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1606879941, var1, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsErrorScreenWithoutCTAPreview (TicketsErrorScreen.kt:25)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketsErrorScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1609_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1609_0, recoveredArgument_1609_1) -> TicketsErrorScreenKt.TicketsErrorScreenWithoutCTAPreview$lambda$2(recoveredCapture_1609_0, (androidx.compose.runtime.Composer)recoveredArgument_1609_0, ((java.lang.Integer)recoveredArgument_1609_1).intValue())));
      }
   }

   private static final Unit TicketsErrorScreen$lambda$0(ErrorState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      TicketsErrorScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TicketsErrorScreenWithCTAPreview$lambda$1(int var0, Composer var1, int var2) {
      TicketsErrorScreenWithCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketsErrorScreenWithoutCTAPreview$lambda$2(int var0, Composer var1, int var2) {
      TicketsErrorScreenWithoutCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
