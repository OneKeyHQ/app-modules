package io.intercom.android.sdk.helpcenter.utils;

import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006",
   d2 = {"Lio/intercom/android/sdk/helpcenter/utils/HelpCenterEligibilityChecker;", "", "<init>", "()V", "isEligibleUser", "", "intercom-sdk-base_release"}
)
@StabilityInferred(parameters = 1)
public final class HelpCenterEligibilityChecker {
   @NotNull
   public static final HelpCenterEligibilityChecker INSTANCE = new HelpCenterEligibilityChecker();
   public static int $stable;

   static {}

   private HelpCenterEligibilityChecker() {
   }

   public final boolean isEligibleUser() {
      Twig var1 = LumberMill.getLogger();
      UserIdentity var2 = Injector.get().getUserIdentity();
      boolean var10000;
      if (var2.identityExists() && !var2.isSoftReset()) {
         var10000 = true;
      } else {
         var1.e(
            "The app config has not been retrieved, please call registerUnidentifiedUser() or registerIdentifiedUser(Registration) before calling displayHelpCenter()."
         );
         var10000 = false;
      }

      return var10000;
   }
}
