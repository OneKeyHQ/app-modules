package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.lib.interfaces.LinkListBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.Link;
import io.intercom.android.sdk.blocks.lib.models.LinkList;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class LinkListRenderer implements LinkListBlock {
   private final Api api;
   private final Provider appConfigProvider;
   private final String conversationId;

   LinkListRenderer(Api var1, Provider var2, String var3) {
      this.api = var1;
      this.appConfigProvider = var2;
      this.conversationId = var3;
   }

   @Override
   public View addLinkListBlock(LinkList var1, BlockMetadata var2, ViewGroup var3) {
      Context var4 = var3.getContext();
      LayoutInflater var5 = LayoutInflater.from(var4);
      FrameLayout var6 = (FrameLayout)var5.inflate(layout.intercom_card_list_block, var3, false);
      int var7 = ColorUtils.primaryOrDarkColor(var4, (AppConfig)this.appConfigProvider.get());
      LinearLayout var8 = (LinearLayout)var6.findViewById(id.card_links);
      var8.setShowDividers(2);
      int var9 = ContextCompat.getColor(var4, color.intercom_container_border);
      var8.setDividerDrawable(new ColorDrawable(var9));
      var8.setOrientation(1);

      for (java.lang.Object recoveredEntryElement_0_54 : var1.getLinks()) {
         Link var11 = (Link) recoveredEntryElement_0_54;
         this.addLinkToLayout(var8, var11, var7, var4, false);
      }

      Link var12 = var1.getFooterLink();
      if (!var12.getText().isEmpty()) {
         this.addLinkToLayout(var8, var12, var7, var4, true);
      }

      return var6;
   }

   private void addLinkToLayout(LinearLayout var1, Link var2, int var3, Context var4, boolean var5) {
      FrameLayout var6 = new FrameLayout(var4);
      var6.setLayoutParams(new LayoutParams(-1, -2));
      TextView var7 = new TextView(var4);
      LayoutParams var8 = new LayoutParams(-2, -1);
      if (var5) {
         var8.gravity = 17;
      }

      var7.setLayoutParams(var8);
      var7.setTextColor(var3);
      var7.setTextSize(16.0F);
      var7.setText(var2.getTitle());
      var7.setMaxLines(2);
      var7.setEllipsize(TruncateAt.END);
      var7.setPadding(ScreenUtils.dpToPx(24.0F, var4), ScreenUtils.dpToPx(16.0F, var4), ScreenUtils.dpToPx(16.0F, var4), ScreenUtils.dpToPx(16.0F, var4));
      if ("educate.help_center".equals(var2.getLinkType())) {
         Drawable var9 = ContextCompat.getDrawable(var4, drawable.intercom_open_help_center);
         var9.setColorFilter(var3, Mode.SRC_IN);
         var7.setCompoundDrawablesWithIntrinsicBounds(var9, null, null, null);
         var7.setCompoundDrawablePadding(ScreenUtils.dpToPx(8.0F, var4));
      }

      var6.addView(var7);
      var6.setOnClickListener(new LinkListRenderer$1(this, var2, var4));
      TypedValue var10 = new TypedValue();
      var4.getTheme().resolveAttribute(16843534, var10, true);
      var6.setBackgroundResource(var10.resourceId);
      var1.addView(var6);
   }

   // $VF: synthetic method
   static Api access$000(LinkListRenderer var0) {
      return var0.api;
   }
}
