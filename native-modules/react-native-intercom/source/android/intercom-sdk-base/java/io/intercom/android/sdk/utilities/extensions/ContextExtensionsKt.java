package io.intercom.android.sdk.utilities.extensions;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\u0000¨\u0006\u0003",
   d2 = {"findActivity", "Landroid/app/Activity;", "Landroid/content/Context;", "intercom-sdk-base_release"}
)
public final class ContextExtensionsKt {
   @Nullable
   public static final Activity findActivity(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");

      for (Context var1 = var0; var1 instanceof ContextWrapper; var1 = ((ContextWrapper)var1).getBaseContext()) {
         if (var1 instanceof Activity) {
            return (Activity)var1;
         }
      }

      return null;
   }
}
