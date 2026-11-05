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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\t\u001a\u00060\u0003j\u0002`\u0004HÆ\u0003J\u0017\u0010\n\u001a\u00020\u00002\f\b\u0002\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH×\u0003J\t\u0010\u000f\u001a\u00020\u0010H×\u0001J\t\u0010\u0011\u001a\u00020\u0012H×\u0001R\u0015\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$UnexpectedError;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "exception",
         "Ljava/lang/Exception;",
         "Lkotlin/Exception;",
         "<init>",
         "(Ljava/lang/Exception;)V",
         "getException",
         "()Ljava/lang/Exception;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class MediaUploadRepository$UploadError$UnexpectedError extends MediaUploadRepository$UploadError {
   @NotNull
   private final Exception exception;
   public static final int $stable;

   static { $stable = 8; }

   public MediaUploadRepository$UploadError$UnexpectedError(@NotNull Exception var1) {
      Intrinsics.checkNotNullParameter(var1, "exception");
      super(false, 1, null);
      this.exception = var1;
   }

   @NotNull
   public final Exception getException() {
      return this.exception;
   }

   @NotNull
   public final Exception component1() {
      return this.exception;
   }

   @NotNull
   public final MediaUploadRepository$UploadError$UnexpectedError copy(@NotNull Exception var1) {
      Intrinsics.checkNotNullParameter(var1, "exception");
      return new MediaUploadRepository$UploadError$UnexpectedError(var1);
   }

   // $VF: synthetic method
   public static MediaUploadRepository$UploadError$UnexpectedError copy$default(
      MediaUploadRepository$UploadError$UnexpectedError var0, Exception var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.exception;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "UnexpectedError(exception=" + this.exception + ')';
   }

   @Override
   public int hashCode() {
      return this.exception.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof MediaUploadRepository$UploadError$UnexpectedError)) {
         return false;
      }

      MediaUploadRepository$UploadError$UnexpectedError var2 = (MediaUploadRepository$UploadError$UnexpectedError)var1;
      return Intrinsics.areEqual(this.exception, var2.exception);
   }
}
