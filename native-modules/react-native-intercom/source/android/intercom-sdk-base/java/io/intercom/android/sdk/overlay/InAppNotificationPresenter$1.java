package io.intercom.android.sdk.overlay;

import android.view.ViewGroup;
import java.util.List;

class InAppNotificationPresenter$1 implements Runnable {
   // $VF: synthetic field
   final List val$conversations;
   // $VF: synthetic field
   final ViewGroup val$root;
   // $VF: synthetic field
   final InAppNotificationPresenter this$0;

   InAppNotificationPresenter$1(InAppNotificationPresenter var1, List var2, ViewGroup var3) {
      this.this$0 = var1;
      this.val$conversations = var2;
      this.val$root = var3;
   }

   @Override
   public void run() {
      this.this$0.displayNotificationsAfterAttach(this.val$conversations, this.val$root);
   }
}
