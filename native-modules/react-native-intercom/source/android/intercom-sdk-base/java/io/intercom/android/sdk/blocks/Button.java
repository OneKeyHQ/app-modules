package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.ButtonBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.FacebookBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.TwitterBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.ParagraphView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BackgroundUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.FontUtils;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.ButtonSelector;

class Button implements ButtonBlock, FacebookBlock, TwitterBlock {
   private static final int FACEBOOK_LOGO_PADDING_LEFT_DP = 20;
   private static final int TWITTER_LOGO_PADDING_LEFT_DP = 16;
   private final StyleType style;
   private final Provider appConfigProvider;
   @Nullable
   private final ButtonClickListener buttonClickListener;

   public Button(StyleType var1, Provider var2, @Nullable ButtonClickListener var3) {
      this.style = var1;
      this.appConfigProvider = var2;
      this.buttonClickListener = var3;
   }

   @Override
   public View addButton(Spanned var1, String var2, BlockAlignment var3, BlockMetadata var4, ViewGroup var5) {
      Context var6 = var5.getContext();
      View var7;
      if (this.style != StyleType.NOTE && this.style != StyleType.ARTICLE && this.style != StyleType.POST && this.style != StyleType.CONTAINER_CARD) {
         var7 = this.createDefaultTextView(var6, var1, var3);
      } else {
         var7 = this.createFullWidthButton(var6, var1);
      }

      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(var7);
      return this.setButtonDefaults(var7, var2, var3, var4.isLastObject());
   }

   @Override
   public View addFacebookButton(String var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4) {
      Context var5 = var4.getContext();
      String var6 = var5.getString(string.intercom_facebook_like);
      View var7;
      if (this.style != StyleType.NOTE && this.style != StyleType.ARTICLE && this.style != StyleType.POST && this.style != StyleType.CONTAINER_CARD) {
         var7 = this.createDefaultTextView(var5, HtmlCompat.fromHtml(var6), var2);
      } else {
         int var8 = ScreenUtils.dpToPx(20.0F, var5);
         var7 = this.createButtonWithLogo(var5, var6, var8, drawable.intercom_icn_fb);
      }

      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(var7);
      return this.setButtonDefaults(var7, var1, var2, var3.isLastObject());
   }

   @Override
   public View addTwitterButton(String var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4) {
      Context var5 = var4.getContext();
      String var6 = var5.getString(string.intercom_twitter_follow);
      View var7;
      if (this.style != StyleType.NOTE && this.style != StyleType.ARTICLE && this.style != StyleType.POST && this.style != StyleType.CONTAINER_CARD) {
         var7 = this.createDefaultTextView(var5, HtmlCompat.fromHtml(var6), var2);
      } else {
         int var8 = ScreenUtils.dpToPx(16.0F, var5);
         var7 = this.createButtonWithLogo(var5, var6, var8, drawable.intercom_icn_twitter);
      }

      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(var7);
      return this.setButtonDefaults(var7, var1, var2, var3.isLastObject());
   }

   private View setButtonDefaults(View var1, String var2, BlockAlignment var3, boolean var4) {
      if (this.buttonClickListener != null && this.buttonClickListener.shouldHandleClicks()) {
         var1.setOnClickListener(new Button$1(this, var2));
      }

      BlockUtils.setLayoutMarginsAndGravity(var1, var3.getGravity(), var4);
      return var1;
   }

   private TextView createDefaultTextView(Context var1, Spanned var2, BlockAlignment var3) {
      AppConfig var4 = (AppConfig)this.appConfigProvider.get();
      TextView var5 = new ParagraphView(var1, this.style, var3, var4);
      var5.setTextColor(var4.getPrimaryColor());
      var5.setPaintFlags(var5.getPaintFlags() | 8);
      var5.setGravity(var3.getGravity());
      var5.setText(var2);
      return var5;
   }

   private android.widget.Button createFullWidthButton(Context var1, Spanned var2) {
      AppConfig var3 = (AppConfig)this.appConfigProvider.get();
      android.widget.Button var4 = new android.widget.Button(var1);
      var4.setTextSize(16.0F);
      var4.setTextColor(ColorUtils.whiteOrBlackAccessibility(var1, var3));
      var4.setAllCaps(false);
      var4.setGravity(17);
      var4.setText(var2);
      BlockUtils.createLayoutParams(var4, -1, -2);
      BlockUtils.setDefaultMarginBottom(var4);
      int var5 = var3.getPrimaryColor();
      BackgroundUtils.setBackground(var4, new ButtonSelector(var1, drawable.intercom_border, var5));
      FontUtils.setRobotoMediumTypeface(var4);
      return var4;
   }

   private View createButtonWithLogo(Context var1, String var2, int var3, @DrawableRes int var4) {
      FrameLayout var5 = new FrameLayout(var1);
      BlockUtils.createLayoutParams(var5, -1, ScreenUtils.dpToPx(44.0F, var1));
      BlockUtils.setDefaultMarginBottom(var5);
      int var6 = ((AppConfig)this.appConfigProvider.get()).getPrimaryColor();
      BackgroundUtils.setBackground(var5, new ButtonSelector(var1, drawable.intercom_border, var6));
      ((LayoutParams)var5.getLayoutParams()).gravity = 17;
      ImageView var7 = new ImageView(var1);
      BlockUtils.createLayoutParams(var7, -2, -1);
      var7.setImageResource(var4);
      var7.setBackgroundResource(color.intercom_full_transparent_full_black);
      if (ColorUtils.isColorLight(((AppConfig)this.appConfigProvider.get()).getPrimaryColor())) {
         DrawableCompat.setTint(var7.getDrawable(), ContextCompat.getColor(var1, color.intercom_accessibility_black));
      }

      var7.setPadding(var3, 0, 0, 0);
      ((LayoutParams)var7.getLayoutParams()).gravity = 8388627;
      TextView var8 = new TextView(var1);
      BlockUtils.createLayoutParams(var8, -1, -1);
      var8.setText(var2);
      var8.setTextSize(16.0F);
      var8.setTextColor(ColorUtils.whiteOrBlackAccessibility(var1, (AppConfig)this.appConfigProvider.get()));
      var8.setBackgroundResource(color.intercom_full_transparent_full_black);
      var8.setGravity(17);
      FontUtils.setRobotoMediumTypeface(var8);
      var5.addView(var7);
      var5.addView(var8);
      return var5;
   }

   // $VF: synthetic method
   static ButtonClickListener access$000(Button var0) {
      return var0.buttonClickListener;
   }
}
