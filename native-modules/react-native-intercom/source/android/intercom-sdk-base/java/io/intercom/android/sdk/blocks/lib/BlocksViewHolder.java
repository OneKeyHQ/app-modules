package io.intercom.android.sdk.blocks.lib;

import io.intercom.android.sdk.blocks.lib.interfaces.AttachmentListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.ButtonBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.CodeBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.ConversationRatingBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.FacebookBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.HeadingBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.ImageBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.ImageTextBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.LinkBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.LinkListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.LocalAttachmentBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.LocalImageBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.MessengerCardBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.OrderedListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.ParagraphBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.SubheadingBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.TwitterBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.UnorderedListBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.VideoBlock;
import io.intercom.android.sdk.blocks.lib.interfaces.VideoFileBlock;

public class BlocksViewHolder {
   private int layout;
   private ParagraphBlock paragraph;
   private HeadingBlock heading;
   private SubheadingBlock subheading;
   private CodeBlock code;
   private LinkBlock link;
   private LinkListBlock linkList;
   private ConversationRatingBlock conversationRating;
   private MessengerCardBlock messengerCardBlock;
   private UnorderedListBlock unorderedList;
   private OrderedListBlock orderedList;
   private ImageBlock image;
   private ImageTextBlock imageText;
   private ButtonBlock button;
   private AttachmentListBlock attachmentList;
   private FacebookBlock facebookButton;
   private TwitterBlock twitterButton;
   private VideoBlock video;
   private VideoFileBlock videoFile;
   private LocalImageBlock localImage;
   private LocalAttachmentBlock localAttachment;

   public int getLayout() {
      return this.layout;
   }

   public void setLayout(int var1) {
      this.layout = var1;
   }

   public ParagraphBlock getParagraph() {
      return this.paragraph;
   }

   public void setParagraph(ParagraphBlock var1) {
      this.paragraph = var1;
   }

   public HeadingBlock getHeading() {
      return this.heading;
   }

   public void setHeading(HeadingBlock var1) {
      this.heading = var1;
   }

   public SubheadingBlock getSubheading() {
      return this.subheading;
   }

   public void setSubheading(SubheadingBlock var1) {
      this.subheading = var1;
   }

   public CodeBlock getCode() {
      return this.code;
   }

   public void setCode(CodeBlock var1) {
      this.code = var1;
   }

   public LinkBlock getLink() {
      return this.link;
   }

   public void setLink(LinkBlock var1) {
      this.link = var1;
   }

   public LinkListBlock getLinkList() {
      return this.linkList;
   }

   public void setLinkList(LinkListBlock var1) {
      this.linkList = var1;
   }

   public ConversationRatingBlock getConversationRating() {
      return this.conversationRating;
   }

   public void setConversationRating(ConversationRatingBlock var1) {
      this.conversationRating = var1;
   }

   public MessengerCardBlock getMessengerCardBlock() {
      return this.messengerCardBlock;
   }

   public void setMessengerCardBlock(MessengerCardBlock var1) {
      this.messengerCardBlock = var1;
   }

   public UnorderedListBlock getUnorderedList() {
      return this.unorderedList;
   }

   public void setUnorderedList(UnorderedListBlock var1) {
      this.unorderedList = var1;
   }

   public OrderedListBlock getOrderedList() {
      return this.orderedList;
   }

   public void setOrderedList(OrderedListBlock var1) {
      this.orderedList = var1;
   }

   public ImageBlock getImage() {
      return this.image;
   }

   public void setImage(ImageBlock var1) {
      this.image = var1;
   }

   public ImageTextBlock getImageText() {
      return this.imageText;
   }

   public void setImageText(ImageTextBlock var1) {
      this.imageText = var1;
   }

   public LocalImageBlock getLocalImage() {
      return this.localImage;
   }

   public void setLocalImage(LocalImageBlock var1) {
      this.localImage = var1;
   }

   public ButtonBlock getButton() {
      return this.button;
   }

   public void setButton(ButtonBlock var1) {
      this.button = var1;
   }

   public FacebookBlock getFacebookButton() {
      return this.facebookButton;
   }

   public void setFacebookButton(FacebookBlock var1) {
      this.facebookButton = var1;
   }

   public TwitterBlock getTwitterButton() {
      return this.twitterButton;
   }

   public void setTwitterButton(TwitterBlock var1) {
      this.twitterButton = var1;
   }

   public VideoBlock getVideo() {
      return this.video;
   }

   public void setVideo(VideoBlock var1) {
      this.video = var1;
   }

   public AttachmentListBlock getAttachmentList() {
      return this.attachmentList;
   }

   public void setAttachmentList(AttachmentListBlock var1) {
      this.attachmentList = var1;
   }

   public LocalAttachmentBlock getLocalAttachment() {
      return this.localAttachment;
   }

   public void setLocalAttachment(LocalAttachmentBlock var1) {
      this.localAttachment = var1;
   }

   public VideoFileBlock getVideoFile() {
      return this.videoFile;
   }

   public void setVideoFile(VideoFileBlock var1) {
      this.videoFile = var1;
   }
}
