package io.intercom.android.sdk.utilities;

import java.util.regex.Pattern;

public class AttributeValidatorUtils {
   private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])",
      2
   );
   private static final Pattern VALID_WEBSITE_REGEX = Pattern.compile("^(https?://)?(www\\.)?([\\w]+\\.)+[\u200c\u200b\\w]{2,63}/?$");
   private static final Pattern VALID_NUMBER_REGEX = Pattern.compile("^[-+]?\\d+$");
   private static final Pattern VALID_FLOAT_REGEX = Pattern.compile("^[-+]?\\d*\\.?\\d*$");

   public static int validateAttribute(String var0, String var1) {
      switch (var1) {
         case "email":
            return VALID_EMAIL_ADDRESS_REGEX.matcher(var0).matches() ? 0 : 2;
         case "company.website":
            return VALID_WEBSITE_REGEX.matcher(var0).matches() ? 0 : 1;
         case "number":
            return VALID_NUMBER_REGEX.matcher(var0).matches() ? 0 : 3;
         case "float":
            return VALID_FLOAT_REGEX.matcher(var0).matches() ? 0 : 3;
         case "phone":
            return PhoneNumberValidator.validatePhoneNumber(var0);
         case "integer":
            try {
               Integer.parseInt(var0);
               return 0;
            } catch (NumberFormatException var5) {
               return 3;
            }
         default:
            return 0;
      }
   }
}
