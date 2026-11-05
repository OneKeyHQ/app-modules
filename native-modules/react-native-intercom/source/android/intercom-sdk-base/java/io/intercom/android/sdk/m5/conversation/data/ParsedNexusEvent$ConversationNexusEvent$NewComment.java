package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusEventType;
import io.intercom.android.sdk.models.Part$Builder;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0007HÆ\u0003J5\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$NewComment;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "conversationId",
         "",
         "createdByUser",
         "ticketId",
         "partBuilder",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Part$Builder;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getCreatedByUser",
         "getTicketId",
         "getPartBuilder",
         "()Lio/intercom/android/sdk/models/Part$Builder;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ParsedNexusEvent$ConversationNexusEvent$NewComment extends ParsedNexusEvent$ConversationNexusEvent {
   @NotNull
   private final String conversationId;
   @NotNull
   private final String createdByUser;
   @Nullable
   private final String ticketId;
   @Nullable
   private final Part$Builder partBuilder;
   public static final int $stable;

   static { $stable = 8; }

   public ParsedNexusEvent$ConversationNexusEvent$NewComment(@NotNull String var1, @NotNull String var2, @Nullable String var3, @Nullable Part$Builder var4) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      super(var1, NexusEventType.NewComment);
      this.conversationId = var1;
      this.createdByUser = var2;
      this.ticketId = var3;
      this.partBuilder = var4;
   }

   // $VF: synthetic method
   public ParsedNexusEvent$ConversationNexusEvent$NewComment(String var1, String var2, String var3, Part$Builder var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (String) (null) : var3), (((var5 & 8) != 0) ? (Part$Builder) (null) : var4));
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

   @Nullable
   public final String getTicketId() {
      return this.ticketId;
   }

   @Nullable
   public final Part$Builder getPartBuilder() {
      return this.partBuilder;
   }

   @NotNull
   public final String component1() {
      return this.conversationId;
   }

   @NotNull
   public final String component2() {
      return this.createdByUser;
   }

   @Nullable
   public final String component3() {
      return this.ticketId;
   }

   @Nullable
   public final Part$Builder component4() {
      return this.partBuilder;
   }

   @NotNull
   public final ParsedNexusEvent$ConversationNexusEvent$NewComment copy(
      @NotNull String var1, @NotNull String var2, @Nullable String var3, @Nullable Part$Builder var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "createdByUser");
      return new ParsedNexusEvent$ConversationNexusEvent$NewComment(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ParsedNexusEvent$ConversationNexusEvent$NewComment copy$default(
      ParsedNexusEvent$ConversationNexusEvent$NewComment var0, String var1, String var2, String var3, Part$Builder var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.createdByUser;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.ticketId;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.partBuilder;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "NewComment(conversationId="
         + this.conversationId
         + ", createdByUser="
         + this.createdByUser
         + ", ticketId="
         + this.ticketId
         + ", partBuilder="
         + this.partBuilder
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      var1 = var1 * 31 + this.createdByUser.hashCode();
      var1 = var1 * 31 + (this.ticketId == null ? 0 : this.ticketId.hashCode());
      return var1 * 31 + (this.partBuilder == null ? 0 : this.partBuilder.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ParsedNexusEvent$ConversationNexusEvent$NewComment)) {
         return false;
      } else {
         ParsedNexusEvent$ConversationNexusEvent$NewComment var2 = (ParsedNexusEvent$ConversationNexusEvent$NewComment)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.createdByUser, var2.createdByUser)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.ticketId, var2.ticketId) ? false : Intrinsics.areEqual(this.partBuilder, var2.partBuilder);
         }
      }
   }
}
