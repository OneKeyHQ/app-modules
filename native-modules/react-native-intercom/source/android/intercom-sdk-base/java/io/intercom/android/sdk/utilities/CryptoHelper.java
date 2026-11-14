package io.intercom.android.sdk.utilities;

import android.util.Base64;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÁ\u0002\u0018\u00002\u00020\u0001:\u0003\u001f !B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0007H\u0000¢\u0006\u0002\b\u0016J\u0015\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u0007H\u0000¢\u0006\u0002\b\u0019J\u0014\u0010\u001a\u001a\u00020\u001b2\n\u0010\u001c\u001a\u00060\u001dj\u0002`\u001eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R$\u0010\r\u001a\u00020\u000e8\u0000@\u0000X\u0081\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\u000f\u0010\u0003\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/utilities/CryptoHelper;",
         "",
         "<init>",
         "()V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "KEY_ALIAS",
         "",
         "ANDROID_KEYSTORE",
         "TRANSFORMATION",
         "GCM_IV_LENGTH",
         "",
         "GCM_TAG_LENGTH",
         "keyProvider",
         "Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;",
         "getKeyProvider$intercom_sdk_base_release$annotations",
         "getKeyProvider$intercom_sdk_base_release",
         "()Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;",
         "setKeyProvider$intercom_sdk_base_release",
         "(Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;)V",
         "encrypt",
         "plaintext",
         "encrypt$intercom_sdk_base_release",
         "decrypt",
         "encrypted",
         "decrypt$intercom_sdk_base_release",
         "handleKeyStoreError",
         "",
         "e",
         "Ljava/lang/Exception;",
         "Lkotlin/Exception;",
         "KeyProvider",
         "CachingKeyProvider",
         "AndroidKeystoreKeyProvider",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class CryptoHelper {
   @NotNull
   public static final CryptoHelper INSTANCE = new CryptoHelper();
   @NotNull
   private static final Twig twig;
   @NotNull
   private static final String KEY_ALIAS = "intercom_sdk_prefs_key";
   @NotNull
   private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
   @NotNull
   private static final String TRANSFORMATION = "AES/GCM/NoPadding";
   private static final int GCM_IV_LENGTH = 12;
   private static final int GCM_TAG_LENGTH = 128;
   @NotNull
   private static CryptoHelper$KeyProvider keyProvider = new CryptoHelper$CachingKeyProvider(CryptoHelper$AndroidKeystoreKeyProvider.INSTANCE);
   public static final int $stable;

   static { $stable = 8; }

   private CryptoHelper() {
   }

   @NotNull
   public final CryptoHelper$KeyProvider getKeyProvider$intercom_sdk_base_release() {
      return keyProvider;
   }

   public final void setKeyProvider$intercom_sdk_base_release(@NotNull CryptoHelper$KeyProvider var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      keyProvider = var1;
   }

   /** @deprecated */
   // $VF: synthetic method
   @VisibleForTesting
   public static void getKeyProvider$intercom_sdk_base_release$annotations() {
   }

   @NotNull
   public final String encrypt$intercom_sdk_base_release(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "plaintext");
      if (var1.length() == 0) {
         return "";
      }

      String var2;
      try {
         SecretKey var11 = keyProvider.getOrCreateKey();
         Cipher var3 = Cipher.getInstance("AES/GCM/NoPadding");
         var3.init(1, var11);
         byte[] var4 = var3.getIV();
         byte[] var10001 = var1.getBytes(Charsets.UTF_8);
         Intrinsics.checkNotNullExpressionValue(var10001, "getBytes(...)");
         byte[] var5 = var3.doFinal(var10001);
         Intrinsics.checkNotNull(var4);
         Intrinsics.checkNotNull(var5);
         byte[] var6 = ArraysKt.plus(var4, var5);
         var2 = Base64.encodeToString(var6, 2);
      } catch (KeyStoreException var7) {
         this.handleKeyStoreError(var7);
         var2 = "";
      } catch (UnrecoverableKeyException var8) {
         this.handleKeyStoreError(var8);
         var2 = "";
      } catch (InvalidKeyException var9) {
         this.handleKeyStoreError(var9);
         var2 = "";
      } catch (Exception var10) {
         twig.w(var10, "encryption failed");
         var2 = "";
      }

      return var2;
   }

   @NotNull
   public final String decrypt$intercom_sdk_base_release(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "encrypted");
      if (var1.length() == 0) {
         return "";
      }

      String var2;
      try {
         SecretKey var13 = keyProvider.getOrCreateKey();
         byte[] var3 = Base64.decode(var1, 2);
         if (var3.length <= 12) {
            return "";
         }

         Intrinsics.checkNotNull(var3);
         byte[] var4 = ArraysKt.copyOfRange((byte[])var3, 0, 12);
         byte[] var5 = ArraysKt.copyOfRange((byte[])var3, 12, var3.length);
         Cipher var6 = Cipher.getInstance("AES/GCM/NoPadding");
         var6.init(2, var13, new GCMParameterSpec(128, var4));
         byte[] var10000 = var6.doFinal(var5);
         Intrinsics.checkNotNullExpressionValue(var10000, "doFinal(...)");
         byte[] var7 = var10000;
         Charset var8 = Charsets.UTF_8;
         var2 = new String(var7, var8);
      } catch (KeyStoreException var9) {
         this.handleKeyStoreError(var9);
         var2 = "";
      } catch (UnrecoverableKeyException var10) {
         this.handleKeyStoreError(var10);
         var2 = "";
      } catch (InvalidKeyException var11) {
         this.handleKeyStoreError(var11);
         var2 = "";
      } catch (Exception var12) {
         twig.w(var12, "decryption failed");
         var2 = "";
      }

      return var2;
   }

   private final void handleKeyStoreError(Exception var1) {
      twig.w(var1, "Keystore error, regenerating key. Data encrypted with old key will be lost.");

      try {
         keyProvider.deleteKey();
         keyProvider.getOrCreateKey();
      } catch (Exception var3) {
         twig.w(var3, "failed to regenerate key");
      }
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}
