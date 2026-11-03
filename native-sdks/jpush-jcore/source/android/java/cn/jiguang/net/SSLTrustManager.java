package cn.jiguang.net;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLTrustManager implements X509TrustManager {
   private X509TrustManager recoverField_a;

   public SSLTrustManager(String var1) throws CertificateException , java.io.IOException , java.security.KeyStoreException , java.security.NoSuchAlgorithmException {
      try {
         CertificateFactory var2 = CertificateFactory.getInstance("X.509");
         ByteArrayInputStream var3 = new ByteArrayInputStream(var1.getBytes());
         X509Certificate var4 = (X509Certificate)var2.generateCertificate(var3);
         var3.close();
         TrustedCertificateEntry var5 = new TrustedCertificateEntry(var4);
         KeyStore var6 = KeyStore.getInstance(KeyStore.getDefaultType());
         var6.load(null, null);
         var6.setEntry("ca_root", var5, null);
         TrustManagerFactory var7 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         var7.init(var6);
         TrustManager[] var8 = var7.getTrustManagers();

         for (TrustManager var12 : var8) {
            if (var12 instanceof X509TrustManager) {
               this.recoverField_a = (X509TrustManager)var12;
               return;
            }
         }

         throw new NoSuchAlgorithmException("No X509TrustManager found");
      } catch (Throwable var13) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SSLTrustManager", "init trustManager failed, error:" + var13);
         throw var13;
      }
   }

   @Override
   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SSLTrustManager", "checkClientTrusted");
      this.recoverField_a.checkClientTrusted(var1, var2);
   }

   @Override
   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException, CertificateExpiredException, CertificateNotYetValidException {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SSLTrustManager", "checkServerTrusted");
      this.recoverField_a.checkServerTrusted(var1, var2);
      if (var1 != null && var1.length != 0 && var1[0] != null) {
         try {
            var1[0].checkValidity();
         } catch (CertificateExpiredException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SSLTrustManager", "checkServerTrusted: CertificateExpiredException:" + var4.getLocalizedMessage());
            throw var4;
         } catch (CertificateNotYetValidException var5) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SSLTrustManager", "checkServerTrusted: CertificateNotYetValidException:" + var5.getLocalizedMessage());
            throw var5;
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SSLTrustManager", "checkServerTrusted failed, error" + var6.getLocalizedMessage());
            throw new CertificateException("Check Server x509Certificates exception: " + var6);
         }
      } else {
         throw new CertificateException("Check Server x509Certificates is empty");
      }
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SSLTrustManager", "getAcceptedIssuers");
      return this.recoverField_a.getAcceptedIssuers();
   }
}
