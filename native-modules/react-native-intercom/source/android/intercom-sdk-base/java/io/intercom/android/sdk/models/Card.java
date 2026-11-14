package io.intercom.android.sdk.models;

import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.blocks.lib.models.Author;

@AutoValue
public abstract class Card {
   public static Card create(String var0, String var1, String var2, String var3, Author var4) {
      return new AutoValue_Card(var0, var1, var2, var3, var4);
   }

   public abstract String getType();

   public abstract String getText();

   public abstract String getTitle();

   public abstract String getDescription();

   public abstract Author getAuthor();
}
