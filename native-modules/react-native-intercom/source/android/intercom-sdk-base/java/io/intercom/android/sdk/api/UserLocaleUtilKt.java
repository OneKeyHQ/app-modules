package io.intercom.android.sdk.api;

import android.content.Context;
import android.os.LocaleList;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000¨\u0006\u0004",
   d2 = {"getUserLocaleString", "", "context", "Landroid/content/Context;", "intercom-sdk-base_release"}
)
public final class UserLocaleUtilKt {
   @NotNull
   public static final String getUserLocaleString(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      String var7;
      if (VERSION.SDK_INT >= 24) {
         LocaleList var10000 = var0.getResources().getConfiguration().getLocales();
         Intrinsics.checkNotNullExpressionValue(var10000, "getLocales(...)");
         LocaleList var1 = var10000;
         List var2 = new ArrayList();
         int var3 = 0;

         for (int var4 = var1.size(); var3 < var4; var3++) {
            Locale var5 = var1.get(var3);
            var2.add(var5.getLanguage() + '-' + var5.getCountry());
         }

         var7 = CollectionsKt.joinToString$default(var2, ",", null, null, 0, null, null, 62, null);
      } else {
         Locale var6 = var0.getResources().getConfiguration().locale;
         var7 = var6.getLanguage() + '-' + var6.getCountry();
      }

      return var7;
   }
}
