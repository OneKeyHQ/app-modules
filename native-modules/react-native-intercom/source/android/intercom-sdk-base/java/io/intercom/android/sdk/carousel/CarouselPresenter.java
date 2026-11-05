package io.intercom.android.sdk.carousel;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.OverlayState;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.models.carousel.CarouselScreen;
import io.intercom.android.sdk.models.carousel.ScreenAction;
import java.util.Iterator;
import java.util.List;

public final class CarouselPresenter {
   private final IntercomDataLayer dataLayer;
   private final Api api;
   private UserIdentity userIdentity;
   private final MetricTracker metricTracker;
   private final PermissionManager permissionManager;
   private Carousel carousel = Carousel.NULL;
   private int initialNumberOfScreens;
   private CarouselView view = null;
   private String carouselSource = "programmatic";
   @Nullable
   private String carouselId = null;

   public CarouselPresenter(Api var1, UserIdentity var2, MetricTracker var3, PermissionManager var4, IntercomDataLayer var5) {
      this.api = var1;
      this.userIdentity = var2;
      this.metricTracker = var3;
      this.permissionManager = var4;
      this.dataLayer = var5;
      this.filterCarouselScreens(this.carousel);
   }

   public void attachView(CarouselView var1) {
      this.view = var1;
   }

   public void detachView() {
      this.view = null;
   }

   public String getCarouselSource() {
      return this.carouselSource;
   }

   public void fetchCarousel(@Nullable String var1) {
      this.carouselId = var1;
      this.setSource(var1);
      if ("programmatic".equals(this.carouselSource)) {
         if (TextUtils.isEmpty(var1)) {
            this.view.logEmptyCarouselError();
            this.view.showNotFoundError();
         } else if (this.noUserRegistered()) {
            this.view.logUserNotRegisteredError();
            this.view.showNotFoundError();
         } else {
            this.fetchProgrammaticCarousel(var1);
         }
      } else {
         this.fetchAutomaticCarousel();
      }
   }

   private boolean noUserRegistered() {
      return !this.userIdentity.identityExists() || this.userIdentity.isSoftReset();
   }

   public void retryFetch(@Nullable String var1) {
      if (TextUtils.isEmpty(var1)) {
         if (this.view != null) {
            this.view.showNotFoundError();
         }
      } else {
         this.metricTracker.retriedCarouselFetching(var1, this.carouselSource);
         this.fetchProgrammaticCarousel(var1);
      }
   }

   public Carousel getCarousel() {
      return this.carousel;
   }

   public void resetPersistedCarousel() {
      this.dataLayer.clearCarousel();
   }

   public void recordCompletedStatsIfNeeded() {
      String var1 = this.getCarousel().getInstanceId();
      if (!TextUtils.isEmpty(var1)) {
         this.api.markCarouselAsCompleted(var1, this.isTriggeredFromCode());
      }
   }

   public void recordDismissedOrCompletedStats(boolean var1, boolean var2) {
      String var3 = this.getCarousel().getInstanceId();
      if (!TextUtils.isEmpty(var3)) {
         if (var1) {
            if (!var2) {
               this.api.markCarouselAsCompleted(var3, this.isTriggeredFromCode());
            }
         } else {
            this.api.markCarouselAsDismissed(var3, this.isTriggeredFromCode());
         }
      }
   }

   public void recordScreenSeenStats(String var1) {
      String var2 = this.getCarousel().getInstanceId();
      this.api.markCarouselScreenViewed(var2, var1, this.isTriggeredFromCode());
   }

   public void recordActionButtonTappedStats(@NonNull String var1) {
      String var2 = this.getCarousel().getInstanceId();
      this.api.markCarouselActionButtonTapped(var2, var1, this.isTriggeredFromCode());
   }

   public void recordOpenMetric() {
      int var1 = this.getCarousel().getScreens().size();
      this.metricTracker.openedCarousel(this.getCarousel().getInstanceId(), this.carouselSource, this.initialNumberOfScreens, var1);
   }

