package io.intercom.android.sdk.utilities;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import io.intercom.android.sdk.R.raw;
import io.intercom.android.sdk.models.CountryAreaCode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {
   private static final int PRIORITY_NO_CONFLICTING_CODES = 0;
   private static final int PRIORITY_CONFLICTS_WITH_NO_SUB_CODES = 1;
   private static final int PRIORITY_CONFLICTS_WITH_SUB_CODES = 2;
   private static final String NON_NUMBER_PATTERN = "\\D";
   private static final String WHITESPACE_PATTERN = "\\s+";
   private static final String PREFIX_PATTERN = "^(\\+)|(00)";
   private static final String SPECIAL_CHARACTERS = "^(\\()|(\\))|(-)";
   private static final int MIN_LENGTH_WITHOUT_PREFIX = 6;
   private static final int MAX_LENGTH_WITHOUT_PREFIX = 15;
   private static final List countryAreaCodes = new ArrayList();

   public static void loadCountryAreaCodes(Context var0) {
      if (countryAreaCodes.isEmpty()) {
         List var1 = parseCodes(var0);
         countryAreaCodes.addAll(var1);
      }
   }

   @VisibleForTesting
   static List parseCodes(Context var0) {
      String var1 = loadJsonFromAsset(var0);
      CountryAreaCode[] var2 = (CountryAreaCode[])new Gson().fromJson(var1, CountryAreaCode[].class);
      List var3 = new ArrayList();
      if (var2 != null) {
         var3 = Arrays.asList(var2);
      }

      return var3;
   }

   private static String loadJsonFromAsset(Context var0) {
      String var1 = "";

      try {
         InputStream var2 = var0.getResources().openRawResource(raw.intercom_area_codes);
         int var3 = var2.available();
         byte[] var4 = new byte[var3];
         var2.read(var4);
         var2.close();
         var1 = new String(var4, "UTF-8");
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return var1;
   }

   static int validatePhoneNumber(String var0) {
      String var1 = normalizeNumber(var0);
      if (!containsPrefix(var1)) {
         return 4;
      } else {
         var1 = stripPrefix(var1);
         if (Pattern.compile("\\D").matcher(var1).find()) {
            return 3;
         } else {
            var1 = var1.replaceAll("\\D", "");
            if (var1.length() > 15) {
               return 5;
            } else {
               CountryAreaCode var2 = getCountryAreaCodeFromNumber(var1);
               int var3 = var2.getDialCode().length();
               if (var2.equals(CountryAreaCode.UNKNOWN)) {
                  return 6;
               } else {
                  return var1.substring(var3).length() < 6 ? 7 : 0;
               }
            }
         }
      }
   }

   public static String normalizeNumber(String var0) {
      return var0.replaceAll("\\s+", "").replaceAll("^(\\()|(\\))|(-)", "");
   }

   @VisibleForTesting
   static boolean containsPrefix(String var0) {
      Matcher var1 = Pattern.compile("^(\\+)|(00)").matcher(var0);
      return !var1.find() ? false : var1.start() == 0;
   }

   public static String stripPrefix(String var0) {
      return var0.replaceFirst("^(\\+)|(00)", "");
   }

   public static CountryAreaCode getCountryAreaCodeFromNumber(String var0) {
      CountryAreaCode var1 = CountryAreaCode.UNKNOWN;

      for (java.lang.Object recoveredEntryElement_0_105 : countryAreaCodes) {
         CountryAreaCode var3 = (CountryAreaCode) recoveredEntryElement_0_105;
         String var4 = var3.getDialCode();
         if (var0.startsWith(var4)) {
            int var5 = var3.getPriority();
            if (var5 == 0) {
               return var3;
            }

            if (var5 == 1) {
               var1 = var3;
            } else if (var5 == 2) {
               String var6 = var0.substring(var3.getDialCode().length());

               for (java.lang.Object recoveredEntryElement_0_118 : var3.getAreaCodes()) {
                  String var8 = (String) recoveredEntryElement_0_118;
                  if (var6.startsWith(var8)) {
                     return var3;
                  }
               }
            }
         }
      }

      return var1;
   }

   public static CountryAreaCode getCountryAreaCodeFromLocale(String var0) {
      for (java.lang.Object recoveredEntryElement_0_131 : countryAreaCodes) {
         CountryAreaCode var2 = (CountryAreaCode) recoveredEntryElement_0_131;
         String var3 = var2.getCode();
         if (var0.equalsIgnoreCase(var3)) {
            return var2;
         }
      }

      return CountryAreaCode.UNKNOWN;
   }
}
