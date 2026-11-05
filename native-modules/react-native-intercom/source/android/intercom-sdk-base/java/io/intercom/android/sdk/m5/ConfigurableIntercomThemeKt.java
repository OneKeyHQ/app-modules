package io.intercom.android.sdk.m5;

import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.CustomizationColorsModel;
import io.intercom.android.sdk.models.CustomizationModel;
import io.intercom.android.sdk.ui.theme.IntercomColors;
import io.intercom.android.sdk.ui.theme.IntercomColorsKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.ui.theme.ThemeMode;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.ThemeUtils;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a(\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0011\u0010\n\u001a\r\u0012\u0004\u0012\u00020\u00070\u000b¢\u0006\u0002\b\fH\u0001¢\u0006\u0002\u0010\r\"\u001e\u0010\u0000\u001a\u00020\u00018\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0004\b\u0004\u0010\u0005¨\u0006\u000e",
   d2 = {
         "FinTableBlocksEnabled",
         "",
         "getFinTableBlocksEnabled",
         "()Z",
         "setFinTableBlocksEnabled",
         "(Z)V",
         "ConfigurableIntercomTheme",
         "",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "content",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Lio/intercom/android/sdk/identity/AppConfig;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class ConfigurableIntercomThemeKt {
   static {}
   @RestrictTo(Scope.LIBRARY_GROUP)
   private static boolean FinTableBlocksEnabled;

   public static final boolean getFinTableBlocksEnabled() {
      return FinTableBlocksEnabled;
   }

   public static final void setFinTableBlocksEnabled(boolean var0) {
      FinTableBlocksEnabled = var0;
   }

   @Composable
   @ComposableInferredTarget(scheme = "[0[0]]")
   public static final void ConfigurableIntercomTheme(@NotNull AppConfig var0, @NotNull Function2 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "appConfig");
      Intrinsics.checkNotNullParameter(var1, "content");
      var2 = var2.startRestartGroup(180143044);
      ComposerKt.sourceInformation(var2, "C(ConfigurableIntercomTheme)23@854L2597:ConfigurableIntercomTheme.kt#uy8qak");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(180143044, var4, -1, "io.intercom.android.sdk.m5.ConfigurableIntercomTheme (ConfigurableIntercomTheme.kt:23)");
         }

         ConfigModules var10000 = var0.getConfigModules();
         CustomizationModel var6 = var10000 != null ? var10000.getCustomization() : null;
         var2.startReplaceGroup(-1126601820);
         ComposerKt.sourceInformation(var2, "*33@1235L119,38@1420L119,67@2693L119,72@2878L119");
         IntercomColors var34;
         if (var6 == null) {
            var34 = null;
         } else {
            CustomizationModel var8 = var6;
            int var9 = 0;
            ThemeMode var10 = ThemeManager.INSTANCE.getCurrentThemeMode();
            var2.startReplaceGroup(-1126603219);
            ComposerKt.sourceInformation(var2, "29@1103L21");
            boolean var35;
            if (var10 == ThemeMode.DARK) {
               var35 = false;
            } else if (var10 == ThemeMode.LIGHT) {
               var35 = true;
            } else {
               if (var10 != ThemeMode.SYSTEM) {
                  throw new NoWhenBranchMatchedException();
               }

               var35 = !DarkThemeKt.isSystemInDarkTheme(var2, 0);
            }

            boolean var11 = var35;
            var2.endReplaceGroup();
            boolean var12 = var11;
            long var13 = ColorExtensionsKt.toComposeColor$default(
               ThemeUtils.INSTANCE
                  .selectColorForThemeCompose$intercom_sdk_base_release(
                     var8.getAction().getBackgroundColor(), var8.getAction().getBackgroundColorDark(), var2, 384
                  ),
               0.0F,
               1,
               null
            );
            long var15 = ColorExtensionsKt.toComposeColor$default(
               ThemeUtils.INSTANCE
                  .selectColorForThemeCompose$intercom_sdk_base_release(
                     var8.getAction().getForegroundColor(), var8.getAction().getForegroundColorDark(), var2, 384
                  ),
               0.0F,
               1,
               null
            );
            CustomizationColorsModel var17 = var12 ? var8.getActionContrastWhite() : var8.getActionContrastDark();
            var2.startReplaceGroup(-1126565128);
            ComposerKt.sourceInformation(var2, "*54@2183L125");
            Color var36;
            if (var17 == null) {
               var36 = null;
            } else {
               CustomizationColorsModel var18 = var17;
               int var19 = 0;
               var36 = Color.recovered$f46ef195ad4a2363(
                  ColorExtensionsKt.toComposeColor$default(
                     ThemeUtils.INSTANCE
                        .selectColorForThemeCompose$intercom_sdk_base_release(var18.getBackgroundColor(), var18.getBackgroundColorDark(), var2, 384),
                     0.0F,
                     1,
                     null
                  )
               );
            }

            Color var20 = var36;
            var2.endReplaceGroup();
            long var21 = var20 != null ? var20.recovered$45f9de7b85e0e0b5() : var13;
            var2.startReplaceGroup(-1126556040);
            ComposerKt.sourceInformation(var2, "*61@2467L125");
            Color var37;
            if (var17 == null) {
               var37 = null;
            } else {
               CustomizationColorsModel var23 = var17;
               int var24 = 0;
               var37 = Color.recovered$f46ef195ad4a2363(
                  ColorExtensionsKt.toComposeColor$default(
                     ThemeUtils.INSTANCE
                        .selectColorForThemeCompose$intercom_sdk_base_release(var23.getForegroundColor(), var23.getForegroundColorDark(), var2, 384),
                     0.0F,
                     1,
                     null
                  )
               );
            }

            Color var25 = var37;
            var2.endReplaceGroup();
            long var26 = var25 != null ? var25.recovered$45f9de7b85e0e0b5() : var15;
            long var28 = ColorExtensionsKt.toComposeColor$default(
               ThemeUtils.INSTANCE
                  .selectColorForThemeCompose$intercom_sdk_base_release(
                     var8.getHeader().getBackgroundColor(), var8.getHeader().getBackgroundColorDark(), var2, 384
                  ),
               0.0F,
               1,
               null
            );
            long var30 = ColorExtensionsKt.toComposeColor$default(
               ThemeUtils.INSTANCE
                  .selectColorForThemeCompose$intercom_sdk_base_release(
                     var8.getHeader().getForegroundColor(), var8.getHeader().getForegroundColorDark(), var2, 384
                  ),
               0.0F,
               1,
               null
            );
            var34 = IntercomColorsKt.recovered$cb0ce17da3a10a76(var13, var15, var21, var26, var28, var30, var12);
         }

         IntercomColors var7 = var34;
         var2.endReplaceGroup();
         IntercomColors var5 = var7;
         var2.startReplaceGroup(-1126603036);
         ComposerKt.sourceInformation(var2, "86@3419L6");
         IntercomColors var33 = var5 == null ? IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable) : var5;
         var2.endReplaceGroup();
         IntercomThemeKt.IntercomTheme(var33, null, null, var1, var2, 7168 & var4 << 6, 6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var38 = var2.endRestartGroup();
      if (var38 != null) {
         final io.intercom.android.sdk.identity.AppConfig recoveredCapture_139_0 = var0;
         final kotlin.jvm.functions.Function2 recoveredCapture_139_1 = var1;
         final int recoveredCapture_139_2 = var3;
         var38.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_139_0, recoveredArgument_139_1) -> ConfigurableIntercomThemeKt.ConfigurableIntercomTheme$lambda$3(recoveredCapture_139_0, recoveredCapture_139_1, recoveredCapture_139_2, (androidx.compose.runtime.Composer)recoveredArgument_139_0, ((java.lang.Integer)recoveredArgument_139_1).intValue())));
      }
   }

   private static final Unit ConfigurableIntercomTheme$lambda$3(AppConfig var0, Function2 var1, int var2, Composer var3, int var4) {
      ConfigurableIntercomTheme(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }
}
