package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0006HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0006H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$SizeLimitExceeded;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "size",
         "",
         "limit",
         "limitMB",
         "",
         "<init>",
         "(JJLjava/lang/String;)V",
         "getSize",
         "()J",
         "getLimit",
         "getLimitMB",
         "()Ljava/lang/String;",
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
public final class MediaUploadRepository$UploadError$SizeLimitExceeded extends MediaUploadRepository$UploadError {
   private final long size;
   private final long limit;
   @NotNull
   private final String limitMB;
   public static int $stable;

   static {}

   public MediaUploadRepository$UploadError$SizeLimitExceeded(long var1, long var3, @NotNull String var5) {
      Intrinsics.checkNotNullParameter(var5, "limitMB");
      super(false, null);
      this.size = var1;
      this.limit = var3;
      this.limitMB = var5;
   }

   public final long getSize() {
      return this.size;
   }

   public final long getLimit() {
      return this.limit;
   }

   @NotNull
   public final String getLimitMB() {
      return this.limitMB;
   }

   public final long component1() {
      return this.size;
   }

   public final long component2() {
      return this.limit;
   }

   @NotNull
   public final String component3() {
      return this.limitMB;
   }

   @NotNull
   public final MediaUploadRepository$UploadError$SizeLimitExceeded copy(long var1, long var3, @NotNull String var5) {
      Intrinsics.checkNotNullParameter(var5, "limitMB");
      return new MediaUploadRepository$UploadError$SizeLimitExceeded(var1, var3, var5);
   }

   // $VF: synthetic method
   public static MediaUploadRepository$UploadError$SizeLimitExceeded copy$default(
      MediaUploadRepository$UploadError$SizeLimitExceeded var0, long var1, long var3, String var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.size;
      }

      if ((var6 & 2) != 0) {
         var3 = var0.limit;
      }

      if ((var6 & 4) != 0) {
         var5 = var0.limitMB;
      }

      return var0.copy(var1, var3, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "SizeLimitExceeded(size=" + this.size + ", limit=" + this.limit + ", limitMB=" + this.limitMB + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.size);
      var1 = var1 * 31 + Long.hashCode(this.limit);
      return var1 * 31 + this.limitMB.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MediaUploadRepository$UploadError$SizeLimitExceeded)) {
         return false;
      } else {
         MediaUploadRepository$UploadError$SizeLimitExceeded var2 = (MediaUploadRepository$UploadError$SizeLimitExceeded)var1;
         if (this.size != var2.size) {
            return false;
         } else {
            return this.limit != var2.limit ? false : Intrinsics.areEqual(this.limitMB, var2.limitMB);
         }
      }
   }
}
