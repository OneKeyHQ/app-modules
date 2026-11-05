package io.intercom.android.sdk.m5.conversation.reducers;

import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Help;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$StartNewConversation;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$NoIndicator;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadCountIndicator;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadDotIndicator;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.conversation.states.PushNotificationsBannerState;
import io.intercom.android.sdk.m5.conversation.states.TicketProgressRowState;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Header;
import io.intercom.android.sdk.models.Header$Collapsed;
import io.intercom.android.sdk.models.Header$Collapsed$HeaderIconType;
import io.intercom.android.sdk.models.Space$Type;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import io.intercom.android.sdk.utilities.extensions.ConversationExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000J\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u001aV\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0000\u001a6\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\r\u001a\u00020\fH\u0000\u001a\u0010\u0010\u0015\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002\u001a\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\fH\u0002¨\u0006\u0019",
   d2 = {
         "reduceTopAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "isNewConversation",
         "",
         "hasPendingMessages",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "openResponseHeader",
         "Lio/intercom/android/sdk/models/Header;",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "unreadConversationsCount",
         "",
         "unreadTicketsCount",
         "launchMode",
         "Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "pushNotificationsBannerState",
         "Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "reduceHeaderMenuItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "getNavIcon",
         "getCappedUnreadCountString",
         "",
         "unreadCount",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHeaderReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeaderReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/HeaderReducerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,196:1\n1#2:197\n1563#3:198\n1634#3,3:199\n*S KotlinDebug\n*F\n+ 1 HeaderReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/HeaderReducerKt\n*L\n93#1:198\n93#1:199,3\n*E\n"
)
public final class HeaderReducerKt {
   @NotNull
   public static final TopAppBarUiState reduceTopAppBarUiState(
      boolean var0,
      boolean var1,
      @Nullable Conversation var2,
      @Nullable Header var3,
      @NotNull AppConfig var4,
      int var5,
      int var6,
      @NotNull LaunchMode var7,
      @Nullable PushNotificationsBannerState var8
   ) {
      Intrinsics.checkNotNullParameter(var4, "config");
      Intrinsics.checkNotNullParameter(var7, "launchMode");
      List var9 = reduceHeaderMenuItems(var4, var5, var0, var7, var6);
      if (var0 && var3 == null) {
         return TopAppBarUiState.recovered$c1f0d5c4f39cbc8e(
            TopAppBarUiState.Companion.getDefault(),
            null,
            getNavIcon(var7),
            null,
            null,
            null,
            false,
            null,
            var9,
            null,
            null,
            null,
            null,
            null,
            null,
            var8,
            16253,
            null
         );
      }

      Header var10000;
      label66: {
         if (var2 != null) {
            var10000 = var2.getHeader();
            if (var10000 != null) {
               break label66;
            }
         }

         var10000 = var3;
         if (var3 == null) {
            return TopAppBarUiState.recovered$c1f0d5c4f39cbc8e(
               TopAppBarUiState.Companion.getDefault(),
               null,
               getNavIcon(var7),
               null,
               null,
               null,
               false,
               null,
               var9,
               null,
               null,
               null,
               null,
               null,
               null,
               var8,
               16253,
               null
            );
         }
      }

      Header var10 = var10000;
      long var11 = ColorExtensionsKt.toComposeColor$default(var10.getBackgroundColor(), 0.0F, 1, null);
      long var13 = ColorExtensionsKt.toComposeColor$default(var10.getBackgroundColorDark(), 0.0F, 1, null);
      long var15 = ColorExtensionsKt.toComposeColor$default(var10.getForegroundPrimaryColor(), 0.0F, 1, null);
      long var17 = ColorExtensionsKt.toComposeColor$default(var10.getForegroundPrimaryColorDark(), 0.0F, 1, null);
      long var19 = ColorExtensionsKt.toComposeColor$default(var10.getForegroundSecondaryColor(), 0.0F, 1, null);
      long var21 = ColorExtensionsKt.toComposeColor$default(var10.getForegroundSecondaryColorDark(), 0.0F, 1, null);
      TicketProgressRowState var23 = (var2 != null ? var2.getTicket() : null) != null && ConversationExtensionsKt.hasNonTicketParts(var2) | var1
         ? new TicketProgressRowState(var2.getTicket().getTitle(), var2.getTicket().getCurrentStatus().getTitle())
         : null;
      Header$Collapsed var24 = var10.getCollapsed();
      int var25 = 0;
      StringProvider var68 = new ActualString(var24.getTitle());
      Integer var10001 = getNavIcon(var7);
      String var10002 = var24.getSubtitle();
      ActualString var70;
      if (var10002 != null) {
         String var26 = var10002;
         Integer var27 = var10001;
         StringProvider var28 = var68;
         int var29 = 0;
         ActualString var30 = new ActualString(var26);
         var68 = var28;
         var10001 = var27;
         var70 = var30;
      } else {
         var70 = null;
      }

      StringProvider var71 = var70;
      Header$Collapsed$HeaderIconType var10003 = var24.getIcon();
      Integer var73;
      if (var10003 != null) {
         Header$Collapsed$HeaderIconType var57 = var10003;
         StringProvider var64 = var71;
         Integer var58 = var10001;
         StringProvider var60 = var68;
         int var62 = 0;
         int var69;
         switch (var57) {
            case CLOCK:
               var69 = drawable.intercom_clock;
               break;
            case AI:
               var69 = drawable.intercom_ic_ai;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         Integer var31 = var69;
         var68 = var60;
         var10001 = var58;
         var71 = var64;
         var73 = var31;
      } else {
         var73 = null;
      }

      List var75;
      label54: {
         AvatarDetails var10004 = var24.getAvatarDetails();
         if (var10004 != null) {
            var75 = var10004.getAvatars();
            if (var75 != null) {
               Iterable var63 = var75;
               Integer var66 = var73;
               StringProvider var65 = var71;
               Integer var59 = var10001;
               StringProvider var61 = var68;
               int var32 = 0;
               Iterable var33 = var63;
               ArrayList var34 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var63, 10));
               int var35 = 0;

               for (Object var37 : var33) {
                  Avatar$Builder var38 = (Avatar$Builder)var37;
                  Collection var39 = var34;
                  int var40 = 0;
                  Avatar var72 = var38.build();
                  Intrinsics.checkNotNullExpressionValue(var72, "build(...)");
                  var39.add(new AvatarWrapper(var72, var10.getUseBotHeader()));
               }

               List var67 = (List)var34;
               var68 = var61;
               var10001 = var59;
               var71 = var65;
               var73 = var66;
               var75 = var67;
               break label54;
            }
         }

         var75 = CollectionsKt.emptyList();
      }

