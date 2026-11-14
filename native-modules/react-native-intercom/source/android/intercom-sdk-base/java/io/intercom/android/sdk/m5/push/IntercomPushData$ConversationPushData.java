package io.intercom.android.sdk.m5.push;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001:\u0001$B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001c\u001a\u00020\tHÆ\u0003J\t\u0010\u001d\u001a\u00020\tHÆ\u0003JE\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\tHÇ\u0001J\u0013\u0010\u001f\u001a\u00020\t2\b\u0010 \u001a\u0004\u0018\u00010!H×\u0003J\t\u0010\"\u001a\u00020\u0015H×\u0001J\t\u0010#\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0013R\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0013R\u0014\u0010\u0014\u001a\u00020\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData;",
         "authorName",
         "",
         "avatarUrl",
         "conversationId",
         "messageData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "isCurrentUser",
         "",
         "isNewConversation",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;ZZ)V",
         "getAuthorName",
         "()Ljava/lang/String;",
         "getAvatarUrl",
         "getConversationId",
         "getMessageData",
         "()Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "()Z",
         "notificationId",
         "",
         "getNotificationId",
         "()I",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "MessageData",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPushData$ConversationPushData implements IntercomPushData {
   @NotNull
   private final String authorName;
   @NotNull
   private final String avatarUrl;
   @NotNull
   private final String conversationId;
   @NotNull
   private final IntercomPushData$ConversationPushData$MessageData messageData;
   private final boolean isCurrentUser;
   private final boolean isNewConversation;
   public static final int $stable;

   static { $stable = 8; }

   public IntercomPushData$ConversationPushData(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull IntercomPushData$ConversationPushData$MessageData var4,
      boolean var5,
      boolean var6
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "authorName");
      Intrinsics.checkNotNullParameter(var2, "avatarUrl");
      Intrinsics.checkNotNullParameter(var3, "conversationId");
      Intrinsics.checkNotNullParameter(var4, "messageData");
      this.authorName = var1;
      this.avatarUrl = var2;
      this.conversationId = var3;
      this.messageData = var4;
      this.isCurrentUser = var5;
      this.isNewConversation = var6;
   }

   @NotNull
   public final String getAuthorName() {
      return this.authorName;
   }

   @NotNull
   public final String getAvatarUrl() {
      return this.avatarUrl;
   }

   @NotNull
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final IntercomPushData$ConversationPushData$MessageData getMessageData() {
      return this.messageData;
   }

   public final boolean isCurrentUser() {
      return this.isCurrentUser;
   }

   public final boolean isNewConversation() {
      return this.isNewConversation;
   }

   @Override
   public int getNotificationId() {
      return this.conversationId.hashCode();
   }

   @NotNull
   public final String component1() {
      return this.authorName;
   }

   @NotNull
   public final String component2() {
      return this.avatarUrl;
   }

   @NotNull
   public final String component3() {
      return this.conversationId;
   }

   @NotNull
   public final IntercomPushData$ConversationPushData$MessageData component4() {
      return this.messageData;
   }

   public final boolean component5() {
      return this.isCurrentUser;
   }

   public final boolean component6() {
      return this.isNewConversation;
   }

   @NotNull
   public final IntercomPushData$ConversationPushData copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull IntercomPushData$ConversationPushData$MessageData var4,
      boolean var5,
      boolean var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "authorName");
      Intrinsics.checkNotNullParameter(var2, "avatarUrl");
      Intrinsics.checkNotNullParameter(var3, "conversationId");
      Intrinsics.checkNotNullParameter(var4, "messageData");
      return new IntercomPushData$ConversationPushData(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static IntercomPushData$ConversationPushData copy$default(
      IntercomPushData$ConversationPushData var0,
      String var1,
      String var2,
      String var3,
      IntercomPushData$ConversationPushData$MessageData var4,
      boolean var5,
      boolean var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.authorName;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.avatarUrl;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.conversationId;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.messageData;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.isCurrentUser;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.isNewConversation;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationPushData(authorName="
         + this.authorName
         + ", avatarUrl="
         + this.avatarUrl
         + ", conversationId="
         + this.conversationId
         + ", messageData="
         + this.messageData
         + ", isCurrentUser="
         + this.isCurrentUser
         + ", isNewConversation="
         + this.isNewConversation
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.authorName.hashCode();
      var1 = var1 * 31 + this.avatarUrl.hashCode();
      var1 = var1 * 31 + this.conversationId.hashCode();
      var1 = var1 * 31 + this.messageData.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isCurrentUser);
      return var1 * 31 + Boolean.hashCode(this.isNewConversation);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomPushData$ConversationPushData)) {
         return false;
      } else {
         IntercomPushData$ConversationPushData var2 = (IntercomPushData$ConversationPushData)var1;
         if (!Intrinsics.areEqual(this.authorName, var2.authorName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.avatarUrl, var2.avatarUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.messageData, var2.messageData)) {
            return false;
         } else {
            return this.isCurrentUser != var2.isCurrentUser ? false : this.isNewConversation == var2.isNewConversation;
         }
      }
   }
}
