package io.intercom.android.sdk.blocks.lib.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.BlockType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Block implements Parcelable {
   private final List attachments;
   private final List items;
   private final Map data;
   private final BlockType type;
   private final BlockAlignment align;
   private final Author author;
   private final Image image;
   private final String text;
   private final String title;
   private final String description;
   private final String linkType;
   private final String siteName;
   private final String articleId;
   private final String language;
   private final String highlightedHtml;
   private final String url;
   private final String thumbnailUrl;
   private final String previewUrl;
   private final String attribution;
   private final String linkUrl;
   private final String trackingUrl;
   private final String fallbackUrl;
   private final String username;
   private final String provider;
   private final String id;
   private final int width;
   private final int height;
   private final long duration;
   private final int ratingIndex;
   private final String remark;
   private final List options;
   private final boolean blockRating;
   private final List links;
   private final Link footerLink;
   private final String caption;
   private final List rows;
   private final Uri local_uri;
   private final String imageUrl;
   private final int imageWidth;
   private final int imageHeight;
   private final String ticket_type_title;
   private final TicketType ticket_type;
   private long ticketTypeId;
   public static final Creator CREATOR = new Block$1();

   Block() {
      this(new Block$Builder());
   }

   private Block(Block$Builder var1) {
      this.type = BlockType.typeValueOf(var1.type);
      this.text = var1.text == null ? "" : var1.text;
      this.title = var1.title == null ? "" : var1.title;
      this.description = var1.description == null ? "" : var1.description;
      this.linkType = var1.linkType == null ? "" : var1.linkType;
      this.siteName = var1.siteName == null ? "" : var1.siteName;
      this.articleId = var1.articleId == null ? "" : var1.articleId;
      this.author = var1.author == null ? new Author() : var1.author;
      this.image = var1.image == null ? new Image() : var1.image;
      this.data = var1.data == null ? Collections.emptyMap() : var1.data;
      this.language = var1.language == null ? "" : var1.language;
      this.highlightedHtml = var1.highlightedHtml == null ? "" : var1.highlightedHtml;
      this.url = var1.url == null ? "" : var1.url;
      this.thumbnailUrl = var1.thumbnailUrl == null ? "" : var1.thumbnailUrl;
      this.linkUrl = var1.linkUrl == null ? "" : var1.linkUrl;
      this.trackingUrl = var1.trackingUrl == null ? "" : var1.trackingUrl;
      this.fallbackUrl = var1.fallbackUrl == null ? "" : var1.fallbackUrl;
      this.username = var1.username == null ? "" : var1.username;
      this.provider = var1.provider == null ? "" : var1.provider;
      this.id = var1.id == null ? "" : var1.id;
      this.align = BlockAlignment.alignValueOf(var1.align);
      this.width = var1.width == null ? 0 : var1.width;
      this.height = var1.height == null ? 0 : var1.height;
      this.duration = var1.duration == null ? 0L : var1.duration;
      this.previewUrl = var1.previewUrl == null ? "" : var1.previewUrl;
      this.attribution = var1.attribution == null ? "" : var1.attribution;
      this.local_uri = var1.local_uri == null ? Uri.EMPTY : var1.local_uri;
      this.imageUrl = var1.imageUrl == null ? "" : var1.imageUrl;
      this.imageWidth = var1.imageWidth == null ? 0 : var1.imageWidth;
      this.imageHeight = var1.imageHeight == null ? 0 : var1.imageHeight;
      this.attachments = new ArrayList();
      if (var1.attachments != null) {
         for (java.lang.Object recoveredEntryElement_0_99 : var1.attachments) {
            BlockAttachment var3 = (BlockAttachment) recoveredEntryElement_0_99;
            if (var3 != null) {
               this.attachments.add(var3);
            }
         }
      }

      this.items = new ArrayList();
      if (var1.items != null) {
         for (java.lang.Object recoveredEntryElement_0_108 : var1.items) {
            String var8 = (String) recoveredEntryElement_0_108;
            if (var8 != null) {
               this.items.add(var8);
            }
         }
      }

      if (var1.ratingIndex != null) {
         this.ratingIndex = var1.ratingIndex;
      } else if (var1.rating_index != null) {
         this.ratingIndex = var1.rating_index;
      } else {
         this.ratingIndex = -1;
      }

      this.remark = var1.remark == null ? "" : var1.remark;
      this.options = new ArrayList();
      if (var1.options != null) {
         for (java.lang.Object recoveredEntryElement_0_126 : var1.options) {
            ConversationRatingOption var9 = (ConversationRatingOption) recoveredEntryElement_0_126;
            if (var9 != null) {
               this.options.add(var9);
            }
         }
      }

      this.blockRating = var1.block_rating != null && var1.block_rating;
      this.links = new ArrayList();
      if (var1.links != null) {
         for (java.lang.Object recoveredEntryElement_0_136 : var1.links) {
            Block$Builder var10 = (Block$Builder) recoveredEntryElement_0_136;
            if (var10 != null) {
               this.links.add(Link.fromBlock(var10.build()));
            }
         }
      }

      this.footerLink = var1.footerLink == null ? new Link() : Link.fromBlock(var1.footerLink.build());
      this.caption = var1.caption == null ? "" : var1.caption;
      this.rows = new ArrayList();
      if (var1.rows != null) {
         for (java.lang.Object recoveredEntryElement_0_147 : var1.rows) {
            TableRow$Builder var11 = (TableRow$Builder) recoveredEntryElement_0_147;
            if (var11 != null) {
               this.rows.add(var11.build());
            }
         }
      }

      this.ticket_type_title = var1.ticket_type_title == null ? "" : var1.ticket_type_title;
      this.ticket_type = var1.ticket_type == null ? TicketType.Companion.getNULL() : var1.ticket_type;
      this.ticketTypeId = var1.ticket_type_id;
   }

   public long getTicketTypeId() {
      return this.ticketTypeId;
   }

   public Block$Builder toBuilder() {
      Block$Builder var1 = new Block$Builder();
      switch (this.type) {
         case PARAGRAPH:
            var1.withText(this.text).withType(this.type.getSerializedName());
            break;
         case LOCALIMAGE:
         case IMAGE:
            var1.withType(this.type.getSerializedName())
               .withUrl(this.url)
               .withLocalUri(this.local_uri)
               .withAttribution(this.attribution.isEmpty() ? null : this.attribution)
               .withPreviewUrl(this.previewUrl.isEmpty() ? null : this.previewUrl)
               .withHeight(this.height)
               .withWidth(this.width);
            break;
         case LOCAL_ATTACHMENT:
            var1.withType(this.type.getSerializedName()).withAttachments(this.attachments);
      }

      return var1;
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

   public Author getAuthor() {
      return this.author;
   }

   public Image getImage() {
      return this.image;
   }

   public Map getData() {
      return this.data;
   }

   public String getLanguage() {
      return this.language;
   }

   public String getHighlightedHtml() {
      return this.highlightedHtml;
   }

   public String getUrl() {
      return this.url;
   }

   public String getThumbnailUrl() {
      return this.thumbnailUrl;
   }

   public String getLinkUrl() {
      return this.linkUrl;
   }

   public String getTrackingUrl() {
      return this.trackingUrl;
   }

   public String getFallbackUrl() {
      return this.fallbackUrl;
   }

   public String getUsername() {
      return this.username;
   }

   public String getProvider() {
      return this.provider;
   }

   public String getId() {
      return this.id;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public String getPreviewUrl() {
      return this.previewUrl;
   }

   public String getAttribution() {
      return this.attribution;
   }

   public List getItems() {
      return this.items;
   }

   public List getAttachments() {
      return this.attachments;
   }

   public BlockAlignment getAlign() {
      return this.align;
   }

   public int getRatingIndex() {
      return this.ratingIndex;
   }

   public String getRemark() {
      return this.remark;
   }

   public List getOptions() {
      return this.options;
   }

   public boolean isBlockRating() {
      return this.blockRating;
   }

   public List getLinks() {
      return this.links;
   }

   public Link getFooterLink() {
      return this.footerLink;
   }

   public String getCaption() {
      return this.caption;
   }

   public List getRows() {
      return this.rows;
   }

   public Uri getLocalUri() {
      return this.local_uri;
   }

   public String getImageUrl() {
      return this.imageUrl;
   }

   public int getImageWidth() {
      return this.imageWidth;
   }

   public int getImageHeight() {
      return this.imageHeight;
   }

   public String getTicketTypeTitle() {
      return this.ticket_type_title;
   }

   public TicketType getTicketType() {
      return this.ticket_type;
   }

   @Override
   public String toString() {
      return "Block{attachments="
         + this.attachments
         + ", items="
         + this.items
         + ", data="
         + this.data
         + ", type="
         + this.type
         + ", align="
         + this.align
         + ", author="
         + this.author
         + ", image="
         + this.image
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
         + ", siteName='"
         + this.siteName
         + '\''
         + ", articleId='"
         + this.articleId
         + '\''
         + ", language='"
         + this.language
         + '\''
         + ", highlightedHtml='"
         + this.highlightedHtml
         + '\''
         + ", url='"
         + this.url
         + '\''
         + ", thumbnailUrl='"
         + this.thumbnailUrl
         + '\''
         + ", previewUrl='"
         + this.previewUrl
         + '\''
         + ", attribution='"
         + this.attribution
         + '\''
         + ", linkUrl='"
         + this.linkUrl
         + '\''
         + ", trackingUrl='"
         + this.trackingUrl
         + '\''
         + ", fallbackUrl='"
         + this.fallbackUrl
         + '\''
         + ", username='"
         + this.username
         + '\''
         + ", provider='"
         + this.provider
         + '\''
         + ", id='"
         + this.id
         + '\''
         + ", width="
         + this.width
         + ", height="
         + this.height
         + ", ratingIndex="
         + this.ratingIndex
         + ", remark='"
         + this.remark
         + '\''
         + ", options="
         + this.options
         + ", blockRating="
         + this.blockRating
         + ", links="
         + this.links
         + ", footerLink="
         + this.footerLink
         + ", caption='"
         + this.caption
         + '\''
         + ", rows="
         + this.rows
         + ", imageUrl="
         + this.imageUrl
         + ", imageWidth="
         + this.imageWidth
         + ", imageHeight="
         + this.imageHeight
         + ", ticket_type_title="
         + this.ticket_type_title
         + ", ticket_type="
         + this.ticket_type
         + '}';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         Block var2 = (Block)var1;
         if (this.width != var2.width) {
            return false;
         }

         if (this.height != var2.height) {
            return false;
         }

         if (this.ratingIndex != var2.ratingIndex) {
            return false;
         }

         if (this.attachments != null ? this.attachments.equals(var2.attachments) : var2.attachments == null) {
            if (this.items != null ? this.items.equals(var2.items) : var2.items == null) {
               if (this.data != null ? this.data.equals(var2.data) : var2.data == null) {
                  if (this.type != var2.type) {
                     return false;
                  }

                  if (this.align != var2.align) {
                     return false;
                  }

                  if (this.author != null ? this.author.equals(var2.author) : var2.author == null) {
                     if (this.image != null ? this.image.equals(var2.image) : var2.image == null) {
                        if (this.text != null ? this.text.equals(var2.text) : var2.text == null) {
                           if (this.title != null ? this.title.equals(var2.title) : var2.title == null) {
                              if (this.description != null ? this.description.equals(var2.description) : var2.description == null) {
                                 if (this.linkType != null ? this.linkType.equals(var2.linkType) : var2.linkType == null) {
                                    if (this.siteName != null ? this.siteName.equals(var2.siteName) : var2.siteName == null) {
                                       if (this.articleId != null ? this.articleId.equals(var2.articleId) : var2.articleId == null) {
                                          if (this.language != null ? this.language.equals(var2.language) : var2.language == null) {
                                             if (this.highlightedHtml != null
                                                ? this.highlightedHtml.equals(var2.highlightedHtml)
                                                : var2.highlightedHtml == null) {
                                                if (this.url != null ? this.url.equals(var2.url) : var2.url == null) {
                                                   if (this.thumbnailUrl != null ? this.thumbnailUrl.equals(var2.thumbnailUrl) : var2.thumbnailUrl == null) {
                                                      if (this.previewUrl != null ? this.previewUrl.equals(var2.previewUrl) : var2.previewUrl == null) {
                                                         if (this.attribution != null ? this.attribution.equals(var2.attribution) : var2.attribution == null) {
                                                            if (this.linkUrl != null ? this.linkUrl.equals(var2.linkUrl) : var2.linkUrl == null) {
                                                               if (this.trackingUrl != null
                                                                  ? this.trackingUrl.equals(var2.trackingUrl)
                                                                  : var2.trackingUrl == null) {
                                                                  if (this.fallbackUrl != null
                                                                     ? this.fallbackUrl.equals(var2.fallbackUrl)
                                                                     : var2.fallbackUrl == null) {
                                                                     if (this.username != null ? this.username.equals(var2.username) : var2.username == null) {
                                                                        if (this.provider != null ? this.provider.equals(var2.provider) : var2.provider == null
                                                                           )
                                                                         {
                                                                           if (this.id != null ? this.id.equals(var2.id) : var2.id == null) {
                                                                              if (this.remark != null ? this.remark.equals(var2.remark) : var2.remark == null) {
                                                                                 if (this.options != null
                                                                                    ? this.options.equals(var2.options)
                                                                                    : var2.options == null) {
                                                                                    if (this.blockRating != var2.blockRating) {
                                                                                       return false;
                                                                                    }

                                                                                    if (this.links != null ? this.links.equals(var2.links) : var2.links == null
                                                                                       )
                                                                                     {
                                                                                       if (this.caption != null
                                                                                          ? this.caption.equals(var2.caption)
                                                                                          : var2.caption == null) {
                                                                                          if (this.rows != null
                                                                                             ? this.rows.equals(var2.rows)
                                                                                             : var2.rows == null) {
                                                                                             if (this.local_uri != null
                                                                                                ? this.local_uri.equals(var2.local_uri)
                                                                                                : var2.local_uri == null) {
                                                                                                if (this.imageUrl != null
                                                                                                   ? this.imageUrl.equals(var2.imageUrl)
                                                                                                   : var2.imageUrl == null) {
                                                                                                   if (this.imageWidth != var2.imageWidth) {
                                                                                                      return false;
                                                                                                   }

                                                                                                   if (this.imageHeight != var2.imageHeight) {
                                                                                                      return false;
                                                                                                   }

                                                                                                   if (this.ticket_type_title != null
                                                                                                      ? this.ticket_type_title.equals(var2.ticket_type_title)
                                                                                                      : var2.ticket_type_title == null) {
                                                                                                      if (this.ticket_type != null
                                                                                                         ? this.ticket_type.equals(var2.ticket_type)
                                                                                                         : var2.ticket_type == null) {
                                                                                                         return this.footerLink != null
                                                                                                            ? this.footerLink.equals(var2.footerLink)
                                                                                                            : var2.footerLink == null;
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
      int var1 = this.attachments != null ? this.attachments.hashCode() : 0;
      var1 = 31 * var1 + (this.items != null ? this.items.hashCode() : 0);
      var1 = 31 * var1 + (this.data != null ? this.data.hashCode() : 0);
      var1 = 31 * var1 + (this.type != null ? this.type.hashCode() : 0);
      var1 = 31 * var1 + (this.align != null ? this.align.hashCode() : 0);
      var1 = 31 * var1 + (this.author != null ? this.author.hashCode() : 0);
      var1 = 31 * var1 + (this.image != null ? this.image.hashCode() : 0);
      var1 = 31 * var1 + (this.text != null ? this.text.hashCode() : 0);
      var1 = 31 * var1 + (this.title != null ? this.title.hashCode() : 0);
      var1 = 31 * var1 + (this.description != null ? this.description.hashCode() : 0);
      var1 = 31 * var1 + (this.linkType != null ? this.linkType.hashCode() : 0);
      var1 = 31 * var1 + (this.siteName != null ? this.siteName.hashCode() : 0);
      var1 = 31 * var1 + (this.articleId != null ? this.articleId.hashCode() : 0);
      var1 = 31 * var1 + (this.language != null ? this.language.hashCode() : 0);
      var1 = 31 * var1 + (this.highlightedHtml != null ? this.highlightedHtml.hashCode() : 0);
      var1 = 31 * var1 + (this.url != null ? this.url.hashCode() : 0);
      var1 = 31 * var1 + (this.thumbnailUrl != null ? this.thumbnailUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.previewUrl != null ? this.previewUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.attribution != null ? this.attribution.hashCode() : 0);
      var1 = 31 * var1 + (this.linkUrl != null ? this.linkUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.trackingUrl != null ? this.trackingUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.fallbackUrl != null ? this.fallbackUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.username != null ? this.username.hashCode() : 0);
      var1 = 31 * var1 + (this.provider != null ? this.provider.hashCode() : 0);
      var1 = 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
      var1 = 31 * var1 + this.width;
      var1 = 31 * var1 + this.height;
      var1 = 31 * var1 + this.ratingIndex;
      var1 = 31 * var1 + (this.remark != null ? this.remark.hashCode() : 0);
      var1 = 31 * var1 + (this.options != null ? this.options.hashCode() : 0);
      var1 = 31 * var1 + (this.blockRating ? 1 : 0);
      var1 = 31 * var1 + (this.links != null ? this.links.hashCode() : 0);
      var1 = 31 * var1 + (this.footerLink != null ? this.footerLink.hashCode() : 0);
      var1 = 31 * var1 + (this.caption != null ? this.caption.hashCode() : 0);
      var1 = 31 * var1 + (this.rows != null ? this.rows.hashCode() : 0);
      var1 = 31 * var1 + (this.local_uri != null ? this.local_uri.hashCode() : 0);
      var1 = 31 * var1 + (this.imageUrl != null ? this.imageUrl.hashCode() : 0);
      var1 = 31 * var1 + this.imageWidth;
      var1 = 31 * var1 + this.imageHeight;
      var1 = 31 * var1 + (this.ticket_type_title != null ? this.ticket_type_title.hashCode() : 0);
      return 31 * var1 + (this.ticket_type != null ? this.ticket_type.hashCode() : 0);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeTypedList(this.attachments);
      var1.writeStringList(this.items);
      var1.writeInt(this.data.size());

      for (java.lang.Object recoveredEntryElement_0_711 : this.data.entrySet()) {
         Entry var4 = (Entry) recoveredEntryElement_0_711;
         var1.writeString((String)var4.getKey());
         var1.writeString((String)var4.getValue());
      }

      var1.writeInt(this.type == null ? -1 : this.type.ordinal());
      var1.writeInt(this.align == null ? -1 : this.align.ordinal());
      var1.writeParcelable(this.author, var2);
      var1.writeParcelable(this.image, var2);
      var1.writeString(this.text);
      var1.writeString(this.title);
      var1.writeString(this.description);
      var1.writeString(this.linkType);
      var1.writeString(this.siteName);
      var1.writeString(this.articleId);
      var1.writeString(this.language);
      var1.writeString(this.highlightedHtml);
      var1.writeString(this.url);
      var1.writeString(this.thumbnailUrl);
      var1.writeString(this.linkUrl);
      var1.writeString(this.trackingUrl);
      var1.writeString(this.fallbackUrl);
      var1.writeString(this.username);
      var1.writeString(this.provider);
      var1.writeString(this.previewUrl);
      var1.writeString(this.attribution);
      var1.writeString(this.id);
      var1.writeInt(this.width);
      var1.writeInt(this.height);
      var1.writeLong(this.duration);
      var1.writeInt(this.ratingIndex);
      var1.writeString(this.remark);
      var1.writeTypedList(this.options);
      var1.writeByte((byte)(this.blockRating ? 1 : 0));
      var1.writeTypedList(this.links);
      var1.writeParcelable(this.footerLink, var2);
      var1.writeString(this.caption);
      var1.writeTypedList(this.rows);
      var1.writeParcelable(this.local_uri, var2);
      var1.writeString(this.imageUrl);
      var1.writeInt(this.imageWidth);
      var1.writeInt(this.imageHeight);
      var1.writeString(this.ticket_type_title);
      var1.writeParcelable(this.ticket_type, var2);
   }

   protected Block(Parcel var1) {
      this.attachments = var1.createTypedArrayList(BlockAttachment.CREATOR);
      this.items = var1.createStringArrayList();
      int var2 = var1.readInt();
      this.data = new HashMap(var2);

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = var1.readString();
         String var5 = var1.readString();
         this.data.put(var4, var5);
      }

      int var6 = var1.readInt();
      this.type = var6 == -1 ? null : BlockType.values()[var6];
      int var7 = var1.readInt();
      this.align = var7 == -1 ? null : BlockAlignment.values()[var7];
      this.author = (Author)var1.readParcelable(Author.class.getClassLoader());
      this.image = (Image)var1.readParcelable(Image.class.getClassLoader());
      this.text = var1.readString();
      this.title = var1.readString();
      this.description = var1.readString();
      this.linkType = var1.readString();
      this.siteName = var1.readString();
      this.articleId = var1.readString();
      this.language = var1.readString();
      this.highlightedHtml = var1.readString();
      this.url = var1.readString();
      this.thumbnailUrl = var1.readString();
      this.linkUrl = var1.readString();
      this.trackingUrl = var1.readString();
      this.fallbackUrl = var1.readString();
      this.username = var1.readString();
      this.provider = var1.readString();
      this.previewUrl = var1.readString();
      this.attribution = var1.readString();
      this.id = var1.readString();
      this.width = var1.readInt();
      this.height = var1.readInt();
      this.duration = var1.readLong();
      this.ratingIndex = var1.readInt();
      this.remark = var1.readString();
      this.options = new ArrayList();
      var1.readList(this.options, ConversationRatingOption.class.getClassLoader());
      this.blockRating = var1.readByte() != 0;
      this.links = new ArrayList();
      var1.readList(this.links, Link.class.getClassLoader());
      this.footerLink = (Link)var1.readParcelable(Link.class.getClassLoader());
      this.caption = var1.readString();
      this.rows = var1.createTypedArrayList(TableRow.CREATOR);
      this.local_uri = (Uri)var1.readParcelable(Uri.class.getClassLoader());
      this.imageUrl = var1.readString();
      this.imageWidth = var1.readInt();
      this.imageHeight = var1.readInt();
      this.ticket_type_title = var1.readString();
      this.ticket_type = (TicketType)var1.readParcelable(TicketType.class.getClassLoader());
   }

   // $VF: synthetic method
   Block(Block$Builder var1, Block$1 var2) {
      this(var1);
   }
}
