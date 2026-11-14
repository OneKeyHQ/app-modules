package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeMessengerAppData;",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "cardTitle",
         "",
         "type",
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "fallbackUrl",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/HomeCardType;Ljava/lang/String;)V",
         "getFallbackUrl",
         "()Ljava/lang/String;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HomeCards$HomeMessengerAppData extends HomeCards {
   @SerializedName("fallback_url")
   @NotNull
   private final String fallbackUrl;
   public static int $stable;

   static {}

   public HomeCards$HomeMessengerAppData(@NotNull String var1, @NotNull HomeCardType var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "fallbackUrl");
      super(var1, var2);
      this.fallbackUrl = var3;
   }

   @NotNull
   public final String getFallbackUrl() {
      return this.fallbackUrl;
   }
}
