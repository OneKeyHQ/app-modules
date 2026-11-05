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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u0003¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0018\u001a\u00020\bHÆ\u0003J\t\u0010\u0019\u001a\u00020\bHÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003JE\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u001c\u001a\u00020\b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH×\u0003J\t\u0010\u001f\u001a\u00020 H×\u0001J\t\u0010!\u001a\u00020\u0003H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0012R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000e¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "conversationId",
         "",
         "createdByUser",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "isBot",
         "",
         "showAvatar",
         "clientId",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Avatar;ZZLjava/lang/String;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getCreatedByUser",
         "getAvatar",
         "()Lio/intercom/android/sdk/models/Avatar;",
         "()Z",
         "getShowAvatar",
         "getClientId",
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
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping extends ParsedNexusEvent$ConversationNexusEvent {
   @NotNull
   private final String conversationId;
   @NotNull
   private final String createdByUser;
   @NotNull
   private final Avatar avatar;
   private final boolean isBot;
   private final boolean showAvatar;
   @NotNull
   private final String clientId;
   public static final int $stable;

   static { $stable = 8; }

   public ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping(
      @NotNull String var1, @NotNull String var2, @NotNull Avatar var3, boolean var4, boolean var5, @NotNull String var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      Intrinsics.checkNotNullParameter(var6, "clientId");
      super(var1, NexusEventType.AdminIsTyping);
      this.conversationId = var1;
      this.createdByUser = var2;
      this.avatar = var3;
      this.isBot = var4;
      this.showAvatar = var5;
      this.clientId = var6;
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

   public final boolean getShowAvatar() {
      return this.showAvatar;
   }

   @NotNull
   public final String getClientId() {
      return this.clientId;
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

   public final boolean component5() {
      return this.showAvatar;
   }

   @NotNull
   public final String component6() {
      return this.clientId;
   }

   @NotNull
   public final ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping copy(
      @NotNull String var1, @NotNull String var2, @NotNull Avatar var3, boolean var4, boolean var5, @NotNull String var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      Intrinsics.checkNotNullParameter(var6, "clientId");
      return new ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping copy$default(
      ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping var0,
      String var1,
      String var2,
      Avatar var3,
      boolean var4,
      boolean var5,
      String var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.createdByUser;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.avatar;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.isBot;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.showAvatar;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.clientId;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "AdminIsTyping(conversationId="
         + this.conversationId
         + ", createdByUser="
         + this.createdByUser
         + ", avatar="
         + this.avatar
         + ", isBot="
         + this.isBot
         + ", showAvatar="
         + this.showAvatar
         + ", clientId="
         + this.clientId
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      var1 = var1 * 31 + this.createdByUser.hashCode();
      var1 = var1 * 31 + this.avatar.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isBot);
      var1 = var1 * 31 + Boolean.hashCode(this.showAvatar);
      return var1 * 31 + this.clientId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping)) {
         return false;
      } else {
         ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping var2 = (ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.createdByUser, var2.createdByUser)) {
            return false;
         } else if (!Intrinsics.areEqual(this.avatar, var2.avatar)) {
            return false;
         } else if (this.isBot != var2.isBot) {
            return false;
         } else {
            return this.showAvatar != var2.showAvatar ? false : Intrinsics.areEqual(this.clientId, var2.clientId);
         }
      }
   }
}
