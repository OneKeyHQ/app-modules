package io.intercom.android.sdk.utilities;

import android.text.SpannableStringBuilder;
import java.util.Map;

class Phrase$LeftCurlyBracketToken extends Phrase$Token {
   Phrase$LeftCurlyBracketToken(Phrase$Token var1) {
      super(var1);
   }

   @Override
   void expand(SpannableStringBuilder var1, Map var2) {
      int var3 = this.getFormattedStart();
      var1.replace(var3, var3 + 2, "{");
   }

   @Override
   int getFormattedLength() {
      return 1;
   }
}
