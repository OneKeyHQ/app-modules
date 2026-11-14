package io.intercom.android.sdk.overlay;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import androidx.compose.ui.platform.ComposeView;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.notification.InAppNotificationCardKt;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.AvatarUtils;

class StackableSnippet extends InAppNotification implements OnTouchListener {
   private static final int BASE_ELEVATION = 18;
   private static final int ELEVATION_STEP = 3;
   private final Handler handler;

   public StackableSnippet(Context var1, Conversation var2, int var3, Handler var4, int var5, InAppNotification$Listener var6, Provider var7) {
      super(var1, var2, var3, var5, var6, var7);
      this.handler = var4;
   }

   @Override
   public void moveBackward(ViewGroup var1, AnimatorListenerAdapter var2) {
      this.position++;
      this.animateToPosition(var1.getContext());
   }

   @Override
   public void display(ViewGroup var1, LayoutInflater var2, boolean var3, int var4) {
      this.bottomPadding = var4;
      this.setupView(var1, var2);
      this.setViewData(var4);
      if (var3) {
         this.animateOnScreen();
      } else {
         this.overlayRoot.setVisibility(0);
      }
   }

   @Override
   public void update(Conversation var1, Runnable var2) {
      this.conversation = var1;
      View var3 = this.overlayRoot.findViewById(id.notification_root);
      View var4 = this.overlayRoot.findViewById(id.in_app_notification_message_summary);
      this.performReplyPulse(var3, var4, var2);
   }

   @Override
   protected void updateViewDataDuringReplyPulse(int var1) {
      this.setViewData(var1);
   }

   private void setupView(ViewGroup var1, LayoutInflater var2) {
      if (this.overlayRoot == null) {
         this.overlayRoot = (ViewGroup)var2.inflate(layout.intercom_preview_notification, var1, false);
      }

      if (!this.isAttached()) {
         var1.addView(this.overlayRoot, 0);
      }
   }

   private void setViewData(int var1) {
      Context var2 = this.overlayRoot.getContext();
      ComposeView var3 = (ComposeView)this.overlayRoot.findViewById(id.chat_snippet_compose_view);
      if (ComposeCompatibilityUtilKt.hideForLegacyActivity(var3)) {
         this.setupLegacyNotification(var2, var1);
      } else {
         this.setupComposeNotification(var3, var2, var1);
      }

      this.overlayRoot.setElevation(18 - 3 * this.position);
      this.overlayRoot.setScaleX(1.0F - this.position * 0.05F);
      if (this.position == 0) {
         this.beginListeningForTouchEvents();
      }
   }

   private void setupComposeNotification(ComposeView var1, Context var2, int var3) {
      InAppNotificationCardKt.addInappNotificationCardToView(var1, this.conversation);
      var1.setVisibility(0);
      LayoutParams var4 = (LayoutParams)this.overlayRoot.getLayoutParams();
      var4.bottomMargin = this.calculateBottomMargin(this.position, var3, var2);
      FrameLayout var5 = (FrameLayout)this.overlayRoot.findViewById(id.parent_card);
      if (var5 != null) {
         var5.setVisibility(8);
      }
   }

   private void setupLegacyNotification(Context var1, int var2) {
      AppConfig var3 = (AppConfig)this.appConfigProvider.get();
      TextView var4 = (TextView)this.overlayRoot.findViewById(id.reply_from_textview);
      var4.setText(this.getHeaderText());
      this.setSummaryText(this.conversation.lastPart());
      ImageView var5 = (ImageView)this.overlayRoot.findViewById(id.preview_avatar);
      AvatarUtils.loadAvatarIntoView(this.conversation.lastPart().getParticipant().getAvatar(), var5, var3);
      LayoutParams var6 = (LayoutParams)this.overlayRoot.getLayoutParams();
      var6.bottomMargin = this.calculateBottomMargin(this.position, var2, var1);
      FrameLayout var7 = (FrameLayout)this.overlayRoot.findViewById(id.parent_card);
      if (var7 != null) {
         var7.setVisibility(0);
      }
   }

   private void setSummaryText(Part var1) {
      TextView var2 = (TextView)this.overlayRoot.findViewById(id.in_app_notification_message_summary);
      if (var1.getEventData().getCustomStatePrefix() != null && !var1.getEventData().getCustomStatePrefix().isEmpty()) {
         var2.setText(var1.getEventData().getCustomStatePrefix());
      } else {
         var2.setText(var1.getSummary());
      }
   }

   private void animateOnScreen() {
      this.handler.postDelayed(() -> {
         if (this.overlayRoot != null) {
            this.overlayRoot.setVisibility(0);
            this.overlayRoot.setY(this.screenHeight);
            this.overlayRoot.animate().setInterpolator(new OvershootInterpolator(0.6F)).translationY(0.0F).setDuration(300L).start();
         }
      }, this.position * 70);
   }

   @Override
   protected void onNotificationPressed(View var1) {
      var1.animate().scaleX(0.95F).scaleY(0.95F).setDuration(50L).start();
   }

   @Override
   protected void onNotificationReleased(View var1) {
      var1.animate().scaleX(1.0F).scaleY(1.0F).setDuration(50L).start();
   }
}
