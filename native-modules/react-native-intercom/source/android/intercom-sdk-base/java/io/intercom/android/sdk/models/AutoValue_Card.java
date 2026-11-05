package io.intercom.android.sdk.models;

import io.intercom.android.sdk.blocks.lib.models.Author;
import org.jspecify.annotations.Nullable;

final class AutoValue_Card extends Card {
   private final String type;
   private final String text;
   private final String title;
   private final String description;
   private final Author author;

   AutoValue_Card(String var1, String var2, String var3, String var4, Author var5) {
      if (var1 == null) {
         throw new NullPointerException("Null type");
      }

      this.type = var1;
      if (var2 == null) {
         throw new NullPointerException("Null text");
      }

      this.text = var2;
      if (var3 == null) {
         throw new NullPointerException("Null title");
      }

      this.title = var3;
      if (var4 == null) {
         throw new NullPointerException("Null description");
      }

      this.description = var4;
      if (var5 == null) {
         throw new NullPointerException("Null author");
      }

      this.author = var5;
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public String getTitle() {
      return this.title;
   }

   @Override
   public String getDescription() {
      return this.description;
   }

   @Override
   public Author getAuthor() {
      return this.author;
   }

   @Override
   public String toString() {
      return "Card{type=" + this.type + ", text=" + this.text + ", title=" + this.title + ", description=" + this.description + ", author=" + this.author + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Card)) {
         return false;
      }

      Card var2 = (Card)var1;
      return this.type.equals(var2.getType())
         && this.text.equals(var2.getText())
         && this.title.equals(var2.getTitle())
         && this.description.equals(var2.getDescription())
         && this.author.equals(var2.getAuthor());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.type.hashCode();
      var1 *= 1000003;
      var1 ^= this.text.hashCode();
      var1 *= 1000003;
      var1 ^= this.title.hashCode();
      var1 *= 1000003;
      var1 ^= this.description.hashCode();
      var1 *= 1000003;
      return var1 ^ this.author.hashCode();
   }
}
