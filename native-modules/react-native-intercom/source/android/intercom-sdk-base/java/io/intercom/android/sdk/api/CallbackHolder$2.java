package io.intercom.android.sdk.api;

import io.intercom.android.sdk.m5.data.IntercomEvent$BaseResponseReceived;
import io.intercom.android.sdk.models.UsersResponse;
import io.intercom.android.sdk.models.UsersResponse$Builder;

class CallbackHolder$2 extends BaseCallback {
   // $VF: synthetic field
   final CallbackHolder this$0;

   CallbackHolder$2(CallbackHolder var1) {
      this.this$0 = var1;
   }

   public void onSuccess(UsersResponse$Builder var1) {
      UsersResponse var2 = var1.build();
      this.this$0.dataLayer.emitEvent(new IntercomEvent$BaseResponseReceived(var2));
      CallbackHolder.access$000(this.this$0).track(var2);
      CallbackHolder.access$100(this.this$0).update(var2.getUser());
      this.this$0.dataLayer.updateConfig(var2.getConfig());
      this.this$0.dataLayer.updateOverlayConversations(var2.getUnreadConversations().getConversations());
      this.this$0.dataLayer.updateUnreadConversationIds(var2.getUnreadConversations().getUnreadConversationIds());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onSuccess(Object var1) {
      this.onSuccess((UsersResponse$Builder)var1);
   }
}
