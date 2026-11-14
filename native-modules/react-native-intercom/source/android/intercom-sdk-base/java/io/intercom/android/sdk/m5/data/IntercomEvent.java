package io.intercom.android.sdk.m5.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\t\u0004\u0005\u0006\u0007\b\t\n\u000b\fB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\t\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/data/IntercomEvent;",
         "",
         "<init>",
         "()V",
         "NewConversation",
         "CardUpdated",
         "BaseResponseReceived",
         "SoftReset",
         "AppEnteredForeground",
         "ActivityReadyForViewAttachment",
         "ActivityPaused",
         "ActivityStopped",
         "AppEnteredBackground",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$ActivityPaused;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$ActivityReadyForViewAttachment;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$ActivityStopped;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$AppEnteredBackground;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$AppEnteredForeground;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$BaseResponseReceived;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$CardUpdated;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$NewConversation;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent$SoftReset;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class IntercomEvent {
   public static int $stable;

   static {}

   private IntercomEvent() {
   }

   // $VF: synthetic method
   public IntercomEvent(DefaultConstructorMarker var1) {
      this();
   }
}
