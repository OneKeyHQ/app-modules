package io.intercom.android.sdk.utilities;

import android.text.SpannableStringBuilder;
import java.util.Map;

class Phrase$KeyToken extends Phrase$Token {
   private final String key;
   private CharSequence value;

   Phrase$KeyToken(Phrase$Token var1, String var2) {
      super(var1);
      this.key = var2;
   }

   @Override
   void expand(SpannableStringBuilder var1, Map var2) {
      this.value = (CharSequence)var2.get(this.key);
      int var3 = this.getFormattedStart();
      int var4 = var3 + this.key.length() + 2;
      var1.replace(var3, var4, this.value);
   }

   @Override
   int getFormattedLength() {
      return this.value.length();
   }
}
