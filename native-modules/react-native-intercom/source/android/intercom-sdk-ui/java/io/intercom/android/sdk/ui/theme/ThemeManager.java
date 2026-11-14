package io.intercom.android.sdk.ui.theme;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0005J\u0006\u0010\u000b\u001a\u00020\bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0005J\u0006\u0010\r\u001a\u00020\u0005J\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fJ\u000e\u0010\u0010\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0005J\b\u0010\u0011\u001a\u00020\bH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/ThemeManager;",
         "",
         "<init>",
         "()V",
         "sessionOverride",
         "Lio/intercom/android/sdk/ui/theme/ThemeMode;",
         "serverTheme",
         "initialize",
         "",
         "setSessionOverride",
         "themeMode",
         "clearSessionOverride",
         "updateServerTheme",
         "getCurrentThemeMode",
         "getThemeModeState",
         "Landroidx/compose/runtime/MutableState;",
         "setThemeModeForTesting",
         "updateCurrentTheme",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ThemeManager {
   @NotNull
   public static final ThemeManager INSTANCE = new ThemeManager();
   @Nullable
   private static ThemeMode sessionOverride;
   @NotNull
   private static ThemeMode serverTheme = ThemeMode.LIGHT;
   public static final int $stable;

   static { $stable = 8; }

   private ThemeManager() {
   }

   public final void initialize() {
      sessionOverride = null;
      this.updateCurrentTheme();
   }

   public final void setSessionOverride(@NotNull ThemeMode var1) {
      Intrinsics.checkNotNullParameter(var1, "themeMode");
      sessionOverride = var1;
      this.updateCurrentTheme();
   }

   public final void clearSessionOverride() {
      sessionOverride = null;
      this.updateCurrentTheme();
   }

   public final void updateServerTheme(@NotNull ThemeMode var1) {
      Intrinsics.checkNotNullParameter(var1, "themeMode");
      serverTheme = var1;
      this.updateCurrentTheme();
   }

   @NotNull
   public final ThemeMode getCurrentThemeMode() {
      ThemeMode var10000 = sessionOverride;
      if (sessionOverride == null) {
         var10000 = serverTheme;
      }

      return var10000;
   }

   @NotNull
   public final MutableState getThemeModeState() {
      return IntercomColorsKt.getCurrentThemeMode();
   }

   public final void setThemeModeForTesting(@NotNull ThemeMode var1) {
      Intrinsics.checkNotNullParameter(var1, "themeMode");
      this.setSessionOverride(var1);
   }

   private final void updateCurrentTheme() {
      IntercomColorsKt.getCurrentThemeMode().setValue(this.getCurrentThemeMode());
   }
}
