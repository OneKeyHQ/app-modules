package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

abstract class ChatNotification extends InAppNotification {
   private static final int ANIMATION_DURATION = 170;
   private static final int GROWTH_WIDTH = 355;

   protected ChatNotification(Context var1, Conversation var2, int var3, int var4, InAppNotification$Listener var5, Provider var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected abstract ViewGroup inflateChatRootView(ViewGroup var1, LayoutInflater var2);

   protected abstract View getContentContainer();

   @Override
   protected abstract void updateViewDataDuringReplyPulse(int var1);

   @Override
   public void display(ViewGroup var1, LayoutInflater var2, boolean var3, int var4) {
      if (this.overlayRoot == null) {
         this.overlayRoot = this.inflateChatRootView(var1, var2);
      }

      if (!this.isAttached()) {
         var1.addView(this.overlayRoot, 0);
      }

      LayoutParams var5 = (LayoutParams)this.overlayRoot.getLayoutParams();
      int var6 = this.calculateBottomMargin(this.position, var4, this.overlayRoot.getContext());
      var5.setMargins(var5.leftMargin, var5.topMargin, var5.rightMargin, var6);
      this.overlayRoot.setLayoutParams(var5);
      this.bottomPadding = var4;
      this.populateViewsWithData();
      this.overlayRoot.setVisibility(0);
      this.beginListeningForTouchEvents();
   }

   @Override
   protected void onNotificationPressed(View var1) {
      var1.animate().scaleX(0.9F).scaleY(0.9F).alpha(0.9F).setDuration(50L).start();
   }

   @Override
   protected void onNotificationReleased(View var1) {
      var1.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(50L).start();
   }

   @Override
   public void moveBackward(ViewGroup var1, AnimatorListenerAdapter var2) {
      this.position++;
      this.animateToPosition(var1.getContext());
      this.expandChat(var1, var2);
   }

   abstract void populateViewsWithData();

   private void expandChat(ViewGroup var1, AnimatorListenerAdapter var2) {
      ViewGroup var3 = (ViewGroup)var1.findViewById(id.chathead_text_container);
      if (var3 != null) {
         TextView var4 = (TextView)var1.findViewById(id.chathead_text_header);
         View var5 = this.getContentContainer();
         MarginLayoutParams var6 = (MarginLayoutParams)var3.getLayoutParams();
         View var7 = var1.findViewById(id.chat_avatar_container);
         float var8 = var7.getTranslationX();
         float var9 = var7.getTranslationY();
         ObjectAnimator.ofPropertyValuesHolder(
               var7,
               PropertyValuesHolder.ofFloat(View.ALPHA, 1.0F, 0.8F),
               PropertyValuesHolder.ofFloat("translationX", var8, var8 + 100.0F),
               PropertyValuesHolder.ofFloat("translationY", var9, var9 - 40.0F),
               PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 0.8F),
               PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 0.8F)
            )
            .setDuration(170L)
            .start();
         ObjectAnimator.ofFloat(var4, View.ALPHA, 1.0F, 0.0F).setDuration(170L).start();
         ObjectAnimator.ofFloat(var5, View.ALPHA, 1.0F, 0.0F).setDuration(170L).start();
         this.getWidthAnimator(var3, var2).start();
         this.getWidthAnimator(this.overlayRoot).start();
         this.getMarginAnimator(var3, var6).start();
      }
   }

   private Animator getMarginAnimator(ViewGroup var1, MarginLayoutParams var2) {
      int var3 = var2.leftMargin;
      ValueAnimator var4 = ValueAnimator.ofInt(var3, 0);
      var4.addUpdateListener(var1x -> {
         int animatedMargin = (Integer)var1x.getAnimatedValue();
         MarginLayoutParams animatedParams = (MarginLayoutParams)var1.getLayoutParams();
         animatedParams.leftMargin = animatedMargin;
         var1.setLayoutParams(animatedParams);
      });
      var4.setDuration(170L);
      return var4;
   }

   private Animator getWidthAnimator(ViewGroup var1, AnimatorListener var2) {
      int var3 = Math.abs(var1.getMeasuredWidth() - ScreenUtils.dpToPx(355.0F, var1.getContext()));
      ValueAnimator var4 = this.getWidthAnimator(var1);
      var4.addUpdateListener(new ChatNotification$1(this, var3, var2));
      return var4;
   }

   private ValueAnimator getWidthAnimator(ViewGroup var1) {
      int var2 = var1.getMeasuredWidth();
      int var3 = ScreenUtils.dpToPx(355.0F, var1.getContext());
      ValueAnimator var4 = ValueAnimator.ofInt(var2, var3);
      var4.addUpdateListener(var1x -> {
         int animatedWidth = (Integer)var1x.getAnimatedValue();
         android.view.ViewGroup.LayoutParams animatedParams = var1.getLayoutParams();
         animatedParams.width = animatedWidth;
         var1.setLayoutParams(animatedParams);
      });
      var4.setDuration(170L);
      return var4;
   }
}
