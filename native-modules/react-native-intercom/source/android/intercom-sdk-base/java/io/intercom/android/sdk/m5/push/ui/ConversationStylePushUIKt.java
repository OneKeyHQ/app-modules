package io.intercom.android.sdk.m5.push.ui;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import androidx.core.app.Person;
import androidx.core.app.NotificationCompat.InboxStyle;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.NotificationCompat.MessagingStyle.Message;
import androidx.core.app.Person.Builder;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.push.ConversationActionHandlerKt;
import io.intercom.android.sdk.m5.push.ConversationDeepLinkRouterKt;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Attachment;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Text;
import io.intercom.android.sdk.m5.push.NotificationChannel;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000`\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aF\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0000\u001a0\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u00122\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0000\u001a\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H\u0002\u001a,\u0010\u0017\u001a\u00020\u0018*\u00020\t2\u0006\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0000\"\u000e\u0010\u001f\u001a\u00020\u0016X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010 \u001a\u00020\u0014X\u0080T¢\u0006\u0002\n\u0000¨\u0006!",
   d2 = {
         "buildConversationStyleNotification",
         "Landroid/app/Notification;",
         "context",
         "Landroid/content/Context;",
         "conversation",
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation;",
         "shortcutInfo",
         "Landroidx/core/content/pm/ShortcutInfoCompat;",
         "conversationPushData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData;",
         "notificationChannel",
         "Lio/intercom/android/sdk/m5/push/NotificationChannel;",
         "isSilent",
         "",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "buildConversationStyleSummaryNotification",
         "conversations",
         "",
         "getMessagesContentText",
         "",
         "messagesCount",
         "",
         "toMessage",
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation$Message;",
         "timestamp",
         "",
         "avatarBitmap",
         "Landroid/graphics/Bitmap;",
         "contentImageUri",
         "Landroid/net/Uri;",
         "SUMMARY_NOTIFICATION_ID",
         "KEY_GROUP_CONVERSATION",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationStylePushUI.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationStylePushUI.kt\nio/intercom/android/sdk/m5/push/ui/ConversationStylePushUIKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 SpannableStringBuilder.kt\nandroidx/core/text/SpannableStringBuilderKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,168:1\n1869#2,2:169\n1374#2:171\n1460#2,5:172\n1869#2:177\n1869#2:178\n1870#2:188\n1870#2:189\n41#3,2:179\n87#3:182\n74#3,4:183\n43#3:187\n1#4:181\n*S KotlinDebug\n*F\n+ 1 ConversationStylePushUI.kt\nio/intercom/android/sdk/m5/push/ui/ConversationStylePushUIKt\n*L\n41#1:169,2\n90#1:171\n90#1:172,5\n93#1:177\n94#1:178\n94#1:188\n93#1:189\n95#1:179,2\n96#1:182\n96#1:183,4\n95#1:187\n*E\n"
)
public final class ConversationStylePushUIKt {
   public static final int SUMMARY_NOTIFICATION_ID = 9999997;
   @NotNull
   public static final String KEY_GROUP_CONVERSATION = "io.intercom.android.sdk.INTERCOM_KEY_GROUP_CONVERSATION";

   @NotNull
   public static final Notification buildConversationStyleNotification(
      @NotNull Context var0,
      @NotNull IntercomPushConversation var1,
      @Nullable ShortcutInfoCompat var2,
      @NotNull IntercomPushData$ConversationPushData var3,
      @NotNull NotificationChannel var4,
      boolean var5,
      @Nullable TaskStackBuilder var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "conversation");
      Intrinsics.checkNotNullParameter(var3, "conversationPushData");
      Intrinsics.checkNotNullParameter(var4, "notificationChannel");
      String var10000 = var0.getString(string.intercom_new_notifications);
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var7 = var10000;
      String var8 = getMessagesContentText(var0, var1.getMessages().size());
      var10000 = var0.getString(string.intercom_you);
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var9 = var10000;
      MessagingStyle var10 = new MessagingStyle(new Builder().setName(var9).build());
      Iterable var11 = var1.getMessages();
      int var12 = 0;

      for (Object var14 : var11) {
         IntercomPushConversation$Message var15 = (IntercomPushConversation$Message)var14;
         int var16 = 0;
         Message var17 = new Message(var15.getMessage(), var15.getTimestamp(), var15.getPerson());
         Message var18 = var17;
         MessagingStyle var19 = var10;
         int var20 = 0;
         Uri var27 = var15.getContentImageUri();
         if (var27 != null) {
            Uri var21 = var27;
            int var22 = 0;
            var18.setData("image/png", var21);
         }

         var19.addMessage(var17);
      }

      androidx.core.app.NotificationCompat.Builder var23 = BasePushUIKt.createBaseNotificationBuilder(var0, var7, var8, var4)
         .setStyle(var10)
         .setShortcutInfo(var2)
         .setContentIntent(ConversationDeepLinkRouterKt.buildIntentForConversationScreen(var0, var1.getConversationId(), var6));
      androidx.core.app.NotificationCompat.Builder var24 = var23;
      int var25 = 0;
      if (VERSION.SDK_INT >= 24 && var3.getMessageData() instanceof IntercomPushData$ConversationPushData$MessageData$Attachment) {
         var24.addAction(
            ConversationActionHandlerKt.buildContextualAction(
               var0, ((IntercomPushData$ConversationPushData$MessageData$Attachment)var3.getMessageData()).getUrl()
            )
         );
      }

      if (VERSION.SDK_INT >= 31) {
         var24.setBubbleMetadata(BubbleMetaDataKt.getBubbleMetaData(var0, var1));
      }

      Notification var28 = var23.setGroup("io.intercom.android.sdk.INTERCOM_KEY_GROUP_CONVERSATION").setSilent(var5).build();
      Intrinsics.checkNotNullExpressionValue(var28, "build(...)");
      return var28;
   }

   // $VF: synthetic method
   public static Notification buildConversationStyleNotification$default(
      Context var0,
      IntercomPushConversation var1,
      ShortcutInfoCompat var2,
      IntercomPushData$ConversationPushData var3,
      NotificationChannel var4,
      boolean var5,
      TaskStackBuilder var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 64) != 0) {
         var6 = null;
      }

      return buildConversationStyleNotification(var0, var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   public static final Notification buildConversationStyleSummaryNotification(
      @NotNull Context var0, @NotNull List var1, @NotNull NotificationChannel var2, @Nullable TaskStackBuilder var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "conversations");
      Intrinsics.checkNotNullParameter(var2, "notificationChannel");
      String recoveredTitle = var0.getString(string.intercom_new_notifications);
      Intrinsics.checkNotNullExpressionValue(recoveredTitle, "getString(...)");
      String var4 = recoveredTitle;
      CharSequence var10000;
      Iterable var6 = var1;
      Context var34 = var0;
      int var7 = 0;
      Iterable var8 = var6;
      ArrayList var9 = new ArrayList();
      int var10 = 0;

      for (Object var12 : var8) {
         IntercomPushConversation var13 = (IntercomPushConversation)var12;
         int var14 = 0;
         Iterable var42 = var13.getMessages();
         CollectionsKt.addAll(var9, var42);
      }

      String var5 = getMessagesContentText(var34, ((List)var9).size());
      InboxStyle var35 = new InboxStyle().setBigContentTitle(var4);
      Iterable var36 = var1;
      int var37 = 0;

      for (Object var39 : var36) {
         IntercomPushConversation var40 = (IntercomPushConversation)var39;
         int var41 = 0;
         Iterable $this$forEach$ivx = var40.getMessages();
         int $i$f$forEachx = 0;

         for (Object element$ivx : $this$forEach$ivx) {
            IntercomPushConversation$Message var17 = (IntercomPushConversation$Message)element$ivx;
            int var18 = 0;
            int var19 = 0;
            SpannableStringBuilder var20 = new SpannableStringBuilder();
            SpannableStringBuilder var21 = var20;
            int var22 = 0;
            Person var45 = var17.getPerson();
            if (var45 != null) {
               var10000 = var45.getName();
               if (var10000 != null) {
                  CharSequence var23 = var10000;
                  int var24 = 0;
                  SpannableStringBuilder var25 = var21;
                  int var26 = 0;
                  SpannableStringBuilder var27 = var25;
                  Object var28 = new StyleSpan(1);
                  int var29 = 0;
                  int var30 = var27.length();
                  SpannableStringBuilder var31 = var27;
                  int var32 = 0;
                  var31.append("" + var23 + ' ');
                  var27.setSpan(var28, var30, var27.length(), 17);
               }
            }

            var21.append(var17.getMessage());
            SpannedString var33 = new SpannedString(var20);
            var35.addLine(var33);
         }
      }

      Notification var47 = BasePushUIKt.createBaseNotificationBuilder(var0, var4, var5, var2)
         .setContentIntent(ConversationDeepLinkRouterKt.buildIntentForMessagesScreen(var0, var3))
         .setGroup("io.intercom.android.sdk.INTERCOM_KEY_GROUP_CONVERSATION")
         .setGroupSummary(true)
         .setStyle(var35)
         .build();
      Intrinsics.checkNotNullExpressionValue(var47, "build(...)");
      return var47;
   }

   private static final String getMessagesContentText(Context var0, int var1) {
      String var2 = var1 == 1
         ? var0.getString(string.intercom_one_new_message)
         : Phrase.from(var0, string.intercom_new_messages).put("n", var1).format().toString();
      Intrinsics.checkNotNull(var2);
      return var2;
   }

   @NotNull
   public static final IntercomPushConversation$Message toMessage(
      @NotNull IntercomPushData$ConversationPushData var0, long var1, @Nullable Bitmap var3, @Nullable Uri var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Person var10000;
      if (var0.isCurrentUser()) {
         var10000 = null;
      } else {
         Builder var14 = new Builder().setName(var0.getAuthorName()).setKey(var0.getAuthorName() + var0.getAvatarUrl());
         IconCompat var10001;
         if (var3 != null) {
            Bitmap var6 = var3;
            Builder var8 = var14;
            int var7 = 0;
            var10001 = IconCompat.createWithBitmap(var6);
            var14 = var8;
         } else {
            var10001 = null;
         }

         var10000 = var14.setIcon(var10001).build();
      }

      IntercomPushData$ConversationPushData$MessageData var5 = var0.getMessageData();
      String var10002;
      if (var5 instanceof IntercomPushData$ConversationPushData$MessageData$Text) {
         var10002 = ((IntercomPushData$ConversationPushData$MessageData$Text)var0.getMessageData()).getMessage();
      } else if (var5 instanceof IntercomPushData$ConversationPushData$MessageData$Image) {
         var10002 = ((IntercomPushData$ConversationPushData$MessageData$Image)var0.getMessageData()).getTitle();
      } else {
         if (!(var5 instanceof IntercomPushData$ConversationPushData$MessageData$Attachment)) {
            throw new NoWhenBranchMatchedException();
         }

         var10002 = ((IntercomPushData$ConversationPushData$MessageData$Attachment)var0.getMessageData()).getTitle();
      }

      Uri var9 = var4;
      String var10 = var10002;
      long var11 = var1;
      Person var13 = var10000;
      return new IntercomPushConversation$Message(var13, var11, var10, var9);
   }

   // $VF: synthetic method
   public static IntercomPushConversation$Message toMessage$default(
      IntercomPushData$ConversationPushData var0, long var1, Bitmap var3, Uri var4, int var5, Object var6
   ) {
      if ((var5 & 2) != 0) {
         var3 = null;
      }

      if ((var5 & 4) != 0) {
         var4 = null;
      }

      return toMessage(var0, var1, var3, var4);
   }
}
