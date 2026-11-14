package io.intercom.android.sdk.overlay;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.CustomizationColorsModel;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.ThemeUtils;

public class DefaultLauncherPresenter implements DefaultLauncher$Listener {
   private final LayoutInflater inflater;
   private final LauncherOpenBehaviour openBehaviour;
   private final MetricTracker metricTracker;
   private final int cachedDefaultPadding;
   private int previousUnreadCount = 0;
   private int bottomPadding = 0;
   @VisibleForTesting
   @Nullable
   DefaultLauncher defaultLauncher;

   public DefaultLauncherPresenter(LayoutInflater var1, LauncherOpenBehaviour var2, MetricTracker var3) {
      this.inflater = var1;
      this.openBehaviour = var2;
      this.metricTracker = var3;
      this.cachedDefaultPadding = this.getDefaultPadding(var1.getContext().getResources());
      this.bottomPadding = this.cachedDefaultPadding;
   }

   public void displayLauncherOnAttachedRoot(ViewGroup var1, AppConfig var2) {
      if (this.defaultLauncher != null && !this.defaultLauncher.isAttachedToRoot(var1)) {
         this.defaultLauncher.removeView();
         this.defaultLauncher = null;
      }

      if (this.defaultLauncher == null) {
         this.defaultLauncher = new DefaultLauncher(var1, this.inflater, this, this.bottomPadding);
         this.defaultLauncher.fadeOnScreen();
      }

      this.setLauncherBackgroundColor(var2);
      this.setLauncherLogo(var2);
      this.setUnreadCount(this.previousUnreadCount);
   }

   DefaultLauncher getAndUnsetLauncher() {
      DefaultLauncher var1 = this.defaultLauncher;
      this.defaultLauncher = null;
      return var1;
   }

   void setLauncherBackgroundColor(@ColorInt int var1, @ColorInt int var2) {
      if (this.defaultLauncher != null) {
         this.defaultLauncher.setLauncherColor(var1, var2);
      }
   }

   void setLauncherBackgroundColor(AppConfig var1) {
      ConfigModules var2 = var1.getConfigModules();
      if (var2 != null) {
         CustomizationColorsModel var3 = var2.getCustomization().getAction();
         String var4 = ThemeUtils.selectColorForTheme(this.inflater.getContext(), var3.getBackgroundColor(), var3.getBackgroundColorDark());
         String var5 = ThemeUtils.selectColorForTheme(
            this.inflater.getContext(), var3.getForegroundLowContrastColor(), var3.getForegroundLowContrastColorDark()
         );
         int var6 = ColorUtils.parseColor(var4);
         int var7 = ColorUtils.parseColor(var5);
         this.setLauncherBackgroundColor(var7, var6);
      }
   }

   void setLauncherLogo(AppConfig var1) {
      boolean var2 = !TextUtils.isEmpty(var1.getLauncherLogoUrl())
         || var1.getConfigModules() != null
            && (
               !TextUtils.isEmpty(var1.getConfigModules().getCustomization().getLauncherLogoUrl())
                  || !TextUtils.isEmpty(var1.getConfigModules().getCustomization().getLauncherLogoDarkUrl())
            );
      if (this.defaultLauncher != null && var2) {
         this.defaultLauncher.setLauncherLogo(var1);
      } else if (this.defaultLauncher != null) {
         this.defaultLauncher.setDefaultLogo();
      }
   }

   void removeLauncher() {
      if (this.defaultLauncher != null) {
         this.defaultLauncher.fadeOffScreen(null);
         this.defaultLauncher = null;
      }
   }

   public boolean isDisplaying() {
      return this.defaultLauncher != null;
   }

   public void setUnreadCount(int var1) {
      if (this.isDisplaying()) {
         String var2 = String.valueOf(var1);
         if (var1 > this.previousUnreadCount) {
            this.metricTracker.receivedNotificationFromBadgeWhenMessengerClosed(var2);
         }

         if (var1 > 0) {
            this.defaultLauncher.setBadgeCount(var2);
         } else {
            this.defaultLauncher.hideBadgeCount();
         }
      }

      this.previousUnreadCount = var1;
   }

   @Override
   public void onLauncherClicked(Context var1) {
      this.openBehaviour.openMessenger(var1);
   }

   private int getDefaultPadding(Resources var1) {
      return var1.getDimensionPixelSize(dimen.intercom_launcher_padding_bottom) + var1.getDimensionPixelSize(dimen.intercom_bottom_padding);
   }

   public void setBottomPadding(int var1) {
      this.bottomPadding = this.cachedDefaultPadding + var1;
      if (this.isDisplaying()) {
         this.defaultLauncher.updateBottomPadding(this.bottomPadding);
      }
   }
}
