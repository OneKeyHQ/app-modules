package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.widget.ImageView;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.ResizableImageView;

class Image {
   private final StyleType style;

   Image(StyleType var1) {
      this.style = var1;
   }

   protected StyleType getStyle() {
      return this.style;
   }

   protected void setImageViewBounds(int var1, int var2, ResizableImageView var3, Builder var4) {
      int var5 = this.getTotalViewPadding(var3.getContext());
      var3.setTotalViewPadding(var5);
      var3.setDisplayImageDimensions(var1, var2);
      Point var6 = var3.getImageDimens();
      if (var6.x > 0 && var6.y > 0) {
         var4.size(var6.x, var6.y);
      }
   }

   protected void setBackground(ImageView var1) {
      if (this.style == StyleType.CAROUSEL) {
         var1.setBackgroundResource(drawable.intercom_square_image_preview);
      } else {
         var1.setBackgroundResource(drawable.intercom_rounded_image_preview);
      }
   }

   private int getTotalViewPadding(Context var1) {
      Resources var2 = var1.getResources();
      switch (this.style) {
         case USER:
         case ADMIN:
         case PREVIEW:
            float var3 = var2.getDimension(dimen.intercom_avatar_size);
            float var4 = var2.getDimension(dimen.intercom_conversation_row_icon_spacer);
            float var5 = var2.getDimension(dimen.intercom_conversation_row_margin);
            float var6 = var2.getDimension(dimen.intercom_cell_horizontal_padding);
            return (int)(var3 + var4 + var5 + var6 * 3.0F);
         case POST: {
            float var12 = var2.getDimension(dimen.intercom_cell_content_padding);
            float var14 = var2.getDimension(dimen.intercom_post_cell_padding);
            return (int)(var12 + var14) * 2;
         }
         case ARTICLE:
         case NOTE: {
            float var11 = var2.getDimension(dimen.intercom_cell_content_padding);
            float var13 = var2.getDimension(dimen.intercom_note_cell_padding);
            float var15 = var2.getDimension(dimen.intercom_note_layout_margin);
            return (int)(var11 + var13 + var15) * 2;
         }
         case CONTAINER_CARD: {
            float var7 = var2.getDimension(dimen.intercom_cell_content_padding);
            float var8 = var2.getDimension(dimen.intercom_note_cell_padding);
            float var9 = var2.getDimension(dimen.intercom_note_layout_margin);
            return (int)(var7 + var8 + var9) * 2;
         }
         case CHAT_FULL:
            return this.calculateChatFullPadding(var1);
         case CAROUSEL:
            float var10 = var2.getDimension(dimen.intercom_carousel_padding);
            return (int)var10 * 2;
         default:
            return 0;
      }
   }

   private int calculateChatFullPadding(Context var1) {
      Resources var2 = var1.getResources();
      float var3 = var2.getDimension(dimen.intercom_chat_overlay_width);
      float var4 = var2.getDimension(dimen.intercom_chat_overlay_padding_right);
      float var5 = var2.getDimension(dimen.intercom_chat_overlay_text_margin_left);
      float var6 = var2.getDimension(dimen.intercom_chat_overlay_text_padding_left);
      float var7 = var2.getDimension(dimen.intercom_chat_overlay_text_padding_right);
      float var8 = var4 + var5 + var6 + var7;
      int var9 = ScreenUtils.getScreenDimensions(var1).x;
      return (int)(var9 - (var3 - var8));
   }
}
