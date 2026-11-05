package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.LoadingScreenKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import kotlin.Metadata;
import kotlin.Unit;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "InboxLoadingScreen",
         "",
         "animateShimmer",
         "",
         "(ZLandroidx/compose/runtime/Composer;II)V",
         "HomeLoadingContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class InboxLoadingScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxLoadingScreen(boolean var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(1258200696);
      ComposerKt.sourceInformation(var1, "C(InboxLoadingScreen)9@314L105:InboxLoadingScreen.kt#po70xe");
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
            var0 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1258200696, var4, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxLoadingScreen (InboxLoadingScreen.kt:9)");
         }

         LoadingScreenKt.LoadingScreen(null, drawable.intercom_inbox_loading_state, var0, var1, 896 & var4 << 6, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final boolean recoveredCapture_982_0 = var0;
         final int recoveredCapture_982_1 = var2;
         final int recoveredCapture_982_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_982_0, recoveredArgument_982_1) -> InboxLoadingScreenKt.InboxLoadingScreen$lambda$0(recoveredCapture_982_0, recoveredCapture_982_1, recoveredCapture_982_2, (androidx.compose.runtime.Composer)recoveredArgument_982_0, ((java.lang.Integer)recoveredArgument_982_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeLoadingContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1591477138);
      ComposerKt.sourceInformation(var0, "C(HomeLoadingContentPreview)14@498L42:InboxLoadingScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1591477138, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.HomeLoadingContentPreview (InboxLoadingScreen.kt:13)");
         }

         InboxLoadingScreen(false, var0, 6, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_981_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_981_0, recoveredArgument_981_1) -> InboxLoadingScreenKt.HomeLoadingContentPreview$lambda$1(recoveredCapture_981_0, (androidx.compose.runtime.Composer)recoveredArgument_981_0, ((java.lang.Integer)recoveredArgument_981_1).intValue())));
      }
   }

   private static final Unit InboxLoadingScreen$lambda$0(boolean var0, int var1, int var2, Composer var3, int var4) {
      InboxLoadingScreen(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit HomeLoadingContentPreview$lambda$1(int var0, Composer var1, int var2) {
      HomeLoadingContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
