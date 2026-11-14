package io.intercom.android.sdk.blocks.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.blocks.StyleType;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.utilities.IntentUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

@SuppressLint("ViewConstructor")
public class AttachmentView extends LinearLayout implements OnClickListener {
   static final int ATTACHMENT_ICON_WIDTH_DP = 20;
   static final int DIVIDER_MARGIN_DP = 6;
   static final int DIVIDER_WIDTH_DP = 1;
   private final String url;

   public AttachmentView(Context var1, StyleType var2, @ColorInt int var3, BlockAttachment var4) {
      super(var1);
      this.url = var4.getUrl();
      this.setOrientation(0);
      ImageView var5 = this.setupIcon(var1, var2, var3);
      this.addView(var5);
      if (this.isUserMessage(var2)) {
         View var6 = this.setupDivider(var1);
         this.addView(var6);
      }

      TextView var7 = this.setupName(var1, var2, var3, var4.getName());
      this.addView(var7);
   }

   private ImageView setupIcon(Context var1, StyleType var2, int var3) {
      ImageView var4 = new ImageView(var1);
      int var5 = ScreenUtils.dpToPx(20.0F, var1);
      LinearLayout.LayoutParams var6 = new LinearLayout.LayoutParams(var5, -2);
      var6.gravity = 8388627;
      var4.setLayoutParams(var6);
      var4.setImageResource(drawable.intercom_icn_attachment);
      if (!this.isUserMessage(var2)) {
         var4.setColorFilter(var3);
      }

      return var4;
   }

   private View setupDivider(Context var1) {
      View var2 = new View(var1);
      int var3 = ScreenUtils.dpToPx(1.0F, var1);
      int var4 = ScreenUtils.dpToPx(6.0F, var1);
      LinearLayout.LayoutParams var5 = new LinearLayout.LayoutParams(var3, -1);
      var5.setMargins(var4, 0, var4, 0);
      var2.setLayoutParams(var5);
      var2.setBackgroundResource(color.intercom_white);
      return var2;
   }

   private TextView setupName(Context var1, StyleType var2, int var3, String var4) {
      TextView var5 = new TextView(var1);
      LinearLayout.LayoutParams var6 = new LinearLayout.LayoutParams(-2, -2);
      var6.gravity = 8388627;
      var5.setLayoutParams(var6);
      var5.setEllipsize(TruncateAt.END);
      var5.setBackgroundResource(color.intercom_full_transparent_full_black);
      var5.setGravity(8388627);
      var5.setTextSize(15.0F);
      var5.setText(var4);
      var5.setOnClickListener(this);
      if (this.isUserMessage(var2)) {
         var5.setTextColor(ContextCompat.getColor(var1, color.intercom_white));
      } else {
         var5.setTextColor(var3);
      }

      return var5;
   }

   @Override
   public void onClick(View var1) {
      Uri var2 = Uri.parse(this.url);
      if (!Uri.EMPTY.equals(var2)) {
         Intent var3 = new Intent("android.intent.action.VIEW", var2);
         var3.setFlags(268435456);
         IntentUtils.safelyOpenIntent(this.getContext(), var3);
      }
   }

   private boolean isUserMessage(StyleType var1) {
      return StyleType.PREVIEW == var1 || StyleType.USER == var1;
   }
}
