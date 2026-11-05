package io.intercom.android.sdk.m5.push;

import android.os.Bundle;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0012\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0013",
   d2 = {
         "getSimplePushDataFromBundle",
         "Lio/intercom/android/sdk/m5/push/SimplePushData;",
         "bundle",
         "Landroid/os/Bundle;",
         "AppName",
         "",
         "AuthorName",
         "Body",
         "ContentImageUrl",
         "ConversationId",
         "ConversationPartType",
         "ImageUrl",
         "InstanceId",
         "IntercomPushType",
         "Message",
         "MessageData",
         "Receiver",
         "Title",
         "Uri",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPushDataKt {
   @NotNull
   private static final String AppName = "app_name";
   @NotNull
   private static final String AuthorName = "author_name";
   @NotNull
   private static final String Body = "body";
   @NotNull
   private static final String ContentImageUrl = "content_image_url";
   @NotNull
   private static final String ConversationId = "conversation_id";
   @NotNull
   private static final String ConversationPartType = "conversation_part_type";
   @NotNull
   private static final String ImageUrl = "image_url";
   @NotNull
   private static final String InstanceId = "instance_id";
   @NotNull
   private static final String IntercomPushType = "intercom_push_type";
   @NotNull
   private static final String Message = "message";
   @NotNull
   private static final String MessageData = "message_data";
   @NotNull
   private static final String Receiver = "receiver";
   @NotNull
   private static final String Title = "title";
   @NotNull
   private static final String Uri = "uri";

   private static final SimplePushData getSimplePushDataFromBundle(Bundle var0) {
      String var10000 = var0.getString("intercom_push_type", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var1 = var10000;
      var10000 = var0.getString("conversation_id", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var2 = var10000;
      var10000 = var0.getString("title", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var3 = var10000;
      var10000 = var0.getString("message", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var4 = var10000;
      var10000 = var0.getString("receiver", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var5 = var10000;
      var10000 = var0.getString("author_name", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var6 = var10000;
      var10000 = var0.getString("body", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var7 = var10000;
      var10000 = var0.getString("app_name", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var8 = var10000;
      var10000 = var0.getString("content_image_url", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var9 = var10000;
      var10000 = var0.getString("image_url", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var10 = var10000;
      var10000 = var0.getString("uri", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var11 = var10000;
      var10000 = var0.getString("instance_id", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var12 = var10000;
      var10000 = var0.getString("conversation_part_type", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var13 = var10000;
      var10000 = var0.getString("message_data", "");
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var14 = var10000;
      return new SimplePushData(var1, var2, var3, var4, var7, var5, var6, var8, var9, var10, var11, var12, var13, var14);
   }

   // $VF: synthetic method
   public static final SimplePushData access$getSimplePushDataFromBundle(Bundle var0) {
      return getSimplePushDataFromBundle(var0);
   }
}
