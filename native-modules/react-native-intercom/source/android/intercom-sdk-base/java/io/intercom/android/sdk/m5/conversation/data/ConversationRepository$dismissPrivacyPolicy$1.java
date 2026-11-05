package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007",
   d2 = {
         "io/intercom/android/sdk/m5/conversation/data/ConversationRepository$dismissPrivacyPolicy$1",
         "Lio/intercom/android/sdk/IntercomStatusCallback;",
         "onSuccess",
         "",
         "onFailure",
         "intercomError",
         "Lio/intercom/android/sdk/IntercomError;",
         "intercom-sdk-base_release"
   }
)
public final class ConversationRepository$dismissPrivacyPolicy$1 implements IntercomStatusCallback {
   ConversationRepository$dismissPrivacyPolicy$1() {
   }

   @Override
   public void onSuccess() {
   }

   @Override
   public void onFailure(IntercomError var1) {
      Intrinsics.checkNotNullParameter(var1, "intercomError");
   }
}
