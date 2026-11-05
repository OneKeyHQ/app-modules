package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewGroup;
import io.intercom.android.sdk.models.Conversation;
import java.util.List;
import java.util.Map;

class InAppNotificationPresenter$3 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final Conversation val$conversation;
   // $VF: synthetic field
   final ViewGroup val$root;
   // $VF: synthetic field
   final Map val$notifications;
   // $VF: synthetic field
   final List val$conversations;
   // $VF: synthetic field
   final InAppNotificationPresenter this$0;

   InAppNotificationPresenter$3(InAppNotificationPresenter var1, Conversation var2, ViewGroup var3, Map var4, List var5) {
      this.this$0 = var1;
      this.val$conversation = var2;
      this.val$root = var3;
      this.val$notifications = var4;
      this.val$conversations = var5;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.addNewView(this.val$conversation, this.val$root, this.val$notifications);
      this.this$0.handler.postDelayed(new InAppNotificationPresenter$3$1(this), 220L);
   }
}
