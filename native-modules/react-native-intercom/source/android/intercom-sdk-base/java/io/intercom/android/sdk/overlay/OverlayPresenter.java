package io.intercom.android.sdk.overlay;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.graphics.Insets;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Intercom$Visibility;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.activities.IntercomCarouselActivity;
import io.intercom.android.sdk.activities.IntercomNoteActivity;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.IntercomRootActivity;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityPaused;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityReadyForViewAttachment;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityStopped;
import io.intercom.android.sdk.m5.data.IntercomEvent$AppEnteredBackground;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$MessagesScreenArgs;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgsKt;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.metrics.MetricTracker$InAppMessageViewContext;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.DeliveryOption;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.OverlayState;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.post.IntercomPostActivity;
import io.intercom.android.sdk.survey.model.SurveyData;
import io.intercom.android.sdk.survey.ui.IntercomSurveyActivity;
import io.intercom.android.sdk.utilities.AvatarUtils;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import io.intercom.android.sdk.utilities.ViewUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kotlin.Unit;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;

public class OverlayPresenter implements InAppNotificationPresenter$Listener {
   final DefaultLauncherPresenter launcherPresenter;
   final InAppNotificationPresenter inAppNotificationPresenter;
   private final MetricTracker metricTracker;
   private final Provider appConfigProvider;
   private final Handler handler;
   private final UserIdentity userIdentity;
   private final Twig twig = LumberMill.getLogger();
   private final Set viewedPartIds = new HashSet();
   private final IntercomDataLayer intercomDataLayer;
   private final Provider api;
   @Nullable
   @VisibleForTesting
   Activity previousPausedActivity = null;
   @Nullable
   @VisibleForTesting
   Activity previousResumedActivity = null;

   public OverlayPresenter(Application var1, Provider var2, MetricTracker var3, UserIdentity var4, IntercomDataLayer var5, Provider var6) {
      this.appConfigProvider = var2;
      this.metricTracker = var3;
      this.userIdentity = var4;
      this.intercomDataLayer = var5;
      this.api = var6;
      LayoutInflater var7 = LayoutInflater.from(var1);
      this.handler = new Handler(Looper.getMainLooper());
      this.inAppNotificationPresenter = new InAppNotificationPresenter(var7, this.handler, this, var3, var2, new ContextLocaliser(var2), this.hostActivity());
      this.launcherPresenter = new DefaultLauncherPresenter(var7, new LauncherOpenBehaviour(), var3);
      CoroutineScope var8 = CoroutineScopeKt.MainScope();
      var5.unreadConversationIdsUpdates(var8, (kotlin.jvm.functions.Function1<java.util.Set, kotlin.Unit>) (java.util.Set var1x) -> {
         OverlayState callbackOverlayState = (OverlayState)this.intercomDataLayer.getOverlayState().getValue();
         this.onStateChange(var1x.size(), callbackOverlayState);
         return Unit.INSTANCE;
      });
      var5.configUpdates(var8, (kotlin.jvm.functions.Function1<io.intercom.android.sdk.identity.AppConfig, kotlin.Unit>) (io.intercom.android.sdk.identity.AppConfig var1x) -> {
         this.onNewConfig();
         return Unit.INSTANCE;
      });
      var5.overlayStateUpdates(var8, (kotlin.jvm.functions.Function1<OverlayState, kotlin.Unit>) (OverlayState var1x) -> {
         Integer callbackUnreadCount = ((Set)this.intercomDataLayer.getUnreadConversationIds().getValue()).size();
         this.onStateChange(callbackUnreadCount, var1x);
         return Unit.INSTANCE;
      });
      var5.listenToEvents(var8, (kotlin.jvm.functions.Function1<IntercomEvent, kotlin.Unit>) (IntercomEvent var1x) -> {
         this.handleLifecycleEvents(var1x);
         return Unit.INSTANCE;
      });
   }

