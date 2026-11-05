package io.intercom.android.sdk.helpcenter.api;

import io.intercom.android.sdk.helpcenter.sections.HelpCenterCollectionContent;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\u0003H&¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/CollectionContentRequestCallback;",
         "",
         "onComplete",
         "",
         "response",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent;",
         "onError",
         "errorCode",
         "",
         "onFailure",
         "intercom-sdk-base_release"
   }
)
public interface CollectionContentRequestCallback {
   void onComplete(@NotNull HelpCenterCollectionContent var1);

   void onError(int var1);

   void onFailure();
}
