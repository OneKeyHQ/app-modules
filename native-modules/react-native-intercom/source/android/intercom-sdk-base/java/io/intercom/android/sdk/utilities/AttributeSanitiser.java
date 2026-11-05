package io.intercom.android.sdk.utilities;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.util.Map;

public class AttributeSanitiser {
   private static final String EMAIL = "email";
   private static final String USER_ID = "user_id";
   private static final Twig twig = LumberMill.getLogger();

   public static void anonymousSanitisation(Map var0) {
      if (var0.containsKey("email")) {
         Object var1 = var0.remove("email");
         twig.e(
            String.format("You cannot update the email of an anonymous user. Please call registerIdentified user instead. The email: %s was NOT applied", var1)
         );
      }

      if (var0.containsKey("user_id")) {
         Object var2 = var0.remove("user_id");
         twig.e(
            String.format(
               "You cannot update the user_id of an anonymous user. Please call registerIdentified user instead. The user_id: %s was NOT applied", var2
            )
         );
      }
   }
}
