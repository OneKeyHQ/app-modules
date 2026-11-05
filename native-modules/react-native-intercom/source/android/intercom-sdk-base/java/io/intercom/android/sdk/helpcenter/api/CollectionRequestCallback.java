package io.intercom.android.sdk.helpcenter.api;

import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H&J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u0003H&¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/CollectionRequestCallback;",
         "",
         "onComplete",
         "",
         "response",
         "",
         "Lio/intercom/android/sdk/helpcenter/collections/HelpCenterCollection;",
         "onError",
         "errorCode",
         "",
         "onFailure",
         "intercom-sdk-base_release"
   }
)
public interface CollectionRequestCallback {
   void onComplete(@NotNull List var1);

   void onError(int var1);

   void onFailure();
}
