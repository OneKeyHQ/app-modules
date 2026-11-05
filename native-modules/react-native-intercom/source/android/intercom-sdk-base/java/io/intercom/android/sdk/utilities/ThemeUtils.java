package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.net.Uri;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.ui.theme.ThemeMode;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u001a\n\u0002\u0018\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\b\bJ\u000f\u0010\t\u001a\u00020\u0005H\u0001¢\u0006\u0004\b\n\u0010\u000bJ&\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\rH\u0007J%\u0010\u0010\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\rH\u0001¢\u0006\u0004\b\u0011\u0010\u0012J2\u0010\u0013\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\r2\b\u0010\u0015\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\rH\u0007J+\u0010\u0017\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0018\u001a\u0004\u0018\u00010\r2\b\u0010\u0019\u001a\u0004\u0018\u00010\rH\u0001¢\u0006\u0002\b\u001aJ+\u0010\u001b\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u001c\u001a\u0004\u0018\u00010\r2\b\u0010\u001d\u001a\u0004\u0018\u00010\rH\u0001¢\u0006\u0002\b\u001eJ\"\u0010\u001f\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\r2\b\u0010!\u001a\u0004\u0018\u00010\rH\u0007J!\u0010\"\u001a\u00020\r2\u0006\u0010 \u001a\u00020\r2\b\u0010!\u001a\u0004\u0018\u00010\rH\u0001¢\u0006\u0004\b#\u0010\u0012J\u0018\u0010$\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010%\u001a\u00020\rH\u0007¨\u0006&²\u0006\n\u0010'\u001a\u00020(X\u008a\u0084\u0002",
   d2 = {
         "Lio/intercom/android/sdk/utilities/ThemeUtils;",
         "",
         "<init>",
         "()V",
         "isDarkMode",
         "",
         "context",
         "Landroid/content/Context;",
         "isDarkMode$intercom_sdk_base_release",
         "isDarkModeCompose",
         "isDarkModeCompose$intercom_sdk_base_release",
         "(Landroidx/compose/runtime/Composer;I)Z",
         "selectUrlForTheme",
         "",
         "lightUrl",
         "darkUrl",
         "selectUrlForThemeCompose",
         "selectUrlForThemeCompose$intercom_sdk_base_release",
         "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "selectLauncherLogoUrl",
         "launcherLogoUrl",
         "launcherLogoDarkUrl",
         "legacyLauncherLogoUrl",
         "selectHeaderLogoUrl",
         "logoUrl",
         "logoDarkUrl",
         "selectHeaderLogoUrl$intercom_sdk_base_release",
         "selectHeaderBackgroundImageUrl",
         "imageUrl",
         "imageDarkUrl",
         "selectHeaderBackgroundImageUrl$intercom_sdk_base_release",
         "selectColorForTheme",
         "lightColor",
         "darkColor",
         "selectColorForThemeCompose",
         "selectColorForThemeCompose$intercom_sdk_base_release",
         "appendInterfaceStyleToUrl",
         "url",
         "intercom-sdk-base_release",
         "currentThemeMode",
         "Lio/intercom/android/sdk/ui/theme/ThemeMode;"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nThemeUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThemeUtils.kt\nio/intercom/android/sdk/utilities/ThemeUtils\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n+ 3 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,214:1\n29#2:215\n85#3:216\n*S KotlinDebug\n*F\n+ 1 ThemeUtils.kt\nio/intercom/android/sdk/utilities/ThemeUtils\n*L\n206#1:215\n44#1:216\n*E\n"
)
public final class ThemeUtils {
   @NotNull
   public static final ThemeUtils INSTANCE = new ThemeUtils();
   public static int $stable;

   static {}

   private ThemeUtils() {
   }

   @JvmStatic
   public static final boolean isDarkMode$intercom_sdk_base_release(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      boolean var10000;
      switch (ThemeManager.INSTANCE.getCurrentThemeMode()) {
         case DARK:
            var10000 = true;
            break;
         case LIGHT:
            var10000 = false;
            break;
         case SYSTEM:
            int var1 = var0.getResources().getConfiguration().uiMode;
            var10000 = (var1 & 48) == 32;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @Composable
   public final boolean isDarkModeCompose$intercom_sdk_base_release(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(121228161);
      ComposerKt.sourceInformation(var1, "C(isDarkModeCompose)47@1816L21:ThemeUtils.kt#4knv12");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(121228161, var2, -1, "io.intercom.android.sdk.utilities.ThemeUtils.isDarkModeCompose (ThemeUtils.kt:42)");
      }

      MutableState var3 = ThemeManager.INSTANCE.getThemeModeState();
      ThemeMode var5 = isDarkModeCompose$lambda$0(var3);
      boolean var10000;
      if (var5 == ThemeMode.DARK) {
         var10000 = true;
      } else if (var5 == ThemeMode.LIGHT) {
         var10000 = false;
      } else {
         if (var5 != ThemeMode.SYSTEM) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = DarkThemeKt.isSystemInDarkTheme(var1, 0);
      }

      boolean var4 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var4;
   }

   @JvmStatic
   @Nullable
   public static final String selectUrlForTheme(@NotNull Context var0, @Nullable String var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      if (isDarkMode$intercom_sdk_base_release(var0)) {
         CharSequence var3 = var2;
         if (var3 != null && var3.length() != 0) {
            return var2;
         }
      }

      return var1;
   }

   @Composable
   @Nullable
   public final String selectUrlForThemeCompose$intercom_sdk_base_release(@Nullable String var1, @Nullable String var2, @Nullable Composer var3, int var4) {
      var3.startReplaceGroup(1242850468);
      ComposerKt.sourceInformation(var3, "C(selectUrlForThemeCompose)P(1)79@2972L19:ThemeUtils.kt#4knv12");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1242850468, var4, -1, "io.intercom.android.sdk.utilities.ThemeUtils.selectUrlForThemeCompose (ThemeUtils.kt:78)");
      }

