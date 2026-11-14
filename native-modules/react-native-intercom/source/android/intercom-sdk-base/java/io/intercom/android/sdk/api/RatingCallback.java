package io.intercom.android.sdk.api;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&¨\u0006\u0007",
   d2 = {
         "Lio/intercom/android/sdk/api/RatingCallback;",
         "",
         "onSuccess",
         "",
         "onFailure",
         "errorObject",
         "Lio/intercom/android/sdk/api/ErrorObject;",
         "intercom-sdk-base_release"
   }
)
public interface RatingCallback {
   void onSuccess();

   void onFailure(@NotNull ErrorObject var1);
}
