package io.intercom.android.sdk.helpcenter.utils;

import android.content.Context;
import android.content.Intent;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000,\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u0000\u001a\u001e\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u0000\u001a\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a*\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0001H\u0001\u001a \u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0003H\u0000\u001a&\u0010\u0012\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u00142\u0006\u0010\u000e\u001a\u00020\u0003H\u0000\u001a\u001e\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u0002¨\u0006\u0016",
   d2 = {
         "isHelpCenterArticleUrl",
         "",
         "url",
         "",
         "helpCenterUrls",
         "",
         "isHelpCenterCollectionUrl",
         "extractIdFromLastSegment",
         "lastSegment",
         "openArticle",
         "",
         "context",
         "Landroid/content/Context;",
         "articleId",
         "place",
         "shouldHideReactions",
         "openCollection",
         "collectionId",
         "openCollections",
         "collectionIds",
         "",
         "isHelpCenterUrl",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterUrlUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterUrlUtils.kt\nio/intercom/android/sdk/helpcenter/utils/HelpCenterUrlUtilsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n1761#2,3:62\n*S KotlinDebug\n*F\n+ 1 HelpCenterUrlUtils.kt\nio/intercom/android/sdk/helpcenter/utils/HelpCenterUrlUtilsKt\n*L\n60#1:62,3\n*E\n"
)
public final class HelpCenterUrlUtilsKt {
   public static final boolean isHelpCenterArticleUrl(@NotNull String var0, @NotNull Set var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "helpCenterUrls");
      return isHelpCenterUrl(var0, var1) && StringsKt.contains$default(var0, "/articles/", false, 2, null);
   }

   public static final boolean isHelpCenterCollectionUrl(@NotNull String var0, @NotNull Set var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "helpCenterUrls");
      return isHelpCenterUrl(var0, var1) && StringsKt.contains$default(var0, "/collections/", false, 2, null);
   }

   @NotNull
   public static final String extractIdFromLastSegment(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "lastSegment");
      CharSequence var10000 = var0;
      String[] var2 = new String[]{"-"};
      List var1 = StringsKt.split$default(var10000, var2, false, 0, 6, null);
      return !var1.isEmpty() ? (String)var1.get(0) : "";
   }

   @JvmOverloads
   public static final void openArticle(@NotNull Context var0, @NotNull String var1, @NotNull String var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "place");
      Intent var4 = IntercomArticleActivity.Companion.buildIntent(var0, new IntercomArticleActivity$ArticleActivityArguments(var1, var2, false, var3, 4, null));
      var0.startActivity(var4);
   }

   // $VF: synthetic method
   public static void openArticle$default(Context var0, String var1, String var2, boolean var3, int var4, Object var5) {
      if ((var4 & 8) != 0) {
         var3 = false;
      }

      openArticle(var0, var1, var2, var3);
   }

   public static final void openCollection(@NotNull Context var0, @NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var2, "place");
      IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollection(var0, var1, var2);
   }

   public static final void openCollections(@NotNull Context var0, @NotNull List var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      Intrinsics.checkNotNullParameter(var2, "place");
      if (var1.size() == 1) {
         IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollection(var0, (String)CollectionsKt.first(var1), var2);
      } else {
         IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollections(var0, var1, var2);
      }
   }

   private static final boolean isHelpCenterUrl(String var0, Set var1) {
      var var2 = var1;
      int var3 = 0;
      boolean var10000;
      if (var2 instanceof Collection && ((Collection)var2).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var4 = var2.iterator();

         while (true) {
            if (!var4.hasNext()) {
               var10000 = false;
               break;
            }

            Object var5 = var4.next();
            String var6 = (String)var5;
            int var7 = 0;
            if (StringsKt.startsWith$default(var0, var6, false, 2, null)) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   @JvmOverloads
   public static final void openArticle(@NotNull Context var0, @NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "place");
      openArticle$default(var0, var1, var2, false, 8, null);
   }
}
