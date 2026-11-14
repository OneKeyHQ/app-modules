package io.intercom.android.sdk.api;

import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/api/IntercomFlipperClient;",
         "",
         "<init>",
         "()V",
         "addInterceptor",
         "",
         "builder",
         "Lokhttp3/OkHttpClient$Builder;",
         "context",
         "Landroid/content/Context;",
         "addDefaultFlipperInterceptor",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomFlipperClient {
   @NotNull
   public static final IntercomFlipperClient INSTANCE = new IntercomFlipperClient();
   public static int $stable;

   static {}

   private IntercomFlipperClient() {
   }

   public final void addInterceptor(@NotNull Builder var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var1, "builder");
      Intrinsics.checkNotNullParameter(var2, "context");
   }

   public final void addDefaultFlipperInterceptor(@NotNull Builder var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var1, "builder");
      Intrinsics.checkNotNullParameter(var2, "context");
   }
}