   public void recordDismissedOrCompletedMetric(String var1, boolean var2, int var3) {
      String var4 = this.getCarousel().getInstanceId();
      if (TextUtils.isEmpty(var4)) {
         if (!TextUtils.isEmpty(this.carouselId)) {
            this.metricTracker.dismissedCarouselFetching(var1, this.carouselId, this.carouselSource);
         }
      } else {
         int var5 = this.getCarousel().getScreens().size();
         if (var2) {
            this.metricTracker.completedCarousel(var1, var4, this.carouselSource, this.initialNumberOfScreens, var5);
         } else {
            this.metricTracker.dismissedCarousel(var1, var4, this.carouselSource, this.initialNumberOfScreens, var5, var3);
         }
      }
   }

   public void recordPermissionRequestedMetric(String var1, int[] var2, String var3) {
      boolean var4 = false;
      String var5 = this.getCarousel().getInstanceId();

      for (int var9 : var2) {
         if (var9 == 0) {
            var4 = true;
            this.api.markPermissionGranted(var5, var3, this.isTriggeredFromCode());
            break;
         }
      }

      this.metricTracker.requestedPermission(var1, var5, this.carouselSource, var4);
   }

   public void recordSentToPermissionSettingsMetric(ScreenAction var1, String var2) {
      String var3 = var1.getType();
      List var4 = var1.getValidPermissions(this.permissionManager);
      boolean var5 = this.permissionManager.permissionsGranted(var4);
      String var6 = this.getCarousel().getInstanceId();
      if (var5) {
         this.api.markPermissionGranted(var6, var2, this.isTriggeredFromCode());
      }

      this.metricTracker.sentToPermissionSettings(var3, var6, this.carouselSource, var5);
   }

   @VisibleForTesting
   void filterCarouselScreens(Carousel var1) {
      Iterator var2 = var1.getScreens().iterator();

      while (var2.hasNext()) {
         CarouselScreen var3 = (CarouselScreen)var2.next();
         if (this.screenShouldBeRemoved(var3)) {
            var2.remove();
         }
      }
   }

   @VisibleForTesting
   boolean screenShouldBeRemoved(CarouselScreen var1) {
      ScreenAction var2 = var1.getPermissionAction();
      if (ScreenAction.NULL.equals(var2)) {
         return false;
      }

      List var3 = var2.getValidPermissions(this.permissionManager);
      return var3.isEmpty() || this.permissionManager.permissionsGranted(var3);
   }

   private void fetchAutomaticCarousel() {
      this.carousel = ((OverlayState)this.dataLayer.getOverlayState().getValue()).getCarousel();
      this.initialNumberOfScreens = this.carousel.getScreens().size();
      this.filterCarouselScreens(this.carousel);
      if (this.view != null) {
         this.view.showSuccess(this.carousel);
      }
   }

   private void fetchProgrammaticCarousel(@NonNull String var1) {
      this.metricTracker.startedCarouselFetching(var1, this.carouselSource);
      if (this.view != null) {
         this.view.showLoading();
      }

      this.api.fetchProgrammaticCarousel(var1, new CarouselPresenter$1(this, var1));
   }

   private boolean isTriggeredFromCode() {
      return this.carouselSource.equals("programmatic");
   }

   private void setSource(@Nullable String var1) {
      this.carouselSource = var1 == null ? "automatic" : "programmatic";
   }

   // $VF: synthetic method
   static Carousel access$002(CarouselPresenter var0, Carousel var1) {
      return var0.carousel = var1;
   }

   // $VF: synthetic method
   static CarouselView access$100(CarouselPresenter var0) {
      return var0.view;
   }

   // $VF: synthetic method
   static Carousel access$000(CarouselPresenter var0) {
      return var0.carousel;
   }

   // $VF: synthetic method
   static String access$200(CarouselPresenter var0) {
      return var0.carouselSource;
   }

   // $VF: synthetic method
   static MetricTracker access$300(CarouselPresenter var0) {
      return var0.metricTracker;
   }
}
