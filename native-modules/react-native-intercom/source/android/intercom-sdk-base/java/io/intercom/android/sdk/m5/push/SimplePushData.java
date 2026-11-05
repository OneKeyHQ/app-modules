package io.intercom.android.sdk.m5.push;

import android.content.Context;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.ui.common.ActualStringOrResKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.serialization.builtins.BuiltinSerializersKt;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonBuilder;
import kotlinx.serialization.json.JsonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001Bw\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\u0006\u0010\u0010\u001a\u00020\u0003¢\u0006\u0004\b\u0011\u0010\u0012J\u0006\u0010(\u001a\u00020\u0003J\u000e\u0010)\u001a\u00020\u00032\u0006\u0010*\u001a\u00020+J\u0006\u0010,\u001a\u00020-J\b\u0010!\u001a\u0004\u0018\u00010.J\t\u0010/\u001a\u00020\u0003HÆ\u0003J\t\u00100\u001a\u00020\u0003HÆ\u0003J\t\u00101\u001a\u00020\u0003HÆ\u0003J\t\u00102\u001a\u00020\u0003HÆ\u0003J\t\u00103\u001a\u00020\u0003HÆ\u0003J\t\u00104\u001a\u00020\u0003HÆ\u0003J\t\u00105\u001a\u00020\u0003HÆ\u0003J\t\u00106\u001a\u00020\u0003HÆ\u0003J\t\u00107\u001a\u00020\u0003HÆ\u0003J\t\u00108\u001a\u00020\u0003HÆ\u0003J\t\u00109\u001a\u00020\u0003HÆ\u0003J\t\u0010:\u001a\u00020\u0003HÆ\u0003J\t\u0010;\u001a\u00020\u0003HÆ\u0003J\t\u0010<\u001a\u00020\u0003HÆ\u0003J\u0095\u0001\u0010=\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u0003HÆ\u0001J\u0013\u0010>\u001a\u00020-2\b\u0010?\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010@\u001a\u00020AHÖ\u0001J\t\u0010B\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0014R\u0011\u0010\r\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0014R\u0011\u0010\u000e\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0014R\u0011\u0010\u000f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0014R\u0011\u0010\u0010\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0014R\u000e\u0010\"\u001a\u00020#X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010$\u001a\n &*\u0004\u0018\u00010%0%X\u0082\u0004¢\u0006\u0004\n\u0002\u0010'¨\u0006C",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/SimplePushData;",
         "",
         "intercomPushType",
         "",
         "conversationId",
         "title",
         "message",
         "body",
         "receiver",
         "authorName",
         "appName",
         "contentImageUrl",
         "imageUrl",
         "uri",
         "instanceId",
         "conversationPartType",
         "messageData",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getIntercomPushType",
         "()Ljava/lang/String;",
         "getConversationId",
         "getTitle",
         "getMessage",
         "getBody",
         "getReceiver",
         "getAuthorName",
         "getAppName",
         "getContentImageUrl",
         "getImageUrl",
         "getUri",
         "getInstanceId",
         "getConversationPartType",
         "getMessageData",
         "json",
         "Lkotlinx/serialization/json/Json;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "kotlin.jvm.PlatformType",
         "Lcom/intercom/twig/Twig;",
         "getContentText",
         "getContentTitle",
         "context",
         "Landroid/content/Context;",
         "isIntercomPush",
         "",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
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
         "component12",
         "component13",
         "component14",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomPushData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPushData.kt\nio/intercom/android/sdk/m5/push/SimplePushData\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Json.kt\nkotlinx/serialization/json/Json\n*L\n1#1,179:1\n1#2:180\n147#3:181\n*S KotlinDebug\n*F\n+ 1 IntercomPushData.kt\nio/intercom/android/sdk/m5/push/SimplePushData\n*L\n137#1:181\n*E\n"
)
final class SimplePushData {
   @NotNull
   private final String intercomPushType;
   @NotNull
   private final String conversationId;
   @NotNull
   private final String title;
   @NotNull
   private final String message;
   @NotNull
   private final String body;
   @NotNull
   private final String receiver;
   @NotNull
   private final String authorName;
   @NotNull
   private final String appName;
   @NotNull
   private final String contentImageUrl;
   @NotNull
   private final String imageUrl;
   @NotNull
   private final String uri;
   @NotNull
   private final String instanceId;
   @NotNull
   private final String conversationPartType;
   @NotNull
   private final String messageData;
   @NotNull
   private final Json json;
   private final Twig twig;

