package io.intercom.android.sdk.m5.conversation.reducers;

import android.text.format.DateUtils;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$AskedAboutRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BigTicketRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$DayDividerRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$EventRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$MergedConversationRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$NewMessagesRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$NoteCardRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$PostCardRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TicketStatusRow;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.EventLabelSegment;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.EventContent;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.tickets.TicketDetailReducerKt;
import io.intercom.android.sdk.tickets.TicketLaunchedFrom$Conversation;
import io.intercom.android.sdk.utilities.extensions.ConversationExtensionsKt;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000R\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u001a&\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000\u001a\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\u0000\u001a\u000e\u0010\u000b\u001a\u00020\f*\u0004\u0018\u00010\fH\u0002\u001a\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0000\u001a\u001c\u0010\u0011\u001a\u00020\u0012*\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012H\u0002\u001a\u0018\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u0006H\u0000\u001a\u0010\u0010\u0015\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0010H\u0002\u001a$\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00190\u0018H\u0002\u001a\u001e\u0010\u001a\u001a\u00020\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00100\u00012\u0006\u0010\u001d\u001a\u00020\u001eH\u0000¨\u0006\u001f",
   d2 = {
         "reduceMessages",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "clientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "markLastNonSelfPart",
         "rows",
         "pinned",
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "buildSenderLabel",
         "",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "shouldShowQuickReplies",
         "",
         "isLastPart",
         "isAdminOrAltParticipant",
         "reduceEvent",
         "hasPendingMessageAfter",
         "pendingMessages",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage;",
         "getGroupingPosition",
         "Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "allParts",
         "index",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationPartsReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationPartsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ConversationPartsReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,319:1\n827#2:320\n855#2,2:321\n785#2:323\n796#2:324\n1878#2,2:325\n797#2,2:327\n1880#2:329\n799#2:330\n1491#2:331\n1516#2,3:332\n1519#2,3:342\n1869#2:345\n1563#2:346\n1634#2,3:347\n1573#2:350\n1604#2,4:351\n1870#2:355\n388#2,7:356\n1573#2:363\n1604#2,4:364\n1563#2:369\n1634#2,3:370\n384#3,7:335\n1#4:368\n*S KotlinDebug\n*F\n+ 1 ConversationPartsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ConversationPartsReducerKt\n*L\n52#1:320\n52#1:321,2\n56#1:323\n56#1:324\n56#1:325,2\n56#1:327,2\n56#1:329\n56#1:330\n69#1:331\n69#1:332,3\n69#1:342,3\n75#1:345\n83#1:346\n83#1:347,3\n88#1:350\n88#1:351,4\n75#1:355\n202#1:356,7\n210#1:363\n210#1:364,4\n274#1:369\n274#1:370,3\n69#1:335,7\n*E\n"
)
public final class ConversationPartsReducerKt {
   @NotNull
   public static final List reduceMessages(@NotNull ConversationClientState var0, @NotNull UserIdentity var1, @NotNull AppConfig var2) {
      Intrinsics.checkNotNullParameter(var0, "clientState");
      Intrinsics.checkNotNullParameter(var1, "userIdentity");
      Intrinsics.checkNotNullParameter(var2, "config");
      Conversation var10000 = var0.getConversation();
      if (var10000 == null) {
         return CollectionsKt.emptyList();
      }

      Conversation var3 = var10000;
      if (var3.getTicket() != null && !ConversationExtensionsKt.hasNonTicketParts(var3) && var0.getPendingMessages().isEmpty()) {
         return CollectionsKt.listOf(
            new ContentRow$BigTicketRow(
               TicketDetailReducerKt.computeTicketViewState$default(
                  var3.getTicket(), var1, null, new TicketLaunchedFrom$Conversation(var3.getTicket()), 4, null
               ),
               var3.getTicket().getId()
            )
         );
      }

      List var5 = CollectionsKt.createListBuilder();
      List var6 = var5;
      int var7 = 0;
      Object var8 = null;
      Iterable var9 = var3.parts();
      int var10 = 0;
      Iterable var11 = var9;
      ArrayList var12 = new ArrayList();
      int var13 = 0;

      for (Object var15 : var11) {
         Part var16 = (Part)var15;
         int var17 = 0;
         List var107 = var16.getReplyOptions();
         Intrinsics.checkNotNullExpressionValue(var107, "getReplyOptions(...)");
         if (var107.isEmpty() || !var3.getPreventEndUserReplies()) {
            var12.add(var15);
         }
      }

      var9 = (Iterable & List)var12;
      var10 = 0;
      var11 = var9;
      var12 = new ArrayList();
      var13 = 0;
      Iterable var65 = var11;
      int var68 = 0;
      int var71 = 0;

      for (Object var18 : var65) {
         int var19 = var71++;
         if (var19 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Object var20 = var18;
         int var21 = var19;
         int var22 = 0;
         Part var23 = (Part)var20;
         int var24 = var21;
         int var25 = 0;
         if (var23.getMessageStyle() != MessageStyle.QUICK_REPLY
            || var24 == CollectionsKt.getLastIndex(var3.parts()) && !hasPendingMessageAfter(var23, var0.getPendingMessages())) {
            var12.add(var20);
         }
      }

      List recoveredFilteredParts = (List)var12;
      var9 = recoveredFilteredParts;
      List var52 = recoveredFilteredParts;
      int var56 = 0;
      var8 = CollectionsKt.lastOrNull(var52);
      var9 = var9;
      var10 = 0;
      var11 = var9;
      Map destination$iv$ivx = new LinkedHashMap();
      var13 = 0;

      for (Object var69 : var11) {
         Part var72 = (Part)var69;
         int var77 = 0;
         Calendar var109 = Calendar.getInstance();
         Intrinsics.checkNotNullExpressionValue(var109, "getInstance(...)");
         Calendar var80 = var109;
         var80.setTime(new Date(var72.getCreatedAt() * 1000));
         Object var83 = TuplesKt.to(var80.get(1), var80.get(6));
         Map var86 = destination$iv$ivx;
         int var89 = 0;
         Object var92 = var86.get(var83);
         Object var110;
         if (var92 == null) {
            int var95 = 0;
            Object var96 = new ArrayList();
            var86.put(var83, var96);
            var110 = var96;
         } else {
            var110 = var92;
         }

         List var73 = (List)var110;
         var73.add(var69);
      }

      var9 = destination$iv$ivx.values();
      var10 = 0;

      for (Object var61 : var9) {
         List var64 = (List)var61;
         int var67 = 0;
         boolean var70 = DateUtils.isToday(((Part)CollectionsKt.first(var64)).getCreatedAt() * 1000);
         if (!((Part)CollectionsKt.first(var64)).isInitialMessage() || !PartExtensionsKt.isLinkCard((Part)CollectionsKt.first(var64)) && !var70) {
            var6.add(new ContentRow$DayDividerRow(((Part)CollectionsKt.first(var64)).getCreatedAt()));
         }

         Collection var111 = var64;
         Iterable var74 = var0.getPendingMessages().values();
         Collection var78 = var111;
         int var81 = 0;
         Iterable var84 = var74;
         var destination$iv$ivxx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var74, 10));
         int var90 = 0;

         for (Object var97 : var84) {
            PendingMessage var99 = (PendingMessage)var97;
            Collection var101 = destination$iv$ivxx;
            int var26 = 0;
            var101.add(var99.getPart());
         }

         List var27 = CollectionsKt.plus(var78, (Iterable & List)destination$iv$ivxx);
         List var75 = CollectionsKt.createListBuilder();
         List var82 = var75;
         var78 = var6;
         int var85 = 0;
         Iterable recoveredParts = var64;
         var90 = 0;
         Iterable var94 = recoveredParts;
         Collection destination$iv$ivxxx = new ArrayList(CollectionsKt.collectionSizeOrDefault(recoveredParts, 10));
         int var100 = 0;
         int var102 = 0;

         for (Object var29 : var94) {
            int var30 = var102++;
            if (var30 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Part var31 = (Part)var29;
            int var32 = var30;
            Collection var33 = destination$iv$ivxxx;
            int var34 = 0;
            if (var0.getNewMessageId() != null && Intrinsics.areEqual(var0.getNewMessageId(), var31.getId())) {
               String var10003 = var31.getId();
               Intrinsics.checkNotNullExpressionValue(var10003, "getId(...)");
               var82.add(new ContentRow$NewMessagesRow(var10003));
            }

            boolean var112;
            if (var31.isInitialMessage() && PartExtensionsKt.isLinkCard(var31)) {
               var112 = var82.add(new ContentRow$AskedAboutRow(var31));
            } else if (var31.isEvent()) {
               var112 = var82.add(reduceEvent(var31));
            } else if (var31.getMessageStyle() == MessageStyle.POST) {
               var112 = var82.add(new ContentRow$PostCardRow(var31, var2.getName()));
            } else if (var31.getMessageStyle() == MessageStyle.NOTE) {
               var112 = var82.add(new ContentRow$NoteCardRow(var31, var2.getName()));
            } else if (var31.getMessageStyle() == MessageStyle.FIN_ANSWER) {
               ContentRow$FinAnswerRow var35 = new ContentRow$FinAnswerRow(
                  var31, getGroupingPosition(var27, var32), new SenderMetadata(buildSenderLabel(var31), false, 2, null)
               );
               var112 = var82.add(var35);
            } else if (PartExtensionsKt.isQuickReplyOnly(var31)) {
               List var113 = var31.getReplyOptions();
               Intrinsics.checkNotNullExpressionValue(var113, "getReplyOptions(...)");
               String var10004 = var31.getId();
               Intrinsics.checkNotNullExpressionValue(var10004, "getId(...)");
               var112 = var82.add(new ContentRow$QuickRepliesRow(var113, var10004));
            } else {
               boolean var103 = isAdminOrAltParticipant(var31, var1);
               boolean var36 = Intrinsics.areEqual(var31.getId(), (Part)var8 != null ? ((Part)var8).getId() : null)
                  && var0.getPendingMessages().isEmpty()
                  && !var0.getFinStreamingData().isFinStreaming()
                  && Intrinsics.areEqual(var0.getCurrentlyTypingState(), new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null));
               Part var37 = var31;
               Part var38 = var37;
               int var39 = 0;
               var38.setParentConversation(var3);
               List var41 = var0.getLoadingAttributeIdentifiers();
               List var42 = var0.getFailedAttributeIdentifiers();
               boolean var43 = var103;
               boolean var44 = var36;
               Part var45 = var37;
               ContentRow$BubbleMessageRow$PartWrapper var40 = new ContentRow$BubbleMessageRow$PartWrapper(var45, var44, var43, var42, var41);
               GroupingPosition var105 = getGroupingPosition(var27, var32);
               SenderMetadata var106 = var103 ? new SenderMetadata(buildSenderLabel(var31), false, 2, null) : null;
               ContentRow$BubbleMessageRow var104 = new ContentRow$BubbleMessageRow(var40, var105, null, false, false, false, var106, 48, null);
               if (shouldShowQuickReplies(var31, var36, var103)) {
                  var82.add(var104);
                  List var114 = var31.getReplyOptions();
                  Intrinsics.checkNotNullExpressionValue(var114, "getReplyOptions(...)");
                  String var115 = var31.getId();
                  Intrinsics.checkNotNullExpressionValue(var115, "getId(...)");
                  var112 = var82.add(new ContentRow$QuickRepliesRow(var114, var115));
               } else {
                  var112 = var82.add(var104);
               }
            }

            var33.add(var112);
         }

         var78.addAll(CollectionsKt.build(var75));
      }

