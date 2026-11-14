package io.intercom.android.sdk.utilities;

import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import javax.crypto.SecretKey;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\t\u001a\u00020\u0006H\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/utilities/CryptoHelper$CachingKeyProvider;",
         "Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;",
         "delegate",
         "<init>",
         "(Lio/intercom/android/sdk/utilities/CryptoHelper$KeyProvider;)V",
         "cachedKey",
         "Ljavax/crypto/SecretKey;",
         "lock",
         "",
         "getOrCreateKey",
         "deleteKey",
         "",
         "intercom-sdk-base_release"
   }
)
@VisibleForTesting
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nCryptoHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CryptoHelper.kt\nio/intercom/android/sdk/utilities/CryptoHelper$CachingKeyProvider\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,175:1\n1#2:176\n*E\n"
)
public final class CryptoHelper$CachingKeyProvider implements CryptoHelper$KeyProvider {
   @NotNull
   private final CryptoHelper$KeyProvider delegate;
   @Nullable
   private volatile SecretKey cachedKey;
   @NotNull
   private final Object lock;
   public static final int $stable;

   static { $stable = 8; }

   public CryptoHelper$CachingKeyProvider(@NotNull CryptoHelper$KeyProvider var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "delegate");
      this.delegate = var1;
      this.lock = new Object();
   }

   @NotNull
   @Override
   public SecretKey getOrCreateKey() throws Exception {
      if (this.cachedKey != null) {
         SecretKey var11 = this.cachedKey;
         int var13 = 0;
         return var11;
      }

      synchronized (this.lock) {
         int var2 = 0;
         if (this.cachedKey != null) {
            SecretKey var12 = this.cachedKey;
            int var4 = 0;
            return var12;
         } else {
            SecretKey var5 = this.delegate.getOrCreateKey();
            SecretKey var6 = var5;
            int var3 = 0;
            this.cachedKey = var6;
            return var5;
         }
      }
   }

   // $VF: Extended synchronized range to monitorexit
   @Override
   public void deleteKey() throws Exception {
      synchronized (this.lock) {
         int var2 = 0;
         this.cachedKey = null;
         this.delegate.deleteKey();
         Unit var5 = Unit.INSTANCE;
      }
   }
}
