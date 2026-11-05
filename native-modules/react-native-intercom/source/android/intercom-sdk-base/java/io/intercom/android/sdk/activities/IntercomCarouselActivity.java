package io.intercom.android.sdk.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.carousel.CarouselListener;
import io.intercom.android.sdk.carousel.CarouselPresenter;
import io.intercom.android.sdk.carousel.CarouselScreenFragment;
import io.intercom.android.sdk.carousel.CarouselScreenPagerAdapter;
import io.intercom.android.sdk.carousel.CarouselView;
import io.intercom.android.sdk.carousel.PermissionManager;
import io.intercom.android.sdk.carousel.permission.PermissionRequest;
import io.intercom.android.sdk.carousel.permission.PermissionRequestFactory;
import io.intercom.android.sdk.carousel.permission.PermissionResultListener;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.IntercomRootActivity;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.models.carousel.CarouselScreen;
import io.intercom.android.sdk.models.carousel.ScreenAction;
import io.intercom.android.sdk.utilities.BackgroundUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.FontUtils;
import io.intercom.android.sdk.utilities.LinkOpener;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.IntercomShimmerLayout;
import java.util.ArrayList;
import java.util.List;

public class IntercomCarouselActivity
   extends IntercomBaseActivity
   implements OnPageChangeListener,
   OnClickListener,
   CarouselListener,
   PermissionResultListener,
   CarouselView {
   private static final int PERMISSION_REQUEST_CODE = 12306;
   private static final String PARCEL_CAROUSEL_ID = "parcel_carousel_id";
   private static final String STATE_IS_LAST_SCREEN_VIEWED = "state_is_last_screen_viewed";
   private static final String STATE_HAS_RECORDED_COMPLETION_STATS = "state_has_recorded_completion_stats";
   private static final String STATE_NUMBER_OF_SCREENS_SEEN = "state_number_of_screens_seen";
   private static final String STATE_CURRENT_PERMISSION_ACTION = "state_current_permission_action";
   private static final String STATE_SENT_TO_SETTINGS = "state_sent_to_settings";
   private static final String STATE_CURRENT_SCREEN_ID = "state_current_screen_id";
   @VisibleForTesting
   ViewPager viewPager;
   private View closeBackground;
   private ImageButton closeButton;
   private ConstraintLayout closeButtonContainer;
   @VisibleForTesting
   LinearLayout navigationDotsLayout;
   private FrameLayout stateContainer;
   private ConstraintLayout rootView;
   private final Twig twig = LumberMill.getLogger();
   @VisibleForTesting
   CarouselScreenPagerAdapter adapter;
   @VisibleForTesting
   CarouselPresenter carouselPresenter;
   @VisibleForTesting
   PermissionManager permissionManager;
   @VisibleForTesting
   MetricTracker metricTracker;
   @VisibleForTesting
   Provider appConfigProvider;
   @VisibleForTesting
   PermissionRequest permissionRequest;
   @VisibleForTesting
   boolean isLastScreenViewed = false;
   @VisibleForTesting
   boolean hasRecordedCompletionStats = false;
   @VisibleForTesting
   int numberOfScreensSeen = 0;
   @VisibleForTesting
   ScreenAction currentPermissionAction = ScreenAction.NULL;
   @VisibleForTesting
   boolean sentToSettings = false;
   @VisibleForTesting
   String currentScreenId = "";

   public static Intent buildIntent(Context var0, String var1) {
      Intent var2 = new Intent(var0, IntercomCarouselActivity.class);
      var2.putExtra("parcel_carousel_id", var1);
      var2.setFlags(268435456);
      return var2;
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      EdgeToEdge.enable(this);
      super.onCreate(var1);
      this.getOnBackPressedDispatcher().addCallback(new IntercomCarouselActivity$1(this, true));
      this.setContentView(layout.intercom_activity_carousel);
      this.setInsets();
      this.viewPager = (ViewPager)this.findViewById(id.intercom_view_pager);
      this.closeBackground = this.findViewById(id.intercom_close_background);
      this.closeButton = (ImageButton)this.findViewById(id.intercom_close);
      this.closeButtonContainer = (ConstraintLayout)this.findViewById(id.intercom_carousel_close_container);
      this.navigationDotsLayout = (LinearLayout)this.findViewById(id.intercom_page_navigation_layout);
      this.stateContainer = (FrameLayout)this.findViewById(id.intercom_state_container);
      this.stateContainer.setVisibility(8);
      this.closeButton.setOnClickListener(this);
      this.closeButtonContainer.setOnClickListener(this);
      Injector var2 = Injector.get();
      this.appConfigProvider = var2.getAppConfigProvider();
      this.permissionManager = new PermissionManager(this);
      this.permissionRequest = PermissionRequestFactory.create(this.permissionManager);
      this.permissionRequest.attach(this);
      this.metricTracker = var2.getMetricTracker();
      this.carouselPresenter = new CarouselPresenter(var2.getApi(), var2.getUserIdentity(), this.metricTracker, this.permissionManager, var2.getDataLayer());
      if (var1 != null) {
         this.restoreInstanceState(var1);
      }

      this.carouselPresenter.attachView(this);
      this.carouselPresenter.fetchCarousel(this.getIntent().getStringExtra("parcel_carousel_id"));
   }

   private void setInsets() {
      this.rootView = (ConstraintLayout)this.findViewById(id.intercom_carousel_root);
      ViewCompat.setOnApplyWindowInsetsListener(this.rootView, (var0, var1) -> {
         Insets var2 = var1.getInsets(Type.systemBars() | Type.displayCutout());
         var0.setPadding(var2.left, var2.top, var2.right, var2.bottom);
         return WindowInsetsCompat.CONSUMED;
      });
   }

   @Override
   protected void onDestroy() {
      this.permissionRequest.detach();
      super.onDestroy();
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle var1) {
      super.onSaveInstanceState(var1);
      var1.putBoolean("state_is_last_screen_viewed", this.isLastScreenViewed);
      var1.putBoolean("state_has_recorded_completion_stats", this.hasRecordedCompletionStats);
      var1.putInt("state_number_of_screens_seen", this.numberOfScreensSeen);
      var1.putParcelable("state_current_permission_action", this.currentPermissionAction);
      var1.putBoolean("state_sent_to_settings", this.sentToSettings);
      var1.putString("state_current_screen_id", this.currentScreenId);
   }

   @VisibleForTesting
   void restoreInstanceState(@NonNull Bundle var1) {
      this.isLastScreenViewed = var1.getBoolean("state_is_last_screen_viewed", false);
      this.hasRecordedCompletionStats = var1.getBoolean("state_has_recorded_completion_stats", false);
      this.numberOfScreensSeen = var1.getInt("state_number_of_screens_seen", 0);
      ScreenAction var2 = (ScreenAction)var1.getParcelable("state_current_permission_action");
      if (var2 != null) {
         this.currentPermissionAction = var2;
      }

      this.sentToSettings = var1.getBoolean("state_sent_to_settings", false);
      String var3 = var1.getString("state_current_screen_id");
      if (var3 != null) {
         this.currentScreenId = var3;
      }
   }

   @Override
   public void onRequestPermissionsResult(int var1, @NonNull String[] var2, @NonNull int[] var3) {
      super.onRequestPermissionsResult(var1, var2, var3);
      if (var1 == 12306) {
         this.carouselPresenter.recordPermissionRequestedMetric(this.currentPermissionAction.getType(), var3, this.currentScreenId);
         this.permissionRequest.handleResult(var2, var3);
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (this.sentToSettings) {
         this.sentToSettings = false;
         this.carouselPresenter.recordSentToPermissionSettingsMetric(this.currentPermissionAction, this.currentScreenId);
      }
   }

   @Override
   public void onPageScrolled(int var1, float var2, int var3) {
   }

   @Override
   public void onPageSelected(int var1) {
      List var2 = this.carouselPresenter.getCarousel().getScreens();
      int var3 = var2.size();
      if (var3 > var1) {
         CarouselScreen var4 = (CarouselScreen)var2.get(var1);
         this.currentPermissionAction = var4.getPermissionAction();
         this.currentScreenId = var4.getId();
         boolean var5 = ColorUtils.isColorLight(var4.getBackgroundColor());
         this.renderNavigationDots(this.adapter.getCount(), var5, var1);
         this.updateCloseButtonColor(var5);
         if (var1 >= this.numberOfScreensSeen) {
            this.carouselPresenter.recordScreenSeenStats(var4.getId());
            this.numberOfScreensSeen = var1 + 1;
         }

         if (var1 + 1 == var3) {
            this.isLastScreenViewed = true;
            if (!this.hasRecordedCompletionStats) {
               this.carouselPresenter.recordCompletedStatsIfNeeded();
               this.hasRecordedCompletionStats = true;
            }
         }
      }
   }

   @Override
   public void onPageScrollStateChanged(int var1) {
   }

   @Override
   public void onClick(View var1) {
      this.dismissCarousel("from_close_button");
   }

   @Override
   public void dismissCarousel(String var1) {
      if (!Carousel.NULL.equals(this.carouselPresenter.getCarousel())) {
         this.carouselPresenter.recordDismissedOrCompletedStats(this.isLastScreenViewed, this.hasRecordedCompletionStats);
         this.carouselPresenter.recordDismissedOrCompletedMetric(var1, this.isLastScreenViewed, this.numberOfScreensSeen);
      }

      this.closeCarouselAndResetPersistedData();
   }

   @Override
   public void selectNextScreen(String var1) {
      int var2 = this.viewPager.getCurrentItem() + 1;
      if (var2 < this.adapter.getCount()) {
         this.viewPager.setCurrentItem(var2);
      } else {
         this.dismissCarousel(var1);
      }
   }

   @Override
   public void requestPermissions(ScreenAction var1) {
      this.permissionRequest.request(var1, 12306);
   }

   @Override
   public void openLink(String var1) {
      this.dismissCarousel("from_cta");
      LinkOpener.handleUrl(var1, this, Injector.get().getApi());
   }

   @Override
   public void startChat() {
      this.dismissCarousel("from_cta");
      if (((AppConfig)this.appConfigProvider.get()).isInboundMessages()) {
         this.startActivity(ConversationScreenOpenerKt.getComposerIntent(this, "", false, null, null, IntercomRootActivity.class));
      }
   }

   @Override
   public void skipPermissionScreen() {
      this.metricTracker
         .skippedPermission(
            this.currentPermissionAction.getType(), this.carouselPresenter.getCarousel().getInstanceId(), this.carouselPresenter.getCarouselSource()
         );
      this.selectNextScreen("from_permission_skipped");
   }

   @Override
   public void trackActionButtonTappedStats() {
      this.carouselPresenter.recordActionButtonTappedStats(this.currentScreenId);
   }

   @Override
   public void showGranted() {
      this.selectNextScreenWithDelay();
   }

   @Override
   public void showDeniedTemporarily() {
      this.selectNextScreen("from_permission");
   }

   @Override
   public void showDeniedPermanently() {
      this.showGoToSettingsDialog();
   }

   @RequiresApi(api = 30)
   @Override
   public void requestBackgroundLocationPermission() {
      this.showRationaleDialog(
         string.intercom_access_background_location_title, string.intercom_access_background_location_message, new IntercomCarouselActivity$2(this)
      );
   }

   @Override
   public void showSuccess(Carousel var1) {
      this.stateContainer.setVisibility(8);
      List var2 = var1.getScreens();
      if (var2.isEmpty()) {
         this.closeCarouselAndResetPersistedData();
      } else {
         this.carouselPresenter.recordOpenMetric();
      }

      this.closeButtonContainer.setVisibility(var1.isDismissible() ? 0 : 8);
      this.showOrHideNavigationDots(var2);
      this.setupViewPagerWithScreens(var2);
   }

   @Override
   public void showLoading() {
      this.showState(this.buildLoadingState());
   }

   @Override
   public void showNotFoundError() {
      this.showState(this.buildErrorMessage(string.intercom_page_not_found));
   }

   @Override
   public void showGenericError() {
      this.showState(this.buildErrorMessage(string.intercom_something_went_wrong_try_again));
      this.stateContainer.addView(this.buildRetryButton());
   }

   @Override
   public void logEmptyCarouselError() {
      this.twig.e("Could not open carousel without id");
   }

   @Override
   public void logUserNotRegisteredError() {
      this.twig
         .e(
            "The app config has not been retrieved, please call registerUnidentifiedUser() or registerIdentifiedUser(Registration) before calling displayCarousel(carouselId)."
         );
   }

   private void showState(View var1) {
      this.setCloseButtonColor(color.intercom_carousel_grey);
      this.stateContainer.removeAllViews();
      this.stateContainer.addView(var1);
      this.stateContainer.setVisibility(0);
   }

   private void setupViewPagerWithScreens(List var1) {
      List var2 = new ArrayList(var1.size());

      for (java.lang.Object recoveredEntryElement_0_392 : var1) {
         CarouselScreen var4 = (CarouselScreen) recoveredEntryElement_0_392;
         var2.add(CarouselScreenFragment.newInstance(var4));
      }

      this.adapter = new CarouselScreenPagerAdapter(this.getSupportFragmentManager(), var2);
      this.viewPager.setAdapter(this.adapter);
      this.viewPager.addOnPageChangeListener(this);
      this.viewPager.post(() -> this.onPageSelected(this.viewPager.getCurrentItem()));
   }

   @VisibleForTesting
   void showOrHideNavigationDots(List var1) {
      this.navigationDotsLayout.setVisibility(var1.size() > 1 ? 0 : 8);
   }

   @VisibleForTesting
   void renderNavigationDots(int var1, boolean var2, int var3) {
      this.navigationDotsLayout.removeAllViews();
      int var4 = var2 ? drawable.intercom_tab_dot_selector_dark : drawable.intercom_tab_dot_selector_light;

      for (int var5 = 0; var5 < var1; var5++) {
         View var6 = new View(this);
         int var7 = ScreenUtils.dpToPx(16.0F, this);
         var6.setLayoutParams(new LayoutParams(var7, var7));
         var6.setBackgroundResource(var4);
         this.navigationDotsLayout.addView(var6);
         if (var5 == var3) {
            var6.setSelected(true);
         }
      }
   }

   private View buildLoadingState() {
      ShimmerFrameLayout var1 = this.buildLoadingContainer();
      var1.addView(this.buildLoadingContent());
      return var1;
   }

   private ShimmerFrameLayout buildLoadingContainer() {
      ShimmerFrameLayout var1 = new IntercomShimmerLayout(this);
      LayoutParams var2 = new android.widget.FrameLayout.LayoutParams(-1, -1);
      var1.setLayoutParams(var2);
      var1.setClickable(true);
      var1.setFocusable(true);
      return var1;
   }

   private View buildLoadingContent() {
      ImageView var1 = new ImageView(this);
      var1.setLayoutParams(this.buildContentLayoutParams());
      var1.setAdjustViewBounds(true);
      var1.setScaleType(ScaleType.FIT_XY);
      var1.setBackgroundResource(drawable.intercom_content_loading);
      return var1;
   }

   private View buildErrorMessage(@StringRes int var1) {
      TextView var2 = new TextView(this);
      var2.setLayoutParams(this.buildContentLayoutParams());
      BlockUtils.setMarginBottom(var2, 16);
      this.styleErrorMessage(var2);
      var2.setText(var1);
      return var2;
   }

   private View buildRetryButton() {
      TextView var1 = (TextView)LayoutInflater.from(this).inflate(layout.intercom_carousel_action_button, this.stateContainer, false);
      FontUtils.setRobotoMediumTypeface(var1);
      var1.setText(string.intercom_retry);
      BackgroundUtils.setButtonColor(var1, ((AppConfig)this.appConfigProvider.get()).getPrimaryColor());
      var1.setLayoutParams(this.buildRetryButtonLayoutParams());
      var1.setOnClickListener(var1x -> this.carouselPresenter.retryFetch(this.getIntent().getStringExtra("parcel_carousel_id")));
      return var1;
   }

   private android.widget.FrameLayout.LayoutParams buildRetryButtonLayoutParams() {
      android.widget.FrameLayout.LayoutParams var1 = this.buildContentLayoutParams();
      var1.height = this.getResources().getDimensionPixelSize(dimen.intercom_office_hours_height);
      var1.setMargins(
         var1.leftMargin, var1.topMargin, var1.rightMargin, this.getResources().getDimensionPixelSize(dimen.intercom_carousel_action_button_bottom_padding)
      );
      var1.gravity = 80;
      return var1;
   }

   private android.widget.FrameLayout.LayoutParams buildContentLayoutParams() {
      android.widget.FrameLayout.LayoutParams var1 = new android.widget.FrameLayout.LayoutParams(-1, -2);
      int var2 = this.getResources().getDimensionPixelSize(dimen.intercom_carousel_padding);
      var1.setMarginStart(var2);
      var1.setMarginEnd(var2);
      return var1;
   }

   @SuppressLint("WrongConstant")
   private void styleErrorMessage(TextView var1) {
      var1.setBreakStrategy(0);
      var1.setTextColor(ContextCompat.getColor(this, color.intercom_black));
      var1.setTextSize(34.0F);
      var1.setTypeface(null, 1);
      var1.setLineSpacing(ScreenUtils.dpToPx(6.0F, var1.getContext()), 1.0F);
      var1.setGravity(1);
   }

   private void updateCloseButtonColor(boolean var1) {
      int var2 = var1 ? color.intercom_carousel_grey : color.intercom_white;
      this.setCloseButtonColor(var2);
   }

   private void setCloseButtonColor(@ColorRes int var1) {
      int var2 = ContextCompat.getColor(this, var1);
      Drawable var3 = this.closeBackground.getBackground();
      var3.setTint(var2);
      Drawable var4 = this.closeButton.getDrawable();
      var4.setTint(var2);
   }

   @VisibleForTesting
   void goToSettings() {
      this.sentToSettings = true;
      Uri var1 = Uri.fromParts("package", this.getPackageName(), null);
      this.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var1));
   }

   @VisibleForTesting
   void showGoToSettingsDialog() {
      this.showRationaleDialog(string.intercom_permission_denied, string.intercom_go_to_device_settings, new IntercomCarouselActivity$3(this));
   }

   private void showRationaleDialog(@StringRes int var1, @StringRes int var2, @NonNull android.content.DialogInterface.OnClickListener var3) {
      AlertDialog var4 = new Builder(this)
         .setTitle(var1)
         .setMessage(var2)
         .setPositiveButton(string.intercom_app_settings, var3)
         .setNegativeButton(string.intercom_not_now, new IntercomCarouselActivity$4(this))
         .setCancelable(false)
         .show();
      int var5 = ColorUtils.primaryOrDarkColor(this, (AppConfig)this.appConfigProvider.get());
      var4.getButton(-2).setTextColor(var5);
      var4.getButton(-1).setTextColor(var5);
   }

   @VisibleForTesting
   void selectNextScreenWithDelay() {
      new Handler().postDelayed(new IntercomCarouselActivity$5(this), 1000L);
   }

   @VisibleForTesting
   void closeCarouselAndResetPersistedData() {
      this.carouselPresenter.resetPersistedCarousel();
      this.finish();
   }
}
