package io.intercom.android.sdk.utilities;

import android.text.SpannableStringBuilder;
import java.util.Map;

abstract class Phrase$Token {
   private final Phrase$Token prev;
   Phrase$Token next;

   protected Phrase$Token(Phrase$Token var1) {
      this.prev = var1;
      if (var1 != null) {
         var1.next = this;
      }
   }

   abstract void expand(SpannableStringBuilder var1, Map var2);

   abstract int getFormattedLength();

   final int getFormattedStart() {
      return this.prev == null ? 0 : this.prev.getFormattedStart() + this.prev.getFormattedLength();
   }
}
