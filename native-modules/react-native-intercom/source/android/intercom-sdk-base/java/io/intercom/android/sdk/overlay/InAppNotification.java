package io.intercom.android.sdk.overlay;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnTouchListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout.LayoutParams;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.Phrase;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;

abstract class InAppNotification implements OnTouchListener {
   protected static final int BASE_MARGIN_DP = 16;
   private static final float RESISTANCE = 2.0F;
   private static final int ANIMATE_OFFSCREEN_OFFSET = 200;
   private static final int DISMISS_DISTANCE_DP = 80;
   protected static final float SCALE_OFFSET = 0.05F;
   protected static final int MARGIN_OFFSET_DP = 8;
   final InAppNotification$Listener listener;
   protected final int screenHeight;
   protected final Context localisedContext;
   protected final Provider appConfigProvider;
   protected Conversation conversation;
   protected int position;
   private float initialViewX;
   private float initialTouchX;
   protected ViewGroup overlayRoot;
   protected int bottomPadding = 0;

   protected InAppNotification(Context var1, Conversation var2, int var3, int var4, InAppNotification$Listener var5, Provider var6) {
      this.conversation = var2;
      this.listener = var5;
      this.position = var3;
      this.screenHeight = var4;
      this.localisedContext = var1;
      this.appConfigProvider = var6;
   }

   protected int calculateBottomMargin(int var1, int var2, Context var3) {
      Resources var4 = var3.getResources();
      int var5 = ScreenUtils.dpToPx(16 + 8 * var1, var3);
      return var5 + var4.getDimensionPixelSize(dimen.intercom_bottom_padding) + var2;
   }

   public void removeView() {
      View var1 = this.getRootView();
      if (var1 != null && var1.getParent() != null) {
         ((ViewGroup)var1.getParent()).removeView(var1);
      }
   }

   public Conversation getConversation() {
      return this.conversation;
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      float var3 = (var2.getRawX() - this.initialTouchX) / 2.0F;
      switch (var2.getAction()) {
         case 0:
            this.initialViewX = var1.getX();
            this.initialTouchX = var2.getRawX();
            this.onNotificationPressed(this.getRootView());
            break;
         case 1:
            float var4 = Math.abs(var3);
            if (var4 < 5.0F) {
               this.listener.onInAppNotificationTap(this.conversation);
            } else if (var4 > ScreenUtils.dpToPx(80.0F, this.localisedContext)) {
               this.stopListeningForTouchEvents();
               this.animateOffScreen(var1);
            } else {
               this.onNotificationReleased(this.getRootView());
               this.animateToOriginalPosition(var1);
            }
            break;
         case 2:
            var1.setX(var3 + this.initialViewX);
      }

      return true;
   }

   private void animateToOriginalPosition(View var1) {
      var1.animate().setInterpolator(new OvershootInterpolator(0.6F)).translationX(0.0F).setDuration(300L).start();
   }

   private void animateOffScreen(View var1) {
      int var2 = ScreenUtils.dpToPx(200.0F, var1.getContext());
      float var3;
      if (var1.getX() > this.initialViewX) {
         var3 = this.getParentOrScreenWidth(var1) + var2;
      } else {
         var3 = -1 * var1.getWidth() - var2;
      }

      var1.animate().setInterpolator(new OvershootInterpolator(0.6F)).translationX(var3).setDuration(300L).setListener(new InAppNotification$1(this)).start();
   }

   private int getParentOrScreenWidth(View var1) {
      ViewParent var2 = var1.getParent();
      return var2 instanceof View ? ((View)var2).getWidth() : var1.getResources().getDisplayMetrics().widthPixels;
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int var1) {
      this.position = var1;
   }

   public boolean isAttached() {
      return this.overlayRoot.getParent() != null;
   }

   protected CharSequence getHeaderText() {
      Part var1 = this.conversation.lastPart();
      String var2 = PartExtensionsKt.resolvedDisplayName(var1);
      if (this.conversation.getParticipants().size() > 1) {
         return Phrase.from(this.localisedContext, string.intercom_reply_from_admin).putOptional("name", var2).format();
      } else {
         return this.conversation.parts().isEmpty()
            ? var2
            : var2 + " • " + TimeFormatterExtKt.formattedDateFromLong(var1.getCreatedAt(), this.localisedContext);
      }
   }

