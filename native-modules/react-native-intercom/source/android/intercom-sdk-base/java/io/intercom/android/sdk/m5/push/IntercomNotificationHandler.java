package io.intercom.android.sdk.m5.push;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.Person.Builder;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData;
import io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData;
import io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image;
import io.intercom.android.sdk.m5.push.ui.ConversationShortcutKt;
import io.intercom.android.sdk.m5.push.ui.ConversationStylePushUIKt;
import io.intercom.android.sdk.m5.push.ui.DeepLinkStylePushUIKt;
import io.intercom.android.sdk.m5.push.ui.IntercomPushConversation;
import io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message;
import io.intercom.android.sdk.models.HostAppState;
import io.intercom.android.sdk.utilities.GroupConversationTextFormatter;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000~\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J1\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0000¢\u0006\u0002\b\u001aJ\"\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0002J;\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020 2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010!\u001a\u00020\"2\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0000¢\u0006\u0002\b#J@\u0010$\u001a\u0016\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u0005\u0018\u00010%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010+2\b\u0010,\u001a\u0004\u0018\u00010-H\u0002JR\u0010.\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010/\u001a\u00020\u00062\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u001f\u001a\u00020 2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010!\u001a\u00020\"2\u0006\u00100\u001a\u0002012\b\u0010,\u001a\u0004\u0018\u00010-H\u0002J\u000e\u00102\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J2\u00103\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050%2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020 2\u0006\u00104\u001a\u00020)H\u0002J\u0015\u00105\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0000¢\u0006\u0002\b6R*\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0000@\u0000X\u0081\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\u0007\u0010\u0003\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0018\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000f¨\u00067",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomNotificationHandler;",
         "",
         "<init>",
         "()V",
         "conversations",
         "",
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation;",
         "getConversations$intercom_sdk_base_release$annotations",
         "getConversations$intercom_sdk_base_release",
         "()Ljava/util/List;",
         "setConversations$intercom_sdk_base_release",
         "(Ljava/util/List;)V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "kotlin.jvm.PlatformType",
         "Lcom/intercom/twig/Twig;",
         "processIntercomPushNotification",
         "",
         "context",
         "Landroid/content/Context;",
         "intercomPushData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData;",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "timeProvider",
         "Lio/intercom/android/sdk/utilities/commons/TimeProvider;",
         "processIntercomPushNotification$intercom_sdk_base_release",
         "processDeepLinkPushNotification",
         "deepLinkPushData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$DeepLinkPushData;",
         "processConversationPushNotification",
         "conversationPushData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData;",
         "isSilent",
         "",
         "processConversationPushNotification$intercom_sdk_base_release",
         "updateConversationImages",
         "Lkotlin/Pair;",
         "conversationId",
         "",
         "messageTimestamp",
         "",
         "contentImageUri",
         "Landroid/net/Uri;",
         "avatarBitmap",
         "Landroid/graphics/Bitmap;",
         "showNotification",
         "conversation",
         "notificationChannel",
         "Lio/intercom/android/sdk/m5/push/NotificationChannel;",
         "clear",
         "updateConversations",
         "timestamp",
         "setUpNotificationChannels",
         "setUpNotificationChannels$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nIntercomNotificationHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomNotificationHandler.kt\nio/intercom/android/sdk/m5/push/IntercomNotificationHandler\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,364:1\n1#2:365\n1#2:386\n360#3,7:366\n774#3:373\n865#3,2:374\n1617#3,9:376\n1869#3:385\n1870#3:387\n1626#3:388\n1669#3,8:389\n774#3:397\n865#3,2:398\n*S KotlinDebug\n*F\n+ 1 IntercomNotificationHandler.kt\nio/intercom/android/sdk/m5/push/IntercomNotificationHandler\n*L\n309#1:386\n199#1:366,7\n218#1:373\n218#1:374,2\n309#1:376,9\n309#1:385\n309#1:387\n309#1:388\n309#1:389,8\n322#1:397\n322#1:398,2\n*E\n"
)
public final class IntercomNotificationHandler {
   @NotNull
   public static final IntercomNotificationHandler INSTANCE = new IntercomNotificationHandler();
   @NotNull
   private static List conversations = CollectionsKt.emptyList();
   private static final Twig twig = LumberMill.getLogger();
   public static final int $stable;