   @VisibleForTesting
   void handleLifecycleEvents(IntercomEvent var1) {
      Activity var2 = this.previousPausedActivity;
      Activity var3 = this.previousResumedActivity;
      if (var1 instanceof IntercomEvent$ActivityReadyForViewAttachment) {
         Activity var4 = ((IntercomEvent$ActivityReadyForViewAttachment)var1).getActivity();
         if (var4 != var2) {
            this.removeOverlays(var2);
         }

         this.previousResumedActivity = var4;
         this.previousPausedActivity = null;
      } else if (var1 instanceof IntercomEvent$ActivityPaused) {
         this.previousPausedActivity = ((IntercomEvent$ActivityPaused)var1).getActivity();
         this.previousResumedActivity = null;
      } else if (var1 instanceof IntercomEvent$ActivityStopped) {
         Activity var5 = ((IntercomEvent$ActivityStopped)var1).getActivity();
         if (var5 == var2) {
            this.removeOverlays(var2);
            this.previousPausedActivity = null;
         }
      } else if (var1 instanceof IntercomEvent$AppEnteredBackground) {
         this.cancelAnimations();
         this.removeOverlays(var2);
         this.removeOverlays(var3);
         this.previousPausedActivity = null;
         this.previousResumedActivity = null;
      }
   }

   public void onStateChange(Integer var1, OverlayState var2) {
      Activity var3 = var2.getResumedHostActivity();
      List var4 = var2.getConversations();
      SurveyData var5 = var2.getSurveyData();
      Carousel var6 = var2.getCarousel();
      int var7 = var2.getBottomPadding();
      Intercom$Visibility var8 = var2.getLauncherVisibility();
      Intercom$Visibility var9 = var2.getInAppNotificationsVisibility();
      this.launcherPresenter.setBottomPadding(var7);
      this.launcherPresenter.setUnreadCount(var1);
      this.inAppNotificationPresenter.setBottomPadding(var7);
      if (var3 != null && !var3.isFinishing() && !var3.isDestroyed()) {
         if (this.shouldDisplaySurvey(var5, var3)) {
            this.openSurvey(var3);
         } else if (this.shouldDisplayCarousel(var6, var3)) {
            this.openCarousel(var3);
         } else if (this.shouldDisplayNotifications(var4, var9, var3)) {
            this.preloadAvatarThenDisplayNotifications(var4, var9, var3, this.userIdentity.getFingerprint());
         } else if (this.shouldDisplayLauncher(var4, var9, var8, var3)) {
            ViewGroup var10 = this.getRootView(var3);
            ViewUtils.waitForViewAttachment(var10, () -> {
               if (!var3.isFinishing() && !var3.isDestroyed()) {
                  this.inAppNotificationPresenter.reset(var10);
                  this.launcherPresenter.displayLauncherOnAttachedRoot(var10, (AppConfig)this.appConfigProvider.get());
               }
            });
         } else {
            this.removeOverlaysIfPresent(var3);
         }
      }
   }

   public void softReset() {
      this.cancelAnimations();
      Activity var1 = this.hostActivity();
      if (var1 != null) {
         this.removeOverlaysIfPresent(var1);
      }

      this.previousPausedActivity = null;
      this.previousResumedActivity = null;
   }

   public void onNewConfig() {
      Intercom$Visibility var1 = ((OverlayState)this.intercomDataLayer.getOverlayState().getValue()).getLauncherVisibility();
      Intercom$Visibility var2 = ((OverlayState)this.intercomDataLayer.getOverlayState().getValue()).getInAppNotificationsVisibility();
      List var3 = ((OverlayState)this.intercomDataLayer.getOverlayState().getValue()).getConversations();
      Activity var4 = ((OverlayState)this.intercomDataLayer.getOverlayState().getValue()).getResumedHostActivity();
      if (var4 != null && !var4.isFinishing() && !var4.isDestroyed() && this.shouldDisplayLauncher(var3, var2, var1, var4)) {
         ViewGroup var5 = this.getRootView(var4);
         ViewUtils.waitForViewAttachment(var5, () -> {
            if (!var4.isFinishing() && !var4.isDestroyed()) {
               this.launcherPresenter.displayLauncherOnAttachedRoot(var5, (AppConfig)this.appConfigProvider.get());
            }
         });
      } else {
         this.launcherPresenter.setLauncherBackgroundColor((AppConfig)this.appConfigProvider.get());
      }
   }

