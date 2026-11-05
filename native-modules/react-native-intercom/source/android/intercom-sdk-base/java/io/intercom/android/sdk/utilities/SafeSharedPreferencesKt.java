package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007",
   d2 = {
         "twig",
         "Lcom/intercom/twig/Twig;",
         "getSharedPreferencesOrNull",
         "Landroid/content/SharedPreferences;",
         "Landroid/content/Context;",
         "name",
         "",
         "intercom-sdk-base_release"
   }
)
public final class SafeSharedPreferencesKt {
   @NotNull
   private static final Twig twig;

   @Nullable
   public static final SharedPreferences getSharedPreferencesOrNull(@NotNull Context var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "name");

      SharedPreferences var2;
      try {
         var2 = var0.getSharedPreferences(var1, 0);
      } catch (Exception var4) {
         twig.w("Failed to access legacy SharedPreferences '" + var1 + "', skipping migration: " + var4.getMessage());
         var2 = null;
      }

      return var2;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}
