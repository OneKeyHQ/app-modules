package io.intercom.android.sdk.api;

import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.UnreadConversationsTracker;
import retrofit2.Callback;

class CallbackHolder {
   final IntercomDataLayer dataLayer;
   private static final Twig TWIG = LumberMill.getLogger();
   private final UserIdentity userIdentity;
   private final UnreadConversationsTracker unreadConversationsTracker;

   CallbackHolder(IntercomDataLayer var1, UserIdentity var2, UnreadConversationsTracker var3) {
      this.dataLayer = var1;
      this.userIdentity = var2;
      this.unreadConversationsTracker = var3;
   }

   BaseCallback logEventCallback() {
      return new CallbackHolder$1(this);
   }

   BaseCallback unreadCallback() {
      return new CallbackHolder$2(this);
   }

   Callback loggingCallback(String var1) {
      return new CallbackHolder$3(this, var1);
   }

   Callback ratingCallback(String var1, @Nullable RatingCallback var2) {
      return new CallbackHolder$4(this, var1, var2);
   }

   // $VF: synthetic method
   static UnreadConversationsTracker access$000(CallbackHolder var0) {
      return var0.unreadConversationsTracker;
   }

   // $VF: synthetic method
   static UserIdentity access$100(CallbackHolder var0) {
      return var0.userIdentity;
   }

   // $VF: synthetic method
   static Twig access$200() {
      return TWIG;
   }
}
