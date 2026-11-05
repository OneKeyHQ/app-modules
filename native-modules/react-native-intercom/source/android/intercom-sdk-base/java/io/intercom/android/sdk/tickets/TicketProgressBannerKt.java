package io.intercom.android.sdk.tickets;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a5\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\u000e",
   d2 = {
         "TicketProgressBanner",
         "",
         "name",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "visible",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketProgressRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TicketProgressRowLongTextPreview",
         "intercom-sdk-base_release"
   }
)
public final class TicketProgressBannerKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketProgressBanner(
      @NotNull String var0, @NotNull Function0 var1, boolean var2, @Nullable Modifier var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "name");
      Intrinsics.checkNotNullParameter(var1, "onClick");
      var4 = var4.startRestartGroup(-108721483);
      ComposerKt.sourceInformation(var4, "C(TicketProgressBanner)P(1,2,3)54@2056L1576,42@1699L1933:TicketProgressBanner.kt#3ofz5f");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-108721483, var7, -1, "io.intercom.android.sdk.tickets.TicketProgressBanner (TicketProgressBanner.kt:40)");
         }

         int var8 = 1000;
         EnterTransition var9 = EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(var8, var8, null, 4, null), 0.0F, 2, null);
         ExitTransition var10 = EnterExitTransitionKt.fadeOut$default(AnimationSpecKt.tween$default(var8, 0, null, 6, null), 0.0F, 2, null);
         AnimatedVisibilityKt.AnimatedVisibility(
            var2,
            var3,
            var9,
            var10,
            null,
            ComposableLambdaKt.rememberComposableLambda(417589981, true, new TicketProgressBannerKt$TicketProgressBanner$1(var1, var0), var4, 54),
            var4,
            200064 | 14 & var7 >> 6 | 112 & var7 >> 6,
            16
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final java.lang.String recoveredCapture_1544_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1544_1 = var1;
         final boolean recoveredCapture_1544_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_1544_3 = var3;
         final int recoveredCapture_1544_4 = var5;
         final int recoveredCapture_1544_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1544_0, recoveredArgument_1544_1) -> TicketProgressBannerKt.TicketProgressBanner$lambda$0(recoveredCapture_1544_0, recoveredCapture_1544_1, recoveredCapture_1544_2, recoveredCapture_1544_3, recoveredCapture_1544_4, recoveredCapture_1544_5, (androidx.compose.runtime.Composer)recoveredArgument_1544_0, ((java.lang.Integer)recoveredArgument_1544_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketProgressRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-433396877);
      ComposerKt.sourceInformation(var0, "C(TicketProgressRowPreview)100@3712L100:TicketProgressBanner.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-433396877, var1, -1, "io.intercom.android.sdk.tickets.TicketProgressRowPreview (TicketProgressBanner.kt:99)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketProgressBannerKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1546_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1546_0, recoveredArgument_1546_1) -> TicketProgressBannerKt.TicketProgressRowPreview$lambda$1(recoveredCapture_1546_0, (androidx.compose.runtime.Composer)recoveredArgument_1546_0, ((java.lang.Integer)recoveredArgument_1546_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketProgressRowLongTextPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-384429412);
      ComposerKt.sourceInformation(var0, "C(TicketProgressRowLongTextPreview)108@3891L137:TicketProgressBanner.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-384429412, var1, -1, "io.intercom.android.sdk.tickets.TicketProgressRowLongTextPreview (TicketProgressBanner.kt:107)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketProgressBannerKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1545_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1545_0, recoveredArgument_1545_1) -> TicketProgressBannerKt.TicketProgressRowLongTextPreview$lambda$2(recoveredCapture_1545_0, (androidx.compose.runtime.Composer)recoveredArgument_1545_0, ((java.lang.Integer)recoveredArgument_1545_1).intValue())));
      }
   }

   private static final Unit TicketProgressBanner$lambda$0(
      String var0, Function0 var1, boolean var2, Modifier var3, int var4, int var5, Composer var6, int var7
   ) {
      TicketProgressBanner(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TicketProgressRowPreview$lambda$1(int var0, Composer var1, int var2) {
      TicketProgressRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketProgressRowLongTextPreview$lambda$2(int var0, Composer var1, int var2) {
      TicketProgressRowLongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
