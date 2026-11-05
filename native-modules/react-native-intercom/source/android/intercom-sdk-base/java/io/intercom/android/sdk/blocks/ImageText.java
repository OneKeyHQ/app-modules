package io.intercom.android.sdk.blocks;

import android.graphics.Color;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.ImageTextBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.utilities.FontUtils;

public class ImageText implements ImageTextBlock {
   private final StyleType style;
   private final UploadingImageCache uploadingImageCache;

   ImageText(StyleType var1, UploadingImageCache var2) {
      this.style = var1;
      this.uploadingImageCache = var2;
   }

   @Override
   public View addImageText(Spanned var1, Spanned var2, String var3, String var4, int var5, int var6, BlockMetadata var7, ViewGroup var8) {
      LayoutInflater var9 = LayoutInflater.from(var8.getContext());
      LinearLayout var10 = (LinearLayout)var9.inflate(layout.intercom_image_text_block, var8, false);
      View var11 = new NetworkImage(this.style, this.uploadingImageCache, null).addImage(var3, "", var4, var5, var6, BlockAlignment.LEFT, var7, var10);
      ((LayoutParams)var11.getLayoutParams()).setMargins(0, 0, 0, 0);
      FrameLayout var12 = (FrameLayout)var10.findViewById(id.image_holder);
      var12.addView(var11);
      TextView var13 = (TextView)var10.findViewById(id.title);
      TextView var14 = (TextView)var10.findViewById(id.text);
      var13.setText(var1);
      var14.setText(var2);
      FontUtils.setRobotoMediumTypeface(var13);
      String var15 = var7.getAppearance().getTextColor();
      if (!TextUtils.isEmpty(var15)) {
         int var16 = Color.parseColor(var15);
         var13.setTextColor(var16);
         var14.setTextColor(var16);
      }

      return var10;
   }
}