   static { $stable = 8; }

   private IntercomNotificationHandler() {
   }

   @NotNull
   public final List getConversations$intercom_sdk_base_release() {
      return conversations;
   }

   public final void setConversations$intercom_sdk_base_release(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      conversations = var1;
   }

   /** @deprecated */
   // $VF: synthetic method
   @VisibleForTesting
   public static void getConversations$intercom_sdk_base_release$annotations() {
   }

   public final void processIntercomPushNotification$intercom_sdk_base_release(
      @NotNull Context var1, @NotNull IntercomPushData var2, @Nullable TaskStackBuilder var3, @NotNull TimeProvider var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "intercomPushData");
      Intrinsics.checkNotNullParameter(var4, "timeProvider");
      IntercomPushData var5 = var2;
      if (var5 instanceof io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData) {
         this.processDeepLinkPushNotification(var1, (io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData)var2, var3);
      } else {
         if (!(var5 instanceof io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData)) {
            throw new NoWhenBranchMatchedException();
         }

         processConversationPushNotification$intercom_sdk_base_release$default(this, var1, (io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData)var2, var3, false, var4, 8, null);
      }
   }

   // $VF: synthetic method
   public static void processIntercomPushNotification$intercom_sdk_base_release$default(
      IntercomNotificationHandler var0, Context var1, IntercomPushData var2, TaskStackBuilder var3, TimeProvider var4, int var5, Object var6
   ) {
      if ((var5 & 8) != 0) {
         var4 = TimeProvider.SYSTEM;
      }

      var0.processIntercomPushNotification$intercom_sdk_base_release(var1, var2, var3, var4);
   }

   private final void processDeepLinkPushNotification(Context var1, io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData var2, TaskStackBuilder var3) {
      twig.i("This is a push only message");
      Injector.get().getMetricTracker().receivedPushOnlyNotification("instance_id:" + var2.getInstanceId(), var2.getInstanceId());
      AppConfig var4 = (AppConfig)Injector.get().getAppConfigProvider().get();
      Intrinsics.checkNotNull(var4);
      final android.content.Context recoveredExpressionCapture_1186_0 = var1;
      final io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData recoveredExpressionCapture_1186_1 = var2;
      final android.app.TaskStackBuilder recoveredExpressionCapture_1186_2 = var3;
      final android.content.Context recoveredExpressionCapture_1187_0 = var1;
      final io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData recoveredExpressionCapture_1187_1 = var2;
      final android.app.TaskStackBuilder recoveredExpressionCapture_1187_2 = var3;
      IntercomPushBitmapUtilsKt.loadBitmaps$default(
         var1,
         var4,
         var2.getContentImageUrl(),
         null,
         null,
         null,
         ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.push.IntercomNotificationHandler.processDeepLinkPushNotification$lambda$0(recoveredExpressionCapture_1186_0, recoveredExpressionCapture_1186_1, recoveredExpressionCapture_1186_2)),
         ((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1187_0, recoveredExpressionArgument_1187_1) -> io.intercom.android.sdk.m5.push.IntercomNotificationHandler.processDeepLinkPushNotification$lambda$1(recoveredExpressionCapture_1187_0, recoveredExpressionCapture_1187_1, recoveredExpressionCapture_1187_2, (android.graphics.Bitmap) ((android.graphics.Bitmap)recoveredExpressionArgument_1187_0), (android.graphics.Bitmap) ((android.graphics.Bitmap)recoveredExpressionArgument_1187_1))),
         56,
         null
      );
   }

   public final void processConversationPushNotification$intercom_sdk_base_release(
      @NotNull Context var1, @NotNull io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var2, @Nullable TaskStackBuilder var3, boolean var4, @NotNull TimeProvider var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "conversationPushData");
      Intrinsics.checkNotNullParameter(var5, "timeProvider");
      if (!((HostAppState)Injector.get().getDataLayer().getHostAppState().getValue()).isForeground()) {
         NotificationChannel var6;
         String var11;
         label23: {
            twig.i("This is a background push message");
            Injector.get().getMetricTracker().receivedPushNotification(var2.getConversationId());
            var6 = var2.isNewConversation() ? NotificationChannel.NEW_CHATS_CHANNEL : NotificationChannel.CHAT_REPLIES_CHANNEL;
            io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData var9 = var2.getMessageData();
            io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image var10000 = var9 instanceof io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image ? (io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image)var9 : null;
            if ((var9 instanceof io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image ? (io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData$MessageData$Image)var9 : null) != null) {
               var11 = var10000.getUrl();
               if (var11 != null) {
                  break label23;
               }
            }

            var11 = "";
         }

         String var7 = var11;
         long var8 = var5.currentTimeMillis();
         AppConfig var10 = (AppConfig)Injector.get().getAppConfigProvider().get();
         Intrinsics.checkNotNull(var10);
         final android.content.Context recoveredExpressionCapture_1184_0 = var1;
         final io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData recoveredExpressionCapture_1184_1 = var2;
         final long recoveredExpressionCapture_1184_2 = var8;
         final android.app.TaskStackBuilder recoveredExpressionCapture_1184_3 = var3;
         final boolean recoveredExpressionCapture_1184_4 = var4;
         final io.intercom.android.sdk.m5.push.NotificationChannel recoveredExpressionCapture_1184_5 = var6;
         final io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData recoveredExpressionCapture_1185_0 = var2;
         final long recoveredExpressionCapture_1185_1 = var8;
         final android.content.Context recoveredExpressionCapture_1185_2 = var1;
         final android.app.TaskStackBuilder recoveredExpressionCapture_1185_3 = var3;
         final io.intercom.android.sdk.m5.push.NotificationChannel recoveredExpressionCapture_1185_4 = var6;
         IntercomPushBitmapUtilsKt.loadBitmaps$default(
            var1,
            var10,
            var7,
            var2.getAvatarUrl(),
            var2.getAuthorName(),
            null,
            ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.push.IntercomNotificationHandler.processConversationPushNotification$lambda$2(recoveredExpressionCapture_1184_0, recoveredExpressionCapture_1184_1, recoveredExpressionCapture_1184_2, recoveredExpressionCapture_1184_3, recoveredExpressionCapture_1184_4, recoveredExpressionCapture_1184_5)),
            ((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1185_0, recoveredExpressionArgument_1185_1) -> io.intercom.android.sdk.m5.push.IntercomNotificationHandler.processConversationPushNotification$lambda$4(recoveredExpressionCapture_1185_0, recoveredExpressionCapture_1185_1, recoveredExpressionCapture_1185_2, recoveredExpressionCapture_1185_3, recoveredExpressionCapture_1185_4, (android.graphics.Bitmap) ((android.graphics.Bitmap)recoveredExpressionArgument_1185_0), (android.graphics.Bitmap) ((android.graphics.Bitmap)recoveredExpressionArgument_1185_1))),
            32,
            null
         );
      } else {
         twig.i("Intercom message received but not displayed in notification bar. This happened because the host app was in the foreground.");
      }
   }

   // $VF: synthetic method
   public static void processConversationPushNotification$intercom_sdk_base_release$default(
      IntercomNotificationHandler var0, Context var1, io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var2, TaskStackBuilder var3, boolean var4, TimeProvider var5, int var6, Object var7
   ) {
      if ((var6 & 8) != 0) {
         var4 = false;
      }

      if ((var6 & 16) != 0) {
         var5 = TimeProvider.SYSTEM;
      }

      var0.processConversationPushNotification$intercom_sdk_base_release(var1, var2, var3, var4, var5);
   }

   private final synchronized Pair updateConversationImages(String var1, long var2, Uri var4, Bitmap var5) {
      Iterable var9 = conversations;
      Iterator var10 = var9.iterator();

      Object var10000;
      while (true) {
         if (var10.hasNext()) {
            Object var11 = var10.next();
            IntercomPushConversation var12 = (IntercomPushConversation)var11;
            int var13 = 0;
            if (!Intrinsics.areEqual(var12.getConversationId(), var1)) {
               continue;
            }

            var10000 = var11;
            break;
         }

         var10000 = null;
         break;
      }

      IntercomPushConversation var42 = (IntercomPushConversation)var10000;
      if ((IntercomPushConversation)var10000 == null) {
         return null;
      }

      IntercomPushConversation var6 = var42;
      if (var6.getMessages().isEmpty()) {
         return null;
      }

      List var8 = var6.getMessages();
      int var23 = 0;
      int var25 = 0;
      Iterator var27 = var8.iterator();

      int var43;
      while (true) {
         if (!var27.hasNext()) {
            var43 = -1;
            break;
         }

         Object var29 = var27.next();
         io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var33 = (io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message)var29;
         int var14 = 0;
         if (var33.getTimestamp() == var2) {
            var43 = var25;
            break;
         }

         var25++;
      }

      int var7 = var43;
      if (var7 == -1) {
         return null;
      }

      io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var22 = (io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message)var6.getMessages().get(var7);
      Person var44 = var22.getPerson();
      Person var46;
      if (var44 != null) {
         Builder var45;
         IconCompat var10001;
         label54: {
            Person var30 = var44;
            int var34 = 0;
            var45 = new Builder().setName(var30.getName()).setKey(var30.getKey());
            if (var5 != null) {
               Bitmap var37 = var5;
               Builder var15 = var45;
               int var16 = 0;
               var10001 = IconCompat.createWithBitmap(var37);
               var45 = var15;
               if (var10001 != null) {
                  break label54;
               }
            }

            var10001 = var30.getIcon();
         }

         var46 = var45.setIcon(var10001).build();
      } else {
         var46 = null;
      }

      Person var24 = var46;
      io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var26 = io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message.copy$default(var22, var24, 0L, null, var4, 6, null);
      List var31 = CollectionsKt.toMutableList(var6.getMessages());
      List var35 = var31;
      int var38 = 0;
      var35.set(var7, var26);
      List var28 = var31;
      IntercomPushConversation var32 = IntercomPushConversation.copy$default(var6, null, null, var28, 3, null);
      Iterable var36 = conversations;
      int var39 = 0;
      Iterable var40 = var36;
      ArrayList var41 = new ArrayList();
      int var17 = 0;

      for (Object var19 : var40) {
         IntercomPushConversation var20 = (IntercomPushConversation)var19;
         int var21 = 0;
         if (!Intrinsics.areEqual(var20.getConversationId(), var1)) {
            var41.add(var19);
         }
      }

      conversations = CollectionsKt.plus((Collection & List)var41, var32);
      return TuplesKt.to(var32, conversations);
   }

   private final void showNotification(
      Context var1,
      IntercomPushConversation var2,
      List var3,
      io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var4,
      TaskStackBuilder var5,
      boolean var6,
      NotificationChannel var7,
      Bitmap var8
   ) {
      Pair var9 = ConversationShortcutKt.createTemporaryShortcut(var1, var2.getConversationId(), var2.getConversationTitle(), var8);
      List var10 = (List)var9.component1();
      ShortcutInfoCompat var11 = (ShortcutInfoCompat)var9.component2();
      Notification var12 = ConversationStylePushUIKt.buildConversationStyleNotification(var1, var2, var11, var4, var7, var6, var5);
      Notification var13 = var3.size() > 1 ? ConversationStylePushUIKt.buildConversationStyleSummaryNotification(var1, var3, var7, var5) : null;
      Map var14 = MapsKt.createMapBuilder();
      Map var15 = var14;
      Context var19 = var1;
      int var16 = 0;
      var15.put(var4.getNotificationId(), var12);
      if (var13 != null) {
         Notification var17 = var13;
         int var18 = 0;
         var15.put(9999997, var17);
      }

      Map var10001 = MapsKt.build(var14);
      Twig var10002 = twig;
      Intrinsics.checkNotNullExpressionValue(twig, "twig");
      NotificationPermissionCheckerKt.showNotifications(var19, var10001, var10002);
      Twig var10003 = twig;
      Intrinsics.checkNotNullExpressionValue(twig, "twig");
      ConversationShortcutKt.resetShortcuts(var1, var10, var11, var10003);
   }

   public final synchronized void clear(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      if (!conversations.isEmpty()) {
         twig.i("Removing Intercom push notifications.");
      }

      try {
         NotificationManagerCompat var8 = NotificationManagerCompat.from(var1);
         int var4 = 0;
         var8.cancelAll();
      } catch (NullPointerException var5) {
         Twig var9 = twig;
         Object[] var7 = new Object[]{var5};
         var9.w("Failed to clear notifications due to unavailable NotificationManager service.", var7);
      } catch (Exception var6) {
         Twig var10000 = twig;
         Object[] var3 = new Object[]{var6};
         var10000.w("Failed to clear notifications due to unexpected error.", var3);
      }

      conversations = CollectionsKt.emptyList();
   }

   private final synchronized Pair updateConversations(Context var1, io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var2, long var3) {
      Iterable var7 = conversations;
      Iterator var8 = var7.iterator();

      Object var10000;
      while (true) {
         if (var8.hasNext()) {
            Object var9 = var8.next();
            IntercomPushConversation var10 = (IntercomPushConversation)var9;
            int var11 = 0;
            if (!Intrinsics.areEqual(var10.getConversationId(), var2.getConversationId())) {
               continue;
            }

            var10000 = var9;
            break;
         }

         var10000 = null;
         break;
      }

      IntercomPushConversation var5 = (IntercomPushConversation)var10000;
      io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var6 = ConversationStylePushUIKt.toMessage$default(var2, var3, null, null, 6, null);
      IntercomPushConversation var57;
      if (var5 == null) {
         String var55 = var2.getConversationId();
         CharSequence var30 = var2.getAuthorName();
         CharSequence var10001;
         if (StringsKt.isBlank(var30)) {
            String var25 = var55;
            int var33 = 0;
            String var56 = var1.getString(string.intercom_new_notifications);
            Intrinsics.checkNotNullExpressionValue(var56, "getString(...)");
            var10001 = var56;
            var55 = var25;
         } else {
            var10001 = var30;
         }

         String var60 = (String)var10001;
         List var26 = CollectionsKt.listOf(var6);
         String var27 = var60;
         String var28 = var55;
         var57 = new IntercomPushConversation(var28, var27, var26);
      } else {
         io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var58 = (io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message)CollectionsKt.lastOrNull(var5.getMessages());
         List var31 = (var58 != null ? var58.isCurrentUser() : false) && !var2.isCurrentUser()
            ? CollectionsKt.listOf(var6)
            : CollectionsKt.plus(var5.getMessages(), var6);
         Iterable var36 = var31;
         int var40 = 0;
         Iterable var12 = var36;
         ArrayList var13 = new ArrayList();
         int var14 = 0;
         Iterable var15 = var12;
         int var16 = 0;

         for (Object var18 : var15) {
            Object var19 = var18;
            int var20 = 0;
            io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message var21 = (io.intercom.android.sdk.m5.push.ui.IntercomPushConversation$Message)var19;
            int var22 = 0;
            Person var59 = var21.getPerson();
            if (var59 != null) {
               Object var23 = var59;
               int var24 = 0;
               var13.add(var23);
            }
         }

         var var37 = (Iterable & List)var13;
         int var41 = 0;
         HashSet var43 = new HashSet();
         ArrayList var45 = new ArrayList();

         for (Object var49 : var37) {
            Person var51 = (Person)var49;
            int var54 = 0;
            Object var52 = var51.getKey();
            if (var43.add(var52)) {
               var45.add(var49);
            }
         }

         List var34 = var45;
         String var38 = var34.size() > 1
            ? GroupConversationTextFormatter.groupConversationTitle(
                  ((Person)CollectionsKt.last(var34)).getName().toString(), CollectionsKt.getLastIndex(var34), var1
               )
               .toString()
            : var5.getConversationTitle();
         var57 = IntercomPushConversation.copy$default(var5, null, var38, var31, 1, null);
      }

      IntercomPushConversation var29 = var57;
      Iterable var32 = conversations;
      int var35 = 0;
      Iterable var39 = var32;
      ArrayList var42 = new ArrayList();
      int var44 = 0;

      for (Object var48 : var39) {
         IntercomPushConversation var50 = (IntercomPushConversation)var48;
         int var53 = 0;
         if (!Intrinsics.areEqual(var50.getConversationId(), var29.getConversationId())) {
            var42.add(var48);
         }
      }

      conversations = CollectionsKt.plus((Collection & List)var42, var29);
      return TuplesKt.to(var29, conversations);
   }

   public final void setUpNotificationChannels$intercom_sdk_base_release(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");

      try {
         NotificationChannelCompat var2 = new androidx.core.app.NotificationChannelCompat.Builder(NotificationChannel.CHAT_REPLIES_CHANNEL.getChannelName(), 4)
            .setName(var1.getString(string.intercom_notification_channel_chat_replies_title))
            .setDescription(var1.getString(string.intercom_notification_channel_chat_replies_description))
            .build();
         NotificationChannelCompat var11 = new androidx.core.app.NotificationChannelCompat.Builder(NotificationChannel.NEW_CHATS_CHANNEL.getChannelName(), 4)
            .setName(var1.getString(string.intercom_notification_channel_new_chats_title))
            .setDescription(var1.getString(string.intercom_notification_channel_new_chats_description))
            .build();
         NotificationChannelCompat var4 = new androidx.core.app.NotificationChannelCompat.Builder(NotificationChannel.ACTIONS_CHANNEL.getChannelName(), 4)
            .setName(var1.getString(string.intercom_notification_channel_actions_title))
            .setDescription(var1.getString(string.intercom_notification_channel_actions_description))
            .build();
         NotificationManagerCompat var5 = NotificationManagerCompat.from(var1);
         int var6 = 0;
         NotificationChannelCompat[] var7 = new NotificationChannelCompat[]{var2, var11, var4};
         var5.createNotificationChannelsCompat(CollectionsKt.listOf(var7));
      } catch (NullPointerException var8) {
         Twig var12 = twig;
         Object[] var10 = new Object[]{var8};
         var12.w(
            "Failed to create notification channels due to unavailable NotificationManager service. This can happen during app initialization or in restricted environments. Notifications may not work until the service becomes available.",
            var10
         );
      } catch (Exception var9) {
         Twig var10000 = twig;
         Object[] var3 = new Object[]{var9};
         var10000.w("Failed to create notification channels due to unexpected error.", var3);
      }
   }

   private static final Unit processDeepLinkPushNotification$lambda$0(Context var0, io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData var1, TaskStackBuilder var2) {
      Notification var3 = DeepLinkStylePushUIKt.buildDeepLinkNotification$default(var0, var1, null, var2, false, 16, null);
      int var10001 = var1.getNotificationId();
      Twig var10003 = twig;
      Intrinsics.checkNotNullExpressionValue(twig, "twig");
      NotificationPermissionCheckerKt.showNotification(var0, var10001, var3, var10003);
      return Unit.INSTANCE;
   }

   private static final Unit processDeepLinkPushNotification$lambda$1(Context var0, io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData var1, TaskStackBuilder var2, Bitmap var3, Bitmap var4) {
      if (var3 != null) {
         Notification var5 = DeepLinkStylePushUIKt.buildDeepLinkNotification(var0, var1, var3, var2, true);
         int var10001 = var1.getNotificationId();
         Twig var10003 = twig;
         Intrinsics.checkNotNullExpressionValue(twig, "twig");
         NotificationPermissionCheckerKt.showNotification(var0, var10001, var5, var10003);
      }

      return Unit.INSTANCE;
   }

   private static final Unit processConversationPushNotification$lambda$2(
      Context var0, io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var1, long var2, TaskStackBuilder var4, boolean var5, NotificationChannel var6
   ) {
      Pair var7 = INSTANCE.updateConversations(var0, var1, var2);
      IntercomPushConversation var8 = (IntercomPushConversation)var7.component1();
      List var9 = (List)var7.component2();
      INSTANCE.showNotification(var0, var8, var9, var1, var4, var5, var6, null);
      return Unit.INSTANCE;
   }

   private static final Unit processConversationPushNotification$lambda$4(
      io.intercom.android.sdk.m5.push.IntercomPushData$ConversationPushData var0, long var1, Context var3, TaskStackBuilder var4, NotificationChannel var5, Bitmap var6, Bitmap var7
   ) {
      Uri var10000;
      if (var6 != null) {
         Bitmap var10 = var6;
         int var11 = 0;
         var10000 = IntercomPushBitmapUtilsKt.getBitmapUri(var3, var10);
      } else {
         var10000 = null;
      }

      Uri var8 = var10000;
      Pair var14 = INSTANCE.updateConversationImages(var0.getConversationId(), var1, var8, var7);
      if (var14 == null) {
         return Unit.INSTANCE;
      }

      Pair var9 = var14;
      IntercomPushConversation var12 = (IntercomPushConversation)var9.component1();
      List var13 = (List)var9.component2();
      INSTANCE.showNotification(var3, var12, var13, var0, var4, true, var5, var7);
      return Unit.INSTANCE;
   }
}
