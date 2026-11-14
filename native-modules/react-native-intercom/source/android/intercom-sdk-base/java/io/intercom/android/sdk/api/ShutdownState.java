package io.intercom.android.sdk.api;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.utilities.commons.DeviceUtils;
import io.intercom.android.sdk.utilities.commons.TimeProvider;

public class ShutdownState {
   private final ShutdownStore shutdownStore;
   private final Context context;
   private final AppIdentity appIdentity;
   private final TimeProvider timeProvider;
   @VisibleForTesting
   long shutdownExpiry;
   private String shutdownReason;
   @VisibleForTesting
   String shutdownFingerprint;

   public ShutdownState(ShutdownStore var1, Context var2, AppIdentity var3, TimeProvider var4) {
      this.shutdownStore = var1;
      this.context = var2;
      this.appIdentity = var3;
      this.timeProvider = var4;
      this.shutdownExpiry = var1.getShutdownExpiry(var4.currentTimeMillis());
      this.shutdownReason = var1.getShutdownReason();
      this.shutdownFingerprint = var1.getShutdownFingerprint(generateAppFingerprint(var2, var3));
   }

   public boolean canSendNetworkRequests() {
      boolean var1 = this.shutdownExpiry <= this.timeProvider.currentTimeMillis();
      boolean var2 = this.shutdownFingerprint.equals(generateAppFingerprint(this.context, this.appIdentity));
      return var1 || !var2;
   }

   public void updateShutdownState(long var1, String var3) {
      this.shutdownExpiry = this.timeProvider.currentTimeMillis() + var1;
      this.shutdownReason = var3;
      this.shutdownFingerprint = generateAppFingerprint(this.context, this.appIdentity);
      this.persistCachedAttributes();
   }

   public String getShutdownReason() {
      return this.shutdownReason;
   }

   private void persistCachedAttributes() {
      this.shutdownStore.save(this.shutdownFingerprint, this.shutdownExpiry, this.shutdownReason);
   }

   private static String generateAppFingerprint(Context var0, AppIdentity var1) {
      return var0.getPackageName() + "-" + DeviceUtils.getAppVersion(var0) + "-" + var1.appId() + "-" + "18.10.0";
   }
}
