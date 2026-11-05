package io.intercom.android.sdk.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.activities.IntercomCarouselActivity;
import io.intercom.android.sdk.activities.IntercomNoteActivity;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.helpcenter.utils.HelpCenterUrlUtilsKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.IntercomRootActivity;
import io.intercom.android.sdk.post.IntercomPostActivity;
import io.intercom.android.sdk.survey.ui.IntercomSurveyActivity;
import java.util.Locale;
import java.util.Set;

public class LinkOpener {
   private static final Twig twig = LumberMill.getLogger();

   public static void handleUrl(String var0, Context var1, Api var2) {
      handleUrl(var0, var1, var2, false);
   }

   public static void handleUrl(String var0, Context var1, Api var2, boolean var3) {
      if (!TextUtils.isEmpty(var0)) {
         Uri var4;
         if (isTrackingUrl(var0)) {
            var2.hitTrackingUrl(var0);
            var4 = getTargetUriFromTrackingUrl(var0);
         } else {
            var4 = parseUrl(var0);
         }

         AppConfig var5 = (AppConfig)Injector.get().getAppConfigProvider().get();
         Set var6 = var5.getHelpCenterUrls();
         String var7 = var4.toString();
         String var8 = getPlaceFromContext(var1);
         String var9 = var4.getLastPathSegment();
         boolean var10 = HelpCenterUrlUtilsKt.isHelpCenterArticleUrl(var7, var6);
         boolean var11 = HelpCenterUrlUtilsKt.isHelpCenterCollectionUrl(var7, var6);
         if ((var10 || var11) && !TextUtils.isEmpty(var9)) {
            String var13 = HelpCenterUrlUtilsKt.extractIdFromLastSegment(var9);
            if (var10) {
               HelpCenterUrlUtilsKt.openArticle(var1, var13, var8, var3);
            } else {
               HelpCenterUrlUtilsKt.openCollection(var1, var13, var8);
            }
         } else if (!var10 && !var11) {
            openUrl(var4, var1);
         } else {
            String var12 = var10 ? "article" : "collection";
            twig.e("Help center " + var12 + " URL has no path segment: " + var7);
            openUrl(var4, var1);
         }
      }
   }

   private static String getPlaceFromContext(Context var0) {
      if (var0 instanceof IntercomCarouselActivity) {
         return "carousel_message";
      } else if (var0 instanceof IntercomPostActivity || var0 instanceof IntercomNoteActivity) {
         return "in_app";
      } else if (var0 instanceof IntercomRootActivity) {
         return "conversation";
      } else {
         return var0 instanceof IntercomSurveyActivity ? "survey" : "push";
      }
   }

   @VisibleForTesting
   static Uri getTargetUriFromTrackingUrl(String var0) {
      Uri var1 = Uri.parse(var0);
      String var2 = var1.isHierarchical() ? var1.getQueryParameter("url") : var0;
      String var3 = var2 == null ? var0 : var2;
      return parseUrl(var3);
   }

   private static Uri parseUrl(String var0) {
      Uri var1 = Uri.parse(var0);
      if (var1.getScheme() == null) {
         var1 = Uri.parse("http://" + var0);
      }

      if (var1.getScheme().equalsIgnoreCase("http") || var1.getScheme().equalsIgnoreCase("https")) {
         var1 = normalizeScheme(var1);
      }

      return var1;
   }

   private static Uri normalizeScheme(Uri var0) {
      String var1 = var0.getScheme();
      if (var1 == null) {
         return var0;
      }

      String var2 = var1.toLowerCase(Locale.ROOT);
      return var1.equals(var2) ? var0 : var0.buildUpon().scheme(var2).build();
   }

   private static boolean isTrackingUrl(String var0) {
      String var1 = Uri.parse(var0).getHost();
      return "via.intercom.io".equals(var1) || "via.eu.intercom.io".equals(var1) || "via.au.intercom.io".equals(var1);
   }

   private static void openUrl(Uri var0, Context var1) {
      String var2 = "mailto".equals(var0.getScheme()) ? "android.intent.action.SENDTO" : "android.intent.action.VIEW";
      Intent var3 = new Intent(var2, var0);
      var3.addFlags(268435456);

      try {
         IntentUtils.safelyOpenIntent(var1, var3);
      } catch (ActivityNotFoundException var5) {
         twig.e("No Activity found to handle the URL '" + var0.toString() + "'");
      } catch (SecurityException var6) {
         twig.e("Couldn't open link because of error: " + var6.getMessage());
      }
   }
}
