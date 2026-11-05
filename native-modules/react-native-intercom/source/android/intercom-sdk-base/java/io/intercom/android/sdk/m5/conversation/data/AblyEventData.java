package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0001¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÆ\u0003J)\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0001HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00018\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/AblyEventData;",
         "",
         "conversationId",
         "",
         "conversationPart",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "conversation",
         "<init>",
         "(JLio/intercom/android/sdk/models/Part$Builder;Ljava/lang/Object;)V",
         "getConversationId",
         "()J",
         "getConversationPart",
         "()Lio/intercom/android/sdk/models/Part$Builder;",
         "getConversation",
         "()Ljava/lang/Object;",
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
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AblyEventData {
   @SerializedName("conversationId")
   private final long conversationId;
   @SerializedName("mobileConversationPart")
   @NotNull
   private final Part$Builder conversationPart;
   @SerializedName("conversation")
   @Nullable
   private final Object conversation;
   public static final int $stable;

   static { $stable = 8; }

   public AblyEventData(long var1, @NotNull Part$Builder var3, @Nullable Object var4) {
      super();
      Intrinsics.checkNotNullParameter(var3, "conversationPart");
      this.conversationId = var1;
      this.conversationPart = var3;
      this.conversation = var4;
   }

   // $VF: synthetic method
   public AblyEventData(long var1, Part$Builder var3, Object var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var3, (((var5 & 4) != 0) ? (Object) (null) : var4));
   }

   public final long getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final Part$Builder getConversationPart() {
      return this.conversationPart;
   }

   @Nullable
   public final Object getConversation() {
      return this.conversation;
   }

   public final long component1() {
      return this.conversationId;
   }

   @NotNull
   public final Part$Builder component2() {
      return this.conversationPart;
   }

   @Nullable
   public final Object component3() {
      return this.conversation;
   }

   @NotNull
   public final AblyEventData copy(long var1, @NotNull Part$Builder var3, @Nullable Object var4) {
      Intrinsics.checkNotNullParameter(var3, "conversationPart");
      return new AblyEventData(var1, var3, var4);
   }

   // $VF: synthetic method
   public static AblyEventData copy$default(AblyEventData var0, long var1, Part$Builder var3, Object var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.conversationPart;
      }

      if ((var5 & 4) != 0) {
         var4 = var0.conversation;
      }

      return var0.copy(var1, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "AblyEventData(conversationId="
         + this.conversationId
         + ", conversationPart="
         + this.conversationPart
         + ", conversation="
         + this.conversation
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.conversationId);
      var1 = var1 * 31 + this.conversationPart.hashCode();
      return var1 * 31 + (this.conversation == null ? 0 : this.conversation.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AblyEventData)) {
         return false;
      } else {
         AblyEventData var2 = (AblyEventData)var1;
         if (this.conversationId != var2.conversationId) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationPart, var2.conversationPart) ? false : Intrinsics.areEqual(this.conversation, var2.conversation);
         }
      }
   }
}
