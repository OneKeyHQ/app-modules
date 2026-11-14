package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J+\u0010\u0011\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0014\u0010\f\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\t¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$MergedConversationRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "description",
         "",
         "conversationId",
         "partId",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getDescription",
         "()Ljava/lang/String;",
         "getConversationId",
         "getPartId",
         "key",
         "getKey",
         "component1",
         "component2",
         "component3",
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
@StabilityInferred(parameters = 1)
public final class ContentRow$MergedConversationRow implements ContentRow {
   @Nullable
   private final String description;
   @Nullable
   private final String conversationId;
   @NotNull
   private final String partId;
   public static int $stable;

   static {}

   public ContentRow$MergedConversationRow(@Nullable String var1, @Nullable String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var3, "partId");
      this.description = var1;
      this.conversationId = var2;
      this.partId = var3;
   }

   @Nullable
   public final String getDescription() {
      return this.description;
   }

   @Nullable
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   @NotNull
   @Override
   public String getKey() {
      return "merged_conversation_row_" + this.partId;
   }

   @Nullable
   public final String component1() {
      return this.description;
   }

   @Nullable
   public final String component2() {
      return this.conversationId;
   }

   @NotNull
   public final String component3() {
      return this.partId;
   }

   @NotNull
   public final ContentRow$MergedConversationRow copy(@Nullable String var1, @Nullable String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var3, "partId");
      return new ContentRow$MergedConversationRow(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ContentRow$MergedConversationRow copy$default(
      ContentRow$MergedConversationRow var0, String var1, String var2, String var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.description;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.conversationId;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.partId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "MergedConversationRow(description=" + this.description + ", conversationId=" + this.conversationId + ", partId=" + this.partId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.description == null ? 0 : this.description.hashCode();
      var1 = var1 * 31 + (this.conversationId == null ? 0 : this.conversationId.hashCode());
      return var1 * 31 + this.partId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$MergedConversationRow)) {
         return false;
      } else {
         ContentRow$MergedConversationRow var2 = (ContentRow$MergedConversationRow)var1;
         if (!Intrinsics.areEqual(this.description, var2.description)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationId, var2.conversationId) ? false : Intrinsics.areEqual(this.partId, var2.partId);
         }
      }
   }
}
