package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$UnSupported;",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "cardTitle",
         "",
         "type",
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/HomeCardType;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HomeCards$UnSupported extends HomeCards {
   public static int $stable;

   static {}

   public HomeCards$UnSupported(@NotNull String var1, @NotNull HomeCardType var2) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "type");
      super(var1, var2);
   }
}
