package io.intercom.android.sdk.utilities;

import android.app.Application;
import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.api.ApiFactory;
import io.intercom.android.sdk.logger.LumberMill;

public class ValidatorUtil {
   private static final String API_PREFIX = "android_sdk-";
   private static final Twig twig = LumberMill.getLogger();

   public static boolean isValidConstructorParams(@Nullable Application var0, @Nullable String var1, @Nullable String var2) {
      boolean var3 = true;
      if (var0 == null) {
         twig.e("The Application passed in was null.We need an Application to enable Intercom in your app");
         var3 = false;
      }

      if (appIdIsInvalid(var2)) {
         twig.e("The app ID (" + var2 + ") you provided is either null or empty. We need a valid app ID to enable Intercom in your app");
         var3 = false;
      }

      if (apiKeyIsTooShort(var1)) {
         twig.e(
            "The API key provided ("
               + var1
               + ") is too short.\nPlease check that you are using an Intercom Android SDK key and have not passed the appId into the apiKey field\n"
         );
         var3 = false;
      }

      if (apiKeyIsMissingPrefix(var1)) {
         twig.e(
            "The API key provided ("
               + var1
               + ") does not begin with 'android_sdk-'.\nPlease check that you are using an Intercom Android SDK key and have not passed the appId into the apiKey field\n"
         );
         var3 = false;
      }

      return var3;
   }

   public static boolean isValidAppIdParams(@Nullable String var0, @Nullable String var1) {
      boolean var2 = true;
      if (appIdIsInvalid(var1)) {
         twig.e("The app ID (" + var1 + ") you provided is either null or empty. We need a valid app ID to enable Intercom in your app");
         var2 = false;
      }

      if (apiKeyIsTooShort(var0)) {
         twig.e(
            "The API key provided ("
               + var0
               + ") is too short.\nPlease check that you are using an Intercom Android SDK key and have not passed the appId into the apiKey field\n"
         );
         var2 = false;
      }

      if (apiKeyIsMissingPrefix(var0)) {
         twig.e(
            "The API key provided ("
               + var0
               + ") does not begin with 'android_sdk-'.\nPlease check that you are using an Intercom Android SDK key and have not passed the appId into the apiKey field\n"
         );
         var2 = false;
      }

      return var2;
   }

   private static boolean appIdIsInvalid(@Nullable String var0) {
      return var0 == null || ApiFactory.INSTANCE.removeInvalidCharacters(var0).isEmpty();
   }

   static boolean apiKeyIsTooShort(@Nullable String var0) {
      return var0 == null || var0.length() < 52;
   }

   static boolean apiKeyIsMissingPrefix(@Nullable String var0) {
      return var0 == null || !var0.startsWith("android_sdk-");
   }
}
