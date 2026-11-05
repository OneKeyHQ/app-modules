package io.intercom.android.sdk.models;

import android.app.Activity;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Intercom$Visibility;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.survey.model.SurveyData;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b(\b\u0081\b\u0018\u0000 <2\u00020\u0001:\u0001<B\u0085\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0015¢\u0006\u0004\b\u0017\u0010\u0018J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\t\u0010-\u001a\u00020\u0005HÆ\u0003J\t\u0010.\u001a\u00020\u0007HÆ\u0003J\t\u0010/\u001a\u00020\tHÆ\u0003J\t\u00100\u001a\u00020\tHÆ\u0003J\t\u00101\u001a\u00020\fHÆ\u0003J\t\u00102\u001a\u00020\fHÆ\u0003J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fHÆ\u0003J\u000f\u00104\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012HÆ\u0003J\u000b\u00105\u001a\u0004\u0018\u00010\u0015HÆ\u0003J\u000b\u00106\u001a\u0004\u0018\u00010\u0015HÆ\u0003J\u0087\u0001\u00107\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0015HÇ\u0001J\u0013\u00108\u001a\u00020\f2\b\u00109\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010:\u001a\u00020\u0007H×\u0001J\t\u0010;\u001a\u00020\u0013H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\r\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b$\u0010#R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0015¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0015¢\u0006\b\n\u0000\u001a\u0004\b+\u0010*¨\u0006=",
   d2 = {
         "Lio/intercom/android/sdk/models/OverlayState;",
         "",
         "surveyData",
         "Lio/intercom/android/sdk/survey/model/SurveyData;",
         "carousel",
         "Lio/intercom/android/sdk/models/carousel/Carousel;",
         "bottomPadding",
         "",
         "launcherVisibility",
         "Lio/intercom/android/sdk/Intercom$Visibility;",
         "inAppNotificationsVisibility",
         "carouselEnabled",
         "",
         "surveyEnabled",
         "conversations",
         "",
         "Lio/intercom/android/sdk/models/Conversation;",
         "dismissedPartIds",
         "",
         "",
         "resumedHostActivity",
         "Landroid/app/Activity;",
         "pausedHostActivity",
         "<init>",
         "(Lio/intercom/android/sdk/survey/model/SurveyData;Lio/intercom/android/sdk/models/carousel/Carousel;ILio/intercom/android/sdk/Intercom$Visibility;Lio/intercom/android/sdk/Intercom$Visibility;ZZLjava/util/List;Ljava/util/Set;Landroid/app/Activity;Landroid/app/Activity;)V",
         "getSurveyData",
         "()Lio/intercom/android/sdk/survey/model/SurveyData;",
         "getCarousel",
         "()Lio/intercom/android/sdk/models/carousel/Carousel;",
         "getBottomPadding",
         "()I",
         "getLauncherVisibility",
         "()Lio/intercom/android/sdk/Intercom$Visibility;",
         "getInAppNotificationsVisibility",
         "getCarouselEnabled",
         "()Z",
         "getSurveyEnabled",
         "getConversations",
         "()Ljava/util/List;",
         "getDismissedPartIds",
         "()Ljava/util/Set;",
         "getResumedHostActivity",
         "()Landroid/app/Activity;",
         "getPausedHostActivity",
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
         "component11",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class OverlayState {
   @NotNull
   public static final OverlayState$Companion Companion = new OverlayState$Companion(null);
   @NotNull
   private final SurveyData surveyData;
   @NotNull
   private final Carousel carousel;
   private final int bottomPadding;
   @NotNull
   private final Intercom$Visibility launcherVisibility;
   @NotNull
   private final Intercom$Visibility inAppNotificationsVisibility;
   private final boolean carouselEnabled;
   private final boolean surveyEnabled;
   @NotNull
   private final List conversations;
   @NotNull
   private final Set dismissedPartIds;
   @Nullable
   private final Activity resumedHostActivity;
   @Nullable
   private final Activity pausedHostActivity;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   public static final OverlayState NULL = new OverlayState(null, null, 0, null, null, false, false, null, null, null, null, 2047, null);

   public OverlayState(
      @NotNull SurveyData var1,
      @NotNull Carousel var2,
      int var3,
      @NotNull Intercom$Visibility var4,
      @NotNull Intercom$Visibility var5,
      boolean var6,
      boolean var7,
      @NotNull List var8,
      @NotNull Set var9,
      @Nullable Activity var10,
      @Nullable Activity var11
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "surveyData");
      Intrinsics.checkNotNullParameter(var2, "carousel");
      Intrinsics.checkNotNullParameter(var4, "launcherVisibility");
      Intrinsics.checkNotNullParameter(var5, "inAppNotificationsVisibility");
      Intrinsics.checkNotNullParameter(var8, "conversations");
      Intrinsics.checkNotNullParameter(var9, "dismissedPartIds");
      this.surveyData = var1;
      this.carousel = var2;
      this.bottomPadding = var3;
      this.launcherVisibility = var4;
      this.inAppNotificationsVisibility = var5;
      this.carouselEnabled = var6;
      this.surveyEnabled = var7;
      this.conversations = var8;
      this.dismissedPartIds = var9;
      this.resumedHostActivity = var10;
      this.pausedHostActivity = var11;
   }

   // $VF: synthetic method
   public OverlayState(
      SurveyData var1,
      Carousel var2,
      int var3,
      Intercom$Visibility var4,
      Intercom$Visibility var5,
      boolean var6,
      boolean var7,
      List var8,
      Set var9,
      Activity var10,
      Activity var11,
      int var12,
      DefaultConstructorMarker var13
   ) {
      this((((var12 & 1) != 0) ? (SurveyData) (SurveyData.Companion.getNULL()) : var1), (((var12 & 2) != 0) ? (Carousel) (Carousel.NULL) : var2), (((var12 & 4) != 0) ? (int) (0) : var3), (((var12 & 8) != 0) ? (Intercom$Visibility) (Intercom$Visibility.GONE) : var4), (((var12 & 16) != 0) ? (Intercom$Visibility) (Intercom$Visibility.VISIBLE) : var5), (((var12 & 32) != 0) ? (boolean) (true) : var6), (((var12 & 64) != 0) ? (boolean) (true) : var7), (((var12 & 128) != 0) ? (List) (CollectionsKt.emptyList()) : var8), (((var12 & 256) != 0) ? (Set) (SetsKt.emptySet()) : var9), (((var12 & 512) != 0) ? (Activity) (null) : var10), (((var12 & 1024) != 0) ? (Activity) (null) : var11));
   }

   @NotNull
   public final SurveyData getSurveyData() {
      return this.surveyData;
   }

   @NotNull
   public final Carousel getCarousel() {
      return this.carousel;
   }

   public final int getBottomPadding() {
      return this.bottomPadding;
   }

   @NotNull
   public final Intercom$Visibility getLauncherVisibility() {
      return this.launcherVisibility;
   }

   @NotNull
   public final Intercom$Visibility getInAppNotificationsVisibility() {
      return this.inAppNotificationsVisibility;
   }

   public final boolean getCarouselEnabled() {
      return this.carouselEnabled;
   }

   public final boolean getSurveyEnabled() {
      return this.surveyEnabled;
   }

   @NotNull
   public final List getConversations() {
      return this.conversations;
   }

   @NotNull
   public final Set getDismissedPartIds() {
      return this.dismissedPartIds;
   }

   @Nullable
   public final Activity getResumedHostActivity() {
      return this.resumedHostActivity;
   }

   @Nullable
   public final Activity getPausedHostActivity() {
      return this.pausedHostActivity;
   }

   @NotNull
   public final SurveyData component1() {
      return this.surveyData;
   }

   @NotNull
   public final Carousel component2() {
      return this.carousel;
   }

   public final int component3() {
      return this.bottomPadding;
   }

   @NotNull
   public final Intercom$Visibility component4() {
      return this.launcherVisibility;
   }

   @NotNull
   public final Intercom$Visibility component5() {
      return this.inAppNotificationsVisibility;
   }

   public final boolean component6() {
      return this.carouselEnabled;
   }

   public final boolean component7() {
      return this.surveyEnabled;
   }

   @NotNull
   public final List component8() {
      return this.conversations;
   }

   @NotNull
   public final Set component9() {
      return this.dismissedPartIds;
   }

   @Nullable
   public final Activity component10() {
      return this.resumedHostActivity;
   }

   @Nullable
   public final Activity component11() {
      return this.pausedHostActivity;
   }

   @NotNull
   public final OverlayState copy(
      @NotNull SurveyData var1,
      @NotNull Carousel var2,
      int var3,
      @NotNull Intercom$Visibility var4,
      @NotNull Intercom$Visibility var5,
      boolean var6,
      boolean var7,
      @NotNull List var8,
      @NotNull Set var9,
      @Nullable Activity var10,
      @Nullable Activity var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "surveyData");
      Intrinsics.checkNotNullParameter(var2, "carousel");
      Intrinsics.checkNotNullParameter(var4, "launcherVisibility");
      Intrinsics.checkNotNullParameter(var5, "inAppNotificationsVisibility");
      Intrinsics.checkNotNullParameter(var8, "conversations");
      Intrinsics.checkNotNullParameter(var9, "dismissedPartIds");
      return new OverlayState(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   // $VF: synthetic method
   public static OverlayState copy$default(
      OverlayState var0,
      SurveyData var1,
      Carousel var2,
      int var3,
      Intercom$Visibility var4,
      Intercom$Visibility var5,
      boolean var6,
      boolean var7,
      List var8,
      Set var9,
      Activity var10,
      Activity var11,
      int var12,
      Object var13
   ) {
      if ((var12 & 1) != 0) {
         var1 = var0.surveyData;
      }

      if ((var12 & 2) != 0) {
         var2 = var0.carousel;
      }

      if ((var12 & 4) != 0) {
         var3 = var0.bottomPadding;
      }

      if ((var12 & 8) != 0) {
         var4 = var0.launcherVisibility;
      }

      if ((var12 & 16) != 0) {
         var5 = var0.inAppNotificationsVisibility;
      }

      if ((var12 & 32) != 0) {
         var6 = var0.carouselEnabled;
      }

      if ((var12 & 64) != 0) {
         var7 = var0.surveyEnabled;
      }

      if ((var12 & 128) != 0) {
         var8 = var0.conversations;
      }

      if ((var12 & 256) != 0) {
         var9 = var0.dismissedPartIds;
      }

      if ((var12 & 512) != 0) {
         var10 = var0.resumedHostActivity;
      }

      if ((var12 & 1024) != 0) {
         var11 = var0.pausedHostActivity;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("OverlayState(surveyData=")
         .append(this.surveyData)
         .append(", carousel=")
         .append(this.carousel)
         .append(", bottomPadding=")
         .append(this.bottomPadding)
         .append(", launcherVisibility=")
         .append(this.launcherVisibility)
         .append(", inAppNotificationsVisibility=")
         .append(this.inAppNotificationsVisibility)
         .append(", carouselEnabled=")
         .append(this.carouselEnabled)
         .append(", surveyEnabled=")
         .append(this.surveyEnabled)
         .append(", conversations=")
         .append(this.conversations)
         .append(", dismissedPartIds=")
         .append(this.dismissedPartIds)
         .append(", resumedHostActivity=")
         .append(this.resumedHostActivity)
         .append(", pausedHostActivity=")
         .append(this.pausedHostActivity)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.surveyData.hashCode();
      var1 = var1 * 31 + this.carousel.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.bottomPadding);
      var1 = var1 * 31 + this.launcherVisibility.hashCode();
      var1 = var1 * 31 + this.inAppNotificationsVisibility.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.carouselEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.surveyEnabled);
      var1 = var1 * 31 + this.conversations.hashCode();
      var1 = var1 * 31 + this.dismissedPartIds.hashCode();
      var1 = var1 * 31 + (this.resumedHostActivity == null ? 0 : this.resumedHostActivity.hashCode());
      return var1 * 31 + (this.pausedHostActivity == null ? 0 : this.pausedHostActivity.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OverlayState)) {
         return false;
      } else {
         OverlayState var2 = (OverlayState)var1;
         if (!Intrinsics.areEqual(this.surveyData, var2.surveyData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.carousel, var2.carousel)) {
            return false;
         } else if (this.bottomPadding != var2.bottomPadding) {
            return false;
         } else if (this.launcherVisibility != var2.launcherVisibility) {
            return false;
         } else if (this.inAppNotificationsVisibility != var2.inAppNotificationsVisibility) {
            return false;
         } else if (this.carouselEnabled != var2.carouselEnabled) {
            return false;
         } else if (this.surveyEnabled != var2.surveyEnabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversations, var2.conversations)) {
            return false;
         } else if (!Intrinsics.areEqual(this.dismissedPartIds, var2.dismissedPartIds)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.resumedHostActivity, var2.resumedHostActivity)
               ? false
               : Intrinsics.areEqual(this.pausedHostActivity, var2.pausedHostActivity);
         }
      }
   }

   public OverlayState() {
      this(null, null, 0, null, null, false, false, null, null, null, null, 2047, null);
   }
}
