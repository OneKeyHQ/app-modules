package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Build.VERSION;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\u001a\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u0005H\u0000\u001a\f\u0010\b\u001a\u00020\t*\u00020\u0003H\u0000¨\u0006\n",
   d2 = {
         "readHostFromManifest",
         "",
         "context",
         "Landroid/content/Context;",
         "getServerRegionFromManifest",
         "",
         "getRegionCode",
         "id",
         "getLocaleCompat",
         "Ljava/util/Locale;",
         "intercom-sdk-base_release"
   }
)
public final class UtilsKt {
   @Nullable
   public static final String readHostFromManifest(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      String var1 = null;

      try {
         ApplicationInfo var10000 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 128);
         Intrinsics.checkNotNullExpressionValue(var10000, "getApplicationInfo(...)");
         ApplicationInfo var2 = var10000;
         Bundle var3 = var2.metaData;
         var1 = var3 != null ? var3.getString("io.intercom.android.sdk.host") : null;
      } catch (NameNotFoundException var4) {
         var4.printStackTrace();
      }

      return var1;
   }

   public static final <E extends Exception> int getServerRegionFromManifest(@NotNull Context var0) throws E {
      Intrinsics.checkNotNullParameter(var0, "context");
      ApplicationInfo var10000;
      try {
         var10000 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 128);
      } catch (NameNotFoundException sourceFailure) {
         throw (E)sourceFailure;
      }
      Intrinsics.checkNotNullExpressionValue(var10000, "getApplicationInfo(...)");
      ApplicationInfo var1 = var10000;
      return var1.metaData != null ? var1.metaData.getInt("io.intercom.android.sdk.server.region") : 0;
   }

   public static final int getRegionCode(@NotNull Context var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return var0.getResources().getInteger(var1);
   }

   @NotNull
   public static final Locale getLocaleCompat(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Configuration var1 = var0.getResources().getConfiguration();
      Locale var10000;
      if (VERSION.SDK_INT >= 24) {
         Locale var2 = var1.getLocales().get(0);
         Intrinsics.checkNotNull(var2);
         var10000 = var2;
      } else {
         Locale var3 = var1.locale;
         Intrinsics.checkNotNull(var3);
         var10000 = var3;
      }

      return var10000;
   }
}
