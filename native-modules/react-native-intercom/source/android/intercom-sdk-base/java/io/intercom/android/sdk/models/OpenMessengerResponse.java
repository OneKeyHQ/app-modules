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
   d1 = "\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\"\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001:\u00029:B\u0081\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u0014\u0010\u0015J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010*\u001a\u00020\u0007HÆ\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\tHÆ\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\u000f\u0010-\u001a\b\u0012\u0004\u0012\u00020\t0\rHÆ\u0003J\u000b\u0010.\u001a\u0004\u0018\u00010\u000fHÆ\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0011HÆ\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\tHÆ\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\tHÆ\u0003J\u0083\u0001\u00102\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\tHÇ\u0001J\u0013\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00106\u001a\u000207H×\u0001J\t\u00108\u001a\u00020\tH×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0018\u0010\b\u001a\u0004\u0018\u00010\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0018\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u000f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00118\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0018\u0010\u0012\u001a\u0004\u0018\u00010\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001dR\u0018\u0010\u0013\u001a\u0004\u0018\u00010\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b'\u0010\u001d¨\u0006;",
   d2 = {
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "",
         "composerSuggestions",
         "Lio/intercom/android/sdk/models/ComposerSuggestions;",
         "newConversationData",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData;",
         "composerState",
         "Lio/intercom/android/sdk/models/ComposerState;",
         "requestId",
         "",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "openInboundConversations",
         "",
         "privacyNotice",
         "Lio/intercom/android/sdk/models/PrivacyNotice;",
         "teamPresence",
         "Lio/intercom/android/sdk/models/TeamPresence;",
         "resolutionBotBehaviorVersionId",
         "botIntroId",
         "<init>",
         "(Lio/intercom/android/sdk/models/ComposerSuggestions;Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData;Lio/intercom/android/sdk/models/ComposerState;Ljava/lang/String;Lio/intercom/android/sdk/models/PoweredBy;Ljava/util/List;Lio/intercom/android/sdk/models/PrivacyNotice;Lio/intercom/android/sdk/models/TeamPresence;Ljava/lang/String;Ljava/lang/String;)V",
         "getComposerSuggestions",
         "()Lio/intercom/android/sdk/models/ComposerSuggestions;",
         "getNewConversationData",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData;",
         "getComposerState",
         "()Lio/intercom/android/sdk/models/ComposerState;",
         "getRequestId",
         "()Ljava/lang/String;",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "getOpenInboundConversations",
         "()Ljava/util/List;",
         "getPrivacyNotice",
         "()Lio/intercom/android/sdk/models/PrivacyNotice;",
         "getTeamPresence",
         "()Lio/intercom/android/sdk/models/TeamPresence;",
         "getResolutionBotBehaviorVersionId",
         "getBotIntroId",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "NewConversationData",
         "IconType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class OpenMessengerResponse {
   @SerializedName("composer_suggestions")
   @Nullable
   private final ComposerSuggestions composerSuggestions;
   @SerializedName("new_conversation")
   @Nullable
   private final OpenMessengerResponse$NewConversationData newConversationData;
   @SerializedName("composer_state")
   @NotNull
   private final ComposerState composerState;
   @SerializedName("request_id")
   @Nullable
   private final String requestId;
   @SerializedName("powered_by")
   @Nullable
   private final PoweredBy poweredBy;
   @SerializedName("open_inbound_conversation_ids")
   @NotNull
   private final List openInboundConversations;
   @SerializedName("privacy_policy_notice")
   @Nullable
   private final PrivacyNotice privacyNotice;
   @SerializedName("team_presence")
   @Nullable
   private final TeamPresence teamPresence;
   @SerializedName("resolution_bot_behavior_version_id")
   @Nullable
   private final String resolutionBotBehaviorVersionId;
   @SerializedName("bot_intro_id")
   @Nullable
   private final String botIntroId;
   public static final int $stable;

   static { $stable = 8; }

   public OpenMessengerResponse(
      @Nullable ComposerSuggestions var1,
      @Nullable OpenMessengerResponse$NewConversationData var2,
      @NotNull ComposerState var3,
      @Nullable String var4,
      @Nullable PoweredBy var5,
      @NotNull List var6,
      @Nullable PrivacyNotice var7,
      @Nullable TeamPresence var8,
      @Nullable String var9,
      @Nullable String var10
   ) {
      super();
      Intrinsics.checkNotNullParameter(var3, "composerState");
      Intrinsics.checkNotNullParameter(var6, "openInboundConversations");
      this.composerSuggestions = var1;
      this.newConversationData = var2;
      this.composerState = var3;
      this.requestId = var4;
      this.poweredBy = var5;
      this.openInboundConversations = var6;
      this.privacyNotice = var7;
      this.teamPresence = var8;
      this.resolutionBotBehaviorVersionId = var9;
      this.botIntroId = var10;
   }

   // $VF: synthetic method
   public OpenMessengerResponse(
      ComposerSuggestions var1,
      OpenMessengerResponse$NewConversationData var2,
      ComposerState var3,
      String var4,
      PoweredBy var5,
      List var6,
      PrivacyNotice var7,
      TeamPresence var8,
      String var9,
      String var10,
      int var11,
      DefaultConstructorMarker var12
   ) {
      this((((var11 & 1) != 0) ? (ComposerSuggestions) (null) : var1), (((var11 & 2) != 0) ? (OpenMessengerResponse$NewConversationData) (null) : var2), (((var11 & 4) != 0) ? (ComposerState) (ComposerState.Companion.getNULL()) : var3), (((var11 & 8) != 0) ? (String) (null) : var4), (((var11 & 16) != 0) ? (PoweredBy) (null) : var5), (((var11 & 32) != 0) ? (List) (CollectionsKt.emptyList()) : var6), (((var11 & 64) != 0) ? (PrivacyNotice) (null) : var7), (((var11 & 128) != 0) ? (TeamPresence) (null) : var8), (((var11 & 256) != 0) ? (String) (null) : var9), (((var11 & 512) != 0) ? (String) (null) : var10));
   }

   @Nullable
   public final ComposerSuggestions getComposerSuggestions() {
      return this.composerSuggestions;
   }

   @Nullable
   public final OpenMessengerResponse$NewConversationData getNewConversationData() {
      return this.newConversationData;
   }

   @NotNull
   public final ComposerState getComposerState() {
      return this.composerState;
   }

   @Nullable
   public final String getRequestId() {
      return this.requestId;
   }

   @Nullable
   public final PoweredBy getPoweredBy() {
      return this.poweredBy;
   }

   @NotNull
   public final List getOpenInboundConversations() {
      return this.openInboundConversations;
   }

   @Nullable
   public final PrivacyNotice getPrivacyNotice() {
      return this.privacyNotice;
   }

   @Nullable
   public final TeamPresence getTeamPresence() {
      return this.teamPresence;
   }

   @Nullable
   public final String getResolutionBotBehaviorVersionId() {
      return this.resolutionBotBehaviorVersionId;
   }

   @Nullable
   public final String getBotIntroId() {
      return this.botIntroId;
   }

   @Nullable
   public final ComposerSuggestions component1() {
      return this.composerSuggestions;
   }

   @Nullable
   public final OpenMessengerResponse$NewConversationData component2() {
      return this.newConversationData;
   }

   @NotNull
   public final ComposerState component3() {
      return this.composerState;
   }

   @Nullable
   public final String component4() {
      return this.requestId;
   }

   @Nullable
   public final PoweredBy component5() {
      return this.poweredBy;
   }

   @NotNull
   public final List component6() {
      return this.openInboundConversations;
   }

   @Nullable
   public final PrivacyNotice component7() {
      return this.privacyNotice;
   }

   @Nullable
   public final TeamPresence component8() {
      return this.teamPresence;
   }

   @Nullable
   public final String component9() {
      return this.resolutionBotBehaviorVersionId;
   }

   @Nullable
   public final String component10() {
      return this.botIntroId;
   }

   @NotNull
   public final OpenMessengerResponse copy(
      @Nullable ComposerSuggestions var1,
      @Nullable OpenMessengerResponse$NewConversationData var2,
      @NotNull ComposerState var3,
      @Nullable String var4,
      @Nullable PoweredBy var5,
      @NotNull List var6,
      @Nullable PrivacyNotice var7,
      @Nullable TeamPresence var8,
      @Nullable String var9,
      @Nullable String var10
   ) {
      Intrinsics.checkNotNullParameter(var3, "composerState");
      Intrinsics.checkNotNullParameter(var6, "openInboundConversations");
      return new OpenMessengerResponse(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   // $VF: synthetic method
   public static OpenMessengerResponse copy$default(
      OpenMessengerResponse var0,
      ComposerSuggestions var1,
      OpenMessengerResponse$NewConversationData var2,
      ComposerState var3,
      String var4,
      PoweredBy var5,
      List var6,
      PrivacyNotice var7,
      TeamPresence var8,
      String var9,
      String var10,
      int var11,
      Object var12
   ) {
      if ((var11 & 1) != 0) {
         var1 = var0.composerSuggestions;
      }

      if ((var11 & 2) != 0) {
         var2 = var0.newConversationData;
      }

      if ((var11 & 4) != 0) {
         var3 = var0.composerState;
      }

      if ((var11 & 8) != 0) {
         var4 = var0.requestId;
      }

      if ((var11 & 16) != 0) {
         var5 = var0.poweredBy;
      }

      if ((var11 & 32) != 0) {
         var6 = var0.openInboundConversations;
      }

      if ((var11 & 64) != 0) {
         var7 = var0.privacyNotice;
      }

      if ((var11 & 128) != 0) {
         var8 = var0.teamPresence;
      }

      if ((var11 & 256) != 0) {
         var9 = var0.resolutionBotBehaviorVersionId;
      }

      if ((var11 & 512) != 0) {
         var10 = var0.botIntroId;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @NotNull
   @Override
   public String toString() {
      return "OpenMessengerResponse(composerSuggestions="
         + this.composerSuggestions
         + ", newConversationData="
         + this.newConversationData
         + ", composerState="
         + this.composerState
         + ", requestId="
         + this.requestId
         + ", poweredBy="
         + this.poweredBy
         + ", openInboundConversations="
         + this.openInboundConversations
         + ", privacyNotice="
         + this.privacyNotice
         + ", teamPresence="
         + this.teamPresence
         + ", resolutionBotBehaviorVersionId="
         + this.resolutionBotBehaviorVersionId
         + ", botIntroId="
         + this.botIntroId
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.composerSuggestions == null ? 0 : this.composerSuggestions.hashCode();
      var1 = var1 * 31 + (this.newConversationData == null ? 0 : this.newConversationData.hashCode());
      var1 = var1 * 31 + this.composerState.hashCode();
      var1 = var1 * 31 + (this.requestId == null ? 0 : this.requestId.hashCode());
      var1 = var1 * 31 + (this.poweredBy == null ? 0 : this.poweredBy.hashCode());
      var1 = var1 * 31 + this.openInboundConversations.hashCode();
      var1 = var1 * 31 + (this.privacyNotice == null ? 0 : this.privacyNotice.hashCode());
      var1 = var1 * 31 + (this.teamPresence == null ? 0 : this.teamPresence.hashCode());
      var1 = var1 * 31 + (this.resolutionBotBehaviorVersionId == null ? 0 : this.resolutionBotBehaviorVersionId.hashCode());
      return var1 * 31 + (this.botIntroId == null ? 0 : this.botIntroId.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OpenMessengerResponse)) {
         return false;
      } else {
         OpenMessengerResponse var2 = (OpenMessengerResponse)var1;
         if (!Intrinsics.areEqual(this.composerSuggestions, var2.composerSuggestions)) {
            return false;
         } else if (!Intrinsics.areEqual(this.newConversationData, var2.newConversationData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.composerState, var2.composerState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.requestId, var2.requestId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.poweredBy, var2.poweredBy)) {
            return false;
         } else if (!Intrinsics.areEqual(this.openInboundConversations, var2.openInboundConversations)) {
            return false;
         } else if (!Intrinsics.areEqual(this.privacyNotice, var2.privacyNotice)) {
            return false;
         } else if (!Intrinsics.areEqual(this.teamPresence, var2.teamPresence)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.resolutionBotBehaviorVersionId, var2.resolutionBotBehaviorVersionId)
               ? false
               : Intrinsics.areEqual(this.botIntroId, var2.botIntroId);
         }
      }
   }

   public OpenMessengerResponse() {
      this(null, null, null, null, null, null, null, null, null, null, 1023, null);
   }
}
