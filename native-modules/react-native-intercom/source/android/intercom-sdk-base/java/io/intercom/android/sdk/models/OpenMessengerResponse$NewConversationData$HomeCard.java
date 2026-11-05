package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\bHÆ\u0003J7\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001a\u001a\u00020\u001bH×\u0001J\t\u0010\u001c\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "",
         "text",
         "",
         "subtitle",
         "avatarDetails",
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "icon",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$IconType;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/AvatarDetails;Lio/intercom/android/sdk/models/OpenMessengerResponse$IconType;)V",
         "getText",
         "()Ljava/lang/String;",
         "getSubtitle",
         "getAvatarDetails",
         "()Lio/intercom/android/sdk/models/AvatarDetails;",
         "getIcon",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$IconType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class OpenMessengerResponse$NewConversationData$HomeCard {
   @SerializedName("text")
   @NotNull
   private final String text;
   @SerializedName("subtitle")
   @Nullable
   private final String subtitle;
   @SerializedName("avatar_details")
   @Nullable
   private final AvatarDetails avatarDetails;
   @SerializedName("icon")
   @Nullable
   private final OpenMessengerResponse$IconType icon;
   public static final int $stable;

   static { $stable = 8; }

   public OpenMessengerResponse$NewConversationData$HomeCard(
      @NotNull String var1, @Nullable String var2, @Nullable AvatarDetails var3, @Nullable OpenMessengerResponse$IconType var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      this.text = var1;
      this.subtitle = var2;
      this.avatarDetails = var3;
      this.icon = var4;
   }

   // $VF: synthetic method
   public OpenMessengerResponse$NewConversationData$HomeCard(
      String var1, String var2, AvatarDetails var3, OpenMessengerResponse$IconType var4, int var5, DefaultConstructorMarker var6
   ) {
      this(var1, (((var5 & 2) != 0) ? (String) (null) : var2), (((var5 & 4) != 0) ? (AvatarDetails) (null) : var3), (((var5 & 8) != 0) ? (OpenMessengerResponse$IconType) (null) : var4));
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final String getSubtitle() {
      return this.subtitle;
   }

   @Nullable
   public final AvatarDetails getAvatarDetails() {
      return this.avatarDetails;
   }

   @Nullable
   public final OpenMessengerResponse$IconType getIcon() {
      return this.icon;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   @Nullable
   public final String component2() {
      return this.subtitle;
   }

   @Nullable
   public final AvatarDetails component3() {
      return this.avatarDetails;
   }

   @Nullable
   public final OpenMessengerResponse$IconType component4() {
      return this.icon;
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData$HomeCard copy(
      @NotNull String var1, @Nullable String var2, @Nullable AvatarDetails var3, @Nullable OpenMessengerResponse$IconType var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "text");
      return new OpenMessengerResponse$NewConversationData$HomeCard(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static OpenMessengerResponse$NewConversationData$HomeCard copy$default(
      OpenMessengerResponse$NewConversationData$HomeCard var0,
      String var1,
      String var2,
      AvatarDetails var3,
      OpenMessengerResponse$IconType var4,
      int var5,
      Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.subtitle;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.avatarDetails;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.icon;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "HomeCard(text=" + this.text + ", subtitle=" + this.subtitle + ", avatarDetails=" + this.avatarDetails + ", icon=" + this.icon + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      var1 = var1 * 31 + (this.subtitle == null ? 0 : this.subtitle.hashCode());
      var1 = var1 * 31 + (this.avatarDetails == null ? 0 : this.avatarDetails.hashCode());
      return var1 * 31 + (this.icon == null ? 0 : this.icon.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OpenMessengerResponse$NewConversationData$HomeCard)) {
         return false;
      } else {
         OpenMessengerResponse$NewConversationData$HomeCard var2 = (OpenMessengerResponse$NewConversationData$HomeCard)var1;
         if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subtitle, var2.subtitle)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.avatarDetails, var2.avatarDetails) ? false : this.icon == var2.icon;
         }
      }
   }
}
