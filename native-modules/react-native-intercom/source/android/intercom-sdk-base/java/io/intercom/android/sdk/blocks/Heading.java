package io.intercom.android.sdk.blocks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.HeadingBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.SubheadingBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.CustomizationColorsModel;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.FontUtils;
import io.intercom.android.sdk.utilities.TrackingLinkMovementMethod;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class Heading implements HeadingBlock, SubheadingBlock {
   private static final int LARGE_TEXT_SP = 24;
   private static final int MEDIUM_TEXT_SP = 20;
   private static final int SMALL_TEXT_SP = 15;
   private static final int LARGE_MARGIN_BOTTOM_DP = 32;
   private static final int MARGIN_BOTTOM_DP = 16;
   private final StyleType style;
   private final Provider appConfigProvider;

   Heading(StyleType var1, Provider var2) {
      this.style = var1;
      this.appConfigProvider = var2;
   }

   @Override
   public View addHeading(Spanned var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4) {
      Context var5 = var4.getContext();
      TextView var6 = new TextView(var5);
      var6.setMovementMethod(new TrackingLinkMovementMethod());
      BlockUtils.createLayoutParams(var6, -2, -2);
      ConfigModules var8 = ((AppConfig)this.appConfigProvider.get()).getConfigModules();
      int var7;
      if (var8 != null) {
         CustomizationColorsModel var9 = var8.getCustomization().getActionContrastWhite();
         CustomizationColorsModel var10 = var9 == null ? var8.getCustomization().getAction() : var9;
         var7 = Color.parseColor(var10.getBackgroundColor());
      } else {
         var7 = -16777216;
      }

      switch (this.style) {
         case POST:
            int var11 = ColorUtils.lightenColor(var7);
            this.styleAnnouncementHeading(var6, var11, 24, 32);
            FontUtils.setRobotoLightTypeface(var6);
            break;
         case NOTE:
            int var12 = ContextCompat.getColor(var5, color.intercom_grey_700);
            this.styleAnnouncementHeading(var6, var12, 24, 32);
            break;
         case ARTICLE:
            this.styleAnnouncementHeading(var6, ColorUtils.primaryOrDarkColor(var5, (AppConfig)this.appConfigProvider.get()), 24, 32);
            break;
         case CONTAINER_CARD:
            this.styleAnnouncementHeading(var6, var7, 20, 32);
            break;
         case CHAT_FULL:
            this.styleChatHeading(var6, ContextCompat.getColor(var5, color.intercom_grey_800));
            var6.setTypeface(null, 1);
            break;
         case CAROUSEL:
            this.styleCarouselHeading1(var6, var3);
            break;
         default:
            this.styleChatHeading(var6, ContextCompat.getColor(var5, color.intercom_grey_800));
            var6.setTypeface(null, 1);
      }

      var6.setText(var1);
      var6.setGravity(var2.getGravity());
      BlockUtils.setLayoutMarginsAndGravity(var6, var2.getGravity(), var3.isLastObject());
      AccessibilityUtils.INSTANCE.removeClickAbilityAnnouncement(var6);
      AccessibilityUtils.INSTANCE.addHeadingAnnouncement(var6);
      return var6;
   }

   @Override
   public View addSubheading(Spanned var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4) {
      Context var5 = var4.getContext();
      TextView var6 = new TextView(var5);
      var6.setMovementMethod(new TrackingLinkMovementMethod());
      BlockUtils.createLayoutParams(var6, -2, -2);
      ConfigModules var8 = ((AppConfig)this.appConfigProvider.get()).getConfigModules();
      int var7;
      if (var8 != null) {
         CustomizationColorsModel var9 = var8.getCustomization().getActionContrastWhite();
         CustomizationColorsModel var10 = var9 == null ? var8.getCustomization().getAction() : var9;
         var7 = Color.parseColor(var10.getBackgroundColor());
      } else {
         var7 = -16777216;
      }

      switch (this.style) {
         case POST:
            this.styleAnnouncementHeading(var6, ColorUtils.lightenColor(var7), 15, 16);
            FontUtils.setRobotoLightTypeface(var6);
            break;
         case NOTE:
         case CONTAINER_CARD:
            int var11 = ContextCompat.getColor(var5, color.intercom_grey_700);
            this.styleAnnouncementHeading(var6, var11, 15, 16);
            break;
         case ARTICLE:
            this.styleAnnouncementHeading(var6, ColorUtils.primaryOrDarkColor(var5, (AppConfig)this.appConfigProvider.get()), 15, 16);
            break;
         case CHAT_FULL:
            this.styleChatHeading(var6, ContextCompat.getColor(var5, color.intercom_grey_800));
            break;
         case CAROUSEL:
            this.styleCarouselHeading2(var6, var3);
            break;
         default:
            this.styleChatHeading(var6, var7);
      }

      var6.setText(var1);
      var6.setGravity(var2.getGravity());
      BlockUtils.setLayoutMarginsAndGravity(var6, var2.getGravity(), var3.isLastObject());
      AccessibilityUtils.INSTANCE.removeClickAbilityAnnouncement(var6);
      AccessibilityUtils.INSTANCE.addHeadingAnnouncement(var6);
      return var6;
   }

   private void styleChatHeading(TextView var1, int var2) {
      var1.setTextSize(15.0F);
      var1.setTextColor(var2);
      var1.setLinkTextColor(var2);
      BlockUtils.setSmallLineSpacing(var1);
      BlockUtils.setDefaultMarginBottom(var1);
   }

   private void styleAnnouncementHeading(TextView var1, @ColorInt int var2, int var3, int var4) {
      var1.setTextSize(var3);
      var1.setTextColor(var2);
      var1.setLinkTextColor(var2);
      var1.setMovementMethod(new TrackingLinkMovementMethod());
      BlockUtils.setLargeLineSpacing(var1);
      BlockUtils.setMarginBottom(var1, var4);
   }

   private void styleCarouselHeading1(TextView var1, BlockMetadata var2) {
      this.applyGenericCarouselHeadingStyle(var1, var2);
      var1.setTextSize(48.0F);
      var1.setLineSpacing(ScreenUtils.dpToPx(8.0F, var1.getContext()), 1.0F);
   }

   private void styleCarouselHeading2(TextView var1, BlockMetadata var2) {
      this.applyGenericCarouselHeadingStyle(var1, var2);
      var1.setTextSize(34.0F);
      var1.setLineSpacing(ScreenUtils.dpToPx(6.0F, var1.getContext()), 1.0F);
   }

   @SuppressLint("WrongConstant")
   private void applyGenericCarouselHeadingStyle(TextView var1, BlockMetadata var2) {
      var1.setBreakStrategy(0);
      var1.setTypeface(null, 1);
      BlockUtils.setMarginBottom(var1, 16);
      String var3 = var2.getAppearance().getTextColor();
      if (!var3.isEmpty()) {
         int var4 = Color.parseColor(var3);
         var1.setTextColor(var4);
         var1.setLinkTextColor(var4);
      }
   }
}
