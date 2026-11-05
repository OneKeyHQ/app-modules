package io.intercom.android.sdk.helpcenter.articles;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b%\b\u0087\b\u0018\u0000 52\u00020\u0001:\u00015Bk\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0001\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\u0007\u0012\b\b\u0001\u0010\t\u001a\u00020\u0007\u0012\b\b\u0001\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\u0004\b\u0012\u0010\u0013J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0010\u0010'\u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u0019J\t\u0010(\u001a\u00020\u0007HÆ\u0003J\t\u0010)\u001a\u00020\u0007HÆ\u0003J\t\u0010*\u001a\u00020\u0007HÆ\u0003J\t\u0010+\u001a\u00020\fHÆ\u0003J\t\u0010,\u001a\u00020\fHÆ\u0003J\t\u0010-\u001a\u00020\u000fHÆ\u0003J\u000b\u0010.\u001a\u0004\u0018\u00010\u0011HÆ\u0003Jz\u0010/\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0003\u0010\b\u001a\u00020\u00072\b\b\u0003\u0010\t\u001a\u00020\u00072\b\b\u0003\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011HÇ\u0001¢\u0006\u0002\u00100J\u0013\u00101\u001a\u00020\u000f2\b\u00102\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00103\u001a\u00020\u0007H×\u0001J\t\u00104\u001a\u00020\fH×\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\t\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001cR\u0011\u0010\n\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\r\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\"R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$¨\u00066",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "conversationState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ConversationState;",
         "subtitleText",
         "",
         "messageButtonText",
         "messageButtonIcon",
         "messageButtonColor",
         "metricPlace",
         "",
         "metricContext",
         "isFromSearchBrowse",
         "",
         "ctaData",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ConversationState;Ljava/lang/Integer;IIILjava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;)V",
         "getArticleMetadata",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "getConversationState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ConversationState;",
         "getSubtitleText",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getMessageButtonText",
         "()I",
         "getMessageButtonIcon",
         "getMessageButtonColor",
         "getMetricPlace",
         "()Ljava/lang/String;",
         "getMetricContext",
         "()Z",
         "getCtaData",
         "()Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;",
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
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ConversationState;Ljava/lang/Integer;IIILjava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$Cta;)Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleViewState$TeamPresenceState {
   @NotNull
   public static final ArticleViewState$TeamPresenceState$Companion Companion = new ArticleViewState$TeamPresenceState$Companion(null);
   @Nullable
   private final ArticleMetadata articleMetadata;
   @Nullable
   private final ArticleViewState$ConversationState conversationState;
   @Nullable
   private final Integer subtitleText;
   private final int messageButtonText;
   private final int messageButtonIcon;
   private final int messageButtonColor;
   @NotNull
   private final String metricPlace;
   @NotNull
   private final String metricContext;
   private final boolean isFromSearchBrowse;
   @Nullable
   private final OpenMessengerResponse$NewConversationData$Cta ctaData;
   public static int $stable;

   static {}
   @NotNull
   private static final ArticleViewState$TeamPresenceState defaultTeamPresenceState;

   public ArticleViewState$TeamPresenceState(
      @Nullable ArticleMetadata var1,
      @Nullable ArticleViewState$ConversationState var2,
      @StringRes @Nullable Integer var3,
      @StringRes int var4,
      @DrawableRes int var5,
      @ColorInt int var6,
      @NotNull String var7,
      @NotNull String var8,
      boolean var9,
      @Nullable OpenMessengerResponse$NewConversationData$Cta var10
   ) {
      super();
      Intrinsics.checkNotNullParameter(var7, "metricPlace");
      Intrinsics.checkNotNullParameter(var8, "metricContext");
      this.articleMetadata = var1;
      this.conversationState = var2;
      this.subtitleText = var3;
      this.messageButtonText = var4;
      this.messageButtonIcon = var5;
      this.messageButtonColor = var6;
      this.metricPlace = var7;
      this.metricContext = var8;
      this.isFromSearchBrowse = var9;
      this.ctaData = var10;
   }

   // $VF: synthetic method
   public ArticleViewState$TeamPresenceState(
      ArticleMetadata var1,
      ArticleViewState$ConversationState var2,
      Integer var3,
      int var4,
      int var5,
      int var6,
      String var7,
      String var8,
      boolean var9,
      OpenMessengerResponse$NewConversationData$Cta var10,
      int var11,
      DefaultConstructorMarker var12
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, (((var11 & 256) != 0) ? (boolean) (false) : var9), (((var11 & 512) != 0) ? (OpenMessengerResponse$NewConversationData$Cta) (null) : var10));
   }

   @Nullable
   public final ArticleMetadata getArticleMetadata() {
      return this.articleMetadata;
   }

   @Nullable
   public final ArticleViewState$ConversationState getConversationState() {
      return this.conversationState;
   }

   @Nullable
   public final Integer getSubtitleText() {
      return this.subtitleText;
   }

   public final int getMessageButtonText() {
      return this.messageButtonText;
   }

   public final int getMessageButtonIcon() {
      return this.messageButtonIcon;
   }

   public final int getMessageButtonColor() {
      return this.messageButtonColor;
   }

   @NotNull
   public final String getMetricPlace() {
      return this.metricPlace;
   }

   @NotNull
   public final String getMetricContext() {
      return this.metricContext;
   }

   public final boolean isFromSearchBrowse() {
      return this.isFromSearchBrowse;
   }

   @Nullable
   public final OpenMessengerResponse$NewConversationData$Cta getCtaData() {
      return this.ctaData;
   }

   @Nullable
   public final ArticleMetadata component1() {
      return this.articleMetadata;
   }

   @Nullable
   public final ArticleViewState$ConversationState component2() {
      return this.conversationState;
   }

   @Nullable
   public final Integer component3() {
      return this.subtitleText;
   }

   public final int component4() {
      return this.messageButtonText;
   }

   public final int component5() {
      return this.messageButtonIcon;
   }

   public final int component6() {
      return this.messageButtonColor;
   }

   @NotNull
   public final String component7() {
      return this.metricPlace;
   }

   @NotNull
   public final String component8() {
      return this.metricContext;
   }

   public final boolean component9() {
      return this.isFromSearchBrowse;
   }

   @Nullable
   public final OpenMessengerResponse$NewConversationData$Cta component10() {
      return this.ctaData;
   }

   @NotNull
   public final ArticleViewState$TeamPresenceState copy(
      @Nullable ArticleMetadata var1,
      @Nullable ArticleViewState$ConversationState var2,
      @StringRes @Nullable Integer var3,
      @StringRes int var4,
      @DrawableRes int var5,
      @ColorInt int var6,
      @NotNull String var7,
      @NotNull String var8,
      boolean var9,
      @Nullable OpenMessengerResponse$NewConversationData$Cta var10
   ) {
      Intrinsics.checkNotNullParameter(var7, "metricPlace");
      Intrinsics.checkNotNullParameter(var8, "metricContext");
      return new ArticleViewState$TeamPresenceState(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   // $VF: synthetic method
   public static ArticleViewState$TeamPresenceState copy$default(
      ArticleViewState$TeamPresenceState var0,
      ArticleMetadata var1,
      ArticleViewState$ConversationState var2,
      Integer var3,
      int var4,
      int var5,
      int var6,
      String var7,
      String var8,
      boolean var9,
      OpenMessengerResponse$NewConversationData$Cta var10,
      int var11,
      Object var12
   ) {
      if ((var11 & 1) != 0) {
         var1 = var0.articleMetadata;
      }

      if ((var11 & 2) != 0) {
         var2 = var0.conversationState;
      }

      if ((var11 & 4) != 0) {
         var3 = var0.subtitleText;
      }

      if ((var11 & 8) != 0) {
         var4 = var0.messageButtonText;
      }

      if ((var11 & 16) != 0) {
         var5 = var0.messageButtonIcon;
      }

      if ((var11 & 32) != 0) {
         var6 = var0.messageButtonColor;
      }

      if ((var11 & 64) != 0) {
         var7 = var0.metricPlace;
      }

      if ((var11 & 128) != 0) {
         var8 = var0.metricContext;
      }

      if ((var11 & 256) != 0) {
         var9 = var0.isFromSearchBrowse;
      }

      if ((var11 & 512) != 0) {
         var10 = var0.ctaData;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @NotNull
   @Override
   public String toString() {
      return "TeamPresenceState(articleMetadata="
         + this.articleMetadata
         + ", conversationState="
         + this.conversationState
         + ", subtitleText="
         + this.subtitleText
         + ", messageButtonText="
         + this.messageButtonText
         + ", messageButtonIcon="
         + this.messageButtonIcon
         + ", messageButtonColor="
         + this.messageButtonColor
         + ", metricPlace="
         + this.metricPlace
         + ", metricContext="
         + this.metricContext
         + ", isFromSearchBrowse="
         + this.isFromSearchBrowse
         + ", ctaData="
         + this.ctaData
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleMetadata == null ? 0 : this.articleMetadata.hashCode();
      var1 = var1 * 31 + (this.conversationState == null ? 0 : this.conversationState.hashCode());
      var1 = var1 * 31 + (this.subtitleText == null ? 0 : this.subtitleText.hashCode());
      var1 = var1 * 31 + Integer.hashCode(this.messageButtonText);
      var1 = var1 * 31 + Integer.hashCode(this.messageButtonIcon);
      var1 = var1 * 31 + Integer.hashCode(this.messageButtonColor);
      var1 = var1 * 31 + this.metricPlace.hashCode();
      var1 = var1 * 31 + this.metricContext.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isFromSearchBrowse);
      return var1 * 31 + (this.ctaData == null ? 0 : this.ctaData.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArticleViewState$TeamPresenceState)) {
         return false;
      } else {
         ArticleViewState$TeamPresenceState var2 = (ArticleViewState$TeamPresenceState)var1;
         if (!Intrinsics.areEqual(this.articleMetadata, var2.articleMetadata)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationState, var2.conversationState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subtitleText, var2.subtitleText)) {
            return false;
         } else if (this.messageButtonText != var2.messageButtonText) {
            return false;
         } else if (this.messageButtonIcon != var2.messageButtonIcon) {
            return false;
         } else if (this.messageButtonColor != var2.messageButtonColor) {
            return false;
         } else if (!Intrinsics.areEqual(this.metricPlace, var2.metricPlace)) {
            return false;
         } else if (!Intrinsics.areEqual(this.metricContext, var2.metricContext)) {
            return false;
         } else {
            return this.isFromSearchBrowse != var2.isFromSearchBrowse ? false : Intrinsics.areEqual(this.ctaData, var2.ctaData);
         }
      }
   }

   // $VF: synthetic method
   public static final ArticleViewState$TeamPresenceState access$getDefaultTeamPresenceState$cp() {
      return defaultTeamPresenceState;
   }

   static {
      int var0 = string.intercom_the_team_can_help_if_needed;
      int var1 = string.intercom_send_us_a_message;
      int var2 = drawable.intercom_new_conversation_send_button;
      defaultTeamPresenceState = new ArticleViewState$TeamPresenceState(
         null, null, var0, var1, var2, -16777216, "article", "style_human", false, null, 512, null
      );
   }
}
