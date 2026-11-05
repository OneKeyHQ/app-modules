package io.intercom.android.sdk.helpcenter.api;

import java.util.Map;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class HelpCenterApi$DefaultImpls {
   // $VF: synthetic method
   public static Object fetchCollections$default(HelpCenterApi var0, Map var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: fetchCollections");
      }

      if ((var3 & 1) != 0) {
         var1 = HelpCenterApiHelper.INSTANCE.addDefaultOptions();
      }

      return var0.fetchCollections(var1, var2);
   }

   // $VF: synthetic method
   public static Object fetchCollectionDetails$default(HelpCenterApi var0, String var1, Map var2, Continuation var3, int var4, Object var5) {
      if (var5 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: fetchCollectionDetails");
      }

      if ((var4 & 2) != 0) {
         var2 = HelpCenterApiHelper.INSTANCE.addDefaultOptions();
      }

      return var0.fetchCollectionDetails(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Object searchForArticles$default(HelpCenterApi var0, String var1, String var2, Map var3, Continuation var4, int var5, Object var6) {
      if (var6 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: searchForArticles");
      }

      if ((var5 & 2) != 0) {
         var2 = null;
      }

      if ((var5 & 4) != 0) {
         var3 = HelpCenterApiHelper.INSTANCE.addDefaultOptions();
      }

      return var0.searchForArticles(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Object reactToArticle$default(
      HelpCenterApi var0, String var1, int var2, String var3, boolean var4, String var5, Map var6, Continuation var7, int var8, Object var9
   ) {
      if (var9 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: reactToArticle");
      }

      if ((var8 & 8) != 0) {
         var4 = false;
      }

      if ((var8 & 16) != 0) {
         var5 = null;
      }

      if ((var8 & 32) != 0) {
         var6 = HelpCenterApiHelper.INSTANCE.addDefaultOptions();
      }

      return var0.reactToArticle(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static Object fetchArticle$default(HelpCenterApi var0, String var1, Map var2, Continuation var3, int var4, Object var5) {
      if (var5 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: fetchArticle");
      }

      if ((var4 & 2) != 0) {
         var2 = HelpCenterApiHelper.INSTANCE.addDefaultOptions();
      }

      return var0.fetchArticle(var1, var2, var3);
   }
}
