package io.intercom.android.sdk.m5.conversation.states;

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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0006HÆ\u0003J#\u0010\u0011\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0006H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$QuickRepliesRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "replyOptions",
         "",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "partId",
         "",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;)V",
         "getReplyOptions",
         "()Ljava/util/List;",
         "getPartId",
         "()Ljava/lang/String;",
         "key",
         "getKey",
         "component1",
         "component2",
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
public final class ContentRow$QuickRepliesRow implements ContentRow {
   @NotNull
   private final List replyOptions;
   @NotNull
   private final String partId;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$QuickRepliesRow(@NotNull List var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "replyOptions");
      Intrinsics.checkNotNullParameter(var2, "partId");
      this.replyOptions = var1;
      this.partId = var2;
   }

   @NotNull
   public final List getReplyOptions() {
      return this.replyOptions;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   @NotNull
   @Override
   public String getKey() {
      return "quick_replies_row_" + this.partId;
   }

   @NotNull
   public final List component1() {
      return this.replyOptions;
   }

   @NotNull
   public final String component2() {
      return this.partId;
   }

   @NotNull
   public final ContentRow$QuickRepliesRow copy(@NotNull List var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "replyOptions");
      Intrinsics.checkNotNullParameter(var2, "partId");
      return new ContentRow$QuickRepliesRow(var1, var2);
   }

   // $VF: synthetic method
   public static ContentRow$QuickRepliesRow copy$default(ContentRow$QuickRepliesRow var0, List var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.replyOptions;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.partId;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "QuickRepliesRow(replyOptions=" + this.replyOptions + ", partId=" + this.partId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.replyOptions.hashCode();
      return var1 * 31 + this.partId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$QuickRepliesRow)) {
         return false;
      }

      ContentRow$QuickRepliesRow var2 = (ContentRow$QuickRepliesRow)var1;
      return !Intrinsics.areEqual(this.replyOptions, var2.replyOptions) ? false : Intrinsics.areEqual(this.partId, var2.partId);
   }
}
