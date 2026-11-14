package io.intercom.android.sdk.metrics;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.overlay.LauncherOpenBehaviour$LauncherType;
import io.intercom.android.sdk.user.DeviceData;
import io.intercom.android.sdk.utilities.commons.DeviceUtils;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MetricTracker {
   static final String METADATA_BADGE_VALUE = "badge_value";
   static final String METADATA_IS_ATTACHMENT = "is_attachment";
   static final String METADATA_IS_GIF = "is_gif";
   static final String METADATA_MESSAGE_ID = "message_id";
   static final String METADATA_CONVERSATION_ID = "conversation_id";
   static final String METADATA_TICKET_ID = "ticket_id";
   static final String METADATA_PART_ID = "part_id";
   static final String METADATA_QUICK_REPLY_ID = "quick_reply_id";
   static final String METADATA_COMMENT_ID = "comment_id";
   static final String METADATA_REACTION_INDEX = "reaction_index";
   static final String METADATA_MESSAGE_TYPE = "message_type";
   static final String METADATA_PUSH_TYPE = "push_type";
   static final String METADATA_HAS_ARTICLE_CARD = "has_article_card";
   static final String METADATA_SEARCH_QUERY = "search_query";
   static final String METADATA_TEAMMATE_STATUS = "teammate_status";
   static final String METADATA_TIME_SINCE_LAST_ACTIVE = "time_since_last_active";
   static final String METADATA_WITHIN_OFFICE_HOURS = "within_office_hours";
   static final String METADATA_SUGGESTION_ID = "suggestion_id";
   static final String METADATA_INSTANCE_ID = "instance_id";
   static final String METADATA_REQUEST_CONTENT_ID = "requested_content_id";
   static final String METADATA_REQUEST_ERROR_CODE = "error_code";
   static final String METADATA_CLOSED_CONVERSATION_ID = "closed_conversation_id";
   static final String METADATA_SOURCE = "source";
   static final String METADATA_ERROR = "error";
   static final String METADATA_FILTERED = "filtered";
   static final String METADATA_URL = "url";
   static final String METADATA_VIDEO_URL = "video_url";
   static final String METADATA_VIDEO_WIDTH = "video_width";
   static final String METADATA_VIDEO_HEIGHT = "video_height";
   static final String METADATA_SCREENS_IN_MESSAGE = "screens_in_message";
   static final String METADATA_SCREENS_SHOWN = "screens_shown";
   static final String METADATA_SCREENS_SEEN = "screens_seen";
   static final String METADATA_PERMISSION_GRANTED = "permission_granted";
   static final String METADATA_HOME_SCREEN_INDEX = "home_screen_index";
   static final String METADATA_SURVEY_ID = "survey_id";
   static final String METADATA_SURVEY_FORMAT = "format";
   static final String METADATA_PLATFORM = "platform";
   static final String METADATA_STEPS_COMPLETE = "steps_complete";
   static final String METADATA_SPACE = "space";
   static final String METADATA_CONTENT_TYPE = "content_type";
   static final String QUESTIONS_SEEN = "questions_seen";
   static final String METADATA_MAX_STEPS = "max_steps";
   static final String TICKET_TYPE_ID = "ticket_type_id";
   static final String TICKET_STATUS = "ticket_status";
   static final String VALUE_MESSAGE = "message";
   static final String VALUE_NOTIFICATION = "notification";
   static final String VALUE_ACTIVE = "active";
   static final String VALUE_AWAY = "away";
   static final String VALUE_ANDROID_SDK = "android-sdk";
   private final UserIdentity userIdentity;
   private final TimeProvider timeProvider;
   private final MetricsStore store;
   private final String sdkVersion;
   private final String appVersion;
   private final String appMinSdkVersion;
   private final String appName;

   public MetricTracker(UserIdentity var1, MetricsStore var2, android.content.Context var3) {
      this(var1, var2, TimeProvider.SYSTEM, var3);
   }

   @VisibleForTesting
   MetricTracker(UserIdentity var1, MetricsStore var2, TimeProvider var3, android.content.Context var4) {
      this.userIdentity = var1;
      this.timeProvider = var3;
      this.store = var2;
      this.sdkVersion = "18.10.0";
      this.appVersion = DeviceUtils.getAppVersion(var4);
      this.appMinSdkVersion = DeviceUtils.getAppMinSdkVersionVersion(var4);
      this.appName = DeviceData.getApplicationName(var4);
   }

   public void openedMessengerConversationList(LauncherOpenBehaviour$LauncherType var1) {
      this.track(this.newMetric("opened", "conversation_list", "messenger", contextFrom(var1)));
   }

   public void openedMessengerConversation(String var1, LauncherOpenBehaviour$LauncherType var2) {
      this.track(this.newMetric("opened", "conversation", "messenger", contextFrom(var2)).addMetaData("conversation_id", var1));
   }

   public void openedMessengerNewConversation(LauncherOpenBehaviour$LauncherType var1) {
      this.track(this.newMetric("opened", "new_conversation", "messenger", contextFrom(var1)));
   }

   private static String contextFrom(LauncherOpenBehaviour$LauncherType var0) {
      return LauncherOpenBehaviour$LauncherType.DEFAULT == var0 ? "from_launcher" : "from_custom_launcher";
   }

   public void closedMessengerBackButton() {
      this.track(this.newMetric("closed", "messenger", "messenger", "from_android_back_button"));
   }

   public void closedMessengerCloseButton() {
      this.track(this.newMetric("closed", "messenger", "messenger", "from_close_button"));
   }

   void typeInNewConversation() {
      this.track(this.newMetric("typed", "message", "messenger", "in_new_conversation"));
   }

   void typeInNewFollowUpConversation(String var1) {
      this.track(this.newMetric("typed", "message", "messenger", "from_closed_conversation").addMetaData("closed_conversation_id", var1));
   }

   void typeInConversation(String var1) {
      this.track(this.newMetric("typed", "reply", "messenger", "in_conversation").addMetaData("conversation_id", var1));
   }

   public void sentInNewConversation(boolean var1, boolean var2, String var3, String var4, boolean var5) {
      this.track(
         this.newMetric("sent", "message", "messenger", "in_new_conversation")
            .addMetaData("is_attachment", var1)
            .addMetaData("is_gif", var2)
            .addMetaData("message_id", var3)
            .addMetaData("conversation_id", var4)
            .addMetaData("within_office_hours", var5)
      );
   }

   public void sentInNewFollowupConversation(boolean var1, boolean var2, String var3, String var4, boolean var5, String var6) {
      this.track(
         this.newMetric("sent", "message", "messenger", "in_followup_conversation")
            .addMetaData("is_attachment", var1)
            .addMetaData("is_gif", var2)
            .addMetaData("message_id", var3)
            .addMetaData("conversation_id", var4)
            .addMetaData("within_office_hours", var5)
            .addMetaData("closed_conversation_id", var6)
      );
   }

   public void sentInConversation(boolean var1, boolean var2, String var3, String var4, boolean var5, boolean var6, String var7) {
      this.track(
         this.newMetric("sent", "reply", "messenger", "in_conversation")
            .addMetaData("is_attachment", var1)
            .addMetaData("is_gif", var2)
            .addMetaData("message_id", var3)
            .addMetaData("conversation_id", var4)
            .addMetaData("within_office_hours", var5)
            .addMetaData("teammate_status", var6 ? "active" : "away")
            .addMetaData("time_since_last_active", var7)
      );
   }

   public void openConversationsListFromNewConversation() {
      this.track(this.newMetric("opened", "conversation_list", "messenger", "from_new_conversation"));
   }

   public void startConversation(String var1) {
      this.track(this.newMetric("opened", "conversation", "messenger", "from_new_conversation").addMetaData("conversation_id", var1));
   }

   public void startFollowupConversation(String var1, String var2) {
      this.track(
         this.newMetric("opened", "conversation", "messenger", "in_followup_conversation")
            .addMetaData("conversation_id", var1)
            .addMetaData("closed_conversation_id", var2)
      );
   }

   public void openConversationsListFromConversation(String var1) {
      this.track(this.newMetric("opened", "conversation_list", "messenger", "from_conversation").addMetaData("conversation_id", var1));
   }

   public void openedConversationFromFull(String var1, String var2) {
      this.track(this.newMetric("opened", "conversation", "messenger", "from_full").addMetaData("comment_id", var2).addMetaData("conversation_id", var1));
   }

   public void openedConversationFromSnippet(String var1, String var2) {
      this.track(this.newMetric("opened", "conversation", "messenger", "from_snippet").addMetaData("comment_id", var2).addMetaData("conversation_id", var1));
   }

   public void receivedReply(boolean var1, boolean var2, String var3, String var4) {
      this.track(
         this.newMetric("received", "reply", "messenger", "in_conversation")
            .addMetaData("is_attachment", var1)
            .addMetaData("has_article_card", var2)
            .addMetaData("part_id", var3)
            .addMetaData("conversation_id", var4)
      );
   }

   public void viewedReply(boolean var1, boolean var2, String var3, String var4) {
      this.track(
         this.newMetric("viewed", "reply", "messenger", "from_snippet")
            .addMetaData("is_attachment", var1)
            .addMetaData("has_article_card", var2)
            .addMetaData("comment_id", var3)
            .addMetaData("conversation_id", var4)
      );
   }

   public void sentReaction(String var1, String var2, int var3, MetricTracker$ReactionLocation var4) {
      if (MetricTracker$ReactionLocation.CONVERSATION == var4) {
         this.track(
            this.newMetric("sent", "reaction", "messenger", "in_conversation")
               .addMetaData("message_id", var2)
               .addMetaData("reaction_index", var3)
               .addMetaData("conversation_id", var1)
         );
      } else if (MetricTracker$ReactionLocation.LINK == var4) {
         this.track(this.newEducateMetric("sent", "reaction", "messenger", "on_article").addMetaData("message_id", var2).addMetaData("conversation_id", var1));
      } else {
         this.track(this.newMetric("sent", "reaction", "in_app", "from_full").addMetaData("message_id", var2).addMetaData("conversation_id", var1));
      }
   }

   public void openConversationFromConversationList(String var1, boolean var2, boolean var3, String var4) {
      this.track(
         this.newMetric("opened", "conversation", "messenger", "from_conversation_list")
            .addMetaData("conversation_id", var1)
            .addMetaData("within_office_hours", var2)
            .addMetaData("teammate_status", var3 ? "active" : "away")
            .addMetaData("time_since_last_active", var4)
      );
   }

   public void receivedPushNotification(String var1) {
      this.track(this.newMetric("received", "message", "push", "from_push").addMetaData("conversation_id", var1).addMetaData("push_type", "notification"));
   }

   public void receivedPushOnlyNotification(String var1, String var2) {
      this.track(
         this.newMetric("received", "message", "push", "from_push")
            .addMetaData("conversation_id", var1)
            .addMetaData("instance_id", var2)
            .addMetaData("push_type", "message")
      );
   }

   public void viewedPushNotification(String var1) {
      this.track(this.newMetric("viewed", "message", "in_app", "from_push").addMetaData("conversation_id", var1).addMetaData("push_type", "notification"));
   }

   public void openedPushOnlyNotification(String var1) {
      this.track(this.newMetric("opened", "message", "in_app", "from_push").addMetaData("conversation_id", var1).addMetaData("push_type", "message"));
   }

   public void dismissedPushNotification(String var1) {
      this.track(this.newMetric("dismissed", "message", "in_app", "from_push").addMetaData("conversation_id", var1).addMetaData("push_type", "notification"));
   }

   public void dismissedPushOnlyNotification(String var1, String var2) {
      this.track(
         this.newMetric("dismissed", "message", "in_app", "from_push")
            .addMetaData("conversation_id", var1)
            .addMetaData("instance_id", var2)
            .addMetaData("push_type", "message")
      );
   }

   public void receivedMessageFromSnippetWhenClosed(boolean var1, boolean var2, String var3, String var4, MessageStyle var5) {
      this.track(
         this.newMetric("received", "message", "in_app", "from_snippet")
            .addMetaData("conversation_id", var3)
            .addMetaData("message_id", var4)
            .addMetaData("message_type", var5.name().toLowerCase())
            .addMetaData("is_attachment", var1)
            .addMetaData("has_article_card", var2)
      );
   }

   public void receivedReplyFromSnippetWhenClosed(boolean var1, boolean var2, String var3, String var4) {
      this.track(
         this.newMetric("received", "reply", "in_app", "from_snippet")
            .addMetaData("conversation_id", var3)
            .addMetaData("comment_id", var4)
            .addMetaData("is_attachment", var1)
            .addMetaData("has_article_card", var2)
      );
   }

   public void receivedMessageFromFullWhenClosed(boolean var1, boolean var2, String var3, String var4, MessageStyle var5) {
      this.track(
         this.newMetric("received", "message", "in_app", "from_full")
            .addMetaData("conversation_id", var3)
            .addMetaData("message_id", var4)
            .addMetaData("message_type", var5.name().toLowerCase())
            .addMetaData("is_attachment", var1)
            .addMetaData("has_article_card", var2)
      );
   }

   public void receivedQuickReply(String var1, String var2, String var3) {
      this.track(
         this.newMetric("received", "quick_replies", "messenger", "in_conversation")
            .addMetaData("conversation_id", var2)
            .addMetaData("part_id", var3)
            .addMetaData("quick_reply_id", var1)
      );
   }

   public void clickedQuickReply(String var1, String var2, String var3) {
      this.track(
         this.newMetric("clicked", "quick_reply", "messenger", "in_conversation")
            .addMetaData("conversation_id", var2)
            .addMetaData("part_id", var3)
            .addMetaData("quick_reply_id", var1)
      );
   }

   public void receivedComposerSuggestions(String var1, String var2, String var3) {
      this.track(
         this.newMetric("received", "composer_suggestions", "messenger", "in_conversation")
            .addMetaData("conversation_id", var2)
            .addMetaData("part_id", var3)
            .addMetaData("suggestion_id", var1)
      );
   }

   public void clickedComposerSuggestion(String var1, String var2, String var3) {
      this.track(
         this.newMetric("clicked", "suggestion", "messenger", "in_conversation")
            .addMetaData("conversation_id", var2)
            .addMetaData("part_id", var3)
            .addMetaData("suggestion_id", var1)
      );
   }

   public void dismissInAppMessageSnippet(String var1, String var2) {
      this.track(this.newMetric("dismissed", "message", "in_app", "from_snippet").addMetaData("conversation_id", var1).addMetaData("message_id", var2));
   }

   public void dismissInAppCommentSnippet(String var1, String var2) {
      this.track(this.newMetric("dismissed", "reply", "in_app", "from_snippet").addMetaData("conversation_id", var1).addMetaData("comment_id", var2));
   }

   public void viewedInAppFromMessenger(String var1, String var2, MessageStyle var3) {
      this.track(
         this.newMetric("viewed", "message", "messenger", "from_full")
            .addMetaData("message_type", var3.name().toLowerCase())
            .addMetaData("message_id", var2)
            .addMetaData("conversation_id", var1)
      );
   }

   public void viewedInApp(String var1, String var2, @MetricTracker$InAppMessageViewContext int var3) {
      this.track(this.newMetric("viewed", "message", "in_app", contextForInApp(var3)).addMetaData("message_id", var2).addMetaData("conversation_id", var1));
   }

   private static String contextForInApp(@MetricTracker$InAppMessageViewContext int var0) {
      return var0 == 0 ? "from_snippet" : "from_full";
   }

   public void closedInAppFromFull(String var1, String var2) {
      this.track(this.newMetric("closed", "message", "in_app", "from_full").addMetaData("message_id", var2).addMetaData("conversation_id", var1));
   }

   public void receivedNotificationFromBadgeWhenMessengerClosed(String var1) {
      this.track(this.newMetric("received", "badge", "in_app", "from_launcher").addMetaData("badge_value", var1));
   }

   public void receivedOperatorReply(String var1) {
      this.track(this.newEducateMetric("received", "operator_reply", "messenger", "from_conversation").addMetaData("conversation_id", var1));
   }

   public void openedNativeHelpCenter(String var1, @Nullable String var2) {
      String var3 = var2;
      if (var3 == null) {
         var3 = "no_context";
      }

      this.track(this.newHelpCenterMetric("opened", "help_center", var1, var3, "search_browse".equals(var1)));
   }

   public void searchedNativeHelpCenter(boolean var1) {
      this.track(this.newHelpCenterMetric("searched", "help_center", "search_results", "no_context", var1));
   }

   public void openedNativeHelpCenterSearchResult(boolean var1, String var2) {
      this.track(this.newHelpCenterMetric("clicked", "help_center", "search_results", "no_context", var1).addMetaData("search_query", var2));
   }

   public void clickedNewConversation(String var1, String var2, boolean var3) {
      this.track(this.newHelpCenterMetric("clicked", "new_conversation", var1, var2, var3));
   }

   public void sentArticleReaction(String var1, @Nullable Boolean var2, boolean var3) {
      MetricObject var4 = this.newHelpCenterMetric("sent", "article_reaction", "article", var1, var3);
      if (var2 != null) {
         var4.addMetaData("conversation_shown", var2);
      }

      this.track(var4);
   }

   public void failedHelpCenter(String var1, String var2, @Nullable String var3, boolean var4) {
      MetricObject var5 = this.newHelpCenterMetric("failed", var1, var2, "no_context", var4);
      if (var3 != null) {
         var5.addMetaData("error", var3);
      }

      this.track(var5);
   }

   public void requestedHelpCenterData(String var1) {
      MetricObject var2 = this.newEducateMetric("requested", "help_center_data", var1, "no_context");
      this.track(var2);
   }

   public void viewedNativeHelpCenter(String var1, @Nullable Boolean var2, boolean var3) {
      MetricObject var4 = this.newHelpCenterMetric("viewed", "help_center", var1, "no_context", var3);
      if (var2 != null) {
         var4.addMetaData("filtered", var2);
      }

      this.track(var4);
   }

   public void viewedCreateTicketForm(@Nullable Long var1, String var2, String var3) {
      MetricObject var4 = this.newMetric("viewed", "create_ticket_form", "messenger", var3);
      if (var1 != null) {
         var4.addMetaData("ticket_type_id", var1);
      }

      var4.addMetaData("conversation_id", var2);
      this.track(var4);
   }

   public void viewedTicketDetails(@Nullable Long var1, String var2, String var3, String var4) {
      MetricObject var5 = this.newMetric("viewed", "ticket_details", "messenger", var4);
      if (var1 != null) {
         var5.addMetaData("ticket_type_id", var1);
      }

      var5.addMetaData("ticket_status", var3);
      var5.addMetaData("conversation_id", var2);
      this.track(var5);
   }

   public void submittedCreateTicketForm(@Nullable Long var1, String var2, String var3) {
      MetricObject var4 = this.newMetric("submitted", "create_ticket_form", "messenger", var3);
      if (var1 != null) {
         var4.addMetaData("ticket_type_id", var1);
      }

      var4.addMetaData("conversation_id", var2);
      this.track(var4);
   }

   public void viewedSearchBrowseCard(int var1) {
      MetricObject var2 = this.createMetric("search_browse_event", "messenger", "viewed", "search_browse_card", "home", "messenger");
      var2.addMetaData("home_screen_index", var1);
      this.track(var2);
   }

   public void clickedSearchBrowseCard() {
      MetricObject var1 = this.createMetric("educate_event", "messenger", "clicked", "search_browse_card_input", "home", "messenger");
      this.track(var1);
   }

   public void clickedArticleSuggestion(String var1) {
      MetricObject var2 = this.createMetric("m5_metric", "messenger", "clicked", "article_suggestion", "search_browse_card", "messenger")
         .addMetaData("article_id", var1);
      this.track(var2);
   }

   public void viewedArticleSuggestions(List var1) {
      MetricObject var2 = this.createMetric("m5_metric", "messenger", "viewed", "article_suggestions", "search_browse_card", "messenger")
         .addMetaData("articles_count", var1.size())
         .addMetaData("articles_shown", var1);
      this.track(var2);
   }

   public void clickedInput(String var1, String var2) {
      this.track(this.newMetric("clicked", var2, "messenger", "in_conversation").addMetaData("conversation_id", var1));
   }

   public void searchedGifInput(String var1) {
      this.track(this.newMetric("searched", "expanded_gif_input", "messenger", "in_conversation").addMetaData("search_query", var1));
   }

   public void expandedInput(String var1) {
      this.track(this.newMetric("opened", "expanded_gif_input", "messenger", "in_conversation").addMetaData("conversation_id", var1));
   }

   public void viewedMessengerSheet(String var1, String var2, boolean var3) {
      String var4 = var3 ? "home_screen" : "conversation";
      this.track(this.newMetric("viewed", "messenger_sheet", "messenger", var4).addMetaData("url", var2).addMetaData("conversation_id", var1));
   }

   public void closedMessengerSheet(String var1, String var2, boolean var3) {
      String var4 = var3 ? "home_screen" : "conversation";
      this.track(this.newMetric("closed", "messenger_sheet", "messenger", var4).addMetaData("url", var2).addMetaData("conversation_id", var1));
   }

   public void messengerCardWebViewEvent(String var1, String var2, String var3, @Nullable Map var4, boolean var5) {
      String var6 = var5 ? "home_screen" : "conversation";
      MetricObject var7 = this.newEducateMetric(var1, var2, var3, var6);
      this.addMetadataToMetricObject(var7, var4);
      this.track(var7);
   }

   public void clickedSpace(String var1) {
      this.track(this.newMetric("clicked", var1, "messenger", "home"));
   }

   public void viewedNewConversation(String var1) {
      this.track(this.newMetric("viewed", "new_conversation", "messenger", var1));
   }

   public void clickedExternalLinkCard() {
      this.track(this.newMetric("clicked", "external_link", "messenger", "home"));
   }

   public void viewedConversation(String var1, String var2) {
      this.track(this.newMetric("viewed", "conversation", "messenger", var1).addMetaData("conversation_id", var2));
   }

   public void clickedLauncher() {
      this.track(this.newMetric("clicked", "launcher", "messenger", "no_context"));
   }

   public void receivedInAppOnLegacyActivity() {
      this.track(this.newMetric("received", "in_app_on_legacy_activity", "in_app", "no_context"));
   }

   public void clickedPoweredBy(String var1) {
      this.track(this.newMetric("clicked", "we_run_on_intercom", "messenger", var1));
   }

   public void viewedSpace(String var1) {
      this.track(this.newMetric("viewed", var1, "messenger", "no_context"));
   }

   public void startedToPlayVideo() {
      this.track(this.newMetric("started", "video_auto_message", "in_app", "conversation"));
   }

   public void loadedVideo(String var1, int var2, int var3) {
      this.track(
         this.newMetric("loaded", "video_auto_message", "in_app", "conversation")
            .addMetaData("video_url", var1)
            .addMetaData("video_width", var2)
            .addMetaData("video_height", var3)
      );
   }

   public void failedToPlayVideo(String var1) {
      this.track(this.newMetric("failed", "video_auto_message", "in_app", "conversation").addMetaData("video_url", var1));
   }

   public void completedVideo() {
      this.track(this.newMetric("completed", "video_auto_message", "in_app", "conversation"));
   }

   public void openedCarousel(String var1, String var2, int var3, int var4) {
      this.track(
         this.newCarouselMetric("opened", "carousel_message", "from_carousel", var1, var2)
            .addMetaData("screens_in_message", var3)
            .addMetaData("screens_shown", var4)
      );
   }

   public void dismissedCarousel(String var1, String var2, String var3, int var4, int var5, int var6) {
      this.track(
         this.newCarouselMetric("dismissed", "carousel_message", var1, var2, var3)
            .addMetaData("screens_in_message", var4)
            .addMetaData("screens_shown", var5)
            .addMetaData("screens_seen", var6)
      );
   }

   public void dismissedCarouselFetching(String var1, String var2, String var3) {
      this.track(this.newCarouselMetric("dismissed", "carousel_message_preload", var1, var3).addMetaData("requested_content_id", var2));
   }

   public void failedCarousel(String var1, String var2, int var3) {
      this.track(
         this.newCarouselMetric("failed", "carousel_message_preload", "from_carousel", var2)
            .addMetaData("error_code", var3)
            .addMetaData("requested_content_id", var1)
      );
   }

   public void startedCarouselFetching(String var1, String var2) {
      this.track(this.newCarouselMetric("started", "carousel_message_preload", "from_carousel", var2).addMetaData("requested_content_id", var1));
   }

   public void retriedCarouselFetching(String var1, String var2) {
      this.track(this.newCarouselMetric("clicked", "carousel_message_preload", "from_retry", var2).addMetaData("requested_content_id", var1));
   }

   public void completedCarousel(String var1, String var2, String var3, int var4, int var5) {
      this.track(
         this.newCarouselMetric("completed", "carousel_message", var1, var2, var3).addMetaData("screens_in_message", var4).addMetaData("screens_shown", var5)
      );
   }

   @SuppressLint("WrongConstant")
   public void skippedPermission(String var1, String var2, String var3) {
      this.track(this.newCarouselMetric("clicked", "carousel_permission_skipped", var1, var2, var3));
   }

   @SuppressLint("WrongConstant")
   public void requestedPermission(String var1, String var2, String var3, boolean var4) {
      this.track(this.newCarouselMetric("clicked", "carousel_permission_dialog", var1, var2, var3).addMetaData("permission_granted", var4));
   }

   @SuppressLint("WrongConstant")
   public void sentToPermissionSettings(String var1, String var2, String var3, boolean var4) {
      this.track(this.newCarouselMetric("clicked", "carousel_permission_settings", var1, var2, var3).addMetaData("permission_granted", var4));
   }

   public void educateWebviewMetric(String var1, String var2, String var3, @Nullable Map var4, String var5) {
      MetricObject var6 = this.newEducateMetric(var1, var2, var3, var5);
      if (var5.equals("from_article_on_button")) {
         var6.addMetaData("source", "programmatic");
      } else {
         var6.addMetaData("source", "automatic");
      }

      this.addMetadataToMetricObject(var6, var4);
      this.track(var6);
   }

   public void surveyOpened(String var1, String var2, String var3, String var4, String var5) {
      MetricObject var6 = this.newSurveyMetric(var1, var2, var3)
         .addMetaData("platform", "android-sdk")
         .addMetaData("survey_id", var4)
         .addMetaData("format", var5);
      this.track(var6);
   }

   public void surveyCompleted(String var1, String var2, String var3, String var4, String var5, int var6, int var7, int var8) {
      MetricObject var9 = this.newSurveyMetric(var1, var2, var3)
         .addMetaData("platform", "android-sdk")
         .addMetaData("survey_id", var4)
         .addMetaData("format", var5)
         .addMetaData("steps_complete", var6)
         .addMetaData("questions_seen", var8)
         .addMetaData("max_steps", var7);
      this.track(var9);
   }

   public void surveyDismissed(String var1, String var2, String var3, String var4, String var5, int var6, int var7, int var8) {
      MetricObject var9 = this.newSurveyMetric(var1, var2, var3)
         .addMetaData("platform", "android-sdk")
         .addMetaData("survey_id", var4)
         .addMetaData("format", var5)
         .addMetaData("steps_complete", var6)
         .addMetaData("questions_seen", var8)
         .addMetaData("max_steps", var7);
      this.track(var9);
   }

   public void moreMenuClicked(String var1) {
      this.track(this.newMetric("clicked", "more_menu", "conversation", var1));
   }

   public void conversationsInMoreMenuClicked(String var1) {
      this.track(this.newMetric("clicked", "messages", "conversation", var1));
   }

   public void newConversationInMoreMenuClicked(String var1) {
      this.track(this.newMetric("clicked", "new_conversation", "conversation", var1));
   }

   public void ticketsInMoreMenuClicked(String var1) {
      this.track(this.newMetric("clicked", "tickets", "conversation", var1));
   }

   public void helpInMoreMenuClicked(String var1) {
      this.track(this.newMetric("clicked", "help", "conversation", var1));
   }

   private void track(MetricObject var1) {
      this.store.track(var1);
   }

   private MetricObject newMetric(String var1, String var2, String var3, String var4) {
      return this.createMetric("m5_metric", "messenger", var1, var2, var3, var4);
   }

   private MetricObject newEducateMetric(String var1, String var2, String var3, String var4) {
      return this.createMetric("educate_event", "educate", var1, var2, var3, var4);
   }

   private MetricObject newHelpCenterMetric(String var1, String var2, String var3, String var4, boolean var5) {
      String var6 = var5 ? "search_browse_event" : "educate_event";
      return this.createMetric(var6, "educate", var1, var2, var3, var4);
   }

   private MetricObject newCarouselMetric(String var1, String var2, String var3, String var4, String var5) {
      return this.newCarouselMetric(var1, var2, var3, var5).addMetaData("instance_id", var4);
   }

   private MetricObject newCarouselMetric(String var1, String var2, String var3, String var4) {
      return this.createMetric("carousel_metric", "messenger", var1, var2, "carousel_message", var3).addMetaData("source", var4);
   }

   private MetricObject newSurveyMetric(String var1, String var2, String var3) {
      return this.createMetric("survey_metric", "messenger", var1, var2, "survey", var3);
   }

   private MetricObject createMetric(String var1, String var2, String var3, String var4, String var5, String var6) {
      return new MetricObject(
         var1,
         var2,
         UUID.randomUUID().toString(),
         this.userIdentity.getIntercomId(),
         var3,
         var4,
         var5,
         var6,
         this.timeProvider,
         this.appVersion,
         this.sdkVersion,
         this.appMinSdkVersion,
         this.appName
      );
   }

   private void addMetadataToMetricObject(MetricObject var1, @Nullable Map var2) {
      if (var2 != null && !var2.isEmpty()) {
         for (java.lang.Object recoveredEntryElement_0_732 : var2.keySet()) {
            String var4 = (String) recoveredEntryElement_0_732;
            var1.addMetaData(var4, var2.get(var4));
         }
      }
   }
}
