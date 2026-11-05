package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH×\u0003J\t\u0010\u000e\u001a\u00020\u0003H×\u0001J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$ServerError;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "code",
         "",
         "<init>",
         "(I)V",
         "getCode",
         "()I",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class MediaUploadRepository$UploadError$ServerError extends MediaUploadRepository$UploadError {
   private final int code;
   public static int $stable;

   static {}

   public MediaUploadRepository$UploadError$ServerError(int var1) {
      super(false, 1, null);
      this.code = var1;
   }

   public final int getCode() {
      return this.code;
   }

   public final int component1() {
      return this.code;
   }

   @NotNull
   public final MediaUploadRepository$UploadError$ServerError copy(int var1) {
      return new MediaUploadRepository$UploadError$ServerError(var1);
   }

   // $VF: synthetic method
   public static MediaUploadRepository$UploadError$ServerError copy$default(MediaUploadRepository$UploadError$ServerError var0, int var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.code;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ServerError(code=" + this.code + ')';
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this.code);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof MediaUploadRepository$UploadError$ServerError)) {
         return false;
      }

      MediaUploadRepository$UploadError$ServerError var2 = (MediaUploadRepository$UploadError$ServerError)var1;
      return this.code == var2.code;
   }
}
