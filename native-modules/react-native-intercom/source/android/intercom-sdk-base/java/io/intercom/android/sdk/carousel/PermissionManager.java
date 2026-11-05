package io.intercom.android.sdk.carousel;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionManager {
   private static final Twig twig = LumberMill.getLogger();
   private static final String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
   private final Activity activity;

   public PermissionManager(Activity var1) {
      this.activity = var1;
   }

   public void requestPermissions(@NonNull String[] var1, @IntRange(from = 0L) int var2) {
      ActivityCompat.requestPermissions(this.activity, var1, var2);
   }

   public boolean permissionsGranted(List var1) {
      for (java.lang.Object recoveredEntryElement_0_33 : var1) {
         String var3 = (String) recoveredEntryElement_0_33;
         if (this.getPermissionStatus(var3) != 0) {
            return false;
         }
      }

      return true;
   }

   public boolean anyPermissionPermanentlyDeniedInResult(String[] var1, int[] var2) {
      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var2[var3] == -1 && !ActivityCompat.shouldShowRequestPermissionRationale(this.activity, var1[var3])) {
            return true;
         }
      }

      return false;
   }

   public List permissionsExistInManifest(List var1) {
      List var2 = new ArrayList();
      List var3 = this.permissionsInManifest();

      for (java.lang.Object recoveredEntryElement_0_56 : var1) {
         String var5 = (String) recoveredEntryElement_0_56;
         if (var3.contains(var5)) {
            boolean var6 = "android.permission.ACCESS_BACKGROUND_LOCATION".equals(var5);
            boolean var7 = VERSION.SDK_INT < 29;
            if (!var6 || !var7) {
               var2.add(var5);
            }
         }
      }

      return var2;
   }

   public int getPermissionStatus(String var1) {
      if (ContextCompat.checkSelfPermission(this.activity, var1) == 0) {
         return 0;
      } else {
         return ActivityCompat.shouldShowRequestPermissionRationale(this.activity, var1) ? 1 : 2;
      }
   }

   @VisibleForTesting
   List permissionsInManifest() {
      try {
         PackageManager var1 = this.activity.getPackageManager();
         PackageInfo var2 = var1.getPackageInfo(this.activity.getPackageName(), 4096);
         String[] var3 = var2.requestedPermissions;
         return Arrays.asList(var3);
      } catch (NameNotFoundException var4) {
         twig.internal("Package name is unknown, error: " + var4.getMessage());
         return new ArrayList();
      }
   }
}
