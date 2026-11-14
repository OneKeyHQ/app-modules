package io.intercom.android.sdk.tickets;

import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.Ticket$ConversationButton;
import io.intercom.android.sdk.models.Ticket$ConversationButton$IconType;
import io.intercom.android.sdk.models.Ticket$Status;
import io.intercom.android.sdk.models.Ticket$TicketAttribute;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000H\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u001a\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\t\u001a\u00020\nH\u0002\u001a\f\u0010\u000f\u001a\u00020\u0010*\u00020\u0011H\u0002\u001a\u0014\u0010\u0012\u001a\u00020\u0010*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0000\u001a\u0014\u0010\u0015\u001a\u00020\u0010*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0000\u001a\f\u0010\u0016\u001a\u00020\u0013*\u00020\u0017H\u0000¨\u0006\u0018",
   d2 = {
         "computeTicketViewState",
         "Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "user",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "activeAdminsAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "launchedFrom",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "conversationButtonState",
         "Lio/intercom/android/sdk/tickets/ConversationButtonState;",
         "conversationButton",
         "Lio/intercom/android/sdk/models/Ticket$ConversationButton;",
         "isUnassignedOrBot",
         "",
         "Lio/intercom/android/sdk/models/Participant;",
         "isDone",
         "Lio/intercom/android/sdk/tickets/TicketStatus;",
         "currentTicketStatus",
         "isLoading",
         "toTicketStatus",
         "Lio/intercom/android/sdk/models/Ticket$Status;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketDetailReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailReducer.kt\nio/intercom/android/sdk/tickets/TicketDetailReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,92:1\n1563#2:93\n1634#2,3:94\n774#2:97\n865#2,2:98\n*S KotlinDebug\n*F\n+ 1 TicketDetailReducer.kt\nio/intercom/android/sdk/tickets/TicketDetailReducerKt\n*L\n32#1:93\n32#1:94,3\n42#1:97\n42#1:98,2\n*E\n"
)
public final class TicketDetailReducerKt {
   @NotNull
   public static final TicketDetailState$TicketDetailContentState computeTicketViewState(
      @NotNull Ticket var0, @NotNull UserIdentity var1, @NotNull List var2, @NotNull TicketLaunchedFrom var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "ticket");
      Intrinsics.checkNotNullParameter(var1, "user");
      Intrinsics.checkNotNullParameter(var2, "activeAdminsAvatars");
      Intrinsics.checkNotNullParameter(var3, "launchedFrom");
      String var10000 = var0.getTitle();
      Participant var10001 = var0.getAssignee().build();
      Intrinsics.checkNotNullExpressionValue(var10001, "build(...)");
      List var48;
      if (isUnassignedOrBot(var10001) && !var2.isEmpty()) {
         var48 = CollectionsKt.take(var2, 3);
      } else {
         Avatar var10003 = var0.getAssignee().build().getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10003, "getAvatar(...)");
         var48 = CollectionsKt.listOf(new AvatarWrapper(var10003, false, 2, null));
      }

      String var10002 = var0.getCurrentStatus().getStatusDetail();
      long var10004 = toTicketStatus(var0.getCurrentStatus()).recovered$6f15fcf6c761ad06();
      Iterable var4 = var0.getStatusList();
      long var19 = var10004;
      String var18 = "";
      String var17 = var10002;
      List var16 = var48;
      String var13 = var10000;
      int var5 = 0;
      Iterable var6 = var4;
      ArrayList var7 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var4, 10));
      int var8 = 0;

      for (Object var10 : var6) {
         Ticket$Status var11 = (Ticket$Status)var10;
         Collection var21 = var7;
         int var12 = 0;
         var21.add(
            new TicketTimelineCardState$ProgressSection(
               isDone(toTicketStatus(var11), toTicketStatus(var0.getCurrentStatus())),
               isLoading(toTicketStatus(var11), toTicketStatus(var0.getCurrentStatus()))
            )
         );
      }

      List var47 = (List)var7;
      String var10006 = var0.getCurrentStatus().getTitle();
      Long var10007 = var0.getLatestStatusUpdatedAt();
      Object var22 = null;
      Long var23 = var10007;
      String var24 = var10006;
      List var25 = var47;
      long var26 = var19;
      String var28 = var18;
      String var29 = var17;
      List var30 = var16;
      TicketTimelineCardState var49 = new TicketTimelineCardState(var30, var29, var28, var26, var25, var24, var23, (DefaultConstructorMarker)var22);
      var4 = var0.getAttributes();
      TicketTimelineCardState var14 = var49;
      var13 = var13;
      var5 = 0;
      var6 = var4;
      var7 = new ArrayList();
      var8 = 0;

      for (Object var43 : var6) {
         Ticket$TicketAttribute var44 = (Ticket$TicketAttribute)var43;
         int var45 = 0;
         if (var44.hasValue()) {
            var7.add(var43);
         }
      }

      List var15 = (List)var7;
      String var50 = var1.getEmail();
      Intrinsics.checkNotNullExpressionValue(var50, "getEmail(...)");
      ConversationButtonState var31 = conversationButtonState(var0.getConversationButton(), var3);
      String var32 = var0.getConversationId();
      String var33 = var50;
      List var34 = var15;
      TicketTimelineCardState var35 = var14;
      String var36 = var13;
      return new TicketDetailState$TicketDetailContentState(var36, var35, var34, var33, var32, var31);
   }

   // $VF: synthetic method
   public static TicketDetailState$TicketDetailContentState computeTicketViewState$default(
      Ticket var0, UserIdentity var1, List var2, TicketLaunchedFrom var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var2 = CollectionsKt.emptyList();
      }

      return computeTicketViewState(var0, var1, var2, var3);
   }

   private static final ConversationButtonState conversationButtonState(Ticket$ConversationButton var0, TicketLaunchedFrom var1) {
      ConversationButtonState var10000;
      boolean var10002 = !(var1 instanceof TicketLaunchedFrom$Conversation) && var0 != null;
      Ticket$ConversationButton$IconType var10003 = var0 != null ? var0.getIcon() : null;
      Integer var2;
      switch (var10003 == null ? -1 : TicketDetailReducerKt$WhenMappings.$EnumSwitchMapping$0[var10003.ordinal()]) {
         case 1:
            var2 = drawable.intercom_send_message_icon;
            break;
         case 2:
            var2 = drawable.intercom_messages_icon;
            break;
         default:
            var2 = null;
      }

      ActualString var10004;
      String var10006;
      label30: {
         ;
         if (var0 != null) {
            var10006 = var0.getText();
            if (var10006 != null) {
               break label30;
            }
         }

         var10006 = "";
      }

      var10004 = new ActualString(var10006);
      var10000 = new ConversationButtonState(var10002, var2, var10004);
      return var10000;
   }

   private static final boolean isUnassignedOrBot(Participant var0) {
      return Intrinsics.areEqual(var0, Participant.create("", "", "user", "", Avatar.create("", ""), false)) || var0.isBot();
   }

   public static final boolean isDone(@NotNull TicketStatus var0, @NotNull TicketStatus var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "currentTicketStatus");
      return var1.ordinal() >= var0.ordinal();
   }

   public static final boolean isLoading(@NotNull TicketStatus var0, @NotNull TicketStatus var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "currentTicketStatus");
      return var1.ordinal() == var0.ordinal() && var1.ordinal() < ArraysKt.getLastIndex(TicketStatus.values());
   }

   @NotNull
   public static final TicketStatus toTicketStatus(@NotNull Ticket$Status var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      String var1 = var0.getType();
      switch (var1.hashCode()) {
         case -882067636:
            if (var1.equals("waiting_on_customer")) {
               return TicketStatus.WaitingOnCustomer;
            }
            break;
         case -753541113:
            if (var1.equals("in_progress")) {
               return TicketStatus.InProgress;
            }
            break;
         case -341328904:
            if (var1.equals("resolved")) {
               return TicketStatus.Resolved;
            }
            break;
         case 348678395:
            if (var1.equals("submitted")) {
               return TicketStatus.Submitted;
            }
      }

      return TicketStatus.Submitted;
   }
}
