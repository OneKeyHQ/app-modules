package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\u000b2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0003H×\u0001J\t\u0010\u0014\u001a\u00020\u0015H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\n\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\r¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository$AddResult;",
         "",
         "addedCount",
         "",
         "rejectedByLimit",
         "<init>",
         "(II)V",
         "getAddedCount",
         "()I",
         "getRejectedByLimit",
         "hasLimitRejections",
         "",
         "getHasLimitRejections",
         "()Z",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class MediaUploadStateRepository$AddResult {
   private final int addedCount;
   private final int rejectedByLimit;
   public static int $stable;

   static {}

   public MediaUploadStateRepository$AddResult(int var1, int var2) {
      this.addedCount = var1;
      this.rejectedByLimit = var2;
   }

   public final int getAddedCount() {
      return this.addedCount;
   }

   public final int getRejectedByLimit() {
      return this.rejectedByLimit;
   }

   public final boolean getHasLimitRejections() {
      return this.rejectedByLimit > 0;
   }

   public final int component1() {
      return this.addedCount;
   }

   public final int component2() {
      return this.rejectedByLimit;
   }

   @NotNull
   public final MediaUploadStateRepository$AddResult copy(int var1, int var2) {
      return new MediaUploadStateRepository$AddResult(var1, var2);
   }

   // $VF: synthetic method
   public static MediaUploadStateRepository$AddResult copy$default(MediaUploadStateRepository$AddResult var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.addedCount;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.rejectedByLimit;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AddResult(addedCount=" + this.addedCount + ", rejectedByLimit=" + this.rejectedByLimit + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.addedCount);
      return var1 * 31 + Integer.hashCode(this.rejectedByLimit);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof MediaUploadStateRepository$AddResult)) {
         return false;
      }

      MediaUploadStateRepository$AddResult var2 = (MediaUploadStateRepository$AddResult)var1;
      return this.addedCount != var2.addedCount ? false : this.rejectedByLimit == var2.rejectedByLimit;
   }
}
