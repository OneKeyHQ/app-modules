package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import okhttp3.RequestBody;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class MessengerApi$DefaultImpls {
   // $VF: synthetic method
   public static Object getConversationsSuspend$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getConversationsSuspend");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getConversationsSuspend(var1, var2);
   }

   // $VF: synthetic method
   public static Object getUnreadConversationsSuspended$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getUnreadConversationsSuspended");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getUnreadConversationsSuspended(var1, var2);
   }

   // $VF: synthetic method
   public static Object getConversationSuspend$default(MessengerApi var0, String var1, RequestBody var2, Continuation var3, int var4, Object var5) {
      if (var5 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getConversationSuspend");
      }

      if ((var4 & 2) != 0) {
         var2 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getConversationSuspend(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Object openMessengerSuspended$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: openMessengerSuspended");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.openMessengerSuspended(var1, var2);
   }

   // $VF: synthetic method
   public static Object getHomeCardsV2Suspend$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getHomeCardsV2Suspend");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getHomeCardsV2Suspend(var1, var2);
   }

   // $VF: synthetic method
   public static Object getAblyConnectConfigSuspend$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getAblyConnectConfigSuspend");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getAblyConnectConfigSuspend(var1, var2);
   }

   // $VF: synthetic method
   public static Object getAblyAuthTokenSuspend$default(MessengerApi var0, RequestBody var1, Continuation var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getAblyAuthTokenSuspend");
      }

      if ((var3 & 1) != 0) {
         var1 = MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null);
      }

      return var0.getAblyAuthTokenSuspend(var1, var2);
   }
}
