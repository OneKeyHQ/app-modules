package io.intercom.android.sdk.m5.push.ui;

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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u0019B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J-\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation;",
         "",
         "conversationId",
         "",
         "conversationTitle",
         "messages",
         "",
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation$Message;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getConversationTitle",
         "getMessages",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Message",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPushConversation {
   @NotNull
   private final String conversationId;
   @NotNull
   private final String conversationTitle;
   @NotNull
   private final List messages;
   public static final int $stable;

   static { $stable = 8; }

   public IntercomPushConversation(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "conversationTitle");
      Intrinsics.checkNotNullParameter(var3, "messages");
      this.conversationId = var1;
      this.conversationTitle = var2;
      this.messages = var3;
   }

   @NotNull
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final String getConversationTitle() {
      return this.conversationTitle;
   }

   @NotNull
   public final List getMessages() {
      return this.messages;
   }

   @NotNull
   public final String component1() {
      return this.conversationId;
   }

   @NotNull
   public final String component2() {
      return this.conversationTitle;
   }

   @NotNull
   public final List component3() {
      return this.messages;
   }

   @NotNull
   public final IntercomPushConversation copy(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "conversationTitle");
      Intrinsics.checkNotNullParameter(var3, "messages");
      return new IntercomPushConversation(var1, var2, var3);
   }

   // $VF: synthetic method
   public static IntercomPushConversation copy$default(IntercomPushConversation var0, String var1, String var2, List var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.conversationTitle;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.messages;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomPushConversation(conversationId="
         + this.conversationId
         + ", conversationTitle="
         + this.conversationTitle
         + ", messages="
         + this.messages
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      var1 = var1 * 31 + this.conversationTitle.hashCode();
      return var1 * 31 + this.messages.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomPushConversation)) {
         return false;
      } else {
         IntercomPushConversation var2 = (IntercomPushConversation)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationTitle, var2.conversationTitle) ? false : Intrinsics.areEqual(this.messages, var2.messages);
         }
      }
   }
}
