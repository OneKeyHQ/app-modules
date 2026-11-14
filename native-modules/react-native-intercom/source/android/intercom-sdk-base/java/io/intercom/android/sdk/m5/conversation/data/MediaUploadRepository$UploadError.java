package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0005\b\t\n\u000b\fB\u0013\b\u0004\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0005\r\u000e\u000f\u0010\u0011¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "",
         "canRetry",
         "",
         "<init>",
         "(Z)V",
         "getCanRetry",
         "()Z",
         "SizeLimitExceeded",
         "ClientError",
         "NetworkError",
         "ServerError",
         "UnexpectedError",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$ClientError;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$NetworkError;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$ServerError;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$SizeLimitExceeded;",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError$UnexpectedError;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class MediaUploadRepository$UploadError {
   private final boolean canRetry;
   public static int $stable;

   static {}

   private MediaUploadRepository$UploadError(boolean var1) {
      this.canRetry = var1;
   }

   // $VF: synthetic method
   public MediaUploadRepository$UploadError(boolean var1, int var2, DefaultConstructorMarker var3) {
      if ((var2 & 1) != 0) {
         var1 = true;
      }

      this(var1, null);
   }

   public final boolean getCanRetry() {
      return this.canRetry;
   }

   // $VF: synthetic method
   public MediaUploadRepository$UploadError(boolean var1, DefaultConstructorMarker var2) {
      this(var1);
   }
}