      String var10000;
      label28: {
         boolean var5 = this.isDarkModeCompose$intercom_sdk_base_release(var3, 14 & var4 >> 6);
         if (var5) {
            CharSequence var7 = var2;
            if (var7 != null && var7.length() != 0) {
               var10000 = var2;
               break label28;
            }
         }

         var10000 = var1;
      }

      String var6 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var3.endReplaceGroup();
      return var6;
   }

   @JvmStatic
   @JvmOverloads
   @Nullable
   public static final String selectLauncherLogoUrl(@NotNull Context var0, @Nullable String var1, @Nullable String var2, @Nullable String var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      boolean var4 = isDarkMode$intercom_sdk_base_release(var0);
      if (var4) {
         CharSequence var5 = var2;
         if (var5 != null && var5.length() != 0) {
            return var2;
         }
      }

      CharSequence var6 = var1;
      return var6 != null && var6.length() != 0 ? var1 : var3;
   }

   // $VF: synthetic method
   public static String selectLauncherLogoUrl$default(Context var0, String var1, String var2, String var3, int var4, Object var5) {
      if ((var4 & 8) != 0) {
         var3 = null;
      }

      return selectLauncherLogoUrl(var0, var1, var2, var3);
   }

   @JvmStatic
   @Nullable
   public static final String selectHeaderLogoUrl$intercom_sdk_base_release(@NotNull Context var0, @Nullable String var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      boolean var3 = isDarkMode$intercom_sdk_base_release(var0);
      String var10000;
      if (var3) {
         var10000 = var2;
         if (var2 == null) {
            var10000 = var1;
         }
      } else {
         var10000 = var1;
      }

      return var10000;
   }

   @JvmStatic
   @Nullable
   public static final String selectHeaderBackgroundImageUrl$intercom_sdk_base_release(@NotNull Context var0, @Nullable String var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return selectUrlForTheme(var0, var1, var2);
   }

   @JvmStatic
   @NotNull
   public static final String selectColorForTheme(@NotNull Context var0, @NotNull String var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "lightColor");
      if (isDarkMode$intercom_sdk_base_release(var0)) {
         CharSequence var3 = var2;
         if (var3 != null && var3.length() != 0) {
            return var2;
         }
      }

      return var1;
   }

   @Composable
   @NotNull
   public final String selectColorForThemeCompose$intercom_sdk_base_release(@NotNull String var1, @Nullable String var2, @Nullable Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "lightColor");
      var3.startReplaceGroup(-1398327784);
      ComposerKt.sourceInformation(var3, "C(selectColorForThemeCompose)P(1)187@6597L19:ThemeUtils.kt#4knv12");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1398327784, var4, -1, "io.intercom.android.sdk.utilities.ThemeUtils.selectColorForThemeCompose (ThemeUtils.kt:186)");
      }

      String var10000;
      label28: {
         boolean var5 = this.isDarkModeCompose$intercom_sdk_base_release(var3, 14 & var4 >> 6);
         if (var5) {
            CharSequence var7 = var2;
            if (var7 != null && var7.length() != 0) {
               var10000 = var2;
               break label28;
            }
         }

         var10000 = var1;
      }

      String var6 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var3.endReplaceGroup();
      return var6;
   }

   @JvmStatic
   @NotNull
   public static final String appendInterfaceStyleToUrl(@NotNull Context var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "url");
      String var3 = var1;
      int var4 = 0;
      Uri var2 = Uri.parse(var3);
      var3 = isDarkMode$intercom_sdk_base_release(var0) ? "dark" : "light";
      String var10000 = var2.buildUpon().appendQueryParameter("theme", var3).build().toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      return var10000;
   }

   @JvmStatic
   @JvmOverloads
   @Nullable
   public static final String selectLauncherLogoUrl(@NotNull Context var0, @Nullable String var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return selectLauncherLogoUrl$default(var0, var1, var2, null, 8, null);
   }

   private static final ThemeMode isDarkModeCompose$lambda$0(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (ThemeMode)var1.getValue();
   }
}
