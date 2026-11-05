package io.intercom.android.sdk.blocks.messengercard;

import android.widget.FrameLayout.LayoutParams;

class MessengerCardWebViewInterface$4 implements Runnable {
   // $VF: synthetic field
   final MessengerCardWebViewInterface this$0;

   MessengerCardWebViewInterface$4(MessengerCardWebViewInterface var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      MessengerCardWebViewInterface.access$600(this.this$0).setVisibility(8);
      MessengerCardWebViewInterface.access$400(this.this$0).setLayoutParams(new LayoutParams(-1, -2));
   }
}
