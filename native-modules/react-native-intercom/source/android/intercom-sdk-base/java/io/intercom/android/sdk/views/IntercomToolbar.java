package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.utilities.AvatarUtils;
import io.intercom.android.sdk.utilities.ColorUtils;

public class IntercomToolbar extends Toolbar implements OnTouchListener, OnClickListener {
   private static final int TITLE_FADE_DURATION_MS = 150;
   private final BackButtonCountDrawable backButtonCountDrawable;
   final TextView title;
   private final TextView subtitle;
   private final ImageButton close;
   private final ImageButton backButton;
   private final ImageView avatar;
   private final View activeStateView;
   @Nullable
   private IntercomToolbar$Listener listener;

   public IntercomToolbar(Context var1) {
      this(var1, null);
   }

   public IntercomToolbar(Context var1, @Nullable AttributeSet var2) {
      super(var1, var2);
      View var3 = inflate(var1, layout.intercom_toolbar, this);
      var3.setOnTouchListener(this);
      this.title = (TextView)var3.findViewById(id.intercom_toolbar_title);
      this.subtitle = (TextView)var3.findViewById(id.intercom_toolbar_subtitle);
      this.close = (ImageButton)var3.findViewById(id.intercom_toolbar_close);
      this.backButton = (ImageButton)var3.findViewById(id.intercom_toolbar_inbox);
      this.avatar = (ImageView)var3.findViewById(id.intercom_toolbar_avatar);
      this.activeStateView = var3.findViewById(id.intercom_toolbar_avatar_active_state);
      this.backButtonCountDrawable = new BackButtonCountDrawable(this.getContext(), null);
      this.backButton.setImageDrawable(this.backButtonCountDrawable);
      this.backButton.setOnClickListener(this);
      this.close.setOnClickListener(this);
   }

   public void setListener(@Nullable IntercomToolbar$Listener var1) {
      this.listener = var1;
   }

   public void setCloseButtonVisibility(int var1) {
      this.close.setVisibility(var1);
   }

   public void setSubtitleVisibility(int var1) {
      this.subtitle.setVisibility(var1);
   }

   public void updateToolbarColors(AppConfig var1) {
      boolean var2 = var1.isSecondaryColorRenderDarkText();
      ColorUtils.setTextColorWhiteOrBlack(this.title, var2);
      ColorUtils.setTextColorWhiteOrBlack(this.subtitle, var2);
      ColorUtils.setImageColorWhiteOrBlack(this.close, var2);
      ColorUtils.setImageColorWhiteOrBlack(this.backButton, var2);
   }

   public void setUpPostToolbar(Participant var1, boolean var2, ActiveStatePresenter var3, AppConfig var4) {
      this.setBackgroundColor(0);
      this.subtitle.setTextColor(-1);
      this.title.setTextColor(-1);
      this.close.setColorFilter(-1);
      this.backButton.setVisibility(8);
      this.avatar.setVisibility(0);
      this.activeStateView.setVisibility(0);
      this.title.setTextSize(14.0F);
      this.findViewById(id.intercom_toolbar_divider).setVisibility(0);
      AvatarUtils.loadAvatarIntoView(var1.getAvatar(), this.avatar, var4);
      var3.presentStateDot(var2, this.activeStateView, var4);
   }

   public void setUpNoteToolbar(Participant var1, boolean var2, ActiveStatePresenter var3, AppConfig var4) {
      this.title.setTextColor(ContextCompat.getColor(this.getContext(), color.intercom_accessibility_black));
      this.subtitle.setTextColor(ContextCompat.getColor(this.getContext(), color.intercom_accessibility_grey));
      this.close.setColorFilter(ContextCompat.getColor(this.getContext(), color.intercom_accessibility_grey));
      this.backButton.setVisibility(8);
      this.avatar.setVisibility(0);
      this.activeStateView.setVisibility(0);
      this.title.setTextSize(14.0F);
      this.subtitle.setAlpha(1.0F);
      View var5 = this.findViewById(id.intercom_toolbar_divider);
      var5.setVisibility(0);
      var5.setBackgroundColor(-16777216);
      AvatarUtils.loadAvatarIntoView(var1.getAvatar(), this.avatar, var4);
      var3.presentStateDot(var2, this.activeStateView, var4);
   }

   public void setLeftNavigationItemVisibility(int var1) {
      this.backButton.setVisibility(var1);
   }

   @Override
   public void setTitle(CharSequence var1) {
      if (TextUtils.isEmpty(var1)) {
         this.fadeOutTitle(150);
      } else {
         this.title.setAlpha(1.0F);
         this.title.setText(var1);
      }
   }

   @Override
   public void setSubtitle(@Nullable CharSequence var1) {
      this.subtitle.setText(var1);
      if (!TextUtils.isEmpty(var1)) {
         this.subtitle.setAlpha(1.0F);
      }
   }

   public void fadeOutTitle(int var1) {
      this.title.animate().alpha(0.0F).setDuration(var1).setListener(new IntercomToolbar$1(this)).start();
   }

   @Override
   public void onClick(View var1) {
      if (this.listener != null) {
         int var2 = var1.getId();
         if (var2 == id.intercom_toolbar_close) {
            this.listener.onCloseClicked();
         } else if (var2 == id.intercom_toolbar_inbox) {
            this.listener.onInboxClicked();
         }
      }
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      if (this.listener == null) {
         return false;
      }

      if (var2.getAction() == 1 && var1.getId() == id.intercom_toolbar) {
         this.listener.onToolbarClicked();
      }

      return false;
   }

   public void setLeftNavigationIcon(@Nullable Drawable var1) {
      this.backButton.setImageDrawable(var1);
   }
}
