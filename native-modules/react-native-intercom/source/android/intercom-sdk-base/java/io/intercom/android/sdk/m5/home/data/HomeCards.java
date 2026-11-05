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
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\b\u0011\u0018\u00002\u00020\u0001:\t\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "",
         "cardTitle",
         "",
         "type",
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/HomeCardType;)V",
         "getCardTitle",
         "()Ljava/lang/String;",
         "getType",
         "()Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "HomeSpacesData",
         "HomeNewConversationData",
         "HomeRecentConversationData",
         "HomeHelpCenterData",
         "HomeExternalLinkData",
         "HomeMessengerAppData",
         "HomeRecentTicketsData",
         "HomeTicketLinksData",
         "UnSupported",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public class HomeCards {
   @SerializedName("card_title")
   @NotNull
   private final String cardTitle;
   @SerializedName("type")
   @NotNull
   private final HomeCardType type;
   public static int $stable;

   static {}

   public HomeCards(@NotNull String var1, @NotNull HomeCardType var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "type");
      this.cardTitle = var1;
      this.type = var2;
   }

   @NotNull
   public final String getCardTitle() {
      return this.cardTitle;
   }

   @NotNull
   public final HomeCardType getType() {
      return this.type;
   }
}
