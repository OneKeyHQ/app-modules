package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001:\u0002,-B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e¢\u0006\u0004\b\u0010\u0010\u0011J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\n0\u0007HÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\fHÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u000eHÆ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u000eHÆ\u0003Ja\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000eHÇ\u0001J\u0013\u0010&\u001a\u00020'2\b\u0010(\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010)\u001a\u00020*H×\u0001J\t\u0010+\u001a\u00020\u000eH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0018\u0010\u000b\u001a\u0004\u0018\u00010\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001c¨\u0006.",
   d2 = {
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData;",
         "",
         "cta",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;",
         "homeCard",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "parts",
         "",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "participants",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "header",
         "Lio/intercom/android/sdk/models/Header;",
         "teamIntro",
         "",
         "specialNotice",
         "<init>",
         "(Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;Ljava/util/List;Ljava/util/List;Lio/intercom/android/sdk/models/Header;Ljava/lang/String;Ljava/lang/String;)V",
         "getCta",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;",
         "getHomeCard",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "getParts",
         "()Ljava/util/List;",
         "getParticipants",
         "getHeader",
         "()Lio/intercom/android/sdk/models/Header;",
         "getTeamIntro",
         "()Ljava/lang/String;",
         "getSpecialNotice",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Cta",
         "HomeCard",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class OpenMessengerResponse$NewConversationData {
   @SerializedName("cta")
   @NotNull
   private final OpenMessengerResponse$NewConversationData$Cta cta;
   @SerializedName("home_card")
   @NotNull
   private final OpenMessengerResponse$NewConversationData$HomeCard homeCard;
   @SerializedName("parts")
   @NotNull
   private final List parts;
   @SerializedName("participants")
   @NotNull
   private final List participants;
   @SerializedName("header")
   @Nullable
   private final Header header;
   @SerializedName("team_intro")
   @Nullable
   private final String teamIntro;
   @SerializedName("special_notice")
   @Nullable
   private final String specialNotice;
   public static final int $stable;

   static { $stable = 8; }

   public OpenMessengerResponse$NewConversationData(
      @NotNull OpenMessengerResponse$NewConversationData$Cta var1,
      @NotNull OpenMessengerResponse$NewConversationData$HomeCard var2,
      @NotNull List var3,
      @NotNull List var4,
      @Nullable Header var5,
      @Nullable String var6,
      @Nullable String var7
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "cta");
      Intrinsics.checkNotNullParameter(var2, "homeCard");
      Intrinsics.checkNotNullParameter(var3, "parts");
      Intrinsics.checkNotNullParameter(var4, "participants");
      this.cta = var1;
      this.homeCard = var2;
      this.parts = var3;
      this.participants = var4;
      this.header = var5;
      this.teamIntro = var6;
      this.specialNotice = var7;
   }

   // $VF: synthetic method
   public OpenMessengerResponse$NewConversationData(
      OpenMessengerResponse$NewConversationData$Cta var1,
      OpenMessengerResponse$NewConversationData$HomeCard var2,
      List var3,
      List var4,
      Header var5,
      String var6,
      String var7,
      int var8,
      DefaultConstructorMarker var9
   ) {
      this(var1, var2, (((var8 & 4) != 0) ? (List) (CollectionsKt.emptyList()) : var3), (((var8 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4), (((var8 & 16) != 0) ? (Header) (null) : var5), (((var8 & 32) != 0) ? (String) (null) : var6), (((var8 & 64) != 0) ? (String) (null) : var7));
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData$Cta getCta() {
      return this.cta;
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData$HomeCard getHomeCard() {
      return this.homeCard;
   }

   @NotNull
   public final List getParts() {
      return this.parts;
   }

   @NotNull
   public final List getParticipants() {
      return this.participants;
   }

   @Nullable
   public final Header getHeader() {
      return this.header;
   }

   @Nullable
   public final String getTeamIntro() {
      return this.teamIntro;
   }

   @Nullable
   public final String getSpecialNotice() {
      return this.specialNotice;
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData$Cta component1() {
      return this.cta;
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData$HomeCard component2() {
      return this.homeCard;
   }

   @NotNull
   public final List component3() {
      return this.parts;
   }

   @NotNull
   public final List component4() {
      return this.participants;
   }

   @Nullable
   public final Header component5() {
      return this.header;
   }

   @Nullable
   public final String component6() {
      return this.teamIntro;
   }

   @Nullable
   public final String component7() {
      return this.specialNotice;
   }

   @NotNull
   public final OpenMessengerResponse$NewConversationData copy(
      @NotNull OpenMessengerResponse$NewConversationData$Cta var1,
      @NotNull OpenMessengerResponse$NewConversationData$HomeCard var2,
      @NotNull List var3,
      @NotNull List var4,
      @Nullable Header var5,
      @Nullable String var6,
      @Nullable String var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "cta");
      Intrinsics.checkNotNullParameter(var2, "homeCard");
      Intrinsics.checkNotNullParameter(var3, "parts");
      Intrinsics.checkNotNullParameter(var4, "participants");
      return new OpenMessengerResponse$NewConversationData(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static OpenMessengerResponse$NewConversationData copy$default(
      OpenMessengerResponse$NewConversationData var0,
      OpenMessengerResponse$NewConversationData$Cta var1,
      OpenMessengerResponse$NewConversationData$HomeCard var2,
      List var3,
      List var4,
      Header var5,
      String var6,
      String var7,
      int var8,
      Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.cta;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.homeCard;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.parts;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.participants;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.header;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.teamIntro;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.specialNotice;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "NewConversationData(cta="
         + this.cta
         + ", homeCard="
         + this.homeCard
         + ", parts="
         + this.parts
         + ", participants="
         + this.participants
         + ", header="
         + this.header
         + ", teamIntro="
         + this.teamIntro
         + ", specialNotice="
         + this.specialNotice
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.cta.hashCode();
      var1 = var1 * 31 + this.homeCard.hashCode();
      var1 = var1 * 31 + this.parts.hashCode();
      var1 = var1 * 31 + this.participants.hashCode();
      var1 = var1 * 31 + (this.header == null ? 0 : this.header.hashCode());
      var1 = var1 * 31 + (this.teamIntro == null ? 0 : this.teamIntro.hashCode());
      return var1 * 31 + (this.specialNotice == null ? 0 : this.specialNotice.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OpenMessengerResponse$NewConversationData)) {
         return false;
      } else {
         OpenMessengerResponse$NewConversationData var2 = (OpenMessengerResponse$NewConversationData)var1;
         if (!Intrinsics.areEqual(this.cta, var2.cta)) {
            return false;
         } else if (!Intrinsics.areEqual(this.homeCard, var2.homeCard)) {
            return false;
         } else if (!Intrinsics.areEqual(this.parts, var2.parts)) {
            return false;
         } else if (!Intrinsics.areEqual(this.participants, var2.participants)) {
            return false;
         } else if (!Intrinsics.areEqual(this.header, var2.header)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.teamIntro, var2.teamIntro) ? false : Intrinsics.areEqual(this.specialNotice, var2.specialNotice);
         }
      }
   }
}
