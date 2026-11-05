package io.intercom.android.sdk.models;

import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.blocks.lib.models.Author;
import java.util.List;

@AutoValue
public abstract class Link {
   public static final String ARTICLE_TYPE = "educate.article";
   public static final String SUGGESTION_TYPE = "educate.suggestion";
   public static final String HELP_CENTER_TYPE = "educate.help_center";

   public abstract String getId();

   public abstract Card getCard();

   public abstract List getBlocks();

   public abstract ReactionReply getReactionReply();

   public abstract long getCreatedAt();

   public abstract long getUpdatedAt();

   public String getTitle() {
      return this.getCard().getTitle();
   }

   public String getText() {
      return this.getCard().getText();
   }

   public String getType() {
      return this.getCard().getType();
   }

   public String getDescription() {
      return this.getCard().getDescription();
   }

   public Author getAuthor() {
      return this.getCard().getAuthor();
   }
}