      boolean var10005 = var10.getDisplayActiveIndicator();
      Color var10008 = Color.recovered$f46ef195ad4a2363(var11);
      Color var10009 = Color.recovered$f46ef195ad4a2363(var13);
      Color var10010 = Color.recovered$f46ef195ad4a2363(var15);
      Color var10011 = Color.recovered$f46ef195ad4a2363(var17);
      Color var10012 = Color.recovered$f46ef195ad4a2363(var19);
      Color var10013 = Color.recovered$f46ef195ad4a2363(var21);
      Object var41 = null;
      PushNotificationsBannerState var42 = var8;
      Color var43 = var10013;
      Color var44 = var10012;
      Color var45 = var10011;
      Color var46 = var10010;
      Color var47 = var10009;
      Color var48 = var10008;
      List var49 = var9;
      TicketProgressRowState var50 = var23;
      boolean var51 = var10005;
      List var52 = var75;
      Integer var53 = var73;
      StringProvider var54 = var71;
      Integer var55 = var10001;
      StringProvider var56 = var68;
      return new TopAppBarUiState(
         var56, var55, var54, var53, var52, var51, var50, var49, var48, var47, var46, var45, var44, var43, var42, (DefaultConstructorMarker)var41
      );
   }

   @NotNull
   public static final List reduceHeaderMenuItems(@NotNull AppConfig var0, int var1, boolean var2, @NotNull LaunchMode var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "config");
      Intrinsics.checkNotNullParameter(var3, "launchMode");
      List var5 = CollectionsKt.createListBuilder();
      List var6 = var5;
      int var7 = 0;
      if (var3 == LaunchMode.PROGRAMMATIC) {
         String var8 = getCappedUnreadCountString(var1);
         HeaderMenuItem$TotalCountIndicator var9 = var1 == 0 && var4 > 0
            ? HeaderMenuItem$TotalCountIndicator$UnreadDotIndicator.INSTANCE
            : (var1 > 0 ? new HeaderMenuItem$TotalCountIndicator$UnreadCountIndicator(var8) : HeaderMenuItem$TotalCountIndicator$NoIndicator.INSTANCE);
         List var10000 = var6;
         HeaderMenuItem$TotalCountIndicator var10001 = var9;
         String var10002 = var8;
         boolean var10003 = false;
         String var10 = var0.getSpaceLabelIfExists(Space$Type.MESSAGES);
         StringProvider var10004;
         if (var10 != null) {
            String var11 = var10;
            boolean var12 = false;
            String var13 = var8;
            HeaderMenuItem$TotalCountIndicator var14 = var9;
            List var15 = var6;
            int var16 = 0;
            ActualString var17 = new ActualString(var11);
            var10000 = var15;
            var10001 = var14;
            var10002 = var13;
            var10003 = var12;
            var10004 = var17;
         } else {
            var10004 = new StringRes(string.intercom_messages_space_title, null, 2, null);
         }

         Object var18 = null;
         byte var19 = 4;
         StringProvider var20 = var10004;
         boolean var21 = var10003;
         String var22 = var10002;
         HeaderMenuItem$TotalCountIndicator var23 = var10001;
         var10000.add(new HeaderMenuItem$Messages(var23, var22, var21, var20, var19, (DefaultConstructorMarker)var18));
         if (AppConfigExtensionsKt.canStartNewConversation(var0)) {
            var6.add(new HeaderMenuItem$StartNewConversation(!var2, new StringRes(string.intercom_new_conversation, null, 2, null)));
         }

         if (var0.isSpaceEnabled(Space$Type.HELP)) {
            var6.add(new HeaderMenuItem$Help(true, new StringRes(string.intercom_help_space_title, null, 2, null)));
         }

         if (var0.isSpaceEnabled(Space$Type.TICKETS)) {
            var10000 = var6;
            boolean var35 = var4 > 0;
            boolean var36 = true;
            var10 = var0.getSpaceLabelIfExists(Space$Type.TICKETS);
            StringProvider var37;
            if (var10 != null) {
               String var28 = var10;
               boolean var30 = true;
               boolean var31 = var35;
               List var32 = var6;
               int var33 = 0;
               ActualString var29 = new ActualString(var28);
               var10000 = var32;
               var35 = var31;
               var36 = var30;
               var37 = var29;
            } else {
               var37 = new StringRes(string.intercom_tickets_space_title, null, 2, null);
            }

            StringProvider var24 = var37;
            boolean var25 = var36;
            boolean var26 = var35;
            var10000.add(new HeaderMenuItem$Tickets(var26, var25, var24));
         }
      }

      return CollectionsKt.build(var5);
   }

   private static final int getNavIcon(LaunchMode var0) {
      return var0 == LaunchMode.PROGRAMMATIC ? io.intercom.android.sdk.ui.R.drawable.intercom_ic_close : io.intercom.android.sdk.ui.R.drawable.intercom_ic_back;
   }

   private static final String getCappedUnreadCountString(int var0) {
      return var0 > 9 ? "9+" : (var0 == 0 ? "" : String.valueOf(var0));
   }
}
