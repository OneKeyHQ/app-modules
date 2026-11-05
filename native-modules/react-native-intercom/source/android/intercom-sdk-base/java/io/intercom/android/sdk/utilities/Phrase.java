package io.intercom.android.sdk.utilities;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.StringRes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Phrase {
   private final CharSequence pattern;
   private final Set keys = new HashSet();
   private final Map keysToValues = new HashMap();
   private CharSequence formatted;
   private Phrase$Token head;
   private char curChar;
   private int curCharIndex;
   private static final int EOF = 0;

   public static Phrase from(Fragment var0, @StringRes int var1) {
      return from(var0.getResources(), var1);
   }

   public static Phrase from(View var0, @StringRes int var1) {
      return from(var0.getResources(), var1);
   }

   public static Phrase from(Context var0, @StringRes int var1) {
      return from(var0.getResources(), var1);
   }

   public static Phrase from(Resources var0, @StringRes int var1) {
      return from(var0.getText(var1));
   }

   public static Phrase from(CharSequence var0) {
      return new Phrase(var0);
   }

   public Phrase put(String var1, CharSequence var2) {
      if (!this.keys.contains(var1)) {
         throw new IllegalArgumentException("Key '" + var1 + "' not found in '" + this.pattern + "'");
      }

      if (var2 == null) {
         throw new IllegalArgumentException("Null value for '" + var1 + "' in '" + this.pattern + "'");
      }

      this.keysToValues.put(var1, var2);
      this.formatted = null;
      return this;
   }

   public Phrase put(String var1, int var2) {
      return this.put(var1, Integer.toString(var2));
   }

   public Phrase putOptional(String var1, CharSequence var2) {
      return this.keys.contains(var1) ? this.put(var1, var2) : this;
   }

   public Phrase putOptional(String var1, int var2) {
      return this.keys.contains(var1) ? this.put(var1, var2) : this;
   }

   public CharSequence format() {
      if (this.formatted == null) {
         if (!this.keysToValues.keySet().containsAll(this.keys)) {
            Set var3 = new HashSet(this.keys);
            var3.removeAll(this.keysToValues.keySet());
            throw new IllegalArgumentException("Missing keys: " + var3 + " in '" + this.pattern + "'");
         }

         SpannableStringBuilder var1 = new SpannableStringBuilder(this.pattern);

         for (Phrase$Token var2 = this.head; var2 != null; var2 = var2.next) {
            var2.expand(var1, this.keysToValues);
         }

         this.formatted = var1;
      }

      return this.formatted;
   }

   public void into(TextView var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("TextView must not be null.");
      }

      var1.setText(this.format());
   }

   @Override
   public String toString() {
      return this.pattern.toString();
   }

   private Phrase(CharSequence var1) {
      this.curChar = var1.length() > 0 ? var1.charAt(0) : '\u0000';
      this.pattern = var1;
      Phrase$Token var2 = null;

      Phrase$Token var3;
      while ((var3 = this.token(var2)) != null) {
         if (this.head == null) {
            this.head = var3;
         }

         var2 = var3;
      }
   }

   private Phrase$Token token(Phrase$Token var1) {
      if (this.curChar == 0) {
         return null;
      }

      if (this.curChar == '{') {
         char var2 = this.lookahead();
         if (var2 == '{') {
            return this.leftCurlyBracket(var1);
         } else if (var2 >= 'a' && var2 <= 'z') {
            return this.key(var1);
         } else {
            throw new IllegalArgumentException("Unexpected character '" + var2 + "'; expected key in '" + this.pattern + "'");
         }
      } else {
         return this.text(var1);
      }
   }

   private Phrase$KeyToken key(Phrase$Token var1) {
      StringBuilder var2 = new StringBuilder();
      this.consume();

      while (
         this.curChar >= 'a' && this.curChar <= 'z'
            || this.curChar >= 'A' && this.curChar <= 'Z'
            || this.curChar == '_'
            || this.curChar >= '0' && this.curChar <= '9'
      ) {
         var2.append(this.curChar);
         this.consume();
      }

      if (this.curChar != '}') {
         throw new IllegalArgumentException("Missing closing brace: } in '" + this.pattern + "'");
      }

      this.consume();
      if (var2.length() == 0) {
         throw new IllegalArgumentException("Empty key: {} in '" + this.pattern + "'");
      }

      String var3 = var2.toString();
      this.keys.add(var3);
      return new Phrase$KeyToken(var1, var3);
   }

   private Phrase$TextToken text(Phrase$Token var1) {
      int var2 = this.curCharIndex;

      while (this.curChar != '{' && this.curChar != 0) {
         this.consume();
      }

      return new Phrase$TextToken(var1, this.curCharIndex - var2);
   }

   private Phrase$LeftCurlyBracketToken leftCurlyBracket(Phrase$Token var1) {
      this.consume();
      this.consume();
      return new Phrase$LeftCurlyBracketToken(var1);
   }

   private char lookahead() {
      return this.curCharIndex < this.pattern.length() - 1 ? this.pattern.charAt(this.curCharIndex + 1) : '\u0000';
   }

   private void consume() {
      this.curCharIndex++;
      this.curChar = this.curCharIndex == this.pattern.length() ? '\u0000' : this.pattern.charAt(this.curCharIndex);
   }
}
