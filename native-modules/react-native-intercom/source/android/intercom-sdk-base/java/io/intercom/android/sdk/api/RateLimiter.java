package io.intercom.android.sdk.api;

import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.commons.TimeProvider;

class RateLimiter {
   private final AppConfig appConfig;
   private final TimeProvider timeProvider;
   private long periodStartTimestamp;
   private int limitedRequestCount;
   private final Twig twig = LumberMill.getLogger();

   RateLimiter(AppConfig var1) {
      this(var1, TimeProvider.SYSTEM);
   }

   @VisibleForTesting
   RateLimiter(AppConfig var1, TimeProvider var2) {
      this.appConfig = var1;
      this.timeProvider = var2;
   }

   void recordRequest() {
      if (!this.isInsideCurrentTimePeriod()) {
         this.periodStartTimestamp = this.timeProvider.currentTimeMillis();
         this.limitedRequestCount = 0;
      }

      this.limitedRequestCount++;
   }

   void logError() {
      this.twig.e("Your app is being rate limited because you're performing too many requests per minute");
   }

   boolean isLimited() {
      return this.isInsideCurrentTimePeriod() && this.hasReachedMaxCount();
   }

   private boolean isInsideCurrentTimePeriod() {
      return this.timeProvider.currentTimeMillis() - this.periodStartTimestamp < this.appConfig.getRateLimitPeriodMs();
   }

   private boolean hasReachedMaxCount() {
      return this.limitedRequestCount >= this.appConfig.getRateLimitCount();
   }
}
