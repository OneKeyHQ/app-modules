package io.intercom.android.sdk.overlay;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.DeliveryOption;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import io.intercom.android.sdk.utilities.Phrase;
import io.intercom.android.sdk.utilities.SystemSettings;
import io.intercom.android.sdk.utilities.ViewUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class InAppNotificationPresenter implements InAppNotification$Listener, OnClickListener {
   private static final int PILL_START_OFFSET_DP = 66;
   private static final int MAX_DISPLAYED_NOTIFICATIONS = 3;
   private final Twig twig = LumberMill.getLogger();
   private final InAppNotificationPresenter$Listener listener;
   private final LayoutInflater inflater;
   private final Map notifications = new HashMap();
   private final List lastDisplayedConversations = new ArrayList();
   final Handler handler;
   private final MetricTracker metricTracker;
   private final Provider appConfigProvider;
   private final ContextLocaliser contextLocaliser;
   private final Activity activity;
   private boolean hasAnimated = false;
   private float pillPosition;
   private int bottomPadding = 0;

   public InAppNotificationPresenter(
      LayoutInflater var1, Handler var2, InAppNotificationPresenter$Listener var3, MetricTracker var4, Provider var5, ContextLocaliser var6, Activity var7
   ) {
      this.inflater = var1;
      this.listener = var3;
      this.handler = var2;
      this.metricTracker = var4;
      this.appConfigProvider = var5;
      this.contextLocaliser = var6;
      this.activity = var7;
   }

   public void displayNotifications(ViewGroup var1, List var2) {
      ViewUtils.waitForViewAttachment(var1, new InAppNotificationPresenter$1(this, var2, var1));
   }

   void displayNotificationsAfterAttach(List var1, ViewGroup var2) {
      boolean var3 = !this.lastDisplayedConversations.equals(var1) && !this.notifications.isEmpty();
      Map var4 = new HashMap(this.notifications);
      if (var3) {
         this.updateNotifications(var1, var4, var2);
      } else if (!this.isDisplaying()) {
         this.addNewNotifications(var1, var2);
      }

      this.hasAnimated = true;
      Resources var5 = var2.getResources();
      this.pillPosition = var2.getHeight()
         - var5.getDimensionPixelSize(dimen.intercom_overlay_pill_bottom_margin)
         - var5.getDimensionPixelSize(dimen.intercom_bottom_padding)
         - this.bottomPadding;
      this.displayPill(this.inflater, var2);
      this.lastDisplayedConversations.clear();
      this.lastDisplayedConversations.addAll(var1);
   }

   public boolean isDisplaying() {
      return !this.notifications.isEmpty();
   }

   public void reset(ViewGroup var1) {
      for (java.lang.Object recoveredEntryElement_0_96 : this.notifications.values()) {
         InAppNotification var3 = (InAppNotification) recoveredEntryElement_0_96;
         var3.removeView();
      }

      View var4 = var1.findViewById(id.notification_pill);
      if (var4 != null) {
         var1.removeView(var4);
      }

      this.notifications.clear();
   }

   @Override
   public void onInAppNotificationTap(Conversation var1) {
      this.listener.openNotification(var1);
   }

   @Override
   public void onInAppNotificationDismiss(InAppNotification var1) {
      ViewGroup var2 = null;

      try {
         var2 = this.listener.getRootView();
      } catch (Exception var6) {
         this.twig.internal("Couldn't get root view: " + var6.getMessage());
      }

      Conversation var3 = var1.getConversation();
      if (var2 != null) {
         this.listener.markAsDismissed(var3);
         var2.removeView(var1.getRootView());
         this.notifications.remove(var3.getId());
         this.lastDisplayedConversations.remove(var3);

         for (java.lang.Object recoveredEntryElement_0_130 : this.notifications.values()) {
            InAppNotification var5 = (InAppNotification) recoveredEntryElement_0_130;
            var5.moveForward(var2, this.inflater);
         }

         this.displayPill(this.inflater, var2);
         if (this.notifications.isEmpty()) {
            this.hasAnimated = false;
         }
      }

      Part var7 = var3.getLastAdminPart();
      if (MessageStyle.CHAT.equals(var7.getMessageStyle()) && DeliveryOption.FULL == var7.getDeliveryOption()) {
         this.metricTracker.closedInAppFromFull(var3.getId(), var7.getId());
      } else if (var7.isInitialMessage()) {
         this.metricTracker.dismissInAppMessageSnippet(var3.getId(), var7.getId());
      } else {
         this.metricTracker.dismissInAppCommentSnippet(var3.getId(), var7.getId());
      }
   }

   @Override
   public void onClick(View var1) {
      IntercomRootActivityLauncher.INSTANCE.startMessages(var1.getContext());
   }

   private void addNewNotifications(List var1, ViewGroup var2) {
      int var3 = 0;

      for (int var4 = var1.size(); var3 < var4; var3++) {
         Conversation var5 = (Conversation)var1.get(var3);
         Context var7 = this.contextLocaliser.createLocalisedContext(var2.getContext());
         Part var8 = var5.lastPart();
         InAppNotification var6;
         if (var4 >= 2 || !MessageStyle.CHAT.equals(var8.getMessageStyle())) {
            var6 = new StackableSnippet(var7, var5, var3, this.handler, var2.getHeight(), this, this.appConfigProvider);
         } else if (var8.getDeliveryOption() == DeliveryOption.SUMMARY) {
            var6 = new ChatSnippet(var7, var5, var3, var2.getHeight(), this, this.appConfigProvider);
         } else {
            var6 = new ChatFull(var7, var5, var3, var2.getHeight(), this, this.metricTracker, this.appConfigProvider);
         }

         this.notifications.put(var5.getId(), var6);
         if (var3 < 3) {
            var6.display(var2, this.inflater, !this.hasAnimated, this.bottomPadding);
         }
      }
   }

   private void updateNotifications(List var1, Map var2, ViewGroup var3) {
      Conversation var4 = (Conversation)var1.get(0);
      Iterator var5 = var2.values().iterator();
      InAppNotification var6 = (InAppNotification)var5.next();

      while (var5.hasNext()) {
         InAppNotification var7 = (InAppNotification)var5.next();
         if (var7.getPosition() < var6.getPosition()) {
            var6 = var7;
         }
      }

      if (this.isReply(var4)) {
         var6.update(var4, new InAppNotificationPresenter$2(this, var1, var2, var3));
      } else if (var2.size() == 1 && var6 instanceof ChatSnippet) {
         var6.moveBackward(var3, new InAppNotificationPresenter$3(this, var4, var3, var2, var1));
      } else {
         for (Object recoveredNotification : var2.values()) {
            InAppNotification var8 = (InAppNotification)recoveredNotification;
            if (var8.getPosition() < 3) {
               var8.moveBackward(var3);
            } else {
               var8.setPosition(var8.getPosition() + 1);
            }
         }

         this.addNewView(var4, var3, var2);
         this.syncUpdates(var1, var2, var3);
      }
   }

   void addNewView(Conversation var1, ViewGroup var2, Map var3) {
      Context var4 = this.contextLocaliser.createLocalisedContext(var2.getContext());
      InAppNotification var5 = new StackableSnippet(var4, var1, 0, this.handler, var2.getHeight(), this, this.appConfigProvider);
      var5.display(var2, this.inflater, true, this.bottomPadding);
      if (var3.containsKey(var1.getId())) {
         ((InAppNotification)var3.get(var1.getId())).removeView();
      }

      var3.put(var1.getId(), var5);
   }

   void syncUpdates(List var1, Map var2, ViewGroup var3) {
      for (int var4 = 0; var4 < var1.size(); var4++) {
         Conversation var5 = (Conversation)var1.get(var4);
         InAppNotification var6 = (InAppNotification)var2.remove(var5.getId());
         if (var6 instanceof ChatSnippet && var4 > 0 || var6 == null) {
            if (var6 != null) {
               var6.removeView();
            }

            Context var7 = this.contextLocaliser.createLocalisedContext(var3.getContext());
            var6 = new StackableSnippet(var7, var5, var4, this.handler, var3.getHeight(), this, this.appConfigProvider);
         }

         if (var4 < 3) {
            var6.display(var3, var5, this.inflater, false, this.bottomPadding);
         } else {
            var6.removeView();
         }

         this.notifications.put(var5.getId(), var6);
      }

      for (java.lang.Object recoveredEntryElement_0_241 : var2.values()) {
         InAppNotification var9 = (InAppNotification) recoveredEntryElement_0_241;
         var9.removeView();
      }
   }

   private void displayPill(LayoutInflater var1, ViewGroup var2) {
      TextView var3 = (TextView)var2.findViewById(id.notification_pill);
      if (this.notifications.size() <= 3) {
         if (var3 != null) {
            var2.removeView(var3);
         }
      } else {
         if (var3 == null) {
            var1.inflate(layout.intercom_notification_pill, var2, true);
            var3 = (TextView)var2.findViewById(id.notification_pill);
            var3.setAlpha(0.0F);
            var3.setScaleX(0.4F);
            var3.setScaleY(0.4F);
            var3.setY(this.pillPosition);
            var3.setOnClickListener(this);
            this.animatePill(var3);
         }

         Context var4 = this.contextLocaliser.createLocalisedContext(var3.getContext());
         Phrase.from(var4, string.intercom_plus_x_more).put("n", this.notifications.size() - 3).into(var3);
      }
   }

   private void animatePill(View var1) {
      this.handler.postDelayed(new InAppNotificationPresenter$4(this, var1), (long)(500.0F * SystemSettings.getTransitionScale(var1.getContext())));
   }

   private void updatePillPosition(View var1) {
      PropertyValuesHolder var2 = PropertyValuesHolder.ofFloat("y", var1.getY(), this.pillPosition);
      ObjectAnimator.ofPropertyValuesHolder(var1, var2).setDuration(300L).start();
   }

   private boolean isReply(Conversation var1) {
      return !this.lastDisplayedConversations.isEmpty() && var1.getId().equals(((Conversation)this.lastDisplayedConversations.get(0)).getId());
   }

   public void setBottomPadding(int var1) {
      this.bottomPadding = var1;
      if (this.isDisplaying()) {
         for (java.lang.Object recoveredEntryElement_0_285 : this.notifications.values()) {
            InAppNotification var3 = (InAppNotification) recoveredEntryElement_0_285;
            var3.updateBottomPadding(this.inflater.getContext(), this.bottomPadding);
         }

         ViewGroup var6 = null;

         try {
            var6 = this.listener.getRootView();
         } catch (Exception var5) {
            this.twig.internal("Couldn't get root view: " + var5.getMessage());
         }

         if (var6 != null) {
            Resources var7 = var6.getResources();
            this.pillPosition = var6.getHeight()
               - var7.getDimensionPixelSize(dimen.intercom_overlay_pill_bottom_margin)
               - var7.getDimensionPixelSize(dimen.intercom_bottom_padding)
               - var1;
            View var4 = var6.findViewById(id.notification_pill);
            if (var4 != null) {
               this.updatePillPosition(var4);
            }
         }
      }
   }

   // $VF: synthetic method
   static float access$000(InAppNotificationPresenter var0) {
      return var0.pillPosition;
   }
}
