package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$DayDividerRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "timestamp",
         "",
         "<init>",
         "(J)V",
         "getTimestamp",
         "()J",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
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
public final class ContentRow$DayDividerRow implements ContentRow {
   private final long timestamp;
   public static int $stable;

   static {}

   public ContentRow$DayDividerRow(long var1) {
      this.timestamp = var1;
   }

   public final long getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   @Override
   public String getKey() {
      return "day_divider_row_" + Long.hashCode(this.timestamp);
   }

   public final long component1() {
      return this.timestamp;
   }

   @NotNull
   public final ContentRow$DayDividerRow copy(long var1) {
      return new ContentRow$DayDividerRow(var1);
   }

   // $VF: synthetic method
   public static ContentRow$DayDividerRow copy$default(ContentRow$DayDividerRow var0, long var1, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.timestamp;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "DayDividerRow(timestamp=" + this.timestamp + ')';
   }

   @Override
   public int hashCode() {
      return Long.hashCode(this.timestamp);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$DayDividerRow)) {
         return false;
      }

      ContentRow$DayDividerRow var2 = (ContentRow$DayDividerRow)var1;
      return this.timestamp == var2.timestamp;
   }
}
