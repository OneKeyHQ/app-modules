package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B?\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\fHÆ\u0003JM\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fHÇ\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#H×\u0003J\t\u0010$\u001a\u00020%H×\u0001J\t\u0010&\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;",
         "Lio/intercom/android/sdk/tickets/TicketDetailState;",
         "ticketName",
         "",
         "ticketTimelineCardState",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "ticketAttributes",
         "",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute;",
         "userEmail",
         "conversationId",
         "conversationButtonState",
         "Lio/intercom/android/sdk/tickets/ConversationButtonState;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/tickets/TicketTimelineCardState;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/tickets/ConversationButtonState;)V",
         "getTicketName",
         "()Ljava/lang/String;",
         "getTicketTimelineCardState",
         "()Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "getTicketAttributes",
         "()Ljava/util/List;",
         "getUserEmail",
         "getConversationId",
         "getConversationButtonState",
         "()Lio/intercom/android/sdk/tickets/ConversationButtonState;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
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
public final class TicketDetailState$TicketDetailContentState extends TicketDetailState {
   @NotNull
   private final String ticketName;
   @NotNull
   private final TicketTimelineCardState ticketTimelineCardState;
   @NotNull
   private final List ticketAttributes;
   @NotNull
   private final String userEmail;
   @Nullable
   private final String conversationId;
   @NotNull
   private final ConversationButtonState conversationButtonState;
   public static final int $stable;

   static { $stable = 8; }

   public TicketDetailState$TicketDetailContentState(
      @NotNull String var1,
      @NotNull TicketTimelineCardState var2,
      @NotNull List var3,
      @NotNull String var4,
      @Nullable String var5,
      @NotNull ConversationButtonState var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "ticketName");
      Intrinsics.checkNotNullParameter(var2, "ticketTimelineCardState");
      Intrinsics.checkNotNullParameter(var3, "ticketAttributes");
      Intrinsics.checkNotNullParameter(var4, "userEmail");
      Intrinsics.checkNotNullParameter(var6, "conversationButtonState");
      super(null);
      this.ticketName = var1;
      this.ticketTimelineCardState = var2;
      this.ticketAttributes = var3;
      this.userEmail = var4;
      this.conversationId = var5;
      this.conversationButtonState = var6;
   }

   @NotNull
   public final String getTicketName() {
      return this.ticketName;
   }

   @NotNull
   public final TicketTimelineCardState getTicketTimelineCardState() {
      return this.ticketTimelineCardState;
   }

   @NotNull
   public final List getTicketAttributes() {
      return this.ticketAttributes;
   }

   @NotNull
   public final String getUserEmail() {
      return this.userEmail;
   }

   @Nullable
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final ConversationButtonState getConversationButtonState() {
      return this.conversationButtonState;
   }

   @NotNull
   public final String component1() {
      return this.ticketName;
   }

   @NotNull
   public final TicketTimelineCardState component2() {
      return this.ticketTimelineCardState;
   }

   @NotNull
   public final List component3() {
      return this.ticketAttributes;
   }

   @NotNull
   public final String component4() {
      return this.userEmail;
   }

   @Nullable
   public final String component5() {
      return this.conversationId;
   }

   @NotNull
   public final ConversationButtonState component6() {
      return this.conversationButtonState;
   }

   @NotNull
   public final TicketDetailState$TicketDetailContentState copy(
      @NotNull String var1,
      @NotNull TicketTimelineCardState var2,
      @NotNull List var3,
      @NotNull String var4,
      @Nullable String var5,
      @NotNull ConversationButtonState var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "ticketName");
      Intrinsics.checkNotNullParameter(var2, "ticketTimelineCardState");
      Intrinsics.checkNotNullParameter(var3, "ticketAttributes");
      Intrinsics.checkNotNullParameter(var4, "userEmail");
      Intrinsics.checkNotNullParameter(var6, "conversationButtonState");
      return new TicketDetailState$TicketDetailContentState(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static TicketDetailState$TicketDetailContentState copy$default(
      TicketDetailState$TicketDetailContentState var0,
      String var1,
      TicketTimelineCardState var2,
      List var3,
      String var4,
      String var5,
      ConversationButtonState var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.ticketName;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.ticketTimelineCardState;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.ticketAttributes;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.userEmail;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.conversationId;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.conversationButtonState;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketDetailContentState(ticketName="
         + this.ticketName
         + ", ticketTimelineCardState="
         + this.ticketTimelineCardState
         + ", ticketAttributes="
         + this.ticketAttributes
         + ", userEmail="
         + this.userEmail
         + ", conversationId="
         + this.conversationId
         + ", conversationButtonState="
         + this.conversationButtonState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.ticketName.hashCode();
      var1 = var1 * 31 + this.ticketTimelineCardState.hashCode();
      var1 = var1 * 31 + this.ticketAttributes.hashCode();
      var1 = var1 * 31 + this.userEmail.hashCode();
      var1 = var1 * 31 + (this.conversationId == null ? 0 : this.conversationId.hashCode());
      return var1 * 31 + this.conversationButtonState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketDetailState$TicketDetailContentState)) {
         return false;
      } else {
         TicketDetailState$TicketDetailContentState var2 = (TicketDetailState$TicketDetailContentState)var1;
         if (!Intrinsics.areEqual(this.ticketName, var2.ticketName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticketTimelineCardState, var2.ticketTimelineCardState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ticketAttributes, var2.ticketAttributes)) {
            return false;
         } else if (!Intrinsics.areEqual(this.userEmail, var2.userEmail)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationId, var2.conversationId)
               ? false
               : Intrinsics.areEqual(this.conversationButtonState, var2.conversationButtonState);
         }
      }
   }
}
