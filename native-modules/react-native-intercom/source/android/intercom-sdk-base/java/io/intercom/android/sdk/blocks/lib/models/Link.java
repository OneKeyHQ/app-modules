package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.blocks.lib.BlockType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Link implements Parcelable {
   private final BlockType type;
   private final String text;
   private final String title;
   private final String description;
   private final String linkType;
   private final Author author;
   private final Image image;
   private final Map data;
   private final String siteName;
   private final String articleId;
   private final String url;
   public static final Creator CREATOR = new Link$1();

   Link() {
      this(new Link$Builder());
   }

   public static Link fromBlock(Block var0) {
      if (var0 == null) {
         return new Link();
      }

      Link$Builder var1 = new Link$Builder();
      var1.type = var0.getType().name();
      var1.text = var0.getText();
      var1.title = var0.getTitle();
      var1.description = var0.getDescription();
      var1.linkType = var0.getLinkType();
      var1.author = var0.getAuthor();
      var1.image = var0.getImage();
      var1.data = var0.getData();
      var1.siteName = var0.getSiteName();
      var1.articleId = var0.getArticleId();
      var1.url = var0.getUrl();
      return new Link(var1);
   }

   private Link(Link$Builder var1) {
      this.type = BlockType.typeValueOf(var1.type);
      this.text = var1.text == null ? "" : var1.text;
      this.title = var1.title == null ? "" : var1.title;
      this.description = var1.description == null ? "" : var1.description;
      this.linkType = var1.linkType == null ? "" : var1.linkType;
      this.siteName = var1.siteName == null ? "" : var1.siteName;
      this.articleId = var1.articleId == null ? "" : var1.articleId;
      this.author = var1.author == null ? new Author() : var1.author;
      this.image = var1.image == null ? new Image() : var1.image;
      this.data = var1.data == null ? new HashMap() : var1.data;
      this.url = var1.url == null ? "" : var1.url;
   }

   public BlockType getType() {
      return this.type;
   }

   public String getText() {
      return this.text;
   }

   public String getTitle() {
      return this.title;
   }

   public String getDescription() {
      return this.description;
   }

   public String getLinkType() {
      return this.linkType;
   }

   public String getSiteName() {
      return this.siteName;
   }

   public String getArticleId() {
      return this.articleId;
   }

   public String getUrl() {
      return this.url;
   }

   public Author getAuthor() {
      return this.author;
   }

   public Image getImage() {
      return this.image;
   }

   public Map getData() {
      return this.data;
   }

   @Override
   public String toString() {
      return "LinkCard{type="
         + this.type
         + ", text='"
         + this.text
         + '\''
         + ", title='"
         + this.title
         + '\''
         + ", description='"
         + this.description
         + '\''
         + ", linkType='"
         + this.linkType
         + '\''
         + ", author="
         + this.author
         + ", image="
         + this.image
         + ", data="
         + this.data
         + ", siteName='"
         + this.siteName
         + '\''
         + ", articleId='"
         + this.articleId
         + '\''
         + ", url='"
         + this.url
         + '\''
         + '}';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         Link var2 = (Link)var1;
         if (this.type != var2.type) {
            return false;
         }

         if (this.text != null ? this.text.equals(var2.text) : var2.text == null) {
            if (this.title != null ? this.title.equals(var2.title) : var2.title == null) {
               if (this.description != null ? this.description.equals(var2.description) : var2.description == null) {
                  if (this.linkType != null ? this.linkType.equals(var2.linkType) : var2.linkType == null) {
                     if (this.author != null ? this.author.equals(var2.author) : var2.author == null) {
                        if (this.image != null ? this.image.equals(var2.image) : var2.image == null) {
                           if (this.data != null ? this.data.equals(var2.data) : var2.data == null) {
                              if (this.siteName != null ? this.siteName.equals(var2.siteName) : var2.siteName == null) {
                                 if (this.articleId != null ? this.articleId.equals(var2.articleId) : var2.articleId == null) {
                                    return this.url != null ? this.url.equals(var2.url) : var2.url == null;
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type != null ? this.type.hashCode() : 0;
      var1 = 31 * var1 + (this.text != null ? this.text.hashCode() : 0);
      var1 = 31 * var1 + (this.title != null ? this.title.hashCode() : 0);
      var1 = 31 * var1 + (this.description != null ? this.description.hashCode() : 0);
      var1 = 31 * var1 + (this.linkType != null ? this.linkType.hashCode() : 0);
      var1 = 31 * var1 + (this.author != null ? this.author.hashCode() : 0);
      var1 = 31 * var1 + (this.image != null ? this.image.hashCode() : 0);
      var1 = 31 * var1 + (this.data != null ? this.data.hashCode() : 0);
      var1 = 31 * var1 + (this.siteName != null ? this.siteName.hashCode() : 0);
      var1 = 31 * var1 + (this.articleId != null ? this.articleId.hashCode() : 0);
      return 31 * var1 + (this.url != null ? this.url.hashCode() : 0);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   protected Link(Parcel var1) {
      this.type = BlockType.typeValueOf(var1.readString());
      this.text = var1.readString();
      this.title = var1.readString();
      this.description = var1.readString();
      this.linkType = var1.readString();
      this.siteName = var1.readString();
      this.articleId = var1.readString();
      this.url = var1.readString();
      this.author = (Author)var1.readParcelable(Author.class.getClassLoader());
      this.image = (Image)var1.readParcelable(Image.class.getClassLoader());
      this.data = new HashMap();
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = var1.readString();
         String var5 = var1.readString();
         this.data.put(var4, var5);
      }
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.type.name());
      var1.writeString(this.text);
      var1.writeString(this.title);
      var1.writeString(this.description);
      var1.writeString(this.linkType);
      var1.writeString(this.siteName);
      var1.writeString(this.articleId);
      var1.writeString(this.url);
      var1.writeParcelable(this.author, var2);
      var1.writeParcelable(this.image, var2);
      var1.writeInt(this.data.size());

      for (java.lang.Object recoveredEntryElement_0_250 : this.data.entrySet()) {
         Entry var4 = (Entry) recoveredEntryElement_0_250;
         var1.writeString((String)var4.getKey());
         var1.writeString((String)var4.getValue());
      }
   }

   // $VF: synthetic method
   Link(Link$Builder var1, Link$1 var2) {
      this(var1);
   }
}
