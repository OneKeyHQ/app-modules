package io.intercom.android.sdk.overlay;

import io.intercom.android.sdk.models.Conversation;

interface InAppNotification$Listener {
   void onInAppNotificationTap(Conversation var1);

   void onInAppNotificationDismiss(InAppNotification var1);
}
