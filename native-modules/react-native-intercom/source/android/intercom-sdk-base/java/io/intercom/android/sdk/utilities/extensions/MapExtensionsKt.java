package io.intercom.android.sdk.utilities.extensions;

import android.os.Build.VERSION;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\f\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0004\u001a7\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u00032\u0006\u0010\u0004\u001a\u0002H\u00022\u0006\u0010\u0005\u001a\u0002H\u0001¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "safeGetOrDefault",
         "V",
         "K",
         "",
         "key",
         "default",
         "(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
public final class MapExtensionsKt {
   public static final Object safeGetOrDefault(@NotNull Map var0, Object var1, Object var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Object var10000;
      if (VERSION.SDK_INT >= 24) {
         var10000 = var0.getOrDefault(var1, var2);
      } else {
         var10000 = var0.get(var1);
         if (var10000 == null) {
            var10000 = var2;
         }
      }

      return var10000;
   }
}
