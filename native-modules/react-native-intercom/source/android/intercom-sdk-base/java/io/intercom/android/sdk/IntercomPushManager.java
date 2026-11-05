package io.intercom.android.sdk;

import android.app.Application;
import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IntercomPushManager {
   private static final Twig TWIG = LumberMill.getLogger();

   public static IntercomPushManager$IntercomPushIntegrationType getInstalledModuleType() {
      IntercomPushManager$IntercomPushIntegrationType var0 = IntercomPushManager$IntercomPushIntegrationType.NONE;
      if (fcmModuleInstalled()) {
         TWIG.internal("FCM is installed");
         var0 = IntercomPushManager$IntercomPushIntegrationType.FCM;
      }

      return var0;
   }

   public static void initializeFcmService(Application var0) {
      Class var1 = getFcmServiceClass();
      if (var1 != null) {
         try {
            Method var2 = var1.getDeclaredMethod("initialize", Application.class);
            var2.invoke(null, var0);
         } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException var3) {
            TWIG.internal("FCM is installed but initialize method was not found");
         }
      }
   }

   private static boolean fcmModuleInstalled() {
      return getFcmServiceClass() != null;
   }

   @Nullable
   private static Class getFcmServiceClass() {
      try {
         return Class.forName("io.intercom.android.sdk.fcm.IntercomFcmMessengerService");
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }
}
