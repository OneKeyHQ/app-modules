package io.intercom.android.sdk.m5.push;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.os.BundleKt;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgsKt;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$ConversationScreenArgs;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$MessagesScreenArgs;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a$\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0000\u001a,\u0010\b\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0000\u001a\u001c\u0010\u000b\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0000\u001aA\u0010\f\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0002¢\u0006\u0002\u0010\u0013\"\u000e\u0010\u0014\u001a\u00020\u000eX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0015\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "buildIntentForConversationScreen",
         "Landroid/app/PendingIntent;",
         "context",
         "Landroid/content/Context;",
         "conversationId",
         "",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "buildIntentForDeepLinkScreen",
         "uri",
         "instanceId",
         "buildIntentForMessagesScreen",
         "buildBasePendingIntent",
         "requestCode",
         "",
         "finalIntent",
         "Landroid/content/Intent;",
         "extras",
         "Landroid/os/Bundle;",
         "(Landroid/content/Context;Ljava/lang/Integer;Landroid/content/Intent;Landroid/app/TaskStackBuilder;Landroid/os/Bundle;)Landroid/app/PendingIntent;",
         "DEFAULT_REQUEST_CODE",
         "INTERCOM_PUSH_INSTANCE_ID",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationDeepLinkRouter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationDeepLinkRouter.kt\nio/intercom/android/sdk/m5/push/ConversationDeepLinkRouterKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Uri.kt\nandroidx/core/net/UriKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,121:1\n1#2:122\n29#3:123\n19652#4,2:124\n*S KotlinDebug\n*F\n+ 1 ConversationDeepLinkRouter.kt\nio/intercom/android/sdk/m5/push/ConversationDeepLinkRouterKt\n*L\n43#1:123\n96#1:124,2\n*E\n"
)
public final class ConversationDeepLinkRouterKt {
   private static final int DEFAULT_REQUEST_CODE = 0;
   @NotNull
   public static final String INTERCOM_PUSH_INSTANCE_ID = "io.intercom.android.sdk.INTERCOM_PUSH_INSTANCE_ID";

   @Nullable
   public static final PendingIntent buildIntentForConversationScreen(@NotNull Context var0, @NotNull String var1, @Nullable TaskStackBuilder var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intent var3 = !StringsKt.isBlank(var1)
         ? IntercomRootActivityArgsKt.getIntentForArgs$default(
            var0, new io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$ConversationScreenArgs(var1, "", false, null, null, null, 48, null), null, null, 4, null
         )
         : null;
      Context var10000 = var0;
      Integer var10001;
      if (var3 != null) {
         Context var6 = var0;
         int var5 = 0;
         var10001 = var1.hashCode();
         var10000 = var6;
      } else {
         var10001 = null;
      }

      return buildBasePendingIntent(var10000, var10001, var3, var2, null);
   }

   @Nullable
   public static final PendingIntent buildIntentForDeepLinkScreen(
      @NotNull Context var0, @NotNull String var1, @NotNull String var2, @Nullable TaskStackBuilder var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uri");
      Intrinsics.checkNotNullParameter(var2, "instanceId");
      Intent var10000;
      if (!StringsKt.isBlank(var1)) {
         String var5 = var1;
         int var6 = 0;
         Intent var9 = new Intent("android.intent.action.VIEW", Uri.parse(var5));
         Intent var11 = var9;
         int var7 = 0;
         var11.addCategory("android.intent.category.BROWSABLE");
         var10000 = var9;
      } else {
         var10000 = null;
      }

      Intent var4 = var10000;
      Context var13 = var0;
      Integer var10001;
      if (var4 != null) {
         Context var8 = var0;
         int var12 = 0;
         var10001 = var1.hashCode();
         var13 = var8;
      } else {
         var10001 = null;
      }

      Pair[] var10 = new Pair[]{TuplesKt.to("io.intercom.android.sdk.INTERCOM_PUSH_INSTANCE_ID", var2)};
      return buildBasePendingIntent(var13, var10001, var4, var3, BundleKt.bundleOf(var10));
   }

   @Nullable
   public static final PendingIntent buildIntentForMessagesScreen(@NotNull Context var0, @Nullable TaskStackBuilder var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intent var2 = IntercomRootActivityArgsKt.getIntentForArgs$default(var0, io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$MessagesScreenArgs.INSTANCE, null, null, 4, null);
      return buildBasePendingIntent(var0, 0, var2, var1, null);
   }

   private static final PendingIntent buildBasePendingIntent(Context var0, Integer var1, Intent var2, TaskStackBuilder var3, Bundle var4) {
      Intent var10000 = var0.getPackageManager().getLaunchIntentForPackage(var0.getPackageName());
      if (var10000 == null) {
         return null;
      }

      Intent var5 = var10000;
      if (var4 != null) {
         Bundle var7 = var4;
         int var8 = 0;
         if (var2 != null) {
            var2.putExtras(var7);
         }

         var5.putExtras(var7);
      }

      int var6 = 201326592;
      int var16 = var1 != null ? var1 : 0;
      PendingIntent var22;
      if (var3 != null) {
         if (var2 != null) {
            Intent[] var19 = var3.getIntents();
            Intrinsics.checkNotNullExpressionValue(var19, "getIntents(...)");
            Object[] var17 = var19;
            int var9 = 0;
            int var10 = 0;
            int var11 = var17.length;

            boolean var20;
            while (true) {
               if (var10 >= var11) {
                  var20 = true;
                  break;
               }

               Object var12 = var17[var10];
               Intent var13 = (Intent)var12;
               int var14 = 0;
               if (Intrinsics.areEqual(var13.getComponent(), var2.getComponent())) {
                  var20 = false;
                  break;
               }

               var10++;
            }

            if (var20) {
               var3.addNextIntent(var2);
            }
         }

         Intent[] var21 = var3.getIntents();
         Intrinsics.checkNotNullExpressionValue(var21, "getIntents(...)");
         if (var21.length == 0) {
            var3.addNextIntent(var5);
         }

         PendingIntent var18;
         try {
            var18 = var3.getPendingIntent(var16, var6);
         } catch (IllegalStateException var15) {
            var18 = PendingIntent.getActivity(var0, var16, var5, var6);
         }

         var22 = var18;
      } else {
         var22 = var2 != null ? PendingIntent.getActivity(var0, var16, var2, var6) : PendingIntent.getActivity(var0, var16, var5, var6);
      }

      return var22;
   }

   // $VF: synthetic method
   static PendingIntent buildBasePendingIntent$default(Context var0, Integer var1, Intent var2, TaskStackBuilder var3, Bundle var4, int var5, Object var6) {
      if ((var5 & 16) != 0) {
         var4 = null;
      }

      return buildBasePendingIntent(var0, var1, var2, var3, var4);
   }
}
