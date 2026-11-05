package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationEndedButton$Metadata;",
         "",
         "mergedIntoConversationId",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getMergedIntoConversationId",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationEndedButton$Metadata {
   @SerializedName("merged_into_conversation_id")
   @NotNull
   private final String mergedIntoConversationId;
   public static int $stable;

   static {}

   public ConversationEndedButton$Metadata(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "mergedIntoConversationId");
      this.mergedIntoConversationId = var1;
   }

   @NotNull
   public final String getMergedIntoConversationId() {
      return this.mergedIntoConversationId;
   }

   @NotNull
   public final String component1() {
      return this.mergedIntoConversationId;
   }

   @NotNull
   public final ConversationEndedButton$Metadata copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "mergedIntoConversationId");
      return new ConversationEndedButton$Metadata(var1);
   }

   // $VF: synthetic method
   public static ConversationEndedButton$Metadata copy$default(ConversationEndedButton$Metadata var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.mergedIntoConversationId;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Metadata(mergedIntoConversationId=" + this.mergedIntoConversationId + ')';
   }

   @Override
   public int hashCode() {
      return this.mergedIntoConversationId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ConversationEndedButton$Metadata)) {
         return false;
      }

      ConversationEndedButton$Metadata var2 = (ConversationEndedButton$Metadata)var1;
      return Intrinsics.areEqual(this.mergedIntoConversationId, var2.mergedIntoConversationId);
   }
}
