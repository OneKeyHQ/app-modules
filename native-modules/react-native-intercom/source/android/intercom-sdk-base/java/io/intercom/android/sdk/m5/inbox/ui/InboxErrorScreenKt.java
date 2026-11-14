package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
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
         "InboxErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/components/ErrorState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "InboxErrorScreenWithCTAPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "InboxErrorScreenWithoutCTAPreview",
         "intercom-sdk-base_release"
   }
)
public final class InboxErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxErrorScreen(@NotNull ErrorState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(-659234710);
      ComposerKt.sourceInformation(var2, "C(InboxErrorScreen)P(1)13@456L55:InboxErrorScreen.kt#po70xe");
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
            ComposerKt.traceEventStart(-659234710, var5, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxErrorScreen (InboxErrorScreen.kt:13)");
         }

         IntercomErrorScreenKt.IntercomErrorScreen(var0, var1, var2, 14 & var5 | 112 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.ErrorState recoveredCapture_978_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_978_1 = var1;
         final int recoveredCapture_978_2 = var3;
         final int recoveredCapture_978_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_978_0, recoveredArgument_978_1) -> InboxErrorScreenKt.InboxErrorScreen$lambda$0(recoveredCapture_978_0, recoveredCapture_978_1, recoveredCapture_978_2, recoveredCapture_978_3, (androidx.compose.runtime.Composer)recoveredArgument_978_0, ((java.lang.Integer)recoveredArgument_978_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxErrorScreenWithCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1274028182);
      ComposerKt.sourceInformation(var0, "C(InboxErrorScreenWithCTAPreview)18@595L77:InboxErrorScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1274028182, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxErrorScreenWithCTAPreview (InboxErrorScreen.kt:17)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_979_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_979_0, recoveredArgument_979_1) -> InboxErrorScreenKt.InboxErrorScreenWithCTAPreview$lambda$1(recoveredCapture_979_0, (androidx.compose.runtime.Composer)recoveredArgument_979_0, ((java.lang.Integer)recoveredArgument_979_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxErrorScreenWithoutCTAPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1186679776);
      ComposerKt.sourceInformation(var0, "C(InboxErrorScreenWithoutCTAPreview)26@761L79:InboxErrorScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1186679776, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxErrorScreenWithoutCTAPreview (InboxErrorScreen.kt:25)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxErrorScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_980_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_980_0, recoveredArgument_980_1) -> InboxErrorScreenKt.InboxErrorScreenWithoutCTAPreview$lambda$2(recoveredCapture_980_0, (androidx.compose.runtime.Composer)recoveredArgument_980_0, ((java.lang.Integer)recoveredArgument_980_1).intValue())));
      }
   }

   private static final Unit InboxErrorScreen$lambda$0(ErrorState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      InboxErrorScreen(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit InboxErrorScreenWithCTAPreview$lambda$1(int var0, Composer var1, int var2) {
      InboxErrorScreenWithCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InboxErrorScreenWithoutCTAPreview$lambda$2(int var0, Composer var1, int var2) {
      InboxErrorScreenWithoutCTAPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
