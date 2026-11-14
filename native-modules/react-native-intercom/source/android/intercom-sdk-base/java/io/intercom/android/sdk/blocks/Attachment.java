package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.blocks.lib.interfaces.AttachmentListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.LocalAttachmentBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.AttachmentView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.ProgressLinearLayout;
import io.intercom.android.sdk.views.UploadProgressBar;
import java.util.List;

class Attachment implements AttachmentListBlock, LocalAttachmentBlock {
   private static final int ATTACHMENT_LINE_SPACING_DP = 12;
   private static final int LOCAL_ICON_SIZE_DP = 20;
   private static final int DIVIDER_MARGIN_DP = 6;
   private static final int DIVIDER_WIDTH_DP = 1;
   private final StyleType style;
   private final Provider appConfigProvider;

   public Attachment(StyleType var1, Provider var2) {
      this.style = var1;
      this.appConfigProvider = var2;
   }

   @Override
   public View addAttachmentList(List var1, BlockMetadata var2, ViewGroup var3) {
      Context var4 = var3.getContext();
      LinearLayout var5 = this.getAttachmentListView(var4);
      int var6 = ((AppConfig)this.appConfigProvider.get()).getPrimaryColor();
      int var7 = ContextCompat.getColor(var4, color.intercom_white);
      int var8 = ContextCompat.getColor(var4, color.intercom_accessibility_black);
      int var9 = ColorUtils.isComparedColorsLowerThanAccessibilityContrastRatio(var6, var7) ? var8 : var6;
      int var10 = 0;

      for (int var11 = var1.size(); var10 < var11; var10++) {
         LinearLayout var12 = new AttachmentView(var4, this.style, var9, (BlockAttachment)var1.get(var10));
         if (var10 < var11 - 1) {
            var12.setPadding(var12.getPaddingLeft(), var12.getPaddingTop(), var12.getPaddingRight(), ScreenUtils.dpToPx(12.0F, var4));
         }

         var5.addView(var12);
      }

      BlockUtils.setLayoutMarginsAndGravity(var5, 8388611, var2.isLastObject());
      return var5;
   }

   private LinearLayout getAttachmentListView(Context var1) {
      LinearLayout var2 = new LinearLayout(var1);
      var2.setOrientation(1);
      var2.setBackgroundResource(color.intercom_full_transparent_full_black);
      BlockUtils.createLayoutParams(var2, -2, -2);
      BlockUtils.setDefaultMarginBottom(var2);
      return var2;
   }

   @Override
   public View addAttachment(BlockAttachment var1, BlockMetadata var2, ViewGroup var3) {
      Context var4 = var3.getContext();
      ProgressLinearLayout var5 = this.getLocalAttachmentView(var4);
      FrameLayout var6 = this.getIconHolder(var4);
      ImageView var7 = this.getAttachmentIcon(var4);
      UploadProgressBar var8 = this.getUploadProgressBar(var4);
      View var9 = this.getDivider(var4);
      TextView var10 = this.getTextView(var1.getName(), var4);
      var6.addView(var8);
      var6.addView(var7);
      var5.setUploadProgressBar(var8);
      var5.setAttachmentIcon(var7);
      var5.addView(var10);
      BlockUtils.setLayoutMarginsAndGravity(var5, 8388611, var2.isLastObject());
      return var5;
   }

   private ProgressLinearLayout getLocalAttachmentView(Context var1) {
      ProgressLinearLayout var2 = new ProgressLinearLayout(var1);
      BlockUtils.createLayoutParams(var2, -2, -2);
      BlockUtils.setDefaultMarginBottom(var2);
      var2.setOrientation(0);
      return var2;
   }

   private FrameLayout getIconHolder(Context var1) {
      int var2 = ScreenUtils.dpToPx(20.0F, var1);
      FrameLayout var3 = new FrameLayout(var1);
      BlockUtils.createLayoutParams(var3, var2, var2);
      LayoutParams var4 = (LayoutParams)var3.getLayoutParams();
      var4.gravity = 8388627;
      return var3;
   }

   private UploadProgressBar getUploadProgressBar(Context var1) {
      UploadProgressBar var2 = new UploadProgressBar(var1);
      android.widget.FrameLayout.LayoutParams var3 = new android.widget.FrameLayout.LayoutParams(-1, -1);
      var2.setLayoutParams(var3);
      return var2;
   }

   private ImageView getAttachmentIcon(Context var1) {
      ImageView var2 = new ImageView(var1);
      android.widget.FrameLayout.LayoutParams var3 = new android.widget.FrameLayout.LayoutParams(-1, -1);
      var2.setLayoutParams(var3);
      var2.setVisibility(8);
      var2.setImageResource(drawable.intercom_icn_attachment);
      return var2;
   }

   private View getDivider(Context var1) {
      int var2 = ScreenUtils.dpToPx(6.0F, var1);
      int var3 = ScreenUtils.dpToPx(1.0F, var1);
      View var4 = new View(var1);
      var4.setBackgroundResource(color.intercom_white);
      BlockUtils.createLayoutParams(var4, var3, -1);
      LayoutParams var5 = (LayoutParams)var4.getLayoutParams();
      var5.leftMargin = var2;
      var5.rightMargin = var2;
      return var4;
   }

   private TextView getTextView(String var1, Context var2) {
      TextView var3 = new TextView(var2);
      BlockUtils.createLayoutParams(var3, -2, -2);
      BlockUtils.setSmallLineSpacing(var3);
      var3.setGravity(8388627);
      var3.setEllipsize(TruncateAt.END);
      var3.setTextSize(15.0F);
      var3.setTextColor(ContextCompat.getColor(var2, color.intercom_white));
      var3.setBackgroundResource(color.intercom_full_transparent_full_black);
      var3.setText(var1);
      return var3;
   }
}
