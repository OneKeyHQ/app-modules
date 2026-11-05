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
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$NewMessagesRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "partId",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getPartId",
         "()Ljava/lang/String;",
         "key",
         "getKey",
         "component1",
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
public final class ContentRow$NewMessagesRow implements ContentRow {
   @NotNull
   private final String partId;
   public static int $stable;

   static {}

   public ContentRow$NewMessagesRow(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "partId");
      this.partId = var1;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   @NotNull
   @Override
   public String getKey() {
      return "new_messages_row_" + this.partId;
   }

   @NotNull
   public final String component1() {
      return this.partId;
   }

   @NotNull
   public final ContentRow$NewMessagesRow copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "partId");
      return new ContentRow$NewMessagesRow(var1);
   }

   // $VF: synthetic method
   public static ContentRow$NewMessagesRow copy$default(ContentRow$NewMessagesRow var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.partId;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "NewMessagesRow(partId=" + this.partId + ')';
   }

   @Override
   public int hashCode() {
      return this.partId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$NewMessagesRow)) {
         return false;
      }

      ContentRow$NewMessagesRow var2 = (ContentRow$NewMessagesRow)var1;
      return Intrinsics.areEqual(this.partId, var2.partId);
   }
}
