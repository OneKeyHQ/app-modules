package io.intercom.android.sdk.utilities;

import android.text.SpannableStringBuilder;
import java.util.Map;

class Phrase$TextToken extends Phrase$Token {
   private final int textLength;

   Phrase$TextToken(Phrase$Token var1, int var2) {
      super(var1);
      this.textLength = var2;
   }

   @Override
   void expand(SpannableStringBuilder var1, Map var2) {
   }

   @Override
   int getFormattedLength() {
      return this.textLength;
   }
}
