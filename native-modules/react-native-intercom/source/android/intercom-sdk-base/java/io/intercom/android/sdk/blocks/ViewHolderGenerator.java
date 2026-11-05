package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.widget.LinearLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.Blocks;
import io.intercom.android.sdk.blocks.lib.BlocksViewHolder;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.messengercard.MessengerCard;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Attachments;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.List;

public class ViewHolderGenerator {
   private final UploadingImageCache uploadingImageCache;
   private final Api api;
   private final Provider appConfigProvider;
   private final String conversationId;
   @Nullable
   private final ImageClickListener imageListener;
   @Nullable
   private final ButtonClickListener buttonClickListener;
   private final Gson gson;
   private final MetricTracker metricTracker;

   public ViewHolderGenerator(
      UploadingImageCache var1,
      Api var2,
      Provider var3,
      String var4,
      @Nullable ImageClickListener var5,
      @Nullable ButtonClickListener var6,
      Gson var7,
      MetricTracker var8
   ) {
      this.uploadingImageCache = var1;
      this.api = var2;
      this.appConfigProvider = var3;
      this.conversationId = var4;
      this.imageListener = var5;
      this.buttonClickListener = var6;
      this.gson = var7;
      this.metricTracker = var8;
   }

   public BlocksViewHolder getAdminHolder() {
      return this.generateHolder(layout.intercom_blocks_admin_layout, StyleType.ADMIN);
   }

   public BlocksViewHolder getUserHolder() {
      return this.generateHolder(layout.intercom_blocks_user_layout, StyleType.USER);
   }

   public BlocksViewHolder getPreviewHolder() {
      return this.generateHolder(layout.intercom_blocks_user_layout, StyleType.PREVIEW);
   }

   public BlocksViewHolder getPostHolder() {
      return this.generateHolder(layout.intercom_blocks_container_layout, StyleType.POST);
   }

   public BlocksViewHolder getNoteHolder() {
      return this.generateHolder(layout.intercom_blocks_note_layout, StyleType.NOTE);
   }

   public BlocksViewHolder getArticleHolder() {
      return this.generateHolder(layout.intercom_blocks_article_layout, StyleType.ARTICLE);
   }

   public BlocksViewHolder getContainerCardHolder() {
      return this.generateHolder(layout.intercom_blocks_container_card_layout, StyleType.CONTAINER_CARD);
   }

   public BlocksViewHolder getChatFullHolder() {
      return this.generateHolder(layout.intercom_blocks_admin_layout, StyleType.CHAT_FULL);
   }

   public BlocksViewHolder getLinkHolder() {
      return this.generateHolder(layout.intercom_blocks_container_layout, StyleType.CHAT_FULL);
   }

   public BlocksViewHolder getConversationRatingHolder() {
      return this.generateHolder(layout.intercom_blocks_container_layout, StyleType.CHAT_FULL);
   }

   public BlocksViewHolder getLinkListHolder() {
      return this.generateHolder(layout.intercom_blocks_container_card_layout, StyleType.CHAT_FULL);
   }

   public BlocksViewHolder getCarouselHolder() {
      return this.generateHolder(layout.intercom_blocks_carousel_layout, StyleType.CAROUSEL);
   }

   private BlocksViewHolder generateHolder(@LayoutRes int var1, StyleType var2) {
      BlocksViewHolder var3 = new BlocksViewHolder();
      Button var4 = new Button(var2, this.appConfigProvider, this.buttonClickListener);
      Heading var5 = new Heading(var2, this.appConfigProvider);
      ListBlock var6 = new ListBlock(var2, this.appConfigProvider);
      Attachment var7 = new Attachment(var2, this.appConfigProvider);
      var3.setLayout(var1);
      var3.setParagraph(new Paragraph(var2, this.appConfigProvider));
      var3.setHeading(var5);
      var3.setSubheading(var5);
      var3.setCode(new Code());
      var3.setUnorderedList(var6);
      var3.setOrderedList(var6);
      var3.setImage(new NetworkImage(var2, this.uploadingImageCache, this.imageListener));
      var3.setLocalImage(new LocalImage(var2));
      var3.setButton(var4);
      var3.setAttachmentList(var7);
      var3.setLocalAttachment(var7);
      var3.setTwitterButton(var4);
      var3.setFacebookButton(var4);
      var3.setVideo(new Video(var2, this.api));
      var3.setVideoFile(new VideoFile(var2, this.appConfigProvider, this.metricTracker));
      var3.setLink(new LinkCard(var2, this.appConfigProvider, this.conversationId));
      var3.setConversationRating(new ConversationRatingCard(this.api, this.conversationId, this.appConfigProvider));
      var3.setLinkList(new LinkListRenderer(this.api, this.appConfigProvider, this.conversationId));
      var3.setImageText(new ImageText(var2, this.uploadingImageCache));
      if (!var2.equals(StyleType.CHAT_FULL)) {
         var3.setMessengerCardBlock(new MessengerCard(this.appConfigProvider, this.gson, this.metricTracker, this.conversationId));
      }

      return var3;
   }

   public static LinearLayout createPartsLayout(BlocksViewHolder var0, Part var1, Context var2) {
      List var3 = var1.getBlocks();
      if (!var1.getAttachments().isEmpty()) {
         List var4 = new ArrayList();

         for (java.lang.Object recoveredEntryElement_0_144 : var1.getAttachments()) {
            Attachments var6 = (Attachments) recoveredEntryElement_0_144;
            var4.add(BlockAttachment.create(var6.getName(), var6.getUrl()));
         }

         Block var8 = new Block$Builder().withType(BlockType.ATTACHMENTLIST.name()).withAttachments(var4).build();
         var3.add(var8);
      }

      Blocks var7 = new Blocks(var2, LumberMill.getBlocksTwig());
      return var7.createBlocks(var3, var0);
   }

   public static LinearLayout createLayoutFromBlocks(BlocksViewHolder var0, List var1, Context var2) {
      Blocks var3 = new Blocks(var2, LumberMill.getBlocksTwig());
      return var3.createBlocks(var1, var0);
   }
}