   public void cancelAnimations() {
      this.handler.removeCallbacksAndMessages(null);
   }

   public void removeOverlaysIfPresent(Activity var1) {
      View var2 = var1.findViewById(id.intercom_overlay_root);
      var1.runOnUiThread(() -> {
         if (var2 != null) {
            this.launcherPresenter.removeLauncher();
            this.inAppNotificationPresenter.reset((ViewGroup)var2);
            if (!var1.isFinishing() && !var1.isDestroyed() && var2.getParent() != null) {
               ((ViewGroup)var2.getParent()).removeView(var2);
            }
         }
      });
   }

   void preloadAvatarThenDisplayNotifications(List var1, Intercom$Visibility var2, Activity var3, String var4) {
      AvatarUtils.preloadAvatar(((Conversation)var1.get(0)).lastPart().getParticipant().getAvatar(), () -> {
         if (var4.equals(this.userIdentity.getFingerprint()) && this.shouldDisplayNotifications(var1, var2, var3)) {
            this.displayNotifications(var1, var3);
         }
      }, var3);
   }

   void displayNotifications(List var1, Activity var2) {
      ViewGroup var3 = this.getRootView(var2);
      Conversation var4 = (Conversation)var1.get(0);
      MessageStyle var5 = var4.lastPart().getMessageStyle();
      DeliveryOption var6 = var4.lastPart().getDeliveryOption();
      if (var1.size() > 1
         || this.inAppNotificationPresenter.isDisplaying()
         || var6 == DeliveryOption.SUMMARY
         || var6 == DeliveryOption.FULL && MessageStyle.CHAT.equals(var5)) {
         if (this.launcherPresenter.isDisplaying()) {
            DefaultLauncher var7 = this.launcherPresenter.getAndUnsetLauncher();
            if (var1.size() == 1) {
               var7.pulseForTransformation(new OverlayPresenter$1(this, var7, var3, var1));
            } else {
               var7.fadeOffScreen(new OverlayPresenter$2(this, var7, var3, var1));
            }
         } else {
            this.inAppNotificationPresenter.displayNotifications(var3, var1);
         }
      } else if (var6 == DeliveryOption.FULL) {
         if (MessageStyle.POST.equals(var5)) {
            this.trackInAppView(var4.getId(), var4.lastPart().getId(), 1);
            this.openPost(var4);
         } else if (MessageStyle.NOTE.equals(var5)) {
            this.trackInAppView(var4.getId(), var4.lastPart().getId(), 1);
            this.openNote(var4);
         }
      }
   }

