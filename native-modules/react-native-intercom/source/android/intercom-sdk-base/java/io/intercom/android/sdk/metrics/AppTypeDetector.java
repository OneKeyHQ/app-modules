package io.intercom.android.sdk.metrics;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;

class AppTypeDetector {
   private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");
   private static final Twig twig = LumberMill.getLogger();

   static boolean isDebugBuild(Context var0) {
      try {
         PackageManager var1 = var0.getPackageManager();
         PackageInfo var2 = var1.getPackageInfo(var0.getPackageName(), 64);
         Signature[] var3 = var2.signatures;
         if (var3 != null && var3.length > 0) {
            CertificateFactory var4 = CertificateFactory.getInstance("X.509");

            for (Signature var8 : var3) {
               byte[] var9 = var8.toByteArray();
               ByteArrayInputStream var10 = new ByteArrayInputStream(var9);
               X509Certificate var11 = (X509Certificate)var4.generateCertificate(var10);
               if (var11.getSubjectX500Principal().equals(DEBUG_DN)) {
                  return true;
               }
            }
         }
      } catch (Exception var12) {
         twig.internal("Debug build status is unknown, error: " + var12.getMessage());
      }

      return false;
   }

   static String getInstallerPackageName(Context var0) {
      String var1 = null;

      try {
         var1 = var0.getPackageManager().getInstallerPackageName(var0.getPackageName());
      } catch (Exception var3) {
         twig.internal("Package name is unknown, error: " + var3.getMessage());
      }

      return TextUtils.isEmpty(var1) ? "unknown" : var1;
   }
}
