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
import io.intercom.android.sdk.m5.components.EmptyStateKt;
import io.intercom.android.sdk.models.EmptyState;
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
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "TicketsEmptyScreen",
         "",
         "emptyState",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/models/EmptyState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "EmptyScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class TicketsEmptyScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsEmptyScreen(@NotNull EmptyState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "emptyState");
      var2 = var2.startRestartGroup(1360358580);
      ComposerKt.sourceInformation(var2, "C(TicketsEmptyScreen)16@530L176:TicketsEmptyScreen.kt#yl74uv");
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
            ComposerKt.traceEventStart(1360358580, var5, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsEmptyScreen (TicketsEmptyScreen.kt:15)");
         }

         String var6 = var0.getTitle();
         String var7 = var0.getText();
         int var8 = drawable.intercom_ticket_detail_icon;
         EmptyStateKt.EmptyState(var6, var1, var7, var8, null, var2, 112 & var5, 16);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.models.EmptyState recoveredCapture_1606_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1606_1 = var1;
         final int recoveredCapture_1606_2 = var3;
         final int recoveredCapture_1606_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1606_0, recoveredArgument_1606_1) -> TicketsEmptyScreenKt.TicketsEmptyScreen$lambda$0(recoveredCapture_1606_0, recoveredCapture_1606_1, recoveredCapture_1606_2, recoveredCapture_1606_3, (androidx.compose.runtime.Composer)recoveredArgument_1606_0, ((java.lang.Integer)recoveredArgument_1606_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyScreenPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1850741992);
      ComposerKt.sourceInformation(var0, "C(EmptyScreenPreview)27@770L259:TicketsEmptyScreen.kt#yl74uv");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1850741992, var1, -1, "io.intercom.android.sdk.tickets.list.ui.EmptyScreenPreview (TicketsEmptyScreen.kt:26)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketsEmptyScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1605_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1605_0, recoveredArgument_1605_1) -> TicketsEmptyScreenKt.EmptyScreenPreview$lambda$1(recoveredCapture_1605_0, (androidx.compose.runtime.Composer)recoveredArgument_1605_0, ((java.lang.Integer)recoveredArgument_1605_1).intValue())));
      }
   }

   private static final Unit TicketsEmptyScreen$lambda$0(EmptyState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      TicketsEmptyScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit EmptyScreenPreview$lambda$1(int var0, Composer var1, int var2) {
      EmptyScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
