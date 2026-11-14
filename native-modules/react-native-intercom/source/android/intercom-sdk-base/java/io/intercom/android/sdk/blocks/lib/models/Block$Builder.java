package io.intercom.android.sdk.blocks.lib.models;

import android.net.Uri;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Block$Builder {
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
   String language;
   @SerializedName("highlighted_html")
   String highlightedHtml;
   String url;
   String thumbnailUrl;
   String linkUrl;
   String trackingUrl;
   String fallbackUrl;
   String username;
   String provider;
   String id;
   String align;
   Integer width;
   Integer height;
   Long duration;
   String previewUrl;
   String attribution;
   List attachments;
   List items;
   Integer rating_index;
   Integer ratingIndex;
   String remark;
   List options;
   Boolean block_rating;
   List links;
   Block$Builder footerLink;
   String caption;
   List rows;
   Uri local_uri;
   String imageUrl;
   Integer imageWidth;
   Integer imageHeight;
   String ticket_type_title;
   TicketType ticket_type;
   long ticket_type_id;

   public Block$Builder withText(String var1) {
      this.text = var1;
      return this;
   }

   public Block$Builder withTitle(String var1) {
      this.title = var1;
      return this;
   }

   public Block$Builder withDescription(String var1) {
      this.description = var1;
      return this;
   }

   public Block$Builder withLinkType(String var1) {
      this.linkType = var1;
      return this;
   }

   public Block$Builder withSiteName(String var1) {
      this.siteName = var1;
      return this;
   }

   public Block$Builder withArticleId(String var1) {
      this.articleId = var1;
      return this;
   }

   public Block$Builder withAuthor(Author var1) {
      this.author = var1;
      return this;
   }

   public Block$Builder withImage(Image var1) {
      this.image = var1;
      return this;
   }

   public Block$Builder withData(Map var1) {
      this.data = var1;
      return this;
   }

   public Block$Builder withType(String var1) {
      this.type = var1;
      return this;
   }

   public Block$Builder withLanguage(String var1) {
      this.language = var1;
      return this;
   }

   public Block$Builder withHighlightedHtml(String var1) {
      this.highlightedHtml = var1;
      return this;
   }

   public Block$Builder withUrl(String var1) {
      this.url = var1;
      return this;
   }

   public Block$Builder withThumbnailUrl(String var1) {
      this.thumbnailUrl = var1;
      return this;
   }

   public Block$Builder withAlign(String var1) {
      this.align = var1;
      return this;
   }

   public Block$Builder withHeight(int var1) {
      this.height = var1;
      return this;
   }

   public Block$Builder withDuration(long var1) {
      this.duration = var1;
      return this;
   }

   public Block$Builder withWidth(int var1) {
      this.width = var1;
      return this;
   }

   public Block$Builder withItems(List var1) {
      this.items = var1;
      return this;
   }

   public Block$Builder withPreviewUrl(String var1) {
      this.previewUrl = var1;
      return this;
   }

   public Block$Builder withAttribution(String var1) {
      this.attribution = var1;
      return this;
   }

   public Block$Builder withAttachments(List var1) {
      this.attachments = var1;
      return this;
   }

   public Block$Builder withRatingIndex(Integer var1) {
      this.ratingIndex = var1;
      return this;
   }

   public Block$Builder withRemark(String var1) {
      this.remark = var1;
      return this;
   }

   public Block$Builder withOptions(List var1) {
      this.options = var1;
      return this;
   }

   public Block$Builder withBlockRating(Boolean var1) {
      this.block_rating = var1;
      return this;
   }

   public Block$Builder withLocalUri(Uri var1) {
      this.local_uri = var1;
      return this;
   }

   public Block$Builder withImageUrl(String var1) {
      this.imageUrl = var1;
      return this;
   }

   public Block$Builder withImageWidth(int var1) {
      this.imageWidth = var1;
      return this;
   }

   public Block$Builder withImageHeight(int var1) {
      this.imageHeight = var1;
      return this;
   }

   public Block$Builder withTicketTypeTitle(String var1) {
      this.ticket_type_title = var1;
      return this;
   }

   public Block$Builder withTicketType(TicketType var1) {
      this.ticket_type = var1;
      return this;
   }

   public Block$Builder withCaption(String var1) {
      this.caption = var1;
      return this;
   }

   public Block$Builder withRows(List var1) {
      this.rows = var1;
      return this;
   }

   public Block build() {
      return new Block(this, null);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         Block$Builder var2 = (Block$Builder)var1;
         if (this.type != null ? this.type.equals(var2.type) : var2.type == null) {
            if (this.text != null ? this.text.equals(var2.text) : var2.text == null) {
               if (this.title != null ? this.title.equals(var2.title) : var2.title == null) {
                  if (this.description != null ? this.description.equals(var2.description) : var2.description == null) {
                     if (this.linkType != null ? this.linkType.equals(var2.linkType) : var2.linkType == null) {
                        if (this.siteName != null ? this.siteName.equals(var2.siteName) : var2.siteName == null) {
                           if (this.articleId != null ? this.articleId.equals(var2.articleId) : var2.articleId == null) {
                              if (this.author != null ? this.author.equals(var2.author) : var2.author == null) {
                                 if (this.image != null ? this.image.equals(var2.image) : var2.image == null) {
                                    if (this.data != null ? this.data.equals(var2.data) : var2.data == null) {
                                       if (this.language != null ? this.language.equals(var2.language) : var2.language == null) {
                                          if (this.highlightedHtml != null ? this.highlightedHtml.equals(var2.highlightedHtml) : var2.highlightedHtml == null) {
                                             if (this.url != null ? this.url.equals(var2.url) : var2.url == null) {
                                                if (this.thumbnailUrl != null ? this.thumbnailUrl.equals(var2.thumbnailUrl) : var2.thumbnailUrl == null) {
                                                   if (this.linkUrl != null ? this.linkUrl.equals(var2.linkUrl) : var2.linkUrl == null) {
                                                      if (this.trackingUrl != null ? this.trackingUrl.equals(var2.trackingUrl) : var2.trackingUrl == null) {
                                                         if (this.fallbackUrl != null ? this.fallbackUrl.equals(var2.fallbackUrl) : var2.fallbackUrl == null) {
                                                            if (this.username != null ? this.username.equals(var2.username) : var2.username == null) {
                                                               if (this.provider != null ? this.provider.equals(var2.provider) : var2.provider == null) {
                                                                  if (this.id != null ? this.id.equals(var2.id) : var2.id == null) {
                                                                     if (this.align != null ? this.align.equals(var2.align) : var2.align == null) {
                                                                        if (this.width != null ? this.width.equals(var2.width) : var2.width == null) {
                                                                           if (this.height != null ? this.height.equals(var2.height) : var2.height == null) {
                                                                              if (this.duration != null
                                                                                 ? this.duration.equals(var2.duration)
                                                                                 : var2.duration == null) {
                                                                                 if (this.previewUrl != null
                                                                                    ? this.previewUrl.equals(var2.previewUrl)
                                                                                    : var2.previewUrl == null) {
                                                                                    if (this.attribution != null
                                                                                       ? this.attribution.equals(var2.attribution)
                                                                                       : var2.attribution == null) {
                                                                                       if (this.attachments != null
                                                                                          ? this.attachments.equals(var2.attachments)
                                                                                          : var2.attachments == null) {
                                                                                          if (this.items != null
                                                                                             ? this.items.equals(var2.items)
                                                                                             : var2.items == null) {
                                                                                             if (this.rating_index != null
                                                                                                ? this.rating_index.equals(var2.rating_index)
                                                                                                : var2.rating_index == null) {
                                                                                                if (this.ratingIndex != null
                                                                                                   ? this.ratingIndex.equals(var2.ratingIndex)
                                                                                                   : var2.ratingIndex == null) {
                                                                                                   if (this.remark != null
                                                                                                      ? this.remark.equals(var2.remark)
                                                                                                      : var2.remark == null) {
                                                                                                      if (this.options != null
                                                                                                         ? this.options.equals(var2.options)
                                                                                                         : var2.options == null) {
                                                                                                         if (this.block_rating != null
                                                                                                            ? this.block_rating.equals(var2.block_rating)
                                                                                                            : var2.block_rating == null) {
                                                                                                            if (this.links != null
                                                                                                               ? this.links.equals(var2.links)
                                                                                                               : var2.links == null) {
                                                                                                               if (this.caption != null
                                                                                                                  ? this.caption.equals(var2.caption)
                                                                                                                  : var2.caption == null) {
                                                                                                                  if (this.rows != null
                                                                                                                     ? this.rows.equals(var2.rows)
                                                                                                                     : var2.rows == null) {
                                                                                                                     if (this.imageUrl != null
                                                                                                                        ? this.imageUrl.equals(var2.imageUrl)
                                                                                                                        : var2.imageUrl == null) {
                                                                                                                        if (this.imageWidth != null
                                                                                                                           ? this.imageWidth
                                                                                                                              .equals(var2.imageWidth)
                                                                                                                           : var2.imageWidth == null) {
                                                                                                                           if (this.imageHeight != null
                                                                                                                              ? this.imageHeight
                                                                                                                                 .equals(var2.imageHeight)
                                                                                                                              : var2.imageHeight == null) {
                                                                                                                              if (this.ticket_type_title
                                                                                                                                    != null
                                                                                                                                 ? this.ticket_type_title
                                                                                                                                    .equals(
                                                                                                                                       var2.ticket_type_title
                                                                                                                                    )
                                                                                                                                 : var2.ticket_type_title
                                                                                                                                    == null) {
                                                                                                                                 if (this.ticket_type != null
                                                                                                                                    ? this.ticket_type
                                                                                                                                       .equals(var2.ticket_type)
                                                                                                                                    : var2.ticket_type == null) {
                                                                                                                                    if (!Objects.equals(
                                                                                                                                       this.ticket_type_id,
                                                                                                                                       var2.ticket_type_id
                                                                                                                                    )) {
                                                                                                                                       return false;
                                                                                                                                    } else {
                                                                                                                                       return this.footerLink
                                                                                                                                             != null
                                                                                                                                          ? this.footerLink
                                                                                                                                             .equals(
                                                                                                                                                var2.footerLink
                                                                                                                                             )
                                                                                                                                          : var2.footerLink
                                                                                                                                             == null;
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
      var1 = 31 * var1 + (this.siteName != null ? this.siteName.hashCode() : 0);
      var1 = 31 * var1 + (this.articleId != null ? this.articleId.hashCode() : 0);
      var1 = 31 * var1 + (this.author != null ? this.author.hashCode() : 0);
      var1 = 31 * var1 + (this.image != null ? this.image.hashCode() : 0);
      var1 = 31 * var1 + (this.data != null ? this.data.hashCode() : 0);
      var1 = 31 * var1 + (this.language != null ? this.language.hashCode() : 0);
      var1 = 31 * var1 + (this.highlightedHtml != null ? this.highlightedHtml.hashCode() : 0);
      var1 = 31 * var1 + (this.url != null ? this.url.hashCode() : 0);
      var1 = 31 * var1 + (this.thumbnailUrl != null ? this.thumbnailUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.linkUrl != null ? this.linkUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.trackingUrl != null ? this.trackingUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.fallbackUrl != null ? this.fallbackUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.username != null ? this.username.hashCode() : 0);
      var1 = 31 * var1 + (this.provider != null ? this.provider.hashCode() : 0);
      var1 = 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
      var1 = 31 * var1 + (this.align != null ? this.align.hashCode() : 0);
      var1 = 31 * var1 + (this.width != null ? this.width.hashCode() : 0);
      var1 = 31 * var1 + (this.height != null ? this.height.hashCode() : 0);
      var1 = 31 * var1 + (this.duration != null ? this.duration.hashCode() : 0);
      var1 = 31 * var1 + (this.previewUrl != null ? this.previewUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.attribution != null ? this.attribution.hashCode() : 0);
      var1 = 31 * var1 + (this.attachments != null ? this.attachments.hashCode() : 0);
      var1 = 31 * var1 + (this.items != null ? this.items.hashCode() : 0);
      var1 = 31 * var1 + (this.rating_index != null ? this.rating_index.hashCode() : 0);
      var1 = 31 * var1 + (this.ratingIndex != null ? this.ratingIndex.hashCode() : 0);
      var1 = 31 * var1 + (this.remark != null ? this.remark.hashCode() : 0);
      var1 = 31 * var1 + (this.options != null ? this.options.hashCode() : 0);
      var1 = 31 * var1 + (this.block_rating != null ? this.block_rating.hashCode() : 0);
      var1 = 31 * var1 + (this.links != null ? this.links.hashCode() : 0);
      var1 = 31 * var1 + (this.footerLink != null ? this.footerLink.hashCode() : 0);
      var1 = 31 * var1 + (this.caption != null ? this.caption.hashCode() : 0);
      var1 = 31 * var1 + (this.rows != null ? this.rows.hashCode() : 0);
      var1 = 31 * var1 + (this.imageUrl != null ? this.imageUrl.hashCode() : 0);
      var1 = 31 * var1 + (this.imageWidth != null ? this.imageWidth.hashCode() : 0);
      var1 = 31 * var1 + (this.imageHeight != null ? this.imageHeight.hashCode() : 0);
      var1 = 31 * var1 + (this.ticket_type_title != null ? this.ticket_type_title.hashCode() : 0);
      var1 = 31 * var1 + (this.ticket_type != null ? this.ticket_type.hashCode() : 0);
      return 31 * var1 + (int)(this.ticket_type_id ^ this.ticket_type_id >>> 32);
   }
}
