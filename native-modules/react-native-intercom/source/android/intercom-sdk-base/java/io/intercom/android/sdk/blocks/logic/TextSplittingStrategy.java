package io.intercom.android.sdk.blocks.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TextSplittingStrategy {
   private static final String NEW_PARAGRAPH_DELIMETER = "\n\n";
   private static final String NEW_LINE = "\n";
   private static final String NEW_LINE_REPLACEMENT = "<br>";

   public List apply(String var1) {
      String[] var2 = var1.split("\n\n");

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var2[var3].replace("\n", "<br>");
      }

      List var4 = new ArrayList(Arrays.asList(var2));
      var4.removeAll(Collections.singleton(""));
      return var4;
   }
}