   public void updateBottomPadding(Context var1, int var2) {
      if (this.bottomPadding != var2 && this.overlayRoot != null) {
         this.bottomPadding = var2;
         int var3 = this.calculateBottomMargin(this.position, var2, var1);
         LayoutParams var4 = (LayoutParams)this.overlayRoot.getLayoutParams();
         ValueAnimator var5 = ValueAnimator.ofInt(var4.bottomMargin, var3);
         int var6 = 300;
         var5.setDuration(var6);
         var5.addUpdateListener(var2x -> {
            var4.setMargins(var4.leftMargin, var4.topMargin, var4.rightMargin, (Integer)var2x.getAnimatedValue());
            this.overlayRoot.setLayoutParams(var4);
         });
         var5.start();
      }
   }

   protected void animateToPosition(Context var1) {
      int var2 = this.calculateBottomMargin(this.position, this.bottomPadding, var1);
      LayoutParams var3 = (LayoutParams)this.overlayRoot.getLayoutParams();
      float var4 = this.overlayRoot.getScaleX();
      float var5 = 1.0F - this.position * 0.05F;
      ValueAnimator var6 = ValueAnimator.ofInt(var3.bottomMargin, var2);
      var6.setDuration(200L);
      var6.addUpdateListener(var2x -> {
         var3.setMargins(var3.leftMargin, var3.topMargin, var3.rightMargin, (Integer)var2x.getAnimatedValue());
         this.overlayRoot.setLayoutParams(var3);
      });
      PropertyValuesHolder var7 = PropertyValuesHolder.ofFloat("scaleX", var4, var5);
      ObjectAnimator var8 = ObjectAnimator.ofPropertyValuesHolder(this.overlayRoot, var7);
      var8.setDuration(200L);
      var6.start();
      var8.start();
   }

   protected void animateToBackOfStack(Context var1) {
      Resources var2 = var1.getResources();
      int var3 = (int)var2.getDimension(dimen.intercom_notification_preview_height);
      int var4 = this.calculateBottomMargin(this.position, this.bottomPadding, var1);
      int var5 = this.screenHeight - (var3 + var4);
      int var6 = var5 + ScreenUtils.dpToPx(8.0F, var1);
      PropertyValuesHolder var7 = PropertyValuesHolder.ofFloat(View.Y, var6, var5);
      PropertyValuesHolder var8 = PropertyValuesHolder.ofFloat(View.ALPHA, this.overlayRoot.getAlpha(), 1.0F);
      PropertyValuesHolder var9 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.85F, 1.0F - this.position * 0.05F);
      ObjectAnimator.ofPropertyValuesHolder(this.overlayRoot, var7, var8, var9).setDuration(200L).start();
   }

   protected void performReplyPulse(View var1, View var2, Runnable var3) {
      PropertyValuesHolder var4 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.05F);
      PropertyValuesHolder var5 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.05F);
      ObjectAnimator var6 = ObjectAnimator.ofPropertyValuesHolder(var1, var4, var5).setDuration(150L);
      var6.setRepeatCount(1);
      var6.setRepeatMode(2);
      var6.start();
      ObjectAnimator var7 = ObjectAnimator.ofFloat(var2, View.ALPHA, 1.0F, 0.0F).setDuration(200L);
      var7.addListener(new InAppNotification$2(this, var2, var3));
      var7.start();
   }

   public void moveForward(ViewGroup var1, LayoutInflater var2) {
      this.position--;
      if (this.position <= 0) {
         this.animateToPosition(var1.getContext());
         this.beginListeningForTouchEvents();
      } else if (this.position == 1) {
         this.animateToPosition(var1.getContext());
      } else if (this.position == 2) {
         this.display(var1, var2, false, this.bottomPadding);
         this.animateToBackOfStack(var1.getContext());
      }
   }

   protected void stopListeningForTouchEvents() {
      this.overlayRoot.setOnTouchListener(null);
   }

   protected void beginListeningForTouchEvents() {
      this.overlayRoot.setOnTouchListener(this);
   }

   protected View getRootView() {
      return this.overlayRoot;
   }

   protected abstract void onNotificationPressed(View var1);

   protected abstract void onNotificationReleased(View var1);

   public void display(ViewGroup var1, Conversation var2, LayoutInflater var3, boolean var4, int var5) {
      this.conversation = var2;
      this.display(var1, var3, var4, var5);
   }

   public abstract void display(ViewGroup var1, LayoutInflater var2, boolean var3, int var4);

   public abstract void update(Conversation var1, Runnable var2);

   protected abstract void updateViewDataDuringReplyPulse(int var1);

   public void moveBackward(ViewGroup var1) {
      this.moveBackward(var1, new InAppNotification$3(this));
   }

   public abstract void moveBackward(ViewGroup var1, AnimatorListenerAdapter var2);
}
