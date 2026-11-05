package io.intercom.android.sdk.blocks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.LinkBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.Link;
import io.intercom.android.sdk.blocks.views.ParagraphView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.utilities.AvatarUtils;
import io.intercom.android.sdk.utilities.BackgroundUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;

class LinkCard implements LinkBlock {
   private final StyleType style;
   private final Provider appConfigProvider;
   private final String conversationId;

   LinkCard(StyleType var1, Provider var2, String var3) {
      this.style = var1;
      this.appConfigProvider = var2;
      this.conversationId = var3;
   }

   @Override
   public View addLinkBlock(Link var1, BlockMetadata var2, ViewGroup var3) {
      if (this.shouldDisplayLink()) {
         return this.createLinkBlock(var1, var3, var2.isSingleObject(), var2.isLastObject());
      }

      TextView var4 = new ParagraphView(var3.getContext(), StyleType.ADMIN, BlockAlignment.LEFT, (AppConfig)this.appConfigProvider.get());
      var4.setText(HtmlCompat.fromHtml(var1.getText()));
      return var4;
   }

   private boolean shouldDisplayLink() {
      return StyleType.ADMIN.equals(this.style) || StyleType.CHAT_FULL.equals(this.style);
   }

   private View createLinkBlock(Link var1, ViewGroup var2, boolean var3, boolean var4) {
      AppConfig var5 = (AppConfig)this.appConfigProvider.get();
      Context var6 = var2.getContext();
      LayoutInflater var7 = LayoutInflater.from(var6);
      LinearLayout var8 = (LinearLayout)var7.inflate(layout.intercom_link_block, var2, false);
      TextView var9 = (TextView)var8.findViewById(id.title);
      ColorUtils.setTextColorPrimaryOrDark(var9, var5);
      var9.setText(var1.getTitle());
      this.setUpDescription(var1, (TextView)var8.findViewById(id.description));
      ImageView var10 = (ImageView)var8.findViewById(id.avatar);
      TextView var11 = (TextView)var8.findViewById(id.author);
      String var12 = var1.getAuthor().getFirstName();
      if (TextUtils.isEmpty(var12)) {
         var10.setVisibility(8);
         var11.setVisibility(8);
      } else {
         this.addAuthorDetails(var1, var10, var11, var12);
      }

      if (!var3) {
         if ("educate.article".equals(var1.getLinkType())) {
            var8.setOnClickListener(new LinkCard$1(this, var6, var1));
         }

         BackgroundUtils.setBackground(var8, ContextCompat.getDrawable(var6, drawable.intercom_conversation_card_background));
         BlockUtils.setMarginBottom(var8, 8);
         int var13 = var6.getResources().getDimensionPixelSize(dimen.intercom_link_padding);
         var8.setPadding(var13, var13, var13, var13);
         BlockUtils.setLayoutMarginsAndGravity(var8, 8388611, var4);
      }

      return var8;
   }

   private void setUpDescription(Link var1, TextView var2) {
      String var3 = var1.getDescription();
      if (TextUtils.isEmpty(var3)) {
         var2.setVisibility(8);
      } else {
         var2.setVisibility(0);
         var2.setText(var3);
      }
   }

   @SuppressLint("SetTextI18n")
   private void addAuthorDetails(Link var1, ImageView var2, TextView var3, String var4) {
      var2.setVisibility(0);
      var3.setVisibility(0);
      Spannable var5 = new SpannableString(var4);
      var5.setSpan(new StyleSpan(1), 0, var4.length(), 33);
      var3.setText("Written by " + var5);
      int var6 = var2.getResources().getDimensionPixelSize(dimen.intercom_avatar_size);
      Avatar var7 = Avatar.create(var1.getAuthor().getAvatar(), "");
      AvatarUtils.createAvatar(var7, var2, var6, (AppConfig)this.appConfigProvider.get());
   }
}
