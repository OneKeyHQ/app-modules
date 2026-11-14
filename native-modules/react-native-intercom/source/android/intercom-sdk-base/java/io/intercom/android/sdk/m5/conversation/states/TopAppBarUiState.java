package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b5\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 P2\u00020\u0001:\u0001PB·\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\t\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0019¢\u0006\u0004\b\u001a\u0010\u001bJ\t\u00103\u001a\u00020\u0003HÆ\u0003J\u0010\u00104\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u001fJ\u000b\u00105\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u00106\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u001fJ\u000f\u00107\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003J\t\u00108\u001a\u00020\fHÆ\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u000eHÆ\u0003J\u000f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00100\tHÆ\u0003J\u0010\u0010;\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\b<J\u0010\u0010=\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\b>J\u0010\u0010?\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\b@J\u0010\u0010A\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\bBJ\u0010\u0010C\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\bDJ\u0010\u0010E\u001a\u0004\u0018\u00010\u0012HÆ\u0003¢\u0006\u0002\bFJ\u000b\u0010G\u001a\u0004\u0018\u00010\u0019HÆ\u0003JÈ\u0001\u0010H\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\t2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0019HÇ\u0001¢\u0006\u0004\bI\u0010JJ\u0013\u0010K\u001a\u00020\f2\b\u0010L\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010M\u001a\u00020\u0005H×\u0001J\t\u0010N\u001a\u00020OH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010 \u001a\u0004\b\u001e\u0010\u001fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001dR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010 \u001a\u0004\b\"\u0010\u001fR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\t¢\u0006\b\n\u0000\u001a\u0004\b)\u0010$R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b,\u0010+R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b-\u0010+R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b.\u0010+R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b/\u0010+R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b0\u0010+R\u0013\u0010\u0018\u001a\u0004\u0018\u00010\u0019¢\u0006\b\n\u0000\u001a\u0004\b1\u00102¨\u0006Q",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "",
         "title",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "navIcon",
         "",
         "subTitle",
         "subTitleLeadingIcon",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "displayActiveIndicator",
         "",
         "ticketStatusState",
         "Lio/intercom/android/sdk/m5/conversation/states/TicketProgressRowState;",
         "headerMenuItems",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "backgroundColorDark",
         "contentColor",
         "contentColorDark",
         "subTitleColor",
         "subTitleColorDark",
         "pushNotificationsBannerState",
         "Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/StringProvider;Ljava/lang/Integer;Lio/intercom/android/sdk/ui/common/StringProvider;Ljava/lang/Integer;Ljava/util/List;ZLio/intercom/android/sdk/m5/conversation/states/TicketProgressRowState;Ljava/util/List;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getTitle",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getNavIcon",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getSubTitle",
         "getSubTitleLeadingIcon",
         "getAvatars",
         "()Ljava/util/List;",
         "getDisplayActiveIndicator",
         "()Z",
         "getTicketStatusState",
         "()Lio/intercom/android/sdk/m5/conversation/states/TicketProgressRowState;",
         "getHeaderMenuItems",
         "getBackgroundColor-QN2ZGVo",
         "()Landroidx/compose/ui/graphics/Color;",
         "getBackgroundColorDark-QN2ZGVo",
         "getContentColor-QN2ZGVo",
         "getContentColorDark-QN2ZGVo",
         "getSubTitleColor-QN2ZGVo",
         "getSubTitleColorDark-QN2ZGVo",
         "getPushNotificationsBannerState",
         "()Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component9-QN2ZGVo",
         "component10",
         "component10-QN2ZGVo",
         "component11",
         "component11-QN2ZGVo",
         "component12",
         "component12-QN2ZGVo",
         "component13",
         "component13-QN2ZGVo",
         "component14",
         "component14-QN2ZGVo",
         "component15",
         "copy",
         "copy-N4y9b34",
         "(Lio/intercom/android/sdk/ui/common/StringProvider;Ljava/lang/Integer;Lio/intercom/android/sdk/ui/common/StringProvider;Ljava/lang/Integer;Ljava/util/List;ZLio/intercom/android/sdk/m5/conversation/states/TicketProgressRowState;Ljava/util/List;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;)Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TopAppBarUiState {
   @NotNull
   public static final TopAppBarUiState$Companion Companion = new TopAppBarUiState$Companion(null);
   @NotNull
   private final StringProvider title;
   @Nullable
   private final Integer navIcon;
   @Nullable
   private final StringProvider subTitle;
   @Nullable
   private final Integer subTitleLeadingIcon;
   @NotNull
   private final List avatars;
   private final boolean displayActiveIndicator;
   @Nullable
   private final TicketProgressRowState ticketStatusState;
   @NotNull
   private final List headerMenuItems;
   @Nullable
   private final Color backgroundColor;
   @Nullable
   private final Color backgroundColorDark;
   @Nullable
   private final Color contentColor;
   @Nullable
   private final Color contentColorDark;
   @Nullable
   private final Color subTitleColor;
   @Nullable
   private final Color subTitleColorDark;
   @Nullable
   private final PushNotificationsBannerState pushNotificationsBannerState;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final TopAppBarUiState recovered$37862cdc11cc58d9 = new TopAppBarUiState(
      new ActualString(""),
      null,
      null,
      null,
      CollectionsKt.emptyList(),
      false,
      null,
      CollectionsKt.emptyList(),
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      16130,
      null
   );

   private TopAppBarUiState(
      StringProvider var1,
      Integer var2,
      StringProvider var3,
      Integer var4,
      List var5,
      boolean var6,
      TicketProgressRowState var7,
      List var8,
      Color var9,
      Color var10,
      Color var11,
      Color var12,
      Color var13,
      Color var14,
      PushNotificationsBannerState var15
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var5, "avatars");
      Intrinsics.checkNotNullParameter(var8, "headerMenuItems");
      this.title = var1;
      this.navIcon = var2;
      this.subTitle = var3;
      this.subTitleLeadingIcon = var4;
      this.avatars = var5;
      this.displayActiveIndicator = var6;
      this.ticketStatusState = var7;
      this.headerMenuItems = var8;
      this.backgroundColor = var9;
      this.backgroundColorDark = var10;
      this.contentColor = var11;
      this.contentColorDark = var12;
      this.subTitleColor = var13;
      this.subTitleColorDark = var14;
      this.pushNotificationsBannerState = var15;
   }

   // $VF: synthetic method
   public TopAppBarUiState(
      StringProvider var1,
      Integer var2,
      StringProvider var3,
      Integer var4,
      List var5,
      boolean var6,
      TicketProgressRowState var7,
      List var8,
      Color var9,
      Color var10,
      Color var11,
      Color var12,
      Color var13,
      Color var14,
      PushNotificationsBannerState var15,
      int var16,
      DefaultConstructorMarker var17
   ) {
      if ((var16 & 2) != 0) {
         var2 = null;
      }

      if ((var16 & 32) != 0) {
         var6 = false;
      }

      if ((var16 & 64) != 0) {
         var7 = null;
      }

      if ((var16 & 128) != 0) {
         var8 = CollectionsKt.emptyList();
      }

      if ((var16 & 256) != 0) {
         var9 = null;
      }

      if ((var16 & 512) != 0) {
         var10 = null;
      }

      if ((var16 & 1024) != 0) {
         var11 = null;
      }

      if ((var16 & 2048) != 0) {
         var12 = null;
      }

      if ((var16 & 4096) != 0) {
         var13 = null;
      }

      if ((var16 & 8192) != 0) {
         var14 = null;
      }

      if ((var16 & 16384) != 0) {
         var15 = null;
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, null);
   }

   @NotNull
   public final StringProvider getTitle() {
      return this.title;
   }

   @Nullable
   public final Integer getNavIcon() {
      return this.navIcon;
   }

   @Nullable
   public final StringProvider getSubTitle() {
      return this.subTitle;
   }

   @Nullable
   public final Integer getSubTitleLeadingIcon() {
      return this.subTitleLeadingIcon;
   }

   @NotNull
   public final List getAvatars() {
      return this.avatars;
   }

   public final boolean getDisplayActiveIndicator() {
      return this.displayActiveIndicator;
   }

   @Nullable
   public final TicketProgressRowState getTicketStatusState() {
      return this.ticketStatusState;
   }

   @NotNull
   public final List getHeaderMenuItems() {
      return this.headerMenuItems;
   }

   @Nullable
   public final Color recovered$4d97b5da718de5d3() {
      return this.backgroundColor;
   }

   @Nullable
   public final Color recovered$cef678816fbbfac0() {
      return this.backgroundColorDark;
   }

   @Nullable
   public final Color recovered$7cce2490cba0e529() {
      return this.contentColor;
   }

   @Nullable
   public final Color recovered$9043379a6aac6e48() {
      return this.contentColorDark;
   }

   @Nullable
   public final Color recovered$cef6cdac25aafd3e() {
      return this.subTitleColor;
   }

   @Nullable
   public final Color recovered$2fa8d65c338be95f() {
      return this.subTitleColorDark;
   }

   @Nullable
   public final PushNotificationsBannerState getPushNotificationsBannerState() {
      return this.pushNotificationsBannerState;
   }

   @NotNull
   public final StringProvider component1() {
      return this.title;
   }

   @Nullable
   public final Integer component2() {
      return this.navIcon;
   }

   @Nullable
   public final StringProvider component3() {
      return this.subTitle;
   }

   @Nullable
   public final Integer component4() {
      return this.subTitleLeadingIcon;
   }

   @NotNull
   public final List component5() {
      return this.avatars;
   }

   public final boolean component6() {
      return this.displayActiveIndicator;
   }

   @Nullable
   public final TicketProgressRowState component7() {
      return this.ticketStatusState;
   }

   @NotNull
   public final List component8() {
      return this.headerMenuItems;
   }

   @Nullable
   public final Color recovered$5dd4387e647f1480() {
      return this.backgroundColor;
   }

   @Nullable
   public final Color recovered$d263ae643020ff97() {
      return this.backgroundColorDark;
   }

   @Nullable
   public final Color recovered$baf2f483c64ef276() {
      return this.contentColor;
   }

   @Nullable
   public final Color recovered$bd0135ece50ff394() {
      return this.contentColorDark;
   }

   @Nullable
   public final Color recovered$f9ab23cd9a0e04a5() {
      return this.subTitleColor;
   }

   @Nullable
   public final Color recovered$3755dfbe9a62fbbc() {
      return this.subTitleColorDark;
   }

   @Nullable
   public final PushNotificationsBannerState component15() {
      return this.pushNotificationsBannerState;
   }

   @NotNull
   public final TopAppBarUiState recovered$4636b254eb0572d1(
      @NotNull StringProvider var1,
      @Nullable Integer var2,
      @Nullable StringProvider var3,
      @Nullable Integer var4,
      @NotNull List var5,
      boolean var6,
      @Nullable TicketProgressRowState var7,
      @NotNull List var8,
      @Nullable Color var9,
      @Nullable Color var10,
      @Nullable Color var11,
      @Nullable Color var12,
      @Nullable Color var13,
      @Nullable Color var14,
      @Nullable PushNotificationsBannerState var15
   ) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var5, "avatars");
      Intrinsics.checkNotNullParameter(var8, "headerMenuItems");
      return new TopAppBarUiState(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, null);
   }

   // $VF: synthetic method
   public static TopAppBarUiState recovered$c1f0d5c4f39cbc8e(
      TopAppBarUiState var0,
      StringProvider var1,
      Integer var2,
      StringProvider var3,
      Integer var4,
      List var5,
      boolean var6,
      TicketProgressRowState var7,
      List var8,
      Color var9,
      Color var10,
      Color var11,
      Color var12,
      Color var13,
      Color var14,
      PushNotificationsBannerState var15,
      int var16,
      Object var17
   ) {
      if ((var16 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var16 & 2) != 0) {
         var2 = var0.navIcon;
      }

      if ((var16 & 4) != 0) {
         var3 = var0.subTitle;
      }

      if ((var16 & 8) != 0) {
         var4 = var0.subTitleLeadingIcon;
      }

      if ((var16 & 16) != 0) {
         var5 = var0.avatars;
      }

      if ((var16 & 32) != 0) {
         var6 = var0.displayActiveIndicator;
      }

      if ((var16 & 64) != 0) {
         var7 = var0.ticketStatusState;
      }

      if ((var16 & 128) != 0) {
         var8 = var0.headerMenuItems;
      }

      if ((var16 & 256) != 0) {
         var9 = var0.backgroundColor;
      }

      if ((var16 & 512) != 0) {
         var10 = var0.backgroundColorDark;
      }

      if ((var16 & 1024) != 0) {
         var11 = var0.contentColor;
      }

      if ((var16 & 2048) != 0) {
         var12 = var0.contentColorDark;
      }

      if ((var16 & 4096) != 0) {
         var13 = var0.subTitleColor;
      }

      if ((var16 & 8192) != 0) {
         var14 = var0.subTitleColorDark;
      }

      if ((var16 & 16384) != 0) {
         var15 = var0.pushNotificationsBannerState;
      }

      return var0.recovered$4636b254eb0572d1(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("TopAppBarUiState(title=")
         .append(this.title)
         .append(", navIcon=")
         .append(this.navIcon)
         .append(", subTitle=")
         .append(this.subTitle)
         .append(", subTitleLeadingIcon=")
         .append(this.subTitleLeadingIcon)
         .append(", avatars=")
         .append(this.avatars)
         .append(", displayActiveIndicator=")
         .append(this.displayActiveIndicator)
         .append(", ticketStatusState=")
         .append(this.ticketStatusState)
         .append(", headerMenuItems=")
         .append(this.headerMenuItems)
         .append(", backgroundColor=")
         .append(this.backgroundColor)
         .append(", backgroundColorDark=")
         .append(this.backgroundColorDark)
         .append(", contentColor=")
         .append(this.contentColor)
         .append(", contentColorDark=");
      var1.append(this.contentColorDark)
         .append(", subTitleColor=")
         .append(this.subTitleColor)
         .append(", subTitleColorDark=")
         .append(this.subTitleColorDark)
         .append(", pushNotificationsBannerState=")
         .append(this.pushNotificationsBannerState)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + (this.navIcon == null ? 0 : this.navIcon.hashCode());
      var1 = var1 * 31 + (this.subTitle == null ? 0 : this.subTitle.hashCode());
      var1 = var1 * 31 + (this.subTitleLeadingIcon == null ? 0 : this.subTitleLeadingIcon.hashCode());
      var1 = var1 * 31 + this.avatars.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.displayActiveIndicator);
      var1 = var1 * 31 + (this.ticketStatusState == null ? 0 : this.ticketStatusState.hashCode());
      var1 = var1 * 31 + this.headerMenuItems.hashCode();
      var1 = var1 * 31 + (this.backgroundColor == null ? 0 : Color.recovered$87e060624084b26b(this.backgroundColor.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.backgroundColorDark == null ? 0 : Color.recovered$87e060624084b26b(this.backgroundColorDark.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.contentColor == null ? 0 : Color.recovered$87e060624084b26b(this.contentColor.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.contentColorDark == null ? 0 : Color.recovered$87e060624084b26b(this.contentColorDark.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.subTitleColor == null ? 0 : Color.recovered$87e060624084b26b(this.subTitleColor.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.subTitleColorDark == null ? 0 : Color.recovered$87e060624084b26b(this.subTitleColorDark.recovered$45f9de7b85e0e0b5()));
      return var1 * 31 + (this.pushNotificationsBannerState == null ? 0 : this.pushNotificationsBannerState.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TopAppBarUiState)) {
         return false;
      } else {
         TopAppBarUiState var2 = (TopAppBarUiState)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.navIcon, var2.navIcon)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subTitle, var2.subTitle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subTitleLeadingIcon, var2.subTitleLeadingIcon)) {
            return false;
         } else if (!Intrinsics.areEqual(this.avatars, var2.avatars)) {
            return false;
         } else if (this.displayActiveIndicator != var2.displayActiveIndicator) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticketStatusState, var2.ticketStatusState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.headerMenuItems, var2.headerMenuItems)) {
            return false;
         } else if (!Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.backgroundColorDark, var2.backgroundColorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.contentColor, var2.contentColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.contentColorDark, var2.contentColorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subTitleColor, var2.subTitleColor)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.subTitleColorDark, var2.subTitleColorDark)
               ? false
               : Intrinsics.areEqual(this.pushNotificationsBannerState, var2.pushNotificationsBannerState);
         }
      }
   }

   // $VF: synthetic method
   public TopAppBarUiState(
      StringProvider var1,
      Integer var2,
      StringProvider var3,
      Integer var4,
      List var5,
      boolean var6,
      TicketProgressRowState var7,
      List var8,
      Color var9,
      Color var10,
      Color var11,
      Color var12,
      Color var13,
      Color var14,
      PushNotificationsBannerState var15,
      DefaultConstructorMarker var16
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   // $VF: synthetic method
   public static final TopAppBarUiState access$getDefault$cp() {
      return recovered$37862cdc11cc58d9;
   }
}
