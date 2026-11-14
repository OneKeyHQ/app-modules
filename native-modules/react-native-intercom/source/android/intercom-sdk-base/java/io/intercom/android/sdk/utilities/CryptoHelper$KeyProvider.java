package io.intercom.android.sdk.utilities;

import javax.crypto.SecretKey;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;",
         "",
         "getOrCreateKey",
         "Ljavax/crypto/SecretKey;",
         "deleteKey",
         "",
         "intercom-sdk-base_release"
   }
)
public interface CryptoHelper$KeyProvider {
   @NotNull
   SecretKey getOrCreateKey() throws Exception;

   void deleteKey() throws Exception;
}
