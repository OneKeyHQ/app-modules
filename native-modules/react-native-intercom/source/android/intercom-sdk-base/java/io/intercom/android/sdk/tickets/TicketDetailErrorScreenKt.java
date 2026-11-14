package io.intercom.android.sdk.tickets;

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
         "TicketDetailErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketDetailErrorScreenWithCTAPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TicketDetailErrorScreenWithoutCTAPreview",
         "intercom-sdk-base_release"
   }
)
public final class TicketDetailErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailErrorScreen(@NotNull ErrorState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(-2061608265);
      ComposerKt.sourceInformation(var2, "C(TicketDetailErrorScreen)P(1)13@459L55:TicketDetailErrorScreen.kt#3ofz5f");
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
            ComposerKt.traceEventStart(-2061608265, var5, -1, "io.intercom.android.sdk.tickets.TicketDetailErrorScreen (TicketDetailErrorScreen.kt:13)");
         }

         IntercomErrorScreenKt.IntercomErrorScreen(var0, var1, var2, 14 & var5 | 112 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_1538_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1538_1 = var1;
         final int recoveredCapture_1538_2 = var3;
         final int recoveredCapture_1538_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1538_0, recoveredArgument_1538_1) -> TicketDetailErrorScreenKt.TicketDetailErrorScreen$lambda$0(recoveredCapture_1538_0, recoveredCapture_1538_1, recoveredCapture_1538_2, recoveredCapture_1538_3, (androidx.compose.runtime.Composer)recoveredArgument_1538_0, ((java.lang.Integer)recoveredArgument_1538_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailErrorScreenWithCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1196795215);
      ComposerKt.sourceInformation(var0, "C(TicketDetailErrorScreenWithCTAPreview)18@596L84:TicketDetailErrorScreen.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1196795215, var1, -1, "io.intercom.android.sdk.tickets.TicketDetailErrorScreenWithCTAPreview (TicketDetailErrorScreen.kt:17)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketDetailErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1539_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1539_0, recoveredArgument_1539_1) -> TicketDetailErrorScreenKt.TicketDetailErrorScreenWithCTAPreview$lambda$1(recoveredCapture_1539_0, (androidx.compose.runtime.Composer)recoveredArgument_1539_0, ((java.lang.Integer)recoveredArgument_1539_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailErrorScreenWithoutCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(989031425);
      ComposerKt.sourceInformation(var0, "C(TicketDetailErrorScreenWithoutCTAPreview)26@767L86:TicketDetailErrorScreen.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               989031425, var1, -1, "io.intercom.android.sdk.tickets.TicketDetailErrorScreenWithoutCTAPreview (TicketDetailErrorScreen.kt:25)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketDetailErrorScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1540_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1540_0, recoveredArgument_1540_1) -> TicketDetailErrorScreenKt.TicketDetailErrorScreenWithoutCTAPreview$lambda$2(recoveredCapture_1540_0, (androidx.compose.runtime.Composer)recoveredArgument_1540_0, ((java.lang.Integer)recoveredArgument_1540_1).intValue())));
      }
   }

   private static final Unit TicketDetailErrorScreen$lambda$0(ErrorState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      TicketDetailErrorScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailErrorScreenWithCTAPreview$lambda$1(int var0, Composer var1, int var2) {
      TicketDetailErrorScreenWithCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailErrorScreenWithoutCTAPreview$lambda$2(int var0, Composer var1, int var2) {
      TicketDetailErrorScreenWithoutCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
