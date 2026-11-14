package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$HomeCard;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\b\u0007\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u0012\u0006\u0010\f\u001a\u00020\t\u0012\u0006\u0010\r\u001a\u00020\t\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0016\u0010\f\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0016\u0010\r\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeNewConversationData;",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "cardTitle",
         "",
         "type",
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "action",
         "Lio/intercom/android/sdk/m5/home/data/Action;",
         "preventMultipleInboundConversationsEnabled",
         "",
         "openInboundConversationsIds",
         "",
         "showFinAvatar",
         "showTeammateExpectations",
         "homeCard",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/HomeCardType;Lio/intercom/android/sdk/m5/home/data/Action;ZLjava/util/List;ZZLio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;)V",
         "getAction",
         "()Lio/intercom/android/sdk/m5/home/data/Action;",
         "getPreventMultipleInboundConversationsEnabled",
         "()Z",
         "getOpenInboundConversationsIds",
         "()Ljava/util/List;",
         "getShowFinAvatar",
         "getShowTeammateExpectations",
         "getHomeCard",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeCards$HomeNewConversationData extends HomeCards {
   @SerializedName("action")
   @NotNull
   private final Action action;
   @SerializedName("prevent_multiple_inbound_conversations_enabled")
   private final boolean preventMultipleInboundConversationsEnabled;
   @SerializedName("open_inbound_conversation_ids")
   @NotNull
   private final List openInboundConversationsIds;
   @SerializedName("show_fin_avatar")
   private final boolean showFinAvatar;
   @SerializedName("show_teammate_expectations")
   private final boolean showTeammateExpectations;
   @Nullable
   private final OpenMessengerResponse$NewConversationData$HomeCard homeCard;
   public static final int $stable;

   static { $stable = 8; }

   public HomeCards$HomeNewConversationData(
      @NotNull String var1,
      @NotNull HomeCardType var2,
      @NotNull Action var3,
      boolean var4,
      @NotNull List var5,
      boolean var6,
      boolean var7,
      @Nullable OpenMessengerResponse$NewConversationData$HomeCard var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "action");
      Intrinsics.checkNotNullParameter(var5, "openInboundConversationsIds");
      super(var1, var2);
      this.action = var3;
      this.preventMultipleInboundConversationsEnabled = var4;
      this.openInboundConversationsIds = var5;
      this.showFinAvatar = var6;
      this.showTeammateExpectations = var7;
      this.homeCard = var8;
   }

   @NotNull
   public final Action getAction() {
      return this.action;
   }

   public final boolean getPreventMultipleInboundConversationsEnabled() {
      return this.preventMultipleInboundConversationsEnabled;
   }

   @NotNull
   public final List getOpenInboundConversationsIds() {
      return this.openInboundConversationsIds;
   }

   public final boolean getShowFinAvatar() {
      return this.showFinAvatar;
   }

   public final boolean getShowTeammateExpectations() {
      return this.showTeammateExpectations;
   }

   @Nullable
   public final OpenMessengerResponse$NewConversationData$HomeCard getHomeCard() {
      return this.homeCard;
   }
}
