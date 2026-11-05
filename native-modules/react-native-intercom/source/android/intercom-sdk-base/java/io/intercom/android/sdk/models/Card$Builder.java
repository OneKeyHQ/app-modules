package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.blocks.lib.models.Author;
import io.intercom.android.sdk.utilities.NullSafety;

public final class Card$Builder {
   @Nullable
   String type;
   @Nullable
   String text;
   @Nullable
   String title;
   @Nullable
   String description;
   @Nullable
   Author author;
   @Nullable
   String linkType;
   @Nullable
   String articleId;

   public Card build() {
      Author var1 = this.author == null ? new Author() : this.author;
      return Card.create(
         NullSafety.valueOrEmpty(this.type),
         NullSafety.valueOrEmpty(this.text),
         NullSafety.valueOrEmpty(this.title),
         NullSafety.valueOrEmpty(this.description),
         var1
      );
   }
}
