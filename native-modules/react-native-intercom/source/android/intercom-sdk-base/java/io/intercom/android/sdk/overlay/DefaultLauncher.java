package io.intercom.android.sdk.overlay;

import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.BackgroundUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.ThemeUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class DefaultLauncher implements OnTouchListener {
   private static final int ANIMATION_DURATION_MS = 300;
   private static final int CUSTOM_LAUNCHER_SIZE_DP = 34;
   private final TextView badgeCount;
   final View launcherRoot;
   final DefaultLauncher$Listener listener;
   private final ImageButton badge;

   public DefaultLauncher(ViewGroup var1, LayoutInflater var2, DefaultLauncher$Listener var3, int var4) {
      this.listener = var3;
      var2.inflate(layout.intercom_default_launcher, var1, true);
      this.launcherRoot = var1.findViewById(id.launcher_root);
      MarginLayoutParams var5 = (MarginLayoutParams)this.launcherRoot.getLayoutParams();
      var5.setMargins(var5.leftMargin, var5.topMargin, var5.rightMargin, var4);
      this.launcherRoot.setLayoutParams(var5);
      this.badge = (ImageButton)this.launcherRoot.findViewById(id.launcher_icon);
      this.badgeCount = (TextView)this.launcherRoot.findViewById(id.launcher_badge_count);
      this.launcherRoot.setOnTouchListener(this);
   }

   public void setLauncherColor(@ColorInt int var1, @ColorInt int var2) {
      Context var3 = this.badge.getContext();
      Drawable var4 = ContextCompat.getDrawable(var3, drawable.intercom_solid_circle);
      var4.setColorFilter(var2, Mode.SRC_IN);
      Drawable var5 = var4.getConstantState().newDrawable();
      int var6 = ContextCompat.getColor(var3, color.intercom_inbox_count_background);
      var5.setColorFilter(var6, Mode.SRC_IN);
      BackgroundUtils.setBackground(this.badgeCount, var5);
      BackgroundUtils.setBackground(this.badge, var4);
      Drawable var7 = this.badge.getDrawable();
      if (var7 != null) {
         var7.setColorFilter(var1, Mode.SRC_IN);
      }
   }

   public void setLauncherLogo(AppConfig var1) {
      Context var2 = this.badge.getContext();
      int var3 = ScreenUtils.dpToPx(34.0F, var2);
      String var4 = var1.getConfigModules() != null ? var1.getConfigModules().getCustomization().getLauncherLogoUrl() : null;
      String var5 = var1.getConfigModules() != null ? var1.getConfigModules().getCustomization().getLauncherLogoDarkUrl() : null;
      String var6 = var1.getLauncherLogoUrl();
      String var7 = ThemeUtils.selectLauncherLogoUrl(var2, var4, var5, var6);
      ImageRequest var8 = new Builder(var2).data(var7).error(drawable.intercom_launcher_icon).target(this.badge).size(var3, var3).build();
      IntercomCoilKt.loadIntercomImage(var2, var8);
   }

   public void setDefaultLogo() {
      this.badge.setImageResource(drawable.intercom_launcher_icon);
   }

   public void setBadgeCount(String var1) {
      this.badgeCount.setVisibility(0);
      this.badgeCount.setText(var1);
   }

   public void hideBadgeCount() {
      this.badgeCount.setVisibility(8);
   }

   public void fadeOnScreen() {
      this.launcherRoot.setAlpha(0.0F);
      this.launcherRoot.animate().alpha(1.0F).setDuration(100L).start();
   }

   public void fadeOffScreen(@Nullable AnimatorListener var1) {
      this.launcherRoot.animate().alpha(0.0F).setDuration(100L).setListener(var1).start();
   }

   public void pulseForTransformation(AnimatorListener var1) {
      this.launcherRoot.animate().scaleX(1.1F).scaleY(1.1F).setDuration(100L).setListener(new DefaultLauncher$1(this, var1)).start();
   }

   public void removeView() {
      if (this.launcherRoot.getParent() != null) {
         ((ViewGroup)this.launcherRoot.getParent()).removeView(this.launcherRoot);
      }
   }

   public boolean isAttachedToRoot(ViewGroup var1) {
      return this.launcherRoot.getParent() == var1;
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      switch (var2.getAction()) {
         case 0:
            var1.setScaleX(0.9F);
            var1.setScaleY(0.9F);
            break;
         case 1:
            this.callListenerWithFadeOut();
      }

      return true;
   }

   private void callListenerWithFadeOut() {
      this.launcherRoot.setAlpha(1.0F);
      this.launcherRoot.animate().alpha(0.0F).setDuration(50L).setListener(new DefaultLauncher$2(this)).start();
   }

   public void updateBottomPadding(int var1) {
      MarginLayoutParams var2 = (MarginLayoutParams)this.launcherRoot.getLayoutParams();
      ValueAnimator var3 = ValueAnimator.ofInt(var2.bottomMargin, var1);
      var3.setDuration(300L);
      var3.addUpdateListener(new DefaultLauncher$3(this, var2));
      var3.start();
   }
}
