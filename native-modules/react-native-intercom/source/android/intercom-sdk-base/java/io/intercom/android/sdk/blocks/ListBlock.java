package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.blocks.lib.interfaces.OrderedListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.UnorderedListBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.spans.OrderedListSpan;
import io.intercom.android.sdk.spans.UnorderedListSpan;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.TrackingLinkMovementMethod;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;
import java.util.List;

class ListBlock implements OrderedListBlock, UnorderedListBlock {
   private static final int LARGE_TEXT_SIZE_SP = 16;
   private static final int SMALL_TEXT_SIZE_SP = 14;
   private final StyleType style;
   private final Provider appConfigProvider;

   ListBlock(StyleType var1, Provider var2) {
      this.style = var1;
      this.appConfigProvider = var2;
   }

   @Override
   public View addOrderedList(List var1, BlockMetadata var2, ViewGroup var3) {
      Context var4 = var3.getContext();
      TextView var5 = this.getListStyledTextView(var4, var2);
      int var6 = (int)var3.getResources().getDimension(dimen.intercom_list_indentation);
      CharSequence var7 = "";
      int var8 = 0;

      for (int var9 = var1.size(); var8 < var9; var8++) {
         String var10 = (String)var1.get(var8);
         if (!var10.isEmpty()) {
            String var11 = var8 < var9 - 1 ? "<br />" : "";
            Spanned var12 = HtmlCompat.fromHtml(var10 + var11);
            SpannableString var13 = new SpannableString(var12);
            var13.setSpan(new OrderedListSpan(var6, var8 + 1 + "."), 0, var12.length(), 0);
            var7 = TextUtils.concat(var7, var13);
         }
      }

      var5.setText(var7);
      BlockUtils.setLayoutMarginsAndGravity(var5, 8388611, var2.isLastObject());
      return var5;
   }

   @Override
   public View addUnorderedList(List var1, BlockMetadata var2, ViewGroup var3) {
      Context var4 = var3.getContext();
      TextView var5 = this.getListStyledTextView(var4, var2);
      int var6 = (int)var3.getResources().getDimension(dimen.intercom_list_indentation);
      CharSequence var7 = "";
      int var8 = 0;

      for (int var9 = var1.size(); var8 < var9; var8++) {
         String var10 = (String)var1.get(var8);
         if (!var10.isEmpty()) {
            String var11 = var8 < var9 - 1 ? "<br />" : "";
            Spanned var12 = HtmlCompat.fromHtml(var10 + var11);
            SpannableString var13 = new SpannableString(var12);
            var13.setSpan(new UnorderedListSpan(var6, var4), 0, var12.length(), 0);
            var7 = TextUtils.concat(var7, var13);
         }
      }

      var5.setText(var7);
      BlockUtils.setLayoutMarginsAndGravity(var5, 8388611, var2.isLastObject());
      return var5;
   }

   private TextView getListStyledTextView(Context var1, BlockMetadata var2) {
      TextView var3 = new TextView(var1);
      var3.setLinkTextColor(((AppConfig)this.appConfigProvider.get()).getPrimaryColor());
      var3.setMovementMethod(new TrackingLinkMovementMethod());
      BlockUtils.createLayoutParams(var3, -2, -2);
      BlockUtils.setMarginLeft(var3, 4);
      switch (this.style) {
         case ADMIN: {
            int var10 = ContextCompat.getColor(var1, color.intercom_grey_800);
            this.styleAsChatList(var3, var10);
            break;
         }
         case ARTICLE:
         case NOTE:
         case CONTAINER_CARD: {
            int var9 = ContextCompat.getColor(var1, color.intercom_grey_700);
            this.styleAsAnnouncementList(var3, var9);
            break;
         }
         case POST: {
            int var8 = ContextCompat.getColor(var1, color.intercom_white);
            this.styleAsAnnouncementList(var3, var8);
            break;
         }
         case CHAT_FULL: {
            int var7 = ContextCompat.getColor(var1, color.intercom_grey_800);
            this.styleAsChatList(var3, var7);
            var3.setMovementMethod(null);
            break;
         }
         case CAROUSEL:
            var3.setTextSize(20.0F);
            String var5 = var2.getAppearance().getTextColor();
            if (!var5.isEmpty()) {
               int colorx = Color.parseColor(var5);
               var3.setTextColor(colorx);
               var3.setLinkTextColor(colorx);
            }
            break;
         default: {
            int var4 = ContextCompat.getColor(var1, color.intercom_white);
            this.styleAsChatList(var3, var4);
         }
      }

      return var3;
   }

   private void styleAsChatList(TextView var1, int var2) {
      var1.setTextSize(14.0F);
      var1.setTextColor(var2);
      BlockUtils.setDefaultMarginBottom(var1);
      BlockUtils.setSmallLineSpacing(var1);
   }

   private void styleAsAnnouncementList(TextView var1, int var2) {
      var1.setTextSize(16.0F);
      var1.setTextColor(var2);
      BlockUtils.setMarginBottom(var1, 16);
      BlockUtils.setLargeLineSpacing(var1);
   }
}
