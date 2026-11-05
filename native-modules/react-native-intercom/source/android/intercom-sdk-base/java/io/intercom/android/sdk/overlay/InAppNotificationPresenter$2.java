package io.intercom.android.sdk.overlay;

import android.view.ViewGroup;
import java.util.List;
import java.util.Map;

class InAppNotificationPresenter$2 implements Runnable {
   // $VF: synthetic field
   final List val$conversations;
   // $VF: synthetic field
   final Map val$notifications;
   // $VF: synthetic field
   final ViewGroup val$root;
   // $VF: synthetic field
   final InAppNotificationPresenter this$0;

   InAppNotificationPresenter$2(InAppNotificationPresenter var1, List var2, Map var3, ViewGroup var4) {
      this.this$0 = var1;
      this.val$conversations = var2;
      this.val$notifications = var3;
      this.val$root = var4;
   }

   @Override
   public void run() {
      this.this$0.syncUpdates(this.val$conversations, this.val$notifications, this.val$root);
   }
}
