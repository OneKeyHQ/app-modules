package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
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
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a=\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b¨\u0006\f",
   d2 = {
         "HelpCenterTopBar",
         "",
         "onBackClick",
         "Lkotlin/Function0;",
         "onSearchClick",
         "navIcon",
         "",
         "title",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;ILio/intercom/android/sdk/ui/common/StringProvider;Landroidx/compose/runtime/Composer;II)V",
         "HelpCenterTopBarPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class HelpCenterTopBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterTopBar(
      @NotNull Function0 var0, @NotNull Function0 var1, int var2, @Nullable StringProvider var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "onBackClick");
      Intrinsics.checkNotNullParameter(var1, "onSearchClick");
      var4 = var4.startRestartGroup(644687815);
      ComposerKt.sourceInformation(var4, "C(HelpCenterTopBar)P(1,2)21@856L6,22@907L6,24@963L9,29@1122L315,23@927L510:HelpCenterTopBar.kt#ngglre");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var4.changed(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var7 |= (var6 & 8) == 0 && ((var5 & 4096) == 0 ? var4.changed(var3) : var4.changedInstance(var3)) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         var4.startDefaults();
         if ((var5 & 1) != 0 && !var4.getDefaultsInvalid()) {
            var4.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               var7 &= -7169;
            }
         } else {
            if ((var6 & 4) != 0) {
               var2 = drawable.intercom_ic_back;
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               var3 = new StringRes(string.intercom_get_help, null, 2, null);
               var7 &= -7169;
            }
         }

         var4.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(644687815, var7, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBar (HelpCenterTopBar.kt:20)");
         }

         long var8 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4();
         long var10 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            var3.getText(var4, StringProvider.$stable | 14 & var7 >> 9),
            null,
            null,
            null,
            var0,
            var2,
            false,
            var8,
            var10,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(736282477, true, new HelpCenterTopBarKt$HelpCenterTopBar$1(var1, var10), var4, 54),
            var4,
            458752 & var7 << 15 | 3670016 & var7 << 12,
            384,
            3229
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_877_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_877_1 = var1;
         final int recoveredCapture_877_2 = var2;
         final io.intercom.android.sdk.ui.common.StringProvider recoveredCapture_877_3 = var3;
         final int recoveredCapture_877_4 = var5;
         final int recoveredCapture_877_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_877_0, recoveredArgument_877_1) -> HelpCenterTopBarKt.HelpCenterTopBar$lambda$0(recoveredCapture_877_0, recoveredCapture_877_1, recoveredCapture_877_2, recoveredCapture_877_3, recoveredCapture_877_4, recoveredCapture_877_5, (androidx.compose.runtime.Composer)recoveredArgument_877_0, ((java.lang.Integer)recoveredArgument_877_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterTopBarPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-536592516);
      ComposerKt.sourceInformation(var0, "C(HelpCenterTopBarPreview)43@1516L119:HelpCenterTopBar.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -536592516, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBarPreview (HelpCenterTopBar.kt:42)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterTopBarKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_878_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_878_0, recoveredArgument_878_1) -> HelpCenterTopBarKt.HelpCenterTopBarPreview$lambda$1(recoveredCapture_878_0, (androidx.compose.runtime.Composer)recoveredArgument_878_0, ((java.lang.Integer)recoveredArgument_878_1).intValue())));
      }
   }

   private static final Unit HelpCenterTopBar$lambda$0(
      Function0 var0, Function0 var1, int var2, StringProvider var3, int var4, int var5, Composer var6, int var7
   ) {
      HelpCenterTopBar(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterTopBarPreview$lambda$1(int var0, Composer var1, int var2) {
      HelpCenterTopBarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
