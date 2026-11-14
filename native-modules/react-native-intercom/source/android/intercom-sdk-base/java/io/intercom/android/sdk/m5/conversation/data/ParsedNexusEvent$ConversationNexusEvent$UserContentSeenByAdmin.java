package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusEventType;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0014\u001a\u00020\bHÆ\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0016\u001a\u00020\b2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0010¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "conversationId",
         "",
         "createdByUser",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "isBot",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Avatar;Z)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getCreatedByUser",
         "getAvatar",
         "()Lio/intercom/android/sdk/models/Avatar;",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin extends ParsedNexusEvent$ConversationNexusEvent {
   @NotNull
   private final String conversationId;
   @NotNull
   private final String createdByUser;
   @NotNull
   private final Avatar avatar;
   private final boolean isBot;
   public static final int $stable;

   static { $stable = 8; }

   public ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin(@NotNull String var1, @NotNull String var2, @NotNull Avatar var3, boolean var4) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      super(var1, NexusEventType.AdminIsTyping);
      this.conversationId = var1;
      this.createdByUser = var2;
      this.avatar = var3;
      this.isBot = var4;
   }

   @NotNull
   @Override
   public String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final String getCreatedByUser() {
      return this.createdByUser;
   }

   @NotNull
   public final Avatar getAvatar() {
      return this.avatar;
   }

   public final boolean isBot() {
      return this.isBot;
   }

   @NotNull
   public final String component1() {
      return this.conversationId;
   }

   @NotNull
   public final String component2() {
      return this.createdByUser;
   }

   @NotNull
   public final Avatar component3() {
      return this.avatar;
   }

   public final boolean component4() {
      return this.isBot;
   }

   @NotNull
   public final ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin copy(
      @NotNull String var1, @NotNull String var2, @NotNull Avatar var3, boolean var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      return new ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin copy$default(
      ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin var0, String var1, String var2, Avatar var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.createdByUser;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.avatar;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.isBot;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "UserContentSeenByAdmin(conversationId="
         + this.conversationId
         + ", createdByUser="
         + this.createdByUser
         + ", avatar="
         + this.avatar
         + ", isBot="
         + this.isBot
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      var1 = var1 * 31 + this.createdByUser.hashCode();
      var1 = var1 * 31 + this.avatar.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isBot);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin)) {
         return false;
      } else {
         ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin var2 = (ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.createdByUser, var2.createdByUser)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.avatar, var2.avatar) ? false : this.isBot == var2.isBot;
         }
      }
   }
}
