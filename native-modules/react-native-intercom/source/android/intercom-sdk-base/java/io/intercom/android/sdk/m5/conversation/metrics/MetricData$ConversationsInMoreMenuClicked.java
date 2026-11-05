package io.intercom.android.sdk.m5.conversation.metrics;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u000bH×\u0003J\t\u0010\f\u001a\u00020\rH×\u0001J\t\u0010\u000e\u001a\u00020\u000fH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0006¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData$ConversationsInMoreMenuClicked;",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "isMoreMenuContext",
         "",
         "<init>",
         "(Z)V",
         "()Z",
         "component1",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class MetricData$ConversationsInMoreMenuClicked extends MetricData {
   private final boolean isMoreMenuContext;
   public static int $stable;

   static {}

   public MetricData$ConversationsInMoreMenuClicked(boolean var1) {
      super(null);
      this.isMoreMenuContext = var1;
   }

   public final boolean isMoreMenuContext() {
      return this.isMoreMenuContext;
   }

   public final boolean component1() {
      return this.isMoreMenuContext;
   }

   @NotNull
   public final MetricData$ConversationsInMoreMenuClicked copy(boolean var1) {
      return new MetricData$ConversationsInMoreMenuClicked(var1);
   }

   // $VF: synthetic method
   public static MetricData$ConversationsInMoreMenuClicked copy$default(MetricData$ConversationsInMoreMenuClicked var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.isMoreMenuContext;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationsInMoreMenuClicked(isMoreMenuContext=" + this.isMoreMenuContext + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.isMoreMenuContext);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof MetricData$ConversationsInMoreMenuClicked)) {
         return false;
      }

      MetricData$ConversationsInMoreMenuClicked var2 = (MetricData$ConversationsInMoreMenuClicked)var1;
      return this.isMoreMenuContext == var2.isMoreMenuContext;
   }
}