   public SimplePushData(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull String var7,
      @NotNull String var8,
      @NotNull String var9,
      @NotNull String var10,
      @NotNull String var11,
      @NotNull String var12,
      @NotNull String var13,
      @NotNull String var14
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "intercomPushType");
      Intrinsics.checkNotNullParameter(var2, "conversationId");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "message");
      Intrinsics.checkNotNullParameter(var5, "body");
      Intrinsics.checkNotNullParameter(var6, "receiver");
      Intrinsics.checkNotNullParameter(var7, "authorName");
      Intrinsics.checkNotNullParameter(var8, "appName");
      Intrinsics.checkNotNullParameter(var9, "contentImageUrl");
      Intrinsics.checkNotNullParameter(var10, "imageUrl");
      Intrinsics.checkNotNullParameter(var11, "uri");
      Intrinsics.checkNotNullParameter(var12, "instanceId");
      Intrinsics.checkNotNullParameter(var13, "conversationPartType");
      Intrinsics.checkNotNullParameter(var14, "messageData");
      this.intercomPushType = var1;
      this.conversationId = var2;
      this.title = var3;
      this.message = var4;
      this.body = var5;
      this.receiver = var6;
      this.authorName = var7;
      this.appName = var8;
      this.contentImageUrl = var9;
      this.imageUrl = var10;
      this.uri = var11;
      this.instanceId = var12;
      this.conversationPartType = var13;
      this.messageData = var14;
      this.json = JsonKt.Json$default(null, ((kotlin.jvm.functions.Function1) (recoveredArgument_1189_0) -> SimplePushData.json$lambda$0((kotlinx.serialization.json.JsonBuilder)recoveredArgument_1189_0)), 1, null);
      this.twig = LumberMill.getLogger();
   }

   @NotNull
   public final String getIntercomPushType() {
      return this.intercomPushType;
   }

   @NotNull
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getMessage() {
      return this.message;
   }

   @NotNull
   public final String getBody() {
      return this.body;
   }

   @NotNull
   public final String getReceiver() {
      return this.receiver;
   }

   @NotNull
   public final String getAuthorName() {
      return this.authorName;
   }

   @NotNull
   public final String getAppName() {
      return this.appName;
   }

   @NotNull
   public final String getContentImageUrl() {
      return this.contentImageUrl;
   }

   @NotNull
   public final String getImageUrl() {
      return this.imageUrl;
   }

   @NotNull
   public final String getUri() {
      return this.uri;
   }

   @NotNull
   public final String getInstanceId() {
      return this.instanceId;
   }

   @NotNull
   public final String getConversationPartType() {
      return this.conversationPartType;
   }

   @NotNull
   public final String recoveredReturn$0f5ca4a8a6d2ec73() {
      return this.messageData;
   }

   @NotNull
   public final String getContentText() {
      CharSequence var1 = this.message;
      CharSequence var10000;
      if (StringsKt.isBlank(var1)) {
         int var2 = 0;
         var10000 = this.body;
      } else {
         var10000 = var1;
      }

      return (String)var10000;
   }

   @NotNull
   public final String getContentTitle(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      String var10000;
      if (!StringsKt.isBlank(this.title)) {
         var10000 = this.title;
      } else if (!StringsKt.isBlank(this.authorName) && !StringsKt.isBlank(this.appName)) {
         int var10001 = string.intercom_teammate_from_company;
         Pair[] var2 = new Pair[]{TuplesKt.to("name", this.authorName), TuplesKt.to("company", this.appName)};
         var10000 = ActualStringOrResKt.parseString(var1, var10001, CollectionsKt.listOf(var2));
      } else {
         var10000 = !StringsKt.isBlank(this.authorName) ? this.authorName : this.appName;
      }

      return var10000;
   }

   public final boolean isIntercomPush() {
      return !StringsKt.isBlank(this.intercomPushType) && Intrinsics.areEqual("intercom_sdk", this.receiver);
   }

   @Nullable
   public final IntercomPushData$ConversationPushData$MessageData getMessageData() {
      IntercomPushData$ConversationPushData$MessageData var10000;
      if (!StringsKt.isBlank(this.body)) {
         var10000 = new IntercomPushData$ConversationPushData$MessageData$Text(this.body);
      } else if (!StringsKt.isBlank(this.messageData)) {
         IntercomPushData$ConversationPushData$MessageData var1;
         try {
            Json var5 = this.json;
            String var2 = this.messageData;
            int var3 = 0;
            var5.getSerializersModule();
            var1 = (IntercomPushData$ConversationPushData$MessageData)var5.decodeFromString(
               BuiltinSerializersKt.getNullable(IntercomPushData$ConversationPushData$MessageData.Companion.serializer()), var2
            );
         } catch (IllegalArgumentException var4) {
            this.twig.e(var4);
            var1 = null;
         }

         var10000 = var1;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @NotNull
   public final String component1() {
      return this.intercomPushType;
   }

   @NotNull
   public final String component2() {
      return this.conversationId;
   }

   @NotNull
   public final String component3() {
      return this.title;
   }

   @NotNull
   public final String component4() {
      return this.message;
   }

   @NotNull
   public final String component5() {
      return this.body;
   }

   @NotNull
   public final String component6() {
      return this.receiver;
   }

   @NotNull
   public final String component7() {
      return this.authorName;
   }

   @NotNull
   public final String component8() {
      return this.appName;
   }

   @NotNull
   public final String component9() {
      return this.contentImageUrl;
   }

   @NotNull
   public final String component10() {
      return this.imageUrl;
   }

   @NotNull
   public final String component11() {
      return this.uri;
   }

   @NotNull
   public final String component12() {
      return this.instanceId;
   }

   @NotNull
   public final String component13() {
      return this.conversationPartType;
   }

   @NotNull
   public final String component14() {
      return this.messageData;
   }

   @NotNull
   public final SimplePushData copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull String var7,
      @NotNull String var8,
      @NotNull String var9,
      @NotNull String var10,
      @NotNull String var11,
      @NotNull String var12,
      @NotNull String var13,
      @NotNull String var14
   ) {
      Intrinsics.checkNotNullParameter(var1, "intercomPushType");
      Intrinsics.checkNotNullParameter(var2, "conversationId");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "message");
      Intrinsics.checkNotNullParameter(var5, "body");
      Intrinsics.checkNotNullParameter(var6, "receiver");
      Intrinsics.checkNotNullParameter(var7, "authorName");
      Intrinsics.checkNotNullParameter(var8, "appName");
      Intrinsics.checkNotNullParameter(var9, "contentImageUrl");
      Intrinsics.checkNotNullParameter(var10, "imageUrl");
      Intrinsics.checkNotNullParameter(var11, "uri");
      Intrinsics.checkNotNullParameter(var12, "instanceId");
      Intrinsics.checkNotNullParameter(var13, "conversationPartType");
      Intrinsics.checkNotNullParameter(var14, "messageData");
      return new SimplePushData(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   // $VF: synthetic method
   public static SimplePushData copy$default(
      SimplePushData var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      String var9,
      String var10,
      String var11,
      String var12,
      String var13,
      String var14,
      int var15,
      Object var16
   ) {
      if ((var15 & 1) != 0) {
         var1 = var0.intercomPushType;
      }

      if ((var15 & 2) != 0) {
         var2 = var0.conversationId;
      }

      if ((var15 & 4) != 0) {
         var3 = var0.title;
      }

      if ((var15 & 8) != 0) {
         var4 = var0.message;
      }

      if ((var15 & 16) != 0) {
         var5 = var0.body;
      }

      if ((var15 & 32) != 0) {
         var6 = var0.receiver;
      }

      if ((var15 & 64) != 0) {
         var7 = var0.authorName;
      }

      if ((var15 & 128) != 0) {
         var8 = var0.appName;
      }

      if ((var15 & 256) != 0) {
         var9 = var0.contentImageUrl;
      }

      if ((var15 & 512) != 0) {
         var10 = var0.imageUrl;
      }

      if ((var15 & 1024) != 0) {
         var11 = var0.uri;
      }

      if ((var15 & 2048) != 0) {
         var12 = var0.instanceId;
      }

      if ((var15 & 4096) != 0) {
         var13 = var0.conversationPartType;
      }

      if ((var15 & 8192) != 0) {
         var14 = var0.messageData;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("SimplePushData(intercomPushType=")
         .append(this.intercomPushType)
         .append(", conversationId=")
         .append(this.conversationId)
         .append(", title=")
         .append(this.title)
         .append(", message=")
         .append(this.message)
         .append(", body=")
         .append(this.body)
         .append(", receiver=")
         .append(this.receiver)
         .append(", authorName=")
         .append(this.authorName)
         .append(", appName=")
         .append(this.appName)
         .append(", contentImageUrl=")
         .append(this.contentImageUrl)
         .append(", imageUrl=")
         .append(this.imageUrl)
         .append(", uri=")
         .append(this.uri)
         .append(", instanceId=");
      var1.append(this.instanceId)
         .append(", conversationPartType=")
         .append(this.conversationPartType)
         .append(", messageData=")
         .append(this.messageData)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.intercomPushType.hashCode();
      var1 = var1 * 31 + this.conversationId.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.message.hashCode();
      var1 = var1 * 31 + this.body.hashCode();
      var1 = var1 * 31 + this.receiver.hashCode();
      var1 = var1 * 31 + this.authorName.hashCode();
      var1 = var1 * 31 + this.appName.hashCode();
      var1 = var1 * 31 + this.contentImageUrl.hashCode();
      var1 = var1 * 31 + this.imageUrl.hashCode();
      var1 = var1 * 31 + this.uri.hashCode();
      var1 = var1 * 31 + this.instanceId.hashCode();
      var1 = var1 * 31 + this.conversationPartType.hashCode();
      return var1 * 31 + this.messageData.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimplePushData)) {
         return false;
      } else {
         SimplePushData var2 = (SimplePushData)var1;
         if (!Intrinsics.areEqual(this.intercomPushType, var2.intercomPushType)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.message, var2.message)) {
            return false;
         } else if (!Intrinsics.areEqual(this.body, var2.body)) {
            return false;
         } else if (!Intrinsics.areEqual(this.receiver, var2.receiver)) {
            return false;
         } else if (!Intrinsics.areEqual(this.authorName, var2.authorName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.appName, var2.appName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.contentImageUrl, var2.contentImageUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.imageUrl, var2.imageUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uri, var2.uri)) {
            return false;
         } else if (!Intrinsics.areEqual(this.instanceId, var2.instanceId)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationPartType, var2.conversationPartType) ? false : Intrinsics.areEqual(this.messageData, var2.messageData);
         }
      }
   }

   private static final Unit json$lambda$0(JsonBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$Json");
      var0.setIgnoreUnknownKeys(true);
      return Unit.INSTANCE;
   }
}
