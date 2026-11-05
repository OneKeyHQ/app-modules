package io.intercom.android.sdk.m5.home.ui.helpers;

import android.view.ViewGroup;
import android.view.ViewParent;
import io.intercom.android.sdk.blocks.messengercard.CardWebView;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\u001a\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0005\u001a\u00020\u0002H\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\t\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\u0002H\u0002\u001a\u0010\u0010\f\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0002H\u0002\u001a\b\u0010\r\u001a\u00020\u0007H\u0000\"\u001a\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e",
   d2 = {
         "webViewCache",
         "",
         "",
         "Lio/intercom/android/sdk/blocks/messengercard/CardWebView;",
         "getCachedWebView",
         "url",
         "cacheWebView",
         "",
         "webView",
         "getFromURL",
         "attribute",
         "missingAttributeValue",
         "getIdFromURL",
         "clearWebViewCache",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nInMemoryWebViewCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InMemoryWebViewCache.kt\nio/intercom/android/sdk/m5/home/ui/helpers/InMemoryWebViewCacheKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,39:1\n1869#2,2:40\n*S KotlinDebug\n*F\n+ 1 InMemoryWebViewCache.kt\nio/intercom/android/sdk/m5/home/ui/helpers/InMemoryWebViewCacheKt\n*L\n33#1:40,2\n*E\n"
)
public final class InMemoryWebViewCacheKt {
   @NotNull
   private static Map webViewCache = new LinkedHashMap();

   @Nullable
   public static final CardWebView getCachedWebView(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "url");
      String var1 = getIdFromURL(var0);
      CardWebView var2 = (CardWebView)webViewCache.get(var1);
      if (var2 != null) {
         CardWebView var3 = var2;
         int var4 = 0;
         ViewParent var5 = var3.getParent();
         ViewGroup var10000 = var5 instanceof ViewGroup ? (ViewGroup)var5 : null;
         if ((var5 instanceof ViewGroup ? (ViewGroup)var5 : null) != null) {
            var10000.removeView(var3);
         }
      }

      return var2;
   }

   public static final void cacheWebView(@NotNull String var0, @NotNull CardWebView var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "webView");
      String var2 = getIdFromURL(var0);
      ViewParent var3 = var1.getParent();
      ViewGroup var10000 = var3 instanceof ViewGroup ? (ViewGroup)var3 : null;
      if ((var3 instanceof ViewGroup ? (ViewGroup)var3 : null) != null) {
         var10000.removeView(var1);
      }

      webViewCache.put(var2, var1);
   }

   private static final String getFromURL(String var0, String var1, String var2) {
      return StringsKt.substringBefore$default(StringsKt.substringAfter(var0, var1 + '=', var2), "&", null, 2, null);
   }

   // $VF: synthetic method
   static String getFromURL$default(String var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 4) != 0) {
         var2 = var0;
      }

      return getFromURL(var0, var1, var2);
   }

   private static final String getIdFromURL(String var0) {
      return getFromURL$default(var0, "card_id", null, 4, null) + '#' + getFromURL(var0, "theme", "");
   }

   public static final void clearWebViewCache() {
      Iterable var0 = webViewCache.values();
      int var1 = 0;

      for (Object var3 : var0) {
         CardWebView var4 = (CardWebView)var3;
         int var5 = 0;
         ViewParent var6 = var4.getParent();
         ViewGroup var10000 = var6 instanceof ViewGroup ? (ViewGroup)var6 : null;
         if ((var6 instanceof ViewGroup ? (ViewGroup)var6 : null) != null) {
            var10000.removeView(var4);
         }

         var4.destroy();
      }

      webViewCache.clear();
   }
}
