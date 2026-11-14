package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Collections;
import java.util.List;

public class LinkList implements Parcelable {
   private final List links;
   private final Link footerLink;
   public static final Creator CREATOR = new LinkList$1();

   public LinkList(List var1, Link var2) {
      this.links = var1;
      this.footerLink = var2;
   }

   public static LinkList fromBlock(Block var0) {
      return var0 == null ? new LinkList(Collections.emptyList(), new Link$Builder().build()) : new LinkList(var0.getLinks(), var0.getFooterLink());
   }

   public List getLinks() {
      return this.links;
   }

   public Link getFooterLink() {
      return this.footerLink;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LinkList var2 = (LinkList)var1;
         return !this.links.equals(var2.links) ? false : this.footerLink.equals(var2.footerLink);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.links.hashCode();
      return 31 * var1 + this.footerLink.hashCode();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeList(this.links);
      var1.writeParcelable(this.footerLink, var2);
   }
}
