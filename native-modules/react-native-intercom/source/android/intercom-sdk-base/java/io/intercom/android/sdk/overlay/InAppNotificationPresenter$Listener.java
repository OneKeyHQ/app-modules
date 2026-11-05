package io.intercom.android.sdk.overlay;

import android.view.ViewGroup;
import io.intercom.android.sdk.models.Conversation;

interface InAppNotificationPresenter$Listener {
   ViewGroup getRootView() throws Exception;

   void markAsDismissed(Conversation var1);

   void openNotification(Conversation var1);
}
