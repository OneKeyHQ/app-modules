package io.intercom.android.sdk.models;

public class LinkResponse extends BaseResponse {
   private final Link link;

   LinkResponse(LinkResponse$Builder var1) {
      super(var1);
      this.link = var1.article == null ? new Link$Builder().build() : var1.article.build();
   }

   public Link getLink() {
      return this.link;
   }
}
