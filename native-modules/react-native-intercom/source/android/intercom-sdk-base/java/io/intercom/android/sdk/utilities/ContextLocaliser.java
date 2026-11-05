package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.content.res.Configuration;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import java.util.Locale;

public class ContextLocaliser {
   private static final String SIMPLIFIED_CHINESE_ISO_15924 = "hans";
   private static final String SIMPLIFIED_CHINESE_COUNTRY = "CN";
   private static final String TRADITIONAL_CHINESE_ISO_15924 = "hant";
   private static final String TRADITIONAL_CHINESE_COUNTRY = "TW";
   private static final String FORMAL_TAG = "form";
   private static final String FAKE_FORMAL_COUNTRY = "FO";
   private static final Twig twig = LumberMill.getLogger();
   private final Provider appConfigProvider;

   public ContextLocaliser(Provider var1) {
      this.appConfigProvider = var1;
   }

   public Context createLocalisedContext(Context var1) {
      Configuration var2 = new Configuration(var1.getResources().getConfiguration());
      Locale var3 = convertToLocale(((AppConfig)this.appConfigProvider.get()).getLocale());
      var2.setLocale(var3);
      return var1.createConfigurationContext(var2);
   }

   public static Locale convertToLocale(String var0) {
      if (var0.length() <= 2) {
         return new Locale(var0);
      }

      String var1 = var0.substring(0, 2);
      String var2 = var0.substring(3);
      if ("hans".equalsIgnoreCase(var2)) {
         var2 = "CN";
      } else if ("hant".equalsIgnoreCase(var2)) {
         var2 = "TW";
      } else if ("form".equalsIgnoreCase(var2)) {
         var2 = "FO";
      }

      return new Locale(var1, var2);
   }
}