   @Override
   public ViewGroup getRootView() {
      Activity var1 = this.hostActivity();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.getRootView(var1);
      }
   }

   private ViewGroup getRootView(Activity var1) {
      ViewGroup var2 = (ViewGroup)var1.findViewById(id.intercom_overlay_root);
      if (var2 == null) {
         var2 = new FrameLayout(var1);
         var2.setClipChildren(false);
         var2.setClipToPadding(false);
         var2.setId(id.intercom_overlay_root);
         if (VERSION.SDK_INT >= 30) {
            this.setupUniversalOverlayPositioning(var2);
         } else {
            this.setupLegacyOverlayPositioning(var2, var1);
         }

         var1.addContentView(var2, new LayoutParams(-1, -1));
      }

      return var2;
   }

   private void setupUniversalOverlayPositioning(ViewGroup var1) {
      var1.addOnAttachStateChangeListener(new OverlayPresenter$3(this));
   }

   private void applyOverlayInsetsPadding(View var1, Insets var2) {
      int var3 = (int)(8.0F * var1.getResources().getDisplayMetrics().density);
      var1.setPadding(var2.left, var2.top, var2.right, var2.bottom + var3);
   }

   private void setupLegacyOverlayPositioning(ViewGroup var1, Activity var2) {
      var1.setFitsSystemWindows(false);
      var1.getViewTreeObserver().addOnGlobalLayoutListener(new OverlayPresenter$4(this, var1, var2));
   }

   private void applyLegacyBottomPadding(ViewGroup var1, Activity var2) {
      try {
         int var3 = (int)(8.0F * var2.getResources().getDisplayMetrics().density);
         int var7 = this.getLegacyNavigationBarHeight(var2);
         if (var7 > 0) {
            var1.setPadding(var1.getPaddingLeft(), var1.getPaddingTop(), var1.getPaddingRight(), var7 + var3);
         } else {
            int var8 = (int)(24.0F * var2.getResources().getDisplayMetrics().density) + var3;
            var1.setPadding(var1.getPaddingLeft(), var1.getPaddingTop(), var1.getPaddingRight(), var8);
         }
      } catch (Throwable var6) {
         int var4 = (int)(8.0F * var2.getResources().getDisplayMetrics().density);
         int var5 = (int)(24.0F * var2.getResources().getDisplayMetrics().density) + var4;
         var1.setPadding(var1.getPaddingLeft(), var1.getPaddingTop(), var1.getPaddingRight(), var5);
      }
   }

   private int getLegacyNavigationBarHeight(Activity var1) {
      Resources var2 = var1.getResources();
      int var3 = var2.getIdentifier("navigation_bar_height", "dimen", "android");
      return var3 > 0 ? var2.getDimensionPixelSize(var3) : 0;
   }

   @Override
   public void markAsDismissed(Conversation var1) {
      ((Api)this.api.get()).markConversationAsDismissed(var1.getId());
      this.intercomDataLayer.markConversationPartAsDismissed(var1.lastPart().getId());
   }

   @Override
   public void openNotification(Conversation var1) {
      Part var2 = var1.getLastAdminPart();
      MessageStyle var3 = var2.getMessageStyle();
      if (MessageStyle.POST.equals(var3)) {
         this.trackInAppView(var1.getId(), var1.lastPart().getId(), 0);
         this.openPost(var1);
      } else if (MessageStyle.NOTE.equals(var3)) {
         this.trackInAppView(var1.getId(), var1.lastPart().getId(), 0);
         this.openNote(var1);
      } else {
         this.openMessenger(var1);
         if (MessageStyle.CHAT.equals(var3) && DeliveryOption.FULL == var2.getDeliveryOption()) {
            this.metricTracker.openedConversationFromFull(var1.getId(), var2.getId());
         } else {
            this.metricTracker.openedConversationFromSnippet(var1.getId(), var2.getId());
         }
      }
   }

   private void openMessenger(Conversation var1) {
      Activity var2 = this.hostActivity();
      if (var2 != null) {
         ArrayList var3 = new ArrayList();
         Intent var4 = ConversationScreenOpenerKt.getConversationIntent(var2, var1.getId());
         Intent var5 = IntercomRootActivityArgsKt.getIntentForArgs(
            var2, IntercomRootActivityArgs$MessagesScreenArgs.INSTANCE, IntercomRootActivity.class, 268435456
         );
         var3.add(var5);
         var3.add(var4);

         try {
            var2.startActivities((Intent[])var3.toArray(new Intent[0]));
         } catch (RuntimeException var7) {
            this.twig.internal("Overlay", "Error opening messenger from notification: " + var7.getMessage());
         }
      }
   }

   private void openPost(Conversation var1) {
      Activity var2 = this.hostActivity();
      if (var2 != null) {
         var2.startActivity(
            IntercomPostActivity.buildPostIntent(var2, var1.lastPart(), var1.getId(), var1.lastParticipatingAdmin(), var1.getComposerState().isVisible(), true)
         );
      }
   }

   private void openNote(Conversation var1) {
      try {
         Activity var2 = this.hostActivity();
         if (var2 != null) {
            var2.startActivity(
               IntercomNoteActivity.buildNoteIntent(var2, var1.lastPart(), var1.getId(), var1.lastParticipatingAdmin(), var1.getComposerState().isVisible())
            );
         }
      } catch (IllegalArgumentException var3) {
         this.twig.internal("Overlay", "Error loading the note " + var3.getMessage());
      }
   }

   private void openSurvey(@NonNull Activity var1) {
      var1.startActivity(IntercomSurveyActivity.buildIntent(var1));
   }

   private void openCarousel(@NonNull Activity var1) {
      var1.startActivity(new Intent(var1, IntercomCarouselActivity.class));
   }

   @Nullable
   private Activity hostActivity() {
      return ((OverlayState)this.intercomDataLayer.getOverlayState().getValue()).getResumedHostActivity();
   }

   private void trackInAppView(String var1, String var2, @MetricTracker$InAppMessageViewContext int var3) {
      if (!this.viewedPartIds.contains(var2)) {
         this.viewedPartIds.add(var2);
         this.metricTracker.viewedInApp(var1, var2, var3);
      }
   }

   @VisibleForTesting
   boolean shouldDisplaySurvey(SurveyData var1, @Nullable Activity var2) {
      return !SurveyData.Companion.getNULL().equals(var1) && !this.userIdentity.isSoftReset() && var2 != null;
   }

   @VisibleForTesting
   boolean shouldDisplayCarousel(Carousel var1, @Nullable Activity var2) {
      return !Carousel.NULL.equals(var1) && !this.userIdentity.isSoftReset() && var2 != null;
   }

   boolean shouldDisplayNotifications(List var1, Intercom$Visibility var2, @Nullable Activity var3) {
      return var2 == Intercom$Visibility.VISIBLE && !this.userIdentity.isSoftReset() && !var1.isEmpty() && var3 != null;
   }

   boolean shouldDisplayLauncher(List var1, Intercom$Visibility var2, Intercom$Visibility var3, @Nullable Activity var4) {
      if (var2 == Intercom$Visibility.VISIBLE) {
         for (java.lang.Object recoveredEntryElement_0_424 : var1) {
            Conversation var6 = (Conversation) recoveredEntryElement_0_424;
            if (DeliveryOption.BADGE != var6.lastPart().getDeliveryOption()) {
               return false;
            }
         }
      }

      return var3 == Intercom$Visibility.VISIBLE
         && !this.userIdentity.isSoftReset()
         && this.userIdentity.identityExists()
         && ((AppConfig)this.appConfigProvider.get()).isReceivedFromServer()
         && var4 != null;
   }

   public void refreshStateBecauseUserIdentityIsNotInStoreYet() {
      Integer var1 = ((Set)this.intercomDataLayer.getUnreadConversationIds().getValue()).size();
      OverlayState var2 = (OverlayState)this.intercomDataLayer.getOverlayState().getValue();
      this.onStateChange(var1, var2);
   }

   private void removeOverlays(@Nullable Activity var1) {
      if (var1 != null) {
         this.removeOverlaysIfPresent(var1);
      }
   }

   // $VF: synthetic method
   static void access$000(OverlayPresenter var0, View var1, Insets var2) {
      var0.applyOverlayInsetsPadding(var1, var2);
   }

   // $VF: synthetic method
   static void access$100(OverlayPresenter var0, ViewGroup var1, Activity var2) {
      var0.applyLegacyBottomPadding(var1, var2);
   }
}
