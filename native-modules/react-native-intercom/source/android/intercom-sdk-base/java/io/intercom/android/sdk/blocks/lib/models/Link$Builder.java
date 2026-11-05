package io.intercom.android.sdk.blocks.lib.models;

import java.util.Map;

public final class Link$Builder {
   String type;
   String text;
   String title;
   String description;
   String linkType;
   String siteName;
   String articleId;
   Author author;
   Image image;
   Map data;
   String url;

   public Link$Builder withText(String var1) {
      this.text = var1;
      return this;
   }

   public Link$Builder withTitle(String var1) {
      this.title = var1;
      return this;
   }

   public Link$Builder withDescription(String var1) {
      this.description = var1;
      return this;
   }

   public Link$Builder withLinkType(String var1) {
      this.linkType = var1;
      return this;
   }

   public Link$Builder withSiteName(String var1) {
      this.siteName = var1;
      return this;
   }

   public Link$Builder withArticleId(String var1) {
      this.articleId = var1;
      return this;
   }

   public Link$Builder withAuthor(Author var1) {
      this.author = var1;
      return this;
   }

   public Link$Builder withImage(Image var1) {
      this.image = var1;
      return this;
   }

   public Link$Builder withData(Map var1) {
      this.data = var1;
      return this;
   }

   public Link$Builder withType(String var1) {
      this.type = var1;
      return this;
   }

   public Link$Builder withUrl(String var1) {
      this.url = var1;
      return this;
   }

   public Link build() {
      return new Link(this, null);
   }
}
