package io.intercom.android.sdk.utilities;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyGenParameterSpec.Builder;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.SecretKeyEntry;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\u0005H\u0002¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/utilities/CryptoHelper$AndroidKeystoreKeyProvider;",
         "Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;",
         "<init>",
         "()V",
         "getOrCreateKey",
         "Ljavax/crypto/SecretKey;",
         "deleteKey",
         "",
         "generateKey",
         "intercom-sdk-base_release"
   }
)
final class CryptoHelper$AndroidKeystoreKeyProvider implements CryptoHelper$KeyProvider {
   @NotNull
   public static final CryptoHelper$AndroidKeystoreKeyProvider INSTANCE = new CryptoHelper$AndroidKeystoreKeyProvider();

   private CryptoHelper$AndroidKeystoreKeyProvider() {
   }

   @NotNull
   @Override
   public SecretKey getOrCreateKey() throws Exception {
      KeyStore var1 = KeyStore.getInstance("AndroidKeyStore");
      var1.load(null);
      Entry var3 = var1.getEntry("intercom_sdk_prefs_key", null);
      SecretKeyEntry var2 = var3 instanceof SecretKeyEntry ? (SecretKeyEntry)var3 : null;
      if (var2 != null) {
         SecretKey var10000 = var2.getSecretKey();
         Intrinsics.checkNotNullExpressionValue(var10000, "getSecretKey(...)");
         return var10000;
      } else {
         return this.generateKey();
      }
   }

   @Override
   public void deleteKey() throws Exception {
      KeyStore var1 = KeyStore.getInstance("AndroidKeyStore");
      var1.load(null);
      var1.deleteEntry("intercom_sdk_prefs_key");
   }

   private final SecretKey generateKey() throws Exception {
      KeyGenerator var1 = KeyGenerator.getInstance("AES", "AndroidKeyStore");
      Builder var10000 = new Builder("intercom_sdk_prefs_key", 3);
      String[] var3 = new String[]{"GCM"};
      var10000 = var10000.setBlockModes(var3);
      var3 = new String[]{"NoPadding"};
      KeyGenParameterSpec var6 = var10000.setEncryptionPaddings(var3).setKeySize(256).build();
      Intrinsics.checkNotNullExpressionValue(var6, "build(...)");
      KeyGenParameterSpec var2 = var6;
      var1.init(var2);
      SecretKey var7 = var1.generateKey();
      Intrinsics.checkNotNullExpressionValue(var7, "generateKey(...)");
      return var7;
   }
}