      return CollectionsKt.build(var5);
   }

   @NotNull
   public static final List markLastNonSelfPart(@NotNull List var0) {
      Intrinsics.checkNotNullParameter(var0, "rows");
      List var2 = var0;
      int var3 = 0;
      ListIterator var4 = var2.listIterator(var2.size());

      int var10000;
      while (true) {
         if (!var4.hasPrevious()) {
            var10000 = -1;
            break;
         }

         ContentRow var5 = (ContentRow)var4.previous();
         int var6 = 0;
         if (var5 instanceof ContentRow$BubbleMessageRow && ((ContentRow$BubbleMessageRow)var5).getPartWrapper().isAdminOrAltParticipant()
            || var5 instanceof ContentRow$FinAnswerRow) {
            var10000 = var4.nextIndex();
            break;
         }
      }

      int var1 = var10000;
      List var21;
      if (var1 == -1) {
         var21 = var0;
      } else {
         Iterable var16 = var0;
         var3 = 0;
         Iterable var18 = var16;
         ArrayList var19 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var16, 10));
         int var20 = 0;
         int var7 = 0;

         for (Object var9 : var18) {
            int var10 = var7++;
            if (var10 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            ContentRow var11 = (ContentRow)var9;
            int var12 = var10;
            Collection var15 = var19;
            int var13 = 0;
            ContentRow var22;
            if (var12 != var1) {
               var22 = var11;
            } else {
               ContentRow var14 = var11;
               var22 = var14 instanceof ContentRow$BubbleMessageRow
                  ? ContentRow$BubbleMessageRow.copy$default(
                     (ContentRow$BubbleMessageRow)var11,
                     null,
                     null,
                     null,
                     false,
                     false,
                     false,
                     pinned(((ContentRow$BubbleMessageRow)var11).getSenderMetadata()),
                     63,
                     null
                  )
                  : (
                     var14 instanceof ContentRow$FinAnswerRow
                        ? ContentRow$FinAnswerRow.copy$default(
                           (ContentRow$FinAnswerRow)var11, null, null, pinned(((ContentRow$FinAnswerRow)var11).getSenderMetadata()), 3, null
                        )
                        : var11
                  );
            }

            var15.add(var22);
         }

         var21 = (List)var19;
      }

      return var21;
   }

   private static final SenderMetadata pinned(SenderMetadata var0) {
      if (var0 != null) {
         SenderMetadata var10000 = SenderMetadata.copy$default(var0, null, true, 1, null);
         if (var10000 != null) {
            return var10000;
         }
      }

      return new SenderMetadata("", true);
   }

   @NotNull
   public static final String buildSenderLabel(@NotNull Part var0) {
      String var25;
      label51: {
         Intrinsics.checkNotNullParameter(var0, "part");
         io.intercom.android.sdk.models.Metadata var2 = var0.getMetadata();
         if (var2 != null) {
            io.intercom.android.sdk.models.Metadata var5 = var2;
            int var6 = 0;
            String[] var7 = new String[2];
            String var8 = var5.getTitle();
            String var9 = var8;
            byte var10 = 0;
            String[] var11 = var7;
            int var12 = 0;
            int var13 = 0;
            boolean var14 = !StringsKt.isBlank(var9);
            var11[var10] = var14 ? var8 : null;
            String[] var10000 = var7;
            byte var10001 = 1;
            String var10002 = var5.getSuffix();
            if (var10002 != null) {
               var9 = var10002;
               String var22 = var9;
               var10 = 1;
               var11 = var7;
               var13 = 0;
               int var15 = 0;
               var14 = !StringsKt.isBlank(var22);
               var10000 = var11;
               var10001 = var10;
               var10002 = var14 ? var9 : null;
            } else {
               var10002 = null;
            }

            var10000[var10001] = var10002;
            List var3 = CollectionsKt.listOfNotNull(var7);
            if (var3 != null) {
               List var16 = var3;
               List itx = var16;
               int var18 = 0;
               List var4 = !itx.isEmpty() ? var16 : null;
               if (var4 != null) {
                  var25 = CollectionsKt.joinToString$default(var4, " • ", null, null, 0, null, null, 62, null);
                  break label51;
               }
            }
         }

         var25 = null;
      }

      String var1 = var25;
      var25 = var1;
      if (var1 == null) {
         var25 = var0.getParticipant().getForename();
         Intrinsics.checkNotNullExpressionValue(var25, "getForename(...)");
      }

      return var25;
   }

   private static final boolean shouldShowQuickReplies(Part var0, boolean var1, boolean var2) {
      if (var1) {
         List var10000 = var0.getReplyOptions();
         Intrinsics.checkNotNullExpressionValue(var10000, "getReplyOptions(...)");
         if (!var10000.isEmpty() && var2) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isAdminOrAltParticipant(@NotNull Part var0, @NotNull UserIdentity var1) {
      Intrinsics.checkNotNullParameter(var0, "part");
      Intrinsics.checkNotNullParameter(var1, "userIdentity");
      return var0.isAdmin() || !var0.getParticipant().isUserWithId(var1.getIntercomId());
   }

   private static final ContentRow reduceEvent(Part var0) {
      MessageStyle var10000 = var0.getMessageStyle();
      ContentRow var16;
      switch (var10000 == null ? -1 : ConversationPartsReducerKt$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
            String var10002 = var0.getEventData().getStatus();
            String var18 = var0.getEventData().getEventAsPlainText();
            long var19 = var0.getCreatedAt();
            String var10005 = var0.getEventData().getCustomStateLabel();
            String var10006 = var0.getEventData().getCustomStatePrefix();
            String var10007 = var0.getId();
            Intrinsics.checkNotNullExpressionValue(var10007, "getId(...)");
            var16 = new ContentRow$TicketStatusRow(var10002, var18, var19, var10005, var10006, var10007);
            break;
         case 2:
            String var12 = var0.getEventData().getMergedConversationId();
            String var13 = var0.getEventData().getDescription();
            String var17 = var0.getId();
            Intrinsics.checkNotNullExpressionValue(var17, "getId(...)");
            String var14 = var17;
            var16 = new ContentRow$MergedConversationRow(var13, var12, var14);
            break;
         default:
            List var15;
            if (var0.getEventData().getEventContent().isEmpty()) {
               var15 = CollectionsKt.listOf(new EventLabelSegment(var0.getEventData().getEventAsPlainText(), null));
            } else {
               Iterable var2 = var0.getEventData().getEventContent();
               int var3 = 0;
               Iterable var4 = var2;
               ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
               int var6 = 0;

               for (Object var8 : var4) {
                  EventContent var9 = (EventContent)var8;
                  Collection var11 = var5;
                  int var10 = 0;
                  var11.add(new EventLabelSegment(var9.getText(), var9.getWeight()));
               }

               var15 = (List)var5;
            }

            List var1 = var15;
            Avatar var10003 = Avatar.create(var0.getEventData().getParticipant().getAvatar().getImageUrl(), var0.getEventData().getParticipant().getInitial());
            Intrinsics.checkNotNullExpressionValue(var10003, "create(...)");
            String var10004 = var0.getId();
            Intrinsics.checkNotNullExpressionValue(var10004, "getId(...)");
            var16 = new ContentRow$EventRow(var1, var10003, var10004);
      }

      return var16;
   }

   private static final boolean hasPendingMessageAfter(Part var0, Map var1) {
      long var10000 = var0.getCreatedAt();
      Iterable var2 = var1.values();
      long var7 = var10000;
      Iterator var3 = var2.iterator();
      Comparable var13;
      if (!var3.hasNext()) {
         var13 = null;
      } else {
         PendingMessage var4 = (PendingMessage)var3.next();
         int var5 = 0;
         Comparable var10 = var4.getPart().getCreatedAt();

         while (var3.hasNext()) {
            PendingMessage itx = (PendingMessage)var3.next();
            int var6/* $VF was: $i$a$-maxOfOrNull-ConversationPartsReducerKt$hasPendingMessageAfter$1x */ = 0;
            Comparable var12 = itx.getPart().getCreatedAt();
            if (var10.compareTo(var12) < 0) {
               var10 = var12;
            }
         }

         var13 = var10;
      }

      Comparable var9 = var13;
      return (Long)var9 != null ? var7 < (Long)var9 : false;
   }

   @NotNull
   public static final GroupingPosition getGroupingPosition(@NotNull List var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "allParts");
      return PartExtensionsKt.hasPreviousConcatPartNewStyle(var0, var1) && PartExtensionsKt.hasNextConcatPartNewStyle(var0, var1)
         ? GroupingPosition.MIDDLE
         : (
            PartExtensionsKt.hasPreviousConcatPartNewStyle(var0, var1)
               ? GroupingPosition.BOTTOM
               : (PartExtensionsKt.hasNextConcatPartNewStyle(var0, var1) ? GroupingPosition.TOP : GroupingPosition.STANDALONE)
         );
   }
}
